/*
    Video: https://www.youtube.com/watch?v=oCMOYS71NIU
    Based on Neil Kolban example for IDF: https://github.com/nkolban/esp32-snippets/blob/master/cpp_utils/tests/BLE%20Tests/SampleNotify.cpp
    Ported to Arduino ESP32 by Evandro Copercini

   Create a BLE server that, once we receive a connection, will send periodic notifications.
   The service advertises itself as: 6E400001-B5A3-F393-E0A9-E50E24DCCA9E
   Has a characteristic of: 6E400002-B5A3-F393-E0A9-E50E24DCCA9E - used for receiving data with "WRITE" 
   Has a characteristic of: 6E400003-B5A3-F393-E0A9-E50E24DCCA9E - used to send data with  "NOTIFY"

   The design of creating the BLE server is:
   1. Create a BLE Server
   2. Create a BLE Service
   3. Create a BLE Characteristic on the Service
   4. Create a BLE Descriptor on the characteristic
   5. Start the service.
   6. Start advertising.

   In this example rxValue is the data received (only accessible inside that function).
   And txValue is the data to be sent, in this example just a byte incremented every second. 
*/
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <ArduinoJson.h>

enum STATE {
  OFF = 0,
  ON
};

BLEServer *pServer = NULL;
BLECharacteristic * pTxCharacteristic;
bool deviceConnected = false;
bool oldDeviceConnected = false;
uint8_t txValue = 0;

STATE state = OFF;
int red = 0;
int green = 0;
int blue = 0;

int wake_loop = 0;
unsigned long start_time = 0ul;
unsigned long wake_time = 0ul;
int wake_red = 0;
int wake_green = 0;
int wake_blue = 0;

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

#define SERVICE_UUID           "6E400001-B5A3-F393-E0A9-E50E24DCCA9E" // UART service UUID
#define CHARACTERISTIC_UUID_RX "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
#define CHARACTERISTIC_UUID_TX "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"


class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      deviceConnected = true;
    };

    void onDisconnect(BLEServer* pServer) {
      deviceConnected = false;
    }
};

int getColor(char a, char b){
  int ret = 0;
  if(a <= '9'){
    ret = a - '0';
  }
  else{
    ret = a - 'A' + 10;
  }
  ret *= 16;

  if(b <= '9'){
    ret += (b - '0');
  }
  else{
    ret += (b - 'A' + 10);
  }

  return ret;
}

void loopfunc(){
  unsigned long cur_time = millis();
  unsigned long diff = cur_time - start_time;
  double percent = (double)diff / wake_time;
  percent *= percent;
  Serial.println(percent);
  if(percent >= 1){
    percent = 1.0;
  }
  ledcWrite(0, wake_red * percent);
  ledcWrite(1, wake_green * percent);
  ledcWrite(2, wake_blue * percent);
  if(diff >= wake_time){
    wake_loop = 0;
  }
}

class MyCallbacks: public BLECharacteristicCallbacks {
  void onWrite(BLECharacteristic *pCharacteristic) {
    std::string rxValue = pCharacteristic->getValue();

    if (rxValue.length() > 0) {
      Serial.println("*********");
      Serial.print("Received Value: ");
      for(int i = 0; i < rxValue.length(); i++){
        Serial.print(rxValue[i]);
      }
      Serial.println();
      Serial.println("*********");

      JsonDocument root;
      deserializeJson(root, rxValue);
      std::string request_name = root["requestName"];
      if(request_name.compare("getId") == 0){
        root["machineType"] = "lamp";
        String output;
        serializeJson(root, output);
        pTxCharacteristic->setValue((uint8_t*)output.c_str(), output.length());
        pTxCharacteristic->notify();
      }
      else{
        wake_loop = 0;
        if(request_name.compare("setCurrentColor") == 0){
          std::string color = root["color"];

          state = ON;
          red = getColor(color[0], color[1]);
          green = getColor(color[2], color[3]) * 0.5;
          blue = getColor(color[4], color[5]) * 0.5;
          Serial.printf("%d %d %d\n", red, green, blue);
          ledcWrite(0, red);
          ledcWrite(1, green);
          ledcWrite(2, blue);
        }
        else if(request_name.compare("setWakeupColor") == 0){
          start_time = millis();
          wake_time = root["time"];
          wake_time *= 1000;

          std::string color = root["color"];
          wake_red = getColor(color[0], color[1]);
          wake_green = getColor(color[2], color[3]) * 0.5;
          wake_blue = getColor(color[4], color[5]) * 0.5;
          wake_loop = 1;
        }
        else if(request_name.compare("setStateOff") == 0){
          state = OFF;
          ledcWrite(0, 0);
          ledcWrite(1, 0);
          ledcWrite(2, 0);
        }
        else if(request_name.compare("getState") == 0){
          root["state"] = state;
          String output;
          serializeJson(root, output);
          pTxCharacteristic->setValue((uint8_t*)output.c_str(), output.length());
          pTxCharacteristic->notify();
        }
      }
    }
  }
};


void setup() {
  Serial.begin(115200);

  // Create the BLE Device
  BLEDevice::init("Lamp");

  // Create the BLE Server
  pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  // Create the BLE Service
  BLEService *pService = pServer->createService(SERVICE_UUID);

  // Create a BLE Characteristic
  pTxCharacteristic = pService->createCharacteristic(
										CHARACTERISTIC_UUID_TX,
										BLECharacteristic::PROPERTY_NOTIFY
									);
                      
  pTxCharacteristic->addDescriptor(new BLE2902());

  BLECharacteristic * pRxCharacteristic = pService->createCharacteristic(
											 CHARACTERISTIC_UUID_RX,
											BLECharacteristic::PROPERTY_WRITE
										);

  pRxCharacteristic->setCallbacks(new MyCallbacks());

  // Start the service
  pService->start();

  // Start advertising
  pServer->getAdvertising()->start();
  Serial.println("Waiting a client connection to notify...");
  
  // led pin pwm setting
  ledcSetup(0, 5000, 8);
  ledcSetup(1, 5000, 8);
  ledcSetup(2, 5000, 8);
  ledcAttachPin(25, 0);
  ledcAttachPin(26, 1);
  ledcAttachPin(27, 2);
  ledcAttachPin(14, 0);
  ledcAttachPin(12, 1);
  ledcAttachPin(13, 2);
}

void loop() {
  if (deviceConnected) {
    if(wake_loop){
      loopfunc();
    }
    delay(100);
    // if(Serial.available()){
    //   String input = Serial.readString();
    //   input[input.length()-1] = 0u;
    //   pTxCharacteristic->setValue((uint8_t*)input.c_str(), input.length());
    //   pTxCharacteristic->notify();
    // }
  }

    // disconnecting
    if (!deviceConnected && oldDeviceConnected) {
        delay(500); // give the bluetooth stack the chance to get things ready
        pServer->startAdvertising(); // restart advertising
        Serial.println("start advertising");
        oldDeviceConnected = deviceConnected;
    }
    // connecting
    if (deviceConnected && !oldDeviceConnected) {
		// do stuff here on connecting
        Serial.println("device connecting");
        oldDeviceConnected = deviceConnected;
    }
}

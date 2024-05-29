#!bin/bash

cd build

bluetoothctl disconnect A8:42:E3:B8:13:8A
bluetoothctl remove A8:42:E3:B8:13:8A
bluetoothctl disconnect D4:8A:FC:A7:7B:76
bluetoothctl remove D4:8A:FC:A7:7B:76
bluetoothctl disconnect 30:AE:A4:EB:24:2A
bluetoothctl remove 30:AE:A4:EB:24:2A
bluetoothctl disconnect E4:65:B8:0F:FB:16
bluetoothctl remove E4:65:B8:0F:FB:16
sleep 3

bluetoothctl power off
sleep 2
bluetoothctl power on
sleep 2

./program A8:42:E3:B8:13:8A D4:8A:FC:A7:7B:76 30:AE:A4:EB:24:2A E4:65:B8:0F:FB:16

#!bin/bash

sudo apt install build-essential -y
sudo apt install gcc g++ cmake -y
sudo apt install fonts-nanum -y
sudo apt install libglib2.0-dev libdbus-*dev libusb-dev libudev-dev libical-dev libreadline-dev -y

wget https://mirrors.edge.kernel.org/pub/linux/bluetooth/bluez-5.75.tar.xz
tar xvf bluez-5.75.tar.xz
cd bluez-5.75/
./configure --prefix=/usr --mandir=/usr/share/man --sysconfdir=/etc --localstatedir=/var --enable-experimental
make -j4
sudo make install
cd ..

cmake -B build
cmake --build build

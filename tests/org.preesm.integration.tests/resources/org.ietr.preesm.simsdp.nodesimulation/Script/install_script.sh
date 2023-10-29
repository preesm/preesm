#!/bin/bash
if [ ! -d "simsdp" ]; then
 git clone https://github.com/adrgougeon/simsdp.git
sudo apt update
sudo apt install -y python3 pip default-jdk cmake git libboost-dev meson pkg-config doxygen
pip install -r requirements.txt
pip install .
fi
if [ ! -d "simgrid" ]; then
 git clone https://framagit.org/simgrid/simgrid
cd simgrid
cmake -B build .
sudo make install -j$(nproc) -C build
pip install .
sudo ldconfig
fi

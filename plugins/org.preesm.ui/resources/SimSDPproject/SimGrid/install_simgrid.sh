#!/bin/bash

# Check if simgrid folder exists
if [ ! -d "simgrid" ]; then
    # If the folder doesn't exist, clone it from the Git repository
    git clone https://framagit.org/simgrid/simgrid
    cd simgrid
    cmake -B build .
    sudo make install -j$(nproc) -C build
    pip install .
    sudo ldconfig

fi

# Move to specified directory to test platform
cd simgrid/examples/cpp/trace-platform

# Compiling the cpp file
g++ -o s4u-trace-platform s4u-trace-platform.cpp -lsimgrid

# Run the compiled file with simSDP_network.xml as argument
./s4u-trace-platform simSDP_network.xml






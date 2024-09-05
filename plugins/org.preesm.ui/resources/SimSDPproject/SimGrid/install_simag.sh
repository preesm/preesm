#!/bin/bash

# Check if Adrien Gougeon's simgrid file exists
if [ ! -d "simsdp" ]; then
    # If the folder doesn't exist, clone it from the Git repository
    git clone git@github.com:adrgougeon/simsdp.git
    cd simsdp
        sudo apt update
        sudo apt install -y python3 pip default-jdk cmake git libboost-dev meson pkg-config doxygen
        pip install -r requirements.txt
        pip install .

fi




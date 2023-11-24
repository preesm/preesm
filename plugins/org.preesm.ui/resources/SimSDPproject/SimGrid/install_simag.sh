#!/bin/bash

# Check if Adrien Gougeon's simgrid file exists
if [ ! -d "simsdp-master" ]; then
    # If the folder doesn't exist, clone it from the Git repository
    git clone https://github.com/adrgougeon/simsdp.git
        sudo apt update
        sudo apt install -y python3 pip default-jdk cmake git libboost-dev meson pkg-config doxygen
        pip install -r requirements.txt
        pip install .

fi



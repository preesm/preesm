# Requirements

Some standard libraries and python packages are necessary to use simsdp.

To install them run the following commands:
```
sudo apt update
sudo apt install -y python3 pip default-jdk cmake git libboost-dev meson pkg-config doxygen
pip install -r requirements.txt
```

## Simgrid
You also need to have SimGrid installed on your system.

To install the latest version of SimGrid run the following commands:
```
git clone https://framagit.org/simgrid/simgrid
cd simgrid
cmake -B build .
sudo make install -j$(nproc) -C build
```

If you want to use simsdp without generating c++ code for SimGrid, i.e., use SimGrid directly in python, then you must also run `pip install .` inside the simgrid folder.

# Installation

To make the package visible on the system run `pip install .` at the root of the simsdp folder.

You can also refer to how tests work to use the package without installing it.

You can either use simsdp as a python module inside your own python script, or simply use it directly from a terminal with the `simsdp` command. Use `simsdp -h` to see available options.

# User Documentation

Private github repository cannot have public pages. The documentation is available in another branch of the project: [gh-pages](https://github.com/adrgougeon/simsdp/tree/gh-pages).

The documentation can also be generated locally by running the script `build_doc.sh` located inside the `docs` directory. Documentation will be available as html files inside the `build` directory.

# Tests

Several tests are available inside the `tests` directory. You can execute them individually by running `python TEST_NAME` or all of them by running `pytest`.

Installation of the package is NOT mandatory to run the tests.

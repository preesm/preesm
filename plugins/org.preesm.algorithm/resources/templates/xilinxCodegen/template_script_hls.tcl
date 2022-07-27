# Generate ip in Vivado format
# arguments :
# - arg 0 mode to use, either csynth or cosim
# - arg 1 name of the top function and output IP
# - arg 2-N file names

# Create project and set top level function
variable top [lindex $argv 1]
open_project $top
set_top $top

# Include files
foreach source [lrange $argv 2 end] {
	add_files $source -cflags "-I../include"
}

if {[string match [lindex $argv 0] cosim] == 1} {
  add_files -tb ${top}_testbench.cpp
}

# Setup solution for target FPGA
open_solution "solution1" -flow_target vivado
set_part {xc7z020-clg400-1}

# Set clock target to 10.0 ns
create_clock -period 10.0 -name default

# Synthesize and export top.zip
csynth_design
if {[string match [lindex $argv 0] csynth] == 1} {
  export_design -rtl verilog -format ip_catalog -output ${top}.zip
}
if {[string match [lindex $argv 0] cosim] == 1} {
  cosim_design -O
}

exit

# Generate ip in Vivado format
# arguments :
# - arg 0 name of the top function and output IP
# - arg 1-N file names

# Create project and set top level function
variable top [lindex $argv 0]
open_project $top
set_top $top

# Include files
foreach source [lrange $argv 1 end] {
	add_files $source -cflags "-I../include"
}

# Setup solution for target Zynq 7020
open_solution "solution1" -flow_target vivado
set_part {xc7z020-clg400-1}

# Set clock target to 10 ns
create_clock -period 10 -name default

# Synthesize and export top.zip
csynth_design
export_design -rtl verilog -format ip_catalog -output ${top}.zip

exit

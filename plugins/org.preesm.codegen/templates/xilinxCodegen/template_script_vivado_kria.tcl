package require try
package require cmdline

set options {
	{j.arg	1	"number of parallel jobs"}
}
set usage "script_hls \[options] \nooptions:"

try {
	array set params [::cmdline::getoptions argv $options $usage]
} trap {CMDLINE USAGE} {msg o} {
	puts $msg
	exit 1
}

# Create project with board
create_project vivado vivado -part xck26-sfvc784-2LV-c
set_property board_part xilinx.com:kr260_som:part0:1.1 [current_project]

# Add Processor and configure
create_bd_design "design_1"
update_compile_order -fileset sources_1
# modif : remplacement par zynq_ultra_ps_e:3.5
create_bd_cell -type ip -vlnv xilinx.com:ip:zynq_ultra_ps_e:3.5 "processing_system7_0" 
# modif : remplacement par zynq_ultra_ps_e
apply_bd_automation -rule xilinx.com:bd_rule:zynq_ultra_ps_e -config {make_external "FIXED_IO, DDR" apply_board_preset "1" Master "Disable" Slave "Disable" }  [get_bd_cells processing_system7_0]
set_property -dict [list \
  CONFIG.PSU__USE__M_AXI_GP0 {0} \
  CONFIG.PSU__USE__M_AXI_GP1 {0} \
  CONFIG.PSU__USE__M_AXI_GP2 {1} \
] [get_bd_cells processing_system7_0]

# Add IP
set_property  ip_repo_paths . [current_project]
update_ip_catalog
create_bd_cell -type ip -vlnv xilinx.com:hls:top_graph_gaussian_difference:1.0 top_graph_gaussian_difference_0
create_bd_cell -type ip -vlnv xilinx.com:hls:mem_read_gaussian_difference:1.0 mem_read_gaussian_difference_0
create_bd_cell -type ip -vlnv xilinx.com:hls:mem_write_gaussian_difference:1.0 mem_write_gaussian_difference_0

# Connect IP and processor

# AXIlite controllers
apply_bd_automation -rule xilinx.com:bd_rule:axi4 -config { Clk_master {Auto} Clk_slave {Auto} Clk_xbar {Auto} Master {/processing_system7_0/M_AXI_HPM0_LPD} Slave {/mem_read_gaussian_difference_0/s_axi_control} ddr_seg {Auto} intc_ip {New AXI Interconnect} master_apm {0}}  [get_bd_intf_pins mem_read_gaussian_difference_0/s_axi_control]
apply_bd_automation -rule xilinx.com:bd_rule:axi4 -config { Clk_master {Auto} Clk_slave {Auto} Clk_xbar {Auto} Master {/processing_system7_0/M_AXI_HPM0_LPD} Slave {/mem_write_gaussian_difference_0/s_axi_control} ddr_seg {Auto} intc_ip {New AXI Interconnect} master_apm {0}}  [get_bd_intf_pins mem_write_gaussian_difference_0/s_axi_control]

# AXI memory ports
apply_bd_automation -rule xilinx.com:bd_rule:axi4 -config { Clk_master {/processing_system7_0/pl_clk0 (99 MHz)} Clk_slave {/processing_system7_0/pl_clk0 (99 MHz)} Clk_xbar {/processing_system7_0/pl_clk0 (99 MHz)} Master {/mem_read_gaussian_difference_0/m_axi_gmem} Slave {/mem_write_gaussian_difference_0/s_axi_control} ddr_seg {Auto} intc_ip {/ps7_0_axi_periph} master_apm {0}}  [get_bd_intf_pins mem_read_gaussian_difference_0/m_axi_gmem]
apply_bd_automation -rule xilinx.com:bd_rule:axi4 -config { Clk_master {/processing_system7_0/pl_clk0 (99 MHz)} Clk_slave {/processing_system7_0/pl_clk0 (99 MHz)} Clk_xbar {/processing_system7_0/pl_clk0 (99 MHz)} Master {/mem_write_gaussian_difference_0/m_axi_gmem} Slave {/mem_read_gaussian_difference_0/s_axi_control} ddr_seg {Auto} intc_ip {/ps7_0_axi_periph} master_apm {0}}  [get_bd_intf_pins mem_write_gaussian_difference_0/m_axi_gmem]

# AXI internal FIFOs
create_bd_cell -type ip -vlnv xilinx.com:ip:axis_data_fifo:2.0 axis_data_fifo_input_stream
set_property -dict [list CONFIG.FIFO_DEPTH {64}] [get_bd_cells axis_data_fifo_input_stream]
connect_bd_intf_net [get_bd_intf_pins mem_read_gaussian_difference_0/input_stream] [get_bd_intf_pins axis_data_fifo_input_stream/S_AXIS]
connect_bd_intf_net [get_bd_intf_pins axis_data_fifo_input_stream/M_AXIS] [get_bd_intf_pins top_graph_gaussian_difference_0/input_stream]
#apply_bd_automation -rule xilinx.com:bd_rule:clkrst -config { Clk {/processing_system7_0/FCLK_CLK0 (100 MHz)} Freq {100} Ref_Clk0 {} Ref_Clk1 {} Ref_Clk2 {}}  [get_bd_pins axis_data_fifo_input_stream/s_axis_aclk]

create_bd_cell -type ip -vlnv xilinx.com:ip:axis_data_fifo:2.0 axis_data_fifo_output_stream
set_property -dict [list CONFIG.FIFO_DEPTH {64}] [get_bd_cells axis_data_fifo_output_stream]
connect_bd_intf_net [get_bd_intf_pins top_graph_gaussian_difference_0/output_stream] [get_bd_intf_pins axis_data_fifo_output_stream/S_AXIS]
connect_bd_intf_net [get_bd_intf_pins axis_data_fifo_output_stream/M_AXIS] [get_bd_intf_pins mem_write_gaussian_difference_0/output_stream]
#apply_bd_automation -rule xilinx.com:bd_rule:clkrst -config { Clk {/processing_system7_0/FCLK_CLK0 (100 MHz)} Freq {100} Ref_Clk0 {} Ref_Clk1 {} Ref_Clk2 {}}  [get_bd_pins axis_data_fifo_output_stream/s_axis_aclk]



# Kernel clock (automatically connect FIFOs)
apply_bd_automation -rule xilinx.com:bd_rule:clkrst -config { Clk {/processing_system7_0/pl_clk0 (99 MHz)} Freq {99} Ref_Clk0 {} Ref_Clk1 {} Ref_Clk2 {}}  [get_bd_pins axis_data_fifo_input_stream/s_axis_aclk]
apply_bd_automation -rule xilinx.com:bd_rule:clkrst -config { Clk {/processing_system7_0/pl_clk0 (99 MHz)} Freq {99} Ref_Clk0 {} Ref_Clk1 {} Ref_Clk2 {}}  [get_bd_pins axis_data_fifo_output_stream/s_axis_aclk]

save_bd_design

# Synthesize
make_wrapper -files [get_files vivado/vivado.srcs/sources_1/bd/design_1/design_1.bd] -top
add_files -norecurse vivado/vivado.gen/sources_1/bd/design_1/hdl/design_1_wrapper.v
launch_runs impl_1 -to_step write_bitstream -jobs $params(j)
wait_on_run impl_1

exit

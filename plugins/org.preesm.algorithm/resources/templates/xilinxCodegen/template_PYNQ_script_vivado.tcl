# Create project with PYNQ board
create_project vivado vivado -part xc7z020clg400-1
set_property board_part tul.com.tw:pynq-z2:part0:1.0 [current_project]

# Add Processor and configure
create_bd_design "design_1"
update_compile_order -fileset sources_1
create_bd_cell -type ip -vlnv xilinx.com:ip:processing_system7:5.5 processing_system7_0
apply_bd_automation -rule xilinx.com:bd_rule:processing_system7 -config {make_external "FIXED_IO, DDR" apply_board_preset "1" Master "Disable" Slave "Disable" }  [get_bd_cells processing_system7_0]
set_property -dict [list CONFIG.PCW_USE_S_AXI_HP0 {1}] [get_bd_cells processing_system7_0]

# Add IP
set_property  ip_repo_paths . [current_project]
update_ip_catalog
create_bd_cell -type ip -vlnv xilinx.com:hls:top_graph:1.0 top_graph_0
create_bd_cell -type ip -vlnv xilinx.com:hls:mem_read:1.0 mem_read_0
create_bd_cell -type ip -vlnv xilinx.com:hls:mem_write:1.0 mem_write_0

# Connect IP and processor

# AXIlite controllers
apply_bd_automation -rule xilinx.com:bd_rule:axi4 -config { Clk_master {Auto} Clk_slave {Auto} Clk_xbar {Auto} Master {/processing_system7_0/M_AXI_GP0} Slave {/mem_read_0/s_axi_control} ddr_seg {Auto} intc_ip {New AXI Interconnect} master_apm {0}}  [get_bd_intf_pins mem_read_0/s_axi_control]
apply_bd_automation -rule xilinx.com:bd_rule:axi4 -config { Clk_master {Auto} Clk_slave {Auto} Clk_xbar {Auto} Master {/processing_system7_0/M_AXI_GP0} Slave {/mem_write_0/s_axi_control} ddr_seg {Auto} intc_ip {New AXI Interconnect} master_apm {0}}  [get_bd_intf_pins mem_write_0/s_axi_control]

# AXI memory ports
apply_bd_automation -rule xilinx.com:bd_rule:axi4 -config { Clk_master {Auto} Clk_slave {Auto} Clk_xbar {Auto} Master {/mem_read_0/m_axi_gmem} Slave {/processing_system7_0/S_AXI_HP0} ddr_seg {Auto} intc_ip {New AXI Interconnect} master_apm {0}}  [get_bd_intf_pins processing_system7_0/S_AXI_HP0]
apply_bd_automation -rule xilinx.com:bd_rule:axi4 -config { Clk_master {/processing_system7_0/FCLK_CLK0 (100 MHz)} Clk_slave {/processing_system7_0/FCLK_CLK0 (100 MHz)} Clk_xbar {/processing_system7_0/FCLK_CLK0 (100 MHz)} Master {/mem_write_0/m_axi_gmem} Slave {/processing_system7_0/S_AXI_HP0} ddr_seg {Auto} intc_ip {/axi_mem_intercon} master_apm {0}}  [get_bd_intf_pins mem_write_0/m_axi_gmem]

# AXI internal FIFOs
create_bd_cell -type ip -vlnv xilinx.com:ip:axis_data_fifo:2.0 axis_data_fifo_in1_stream
set_property -dict [list CONFIG.FIFO_DEPTH {64}] [get_bd_cells axis_data_fifo_in1_stream]
connect_bd_intf_net [get_bd_intf_pins mem_read_0/in1_stream_V] [get_bd_intf_pins axis_data_fifo_in1_stream/S_AXIS]
connect_bd_intf_net [get_bd_intf_pins axis_data_fifo_in1_stream/M_AXIS] [get_bd_intf_pins top_graph_0/in1_stream_V]
#apply_bd_automation -rule xilinx.com:bd_rule:clkrst -config { Clk {/processing_system7_0/FCLK_CLK0 (100 MHz)} Freq {100} Ref_Clk0 {} Ref_Clk1 {} Ref_Clk2 {}}  [get_bd_pins axis_data_fifo_in1_stream/s_axis_aclk]

create_bd_cell -type ip -vlnv xilinx.com:ip:axis_data_fifo:2.0 axis_data_fifo_in2_stream
set_property -dict [list CONFIG.FIFO_DEPTH {64}] [get_bd_cells axis_data_fifo_in2_stream]
connect_bd_intf_net [get_bd_intf_pins mem_read_0/in2_stream_V] [get_bd_intf_pins axis_data_fifo_in2_stream/S_AXIS]
connect_bd_intf_net [get_bd_intf_pins axis_data_fifo_in2_stream/M_AXIS] [get_bd_intf_pins top_graph_0/in2_stream_V]
#apply_bd_automation -rule xilinx.com:bd_rule:clkrst -config { Clk {/processing_system7_0/FCLK_CLK0 (100 MHz)} Freq {100} Ref_Clk0 {} Ref_Clk1 {} Ref_Clk2 {}}  [get_bd_pins axis_data_fifo_in2_stream/s_axis_aclk]

create_bd_cell -type ip -vlnv xilinx.com:ip:axis_data_fifo:2.0 axis_data_fifo_outMul_stream
set_property -dict [list CONFIG.FIFO_DEPTH {64}] [get_bd_cells axis_data_fifo_outMul_stream]
connect_bd_intf_net [get_bd_intf_pins top_graph_0/outMul_stream_V] [get_bd_intf_pins axis_data_fifo_outMul_stream/S_AXIS]
connect_bd_intf_net [get_bd_intf_pins axis_data_fifo_outMul_stream/M_AXIS] [get_bd_intf_pins mem_write_0/outMul_stream_V]
#apply_bd_automation -rule xilinx.com:bd_rule:clkrst -config { Clk {/processing_system7_0/FCLK_CLK0 (100 MHz)} Freq {100} Ref_Clk0 {} Ref_Clk1 {} Ref_Clk2 {}}  [get_bd_pins axis_data_fifo_outMul_stream/s_axis_aclk]

create_bd_cell -type ip -vlnv xilinx.com:ip:axis_data_fifo:2.0 axis_data_fifo_outAdd_stream
set_property -dict [list CONFIG.FIFO_DEPTH {64}] [get_bd_cells axis_data_fifo_outAdd_stream]
connect_bd_intf_net [get_bd_intf_pins top_graph_0/outAdd_stream_V] [get_bd_intf_pins axis_data_fifo_outAdd_stream/S_AXIS]
connect_bd_intf_net [get_bd_intf_pins axis_data_fifo_outAdd_stream/M_AXIS] [get_bd_intf_pins mem_write_0/outAdd_stream_V]
#apply_bd_automation -rule xilinx.com:bd_rule:clkrst -config { Clk {/processing_system7_0/FCLK_CLK0 (100 MHz)} Freq {100} Ref_Clk0 {} Ref_Clk1 {} Ref_Clk2 {}}  [get_bd_pins axis_data_fifo_outAdd_stream/s_axis_aclk]

# Kernel clock (automatically connect FIFOs)
apply_bd_automation -rule xilinx.com:bd_rule:clkrst -config { Clk {/processing_system7_0/FCLK_CLK0 (100 MHz)} Freq {100} Ref_Clk0 {} Ref_Clk1 {} Ref_Clk2 {}}  [get_bd_pins top_graph_0/ap_clk]

save_bd_design

# Synthesize
make_wrapper -files [get_files vivado/vivado.srcs/sources_1/bd/design_1/design_1.bd] -top
add_files -norecurse vivado/vivado.gen/sources_1/bd/design_1/hdl/design_1_wrapper.v
launch_runs impl_1 -to_step write_bitstream -jobs 2
wait_on_run impl_1

exit

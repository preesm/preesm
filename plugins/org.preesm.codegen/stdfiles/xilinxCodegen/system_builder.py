# 2024-11-08T14:18:30.014486
import vitis
import os
import sys
import pdb
import argparse

# Component part numbers : 
#xck26-sfvc784-2LV-c : kria 260, part number xilinx.com:kr260_som:part0:1.1
#xcvu9p-flga2104-2-i : virtex ultrascale+, 
# xc7z020-clg400-1 : pynq, part number tul.com.tw:pynq-z2:part0:1.0

def main(comp_name, sys_proj_name, boot_dir, target):
	workspace = os.path.abspath("./")
	hls_folder = os.path.abspath("../")
	code_folder = os.path.abspath("../../")
	sysroot = boot_dir + "sysroots/cortexa72-cortexa53-xilinx-linux/"

	targets = {"kr260": "xck26-sfvc784-2LV-c", "ultrascale": "xck26-sfvc784-2LV-c"}

	print("target platform :", target)

	client = vitis.create_client()
	client.set_workspace(path=workspace)


	""" ---- Build Platform ---- """

	# default config for ultrascale zcu104 platform
	hw_path = "/data/Xilinx/Vitis/2024.1/base_platforms/xilinx_zcu104_base_202410_1/xilinx_zcu104_base_202410_1.xpfm"
	p_os = "linux"
	p_cpu = "psu_cortexa53_0"
	p_domain_name = "linux_psu_cortexa53"

	if target == "kr260":
		platform = client.create_platform_component(name="platform", hw_design=hls_folder+"/vitis_platform/mydevice/hw/kr260_hardware_platform.xsa", os=p_os, cpu=p_cpu, domain_name=p_domain_name)	
	elif target == "ultrascale":
		platform = client.create_platform_component(name="platform", platform_xpfm_path=hw_path)
		domain = platform.add_domain(cpu=p_cpu, os=p_os, name=p_domain_name)
		status = platform.generate_boot_bsp(target_processor=p_cpu)

	platform = client.get_component(name="platform")

	domain = platform.get_domain(name="linux_psu_cortexa53")

	status = domain.generate_bif()

	status = domain.set_boot_dir(path=boot_dir)

	status = domain.set_dtb(path=hls_folder + "/vitis_platform/mydevice/psu_cortexa53_0/device_tree_domain/bsp/system.dtb")

	status = platform.build()


	""" ---- HLS Components creation ---- """

	# for now I create only one hls component. Later it will have to be just as many as necessary.
	accelerators = [line.rstrip() for line in open(code_folder + "/generated/clusters_list", "r")]
	hls_kernels = []
	for acc in accelerators:
		hls_kernels.append(acc)
		hls_kernels.append("mem_read_" + acc) # to each accelerator its read and write kernels
		hls_kernels.append("mem_write_" + acc)

	hls_kernel_files = [k + ".cpp" for k in hls_kernels]
	testbench_files = [file for file in os.listdir(code_folder+"/generated") if "testbench" in file]


	for kernel in hls_kernels:
		comp = client.create_hls_component(name = kernel, cfg_file = ["hls_config.cfg"],template = "empty_hls_component")
		cfg_path = os.path.join(workspace, kernel, 'hls_config.cfg')
		cfg_obj = client.get_config_file(cfg_path)
		cfg_obj.set_value('', key="part", value=targets[target])
		liste_hls_usercmake = [
		f"syn.top={kernel}",
		f"syn.file={code_folder}/generated/{kernel}.cpp",
		f"syn.cflags=-I{code_folder}/include -I{code_folder}/generated"
		]
		cfg_obj.add_lines('hls', liste_hls_usercmake)


	""" ---- Application Component creation ---- """
	# TODO make it generic for several accelerators
	comp = client.get_component(name=accelerators[0]) 
	comp = client.create_app_component(name="app_component", platform = hls_folder + "/system_project/platform/export/platform/platform.xpfm", domain = "linux_psu_cortexa53")
	comp = client.get_component("app_component")
	status = comp.set_sysroot(sysroot=sysroot)

	# set all .cpp files as app source. Vitis shall sort set out.
	gen_CPPfiles = [file for file in os.listdir(code_folder+"/generated") if file.endswith(".cpp") and not(file in hls_kernel_files + testbench_files)]
	status = comp.import_files(from_loc=code_folder+"/generated", files=gen_CPPfiles)

	source_files = [file for file in os.listdir(code_folder+"/src") if file.endswith(".cpp") and not(file in hls_kernel_files)]
	status = comp.import_files(from_loc=code_folder+"/src", files=source_files)

	status = comp.import_files(from_loc=code_folder+"/generated/libs/common/includes", files=["xcl2"])

	# include folders
	status = comp.set_app_config(key="USER_INCLUDE_DIRECTORIES", values=f"{code_folder}/generated/libs/common/includes/xcl2 \n {code_folder}/generated \n {code_folder}/include")

	# set the vitis compilation flag
	status = comp.set_app_config(key="USER_COMPILE_DEFINITIONS", values="VITIS_COMPILATION")


	""" ---- Create System project ---- """

	proj = client.create_sys_project(name="system_project", platform=f"{hls_folder}/system_project/platform/export/platform/platform.xpfm", template="empty_accelerated_application")

	proj = client.get_sys_project(name="system_project")

	# mettre le nom de l'algo top ici
	status = proj.add_container(name="exemple_cluster")

	for comp in hls_kernels:
		proj = proj.add_component(name=comp, container_name=["exemple_cluster"])

	proj = proj.add_component(name="app_component")

	packagecfg_path = os.path.join(workspace, sys_proj_name, 'package/package.cfg')
	cfg_obj = client.get_config_file(packagecfg_path)
	liste_cfg_package = [
	f"dtb={hls_folder}/vitis_platform/dtbo_output/pl.dtbo",
	f"kernel_image={boot_dir}/Image",
	f"rootfs={boot_dir}/rootfs.ext4"
	]
	cfg_obj.add_lines('package', liste_cfg_package)

	connectivity_cfg = os.path.join(workspace, sys_proj_name, 'hw_link/exemple_cluster-link.cfg')
	cfg_obj = client.get_config_file(connectivity_cfg)
	connections = []
	with open(code_folder + "/generated/connectivity.cfg", "r") as file:
		for line in file:
			connections.append(line.rstrip())
	cfg_obj.add_lines('connectivity', connections)



	# status = client.create_launch_config(project_name="system_project", launch_config="system_project", target="system_project", build_output_path="system_project")

	# TODO uncomment to automatically build the project
	# status = proj.build(target="hw_emu")

	# je ne sais toujours pas comment créer automatiquement une config de lancement hélas
	#status = proj.create_launch_config(project_name="system_project", launch_config="system_project", target="system_project", build_output_path="system_project")

if __name__ == "__main__":
	if len(sys.argv) != 5:
		print("Usage : python builder.py <component_name> <system project name> <common image path> <target>")
		sys.exit(1)

	comp_name = sys.argv[1]
	sys_proj_name = sys.argv[2] 
	boot_dir = sys.argv[3]
	target = sys.argv[4]
	main(comp_name, sys_proj_name, boot_dir, target)

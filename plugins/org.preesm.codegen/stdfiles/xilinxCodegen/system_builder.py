# 2024-11-08T14:18:30.014486
import os
import sys
import pdb
import vitis

# Component part numbers : 
#xck26-sfvc784-2LV-c : kria 260, part number xilinx.com:kr260_som:part0:1.1
#xcvu9p-flga2104-2-i : virtex ultrascale+, 
# xc7z020-clg400-1 : pynq, part number tul.com.tw:pynq-z2:part0:1.0


def main(comp_name, sys_proj_name, common_image, target, version, vitis_loc, platform_name, target_build):
	workspace = os.path.abspath("./")
	codegen_folder = os.path.abspath("../")
	code_folder = os.path.abspath("../../")
	
	sysroot = common_image + "/sysroots/cortexa72-cortexa53-xilinx-linux/"
	if not(os.path.isdir(sysroot)):
		print(sysroot)
		sysroot = common_image + "/sysroots/cortexa72-cortexa53-amd-linux/"

	targets = {"kr260": "xck26-sfvc784-2LV-c", "ultrascale": "xck26-sfvc784-2LV-c"}

	client = vitis.create_client()
	client.set_workspace(path=workspace)


	sys.path.append(os.path.abspath(os.path.join(os.path.dirname("project_config.py"), code_folder)))

	import project_config


	""" ---- Build Platform ---- """

	p_os = "linux"
	p_cpu = "psu_cortexa53_0"
	p_domain_name = "linux_psu_cortexa53"

	platform = client.create_platform_component(name="platform", hw_design=codegen_folder+f"/vitis_platform/mydevice/hw/{platform_name}.xsa", os=p_os, cpu=p_cpu, domain_name=p_domain_name)	

	platform = client.get_component(name="platform")

	domain = platform.get_domain(name="linux_psu_cortexa53")

	status = domain.generate_bif()

	status = domain.set_boot_dir(path=common_image)

	status = domain.set_dtb(path=codegen_folder + "/vitis_platform/mydevice/psu_cortexa53_0/device_tree_domain/bsp/system.dtb")

	status = platform.build()


	""" ---- HLS Components creation ---- """

	# for now I create only one hls component. Later it will have to be just as many as necessary.
	accelerators = [line.rstrip() for line in open(codegen_folder + "/clusters_list", "r")]
	hls_kernels = []
	for acc in accelerators:
		hls_kernels.append(acc)
		hls_kernels.append("mem_read_" + acc) # to each accelerator its read and write kernels
		hls_kernels.append("mem_write_" + acc)

	hls_kernel_files = [k + ".cpp" for k in hls_kernels]
	testbench_files = [file for file in os.listdir(codegen_folder) if "testbench" in file]


	for kernel in hls_kernels:
		comp = client.create_hls_component(name = kernel, cfg_file = ["hls_config.cfg"],template = "empty_hls_component")
		cfg_path = os.path.join(workspace, kernel, 'hls_config.cfg')
		cfg_obj = client.get_config_file(cfg_path)
		cfg_obj.set_value('', key="part", value=targets[target])
		liste_hls_usercmake = [
		f"syn.top={kernel}",
		f"syn.file={codegen_folder}/{kernel}.cpp",
		f"syn.cflags=-I{code_folder}/include -I{codegen_folder}"
		]
		cfg_obj.add_lines('hls', liste_hls_usercmake)

	""" ---- Application Component creation ---- """
	# TODO make it generic for several accelerators
	comp = client.get_component(name=accelerators[0]) 
	comp = client.create_app_component(name="app_component", platform = codegen_folder+"/system_project/platform/export/platform/platform.xpfm", domain = "linux_psu_cortexa53")
	comp = client.get_component("app_component")
	status = comp.set_sysroot(sysroot=sysroot)

	# set all .cpp files as app source. Vitis shall sort them out.
	gen_CPPfiles = [file for file in os.listdir(codegen_folder) if file.endswith(".cpp") and not(file in hls_kernel_files + testbench_files)]
	status = comp.import_files(from_loc=codegen_folder, files=gen_CPPfiles)

	source_files = [file for file in os.listdir(code_folder+"/src") if not(file.endswith(".h")) and not(file in hls_kernel_files)]
	status = comp.import_files(from_loc=code_folder+"/src", files=source_files)

	status = comp.import_files(from_loc=codegen_folder+"/libs/common/includes", files=["xcl2"])

	# include folders
	include_paths = [f"{codegen_folder}/libs/common/includes/xcl2", f"{codegen_folder}", f"{code_folder}/include"] + project_config.includes
	status = comp.set_app_config(key="USER_INCLUDE_DIRECTORIES", values=include_paths)

	# libs 
	status = comp.set_app_config(key="USER_LINK_LIBRARIES", values=project_config.libs)
	status = comp.set_app_config(key="USER_LINK_DIRECTORIES", values=project_config.libs_paths)
	

	# set the vitis compilation flag
	status = comp.set_app_config(key="USER_COMPILE_DEFINITIONS", values="VITIS_COMPILATION")
	try: # for vitis > 2025 
		status = comp.set_app_config(key="USER_CMAKE_CXX_STANDARD", values=project_config.cpp_version)
	except:
		project_config.flags += "-std=c++" + project_config.cpp_version
	status = comp.set_app_config(key="USER_COMPILE_OTHER_FLAGS", values=project_config.flags)

	# build application for hardware emulation
	comp.build(target=target_build)

	
	""" ---- Create System project ---- """

	proj = client.create_sys_project(name="system_project", platform=f"{codegen_folder}/system_project/platform/export/platform/platform.xpfm", template="empty_accelerated_application")

	proj = client.get_sys_project(name="system_project")

	# mettre le nom de l'algo top ici
	status = proj.add_container(name="container")

	for comp in hls_kernels:
		try:
			proj = proj.add_component(name=comp, container_name=["container"])
		except Exception as e:
			print(e)
			print(type(e))

	proj = proj.add_component(name="app_component")

	packagecfg_path = os.path.join(workspace, sys_proj_name, 'package/package.cfg')
	cfg_obj = client.get_config_file(packagecfg_path)
	print("package : ", cfg_obj.get_lines("package", "dtb"), "  ",cfg_obj.get_lines("package", "kernel_image"))
	liste_cfg_package = [
	f"dtb={codegen_folder}/vitis_platform/dtbo_output/pl.dtbo",
	]
	if(not cfg_obj.get_lines("package", "rootfs")):
		liste_cfg_package.append(f"rootfs={common_image}/rootfs.ext4")
	if(not cfg_obj.get_lines("package", "kernel_image")):
		liste_cfg_package.append(f"kernel_image={common_image}/Image")

	cfg_obj.add_lines('package', liste_cfg_package)

	connectivity_cfg = os.path.join(workspace, sys_proj_name, 'hw_link/container-link.cfg')
	cfg_obj = client.get_config_file(connectivity_cfg)
	connections = []
	with open(codegen_folder + "/connectivity.cfg", "r") as file:
		for line in file:
			connections.append(line.rstrip())
	cfg_obj.add_lines('connectivity', connections)

	# status = client.create_launch_config(project_name="system_project", launch_config="system_project", target="system_project", build_output_path="system_project")

	# TODO uncomment to automatically build the project
	status = proj.build(target=target_build)

	# je ne sais toujours pas comment créer automatiquement une config de lancement hélas
	#status = proj.create_launch_config(project_name="system_project", launch_config="system_project", target="system_project", build_output_path="system_project")

if __name__ == "__main__":
    print("builder.py usage : python builder.py <component_name> <system project name> <common image path> <target> <vitis version> <vitis path> <platform name>")
    if len(sys.argv) != 9:
        sys.exit(1)

    comp_name = sys.argv[1]
    sys_proj_name = sys.argv[2] 
    common_image = sys.argv[3]
    target = sys.argv[4]
    version = sys.argv[5]
    vitis_loc = sys.argv[6]
    platform_name = sys.argv[7]
    target_build = sys.argv[8]
    main(comp_name, sys_proj_name, common_image, target, version, vitis_loc, platform_name, target_build)

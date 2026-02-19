# 2024-11-08T14:18:30.014486
import os
import sys
import pdb
import vitis
import re

# Component part numbers : 
#xck26-sfvc784-2LV-c : kria 260, part number xilinx.com:kr260_som:part0:1.1
#xcvu9p-flga2104-2-i : virtex ultrascale+, 
# xc7z020-clg400-1 : pynq, part number tul.com.tw:pynq-z2:part0:1.0


def remove_hpp_includes(input_path, output_path=None):
	print("----- Removing any .hpp inclusion from preesm_gen.h -----")
	with open(input_path, "r") as f:
		lines = f.readlines()

	pattern = re.compile(r'^\s*#\s*include\s*["<].*\.hpp[">]')

	filtered = [line for line in lines if not pattern.search(line)]

	if output_path:
		with open(output_path, "w") as f:
			f.writelines(filtered)
	else:
		# overwrite original file
		with open(input_path, "w") as f:
			f.writelines(filtered)

def insert_includes(header_file, includes):
	with open(header_file, "r") as f:
		lines = f.readlines()

	include_lines = [f'#include <{inc}>\n' for inc in includes]

	# Find the last include line in the file
	last_include_index = -1
	include_pattern = re.compile(r'^\s*#\s*include\b')

	for i, line in enumerate(lines):
		if include_pattern.match(line):
			last_include_index = i

	if last_include_index == -1:
		# No includes: insert at top
		new_lines = include_lines + ["\n"] + lines
	else:
		# Insert after last include
		new_lines = (
			lines[: last_include_index + 1]
			+ include_lines
			+ lines[last_include_index + 1 :]
		)

	with open(header_file, "w") as f:
		f.writelines(new_lines)

def remove_nk_lines(path, output_path=None):
	with open(path, "r") as f:
		lines = f.readlines()

	pattern = re.compile(r'^\s*nk=')

	filtered = [line for line in lines if not pattern.match(line)]

	if output_path:
		with open(output_path, "w") as f:
			f.writelines(filtered)
	else:
		with open(path, "w") as f:
			f.writelines(filtered)

core_file_pattern = re.compile(r'^core[0-9]+\.cpp$')


def edit_core_files(directory="."):
	# List all matching files
	files = [f for f in os.listdir(directory) if core_file_pattern.match(f)]

	if not files:
		print("No coreX.cpp files found.")
		return

	for filename in files:
		filepath = os.path.join(directory, filename)

		try:
			with open(filepath, "r") as f:
				content = f.readlines()
		except IOError:
			print(f"Error: cannot read {filename}")
			continue

		new_lines = []
		for line in content:
			if "shared_mem" in line:
				# Convert leading float → extern float
				line = re.sub(
					r'^([ \t]*)float',
					r'\1extern float',
					line
				)
				# Convert leading unsigned → extern unsigned
				line = re.sub(
					r'^([ \t]*)unsigned',
					r'\1extern unsigned',
					line
				)

			new_lines.append(line)

		try:
			with open(filepath, "w") as f:
				f.writelines(new_lines)
			print(f"Updated: {filename}")
		except IOError:
			print(f"Error: cannot write {filename}")


def instrument_execution(codegen_path):
	# Read file
	with open(codegen_path + "/core0.cpp", "r") as f:
	    code = f.read()

	# --------------------------------------------------------------------
	# 1. Insert timespec subtraction function after "// Core Global Definitions"
	# --------------------------------------------------------------------
	global_insert = '''
	enum { NS_PER_SECOND = 1000000000 };
	void sub_timespec(struct timespec t1, struct timespec t2, struct timespec *td)
	{
	    td->tv_nsec = t2.tv_nsec - t1.tv_nsec;
	    td->tv_sec  = t2.tv_sec - t1.tv_sec;
	    if (td->tv_sec > 0 && td->tv_nsec < 0)
	    {
	        td->tv_nsec += NS_PER_SECOND;
	        td->tv_sec--;
	    }
	    else if (td->tv_sec < 0 && td->tv_nsec > 0)
	    {
	        td->tv_nsec -= NS_PER_SECOND;
	        td->tv_sec++;
	    }
	}
	'''

	code = code.replace("// Core Global Definitions",
	                    global_insert + "\n// Core Global Definitions")

	# --------------------------------------------------------------------
	# 2. Insert variable declarations before the 'for(index...' line
	# --------------------------------------------------------------------
	decl_insert = 'struct timespec start, finish, delta, latence = {0,0};\n'

	code = re.sub(
	    r'(?=for\s*\(\s*index\s*=\s*0\s*;)',
	    decl_insert,
	    code,
	    count=1
	)

	# --------------------------------------------------------------------
	# NEW STEP — Insert clock_gettime(CLOCK_REALTIME, &start) after "// loop body"
	# --------------------------------------------------------------------

	body_insert = 'clock_gettime(CLOCK_REALTIME, &start);\n'

	code = code.replace(
	    "// loop body",
	    "// loop body\n    " + body_insert    # keep indentation
	)

	# --------------------------------------------------------------------
	# 3. Insert inside loop after "// loop footer\n    pthread_barrier_wait(&iter_barrier);"
	# --------------------------------------------------------------------
	loop_footer_pattern = (
		r'//\s*loop footer\s*\n'          # comment line
		r'[ \t]*pthread_barrier_wait\s*'  # indentation + function name
		r'\(\s*&iter_barrier\s*\)\s*;'    # parentheses and semicolon
	)

	loop_insert = '''
	    clock_gettime(CLOCK_REALTIME, &finish);
	    sub_timespec(start, finish, &delta);
	    printf("latence à i=%d : %d.%.9ld s \\n", index, (int)delta.tv_sec, delta.tv_nsec);
	    latence.tv_nsec += delta.tv_nsec;
	    latence.tv_sec += delta.tv_sec;
	'''

	code = re.sub(loop_footer_pattern,
	              lambda m: m.group(0) + loop_insert,
	              code,
	              count=1)

	# --------------------------------------------------------------------
	# 4. Insert average print after the closing brace of the loop
	# --------------------------------------------------------------------

	avg_insert = '''
	printf("latence moyenne : %d.%.9ld s\\n",
	       (int) latence.tv_sec / PREESM_LOOP_SIZE,
	       latence.tv_nsec / PREESM_LOOP_SIZE);
	'''

	# Find loop footer occurrence
	footer_match = re.search(loop_footer_pattern, code)
	if footer_match:
	    footer_end = footer_match.end()

	    # Find the next closing brace after the footer
	    closing_brace_pos = code.find('}', footer_end)

	    if closing_brace_pos != -1:
	        # Insert the average print AFTER this brace
	        code = (code[:closing_brace_pos+1] +
	                avg_insert +
	                code[closing_brace_pos+1:])
	    else:
	        print("Warning: closing brace after loop footer not found.")
	else:
	    print("Warning: loop footer not found. rololoooo")

	# --------------------------------------------------------------------
	# Write result back to file
	# --------------------------------------------------------------------
	with open(codegen_path + "/core0.cpp", "w") as f:
	    f.write(code)


	# write loop number in preesm_gen.h
	define_line = "#define PREESM_LOOP_SIZE 20\n"
	try:
	    with open(codegen_path + "/preesm_gen.h", "r") as f:
	        lines = f.readlines()
	except IOError:
	    print(f"Error: cannot read preesm_gen.h")
	    sys.exit(1)

	new_lines = []
	inserted = False

	for line in lines:
	    if not inserted and line.lstrip().startswith("#ifdef PREESM_LOOP_SIZE"):
	        # Insert BEFORE the #ifdef line
	        new_lines.append(define_line)
	        inserted = True
	    new_lines.append(line)

	try:
	    with open(codegen_path + "/preesm_gen.h", "w") as f:
	        f.writelines(new_lines)
	    print(f"Inserted PREESM_LOOP_SIZE define in: preesm_gen.h")
	except IOError:
	    print(f"Error: cannot write preesm_gen.h")

	print(f"Instrumentation done")


def main(comp_name, sys_proj_name, common_image, target, platform_name, target_build, instrument):
	print("----- Starting vitis -----")

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


	""" ---- Create Platform ---- """

	print("----- Creating platform -----")

	p_os = "linux"
	p_cpu = "psu_cortexa53_0"
	p_domain_name = "linux_psu_cortexa53"

	platform = client.create_platform_component(name="platform", hw_design=codegen_folder+f"/vitis_platform/mydevice/hw/{platform_name}.xsa", os=p_os, cpu=p_cpu, domain_name=p_domain_name)	

	platform = client.get_component(name="platform")

	domain = platform.get_domain(name="linux_psu_cortexa53")

	status = domain.generate_bif()

	status = domain.set_boot_dir(path=common_image)

	status = domain.set_dtb(path=codegen_folder + "/vitis_platform/mydevice/psu_cortexa53_0/device_tree_domain/bsp/system.dtb")


	""" ---- HLS Components creation ---- """

	print("----- Creating HLS components -----")

	# for now I create only one hls component. Later it will have to be just as many as necessary.
	accelerators = [line.rstrip() for line in open(codegen_folder + "/clusters_list", "r")]
	unique_accelerators = set(accelerators)
	print("accelerators : ", unique_accelerators)
	hls_kernels = []
	for acc in unique_accelerators:
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
	print("----- Creating application component -----")

	remove_hpp_includes(input_path=codegen_folder + "/preesm_gen.h")
	edit_core_files(codegen_folder)
	if instrument:
		instrument_execution(codegen_folder)
	
	# insert <complex> inclusion for all read and write kernels
	for kernel in unique_accelerators:
		insert_includes(codegen_folder + "/mem_read_" + kernel + ".cpp", ["complex"])
		insert_includes(codegen_folder + "/mem_write_" + kernel + ".cpp", ["complex"])

	# TODO make it generic for several accelerators
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
	#comp.build(target=target_build)

	
	""" ---- Create System project ---- """
	print("----- Creating system project -----")

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
	liste_cfg_package = [
	f"dtb={codegen_folder}/vitis_platform/dtbo_output/pl.dtbo",
	]
	if(not cfg_obj.get_lines("package", "rootfs")):
		liste_cfg_package.append(f"rootfs={common_image}/rootfs.ext4")
	if(not cfg_obj.get_lines("package", "kernel_image")):
		liste_cfg_package.append(f"kernel_image={common_image}/Image")

	cfg_obj.add_lines('package', liste_cfg_package)

	connectivity_cfg = os.path.join(workspace, sys_proj_name, 'hw_link/container-link.cfg')
	
	# remove redundant IP instantiations for a fresh start 
	remove_nk_lines(connectivity_cfg)

	cfg_obj = client.get_config_file(connectivity_cfg)
	connections = []

	with open(codegen_folder + "/connectivity.cfg", "r") as file:
		# create the instances
		for acc in unique_accelerators:
			nb_instances = accelerators.count(acc)
			acc_instances = ",".join([acc + "_" + str(i+1) for i in range(nb_instances)])
			read_instances = ",".join(["mem_read_" + acc + "_" + str(i+1) for i in range(nb_instances)])
			write_instances = ",".join(["mem_write_" + acc + "_" + str(i+1) for i in range(nb_instances)])
			instances = [f"nk={acc}:{nb_instances}:{acc_instances}", f"nk=mem_read_{acc}:{nb_instances}:{read_instances}", f"nk=mem_write_{acc}:{nb_instances}:{write_instances}"]
			cfg_obj.add_lines("connectivity", instances)
		for line in file:
			connections.append(line.rstrip())
	cfg_obj.add_lines('connectivity', connections)

	print("----- Building system project -----")
	#status = platform.build()
	#status = proj.build(target=target_build)

	# je ne sais toujours pas comment créer automatiquement une config de lancement hélas
	#status = proj.create_launch_config(project_name="system_project", launch_config="system_project", target="system_project", build_output_path="system_project")

	print("Build finished")

if __name__ == "__main__":
	if len(sys.argv) != 8:    
		print("builder.py usage : python builder.py <component_name> <system project name> <common image path> <target> <platform name> <instrument true:false>")
		print(sys.argv)
		sys.exit(1)

	comp_name = sys.argv[1]
	sys_proj_name = sys.argv[2] 
	common_image = sys.argv[3]
	target = sys.argv[4]
	platform_name = sys.argv[5]
	target_build = sys.argv[6]
	instrument = sys.argv[7]
	main(comp_name, sys_proj_name, common_image, target, platform_name, target_build, instrument == "true")

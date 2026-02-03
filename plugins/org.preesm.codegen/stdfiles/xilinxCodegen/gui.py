import tkinter as tk
from tkinter import font
from tkinter import ttk
import sys
import subprocess

window = tk.Tk()

# default values (change whichever you want)
default_common_image = "~/xilinx-zynqmp-common-v2024.1/"
default_vitis_version = "2024.1"
default_vitis_dir = "tools/Xilinx/Vitis/2024.1/"
default_target_platform = ["kr260", "zcu104"]
default_target_build = ["hardmare emulation", "hardware"]
default_platform_name = "kr260_hardware_platform_full_150MHz"
default_vivado_plaftorm_path = "~/vivado_soc/build/vivado"
build_vivavo_platform = tk.IntVar()
instrument = tk.IntVar(value=1)
default_font = ("latin modern sans",) # latin modern sans

padx = 10
pady = 10

common_image = "~/xilinx-zynqmp-common-v2024.1/"
vitis_version = "2024.1"
vitis_dir = "tools/Xilinx/Vitis/2024.1/"
target_platform = "kr260"
target_build = "hardware"
vivado_plaftorm_path = ""
platform_name = "kr260_hardware_platform_full_200MHz"


def run_step1():
	print("step 1")
	retreive_values()
	
	if build_vivavo_platform.get():
		cmd_step1 = f"make step1 VERSION={vitis_version} VITIS_DIR={vitis_dir} COMMON_IMAGE={common_image} TARGET={target_platform}"
		res = subprocess.Popen([cmd_step1], shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, bufsize=0, text=True)
	else:	# on copie juste la plateforme
		res = subprocess.Popen([f"mkdir -p vivado_soc/build/vivado && cp {vivado_plaftorm_path + "/" + platform_name}.xsa vivado_soc/build/vivado"], shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, bufsize=0, text=True)
	for line in map(str.rstrip, res.stdout):
		print(line)
	print("step 1 fini")

def run_step2():
	print("step 2")
	retreive_values()
	#xsa_name = vivado_plaftorm_path.split("/")[-1].split(".")[0]
	cmd_step2 = f"make step2 VERSION={vitis_version} VITIS_DIR={vitis_dir} COMMON_IMAGE={common_image} TARGET={target_platform} XSA_NAME={platform_name}"
	res = subprocess.Popen([cmd_step2], shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, bufsize=0, text=True)
	for line in map(str.rstrip, res.stdout):
		print(line)

	print("step 2 fini")

def run_step3():
	print("step 3")
	retreive_values()
	cmd_step3 = f"make step3 VERSION={vitis_version} VITIS_DIR={vitis_dir} COMMON_IMAGE={common_image} TARGET={target_platform} PLATFORM_NAME={platform_name} TARGET_BUILD={target_build} " + ("INSTRUMENT=true" if instrument.get() else "")
	print(cmd_step3)
	res = subprocess.Popen([cmd_step3], shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, bufsize=0, text=True)
	for line in map(str.rstrip, res.stdout):
		print(line)
	print("step 3 fini")

def run_all():
	run_step1()
	run_step2()
	run_step3()

def retreive_values():
	global common_image, vitis_version, vitis_dir, target_platform, target_build, vivado_plaftorm_path, platform_name, target_build
	common_image = e_common_image.get()
	vitis_version = e_vitis_version.get()
	vitis_dir = e_vitis_dir.get()
	target_platform = list_target_platform.get()
	target_build = list_target_build.get()
	vivado_plaftorm_path = e_vivado_plaftorm_path.get()
	platform_name = e_platform_name.get().split(".")[0]
	target_build = "hw" if list_target_build.get() == "hardware" else "hw_emu"



window.tk.call('tk', 'scaling', 2.0)
window.geometry("800x800")

defaultFont = tk.font.nametofont("TkDefaultFont") ; defaultFont.configure(family=default_font, size=12)


# ----- Variables de make -----
variable_frame = tk.LabelFrame(window, text="Variables") ; variable_frame.grid(row=0, column=0, padx=padx, pady=pady, sticky="W")

tk.Label(variable_frame, text="Common image").grid(row=0, sticky="W")
tk.Label(variable_frame, text="Vitis version").grid(row=1, sticky="W")
tk.Label(variable_frame, text="Vitis directory").grid(row=2, sticky="W")
tk.Label(variable_frame, text="Target platform").grid(row=3, sticky="W")
tk.Label(variable_frame, text="platform name").grid(row=4, sticky="W")
tk.Label(variable_frame, text="Target build").grid(row=5, sticky="W")
tk.Label(variable_frame, text="Build vivado platform").grid(row=6, sticky="W")
tk.Label(variable_frame, text="Instrument execution").grid(row=7, sticky="W")
tk.Label(variable_frame, text="Vivado platform path").grid(row=8, sticky="W")

e_common_image  = tk.Entry(variable_frame, width=40, font=defaultFont) ; e_common_image.grid(row=0, column=1, padx=padx, pady=pady, sticky="W")  ; e_common_image.insert(0, default_common_image)
e_vitis_version = tk.Entry(variable_frame, width=40, font=defaultFont) ; e_vitis_version.grid(row=1, column=1, padx=padx, pady=pady, sticky="W") ; e_vitis_version.insert(0, default_vitis_version)
e_vitis_dir     = tk.Entry(variable_frame, width=40, font=defaultFont) ; e_vitis_dir.grid(row=2, column=1, padx=padx, pady=pady, sticky="W")     ; e_vitis_dir.insert(0, default_vitis_dir)

list_target_platform = ttk.Combobox(variable_frame, values=default_target_platform, font=defaultFont) ; list_target_platform.current(0) ; list_target_platform.grid(row=3, column=1, padx=padx, pady=pady, sticky="W")
e_platform_name    = tk.Entry(variable_frame, width=40, font=defaultFont) ; e_platform_name.grid(row=4, column=1, padx=padx, pady=pady, sticky="W")     ; e_platform_name.insert(0, default_platform_name)
list_target_build    = ttk.Combobox(variable_frame, values=default_target_build, font=defaultFont) 	  ; list_target_build.current(1) 	; list_target_build.grid(row=5, column=1, padx=padx, pady=pady, sticky="W")

b_build_vivavo_platform = tk.Checkbutton(variable_frame, text="", onvalue=1, offvalue=0, variable=build_vivavo_platform) ; b_build_vivavo_platform.grid(row=6, column=1, padx=padx, pady=pady)
b_instrument = tk.Checkbutton(variable_frame, text="", onvalue=1, offvalue=0, variable=instrument) ; b_instrument.grid(row=7, column=1, padx=padx, pady=pady)
e_vivado_plaftorm_path = tk.Entry(variable_frame, width=40, font=defaultFont) ; e_vivado_plaftorm_path.grid(row=8, column=1, padx=padx, pady=pady, sticky="W") ; e_vivado_plaftorm_path.insert(0, default_vivado_plaftorm_path) 


# ----- Actions de make -----
action_frame = tk.LabelFrame(window, text="Actions") ; action_frame.grid(row=2, column=0, padx=padx, pady=pady, sticky="W")

step1 = tk.Button(action_frame, text="plateforme vivado", command=run_step1) ; step1.grid(row=0, column=0, padx=padx, pady=pady)
step2 = tk.Button(action_frame, text="plateforme vitis", command=run_step2) ; step2.grid(row=0, column=1, padx=padx, pady=pady)
step3 = tk.Button(action_frame, text="implémentation", command=run_step3) ; step3.grid(row=0, column=2, padx=padx, pady=pady)
step3 = tk.Button(action_frame, text="run all", command=run_all)  ; step3.grid(row=1, column=1, padx=padx, pady=pady)


if len(sys.argv) > 1 and sys.argv[1] == "auto":
	run_all()
else:
	window.mainloop()

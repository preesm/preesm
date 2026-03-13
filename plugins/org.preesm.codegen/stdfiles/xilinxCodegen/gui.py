import sys
import subprocess
import tkinter as tk
from tkinter import font
from tkinter import ttk


# default values (change whichever you want)

default_common_image = "~/xilinx-zynqmp-common-v2024.1/"
default_target_platform = ["kr260", "zcu104"]
default_target_build = ["hardware", "hardware_emulation"]
default_platform_name = "kr260_hardware_platform_full_150MHz"
default_vivado_plaftorm_path = "~/vivado_soc/build/vivado"
default_build_vivavo_platform = False #tk.IntVar()
default_instrument = True #tk.IntVar(value=1)
default_font = ("latin modern sans",) # latin modern sans

gui_used = True

# just declaring them as global
e_common_image =  list_target_platform = list_target_build = e_vivado_plaftorm_path = e_platform_name = list_target_build = b_instrument = 0

padx = 10
pady = 10

# all-caps parameters will be passed as arguments to makefiles
# non-caps parameters will be used internally only
dic_params = {
	"COMMON_IMAGE": 		   "~/xilinx-zynqmp-common-v2024.1/",
	"TARGET": 	   "kr260",
	"TARGET_BUILD": 		   "hardware",
	"vivado_plaftorm_path":  "",
	"XSA_NAME": 		   "kr260_hardware_platform_full_200MHz",
	"build_vivavo_platform": False,
	"instrument": 		   True
}

# all caps parameters are passed to makefiles everytime to facilitate future parameter addition
def get_parameters():
	return " ".join(f"{key}={value}" for key, value in dic_params.items() if key.isupper())

def run_step1():
	print("step 1")
	if gui_used:
		retreive_values_gui()
	else: 
		retreive_value_auto()
	
	if dic_params["build_vivavo_platform"]:
		cmd_step1 = f"make step1 " + get_parameters()
		print(cmd_step1)
		res = subprocess.Popen([cmd_step1], shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, bufsize=0, text=True)
	else:	# on copie juste la plateforme
		print(f"mkdir -p vivado_soc/build/vivado && cp {dic_params["vivado_plaftorm_path"] + "/" + dic_params["XSA_NAME"]}.xsa vivado_soc/build/vivado")
		res = subprocess.Popen([f"mkdir -p vivado_soc/build/vivado && cp {dic_params["vivado_plaftorm_path"] + "/" + dic_params["XSA_NAME"]}.xsa vivado_soc/build/vivado"], shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, bufsize=0, text=True)
	for line in map(str.rstrip, res.stdout):
		print(line)
	print("step 1 fini")

def run_step2():
	print("step 2")
	if gui_used:
		retreive_values_gui()
	else: 
		retreive_value_auto()

	cmd_step2 = f"make step2 COMMON_IMAGE={dic_params["COMMON_IMAGE"]} TARGET={dic_params["TARGET"]} XSA_NAME={dic_params["XSA_NAME"]}"
	print(cmd_step2)
	res = subprocess.Popen([cmd_step2], shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, bufsize=0, text=True)
	for line in map(str.rstrip, res.stdout):
		print(line)
	print("step 2 fini")

def run_step3():
	print("step 3")
	if gui_used:
		retreive_values_gui()
	else: 
		retreive_value_auto()

	cmd_step3 = f"make step3 COMMON_IMAGE={dic_params["COMMON_IMAGE"]} TARGET={dic_params["TARGET"]} PLATFORM_NAME={dic_params["XSA_NAME"]} TARGET_BUILD={dic_params["TARGET_BUILD"]} " + ("INSTRUMENT=true" if dic_params["instrument"] else "")
	print(cmd_step3)
	res = subprocess.Popen([cmd_step3], shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, bufsize=0, text=True)
	for line in map(str.rstrip, res.stdout):
		print(line)
	print("step 3 fini")

def run_all():
	run_step1()
	run_step2()
	run_step3()

def retreive_values_gui():
	dic_params["build_vivavo_platform"] = default_build_vivavo_platform.get()
	dic_params["COMMON_IMAGE"] 		 = e_common_image.get()
	dic_params["TARGET"] 	 = list_target_platform.get()
	dic_params["vivado_plaftorm_path"] = e_vivado_plaftorm_path.get()
	dic_params["XSA_NAME"] 		 = e_platform_name.get().split(".")[0]
	dic_params["TARGET_BUILD"] 		 = "hw" if list_target_build.get() == "hardware" else "hw_emu"
	dic_params["instrument"] 			 = default_instrument.get()
	print("parameters : ", dic_params)

def retreive_value_auto():	
	dic_params["build_vivavo_platform"] = default_build_vivavo_platform
	dic_params["COMMON_IMAGE"] = default_common_image
	dic_params["TARGET"] = "kr260"
	dic_params["TARGET_BUILD"] = "hw"
	dic_params["vivado_plaftorm_path"] = default_vivado_plaftorm_path
	dic_params["XSA_NAME"] = default_platform_name
	dic_params["instrument"]  = default_instrument
	print("parameters : ", dic_params)


def run_gui():
	global e_common_image, list_target_platform, list_target_build, e_vivado_plaftorm_path, e_platform_name, list_target_build, default_build_vivavo_platform, b_instrument
	global default_build_vivavo_platform, default_instrument
	print("running as gui")

	window = tk.Tk()

	window.tk.call('tk', 'scaling', 2.0)
	window.geometry("800x800")

	defaultFont = tk.font.nametofont("TkDefaultFont") ; defaultFont.configure(family=default_font, size=12)


	# ----- Variables de make -----
	variable_frame = tk.LabelFrame(window, text="Variables") ; variable_frame.grid(row=0, column=0, padx=padx, pady=pady, sticky="W")

	tk.Label(variable_frame, text="Common image").grid(row=0, sticky="W")
	tk.Label(variable_frame, text="Target platform").grid(row=3, sticky="W")
	tk.Label(variable_frame, text="platform/xsa name").grid(row=4, sticky="W")
	tk.Label(variable_frame, text="Target build").grid(row=5, sticky="W")
	tk.Label(variable_frame, text="Build vivado platform").grid(row=6, sticky="W")
	tk.Label(variable_frame, text="Instrument execution").grid(row=7, sticky="W")
	tk.Label(variable_frame, text="Vivado platform path").grid(row=8, sticky="W")

	e_common_image  = tk.Entry(variable_frame, width=40, font=defaultFont) ; e_common_image.grid(row=0, column=1, padx=padx, pady=pady, sticky="W")  ; e_common_image.insert(0, default_common_image)

	list_target_platform = ttk.Combobox(variable_frame, values=default_target_platform, font=defaultFont) ; list_target_platform.current(0) ; list_target_platform.grid(row=3, column=1, padx=padx, pady=pady, sticky="W")
	e_platform_name    	 = tk.Entry(variable_frame, width=40, font=defaultFont) ; e_platform_name.grid(row=4, column=1, padx=padx, pady=pady, sticky="W")     ; e_platform_name.insert(0, default_platform_name)
	list_target_build    = ttk.Combobox(variable_frame, values=default_target_build, font=defaultFont) 	  ; list_target_build.current(0) 	; list_target_build.grid(row=5, column=1, padx=padx, pady=pady, sticky="W")

	default_build_vivavo_platform = tk.IntVar()
	default_instrument = tk.IntVar(value=1)
	b_build_vivavo_platform = tk.Checkbutton(variable_frame, text="", onvalue=1, offvalue=0, variable=default_build_vivavo_platform) ; b_build_vivavo_platform.grid(row=6, column=1, padx=padx, pady=pady)
	b_instrument 			= tk.Checkbutton(variable_frame, text="", onvalue=1, offvalue=0, variable=default_instrument) ; b_instrument.grid(row=7, column=1, padx=padx, pady=pady)
	e_vivado_plaftorm_path  = tk.Entry(variable_frame, width=40, font=defaultFont) ; e_vivado_plaftorm_path.grid(row=8, column=1, padx=padx, pady=pady, sticky="W") ; e_vivado_plaftorm_path.insert(0, default_vivado_plaftorm_path) 

	# ----- Actions de make -----
	action_frame = tk.LabelFrame(window, text="Actions") ; action_frame.grid(row=2, column=0, padx=padx, pady=pady, sticky="W")

	step1 = tk.Button(action_frame, text="plateforme vivado", command=run_step1) ; step1.grid(row=0, column=0, padx=padx, pady=pady)
	step2 = tk.Button(action_frame, text="plateforme vitis", command=run_step2) ; step2.grid(row=0, column=1, padx=padx, pady=pady)
	step3 = tk.Button(action_frame, text="implémentation", command=run_step3) ; step3.grid(row=0, column=2, padx=padx, pady=pady)
	step3 = tk.Button(action_frame, text="run all", command=run_all)  ; step3.grid(row=1, column=1, padx=padx, pady=pady)

	window.mainloop()

def run_auto():
	run_all()


if __name__ == "__main__":
	if len(sys.argv) != 2:
		print("Usage : python gui.py < gui | auto >")
		exit(1)

	if sys.argv[1] == "auto":
		gui_used = False
		run_auto()
	else:
		gui_used = True
		run_gui()

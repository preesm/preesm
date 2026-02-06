import lxml.etree as et
import sys
import os
from xml.dom import minidom

if len(sys.argv) != 3:
    print("usage : python edit_scenario.py <scenario_file> <top_graph_name>")
    exit()

scenario_file = os.path.abspath(sys.argv[1])
top_graph_name = sys.argv[2]


fpga_type = "FPGA"

# timing order : [EXECUTION_TIME, INITIATION_INTERVAL]
# thus [0] >= [1]
# entry format : { "opname": PE_name, "time": [EXE_TIME,II], "vertexname": "full_path_name" },
timings_data = [
    { "opname": fpga_type,"time": [303,282],"vertexname": "fft2d/FFT1DColumns/FFT", },
    { "opname": fpga_type,"time": [146,146],"vertexname": "fft2d/FFT1DColumns/downscale" },
    { "opname": fpga_type,"time": [258,256],"vertexname": "fft2d/FFT1DColumns/maxStream" },
    { "opname": fpga_type,"time": [146,146],"vertexname": "fft2d/FFT1DColumns/upscale" },
    { "opname": fpga_type,"time": [303,282],"vertexname": "fft2d/FFT1DLines/FFT" },
    { "opname": fpga_type,"time": [146,146],"vertexname": "fft2d/FFT1DLines/downscale" },
    { "opname": fpga_type,"time": [258,258],"vertexname": "fft2d/FFT1DLines/maxStream" },
    { "opname": fpga_type,"time": [146,146],"vertexname": "fft2d/FFT1DLines/upscale" },
    # Ajoutez autant de lignes que nécessaire
]

# 1. Charger le fichier XML
parser = et.XMLParser(remove_blank_text=True)
tree = et.parse(scenario_file, parser)
root = tree.getroot()

# 2. Trouver ou créer le tag <timings>
timings = root.find("timings")
if timings is None:
    timings = et.SubElement(root, "timings")

# 3. Ajouter chaque ligne de timing
for data in timings_data:
    timing = et.SubElement(
        timings,
        "timing",
        {
            "opname": data["opname"],
            "time": str(data["time"][0]),
            "timingtype": "EXECUTION_TIME",
            "vertexname": data["vertexname"],
        },
    )
    timing = et.SubElement(
        timings,
        "timing",
        {
            "opname": data["opname"],
            "time": str(data["time"][1]),
            "timingtype": "INITIATION_INTERVAL",
            "vertexname": data["vertexname"],
        },
    )


# 4. ajouter les acteurs mappés sur FPGA
constraints = root.find("constraints")
if constraints is None:
    constraints = et.SubElement(root, 'constraints')
constraint_group = constraints.find('constraintGroup')
if constraint_group is None:
    constraint_group = et.SubElement(constraints, 'constraintGroup')
for actor in [actor for actor in timings_data if actor["opname"] == fpga_type]:
    et.SubElement(constraint_group, 'task', {'name': actor["vertexname"]})


tree.write(scenario_file, encoding="utf-8", xml_declaration=True, pretty_print=True)

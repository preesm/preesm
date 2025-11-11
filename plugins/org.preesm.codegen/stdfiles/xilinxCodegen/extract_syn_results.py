import xml.etree.ElementTree as ET
import sys
import os
import vitis
import shutil
import re

def extract_latencies_and_intervals(xml_path):
    tree = ET.parse(xml_path)
    root = tree.getroot()

    # Dictionnaire pour stocker les résultats
    results = {}

    # Recherche dans la section PerformanceEstimates / SummaryOfOverallLatency
    summary = root.find(".//PerformanceEstimates/SummaryOfOverallLatency")
    if summary is not None:
        results["Best-caseLatency"] = summary.findtext("Best-caseLatency")
        results["Average-caseLatency"] = summary.findtext("Average-caseLatency")
        results["Worst-caseLatency"] = summary.findtext("Worst-caseLatency")

        results["Best-caseRealTimeLatency"] = summary.findtext("Best-caseRealTimeLatency")
        results["Average-caseRealTimeLatency"] = summary.findtext("Average-caseRealTimeLatency")
        results["Worst-caseRealTimeLatency"] = summary.findtext("Worst-caseRealTimeLatency")

        results["Interval-min"] = max(int(summary.findtext("Interval-min")),1)
        results["Interval-max"] = max(int(summary.findtext("Interval-max")),1)

    return results


def extract_metrics(xml_path):
    """Extract Worst-caseLatency and Interval-max from a HLS synthesis XML file."""
    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()

        summary = root.find(".//PerformanceEstimates/SummaryOfOverallLatency")
        if summary is None:
            return None

        data = {}
        data["file"] = os.path.basename(xml_path)
        data["Worst-caseLatency"] = max(int(summary.findtext("Worst-caseLatency")), 1)
        data["Interval-max"] = max(int(summary.findtext("Interval-max")),1)

        # find the top kernel name
        top_name = root.findtext(".//UserAssignments/TopModelName")
        if top_name:
            data["Function"] = top_name
        else:
            data["Function"] = os.path.splitext(os.path.basename(xml_path))[0]

        return data

    except ET.ParseError:
        print("Warning: could not parse {}".format(xml_path))
        return None


if __name__ == "__main__":
    if len(sys.argv) != 1:
        print("usage : python3 extract_syn_results.py")
        sys.exit(1)

    generated_folder = os.getcwd()
    code_folder = os.path.abspath(generated_folder + "/..")
    clusters_file = os.path.abspath(generated_folder + "/clusters_list")
    workspace = os.path.abspath(generated_folder + "/timings")

    # get the clusters' list
    clusters_list = []
    with open(clusters_file, "r") as f:
        for line in f:
            clusters_list.append(line.rstrip())
    print("list of cluster kernels : ", ", ".join(clusters_list))

    
    shutil.rmtree(workspace + "/", ignore_errors=True)
    os.makedirs(workspace)
    
    client = vitis.create_client()
    client.set_workspace(path=workspace)
    
    for kernel in clusters_list:
        # create pseudo hls component
        comp = client.create_hls_component(name = kernel,template = "empty_hls_component")
        cfg_path = os.path.join(workspace, kernel, 'hls_config.cfg')
        cfg_obj = client.get_config_file(cfg_path)
        cfg_obj.set_value("", key="part", value="xczu7ev-ffvc1156-2-e") # any target is fine since we just synthesize
        liste_hls_usercmake = [
        f"syn.top={kernel}",
        f"syn.file={generated_folder}/{kernel}.cpp",
        f"syn.cflags=-I{code_folder}/include -I{generated_folder}/",
        ]
        cfg_obj.add_lines('hls', liste_hls_usercmake)

        comp.run(operation="SYNTHESIS")
        # Operation to be executed. Valid types are 
        # ‘C_SIMULATION’, ‘SYNTHESIS’, ‘CO_SIMULATION’, ‘IMPLEMENTATION’, ‘ANALYSIS_OPTIMIZATION’, and ‘PACKAGE’

    vitis.dispose()
    

    functiontoactor = {}
    for kernel in clusters_list:
        # first, extract the actors names, along with the functions they are instanciated with
        # I wanted to map the synthesized module to the original task/actor name, but I couldn't find a way...
        with open(generated_folder + "/" + kernel + ".cpp", "r") as f:
            task_lines = [line.strip() for line in f if line.lstrip().startswith("hls_thread_local hls::task")]
            pattern = r'hls_thread_local\s+hls::task\s+(\w+)\s*\(\s*([^,\s]+)'
            for line in task_lines:
                match = re.search(pattern, line)
                if match:
                    task_name = match.group(1)
                    function_name = re.sub(r'<[^>]*>?', '', match.group(2)) # get rid of potential template params in name
                    functiontoactor[function_name] = task_name

        xml_file = workspace + "/" + kernel + "/" + kernel + "/hls/syn/report/" + kernel + "_csynth.xml"

        metrics = extract_latencies_and_intervals(xml_file)

        print(f"\n=== HlS estimated timings of cluster kernel {kernel} ===")
        for key, value in metrics.items():
            print(f"\t{key}: {value}")

        results = []
        longest_name = 10
        longest_lat_line = 10
        longest_interval_line = 10
        report_dir = workspace + "/" + kernel + "/" + kernel + "/hls/syn/report/"
        for fname in os.listdir(report_dir):
            if fname.lower().endswith(".xml"):
                path = os.path.join(report_dir, fname)
                metrics = extract_metrics(path)
                if metrics:
                    results.append(metrics)
                    longest_name = max(longest_name, len(metrics.get("Function", "?")))
                    longest_lat_line = max(longest_lat_line, len(str(metrics.get("Worst-caseLatency", "?"))))
                    longest_interval_line = max(longest_name, len(str(metrics.get("Interval-max", "?"))))

         # Print result table
        print(f"\n=== HLS timings of sub-actors in cluster {kernel} (Worst-case Latency & Interval-max) ===")
        print("Tasks and associated function :")
        for key, value in functiontoactor.items():
            print(f"{key}: {value}")

        print("-"*(longest_lat_line+longest_lat_line+longest_interval_line+2))
        print(f'{"Function":<{longest_name}}  {"Latency":<{longest_lat_line}}  {"Interval":<{longest_interval_line}}')
        print("-"*(longest_lat_line+longest_lat_line+longest_interval_line+2))

        for r in results:
            print(f'{r.get("Function", "?"):<{longest_name}}  {r.get("Worst-caseLatency"):<{longest_lat_line}}  {r.get("Interval-max", "-"):<{longest_interval_line}}')

        print("-"*(longest_lat_line+longest_lat_line+longest_interval_line+2))
        print("\n")

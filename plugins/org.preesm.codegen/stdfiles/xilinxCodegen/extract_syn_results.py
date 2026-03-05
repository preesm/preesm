import xml.etree.ElementTree as ET
import sys
import os
import shutil
import re
import pandas as pd


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


def extract_metrics(folder_path, xml_path, cluster_name):
    """Extract per-first-level-module Worst-caseLatency and Interval-max 
    from a HLS synthesis XML file."""
    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()

        # Build a lookup: module name -> latency data from ModuleInformation
        module_latency = {}
        for module in root.findall(".//ModuleInformation/Module"):
            name = module.findtext("n")
            summary = module.find("PerformanceEstimates/SummaryOfOverallLatency")
            if name and summary is not None:
                worst = summary.findtext("Worst-caseLatency")
                interval = summary.findtext("Interval-max")
                # Interval-max may not exist at module level; fall back to PipelineInitiationInterval
                if interval is None:
                    interval = summary.findtext("PipelineInitiationInterval")
                module_latency[name] = {
                    "Worst-caseLatency": max(int(worst), 1) if worst else 1,
                    "Interval-max": max(int(interval), 1) if interval else 1,
                }

        # Get first-level module names from RTLDesignHierarchy/TopModule
        top_module = root.find(".//RTLDesignHierarchy/TopModule")
        first_level_names = []
        if top_module is not None:
            for instance in top_module.findall("InstancesList/Instance"):
                mod_name = instance.findtext("ModuleName")
                if mod_name:
                    first_level_names.append(mod_name)

        filename = os.path.basename(xml_path)
        results = []
        for name in first_level_names:
            report_file_path = os.path.join(folder_path, name + "_csynth.xml")
            result_tree = ET.parse(report_file_path)
            result_root = result_tree.getroot()

            lat = module_latency.get(name, {"Worst-caseLatency": max(int(result_root.findtext(".//Worst-caseLatency")), 1), "Interval-max": max(int(result_root.findtext(".//Interval-max")), 1)})
            results.append({
                "file": filename,
                "actor": cluster_name + "/" + name,
                "latency": lat["Worst-caseLatency"],
                "II": lat["Interval-max"],
            })

        return results  # returns a list of dicts, one per first-level module

    except ET.ParseError:
        print("Warning: could not parse {}".format(xml_path))
        return []


def update_timings_xlsx(xlsx_path, timings):
    try:
        df = pd.read_excel(xlsx_path, index_col=0)
    except FileNotFoundError:
        print(f"creating file {xlsx_path}")
        df = pd.DataFrame(columns=["FPGA-latency", "FPGA-II"])
        df.index.name = "Actors"
    
    for dic in timings:
        actor = dic.get("actor")
        latency = dic.get("latency")
        II = dic.get("II")
        df.loc[actor, "FPGA-latency"] = latency
        df.loc[actor, "FPGA-II"] = II

    df["FPGA-latency"] = df["FPGA-latency"].astype("Int64")
    df["FPGA-II"] = df["FPGA-II"].astype("Int64")
    df.to_excel(xlsx_path)

def update_timings_xls(xls_path, timings):
    try:
        df = pd.read_excel(xls_path, index_col=0)
    except FileNotFoundError:
        print(f"creating file {xls_path}")
        df = pd.DataFrame(columns=["FPGA-latency", "FPGA-II"])
        df.index.name = "Actors"
    
    for dic in timings:
        actor = dic.get("actor")
        latency = dic.get("latency")
        II = dic.get("II")
        df.loc[actor, "FPGA-latency"] = latency
        df.loc[actor, "FPGA-II"] = II

    df["FPGA-latency"] = df["FPGA-latency"].astype("Int64")
    df["FPGA-II"] = df["FPGA-II"].astype("Int64")
    df.to_excel(xls_path)

def update_timings_csv(csv_path, timings):
    """ généré par claude, pas encore testé """
    try:
        df = pd.read_csv(csv_path, index_col=0, sep=";")
    except FileNotFoundError:
        print(f"creating file {csv_path}")
        df = pd.DataFrame(columns=["FPGA-latency", "FPGA-II"])
        df.index.name = "Actors"
    
    for dic in timings:
        actor = dic.get("actor")
        latency = dic.get("latency")
        II = dic.get("II")
        df.loc[actor, "FPGA-latency"] = int(latency)
        df.loc[actor, "FPGA-II"] = int(II)

    df["FPGA-latency"] = df["FPGA-latency"].astype("Int64")
    df["FPGA-II"] = df["FPGA-II"].astype("Int64")
    df.to_csv(csv_path, sep=";")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("usage : python3 extract_syn_results.py <timing file path>")
        sys.exit(1)

    generated_folder = os.getcwd()
    code_folder = os.path.abspath(generated_folder + "/..")
    clusters_file = os.path.abspath(generated_folder + "/clusters_list")
    workspace = os.path.abspath(generated_folder + "/timings")

    timing_file_path = os.path.abspath(sys.argv[1])
    ext = os.path.splitext(timing_file_path)[1]

    # get the clusters' list
    clusters_list = []
    with open(clusters_file, "r") as f:
        for line in f:
            clusters_list.append(line.rstrip())
    print("list of cluster kernels : ", ", ".join(clusters_list))

    
    import vitis
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
        xml_file = workspace + "/" + kernel + "/" + kernel + "/hls/syn/report/" + kernel + "_csynth.xml"

        # first extract the timing of the overall FPGA cluster
        metrics = extract_latencies_and_intervals(xml_file)

        print(f"\n=== HlS estimated timings of cluster kernel {kernel} ===")
        for key, value in metrics.items():
            print(f"\t{key}: {value}")

        # then we extract the timings of each individual actor
        results = []
        longest_name = len(kernel) + 30
        longest_lat_line = 10
        longest_interval_line = 10
        report_dir = workspace + "/" + kernel + "/" + kernel + "/hls/syn/report/"

        results = extract_metrics(report_dir, os.path.join(report_dir, "csynth.xml"), kernel) 

        if ext == ".xlsx":
            update_timings_xlsx(timing_file_path, results)
        elif ext == ".xls":
            update_timings_xls(timing_file_path, results)
        elif ext == ".csv":
            update_timings_csv(timing_file_path, results)
        elif not(ext):
            update_timings_xlsx(timing_file_path + ".xlsx", results)
            update_timings_csv(timing_file_path + ".csv", results)
        else:
            print(f"error : extension {ext} not supported in path to timings file {timing_file_path}")

         # Print result table
        print(f"\n=== HLS timings of sub-actors in cluster {kernel} (Worst-case Latency & Interval-max) ===")

        print("-"*(longest_name+longest_lat_line+longest_interval_line+20))
        print(f'{"actor":<{longest_name}}  {"Latency":<{longest_lat_line}}  {"Interval":<{longest_interval_line}}')
        print("-"*(longest_name+longest_lat_line+longest_interval_line+20))

        for r in results:
            print(f'{r.get("actor", "?"):<{longest_name}}  {r.get("latency"):<{longest_lat_line}}  {r.get("II", "-"):<{longest_interval_line}}')

        print("-"*(longest_name+longest_lat_line+longest_interval_line+20))
        print("\n")

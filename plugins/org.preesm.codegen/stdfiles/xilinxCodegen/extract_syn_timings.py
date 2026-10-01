"""
Extract per-actor HLS timings after synthesis.

Design rule: never guess, never silently substitute a default. Any doubt about
what the reports say is a hard error, because a wrong timing that looks
plausible costs far more than a run that stops.

Where the numbers come from
---------------------------
Every module has its own <module>_csynth.xml beside the top-level one, and its
ROOT <PerformanceEstimates><SummaryOfOverallLatency> describes that module
itself. Those are the figures used here:

    latency = Worst-caseLatency
    II      = Interval-max

Both are worst case, so they are consistent with one another.

The top-level csynth.xml is read for the module hierarchy and to cross-check
each latency. The hierarchy cannot be dispensed with: far more modules have a
report than there are actors, and name alone does not separate them. In
color_filter, 18 modules have a report and 8 are actors; among the other 10,
"rgb_to_h" is a SUB-module of rgbtoh_CLUSTEREDIN_COLOR_FILTER, yet its plain
name is indistinguishable from a genuine special actor such as "Broadcast_dwt"
or "Duplicate". Enumerating report files alone would emit a row for it, with a
submodule's timings, and no naming rule could catch that. Only the hierarchy
says which modules sit at the first level.

Two traps this avoids, each of which silently produced wrong numbers before:

  * <PipelineInitiationInterval> in the AGGREGATE report is not a module's
    initiation interval. For an unpipelined module it is a range string such
    as "948 ~ 4728"; for a "loop auto-rewind" module it is the inner loop's
    II. On DWT_2D_fpga it disagreed with the module's own report on 35 of 44
    modules, while Worst-caseLatency agreed on all 44.
  * the aggregate csynth.rpt "Interval" column also differs from what a module
    reports about itself -- 12 against 10 for downsample_row -- so the
    module's own report is authoritative and the aggregate one is not read.
"""

import xml.etree.ElementTree as ET
import sys
import os
import shutil
import re
import glob
import pandas as pd


class ReportError(Exception):
    """The reports cannot be interpreted with certainty. Always fatal."""


# In the report folder Vitis writes:
#   csynth.xml            the aggregate report: hierarchy + every module
#   <module>_csynth.xml   one per module, including the top module itself
# The top module therefore has BOTH, and they are not interchangeable:
# DWT_2D_fpga_csynth.xml has no RTLDesignHierarchy and no ModuleInformation.
AGGREGATE_REPORT = "csynth.xml"


# Modules that are never PREESM actors and are skipped.
#
#   read_/write_ : the memory synchronisers PREESM emits around the graph. The
#       boundary check keeps an actor whose name merely contains those letters,
#       such as "thread_pool", from being skipped.
#   entry_proc, KPN* : created by Vitis / PREESM, not actors.
#   *_Pipeline_* : created by Vitis for one loop inside an actor, so it
#       describes part of an actor rather than the actor itself.
SKIP_IO = re.compile(r"(^|[^A-Za-z])(read|write)_")
SKIP_GENERATED = re.compile(r"(^entry_proc$|^KPN(_\d+)?$|_Pipeline_)")


def skip_reason(module_name, top_name):
    """Why this module is not an actor, or None if it is one."""
    if module_name == top_name:
        return "the cluster's own top module"
    if SKIP_IO.search(module_name):
        return "a read/write memory synchroniser"
    if SKIP_GENERATED.search(module_name):
        return "auto-generated"
    return None

# A name that looks like a mangled C++ template instantiation means the PREESM
# wrapper was inlined away and the actor's identity is unrecoverable.
MANGLED = re.compile(r"(_parameterized|_ap_uint|_ap_fixed|_ap_int|_\d+_\d+|_s$|_\d+$)")


# --------------------------------------------------------------------------- #
# reading one report
# --------------------------------------------------------------------------- #
def load_xml(path):
    if not os.path.isfile(path):
        raise ReportError("{} not found".format(path))
    try:
        return ET.parse(path).getroot()
    except ET.ParseError as exc:
        raise ReportError("{} is not valid XML: {}".format(path, exc))


def _required_int(summary, tag, path, module_name):
    text = summary.findtext(tag)
    if text is None or not text.strip():
        raise ReportError(
            "{}: module {!r} has no <{}> in its root summary".format(
                path, module_name, tag
            )
        )
    text = text.strip()
    try:
        return int(text)
    except ValueError:
        raise ReportError(
            "{}: module {!r} has <{}> = {!r}, which is not a plain integer. A "
            "range such as '948 ~ 4728' means the module has no single value "
            "and the actor cannot be characterised.".format(
                path, module_name, tag, text
            )
        )


def module_report_path(folder_path, module_name):
    return os.path.join(folder_path, module_name + "_csynth.xml")


def read_module_report(folder_path, module_name):
    """
    (latency, interval) from a module's own <module>_csynth.xml.

    Taken from the ROOT summary -- the top-most one, which describes this
    module rather than any submodule of it.
    """
    path = module_report_path(folder_path, module_name)
    if not os.path.isfile(path):
        raise ReportError(
            "no report for module {!r}: {} does not exist. Every actor must "
            "have its own csynth.xml beside the top-level one.".format(
                module_name, path
            )
        )
    root = load_xml(path)

    summary = root.find("./PerformanceEstimates/SummaryOfOverallLatency")
    if summary is None:
        raise ReportError(
            "{}: no root <PerformanceEstimates><SummaryOfOverallLatency>. A "
            "nested summary would describe a submodule, not this module, so "
            "it is not used.".format(path)
        )

    latency = _required_int(summary, "Worst-caseLatency", path, module_name)
    interval = _required_int(summary, "Interval-max", path, module_name)
    if latency < 0 or interval < 0:
        raise ReportError(
            "{}: module {!r} has a negative timing (latency {}, interval {})"
            .format(path, module_name, latency, interval)
        )

    # A differing min and max means the interval varies with the data. The
    # worst case is taken, consistent with Worst-caseLatency, and said aloud.
    low_text = summary.findtext("Interval-min")
    if low_text is not None and low_text.strip():
        try:
            low = int(low_text.strip())
        except ValueError:
            raise ReportError(
                "{}: module {!r} has a non-integer Interval-min {!r}".format(
                    path, module_name, low_text.strip()
                )
            )
        if low != interval:
            print("note: {} interval varies from {} to {}; taking the worst "
                  "case {}".format(module_name, low, interval, interval))

    return latency, interval


# --------------------------------------------------------------------------- #
# the top-level report: hierarchy and cross-check
# --------------------------------------------------------------------------- #
def xml_top_name(root):
    name = root.findtext(".//RTLDesignHierarchy/TopModule/ModuleName")
    if not name or not name.strip():
        raise ReportError("no RTLDesignHierarchy/TopModule/ModuleName in the report")
    return name.strip()


def xml_latencies(root):
    """{module name: Worst-caseLatency} for every module that states one."""
    latencies = {}
    for module in root.findall(".//ModuleInformation/Module"):
        name = module.findtext("Name")
        if not name or not name.strip():
            raise ReportError(
                "a <Module> entry has no <Name>; this report's schema is not "
                "the one this script understands"
            )
        name = name.strip()
        text = module.findtext(
            "PerformanceEstimates/SummaryOfOverallLatency/Worst-caseLatency"
        )
        if text is None or not text.strip():
            continue
        try:
            latencies[name] = int(text.strip())
        except ValueError:
            raise ReportError(
                "module {!r} has a non-integer Worst-caseLatency {!r}".format(
                    name, text.strip()
                )
            )
    if not latencies:
        raise ReportError(
            "no module latencies in the top-level XML. Older reports name the "
            "tag <n> rather than <Name>; this script requires <Name>."
        )
    return latencies


KPN_MODULE = re.compile(r"^KPN(_\d+)?$")


def candidate_instances(root):
    """
    [(InstName, ModuleName)] for the actor-level modules of the design.

    A KPN module is a container PREESM may wrap the actors in, so it is
    treated as transparent: its children are lifted to the actor level rather
    than replacing it. That covers all three shapes seen in practice --

        no KPN at all           the top module's own children are the actors
        one KPN holding all     its children are the actors
        a KPN alongside actors  both are collected

    -- and the last one is why the KPN cannot simply be used instead of the
    top module: an actor sitting beside the KPN would be dropped without a
    word. InstName is carried along because it is the only thing that tells
    two instances of one shared module apart.
    """
    top = root.find(".//RTLDesignHierarchy/TopModule")
    if top is None:
        raise ReportError("no RTLDesignHierarchy/TopModule in the report")

    instances = []
    containers = []

    def collect(node, path):
        for instance in node.findall("InstancesList/Instance"):
            module = instance.findtext("ModuleName")
            if not module or not module.strip():
                raise ReportError(
                    "an instance under {} has no ModuleName".format(
                        " / ".join(path) or "the top module"
                    )
                )
            module = module.strip()
            inst_name = (instance.findtext("InstName") or "").strip()
            if KPN_MODULE.match(module):
                if module in path:
                    raise ReportError(
                        "the hierarchy loops: {} contains itself".format(module)
                    )
                containers.append(module)
                collect(instance, path + [module])
            else:
                instances.append((inst_name, module))

    collect(top, [])

    if containers:
        print("note: descended into KPN container(s): {}".format(
            ", ".join(sorted(set(containers)))))
    else:
        print("note: no KPN module; taking the first-level modules of {!r}"
              .format(xml_top_name(root)))

    if not instances:
        raise ReportError(
            "no modules found below {!r}".format(xml_top_name(root))
        )

    duplicates = [n for n in {i for i, _ in instances}
                  if n and [i for i, _ in instances].count(n) > 1]
    if duplicates:
        raise ReportError(
            "instance name(s) {} appear more than once in the hierarchy; the "
            "report is ambiguous".format(", ".join(sorted(duplicates)))
        )
    return instances


# --------------------------------------------------------------------------- #
# actor naming
# --------------------------------------------------------------------------- #
def actor_name(module_name):
    """PREESM actor name for a module, or raise if it cannot be recovered."""
    if "_CLUSTEREDIN_" in module_name:
        name = module_name.split("_CLUSTEREDIN_")[0]
        if not name:
            raise ReportError(
                "module {!r} starts with _CLUSTEREDIN_".format(module_name)
            )
        return name

    if MANGLED.search(module_name):
        raise ReportError(
            "module {!r} carries no _CLUSTEREDIN_ tag and looks like a mangled "
            "template instantiation, so its PREESM actor name cannot be "
            "recovered. Emit '#pragma HLS inline off' on the actor wrappers "
            "and re-synthesise.".format(module_name)
        )

    # A PREESM special actor (Broadcast, Duplicate, ...) is emitted directly in
    # the top file, so its module name already is the actor name.
    return module_name


# --------------------------------------------------------------------------- #
# extraction
# --------------------------------------------------------------------------- #
def report_unused_files(folder_path, actor_modules, top_name):
    """
    Say which per-module reports in the folder were not used as actors.

    They are the nested sub-modules and the skipped ones; naming them keeps
    the choice visible instead of leaving files silently ignored.
    """
    suffix = "_csynth.xml"
    present = set()
    for path in glob.glob(os.path.join(folder_path, "*" + suffix)):
        present.add(os.path.basename(path)[: -len(suffix)])
    unused = sorted(present - set(actor_modules) - {top_name})
    if unused:
        print("note: {} other report(s) in {} are sub-modules or skipped, not "
              "actors: {}".format(len(unused), folder_path, ", ".join(unused)))


def extract_metrics(folder_path, xml_path, cluster_name):
    """
    One row per actor, each read from that actor's own module report.

    The top-level report supplies the module hierarchy and cross-checks every
    latency; the timings themselves come from <module>_csynth.xml.
    """
    root = load_xml(xml_path)

    top = xml_top_name(root)
    if top != cluster_name:
        raise ReportError(
            "the report describes {!r} but the cluster is {!r}".format(
                top, cluster_name
            )
        )

    latencies = xml_latencies(root)
    instances = candidate_instances(root)

    # drop the modules that are not actors, saying which and why
    actors = []
    for inst_name, module in instances:
        reason = skip_reason(module, top)
        if reason is None:
            actors.append((inst_name, module))
        else:
            print("skipping {}: {}".format(module, reason))
    if not actors:
        raise ReportError(
            "every first-level module was skipped as non-actor; nothing to "
            "extract from {}".format(xml_path)
        )

    # One module instantiated twice means two actors share a function. Their
    # reports are identical, so which timing belongs to which actor cannot be
    # established from the reports alone.
    by_module = {}
    for inst_name, module in actors:
        by_module.setdefault(module, []).append(inst_name)
    shared = {m: n for m, n in by_module.items() if len(n) > 1}
    if shared:
        module, inst_names = sorted(shared.items())[0]
        raise ReportError(
            "module {!r} is instantiated {} times (as {}), so two or more "
            "actors share one function and the report cannot say which timing "
            "belongs to which actor. Give each actor its own wrapper so the "
            "modules stay distinct.".format(
                module, len(inst_names), ", ".join(inst_names)
            )
        )

    report_unused_files(folder_path, [m for _, m in actors], top)

    results = []
    seen = {}
    for inst_name, module in actors:
        latency, interval = read_module_report(folder_path, module)

        if module not in latencies:
            raise ReportError(
                "actor {!r} has no Worst-caseLatency in the top-level report, "
                "so its own report cannot be cross-checked".format(module)
            )
        if latencies[module] != latency:
            raise ReportError(
                "actor {!r}: latency is {} in {} but {} in the top-level "
                "report".format(
                    module, latency, module_report_path(folder_path, module),
                    latencies[module]
                )
            )

        name = actor_name(module)
        if name in seen:
            raise ReportError(
                "modules {!r} and {!r} both map to actor {!r}; their timings "
                "would overwrite each other".format(seen[name], module, name)
            )
        seen[name] = module

        # PREESM needs strictly positive figures. A latency of 0 is real (a
        # free-running broadcast) and is raised to 1 here -- reported, not
        # applied silently.
        if latency == 0:
            print("note: {} has latency 0 in its report, recorded as 1".format(module))
        if interval == 0:
            print("note: {} has interval 0 in its report, recorded as 1".format(module))

        results.append({
            "file": os.path.basename(module_report_path(folder_path, module)),
            "actor": cluster_name + "/" + name,
            "latency": max(latency, 1),
            "II": max(interval, 1),
        })

    return results


def extract_latencies_and_intervals(xml_path):
    """
    Cluster-level figures, from the aggregate report's root summary.

    The top module also has a report of its own; when it is present the two
    are cross-checked, since they should describe the same thing.
    """
    root = load_xml(xml_path)
    top = xml_top_name(root)

    summary = root.find("./PerformanceEstimates/SummaryOfOverallLatency")
    if summary is None:
        raise ReportError(
            "{}: no root <PerformanceEstimates><SummaryOfOverallLatency>".format(
                xml_path
            )
        )

    results = {}
    for tag in ("Best-caseLatency", "Average-caseLatency", "Worst-caseLatency",
                "Best-caseRealTimeLatency", "Average-caseRealTimeLatency",
                "Worst-caseRealTimeLatency"):
        text = summary.findtext(tag)
        if text is None or not text.strip():
            raise ReportError(
                "{}: the root summary has no <{}>".format(xml_path, tag)
            )
        results[tag] = text.strip()
    results["Latency"] = _required_int(summary, "Worst-caseLatency", xml_path, top)
    results["Interval"] = _required_int(summary, "Interval-max", xml_path, top)

    own = module_report_path(os.path.dirname(xml_path), top)
    if os.path.isfile(own):
        own_latency, own_interval = read_module_report(os.path.dirname(xml_path), top)
        if (own_latency, own_interval) != (results["Latency"], results["Interval"]):
            raise ReportError(
                "top module {!r}: the aggregate report says latency {} and "
                "interval {}, but {} says {} and {}".format(
                    top, results["Latency"], results["Interval"], own,
                    own_latency, own_interval
                )
            )
    return results


def update_timings_xlsx(xlsx_path, timings):
    try:
        df = pd.read_excel(xlsx_path, index_col=0, engine='openpyxl')
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
    if not(len(sys.argv) == 2 or len(sys.argv) == 3):
        # relative path to codegen is necessary when not launching fron the codegen folder.
        print("usage : python3 extract_syn_results.py <timing file path> [relative path to codegen]")
        sys.exit(1)

    given_codegen = ""
    if len(sys.argv) == 3:
        given_codegen=sys.argv[2]

    generated_folder = os.getcwd() if not(given_codegen) else given_codegen
    code_folder = os.path.abspath(generated_folder + "/..")
    clusters_file = os.path.abspath(generated_folder + "/clusters_list")
    workspace = os.path.abspath(generated_folder + "/timings")

    timing_file_path = os.path.abspath(os.path.join(given_codegen, sys.argv[1]))
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
        # other possible target : zcu104 xczu7ev-ffvc1156-2-e
        comp = client.create_hls_component(name = kernel,template = "empty_hls_component")
        cfg_path = os.path.join(workspace, kernel, 'hls_config.cfg')
        cfg_obj = client.get_config_file(cfg_path)
        cfg_obj.set_value("", key="part", value="xczu9eg-ffvb1156-2-e") #zcu102 : any target is fine since we just synthesize
        liste_hls_usercmake = [
        f"syn.top={kernel}",
        f"syn.file={generated_folder}/{kernel}.cpp",
        f"syn.cflags=-I{code_folder}/include -I{generated_folder}/ -DTIMINGS_EXTRACTION",
        "clock=5ns"
        ]
        cfg_obj.add_lines('hls', liste_hls_usercmake)

        comp.run(operation="SYNTHESIS")
        # Operation to be executed. Valid types are
        # 'C_SIMULATION', 'SYNTHESIS', 'CO_SIMULATION', 'IMPLEMENTATION', 'ANALYSIS_OPTIMIZATION', and 'PACKAGE'

    vitis.dispose()

    try:
        for kernel in clusters_list:
            report_dir = os.path.join(workspace, kernel, kernel, "hls", "syn", "report")
            # the aggregate report, not <kernel>_csynth.xml: that one is the
            # top module's own report and carries no hierarchy
            xml_file = os.path.join(report_dir, AGGREGATE_REPORT)

            metrics = extract_latencies_and_intervals(xml_file)
            print(f"\n=== HlS estimated timings of cluster kernel {kernel} ===")
            for key, value in metrics.items():
                print(f"\t{key}: {value}")

            results = extract_metrics(report_dir, xml_file, kernel)

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
                raise ReportError(
                    f"extension {ext} not supported in path to timings file "
                    f"{timing_file_path}")

            longest_name = max([len(r["actor"]) for r in results] + [len("actor")])
            width = longest_name + 26
            print(f"\n=== HLS timings of sub-actors in cluster {kernel} "
                  f"(Latency & Interval) ===")
            print("-" * width)
            print(f'{"actor":<{longest_name}}  {"Latency":<10}  {"Interval":<10}')
            print("-" * width)
            for r in results:
                print(f'{r["actor"]:<{longest_name}}  {r["latency"]:<10}  {r["II"]:<10}')
            print("-" * width)
            print("\n")

    except ReportError as exc:
        print(f"\nERROR: {exc}", file=sys.stderr)
        print("Aborting: no timings were written.", file=sys.stderr)
        sys.exit(1)
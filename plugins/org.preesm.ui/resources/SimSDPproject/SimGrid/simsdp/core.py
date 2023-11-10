# Copyright (C) 2023. Adrien Gougeon. All rights reserved.

# This file is part of simsdp.
# simsdp is free software: you can redistribute it and/or modify it under the terms
# of the GNU General Public License as published by the Free Software Foundation,
# either version 3 of the License, or any later version.
# simsdp is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
# See the GNU General Public License for more details.
# You should have received a copy of the GNU General Public License along with simsdp. 
# If not, see <https://www.gnu.org/licenses/>.

import argparse
import os
import pathlib
import shutil
import subprocess
import pandas as pd
import networkx as nx
import xml.etree.ElementTree as ET

simgrid_found = False
try:
    from simgrid import Engine
    from simgrid import CommTask as SimGridCommTask
    from simgrid import ExecTask as SimGridExecTask
    simgrid_found = True
except ImportError:
    pass

from .task import *
from .aux import _codegen, _build, _parse_output, _nxgraph_from_pisdf, _tasks_from_nxgraph, _dict_to_csv

directory = '__tmp__'

# ---------- Commmand Line ---------- #

def parse():
    parser = argparse.ArgumentParser(
        prog='simsdp',
        description='Command line tool to update a forked git repository.'
        )
    parser.add_argument('preesm_folder',
                        help='Location of the PREESM folder with .pi and .xml files.')
    parser.add_argument('-t', '--trigger',
                        default=1000,
                        help='Number of times to trigger the entry points of the graph (default=1000).')
    parser.add_argument('-p', '--platform',
                        help='Path to the platform .xml file.')
    parser.add_argument('-bw', '--bandwidth',
                        default='1GBps',
                        help='Bandwidth of the links of the star platform (default=1GBps). Ignored if a platform file is specified with option -p.')
    parser.add_argument('-lat', '--latency',
                        default='0.1ms',
                        help='Latency of the links of the star platform (default=0.1ms). Ignored if a platform file is specified with option -p.')
    parser.add_argument('-c', '--capture_output',
                        action='store_true',
                        help='Capture output. Automatically set `load=True`.')
    parser.add_argument('-s', '--show_finished',
                        action='store_true',
                        help='Display finished tasks when running simulation. \
                            If capture_output is also set then the tasks will only be displayed when the simulation is finished.')
    parser.add_argument('-l', '--load',
                        action='store_true',
                        help='Display load of hosts and comms at the end of the simulation.')
    parser.add_argument('-v', '--verbose',
                        action='store_true',
                        help='Display build output.')
    parser.add_argument('-k', '--keep_generated_files',
                        action='store_true',
                        help='Do not delete the temporary folder __tmp__ when everything is done.')
    parser.add_argument('-o', '--output',
                        default=None,
                        help='Specify a filename to generate a csv. \
                            Automatically set `capture_output=True`.')
    parser.add_argument('-j', '--joules',
                        action='store_true',
                        help='Evaluate consumption during simulation. Does not work with the default star platform. \
                            Each host (or link) you want to evaluate the consumption must have the property "wattage_per_state" (or "wattage_range" for links).')
    return parser.parse_args()

def main():
    args = parse()
    tasks = tasks_from_preesm_folder_v5(args.preesm_folder)
    entry_points = get_entry_points(tasks)
    for entry_point in entry_points:
        entry_point.queued = args.trigger
    if args.platform == None:
        if args.joules:
            print('Cannot evaluate consumption with the default star platform. Please provide your own platform with option -p.')
            exit()
        hosts = get_all_host(tasks)
        args.platform = generate_star_platform(hosts,
                                      bw=args.bandwidth,
                                      latency=args.latency,
                                      cores=1,
                                      wattage=None,
                                      wattage_links=None)
    out,dic = run_simgrid(tasks,
                          args.platform,
                          capture_output=args.capture_output,
                          show_finished=args.show_finished,
                          load=args.load,
                          consumption=args.joules,
                          verbose=args.verbose,
                          clean=not args.keep_generated_files,
                          to_csv=args.output)
    if (args.capture_output):
        print(dic)

# ---------- Tasks ---------- #

def get_entry_points(tasks):
    """
    Retrieve tasks that do not have parents, i.e., entry points of the graph.
    Args:
        tasks:     A list of tasks.
    Return:
        A list of entry point tasks.
    """
    successors = [s for t in tasks for s in t.successors]
    return [t for t in tasks if t.name not in successors]

def get_all_host(tasks):
    """
    Retrieve the list of different hosts from a list of tasks.
    Args:
        tasks:    A list of tasks.
    Return:
        A dict of tasks in WfFormat.
    """
    hosts = set()
    for t in tasks:
        try:
            hosts.add(t.host)
        except:
            hosts.add(t.source)
            hosts.add(t.destination)
    return sorted(list(hosts))

if (simgrid_found):
    def simgrid_tasks_from_simsdp_tasks(simsdp_tasks):
        """
        Retrieve a list of task instances of the SimGrid class Task from  a list of task instance of the simsdp class Task.
        Args:
            simsdp_tasks:    A list of task instance of the simsdp class Task
        Return:
            A list of task instances of the SimGrid class Task
        """
        e = Engine.instance
        simgrid_tasks = {}
        for t in simsdp_tasks:
            try:
                simgrid_tasks[t.name] = SimGridExecTask.init(t.name, t.amount, e.host_by_name(t.host))
            except:
                simgrid_tasks[t.name] = SimGridCommTask.init(t.name, float(t.amount), e.host_by_name(t.source), e.host_by_name(t.destination))
    
        for t in simsdp_tasks:
            for s in t.successors:
                simgrid_tasks[t.name].add_successor(simgrid_tasks[s])
        return simgrid_tasks

# ---------- PREESM ---------- #

# assume that the name of the host for each task is in its own name in the gantt file
# for instance the hosts of the task sub0_node0_0 is node_0
def tasks_from_preesm_folder_v4(path):
    """
    Retrieve a list of task from a folder containing top graph pisdf and a gantt file.
    Args:
        path:     Path to the preesm dir with the necessary files.
    Return:
        A list of tasks
    """
    pisdf_file = [f for f in os.listdir(path) if '.pi' in f][0]
    gantt_file = [f for f in os.listdir(path) if '.xml' in f][0]
    nx_graph = _nxgraph_from_pisdf(f'{path}/{pisdf_file}')
    gantt_root = ET.parse(f'{path}/{gantt_file}').getroot()
    actors = {}
    for child in gantt_root:
        title = child.get('title')
        if title and '_init_' not in title and '_end_' not in title:
            title = '_'.join(title.split('_')[:-1])
            actors[title] = {
                'timing': round((int(child.get('end')) - int(child.get('start'))) * 1e-3, 6),
                'host': title.split('_')[-1]
                }
    for node_id,attr in nx_graph.nodes(data=True):
        if '_to_' not in node_id:
            amount = actors.get(node_id)['timing']
            nx_graph.nodes[node_id]['amount'] = amount if amount else 0
            nx_graph.nodes[node_id]['host'] = node_id.split('_')[-1]
    return _tasks_from_nxgraph(nx_graph)

# assume that the name of the host for each task is in the "mapping" property of the gantt
def tasks_from_preesm_folder_v5(path):
    """
    Retrieve a list of task from a folder containing top graph pisdf and a gantt file.
    Args:
        path:     Path to the preesm dir with the necessary files.
    Return:
        A list of tasks
    """
    # path = os.path.abspath(path)
    pisdf_file = [f for f in os.listdir(path) if '.pi' in f][0]
    gantt_file = [f for f in os.listdir(path) if '.xml' in f][0]
    nx_graph = _nxgraph_from_pisdf(f'{path}/{pisdf_file}')
    gantt_root = ET.parse(f'{path}/{gantt_file}').getroot()
    actors = {}
    for child in gantt_root:
        title = child.get('title')
        if title and '_init_' not in title and '_end_' not in title:
            title = '_'.join(title.split('_')[:-1])
            actors[title] = {
                'timing': round((int(child.get('end')) - int(child.get('start'))) * 1e-3, 6),
                'host': child.get('mapping')
                }
    for node_id,attr in nx_graph.nodes(data=True):
        if '_to_' not in node_id:
            amount = actors.get(node_id)['timing']
            nx_graph.nodes[node_id]['amount'] = amount if amount else 0
            nx_graph.nodes[node_id]['host'] = actors.get(node_id)['host']
    return _tasks_from_nxgraph(nx_graph)

# Same as tasks_from_preesm_folder_v5 but uses the files instead of the folder containing them
def tasks_from_preesm(pisdf_file, gantt_file):
    """
    Retrieve a list of task from a top graph pisdf and a gantt file.
    Args:
        pisdf_file:     Path to the pisdf file.
        gantt_file:     Path to the gantt file.
    Return:
        A list of tasks
    """
    nx_graph = _nxgraph_from_pisdf(pisdf_file)
    gantt_root = ET.parse(gantt_file).getroot()
    actors = {}
    for child in gantt_root:
        title = child.get('title')
        if title and '_init_' not in title and '_end_' not in title:
            title = '_'.join(title.split('_')[:-1])
            actors[title] = {
                'timing': round((int(child.get('end')) - int(child.get('start'))) * 1e-3, 6),
                'host': child.get('mapping')
                }
    for node_id,attr in nx_graph.nodes(data=True):
        if '_to_' not in node_id:
            amount = actors.get(node_id)['timing']
            nx_graph.nodes[node_id]['amount'] = amount if amount else 0
            nx_graph.nodes[node_id]['host'] = actors.get(node_id)['host']
    return _tasks_from_nxgraph(nx_graph)

def run_preesm(preesm_exe_folder_path, preesm_project_folder_path, workflow, scenario, verbose=False):
    """
    Run a PREESM project.
    Args:
        preesm_exe_folder_path:     Path to the folder containing the PREESM executable.
        preesm_project_folder_path: Path to the folder containing the PREESM project.
        workflow: The workflow to run.
        scenario: The scenario to run.
    Kwargs:
        verbose: Specify if we want to display execution output.
    """
    out = subprocess.run(f'{pathlib.Path(__file__).parent.resolve()}/commandLinePreesm.sh {preesm_exe_folder_path} {preesm_project_folder_path} {workflow} {scenario}', shell=True, stdout=None if verbose else subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if (out.returncode != 0 or (not verbose and 'ERROR' in out.stdout)):
        print(out.stdout)
        exit()

# ---------- SimGrid ---------- #

def generate_star_platform(hosts, bw='1GBps', latency='0.1ms', cores=1, wattage=None, wattage_links=None):
    """
    Generate a star platform file with all hosts connected to a unique router.
    Args:
        hosts:     A list of hosts.
    Kwargs:
        bw:        Bandwidth of the links.
        latency:   Latency of the links.
        cores:     Specify the number of cores per hosts. Can be a single integer or a per host dict `{host: nb_cores}`.
        wattage:   Specify the power consumption of the hosts. Can be a single pair (idle power,fully loaded power) or a per host dict `{host: (idle power, fully loaded power)}.
        wattage_links:   Specify the power consumption of the links (idle power,fully loaded power). The power are for one link but are applied to all of them.
    Return:
        The path to the platform.
    """

    try:
        len(cores) # check that cores is a dict
        for host in hosts:
            if host not in cores:
                print(f'No core number for host {host}.')
                exit(1)
    except:
        cores = {host:cores for host in hosts}
    
    if wattage:
        try:
            wattage[0] # check that wattage is not a dict
            wattage = {host:(wattage[0],wattage[1]) for host in hosts}
        except:
            for host in hosts:
                if host not in wattage:
                    print(f'No wattage values for host {host}.')
                    exit(1)
            
    header  = '<?xml version="1.0"?>\n'
    header += '<!DOCTYPE platform SYSTEM "https://simgrid.org/simgrid.dtd">\n'
    header += '<platform version="4.1">\n'
    header += '  <zone id="world" routing="Floyd">\n'
    header += '    <router id="router"/>\n'

    footer = '</zone>\n</platform>'
    body = ''
    for i in range(len(hosts)):
        if not wattage:
            body += f'    <host id="{hosts[i]}" speed="1f" core="{cores[hosts[i]]}"/>\n'
        else:
            body += f'    <host id="{hosts[i]}" speed="1f" core="{cores[hosts[i]]}">\n'
            body += f'      <prop id="wattage_per_state" value="{wattage[hosts[1]][0]}:{wattage[hosts[1]][1]}:{wattage[hosts[1]][1]}"/>\n'
            body += f'    </host>\n'
    for host in hosts:
        if not wattage_links:
            body += f'    <link id="{host}-router" bandwidth="{bw}" latency="{latency}"/>\n'
        else:
            body += f'    <link id="{host}-router" bandwidth="{bw}" latency="{latency}">\n'
            body += f'      <prop id="wattage_range" value="{wattage_links[0]}:{wattage_links[1]}"/>\n'
            body += f'    </link>\n'
    for host in hosts:
        body += f'    <route src="{host}" dst="router"><link_ctn id="{host}-router"/></route>\n'
    platform_name = 'generated_platform.xml'
    if not os.path.exists(f'{directory}/platforms'):
        os.makedirs(f'{directory}/platforms')
    with open(f'{directory}/platforms/{platform_name}', 'w+') as f:
        f.write(header + body + footer)
    return f'{directory}/platforms/{platform_name}'

def run_simgrid(tasks, platform, capture_output=False, show_finished=False, load=False, consumption=False, verbose=False, clean=True, to_csv=None):
    """
    Run a Simgrid simulation using a list of tasks. Automatically handle codegen and build.
    
    Note: 
        When the output is captured the function will only display its output at the end, and nothing during its execution.
    Args:
        tasks:     A list of tasks.
        platform:       The platform we want to run the simulation on.
    Kwargs:
        capture_output: Specify if the output will be captured. Automatically set `load=True`.
        show_finished:  Specify if finished tasks will be displayed when running code. 
                        If capture_output is also set to True then the output will only be displayed at the end.
        load:           Specify if we want to display load of hosts and comms at the end of execution.
        consumption:    Specify if we want to evaluate consumption during simulation.
        verbose:        Specify if we want to display build output.
        clean:          Delete temporary folder when everything is done.
        to_csv:         Specify a filename to generate a csv. If not `None`, Automatically set `capture_output=True`.
    Return:
        A pair `out`,`dict`

        If output is captured, `out` is the output of the simulation and `dict` is a dict containing the processed output.
        
        Otherwise, `out` is the return code of the simulation and `dict` is `None`.
    """
    # to build the csv we need to capture output
    if to_csv:
        capture_output = True
    # capturing output is only useful if we measure idle time
    if capture_output:
        load = True
    _codegen(tasks, show_finished=show_finished, load=load, consumption=consumption)
    _build(verbose=verbose)
    out = subprocess.run(f'{directory}/build/main {platform}', shell=True, stdout=subprocess.PIPE if capture_output else None, stderr=subprocess.STDOUT if capture_output else None, text=True)
    if capture_output:
        dic = _parse_output(out.stdout)
        if show_finished:
            [print(line) for line in out.stdout.split('\n')]
    if clean:
        shutil.rmtree('__tmp__')
    if to_csv:
        _dict_to_csv(dic, to_csv)
    return (out.stdout[:-1],dic) if capture_output else (out.returncode, None)

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

import os 
import sys
import json
import subprocess
import pandas as pd
import networkx as nx
from collections import defaultdict
import xml.etree.ElementTree as ET

from .task import *

directory = '__tmp__'

# ---------- NetworkX ---------- #

def _nxgraph_from_pisdf(pisdf):
    """
    Retrieve a networkx graph from a pisdf file.
    Args:
        pisdf:     Path to the pisdf file.
    Return:
        A networkx graph.
    """
    xml_graph = ET.parse(pisdf).getroot()[-1]
    nx_graph = nx.MultiDiGraph()
    nodes_outputports_bytes = {}
    for child in xml_graph:
        tag = child.tag.split('}')[1]
        if tag == 'data' or child.get('kind') in ['param', 'dependency']:
            continue
        elif tag == 'node':
            node_id = child.get('id')
            if child.get('kind') == 'delay':
                continue
            nx_graph.add_node(node_id)
            if child.get('kind') == 'end':
                nx_graph.add_edge(node_id, child.get('init_end_ref'))
            nodes_outputports_bytes[node_id] = {}
            for port in child:
                if port.get('kind') == 'output':
                    nodes_outputports_bytes[node_id][port.get('name')] = port.get('expr')
        elif tag == 'edge':
            source = child.get('source')
            source_port = child.get('sourceport')
            target = child.get('target')
            target_port = child.get('targetport')
            comm_id = f'{source}_at_{source_port}_to_{target}_at_{target_port}'
            nx_graph.add_node(comm_id, amount=nodes_outputports_bytes[source][source_port])
            nx_graph.add_edges_from([(source,comm_id),(comm_id,target)])
    return nx_graph

def _tasks_from_nxgraph(nx_graph):
    """
    Retrieve tasks from a networkx graph. 
    Args:
        nx_graph:     A networkx graph
    Return:
        A list of tasks.
    """
    tasks = []
    for node_id,attr in nx_graph.nodes(data=True):
        succ = list(nx_graph.successors(node_id))
        if '_to_' in node_id:
            pred = list(nx_graph.predecessors(node_id))
            if len(pred) != 1:
                print('CommTask must have exactly 1 predecessor')
            if len(succ) != 1:
                print('CommTask must have exactly 1 successor')
            tmp_task = CommTask(node_id, attr['amount'], nx_graph.nodes[pred[0]]['host'], nx_graph.nodes[succ[0]]['host'])
        else:
            tmp_task = ExecTask(node_id, attr['amount'], attr['host'])
        tmp_task.add_successors(succ)
        tasks.append(tmp_task)
    return tasks

# ---------- Python to C++ ---------- #

def _codegen(tasks, show_finished=True, load=True, consumption=False):
    """
    Generate a c++ code file to execute a list of tasks.
    Args:
        tasks:     A list of tasks.
    Kwargs:
        show_finished:  Specify if finished tasks will be displayed when running code.
        load:           Specify if we want to display load of hosts and comms at the end of execution.
        consumption:    Specify if we want to display the consumption of hosts at the end of execution.
    """
    # FIX LOAD PLUGIN (seg fault with some config?)
    if not os.path.exists(f'{directory}/src'):
        os.makedirs(f'{directory}/src')
    body = ''    
    hosts = []
    body += '  // Retrieve Hosts\n'
    for t in tasks:
        try:
            if t.host not in hosts:
                hosts.append(t.host)
                body += f'  auto {t.host} = e.host_by_name("{t.host}");\n'
        except:
            if t.source not in hosts:
                hosts.append(t.source)
                body += f'  auto {t.source} = e.host_by_name("{t.source}");\n'
            if t.destination not in hosts:
                hosts.append(t.destination)
                body += f'  auto {t.destination} = e.host_by_name("{t.destination}");\n'
    body += '\n  // Create Tasks\n'
    for t in tasks:
        try:
            body += f'  auto {t.name} = simgrid::s4u::ExecTask::init("{t.name}", {t.amount}, {t.host});\n'
        except:
            body += f'  auto {t.name} = simgrid::s4u::CommTask::init("{t.name}", {t.amount}, {t.source}, {t.destination});\n'
    body += '\n  // Create the graph by defining dependencies between tasks\n'
    for t in tasks:
        for s in t.successors:
            body += f'  {t.name}->add_successor({s});\n'
    body += '\n  // Enqueue firings for entry points of the graph\n'
    for t in tasks:
        if t.queued != 0:
            body += f'  {t.name}->enqueue_firings({t.queued});\n\n'
    if show_finished:
        body += '''  // Add a function to be called when tasks end for log purpose
        simgrid::s4u::Task::on_completion_cb([](simgrid::s4u::Task* t) {
    XBT_INFO("Task %s finished (%d)", t->get_name().c_str(), t->get_count());
  });

'''

    header  = '#include "simgrid/plugins/energy.h"\n' if consumption else ''
    header += '#include "simgrid/plugins/load.h"\n' if load else ''
    header += '#include "simgrid/s4u.hpp"\n'
    header += '#include <map>\n\n'
    header += 'XBT_LOG_NEW_DEFAULT_CATEGORY(main, "Messages specific for this s4u example");\n\n'
    header += 'int main(int argc, char* argv[]) {\n'
    header += '  simgrid::s4u::Engine e(&argc, argv);\n'
    header += '  sg_host_energy_plugin_init();\n  sg_link_energy_plugin_init();\n' if consumption else ''
    header += '  e.load_platform(argv[1]);\n'
    header += '  sg_host_load_plugin_init();\n\n' if load else '\n'
    header += '''  // Store time usage of links for later use
  std::map<std::string, double> links_usage_s;
  simgrid::s4u::Comm::on_completion_cb(
    [&links_usage_s](const simgrid::s4u::Comm &comm) {
      auto route_links = comm.get_source()->route_to(comm.get_destination()).first;
      auto delta_t = comm.get_finish_time() - comm.get_start_time();
      for (auto const &link : route_links) {
        auto link_name = link->get_cname();
        if (links_usage_s.find(link_name) != links_usage_s.end())
          links_usage_s[link_name] += delta_t;
        else
          links_usage_s[link_name] = delta_t;
      }
    }
  );

'''
    footer  = '''  // Start the simulation
  e.run();

'''
    footer += '''  // Display post-simulation statistics
    XBT_INFO("--- Host Load ---");
  for (auto host : e.get_all_hosts())
    XBT_INFO("%s %f%%", host->get_cname(), (1 - sg_host_get_total_idle_time(host) / e.get_clock()) * 100);
  XBT_INFO("--- Link Load ---");
  for (auto const &[link, usage_s] : links_usage_s)
    XBT_INFO("%s %f%%", link.c_str(), usage_s / e.get_clock() * 100);\n''' if load else ''
    footer += '  return 0;\n}'
    with open(f'{directory}/src/main.cpp', 'w+') as f:
        if f.read() != header + body + footer:
            f.write(header + body + footer)

def _create_meson_file():
    """
    Generate the meson file to build generated code.
    """
    with open(f'{directory}/meson.build','w+') as f:
        f.write("project('simsdp', 'cpp')\nexecutable('main', 'src/main.cpp', dependencies : dependency('simgrid'))")

def _build(verbose=False):
    """
    Build the c++ generated code.
    Kwargs:
        verbose:  Specify if we want to display build output.
    """
    if not os.path.exists(f'{directory}/meson.build'):
        _create_meson_file()
    if not os.path.exists(f'{directory}/build/build.ninja'):
        out = subprocess.run(f'meson setup {directory}/build {directory}', shell=True, stdout=None if verbose else subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if out.returncode != 0:
            print(out.stdout)
            exit(1)
    out = subprocess.run(f'ninja -C {directory}/build', shell=True, stdout=None if verbose else subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if out.returncode != 0:
        print(out.stdout)
        exit(1)

def _parse_output(out):
    """
    Parse the output of a simulation.
    Args:
        out:     Output of the Simgrid simulation.
    Return:
        A dict with simulation data (host load, link load, latency).
    """
    dic = {'load (%)': {}, 'energy (J)': {}}
    try:
      dic['latency (s)'] = float(out.split('\n')[:-1][-1].split(' ')[0][1:-1])
    except:
        print(out)
        sys.exit(1)
    parse_load = False
    parse_consumption = False
    for line in out.split('\n')[:-1]:
        if not parse_load and '--- Host Load ---' in line:
            parse_load = True
            continue

        tokens = line.split()
        if parse_load and tokens[2] == 'Energy':
            parse_consumption = True
            parse_load = False

        if parse_consumption:
            if tokens[6][0] == "'":
                dic['energy (J)'][tokens[6][1:-2]] = round(float(tokens[7]),2)
            else:
                dic['energy (J)'][tokens[6][:-1]] = round(float(tokens[7]),2)
        elif parse_load:
            if parse_load and '--- Link Load ---' in line:
                continue
            dic['load (%)'][tokens[2]] = round(float(tokens[3][:-1]),2)
    if len(dic['energy (J)']) == 0:
        dic.pop('energy (J)')
    return dic

def _dict_to_csv(dict,filename='out.csv'):
    """
    Generate a csv file containing simulation data.
    Args:
        dict:        Output from the _parse_output() function.
    Kwargs:
        filename:    Name of the file to generate.
    """
    df = pd.DataFrame.from_dict(dict['load (%)'],orient='index', columns=['load (%)'])
    try: 
        df['energy (J)'] = [v for k,v in dict['energy (J)'].items()]
    except:
        pass
    df.to_csv(filename)

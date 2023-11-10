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

import context
import sys
import simsdp.core as sdpcore
from simgrid import Engine, Task, Host, sg_host_load_plugin_init

# Retrieve tasks
simsdp_tasks = sdpcore.tasks_from_preesm_folder_v5('test_data/ABC - 8n - round0')

# Generate platform
hosts = sdpcore.get_all_host(simsdp_tasks)
platform = sdpcore.generate_star_platform(hosts, bw='1GBps')

# Setting up simgrid simulation
e = Engine(sys.argv)
e.load_platform(platform)
sg_host_load_plugin_init()

# Convert simsdp Tasks to SimGrid Tasks
simgrid_tasks = sdpcore.simgrid_tasks_from_simsdp_tasks(simsdp_tasks)
    
# Set the number of times we want to fire entry point tasks
entry_points = sdpcore.get_entry_points(simsdp_tasks)
for entry_point in entry_points:
    simgrid_tasks[entry_point.name].enqueue_firings(1000)

# Running simulation
e.run()

# Retrieve hosts load during simulation
hosts_load = {}
for host in e.all_hosts:
    hosts_load[host.name] = 1 - (host.total_idle_time / e.clock)
print(hosts_load)

def test():
    assert(hosts_load == {'Node0': 0.9992375714161391, 'Node1': 0.49961878570806095, 'Node2': 0.2498093928540358})



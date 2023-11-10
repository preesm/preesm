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
import simsdp.core as sdpcore

# Retrieve tasks
tasks = sdpcore.tasks_from_preesm_folder_v5('test_data/ABC - 8n - round0')

# Set the number of times we want to fire entry_point tasks
entry_points = sdpcore.get_entry_points(tasks)
for entry_point in entry_points:
    entry_point.queued = 1000

# Generate platform
hosts = sdpcore.get_all_host(tasks)
platform = sdpcore.generate_star_platform(
    hosts,
    bw='1GBps')

# Run simulation and print processed output
out,dic = sdpcore.run_simgrid(tasks, platform, capture_output=True)
print(dic)

def test():
    assert(dic == {
        'load (%)': {
            'Node0': 99.92,
            'Node1': 49.96,
            'Node2': 24.98,
            'Node0-router': 1.3,
            'Node1-router': 0.65,
            'Node2-router': 0.65,
            }, 
        'latency (s)': 400.305204}
        )


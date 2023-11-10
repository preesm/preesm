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
tasks = sdpcore.tasks_from_preesm_folder_v4('test_data/stereo')

# Set the number of times we want to fire entry point tasks
entry_points = sdpcore.get_entry_points(tasks)
for entry_point in entry_points:
    entry_point.queued = 1000

# Generate platform
platform = sdpcore.generate_star_platform(
    sdpcore.get_all_host(tasks),
    bw='1GBps',
    wattage=(100,150),
    wattage_links=(1,1.5))

# Run simulation and print processed output
out,dic = sdpcore.run_simgrid(tasks, platform, capture_output=True, consumption=True)
print(dic)

def test():
    assert(dic == {
        'load (%)': {
            'node0': 15.85, 
            'node1': 9.11, 
            'node2': 99.97, 
            'node0-router': 14.83, 
            'node1-router': 0.04, 
            'node2-router': 14.87}, 
        'energy (J)': {
            'node0': 734106.76, 
            'node1': 711206.76, 
            'node2': 1020206.76, 
            "node0-router": 6985.47, 
            "node1-router": 6803.41, 
            "node2-router": 6986.79}, 
        'latency (s)': 6802.067567}
        )


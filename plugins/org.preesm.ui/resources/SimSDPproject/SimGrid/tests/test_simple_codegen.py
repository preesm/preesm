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
import simsdp.task as sdptask

tasks = [
    sdptask.ExecTask('A', 100, 'node1'),
    sdptask.ExecTask('B', 200, 'node2'),
    sdptask.CommTask('A_to_B', 1e7, 'node1', 'node2'),
]

tasks[0].add_successor(tasks[2].name)
tasks[2].add_successor(tasks[1].name)

tasks[0].queued = 2

platform = sdpcore.generate_star_platform(['node1', 'node2'], cores={'node1': 2, 'node2': 2})
out,dic = sdpcore.run_simgrid(tasks, platform, show_finished=True)

def test():
    assert(out==0)

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

from setuptools import setup

setup(
    name='simsdp',
    version='0.1.0',    
    description='The goal of this project is to transform a Preesm project \
        into a Simgrid simulation to benefit from the task scheduling \
        from Preesm and the communication simulation from Simgrid.',
    author='Adrien Gougeon',
    author_email='adrien.gougeon@ens-rennes.fr',
    packages=['simsdp'],
    install_requires=[
        'networkx',
        'seaborn'
        ],
    entry_points = {
        'console_scripts': [
            'simsdp = simsdp.core:main'
        ]
    }
)
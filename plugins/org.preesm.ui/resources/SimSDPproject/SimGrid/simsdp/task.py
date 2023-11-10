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

from abc import ABC, abstractmethod

class Task(ABC):
    """Task Class.
    
    Attributes:
        name: Name
        amount: Amount of work, either Flops to compute or Bytes to transfer.
        successors: List of successors'names.
        queued: Number of times the task will be executed without consuming its input.
    
    Note:
        A task will start only if is has queued executions. 
        A new execution is queued when it can consume a token sent by each of its predecessors.
    """


    def __init__(self, name: str, amount: float):
        """
        Abstract constructor.

        Args:
            name:    Name of the Task.
            amount:  Amount of work, either Flops to compute or Bytes to transfer.
        """
        self.name = name
        self.amount = amount
        self.successors = []
        self.queued = 0
    
    def add_successor(self, task):
        """
        Add a successor to this task.
        
        Args:
            task:  Name of the successor.
        """
        self.successors.append(task)
    
    def add_successors(self, tasks):
        """
        Add successors to this task.
        
        Args:
            tasks:  List of successor's names.
        """
        self.successors += tasks

    @abstractmethod
    def __repr__(self):       
        """
        Abstract method to make the class abstract. Does nothing.
        """
        pass

class ExecTask(Task):
    """ExecTask Class

    Attributes:
        host: host of the execution.
    """

    def __init__(self, name: str, amount: float, host: str):
        """
        Constructor.

        Args:
            name:  Name the task.
            amount: Flops to execute.
            host:   Host of the execution.
        """
        Task.__init__(self, name, amount)
        self.host = host
    
    def __repr__(self):
        """
        Representation.

        Return:
            A string representing this.
        """
        d = {'name': self.name, 'amount': self.amount, 'host': self.host, 'successors': self.successors}
        return f"ExecTask({d}"

class CommTask(Task):
    """Communication Task.

    Attributes:
        source: source host of the communication.
        destination: destination host of the communication.
    """

    def __init__(self, name: str, amount: float, source: str, destination: str):
        """
        Constructor.

        Args:
            name:  Name the task.
            amount: Flops to execute.
            source:   Source of the communication
            destination:   Destination of the execution.
        """
        Task.__init__(self, name, amount)
        self.source = source
        self.destination = destination

    def __repr__(self):
        """
        Representation.

        Return:
            A string representing this.
        """
        d = {'name': self.name, 'amount': self.amount, 'source': self.source, 'destination': self.destination, 'successors': self.successors}
        return f"CommTask({d}"



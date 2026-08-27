## Clustering

### Clustering task

  * **Identifier**: `clustering.creation`
  * **Implementing Class**: `org.preesm.algorithm.clustering.ClusterCreationTask`
  * **Short description**: Workflow task responsible for identifying, creating, schedule, map and allocate clusters.
The goal is to reduce the complexity of the graph without loosing parallelism,
according to the given heuristics.


#### Inputs
  * **scenario** (of _Scenario_) : Input scenario
  * **PiMM** (of _PiGraph_) : Input PiGraph algorithm
  * **architecture** (of _Design_) : Input S-LAM architecture graph

#### Outputs
  * **PiMM** (of _PiGraph_) : Output PiGraph algorithm, modified by heuristics
  * **scenario** (of _Scenario_) : Modified scenario, with the clusters constraints
  * **clusters** (of _List_) : List containing the created clusters
  * **schedules** (of _List_) : List containing the clusters schedule
  * **allocations** (of _List_) : List containing the clusters allocation

#### Description
Workflow task responsible for identifying, creating, schedule, map and allocate clusters.
The goal is to reduce the complexity of the graph without loosing parallelism,
according to the given heuristics. The available heuristics are described in the parameters section.
If the user wants to create an heuristic, he will have to create them in the source code of PREESM,
in the package org.preesm.algorithm.clustering.


#### Parameters

##### Vertical heuristic
Name of which heuristic will be used to vertically clusterize the graph.
If there is not enough or too many hierarchical level following the heuristic,
it will create or regroup hierarchical levels. This parameter is optional,
and can not be set at all. If so, the input graph will just be flatten.
For now, there is no vertical heuristic.


##### Horizontal heuristic
Name of which heuristic will be used to horizontaly clusterize the graph.Every hierachical
level of the graph will be explored.
There is two available horizontal heuristic:
- The Unique Repetition Count (URC) that will regroup together the actors that are adjacent
and have the same repetition count in the graph
- The Single Repetition Vector (SRV) that will create clusters that contain only one actor,
with a repetition count higher than the number of processing elements. Even if the cluster
contains one actor, the DAG will be smaller since the cluster in the top level will have a
lower repetition count than the actor contained in it.
- The heteregeneous architecture separator that will create clusters only composed of actor mapped on FPGA

| Value | Effect |
| --- | --- |
| _urc_ | Undocumented |
| _urc_ | Undocumented |
| _simple heterogeneous arch_ | Undocumented |

##### Mapping heuristic
Name of which heuristic will assign a component type on which the cluster could be executed on:
- The Simple mapper will select the same component type for every cluster than the main operator one.
- The Classic mapper will select a component that is at least shared by every actors in the cluster.
If none, throws an error.


| Value | Effect |
| --- | --- |
| _classic mapper_ | Undocumented |
| _simple mapper_ | Undocumented |

##### Balancing heuristic
Name of which heuristic will balance the clusters in the graph. By default, clusters are set to its
maximal repetition, but the balancer can modify that. There is two available balancers :
- Complete balancer : will handle balancing even if repetition count of cluster is not divisible
by number of available processing elements, by duplicating the cluster and asdding fork/join actors.
- Basic balancer : will handle balancing only if repetition count of cluster is a divisor of number of
available processing elements.


| Value | Effect |
| --- | --- |
| _complete balancing_ | Undocumented |
| _basic balancing_ | Undocumented |

##### Scheduling heuristic
Name of which heuristic will schedule the clusters in the graph. For now, the only available scheduling
algorithm is the APGAN algorithm. We can't reuse existing scheduling algorithm as they depend on a DAG.
However, DAG creation must be avoided in the cluster synthesis process.


| Value | Effect |
| --- | --- |
| _apgan scheduling_ | Undocumented |

##### Allocation heuristic
Name of which heuristic will allocate the clusters memory. Work with a cluster scheduler only
(see scheduling heuristic parameters for all available schedulers that can work with cluster
allocation).


| Value | Effect |
| --- | --- |
| _simple allocation_ | Undocumented |

##### Verbose
More or less logs

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

##### Debug
Enable intermediate graph creation, to check if everything is ok between substeps.

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

#### See Also

  * - Renaud, Ophélie, Dylan Gageot, Karol Desnos, et Jean-François Nezan.
SCAPE: HW-Aware Clustering of Dataflow Actors for Tunable Scheduling Complexity.
In Design and Architecture for Signal and Image Processing, 2023.

- Renaud, Ophelie, Naouel Haggui, Karol Desnos, et J. F. Nezan. Automated Clustering
and Pipelining of Dataflow Actors for Controlled Scheduling Complexity. 22 octobre 2023.



### Clustering task

  * **Identifier**: `clustering.condensed`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.BigClusteringTask`
  * **Short description**: Workflow task responsible for clustering actors. 

#### Inputs
  * **scenario** (of _Scenario_) : Input scenario
  * **PiMM** (of _PiGraph_) : Input PiGraph algorithm
  * **architecture** (of _Design_) : Input S-LAM architecture graph

#### Outputs
  * **PiMM** (of _PiGraph_) : Output PiGraph algorithm, modified by heuristics
  * **scenario** (of _Scenario_) : Modified scenario
  * **architecture** (of _Design_) : Untouched architecture, just to avoid having an ugly workflow graph.

#### Description
Workflow task responsible for clustering actors. It regroups the 2 clustering tasks (ClusterCreationTask and ClusterCodegenTask) in one. It is prefered to use the 2 separated classes.For more infos on clustering, go see the two following classes : ClusterCreationTask and ClusterCodegenTask.

#### Parameters

##### Vertical heuristic


##### Horizontal heuristic


##### Mapping heuristic


##### Balancing heuristic


##### Scheduling heuristic


##### Allocation heuristic


##### Printer


##### Verbose


##### Debug



### Clustering task

  * **Identifier**: `clustering.codegen`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.ClusterCodegenTask`
  * **Short description**: Workflow task responsible of generating the files that contain the code of all the identified clusters.

#### Inputs
  * **scenario** (of _Scenario_) : Input scenario, containing constraints of clusters
  * **clusters** (of _List_) : Input clusters
  * **schedules** (of _List_) : Input schedules of clusters
  * **allocations** (of _List_) : Input allocations of clusters

#### Outputs
None.

#### Description
    This task works with the task ClusterCreationTask. It will generate the code for every clusters in
    separate files. They will be used during the top graph synthesis process.


#### Parameters

##### Printer
Will select the printer for the right language. For now, only C is available.

| Value | Effect |
| --- | --- |
| _C_ | Undocumented |

##### Verbose


| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

##### Debug


| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

## Schedulers

### External Scheduling from DAG

  * **Identifier**: `org.ietr.preesm.plugin.mapper.external`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ExternalMappingFromDAG`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
This class imports schedule expressed in dedicated json format. It is experimental and limited to flat PiMM and a few architectures (regular x86 and Odroid). See package org.preesm.algorithm.mapper.schedule for the json format.

#### Parameters

##### SCHEDULE_FILE
Undocumented

| Value | Effect |
| --- | --- |
| _/schedule.json_ | default value |


### External Scheduling from PiSDF

  * **Identifier**: `pisdf-mapper.external`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ExternalMappingFromPiMM`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### SCHEDULE_FILE
Undocumented

| Value | Effect |
| --- | --- |
| _/schedule.json_ | default value |


### List Scheduling from SDF - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.fast`
  * **Implementing Class**: `org.preesm.algorithm.mapper.FASTMapping`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |


### Fast Scheduling from DAG - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.fastdag`
  * **Implementing Class**: `org.preesm.algorithm.mapper.FASTMappingFromDAG`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |


### Fast Scheduling from PiSDF

  * **Identifier**: `pisdf-mapper.fast`
  * **Implementing Class**: `org.preesm.algorithm.mapper.FASTMappingFromPiMM`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |


### List Scheduling from SDF - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.listscheduling`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ListSchedulingMapping`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |


### List Scheduling from DAG - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.listschedulingfromdag`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ListSchedulingMappingFromDAG`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple / Switcher_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### EnergyAwareness
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |


### List Scheduling from PiSDF

  * **Identifier**: `pisdf-mapper.list`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ListSchedulingMappingFromPiMM`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### EnergyAwareness
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Turns on energy aware mapping/scheduling |

##### EnergyAwarenessFirstConfig
Undocumented

| Value | Effect |
| --- | --- |
| _First_ | Takes as starting point the first valid combination of PEs |
| _Middle_ | Takes as starting point half of the available PEs |
| _Max_ | Takes as starting point all the available PEs |
| _Random_ | Takes as starting point a random number of PEs |

##### EnergyAwarenessSearchType
Undocumented

| Value | Effect |
| --- | --- |
| _Thorough_ | Analyzes PE combinations one by one until the performance objective is reached |
| _Halves_ | Divides in halves the remaining available PEs and goes up/down depending if the FPS reached are below/above the objective |


### Simple Scheduling from DAG - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.simple`
  * **Implementing Class**: `org.preesm.algorithm.mapper.MainCoreMappingFromDAG`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters
None.


### Simple Scheduling from PiSDF

  * **Identifier**: `pisdf-mapper.simple`
  * **Implementing Class**: `org.preesm.algorithm.mapper.MainCoreMappingFromPiMM`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters
None.


### PFast Scheduling from SDF - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.pfast`
  * **Implementing Class**: `org.preesm.algorithm.mapper.PFASTMapping`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |

##### nodesMin
Undocumented

| Value | Effect |
| --- | --- |
| _5_ | Undocumented |

##### procNumber
Undocumented

| Value | Effect |
| --- | --- |
| _1_ | Undocumented |

##### fastNumber
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |


### PFast Scheduling from DAG - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.pfastdag`
  * **Implementing Class**: `org.preesm.algorithm.mapper.PFASTMappingFromDAG`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |

##### nodesMin
Undocumented

| Value | Effect |
| --- | --- |
| _5_ | Undocumented |

##### procNumber
Undocumented

| Value | Effect |
| --- | --- |
| _1_ | Undocumented |

##### fastNumber
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |


### PFast Scheduling from PiSDF

  * **Identifier**: `pisdf-mapper.pfast`
  * **Implementing Class**: `org.preesm.algorithm.mapper.PFASTMappingFromPiMM`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |

##### nodesMin
Undocumented

| Value | Effect |
| --- | --- |
| _5_ | Undocumented |

##### procNumber
Undocumented

| Value | Effect |
| --- | --- |
| _1_ | Undocumented |

##### fastNumber
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |


### Periodic Scheduling from PiSDF to old DAG

  * **Identifier**: `pisdf-mapper.periodic.DAG`
  * **Implementing Class**: `org.preesm.algorithm.mapper.PeriodicMappingFromPiMMTask`
  * **Short description**: Schedule and maps actors according to their periods.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Schedule and map actors according to their periods thanks to a list scheduler. Only works for homogeneous architectures, does not take into account communication times. Works also if there are no periods in the graph. Result is exported in the same format as pisdf-mapper.list standard scheduler.

#### Parameters
None.


### Periodic Scheduling using choco solver from PiSDF

  * **Identifier**: `mapper2.choco`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.ChocoSchedulerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Mapping** (of _Mapping_)
  * **Schedule** (of _Schedule_)

#### Description
Undocumented

#### Parameters

##### Max Solution
Undocumented

##### Verbose
Undocumented


### List Scheduling from PiSDF

  * **Identifier**: `mapper2.list`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.LegacyListSchedulerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Mapping** (of _Mapping_)
  * **Schedule** (of _Schedule_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

##### EnergyAwareness
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Turns on energy aware mapping/scheduling |
| _False_ | Undocumented |

##### EnergyAwarenessFirstConfig
Undocumented

| Value | Effect |
| --- | --- |
| _First_ | Takes as starting point the first valid combination of PEs |
| _Middle_ | Takes as starting point half of the available PEs |
| _Max_ | Takes as starting point all the available PEs |
| _Random_ | Takes as starting point a random number of PEs |

##### EnergyAwarenessSearchType
Undocumented

| Value | Effect |
| --- | --- |
| _Thorough_ | Analyzes PE combinations one by one until the performance objective is reached |
| _Halves_ | Divides in halves the remaining available PEs and goes up/down depending if the FPS reached are below/above the objective |


### Periodic Scheduling from PiSDF

  * **Identifier**: `mapper2.periodic`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.PeriodicSchedulerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Mapping** (of _Mapping_)
  * **Schedule** (of _Schedule_)

#### Description
Undocumented

#### Parameters
None.


### Periodic Scheduling from PiSDF

  * **Identifier**: `mapper2.simple`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.SimpleSchedulerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Mapping** (of _Mapping_)
  * **Schedule** (of _Schedule_)

#### Description
Undocumented

#### Parameters
None.

## Gantt exporters

### ABC Gantt exporter

  * **Identifier**: `org.ietr.preesm.stats.exporter.StatsExporterTask`
  * **Implementing Class**: `org.preesm.algorithm.mapper.stats.exporter.StatsExporterTask`
  * **Short description**: This task exports scheduling results as a *.pgantt file that can be viewed using the ganttDisplay viewer [1].

#### Inputs
  * **ABC** (of _LatencyAbc_)
  * **scenario** (of _Scenario_)

#### Outputs
None.

#### Description
This task exports scheduling results as a *.pgantt file that can be viewed using the ganttDisplay viewer [1]. The exported *.pgantt file uses the XML syntax.

#### Parameters

##### path
Path of the exported *.pgantt file. If the specified directory does not exist, it will not be created.

| Value | Effect |
| --- | --- |
| _/stats/xml/_ | Path within the Preesm project containing the workflow where the ”Gantt Exporter” task is instantiated. Exported Gantt will be named as follows: **/path/in/proj/<scenario name> stats.pgantt**. If a graph with this name already exists in the given path, it will be overwritten. |

#### See Also

  * **[1]**: https://github.com/preesm/gantt-display


### ABC Gantt displayer

  * **Identifier**: `org.ietr.preesm.plugin.mapper.plot`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ui.stats.StatEditorAbcTask`
  * **Short description**: Displays the result of a mapping/scheduling algorithm as a Gantt diagram.

#### Inputs
  * **ABC** (of _LatencyAbc_)
  * **scenario** (of _Scenario_)

#### Outputs
None.

#### Description
Displays the result of a mapping/scheduling algorithm as a Gantt diagram.

#### Parameters
None.

#### See Also

  * **Speedup assessment chart**: Maxime Pelcat. Prototypage Rapide et Génération de Code pour DSP Multi-Coeurs Appliqués à la Couche Physique des Stations de Base 3GPP LTE. PhD thesis, INSA de Rennes, 2010.


### Synthesis Gantt displayer and exporter

  * **Identifier**: `gantt-output`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ui.stats.StatEditorSynthesisTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)
  * **Schedule** (of _Schedule_)
  * **Mapping** (of _Mapping_)
  * **Allocation** (of _Allocation_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### display
Specify if statistics, including Gantt diagram, must be displayed or not.

| Value | Effect |
| --- | --- |
| _true/false_ | Undocumented |

##### file path
Folder to store Gantt diagram as xml file. Path is relative to the project, put "/" if at root, may be empty.

| Value | Effect |
| --- | --- |
| _/path/to_ | Undocumented |

## Memory Optimization

### MEG Updater

  * **Identifier**: `org.ietr.preesm.memory.exclusiongraph.MemExUpdater`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MemExUpdater`
  * **Short description**: Relax memory allocation constraints of the MEG using scheduling information.

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **MemEx** (of _MemoryExclusionGraph_)

#### Outputs
  * **MemEx** (of _MemoryExclusionGraph_)

#### Description
The MEG used in Preesm can be updated with scheduling information to remove exclusions between memory objects and make better allocations possible.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the start and completion time of the update, as well as characteristics (number of memory objects, density of exclusions) of the MEGs both before and after the update.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

#### See Also

  * **MEG update**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.


### Memory Allocation

  * **Identifier**: `org.ietr.preesm.memory.allocation.MemoryAllocatorTask`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MemoryAllocatorTask`
  * **Short description**: Perform the memory allocation for the given MEG.

#### Inputs
  * **MemEx** (of _MemoryExclusionGraph_) : Input Memory Exclusion Graph

#### Outputs
  * **MEGs** (of _Map_) : Map associating, for each memory element in the architecture, according to the chosen _Distribution_ parameter value, a Memory Exclusion Graph annotated with allocation information (i.e. buffer addresses, etc.).

#### Description
Workflow task responsible for allocating the memory objects of the given MEG.

#### Parameters

##### Verbose
Verbosity of the task.

| Value | Effect |
| --- | --- |
| _True_ | Detailed statistics of the allocation process are logged |
| _False_ | Logged information is kept to a minimum |

##### Allocator(s)
Specify which memory allocation algorithm(s) should be used. If the string value of the parameters contains several algorithm names, all will be executed one by one.

| Value | Effect |
| --- | --- |
| _Basic_ | Each memory object is allocated in a dedicated memory space. Memory allocated for a given object is not reused for other. |
| _BestFit_ | Memory objects are allocated one by one; allocating each object to the available space in memory whose size is the closest to the size of the allocated object. If MEG exclusions permit it, memory allocated for a memory object may be reused for others. |
| _FirstFit_ | Memory objects are allocated one by one; allocating each object to the first available space in memory whose size is the large enough to allocate the object. If MEG exclusions permit it, memory allocated for a memory object may be reused for others. |
| _DeGreef_ | Algorithm adapted from DeGreef (1997)}. If MEG exclusions permit it, memory allocated for a memory object may be reused for others. |

##### Distribution
Specify which memory architecture should be used to allocate the memory.

| Value | Effect |
| --- | --- |
| _SharedOnly_ | (Default) All memory objects are allocated in a single memory bank accessible to all PE. |
| _DistributedOnly_ | Each PE is associated to a private memory bank that no other PE can access. (Currently supported only in the MPPA code generation.) |
| _Mixed_ | Both private memory banks and a shared memory can be used for allocating memory. |
| _MixedMerged_ | Same as mixed, but the memory allocation algorithm favors buffer merging over memory distribution. |

##### Best/First Fit order
When using FirstFit or BestFit memory allocators, this parameter specifies in which order the memory objects will be fed to the allocation algorithm. If the string value associated to the parameters contains several order names, all will be executed one by one.

| Value | Effect |
| --- | --- |
| _ApproxStableSet_ | Memory objects are sorted into disjoint stable sets. Stable sets are formed one after the other, each with the largest possible number of object. Memory objects are fed to the allocator set by set and in the largest first order within each stable set. |
| _ExactStableSet_ | Similar to 'ApproxStableSet'. Stable set are built using an exact algorithm instead of a heuristic. |
| _LargestFirst_ | Memory objects are allocated in decreasing order of their size. |
| _Shuffle_ | Memory objects are allocated in a random order. Using the 'Nb of Shuffling Tested' parameter, it is possible to test several random orders and only keep the best memory allocation. |

##### Data alignment
Option used to force the allocation of buffers (i.e. Memory objects) with aligned addresses. The data alignment property should always have the same value as the one set in the properties of the Memory Scripts task.

| Value | Effect |
| --- | --- |
| _None_ | No special care is taken to align the buffers in memory. |
| _Data_ | All buffers are aligned on addresses that are multiples of their size. For example, a 32 bites integer is aligned on 32 bits address. |
| _Fixed:=n_ | Where $$n\in \mathbb{N}^*$$. This forces the allocation algorithm to align all buffers on addresses that are multiples of n bits. |

##### Nb of Shuffling Tested
Number of random order tested when using the Shuffle value for the Best/First Fit order parameter.

| Value | Effect |
| --- | --- |
| _$$n\in \mathbb{N}^*$$_ | Number of random order. |

#### Documented Errors

| Message | Explanation |
| --- | --- |
| **The obtained allocation was not valid because mutually exclusive memory objects have overlapping address ranges. The allocator is not working.** | When checking the result of a memory allocation, two memory objects linked with an exclusion in the MEG were allocated in overlapping memory spaces. The error is caused by an invalid memory allocation algorithm and should be corrected in the source code. |
| **The obtained allocation was not valid because there were unaligned memory objects. The allocator is not working.** | When checking the result of a memory allocation, some memory objects were found not to respect the Dala alignment parameter. The error is caused by an invalid memory allocation algorithm and should be corrected in the source code. |

#### See Also

  * **Memory Allocation Algorithms**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.
  * **Distributed Memory Allocation**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. Distributed memory allocation technique for synchronous dataflow graphs. In Signal Processing System (SiPS), Workshop on, pages 1–6. IEEE, 2016.
  * **Broadcast Merging**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory analysis and optimized allocation of dataflow applications on shared-memory MPSoCs. Journal of Signal Processing Systems, Springer, 2014.


### Memory Bounds Estimator

  * **Identifier**: `org.ietr.preesm.memory.bounds.MemoryBoundsEstimator`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MemoryBoundsEstimator`
  * **Short description**: Compute bounds of the amount of memory needed to allocate the MEG

#### Inputs
  * **MemEx** (of _MemoryExclusionGraph_)

#### Outputs
  * **MemEx** (of _MemoryExclusionGraph_)
  * **BoundMin** (of _Long_)
  * **BoundMax** (of _Long_)

#### Description
The analysis technique presented in [1] can be used in Preesm to derive bounds for the amount of memory that can be allocated for an application. The upper bound corresponds to the worst memory allocation possible for an application. The lower bound is a theoretical value that limits the minimum amount of memory that can be allocated. By definition, the lower bound is not always reachable, which means that it might be impossible to find an allocation with this optimal amount of memory. The minimum bound is found by solving the Maximum Weight Clique problem on the MEG. This task provides a convenient way to evaluate the quality of a memory allocation.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the name of the used solver, the start and completion time of the bound estimation algorithm. Computed bounds are always logged, even if the verbose parameter is set to false.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

##### Solver
Specify which algorithm is used to compute the lower bound.

| Value | Effect |
| --- | --- |
| _Heuristic_ | (Default) Heuristic algorithm described in [1] is used. This technique find an approximate solution. |
| _Ostergard_ | Östergård’s algorithm [2] is used. This technique finds an optimal solution, but has a potentially exponential complexity. |
| _Yamaguchi_ | Yamaguchi et al.’s algorithm [3] is used. This technique finds an optimal solution, but has a potentially exponential complexity. |

#### See Also

  * **[1]**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory bounds for the distributed execution of a hierarchical synchronous data-flow graph. In Embedded Computer Systems: Architectures, Modeling, and Simulation (SAMOS XII), 2012 International Conference on, 2012.
  * **[2]**: Patric R. J. Östergård. A new algorithm for the maximum-weight clique problem. Nordic J. of Computing, 8(4):424–436, December 2001.
  * **[3]**: K. Yamaguchi and S. Masuda. A new exact algorithm for the maximum weight clique problem. In 23rd International Conference on Circuit/Systems, Computers and Communications (ITC-CSCC’08), 2008.
  * **Memory Bounds**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.


### MEG Builder

  * **Identifier**: `org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraphBuilder`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MemoryExclusionGraphBuilder`
  * **Short description**: Builds the Memory Exclusion Graph (MEG) modeling the memory allocation constraints.

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **MemEx** (of _MemoryExclusionGraph_)

#### Description
The memory allocation technique used in Preesm is based on a Memory Exclusion Graph (MEG). A MEG is a graph whose vertices model the memory objects that must be allocated in memory in order to run the generated code. In the current version of Preesm, each of these memory objects corresponds either to an edge of the Directed Acyclic Graph (DAG) or to a buffer corresponding to ”delays” of the graph that store data between executions of a schedule. In the MEG, two memory objects are linked by an edge (called an exclusion) if they can not be allocated in overlapping memory spaces.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the start and completion time of the build, as well as characteristics (number of memory objects, density of exclusions) of the produced MEG.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

#### See Also

  * **MEG**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory bounds for the distributed execution of a hierarchical synchronous data-flow graph. In Embedded Computer Systems: Architectures, Modeling, and Simulation (SAMOS XII), 2012 International Conference on, 2012.


### Memory Scripts

  * **Identifier**: `org.ietr.preesm.memory.script.MemoryScriptTask`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MemoryScriptTask`
  * **Short description**: Executes the memory scripts associated to actors and merge buffers.

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **MemEx** (of _MemoryExclusionGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **MemEx** (of _MemoryExclusionGraph_)

#### Description
Executes the memory scripts associated to actors and merge buffers. The purpose of the memory scripts is to allow Preesm to allocate input and output buffers of certain actors in overlapping memory range.

#### Parameters

##### Check
Verification policy used when checking the applicability of the memory scripts written by the developer and associated to the actor.

| Value | Effect |
| --- | --- |
| _Thorough_ | Will generate error messages with a detailed description of the source of the error. This policy should be used when writting memory scripts for the first time. |
| _Fast_ | All errors in memory script are still detected, but error messages are less verbose. This verification policy is faster than the Thorough policy. |
| _None_ | No verification is performed. Use this policy to speed up workflow execution once all memory scripts have been validated.. |

##### False Sharing Prevention
Force additional allocation before/after buffer to prevent false sharing issues.

| Value | Effect |
| --- | --- |
| _False_ | The false sharing prevention mecanism will not be used. |
| _True_ | The false sharing prevention mecanism will be used.Using the Data alignement parameter. |

##### Data alignment
Option used to force the allocation of buffers with aligned addresses. The data alignment property should always have the same value as the one set in the properties of the Memory Allocation task.

| Value | Effect |
| --- | --- |
| _None_ | No special care is taken to align the buffers in memory. |
| _Data_ | All buffers are aligned on addresses that are multiples of their size. For example,a 32 bits integer is aligned on 32 bits address. |
| _Fixed:=$$n$$_ | Where $$n\in \mathbb{N}^*$$. This forces the allocation algorithm to align all buffers on addresses that are multiples of n bits. |

##### Log Path
Specify whether, and where, a log of the buffer matching optimization should be generated. Generated log are in the markdown format, and provide information on all matches created by scripts as well as which match could be applied by the optimization process.

| Value | Effect |
| --- | --- |
| _path/file.txt_ | The path given in this property is relative to the ”Code generation directory” defined in the executed scenario. |
| _empty_ | No log will be generated. |

##### Verbose
Verbosity of the workflow task.

| Value | Effect |
| --- | --- |
| _True_ | The workflow task will be verbose in the console. |
| _False_ | The workflow task will be more quiet in the console. |

#### See Also

  * **Buffer merging**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. On memory reuse between inputs and outputs of dataflow actors. ACM Transactions on Embedded Computing Systems, 15(30):25, January 2016.


### Serial Memory Bounds

  * **Identifier**: `org.ietr.preesm.memory.bounds.SerialMemoryBoundsEstimator`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.SerialMemoryBoundsEstimator`
  * **Short description**: Compute bounds of the amount of memory needed to allocate the MEGs.

#### Inputs
  * **MEGs** (of _Map_)

#### Outputs
None.

#### Description
This task computes the memory bounds (see Memory Bound Estimator Task) for several MEGs, like the one produced by the Memory Allocation task.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the name of the used solver, the start and completion time of the bound estimation algorithm. Computed bounds are always logged, even if the verbose parameter is set to false.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

##### Solver
Specify which algorithm is used to compute the lower bound.

| Value | Effect |
| --- | --- |
| _Heuristic_ | (Default) Heuristic algorithm described in [1] is used. This technique find an approximate solution. |
| _Ostergard_ | Östergård’s algorithm [2] is used. This technique finds an optimal solution, but has a potentially exponential complexity. |
| _Yamaguchi_ | Yamaguchi et al.’s algorithm [3] is used. This technique finds an optimal solution, but has a potentially exponential complexity. |

#### See Also

  * **[1]**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory bounds for the distributed execution of a hierarchical synchronous data-flow graph. In Embedded Computer Systems: Architectures, Modeling, and Simulation (SAMOS XII), 2012 International Conference on, 2012.
  * **[2]**: Patric R. J. Östergård. A new algorithm for the maximum-weight clique problem. Nordic J. of Computing, 8(4):424–436, December 2001.
  * **[3]**: K. Yamaguchi and S. Masuda. A new exact algorithm for the maximum weight clique problem. In 23rd International Conference on Circuit/Systems, Computers and Communications (ITC-CSCC’08), 2008.
  * **Memory Bounds**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.


### Memory Allocation

  * **Identifier**: `alloc2.memalloc`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.memalloc.allocation.PiMemoryAllocatorTask`
  * **Short description**: Perform the memory allocation for the given MEG.

#### Inputs
  * **MemEx** (of _PiMemoryExclusionGraph_) : Input Memory Exclusion Graph
  * **Mapping** (of _Mapping_) : Input Mapping
  * **scenario** (of _Scenario_) : Input scenario
  * **architecture** (of _Design_) : Input architecture
  * **PiMM** (of _PiGraph_) : Input algorithm (in DAG form)

#### Outputs
  * **Allocation** (of _Allocation_) : Final allocation, built on the input MEG

#### Description
Workflow task responsible for allocating the memory objects of the given MEG.

#### Parameters

##### Verbose
Verbosity of the task.

| Value | Effect |
| --- | --- |
| _True_ | Detailed statistics of the allocation process are logged |
| _False_ | Logged information is kept to a minimum |

##### Allocator(s)
Specify which memory allocation algorithm(s) should be used. If the string value of the parameters contains several algorithm names, all will be executed one by one.

| Value | Effect |
| --- | --- |
| _Basic_ | Each memory object is allocated in a dedicated memory space. Memory allocated for a given object is not reused for other. |
| _BestFit_ | Memory objects are allocated one by one; allocating each object to the available space in memory whose size is the closest to the size of the allocated object. If MEG exclusions permit it, memory allocated for a memory object may be reused for others. |
| _FirstFit_ | Memory objects are allocated one by one; allocating each object to the first available space in memory whose size is the large enough to allocate the object. If MEG exclusions permit it, memory allocated for a memory object may be reused for others. |

##### Distribution
Specify which memory architecture should be used to allocate the memory.

| Value | Effect |
| --- | --- |
| _SharedOnly_ | (Default) All memory objects are allocated in a single memory bank accessible to all PE. |
| _DistributedOnly_ | Each PE is associated to a private memory bank that no other PE can access. (Currently supported only in the MPPA code generation.) |
| _Mixed_ | Both private memory banks and a shared memory can be used for allocating memory. |
| _MixedMerged_ | Same as mixed, but the memory allocation algorithm favors buffer merging over memory distribution. |

##### Best/First Fit order
When using FirstFit or BestFit memory allocators, this parameter specifies in which order the memory objects will be fed to the allocation algorithm. If the string value associated to the parameters contains several order names, all will be executed one by one.

| Value | Effect |
| --- | --- |
| _ApproxStableSet_ | Memory objects are sorted into disjoint stable sets. Stable sets are formed one after the other, each with the largest possible number of object. Memory objects are fed to the allocator set by set and in the largest first order within each stable set. |
| _ExactStableSet_ | Similar to 'ApproxStableSet'. Stable set are built using an exact algorithm instead of a heuristic. |
| _LargestFirst_ | Memory objects are allocated in decreasing order of their size. |
| _Shuffle_ | Memory objects are allocated in a random order. Using the 'Nb of Shuffling Tested' parameter, it is possible to test several random orders and only keep the best memory allocation. |

##### Data alignment
Option used to force the allocation of buffers (i.e. Memory objects) with aligned addresses. The data alignment property should always have the same value as the one set in the properties of the Memory Scripts task.

| Value | Effect |
| --- | --- |
| _None_ | No special care is taken to align the buffers in memory. |
| _Data_ | All buffers are aligned on addresses that are multiples of their size. For example, a 32 bites integer is aligned on 32 bits address. |
| _Fixed:=n_ | Where $$n\in \mathbb{N}^*$$. This forces the allocation algorithm to align all buffers on addresses that are multiples of n bits. |

##### Nb of Shuffling Tested
Number of random order tested when using the Shuffle value for the Best/First Fit order parameter.

| Value | Effect |
| --- | --- |
| _$$n\in \mathbb{N}^*$$_ | Number of random order. |

#### Documented Errors

| Message | Explanation |
| --- | --- |
| **The obtained allocation was not valid because mutually exclusive memory objects have overlapping address ranges. The allocator is not working.** | When checking the result of a memory allocation, two memory objects linked with an exclusion in the MEG were allocated in overlapping memory spaces. The error is caused by an invalid memory allocation algorithm and should be corrected in the source code. |
| **The obtained allocation was not valid because there were unaligned memory objects. The allocator is not working.** | When checking the result of a memory allocation, some memory objects were found not to respect the Dala alignment parameter. The error is caused by an invalid memory allocation algorithm and should be corrected in the source code. |

#### See Also

  * **Memory Allocation Algorithms**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.
  * **Distributed Memory Allocation**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. Distributed memory allocation technique for synchronous dataflow graphs. In Signal Processing System (SiPS), Workshop on, pages 1–6. IEEE, 2016.
  * **Broadcast Merging**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory analysis and optimized allocation of dataflow applications on shared-memory MPSoCs. Journal of Signal Processing Systems, Springer, 2014.


### MEG Builder

  * **Identifier**: `alloc2.megbuilder`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.memalloc.meg.PiMEGBuilderTask`
  * **Short description**: Builds the Memory Exclusion Graph (MEG) modeling the memory allocation constraints.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **MemEx** (of _PiMemoryExclusionGraph_)

#### Description
The memory allocation technique used in Preesm is based on a Memory Exclusion Graph (MEG). A MEG is a graph whose vertices model the memory objects that must be allocated in memory in order to run the generated code. In the current version of Preesm, each of these memory objects corresponds either to an edge of the Directed Acyclic Graph (DAG) or to a buffer corresponding to ”delays” of the graph that store data between executions of a schedule. In the MEG, two memory objects are linked by an edge (called an exclusion) if they can not be allocated in overlapping memory spaces.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the start and completion time of the build, as well as characteristics (number of memory objects, density of exclusions) of the produced MEG.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

#### See Also

  * **MEG**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory bounds for the distributed execution of a hierarchical synchronous data-flow graph. In Embedded Computer Systems: Architectures, Modeling, and Simulation (SAMOS XII), 2012 International Conference on, 2012.


### MEG Updater

  * **Identifier**: `alloc2.megupdater`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.memalloc.meg.PiMEGUpdaterTask`
  * **Short description**: Relax memory allocation constraints of the MEG using scheduling information.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **Schedule** (of _Schedule_)
  * **Mapping** (of _Mapping_)
  * **MemEx** (of _PiMemoryExclusionGraph_)

#### Outputs
  * **MemEx** (of _PiMemoryExclusionGraph_)

#### Description
The MEG used in Preesm can be updated with scheduling information to remove exclusions between memory objects and make better allocations possible.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the start and completion time of the update, as well as characteristics (number of memory objects, density of exclusions) of the MEGs both before and after the update.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

#### See Also

  * **MEG update**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.


### Memory Scripts

  * **Identifier**: `alloc2.memoryscript`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.memalloc.script.PiMemoryScriptTask`
  * **Short description**: Executes the memory scripts associated to actors and merge buffers.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **MemEx** (of _PiMemoryExclusionGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **MemEx** (of _PiMemoryExclusionGraph_)

#### Description
Executes the memory scripts associated to actors and merge buffers. The purpose of the memory scripts is to allow Preesm to allocate input and output buffers of certain actors in overlapping memory range.

#### Parameters

##### Check
Verification policy used when checking the applicability of the memory scripts written by the developer and associated to the actor.

| Value | Effect |
| --- | --- |
| _Thorough_ | Will generate error messages with a detailed description of the source of the error. This policy should be used when writting memory scripts for the first time. |
| _Fast_ | All errors in memory script are still detected, but error messages are less verbose. This verification policy is faster than the Thorough policy. |
| _None_ | No verification is performed. Use this policy to speed up workflow execution once all memory scripts have been validated.. |

##### False Sharing Prevention
Force additional allocation before/after buffer to prevent false sharing issues.

| Value | Effect |
| --- | --- |
| _False_ | The false sharing prevention mecanism will not be used. |
| _True_ | The false sharing prevention mecanism will be used.Using the Data alignement parameter. |

##### Data alignment
Option used to force the allocation of buffers with aligned addresses. The data alignment property should always have the same value as the one set in the properties of the Memory Allocation task.

| Value | Effect |
| --- | --- |
| _None_ | No special care is taken to align the buffers in memory. |
| _Data_ | All buffers are aligned on addresses that are multiples of their size. For example,a 32 bits integer is aligned on 32 bits address. |
| _Fixed:=$$n$$_ | Where $$n\in \mathbb{N}^*$$. This forces the allocation algorithm to align all buffers on addresses that are multiples of n bits. |

##### Log Path
Specify whether, and where, a log of the buffer matching optimization should be generated. Generated log are in the markdown format, and provide information on all matches created by scripts as well as which match could be applied by the optimization process.

| Value | Effect |
| --- | --- |
| _path/file.txt_ | The path given in this property is relative to the ”Code generation directory” defined in the executed scenario. |
| _empty_ | No log will be generated. |

##### Verbose
Verbosity of the workflow task.

| Value | Effect |
| --- | --- |
| _True_ | The workflow task will be verbose in the console. |
| _False_ | The workflow task will be more quiet in the console. |

#### See Also

  * **Buffer merging**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. On memory reuse between inputs and outputs of dataflow actors. ACM Transactions on Embedded Computing Systems, 15(30):25, January 2016.

## Graph Transformation

### Static PiMM to IBSDF - _Deprecated_

  * **Identifier**: `org.ietr.preesm.experiment.pimm2sdf.StaticPiMM2SDFTask`
  * **Implementing Class**: `org.preesm.algorithm.pisdf.pimm2sdf.StaticPiMM2SDFTask`
  * **Short description**: Transforms a static PiSDF Graph into an equivalent IBSDF graph.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **SDF** (of _SDFGraph_)

#### Description
In Preesm, since version 2.0.0, the Parameterized and Interfaced SDF (PiSDF) model of computa tion is used as the frontend model in the graphical editor of dataflow graphs. This model makes it possible to design dynamically reconfigurable dataflow graphs where the value of parameters, and production/consumption rates depending on them, might change during the execution of the application. In former versions, the Interface Based SDF (IBSDF) model of computation was used as the front end model for application design. Contrary to the PiSDF, the IBSDF is a static model of computation where production and consumption rates of actors is fixed at compile-time.

The purpose of this workflow task is to transform a static PiSDF graph into an equivalent IBSDF graph. A static PiSDF graph is a PiSDF graph where dynamic reconfiguration features of the PiSDF model of computation are not used.

#### Parameters
None.

#### See Also

  * **IBSDF**: J. Piat, S.S. Bhattacharyya, and M. Raulet. Interface-based hierarchy for synchronous data-flow graphs. In SiPS Proceedings, 2009.
  * **PiSDF**: K. Desnos, M. Pelcat, J.-F. Nezan, S.S. Bhattacharyya, and S. Aridhi. PiMM: Parameterized and interfaced dataflow meta-model for MPSoCs runtime reconfiguration. In Embedded Computer Systems: Architectures, Modeling, and Simulation (SAMOS XIII), 2013 International Conference on, pages 41–48. IEEE, 2013.


### Single-Rate Transformation - _Deprecated_

  * **Identifier**: `org.ietr.preesm.experiment.pimm2srdag.StaticPiMM2SrDAGTask`
  * **Implementing Class**: `org.preesm.algorithm.pisdf.pimm2srdag.StaticPiMM2SrDAGTask`
  * **Short description**: Transforms an SDF graph into an equivalent single-rate SDF graph.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **PiMM** (of _PiGraph_)

#### Description
In Preesm, since version 2.0.0, the Parameterized and Interfaced SDF (PiSDF) model of computa tion is used as the frontend model in the graphical editor of dataflow graphs. This model makes it possible to design dynamically reconfigurable dataflow graphs where the value of parameters, and production/consumption rates depending on them, might change during the execution of the application. In former versions, the Interface Based SDF (IBSDF) model of computation was used as the front end model for application design. Contrary to the PiSDF, the IBSDF is a static model of computation where production and consumption rates of actors is fixed at compile-time.

The purpose of this workflow task is to transform a static PiSDF graph into an equivalent IBSDF graph. A static PiSDF graph is a PiSDF graph where dynamic reconfiguration features of the PiSDF model of computation are not used.

#### Parameters

##### ExplodeImploreSuppr
_(Deprecated: use at your own risks)_

This parameter makes it possible to remove most of the explode and implode actors that are inserted in the graph during the single-rate transformation. The resulting SDF graph is an ill-constructed graph where a single data input/output port of an actor may be connected to several First-In, First-Out queues (Fifos).

| Value | Effect |
| --- | --- |
| _false_ | (default) The suppression of explode/implode special actors is not activated. |
| _true_ | The suppression of explode/implode special actors is activated. |

#### Documented Errors

| Message | Explanation |
| --- | --- |
| **Graph not valid, not schedulable** | Single-rate transformation of the SDF graph was aborted because the top level was not consistent, or it was consistent but did not contained enough delays — i.e. initial data tokens — to make it schedulable. |

#### See Also

  * **Single-rate transformation**: J.L. Pino, S.S. Bhattacharyya, and E.A. Lee. A hierarchical multiprocessor scheduling framework for synchronous dataflow graphs. Electronics Research Laboratory, College of Engineering, University of California, 1995.
  * **Special actors**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. On memory reuse between inputs and outputs of dataflow actors. ACM Transactions on Embedded Computing Systems, 15(30):25, January 2016.
  * **Graph consistency**: E.A. Lee and D.G. Messerschmitt. Synchronous data flow. Proceedings of the IEEE, 75(9):1235 – 1245, sept. 1987.


### Hierarchy Flattening - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.transforms.flathierarchy`
  * **Implementing Class**: `org.preesm.algorithm.transforms.HierarchyFlattening`
  * **Short description**: Transforms a hierarchical IBSDF graph into an equivalent SDF graph.

#### Inputs
  * **SDF** (of _SDFGraph_)

#### Outputs
  * **SDF** (of _SDFGraph_)

#### Description
The purpose of this workflow task is to flatten several levels of the hierarchy of an IBSDF graph and produce an equivalent SDF graph. A hierarchical IBSDF graph is a graph where the internal behavior of some actors is described using another IBSDF subgraph instead of a C header file. When applying this transformation, hierarchical IBSDF actors of the first n levels of hierarchy are replaced with the actors of the IBSDF subgraph with which these hierarchical actors are associated.

#### Parameters
None.

#### Documented Errors

| Message | Explanation |
| --- | --- |
| **Inconsistent Hierarchy, graph can’t be flattened** | Flattening of the IBSDF graph was aborted because one of the graph composing the application, at the top level or deeper in the hierarchy, was not consistent. |

#### See Also

  * **IBSDF**: J. Piat, S.S. Bhattacharyya, and M. Raulet. Interface-based hierarchy for synchronous data-flow graphs. In SiPS Proceedings, 2009.
  * **Graph consistency**: E.A. Lee and D.G. Messerschmitt. Synchronous data flow. Proceedings of the IEEE, 75(9):1235 – 1245, sept. 1987.


### PiSDF Flattener

  * **Identifier**: `org.ietr.preesm.pimm.algorithm.pimm2flat.StaticPiMM2FlatPiMMTask`
  * **Implementing Class**: `org.preesm.model.pisdf.statictools.PiSDFFlattenerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters

##### Perform optimizations
Undocumented

| Value | Effect |
| --- | --- |
| _true / false_ | If true, tries to remove redundant special actors and self loops on delays. |


### PiSDF Single-Rate Transformation

  * **Identifier**: `pisdf-srdag`
  * **Implementing Class**: `org.preesm.model.pisdf.statictools.PiSDFToSingleRateTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters

##### Consistency_Method
Undocumented

| Value | Effect |
| --- | --- |
| _LCM_ |  |
| _Topology_ |  |


### PiSDF Transform Perfect Fit Delay To End Init

  * **Identifier**: `org.ietr.preesm.pisdf.transformperfectfitdelay`
  * **Implementing Class**: `org.preesm.model.pisdf.statictools.PiSDFTransformPerfectFitDelayToEndInitTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters
None.

## Synthesis

### Simple Synhtesis

  * **Identifier**: `pisdf-synthesis.simple`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.PreesmSynthesisTask`
  * **Short description**: Schedule and map actors, and allocate their memory.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Schedule** (of _Schedule_)
  * **Mapping** (of _Mapping_)
  * **Allocation** (of _Allocation_)

#### Description
Schedule and map actors and their communications, and allocate the buffer memory.Multiple available schedulers. Output is working only for the new code generation workflow tasks codegen2.

#### Parameters

##### scheduler
Scheduler used to schedule and map the tasks.

| Value | Effect |
| --- | --- |
| _simple_ | Naive greedy list scheduler. |
| _legacy_ | See workflow task pisdf-mapper.list. |
| _periodic_ | List scheduler (without communication times) respecting actor or graph periods, if any. |
| _choco_ | Optimal scheduler (without communication times) respecting actor or graph periods, if any. |

##### allocation
Allocate the memory for buffers.

| Value | Effect |
| --- | --- |
| _simple_ | Undocumented |
| _legacy_ | Undocumented |


### Simple Synhtesis

  * **Identifier**: `mappingscheduling2`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.tasks.MappingSchedulingTask`
  * **Short description**: Schedule and map actors, and allocate their memory.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Schedule** (of _Schedule_)
  * **Mapping** (of _Mapping_)

#### Description
Schedule and map actors and their communications, and allocate the buffer memory.Multiple available schedulers. Output is working only for the new code generation workflow tasks codegen2.

#### Parameters

##### scheduler
Scheduler used to schedule and map the tasks.

| Value | Effect |
| --- | --- |
| _simple_ | Naive greedy list scheduler. |
| _legacy_ | See workflow task pisdf-mapper.list. |
| _periodic_ | List scheduler (without communication times) respecting actor or graph periods, if any. |
| _choco_ | Optimal scheduler (without communication times) respecting actor or graph periods, if any. |

## Code Generation

### Spider Codegen

  * **Identifier**: `org.ietr.preesm.pimm.algorithm.spider.codegen.SpiderCodegenTask`
  * **Implementing Class**: `org.preesm.codegen.xtend.spider.SpiderCodegenTask`
  * **Short description**: Generate Spider code for dynamic PiSDF.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
None.

#### Description
Generate Spider code for dynamic PiSDF.

#### Parameters

##### scheduler
Runtime scheduler to use.

| Value | Effect |
| --- | --- |
| _list_on_the_go_ | Undocumented |
| _round_robin_ | Undocumented |
| _round_robin_scattered_ | Undocumented |
| _list_ | (Default) |

##### memory-alloc
Runtime memory allocation to use.

| Value | Effect |
| --- | --- |
| _special-actors_ | Undocumented |
| _dummy_ | (Default) |

##### shared-memory-size
Size of the shared memory allocated by Spider.

| Value | Effect |
| --- | --- |
| _$$n$$_ | $$n > 0$$ bytes. (Default = 67108864) |

##### papify
Use of PAPIFY. Select type of feedback given too

| Value | Effect |
| --- | --- |
| _off_ | PAPIFY is off |
| _dump_ | PAPIFY is on. Print csv files |
| _feedback_ | PAPIFY is on. Give feedback to the GRT |
| _both_ | PAPIFY is on. Print csv files and give feedback to the GRT |

##### apollo
Whether to use Apollo.

| Value | Effect |
| --- | --- |
| _true / false_ |  |

##### verbose
Whether to log.

| Value | Effect |
| --- | --- |
| _true / false_ |  |

##### trace
Whether to trace what is happening at runtime.

| Value | Effect |
| --- | --- |
| _true / false_ |  |

##### stack-type
Type of stack to use

| Value | Effect |
| --- | --- |
| _static_ | Use static stack |
| _dynamic_ | Use dynamic stack |

##### graph-optims
Whether to optimize the graph at runtime or not

| Value | Effect |
| --- | --- |
| _true / false_ |  |

##### energy-awareness
Whether to activate or not energy-aware mapping/scheduling.

| Value | Effect |
| --- | --- |
| _true/false_ | Enable/disable energy-aware mapping/scheduling |

#### See Also

  * **Spider**: Heulot, Julien; Pelcat, Maxime; Desnos, Karol; Nezan, Jean-François; Aridhi, Slaheddine (2014) “SPIDER: A Synchronous Parameterized and Interfaced Dataflow-Based RTOS for Multicore DSPs”. EDERC 2014, Milan, Italy.


### Spider2 Codegen

  * **Identifier**: `org.preesm.codegen.xtend.Spider2CodegenTask`
  * **Implementing Class**: `org.preesm.codegen.xtend.spider2.Spider2CodegenTask`
  * **Short description**: Generate code for spider2 library for dynamic PiSDF.

#### Inputs
  * **scenario** (of _Scenario_)

#### Outputs
None.

#### Description
Generate code for spider2 library for dynamic PiSDF.

#### Parameters

##### generate archi
Whether to generate archi file from slam description or not

| Value | Effect |
| --- | --- |
| _true / false_ | (Default = true) |

##### generate cmakelist
Whether to generate CMakeList.txt or not

| Value | Effect |
| --- | --- |
| _true / false_ | (Default = true) |

##### move includes
Whether to move the includes of the project into the generated include folder

| Value | Effect |
| --- | --- |
| _true / false_ | (Default = false) |

##### scheduler
Runtime scheduler to use.

| Value | Effect |
| --- | --- |
| _LIST_ | (Default) |
| _GREEDY_ | Undocumented |

##### mapper
Runtime mapper to use.

| Value | Effect |
| --- | --- |
| _BEST_FIT_ | (Default) |
| _ROUND_ROBIN_ | Undocumented |

##### allocator
Runtime fifo allocator to use.

| Value | Effect |
| --- | --- |
| _DEFAULT_ | (Default) |
| _DEFAULT_NOSYNC_ | Undocumented |

##### executionPolicy
Runtime execution policy to use.

| Value | Effect |
| --- | --- |
| _DELAYED_ | (Default) |
| _JIT_ | Undocumented |

##### runtime
Runtime algorithm to use.

| Value | Effect |
| --- | --- |
| _PISDF_BASED_ | (Default) |
| _SRDAG_BASED_ | Undocumented |

##### runMode
Run mode to use.

| Value | Effect |
| --- | --- |
| _LOOP_ | (Default) |
| _INFINITE_ | Undocumented |
| _EXTERN_LOOP_ | Undocumented |

##### use-verbose
Whether to enable verbose log.

| Value | Effect |
| --- | --- |
| _true / false_ |  |

##### enable-srdag-optims
Whether to optimize the srdag-graphs at runtime or not

| Value | Effect |
| --- | --- |
| _true / false_ |  |


### Code Generation

  * **Identifier**: `org.ietr.preesm.codegen.xtend.task.CodegenTask`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.CodegenTask`
  * **Short description**: Generate code for the application deployment resulting from the workflow execution.

#### Inputs
  * **MEGs** (of _Map_)
  * **DAG** (of _DirectedAcyclicGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
None.

#### Description
This workflow task is responsible for generating code for the application deployment resulting from the workflow execution.

The generated code makes use of 2 macros that can be overridden in the **preesm.h** user header file:
*  **PREESM_VERBOSE** : if defined, the code will print extra info about actor firing;
*  **PREESM_LOOP_SIZE** : when set to an integer value $$n > 0$$, the application will terminate after $$n$$ executions of the graph.
*  **PREESM_NO_AFFINITY** : if defined, the part of the code that sets the affinity to specific cores will be skipped;

When the loop size macro is omitted, the execution can be stopped by setting the global variable **preesmStopThreads** to 1. This variable is defined in the **main.c** generated file, and should be accessed using extern keyword.

#### Parameters

##### Printer
Specify which printer should be used to generate code. Printers are defined in Preesm source code using an extension mechanism that make it possible to define a single printer name for several targeted architecture. Hence, depending on the type of PEs declared in the architecture model, Preesm will automatically select the associated printer class, if it exists.

| Value | Effect |
| --- | --- |
| _C_ | Print C code and shared-memory based communications. Currently compatible with x86, c6678, and arm architectures. |
| _InstrumentedC_ | Print C code instrumented with profiling code, and shared-memory based communications. Currently compatible with x86, c6678 architectures.. |
| _XML_ | Print XML code with all informations used by other printers to print code. Compatible with x86, c6678. |

##### Papify
Enable the PAPI-based code instrumentation provided by PAPIFY

| Value | Effect |
| --- | --- |
| _true/false_ | Print C code instrumented with PAPIFY function calls based on the user-defined configuration of PAPIFY tab in the scenario. Currently compatibe with x86 and MPPA-256 |

##### Apollo
Enable the use of Apollo for intra-actor optimization

| Value | Effect |
| --- | --- |
| _true/false_ | Print C code with Apollo function calls. Currently compatibe with x86 |


### Code Generation with cluster

  * **Identifier**: `org.ietr.preesm.codegen.xtend.task.CodegenClusterTask`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.CodegenWithClusterTask`
  * **Short description**: Generate code for the application deployment resulting from the workflow execution.

#### Inputs
  * **MEGs** (of _Map_)
  * **DAG** (of _DirectedAcyclicGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)
  * **CS** (of _Map_)

#### Outputs
None.

#### Description
This workflow task is responsible for generating code for the application deployment resulting from the workflow execution.

The generated code makes use of 2 macros that can be overridden in the **preesm.h** user header file:
*  **PREESM_VERBOSE** : if defined, the code will print extra info about actor firing;
*  **PREESM_LOOP_SIZE** : when set to an integer value $$n > 0$$, the application will terminate after $$n$$ executions of the graph.
*  **PREESM_NO_AFFINITY** : if defined, the part of the code that sets the affinity to specific cores will be skipped;

When the loop size macro is omitted, the execution can be stopped by setting the global variable **preesmStopThreads** to 1. This variable is defined in the **main.c** generated file, and should be accessed using extern keyword.

#### Parameters

##### Printer
Specify which printer should be used to generate code. Printers are defined in Preesm source code using an extension mechanism that make it possible to define a single printer name for several targeted architecture. Hence, depending on the type of PEs declared in the architecture model, Preesm will automatically select the associated printer class, if it exists.

| Value | Effect |
| --- | --- |
| _C_ | Print C code and shared-memory based communications. Currently compatible with x86, c6678, and arm architectures. |
| _InstrumentedC_ | Print C code instrumented with profiling code, and shared-memory based communications. Currently compatible with x86, c6678 architectures.. |
| _XML_ | Print XML code with all informations used by other printers to print code. Compatible with x86, c6678. |

##### Papify
Enable the PAPI-based code instrumentation provided by PAPIFY

| Value | Effect |
| --- | --- |
| _true/false_ | Print C code instrumented with PAPIFY function calls based on the user-defined configuration of PAPIFY tab in the scenario. Currently compatibe with x86 and MPPA-256 |

## Code Generation 2

### Code Generation 2

  * **Identifier**: `codegen2`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.CodegenTask2`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)
  * **Schedule** (of _Schedule_)
  * **Mapping** (of _Mapping_)
  * **Allocation** (of _Allocation_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### Printer
Specify which printer should be used to generate code. Printers are defined in Preesm source code using an extension mechanism that make it possible to define a single printer name for several targeted architecture. Hence, depending on the type of PEs declared in the architecture model, Preesm will automatically select the associated printer class, if it exists.

| Value | Effect |
| --- | --- |
| _C_ | Print C code and shared-memory based communications. Currently compatible with x86, c6678, and arm architectures. |
| _InstrumentedC_ | Print C code instrumented with profiling code, and shared-memory based communications. Currently compatible with x86, c6678 architectures.. |
| _XML_ | Print XML code with all informations used by other printers to print code. Compatible with x86, c6678. |

##### Papify
Enable the PAPI-based code instrumentation provided by PAPIFY

| Value | Effect |
| --- | --- |
| _true/false_ | Print C code instrumented with PAPIFY function calls based on the user-defined configuration of PAPIFY tab in the scenario. Currently compatibe with x86 and MPPA-256 |

## Graph Exporters

### Parameters exporter

  * **Identifier**: `pisdf-export.parameters`
  * **Implementing Class**: `org.preesm.model.pisdf.serialize.ParametersExporterTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
None.

#### Description
Export parameters of the graph as C header with define. Exports only static parameters. Name of file is: <graphName>_preesm_params.h

#### Parameters

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _/Code_ | default path, relative to the project |


### PiSDF Exporter

  * **Identifier**: `pisdf-export`
  * **Implementing Class**: `org.preesm.model.pisdf.serialize.PiSDFExporterTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _/Algo/generated/pisdf/_ | default path |

##### hierarchical
Undocumented

| Value | Effect |
| --- | --- |
| _true/false_ | Export the whole hierarchy (default: true). When set to true, will export all the hierarchy in the folder given by 'path', replacing refinement paths. Note: exporting hierarchical graph with this option set to false can cause the  the consistency check fail if the children graphs do not exist. |

## Other

### Latency Evaluation

  * **Identifier**: `org.ietr.preesm.latency.LatencyEvaluationPlugin`
  * **Implementing Class**: `org.preesm.algorithm.latency.LatencyEvaluationTask`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **SDF** (of _SDFGraph_)
  * **scenario** (of _Scenario_)
  * **latency** (of _Double_)

#### Description
Undocumented

#### Parameters

##### multicore
Undocumented

| Value | Effect |
| --- | --- |
| _true/false_ |  |

##### method
Undocumented

| Value | Effect |
| --- | --- |
| _FAST_ | (default) Hierarchical method |
| _FLAT_LP_ | Based on Flattening the hierarchy |
| _FLAT_SE_ | Based on Flattening the hierarchy |


### GetPiMM

  * **Identifier**: `org.ietr.preesm.mapper.getpimm`
  * **Implementing Class**: `org.preesm.algorithm.mapper.GetPiMMFromDAGTask`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters
None.


### Hierarchical Scheduler - _Deprecated_

  * **Identifier**: `hsceduler`
  * **Implementing Class**: `org.preesm.algorithm.mapper.algo.HScheduleTask`
  * **Short description**: Undocumented

#### Inputs
None.

#### Outputs
None.

#### Description
Undocumented

#### Parameters
None.


### Single rate SDF to DAG Transformation

  * **Identifier**: `org.ietr.preesm.mapper.SDF2DAGTransformation`
  * **Implementing Class**: `org.preesm.algorithm.mapper.graphtransfo.SDF2DAGTransformation`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)

#### Description
Undocumented

#### Parameters
None.


### Memory Exclusion Graph Mapper

  * **Identifier**: `org.ietr.preesm.memory.distributed.MapperTask`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MapperTask`
  * **Short description**: Undocumented

#### Inputs
  * **MemEx** (of _MemoryExclusionGraph_)

#### Outputs
  * **MemExes** (of _Map_)

#### Description
Undocumented

#### Parameters

##### Verbose
Undocumented

| Value | Effect |
| --- | --- |
| _? C {True, False}_ |  |

##### Distribution
Specify which memory architecture should be used to allocate the memory.

| Value | Effect |
| --- | --- |
| _SharedOnly_ | (Default) All memory objects are allocated in a single memory bank accessible to all PE. |
| _DistributedOnly_ | Each PE is associated to a private memory bank that no other PE can access. (Currently not supported by code generation.) |
| _Mixed_ | Both private memory banks and a shared memory can be used for allocating memory. |
| _MixedMerged_ | Same as mixed, but the memory allocation algorithm favors buffer merging over memory distribution. |


### Activity Exporter of Tokens and Quanta

  * **Identifier**: `org.ietr.preesm.algorithm.moa.activity.ActivityExporter`
  * **Implementing Class**: `org.preesm.algorithm.moa.activity.ActivityExporter`
  * **Short description**: Undocumented

#### Inputs
  * **ABC** (of _LatencyAbc_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _stats/mat/activity_ | Undocumented |

##### human_readable
Undocumented

| Value | Effect |
| --- | --- |
| _Yes_ | Undocumented |


### Activity Exporter of Custom Quanta

  * **Identifier**: `org.ietr.preesm.algorithm.moa.activity.CustomQuantaExporter`
  * **Implementing Class**: `org.preesm.algorithm.moa.activity.CustomQuantaExporter`
  * **Short description**: Undocumented

#### Inputs
  * **ABC** (of _LatencyAbc_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### xls_file
Undocumented

| Value | Effect |
| --- | --- |
| _stats/mat/custom_quanta_in/quanta_in_$SCENARIO$.xls_ |  |

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _stats/mat/activity_ |  |

##### human_readable
Undocumented

| Value | Effect |
| --- | --- |
| _Yes_ |  |


### Activity Exporter of Tokens and Quanta for a single ABC

  * **Identifier**: `org.ietr.preesm.algorithm.moa.activity.MonoActivityExporter`
  * **Implementing Class**: `org.preesm.algorithm.moa.activity.MonoActivityExporter`
  * **Short description**: Undocumented

#### Inputs
  * **ABC** (of _LatencyAbc_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _stats/mat/activity_ |  |

##### human_readable
Undocumented

| Value | Effect |
| --- | --- |
| _Yes_ |  |


### Moldable Parameters setter

  * **Identifier**: `pisdf-mparams.setter`
  * **Implementing Class**: `org.preesm.algorithm.mparameters.SetMoldableParametersTask`
  * **Short description**: Set the moldable parameters default value according to the best schedule found.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Set the moldable parameters default value in the scenario according to the best schedule found.Works only on homogeneous architectures. Different strategies are possible, exhaustive search or heuristics.

#### Parameters

##### 1. Comparisons
Order of comparisons of the metrics (T for throughput or P for power or E for energy or L for latency or M for makespan, separated by >). Latency is indexed from 1 to the maximum number of pipeline stages allowed.

| Value | Effect |
| --- | --- |
| _T>P>L_ | Metrics are compare from left to right. |

##### 2. Thresholds
Objectives of the metrics. Threshold if it is any integer higher than 0, minimize it otherwise.

| Value | Effect |
| --- | --- |
| _0>0>0_ | In the same order as the metrics. |

##### 3. Params objectives
Tells to minimize (-) or maximize (+) a parameter (after main objectives). May be empty.

| Value | Effect |
| --- | --- |
| _>_ | Syntax: >+parentGraphName/parameterName>-... |

##### 4. Number heuristic
Use a DSE heuristic on all moldable parameter expressions which are integer numbers. Only a subset of their expressions are explored.

| Value | Effect |
| --- | --- |
| _false_ | False disables the heuristic. |

##### 5. Retry with delays
Use a DSE heuristic to try to add delays if it improves the throughput. See workflow task pisdf-delays.setter. Number of pipelines is inferred automatically.

| Value | Effect |
| --- | --- |
| _false_ | False disables the heuristic. |

##### 6. Scheduler
Set the scheduler used to estimate each configuration point.

| Value | Effect |
| --- | --- |
| _singleFPGAavegarge_ | Single FPGA average scheduler. |
| _homogeneousListPeriodic_ | Homogeneous periodic list scheduler. |

##### 7. Clustering distance
Set the clustering (positive) distance to be usedfor DSE points on the metrics Pareto front.

| Value | Effect |
| --- | --- |
| _0.0_ | Disables clustering if zero. |

##### 8. Log path
Export all explored points with associated metrics in a csv file.

| Value | Effect |
| --- | --- |
| _/Code/generated/_ | Path relative to the project root. |


### Automatic Placement of Delays

  * **Identifier**: `pisdf-delays.setter`
  * **Implementing Class**: `org.preesm.algorithm.pisdf.autodelays.AutoDelaysTask`
  * **Short description**: Puts delays in a flat PiMM, in order to speed up the execution.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Puts delays in a flat PiMM, in order to speed up the execution. Works only on homogeneous architectures. The heuristic will perform a search of all simple cycles, so the task may take a long time to run if many cycles are present.

#### Parameters

##### Selection cuts
Number of graph cuts to consider, higher or equal to the maximum number of cuts.

| Value | Effect |
| --- | --- |
| _4_ | Split the graph in zones of equivalent work load. |

##### Maximum cuts
Maximum number of graph cuts induced by the added delays. Each graph cut adds one pipeline stage. If delays are already present, the values are summed.

| Value | Effect |
| --- | --- |
| _1_ |  |

##### Test best choco ?
Computes all topological graph cuts with a CP solver. All topological cuts are evaluated with a list scheduler to select the best.

| Value | Effect |
| --- | --- |
| _false_ | False disables this comparison. |

##### Test scheduling ?
Whether or not a schedule must be generated at the end.

| Value | Effect |
| --- | --- |
| _false_ | False disables this feature. |

##### Fill cycles ?
Whether or not the cycles must be broken with extra delays.

| Value | Effect |
| --- | --- |
| _false_ | False disables this feature. |


### Periods Prescheduling Checker

  * **Identifier**: `org.ietr.preesm.pimm.algorithm.checker.periods.PeriodsPreschedulingChecker`
  * **Implementing Class**: `org.preesm.algorithm.pisdf.periods.PeriodsPreschedulingCheckTask`
  * **Short description**: Check necessary condition to schedule graphs with periods (at top level or in actors).

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Check necessary condition to schedule graphs with periods (at top level or in actors). Works only on flat graphs.

#### Parameters

##### Selection rate (%)
Percentage of periodic actors to consider.

| Value | Effect |
| --- | --- |
| _100_ | All periodic actors are checked. |


### Periodic scheduling (without output)

  * **Identifier**: `pisdf-synthesis.void-periodic-schedule`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.VoidPeriodicScheduleTask`
  * **Short description**: Schedule and maps actors according to their periods.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
None.

#### Description
Schedule and map actors according to their periods thanks to a list scheduler. Only works for homogeneous architectures, does not take into account communication times. Works also if there are no periods in the graph.

#### Parameters

##### solver
Algorithm used to schedule and map.

| Value | Effect |
| --- | --- |
| _list_ | List scheduler. |
| _choco_ | Optimal CP scheduler. |


### Throughput Evaluation

  * **Identifier**: `org.ietr.preesm.throughput.ThroughputPlugin`
  * **Implementing Class**: `org.preesm.algorithm.throughput.sdf.ThroughputEvaluationTask`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **throughput** (of _Double_)
  * **SDF** (of _SDFGraph_)
  * **scenario** (of _Scenario_)

#### Description
Undocumented

#### Parameters

##### method
Undocumented

| Value | Effect |
| --- | --- |
| _SR_ | Schedule-Replace technique |
| _ESR_ | Evaluate-Schedule-Replace method |
| _HPeriodic_ | Hierarchical Periodic Schedule method |
| _Classical_ | Based on Flattening the hierarchy |


### SDF2HSDF

  * **Identifier**: `org.ietr.preesm.plugin.transforms.sdf2hsdf`
  * **Implementing Class**: `org.preesm.algorithm.transforms.HSDFTransformation`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)

#### Outputs
  * **SDF** (of _SDFGraph_)

#### Description
transform a SDF graph into a HSDF graph, that is into a single rate graph

#### Parameters
None.


### Algorithm Iterator

  * **Identifier**: `org.ietr.preesm.algorithm.transforms.IterateAlgorithm`
  * **Implementing Class**: `org.preesm.algorithm.transforms.IterateAlgorithm`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)

#### Outputs
  * **SDF** (of _SDFGraph_)

#### Description
Undocumented

#### Parameters

##### nbIt
Undocumented

| Value | Effect |
| --- | --- |
| _1_ |  |

##### setStates
Undocumented

| Value | Effect |
| --- | --- |
| _true_ |  |


### FPGA estimation (thoughput + FIFO sizes)

  * **Identifier**: `pisdf-synthesis.fpga-estimations`
  * **Implementing Class**: `org.preesm.codegen.fpga.FpgaAnalysisMainTask`
  * **Short description**: Schedule actors and estimates the FIFO sizes.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
None.

#### Description
Schedule actors according to their ET and II thanks to an ASAP scheduler. Only works for single FPGA architectures with single frequency domain.Periods in the graph are not taken into account.

#### Parameters

##### Show schedule ?
Whether or not the schedule must be shown at the end.

| Value | Effect |
| --- | --- |
| _false_ | False disables this feature. |

##### Fifo evaluator: 
The name of fifo evaluator to be used.

| Value | Effect |
| --- | --- |
| _avgFifoEval_ | Evaluate with average mode. |
| _sdfFifoEval_ | Evaluate with SDF mode. |
| _adfgFifoEvalExact_ | Evaluate with ADFG exact mode using ojAlgo. |
| _adfgFifoEvalLinear_ | Evaluate with ADFG linear approximation mode using ojAlgo. |

##### Pack tokens ?
Whether or not the tokens should be packed to otpimize bram usage.

| Value | Effect |
| --- | --- |
| _false_ | False disables this feature. |


### Papify Engine - _Deprecated_

  * **Identifier**: `org.ietr.preesm.codegen.xtend.task.CodegenPapifyEngineTask`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.CodegenPapifyEngineTask`
  * **Short description**: Deprecated - does nothing (as of v3.9.1). See parameter 'Papify' in CodegenTask'.

Old doc: Generate the required instrumentation code for the application based on the PAPIFY tab information.

#### Inputs
  * **scenario** (of _Scenario_)
  * **DAG** (of _DirectedAcyclicGraph_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)

#### Description
Deprecated - does nothing (as of v3.9.1). See parameter 'Papify' in CodegenTask'.

Old doc: This workflow task is responsible for generating the instrumentation of the code for the application based on the PAPIFY tab information.

The generated code makes use of 1 macro that enables/disables the monitoring in the **preesm.h** user header file:
*  **_PREESM_PAPIFY_MONITOR** : if defined, the code monitoring will take place;


#### Parameters
None.


### PiSDF BRV Exporter

  * **Identifier**: `pisdf-brv-export`
  * **Implementing Class**: `org.preesm.model.pisdf.brv.BRVExporter`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _/stats/xml/_ | default value |


### PiSDF Checker

  * **Identifier**: `pisdf-checker`
  * **Implementing Class**: `org.preesm.model.pisdf.check.PiGraphConsistenceCheckerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters
None.


### scenario

  * **Identifier**: `org.ietr.preesm.scenario.task`
  * **Implementing Class**: `org.preesm.model.scenario.workflow.AlgorithmAndArchitectureScenarioNode`
  * **Short description**: Undocumented

#### Inputs
None.

#### Outputs
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters
None.


### Slam Flattener

  * **Identifier**: `org.ietr.preesm.archi.slam.flattening`
  * **Implementing Class**: `org.preesm.model.slam.utils.SlamHierarchyFlattening`
  * **Short description**: Undocumented

#### Inputs
  * **architecture** (of _Design_)

#### Outputs
  * **architecture** (of _Design_)

#### Description
Undocumented

#### Parameters

##### depth
Undocumented

| Value | Effect |
| --- | --- |
| _n > 0_ | default = 1 |

## Clustering

### Clustering task

  * **Identifier**: `clustering.creation`
  * **Implementing Class**: `org.preesm.algorithm.clustering.ClusterCreationTask`
  * **Short description**: Workflow task responsible for identifying, creating, schedule, map and allocate clusters.
The goal is to reduce the complexity of the graph without loosing parallelism,
according to the given heuristics.


#### Inputs
  * **scenario** (of _Scenario_) : Input scenario
  * **PiMM** (of _PiGraph_) : Input PiGraph algorithm
  * **architecture** (of _Design_) : Input S-LAM architecture graph

#### Outputs
  * **PiMM** (of _PiGraph_) : Output PiGraph algorithm, modified by heuristics
  * **scenario** (of _Scenario_) : Modified scenario, with the clusters constraints
  * **clusters** (of _List_) : List containing the created clusters
  * **schedules** (of _List_) : List containing the clusters schedule
  * **allocations** (of _List_) : List containing the clusters allocation

#### Description
Workflow task responsible for identifying, creating, schedule, map and allocate clusters.
The goal is to reduce the complexity of the graph without loosing parallelism,
according to the given heuristics. The available heuristics are described in the parameters section.
If the user wants to create an heuristic, he will have to create them in the source code of PREESM,
in the package org.preesm.algorithm.clustering.


#### Parameters

##### Vertical heuristic
Name of which heuristic will be used to vertically clusterize the graph.
If there is not enough or too many hierarchical level following the heuristic,
it will create or regroup hierarchical levels. This parameter is optional,
and can not be set at all. If so, the input graph will just be flatten.
For now, there is no vertical heuristic.


##### Horizontal heuristic
Name of which heuristic will be used to horizontaly clusterize the graph.Every hierachical
level of the graph will be explored.
There is two available horizontal heuristic:
- The Unique Repetition Count (URC) that will regroup together the actors that are adjacent
and have the same repetition count in the graph
- The Single Repetition Vector (SRV) that will create clusters that contain only one actor,
with a repetition count higher than the number of processing elements. Even if the cluster
contains one actor, the DAG will be smaller since the cluster in the top level will have a
lower repetition count than the actor contained in it.
- The heteregeneous architecture separator that will create clusters only composed of actor mapped on FPGA

| Value | Effect |
| --- | --- |
| _urc_ | Undocumented |
| _urc_ | Undocumented |
| _simple heterogeneous arch_ | Undocumented |

##### Mapping heuristic
Name of which heuristic will assign a component type on which the cluster could be executed on:
- The Simple mapper will select the same component type for every cluster than the main operator one.
- The Classic mapper will select a component that is at least shared by every actors in the cluster.
If none, throws an error.


| Value | Effect |
| --- | --- |
| _classic mapper_ | Undocumented |
| _simple mapper_ | Undocumented |

##### Balancing heuristic
Name of which heuristic will balance the clusters in the graph. By default, clusters are set to its
maximal repetition, but the balancer can modify that. There is two available balancers :
- Complete balancer : will handle balancing even if repetition count of cluster is not divisible
by number of available processing elements, by duplicating the cluster and asdding fork/join actors.
- Basic balancer : will handle balancing only if repetition count of cluster is a divisor of number of
available processing elements.


| Value | Effect |
| --- | --- |
| _complete balancing_ | Undocumented |
| _basic balancing_ | Undocumented |

##### Scheduling heuristic
Name of which heuristic will schedule the clusters in the graph. For now, the only available scheduling
algorithm is the APGAN algorithm. We can't reuse existing scheduling algorithm as they depend on a DAG.
However, DAG creation must be avoided in the cluster synthesis process.


| Value | Effect |
| --- | --- |
| _apgan scheduling_ | Undocumented |

##### Allocation heuristic
Name of which heuristic will allocate the clusters memory. Work with a cluster scheduler only
(see scheduling heuristic parameters for all available schedulers that can work with cluster
allocation).


| Value | Effect |
| --- | --- |
| _simple allocation_ | Undocumented |

##### Verbose
More or less logs

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

##### Debug
Enable intermediate graph creation, to check if everything is ok between substeps.

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

#### See Also

  * - Renaud, Ophélie, Dylan Gageot, Karol Desnos, et Jean-François Nezan.
SCAPE: HW-Aware Clustering of Dataflow Actors for Tunable Scheduling Complexity.
In Design and Architecture for Signal and Image Processing, 2023.

- Renaud, Ophelie, Naouel Haggui, Karol Desnos, et J. F. Nezan. Automated Clustering
and Pipelining of Dataflow Actors for Controlled Scheduling Complexity. 22 octobre 2023.



### Clustering task

  * **Identifier**: `clustering.condensed`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.BigClusteringTask`
  * **Short description**: Workflow task responsible for clustering actors. 

#### Inputs
  * **scenario** (of _Scenario_) : Input scenario
  * **PiMM** (of _PiGraph_) : Input PiGraph algorithm
  * **architecture** (of _Design_) : Input S-LAM architecture graph

#### Outputs
  * **PiMM** (of _PiGraph_) : Output PiGraph algorithm, modified by heuristics
  * **scenario** (of _Scenario_) : Modified scenario
  * **architecture** (of _Design_) : Untouched architecture, just to avoid having an ugly workflow graph.

#### Description
Workflow task responsible for clustering actors. It regroups the 2 clustering tasks (ClusterCreationTask and ClusterCodegenTask) in one. It is prefered to use the 2 separated classes.For more infos on clustering, go see the two following classes : ClusterCreationTask and ClusterCodegenTask.

#### Parameters

##### Vertical heuristic


##### Horizontal heuristic


##### Mapping heuristic


##### Balancing heuristic


##### Scheduling heuristic


##### Allocation heuristic


##### Printer


##### Verbose


##### Debug



### Clustering task

  * **Identifier**: `clustering.codegen`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.ClusterCodegenTask`
  * **Short description**: Workflow task responsible of generating the files that contain the code of all the identified clusters.

#### Inputs
  * **scenario** (of _Scenario_) : Input scenario, containing constraints of clusters
  * **clusters** (of _List_) : Input clusters
  * **schedules** (of _List_) : Input schedules of clusters
  * **allocations** (of _List_) : Input allocations of clusters

#### Outputs
None.

#### Description
    This task works with the task ClusterCreationTask. It will generate the code for every clusters in
    separate files. They will be used during the top graph synthesis process.


#### Parameters

##### Printer
Will select the printer for the right language. For now, only C is available.

| Value | Effect |
| --- | --- |
| _C_ | Undocumented |

##### Verbose


| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

##### Debug


| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

## Schedulers

### External Scheduling from DAG

  * **Identifier**: `org.ietr.preesm.plugin.mapper.external`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ExternalMappingFromDAG`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
This class imports schedule expressed in dedicated json format. It is experimental and limited to flat PiMM and a few architectures (regular x86 and Odroid). See package org.preesm.algorithm.mapper.schedule for the json format.

#### Parameters

##### SCHEDULE_FILE
Undocumented

| Value | Effect |
| --- | --- |
| _/schedule.json_ | default value |


### External Scheduling from PiSDF

  * **Identifier**: `pisdf-mapper.external`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ExternalMappingFromPiMM`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### SCHEDULE_FILE
Undocumented

| Value | Effect |
| --- | --- |
| _/schedule.json_ | default value |


### List Scheduling from SDF - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.fast`
  * **Implementing Class**: `org.preesm.algorithm.mapper.FASTMapping`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |


### Fast Scheduling from DAG - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.fastdag`
  * **Implementing Class**: `org.preesm.algorithm.mapper.FASTMappingFromDAG`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |


### Fast Scheduling from PiSDF

  * **Identifier**: `pisdf-mapper.fast`
  * **Implementing Class**: `org.preesm.algorithm.mapper.FASTMappingFromPiMM`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |


### List Scheduling from SDF - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.listscheduling`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ListSchedulingMapping`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |


### List Scheduling from DAG - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.listschedulingfromdag`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ListSchedulingMappingFromDAG`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple / Switcher_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### EnergyAwareness
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |


### List Scheduling from PiSDF

  * **Identifier**: `pisdf-mapper.list`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ListSchedulingMappingFromPiMM`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### EnergyAwareness
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Turns on energy aware mapping/scheduling |

##### EnergyAwarenessFirstConfig
Undocumented

| Value | Effect |
| --- | --- |
| _First_ | Takes as starting point the first valid combination of PEs |
| _Middle_ | Takes as starting point half of the available PEs |
| _Max_ | Takes as starting point all the available PEs |
| _Random_ | Takes as starting point a random number of PEs |

##### EnergyAwarenessSearchType
Undocumented

| Value | Effect |
| --- | --- |
| _Thorough_ | Analyzes PE combinations one by one until the performance objective is reached |
| _Halves_ | Divides in halves the remaining available PEs and goes up/down depending if the FPS reached are below/above the objective |


### Simple Scheduling from DAG - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.simple`
  * **Implementing Class**: `org.preesm.algorithm.mapper.MainCoreMappingFromDAG`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters
None.


### Simple Scheduling from PiSDF

  * **Identifier**: `pisdf-mapper.simple`
  * **Implementing Class**: `org.preesm.algorithm.mapper.MainCoreMappingFromPiMM`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters
None.


### PFast Scheduling from SDF - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.pfast`
  * **Implementing Class**: `org.preesm.algorithm.mapper.PFASTMapping`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |

##### nodesMin
Undocumented

| Value | Effect |
| --- | --- |
| _5_ | Undocumented |

##### procNumber
Undocumented

| Value | Effect |
| --- | --- |
| _1_ | Undocumented |

##### fastNumber
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |


### PFast Scheduling from DAG - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.mapper.pfastdag`
  * **Implementing Class**: `org.preesm.algorithm.mapper.PFASTMappingFromDAG`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |

##### nodesMin
Undocumented

| Value | Effect |
| --- | --- |
| _5_ | Undocumented |

##### procNumber
Undocumented

| Value | Effect |
| --- | --- |
| _1_ | Undocumented |

##### fastNumber
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |


### PFast Scheduling from PiSDF

  * **Identifier**: `pisdf-mapper.pfast`
  * **Implementing Class**: `org.preesm.algorithm.mapper.PFASTMappingFromPiMM`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _false_ | Undocumented |

##### fastTime
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |

##### fastLocalSearchTime
Undocumented

| Value | Effect |
| --- | --- |
| _10_ | Undocumented |

##### nodesMin
Undocumented

| Value | Effect |
| --- | --- |
| _5_ | Undocumented |

##### procNumber
Undocumented

| Value | Effect |
| --- | --- |
| _1_ | Undocumented |

##### fastNumber
Undocumented

| Value | Effect |
| --- | --- |
| _100_ | Undocumented |


### Periodic Scheduling from PiSDF to old DAG

  * **Identifier**: `pisdf-mapper.periodic.DAG`
  * **Implementing Class**: `org.preesm.algorithm.mapper.PeriodicMappingFromPiMMTask`
  * **Short description**: Schedule and maps actors according to their periods.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **ABC** (of _LatencyAbc_)

#### Description
Schedule and map actors according to their periods thanks to a list scheduler. Only works for homogeneous architectures, does not take into account communication times. Works also if there are no periods in the graph. Result is exported in the same format as pisdf-mapper.list standard scheduler.

#### Parameters
None.


### Periodic Scheduling using choco solver from PiSDF

  * **Identifier**: `mapper2.choco`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.ChocoSchedulerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Mapping** (of _Mapping_)
  * **Schedule** (of _Schedule_)

#### Description
Undocumented

#### Parameters

##### Max Solution
Undocumented

##### Verbose
Undocumented


### List Scheduling from PiSDF

  * **Identifier**: `mapper2.list`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.LegacyListSchedulerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Mapping** (of _Mapping_)
  * **Schedule** (of _Schedule_)

#### Description
Undocumented

#### Parameters

##### edgeSchedType
Undocumented

| Value | Effect |
| --- | --- |
| _Simple_ | Undocumented |

##### simulatorType
Undocumented

| Value | Effect |
| --- | --- |
| _LooselyTimed_ | Undocumented |

##### Check
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

##### Optimize synchronization
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

##### balanceLoads
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Undocumented |
| _False_ | Undocumented |

##### EnergyAwareness
Undocumented

| Value | Effect |
| --- | --- |
| _True_ | Turns on energy aware mapping/scheduling |
| _False_ | Undocumented |

##### EnergyAwarenessFirstConfig
Undocumented

| Value | Effect |
| --- | --- |
| _First_ | Takes as starting point the first valid combination of PEs |
| _Middle_ | Takes as starting point half of the available PEs |
| _Max_ | Takes as starting point all the available PEs |
| _Random_ | Takes as starting point a random number of PEs |

##### EnergyAwarenessSearchType
Undocumented

| Value | Effect |
| --- | --- |
| _Thorough_ | Analyzes PE combinations one by one until the performance objective is reached |
| _Halves_ | Divides in halves the remaining available PEs and goes up/down depending if the FPS reached are below/above the objective |


### Periodic Scheduling from PiSDF

  * **Identifier**: `mapper2.periodic`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.PeriodicSchedulerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Mapping** (of _Mapping_)
  * **Schedule** (of _Schedule_)

#### Description
Undocumented

#### Parameters
None.


### Periodic Scheduling from PiSDF

  * **Identifier**: `mapper2.simple`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.SimpleSchedulerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Mapping** (of _Mapping_)
  * **Schedule** (of _Schedule_)

#### Description
Undocumented

#### Parameters
None.

## Gantt exporters

### ABC Gantt exporter

  * **Identifier**: `org.ietr.preesm.stats.exporter.StatsExporterTask`
  * **Implementing Class**: `org.preesm.algorithm.mapper.stats.exporter.StatsExporterTask`
  * **Short description**: This task exports scheduling results as a *.pgantt file that can be viewed using the ganttDisplay viewer [1].

#### Inputs
  * **ABC** (of _LatencyAbc_)
  * **scenario** (of _Scenario_)

#### Outputs
None.

#### Description
This task exports scheduling results as a *.pgantt file that can be viewed using the ganttDisplay viewer [1]. The exported *.pgantt file uses the XML syntax.

#### Parameters

##### path
Path of the exported *.pgantt file. If the specified directory does not exist, it will not be created.

| Value | Effect |
| --- | --- |
| _/stats/xml/_ | Path within the Preesm project containing the workflow where the ”Gantt Exporter” task is instantiated. Exported Gantt will be named as follows: **/path/in/proj/<scenario name> stats.pgantt**. If a graph with this name already exists in the given path, it will be overwritten. |

#### See Also

  * **[1]**: https://github.com/preesm/gantt-display


### ABC Gantt displayer

  * **Identifier**: `org.ietr.preesm.plugin.mapper.plot`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ui.stats.StatEditorAbcTask`
  * **Short description**: Displays the result of a mapping/scheduling algorithm as a Gantt diagram.

#### Inputs
  * **ABC** (of _LatencyAbc_)
  * **scenario** (of _Scenario_)

#### Outputs
None.

#### Description
Displays the result of a mapping/scheduling algorithm as a Gantt diagram.

#### Parameters
None.

#### See Also

  * **Speedup assessment chart**: Maxime Pelcat. Prototypage Rapide et Génération de Code pour DSP Multi-Coeurs Appliqués à la Couche Physique des Stations de Base 3GPP LTE. PhD thesis, INSA de Rennes, 2010.


### Synthesis Gantt displayer and exporter

  * **Identifier**: `gantt-output`
  * **Implementing Class**: `org.preesm.algorithm.mapper.ui.stats.StatEditorSynthesisTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)
  * **Schedule** (of _Schedule_)
  * **Mapping** (of _Mapping_)
  * **Allocation** (of _Allocation_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### display
Specify if statistics, including Gantt diagram, must be displayed or not.

| Value | Effect |
| --- | --- |
| _true/false_ | Undocumented |

##### file path
Folder to store Gantt diagram as xml file. Path is relative to the project, put "/" if at root, may be empty.

| Value | Effect |
| --- | --- |
| _/path/to_ | Undocumented |

## Memory Optimization

### MEG Updater

  * **Identifier**: `org.ietr.preesm.memory.exclusiongraph.MemExUpdater`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MemExUpdater`
  * **Short description**: Relax memory allocation constraints of the MEG using scheduling information.

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **MemEx** (of _MemoryExclusionGraph_)

#### Outputs
  * **MemEx** (of _MemoryExclusionGraph_)

#### Description
The MEG used in Preesm can be updated with scheduling information to remove exclusions between memory objects and make better allocations possible.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the start and completion time of the update, as well as characteristics (number of memory objects, density of exclusions) of the MEGs both before and after the update.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

#### See Also

  * **MEG update**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.


### Memory Allocation

  * **Identifier**: `org.ietr.preesm.memory.allocation.MemoryAllocatorTask`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MemoryAllocatorTask`
  * **Short description**: Perform the memory allocation for the given MEG.

#### Inputs
  * **MemEx** (of _MemoryExclusionGraph_) : Input Memory Exclusion Graph

#### Outputs
  * **MEGs** (of _Map_) : Map associating, for each memory element in the architecture, according to the chosen _Distribution_ parameter value, a Memory Exclusion Graph annotated with allocation information (i.e. buffer addresses, etc.).

#### Description
Workflow task responsible for allocating the memory objects of the given MEG.

#### Parameters

##### Verbose
Verbosity of the task.

| Value | Effect |
| --- | --- |
| _True_ | Detailed statistics of the allocation process are logged |
| _False_ | Logged information is kept to a minimum |

##### Allocator(s)
Specify which memory allocation algorithm(s) should be used. If the string value of the parameters contains several algorithm names, all will be executed one by one.

| Value | Effect |
| --- | --- |
| _Basic_ | Each memory object is allocated in a dedicated memory space. Memory allocated for a given object is not reused for other. |
| _BestFit_ | Memory objects are allocated one by one; allocating each object to the available space in memory whose size is the closest to the size of the allocated object. If MEG exclusions permit it, memory allocated for a memory object may be reused for others. |
| _FirstFit_ | Memory objects are allocated one by one; allocating each object to the first available space in memory whose size is the large enough to allocate the object. If MEG exclusions permit it, memory allocated for a memory object may be reused for others. |
| _DeGreef_ | Algorithm adapted from DeGreef (1997)}. If MEG exclusions permit it, memory allocated for a memory object may be reused for others. |

##### Distribution
Specify which memory architecture should be used to allocate the memory.

| Value | Effect |
| --- | --- |
| _SharedOnly_ | (Default) All memory objects are allocated in a single memory bank accessible to all PE. |
| _DistributedOnly_ | Each PE is associated to a private memory bank that no other PE can access. (Currently supported only in the MPPA code generation.) |
| _Mixed_ | Both private memory banks and a shared memory can be used for allocating memory. |
| _MixedMerged_ | Same as mixed, but the memory allocation algorithm favors buffer merging over memory distribution. |

##### Best/First Fit order
When using FirstFit or BestFit memory allocators, this parameter specifies in which order the memory objects will be fed to the allocation algorithm. If the string value associated to the parameters contains several order names, all will be executed one by one.

| Value | Effect |
| --- | --- |
| _ApproxStableSet_ | Memory objects are sorted into disjoint stable sets. Stable sets are formed one after the other, each with the largest possible number of object. Memory objects are fed to the allocator set by set and in the largest first order within each stable set. |
| _ExactStableSet_ | Similar to 'ApproxStableSet'. Stable set are built using an exact algorithm instead of a heuristic. |
| _LargestFirst_ | Memory objects are allocated in decreasing order of their size. |
| _Shuffle_ | Memory objects are allocated in a random order. Using the 'Nb of Shuffling Tested' parameter, it is possible to test several random orders and only keep the best memory allocation. |

##### Data alignment
Option used to force the allocation of buffers (i.e. Memory objects) with aligned addresses. The data alignment property should always have the same value as the one set in the properties of the Memory Scripts task.

| Value | Effect |
| --- | --- |
| _None_ | No special care is taken to align the buffers in memory. |
| _Data_ | All buffers are aligned on addresses that are multiples of their size. For example, a 32 bites integer is aligned on 32 bits address. |
| _Fixed:=n_ | Where $$n\in \mathbb{N}^*$$. This forces the allocation algorithm to align all buffers on addresses that are multiples of n bits. |

##### Nb of Shuffling Tested
Number of random order tested when using the Shuffle value for the Best/First Fit order parameter.

| Value | Effect |
| --- | --- |
| _$$n\in \mathbb{N}^*$$_ | Number of random order. |

#### Documented Errors

| Message | Explanation |
| --- | --- |
| **The obtained allocation was not valid because mutually exclusive memory objects have overlapping address ranges. The allocator is not working.** | When checking the result of a memory allocation, two memory objects linked with an exclusion in the MEG were allocated in overlapping memory spaces. The error is caused by an invalid memory allocation algorithm and should be corrected in the source code. |
| **The obtained allocation was not valid because there were unaligned memory objects. The allocator is not working.** | When checking the result of a memory allocation, some memory objects were found not to respect the Dala alignment parameter. The error is caused by an invalid memory allocation algorithm and should be corrected in the source code. |

#### See Also

  * **Memory Allocation Algorithms**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.
  * **Distributed Memory Allocation**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. Distributed memory allocation technique for synchronous dataflow graphs. In Signal Processing System (SiPS), Workshop on, pages 1–6. IEEE, 2016.
  * **Broadcast Merging**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory analysis and optimized allocation of dataflow applications on shared-memory MPSoCs. Journal of Signal Processing Systems, Springer, 2014.


### Memory Bounds Estimator

  * **Identifier**: `org.ietr.preesm.memory.bounds.MemoryBoundsEstimator`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MemoryBoundsEstimator`
  * **Short description**: Compute bounds of the amount of memory needed to allocate the MEG

#### Inputs
  * **MemEx** (of _MemoryExclusionGraph_)

#### Outputs
  * **MemEx** (of _MemoryExclusionGraph_)
  * **BoundMin** (of _Long_)
  * **BoundMax** (of _Long_)

#### Description
The analysis technique presented in [1] can be used in Preesm to derive bounds for the amount of memory that can be allocated for an application. The upper bound corresponds to the worst memory allocation possible for an application. The lower bound is a theoretical value that limits the minimum amount of memory that can be allocated. By definition, the lower bound is not always reachable, which means that it might be impossible to find an allocation with this optimal amount of memory. The minimum bound is found by solving the Maximum Weight Clique problem on the MEG. This task provides a convenient way to evaluate the quality of a memory allocation.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the name of the used solver, the start and completion time of the bound estimation algorithm. Computed bounds are always logged, even if the verbose parameter is set to false.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

##### Solver
Specify which algorithm is used to compute the lower bound.

| Value | Effect |
| --- | --- |
| _Heuristic_ | (Default) Heuristic algorithm described in [1] is used. This technique find an approximate solution. |
| _Ostergard_ | Östergård’s algorithm [2] is used. This technique finds an optimal solution, but has a potentially exponential complexity. |
| _Yamaguchi_ | Yamaguchi et al.’s algorithm [3] is used. This technique finds an optimal solution, but has a potentially exponential complexity. |

#### See Also

  * **[1]**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory bounds for the distributed execution of a hierarchical synchronous data-flow graph. In Embedded Computer Systems: Architectures, Modeling, and Simulation (SAMOS XII), 2012 International Conference on, 2012.
  * **[2]**: Patric R. J. Östergård. A new algorithm for the maximum-weight clique problem. Nordic J. of Computing, 8(4):424–436, December 2001.
  * **[3]**: K. Yamaguchi and S. Masuda. A new exact algorithm for the maximum weight clique problem. In 23rd International Conference on Circuit/Systems, Computers and Communications (ITC-CSCC’08), 2008.
  * **Memory Bounds**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.


### MEG Builder

  * **Identifier**: `org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraphBuilder`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MemoryExclusionGraphBuilder`
  * **Short description**: Builds the Memory Exclusion Graph (MEG) modeling the memory allocation constraints.

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **MemEx** (of _MemoryExclusionGraph_)

#### Description
The memory allocation technique used in Preesm is based on a Memory Exclusion Graph (MEG). A MEG is a graph whose vertices model the memory objects that must be allocated in memory in order to run the generated code. In the current version of Preesm, each of these memory objects corresponds either to an edge of the Directed Acyclic Graph (DAG) or to a buffer corresponding to ”delays” of the graph that store data between executions of a schedule. In the MEG, two memory objects are linked by an edge (called an exclusion) if they can not be allocated in overlapping memory spaces.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the start and completion time of the build, as well as characteristics (number of memory objects, density of exclusions) of the produced MEG.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

#### See Also

  * **MEG**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory bounds for the distributed execution of a hierarchical synchronous data-flow graph. In Embedded Computer Systems: Architectures, Modeling, and Simulation (SAMOS XII), 2012 International Conference on, 2012.


### Memory Scripts

  * **Identifier**: `org.ietr.preesm.memory.script.MemoryScriptTask`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MemoryScriptTask`
  * **Short description**: Executes the memory scripts associated to actors and merge buffers.

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **MemEx** (of _MemoryExclusionGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **MemEx** (of _MemoryExclusionGraph_)

#### Description
Executes the memory scripts associated to actors and merge buffers. The purpose of the memory scripts is to allow Preesm to allocate input and output buffers of certain actors in overlapping memory range.

#### Parameters

##### Check
Verification policy used when checking the applicability of the memory scripts written by the developer and associated to the actor.

| Value | Effect |
| --- | --- |
| _Thorough_ | Will generate error messages with a detailed description of the source of the error. This policy should be used when writting memory scripts for the first time. |
| _Fast_ | All errors in memory script are still detected, but error messages are less verbose. This verification policy is faster than the Thorough policy. |
| _None_ | No verification is performed. Use this policy to speed up workflow execution once all memory scripts have been validated.. |

##### False Sharing Prevention
Force additional allocation before/after buffer to prevent false sharing issues.

| Value | Effect |
| --- | --- |
| _False_ | The false sharing prevention mecanism will not be used. |
| _True_ | The false sharing prevention mecanism will be used.Using the Data alignement parameter. |

##### Data alignment
Option used to force the allocation of buffers with aligned addresses. The data alignment property should always have the same value as the one set in the properties of the Memory Allocation task.

| Value | Effect |
| --- | --- |
| _None_ | No special care is taken to align the buffers in memory. |
| _Data_ | All buffers are aligned on addresses that are multiples of their size. For example,a 32 bits integer is aligned on 32 bits address. |
| _Fixed:=$$n$$_ | Where $$n\in \mathbb{N}^*$$. This forces the allocation algorithm to align all buffers on addresses that are multiples of n bits. |

##### Log Path
Specify whether, and where, a log of the buffer matching optimization should be generated. Generated log are in the markdown format, and provide information on all matches created by scripts as well as which match could be applied by the optimization process.

| Value | Effect |
| --- | --- |
| _path/file.txt_ | The path given in this property is relative to the ”Code generation directory” defined in the executed scenario. |
| _empty_ | No log will be generated. |

##### Verbose
Verbosity of the workflow task.

| Value | Effect |
| --- | --- |
| _True_ | The workflow task will be verbose in the console. |
| _False_ | The workflow task will be more quiet in the console. |

#### See Also

  * **Buffer merging**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. On memory reuse between inputs and outputs of dataflow actors. ACM Transactions on Embedded Computing Systems, 15(30):25, January 2016.


### Serial Memory Bounds

  * **Identifier**: `org.ietr.preesm.memory.bounds.SerialMemoryBoundsEstimator`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.SerialMemoryBoundsEstimator`
  * **Short description**: Compute bounds of the amount of memory needed to allocate the MEGs.

#### Inputs
  * **MEGs** (of _Map_)

#### Outputs
None.

#### Description
This task computes the memory bounds (see Memory Bound Estimator Task) for several MEGs, like the one produced by the Memory Allocation task.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the name of the used solver, the start and completion time of the bound estimation algorithm. Computed bounds are always logged, even if the verbose parameter is set to false.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

##### Solver
Specify which algorithm is used to compute the lower bound.

| Value | Effect |
| --- | --- |
| _Heuristic_ | (Default) Heuristic algorithm described in [1] is used. This technique find an approximate solution. |
| _Ostergard_ | Östergård’s algorithm [2] is used. This technique finds an optimal solution, but has a potentially exponential complexity. |
| _Yamaguchi_ | Yamaguchi et al.’s algorithm [3] is used. This technique finds an optimal solution, but has a potentially exponential complexity. |

#### See Also

  * **[1]**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory bounds for the distributed execution of a hierarchical synchronous data-flow graph. In Embedded Computer Systems: Architectures, Modeling, and Simulation (SAMOS XII), 2012 International Conference on, 2012.
  * **[2]**: Patric R. J. Östergård. A new algorithm for the maximum-weight clique problem. Nordic J. of Computing, 8(4):424–436, December 2001.
  * **[3]**: K. Yamaguchi and S. Masuda. A new exact algorithm for the maximum weight clique problem. In 23rd International Conference on Circuit/Systems, Computers and Communications (ITC-CSCC’08), 2008.
  * **Memory Bounds**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.


### Memory Allocation

  * **Identifier**: `alloc2.memalloc`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.memalloc.allocation.PiMemoryAllocatorTask`
  * **Short description**: Perform the memory allocation for the given MEG.

#### Inputs
  * **MemEx** (of _PiMemoryExclusionGraph_) : Input Memory Exclusion Graph
  * **Mapping** (of _Mapping_) : Input Mapping
  * **scenario** (of _Scenario_) : Input scenario
  * **architecture** (of _Design_) : Input architecture
  * **PiMM** (of _PiGraph_) : Input algorithm (in DAG form)

#### Outputs
  * **Allocation** (of _Allocation_) : Final allocation, built on the input MEG

#### Description
Workflow task responsible for allocating the memory objects of the given MEG.

#### Parameters

##### Verbose
Verbosity of the task.

| Value | Effect |
| --- | --- |
| _True_ | Detailed statistics of the allocation process are logged |
| _False_ | Logged information is kept to a minimum |

##### Allocator(s)
Specify which memory allocation algorithm(s) should be used. If the string value of the parameters contains several algorithm names, all will be executed one by one.

| Value | Effect |
| --- | --- |
| _Basic_ | Each memory object is allocated in a dedicated memory space. Memory allocated for a given object is not reused for other. |
| _BestFit_ | Memory objects are allocated one by one; allocating each object to the available space in memory whose size is the closest to the size of the allocated object. If MEG exclusions permit it, memory allocated for a memory object may be reused for others. |
| _FirstFit_ | Memory objects are allocated one by one; allocating each object to the first available space in memory whose size is the large enough to allocate the object. If MEG exclusions permit it, memory allocated for a memory object may be reused for others. |

##### Distribution
Specify which memory architecture should be used to allocate the memory.

| Value | Effect |
| --- | --- |
| _SharedOnly_ | (Default) All memory objects are allocated in a single memory bank accessible to all PE. |
| _DistributedOnly_ | Each PE is associated to a private memory bank that no other PE can access. (Currently supported only in the MPPA code generation.) |
| _Mixed_ | Both private memory banks and a shared memory can be used for allocating memory. |
| _MixedMerged_ | Same as mixed, but the memory allocation algorithm favors buffer merging over memory distribution. |

##### Best/First Fit order
When using FirstFit or BestFit memory allocators, this parameter specifies in which order the memory objects will be fed to the allocation algorithm. If the string value associated to the parameters contains several order names, all will be executed one by one.

| Value | Effect |
| --- | --- |
| _ApproxStableSet_ | Memory objects are sorted into disjoint stable sets. Stable sets are formed one after the other, each with the largest possible number of object. Memory objects are fed to the allocator set by set and in the largest first order within each stable set. |
| _ExactStableSet_ | Similar to 'ApproxStableSet'. Stable set are built using an exact algorithm instead of a heuristic. |
| _LargestFirst_ | Memory objects are allocated in decreasing order of their size. |
| _Shuffle_ | Memory objects are allocated in a random order. Using the 'Nb of Shuffling Tested' parameter, it is possible to test several random orders and only keep the best memory allocation. |

##### Data alignment
Option used to force the allocation of buffers (i.e. Memory objects) with aligned addresses. The data alignment property should always have the same value as the one set in the properties of the Memory Scripts task.

| Value | Effect |
| --- | --- |
| _None_ | No special care is taken to align the buffers in memory. |
| _Data_ | All buffers are aligned on addresses that are multiples of their size. For example, a 32 bites integer is aligned on 32 bits address. |
| _Fixed:=n_ | Where $$n\in \mathbb{N}^*$$. This forces the allocation algorithm to align all buffers on addresses that are multiples of n bits. |

##### Nb of Shuffling Tested
Number of random order tested when using the Shuffle value for the Best/First Fit order parameter.

| Value | Effect |
| --- | --- |
| _$$n\in \mathbb{N}^*$$_ | Number of random order. |

#### Documented Errors

| Message | Explanation |
| --- | --- |
| **The obtained allocation was not valid because mutually exclusive memory objects have overlapping address ranges. The allocator is not working.** | When checking the result of a memory allocation, two memory objects linked with an exclusion in the MEG were allocated in overlapping memory spaces. The error is caused by an invalid memory allocation algorithm and should be corrected in the source code. |
| **The obtained allocation was not valid because there were unaligned memory objects. The allocator is not working.** | When checking the result of a memory allocation, some memory objects were found not to respect the Dala alignment parameter. The error is caused by an invalid memory allocation algorithm and should be corrected in the source code. |

#### See Also

  * **Memory Allocation Algorithms**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.
  * **Distributed Memory Allocation**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. Distributed memory allocation technique for synchronous dataflow graphs. In Signal Processing System (SiPS), Workshop on, pages 1–6. IEEE, 2016.
  * **Broadcast Merging**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory analysis and optimized allocation of dataflow applications on shared-memory MPSoCs. Journal of Signal Processing Systems, Springer, 2014.


### MEG Builder

  * **Identifier**: `alloc2.megbuilder`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.memalloc.meg.PiMEGBuilderTask`
  * **Short description**: Builds the Memory Exclusion Graph (MEG) modeling the memory allocation constraints.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **MemEx** (of _PiMemoryExclusionGraph_)

#### Description
The memory allocation technique used in Preesm is based on a Memory Exclusion Graph (MEG). A MEG is a graph whose vertices model the memory objects that must be allocated in memory in order to run the generated code. In the current version of Preesm, each of these memory objects corresponds either to an edge of the Directed Acyclic Graph (DAG) or to a buffer corresponding to ”delays” of the graph that store data between executions of a schedule. In the MEG, two memory objects are linked by an edge (called an exclusion) if they can not be allocated in overlapping memory spaces.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the start and completion time of the build, as well as characteristics (number of memory objects, density of exclusions) of the produced MEG.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

#### See Also

  * **MEG**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory bounds for the distributed execution of a hierarchical synchronous data-flow graph. In Embedded Computer Systems: Architectures, Modeling, and Simulation (SAMOS XII), 2012 International Conference on, 2012.


### MEG Updater

  * **Identifier**: `alloc2.megupdater`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.memalloc.meg.PiMEGUpdaterTask`
  * **Short description**: Relax memory allocation constraints of the MEG using scheduling information.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **Schedule** (of _Schedule_)
  * **Mapping** (of _Mapping_)
  * **MemEx** (of _PiMemoryExclusionGraph_)

#### Outputs
  * **MemEx** (of _PiMemoryExclusionGraph_)

#### Description
The MEG used in Preesm can be updated with scheduling information to remove exclusions between memory objects and make better allocations possible.

#### Parameters

##### Verbose
How verbose will this task be during its execution. In verbose mode, the task will log the start and completion time of the update, as well as characteristics (number of memory objects, density of exclusions) of the MEGs both before and after the update.

| Value | Effect |
| --- | --- |
| _false_ | (Default) The task will not log information. |
| _true_ | The task will log build and MEG information. |

#### See Also

  * **MEG update**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013.


### Memory Scripts

  * **Identifier**: `alloc2.memoryscript`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.memalloc.script.PiMemoryScriptTask`
  * **Short description**: Executes the memory scripts associated to actors and merge buffers.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **MemEx** (of _PiMemoryExclusionGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **MemEx** (of _PiMemoryExclusionGraph_)

#### Description
Executes the memory scripts associated to actors and merge buffers. The purpose of the memory scripts is to allow Preesm to allocate input and output buffers of certain actors in overlapping memory range.

#### Parameters

##### Check
Verification policy used when checking the applicability of the memory scripts written by the developer and associated to the actor.

| Value | Effect |
| --- | --- |
| _Thorough_ | Will generate error messages with a detailed description of the source of the error. This policy should be used when writting memory scripts for the first time. |
| _Fast_ | All errors in memory script are still detected, but error messages are less verbose. This verification policy is faster than the Thorough policy. |
| _None_ | No verification is performed. Use this policy to speed up workflow execution once all memory scripts have been validated.. |

##### False Sharing Prevention
Force additional allocation before/after buffer to prevent false sharing issues.

| Value | Effect |
| --- | --- |
| _False_ | The false sharing prevention mecanism will not be used. |
| _True_ | The false sharing prevention mecanism will be used.Using the Data alignement parameter. |

##### Data alignment
Option used to force the allocation of buffers with aligned addresses. The data alignment property should always have the same value as the one set in the properties of the Memory Allocation task.

| Value | Effect |
| --- | --- |
| _None_ | No special care is taken to align the buffers in memory. |
| _Data_ | All buffers are aligned on addresses that are multiples of their size. For example,a 32 bits integer is aligned on 32 bits address. |
| _Fixed:=$$n$$_ | Where $$n\in \mathbb{N}^*$$. This forces the allocation algorithm to align all buffers on addresses that are multiples of n bits. |

##### Log Path
Specify whether, and where, a log of the buffer matching optimization should be generated. Generated log are in the markdown format, and provide information on all matches created by scripts as well as which match could be applied by the optimization process.

| Value | Effect |
| --- | --- |
| _path/file.txt_ | The path given in this property is relative to the ”Code generation directory” defined in the executed scenario. |
| _empty_ | No log will be generated. |

##### Verbose
Verbosity of the workflow task.

| Value | Effect |
| --- | --- |
| _True_ | The workflow task will be verbose in the console. |
| _False_ | The workflow task will be more quiet in the console. |

#### See Also

  * **Buffer merging**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. On memory reuse between inputs and outputs of dataflow actors. ACM Transactions on Embedded Computing Systems, 15(30):25, January 2016.

## Graph Transformation

### Static PiMM to IBSDF - _Deprecated_

  * **Identifier**: `org.ietr.preesm.experiment.pimm2sdf.StaticPiMM2SDFTask`
  * **Implementing Class**: `org.preesm.algorithm.pisdf.pimm2sdf.StaticPiMM2SDFTask`
  * **Short description**: Transforms a static PiSDF Graph into an equivalent IBSDF graph.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **SDF** (of _SDFGraph_)

#### Description
In Preesm, since version 2.0.0, the Parameterized and Interfaced SDF (PiSDF) model of computa tion is used as the frontend model in the graphical editor of dataflow graphs. This model makes it possible to design dynamically reconfigurable dataflow graphs where the value of parameters, and production/consumption rates depending on them, might change during the execution of the application. In former versions, the Interface Based SDF (IBSDF) model of computation was used as the front end model for application design. Contrary to the PiSDF, the IBSDF is a static model of computation where production and consumption rates of actors is fixed at compile-time.

The purpose of this workflow task is to transform a static PiSDF graph into an equivalent IBSDF graph. A static PiSDF graph is a PiSDF graph where dynamic reconfiguration features of the PiSDF model of computation are not used.

#### Parameters
None.

#### See Also

  * **IBSDF**: J. Piat, S.S. Bhattacharyya, and M. Raulet. Interface-based hierarchy for synchronous data-flow graphs. In SiPS Proceedings, 2009.
  * **PiSDF**: K. Desnos, M. Pelcat, J.-F. Nezan, S.S. Bhattacharyya, and S. Aridhi. PiMM: Parameterized and interfaced dataflow meta-model for MPSoCs runtime reconfiguration. In Embedded Computer Systems: Architectures, Modeling, and Simulation (SAMOS XIII), 2013 International Conference on, pages 41–48. IEEE, 2013.


### Single-Rate Transformation - _Deprecated_

  * **Identifier**: `org.ietr.preesm.experiment.pimm2srdag.StaticPiMM2SrDAGTask`
  * **Implementing Class**: `org.preesm.algorithm.pisdf.pimm2srdag.StaticPiMM2SrDAGTask`
  * **Short description**: Transforms an SDF graph into an equivalent single-rate SDF graph.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)
  * **PiMM** (of _PiGraph_)

#### Description
In Preesm, since version 2.0.0, the Parameterized and Interfaced SDF (PiSDF) model of computa tion is used as the frontend model in the graphical editor of dataflow graphs. This model makes it possible to design dynamically reconfigurable dataflow graphs where the value of parameters, and production/consumption rates depending on them, might change during the execution of the application. In former versions, the Interface Based SDF (IBSDF) model of computation was used as the front end model for application design. Contrary to the PiSDF, the IBSDF is a static model of computation where production and consumption rates of actors is fixed at compile-time.

The purpose of this workflow task is to transform a static PiSDF graph into an equivalent IBSDF graph. A static PiSDF graph is a PiSDF graph where dynamic reconfiguration features of the PiSDF model of computation are not used.

#### Parameters

##### ExplodeImploreSuppr
_(Deprecated: use at your own risks)_

This parameter makes it possible to remove most of the explode and implode actors that are inserted in the graph during the single-rate transformation. The resulting SDF graph is an ill-constructed graph where a single data input/output port of an actor may be connected to several First-In, First-Out queues (Fifos).

| Value | Effect |
| --- | --- |
| _false_ | (default) The suppression of explode/implode special actors is not activated. |
| _true_ | The suppression of explode/implode special actors is activated. |

#### Documented Errors

| Message | Explanation |
| --- | --- |
| **Graph not valid, not schedulable** | Single-rate transformation of the SDF graph was aborted because the top level was not consistent, or it was consistent but did not contained enough delays — i.e. initial data tokens — to make it schedulable. |

#### See Also

  * **Single-rate transformation**: J.L. Pino, S.S. Bhattacharyya, and E.A. Lee. A hierarchical multiprocessor scheduling framework for synchronous dataflow graphs. Electronics Research Laboratory, College of Engineering, University of California, 1995.
  * **Special actors**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. On memory reuse between inputs and outputs of dataflow actors. ACM Transactions on Embedded Computing Systems, 15(30):25, January 2016.
  * **Graph consistency**: E.A. Lee and D.G. Messerschmitt. Synchronous data flow. Proceedings of the IEEE, 75(9):1235 – 1245, sept. 1987.


### Hierarchy Flattening - _Deprecated_

  * **Identifier**: `org.ietr.preesm.plugin.transforms.flathierarchy`
  * **Implementing Class**: `org.preesm.algorithm.transforms.HierarchyFlattening`
  * **Short description**: Transforms a hierarchical IBSDF graph into an equivalent SDF graph.

#### Inputs
  * **SDF** (of _SDFGraph_)

#### Outputs
  * **SDF** (of _SDFGraph_)

#### Description
The purpose of this workflow task is to flatten several levels of the hierarchy of an IBSDF graph and produce an equivalent SDF graph. A hierarchical IBSDF graph is a graph where the internal behavior of some actors is described using another IBSDF subgraph instead of a C header file. When applying this transformation, hierarchical IBSDF actors of the first n levels of hierarchy are replaced with the actors of the IBSDF subgraph with which these hierarchical actors are associated.

#### Parameters
None.

#### Documented Errors

| Message | Explanation |
| --- | --- |
| **Inconsistent Hierarchy, graph can’t be flattened** | Flattening of the IBSDF graph was aborted because one of the graph composing the application, at the top level or deeper in the hierarchy, was not consistent. |

#### See Also

  * **IBSDF**: J. Piat, S.S. Bhattacharyya, and M. Raulet. Interface-based hierarchy for synchronous data-flow graphs. In SiPS Proceedings, 2009.
  * **Graph consistency**: E.A. Lee and D.G. Messerschmitt. Synchronous data flow. Proceedings of the IEEE, 75(9):1235 – 1245, sept. 1987.


### PiSDF Flattener

  * **Identifier**: `org.ietr.preesm.pimm.algorithm.pimm2flat.StaticPiMM2FlatPiMMTask`
  * **Implementing Class**: `org.preesm.model.pisdf.statictools.PiSDFFlattenerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters

##### Perform optimizations
Undocumented

| Value | Effect |
| --- | --- |
| _true / false_ | If true, tries to remove redundant special actors and self loops on delays. |


### PiSDF Single-Rate Transformation

  * **Identifier**: `pisdf-srdag`
  * **Implementing Class**: `org.preesm.model.pisdf.statictools.PiSDFToSingleRateTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters

##### Consistency_Method
Undocumented

| Value | Effect |
| --- | --- |
| _LCM_ |  |
| _Topology_ |  |


### PiSDF Transform Perfect Fit Delay To End Init

  * **Identifier**: `org.ietr.preesm.pisdf.transformperfectfitdelay`
  * **Implementing Class**: `org.preesm.model.pisdf.statictools.PiSDFTransformPerfectFitDelayToEndInitTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters
None.

## Synthesis

### Simple Synhtesis

  * **Identifier**: `pisdf-synthesis.simple`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.PreesmSynthesisTask`
  * **Short description**: Schedule and map actors, and allocate their memory.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Schedule** (of _Schedule_)
  * **Mapping** (of _Mapping_)
  * **Allocation** (of _Allocation_)

#### Description
Schedule and map actors and their communications, and allocate the buffer memory.Multiple available schedulers. Output is working only for the new code generation workflow tasks codegen2.

#### Parameters

##### scheduler
Scheduler used to schedule and map the tasks.

| Value | Effect |
| --- | --- |
| _simple_ | Naive greedy list scheduler. |
| _legacy_ | See workflow task pisdf-mapper.list. |
| _periodic_ | List scheduler (without communication times) respecting actor or graph periods, if any. |
| _choco_ | Optimal scheduler (without communication times) respecting actor or graph periods, if any. |

##### allocation
Allocate the memory for buffers.

| Value | Effect |
| --- | --- |
| _simple_ | Undocumented |
| _legacy_ | Undocumented |


### Simple Synhtesis

  * **Identifier**: `mappingscheduling2`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.tasks.MappingSchedulingTask`
  * **Short description**: Schedule and map actors, and allocate their memory.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **Schedule** (of _Schedule_)
  * **Mapping** (of _Mapping_)

#### Description
Schedule and map actors and their communications, and allocate the buffer memory.Multiple available schedulers. Output is working only for the new code generation workflow tasks codegen2.

#### Parameters

##### scheduler
Scheduler used to schedule and map the tasks.

| Value | Effect |
| --- | --- |
| _simple_ | Naive greedy list scheduler. |
| _legacy_ | See workflow task pisdf-mapper.list. |
| _periodic_ | List scheduler (without communication times) respecting actor or graph periods, if any. |
| _choco_ | Optimal scheduler (without communication times) respecting actor or graph periods, if any. |

## Code Generation

### Spider Codegen

  * **Identifier**: `org.ietr.preesm.pimm.algorithm.spider.codegen.SpiderCodegenTask`
  * **Implementing Class**: `org.preesm.codegen.xtend.spider.SpiderCodegenTask`
  * **Short description**: Generate Spider code for dynamic PiSDF.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
None.

#### Description
Generate Spider code for dynamic PiSDF.

#### Parameters

##### scheduler
Runtime scheduler to use.

| Value | Effect |
| --- | --- |
| _list_on_the_go_ | Undocumented |
| _round_robin_ | Undocumented |
| _round_robin_scattered_ | Undocumented |
| _list_ | (Default) |

##### memory-alloc
Runtime memory allocation to use.

| Value | Effect |
| --- | --- |
| _special-actors_ | Undocumented |
| _dummy_ | (Default) |

##### shared-memory-size
Size of the shared memory allocated by Spider.

| Value | Effect |
| --- | --- |
| _$$n$$_ | $$n > 0$$ bytes. (Default = 67108864) |

##### papify
Use of PAPIFY. Select type of feedback given too

| Value | Effect |
| --- | --- |
| _off_ | PAPIFY is off |
| _dump_ | PAPIFY is on. Print csv files |
| _feedback_ | PAPIFY is on. Give feedback to the GRT |
| _both_ | PAPIFY is on. Print csv files and give feedback to the GRT |

##### apollo
Whether to use Apollo.

| Value | Effect |
| --- | --- |
| _true / false_ |  |

##### verbose
Whether to log.

| Value | Effect |
| --- | --- |
| _true / false_ |  |

##### trace
Whether to trace what is happening at runtime.

| Value | Effect |
| --- | --- |
| _true / false_ |  |

##### stack-type
Type of stack to use

| Value | Effect |
| --- | --- |
| _static_ | Use static stack |
| _dynamic_ | Use dynamic stack |

##### graph-optims
Whether to optimize the graph at runtime or not

| Value | Effect |
| --- | --- |
| _true / false_ |  |

##### energy-awareness
Whether to activate or not energy-aware mapping/scheduling.

| Value | Effect |
| --- | --- |
| _true/false_ | Enable/disable energy-aware mapping/scheduling |

#### See Also

  * **Spider**: Heulot, Julien; Pelcat, Maxime; Desnos, Karol; Nezan, Jean-François; Aridhi, Slaheddine (2014) “SPIDER: A Synchronous Parameterized and Interfaced Dataflow-Based RTOS for Multicore DSPs”. EDERC 2014, Milan, Italy.


### Spider2 Codegen

  * **Identifier**: `org.preesm.codegen.xtend.Spider2CodegenTask`
  * **Implementing Class**: `org.preesm.codegen.xtend.spider2.Spider2CodegenTask`
  * **Short description**: Generate code for spider2 library for dynamic PiSDF.

#### Inputs
  * **scenario** (of _Scenario_)

#### Outputs
None.

#### Description
Generate code for spider2 library for dynamic PiSDF.

#### Parameters

##### generate archi
Whether to generate archi file from slam description or not

| Value | Effect |
| --- | --- |
| _true / false_ | (Default = true) |

##### generate cmakelist
Whether to generate CMakeList.txt or not

| Value | Effect |
| --- | --- |
| _true / false_ | (Default = true) |

##### move includes
Whether to move the includes of the project into the generated include folder

| Value | Effect |
| --- | --- |
| _true / false_ | (Default = false) |

##### scheduler
Runtime scheduler to use.

| Value | Effect |
| --- | --- |
| _LIST_ | (Default) |
| _GREEDY_ | Undocumented |

##### mapper
Runtime mapper to use.

| Value | Effect |
| --- | --- |
| _BEST_FIT_ | (Default) |
| _ROUND_ROBIN_ | Undocumented |

##### allocator
Runtime fifo allocator to use.

| Value | Effect |
| --- | --- |
| _DEFAULT_ | (Default) |
| _DEFAULT_NOSYNC_ | Undocumented |

##### executionPolicy
Runtime execution policy to use.

| Value | Effect |
| --- | --- |
| _DELAYED_ | (Default) |
| _JIT_ | Undocumented |

##### runtime
Runtime algorithm to use.

| Value | Effect |
| --- | --- |
| _PISDF_BASED_ | (Default) |
| _SRDAG_BASED_ | Undocumented |

##### runMode
Run mode to use.

| Value | Effect |
| --- | --- |
| _LOOP_ | (Default) |
| _INFINITE_ | Undocumented |
| _EXTERN_LOOP_ | Undocumented |

##### use-verbose
Whether to enable verbose log.

| Value | Effect |
| --- | --- |
| _true / false_ |  |

##### enable-srdag-optims
Whether to optimize the srdag-graphs at runtime or not

| Value | Effect |
| --- | --- |
| _true / false_ |  |


### Code Generation

  * **Identifier**: `org.ietr.preesm.codegen.xtend.task.CodegenTask`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.CodegenTask`
  * **Short description**: Generate code for the application deployment resulting from the workflow execution.

#### Inputs
  * **MEGs** (of _Map_)
  * **DAG** (of _DirectedAcyclicGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
None.

#### Description
This workflow task is responsible for generating code for the application deployment resulting from the workflow execution.

The generated code makes use of 2 macros that can be overridden in the **preesm.h** user header file:
*  **PREESM_VERBOSE** : if defined, the code will print extra info about actor firing;
*  **PREESM_LOOP_SIZE** : when set to an integer value $$n > 0$$, the application will terminate after $$n$$ executions of the graph.
*  **PREESM_NO_AFFINITY** : if defined, the part of the code that sets the affinity to specific cores will be skipped;

When the loop size macro is omitted, the execution can be stopped by setting the global variable **preesmStopThreads** to 1. This variable is defined in the **main.c** generated file, and should be accessed using extern keyword.

#### Parameters

##### Printer
Specify which printer should be used to generate code. Printers are defined in Preesm source code using an extension mechanism that make it possible to define a single printer name for several targeted architecture. Hence, depending on the type of PEs declared in the architecture model, Preesm will automatically select the associated printer class, if it exists.

| Value | Effect |
| --- | --- |
| _C_ | Print C code and shared-memory based communications. Currently compatible with x86, c6678, and arm architectures. |
| _InstrumentedC_ | Print C code instrumented with profiling code, and shared-memory based communications. Currently compatible with x86, c6678 architectures.. |
| _XML_ | Print XML code with all informations used by other printers to print code. Compatible with x86, c6678. |

##### Papify
Enable the PAPI-based code instrumentation provided by PAPIFY

| Value | Effect |
| --- | --- |
| _true/false_ | Print C code instrumented with PAPIFY function calls based on the user-defined configuration of PAPIFY tab in the scenario. Currently compatibe with x86 and MPPA-256 |

##### Apollo
Enable the use of Apollo for intra-actor optimization

| Value | Effect |
| --- | --- |
| _true/false_ | Print C code with Apollo function calls. Currently compatibe with x86 |


### Code Generation with cluster

  * **Identifier**: `org.ietr.preesm.codegen.xtend.task.CodegenClusterTask`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.CodegenWithClusterTask`
  * **Short description**: Generate code for the application deployment resulting from the workflow execution.

#### Inputs
  * **MEGs** (of _Map_)
  * **DAG** (of _DirectedAcyclicGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)
  * **CS** (of _Map_)

#### Outputs
None.

#### Description
This workflow task is responsible for generating code for the application deployment resulting from the workflow execution.

The generated code makes use of 2 macros that can be overridden in the **preesm.h** user header file:
*  **PREESM_VERBOSE** : if defined, the code will print extra info about actor firing;
*  **PREESM_LOOP_SIZE** : when set to an integer value $$n > 0$$, the application will terminate after $$n$$ executions of the graph.
*  **PREESM_NO_AFFINITY** : if defined, the part of the code that sets the affinity to specific cores will be skipped;

When the loop size macro is omitted, the execution can be stopped by setting the global variable **preesmStopThreads** to 1. This variable is defined in the **main.c** generated file, and should be accessed using extern keyword.

#### Parameters

##### Printer
Specify which printer should be used to generate code. Printers are defined in Preesm source code using an extension mechanism that make it possible to define a single printer name for several targeted architecture. Hence, depending on the type of PEs declared in the architecture model, Preesm will automatically select the associated printer class, if it exists.

| Value | Effect |
| --- | --- |
| _C_ | Print C code and shared-memory based communications. Currently compatible with x86, c6678, and arm architectures. |
| _InstrumentedC_ | Print C code instrumented with profiling code, and shared-memory based communications. Currently compatible with x86, c6678 architectures.. |
| _XML_ | Print XML code with all informations used by other printers to print code. Compatible with x86, c6678. |

##### Papify
Enable the PAPI-based code instrumentation provided by PAPIFY

| Value | Effect |
| --- | --- |
| _true/false_ | Print C code instrumented with PAPIFY function calls based on the user-defined configuration of PAPIFY tab in the scenario. Currently compatibe with x86 and MPPA-256 |

## Code Generation 2

### Code Generation 2

  * **Identifier**: `codegen2`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.CodegenTask2`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)
  * **Schedule** (of _Schedule_)
  * **Mapping** (of _Mapping_)
  * **Allocation** (of _Allocation_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### Printer
Specify which printer should be used to generate code. Printers are defined in Preesm source code using an extension mechanism that make it possible to define a single printer name for several targeted architecture. Hence, depending on the type of PEs declared in the architecture model, Preesm will automatically select the associated printer class, if it exists.

| Value | Effect |
| --- | --- |
| _C_ | Print C code and shared-memory based communications. Currently compatible with x86, c6678, and arm architectures. |
| _InstrumentedC_ | Print C code instrumented with profiling code, and shared-memory based communications. Currently compatible with x86, c6678 architectures.. |
| _XML_ | Print XML code with all informations used by other printers to print code. Compatible with x86, c6678. |

##### Papify
Enable the PAPI-based code instrumentation provided by PAPIFY

| Value | Effect |
| --- | --- |
| _true/false_ | Print C code instrumented with PAPIFY function calls based on the user-defined configuration of PAPIFY tab in the scenario. Currently compatibe with x86 and MPPA-256 |

## Graph Exporters

### Parameters exporter

  * **Identifier**: `pisdf-export.parameters`
  * **Implementing Class**: `org.preesm.model.pisdf.serialize.ParametersExporterTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
None.

#### Description
Export parameters of the graph as C header with define. Exports only static parameters. Name of file is: <graphName>_preesm_params.h

#### Parameters

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _/Code_ | default path, relative to the project |


### PiSDF Exporter

  * **Identifier**: `pisdf-export`
  * **Implementing Class**: `org.preesm.model.pisdf.serialize.PiSDFExporterTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _/Algo/generated/pisdf/_ | default path |

##### hierarchical
Undocumented

| Value | Effect |
| --- | --- |
| _true/false_ | Export the whole hierarchy (default: true). When set to true, will export all the hierarchy in the folder given by 'path', replacing refinement paths. Note: exporting hierarchical graph with this option set to false can cause the  the consistency check fail if the children graphs do not exist. |

## Other

### Latency Evaluation

  * **Identifier**: `org.ietr.preesm.latency.LatencyEvaluationPlugin`
  * **Implementing Class**: `org.preesm.algorithm.latency.LatencyEvaluationTask`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **SDF** (of _SDFGraph_)
  * **scenario** (of _Scenario_)
  * **latency** (of _Double_)

#### Description
Undocumented

#### Parameters

##### multicore
Undocumented

| Value | Effect |
| --- | --- |
| _true/false_ |  |

##### method
Undocumented

| Value | Effect |
| --- | --- |
| _FAST_ | (default) Hierarchical method |
| _FLAT_LP_ | Based on Flattening the hierarchy |
| _FLAT_SE_ | Based on Flattening the hierarchy |


### GetPiMM

  * **Identifier**: `org.ietr.preesm.mapper.getpimm`
  * **Implementing Class**: `org.preesm.algorithm.mapper.GetPiMMFromDAGTask`
  * **Short description**: Undocumented

#### Inputs
  * **DAG** (of _DirectedAcyclicGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters
None.


### Hierarchical Scheduler - _Deprecated_

  * **Identifier**: `hsceduler`
  * **Implementing Class**: `org.preesm.algorithm.mapper.algo.HScheduleTask`
  * **Short description**: Undocumented

#### Inputs
None.

#### Outputs
None.

#### Description
Undocumented

#### Parameters
None.


### Single rate SDF to DAG Transformation

  * **Identifier**: `org.ietr.preesm.mapper.SDF2DAGTransformation`
  * **Implementing Class**: `org.preesm.algorithm.mapper.graphtransfo.SDF2DAGTransformation`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)

#### Description
Undocumented

#### Parameters
None.


### Memory Exclusion Graph Mapper

  * **Identifier**: `org.ietr.preesm.memory.distributed.MapperTask`
  * **Implementing Class**: `org.preesm.algorithm.memory.allocation.tasks.MapperTask`
  * **Short description**: Undocumented

#### Inputs
  * **MemEx** (of _MemoryExclusionGraph_)

#### Outputs
  * **MemExes** (of _Map_)

#### Description
Undocumented

#### Parameters

##### Verbose
Undocumented

| Value | Effect |
| --- | --- |
| _? C {True, False}_ |  |

##### Distribution
Specify which memory architecture should be used to allocate the memory.

| Value | Effect |
| --- | --- |
| _SharedOnly_ | (Default) All memory objects are allocated in a single memory bank accessible to all PE. |
| _DistributedOnly_ | Each PE is associated to a private memory bank that no other PE can access. (Currently not supported by code generation.) |
| _Mixed_ | Both private memory banks and a shared memory can be used for allocating memory. |
| _MixedMerged_ | Same as mixed, but the memory allocation algorithm favors buffer merging over memory distribution. |


### Activity Exporter of Tokens and Quanta

  * **Identifier**: `org.ietr.preesm.algorithm.moa.activity.ActivityExporter`
  * **Implementing Class**: `org.preesm.algorithm.moa.activity.ActivityExporter`
  * **Short description**: Undocumented

#### Inputs
  * **ABC** (of _LatencyAbc_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _stats/mat/activity_ | Undocumented |

##### human_readable
Undocumented

| Value | Effect |
| --- | --- |
| _Yes_ | Undocumented |


### Activity Exporter of Custom Quanta

  * **Identifier**: `org.ietr.preesm.algorithm.moa.activity.CustomQuantaExporter`
  * **Implementing Class**: `org.preesm.algorithm.moa.activity.CustomQuantaExporter`
  * **Short description**: Undocumented

#### Inputs
  * **ABC** (of _LatencyAbc_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### xls_file
Undocumented

| Value | Effect |
| --- | --- |
| _stats/mat/custom_quanta_in/quanta_in_$SCENARIO$.xls_ |  |

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _stats/mat/activity_ |  |

##### human_readable
Undocumented

| Value | Effect |
| --- | --- |
| _Yes_ |  |


### Activity Exporter of Tokens and Quanta for a single ABC

  * **Identifier**: `org.ietr.preesm.algorithm.moa.activity.MonoActivityExporter`
  * **Implementing Class**: `org.preesm.algorithm.moa.activity.MonoActivityExporter`
  * **Short description**: Undocumented

#### Inputs
  * **ABC** (of _LatencyAbc_)

#### Outputs
None.

#### Description
Undocumented

#### Parameters

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _stats/mat/activity_ |  |

##### human_readable
Undocumented

| Value | Effect |
| --- | --- |
| _Yes_ |  |


### Moldable Parameters setter

  * **Identifier**: `pisdf-mparams.setter`
  * **Implementing Class**: `org.preesm.algorithm.mparameters.SetMoldableParametersTask`
  * **Short description**: Set the moldable parameters default value according to the best schedule found.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Set the moldable parameters default value in the scenario according to the best schedule found.Works only on homogeneous architectures. Different strategies are possible, exhaustive search or heuristics.

#### Parameters

##### 1. Comparisons
Order of comparisons of the metrics (T for throughput or P for power or E for energy or L for latency or M for makespan, separated by >). Latency is indexed from 1 to the maximum number of pipeline stages allowed.

| Value | Effect |
| --- | --- |
| _T>P>L_ | Metrics are compare from left to right. |

##### 2. Thresholds
Objectives of the metrics. Threshold if it is any integer higher than 0, minimize it otherwise.

| Value | Effect |
| --- | --- |
| _0>0>0_ | In the same order as the metrics. |

##### 3. Params objectives
Tells to minimize (-) or maximize (+) a parameter (after main objectives). May be empty.

| Value | Effect |
| --- | --- |
| _>_ | Syntax: >+parentGraphName/parameterName>-... |

##### 4. Number heuristic
Use a DSE heuristic on all moldable parameter expressions which are integer numbers. Only a subset of their expressions are explored.

| Value | Effect |
| --- | --- |
| _false_ | False disables the heuristic. |

##### 5. Retry with delays
Use a DSE heuristic to try to add delays if it improves the throughput. See workflow task pisdf-delays.setter. Number of pipelines is inferred automatically.

| Value | Effect |
| --- | --- |
| _false_ | False disables the heuristic. |

##### 6. Scheduler
Set the scheduler used to estimate each configuration point.

| Value | Effect |
| --- | --- |
| _singleFPGAavegarge_ | Single FPGA average scheduler. |
| _homogeneousListPeriodic_ | Homogeneous periodic list scheduler. |

##### 7. Clustering distance
Set the clustering (positive) distance to be usedfor DSE points on the metrics Pareto front.

| Value | Effect |
| --- | --- |
| _0.0_ | Disables clustering if zero. |

##### 8. Log path
Export all explored points with associated metrics in a csv file.

| Value | Effect |
| --- | --- |
| _/Code/generated/_ | Path relative to the project root. |


### Automatic Placement of Delays

  * **Identifier**: `pisdf-delays.setter`
  * **Implementing Class**: `org.preesm.algorithm.pisdf.autodelays.AutoDelaysTask`
  * **Short description**: Puts delays in a flat PiMM, in order to speed up the execution.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Puts delays in a flat PiMM, in order to speed up the execution. Works only on homogeneous architectures. The heuristic will perform a search of all simple cycles, so the task may take a long time to run if many cycles are present.

#### Parameters

##### Selection cuts
Number of graph cuts to consider, higher or equal to the maximum number of cuts.

| Value | Effect |
| --- | --- |
| _4_ | Split the graph in zones of equivalent work load. |

##### Maximum cuts
Maximum number of graph cuts induced by the added delays. Each graph cut adds one pipeline stage. If delays are already present, the values are summed.

| Value | Effect |
| --- | --- |
| _1_ |  |

##### Test best choco ?
Computes all topological graph cuts with a CP solver. All topological cuts are evaluated with a list scheduler to select the best.

| Value | Effect |
| --- | --- |
| _false_ | False disables this comparison. |

##### Test scheduling ?
Whether or not a schedule must be generated at the end.

| Value | Effect |
| --- | --- |
| _false_ | False disables this feature. |

##### Fill cycles ?
Whether or not the cycles must be broken with extra delays.

| Value | Effect |
| --- | --- |
| _false_ | False disables this feature. |


### Periods Prescheduling Checker

  * **Identifier**: `org.ietr.preesm.pimm.algorithm.checker.periods.PeriodsPreschedulingChecker`
  * **Implementing Class**: `org.preesm.algorithm.pisdf.periods.PeriodsPreschedulingCheckTask`
  * **Short description**: Check necessary condition to schedule graphs with periods (at top level or in actors).

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Check necessary condition to schedule graphs with periods (at top level or in actors). Works only on flat graphs.

#### Parameters

##### Selection rate (%)
Percentage of periodic actors to consider.

| Value | Effect |
| --- | --- |
| _100_ | All periodic actors are checked. |


### Periodic scheduling (without output)

  * **Identifier**: `pisdf-synthesis.void-periodic-schedule`
  * **Implementing Class**: `org.preesm.algorithm.synthesis.schedule.VoidPeriodicScheduleTask`
  * **Short description**: Schedule and maps actors according to their periods.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
None.

#### Description
Schedule and map actors according to their periods thanks to a list scheduler. Only works for homogeneous architectures, does not take into account communication times. Works also if there are no periods in the graph.

#### Parameters

##### solver
Algorithm used to schedule and map.

| Value | Effect |
| --- | --- |
| _list_ | List scheduler. |
| _choco_ | Optimal CP scheduler. |


### Throughput Evaluation

  * **Identifier**: `org.ietr.preesm.throughput.ThroughputPlugin`
  * **Implementing Class**: `org.preesm.algorithm.throughput.sdf.ThroughputEvaluationTask`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)
  * **scenario** (of _Scenario_)

#### Outputs
  * **throughput** (of _Double_)
  * **SDF** (of _SDFGraph_)
  * **scenario** (of _Scenario_)

#### Description
Undocumented

#### Parameters

##### method
Undocumented

| Value | Effect |
| --- | --- |
| _SR_ | Schedule-Replace technique |
| _ESR_ | Evaluate-Schedule-Replace method |
| _HPeriodic_ | Hierarchical Periodic Schedule method |
| _Classical_ | Based on Flattening the hierarchy |


### SDF2HSDF

  * **Identifier**: `org.ietr.preesm.plugin.transforms.sdf2hsdf`
  * **Implementing Class**: `org.preesm.algorithm.transforms.HSDFTransformation`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)

#### Outputs
  * **SDF** (of _SDFGraph_)

#### Description
transform a SDF graph into a HSDF graph, that is into a single rate graph

#### Parameters
None.


### Algorithm Iterator

  * **Identifier**: `org.ietr.preesm.algorithm.transforms.IterateAlgorithm`
  * **Implementing Class**: `org.preesm.algorithm.transforms.IterateAlgorithm`
  * **Short description**: Undocumented

#### Inputs
  * **SDF** (of _SDFGraph_)

#### Outputs
  * **SDF** (of _SDFGraph_)

#### Description
Undocumented

#### Parameters

##### nbIt
Undocumented

| Value | Effect |
| --- | --- |
| _1_ |  |

##### setStates
Undocumented

| Value | Effect |
| --- | --- |
| _true_ |  |


### FPGA estimation (thoughput + FIFO sizes)

  * **Identifier**: `pisdf-synthesis.fpga-estimations`
  * **Implementing Class**: `org.preesm.codegen.fpga.FpgaAnalysisMainTask`
  * **Short description**: Schedule actors and estimates the FIFO sizes.

#### Inputs
  * **PiMM** (of _PiGraph_)
  * **architecture** (of _Design_)
  * **scenario** (of _Scenario_)

#### Outputs
None.

#### Description
Schedule actors according to their ET and II thanks to an ASAP scheduler. Only works for single FPGA architectures with single frequency domain.Periods in the graph are not taken into account.

#### Parameters

##### Show schedule ?
Whether or not the schedule must be shown at the end.

| Value | Effect |
| --- | --- |
| _false_ | False disables this feature. |

##### Fifo evaluator: 
The name of fifo evaluator to be used.

| Value | Effect |
| --- | --- |
| _avgFifoEval_ | Evaluate with average mode. |
| _sdfFifoEval_ | Evaluate with SDF mode. |
| _adfgFifoEvalExact_ | Evaluate with ADFG exact mode using ojAlgo. |
| _adfgFifoEvalLinear_ | Evaluate with ADFG linear approximation mode using ojAlgo. |

##### Pack tokens ?
Whether or not the tokens should be packed to otpimize bram usage.

| Value | Effect |
| --- | --- |
| _false_ | False disables this feature. |


### Papify Engine - _Deprecated_

  * **Identifier**: `org.ietr.preesm.codegen.xtend.task.CodegenPapifyEngineTask`
  * **Implementing Class**: `org.preesm.codegen.xtend.task.CodegenPapifyEngineTask`
  * **Short description**: Deprecated - does nothing (as of v3.9.1). See parameter 'Papify' in CodegenTask'.

Old doc: Generate the required instrumentation code for the application based on the PAPIFY tab information.

#### Inputs
  * **scenario** (of _Scenario_)
  * **DAG** (of _DirectedAcyclicGraph_)

#### Outputs
  * **DAG** (of _DirectedAcyclicGraph_)

#### Description
Deprecated - does nothing (as of v3.9.1). See parameter 'Papify' in CodegenTask'.

Old doc: This workflow task is responsible for generating the instrumentation of the code for the application based on the PAPIFY tab information.

The generated code makes use of 1 macro that enables/disables the monitoring in the **preesm.h** user header file:
*  **_PREESM_PAPIFY_MONITOR** : if defined, the code monitoring will take place;


#### Parameters
None.


### PiSDF BRV Exporter

  * **Identifier**: `pisdf-brv-export`
  * **Implementing Class**: `org.preesm.model.pisdf.brv.BRVExporter`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters

##### path
Undocumented

| Value | Effect |
| --- | --- |
| _/stats/xml/_ | default value |


### PiSDF Checker

  * **Identifier**: `pisdf-checker`
  * **Implementing Class**: `org.preesm.model.pisdf.check.PiGraphConsistenceCheckerTask`
  * **Short description**: Undocumented

#### Inputs
  * **PiMM** (of _PiGraph_)

#### Outputs
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters
None.


### scenario

  * **Identifier**: `org.ietr.preesm.scenario.task`
  * **Implementing Class**: `org.preesm.model.scenario.workflow.AlgorithmAndArchitectureScenarioNode`
  * **Short description**: Undocumented

#### Inputs
None.

#### Outputs
  * **scenario** (of _Scenario_)
  * **architecture** (of _Design_)
  * **PiMM** (of _PiGraph_)

#### Description
Undocumented

#### Parameters
None.


### Slam Flattener

  * **Identifier**: `org.ietr.preesm.archi.slam.flattening`
  * **Implementing Class**: `org.preesm.model.slam.utils.SlamHierarchyFlattening`
  * **Short description**: Undocumented

#### Inputs
  * **architecture** (of _Design_)

#### Outputs
  * **architecture** (of _Design_)

#### Description
Undocumented

#### Parameters

##### depth
Undocumented

| Value | Effect |
| --- | --- |
| _n > 0_ | default = 1 |


PREESM Changelog
================

## Release version X.Y.Z
*XXXX.XX.XX*

### New Feature
* new task "pisdf-synthesis.fpga-estimations" to analyse applications on FPGA architectures.
* (UI) Two menu commands to overcome the default parsing of C/C++ arguments as data input by default (due to Eclipse CDT):
  'Exchange Port Direction In/Out' and 'Exchange Port Category Data/Config.'.
* Common datatype sizes are now directly inferred if not provided in the scenario when executing a workflow.

### Changes
* (PiSDF) Flattening and SRDAG transformations now support data interfaces at top-level.
* (SLAM) FPGA and CPU processing elements have they own type, which replace Operators.
* (scenario) multiple kinds of timings can be stored, especially for FPGA.
* (UI) The parsing of C/C++ functions for actor refinements is safer, now done by Eclipse CDT instead of handmade regex.
* (UI) New menu popup action to generate C header refinements of PiSDF graphs.
* (UI) Menu popup actions from right click in the diagram are grouped in a "Preesm" submenu (as in the Project Explorer view).
* (UI) New menu actions for ports in PiSDF graphs to exchange ports category and direction (accessible from a right-click on PiSDF diagrams).
* (algo) Design Space Exploration of Malleable Parameters now computes memory footprint and Pareto front (integration of tbourgoi's work).
* (scenario) The data type now needs to be given in bits. A check was added when opening/executing project from a previous version of PREEEM
* (scenario) Common datatypes sizes are now known and automatically used by PREESM, including templated VitisHLS ap_int and ap_fixed types.
* (workflow) The data alignment now needs to be given in bits (default is now Fixed:=8) for MemoryAllocatorTask and MemoryScriptTask.
* (workflow) Added an explicit False Sharing Prevention flag (false by default) to MemoryScriptTask.
* (Build) Make Tycho Pomless build for many plugins, using Eclipse Target Platform to specify maven dependencies. This makes tests plugins to be renamed for tycho. 
* (RCPTT) Update to a fresher version for JAVA 17 execution
* (CI) Makes Github Actions run tests

### Bug fix
* Add explicit log when data type size is not set in scenario making memory scripts crash.
* Fix #303 : (Linux only) error with SWT_AWT is now correctly handled.
* Fix integration test : reinforcement learning scenario for training uses
the training graph now.
* (GUI) UI of PiSDF : better support of interfaces and model updates from GUI.
* Fix #275 : better error support when reconnecting hierarchical graph with wrong ports
* Fix #19 Fix #39 : new menu popup action to check liveness of PiSDF graphs.
* Fix #322 : as C/C++ parsing is now done by Eclipse CDT, we accept headers with .h .H .hxx .hpp .hh .h++ file extensions, but they are not yet supported by regular codegen.
* Fix #165 : UI warns if fifo types do not correspond to actor refinement using them.
* (GUI) UI of PiSDF : removing a Delay (in the GUI only) was actually not removing its DelayActor from the PiGraph, now fixed.
* Fix #339 : partial fix only, adds restriction to the removal of consecutive special actors for optimization (source and target rates must be equal now)
* Fix #342 : partial fix only, autolayout supports cycles involving DelayActor, but this is not checked by the model (would need an exhaustive cycle search)
* Fix #345 : fix Velocity template internal paths in Spider2 codegen (only tested on Linux)
* Fix #287 : fix codegen printing memcpy when it should not.
* (PiMM) : fix issue when removing unused Fifo (rates at 0) in hierarchical graphs
* Fix #351, #91 and #122 : Fix .pi and .diagram being out of sync.
* (GUI) UI of PiSDF : fix pasting operation failing when a delay is in the clipboard.
* Fix some Checkstyle errors

## Release version 3.21.0
*2020.10.01*

### New Feature

### Changes
* Improves the malleable parameter Design Space Exploration task "pisdf-mparams.setter".
  Now, it proposes automatic pipelining and objectives for parameters.
* In scenario, energy and timing of actors can be parameterized with any parameter of the graph containing them.

### Bug fix
* Fix wrong computation in workflow task: org.ietr.preesm.pimm.algorithm.checker.periods.PeriodsPreschedulingChecker
* Fix #324 : parsing error on Windows when parsing a header from the GUI (org.preesm.commons.files.URLResolver).
* Fix wrong computation of BRV when both production and consumption rates of a fifo are equal to zero.
  Such fifos are now ignored when computing BRV (org.preesm.model.pisdf.brv.LCMBasedBRV).

## Release version 3.20.0
*2020.05.27*

### New Feature
* Clustering: 
 * Uniform Repetition Count chain of actor can be found with the new URCSeeker class.
 * Repetition count on a given PiSDF hierarchy can be balanced between coarse-grained and fine-grained levels with the new PiGraphFiringBalancer class.
 * The Cluster Partitioner task clusters groups of actors which are URC and balances there firings with the PiGraphFiringBalancer class.
 * When cluster schedules contain parallelism handled with OpenMP, pthread threads are not printed in the generated code.
* Model:
 * new class of parameter : Malleable Parameter, accepting a set of possible values.
 * energy of actors can be expressed with parameters in the scenario.
* Workflow:
 * new task "org.preesm.codegen.xtend.Spider2CodegenTask" to generate code for Spider 2 runtime.
 * new task "pisdf-mapper.periodic.DAG" to compute periodic scheduling/mapping for legacy code generation.
 * new task "pisdf-synthesis.void-periodic-schedule" to compute scheduling/mapping only (for new synthesis).
 * new task "pisdf-mparams.setter" to compute best value of malleable parameters (exhaustive and heuristic DSE).
 * new task "pisdf-delays.setter" to automatically set delays in flat PiSDF graphs (for pipelines and cycles).

### Changes
* ConfigInputInterface accepts a default value (to compute BRV in subgraph and to avoid false checks in GUI).
* BRV can be computed on subgraph now.
* Possibility to set the init function of a delay in the GUI, not yet used in the codegen.
* Adding new API methods to Parameter class to ease the manipulation of the Parameter tree.
* Adding new API methods for direct access of FunctionPrototype parameters.
* Cluster attribute of PiGraph is now printed in .pi.
* Clustering task is now called Cluster Scheduler and can schedule the input graph or only hierarchies marked as cluster.
* Cluster codegen :
 * MD5 checking can be printed for sink actors contained in a cluster.
 * Parallel Hierarchy with one children is not considered anymore as a section block.
* Full support of final spider 2 api.
* Fix & Update Checkstyle to 8.29.
* Add Spider2 codegen to unit tests.
* Update Cluster Scheduler integration tests.
* Add NewSynthesis Tests
* Adding direct translation of 1 edge broadcast to REPEAT vertex in Spider2Codegen.
* Codegen directory is now cleaned at the beginning of the workflow.
* Secure SWT_AWT code calls in all GUI parts (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=558874)
* Remove old PiSDF clustering classes (replaced with the Cluster Scheduler) and associated tests.
* Cluster Scheduler allows the user to choose whether cluster schedules contain parallelism information or not.
* Update Spider2 codegent to follow evolution of the API.
* Adapt MPPA 2 printer to print mppa_async communication with cluster block and finite loop block.
* Adding direct translation of non perfect broadcast to REPEAT + FORK pattern in Spider2Codegen task.

### Bug fix
* Fix ids and icons of a few GUI elements.
* Fix expression evaluation exception management.
* Fix false parsing of deleted component in scenario.
* Fix SRDAG exploration of cluster special actor.
* Fix the way how delay are printed in cluster codegen.
* Fix #295 : Check for edge in aggregate buffer was only checking the first buffer (now check in all buffer).
* Fix the dag span value in the stat exporter.
* Fix #285 : copyPiGraphWithHistory now handles copy of subgraphs
* Fix #302 : Verify any opened diagram editor instead of active one. 
* Fix Vulnerabilities and Bugs signaled by Sonar
* Fix parameter name replacement in rate expressions in Spider2CodegenEdge.
* Fix #294 : improve the popup menu action to generate flat optimized PiGraph.
* Fix codegen : fix actor name in generated analysis.csv
* Fix #285 : PiGraph copy with history now works on subgraphs.
* Fix #274 : fix clear refinement feature in the GUI of actors.
* Fix #289 : remove memory annotations in the GUI of data interfaces.
* Fix spider2 codegen with delays having setter / getter actors.
* Fix #313 : fix windows uint64 type definition in lib used by codegen.
* Fix delays in cluster codegen.

## Release version 3.19.0
*2020.01.10*

### New Feature
* Clustering: hierarchy construction from a list of actors in PiSDFSubgraphBuilder;
* List periodic scheduling is now implemented in the new Synthesis task;
* Optimal periodic scheduling is now implemented in the new Synthesis task (using Choco);
* New Workflow task 'gantt-output' to export Gantt with new Synthesis interface;
* New menu command (from Preesm project) to generate standard X86 architecture;
* New Workflow task 'pisdf-export.parameters' to export graph static parameters as C header.

### Changes
* If Gantt displayer cannot load SWT_AWT, the Gantt can be opened in a new window instead;
* BRV computation with LCM method now handles fifo having rates equal to 0 on both sides
  (computation is similar to Spider V2);
* Refactor Communication insertion in the new Synthesis task: removes non necessary transitive
  closure and improves performances.

### Bug fix
* Fix #271 : removes extra coma in spider codegen when no parameters.
* Fix #262 : (codegen) init functions are now called only once
* Fix #264 : Excel timings can handle expression, not only numbers
* Fix #267 : remove useless and bugged 'set fifo type' GUI feature
* Fix #268 : uniformize GUI features command order and names 
* Fix #269 : improve the lookup method by vertex path
* Fix #273 : BRV computation was not adapted to port rates being 0 (see Changes section)
* Fix evaluation of parameters as numbers.

## Release version 3.18.2
*2019.11.28*

### New Feature

### Changes
* Graph period has been added to the PiGraph model and appears in the UI;
* Refactor schedule order manager to use a graph internal representation for predecence;
* Add schedule timer to compute timings (start/duration) for an actor;
* PiSDF: use internal graph representation for topological operations;
* Now firing instance number of any Actor can be stored in PiGraph model (set in SRDAG, not stored in .pi);
* Enable MEG Update un new Synthesis API
  * Data alignment not fully foncitonnal yet;
* Change Vaader notification channel

### Bug fix
* Fix workflow task "org.ietr.preesm.pimm.algorithm.checker.periods.PeriodsPreschedulingChecker";
* Fix workflow task "pisdf-export" (adds graph copy to avoid side-effects);
* Fix codegen: special actor ports order is now preserved during codegen;
* Fix sonarcloud links
* Deactivate signal handling in Windows. (Issue #263)

## Release version 3.18.1
*2019.10.21*

### New Feature
* Codegen now updates MD5 using sink buffers (enabled and printed when loop size and verbose are defined);

### Changes
* Fix compile error in generated TCP code (but execution hangs...);
* Refactor;
* Clustering:
	* Workflow task PiSDF Clustering is now documented
	* Implemented a new schedule transform that optimize parallelism inside of sequantial schedule hierarchy
	* New parameter: Optimization criteria (memory or performance)
	* Codegen can now generate appropriate behavior for clustered-delay

### Bug fix


## Release version 3.18.0
*2019.10.09*

### New Feature
* Spider: 
  * Energy awareness (energy-awareness parameter) now can be activated in SPiDER
    * It will test a different number of PEs in each iteration
    * It will keep the PE config reaching the objective with the lowest (estimated) energy consumption

### Changes
* Hardware Codegen: improved monitoring using PAPIFY
* EnergyAwareness: performance objective now is considered a minimum. The tolerance has been removed;
* Fix PiSDF editor autoLayout, especially if having setters and getters on delays;
* Change the line style of setters and getters to dash dot;
* AutoLayout of Feedback Fifos is smarter (works also when no delays);
* Slam: 
  * move route model to xcore;
  * Refactor routing table;
* Synhtesis API: 
  * remodel schedule/memory allocation;
  * implement delay support;
  * implement communication insertion;
* Refactor:
  * Minor codegen refactor;
  * Refactor memory allocation;
  * Rethrow all exceptions thrown by JEP with expression causing issue;
* Add methods in PiSDFTopologyHelper, and corresponding tests;
* Simulation tab of scenario now has an option to import all data types from
  the graph, with predefined default values for common type names.

### Bug fix
* Fix #74
* Fix #80
* Fix #83
* Fix #85
* Fix #128
* Fix #190
* Fix problem with precedence shift condition (clustering purposes)
* Fix random seed parameter transfer to clustering algorithm
* Fix various bugs in the experimental external mapping importer.
* Fix #234
* Fix #166: force init/end on same operator in kwok scheduler;

## Release version 3.17.0
*2019.09.09*

### New Feature
* Codegen:
  * PREESM now support the codegeneration to manage hardware accelerators developed within [ARTICo³](https://des-cei.github.io/tools/artico3), an open-source runtime reconfigurable processing architecture;

### Changes
* Codegen: useless function removed in HW codegen;
* Refactor;

### Bug fix


## Release version 3.16.0
*2019.09.06*

### New Feature
* Codegen:
  * clusters can now be printed by using CodegenClusterTask
* Apollo optimizations are now supported in Preesm and SPiDER
  * To activate it, Apollo flag must be set to true in the CodeGen workflow task
  * CMakeLists.txt of the application must be modified 
* Scheduling:
  * Mapping/scheduling task (pisdf-mapper.list) can now perform energy-aware mapping/scheduling (It will increase scheduling/mapping task time, as it tests several mappings)
  * The information is taken from energy tab in scenario and the optimization will consider the performance objective to search for the best PEs configuration
  * EnergyAwareness parameter will enable this optimization
  * EnergyAwarenessFirstConfig selects the starting point: First (as less PEs as possible), Max (all PEs), Middle (half amount of PEs of each type) and Random (random starting point)
  * EnergyAwarenessSearchType selects the type of searching done: Thorough (changes PEs number one by one) and halves (Divides in halves the remaining available PEs and goes up/down depending if the FPS reached are below/above the objective)

### Changes
* PiSDF: 
  * PiGraph can be declared as a cluster. Hierarchy of cluster may be ignore by SR and DAG transformation;
  * Add operation to get the unique data port of a SrdagActor (init/end);
* Change PiSDF task categories in task reference;
* Clustering: 
  * cluster are now mappable into component by retrieving common possible mapping of included actors
  * make code clearer for ClusteringBuilder
  * refactored the way of getting clusterizable couples
  * clusters now have timings 
* Schedule: 
  * Readapt the way of exhibiting data parallelism and print expression
  * Add communication nodes
* Refactor:
  * Fix major and critical sonar issues;

### Bug fix
* Fix #218: add support for non connected actors in SRDAG transform;
* Fix #222: allow multiplicity in Codegen Call parameters;
* Fix #226: avoid name conflict in graphml configuration;
* Fix #227: enable Export SVG Diagram on any selected element;

## Release version 3.15.0
*2019.08.28*

### New Feature
* PAPIFY in SPiDER now has different modes:
  * papify=dump: dump PAPIFY info into csv files;
  * papify=feedback: PAPIFY (timing) feedback is retrieved by GRT and timings are updated;
  * papify=both: both modes active at the same time;
* SPiDER now includes information about energy consumed by an actor executed on a specific PE;
* SPiDER now includes information about energy consumption models associated to actors executed on PEs;
* PAPIFY in SPiDER with feedback mode active:
  * energy feedback is also supported. Energy consumption models based on PAPIFY events are considered when updating the energy consumption;
  * energy consumption of one actor being executed on a specific PE type is updated if possible;
* Timings and Energy tabs can import .papify files generated by PAPIFY scripts:
  * Timings: the timing of the actor being executed on each Component is updated;
  * Energy: the energy consumed when executing an actor on each Component is updated;
* PAPIFY tab in scenario now has a new section to include an energy model based on PAPIFY events:
  * The model can be exported as a csv file to apply PAPIFY script and compute the energy;
* PAPIFY output folder 'papify-output' now can be directly parsed from scenario:
  * Timings: the timing of the actor being executed on each Component is updated;
  * Energy: the energy consumed when executing an actor on each Component is updated. To do so, the energy model (included in PAPIFY tab) is considered when parsing the folder;
* PiSDF:
  * Add transform and task to replace "perfect fit delays" with init/end actors;
* Workflow:
  * Throws warning when executing deprecated tasks;
* Clustering:
  * Add 4 clustering algorithm: APGAN, Dummy, Random and Parallel
  * Cluster by generating sub-graph and associated schedule tree
  * [Hardware Codegen] Use of PAPIFY was extended to the Hardware on the FPGA side.

### Changes
* Refactor: rename/move packages in algo & codegen;
* Implement simple memory allocation in the new synthesis API;
* Prepare codegen model generator for the new synthesis API;
* Hardware Codegen Refactor: CHardwarePrinter extends now the CPrinter and override some of the methods;
* Hardware Codegen Refactor: CHardwarePrinter extends now the CPrinter and override some of the methods;
* SPiDER codegen considers empty PAPIFY configs as papify=false;
* Update round buffer memory script to handle multiple inputs;
* Schedule: Add an interface to perform transform;
* Scenario:
  * Update scenario generator to use unix separator in algo/archi URLs;
* PiSDF: 
  * Add more rules to check mergeability, remove getPreds/Succs from TopologyHelper;
  * Add rules in the consistency checker;
* Codegen:
  * Add IteratedBuffer, ClusterBlock, SectionBlock for clustering codegen
  * Add conditional "#pragma omp parallel for" on FiniteLoopBlock
  * Add null checks for codeGenDir (since not set when the parser sees a null length);

### Bug fix
* fix #186
* Fix #189
* Fix #193
* Fix scheduling of graphs with large data rates.
* Fix optimizations of PiSDF flattening task.

## Release version 3.14.1
*2019.07.25*

### New Feature

### Changes
* Schedule API:
  * Rename/move packages;
  * Add evaluation API;
* Releng:
  * Fix build script to fetch RCPTT from mirrors;

### Bug fix


## Release version 3.14.0
*2019.07.25*

### New Feature

### Changes
* Codegen:
  * Update Codegen to properly clean the directory before generating code;
  * Add C/C++ and XML code formatters;
* Update Preesm adapters and copy;
* Releng:
  * Update build script;
  * Fix Jacoco/Sonar config;

### Bug fix


## Release version 3.13.1
*2019.07.23*

### New Feature

### Changes
* Update CPrinter to support mixed C/C++ projects;

### Bug fix
* PAPIFY:
  * Fix PAPIFY code generation


## Release version 3.13.0
*2019.07.18*

### New Feature

### Changes
* Codegen:
  * Added an AbstractCodegenModelGenerator supertype to ease experimentation of another model generator from CodegenEngine;
  * Instrumented codegen now computes timings per PiSDF vertex path name instead of C function name;
* Schedule:
  * Added a reference to a AbstractActor in HierarchicalSchedule to retrieve cluster from their schedule;
  * Refactor legacy scheduler to make it deterministic;
* Releng:
  * Update jacoco/sonar config;
  * Update travis/sonar config;
* PiSDF:
  * Added method to ease regroupment of actors (for clustering process);
  * Refactor Topology helper methods and classes;

### Bug fix

## Release version 3.12.0
*2019.07.11*

### New Feature
* Added a new tab in the scenario to include power and energy information for the architecture and for the algorithm being executed on the different resources. The possibility to include a performance objective is also added;
  * These values are, as of now, not used in the later tasks;
* Add support for exporting hierarchical PiSDF graphs;

### Changes
* Fix gantt viewer PE order;
* Add subgraph disconnector, called before exporting graphs;
* Fix Slam parser;
* MPPA2Explicit now is called MPPA2Cluster;
* MPPA2IOExplicit now is called MPPA2IO;
* Update PiSDF consistency checker;

### Bug fix
* Fix #166: forces scheduler to put init and end actors on same core;
* Fix #170: do not count/launch empty clusters;
* Fix papify for clustered actors;


## Release version 3.11.0
*2019.07.05*

### New Feature

### Changes
* Make sure PiSDF flatten and SRDAG transformations keep track of the input PiGraph;
* Fix Papify component add/remove;
* Fix Papify heterogeneity (different monitoring configs depending on the PE type executing the actor) for PREESM and SPiDER;
* Update comments in PAPIFY UI;
* Update relend;
* Fix PiWriter: avoid throwing NPE when period is not attached to an element;
* Change mirror (ibcp down);
* Add new models for Schedule/Mapping/Memory Allocation;
* Add hardware ID in Slam to identify to which real core the thread affinity should be set;
* Merge the 4 Slam models into 1 file;
* Update codegen:
  * forces affinity of main thread to assigned core;
  * disable affinity on windows;

### Bug fix
* Fix #162

## Release version 3.10.0
*2019.06.20*

### New Feature

### Changes
* Fix Papi event add/remove;
* Deprecated PapifyEngine task, replaced with the new parameter in the codegen task;
* Update sonar plugin to 3.6.0.1398;
* Fix Scenario UI layouts;
* Refactoring;

### Bug fix
* Fix #154: fix scenario UI layouts;

## Release version 3.9.0
*2019.06.18*

### New Feature

### Changes
* Fix PiGraph.isLocallyStatic: now check for all contained parameters to be static;
* When the pi file does not specify a refinement for an actor, the parser was setting a pisdf refinement with null path. The new behavior is to set a CHeader refinement with null path. This allows the graph to be considered flat;
* Delete deprecated RandomSDF task and associated test;
* Cleanup and Fix warnings;
* Add class for helping loading resources from bundle/code base and replace uses;
* Paths in models (PiSDF, Slam) are now represented as Strings instead of IPath;
* Fix parameter validity analysis: new use JEP Wrapper;
* Change implementation of Constraint Groups in scenario to have only one operator per group;
* Make sure the PiGraph and SlamDesign are parsed once only per scenario node in the workflow;
* Fix UI throwing exception when canceling file browse;
* Scenario managers now use the Actor/Component reference instead of component ID or actor path/name;
* Refactor scenario and use XCore for Scenario;
* Add support for parameterized timings and for overriding parameter values in scenario (note: display is wrong when using ConfigInputInterface values);
* Fix Scenario UI: 
  * Timing tab is properly refreshed when param override values are changed;
  * Timing and Paramever override expressions are now properly using config input interface values from parent graphs;
* Remove number of execution of top graph from scenario (and UI);
* Update Xtend/Xcore maven plugins to 2.18.0+;
* Fix PiSDF UI: do show persistence level on Delay only;
* Update SDF/DAGVertex.getReferencePiVertex(): cast is now done inside;
* Fix Scenario UI and cleanup code, replace dialogs with inplace table cell modifiers;

### Bug fix
* Fix #139: better error messages for Name checker
* Fix #121
* Fix #77
* Fix #87
* Fix #155: always show the init function selection dialog and add a note explaining which functions are filtered out;
* Fix #158
* PAPIFY: fix removing PAPIFY timing event not working;


## Release version 3.8.1
*2019.05.22*

### New Feature

### Changes
* Make sure the order of tasks in the reference is always the same;
* Prefix stopThreads with preesm to avoid conflicts with application variables, and document variable;

### Bug fix
* Fix #135: properly use preesmStopThreads to control the loop;

## Release version 3.8.0
*2019.05.21*

### New Feature
*  Clustering and DistributedOnly memory distribution now are compatible;

### Changes
*  Fix C Hardware codegen: properly prefix LOOP_SIZE with PREESM_;
* SpiderCodegen: updated spider codegeneration to support the new Archi model of Spider.
*  Fix C Hardware codegen: properly printed the Release Kernel Instance with the name of the function to be executed in Hardware
*  Update workflow editor to better report task id issues;
*  Changed _PREESM_MONITOR_INIT by _PREESM_PAPIFY_MONITOR;

### Bug fix
*  Fix several bugs on the communications when using the IO in the MPPA code generation;

## Release version 3.7.0
*2019.05.14*

### New Feature
*  Codegen: enabled the code generation for offloading computation on the FPGA Prgrammable logic (hardware accelerators)
*  Codegen: core type "Hardware" is allowed
*  Codegen: multiple parallel Hardware Slots generated succesfully
*  Codegen: core type "MPPA2IOExplicit" is allowed
*  Test Integration: added test for Hardware Codegen
*  Codegen: enabled IO usage on the MPPA code generation
*  Codegen: enabled the use of DistributedOnly through the use of TwinBuffers for the repeated memory transmissions
*  Codegen: DistributedOnly memory distribution --> Support from CodeGen for each architecture needs to be done (DistributedMemoryCommunication printer has been included) Issue #134
*  Codegen: Inter cluster communication supported on the MPPA when DistributedOnly memory distribution is used
*  PAPIFY: PAPIFY now supports the monitoring of clustered actors

### Changes
* SpiderCodegen: updated spider codegeneration to follow up changes of spider develop branch.
* Codegen: Now host/IO/Cluster codes are automatically generated when using MPPA2 code generation.
* Enable RCPTT tests back (https://bugs.eclipse.org/bugs/show_bug.cgi?id=543731)
* PAPIFY now is based on its own classes to be printed --> Better support for each type of architecure
* PAPIFY monitoring can now be enabled/disabled defining/deleting _PREESM_MONITOR_INIT

### Bug fix
*  Fix #94
*  Fix #93
*  Fix #134
*  Fix #136
*  Fix PAPIFY bug where only one monitoring configuration was generated

## Release version 3.6.2
*2019.04.15*

### New Feature

### Changes
*  Codegen: LOOP_SIZE and VERBOSE macros have been prefixed with PREESM_ to avoid conflicts with application macros;

### Bug fix


## Release version 3.6.1
*2019.04.15*

### New Feature

### Changes

### Bug fix
*  Fix codegen: semaphore primitives are note static anymore to be visible during TCP codegen;
*  Fix #126

## Release version 3.6.0
*2019.04.10*

### New Feature
* XML exporter of repetition vector (workflow task id: pisdf-brv-export)
* Use annotations for declaring the tasks;
  * Incidentally, the task reference is now generated and available online;
* Add new CLI application to generate documentation: '-application org.preesm.cli.docgen -mdd <markdown file path>'

### Changes
* Make sure XCore generated code is 1.8 compliant;
* Remove org.preesm.workflows.{scenarios|tasks} extensions, replaced with annotation and global plugin declaration;

### Bug fix
* Fix #106
* Fix #109

## Release version 3.5.0
*2019.03.21*

### New Feature
* Add a sanity check on communication order in intermediate PE for multi-step communication.

### Changes
* Update workflow related messages;
* Delete unused resources;
* Disable sonar cloud on pull requests and other repos;
* Implements graph optimisations for graph flattening and provide rates warnings;
* Improve the periodic schedulability test by automatically computing the fifo breaking cycles;
* Improve the implementation of the tasks sorter for the Gantt display;
* Update Maven config:
  * Update Jacoco to 0.8.3
  * Update Checkstyle to 8.18
  * Update Xtext to 2.17.0

### Bug fix
* Fix #108: Update log messages to display the faulty task name and id, when relevant;
* Fix #113: Use different counter for setting thread count in code generation;
* Fix #118

## Release version 3.4.0
*2019.01.28*

### New Feature
* Add tasks to schedule PiSDF objects;
  * Mark old tasks as deprecated;

### Changes
* Properly throw exception with message;
* Minor refactor;
* Disable graphml editor association for .pi files;
* InstrumentedC now dumps timings in nanosecond and is cross platform

### Bug fix


## Release version 3.3.1
*2019.01.28*

### New Feature

### Changes

### Bug fix
* Fix #105: make sure visibility of tasks is public;


## Release version 3.3.0
*2019.01.25*

### New Feature
* Add workflow parameters 'Verbose Level' to control log level;
* Add workflow parameters 'Error on Warning' to fail and stop execution on warning or severe log entry;

### Changes
* Fix PiSDF single rate export: add suffix to the exported file name to avoid overriding the original graph;

### Bug fix


## Release version 3.2.0
*2019.01.24*

### New Feature
* Add context menu in PiSDF editor to compute the BRV;
* Add context menu in PiSDF editor to compute the SRDAG;

### Changes
* Update release system to support Eclipse 4.10;
  * Keep RCPTT disabled until new release supports 4.10+;
* Fix PiSDF UI:
  * Disable exception throwing at write/parse time when the PiSDF is not consistent in order to enable non-finished design to be saved;
  * Fix dependencies to enable copy/rename refactoring;
  * Fix filters/Add error handling to avoid exceptions when editing algos;
* Fix Scenario parsing: do not fail when graph/archi is not valid;
* Fix PiSDF single rate transformation
  * properly remove dependencies during optimizations;
  * rename parameters with graph prefix;
  * instantiate parameters only when used;

### Bug fix
* Fix #99 : Add code generation path check;
* Fix #103: Remove inline directive to enforce comptatiblity with most C compilers;

## Release version 3.1.3
*2018.12.19*

### New Feature

### Changes
* Enable xtend maven plugin on XCore projects in order to automatically apply XText nature on the eclipse project;
* Force Graphiti to 1.13.0+;
* Use custom XML writer for slam, pisdf and scenarios;
* Add UI tests for workflow and slam editors;

### Bug fix
* Fix issue 92: disable generation of std files for C6678 code generation;

## Release version 3.1.2
*2018.12.18*

### New Feature

### Changes

### Bug fix
*  Fix issue 89 reoppening: proposed solution on initial comment was not safe;

## Release version 3.1.1
*2018.12.17*

### New Feature

### Changes
* Refactor code base;
* Refactor PiSDF:
  * replace init/end String references with Object references;
  * Make Init/End actors special actors;
  * Update graph abstraction and implement high level operations;
  * Add structure checkers;  
* Update TCP Codegen: add threads dedicated to receive operations in order to free the TCP buffers ASAP and avoid deadlocks;

### Bug fix
* Fix issue 89: Fix stdint includes for MSVC;
* Fix issue 90: include missing xslt file and icons in the produced binaries;

## Release version 3.1.0
*2018.12.11*

### New Feature
* Add exporting task for flatten and single rate PiSDF;
  * also update generate diagram feature to support init / end actors;
  * NOT working for flatten graphs yet;

### Changes
* Adding a new function to jep, in order to compute i for max divisibility by 2**i;
* Add Snapshot releases repository;
* Refactor (method/class/package move/rename/delete);
  * have the flatten and single-rate transformations independant from scenarios;
* Remove IBSDF support in scenario;
* Fix PiSDF flattener and srdag transformations to output a consistent model;
* Centralize Xtend and XCore config on parent pom file;
* Remove uses of deprecated EdgeFactories of JGrapht. Use Suppliers instead;
* Fix Papify UI;
* Fix spider codegen to handle latest papify config;
* Updated spider codegen to handle parameterization directly from the workflow task.

### Bug fix
* Fix unicity of ports name when doing fork / join / broadcast / roundbuffer graph optimizations. 


## Release version 3.0.1
*2018.11.23*

### New Feature

### Changes
* Enable maven batch mode to prevent log to exceed size limit on travis;

### Bug fix


## Release version 3.0.0
*2018.11.23*

### New Feature

### Changes
* Update release process;

### Bug fix


## Release version 2.99.0
*2018.11.21*

### New Feature
* Add a simple scheduler that maps everything to the main PE;

### Changes
* Merge DFTools code in Preesm and remove upstream dependency;
* Rename plugins into org.preesm instead of org.ietr.{preesm|dftools};
* Rename packages into org.preesm instead of org.ietr.{preesm|dftools};
* Rename/merge plugins and fix dependencies (including tests);
* Move classes to proper plugins;
* Fix product;
* Fix Loggers;
* Delete unused PreesmApplication;
* Improve periodic actor support in UI; 
* Add periodic actor check (not working with cycles yet);
* Improve Papify support: 
  * implement 2D array for selecting event to monitor per actor;
  * implement codegen for static schedules (no support in spider yet);
* Remove preesm dev feature (moved to another repo);

### Bug fix


## Release version 2.17.1
*2018.11.09*

### New Feature

### Changes
* Use RCPTT runner prefetch script when deploying;
* Add RCPTT to dev feature dependencies;
* Synhcronize RCP plugin and Eclipse product;
* Add missing plugin to Preesm feature;
* Fix CLI Workflow Executor scenario path formatter;

### Bug fix


## Release version 2.17.0
*2018.11.08*

### New Feature

### Changes
* Fix BRV computation (more resilient to integer overflow);
* Fix travis file;
* Add tests
* Fix product definition (exporting from Eclipse now works properly);
* Refactor 
  * Impact DFTool refactor;
  * Remove dead code;
* Update DFTools to 2.0.0+
* Update Graphiti to 1.11.0+

### Bug fix


## Release version 2.16.0
*2018.10.11*

### New Feature
* Add workflow task to manually input a schedule in the flow;
* A period can be specified for each actor (but not for special actors), it is not used during scheduling for now;
  * Consistency is checked during workflow execution;

### Changes
* Major refactoring
  * Use Long instead of Integer in the Memory allocation;
  * Fix many bugs and code smells;
* Adding graph optimizations for PiMM graphs as seperate process from PiMM2SRDAG.
* Adding tests for graph transformations.
* Adding new task to flatten a given PiMM graph.
* Move travis dist to xenial;
* Fix PiSDF Exporter task to read a PiMM instead of a scenario;
* Fix Expression evaluator to properly handle several config input ports connected to the same parameter;
* Copying the timing property during the PiMM2SRDAG conversion;
* Add XSD for .pi files and corresponding Validator class;
* Move Xtend maven plugins to 2.15;
* Add LongExpressions to speedup evaluation;
* Use parameterized tests;
* Add test case for stressing memory allocation;
* Use LongFraction (tweak of apache.math3.Fraction) instead of Rationals and Fractions;

### Bug fix


## Release version 2.15.1
*2018.09.26*

### New Feature

### Changes
* Fix Spider codegen: properly return 0 when initializing archi;
* Update M2E Code Quality repo;
* Codegen now automatically fills its include list given actors refinements;
* Fix PiMM2SRDAG transformation (was giving wrong results / failing on specific cases);

### Bug fix
* Fix path format in scenario editor (issue #81);

## Release version 2.15.0
*2018.09.19*

### New Feature

### Changes
* Add helper method PiGraph.removeFifo(Fifo);
* Have the main operator thread execute in main thread;
  * for default C code generation;
  * for tcp codegen;
* Codegen now embeds communication, fifo, and dump C and header files
  * this includes TCP communication lib;
  * also includes semaphore and barrier implementation for MacOSX;
* Minor fixes in UI;
* Remove EMF dedicated repo (see https://github.com/eclipse/xtext/issues/1233#issuecomment-421904619);

### Bug fix


## Release version 2.14.2
*2018.09.10*

### New Feature

### Changes
* Add MacOSX comptabile icon;

### Bug fix


## Release version 2.14.1
*2018.09.07*

### New Feature

### Changes
* Update Eclipse-EMF maven repo URL;
* Update release scripts;
* Move XCore maven config to parent pom;
* Refactor scheduler;
* Move Codegen model and convert it from ECore to XCore;
* Spider codegen now support heteregeneous hardware.

### Bug fix
* fix issue #69;
* fix issue #71;
* fixing timing issue in spider codegen. It is temporary as it only copy the behavior of PREESM (see issue 73);
* fix issue #76; Spider Codegen now correctly assign parameters to method prototype.

## Release version 2.14.0
*2018.09.03*

### New Feature
* Add support for Delay initialization:
  * Extand semantic of Delay in the PiSDF Model;
  * Add setter and getter actors for intializing and flushing delays;
  * Add support in standard passes (use PiMM to SRDAG);
  * Add graphical support in PiSDF Editor (partial - autolayout can fail when using delay innitialization);
* Add prototype of SyncOptimizer: remove redundant synchronizations when working with zero-copy memories;
* Add sample JSon I/O for schedule/timing;

### Changes
* Replace deprecated DAGIterator with TopologicalDAGIterator;
* Enable XCore codegen plugin back in Maven (https://github.com/eclipse/xtext/issues/1233);
* Properly display errors in a dialog when generating diagram from .pi file fails;
* Update MapperDAG to keep a reference to the PiSDF; Refactor constructor accordingly;
* Update MathFunctionHelper to have lcm/gcd on longs;
* Refactor schedulers (and remove obsolete one);
* Flatten everything when depth level is set to negative values;
* Fix spider codegen:
  * Set delay persistence to have the same default behavior as Preesm;
  * Fix indentation;
* Minor refactoring;
* Update coding policies to 1.3.0;
  * also fix code;
* Improve scenario editor;
* Update external dependencies to [3.3.0,4.0.0);
* Update external Graphiti to [1.10.0,1.0.0);
* Update external DFTools to [1.9.0,2.0.0);
* All objects in the codegen model are now able to hold comments;
* Fix Hierarchical code generator: properly call IBSDF Flattener with -1 to flatten everything;
* Add ScheduledDAGIterator that iterates over a scheduled DAG vertices in the scheduled order;
* Fix old workflow: if persistent property is not set on delays, use permanent behavior by default;

### Bug fix
* Fix issue #62
* Fix issue #61 - include spider codegen;
* Fix issue #68

## Release version 2.13.0
*2018.07.09*

### New Feature
* New code generation with TCP communications - first prototype implementation;

### Changes
* Fix checkstyle configuration;
* Refactor FilesManager: rewrite in plain java and use proper URL resolver;
* Add PiGraph helper operation;
* Fix codegen core comparator for giving proper core ID;
* Update to Eclipse Photon;
* Force Graphiti to [1.9.0,2.0.0);
* Force DFTools to [1.8.0,2.0.0);
* Update Xtend/Xtext to 2.14.0+;
* Fix papify codegen for spider;
* Cleanup Preesm-Papify;

### Bug fix
* Fix issue #61;


## Release version 2.12.3
*2018.06.06*

### New Feature

### Changes
* Fix Prees about page in product;
* Fix Spider code generation to make it compatible with latest spider version;
* Fix dependency creation via UI: show warning when trying to drop the end of a dependency on an interface;
* Cleanup code;
* Add upper version (exclusive) in dependencies:
  * Update ExternalDeps to [3.2.0,4.0.0);
  * Update Graphiti to [1.8.0,2.0.0);
  * Update DFTools to [1.7.3,2.0.0);

### Bug fix


## Release version 2.12.2
*2018.06.01*

### New Feature

### Changes
* Fix Papify codegen configuration;
* Enable notifications to Slack vaader-ietr#preesm;
* Update DFTools to 1.7.2+;
* Changed Spider codegen to catch runtime_error exception instead of const char*;
* In spider codegen, the try - catch in the main auto generated now englobe the init as well;
* Update manifest files;
* Fix xtend warnings;
* Adding papify code generation for spider

### Bug fix
* Fix issue with copy/paste when reconnecting dependencies;


## Release version 2.12.1
*2018.05.17*

### New Feature

### Changes
* Update DFTools to 1.7.0+;
* Update code to be compatible with new version of DFTools that uses longs instead of integers;

### Bug fix


## Release version 2.12.0
*2018.05.16*

### New Feature
* Add new code generation scheme for instrumenting code with PAPI;
* add new code generator : PapifyC
* add new tab in the scenario editor to configure the Papi events to monitor;

### Changes
* Update coding policies to 1.2.4 (fix line split);
* Update DFTools to 1.6.0+;
* Mintor refactoring of the Codegen;
* Adding new facilities method in PiGraph;
* Enforced use of DAG properties instead of SDF properties when conversion of an SDF graph is done;
* Initialization of data size property in SDFEdge before dag conversion;
* Adding workflow task for converting Single rate SDF graph to DAG;
* Deprecating use of Schedule/Mapping task using SDF as input in favor to the new ones with DAG as input;

### Bug fix


## Release version 2.11.0
*2018.04.23*

### New Feature

### Changes
* Add a generic graph definition in PiSDF (graph, vertices, edges);
* Add helper oeprations in PiSDF;
* Update release process to upload releases on GitHub;
* Update operation with more accurate name and return type;
* Fix typos;
* Update checkstyle to 8.8 (and maven plugin to 3.0.0);
* Update Coding policies (now ignores javadoc @throw issues);
* Add script to prefetch RCPTT Runner from different mirror (faster);
* Add NonExecutableActors;
* Add helper methods in PiGraph;
* Minor refactoring;
* Update RCPTT POM Configuration;
* Have travis retry 3 times to fetch dependencies;
* Force DFTools to 1.4.1+;

### Bug fix
* Fix codegen for MS Visual Studio to support thread affinity;
* Properly rethrow exception when parser is failing instead of returning null;

## Release version 2.10.0
*2018.03.29*

### New Feature

### Changes
* Force DFTools to 1.4.0+;
* Force Graphiti to 1.6.0+;
* Force external deps to 3.0.0+;
* Move deploy server to preesm.insa-rennes.fr;
* Update project info (URL);

### Bug fix


## Release version 2.9.0
*2018.03.06*

### New Feature
* Add geometric sum function in expressions;
* Front-end SDF graph analysis plugin that checks if the given SDF graph can be executed in data-parallel fashion i.e. for each actor of the graph, run all its instances in parallel;

### Changes
* Update mailmap file;
* Disable RCPTT fetch when loading projects in Eclipse;
* Add custom license/copyright headers for fi.abo* plugins;
* Allow diagram generation for multiple pi files at once;
* Updated code generation of instrumentation: now time buffer are uint64_t instead of long;
* Update codegen C printer to set thread to core affinity;
* Update codegen C printer: remove GCC warnings in generated CoreX.c code;

### Bug fix


## Release version 2.8.0
*2018.02.09*

### New Feature
* Add throughput evaluation for PiSDF;
* Add min and max function in JEP
* Add a first prototype of the Activity Exporter;

### Changes
* Refactor PISDF;
* Change default flattening depth to -1 (infinite);
* Add missing icon in scenario UI;

### Bug fix
* Fix issue #53
* Fix a crash that occured when opening a scenario with a parameter value whose parameter do not exists in the graph anymore;

## Release version 2.7.0
*2018.02.05*

### New Feature

### Changes
* Refactor PiSDF Model;
* PiSDF: Replace Ecore/Genmodel with XCore;
* PiSDF: Remove XCore generated code from git;
* Update JGrapht to 1.1.0;
* Force DFTools to 1.3.0+;
* Force external deps to 2.0.0+;

### Bug fix


## Release version 2.6.2
*2018.01.23*

### New Feature

### Changes
* Update new Preesm project wizard: do not generate subfolders;
* Move icons to the proper plugin;
* Add shortcut to open property view from PiSDF editor;
* Fix codegen: thread function declaration and reference in the main now have matching name based on the core ID;
* Fix createActorFromRefinement feature: properly set the editor in unsaved state;

### Bug fix


## Release version 2.6.1
*2018.01.11*

### New Feature

### Changes
* Update releng scripts;
* update checkstyle to 8.5;
* Fix coding policies to match Checkstyle 8.5;
* Fix javadoc to respect Checkstyle 8.5;
* update coding policies: max line length is now 120 chars;
* use Maven plugins and coding policies from Maven Central instead of Preesm own maven repo;
* Cleanup UI plugins dependencies;
* Force DFTools to 1.2.10+;


### Bug fix


## Release version 2.6.0
*2017.12.21*

### New Feature
* Add UI Testing via RCPTT;

### Changes
* update release script;
* Update maven dependency plugin to 3.0.2;
* Rename releng profile to doUpdateSite and impact release scripts;
* disable javadoc generation (source is available via the Dev Features and source plugins);
* disable os specific archive format;
* Add dependency to RCPTT in the dev feature;
* Refactor the PiMM Model;
* Remove Multi* Tasks;
* split ui plugins into dedicated plugins (PiSDF, scenario, SDF);

### Bug fix
* fix issue #51


## Release version 2.5.4
*2017.12.04*

### New Feature

### Changes
* Update releng scripts;
* Refactor UI: use PiMMSwitches instead of instanceof ifs;
* Update product: now starts up the Preesm perspective by default and show Preesm in the title;
* Refactor code generation;
* Make scenario, architecture and algorithm visible from the codegen printers;
* Fix autolayout;

### Bug fix
* Force DFTools to version 1.2.7+ (fix issue #50);

## Release version 2.5.3
*2017.11.14*

### New Feature

### Changes
* Fix issue #48: Imporve Gantt chart viewer;
* Refactor & cleanup;
* Update scenario editor to color text field in red when algo/archi is not found;
* Update Papi code generator: enable and tune generation of the main C file;
* Change button label in actor properties from Edit to Browse;
* Add dependencies to Xcore and Sonarlint (dev feature), and CDT (dev + normal feature);
* Force external deps to 1.3.0+
* Force DFTools to 1.2.6+
* Add release scripts
* Remove unused script auto_convert_encoding_and_lineendings.sh

### Bug fix
* Fix issue #47: Make the PiGraph parser more robust;

## Release version 2.5.2
*2017.10.31*

### New Feature
* Add operation on PiMM Actors to access ports more easily;
* Add dependency to Ecore Diagram Tools from the dev feature;
* Enable Travis CI;

### Changes
* Refactoring, cleanup, code factorization, ...;
* Remove Shell from file selection popup API;
* Replace use of PiMMUtil.askFile with FileUtils.browseFile from DFTools;
* Discourage use of PiMMUtil.askFile;
* Check that a dependency is connected to a target before saving;
* Add project information in the POM file;
* When the setter class is unkown, assume the parameter is not locally static instead of throwing an error;


### Bug fix
* Fix cycle detection: ignore non connected ports as they obviously don't contribute to any cycle;
* Fix DependencyCycleDectector: Dependencies without a setter cannot obviously contribute to a cycle;
* Fix issue #43;
* Fix issue #44;
* fix issue #46;

## Release version 2.5.1
*2017.10.17*

### New Feature
* Add feature dependency to TM Terminal to have easy terminal access;
* Add feature dependency to Graphiti SDK Plus source in the dev feature;

### Changes
* Changes actor ports layout: add more gap for better readability;
* Autolayout feature now relayout actor content (for better rendering of old PiSDF graph);
* Update DFTools to 1.2.3 and Graphiti to 1.4.3;

### Bug fix

## Release version 2.5.0
*2017.09.15*

### Changes
* The code generator now generates a main.c(pp) file that adapts to the number of available cores, instead of having to manually edit the CMake file;
* Minor refactoring;

## Release version 2.4.2
*2017.08.17*

### New Feature
* Add menu entry to call the .diagram generator. This allows to generate the .diagram file from a PiMM Model (.pi) file;

### Changes
* Upgrade to Eclipse Oxygen;
* Use new Graphiti extension point name (new version);
* Refactoring;

### Bug fix
* Fix autolayout issue that prevented the feature to work when selection was not empty;
* Fix positionning of Delays when using copy/paste;
* Fix autolayout of dependencies targetting graph interfaces;

## Release version 2.4.1
*2017.07.19*

### Bug fix
* Fix dependency issue;
* Fix Parameter lookup in Dynamic PiMM 2 to SDF task;

## Release version 2.4.0
*2017.07.18*

### New Feature
* Enable copy/paste of vertices (actor, parameters, sink/source, special actors, ...) in the graphical PiMM editor (see https://github.com/preesm/preesm/issues/10);
   * Connects parameters and parameter configurations if paste occurs in the same diagram;
   * Copies fifos when copying group of actors, and connect delay dependencies if paste occurs in the same diagram;
   * Copies dependencies between copied ISetters and copied Parameterizables if paste occurs in a different diagram;

### Changes
* Update scenario editor: file selection popups now filter content more accurately;
* Add explicit verions for Xtend dependencies to avoid bugs due to API break;
* Cleanup releng files;
* Normalize feature licenses;
* Fix PiMM Ecore model: Port.name now has a lower bound of 0 to match the semantic of Parameter ConfigInputPorts (which have no name);
* Refactoring;
* Update DFTools to version 1.4.1 and Graphiti to 1.2.1;

### Bug fix
* Fix error handling in UI decorators (show decorators instead of crashing);
* Fix expression refresh on properties tab of PiMMEditor: now show red background and error message instead of showing multiple popups when expression cannot be evaluated;
* Fix deletion of actor + connected delay;
* Fix auto layout freezes when fifos input/output ports are not connected;
* Fix port deletion: do not delete connection if port deletion is canceled;
* Fix port deletion: properly delete delays

## Release version 2.3.0
*2017.06.26*

### New Feature
* Add test plug-in fragments for future test campaigns
* Build process now produces
  * a source feature (include source code)
  * a 'meta' feature including all development requirements for PREESM
  * The aggregated Javadoc
* Maven build process allows to automatically deploy on SourceForge server
* Add clustering
* Add integration test API prototype
* Add Scheduling checker
* Add new entry in Preesm menu to run Workflows
* Add menus entries in the editor context menu

### Changes
* Add TMF updates repo for latest XTend lib
* Update Graphiti to 1.4.0
* Update DFTools to 1.2.0
* The build process does not require Graphiti and DFTools source code anymore
  * It uses the Preesm complete repo to lookup missing OSGi dependencies (see URL in pom.xml)
* Disable product and rcp.util build
* Third party dependencies are moved to external OSGi dependencies instead of jar archives within projects. See https://github.com/preesm/externaldeps
* The bundle and project names does not show any relation with DFTools
* Add checkstyle hook on Maven build to enforce code style
  * Config file is ./releng/VAADER_checkstyle.xml
  * Installable pre-commit hook in ./releng/hooks/
* Cleanup and Format code using Eclipse template that respects checkstyle config file
  * Eclipse preferences under ./releng/VAADER_eclipse_preferences.epf
* Update charset and line endings to UTF-8 and LF
* Move Ecore generated files to ecore-gen
* Move Ecore compliance level to 8.0
* Graphiti and DFTools have their own release notes
* .gitignore updated
* Unused Maven dependencies removed
* Add LICENCE file
* Update README.md
* Fix copyright header on most files (see ./releng/ scripts)
* Add .mailmap file for prettier git logs
* Spider CodeGen update
* C Parser update: now accepts trailing spaces after arguments
* Modifications in the API of some exceptions
* Update Spider codegen
* Update MPPA codegen
* For XTend version to 2.11+
* Use feature import instead of inclusion
* Add discovery sites in dev feature
* Remove unsupported target environments
* Update Checkstyle config file path in parent POM
* Add Eclipse profile in parent POM to disable m2e configuration outside Eclipse
* Update wrapper scripts
* Cleanup releng files
* Update licensing
* Update headers
* Remove use of composite P2 repositories
* Add Jenkinsfile for Multibranch Pipeline projects
* Major maintainer doc update
* Change upload destination of products to Sourceforge File Release Service
* Fix code coverage settings
* Replace HashMap/Sets with LinkedHashMap/Sets

### Bug fix
* Add new menus for running workflows (https://github.com/preesm/preesm/issues/28)
* Fix a bug in the Workflow due to Graphiti issue (https://github.com/preesm/preesm/issues/31)
* Include .exsd schemas in the binaries (https://github.com/preesm/preesm/issues/32)
* Fix Checkstyle and Findbugs issues
* Fix few warnings that raised after Eclipse cleanup
* fix bug#33: https://github.com/preesm/preesm/issues/33
* Fix bug#34: https://github.com/preesm/preesm/issues/34

## Release version 2.2.5
*2016.12.21 - Preesm: 2.2.5*

### Bug fix
* Fix parsing of header files. (typedefs, const pointers in function parameters, space before semi-colon).

## Release version 2.2.4
*2016.09.28 - Preesm: 2.2.4, DFTools 1.1.8*

### New Feature
* New support for distributed memory architectures during memory allocation and code generation. (cf. Memory allocation task documentation).
* New workflow tasks documentation describing purpose, inputs/outputs, parameters, and errors of tasks.
* `C` and `InstrumentedC` code generation supports following new processing elements types:`ARM`, `GPP`, `ARM_CortexA7`, `ARM_CortexA15`.
* Generated C code supports finite number of graph iteration.
* New systematic simplification of Fork-Join and Join-Fork patterns during single-rate transformation.

### Changes
* Ports of workflow tasks responsible for Memory Allocation and Code Generation were updated. `MEGs` replaces `MemEx` for Memory Allocation output port and Code Generation input port.
* Better parsing of header files.
* Improved IBSDF graph flattening. Better handling of FIFOs with no delay.
* Better FIFO serialization for debug purposes.
* Improve SVG Exporter
* Better error reporting in many workflow tasks.

### Bug fix
* Fix code generation issue when no actor was mapped on the main operator.
* Fix issue with constant parameter values based on expressions (e.g. "2*2").
* Fix creation of multiple outputs for roundbuffers during single-rate transformation.
* Detect and report overflow of token productions/consumption rates during hierarchy flattening.
* Fix absence of ports for special actors during SDF3 Export. (Issue was in single-rate transformation.)
* Fix overflow issue in Mapper.
* Fix overflow issue in scenario actor timing parser.
* Fix loss of memory annotations of ports of hierarcical actors.


## Release version 2.2.3
*2016.01.04 - Preesm: 2.2.3, DFTools 1.1.7*

### Changes
* Error/warning checking mechanism for PiGraphs integrated in eclipse.
* New SVG exporter for PiGraphs
* Lighten most decorator related computation to improve PiMM editor performance.

### Bug fix
* Fix the abnormally long execution time of mapper.

## Release version 2.2.2
*2015.09.16 - Preesm: 2.2.2*

### Bug fix
Remove the *.pi filter plugin from the package explorer

## Release version 2.2.1
*2015.09.14 - Preesm: 2.2.1, Graphiti: 1.3.15, DFTools 1.1.6*

### New Feature
* New automatic layout feature for PiSDF graph (Ctrl+Shift+F).
* New hotkeys in PiSDF graph editor. (Right-click on graph elements for new key combinations).
* New Hierarchy flattening algorithm.
* Promela exporter following SDF3 syntax (task id "org.ietr.preesm.algorithm.exportPromela.PromelaExporter").
* Display associated prototypes in actor properties.
* Throughput and Liveness evaluation for IBSDF graphs. (not documented yet)
* Add a "release notes" file.


### Changes
* Migration to java 8.
* Better excetion handling and signaling during workflow execution.
* Migration to Graphiti 0.12.0
* Adding Maven nature to project.
* Code cleanup (no more warnings !)

### Bug fixes
* Cyclic parameter dependencies crash.
* File access"Not local" and "Missing" resources in Codegen and memory scripts.
* Problem with UTF-8 characters in header parser.


### Bug fix
* Fix workflow task "org.ietr.preesm.pimm.algorithm.checker.periods.PeriodsPreschedulingChecker";
* Fix codegen: special actor ports order is now preserved during codegen;


## Release version 3.18.1
*2019.10.21*

### New Feature
* Codegen now updates MD5 using sink buffers (enabled and printed when loop size and verbose are defined);

### Changes
* Fix compile error in generated TCP code (but execution hangs...);
* Refactor;
* Clustering:
	* Workflow task PiSDF Clustering is now documented
	* Implemented a new schedule transform that optimize parallelism inside of sequantial schedule hierarchy
	* New parameter: Optimization criteria (memory or performance)
	* Codegen can now generate appropriate behavior for clustered-delay

### Bug fix


## Release version 3.18.0
*2019.10.09*

### New Feature
* Spider: 
  * Energy awareness (energy-awareness parameter) now can be activated in SPiDER
    * It will test a different number of PEs in each iteration
    * It will keep the PE config reaching the objective with the lowest (estimated) energy consumption

### Changes
* Hardware Codegen: improved monitoring using PAPIFY
* EnergyAwareness: performance objective now is considered a minimum. The tolerance has been removed;
* Fix PiSDF editor autoLayout, especially if having setters and getters on delays;
* Change the line style of setters and getters to dash dot;
* AutoLayout of Feedback Fifos is smarter (works also when no delays);
* Slam: 
  * move route model to xcore;
  * Refactor routing table;
* Synhtesis API: 
  * remodel schedule/memory allocation;
  * implement delay support;
  * implement communication insertion;
* Refactor:
  * Minor codegen refactor;
  * Refactor memory allocation;
  * Rethrow all exceptions thrown by JEP with expression causing issue;
* Add methods in PiSDFTopologyHelper, and corresponding tests;
* Simulation tab of scenario now has an option to import all data types from
  the graph, with predefined default values for common type names.

### Bug fix
* Fix #74
* Fix #80
* Fix #83
* Fix #85
* Fix #128
* Fix #190
* Fix problem with precedence shift condition (clustering purposes)
* Fix random seed parameter transfer to clustering algorithm
* Fix various bugs in the experimental external mapping importer.
* Fix #234
* Fix #166: force init/end on same operator in kwok scheduler;

## Release version 3.17.0
*2019.09.09*

### New Feature
* Codegen:
  * PREESM now support the codegeneration to manage hardware accelerators developed within [ARTICo³](https://des-cei.github.io/tools/artico3), an open-source runtime reconfigurable processing architecture;

### Changes
* Codegen: useless function removed in HW codegen;
* Refactor;

### Bug fix


## Release version 3.16.0
*2019.09.06*

### New Feature
* Codegen:
  * clusters can now be printed by using CodegenClusterTask
* Apollo optimizations are now supported in Preesm and SPiDER
  * To activate it, Apollo flag must be set to true in the CodeGen workflow task
  * CMakeLists.txt of the application must be modified 
* Scheduling:
  * Mapping/scheduling task (pisdf-mapper.list) can now perform energy-aware mapping/scheduling (It will increase scheduling/mapping task time, as it tests several mappings)
  * The information is taken from energy tab in scenario and the optimization will consider the performance objective to search for the best PEs configuration
  * EnergyAwareness parameter will enable this optimization
  * EnergyAwarenessFirstConfig selects the starting point: First (as less PEs as possible), Max (all PEs), Middle (half amount of PEs of each type) and Random (random starting point)
  * EnergyAwarenessSearchType selects the type of searching done: Thorough (changes PEs number one by one) and halves (Divides in halves the remaining available PEs and goes up/down depending if the FPS reached are below/above the objective)

### Changes
* PiSDF: 
  * PiGraph can be declared as a cluster. Hierarchy of cluster may be ignore by SR and DAG transformation;
  * Add operation to get the unique data port of a SrdagActor (init/end);
* Change PiSDF task categories in task reference;
* Clustering: 
  * cluster are now mappable into component by retrieving common possible mapping of included actors
  * make code clearer for ClusteringBuilder
  * refactored the way of getting clusterizable couples
  * clusters now have timings 
* Schedule: 
  * Readapt the way of exhibiting data parallelism and print expression
  * Add communication nodes
* Refactor:
  * Fix major and critical sonar issues;

### Bug fix
* Fix #218: add support for non connected actors in SRDAG transform;
* Fix #222: allow multiplicity in Codegen Call parameters;
* Fix #226: avoid name conflict in graphml configuration;
* Fix #227: enable Export SVG Diagram on any selected element;

## Release version 3.15.0
*2019.08.28*

### New Feature
* PAPIFY in SPiDER now has different modes:
  * papify=dump: dump PAPIFY info into csv files;
  * papify=feedback: PAPIFY (timing) feedback is retrieved by GRT and timings are updated;
  * papify=both: both modes active at the same time;
* SPiDER now includes information about energy consumed by an actor executed on a specific PE;
* SPiDER now includes information about energy consumption models associated to actors executed on PEs;
* PAPIFY in SPiDER with feedback mode active:
  * energy feedback is also supported. Energy consumption models based on PAPIFY events are considered when updating the energy consumption;
  * energy consumption of one actor being executed on a specific PE type is updated if possible;
* Timings and Energy tabs can import .papify files generated by PAPIFY scripts:
  * Timings: the timing of the actor being executed on each Component is updated;
  * Energy: the energy consumed when executing an actor on each Component is updated;
* PAPIFY tab in scenario now has a new section to include an energy model based on PAPIFY events:
  * The model can be exported as a csv file to apply PAPIFY script and compute the energy;
* PAPIFY output folder 'papify-output' now can be directly parsed from scenario:
  * Timings: the timing of the actor being executed on each Component is updated;
  * Energy: the energy consumed when executing an actor on each Component is updated. To do so, the energy model (included in PAPIFY tab) is considered when parsing the folder;
* PiSDF:
  * Add transform and task to replace "perfect fit delays" with init/end actors;
* Workflow:
  * Throws warning when executing deprecated tasks;
* Clustering:
  * Add 4 clustering algorithm: APGAN, Dummy, Random and Parallel
  * Cluster by generating sub-graph and associated schedule tree
  * [Hardware Codegen] Use of PAPIFY was extended to the Hardware on the FPGA side.

### Changes
* Refactor: rename/move packages in algo & codegen;
* Implement simple memory allocation in the new synthesis API;
* Prepare codegen model generator for the new synthesis API;
* Hardware Codegen Refactor: CHardwarePrinter extends now the CPrinter and override some of the methods;
* Hardware Codegen Refactor: CHardwarePrinter extends now the CPrinter and override some of the methods;
* SPiDER codegen considers empty PAPIFY configs as papify=false;
* Update round buffer memory script to handle multiple inputs;
* Schedule: Add an interface to perform transform;
* Scenario:
  * Update scenario generator to use unix separator in algo/archi URLs;
* PiSDF: 
  * Add more rules to check mergeability, remove getPreds/Succs from TopologyHelper;
  * Add rules in the consistency checker;
* Codegen:
  * Add IteratedBuffer, ClusterBlock, SectionBlock for clustering codegen
  * Add conditional "#pragma omp parallel for" on FiniteLoopBlock
  * Add null checks for codeGenDir (since not set when the parser sees a null length);

### Bug fix
* fix #186
* Fix #189
* Fix #193
* Fix scheduling of graphs with large data rates.
* Fix optimizations of PiSDF flattening task.

## Release version 3.14.1
*2019.07.25*

### New Feature

### Changes
* Schedule API:
  * Rename/move packages;
  * Add evaluation API;
* Releng:
  * Fix build script to fetch RCPTT from mirrors;

### Bug fix


## Release version 3.14.0
*2019.07.25*

### New Feature

### Changes
* Codegen:
  * Update Codegen to properly clean the directory before generating code;
  * Add C/C++ and XML code formatters;
* Update Preesm adapters and copy;
* Releng:
  * Update build script;
  * Fix Jacoco/Sonar config;

### Bug fix


## Release version 3.13.1
*2019.07.23*

### New Feature

### Changes
* Update CPrinter to support mixed C/C++ projects;

### Bug fix
* PAPIFY:
  * Fix PAPIFY code generation


## Release version 3.13.0
*2019.07.18*

### New Feature

### Changes
* Codegen:
  * Added an AbstractCodegenModelGenerator supertype to ease experimentation of another model generator from CodegenEngine;
  * Instrumented codegen now computes timings per PiSDF vertex path name instead of C function name;
* Schedule:
  * Added a reference to a AbstractActor in HierarchicalSchedule to retrieve cluster from their schedule;
  * Refactor legacy scheduler to make it deterministic;
* Releng:
  * Update jacoco/sonar config;
  * Update travis/sonar config;
* PiSDF:
  * Added method to ease regroupment of actors (for clustering process);
  * Refactor Topology helper methods and classes;

### Bug fix

## Release version 3.12.0
*2019.07.11*

### New Feature
* Added a new tab in the scenario to include power and energy information for the architecture and for the algorithm being executed on the different resources. The possibility to include a performance objective is also added;
  * These values are, as of now, not used in the later tasks;
* Add support for exporting hierarchical PiSDF graphs;

### Changes
* Fix gantt viewer PE order;
* Add subgraph disconnector, called before exporting graphs;
* Fix Slam parser;
* MPPA2Explicit now is called MPPA2Cluster;
* MPPA2IOExplicit now is called MPPA2IO;
* Update PiSDF consistency checker;

### Bug fix
* Fix #166: forces scheduler to put init and end actors on same core;
* Fix #170: do not count/launch empty clusters;
* Fix papify for clustered actors;


## Release version 3.11.0
*2019.07.05*

### New Feature

### Changes
* Make sure PiSDF flatten and SRDAG transformations keep track of the input PiGraph;
* Fix Papify component add/remove;
* Fix Papify heterogeneity (different monitoring configs depending on the PE type executing the actor) for PREESM and SPiDER;
* Update comments in PAPIFY UI;
* Update relend;
* Fix PiWriter: avoid throwing NPE when period is not attached to an element;
* Change mirror (ibcp down);
* Add new models for Schedule/Mapping/Memory Allocation;
* Add hardware ID in Slam to identify to which real core the thread affinity should be set;
* Merge the 4 Slam models into 1 file;
* Update codegen:
  * forces affinity of main thread to assigned core;
  * disable affinity on windows;

### Bug fix
* Fix #162

## Release version 3.10.0
*2019.06.20*

### New Feature

### Changes
* Fix Papi event add/remove;
* Deprecated PapifyEngine task, replaced with the new parameter in the codegen task;
* Update sonar plugin to 3.6.0.1398;
* Fix Scenario UI layouts;
* Refactoring;

### Bug fix
* Fix #154: fix scenario UI layouts;

## Release version 3.9.0
*2019.06.18*

### New Feature

### Changes
* Fix PiGraph.isLocallyStatic: now check for all contained parameters to be static;
* When the pi file does not specify a refinement for an actor, the parser was setting a pisdf refinement with null path. The new behavior is to set a CHeader refinement with null path. This allows the graph to be considered flat;
* Delete deprecated RandomSDF task and associated test;
* Cleanup and Fix warnings;
* Add class for helping loading resources from bundle/code base and replace uses;
* Paths in models (PiSDF, Slam) are now represented as Strings instead of IPath;
* Fix parameter validity analysis: new use JEP Wrapper;
* Change implementation of Constraint Groups in scenario to have only one operator per group;
* Make sure the PiGraph and SlamDesign are parsed once only per scenario node in the workflow;
* Fix UI throwing exception when canceling file browse;
* Scenario managers now use the Actor/Component reference instead of component ID or actor path/name;
* Refactor scenario and use XCore for Scenario;
* Add support for parameterized timings and for overriding parameter values in scenario (note: display is wrong when using ConfigInputInterface values);
* Fix Scenario UI: 
  * Timing tab is properly refreshed when param override values are changed;
  * Timing and Paramever override expressions are now properly using config input interface values from parent graphs;
* Remove number of execution of top graph from scenario (and UI);
* Update Xtend/Xcore maven plugins to 2.18.0+;
* Fix PiSDF UI: do show persistence level on Delay only;
* Update SDF/DAGVertex.getReferencePiVertex(): cast is now done inside;
* Fix Scenario UI and cleanup code, replace dialogs with inplace table cell modifiers;

### Bug fix
* Fix #139: better error messages for Name checker
* Fix #121
* Fix #77
* Fix #87
* Fix #155: always show the init function selection dialog and add a note explaining which functions are filtered out;
* Fix #158
* PAPIFY: fix removing PAPIFY timing event not working;


## Release version 3.8.1
*2019.05.22*

### New Feature

### Changes
* Make sure the order of tasks in the reference is always the same;
* Prefix stopThreads with preesm to avoid conflicts with application variables, and document variable;

### Bug fix
* Fix #135: properly use preesmStopThreads to control the loop;

## Release version 3.8.0
*2019.05.21*

### New Feature
*  Clustering and DistributedOnly memory distribution now are compatible;

### Changes
*  Fix C Hardware codegen: properly prefix LOOP_SIZE with PREESM_;
* SpiderCodegen: updated spider codegeneration to support the new Archi model of Spider.
*  Fix C Hardware codegen: properly printed the Release Kernel Instance with the name of the function to be executed in Hardware
*  Update workflow editor to better report task id issues;
*  Changed _PREESM_MONITOR_INIT by _PREESM_PAPIFY_MONITOR;

### Bug fix
*  Fix several bugs on the communications when using the IO in the MPPA code generation;

## Release version 3.7.0
*2019.05.14*

### New Feature
*  Codegen: enabled the code generation for offloading computation on the FPGA Prgrammable logic (hardware accelerators)
*  Codegen: core type "Hardware" is allowed
*  Codegen: multiple parallel Hardware Slots generated succesfully
*  Codegen: core type "MPPA2IOExplicit" is allowed
*  Test Integration: added test for Hardware Codegen
*  Codegen: enabled IO usage on the MPPA code generation
*  Codegen: enabled the use of DistributedOnly through the use of TwinBuffers for the repeated memory transmissions
*  Codegen: DistributedOnly memory distribution --> Support from CodeGen for each architecture needs to be done (DistributedMemoryCommunication printer has been included) Issue #134
*  Codegen: Inter cluster communication supported on the MPPA when DistributedOnly memory distribution is used
*  PAPIFY: PAPIFY now supports the monitoring of clustered actors

### Changes
* SpiderCodegen: updated spider codegeneration to follow up changes of spider develop branch.
* Codegen: Now host/IO/Cluster codes are automatically generated when using MPPA2 code generation.
* Enable RCPTT tests back (https://bugs.eclipse.org/bugs/show_bug.cgi?id=543731)
* PAPIFY now is based on its own classes to be printed --> Better support for each type of architecure
* PAPIFY monitoring can now be enabled/disabled defining/deleting _PREESM_MONITOR_INIT

### Bug fix
*  Fix #94
*  Fix #93
*  Fix #134
*  Fix #136
*  Fix PAPIFY bug where only one monitoring configuration was generated

## Release version 3.6.2
*2019.04.15*

### New Feature

### Changes
*  Codegen: LOOP_SIZE and VERBOSE macros have been prefixed with PREESM_ to avoid conflicts with application macros;

### Bug fix


## Release version 3.6.1
*2019.04.15*

### New Feature

### Changes

### Bug fix
*  Fix codegen: semaphore primitives are note static anymore to be visible during TCP codegen;
*  Fix #126

## Release version 3.6.0
*2019.04.10*

### New Feature
* XML exporter of repetition vector (workflow task id: pisdf-brv-export)
* Use annotations for declaring the tasks;
  * Incidentally, the task reference is now generated and available online;
* Add new CLI application to generate documentation: '-application org.preesm.cli.docgen -mdd <markdown file path>'

### Changes
* Make sure XCore generated code is 1.8 compliant;
* Remove org.preesm.workflows.{scenarios|tasks} extensions, replaced with annotation and global plugin declaration;

### Bug fix
* Fix #106
* Fix #109

## Release version 3.5.0
*2019.03.21*

### New Feature
* Add a sanity check on communication order in intermediate PE for multi-step communication.

### Changes
* Update workflow related messages;
* Delete unused resources;
* Disable sonar cloud on pull requests and other repos;
* Implements graph optimisations for graph flattening and provide rates warnings;
* Improve the periodic schedulability test by automatically computing the fifo breaking cycles;
* Improve the implementation of the tasks sorter for the Gantt display;
* Update Maven config:
  * Update Jacoco to 0.8.3
  * Update Checkstyle to 8.18
  * Update Xtext to 2.17.0

### Bug fix
* Fix #108: Update log messages to display the faulty task name and id, when relevant;
* Fix #113: Use different counter for setting thread count in code generation;
* Fix #118

## Release version 3.4.0
*2019.01.28*

### New Feature
* Add tasks to schedule PiSDF objects;
  * Mark old tasks as deprecated;

### Changes
* Properly throw exception with message;
* Minor refactor;
* Disable graphml editor association for .pi files;
* InstrumentedC now dumps timings in nanosecond and is cross platform

### Bug fix


## Release version 3.3.1
*2019.01.28*

### New Feature

### Changes

### Bug fix
* Fix #105: make sure visibility of tasks is public;


## Release version 3.3.0
*2019.01.25*

### New Feature
* Add workflow parameters 'Verbose Level' to control log level;
* Add workflow parameters 'Error on Warning' to fail and stop execution on warning or severe log entry;

### Changes
* Fix PiSDF single rate export: add suffix to the exported file name to avoid overriding the original graph;

### Bug fix


## Release version 3.2.0
*2019.01.24*

### New Feature
* Add context menu in PiSDF editor to compute the BRV;
* Add context menu in PiSDF editor to compute the SRDAG;

### Changes
* Update release system to support Eclipse 4.10;
  * Keep RCPTT disabled until new release supports 4.10+;
* Fix PiSDF UI:
  * Disable exception throwing at write/parse time when the PiSDF is not consistent in order to enable non-finished design to be saved;
  * Fix dependencies to enable copy/rename refactoring;
  * Fix filters/Add error handling to avoid exceptions when editing algos;
* Fix Scenario parsing: do not fail when graph/archi is not valid;
* Fix PiSDF single rate transformation
  * properly remove dependencies during optimizations;
  * rename parameters with graph prefix;
  * instantiate parameters only when used;

### Bug fix
* Fix #99 : Add code generation path check;
* Fix #103: Remove inline directive to enforce comptatiblity with most C compilers;

## Release version 3.1.3
*2018.12.19*

### New Feature

### Changes
* Enable xtend maven plugin on XCore projects in order to automatically apply XText nature on the eclipse project;
* Force Graphiti to 1.13.0+;
* Use custom XML writer for slam, pisdf and scenarios;
* Add UI tests for workflow and slam editors;

### Bug fix
* Fix issue 92: disable generation of std files for C6678 code generation;

## Release version 3.1.2
*2018.12.18*

### New Feature

### Changes

### Bug fix
*  Fix issue 89 reoppening: proposed solution on initial comment was not safe;

## Release version 3.1.1
*2018.12.17*

### New Feature

### Changes
* Refactor code base;
* Refactor PiSDF:
  * replace init/end String references with Object references;
  * Make Init/End actors special actors;
  * Update graph abstraction and implement high level operations;
  * Add structure checkers;  
* Update TCP Codegen: add threads dedicated to receive operations in order to free the TCP buffers ASAP and avoid deadlocks;

### Bug fix
* Fix issue 89: Fix stdint includes for MSVC;
* Fix issue 90: include missing xslt file and icons in the produced binaries;

## Release version 3.1.0
*2018.12.11*

### New Feature
* Add exporting task for flatten and single rate PiSDF;
  * also update generate diagram feature to support init / end actors;
  * NOT working for flatten graphs yet;

### Changes
* Adding a new function to jep, in order to compute i for max divisibility by 2**i;
* Add Snapshot releases repository;
* Refactor (method/class/package move/rename/delete);
  * have the flatten and single-rate transformations independant from scenarios;
* Remove IBSDF support in scenario;
* Fix PiSDF flattener and srdag transformations to output a consistent model;
* Centralize Xtend and XCore config on parent pom file;
* Remove uses of deprecated EdgeFactories of JGrapht. Use Suppliers instead;
* Fix Papify UI;
* Fix spider codegen to handle latest papify config;
* Updated spider codegen to handle parameterization directly from the workflow task.

### Bug fix
* Fix unicity of ports name when doing fork / join / broadcast / roundbuffer graph optimizations. 


## Release version 3.0.1
*2018.11.23*

### New Feature

### Changes
* Enable maven batch mode to prevent log to exceed size limit on travis;

### Bug fix


## Release version 3.0.0
*2018.11.23*

### New Feature

### Changes
* Update release process;

### Bug fix


## Release version 2.99.0
*2018.11.21*

### New Feature
* Add a simple scheduler that maps everything to the main PE;

### Changes
* Merge DFTools code in Preesm and remove upstream dependency;
* Rename plugins into org.preesm instead of org.ietr.{preesm|dftools};
* Rename packages into org.preesm instead of org.ietr.{preesm|dftools};
* Rename/merge plugins and fix dependencies (including tests);
* Move classes to proper plugins;
* Fix product;
* Fix Loggers;
* Delete unused PreesmApplication;
* Improve periodic actor support in UI; 
* Add periodic actor check (not working with cycles yet);
* Improve Papify support: 
  * implement 2D array for selecting event to monitor per actor;
  * implement codegen for static schedules (no support in spider yet);
* Remove preesm dev feature (moved to another repo);

### Bug fix


## Release version 2.17.1
*2018.11.09*

### New Feature

### Changes
* Use RCPTT runner prefetch script when deploying;
* Add RCPTT to dev feature dependencies;
* Synhcronize RCP plugin and Eclipse product;
* Add missing plugin to Preesm feature;
* Fix CLI Workflow Executor scenario path formatter;

### Bug fix


## Release version 2.17.0
*2018.11.08*

### New Feature

### Changes
* Fix BRV computation (more resilient to integer overflow);
* Fix travis file;
* Add tests
* Fix product definition (exporting from Eclipse now works properly);
* Refactor 
  * Impact DFTool refactor;
  * Remove dead code;
* Update DFTools to 2.0.0+
* Update Graphiti to 1.11.0+

### Bug fix


## Release version 2.16.0
*2018.10.11*

### New Feature
* Add workflow task to manually input a schedule in the flow;
* A period can be specified for each actor (but not for special actors), it is not used during scheduling for now;
  * Consistency is checked during workflow execution;

### Changes
* Major refactoring
  * Use Long instead of Integer in the Memory allocation;
  * Fix many bugs and code smells;
* Adding graph optimizations for PiMM graphs as seperate process from PiMM2SRDAG.
* Adding tests for graph transformations.
* Adding new task to flatten a given PiMM graph.
* Move travis dist to xenial;
* Fix PiSDF Exporter task to read a PiMM instead of a scenario;
* Fix Expression evaluator to properly handle several config input ports connected to the same parameter;
* Copying the timing property during the PiMM2SRDAG conversion;
* Add XSD for .pi files and corresponding Validator class;
* Move Xtend maven plugins to 2.15;
* Add LongExpressions to speedup evaluation;
* Use parameterized tests;
* Add test case for stressing memory allocation;
* Use LongFraction (tweak of apache.math3.Fraction) instead of Rationals and Fractions;

### Bug fix


## Release version 2.15.1
*2018.09.26*

### New Feature

### Changes
* Fix Spider codegen: properly return 0 when initializing archi;
* Update M2E Code Quality repo;
* Codegen now automatically fills its include list given actors refinements;
* Fix PiMM2SRDAG transformation (was giving wrong results / failing on specific cases);

### Bug fix
* Fix path format in scenario editor (issue #81);

## Release version 2.15.0
*2018.09.19*

### New Feature

### Changes
* Add helper method PiGraph.removeFifo(Fifo);
* Have the main operator thread execute in main thread;
  * for default C code generation;
  * for tcp codegen;
* Codegen now embeds communication, fifo, and dump C and header files
  * this includes TCP communication lib;
  * also includes semaphore and barrier implementation for MacOSX;
* Minor fixes in UI;
* Remove EMF dedicated repo (see https://github.com/eclipse/xtext/issues/1233#issuecomment-421904619);

### Bug fix


## Release version 2.14.2
*2018.09.10*

### New Feature

### Changes
* Add MacOSX comptabile icon;

### Bug fix


## Release version 2.14.1
*2018.09.07*

### New Feature

### Changes
* Update Eclipse-EMF maven repo URL;
* Update release scripts;
* Move XCore maven config to parent pom;
* Refactor scheduler;
* Move Codegen model and convert it from ECore to XCore;
* Spider codegen now support heteregeneous hardware.

### Bug fix
* fix issue #69;
* fix issue #71;
* fixing timing issue in spider codegen. It is temporary as it only copy the behavior of PREESM (see issue 73);
* fix issue #76; Spider Codegen now correctly assign parameters to method prototype.

## Release version 2.14.0
*2018.09.03*

### New Feature
* Add support for Delay initialization:
  * Extand semantic of Delay in the PiSDF Model;
  * Add setter and getter actors for intializing and flushing delays;
  * Add support in standard passes (use PiMM to SRDAG);
  * Add graphical support in PiSDF Editor (partial - autolayout can fail when using delay innitialization);
* Add prototype of SyncOptimizer: remove redundant synchronizations when working with zero-copy memories;
* Add sample JSon I/O for schedule/timing;

### Changes
* Replace deprecated DAGIterator with TopologicalDAGIterator;
* Enable XCore codegen plugin back in Maven (https://github.com/eclipse/xtext/issues/1233);
* Properly display errors in a dialog when generating diagram from .pi file fails;
* Update MapperDAG to keep a reference to the PiSDF; Refactor constructor accordingly;
* Update MathFunctionHelper to have lcm/gcd on longs;
* Refactor schedulers (and remove obsolete one);
* Flatten everything when depth level is set to negative values;
* Fix spider codegen:
  * Set delay persistence to have the same default behavior as Preesm;
  * Fix indentation;
* Minor refactoring;
* Update coding policies to 1.3.0;
  * also fix code;
* Improve scenario editor;
* Update external dependencies to [3.3.0,4.0.0);
* Update external Graphiti to [1.10.0,1.0.0);
* Update external DFTools to [1.9.0,2.0.0);
* All objects in the codegen model are now able to hold comments;
* Fix Hierarchical code generator: properly call IBSDF Flattener with -1 to flatten everything;
* Add ScheduledDAGIterator that iterates over a scheduled DAG vertices in the scheduled order;
* Fix old workflow: if persistent property is not set on delays, use permanent behavior by default;

### Bug fix
* Fix issue #62
* Fix issue #61 - include spider codegen;
* Fix issue #68

## Release version 2.13.0
*2018.07.09*

### New Feature
* New code generation with TCP communications - first prototype implementation;

### Changes
* Fix checkstyle configuration;
* Refactor FilesManager: rewrite in plain java and use proper URL resolver;
* Add PiGraph helper operation;
* Fix codegen core comparator for giving proper core ID;
* Update to Eclipse Photon;
* Force Graphiti to [1.9.0,2.0.0);
* Force DFTools to [1.8.0,2.0.0);
* Update Xtend/Xtext to 2.14.0+;
* Fix papify codegen for spider;
* Cleanup Preesm-Papify;

### Bug fix
* Fix issue #61;


## Release version 2.12.3
*2018.06.06*

### New Feature

### Changes
* Fix Prees about page in product;
* Fix Spider code generation to make it compatible with latest spider version;
* Fix dependency creation via UI: show warning when trying to drop the end of a dependency on an interface;
* Cleanup code;
* Add upper version (exclusive) in dependencies:
  * Update ExternalDeps to [3.2.0,4.0.0);
  * Update Graphiti to [1.8.0,2.0.0);
  * Update DFTools to [1.7.3,2.0.0);

### Bug fix


## Release version 2.12.2
*2018.06.01*

### New Feature

### Changes
* Fix Papify codegen configuration;
* Enable notifications to Slack vaader-ietr#preesm;
* Update DFTools to 1.7.2+;
* Changed Spider codegen to catch runtime_error exception instead of const char*;
* In spider codegen, the try - catch in the main auto generated now englobe the init as well;
* Update manifest files;
* Fix xtend warnings;
* Adding papify code generation for spider

### Bug fix
* Fix issue with copy/paste when reconnecting dependencies;


## Release version 2.12.1
*2018.05.17*

### New Feature

### Changes
* Update DFTools to 1.7.0+;
* Update code to be compatible with new version of DFTools that uses longs instead of integers;

### Bug fix


## Release version 2.12.0
*2018.05.16*

### New Feature
* Add new code generation scheme for instrumenting code with PAPI;
* add new code generator : PapifyC
* add new tab in the scenario editor to configure the Papi events to monitor;

### Changes
* Update coding policies to 1.2.4 (fix line split);
* Update DFTools to 1.6.0+;
* Mintor refactoring of the Codegen;
* Adding new facilities method in PiGraph;
* Enforced use of DAG properties instead of SDF properties when conversion of an SDF graph is done;
* Initialization of data size property in SDFEdge before dag conversion;
* Adding workflow task for converting Single rate SDF graph to DAG;
* Deprecating use of Schedule/Mapping task using SDF as input in favor to the new ones with DAG as input;

### Bug fix


## Release version 2.11.0
*2018.04.23*

### New Feature

### Changes
* Add a generic graph definition in PiSDF (graph, vertices, edges);
* Add helper oeprations in PiSDF;
* Update release process to upload releases on GitHub;
* Update operation with more accurate name and return type;
* Fix typos;
* Update checkstyle to 8.8 (and maven plugin to 3.0.0);
* Update Coding policies (now ignores javadoc @throw issues);
* Add script to prefetch RCPTT Runner from different mirror (faster);
* Add NonExecutableActors;
* Add helper methods in PiGraph;
* Minor refactoring;
* Update RCPTT POM Configuration;
* Have travis retry 3 times to fetch dependencies;
* Force DFTools to 1.4.1+;

### Bug fix
* Fix codegen for MS Visual Studio to support thread affinity;
* Properly rethrow exception when parser is failing instead of returning null;

## Release version 2.10.0
*2018.03.29*

### New Feature

### Changes
* Force DFTools to 1.4.0+;
* Force Graphiti to 1.6.0+;
* Force external deps to 3.0.0+;
* Move deploy server to preesm.insa-rennes.fr;
* Update project info (URL);

### Bug fix


## Release version 2.9.0
*2018.03.06*

### New Feature
* Add geometric sum function in expressions;
* Front-end SDF graph analysis plugin that checks if the given SDF graph can be executed in data-parallel fashion i.e. for each actor of the graph, run all its instances in parallel;

### Changes
* Update mailmap file;
* Disable RCPTT fetch when loading projects in Eclipse;
* Add custom license/copyright headers for fi.abo* plugins;
* Allow diagram generation for multiple pi files at once;
* Updated code generation of instrumentation: now time buffer are uint64_t instead of long;
* Update codegen C printer to set thread to core affinity;
* Update codegen C printer: remove GCC warnings in generated CoreX.c code;

### Bug fix


## Release version 2.8.0
*2018.02.09*

### New Feature
* Add throughput evaluation for PiSDF;
* Add min and max function in JEP
* Add a first prototype of the Activity Exporter;

### Changes
* Refactor PISDF;
* Change default flattening depth to -1 (infinite);
* Add missing icon in scenario UI;

### Bug fix
* Fix issue #53
* Fix a crash that occured when opening a scenario with a parameter value whose parameter do not exists in the graph anymore;

## Release version 2.7.0
*2018.02.05*

### New Feature

### Changes
* Refactor PiSDF Model;
* PiSDF: Replace Ecore/Genmodel with XCore;
* PiSDF: Remove XCore generated code from git;
* Update JGrapht to 1.1.0;
* Force DFTools to 1.3.0+;
* Force external deps to 2.0.0+;

### Bug fix


## Release version 2.6.2
*2018.01.23*

### New Feature

### Changes
* Update new Preesm project wizard: do not generate subfolders;
* Move icons to the proper plugin;
* Add shortcut to open property view from PiSDF editor;
* Fix codegen: thread function declaration and reference in the main now have matching name based on the core ID;
* Fix createActorFromRefinement feature: properly set the editor in unsaved state;

### Bug fix


## Release version 2.6.1
*2018.01.11*

### New Feature

### Changes
* Update releng scripts;
* update checkstyle to 8.5;
* Fix coding policies to match Checkstyle 8.5;
* Fix javadoc to respect Checkstyle 8.5;
* update coding policies: max line length is now 120 chars;
* use Maven plugins and coding policies from Maven Central instead of Preesm own maven repo;
* Cleanup UI plugins dependencies;
* Force DFTools to 1.2.10+;


### Bug fix


## Release version 2.6.0
*2017.12.21*

### New Feature
* Add UI Testing via RCPTT;

### Changes
* update release script;
* Update maven dependency plugin to 3.0.2;
* Rename releng profile to doUpdateSite and impact release scripts;
* disable javadoc generation (source is available via the Dev Features and source plugins);
* disable os specific archive format;
* Add dependency to RCPTT in the dev feature;
* Refactor the PiMM Model;
* Remove Multi* Tasks;
* split ui plugins into dedicated plugins (PiSDF, scenario, SDF);

### Bug fix
* fix issue #51


## Release version 2.5.4
*2017.12.04*

### New Feature

### Changes
* Update releng scripts;
* Refactor UI: use PiMMSwitches instead of instanceof ifs;
* Update product: now starts up the Preesm perspective by default and show Preesm in the title;
* Refactor code generation;
* Make scenario, architecture and algorithm visible from the codegen printers;
* Fix autolayout;

### Bug fix
* Force DFTools to version 1.2.7+ (fix issue #50);

## Release version 2.5.3
*2017.11.14*

### New Feature

### Changes
* Fix issue #48: Imporve Gantt chart viewer;
* Refactor & cleanup;
* Update scenario editor to color text field in red when algo/archi is not found;
* Update Papi code generator: enable and tune generation of the main C file;
* Change button label in actor properties from Edit to Browse;
* Add dependencies to Xcore and Sonarlint (dev feature), and CDT (dev + normal feature);
* Force external deps to 1.3.0+
* Force DFTools to 1.2.6+
* Add release scripts
* Remove unused script auto_convert_encoding_and_lineendings.sh

### Bug fix
* Fix issue #47: Make the PiGraph parser more robust;

## Release version 2.5.2
*2017.10.31*

### New Feature
* Add operation on PiMM Actors to access ports more easily;
* Add dependency to Ecore Diagram Tools from the dev feature;
* Enable Travis CI;

### Changes
* Refactoring, cleanup, code factorization, ...;
* Remove Shell from file selection popup API;
* Replace use of PiMMUtil.askFile with FileUtils.browseFile from DFTools;
* Discourage use of PiMMUtil.askFile;
* Check that a dependency is connected to a target before saving;
* Add project information in the POM file;
* When the setter class is unkown, assume the parameter is not locally static instead of throwing an error;


### Bug fix
* Fix cycle detection: ignore non connected ports as they obviously don't contribute to any cycle;
* Fix DependencyCycleDectector: Dependencies without a setter cannot obviously contribute to a cycle;
* Fix issue #43;
* Fix issue #44;
* fix issue #46;

## Release version 2.5.1
*2017.10.17*

### New Feature
* Add feature dependency to TM Terminal to have easy terminal access;
* Add feature dependency to Graphiti SDK Plus source in the dev feature;

### Changes
* Changes actor ports layout: add more gap for better readability;
* Autolayout feature now relayout actor content (for better rendering of old PiSDF graph);
* Update DFTools to 1.2.3 and Graphiti to 1.4.3;

### Bug fix

## Release version 2.5.0
*2017.09.15*

### Changes
* The code generator now generates a main.c(pp) file that adapts to the number of available cores, instead of having to manually edit the CMake file;
* Minor refactoring;

## Release version 2.4.2
*2017.08.17*

### New Feature
* Add menu entry to call the .diagram generator. This allows to generate the .diagram file from a PiMM Model (.pi) file;

### Changes
* Upgrade to Eclipse Oxygen;
* Use new Graphiti extension point name (new version);
* Refactoring;

### Bug fix
* Fix autolayout issue that prevented the feature to work when selection was not empty;
* Fix positionning of Delays when using copy/paste;
* Fix autolayout of dependencies targetting graph interfaces;

## Release version 2.4.1
*2017.07.19*

### Bug fix
* Fix dependency issue;
* Fix Parameter lookup in Dynamic PiMM 2 to SDF task;

## Release version 2.4.0
*2017.07.18*

### New Feature
* Enable copy/paste of vertices (actor, parameters, sink/source, special actors, ...) in the graphical PiMM editor (see https://github.com/preesm/preesm/issues/10);
   * Connects parameters and parameter configurations if paste occurs in the same diagram;
   * Copies fifos when copying group of actors, and connect delay dependencies if paste occurs in the same diagram;
   * Copies dependencies between copied ISetters and copied Parameterizables if paste occurs in a different diagram;

### Changes
* Update scenario editor: file selection popups now filter content more accurately;
* Add explicit verions for Xtend dependencies to avoid bugs due to API break;
* Cleanup releng files;
* Normalize feature licenses;
* Fix PiMM Ecore model: Port.name now has a lower bound of 0 to match the semantic of Parameter ConfigInputPorts (which have no name);
* Refactoring;
* Update DFTools to version 1.4.1 and Graphiti to 1.2.1;

### Bug fix
* Fix error handling in UI decorators (show decorators instead of crashing);
* Fix expression refresh on properties tab of PiMMEditor: now show red background and error message instead of showing multiple popups when expression cannot be evaluated;
* Fix deletion of actor + connected delay;
* Fix auto layout freezes when fifos input/output ports are not connected;
* Fix port deletion: do not delete connection if port deletion is canceled;
* Fix port deletion: properly delete delays

## Release version 2.3.0
*2017.06.26*

### New Feature
* Add test plug-in fragments for future test campaigns
* Build process now produces
  * a source feature (include source code)
  * a 'meta' feature including all development requirements for PREESM
  * The aggregated Javadoc
* Maven build process allows to automatically deploy on SourceForge server
* Add clustering
* Add integration test API prototype
* Add Scheduling checker
* Add new entry in Preesm menu to run Workflows
* Add menus entries in the editor context menu

### Changes
* Add TMF updates repo for latest XTend lib
* Update Graphiti to 1.4.0
* Update DFTools to 1.2.0
* The build process does not require Graphiti and DFTools source code anymore
  * It uses the Preesm complete repo to lookup missing OSGi dependencies (see URL in pom.xml)
* Disable product and rcp.util build
* Third party dependencies are moved to external OSGi dependencies instead of jar archives within projects. See https://github.com/preesm/externaldeps
* The bundle and project names does not show any relation with DFTools
* Add checkstyle hook on Maven build to enforce code style
  * Config file is ./releng/VAADER_checkstyle.xml
  * Installable pre-commit hook in ./releng/hooks/
* Cleanup and Format code using Eclipse template that respects checkstyle config file
  * Eclipse preferences under ./releng/VAADER_eclipse_preferences.epf
* Update charset and line endings to UTF-8 and LF
* Move Ecore generated files to ecore-gen
* Move Ecore compliance level to 8.0
* Graphiti and DFTools have their own release notes
* .gitignore updated
* Unused Maven dependencies removed
* Add LICENCE file
* Update README.md
* Fix copyright header on most files (see ./releng/ scripts)
* Add .mailmap file for prettier git logs
* Spider CodeGen update
* C Parser update: now accepts trailing spaces after arguments
* Modifications in the API of some exceptions
* Update Spider codegen
* Update MPPA codegen
* For XTend version to 2.11+
* Use feature import instead of inclusion
* Add discovery sites in dev feature
* Remove unsupported target environments
* Update Checkstyle config file path in parent POM
* Add Eclipse profile in parent POM to disable m2e configuration outside Eclipse
* Update wrapper scripts
* Cleanup releng files
* Update licensing
* Update headers
* Remove use of composite P2 repositories
* Add Jenkinsfile for Multibranch Pipeline projects
* Major maintainer doc update
* Change upload destination of products to Sourceforge File Release Service
* Fix code coverage settings
* Replace HashMap/Sets with LinkedHashMap/Sets

### Bug fix
* Add new menus for running workflows (https://github.com/preesm/preesm/issues/28)
* Fix a bug in the Workflow due to Graphiti issue (https://github.com/preesm/preesm/issues/31)
* Include .exsd schemas in the binaries (https://github.com/preesm/preesm/issues/32)
* Fix Checkstyle and Findbugs issues
* Fix few warnings that raised after Eclipse cleanup
* fix bug#33: https://github.com/preesm/preesm/issues/33
* Fix bug#34: https://github.com/preesm/preesm/issues/34

## Release version 2.2.5
*2016.12.21 - Preesm: 2.2.5*

### Bug fix
* Fix parsing of header files. (typedefs, const pointers in function parameters, space before semi-colon).

## Release version 2.2.4
*2016.09.28 - Preesm: 2.2.4, DFTools 1.1.8*

### New Feature
* New support for distributed memory architectures during memory allocation and code generation. (cf. Memory allocation task documentation).
* New workflow tasks documentation describing purpose, inputs/outputs, parameters, and errors of tasks.
* `C` and `InstrumentedC` code generation supports following new processing elements types:`ARM`, `GPP`, `ARM_CortexA7`, `ARM_CortexA15`.
* Generated C code supports finite number of graph iteration.
* New systematic simplification of Fork-Join and Join-Fork patterns during single-rate transformation.

### Changes
* Ports of workflow tasks responsible for Memory Allocation and Code Generation were updated. `MEGs` replaces `MemEx` for Memory Allocation output port and Code Generation input port.
* Better parsing of header files.
* Improved IBSDF graph flattening. Better handling of FIFOs with no delay.
* Better FIFO serialization for debug purposes.
* Improve SVG Exporter
* Better error reporting in many workflow tasks.

### Bug fix
* Fix code generation issue when no actor was mapped on the main operator.
* Fix issue with constant parameter values based on expressions (e.g. "2*2").
* Fix creation of multiple outputs for roundbuffers during single-rate transformation.
* Detect and report overflow of token productions/consumption rates during hierarchy flattening.
* Fix absence of ports for special actors during SDF3 Export. (Issue was in single-rate transformation.)
* Fix overflow issue in Mapper.
* Fix overflow issue in scenario actor timing parser.
* Fix loss of memory annotations of ports of hierarcical actors.


## Release version 2.2.3
*2016.01.04 - Preesm: 2.2.3, DFTools 1.1.7*

### Changes
* Error/warning checking mechanism for PiGraphs integrated in eclipse.
* New SVG exporter for PiGraphs
* Lighten most decorator related computation to improve PiMM editor performance.

### Bug fix
* Fix the abnormally long execution time of mapper.

## Release version 2.2.2
*2015.09.16 - Preesm: 2.2.2*

### Bug fix
Remove the *.pi filter plugin from the package explorer

## Release version 2.2.1
*2015.09.14 - Preesm: 2.2.1, Graphiti: 1.3.15, DFTools 1.1.6*

### New Feature
* New automatic layout feature for PiSDF graph (Ctrl+Shift+F).
* New hotkeys in PiSDF graph editor. (Right-click on graph elements for new key combinations).
* New Hierarchy flattening algorithm.
* Promela exporter following SDF3 syntax (task id "org.ietr.preesm.algorithm.exportPromela.PromelaExporter").
* Display associated prototypes in actor properties.
* Throughput and Liveness evaluation for IBSDF graphs. (not documented yet)
* Add a "release notes" file.


### Changes
* Migration to java 8.
* Better excetion handling and signaling during workflow execution.
* Migration to Graphiti 0.12.0
* Adding Maven nature to project.
* Code cleanup (no more warnings !)

### Bug fixes
* Cyclic parameter dependencies crash.
* File access"Not local" and "Missing" resources in Codegen and memory scripts.
* Problem with UTF-8 characters in header parser.

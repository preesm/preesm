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
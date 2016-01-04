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
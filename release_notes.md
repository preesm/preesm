PREESM Changelog
================

## Release version X.Y.Z
*XXXX.XX.XX*

### New Feature
* Enable copy/paste of vertices (actor, parameters, sink/source, special actors, ...) in the graphical PiMM editor (see https://github.com/preesm/preesm/issues/10);

### Changes
* Update scenario editor: file selection popups now filter content more accurately;
* Add explicit verions for Xtend dependencies to avoid bugs due to API break;
* Cleanup releng files
* Normalize feature licenses

### Bug fix
* Fix error handling in UI decorators (show decorators instead of crashing);
* Fix expression refresh on properties tab of PiMMEditor: now show red background and error message instead of showing multiple popups when expression cannot be evaluated;

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

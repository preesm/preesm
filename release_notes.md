PREESM Changelog
================

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

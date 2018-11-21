New Preesm Workflow
===================

Explanation
-----------

From PREESM version 0.6.0, workflow management is not in PREESM itself but in a side project named DFTools. The structure of the .workflow files is modified and needs to be converted.

Conversion method
-----------------

Since DFTools 2.0.0, the old workflow converter appears in the Eclipse UI as a menu entry in the Preesm menu. It is visible only when right clicking, from the file tree, on a workflow that match the old format. New workflows are created named “[old workflow name]_new.workflow”.

**Note:** Since DFTools 2.0.0, parameters are optional in the tasks. When no parameter is found in the specification, the default value will be used. A warning is printed in the log.

Conversion method (using standalone jar)
----------------------------------------

In PREESM version 2.2.5 (DFTools v1.1.8) and earlier, the workflow converter was shipped as a standalone jar file located in the tests folder of the Preesm repository (see [v2.2.5 repository](https://github.com/preesm/preesm/tree/92a38d3430f1787c2d08ff6b58882c5ce1a6e8bd/tests/OldWorkflow2NewWorkflow)). Since version 2.0.0 of DFTools, it is shipped within the Eclipse plugins (see above).

### Howto run

Copy the program “WorkflowConverter.jar” and the file “newWorkflow.xslt” in a directory containing workflows you want to convert. Launch the executable “WorkflowConverter.jar”. New workflows are created named “[old workflow name]_new.workflow”. They are compatible with Preesm 0.6.0 and higher. You can now move or delete “WorkflowConverter.jar” and “newWorkflow.xslt”.

### Manual modifications

All parameters are now mandatory in workflow tasks. You may have some problems if you lack parameters. Opening, modifying (only moving a task is enough) and saving a workflow file automatically adds default parameters if they are not present in tasks.
The inputs of code generation are now DAG, architecture and scenario and these three input ports are mandatory. Modify the workflow if the inputs are different (for instance, ABC and scenario).

**Note:** This manual modification is not necessary since DFTools 2.0.0 (see note above).


For more questions
------------------

Please visit our website : http://preesm.org/

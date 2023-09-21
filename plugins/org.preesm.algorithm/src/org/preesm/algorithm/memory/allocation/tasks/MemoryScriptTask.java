/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2014 - 2015)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.memory.allocation.tasks;

import bsh.EvalError;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.memory.script.MemoryScriptEngine;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.SimulationInfo;
import org.preesm.model.scenario.check.FifoTypeChecker;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * The Class MemoryScriptTask.
 */
@PreesmTask(id = "org.ietr.preesm.memory.script.MemoryScriptTask", name = "Memory Scripts",
    category = "Memory Optimization",

    inputs = { @Port(name = "DAG", type = DirectedAcyclicGraph.class),
        @Port(name = "MemEx", type = MemoryExclusionGraph.class), @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "MemEx", type = MemoryExclusionGraph.class) },

    shortDescription = "Executes the memory scripts associated to actors and merge buffers.",

    description = "Executes the memory scripts associated to actors and merge buffers. The purpose of the "
        + "memory scripts is to allow Preesm to allocate input and output buffers of certain actors in "
        + "overlapping memory range.",

    parameters = {
        @Parameter(name = "Check",
            description = "Verification policy used when checking the applicability of the memory scripts written"
                + " by the developer and associated to the actor.",
            values = {
                @Value(name = "Thorough",
                    effect = "Will generate error messages with a detailed description of the source of the error."
                        + " This policy should be used when writting memory scripts for the first time."),
                @Value(name = "Fast",
                    effect = "All errors in memory script are still detected, but error messages are less verbose. "
                        + "This verification policy is faster than the Thorough policy."),
                @Value(name = "None",
                    effect = "No verification is performed. Use this policy to speed up workflow execution once all"
                        + " memory scripts have been validated..") }),
        @Parameter(name = "False Sharing Prevention",
            description = "Force additional allocation before/after buffer to prevent false sharing issues.",
            values = { @Value(name = "False", effect = "The false sharing prevention mecanism will not be used."),
                @Value(name = "True",
                    effect = "The false sharing prevention mecanism will be used."
                        + "Using the Data alignement parameter.") }),
        @Parameter(name = "Data alignment",
            description = "Option used to force the allocation of buffers with aligned addresses. The data"
                + " alignment property should always have the same value as the one set in the properties of "
                + "the Memory Allocation task.",
            values = { @Value(name = "None", effect = "No special care is taken to align the buffers in memory."),
                @Value(name = "Data",
                    effect = "All buffers are aligned on addresses that are multiples of their size. For example,"
                        + "a 32 bits integer is aligned on 32 bits address."),
                @Value(name = "Fixed:=$$n$$",
                    effect = "Where $$n\\in \\mathbb{N}^*$$. This forces the allocation algorithm to align all buffers"
                        + " on addresses that are multiples of n bits.") }),
        @Parameter(name = "Log Path",
            description = "Specify whether, and where, a log of the buffer matching optimization should be "
                + "generated. Generated log are in the markdown format, and provide information "
                + "on all matches created by scripts as well as which match could be applied by the "
                + "optimization process.",
            values = {
                @Value(name = "path/file.txt",
                    effect = "The path given in this property is relative to the ”Code generation "
                        + "directory” defined in the executed scenario."),
                @Value(name = "empty", effect = "No log will be generated.") }),
        @Parameter(name = "Verbose", description = "Verbosity of the workflow task.",
            values = { @Value(name = "True", effect = "The workflow task will be verbose in the console."),
                @Value(name = "False", effect = "The workflow task will be more quiet in the console.") }), },

    seeAlso = { "**Buffer merging**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. "
        + "On memory reuse between inputs and outputs of dataflow actors. ACM Transactions on Embedded Computing "
        + "Systems, 15(30):25, January 2016." })
public class MemoryScriptTask extends AbstractTaskImplementation {

  public static final String PARAM_VERBOSE        = "Verbose";
  public static final String VALUE_TRUE           = "True";
  public static final String VALUE_FALSE          = "False";
  public static final String PARAM_LOG            = "Log Path";
  public static final String VALUE_LOG            = "log_memoryScripts";
  public static final String PARAM_CHECK          = "Check";
  public static final String VALUE_CHECK_NONE     = "None";
  public static final String VALUE_CHECK_FAST     = "Fast";
  public static final String VALUE_CHECK_THOROUGH = "Thorough";
  public static final String PARAM_FALSE_SHARING  = "False Sharing Prevention";

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> param = new LinkedHashMap<>();
    param.put(MemoryScriptTask.PARAM_VERBOSE,
        "? C {" + MemoryScriptTask.VALUE_TRUE + ", " + MemoryScriptTask.VALUE_FALSE + "}");
    param.put(MemoryScriptTask.PARAM_CHECK, "? C {" + MemoryScriptTask.VALUE_CHECK_NONE + ", "
        + MemoryScriptTask.VALUE_CHECK_FAST + ", " + MemoryScriptTask.VALUE_CHECK_THOROUGH + "}");
    param.put(MemoryScriptTask.PARAM_FALSE_SHARING, MemoryScriptTask.VALUE_FALSE);
    param.put(MemoryAllocatorTask.PARAM_ALIGNMENT, MemoryAllocatorTask.VALUE_ALIGNEMENT_DEFAULT);
    param.put(MemoryScriptTask.PARAM_LOG, MemoryScriptTask.VALUE_LOG);

    return param;
  }

  @Override
  public String monitorMessage() {
    return "Running Memory Optimization Scripts.";
  }

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {
    // Get verbose parameter
    final boolean verbose = parameters.get(MemoryScriptTask.PARAM_VERBOSE).equals(MemoryScriptTask.VALUE_TRUE);

    // Get the log parameter
    final String log = parameters.get(MemoryScriptTask.PARAM_LOG);

    // Get false sharing prevention flag
    final boolean falseSharingPreventionFlag = parameters.get(MemoryScriptTask.PARAM_FALSE_SHARING)
        .equals(MemoryScriptTask.VALUE_TRUE);

    // Retrieve the alignment param
    final String valueAlignment = parameters.get(MemoryAllocatorTask.PARAM_ALIGNMENT);

    // Retrieve the input graph
    final DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");

    // Get the data types from the scenario
    final Scenario scenario = (Scenario) inputs.get("scenario");
    FifoTypeChecker.checkMissingFifoTypeSizes(scenario);
    final SimulationInfo simulationInfo = scenario.getSimulationInfo();

    // Get check policy
    final String checkString = parameters.get(MemoryScriptTask.PARAM_CHECK);

    final MemoryExclusionGraph meg = (MemoryExclusionGraph) inputs.get("MemEx");

    // execute
    final MemoryScriptEngine engine = new MemoryScriptEngine(falseSharingPreventionFlag, valueAlignment, log, verbose);
    try {
      engine.runScripts(dag, simulationInfo, checkString);
    } catch (final EvalError e) {
      final String message = "An error occurred during memory scripts execution";
      throw new PreesmRuntimeException(message, e);
    }
    engine.updateMemEx(meg);

    if (!log.equals("")) {
      // generate
      engine.generateCode(scenario, log);
    }

    // Outputs
    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put("MemEx", meg);
    return outputs;
  }
}

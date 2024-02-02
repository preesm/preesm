/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
 * Julien Hascoet [jhascoet@kalray.eu] (2016)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013 - 2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2013)
 * Raquel Lazcano [raquel.lazcano@upm.es] (2019)
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
package org.preesm.codegen.xtend.task;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.generator.CodegenModelGenerator;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * The Class CodegenTask.
 */
@PreesmTask(id = "org.ietr.preesm.codegen.xtend.task.CodegenSimSDPTask", name = "Intra Node Code Generation",
    category = "Code Generation",

    inputs = { @Port(name = "MEGs", type = Map.class), @Port(name = "DAG", type = DirectedAcyclicGraph.class),
        @Port(name = "scenario", type = Scenario.class), @Port(name = "architecture", type = Design.class) },

    shortDescription = "Generate code for the application deployment resulting from the workflow execution.",

    description = "This workflow task is responsible for generating code for the application deployment resulting "
        + "from the workflow execution.\n\n" + "The generated code makes use of 2 macros that can be overridden in"
        + " the **preesm.h** user header file:\n"
        + "*  **PREESM_VERBOSE** : if defined, the code will print extra info about actor firing;\n"
        + "*  **PREESM_LOOP_SIZE** : when set to an integer value $$n > 0$$, the application will terminate after"
        + " $$n$$ executions of the graph.\n"
        + "*  **PREESM_NO_AFFINITY** : if defined, the part of the code that sets the affinity to specific cores "
        + "will be skipped;\n" + "\n"
        + "When the loop size macro is omitted, the execution can be stopped by setting the global variable "
        + "**preesmStopThreads** to 1. This variable is defined in the **main.c** generated file, and should be "
        + "accessed using extern keyword.",

    parameters = { @Parameter(name = "Printer",
        description = "Specify which printer should be used to generate code. Printers are defined in Preesm source"
            + " code using an extension mechanism that make it possible to define a single printer name for several "
            + "targeted architecture. Hence, depending on the type of PEs declared in the architecture model, Preesm "
            + "will automatically select the associated printer class, if it exists.",
        values = {
            @Value(name = "C",
                effect = "Print C code and shared-memory based communications. Currently compatible with x86, c6678, "
                    + "and arm architectures."),
            @Value(name = "InstrumentedC",
                effect = "Print C code instrumented with profiling code, and shared-memory based communications. "
                    + "Currently compatible with x86, c6678 architectures.."),
            @Value(name = "XML",
                effect = "Print XML code with all informations used by other printers to print code. "
                    + "Compatible with x86, c6678.") }),
        @Parameter(name = "Papify", description = "Enable the PAPI-based code instrumentation provided by PAPIFY",
            values = { @Value(name = "true/false",
                effect = "Print C code instrumented with PAPIFY function calls based on the user-defined configuration"
                    + " of PAPIFY tab in the scenario. Currently compatibe with x86 and MPPA-256") }),
        @Parameter(name = "Apollo", description = "Enable the use of Apollo for intra-actor optimization",
            values = { @Value(name = "true/false",
                effect = "Print C code with Apollo function calls. " + "Currently compatibe with x86") }),
        @Parameter(name = "Multinode", description = "oué", values = { @Value(name = "true/false", effect = "oué") }) })
public class CodegenSimSDPTask extends AbstractTaskImplementation {

  /** The Constant PARAM_PRINTER. */
  public static final String PARAM_PRINTER = "Printer";

  /** The Constant VALUE_PRINTER_IR. */
  public static final String VALUE_PRINTER_IR = "IR";

  /** The Constant PARAM_PAPIFY. */
  public static final String PARAM_PAPIFY = "Papify";

  /** The Constant PARAM_APOLLO. */
  public static final String PARAM_APOLLO = "Apollo";

  /** The Constant PARAM_MULTINODE. */
  public static final String PARAM_MULTINODE = "Multinode";

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute( java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    // Retrieve inputs
    final Scenario scenario = (Scenario) inputs.get("scenario");
    if (scenario.getCodegenDirectory() == null) {
      throw new PreesmRuntimeException("Codegen path has not been specified in scenario, cannot go further.");
    }

    final Design archi = (Design) inputs.get("architecture");
    final DirectedAcyclicGraph algoDAG = (DirectedAcyclicGraph) inputs.get("DAG");
    @SuppressWarnings("unchecked")
    final Map<String, MemoryExclusionGraph> megs = (Map<String, MemoryExclusionGraph>) inputs.get("MEGs");
    if (!(algoDAG instanceof final MapperDAG algo)) {
      throw new PreesmRuntimeException("The input DAG has not been scheduled");
    }
    // Generate intermediate model
    final CodegenModelGenerator generator = new CodegenModelGenerator(archi, algo, megs, scenario, null);
    // Retrieve the PAPIFY flag
    final String papifyMonitoring = parameters.get(CodegenSimSDPTask.PARAM_PAPIFY);
    generator.registerPapify(papifyMonitoring);

    // Retrieve the APOLLO flag
    final String apolloFlag = parameters.get(CodegenSimSDPTask.PARAM_APOLLO);

    // Retrieve the MULTINODE flag
    final String multinode = parameters.get(CodegenSimSDPTask.PARAM_MULTINODE);
    generator.registerMultinode(multinode);

    final Collection<Block> codeBlocks = generator.generate();
    for (final Block block : codeBlocks) {
      if (block instanceof final CoreBlock coreBlock) {
        final int nodeID = Integer.parseInt(scenario.getAlgorithm().getName().replace("sub", ""));
        coreBlock.setNodeID(nodeID);
      }
    }

    // Retrieve the desired printer and target folder path
    final String selectedPrinter = parameters.get(CodegenSimSDPTask.PARAM_PRINTER);
    final String codegenPath = scenario.getCodegenDirectory() + File.separator;

    // Create the codegen engine
    final CodegenEngine engine = new CodegenEngine(codegenPath, codeBlocks, algo.getReferencePiMMGraph(), archi,
        scenario);

    if (CodegenSimSDPTask.VALUE_PRINTER_IR.equals(selectedPrinter)) {
      engine.initializePrinterIR(codegenPath);
    }

    engine.registerApollo(apolloFlag);

    engine.registerPrintersAndBlocks(selectedPrinter);
    engine.preprocessPrinters();
    engine.print();

    headerPrint(codegenPath, scenario.getAlgorithm(), codeBlocks);

    // Create empty output map (codegen doesn't have output)
    return new LinkedHashMap<>();
  }

  private void headerPrint(String codegenPath, PiGraph subGraph, Collection<Block> codeBlocks) {
    final StringConcatenation content = new StringConcatenation();
    content.append(header(subGraph));
    content.append("#include \"preesm.h\"\n");
    content.append("#include \"stdlib.h\"\n");
    content.append("#include <stdio.h>\n");
    final String upper = subGraph.getName().toUpperCase() + "_H";
    content.append("#ifndef " + upper + "\n", "");
    content.append("#define " + upper + "\n", "");
    content.append("void " + subGraph.getName() + "(" + printNodeArg(subGraph, codeBlocks) + "); \n");
    content.append(struct(subGraph, codeBlocks));
    content.append("#endif \n", "");

    PreesmIOHelper.getInstance().print(codegenPath, subGraph.getName() + ".h", content);

  }

  private StringConcatenation struct(PiGraph subGraph, Collection<Block> codeBlocks) {
    final StringConcatenation result = new StringConcatenation();
    result.append("typedef struct {\n");
    final CoreBlock firstBlock = (CoreBlock) codeBlocks.stream().findFirst().get();
    for (final Buffer topBuffer : firstBlock.getTopBuffers()) {
      result.append(topBuffer.getType() + " *" + topBuffer.getName() + ";\n");
    }

    result.append("} ThreadParams" + subGraph.getName().replace("sub", "") + ";\n");
    return result;
  }

  private StringConcatenation header(PiGraph subGraph) {
    final StringConcatenation result = new StringConcatenation();
    result.append("/** \n");
    result.append("* @file " + subGraph.getName() + ".h \n");
    result.append("* @generated by " + this.getClass().getSimpleName() + "\n");
    result.append("* @date " + new Date() + "\n");
    result.append("*/ \n\n");
    return result;
  }

  private String printNodeArg(PiGraph node, Collection<Block> codeBlocks) {

    String str = "";

    final CoreBlock firstBlock = (CoreBlock) codeBlocks.stream().findFirst().get();
    for (final Buffer topBuffer : firstBlock.getTopBuffers()) {
      str += topBuffer.getType() + " *" + topBuffer.getName() + ",";
    }

    if (str.endsWith(",")) {
      str = str.substring(0, str.length() - 1);
    }
    return str;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    final StringBuilder avilableLanguages = new StringBuilder("? C {");

    // Retrieve the languages registered with the printers
    final Set<String> languages = new LinkedHashSet<>();
    final IExtensionRegistry registry = Platform.getExtensionRegistry();

    final IConfigurationElement[] elements = registry
        .getConfigurationElementsFor("org.ietr.preesm.codegen.xtend.printers");
    for (final IConfigurationElement element : elements) {
      languages.add(element.getAttribute("language"));
    }

    for (final String lang : languages) {
      avilableLanguages.append(lang + ", ");
    }
    avilableLanguages.append(CodegenSimSDPTask.VALUE_PRINTER_IR + "}");

    parameters.put(CodegenSimSDPTask.PARAM_PRINTER, avilableLanguages.toString());
    // Papify default
    parameters.put(CodegenSimSDPTask.PARAM_PAPIFY, "false");

    parameters.put(CodegenSimSDPTask.PARAM_MULTINODE, "false");
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Generate intra node xtend code";
  }

}

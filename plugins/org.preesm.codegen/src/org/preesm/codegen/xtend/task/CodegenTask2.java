/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Julien Hascoet [jhascoet@kalray.eu] (2016)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013 - 2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2013)
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.generator2.CodegenModelGenerator2;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * The Class CodegenTask.
 */
@PreesmTask(id = "codegen2", name = "Code Generation 2", category = "Code Generation 2",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class), @Port(name = "Schedule", type = Schedule.class),
        @Port(name = "Mapping", type = Mapping.class), @Port(name = "Allocation", type = Allocation.class) },

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
        @Parameter(name = "Multinode", description = "oué", values = { @Value(name = "true/false", effect = "oué") })

    })
public class CodegenTask2 extends AbstractTaskImplementation {

  /** The Constant PARAM_PRINTER. */
  public static final String PARAM_PRINTER = "Printer";

  /** The Constant VALUE_PRINTER_IR. */
  public static final String VALUE_PRINTER_IR = "IR";

  /** The Constant PARAM_PAPIFY. */
  public static final String PARAM_PAPIFY = "Papify";

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
    final PiGraph algo = (PiGraph) inputs.get("PiMM");

    final Schedule schedule = (Schedule) inputs.get("Schedule");
    final Mapping mapping = (Mapping) inputs.get("Mapping");
    final Allocation memAlloc = (Allocation) inputs.get("Allocation");

    // Retrieve the PAPIFY flag
    final boolean papify = "true".equalsIgnoreCase(parameters.get(CodegenTask2.PARAM_PAPIFY));

    // Retrieve the MULTINODE flag
    final boolean multinode = "true".equalsIgnoreCase(parameters.get(CodegenTask2.PARAM_MULTINODE));

    PreesmLogger.getLogger().log(Level.INFO, "Generating blocks.");
    final List<Block> codeBlocks = CodegenModelGenerator2.generate(archi, algo, scenario, schedule, mapping, memAlloc,
        papify, multinode);

    PreesmLogger.getLogger().log(Level.INFO, "Printing blocks.");

    // Retrieve the desired printer and target folder path
    final String selectedPrinter = parameters.get(CodegenTask2.PARAM_PRINTER);
    final String codegenPath = scenario.getCodegenDirectory() + File.separator;

    // Create the codegen engine
    final CodegenEngine engine = new CodegenEngine(codegenPath, codeBlocks, algo, archi, scenario);

    if (CodegenTask2.VALUE_PRINTER_IR.equals(selectedPrinter)) {
      engine.initializePrinterIR(codegenPath);
    }

    engine.registerPrintersAndBlocks(selectedPrinter);
    engine.preprocessPrinters();

    engine.print();
    // Create empty output map (codegen doesn't have output)
    return new LinkedHashMap<>();
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
    avilableLanguages.append(CodegenTask2.VALUE_PRINTER_IR + "}");

    parameters.put(CodegenTask2.PARAM_PRINTER, avilableLanguages.toString());
    // Papify default
    parameters.put(CodegenTask2.PARAM_PAPIFY, "false");

    parameters.put(CodegenTask2.PARAM_MULTINODE, "false");
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Generate xtend code";
  }

}

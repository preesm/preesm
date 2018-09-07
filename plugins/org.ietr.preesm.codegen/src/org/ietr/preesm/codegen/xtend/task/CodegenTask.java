/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Hascoet <jhascoet@kalray.eu> (2016)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2013)
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
package org.ietr.preesm.codegen.xtend.task;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.codegen.model.codegen.Block;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

/**
 * The Class CodegenTask.
 */
public class CodegenTask extends AbstractTaskImplementation {

  /** The Constant PARAM_PRINTER. */
  public static final String PARAM_PRINTER = "Printer";

  /** The Constant VALUE_PRINTER_IR. */
  public static final String VALUE_PRINTER_IR = "IR";

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
    final PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");
    final Design archi = (Design) inputs.get("architecture");
    @SuppressWarnings("unchecked")
    final Map<String, MemoryExclusionGraph> megs = (Map<String, MemoryExclusionGraph>) inputs.get("MEGs");
    final DirectedAcyclicGraph algo = (DirectedAcyclicGraph) inputs.get("DAG");

    // Generate intermediate model
    final CodegenModelGenerator generator = new CodegenModelGenerator(archi, algo, megs, scenario, workflow);

    final Collection<Block> codeBlocks = generator.generate();

    // Retrieve the desired printer and target folder path
    final String selectedPrinter = parameters.get(CodegenTask.PARAM_PRINTER);
    final String codegenPath = scenario.getCodegenManager().getCodegenDirectory() + File.separator;

    // Create the codegen engine
    final CodegenEngine engine = new CodegenEngine(codegenPath, codeBlocks, generator);

    if (CodegenTask.VALUE_PRINTER_IR.equals(selectedPrinter)) {
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
    String avilableLanguages = "? C {";

    // Retrieve the languages registered with the printers
    final Set<String> languages = new LinkedHashSet<>();
    final IExtensionRegistry registry = Platform.getExtensionRegistry();

    final IConfigurationElement[] elements = registry
        .getConfigurationElementsFor("org.ietr.preesm.codegen.xtend.printers");
    for (final IConfigurationElement element : elements) {
      languages.add(element.getAttribute("language"));
    }

    for (final String lang : languages) {
      avilableLanguages += lang + ", ";
    }
    avilableLanguages += CodegenTask.VALUE_PRINTER_IR + "}";

    parameters.put(CodegenTask.PARAM_PRINTER, avilableLanguages);
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

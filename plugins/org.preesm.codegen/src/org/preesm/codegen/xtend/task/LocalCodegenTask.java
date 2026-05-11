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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.codegen.format.CodeFormatterAndPrinter;
import org.preesm.codegen.fpga.FpgaCodeGenerator;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.generator2.CodegenModelGenerator2;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.FPGA;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * The Class CodegenTask.
 */
@PreesmTask(id = "localcodegen", name = "Local Codegen", category = "Local Codegen",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
      @Port(name = "architecture", type = Design.class), @Port(name = "localSyntheses", type = Map.class), },
    outputs = { @Port(name = "PiMM", type = PiGraph.class) },

    parameters = {
      @Parameter(name = "Printer",
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
                  + " of PAPIFY tab in the scenario. Currently compatibe with x86 and MPPA-256") }) })
public class LocalCodegenTask extends AbstractTaskImplementation {

  /** The Constant PARAM_PRINTER. */
  public static final String PARAM_PRINTER = "Printer";

  /** The Constant VALUE_PRINTER_IR. */
  public static final String VALUE_PRINTER_IR = "IR";

  /** The Constant PARAM_PAPIFY. */
  public static final String PARAM_PAPIFY = "Papify";

  Map<FunctionArgument, org.preesm.model.pisdf.Port> param2Interface = new HashMap<>();

  Map<Fifo, String> fifo2buffName = new HashMap<>();

  final PiMMUserFactory PiMMFactory = org.preesm.model.pisdf.factory.PiMMUserFactory.instance;

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.workflow.implement.AbstractTaskImplementation#execute( java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.preesm.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    PreesmLogger.getLogger().info(" -- Local codegen --");

    // Retrieve inputs
    final Scenario scenario = (Scenario) inputs.get("scenario");
    if (scenario.getCodegenDirectory() == null) {
      throw new PreesmRuntimeException("Codegen path has not been specified in scenario, cannot go further.");
    }
    final Design archi = (Design) inputs.get("architecture");
    final PiGraph algo = (PiGraph) inputs.get("PiMM");

    final Map<PiGraph, SynthesisResult> localSyntheses = (Map<PiGraph, SynthesisResult>) inputs.get("localSyntheses");

    final List<PiGraph> listClusters = algo.getClusters();

    // Retrieve the PAPIFY flag
    final boolean papify = "true".equalsIgnoreCase(parameters.get(LocalCodegenTask.PARAM_PAPIFY));

    for (final PiGraph cluster : listClusters) {
      final var original = PreesmCopyTracker.getOriginalSource(cluster);
      final SynthesisResult localSynthesisResults = localSyntheses.get(original);
      PreesmLogger.getLogger().info("\tLocal codegen of cluster " + original.getName());

      final ComponentInstance mapping = scenario.getPossibleMappings(cluster).getFirst();
      if (mapping.getComponent() instanceof final FPGA fpga) {
        FpgaCodeGenerator.generateFiles(scenario, fpga, (AnalysisResultFPGA) cluster.getSynthesisResult());
        // TODO : have the created CHeaderRefinement replace the PiSDF refinement of this cluster (including in the
        // mappings for example)
        cluster.addRefinement(buildClusterRefinement(cluster, scenario));
      } else {
        buildClusterCode(cluster, scenario, localSynthesisResults, archi);
      }
    }

    // Codegen shouldn't have an output, but this allows to make the local codegen a dependency of the global codegen
    final Map<String, Object> res = new LinkedHashMap<>();
    res.put("PiMM", algo);
    return res;
  }

  private void buildClusterCode(PiGraph cluster, Scenario scenario, SynthesisResult localSynthesis, Design archi) {
    // instead of passing the list of ordered actors for link and generateCode, we would pass the SOM
    final List<AbstractActor> totallyOrderedActors = new ScheduleOrderManager(cluster, localSynthesis.schedule)
        .buildScheduleAndTopologicalOrderedList();

    buildClusterRefinement(cluster, scenario);

    final Map<ComponentInstance, CoreBlock> coreBlocks = new LinkedHashMap<>();

    // 0- init blocks and order
    // I wanted to enable support for multi-mapping on cluster actors, but it does not work.
    // I'm leaving this for the nekt poor soul to need it, but clusterMappings should only contain 1 component.
    final var clusterMappings = scenario.getPossibleMappings(cluster);
    for (final ComponentInstance mapping : clusterMappings) {
      final CoreBlock cb = CodegenModelUserFactory.eINSTANCE.createCoreBlock(mapping,
          (CHeaderRefinement) cluster.getRefinement());
      coreBlocks.put(mapping, cb);
    }

    final List<Block> res = CodegenModelGenerator2.generateClusterCode(archi, cluster, scenario, localSynthesis, false,
        coreBlocks, totallyOrderedActors);

    PreesmLogger.getLogger().log(Level.INFO, "Printing blocks.");

    // Retrieve the desired printer and target folder path
    final String selectedPrinter = "C";
    final String codegenPath = scenario.getCodegenDirectory() + File.separator;

    // Create the codegen engine
    final CodegenEngine engine = new CodegenEngine(codegenPath, res, cluster, archi, scenario, true);

    if (CodegenTask2.VALUE_PRINTER_IR.equals(selectedPrinter)) {
      engine.initializePrinterIR(codegenPath);
    }

    // print .c cluster file
    engine.registerPrintersAndBlocks(selectedPrinter);
    engine.preprocessPrinters();
    engine.print();

    // print .h cluster file
    final StringBuilder hcontent = buildClusterHContent(cluster);
    final IFile iFile = PreesmIOHelper.getInstance().print(codegenPath, cluster.getName() + ".h", hcontent);
    CodeFormatterAndPrinter.format(iFile);

  }

  private CHeaderRefinement buildClusterRefinement(PiGraph cluster, Scenario scenario) {

    // 1 : extract function's arguments
    final CHeaderRefinement clusterHeader = PiMMFactory.createCHeaderRefinement();
    clusterHeader.setFilePath(scenario.getCodegenDirectory() + "/" + cluster.getName() + ".h");

    final FunctionPrototype prototype = PiMMFactory.createFunctionPrototype();
    prototype.setName(cluster.getName() + "_loop");
    clusterHeader.setLoopPrototype(prototype);

    final List<org.preesm.model.pisdf.Port> clusterInputsOutputs = new ArrayList<>();
    clusterInputsOutputs.addAll(cluster.getConfigInputPorts());
    clusterInputsOutputs.addAll(cluster.getAllDataPorts());

    final FunctionArgument[] args = new FunctionArgument[clusterInputsOutputs.size()];

    for (int i = 0; i < clusterInputsOutputs.size(); i++) {
      final org.preesm.model.pisdf.Port port = clusterInputsOutputs.get(i);

      args[i] = PiMMFactory.createFunctionArgument();

      args[i].setDirection(port instanceof DataOutputPort ? Direction.OUT : Direction.IN);
      args[i].setIsConfigurationParameter(port instanceof ConfigInputPort);
      args[i].setIsPassedByReference(port instanceof DataPort);
      args[i].setName(clusterInputsOutputs.get(i).getName());

      // pas sûr à 100% que ça couvre tous les cas mais ça devrait le faire
      if (port instanceof ConfigInputPort) {
        args[i].setType("int");
      } else if (port instanceof final DataInputPort dip) {
        args[i].setType(dip.getFifo().getType());
      } else if (port instanceof final DataOutputPort dop) {
        args[i].setType(dop.getFifo().getType());
      } else {
        throw new PreesmRuntimeException(
            "Port" + port.getName() + " is neither config nor data input/output, I don't know how to process it !");
      }

      param2Interface.put(args[i], port);

      // arg.setIsCPPdefinition(CLUSTERIZE); // je laisse à la valeur par défaut, qui est false
      args[i].setPosition(i);

    }

    prototype.getArguments().addAll(Arrays.asList(args));

    cluster.addRefinement(clusterHeader);

    return clusterHeader;
  }

  private StringBuilder buildClusterHContent(PiGraph cluster) {
    final CHeaderRefinement refinement = (CHeaderRefinement) cluster.getRefinement();
    final StringBuilder Hcontent = fileHeader(cluster);

    final String upper = cluster.getName().toUpperCase() + "_H";
    Hcontent.append("#ifndef " + upper + "\n");
    Hcontent.append("#define " + upper + "\n");

    for (final AbstractActor actor : cluster.getOnlyActors()) {
      if (actor instanceof final Actor a && a.getRefinement() != null) {
        final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) actor).getRefinement());
        if (Hcontent.indexOf("#include \"" + cHeaderRefinement.getFileName()) == -1) {
          Hcontent.append("#include \"" + cHeaderRefinement.getFileName() + "\" \n\n");
        }
      }
    }
    // Hcontent.append(refinement.getInitPrototype() + ";\n"); // pour plus tard
    Hcontent.append(refinement.printLoopSignature() + ";\n"); // loopFunctionSignature(cluster, refinement)

    Hcontent.append("#endif \n");

    return Hcontent;
  }

  /**
   * Translate the subgraph into string C function declaration.
   *
   * @param cluster
   *          Cluster to consider.
   * @return The string content of the loopFunction.
   */
  private String loopFunctionSignature(PiGraph cluster, CHeaderRefinement refinement) {
    final StringBuilder funcLoop = new StringBuilder();

    funcLoop.append("void " + cluster.getName() + "(");

    final int nbArg = refinement.getLoopPrototype().getArguments().size();

    if (nbArg == 0) {
      funcLoop.append(")");
      return funcLoop.toString();
    }

    for (final var arg : refinement.getLoopPrototype().getArguments()) {
      final String type = arg.isIsPassedByReference() ? arg.getType() + "* " : arg.getType();
      funcLoop.append(type + " " + arg.getName() + ",");
    }

    // Removing trailing comma
    funcLoop.deleteCharAt(funcLoop.length() - 1);

    funcLoop.append(")");
    return funcLoop.toString();
  }

  private StringBuilder generateCalls(PiGraph cluster) {
    final StringBuilder result = new StringBuilder();

    for (final Actor actor : cluster.getActorsWithRefinement()) {
      // we need to find the actor's header, find its arguments, find the correspondance between the data arguments and
      // the buffers and the correspondance between the config arguments and the cluster's config arguments

      final CHeaderRefinement actorRefinement = (CHeaderRefinement) actor.getRefinement();
      final List<FunctionArgument> actorInputs = actorRefinement.getLoopPrototype().getArguments();

      // 1 : write the function call
      result.append(PreesmCopyTracker.getOriginalSource(actor).getName() + "(");

      // 2 : loop through arguments. For each of them, find what it is linked to in the graph : a config port, an
      // actor's data port, or a cluster's data port

      for (final FunctionArgument arg : actorInputs) {
        final var originalPort = param2Interface.get(arg);

        switch (originalPort) {
          case final DataInputPort dip:
            // first : figure out if this port is linked to another actor, or the cluster's interfaces
            if (dip.getContainingActor() == cluster) {
              // we need to find what name this argument has in the cluster's signature
              final String clusterArgumentName = dip.getOppositePort().getName();

              result.append(clusterArgumentName + ", ");
            } else {
              // we need to find the appropriate buffer
              final String buffName = fifo2buffName.get(dip.getFifo());
              result.append(buffName + ", ");
            }

            break;
          case final DataOutputPort dop:
            break;
          case final ConfigInputPort cip:
            break;
          default:
            throw new PreesmRuntimeException("no idea how to process this port :" + originalPort.getName());
        }
      }

    }

    return result;
  }

  private StringBuilder generateBuffers(PiGraph cluster, Long stackSize) {
    Long count = 0L;

    final StringBuilder result = new StringBuilder();

    final Map<AbstractVertex, Long> brv = PiBRV.compute(cluster, BRVMethod.LCM);

    for (final Fifo f : cluster.getFifos()) {
      String buffer;
      final AbstractActor sourceActor = f.getSource();
      final DataOutputPort sourcePort = f.getSourcePort();
      if (f.getSource() instanceof DataInterface || f.getTarget() instanceof DataInterface) {
        // we will have to connect the parameters of the cluster's function to this actor's function
      } else {
        final String buffName = sourceActor.getName() + "_" + sourcePort.getName() + "__" + f.getTarget().getName()
            + "_" + f.getTargetPort().getName();

        // we need to find : the buffer's type, its number of data tokens
        final String type = f.getType();
        final var nbTokens = f.getSourcePort().getExpression().evaluateAsLong()
            * brv.get(f.getSourcePort().getContainingActor());

        if (count < stackSize) {
          buffer = type + " " + buffName + "[" + nbTokens + "];\n";
        } else {
          buffer = type + "* " + buffName + " =" + "(" + type + "*) malloc(" + nbTokens + " * sizeof(" + type + "));\n";
        }

        fifo2buffName.put(f, buffName);

        count += nbTokens;
        result.append(buffer);
      }
    }

    return result;
  }

  /**
   * The header file contains file information.
   *
   * @param cluster
   *          Cluster to consider.
   * @return The string content of the header file.
   */
  private StringBuilder fileHeader(PiGraph cluster) {
    final StringBuilder result = new StringBuilder();
    result.append("/**\n");
    final String nameGraph = cluster.getName();
    result.append("* @file /Cluster_" + nameGraph + "_" + nameGraph + ".c/h\n");
    result.append("* @generated by " + this.getClass().getSimpleName() + "\n");
    result.append("* @date " + new Date() + "\n");
    result.append("*/\n\n");
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
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
    avilableLanguages.append(LocalCodegenTask.VALUE_PRINTER_IR + "}");

    parameters.put(LocalCodegenTask.PARAM_PRINTER, avilableLanguages.toString());
    // Papify default
    parameters.put(LocalCodegenTask.PARAM_PAPIFY, "false");
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Generate xtend code";
  }

}

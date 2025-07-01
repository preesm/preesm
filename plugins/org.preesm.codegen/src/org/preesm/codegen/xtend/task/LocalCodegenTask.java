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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.generator2.AllocationToCodegenBuffer;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * The Class CodegenTask.
 */
@PreesmTask(id = "localcodegen", name = "Local Codegen", category = "Local Codegen",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class), @Port(name = "localSyntheses", type = Map.class), },
    outputs = { @Port(name = "PiMM", type = PiGraph.class) },

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
                    + " of PAPIFY tab in the scenario. Currently compatibe with x86 and MPPA-256") }) })
public class LocalCodegenTask extends AbstractTaskImplementation {

  /** The Constant PARAM_PRINTER. */
  public static final String PARAM_PRINTER = "Printer";

  /** The Constant VALUE_PRINTER_IR. */
  public static final String VALUE_PRINTER_IR = "IR";

  /** The Constant PARAM_PAPIFY. */
  public static final String PARAM_PAPIFY = "Papify";

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

    // Retrieve inputs
    final Scenario scenario = (Scenario) inputs.get("scenario");
    if (scenario.getCodegenDirectory() == null) {
      throw new PreesmRuntimeException("Codegen path has not been specified in scenario, cannot go further.");
    }

    final Design archi = (Design) inputs.get("architecture");
    final PiGraph algo = (PiGraph) inputs.get("PiMM");

    final Map<PiGraph, Pair<Actor,
        SynthesisResult>> localSyntheses = (Map<PiGraph, Pair<Actor, SynthesisResult>>) inputs.get("localSyntheses");

    // Retrieve the PAPIFY flag
    final boolean papify = "true".equalsIgnoreCase(parameters.get(LocalCodegenTask.PARAM_PAPIFY));

    for (final PiGraph cluster : localSyntheses.keySet()) {
      final Actor placeHolder = localSyntheses.get(cluster).getLeft();
      final SynthesisResult localSynthesisResults = localSyntheses.get(cluster).getRight();
      final Schedule schedule = localSynthesisResults.schedule;
      final Mapping mapping = localSynthesisResults.mapping;
      final Allocation memAlloc = localSynthesisResults.alloc;

      final CHeaderRefinement placeholderCode = PiMMFactory.createCHeaderRefinement();
      placeholderCode.setFilePath("org.ietr.preesm.sobel/Code/include/placeHolder.h");

      final Set<FunctionArgument> configArguments = new HashSet<>();
      final Set<String> configPortNames = new HashSet<>();
      final Set<FunctionArgument> innerFifos = new HashSet<>();
      final Set<FunctionArgument> interfaceFifos = new HashSet<>();

      // maps a sub-actor to :
      // an argument's local name (for example "nbSlice")
      // the configPort it is linked to (for example divideFactor)
      final Map<Actor, Pair<String, ConfigInputPort>> ActorToCipMap = new HashMap<>();

      for (final Actor a : cluster.getActorsWithRefinement()) {
        final CHeaderRefinement refinement = (CHeaderRefinement) a.getRefinement();
        final FunctionPrototype fp = refinement.getLoopPrototype();
        final List<FunctionArgument> arguments = fp.getArguments();

        // liste de paramètres de config
        a.getConfigInputPorts().stream().forEach(cip -> {
          final Pair<String,
              ConfigInputPort> NameAndCip = new ImmutablePair<String, ConfigInputPort>(cip.getName(), cip);
          ActorToCipMap.put(a, NameAndCip);
        });

        // liste de param de fifo internes
        arguments.stream().filter(arg -> !arg.isIsConfigurationParameter()).toList();

        // liste de param de fifo interfaces
        // localSynthesisResults.alloc.getFifoAllocations()
      }

      final FunctionPrototype prototype = PiMMFactory.createFunctionPrototype();
      prototype.setName("placeHolder");

      final List<org.preesm.model.pisdf.Port> clusterInputsOutputs = new ArrayList<>();
      clusterInputsOutputs.addAll(cluster.getConfigInputPorts());
      clusterInputsOutputs.addAll(cluster.getAllDataPorts());

      final FunctionArgument[] args = new FunctionArgument[clusterInputsOutputs.size()];
      final boolean[] isConfig = new boolean[clusterInputsOutputs.size()];
      final boolean[] reference = new boolean[clusterInputsOutputs.size()];
      final String[] names = new String[clusterInputsOutputs.size()];
      final String[] types = new String[clusterInputsOutputs.size()];
      final Direction[] directions = new Direction[clusterInputsOutputs.size()];

      for (int i = 0; i < clusterInputsOutputs.size(); i++) {
        final org.preesm.model.pisdf.Port port = clusterInputsOutputs.get(i);

        directions[i] = port instanceof DataOutputPort ? Direction.OUT : Direction.IN;
        isConfig[i] = port instanceof ConfigInputPort;
        reference[i] = port instanceof DataPort;
        names[i] = clusterInputsOutputs.get(i).getName();

        // pas sûr à 100% que ça couvre tous les cas mais ça devrait le faire
        if (port instanceof ConfigInputPort) {
          types[i] = "int";
        } else if (port instanceof DataInputPort) {
          // since we have replaced the cluster with a placeholder in the graph, its in/out fifos have been removed
          // therefore we need to retrieve the placeholder's corresponding port's fifo to find the data type
          final List<DataInputPort> correspondingNames = placeHolder.getDataInputPorts().stream()
              .filter(dp -> dp.getName().equals(port.getName())).toList();

          if (correspondingNames.isEmpty()) {
            throw new PreesmRuntimeException("No placeHolder port corresponds to port name " + port.getName());
          }
          if (correspondingNames.size() > 1) {
            throw new PreesmRuntimeException("Several cluster ports have the name " + port.getName() + " !");
          }
          types[i] = correspondingNames.getFirst().getFifo().getType();

        } else if (port instanceof DataOutputPort) {
          // since we have replaced the cluster with a placeholder in the graph, its in/out fifos have been removed
          // therefore we need to retrieve the placeholder's corresponding port's fifo to find the data type
          final List<DataOutputPort> correspondingNames = placeHolder.getDataOutputPorts().stream()
              .filter(dp -> dp.getName().equals(port.getName())).toList();

          if (correspondingNames.isEmpty()) {
            throw new PreesmRuntimeException("No placeHolder port corresponds to port name " + port.getName());
          }
          if (correspondingNames.size() > 1) {
            throw new PreesmRuntimeException("Several cluster ports have the name " + port.getName() + " !");
          }
          types[i] = correspondingNames.getFirst().getFifo().getType();
        }

      }

      for (int i = 0; i < 6; i++) {
        args[i] = PiMMFactory.createFunctionArgument();
        args[i].setDirection(directions[i]);
        args[i].setIsConfigurationParameter(isConfig[i]);
        // arg.setIsCPPdefinition(CLUSTERIZE); // je laisse à la valeur par défaut, qui est false
        args[i].setIsPassedByReference(reference[i]);
        args[i].setName(names[i]);
        args[i].setPosition(i);
        args[i].setType(types[i]);
      }

      prototype.getArguments().addAll(Arrays.asList(args));
      placeholderCode.setLoopPrototype(prototype);

      placeHolder.setRefinement(placeholderCode);

      final Map<ComponentInstance, CoreBlock> coreBlocks = new LinkedHashMap<>();
      // we assume a cluster is mapped to a single accelerator (PE)
      final var PEInstance = scenario.getPossibleMappings(placeHolder).getFirst();
      coreBlocks.put(PEInstance, CodegenModelUserFactory.eINSTANCE.createCoreBlock(PEInstance));

      // instead of passing the list of ordered actors for link and generateCode, we would pass the SOM
      final List<AbstractActor> totallyOrderedActors = new ScheduleOrderManager(algo, schedule)
          .buildScheduleAndTopologicalOrderedList();

      // 1- generate variables (and keep track of them with a linker)
      final var memoryLinker = AllocationToCodegenBuffer.link(memAlloc, scenario, algo, totallyOrderedActors);

      // 2- generate code
      generateCode(coreBlocks, totallyOrderedActors);
    }

    // Codegen shouldn't have an output, but this allows to make the local codegen a dependency of the global codegen
    final Map<String, Object> res = new LinkedHashMap<>();
    res.put("PiMM", algo);
    return res;
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

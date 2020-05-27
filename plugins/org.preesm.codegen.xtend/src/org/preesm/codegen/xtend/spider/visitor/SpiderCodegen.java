/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2017)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2015 - 2017)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2017)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2015)
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
package org.preesm.codegen.xtend.spider.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.preesm.codegen.xtend.spider.SpiderMainFilePrinter;
import org.preesm.codegen.xtend.spider.utils.SpiderConfig;
import org.preesm.codegen.xtend.spider.utils.SpiderNameGenerator;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.scenario.PapiComponent;
import org.preesm.model.scenario.PapiEvent;
import org.preesm.model.scenario.PapifyConfig;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * The Class SpiderCodegen.
 */
public class SpiderCodegen {

  /** The scenario. */
  private final Scenario scenario;

  /** The architecture */
  final Design architecture;

  /** The cpp string. */
  StringBuilder cppString = new StringBuilder();

  /**
   * Append.
   *
   * @param a
   *          the a
   */
  // Shortcut for cppString.append()
  private void append(final Object a) {
    this.cppString.append(a);
  }

  /** The core types ids. */
  /* Map core types to core type indexes */
  private Map<Component, Integer>                  coreTypesIds;
  private Map<Component, Integer>                  coresPerCoreType;
  private Map<Component, EList<ComponentInstance>> coresFromCoreType;

  /** The core ids. */
  private Map<ComponentInstance, Integer> coreIds;

  /** The preprocessor. */
  private SpiderPreProcessVisitor preprocessor;

  /** The timings. */
  /* Map timing strings to actors */
  private Map<AbstractActor, Map<Component, String>> timings;

  /** The energies. */
  /* Map energy doubles to actors */
  private Map<AbstractActor, Map<Component, Double>> energies;

  /** The function map. */
  /* Map functions to function ix */
  private Map<AbstractActor, Integer> functionMap;

  /** The port map. */
  /* Map Port to its description */
  private Map<Port, Integer> portMap;

  /** The constraints. */
  private Map<AbstractActor, Set<ComponentInstance>> constraints;

  /** **/
  private final List<String> coreTypeName = new LinkedList<>();

  /** **/
  private final List<Parameter> dynamicParams = new LinkedList<>();

  /**
   * Instantiates a new spider codegen.
   *
   * @param scenario
   *          the scenario
   */
  public SpiderCodegen(final Scenario scenario, final Design architecture) {
    this.scenario = scenario;
    this.architecture = architecture;
  }

  /**
   * Inits the generator.
   *
   * @param pg
   *          the pg
   */
  public void initGenerator(final PiGraph pg) {
    /* Preprocessor visitor */
    /* Initialize functions, dataports and dependency maps */
    this.preprocessor = new SpiderPreProcessVisitor();
    this.preprocessor.doSwitch(pg);

    this.portMap = this.preprocessor.getPortMap();
    this.functionMap = this.preprocessor.getFunctionMap();

    this.coreTypesIds = new LinkedHashMap<>();
    this.coresPerCoreType = new LinkedHashMap<>();
    this.coresFromCoreType = new LinkedHashMap<>();
    int coreTypeId = 0;
    final Design design = this.scenario.getDesign();
    for (final Component coreType : design.getOperatorComponents()) {
      this.coreTypesIds.put(coreType, coreTypeId++);
      // Link the number of cores associated to each core type
      final EList<Component> components = this.architecture.getComponentHolder().getComponents();
      for (final Component c : components) {
        if (c.equals(coreType)) {
          final EList<ComponentInstance> instances = c.getInstances();
          this.coresPerCoreType.put(coreType, instances.size());
          this.coresFromCoreType.put(coreType, instances);
        }
      }
    }

    this.coreIds = new LinkedHashMap<>();
    ComponentInstance mainOperator = this.scenario.getSimulationInfo().getMainOperator();
    final List<ComponentInstance> orderedOperators = design.getOrderedOperatorComponentInstances();
    if (mainOperator == null) {
      /* Warning */
      PreesmLogger.getLogger().log(Level.WARNING,
          () -> "No Main Operator selected in scenario, " + orderedOperators.get(0) + " used by default");
    }
    this.coreIds.put(mainOperator, 0);
    int coreId = 1;
    for (final ComponentInstance core : orderedOperators) {
      if (!core.equals(mainOperator)) {
        this.coreIds.put(core, coreId++);
      }
    }

    // Generate timings
    this.timings = new LinkedHashMap<>();
    final Map<String, AbstractActor> actorsByNames = this.preprocessor.getActorNames();
    for (final AbstractActor actor : actorsByNames.values()) {
      if (actor != null) {
        if (!this.timings.containsKey(actor)) {
          this.timings.put(actor, new LinkedHashMap<Component, String>());
        }
        if (this.scenario.getTimings().getActorTimings().containsKey(actor)) {
          final EMap<Component, String> listTimings = this.scenario.getTimings().getActorTimings().get(actor);
          for (Entry<Component, String> e : listTimings) {
            this.timings.get(actor).put(e.getKey(), e.getValue());
          }
        }
      } else {
        throw new PreesmRuntimeException();
      }
    }

    // Generate energies
    this.energies = new LinkedHashMap<>();
    for (final AbstractActor actor : actorsByNames.values()) {
      if (actor != null) {
        if (!this.energies.containsKey(actor)) {
          this.energies.put(actor, new LinkedHashMap<Component, Double>());
        }
        if (this.scenario.getEnergyConfig().getAlgorithmEnergy().containsKey(actor)) {
          final EMap<Component, String> listEnergies = this.scenario.getEnergyConfig().getAlgorithmEnergy().get(actor);
          for (Component cp : listEnergies.keySet()) {
            this.energies.get(actor).put(cp,
                (double) this.scenario.getEnergyConfig().evaluateEnergyActorOrDefault(actor, cp));
          }
        }
      } else {
        throw new PreesmRuntimeException();
      }
    }

    // Generate constraints
    this.constraints = new LinkedHashMap<>();
    for (final Entry<ComponentInstance, EList<AbstractActor>> cg : this.scenario.getConstraints()
        .getGroupConstraints()) {
      for (final AbstractActor aa : cg.getValue()) {
        if (this.constraints.get(aa) == null) {
          this.constraints.put(aa, new LinkedHashSet<ComponentInstance>());
        }
        final ComponentInstance core = cg.getKey();
        this.constraints.get(aa).add(core);
      }
    }

    // Add Default timings if needed
    for (final AbstractActor aa : actorsByNames.values()) {
      if (!this.timings.containsKey(aa)) {
        this.timings.put(aa, new LinkedHashMap<Component, String>());
      }
      for (final Component coreType : this.coreTypesIds.keySet()) {
        if (!this.timings.get(aa).containsKey(coreType)) {
          this.timings.get(aa).put(coreType, Integer.toString(ScenarioConstants.DEFAULT_TIMING_TASK.getValue()));
        }
      }
    }

    // Add Default energies if needed
    for (final AbstractActor aa : actorsByNames.values()) {
      if (!this.energies.containsKey(aa)) {
        this.energies.put(aa, new LinkedHashMap<Component, Double>());
      }
      for (final Component coreType : this.coreTypesIds.keySet()) {
        if (!this.energies.get(aa).containsKey(coreType)) {
          this.energies.get(aa).put(coreType, (double) ScenarioConstants.DEFAULT_ENERGY_TASK.getValue());
        }
      }
    }
  }

  final AbstractActor lookupTimingRec(final PiGraph graph, final String vertexName) {
    AbstractActor actor = (AbstractActor) graph.lookupVertex(vertexName);
    if (actor == null) {
      for (final PiGraph g : graph.getChildrenGraphs()) {
        actor = lookupTimingRec(g, vertexName);
        if (actor != null) {
          break;
        }
      }
    }
    return actor;
  }

  /**
   * Generate header code.
   *
   * @param pg
   *          the pg
   * @return the string
   */
  public String generateHeaderCode(final PiGraph pg, final SpiderConfig spiderConfig) {
    this.cppString.setLength(0);

    /* Put license */
    append(getLicense());

    /* Add Include Protection */
    append("#ifndef " + pg.getName().toUpperCase() + "_H\n");
    append("#define " + pg.getName().toUpperCase() + "_H\n\n");

    /* Declare Include Files */
    append("#include <spider.h>\n\n");

    append("#define N_PE_TYPE " + Integer.toString(this.coreTypesIds.keySet().size()) + "\n");
    for (final Component coreType : this.coreTypesIds.keySet()) {
      final String name = "N_" + SpiderNameGenerator.getCoreTypeName(coreType);
      this.coreTypeName.add(name);
      append("#define " + name + " " + Integer.toString(this.coresPerCoreType.get(coreType)) + "\n");
    }
    append("#define N_PE (" + String.join(" + ", this.coreTypeName) + ")\n");
    append("#define SH_MEM_SIZE (" + String.format("0x%08X", spiderConfig.getSharedMemorySize()) + ")\n\n");

    append("void initArchi();\n");
    append("void freeArchi();\n");
    append("\n");

    /* Declare the addGraph method */
    append("#define N_FCT_" + pg.getName().toUpperCase() + " " + this.functionMap.size() + "\n");
    append("extern lrtFct " + pg.getName() + "_fcts[N_FCT_" + pg.getName().toUpperCase() + "];\n");
    append("\n");

    /* Declare Fcts */
    append("void init_" + pg.getName() + "(");
    final List<Parameter> l = new LinkedList<>();
    l.addAll(pg.getParameters());
    Collections.sort(l, (p1, p2) -> p1.getName().compareTo(p2.getName()));
    final StringBuilder parametersProto = new StringBuilder();
    for (final Parameter p : l) {
      if (p.isLocallyStatic() && !p.isDependent() && !p.isConfigurationInterface()) {
        if (parametersProto.length() > 0) {
          parametersProto.append(", ");
        }
        parametersProto.append("Param " + p.getName() + " = " + p.getValueExpression().evaluate());
      }
    }
    append(parametersProto);
    append(");\n");

    append("void free_" + pg.getName() + "();\n");
    append("std::map<lrtFct, std::map<const char *, PapifyConfig*>> get_" + pg.getName() + "_papifyConfigs();\n");
    append("void free_" + pg.getName()
        + "_papifyConfigs(std::map<lrtFct, std::map<const char *, PapifyConfig*>>& map);\n");
    append(
        "std::map<lrtFct, std::map<const char *, std::map<int, double>>> get_" + pg.getName() + "_energyModels();\n");
    append("\n");

    /* Core */
    append("enum class PEVirtID : std::uint32_t {\n");
    final List<ComponentInstance> sortedCores = new ArrayList<>(this.coreIds.keySet());
    Collections.sort(sortedCores, (c1, c2) -> c1.getInstanceName().compareTo(c2.getInstanceName()));
    for (int i = 0; i < this.coreIds.size(); i++) {
      for (final Entry<ComponentInstance, Integer> entry : this.coreIds.entrySet()) {
        if (entry.getValue() == i) {
          final ComponentInstance core = entry.getKey();
          append("\t" + SpiderNameGenerator.getCoreName(core) + " = " + this.coreIds.get(core) + ",\n");
        }
      }
    }
    append("}; \n\n");

    /* Hardware ID */
    append("enum class PEHardwareID : std::uint32_t {\n");
    for (int i = 0; i < this.coreIds.size(); i++) {
      for (final Entry<ComponentInstance, Integer> entry : this.coreIds.entrySet()) {
        if (entry.getValue() == i) {
          final ComponentInstance core = entry.getKey();
          append("\t" + SpiderNameGenerator.getCoreName(core) + " = " + this.coreIds.get(core) + ",\n");
        }
      }
    }
    append("}; \n\n");

    /* Core Type */
    append("enum class PEType : std::uint32_t {\n");

    for (final Entry<Component, Integer> entry : this.coreTypesIds.entrySet()) {
      final Component coreType = entry.getKey();
      append("\t" + SpiderNameGenerator.getCoreTypeName(coreType) + " = " + entry.getValue() + ",\n");
    }
    append("};\n\n");

    /* Fct Ix */
    append("typedef enum{\n");
    for (final Entry<AbstractActor, Integer> entry : this.functionMap.entrySet()) {
      final AbstractActor aa = entry.getKey();
      final Integer integer = entry.getValue();
      append("\t" + SpiderNameGenerator.getFunctionName(aa).toUpperCase() + "_FCT" + " = " + integer + ",\n");
    }
    append("} FctIxs;\n\n");

    /* Close Include Protection */
    append("#endif//" + pg.getName().toUpperCase() + "_H\n");

    return this.cppString.toString();
  }

  public String generateMainCode(final PiGraph pg, final SpiderConfig spiderConfig) {
    final SpiderMainFilePrinter spiderMainFilePrinter = new SpiderMainFilePrinter();
    return spiderMainFilePrinter.print(pg, this.coreTypeName, spiderConfig);
  }

  /**
   * Main method, launching the generation for the whole PiGraph pg, including license, includes, constants and top
   * method generation.
   *
   * @param pg
   *          the pg
   * @return the string
   */
  public String generateGraphCode(final PiGraph pg) {
    this.cppString.setLength(0);

    final StringBuilder tmp = new StringBuilder();
    final SpiderCodegenVisitor codeGenerator = new SpiderCodegenVisitor(this, tmp, this.preprocessor, this.timings,
        this.constraints, this.scenario.getSimulationInfo().getDataTypes(), this.energies);
    // Generate C++ code for the whole PiGraph, at the end, tmp will contain
    // the vertex declaration for pg
    codeGenerator.doSwitch(pg);

    // /Generate the header (license, includes and constants)
    append(getLicense());
    append("#include \"" + pg.getName() + ".h\"\n\n");

    // Generate the prototypes for each method except top
    for (final String p : codeGenerator.getPrototypes()) {
      append(p);
    }
    append("\n");

    // Generate the top method from which the C++ graph building is launch
    topMehod(pg);

    // Concatenate the results
    for (final StringBuilder m : codeGenerator.getMethods()) {
      this.cppString.append(m);
    }
    // Returns the final C++ code
    return this.cppString.toString();
  }

  /**
   * Generate Papify configs for each actors
   *
   * @param pg
   *          The main graph
   * @param scenario
   *          Preesm scenario
   * @return the string
   */
  public String generatePapifyCode(final PiGraph pg, final Scenario scenario) {
    this.cppString.setLength(0);
    // Generate the header (license, includes and constants)
    append(getLicense());

    append("#include <vector>\n");
    append("#include <map>\n");
    append("#include <spider.h>\n");
    append("#include \"" + pg.getName() + ".h\"\n\n");
    // Papify pre-processing
    PapifyConfig papifyConfigManager = scenario.getPapifyConfig();

    final Map<EList<PapiEvent>, Integer> uniqueEventSets = new LinkedHashMap<>();
    int eventSetID = 0;

    final ArrayList<AbstractActor> papifiedActors = new ArrayList<>();

    for (final AbstractActor actor : this.functionMap.keySet()) {
      if (papifyConfigManager.hasPapifyConfig(actor)) {
        papifiedActors.add(actor);
        if (!generatePapifyConfig(papifyConfigManager, actor, uniqueEventSets, eventSetID)) {
          eventSetID++;
        }
      }
    }

    append("std::map<lrtFct, std::map<const char *, PapifyConfig*>> get_" + pg.getName() + "_papifyConfigs() {\n");
    append("\tstd::map<lrtFct, std::map<const char *, PapifyConfig*>> map;\n");
    append("\tstd::map<const char *, PapifyConfig*> mapPapifyConfigs;\n");
    append("\t// Initializing the map\n");
    for (final AbstractActor actor : papifiedActors) {
      append("\tmapPapifyConfigs = create_" + SpiderNameGenerator.getFunctionName(actor) + "_PapifyConfig();\n");
      append("\tif(!mapPapifyConfigs.empty()) {\n");
      append("\t\tmap.insert(std::make_pair(" + pg.getName() + "_fcts["
          + SpiderNameGenerator.getFunctionName(actor).toUpperCase() + "_FCT" + "], mapPapifyConfigs));\n");
      append("\t}\n");
    }
    append("\treturn map;\n");
    append("}\n\n");

    append("void free_" + pg.getName()
        + "_papifyConfigs(std::map<lrtFct, std::map<const char *, PapifyConfig*>>& map) {\n");
    append("\tstd::map<lrtFct, std::map<const char *, PapifyConfig*>>::iterator it;\n");
    append("\t// Freeing memory of the map \n");
    append("\tfor(it = map.begin(); it != map.end(); ++it) { \n");
    append("\t\tdelete it->second.begin()->second;\n");
    append("\t}\n");
    append("}\n");

    // Adding energy consumption models based on PAPIFY events
    generatePapifyEnergyModel(pg);

    // Returns the final C++ code
    return this.cppString.toString();
  }

  /**
   * Generate the information related to energy consumption estimation based on PAPIFY events
   */
  private void generatePapifyEnergyModel(final PiGraph pg) {
    final ArrayList<AbstractActor> actorsWithEnergyModel = new ArrayList<>();

    for (final AbstractActor actor : this.functionMap.keySet()) {
      if (generateActorComponentEnergyModel(actor)) {
        actorsWithEnergyModel.add(actor);
      }
    }
    append(
        "std::map<lrtFct, std::map<const char *, std::map<int, double>>> get_" + pg.getName() + "_energyModels() {\n");
    append("\tstd::map<lrtFct, std::map<const char *, std::map<int, double>>> map;\n");
    append("\tstd::map<const char *, std::map<int, double>> mapEnergyModels;\n");
    append("\t// Initializing the map\n");
    for (final AbstractActor actor : actorsWithEnergyModel) {
      append("\tmapEnergyModels = create_" + SpiderNameGenerator.getFunctionName(actor) + "_EnergyModel();\n");
      append("\tif(!mapEnergyModels.empty()) {\n");
      append("\t\tmap.insert(std::make_pair(" + pg.getName() + "_fcts["
          + SpiderNameGenerator.getFunctionName(actor).toUpperCase() + "_FCT" + "], mapEnergyModels));\n");
      append("\t}\n");
    }
    append("\treturn map;\n");
    append("}\n\n");

  }

  private boolean generateActorComponentEnergyModel(AbstractActor actor) {
    boolean canEstimateEnergy = false;
    PapifyConfig papifyConfigManager = this.scenario.getPapifyConfig();
    EMap<Component, EMap<PapiEvent, Double>> energyModels = papifyConfigManager.getPapifyEnergyKPIModels();
    Map<Component, String> componentsWithMode = new LinkedHashMap<>();
    if (papifyConfigManager.hasValidPapifyConfig() && papifyConfigManager.hasPapifyConfig(actor)) {
      for (Component coreType : this.coresFromCoreType.keySet()) {
        if (energyModels.containsKey(coreType)) {
          EList<PapiComponent> componentPAPIComponents = papifyConfigManager.getSupportedPapiComponents(coreType);
          for (PapiComponent singleComponent : componentPAPIComponents) {
            for (String compName : papifyConfigManager.getActorAssociatedPapiComponents(actor)) {
              if (singleComponent.getId().equalsIgnoreCase(compName)) {
                EMap<PapiEvent, Double> energyModel = energyModels.get(coreType);
                List<PapiEvent> actorEventsOnComponent = papifyConfigManager.getActorComponentEvents(actor, compName);
                Set<PapiEvent> actorEventsOnComponentAsSet = new LinkedHashSet<>(actorEventsOnComponent);
                if (actorEventsOnComponentAsSet.containsAll(energyModel.keySet())) {
                  componentsWithMode.put(coreType, compName);
                }
              }
            }
          }
        }
      }
      if (!componentsWithMode.isEmpty()) {
        addEnergyModelEntry(actor, componentsWithMode);
        canEstimateEnergy = true;
      }
    }
    return canEstimateEnergy;
  }

  private void addEnergyModelEntry(AbstractActor actor, Map<Component, String> componentsWithMode) {
    PapifyConfig papifyConfigManager = this.scenario.getPapifyConfig();
    EMap<Component, EMap<PapiEvent, Double>> energyModels = papifyConfigManager.getPapifyEnergyKPIModels();
    append("\nstatic std::map<const char *, std::map<int, double>> " + "create_"
        + SpiderNameGenerator.getFunctionName(actor) + "_EnergyModel() {\n");
    append("\t// Setting the EnergyModels for actor: " + SpiderNameGenerator.getFunctionName(actor) + "\n");
    append("\tstd::map<const char *, std::map<int, double>> energyModelsMap;\n");
    append("\tstd::map<int, double> columnToParamValueMap;\n");
    for (Entry<Component, String> componentToPrint : componentsWithMode.entrySet()) {
      append("\t// Inserting energy model for " + componentToPrint.getKey().getVlnv().getName() + "\n");
      EMap<PapiEvent, Double> energyModel = energyModels.get(componentToPrint.getKey());
      List<PapiEvent> actorEventsOnComponent = papifyConfigManager.getActorComponentEvents(actor,
          componentToPrint.getValue());
      for (PapiEvent singleEvent : energyModel.keySet()) {
        append("\tcolumnToParamValueMap.insert(std::make_pair("
            + ECollections.indexOf(actorEventsOnComponent, singleEvent, 0) + ", " + energyModel.get(singleEvent)
            + "));\n");
      }
      for (ComponentInstance compInst : this.coresFromCoreType.get(componentToPrint.getKey())) {
        append("\tenergyModelsMap.insert(std::make_pair(\"LRT_" + this.coreIds.get(compInst)
            + "\", columnToParamValueMap));\n");
      }
      append("\tcolumnToParamValueMap.clear();\n");
    }
    append("\treturn energyModelsMap;\n");
    append("}\n\n");
  }

  /**
   * Generate the static initialization functions
   *
   * @param actor
   *          Current actor being papified
   * @param uniqueEventSets
   *          Map of unique event set
   * @param eventSetID
   *          The current event set ID
   * @return true if the actor has the same event set as an existing one, false else
   */
  private boolean generatePapifyConfig(PapifyConfig papifyConfigManager, final AbstractActor actor,
      final Map<EList<PapiEvent>, Integer> uniqueEventSets, final Integer eventSetID) {

    boolean eventMonitoring = false;
    boolean timingMonitoring = false;

    final List<String> compNames = new ArrayList<>();
    final Map<String, EList<PapiEvent>> associatedEvents = new LinkedHashMap<>();

    final Map<String, Integer> configIdPerPapiComponent = new LinkedHashMap<>();

    // Build the Timing variable to be printed
    if (papifyConfigManager.isMonitoringTiming(actor)) {
      timingMonitoring = true;
    }
    // Build the event variables to be printed
    if (papifyConfigManager.isMonitoringEvents(actor)) {
      EList<String> actorCompsSupported = papifyConfigManager.getActorAssociatedPapiComponents(actor);
      for (String compName : actorCompsSupported) {
        associatedEvents.put(compName, papifyConfigManager.getActorComponentEvents(actor, compName));
        compNames.add(compName);
      }
      eventMonitoring = true;
    }

    // Check if this set of ID already exists
    Integer realEventSetID = eventSetID;
    boolean found = false;

    for (final Entry<String, EList<PapiEvent>> entry : associatedEvents.entrySet()) {
      final String compName = entry.getKey();
      found = false;
      EList<PapiEvent> eventSetChecking = entry.getValue();
      for (Map.Entry<EList<PapiEvent>, Integer> eventSet : uniqueEventSets.entrySet()) {
        final EList<PapiEvent> eventSetStored = eventSet.getKey();
        final Integer eventSetStoredID = eventSet.getValue();
        if (EcoreUtil.equals(eventSetStored, eventSetChecking)) {
          realEventSetID = eventSetStoredID;
          found = true;
          break;
        }
      }
      if (!found) {
        uniqueEventSets.put(eventSetChecking, realEventSetID);
      }
      configIdPerPapiComponent.put(compName, realEventSetID);
    }

    append("static std::map<const char *, PapifyConfig*> " + "create_" + SpiderNameGenerator.getFunctionName(actor)
        + "_PapifyConfig() {\n");
    append("\t// Setting the PapifyConfigs for actor: " + SpiderNameGenerator.getFunctionName(actor) + "\n");
    append("\tstd::map<const char *, PapifyConfig*> configMap;\n");
    for (String compNameGen : compNames) {
      append("\n\tPapifyConfig* config_" + compNameGen + "  = new PapifyConfig;\n");
      append("\tconfig_" + compNameGen + "->peID_            = \"\";\n");
      append("\tconfig_" + compNameGen + "->peType_          = \"" + compNameGen + "\";\n");
      append(
          "\tconfig_" + compNameGen + "->actorName_       = \"" + SpiderNameGenerator.getFunctionName(actor) + "\";\n");
      append("\tconfig_" + compNameGen + "->eventSize_       = "
          + Integer.toString(associatedEvents.get(compNameGen).size()) + ";\n");
      append("\tconfig_" + compNameGen + "->eventSetID_      = " + configIdPerPapiComponent.get(compNameGen).toString()
          + ";\n");
      final String timing = timingMonitoring ? "true" : "false";
      append("\tconfig_" + compNameGen + "->isTiming_        = " + timing + ";\n");
      if (eventMonitoring) {
        append("\tconfig_" + compNameGen + "->monitoredEvents_ = std::vector<const char*>("
            + Integer.toString(associatedEvents.get(compNameGen).size()) + ");\n");
        int i = 0;
        for (PapiEvent papiEvent : associatedEvents.get(compNameGen)) {
          append("\tconfig_" + compNameGen + "->monitoredEvents_[" + Integer.toString(i++) + "] = \""
              + papiEvent.getName() + "\";\n");
        }
      }
    }

    if (timingMonitoring) {
      append("\n\tPapifyConfig* config_Timing  = new PapifyConfig;\n");
      append("\tconfig_Timing->peID_            = \"\";\n");
      append("\tconfig_Timing->peType_          = \"\";\n");
      append("\tconfig_Timing->actorName_       = \"" + SpiderNameGenerator.getFunctionName(actor) + "\";\n");
      append("\tconfig_Timing->eventSize_       = 0;\n");
      append("\tconfig_Timing->eventSetID_      = 0;\n");
      final String timing = timingMonitoring ? "true" : "false";
      append("\tconfig_Timing->isTiming_        = " + timing + ";\n");
    }

    boolean configAssociated = false;
    append("\n\t// Mapping actor to LRT PAPIFY configuration: " + actor.getName() + "\n");

    for (final Entry<Component, EList<ComponentInstance>> entry : this.coresFromCoreType.entrySet()) {
      final Component coreType = entry.getKey();
      final EList<ComponentInstance> eList = entry.getValue();
      for (ComponentInstance compInst : eList) {
        configAssociated = false;
        final EList<PapiComponent> corePapifyConfigGroupPE = papifyConfigManager.getSupportedPapiComponents(coreType);
        for (final PapiComponent compType : corePapifyConfigGroupPE) {
          if (compNames.contains(compType.getId())) {
            configAssociated = true;
            append("\tconfigMap.insert(std::make_pair(\"LRT_" + this.coreIds.get(compInst) + "\", config_"
                + compType.getId() + "));\n");
          }
        }
        if (!configAssociated && timingMonitoring) {
          append("\tconfigMap.insert(std::make_pair(\"LRT_" + this.coreIds.get(compInst) + "\", config_Timing));\n");
        }
      }
    }
    append("\treturn configMap;\n");
    append("}\n\n");
    return found;
  }

  /**
   * Generate Papify configs for each actors
   *
   * @param pg
   *          The main graph
   * @param scenario
   *          Preesm scenario
   * @return the string
   */
  public String generateArchiCode(final PiGraph pg, final Scenario scenario) {
    this.cppString.setLength(0);
    // Generate the header (license, includes and constants)
    append(getLicense());

    append("#include <spider.h>\n");
    append("#include <stdlib.h>\n");
    append("#include <stdio.h>\n");
    append("#include <cstring>\n");
    append("#include \"" + pg.getName() + ".h\"\n\n");

    append("static char *shMemBuffer = nullptr;\n\n");

    append("void initArchi() {\n\n");
    append("\t/* === Init SpiderArchiConfig structure === */\n\n");
    append("\tSpiderArchiConfig config;\n");
    append("\tconfig.nPE = N_PE;\n");
    append("\tconfig.nPEType = N_PE_TYPE;\n");
    append("\tconfig.nMemoryUnit = 1;\n\n");
    append("\t/* === Create Archi === */\n\n");
    append("\tauto *archi = Spider::createArchi(config);\n\n");
    if (!this.scenario.getEnergyConfig().getPlatformPower().isEmpty()) {
      double basePower = this.scenario.getEnergyConfig().getPePowerOrDefault("Base");
      append("\t/* === Add base energy === */\n\n");
      append("\tSpider::setBasePower(" + basePower + ");\n\n");
    }
    append("\t/* === Create the different MemoryUnit(s) === */\n\n");
    append("\tshMemBuffer = (char *) std::malloc(SH_MEM_SIZE);\n");
    append("\tif (!shMemBuffer) {\n");
    append("\t\tfprintf(stderr, \"ERROR: failed to allocate [%X] bytes for MemoryUnit [%s]\\n\", "
        + "SH_MEM_SIZE, \"shared-memory\");\n");
    append("\t\texit(-1);\n");
    append("\t}\n");
    append("\tauto *shMem = Spider::createMemoryUnit(shMemBuffer, SH_MEM_SIZE);\n");
    append("\tmemset(shMemBuffer, 0, SH_MEM_SIZE);\n\n");
    append("\t/* === Create the different PE(s) === */\n");
    for (final Component coreType : this.coreTypesIds.keySet()) {
      final String localCoreTypeName = SpiderNameGenerator.getCoreTypeName(coreType);
      append("\n\t/* == " + localCoreTypeName + " == */");
      for (final ComponentInstance c : this.coresFromCoreType.get(coreType)) {
        final String coreName = SpiderNameGenerator.getCoreName(c.getInstanceName());
        final String peName = "pe" + coreType.getVlnv().getName().toUpperCase() + c.getInstanceName();
        append("\n\tauto *" + peName + " = Spider::createPE(\n" + "\t\tstatic_cast<std::uint32_t>(PEType::"
            + localCoreTypeName + "),\n" + "\t\tstatic_cast<std::uint32_t>(PEHardwareID::" + coreName + "),\n"
            + "\t\tstatic_cast<std::uint32_t>(PEVirtID::" + coreName + "),\n" + "\t\t\"" + coreType + "-"
            + c.getInstanceName() + "\",\n" + "\t\tSpiderPEType::LRT_PE,\n" + "\t\tSpiderHWType::PHYS_PE);\n");
        append("\tSpider::setPEMemoryUnit(" + peName + ", shMem);\n");
        if (!this.scenario.getEnergyConfig().getPlatformPower().isEmpty()) {
          double pePower = this.scenario.getEnergyConfig().getPePowerOrDefault(coreType.getVlnv().getName());
          append("\tSpider::setPEPower(" + peName + ", " + pePower + ");\n");
        }
      }
      append("\n\t/* === Set Spider GRT core === */\n\n");
      append("\tSpider::setSpiderGRTVirtualID(archi, static_cast<std::uint32_t>(PEVirtID::"
          + SpiderNameGenerator.getCoreName(scenario.getSimulationInfo().getMainOperator()) + "));\n");
    }
    append("}\n\n");
    append("void freeArchi() {\n\n");
    append("\t/* === Freeing memory buffer allocated for MemoryUnit(s) === */\n\n");
    append("\tif (shMemBuffer) {\n");
    append("\t\tstd::free(shMemBuffer);\n");
    append("\t\tshMemBuffer = nullptr;\n");
    append("\t}\n");
    append("}\n");
    // Returns the final C++ code
    return this.cppString.toString();
  }

  /**
   * Main method, launching the generation for the whole PiGraph pg, including license, includes, constants and top
   * method generation.
   *
   * @param pg
   *          the pg
   * @return the string
   */
  public String generateFunctionCode(final PiGraph pg) {
    this.cppString.setLength(0);

    // /Generate the header (license, includes and constants)
    append(getLicense());

    append("#include <spider.h>\n");
    append("#include \"" + pg.getName() + ".h\"\n\n");

    final Set<String> includeList = new LinkedHashSet<>();
    for (final AbstractActor aa : this.functionMap.keySet()) {
      final Actor a = (Actor) aa;
      if (a.getRefinement() instanceof CHeaderRefinement && !includeList.contains(a.getRefinement().getFileName())) {
        includeList.add(a.getRefinement().getFileName());
      }
    }

    for (final String file : includeList) {
      append("#include \"" + file + "\"\n");
    }

    append("\n");

    /* Generate prototypes */
    for (final AbstractActor aa : this.functionMap.keySet()) {
      append("void ");
      append(SpiderNameGenerator.getFunctionName(aa));
      append("(void* inputFIFOs[], void* outputFIFOs[], Param inParams[], Param outParams[]);\n");
    }
    append("\n");

    /* Generate LrtFct */
    append("lrtFct " + pg.getName() + "_fcts[N_FCT_" + pg.getName().toUpperCase() + "] = {\n");
    for (final AbstractActor aa : this.functionMap.keySet()) {
      append("\t&" + SpiderNameGenerator.getFunctionName(aa) + ",\n");
    }
    append("};\n\n");

    for (Parameter parameter : pg.getAllParameters()) {
      if (parameter.isConfigurable() || parameter.isDependent()) {
        this.dynamicParams.add(parameter);
      }
    }
    // Generate functions
    for (final AbstractActor aa : this.functionMap.keySet()) {
      generateFunctionBody(aa);
    }

    // Returns the final C++ code
    return this.cppString.toString();
  }

  /**
   * Generate the top method, responsible for building the whole C++ PiGraph corresponding to pg.
   *
   * @param pg
   *          the pg
   */
  private void topMehod(final PiGraph pg) {
    final String sgName = pg.getName();

    append("/**\n");
    append(" * This is the method you need to call to build a complete PiSDF graph.\n");
    append(" */\n");

    // The method does not return anything and is named top
    append("void init_" + pg.getName() + "(");

    final StringBuilder params = new StringBuilder();
    final List<Parameter> l = new LinkedList<>();
    l.addAll(pg.getParameters());
    Collections.sort(l, (p1, p2) -> p1.getName().compareTo(p2.getName()));
    final StringBuilder parametersProto = new StringBuilder();
    for (final Parameter p : l) {
      if (p.isLocallyStatic() && !p.isDependent() && !p.isConfigurationInterface()) {
        if (parametersProto.length() > 0) {
          parametersProto.append(", ");
          params.append(", ");
        }
        parametersProto.append("Param " + p.getName());
        params.append(p.getName());
      }
    }
    append(parametersProto);
    append("){\n");

    // Create a top graph and a top vertex
    append("\tPiSDFGraph* topGraph = Spider::createGraph(\n" + "\t\t/*Edges*/    0,\n" + "\t\t/*Params*/   0,\n"
        + "\t\t/*InputIf*/  0,\n" + "\t\t/*OutputIf*/ 0,\n" + "\t\t/*Config*/   0,\n" + "\t\t/*Body*/     1);\n\n");

    append("\tPiSDFVertex* topVertex");
    append(" = Spider::addBodyVertex(\n");
    append("\t\t/*Graph*/   topGraph,\n");
    append("\t\t/*Name*/    \"top\",\n");
    append("\t\t/*FctId*/   -1,\n");
    append("\t\t/*InData*/   0,\n");
    append("\t\t/*OutData*/  0,\n");
    append("\t\t/*InParam*/  0);\n");

    append("\n\t/* Top graph definition */\n");
    if (params.toString().equals("")) {
      append("\t" + sgName + "(topVertex);\n");
    } else {
      append("\t" + sgName + "(topVertex, " + params.toString() + ");\n");
    }

    append("\n\t/* Setting main application graph */\n");
    append("\tSpider::setGraph(topGraph);\n");
    append("}\n");
  }

  /**
   * Generate function body.
   *
   * @param aa
   *          the aa
   */
  private void generateFunctionBody(final AbstractActor aa) {
    append("void ");
    append(SpiderNameGenerator.getFunctionName(aa));
    append("(void* inputFIFOs[], void* outputFIFOs[], Param inParams[], Param outParams[]){\n");

    final Actor a = (Actor) aa;
    if (a.getRefinement() instanceof CHeaderRefinement) {
      final CHeaderRefinement href = (CHeaderRefinement) a.getRefinement();
      final FunctionPrototype proto = href.getLoopPrototype();

      append("\t" + proto.getName() + "(\n");
      int maxParamSize = 0;
      for (final FunctionArgument param : proto.getArguments()) {
        maxParamSize = Math.max(maxParamSize, param.getName().length());
      }

      boolean first = true;
      for (final FunctionArgument param : proto.getArguments()) {
        if (first) {
          first = false;
        } else {
          append(",\n");
        }
        boolean found = false;
        switch (param.getDirection()) {
          case IN:
            if (param.isIsConfigurationParameter()) {
              for (final Port port : a.getConfigInputPorts()) {
                if (port.getName().equals(param.getName())) {
                  append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName())
                      + " */ (Param) inParams[" + this.portMap.get(port) + "]");
                  found = true;
                }
              }
            } else {
              for (final Port port : a.getDataInputPorts()) {
                if (port.getName().equals(param.getName())) {
                  append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName()) + " */ ("
                      + param.getType() + "*) inputFIFOs[" + this.portMap.get(port) + "]");
                  found = true;
                }
              }
            }
            break;
          case OUT:
            if (param.isIsConfigurationParameter()) {
              for (final Port port : a.getConfigOutputPorts()) {
                if (port.getName().equals(param.getName())) {
                  append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName())
                      + " */ (Param*) &outParams[" + this.portMap.get(port) + "]");
                  found = true;
                }
              }
            } else {
              for (final Port port : a.getDataOutputPorts()) {
                if (port.getName().equals(param.getName())) {
                  append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName()) + " */ ("
                      + param.getType() + "*) outputFIFOs[" + this.portMap.get(port) + "]");
                  found = true;
                }
              }
            }
            break;
          default:
        }
        if (!found) {
          PreesmLogger.getLogger().warning("Port " + param.getName() + " in Actor " + a.getName() + " not found.");
        }
      }
      append("\n\t);\n");
    }
    append("}\n\n");
  }

  /**
   * License for PREESM.
   *
   * @return the license
   */
  public String getLicense() {
    return "/**\n" + " * *****************************************************************************\n"
        + " * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,\n"
        + " * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas\n" + " *\n"
        + " * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr\n" + " *\n"
        + " * This software is a computer program whose purpose is to prototype\n" + " * parallel applications.\n"
        + " *\n" + " * This software is governed by the CeCILL-C license under French law and\n"
        + " * abiding by the rules of distribution of free software.  You can  use,\n"
        + " * modify and/ or redistribute the software under the terms of the CeCILL-C\n"
        + " * license as circulated by CEA, CNRS and INRIA at the following URL\n" + " * \"http://www.cecill.info\".\n"
        + " *\n" + " * As a counterpart to the access to the source code and  rights to copy,\n"
        + " * modify and redistribute granted by the license, users are provided only\n"
        + " * with a limited warranty  and the software's author,  the holder of the\n"
        + " * economic rights,  and the successive licensors  have only  limited\n" + " * liability.\n" + " *\n"
        + " * In this respect, the user's attention is drawn to the risks associated\n"
        + " * with loading,  using,  modifying and/or developing or reproducing the\n"
        + " * software by the user in light of its specific status of free software,\n"
        + " * that may mean  that it is complicated to manipulate,  and  that  also\n"
        + " * therefore means  that it is reserved for developers  and  experienced\n"
        + " * professionals having in-depth computer knowledge. Users are therefore\n"
        + " * encouraged to load and test the software's suitability as regards their\n"
        + " * requirements in conditions enabling the security of their systems and/or\n"
        + " * data to be ensured and,  more generally, to use and operate it in the\n"
        + " * same conditions as regards security.\n" + " *\n"
        + " * The fact that you are presently reading this means that you have had\n"
        + " * knowledge of the CeCILL-C license and that you accept its terms.\n"
        + " * ****************************************************************************\n" + " */\n\n";
  }

  /**
   * Gets the core types codes.
   *
   * @return the core types codes
   */
  public Map<Component, Integer> getCoreTypesCodes() {
    return this.coreTypesIds;
  }

  /**
   * @return the coreIds
   */
  protected Map<ComponentInstance, Integer> getCoreIds() {
    return this.coreIds;
  }
}

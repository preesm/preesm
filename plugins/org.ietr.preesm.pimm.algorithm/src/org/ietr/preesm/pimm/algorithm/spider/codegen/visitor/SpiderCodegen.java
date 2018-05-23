/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Hugo Miomandre <hugo.miomandre@insa-rennes.fr> (2017)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015 - 2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2017)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2015)
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
package org.ietr.preesm.pimm.algorithm.spider.codegen.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.papi.PapiComponent;
import org.ietr.preesm.core.scenario.papi.PapiEvent;
import org.ietr.preesm.core.scenario.papi.PapifyConfig;
import org.ietr.preesm.core.scenario.papi.PapifyConfigManager;
import org.ietr.preesm.experiment.model.expression.ExpressionEvaluator;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.util.ActorPath;
import org.ietr.preesm.pimm.algorithm.SpiderMainFilePrinter;
import org.ietr.preesm.pimm.algorithm.spider.codegen.utils.SpiderNameGenerator;

/**
 * The Class SpiderCodegen.
 */
public class SpiderCodegen {

  /** The scenario. */
  private final PreesmScenario scenario;

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
  private Map<String, Integer> coreTypesIds;

  /** The core ids. */
  private Map<String, Integer> coreIds;

  /** The preprocessor. */
  private SpiderPreProcessVisitor preprocessor;

  /** The timings. */
  /* Map timing strings to actors */
  private Map<AbstractActor, Map<String, String>> timings;

  /** The function map. */
  /* Map functions to function ix */
  private Map<AbstractActor, Integer> functionMap;

  /** The port map. */
  /* Map Port to its description */
  private Map<Port, Integer> portMap;

  /** The constraints. */
  private Map<AbstractActor, Set<String>> constraints;

  /**
   * Instantiates a new spider codegen.
   *
   * @param scenario
   *          the scenario
   */
  public SpiderCodegen(final PreesmScenario scenario) {
    this.scenario = scenario;
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
    int coreTypeId = 0;
    for (final String coreType : this.scenario.getOperatorDefinitionIds()) {
      this.coreTypesIds.put(coreType, coreTypeId++);
    }

    this.coreIds = new LinkedHashMap<>();
    String mainOperator = this.scenario.getSimulationManager().getMainOperatorName();
    if ((mainOperator == null) || mainOperator.equals("")) {
      /* Warning */
      mainOperator = this.scenario.getOrderedOperatorIds().get(0);
      WorkflowLogger.getLogger().warning("No Main Operator selected in scenario, " + mainOperator + " used by default");
    }
    this.coreIds.put(mainOperator, 0);
    int coreId = 1;
    for (final String core : this.scenario.getOrderedOperatorIds()) {
      if (!core.equals(mainOperator)) {
        this.coreIds.put(core, coreId++);
      }
    }

    // Generate timings
    final Map<String, AbstractActor> actorsByNames = this.preprocessor.getActorNames();
    this.timings = new LinkedHashMap<>();
    for (final Timing t : this.scenario.getTimingManager().getTimings()) {
      final String actorName = t.getVertexId();
      final AbstractActor aa = actorsByNames.get(actorName);
      if (aa != null) {
        if (!this.timings.containsKey(aa)) {
          this.timings.put(aa, new LinkedHashMap<String, String>());
        }
        this.timings.get(aa).put(t.getOperatorDefinitionId(), t.getStringValue());
      }
    }

    // Generate constraints
    this.constraints = new LinkedHashMap<>();
    for (final ConstraintGroup cg : this.scenario.getConstraintGroupManager().getConstraintGroups()) {
      for (final String actorPath : cg.getVertexPaths()) {
        final AbstractActor aa = ActorPath.lookup(pg, actorPath);
        if (this.constraints.get(aa) == null) {
          this.constraints.put(aa, new LinkedHashSet<String>());
        }
        for (final String core : cg.getOperatorIds()) {
          this.constraints.get(aa).add(core);
        }
      }
    }

    // Add Default timings if needed
    for (final AbstractActor aa : actorsByNames.values()) {
      if (!this.timings.containsKey(aa)) {
        this.timings.put(aa, new LinkedHashMap<String, String>());
      }
      for (final String coreType : this.coreTypesIds.keySet()) {
        if (!this.timings.get(aa).containsKey(coreType)) {
          this.timings.get(aa).put(coreType, "100");
        }
      }
    }
  }

  /**
   * Generate header code.
   *
   * @param pg
   *          the pg
   * @return the string
   */
  public String generateHeaderCode(final PiGraph pg) {
    this.cppString.setLength(0);

    /* Put license */
    append(getLicense());

    /* Add Include Protection */
    append("#ifndef " + pg.getName().toUpperCase() + "_H\n");
    append("#define " + pg.getName().toUpperCase() + "_H\n\n");

    /* Declare Include Files */
    append("#include <spider.h>\n\n");

    /* Declare the addGraph method */
    append("#define N_FCT_" + pg.getName().toUpperCase() + " " + this.functionMap.size() + "\n");
    append("extern lrtFct " + pg.getName() + "_fcts[N_FCT_" + pg.getName().toUpperCase() + "];\n");
    append("\n");

    /* Declare Fcts */
    append("void init_" + pg.getName() + "(");
    final List<Parameter> l = new LinkedList<>();
    l.addAll(pg.getAllParameters());
    Collections.sort(l, (p1, p2) -> p1.getName().compareTo(p2.getName()));
    final StringBuilder parameters_proto = new StringBuilder();
    for (final Parameter p : l) {
      if (p.isLocallyStatic() && !p.isDependent() && !p.isConfigurationInterface()) {
        if (parameters_proto.length() > 0) {
          parameters_proto.append(", ");
        }
        parameters_proto.append("Param " + p.getName() + " = " + ExpressionEvaluator.evaluate(p.getValueExpression()));
      }
    }
    append(parameters_proto);
    append(");\n");

    append("void free_" + pg.getName() + "();\n");
    append("std::map<lrtFct, PapifyConfig*> get_" + pg.getName() + "_papifyConfigs();\n");
    append("void free_" + pg.getName() + "_papifyConfigs(std::map<lrtFct, PapifyConfig*>& map);\n");
    append("\n");

    /* Core */
    append("typedef enum{\n");
    final List<String> sortedCores = new ArrayList<>(this.coreIds.keySet());
    Collections.sort(sortedCores);
    for (int i = 0; i < this.coreIds.size(); i++) {
      for (final Entry<String, Integer> entry : this.coreIds.entrySet()) {
        if (entry.getValue() == i) {
          final String core = entry.getKey();
          append("\t" + SpiderNameGenerator.getCoreName(core) + " = " + this.coreIds.get(core) + ",\n");
        }
      }
    }
    append("} PE;\n\n");

    /* Core Type */
    append("typedef enum{\n");
    for (final String coreType : this.coreTypesIds.keySet()) {
      append("\t" + SpiderNameGenerator.getCoreTypeName(coreType) + " = " + this.coreTypesIds.get(coreType) + ",\n");
    }
    append("} PEType;\n\n");

    /* Fct Ix */
    append("typedef enum{\n");
    for (final AbstractActor aa : this.functionMap.keySet()) {
      append("\t" + SpiderNameGenerator.getFunctionName(aa).toUpperCase() + "_FCT" + " = " + this.functionMap.get(aa) + ",\n");
    }
    append("} FctIxs;\n\n");

    /* Close Include Protection */
    append("#endif//" + pg.getName().toUpperCase() + "_H\n");

    return this.cppString.toString();
  }

  public String generateMainCode(final PiGraph pg, final boolean usingPapify) {
    return SpiderMainFilePrinter.print(pg, this.coreIds.size(), usingPapify);
  }

  /**
   * Main method, launching the generation for the whole PiGraph pg, including license, includes, constants and top method generation.
   *
   * @param pg
   *          the pg
   * @return the string
   */
  public String generateGraphCode(final PiGraph pg) {
    this.cppString.setLength(0);

    final StringBuilder tmp = new StringBuilder();
    final SpiderCodegenVisitor codeGenerator = new SpiderCodegenVisitor(this, tmp, this.preprocessor, this.timings, this.constraints,
        this.scenario.getSimulationManager().getDataTypes());
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

    // Add free fct
    append("\n");
    append("void free_" + pg.getName() + "(){\n");
    append("\tSpider::cleanPiSDF();\n");
    append("}\n");

    // Returns the final C++ code
    return this.cppString.toString();
  }

  /**
   * Generate Papify configs for each actors
   * 
   * @param pg
   * @param scenario
   * @return the string
   */
  public String generatePapifyCode(final PiGraph pg, final PreesmScenario scenario) {
    this.cppString.setLength(0);
    // Generate the header (license, includes and constants)
    append(getLicense());

    append("#include <vector>\n");
    append("#include <map>\n");
    append("#include <spider.h>\n");
    append("#include \"" + pg.getName() + ".h\"\n\n");
    // Papify pre-processing
    PapifyConfigManager papifyConfigManager = scenario.getPapifyConfigManager();

    final HashMap<ArrayList<String>, Integer> uniqueEventSets = new HashMap<>();
    int eventSetID = 0;

    final ArrayList<AbstractActor> papifiedActors = new ArrayList<>();

    for (final AbstractActor actor : this.functionMap.keySet()) {
      PapifyConfig corePapifyConfigGroups = papifyConfigManager.getCorePapifyConfigGroups(actor.getName());
      if (corePapifyConfigGroups != null) {
        papifiedActors.add(actor);
        if (!generatePapifyConfig(corePapifyConfigGroups, actor, uniqueEventSets, eventSetID)) {
          eventSetID++;
        }

      }
      // append("\t" + SpiderNameGenerator.getFunctionName(aa).toUpperCase() + "_FCT" + " = " + this.functionMap.get(aa) + ",\n");
    }

    append("std::map<lrtFct, PapifyConfig*> get_" + pg.getName() + "_papifyConfigs() {\n");
    append("\tstd::map<lrtFct, PapifyConfig*> map;\n");
    append("\t// Initializing the map\n");
    for (final AbstractActor actor : papifiedActors) {
      append("\tmap.insert(std::make_pair(" + pg.getName() + "_fcts[" + SpiderNameGenerator.getFunctionName(actor).toUpperCase() + "_FCT" + "], create"
          + actor.getName() + "PapifyConfig()));\n");
    }
    append("\treturn map;\n");
    append("}\n\n");

    append("void free_" + pg.getName() + "_papifyConfigs(std::map<lrtFct, PapifyConfig*>& map) {\n");
    append("\tstd::map<lrtFct, PapifyConfig*>::iterator it;\n");
    append("\t// Freeing memory of the map \n");
    append("\tfor(it = map.begin(); it != map.end(); ++it) { \n");
    append("\t\tdelete it->second;\n");
    append("\t}\n");
    append("}\n");
    // Returns the final C++ code
    return this.cppString.toString();
  }

  private boolean generatePapifyConfig(final PapifyConfig corePapifyConfigGroups, final AbstractActor actor,
      final HashMap<ArrayList<String>, Integer> uniqueEventSets, final Integer eventSetID) {
    PapiComponent papiComponent = corePapifyConfigGroups.getPAPIComponent();
    Set<PapiEvent> papiEvents = corePapifyConfigGroups.getPAPIEvents();

    Set<PapiEvent> includedEvents = new LinkedHashSet();
    ArrayList<String> eventNames = new ArrayList<>();

    boolean eventMonitoring = false;
    boolean timingMonitoring = false;

    final PapiEvent timingEvent = new PapiEvent();
    timingEvent.setName("Timing");
    // timingEvent.setDesciption("Event to time through PAPI_get_time()");
    // timingEvent.setIndex(9999);
    // timingEvent.setModifiers(new ArrayList<PapiEventModifier>());
    for (PapiEvent event : papiEvents) {
      if (event.getName().equals(timingEvent.getName())) {
        timingMonitoring = true;
      } else if (papiComponent.containsEvent(event)) {
        includedEvents.add(event);
        eventMonitoring = true;
        eventNames.add(event.getName());
      }
    }
    // Check if this set of ID already exists
    Integer realEventSetID = eventSetID;
    boolean found = false;
    if (uniqueEventSets.isEmpty()) {
      uniqueEventSets.put(eventNames, eventSetID);
    } else {
      for (Map.Entry<ArrayList<String>, Integer> eventSet : uniqueEventSets.entrySet()) {
        final ArrayList<String> currentEventSetNames = eventSet.getKey();
        final Integer currentEventSetID = eventSet.getValue();
        if (eventNames.size() != currentEventSetNames.size()) {
          continue;
        } else {
          found = eventNames.containsAll(currentEventSetNames) && currentEventSetNames.containsAll(eventNames);
          if (found) {
            realEventSetID = currentEventSetID;
            break;
          }
        }
      }
    }

    append("static PapifyConfig* " + "create" + actor.getName() + "PapifyConfig() {\n");
    append("\tPapifyConfig* config  = new PapifyConfig;\n\n");
    append("\t// Setting the PapifyConfig for actor: " + actor.getName() + "\n");
    append("\tconfig->peID_            = \"0\";\n");
    append("\tconfig->peType_          = \"" + papiComponent.getId() + "\";\n");
    append("\tconfig->actorName_       = \"" + actor.getName() + "\";\n");
    append("\tconfig->eventSize_       = " + Integer.toString(eventNames.size()) + ";\n");
    append("\tconfig->eventSetID_      = " + realEventSetID.toString() + ";\n");
    final String timing = timingMonitoring ? "true" : "false";
    append("\tconfig->isTiming_        = " + timing + ";\n");
    if (eventMonitoring) {
      append("\tconfig->monitoredEvents_ = std::vector<const char*>(" + Integer.toString(eventNames.size()) + ");\n");
      int i = 0;
      for (String name : eventNames) {
        append("\tconfig->monitoredEvents_[" + Integer.toString(i++) + "] = \"" + name + "\";\n");
      }
    }
    append("\treturn config;\n");
    append("}\n\n");
    return found;
  }

  /**
   * Main method, launching the generation for the whole PiGraph pg, including license, includes, constants and top method generation.
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
      if (a.getRefinement() instanceof CHeaderRefinement) {
        if (!includeList.contains(a.getRefinement().getFileName())) {
          includeList.add(a.getRefinement().getFileName());
        }
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
    l.addAll(pg.getAllParameters());
    Collections.sort(l, (p1, p2) -> p1.getName().compareTo(p2.getName()));
    final StringBuilder parameters_proto = new StringBuilder();
    for (final Parameter p : l) {
      if (p.isLocallyStatic() && !p.isDependent() && !p.isConfigurationInterface()) {
        if (parameters_proto.length() > 0) {
          parameters_proto.append(", ");
          params.append(", ");
        }
        parameters_proto.append("Param " + p.getName());
        params.append(p.getName());
      }
    }
    append(parameters_proto);
    append("){\n");

    // Create a top graph and a top vertex
    append("\tPiSDFGraph* topGraph = Spider::createGraph(\n" + "\t\t/*Edges*/    0,\n" + "\t\t/*Params*/   0,\n" + "\t\t/*InputIf*/  0,\n"
        + "\t\t/*OutputIf*/ 0,\n" + "\t\t/*Config*/   0,\n" + "\t\t/*Body*/     1);\n\n");

    append("\tSpider::addHierVertex(\n" + "\t\t/*Graph*/    topGraph,\n" + "\t\t/*Name*/     \"top\",\n" + "\t\t/*Graph*/    " + sgName + "("
        + params.toString() + "),\n" + "\t\t/*InputIf*/  0,\n" + "\t\t/*OutputIf*/ 0,\n" + "\t\t/*Params*/   0);\n\n");

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
    if ((a.getRefinement() != null) && (a.getRefinement() instanceof CHeaderRefinement)) {
      final CHeaderRefinement href = (CHeaderRefinement) a.getRefinement();
      final FunctionPrototype proto = href.getLoopPrototype();

      append("\t" + proto.getName() + "(\n");
      int maxParamSize = 0;
      for (final FunctionParameter param : proto.getParameters()) {
        maxParamSize = Math.max(maxParamSize, param.getName().length());
      }

      boolean first = true;
      for (final FunctionParameter param : proto.getParameters()) {
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
                  append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName()) + " */ (Param) inParams[" + this.portMap.get(port) + "]");
                  found = true;
                }
              }
            } else {
              for (final Port port : a.getDataInputPorts()) {
                if (port.getName().equals(param.getName())) {
                  append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName()) + " */ (" + param.getType() + "*) inputFIFOs["
                      + this.portMap.get(port) + "]");
                  found = true;
                }
              }
            }
            break;
          case OUT:
            if (param.isIsConfigurationParameter()) {
              for (final Port port : a.getConfigOutputPorts()) {
                if (port.getName().equals(param.getName())) {
                  append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName()) + " */ (Param*) &outParams[" + this.portMap.get(port) + "]");
                  found = true;
                }
              }
            } else {
              for (final Port port : a.getDataOutputPorts()) {
                if (port.getName().equals(param.getName())) {
                  append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName()) + " */ (" + param.getType() + "*) outputFIFOs["
                      + this.portMap.get(port) + "]");
                  found = true;
                }
              }
            }
            break;
          default:
        }
        if (!found) {
          WorkflowLogger.getLogger().warning("Port " + param.getName() + " in Actor " + a.getName() + " not found.");
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
        + " * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,\n" + " * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas\n"
        + " *\n" + " * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr\n" + " *\n"
        + " * This software is a computer program whose purpose is to prototype\n" + " * parallel applications.\n" + " *\n"
        + " * This software is governed by the CeCILL-C license under French law and\n"
        + " * abiding by the rules of distribution of free software.  You can  use,\n"
        + " * modify and/ or redistribute the software under the terms of the CeCILL-C\n"
        + " * license as circulated by CEA, CNRS and INRIA at the following URL\n" + " * \"http://www.cecill.info\".\n" + " *\n"
        + " * As a counterpart to the access to the source code and  rights to copy,\n"
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
        + " * data to be ensured and,  more generally, to use and operate it in the\n" + " * same conditions as regards security.\n" + " *\n"
        + " * The fact that you are presently reading this means that you have had\n" + " * knowledge of the CeCILL-C license and that you accept its terms.\n"
        + " * ****************************************************************************\n" + " */\n\n";
  }

  /**
   * Gets the core types codes.
   *
   * @return the core types codes
   */
  public Map<String, Integer> getCoreTypesCodes() {
    return this.coreTypesIds;
  }

  /**
   * @return the coreIds
   */
  protected Map<String, Integer> getCoreIds() {
    return this.coreIds;
  }
}

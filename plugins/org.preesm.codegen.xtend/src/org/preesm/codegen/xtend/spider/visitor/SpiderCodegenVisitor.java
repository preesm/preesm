/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Hugo Miomandre <hugo.miomandre@insa-rennes.fr> (2017)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
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
package org.preesm.codegen.xtend.spider.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.emf.common.util.EList;
import org.preesm.codegen.xtend.spider.utils.SpiderNameGenerator;
import org.preesm.codegen.xtend.spider.utils.SpiderTypeConverter;
import org.preesm.codegen.xtend.spider.utils.SpiderTypeConverter.PiSDFSubType;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionParameter;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Parameterizable;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.util.PiMMSwitch;
import org.preesm.model.scenario.types.DataType;

// TODO: Find a cleaner way to setParentEdge in Interfaces
/*
 * Ugly workaround for setParentEdge in Interfaces. Must suppose that fifos are always obtained in the same
 * order => Modify the C++ headers?
 * A better way would be a possibility to get edges from one building method to the other (since the parentEdge
 * is in the outer graph),
 * maybe a map from edgeNames to edges with a method getOutputEdgeByName in BaseVertex
 */

/**
 * PiMM models visitor generating C++ code for COMPA Runtime currentGraph: The most outer graph of the PiMM model
 * currentMethod: The StringBuilder used to write the C++ code
 */
public class SpiderCodegenVisitor extends PiMMSwitch<Boolean> {
  private final SpiderPreProcessVisitor preprocessor;

  private final SpiderCodegen callerSpiderCodegen;

  // Ordered set for methods prototypes
  private final LinkedHashSet<String> prototypes = new LinkedHashSet<>();

  // Maps to handle hierarchical graphs
  private final Map<PiGraph, StringBuilder> graph2method    = new LinkedHashMap<>();
  private final Map<PiGraph, List<PiGraph>> graph2subgraphs = new LinkedHashMap<>();

  private final Map<String, DataType> dataTypes;

  private StringBuilder currentMethod;

  private PiGraph       currentGraph;
  private List<PiGraph> currentSubGraphs;

  // Variables containing the type of the currently visited AbstractActor for
  // AbstractActor generation
  // private String currentAbstractActorType;
  // private String currentAbstractActorClass;

  // Map linking data ports to their corresponding description
  private final Map<Port, Integer> portMap;

  private final Map<AbstractActor, Integer> functionMap;

  private final Map<AbstractActor, Map<String, String>> timings;

  private final Map<AbstractActor, Set<String>> constraints;

  public LinkedHashSet<String> getPrototypes() {
    return this.prototypes;
  }

  public Collection<StringBuilder> getMethods() {
    return this.graph2method.values();
  }

  // Shortcut for currentMethod.append()
  private void append(final Object a) {
    this.currentMethod.append(a);
  }

  /**
   */
  public SpiderCodegenVisitor(final SpiderCodegen callerSpiderCodegen, final StringBuilder topMethod,
      final SpiderPreProcessVisitor prepocessor, final Map<AbstractActor, Map<String, String>> timings,
      final Map<AbstractActor, Set<String>> constraints, final Map<String, DataType> dataTypes) {
    this.callerSpiderCodegen = callerSpiderCodegen;
    this.currentMethod = topMethod;
    this.preprocessor = prepocessor;
    this.portMap = this.preprocessor.getPortMap();
    this.functionMap = this.preprocessor.getFunctionMap();
    this.timings = timings;
    this.constraints = constraints;
    this.dataTypes = dataTypes;
  }

  /**
   * When visiting a PiGraph (either the most outer graph or an hierarchical actor), we should generate a new C++ method
   */
  @Override
  public Boolean casePiGraph(final PiGraph pg) {
    // We should first generate the C++ code as for any Actor in the outer
    // graph

    caseAbstractActor(pg);

    // We add pg as a subgraph of the current graph
    if (this.currentSubGraphs == null) {
      this.currentSubGraphs = new ArrayList<>();
    }
    this.currentSubGraphs.add(pg);

    // We stock the informations about the current graph for later use
    final PiGraph currentOuterGraph = this.currentGraph;
    if (currentOuterGraph != null) {
      this.graph2method.put(currentOuterGraph, this.currentMethod);
      this.graph2subgraphs.put(currentOuterGraph, this.currentSubGraphs);
    }
    // We initialize variables which will stock informations about pg during
    // its method generation
    // The new current graph is pg
    this.currentGraph = pg;
    // We start a new StringBuilder to generate its method
    this.currentMethod = new StringBuilder();
    // Currently we know no subgraphs to pg
    this.currentSubGraphs = new ArrayList<>();

    // And then visit pg as a PiGraph, generating the method to build its
    // C++ corresponding PiSDFGraph

    append("\n// Method building PiSDFGraph: ");
    append(pg.getName() + "\n");

    // Generating the method prototype
    generateMethodPrototype(pg);
    // Generating the method body
    generateMethodBody(pg);

    // If pg has no subgraphs, its method has not been added in graph2method
    // map
    if (!this.graph2method.containsKey(this.currentGraph)) {
      this.graph2method.put(this.currentGraph, this.currentMethod);
    }

    // We get back the informations about the outer graph to continue
    // visiting it
    if (currentOuterGraph != null) {
      this.currentMethod = this.graph2method.get(currentOuterGraph);
      this.currentSubGraphs = this.graph2subgraphs.get(currentOuterGraph);
    }
    this.currentGraph = currentOuterGraph;
    return true;
  }

  /**
   * Class that sort parameters with dependencies
   */
  private class ParameterSorting {
    private final Map<Parameter, Integer> ParameterLevels = new LinkedHashMap<>();

    private Integer getLevelParameter(final Parameter p) {
      if (this.ParameterLevels.containsKey(p)) {
        return this.ParameterLevels.get(p);
      }

      int level = 0;
      for (final ConfigInputPort port : p.getConfigInputPorts()) {
        if (port.getIncomingDependency().getSetter() instanceof Parameter) {
          final Parameter incomingParameter = (Parameter) port.getIncomingDependency().getSetter();
          if (!this.ParameterLevels.containsKey(incomingParameter)) {
            getLevelParameter(incomingParameter);
          }
          level = Math.max(level, this.ParameterLevels.get(incomingParameter) + 1);
        }
      }
      this.ParameterLevels.put(p, level);
      return level;
    }

    public List<Parameter> sortParameters(final List<Parameter> params) {
      for (final Parameter p : params) {
        getLevelParameter(p);
      }
      params.sort((p1, p2) -> this.ParameterLevels.get(p1) - this.ParameterLevels.get(p2));
      return params;
    }

  }

  /**
   * Concatenate the signature of the method corresponding to a PiGraph to the currentMethod StringBuilder
   */
  private void generateMethodPrototype(final PiGraph pg) {
    final StringBuilder prototype = new StringBuilder();
    final StringBuilder parameters_proto = new StringBuilder();
    final StringBuilder parameters_def = new StringBuilder();
    final StringBuilder definition = new StringBuilder();

    prototype.append("void ");
    prototype.append(SpiderNameGenerator.getMethodName(pg));
    prototype.append("(PiSDFVertex *" + SpiderNameGenerator.getVertexName(pg));

    definition.append(prototype.toString());

    final List<Parameter> l = new LinkedList<>();
    l.addAll(pg.getParameters());
    Collections.sort(l, (p1, p2) -> p1.getName().compareTo(p2.getName()));

    for (final Parameter p : l) {
      if (p.isLocallyStatic() && !p.isDependent() && !p.isConfigurationInterface()) {
        parameters_proto.append(", ");
        parameters_def.append(", ");
        parameters_proto.append("Param " + p.getName() + " = " + p.getValueExpression().evaluate());
        parameters_def.append("Param " + p.getName());
      }
    }

    prototype.append(parameters_proto);
    definition.append(parameters_def);

    prototype.append(");\n");
    definition.append(")");
    this.prototypes.add(prototype.toString());
    append(definition);
  }

  /**
   * Concatenate the body of the method corresponding to a PiGraph to the currentMethod StringBuilder
   */
  private void generateMethodBody(final PiGraph pg) {
    append("{\n");

    int nInIf = 0;
    int nOutif = 0;
    int nConfig = 0;
    int nBody = 0;

    for (final AbstractActor v : pg.getActors()) {
      switch (SpiderTypeConverter.getType(v)) {
        case PISDF_TYPE_IF:
          if (SpiderTypeConverter.getSubType(v) == PiSDFSubType.PISDF_SUBTYPE_INPUT_IF) {
            nInIf++;
          } else {
            nOutif++;
          }
          break;
        case PISDF_TYPE_CONFIG:
          nConfig++;
          break;
        case PISDF_TYPE_BODY:
          nBody++;
          break;
        default:
          break;
      }
    }

    // Create a graph and a top vertex
    append("\tPiSDFGraph* graph = Spider::createGraph(\n" + "\t\t/*Edges*/    " + pg.getFifos().size() + ",\n"
        + "\t\t/*Params*/   " + pg.getParameters().size() + ",\n" + "\t\t/*InputIf*/  " + nInIf + ",\n"
        + "\t\t/*OutputIf*/ " + nOutif + ",\n" + "\t\t/*Config*/   " + nConfig + ",\n" + "\t\t/*Body*/     " + nBody
        + ");\n");

    // Linking subgraph to its parent graph
    append("\n\t/* Linking subgraph to its parent */\n");
    append("\tSpider::addSubGraph(" + SpiderNameGenerator.getVertexName(pg) + ", graph);\n");

    // Generating parameters
    append("\n\t/* Parameters */\n");

    final List<Parameter> params = new ArrayList<>(pg.getParameters());
    final ParameterSorting ps = new ParameterSorting();
    final List<Parameter> sortedParams = ps.sortParameters(params);

    for (final Parameter p : sortedParams) {
      doSwitch(p);
    }

    // Generating vertices
    append("\n\t/* Vertices */\n");
    for (final AbstractActor v : pg.getActors()) {
      doSwitch(v);
    }
    // Generating edges
    append("\n\t/* Edges */");
    for (final Fifo f : pg.getFifos()) {
      doSwitch(f);
    }

    append("}\n");
  }

  private String generateConfigVertex(final AbstractActor aa) {
    final String vertexName = SpiderNameGenerator.getVertexName(aa);

    String fctIx;
    if (this.functionMap.containsKey(aa)) {
      fctIx = SpiderNameGenerator.getFunctionName(aa).toUpperCase() + "_FCT";
    } else {
      fctIx = "-1";
    }

    // Call the addVertex method on the current graph
    append("\tPiSDFVertex *" + vertexName);
    append(" = Spider::addConfigVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Name*/    \"" + aa.getName() + "\",\n");
    append("\t\t/*FctId*/   " + fctIx + ",\n");
    append("\t\t/*SubType*/ " + "PISDF_SUBTYPE_NORMAL" + ",\n");
    append("\t\t/*InData*/  " + aa.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + aa.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + aa.getConfigInputPorts().size() + ",\n");
    append("\t\t/*OutParam*/" + aa.getConfigOutputPorts().size() + ");\n");

    return vertexName;
  }

  private String generateBodyVertex(final AbstractActor aa) {
    final String vertexName = SpiderNameGenerator.getVertexName(aa);

    String fctIx;
    if (this.functionMap.containsKey(aa)) {
      fctIx = SpiderNameGenerator.getFunctionName(aa).toUpperCase() + "_FCT";
    } else {
      fctIx = "-1";
    }

    // Call the addVertex method on the current graph
    append("\tPiSDFVertex* " + vertexName);
    append(" = Spider::addBodyVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Name*/    \"" + aa.getName() + "\",\n");
    append("\t\t/*FctId*/   " + fctIx + ",\n");
    append("\t\t/*InData*/  " + aa.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + aa.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + aa.getConfigInputPorts().size() + ");\n");

    return vertexName;
  }

  private String generateHierarchicalVertex(final AbstractActor aa) {
    final String vertexName = SpiderNameGenerator.getVertexName(aa);
    final PiGraph subGraph = ((PiGraph) aa);

    // Call the addVertex method on the current graph
    append("\tPiSDFVertex* " + vertexName);
    append(" = Spider::addHierVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Name*/    \"" + aa.getName() + "\",\n");
    append("\t\t/*Graph*/   " + SpiderNameGenerator.getMethodName(subGraph) + "(),\n");

    // final List<Parameter> params = new LinkedList<>();
    // params.addAll(subGraph.getAllParameters());
    // Collections.sort(params, (p1, p2) -> p1.getName().compareTo(p2.getName()));
    // final List<String> paramStrings = new LinkedList<>();
    // for (final Parameter p : params) {
    // if (p.isLocallyStatic() && !p.isDependent() && !p.isConfigurationInterface()) {
    // paramStrings.add(p.getName());
    // }
    // }
    // append(String.join(", ", paramStrings));

    // append("),\n");
    append("\t\t/*InData*/  " + aa.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + aa.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + aa.getConfigInputPorts().size() + ");\n");

    return vertexName;
  }

  /**
   * Generic visit method for all AbstractActors (Actors, PiGraph)
   */
  @Override
  public Boolean caseAbstractActor(final AbstractActor aa) {
    String vertexName;

    if ((aa instanceof Actor) && ((Actor) aa).isConfigurationActor()) {
      vertexName = generateConfigVertex(aa);
    } else if (aa.getName() == "end") {
      caseEndActor(aa);
      return true;
    } else {
      vertexName = generateBodyVertex(aa);
    }

    // Add connections to parameters if necessary
    if (!aa.getConfigOutputPorts().isEmpty()) {
      append("\t/* Adding output parameters */\n");
    }
    for (final ConfigOutputPort cop : aa.getConfigOutputPorts()) {
      for (final Dependency d : cop.getOutgoingDependencies()) {
        append("\tSpider::addOutParam(");
        append(vertexName + ", ");
        append(aa.getConfigOutputPorts().indexOf(cop) + ", ");
        append(SpiderNameGenerator.getParameterName((Parameter) d.getGetter().eContainer()));
        append(");\n");
      }
    }

    // Add connections from parameters if necessary
    if (!aa.getConfigInputPorts().isEmpty()) {
      append("\t/* Adding input parameters */\n");
    }
    for (final ConfigInputPort cip : aa.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(vertexName + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }

    // Adding definition of subgraph (if any)
    if (aa instanceof PiGraph) {
      append("\t/* Generating subgraph definition */\n");
      append("\t" + SpiderNameGenerator.getMethodName((PiGraph) aa) + "(" + vertexName + ");\n");
    }

    if ((aa instanceof Actor) && !(aa instanceof PiGraph)) {
      if (this.constraints.get(aa) != null) {
        // Check if the actor is enabled on all PEs.
        append("\t/* Setting execution constraints */\n");
        final Set<String> peNames = this.constraints.get(aa);
        if (peNames.containsAll(this.callerSpiderCodegen.getCoreIds().keySet())) {
          append("\tSpider::isExecutableOnAllPE(");
          append(vertexName + ");\n");
        } else {
          // Not all the PEs are enabled for the actor
          for (final String core : this.constraints.get(aa)) {
            append("\tSpider::isExecutableOnPE(");
            append(vertexName + ", ");
            append(SpiderNameGenerator.getCoreName(core) + ");\n");
          }
        }
      } else {
        PreesmLogger.getLogger().log(Level.WARNING,
            "Actor " + aa.getName() + " does not have a valid operator to execute on");
      }
    }

    final Map<String, String> aaTimings = this.timings.get(aa);
    if (aaTimings != null) {
      append("\t/* Setting timing on corresponding PEs */\n");
      for (final String coreType : aaTimings.keySet()) {
        append("\tSpider::setTimingOnType(");
        append(vertexName + ", ");
        append(SpiderNameGenerator.getCoreTypeName(coreType) + ", \"");
        append(aaTimings.get(coreType));
        append("\");\n");
      }
    } else {
      PreesmLogger.getLogger().log(Level.WARNING, "Actor " + aa.getName() + " does not have timing information.");
    }

    append("\n");
    return true;
  }

  @Override
  public Boolean caseActor(final Actor a) {
    caseAbstractActor(a);
    return true;
  }

  @Override
  public Boolean caseDataInputInterface(final DataInputInterface dii) {
    final String vertexName = SpiderNameGenerator.getVertexName(dii);

    append("\tPiSDFVertex* " + vertexName);
    append(" = Spider::addInputIf(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Name*/    \"" + vertexName + "\",\n");
    append("\t\t/*InParam*/ " + dii.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : dii.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(vertexName + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface doi) {
    final String vertexName = SpiderNameGenerator.getVertexName(doi);

    append("\tPiSDFVertex* " + vertexName);
    append(" = Spider::addOutputIf(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Name*/    \"" + vertexName + "\",\n");
    append("\t\t/*InParam*/ " + doi.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : doi.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(vertexName + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
    return true;
  }

  /**
   * When visiting a FIFO we should add an edge to the current graph
   */
  @Override
  public Boolean caseFifo(final Fifo f) {
    // Call the addEdge method on the current graph
    append("\n\tSpider::connect(\n");
    append("\t\t/*Graph*/   graph,\n");

    final DataOutputPort srcPort = f.getSourcePort();
    final DataInputPort snkPort = f.getTargetPort();

    long typeSize;
    if (this.dataTypes.containsKey(f.getType())) {
      typeSize = this.dataTypes.get(f.getType()).getSize();
    } else {
      PreesmLogger.getLogger().warning("Type " + f.getType() + " is not defined in scenario (considered size = 1).");
      typeSize = 1;
    }

    final AbstractActor srcActor = (AbstractActor) srcPort.eContainer();
    final AbstractActor snkActor = (AbstractActor) snkPort.eContainer();

    String srcProd = srcPort.getPortRateExpression().getExpressionAsString();
    String snkProd = snkPort.getPortRateExpression().getExpressionAsString();

    /* Change port name in prod/cons/delay */
    for (final ConfigInputPort cfgPort : srcActor.getConfigInputPorts()) {
      final String paramName = ((Parameter) cfgPort.getIncomingDependency().getSetter()).getName();
      srcProd = srcProd.replaceAll("\\b" + cfgPort.getName() + "\\b", paramName);
    }

    for (final ConfigInputPort cfgPort : snkActor.getConfigInputPorts()) {
      final String paramName = ((Parameter) cfgPort.getIncomingDependency().getSetter()).getName();
      snkProd = snkProd.replaceAll("\\b" + cfgPort.getName() + "\\b", paramName);
    }

    String delay = "0";
    if (f.getDelay() != null) {
      delay = f.getDelay().getSizeExpression().getExpressionAsString();

      for (final ConfigInputPort cfgPort : f.getDelay().getConfigInputPorts()) {
        final String paramName = ((Parameter) cfgPort.getIncomingDependency().getSetter()).getName();
        delay = delay.replaceAll("\\b" + cfgPort.getName() + "\\b", paramName);
      }
    }

    append("\t\t/*Src*/ " + SpiderNameGenerator.getVertexName(srcActor) + ", /*SrcPrt*/ " + this.portMap.get(srcPort)
        + ", /*Prod*/ \"(" + srcProd + ")*" + typeSize + "\",\n");

    append("\t\t/*Snk*/ " + SpiderNameGenerator.getVertexName(snkActor) + ", /*SnkPrt*/ " + this.portMap.get(snkPort)
        + ", /*Cons*/ \"(" + snkProd + ")*" + typeSize + "\",\n");

    if (f.getDelay() != null) {
      // append("\t\t/*Delay*/ \"(" + delay + ")*sizeof(" + f.getType() + ")\",0);\n\n");
      append("\t\t/*Delay*/ \"(" + delay + ") * " + typeSize + "\", nullptr, nullptr, nullptr, true);\n");
    } else {
      append("\t\t/*Delay*/ \"0\", nullptr, nullptr, nullptr, false);\n");
    }
    return true;
  }

  /**
   * When visiting a parameter, we should add a parameter to the current graph
   */
  @Override
  public Boolean caseParameter(final Parameter p) {
    final String paramName = SpiderNameGenerator.getParameterName(p);

    final EList<ConfigInputPort> configInputPorts = p.getConfigInputPorts();

    if (!p.isLocallyStatic()) {
      if ((configInputPorts.size() == 1)
          && !(configInputPorts.get(0).getIncomingDependency().getSetter() instanceof Parameter)) {
        /* DYNAMIC */
        // append(
        append("\tPiSDFParam *" + paramName + " = Spider::addDynamicParam(graph, " + "\"" + p.getName() + "\");\n");
      } else {
        /* DYNAMIC DEPENDANT */
        append("\tPiSDFParam *" + paramName + " = Spider::addDynamicDependentParam(graph, " + "\"" + p.getName()
            + "\", \"" + p.getValueExpression().getExpressionAsString() + "\"" + ");\n");
      }
    } else if (p.isConfigurationInterface() && (((ConfigInputInterface) p).getGraphPort() instanceof ConfigInputPort)) {
      /* HERITED */
      append("\tPiSDFParam *" + paramName + " = Spider::addHeritedParam(graph, " + "\"" + p.getName() + "\", "
          + this.portMap.get(((ConfigInputInterface) p).getGraphPort()) + ");\n");
    } else if (configInputPorts.isEmpty()) {
      /* STATIC */
      append("\tPiSDFParam *" + paramName + " = Spider::addStaticParam(graph, " + "\"" + p.getName() + "\", "
          + p.getName() + ");\n");
    } else {
      /* STATIC DEPENDANT */
      append("\tPiSDFParam *" + paramName + " = Spider::addStaticDependentParam(graph, " + "\"" + p.getName() + "\", \""
          + p.getValueExpression().getExpressionAsString() + "\", {");
      for (final ConfigInputPort cip : configInputPorts) {
        final Parameter setter = (Parameter) cip.getIncomingDependency().getSetter();
        append(SpiderNameGenerator.getParameterName(setter));
        // Adding trailing comma
        if (configInputPorts.indexOf(cip) != configInputPorts.size() - 1) {
          append(", ");
        }
      }
      append("});\n");
    }
    return true;
  }

  @Override
  public Boolean caseBroadcastActor(final BroadcastActor ba) {
    append("\tPiSDFVertex* " + SpiderNameGenerator.getVertexName(ba));
    append(" = Spider::addSpecialVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Type*/    " + "PISDF_SUBTYPE_BROADCAST" + ",\n");
    append("\t\t/*InData*/  " + ba.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + ba.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + ba.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : ba.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(SpiderNameGenerator.getVertexName(ba) + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
    return true;
  }

  /**
   *
   */
  public Boolean caseEndActor(final AbstractActor aa) {
    append("\tPiSDFVertex* " + SpiderNameGenerator.getVertexName(aa));
    append(" = Spider::addSpecialVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Type*/    " + "PISDF_SUBTYPE_END" + ",\n");
    append("\t\t/*InData*/  " + aa.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + aa.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + aa.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : aa.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(SpiderNameGenerator.getVertexName(aa) + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
    return true;
  }

  @Override
  public Boolean caseJoinActor(final JoinActor ja) {
    append("\tPiSDFVertex* " + SpiderNameGenerator.getVertexName(ja));
    append(" = Spider::addSpecialVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Type*/    " + "PISDF_SUBTYPE_JOIN" + ",\n");
    append("\t\t/*InData*/  " + ja.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + ja.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + ja.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : ja.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(SpiderNameGenerator.getVertexName(ja) + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
    return true;
  }

  @Override
  public Boolean caseForkActor(final ForkActor fa) {
    append("\tPiSDFVertex* " + SpiderNameGenerator.getVertexName(fa));
    append(" = Spider::addSpecialVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Type*/    " + "PISDF_SUBTYPE_FORK" + ",\n");
    append("\t\t/*InData*/  " + fa.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + fa.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + fa.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : fa.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(SpiderNameGenerator.getVertexName(fa) + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
    return true;
  }

  @Override
  public Boolean caseRoundBufferActor(final RoundBufferActor rba) {
    append("\tPiSDFVertex* " + SpiderNameGenerator.getVertexName(rba));
    append(" = Spider::addSpecialVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Type*/    " + "PISDF_SUBTYPE_ROUNDBUFFER" + ",\n");
    append("\t\t/*InData*/  " + rba.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + rba.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + rba.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : rba.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(SpiderNameGenerator.getVertexName(rba) + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
    return true;
  }

  @Override
  public Boolean caseConfigOutputInterface(final ConfigOutputInterface coi) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataInputPort(final DataInputPort dip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseAbstractVertex(final AbstractVertex av) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDelayActor(final DelayActor da) {
    return true;
  }

  @Override
  public Boolean caseConfigInputPort(final ConfigInputPort cip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseConfigOutputPort(final ConfigOutputPort cop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataOutputPort(final DataOutputPort dop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDelay(final Delay d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDependency(final Dependency d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseExpression(final Expression e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseInterfaceActor(final InterfaceActor ia) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseISetter(final ISetter is) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseParameterizable(final Parameterizable p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean casePort(final Port p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataPort(final DataPort p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean casePiSDFRefinement(final PiSDFRefinement r) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseFunctionParameter(final FunctionParameter functionParameter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseFunctionPrototype(final FunctionPrototype functionPrototype) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseCHeaderRefinement(final CHeaderRefinement hRefinement) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseExecutableActor(final ExecutableActor ea) {
    throw new UnsupportedOperationException();
  }

  /**
   * Class allowing to stock necessary information about graphs when moving through the graph hierarchy
   */
  // private class GraphDescription {
  // List<PiGraph> subGraphs;
  // StringBuilder method;
  //
  // public GraphDescription(List<PiGraph> subGraphs, StringBuilder method) {
  // this.subGraphs = subGraphs;
  // this.method = method;
  // }
  //
  // }w
}

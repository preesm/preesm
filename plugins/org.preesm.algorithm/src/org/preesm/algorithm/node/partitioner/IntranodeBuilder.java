package org.preesm.algorithm.node.partitioner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.codegen.idl.Prototype;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.ExpressionHolder;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.LOOPSeeker;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.util.DefaultTypeSizes;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.TimingType;

public class IntranodeBuilder {
  /**
   * Workflow scenario.
   */
  private final Scenario scenario;
  /**
   * Input graph.
   */
  /**
   * Architecture design.
   */
  private final Design archi;

  private final PiGraph                   graph;
  private final Map<AbstractVertex, Long> brv;
  private final Map<Long, Long>           timeEq; // id node/cumulative time

  private final List<Design> archiList;

  private final Map<Long, List<AbstractActor>>      topoOrderASAP     = new HashMap<>();
  private final Map<Long, Map<AbstractActor, Long>> subs              = new HashMap<>();
  Map<Long, Map<AbstractActor, Long>>               subsCopy          = new HashMap<>();
  List<PiGraph>                                     sublist           = new ArrayList<>();
  private String                                    includePath       = "";
  private String                                    graphPath         = "";
  private final String                              workspaceLocation = ResourcesPlugin.getWorkspace().getRoot()
      .getLocation().toString();
  static String                                     fileError         = "Error occurred during file generation: ";
  private String                                    scenariiPath      = "";

  public IntranodeBuilder(Scenario scenario, Map<AbstractVertex, Long> brv, Map<Long, Long> timeEq,
      List<Design> archiList) {
    this.scenario = scenario;
    this.graph = scenario.getAlgorithm();
    this.archi = scenario.getDesign();
    this.brv = brv;
    this.timeEq = timeEq;
    this.archiList = archiList;
  }

  public List<PiGraph> execute() {
    final String[] uriString = graph.getUrl().split("/");
    graphPath = File.separator + uriString[1] + File.separator + uriString[2] + "/generated/";
    scenariiPath = File.separator + uriString[1] + "/Scenarios/generated/";
    includePath = uriString[1] + "/Code/include/";
    // Identify the actors who will form the sub
    computeSubs();
    computeSplits();

    // Construct subs
    constructSubs();

    // export
    exportSubs();
    return sublist;

  }

  private void exportSubs() {
    for (final PiGraph subgraph : sublist) {
      subgraph.setUrl(graphPath + subgraph.getName() + ".pi");
      // 4. export subs
      graphExporter(subgraph);
      scenarioExporter(subgraph);
    }

  }

  private void scenarioExporter(PiGraph subgraph) {
    final Scenario subScenario = ScenarioUserFactory.createScenario();
    final String indexStr = subgraph.getName().replace("sub", "");
    final Long indexL = Long.decode(indexStr);
    final Design subArchi = archiList.get(indexL.intValue());
    subScenario.setDesign(subArchi);

    subScenario.setAlgorithm(subgraph);
    // Get com nodes and cores names
    final List<ComponentInstance> coreIds = new ArrayList<>(subArchi.getOperatorComponentInstances());
    final List<ComponentInstance> comNodeIds = subArchi.getCommunicationComponentInstances();

    // Set default values for constraints, timings and simulation parameters

    // Add a main core (first of the list)
    if (!coreIds.isEmpty()) {
      subScenario.getSimulationInfo().setMainOperator(coreIds.get(0));
    }
    if (!comNodeIds.isEmpty()) {
      subScenario.getSimulationInfo().setMainComNode(comNodeIds.get(0));
    }

    csvGenerator(subScenario, subgraph, subArchi);
    for (final Component opId : subArchi.getProcessingElements()) {
      for (final AbstractActor aa : subgraph.getAllActors()) {
        if (aa instanceof Actor) {
          subScenario.getTimings().setExecutionTime(aa, opId, this.scenario.getTimings().getExecutionTimeOrDefault(aa,
              archi.getProcessingElement(opId.getVlnv().getName())));
        }
      }

    }
    subScenario.getTimings().getMemTimings().addAll(scenario.getTimings().getMemTimings());

    for (final ComponentInstance coreId : coreIds) {
      for (final AbstractActor aa : subgraph.getAllActors()) {
        if (aa instanceof Actor) {
          subScenario.getConstraints().addConstraint(coreId, aa);
        }
      }
      subScenario.getConstraints().addConstraint(coreId, subgraph);
      subScenario.getSimulationInfo().addSpecialVertexOperator(coreId);
    }

    // Add a average transfer size
    subScenario.getSimulationInfo().setAverageDataSize(scenario.getSimulationInfo().getAverageDataSize());
    // Set the default data type sizes
    for (final Fifo f : subScenario.getAlgorithm().getAllFifos()) {
      final String typeName = f.getType();
      subScenario.getSimulationInfo().getDataTypes().put(typeName,
          DefaultTypeSizes.getInstance().getTypeSize(typeName));
    }
    // add constraint
    subScenario.getConstraints().setGroupConstraintsFileURL("");
    for (final Entry<ComponentInstance, EList<AbstractActor>> gp : subScenario.getConstraints().getGroupConstraints()) {
      for (final AbstractActor actor : subgraph.getAllActors()) {
        gp.getValue().add(actor);
      }
    }
    final String[] uriString = graph.getUrl().split("/");
    subScenario.setCodegenDirectory("/" + uriString[1] + "/Code/generated/" + subgraph.getName());
    subScenario.setSizesAreInBit(true);
  }

  private void csvGenerator(Scenario scenar, PiGraph pigraph, Design architecture) {
    // Create the list of core_types
    final List<Component> coreTypes = new ArrayList<>(architecture.getProcessingElements()); // List of core_types

    // Create the CSV header
    final StringConcatenation content = new StringConcatenation();
    content.append("Actors;");
    for (final Component coreType : coreTypes) {
      coreType.getVlnv().getName();
      content.append(coreType.getVlnv().getName() + ";");
    }

    content.append("\n");

    // Create data rows
    for (final AbstractActor actor : pigraph.getActors()) {
      if (actor instanceof Actor && !(actor instanceof SpecialActor)) {
        content.append(actor.getVertexPath().replace(graph.getName() + "/", "") + ";");

        for (final Component coreType : coreTypes) {
          final String executionTime = scenar.getTimings().getExecutionTimeOrDefault(actor, coreType);
          content.append(executionTime);
        }

        content.append("\n");
      }
    }
    final String path = workspaceLocation + scenariiPath + pigraph.getName() + ".csv";
    scenar.getTimings().setExcelFileURL(scenariiPath + pigraph.getName() + ".csv");
    try (FileOutputStream outputStream = new FileOutputStream(path)) {
      final byte[] bytes = content.toString().getBytes();
      outputStream.write(bytes);
    } catch (final IOException e) {
      final String errorMessage = fileError + e.getMessage();
      PreesmLogger.getLogger().log(Level.INFO, errorMessage);
    }
  }

  private void computeSplits() {
    // 1. split tasks if it's required
    for (final Entry<Long, Map<AbstractActor, Long>> a : subs.entrySet()) {
      final Map<AbstractActor, Long> newValue = new HashMap<>(a.getValue());
      subsCopy.put(a.getKey(), newValue);
    }
    for (Long subRank = 0L; subRank < subs.size(); subRank++) {
      for (final Entry<AbstractActor, Long> a : subs.get(subRank).entrySet()) {
        if (a.getValue() < brv.get(a.getKey())) {
          brv.replace(a.getKey(), split(a.getKey(), a.getValue(), brv.get(a.getKey()), subRank));
        }
      }
    }
    for (Long subRank = 0L; subRank < subs.size(); subRank++) {
      // 2. generate subs
      final List<AbstractActor> list = new ArrayList<>();
      for (final AbstractActor a : subsCopy.get(subRank).keySet()) {
        list.add(a);
      }
      final PiGraph subgraph = new PiSDFSubgraphBuilder(graph, list, "sub_" + subRank).build();

      sublist.add(subgraph);

    }
  }

  private void constructSubs() {

    // 3. free subs (Interface --> sink; container)
    for (final PiGraph subgraph : sublist) {
      for (final DataInputInterface in : subgraph.getDataInputInterfaces()) {
        final Actor src = PiMMUserFactory.instance.createActor();
        src.setName("src_" + in.getName());
        final Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
        src.setRefinement(refinement);
        final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) ((src).getRefinement());
        final Prototype oEmptyPrototype = new Prototype();
        oEmptyPrototype.setIsStandardC(true);

        cHeaderRefinement.setFilePath(includePath + src.getName() + ".h");
        final FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
        cHeaderRefinement.setLoopPrototype(functionPrototype);
        functionPrototype.setName(src.getName());

        src.setContainingGraph(in.getDirectSuccessors().get(0).getContainingGraph());
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        src.getDataOutputPorts().add(dout);
        dout.setName("out");
        dout.setExpression(in.getDataPort().getExpression());
        in.getDataOutputPorts().get(0).getFifo().setSourcePort(dout);
        final FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
        functionArgument.setName(src.getAllPorts().get(0).getName());
        functionArgument.setType(dout.getFifo().getType());
        functionArgument.setDirection(Direction.OUT);
        functionPrototype.getArguments().add(functionArgument);
        generateFileH(src);
        // set 1 because it's an interface
        for (final Component opId : archi.getProcessingElements()) {
          scenario.getTimings().setExecutionTime(src, opId, 1L);
        }

      }
      for (final DataOutputInterface out : subgraph.getDataOutputInterfaces()) {
        final Actor snk = PiMMUserFactory.instance.createActor();
        snk.setName("snk_" + out.getName());
        final Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
        snk.setRefinement(refinement);
        final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (snk.getRefinement());
        final Prototype oEmptyPrototype = new Prototype();
        oEmptyPrototype.setIsStandardC(true);

        cHeaderRefinement.setFilePath(includePath + snk.getName() + ".h");
        final FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
        cHeaderRefinement.setLoopPrototype(functionPrototype);
        functionPrototype.setName(snk.getName());

        snk.setContainingGraph(out.getContainingGraph());
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        snk.getDataInputPorts().add(din);
        din.setName("in");
        din.setExpression(out.getDataPort().getExpression());
        out.getDataInputPorts().get(0).getFifo().setTargetPort(din);
        final FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
        functionArgument.setName(snk.getAllPorts().get(0).getName());
        functionArgument.setType(din.getFifo().getType());
        functionArgument.setDirection(Direction.IN);
        functionPrototype.getArguments().add(functionArgument);
        generateFileH(snk);
        // set 1 because it's an interface
        for (final Component opId : archi.getProcessingElements()) {
          scenario.getTimings().setExecutionTime(snk, opId, 1L);
        }
      }
      // merge cfg
      for (final Dependency dep : subgraph.getDependencies()) {
        ((AbstractVertex) dep.getSetter()).setName(dep.getGetter().getName());
        ((ConfigInputInterface) dep.getSetter()).getGraphPort().setName(dep.getGetter().getName());

      }
      int paramSize = subgraph.getParameters().size();
      for (int i = 0; i < paramSize; i++) {
        for (int j = i + 1; j < paramSize; j++) {
          if (subgraph.getParameters().get(i).getName().equals(subgraph.getParameters().get(j).getName())) {
            subgraph.getParameters().get(i).getOutgoingDependencies()
                .add(subgraph.getParameters().get(j).getOutgoingDependencies().get(0));
            subgraph.removeParameter(subgraph.getParameters().get(j));
            paramSize--;
            j--;
          }
        }
      }
      for (int i = 0; i < subgraph.getConfigInputPorts().size(); i++) {
        if (!(subgraph.getConfigInputPorts().get(i) instanceof Parameter)) {
          final Parameter p = PiMMUserFactory.instance.createParameter(subgraph.getParameters().get(i).getName(),
              subgraph.getParameters().get(i).getExpression().evaluate());
          final Long exp = ((ExpressionHolder) subgraph.getConfigInputPorts().get(i).getIncomingDependency()
              .getSetter()).getExpression().evaluate();
          for (int j = 0; j < subgraph.getParameters().get(i).getOutgoingDependencies().size(); j++) {
            p.getOutgoingDependencies().add(subgraph.getParameters().get(i).getOutgoingDependencies().get(j));
            j--;
          }
          p.setExpression(exp);
          subgraph.addParameter(p);
          subgraph.removeParameter(subgraph.getParameters().get(i));
          i--;
        }
      }

      // remove empty FIFO
      subgraph.getFifos().stream().filter(x -> x.getSourcePort() == null).forEach(x -> subgraph.removeFifo(x));
      subgraph.getFifos().stream().filter(x -> x.getTargetPort() == null).forEach(subgraph::removeFifo);
      subgraph.getAllActors().stream().filter(x -> x instanceof DataInputInterface || x instanceof DataOutputInterface)
          .forEach(x -> subgraph.removeActor(x));
      // subgraph.remove
      PiBRV.compute(subgraph, BRVMethod.LCM);

    }

  }

  private void graphExporter(PiGraph subgraph) {
    // TODO Auto-generated method stub

  }

  private Long split(AbstractActor key, Long rv1, Long rv2, Long subRank) {
    // if data pattern
    // copy instance
    final AbstractActor copy = PiMMUserFactory.instance.copy(key);
    copy.setContainingGraph(key.getContainingGraph());
    int index = 0;
    for (final DataInputPort in : key.getDataInputPorts()) {
      if (!in.getFifo().isHasADelay()) {
        final ForkActor frk = PiMMUserFactory.instance.createForkActor();
        frk.setName("Fork_" + key.getName() + index);
        frk.setContainingGraph(key.getContainingGraph());

        // connect din to frk
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in");
        final Long dt = in.getFifo().getSourcePort().getExpression().evaluate() * brv.get(in.getFifo().getSource());
        final Long rt = in.getExpression().evaluate();
        din.setExpression(dt);

        frk.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setType(in.getFifo().getType());
        fin.setSourcePort(in.getFifo().getSourcePort());
        fin.setTargetPort(din);

        fin.setContainingGraph(key.getContainingGraph());

        // connect fork to oEmpty_0
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        dout.setName("out_0");
        dout.setExpression(dt - (rv1 * rt));
        frk.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(in.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(in);
        fout.setContainingGraph(key.getContainingGraph());

        // connect fork to duplicated actors
        final DataOutputPort doutn = PiMMUserFactory.instance.createDataOutputPort();
        doutn.setName("out_" + 1);
        doutn.setExpression(dt - ((rv2 - rv1) * rt));
        frk.getDataOutputPorts().add(doutn);
        final Fifo foutn = PiMMUserFactory.instance.createFifo();
        foutn.setType(fin.getType());
        foutn.setSourcePort(doutn);
        foutn.setContainingGraph(key.getContainingGraph());
        copy.getDataInputPorts().stream().filter(x -> x.getName().equals(in.getName()))
            .forEach(x -> x.setIncomingFifo(foutn));

        subsCopy.get(subRank).put(frk, 1L);
        index++;
      } else {
        PreesmLogger.getLogger().log(Level.INFO, "global delay not yet handle");
      }
    }
    index = 0;
    for (final DataOutputPort out : key.getDataOutputPorts()) {
      if (!out.getFifo().isHasADelay()) {
        final JoinActor jn = PiMMUserFactory.instance.createJoinActor();
        jn.setName("Join_" + key.getName() + index);
        jn.setContainingGraph(key.getContainingGraph());

        // connect Join to dout
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();

        dout.setName("out");
        final Long dt = out.getFifo().getTargetPort().getExpression().evaluate() * brv.get(out.getFifo().getTarget());
        final Long rt = out.getExpression().evaluate();
        dout.setExpression(dt);
        jn.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(out.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(out.getFifo().getTargetPort());
        fout.setContainingGraph(key.getContainingGraph());

        // connect oEmpty_0 to Join
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in_0");
        din.setExpression(dt - (rv1 * rt));
        jn.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setSourcePort(out);
        fin.setTargetPort(din);
        fin.setContainingGraph(key.getContainingGraph());
        out.getFifo().setType(fout.getType());

        // connect duplicated actors to Join
        final DataInputPort dinn = PiMMUserFactory.instance.createDataInputPort();
        dinn.setName("in_" + 1);
        dinn.setExpression(dt - (rv2 - rv1) * out.getFifo().getSourcePort().getExpression().evaluate());
        jn.getDataInputPorts().add(dinn);
        final Fifo finn = PiMMUserFactory.instance.createFifo();
        finn.setType(fout.getType());
        finn.setTargetPort(dinn);
        finn.setContainingGraph(key.getContainingGraph());
        copy.getDataOutputPorts().stream().filter(x -> x.getName().equals(out.getName()))
            .forEach(x -> x.setOutgoingFifo(finn));

        // }
        if (subsCopy.get(subRank + 1) == null) {
          subsCopy.get(subRank).put(jn, 1L);
          subsCopy.get(subRank).remove(key);
          subsCopy.get(subRank).put(copy, rv2 - rv1);
        } else {
          subsCopy.get(subRank + 1).put(jn, 1L);
          subsCopy.get(subRank + 1).remove(key);
          subsCopy.get(subRank + 1).put(copy, rv2 - rv1);
        }
        index++;
      } else {

        // connect oEmpty delayed output to 1st duplicated actor
        final Fifo fdin = PiMMUserFactory.instance.createFifo();
        fdin.setSourcePort(out);
        copy.getDataInputPorts().stream().filter(x -> x.getFifo() == null).forEach(x -> x.setIncomingFifo(fdin));
        fdin.setContainingGraph(key.getContainingGraph());

      }
    }
    for (final ConfigInputPort cfg : key.getConfigInputPorts()) {
      copy.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName()))
          .forEach(x -> PiMMUserFactory.instance.createDependency(cfg.getIncomingDependency().getSetter(), x));
      copy.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName()))
          .forEach(x -> x.getIncomingDependency().setContainingGraph(cfg.getIncomingDependency().getContainingGraph()));

    }

    // set 1 because it's an interface
    for (final Component opId : archi.getProcessingElements()) {
      scenario.getTimings().setExecutionTime(copy, opId, scenario.getTimings().getExecutionTimeOrDefault(key, opId));
    }
    // remove delay
    ((PiGraph) key.getContainingGraph()).getDelays().stream().filter(x -> x.getContainingFifo().getSourcePort() == null)
        .forEach(x -> ((PiGraph) key.getContainingGraph()).removeDelay(x));
    // remove empty fifo
    ((PiGraph) key.getContainingGraph()).getFifos().stream().filter(x -> x.getSourcePort() == null)
        .forEach(x -> ((PiGraph) key.getContainingGraph()).removeFifo(x));
    ((PiGraph) key.getContainingGraph()).getFifos().stream().filter(x -> x.getTargetPort() == null)
        .forEach(x -> ((PiGraph) key.getContainingGraph()).removeFifo(x));

    return rv2 - rv1;

  }

  private void generateFileH(Actor actor) {
    final String[] uriString = graph.getUrl().split("/");
    final String content = "// jfécekejepeu \n #ifndef " + actor.getName().toUpperCase() + "_H \n #define "
        + actor.getName().toUpperCase() + "_H \n void " + actor.getName() + "("
        + actor.getAllDataPorts().get(0).getFifo().getType() + " " + actor.getAllDataPorts().get(0).getName()
        + "); \n #endif";
    final String path = workspaceLocation + "/" + uriString[1] + "/Code/include/" + actor.getName() + ".h";
    try (FileOutputStream outputStream = new FileOutputStream(path)) {
      final byte[] bytes = content.getBytes();
      outputStream.write(bytes);
    } catch (final IOException e) {
      final String errorMessage = fileError + e.getMessage();
      PreesmLogger.getLogger().log(Level.INFO, errorMessage);
    }
  }

  private void computeSubs() {
    Long nodeID = 0L;
    Long timTemp = 0L;
    preprocessCycle();
    Map<AbstractActor, Long> list = new HashMap<>();
    final Long lastEntry = (long) (topoOrderASAP.entrySet().size() - 1);
    final int lastKey = (topoOrderASAP.get(lastEntry).size() - 1);
    final AbstractActor lastActor = topoOrderASAP.get(lastEntry).get(lastKey);
    for (final Entry<Long, List<AbstractActor>> entry : topoOrderASAP.entrySet()) {
      for (final AbstractActor a : entry.getValue()) {
        Long count = 0L;
        while (count < brv.get(a)) {
          // 1. compute actor timing on slowest core
          Long slow = 0L;
          if (a instanceof Actor) {
            if (scenario.getTimings().getActorTimings().get(a) != null) {
              slow = Long.valueOf(
                  scenario.getTimings().getActorTimings().get(a).get(0).getValue().get(TimingType.EXECUTION_TIME));
              for (final Entry<Component, EMap<TimingType, String>> element : scenario.getTimings().getActorTimings()
                  .get(a)) {
                final Long timeSeek = Long.valueOf(element.getValue().get(TimingType.EXECUTION_TIME));
                if (timeSeek < slow) {
                  slow = timeSeek;
                }
              }
            } else {
              slow = 100L;
            }
          }

          // add instance while lower than target sub time
          Long i;
          for (i = 0L; count + i < brv.get(a) && timTemp < timeEq.get(nodeID); i++) {
            timTemp = timTemp + slow;
          }
          count = count + i;
          list.put(a, i);
          if (timTemp > timeEq.get(nodeID) || a.equals(lastActor)) {

            subs.put(nodeID, list);
            nodeID++;
            list = new HashMap<>();
            timTemp = 0L;
          }
        }
      }
    }
  }

  private void preprocessCycle() {
    final List<AbstractActor> graphLOOPs = new LOOPSeeker(scenario.getAlgorithm()).seek();
    if (!graphLOOPs.isEmpty()) {
      PreesmLogger.getLogger().log(Level.INFO, "cycles are not handle yet");

    }
    // 2. create subs

  }
}

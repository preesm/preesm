package org.preesm.algorithm.clustering;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.algorithm.model.schedule.util.IScheduleTransform;
import org.preesm.algorithm.model.schedule.util.ScheduleDataParallelismExhibiter;
import org.preesm.algorithm.model.schedule.util.ScheduleFlattener;
import org.preesm.algorithm.model.schedule.util.ScheduleParallelismDepthLimiter;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.util.PiSDFMergeabilty;

/**
 * @author dgageot
 *
 */
public class ClusteringBuilder {

  private Map<AbstractActor, Schedule> scheduleMapping;

  private PiGraph pigraph;

  private long seed;

  private IClusteringAlgorithm clusteringAlgorithm;

  private Map<AbstractVertex, Long> repetitionVector;

  private int nbCluster;

  /**
   * @param graph
   *          PiGraph to clusterize
   * @param algorithm
   *          type of clustering algorithm
   * @param seed
   *          seed for random clustering algorithm
   */
  public ClusteringBuilder(final PiGraph graph, final String algorithm, final long seed) {
    this.scheduleMapping = new LinkedHashMap<>();
    this.pigraph = graph;
    this.seed = seed;
    this.clusteringAlgorithm = clusteringAlgorithmFactory(algorithm);
    this.repetitionVector = null;
  }

  public PiGraph getAlgorithm() {
    return pigraph;
  }

  public Map<AbstractActor, Schedule> getScheduleMapping() {
    return scheduleMapping;
  }

  public Map<AbstractVertex, Long> getRepetitionVector() {
    return repetitionVector;
  }

  /**
   * @return schedule mapping including all cluster with corresponding scheduling
   */
  public final Map<AbstractActor, Schedule> processClustering() {
    // Keep original algorithm
    PiGraph origAlgorithm = this.pigraph;

    // Copy input graph for first stage of clustering with clustering algorithm
    PiGraph firstStageGraph = PiMMUserFactory.instance.copyPiGraphWithHistory(origAlgorithm);
    this.pigraph = firstStageGraph;

    nbCluster = 0;
    repetitionVector = PiBRV.compute(firstStageGraph, BRVMethod.LCM);
    // Until the algorithm has to work
    while (!clusteringAlgorithm.clusteringComplete(this)) {
      // Search actors to clusterize
      Pair<ScheduleType, List<AbstractActor>> actorFound = clusteringAlgorithm.findActors(this);
      // Clusterize given actors
      clusterizeActors(actorFound);
      // Compute BRV with the corresponding graph
      repetitionVector = PiBRV.compute(firstStageGraph, BRVMethod.LCM);
    }

    // Perform flattening transformation on schedule graph
    scheduleTransform(new ScheduleFlattener());

    // Set input graph for second stage of clustering from transformed schedule graph
    this.pigraph = origAlgorithm;

    // Recluster from schedule graph
    List<Schedule> schedules = new LinkedList<>();
    schedules.addAll(scheduleMapping.values());
    scheduleMapping.clear();
    nbCluster = 0;
    for (Schedule schedule : schedules) {
      HierarchicalSchedule processedSchedule = (HierarchicalSchedule) processClusteringFrom(schedule);
      scheduleMapping.put(processedSchedule.getAttachedActor(), processedSchedule);
    }

    // Exhibit data parallelism
    scheduleTransform(new ScheduleDataParallelismExhibiter());

    // Limit parallelism at the first layer
    scheduleTransform(new ScheduleParallelismDepthLimiter(1));

    // Verify consistency of result graph
    PiGraphConsistenceChecker.check(pigraph);

    return scheduleMapping;
  }

  private final void scheduleTransform(IScheduleTransform transformer) {
    for (Entry<AbstractActor, Schedule> entry : scheduleMapping.entrySet()) {
      Schedule schedule = entry.getValue();
      schedule = transformer.performTransform(schedule);
      scheduleMapping.replace(entry.getKey(), schedule);
    }
  }

  private final Schedule processClusteringFrom(Schedule schedule) {
    // If it is an hierarchical schedule, explore and cluster actors
    if (schedule instanceof HierarchicalSchedule) {
      HierarchicalSchedule hierSchedule = (HierarchicalSchedule) schedule;
      // Retrieve childrens schedule and actors
      List<Schedule> childSchedules = new LinkedList<>();
      childSchedules.addAll(hierSchedule.getChildren());
      List<AbstractActor> childActors = new LinkedList<>();
      // Clear list of children schedule
      hierSchedule.getChildren().clear();
      for (Schedule child : childSchedules) {
        // Explore children and process clustering into
        Schedule processedChild = processClusteringFrom(child);
        hierSchedule.getChildren().add(processedChild);
        // Retrieve list of children AbstractActor (needed for clusterization)
        if (child instanceof HierarchicalSchedule) {
          childActors.add(((HierarchicalSchedule) processedChild).getAttachedActor());
        } else {
          childActors.addAll(processedChild.getActors());
        }
      }

      // Compute repetition vector
      repetitionVector = PiBRV.compute(pigraph, BRVMethod.LCM);

      // Build new cluster
      PiGraph newCluster = buildClusterGraph(childActors);
      hierSchedule.setAttachedActor(newCluster);
    }

    return schedule;
  }

  private final IClusteringAlgorithm clusteringAlgorithmFactory(String clusteringAlgorithm) {
    if (clusteringAlgorithm != null) {
      if (clusteringAlgorithm.equals("APGAN")) {
        return new APGANClusteringAlgorithm();
      }
      if (clusteringAlgorithm.equals("Dummy")) {
        return new DummyClusteringAlgorithm();
      }
      if (clusteringAlgorithm.equals("Random")) {
        return new RandomClusteringAlgorithm(this.seed);
      }
      if (clusteringAlgorithm.equals("Parallel")) {
        return new ParallelClusteringAlgorithm();
      }
    }
    throw new PreesmRuntimeException(
        "Parameter " + clusteringAlgorithm + " is not part of available clustering algorithm");
  }

  /**
   * clusterize two actors (left -> right, it means that left produce token for right)
   * 
   * @param actorSchedule
   *          schedule of actor inside of cluster
   * @return
   */
  private final void clusterizeActors(Pair<ScheduleType, List<AbstractActor>> actorFound) {

    // Build corresponding hierarchical actor
    AbstractActor cluster = buildClusterGraph(actorFound.getValue());

    // Build corresponding hierarchical schedule
    HierarchicalSchedule schedule = buildHierarchicalSchedule(actorFound);

    // Attach cluster to hierarchical schedule
    schedule.setAttachedActor(cluster);

    // Register the new cluster with it corresponding key
    scheduleMapping.put(cluster, schedule);
  }

  /**
   * @param actorFound
   *          schedule of actor to add to the hierarchical schedule
   * @param repetitionVector
   *          repetition vector corresponding to the graph
   * @param scheduleMapping
   *          scheduling mapping of current clustering process
   * @return hierarchical schedule created
   */
  private final HierarchicalSchedule buildHierarchicalSchedule(Pair<ScheduleType, List<AbstractActor>> actorFound) {

    // Create parallel or sequential schedule
    HierarchicalSchedule schedule = null;
    switch (actorFound.getKey()) {
      case Sequential:
        schedule = ScheduleFactory.eINSTANCE.createSequentialHiearchicalSchedule();
        break;
      case Parallel:
        schedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
        break;
      default:
        throw new PreesmRuntimeException("ClusteringBuilder: Unknown type of schedule");
    }

    // Retrieve actor list
    List<AbstractActor> actorList = actorFound.getValue();

    // Compute rvCluster
    long clusterRepetition = PiSDFMergeabilty.computeGcdRepetition(actorList, repetitionVector);

    // Construct a sequential schedule
    for (AbstractActor a : actorList) {
      addActorToHierarchicalSchedule(schedule, a, repetitionVector.get(a) / clusterRepetition);
    }

    return schedule;
  }

  /**
   * @param schedule
   *          schedule to add element to
   * @param actor
   *          actor to add to the schedule
   * @param repetition
   *          repetition corresponding to the actor
   * @return
   */
  private final void addActorToHierarchicalSchedule(HierarchicalSchedule schedule, AbstractActor actor,
      long repetition) {
    // If we already had clustered actor, retrieve it schedule
    if (scheduleMapping.containsKey(actor)) {
      Schedule subSched = scheduleMapping.get(actor);
      scheduleMapping.remove(actor);
      subSched.setRepetition(repetition);
      schedule.getScheduleTree().add(subSched);
    } else {
      ActorSchedule actorSchedule = null;
      // If actor is delayed, build a sequential actor schedule, otherwise build a parallel actor schedule
      if (ClusteringHelper.isActorDelayed(actor)) {
        actorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
      } else {
        actorSchedule = ScheduleFactory.eINSTANCE.createParallelActorSchedule();
      }
      actorSchedule.setRepetition(repetition);
      actorSchedule.getActorList().add(PreesmCopyTracker.getSource(actor));
      schedule.getScheduleTree().add(actorSchedule);
    }
  }

  /**
   * @param actorList
   *          list of actor to clusterize
   * @return generated PiGraph connected with the parent graph
   */
  private final PiGraph buildClusterGraph(List<AbstractActor> actorList) {
    // Create the cluster actor and set it name
    PiGraph cluster = PiMMUserFactory.instance.createPiGraph();
    cluster.setName("cluster_" + this.nbCluster++);
    cluster.setUrl(pigraph.getUrl() + "/" + cluster.getName() + ".pi");

    // Add cluster to the parent graph
    pigraph.addActor(cluster);
    for (AbstractActor a : actorList) {
      cluster.addActor(a);
    }

    // Compute clusterRepetition
    long clusterRepetition = PiSDFMergeabilty.computeGcdRepetition(actorList, repetitionVector);

    int nbOut = 0;
    int nbIn = 0;
    // Export ports on cluster actor
    for (AbstractActor a : actorList) {
      // Retrieve actor repetition number
      long actorRepetition = repetitionVector.get(a);

      // Attach DataInputPort on the cluster actor
      List<DataInputPort> dipTmp = new ArrayList<>();
      dipTmp.addAll(a.getDataInputPorts());
      for (DataInputPort dip : dipTmp) {
        // We only deport the output if FIFO is not internal
        if (!actorList.contains(dip.getIncomingFifo().getSourcePort().getContainingActor())) {
          setDataInputPortAsHInterface(cluster, dip, "in_" + nbIn++,
              dip.getExpression().evaluate() * actorRepetition / clusterRepetition);
        } else {
          cluster.addFifo(dip.getIncomingFifo());
          Delay delay = dip.getIncomingFifo().getDelay();
          if (delay != null) {
            cluster.addDelay(delay);
          }
        }
      }

      // Attach DataOutputPort on the cluster actor
      List<DataOutputPort> dopTmp = new ArrayList<>();
      dopTmp.addAll(a.getDataOutputPorts());
      for (DataOutputPort dop : dopTmp) {
        // We only deport the output if FIFO is not internal
        if (!actorList.contains(dop.getOutgoingFifo().getTargetPort().getContainingActor())) {
          setDataOutputPortAsHInterface(cluster, dop, "out_" + nbOut++,
              dop.getExpression().evaluate() * actorRepetition / clusterRepetition);
        } else {
          cluster.addFifo(dop.getOutgoingFifo());
          Delay delay = dop.getOutgoingFifo().getDelay();
          if (delay != null) {
            cluster.addDelay(delay);
          }
        }
      }
    }

    // Attach ConfigInputPort on the cluster actor
    List<ConfigInputPort> cfgipTmp = new ArrayList<>();
    for (AbstractActor a : actorList) {
      cfgipTmp.addAll(a.getConfigInputPorts());
    }
    for (Delay delay : cluster.getAllDelays()) {
      cfgipTmp.addAll(delay.getConfigInputPorts());
      delay.setExpression(delay.getExpression().evaluate());
    }
    int nbCfg = 0;
    for (ConfigInputPort cfgip : cfgipTmp) {
      setConfigInputPortAsHInterface(cluster, cfgip, "config_" + nbCfg++);
    }

    return cluster;
  }

  /**
   * @param newHierarchy
   *          new hierarchy
   * @param insideInputPort
   *          DataInputPort to connect outside
   * @param name
   *          name of port
   * @param newExpression
   *          prod/cons value
   */
  private final DataInputInterface setDataInputPortAsHInterface(PiGraph newHierarchy, DataInputPort insideInputPort,
      String name, long newExpression) {
    // Setup DataInputInterface
    DataInputInterface inputInterface = PiMMUserFactory.instance.createDataInputInterface();
    inputInterface.setName(name);
    inputInterface.getDataPort().setName(name);
    newHierarchy.addActor(inputInterface);

    // Setup input of hierarchical actor
    DataInputPort inputPort = (DataInputPort) inputInterface.getGraphPort();
    inputPort.setName(name); // same name than DataInputInterface
    inputPort.setExpression(newExpression);

    // Interconnect the outside with hierarchical actor
    inputPort.setIncomingFifo(PiMMUserFactory.instance.createFifo());
    newHierarchy.getContainingPiGraph().addFifo(inputPort.getFifo());
    Fifo oldFifo = insideInputPort.getFifo();
    if (oldFifo.getDelay() != null) {
      inputPort.getFifo().setDelay(oldFifo.getDelay());
    }
    String dataType = oldFifo.getType();
    inputPort.getIncomingFifo().setSourcePort(oldFifo.getSourcePort());
    inputPort.getIncomingFifo().setType(dataType);
    newHierarchy.getContainingPiGraph().removeFifo(oldFifo); // remove FIFO from containing graph

    // Setup inside communication with DataInputInterface
    DataOutputPort outputDataPort = (DataOutputPort) inputInterface.getDataPort();
    outputDataPort.setExpression(newExpression);
    outputDataPort.setOutgoingFifo(PiMMUserFactory.instance.createFifo());
    outputDataPort.getOutgoingFifo().setTargetPort(insideInputPort);
    outputDataPort.getOutgoingFifo().setType(dataType);
    inputInterface.getDataOutputPorts().add(outputDataPort);
    newHierarchy.addFifo(outputDataPort.getFifo());

    return inputInterface;
  }

  /**
   * @param newHierarchy
   *          new hierarchy
   * @param insideOutputPort
   *          DataOutputPort to connect outside
   * @param name
   *          name of port
   * @param newExpression
   *          prod/cons value
   */
  private final DataOutputInterface setDataOutputPortAsHInterface(PiGraph newHierarchy, DataOutputPort insideOutputPort,
      String name, long newExpression) {
    // Setup DataOutputInterface
    DataOutputInterface outputInterface = PiMMUserFactory.instance.createDataOutputInterface();
    outputInterface.setName(name);
    outputInterface.getDataPort().setName(name);
    newHierarchy.addActor(outputInterface);

    // Setup output of hierarchical actor
    DataOutputPort outputPort = (DataOutputPort) outputInterface.getGraphPort();
    outputPort.setName(name); // same name than DataOutputInterface
    outputPort.setExpression(newExpression);

    // Interconnect the outside with hierarchical actor
    outputPort.setOutgoingFifo(PiMMUserFactory.instance.createFifo());
    newHierarchy.getContainingPiGraph().addFifo(outputPort.getFifo());
    Fifo oldFifo = insideOutputPort.getFifo();
    if (oldFifo.getDelay() != null) {
      outputPort.getFifo().setDelay(oldFifo.getDelay());
    }
    String dataType = oldFifo.getType();
    outputPort.getOutgoingFifo().setTargetPort(oldFifo.getTargetPort());
    outputPort.getOutgoingFifo().setType(dataType);
    newHierarchy.getContainingPiGraph().removeFifo(oldFifo); // remove FIFO from containing graph

    // Setup inside communication with DataOutputInterface
    DataInputPort inputDataPort = (DataInputPort) outputInterface.getDataPort();
    inputDataPort.setExpression(newExpression);
    inputDataPort.setIncomingFifo(PiMMUserFactory.instance.createFifo());
    inputDataPort.getIncomingFifo().setSourcePort(insideOutputPort);
    inputDataPort.getIncomingFifo().setType(dataType);
    outputInterface.getDataInputPorts().add(inputDataPort);
    newHierarchy.addFifo(inputDataPort.getFifo());

    return outputInterface;
  }

  /**
   * @param newHierarchy
   *          new hierarchy
   * @param insideInputPort
   *          ConfigInputPort to connect outside
   * @return generated ConfigInputInterface
   */
  private final ConfigInputInterface setConfigInputPortAsHInterface(PiGraph newHierarchy,
      ConfigInputPort insideInputPort, String name) {
    // Setup ConfigInputInterface
    ConfigInputInterface inputInterface = PiMMUserFactory.instance.createConfigInputInterface();
    inputInterface.setName(name);
    newHierarchy.addParameter(inputInterface);

    // Setup input of hierarchical actor
    ConfigInputPort inputPort = inputInterface.getGraphPort();
    inputPort.setName(name); // same name than ConfigInputInterface

    // Interconnect the outside with hierarchical actor
    inputPort.setIncomingDependency(PiMMUserFactory.instance.createDependency());
    newHierarchy.getContainingPiGraph().addDependency(inputPort.getIncomingDependency());
    Dependency oldDependency = insideInputPort.getIncomingDependency();
    inputPort.getIncomingDependency().setSetter(oldDependency.getSetter());

    // Setup inside communication with ConfigInputInterface
    Dependency dependency = insideInputPort.getIncomingDependency();
    dependency.setSetter(inputInterface);
    newHierarchy.addDependency(dependency);

    return inputInterface;
  }

}

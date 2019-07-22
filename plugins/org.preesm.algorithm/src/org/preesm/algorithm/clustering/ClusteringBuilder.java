package org.preesm.algorithm.clustering;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.algorithm.schedule.ActorSchedule;
import org.preesm.model.algorithm.schedule.HierarchicalSchedule;
import org.preesm.model.algorithm.schedule.ParallelHiearchicalSchedule;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.algorithm.schedule.ScheduleFactory;
import org.preesm.model.algorithm.schedule.SequentialHiearchicalSchedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiGraphConsistenceChecker;

/**
 * @author dgageot
 *
 */
public class ClusteringBuilder {

  private Map<AbstractActor, Schedule> scheduleMapping;

  private PiGraph algorithm;

  private IClusteringAlgorithm clusteringAlgorithm;

  private Map<AbstractVertex, Long> repetitionVector;

  /**
   * @param algorithm
   *          PiGraph to clusterize
   * @param clusteringAlgorithm
   *          type of clustering algorithm
   */
  public ClusteringBuilder(final PiGraph algorithm, final String clusteringAlgorithm) {
    this.scheduleMapping = new LinkedHashMap<>();
    this.algorithm = algorithm;
    this.clusteringAlgorithm = clusteringAlgorithmFactory(clusteringAlgorithm);
    this.repetitionVector = null;
  }

  public PiGraph getAlgorithm() {
    return algorithm;
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
    // Until the algorithm has to work
    while (!clusteringAlgorithm.clusteringComplete(this)) {
      // Compute BRV with the corresponding graph
      repetitionVector = PiBRV.compute(algorithm, BRVMethod.LCM);
      // Search actors to clusterize
      Pair<ScheduleType, List<AbstractActor>> actorFound = clusteringAlgorithm.findActors(this);
      // Clusterize given actors
      clusterizeActors(actorFound);
    }

    // Exhibit data parallelism
    exhibitDataParallelism();

    // Verify consistency of result graph
    PiGraphConsistenceChecker.check(algorithm);

    return scheduleMapping;
  }

  private final void exhibitDataParallelism() {
    for (Entry<AbstractActor, Schedule> entry : scheduleMapping.entrySet()) {
      Schedule schedule = entry.getValue();
      schedule = performDataParallelismExhibition(schedule);
      scheduleMapping.replace(entry.getKey(), schedule);
    }
  }

  private final Schedule performDataParallelismExhibition(Schedule schedule) {

    if (schedule instanceof SequentialHiearchicalSchedule) {

      // if data parallelism can be exhibited
      if ((schedule.getRepetition() > 1) && !ClusteringHelper.isSequentialActorScheduleInside(schedule)) {
        ParallelHiearchicalSchedule parallelSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
        parallelSchedule.setRepetition(schedule.getRepetition());
        schedule.setRepetition(1);
        parallelSchedule.getChildren().add(schedule);
        return parallelSchedule;
      }

      // Explore childrens
      List<Schedule> listActor = new LinkedList<>();
      listActor.addAll(schedule.getChildren());
      for (Schedule child : listActor) {
        int indexOfChild = listActor.indexOf(child);
        Schedule newSched = performDataParallelismExhibition(child);
        if (!schedule.getChildren().contains(newSched)) {
          schedule.getChildren().add(newSched);
          schedule.getChildren().move(indexOfChild, schedule.getChildren().indexOf(newSched));
        }
      }
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
        return new RandomClusteringAlgorithm(System.currentTimeMillis());
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
    long clusterRepetition = ClusteringHelper.computeGcdRepetition(actorList, repetitionVector);

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
    if (scheduleMapping.containsKey(actor)) {
      Schedule subSched = scheduleMapping.get(actor);
      scheduleMapping.remove(actor);
      subSched.setRepetition(repetition);
      schedule.getScheduleTree().add(subSched);
    } else {
      ActorSchedule actorSchedule = null;
      // If actor is self looped, it is a sequential actor schedule
      if (ClusteringHelper.isActorSelfLooped(actor)) {
        actorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
      } else {
        actorSchedule = ScheduleFactory.eINSTANCE.createParallelActorSchedule();
      }
      actorSchedule.setRepetition(repetition);
      actorSchedule.getActorList().add(actor);
      schedule.getScheduleTree().add(actorSchedule);
    }
  }

  /**
   * @param actorList
   *          list of actor to clusterize
   * @return generated PiGraph connected with the parent graph
   */
  private final PiGraph buildClusterGraph(List<AbstractActor> actorList) {
    // Set cluster name to concatenated name of all actor involve in the list
    StringBuilder clusterName = new StringBuilder();
    for (AbstractActor a : actorList) {
      clusterName.append(a.getName());
      clusterName.append("_");
    }
    clusterName.deleteCharAt(clusterName.length() - 1);

    // Create the cluster actor and set it name
    PiGraph cluster = PiMMUserFactory.instance.createPiGraph();
    cluster.setName(clusterName.toString());
    cluster.setUrl(algorithm.getUrl() + "/" + cluster.getName() + ".pi");

    // Add cluster to the parent graph and remove actors from parent
    algorithm.addActor(cluster);
    for (AbstractActor a : actorList) {
      cluster.addActor(a);
    }

    // Compute clusterRepetition
    long clusterRepetition = ClusteringHelper.computeGcdRepetition(actorList, repetitionVector);

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
        }
      }
    }

    // Attach ConfigInputPort on the cluster actor
    List<ConfigInputPort> cfgipTmp = new ArrayList<>();
    for (AbstractActor a : actorList) {
      cfgipTmp.addAll(a.getConfigInputPorts());
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

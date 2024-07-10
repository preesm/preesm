/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Mickaël Dardaillon [mickael.dardaillon@insa-rennes.fr] (2020)
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
package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.eclipse.emf.common.util.ECollections;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.mapping.model.MappingFactory;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.timer.AgnosticTimer;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.PeriodicElement;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.AbstractActorNameComparator;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.check.SlamDesignPEtypeChecker;

/**
 * This scheduler handles the periods defined in the PiGraph and in its actors. However, it does not take into account
 * communication time.
 * <p>
 * For more details, see conference paper "Scheduling of Synchronous Dataflow Graphs with Partially Periodic Real-Time
 * Constraints", published at RTNS 2020 (DOI 10.1145/3394810.3394820).
 *
 * @author ahonorat
 */
public class PeriodicScheduler extends AbstractScheduler {

  /**
   *
   * @author ahonorat
   *
   */
  protected static class VertexAbstraction {

    // Actual start time
    long startTime;
    // Real max finish time of all predecessors
    long predFinishTime;
    // maximum start time if unlimited cores
    long maxStartTime;
    // minimum start time if unlimited cores
    long minStartTime;
    // average of minStartTime and maxStartTime
    long averageStartTime;

    // execution time
    long load;
    // how many times this node has been reached during graph traversal
    int nbVisits;
    // is it a periodic actor?
    boolean isPeriodic;
    // actor in SRDAG
    AbstractActor aa;
    // actor in original PiGraph
    AbstractActor ori;

    protected VertexAbstraction(AbstractActor aa) {
      this.aa = aa;
      this.ori = PreesmCopyTracker.getOriginalSource(aa);

      this.load = 0;
      this.nbVisits = 0;
      this.isPeriodic = false;

      this.startTime = 0;
      this.predFinishTime = 0;
      this.maxStartTime = 0;
      this.minStartTime = 0;
      this.averageStartTime = 0;
    }
  }

  /**
   *
   * @author ahonorat
   *
   */
  protected static class EdgeAbstraction {
    long weight;// not used

    protected EdgeAbstraction() {
      this.weight = 0;
    }
  }

  /**
   *
   * @author ahonorat
   *
   * @throws PreesmSchedulingException
   *           If scheduling fails.
   *
   */
  protected static class CoreAbstraction {

    // finish time of the last task mapped on the core
    long implTime;
    // related component instance in SLAM design
    ComponentInstance ci;
    // schedule ordering of actors mapped to this core
    ActorSchedule coreSched;

    protected CoreAbstraction(ComponentInstance ci, ActorSchedule coreSched) {
      this.implTime = 0;
      this.ci = ci;
      this.coreSched = coreSched;
    }

  }

  protected PiGraph  piGraph;
  protected Design   slamDesign;
  protected Scenario scenario;

  protected DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph;
  protected List<VertexAbstraction>                                  firstNodes; // sources
  protected List<VertexAbstraction>                                  lastNodes;  // sinks

  protected HierarchicalSchedule            topParallelSchedule; // main schedule
  protected Mapping                         resultMapping;       // main mapping
  Map<ComponentInstance, CoreAbstraction>   ciTOca;              // map of SLAM operator components to CoreAbstraction
  Map<AbstractActor, List<CoreAbstraction>> possibleMappings;    // list of allowed core for each original PiGraph actor
  protected List<CoreAbstraction>           cores;               // sorted list of Cores (smallest implTime first)
  protected CoreAbstraction                 defaultCore;         // default component operator in SLAM design

  protected long horizon;            // deadline of the whole schedule
  protected long cTot;               // total load
  protected long cMax;               // maximum load of a single firing
  protected long graphPeriod;        // period of the graph
  protected int  nbFiringsAllocated; // index for allocation check

  protected AgnosticTimer st;

  /**
   * Init public values (reset by {@link exec} method).
   */
  public PeriodicScheduler() {
    cTot = 0L;
    cMax = 0L;
    graphPeriod = 0L;
    cores = null;
  }

  /**
   * Total load of the last schedule attempt.
   *
   * @return Sum of all firing execution times (except special actors).
   */
  public long getTotalLoad() {
    return cTot;
  }

  /**
   * Maximal load of a single firing in the last schedule attempt.
   *
   * @return Maximum firing execution time (except special actors).
   */
  public long getMaximalFiringLoad() {
    return cMax;
  }

  /**
   * Finish time of the last firing.
   *
   * @return Finish time of the last firing, or 0 if not yet computed.
   */
  public long getLastEndTime() {
    if (cores == null) {
      return 0;
    }
    long maxEnd = 0;
    for (final CoreAbstraction ca : cores) {
      maxEnd = Math.max(maxEnd, ca.implTime);
    }
    return maxEnd;
  }

  /**
   * Period of the graph (as in the input graph).
   *
   * @return Graph period or 0 if no graph period.
   */
  public long getGraphPeriod() {
    return graphPeriod;
  }

  @Override
  protected SynthesisResult exec(PiGraph piGraph, Design slamDesign, Scenario scenario) {

    if (!SlamDesignPEtypeChecker.isHomogeneousCPU(slamDesign)) {
      throw new PreesmSchedulingException("This task must be called with a homogeneous CPU architecture, abandon.");
    }

    final int nbCore = slamDesign.getProcessingElements().get(0).getInstances().size();
    PreesmLogger.getLogger().info(() -> "Found " + nbCore + " cores.");

    graphPeriod = piGraph.getPeriod().evaluate();
    PreesmLogger.getLogger().info(() -> "Graph period is: " + graphPeriod);

    final long time = System.nanoTime();

    this.piGraph = piGraph;
    this.slamDesign = slamDesign;
    this.scenario = scenario;
    // TODO test if AgnosticTimer or SimplerTimer (with special actors time) is better
    this.st = new AgnosticTimer(scenario, 1L);

    nbFiringsAllocated = 0;
    // initializes component operators and related attributes
    cores = new ArrayList<>();
    ciTOca = new HashMap<>();
    possibleMappings = new TreeMap<>(new AbstractActorNameComparator());
    topParallelSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
    resultMapping = MappingFactory.eINSTANCE.createMapping();
    for (final ComponentInstance ci : slamDesign.getProcessingElements().get(0).getInstances()) {
      final ActorSchedule createActorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
      topParallelSchedule.getScheduleTree().add(createActorSchedule);
      final CoreAbstraction ca = new CoreAbstraction(ci, createActorSchedule);
      cores.add(ca);
      ciTOca.put(ci, ca);
      if (ci.equals(scenario.getSimulationInfo().getMainOperator())) {
        defaultCore = ca;
      }
    }

    // create abstraction of the input SRDAG, and initializes min/maxStartTime of periodic actors
    createAbsGraph();

    PreesmLogger.getLogger().info(() -> "Starting to schedule and map " + absGraph.vertexSet().size() + " actors.");

    // initializes total load, source and sinks nodes
    cTot = 0;
    cMax = 0;
    firstNodes = new ArrayList<>();
    lastNodes = new ArrayList<>();
    for (final VertexAbstraction va : absGraph.vertexSet()) {
      cTot += va.load;
      cMax = Math.max(cMax, va.load);
      if (absGraph.incomingEdgesOf(va).isEmpty()) {
        firstNodes.add(va);
      }
      if (absGraph.outgoingEdgesOf(va).isEmpty()) {
        lastNodes.add(va);
      }
    }

    // initializes and check horizon
    horizon = graphPeriod;
    if (horizon <= 0) {
      horizon = cTot;
      PreesmLogger.getLogger()
          .info(() -> "No period found: scheduling performed with sequential worst case: " + cTot + " time unit.");
    } else if (cTot / (double) nbCore > horizon) {
      throw new PreesmSchedulingException(
          "Your graph is clearly not schedulable: utilization factor is higher than number of cores. Total load: "
              + cTot);
    }
    // initializes min/max/averageStartTime of all actors (and updates the periodic one)
    setAbsGraph(absGraph, horizon, firstNodes, lastNodes);

    // schedule all nodes in absGraph
    schedule();

    // get the implementation end time
    long maxImpl = 0;
    for (final CoreAbstraction ca : cores) {
      if (ca.implTime > maxImpl) {
        maxImpl = ca.implTime;
      }
    }

    final long duration = System.nanoTime() - time;
    PreesmLogger.getLogger().info(() -> "Time+ " + Math.round(duration / 1e6) + " ms.");

    PreesmLogger.getLogger()
        .info("Periodic scheduler found an implementation time of: " + maxImpl + " (not considering communications)");

    return new SynthesisResult(resultMapping, topParallelSchedule, null);
  }

  /**
   * Compute abstract graph from the PiGraph SRDAG input. Also, initializes the load, the possible mapping, and the
   * min/maxStartTime of actors.
   *
   * @return New abstract graph of the SRADG.
   * @throws PreesmSchedulingException
   *           If remaining idle time is greater than maximum allowed.
   */
  protected DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> createAbsGraph() {
    absGraph = new DefaultDirectedGraph<>(EdgeAbstraction.class);

    final Map<AbstractActor, VertexAbstraction> aaTOva = new TreeMap<>(new AbstractActorNameComparator());
    final Map<AbstractActor, Long> loadMemoization = new TreeMap<>(new AbstractActorNameComparator());
    // copy actors of input PiGraph
    for (final AbstractActor aa : piGraph.getActors()) {
      final VertexAbstraction va = new VertexAbstraction(aa);
      absGraph.addVertex(va);
      aaTOva.put(aa, va);

      // store load, with memoization over original actors
      final AbstractActor originalActor = va.ori;
      if (!loadMemoization.containsKey(originalActor)) {
        final long load = st.doSwitch(originalActor);
        // long load = getLoad(originalActor, slamDesign, scenario);
        loadMemoization.put(originalActor, load);
        va.load = load;
      } else {
        va.load = loadMemoization.get(originalActor);
      }
      // store allowed mappings, with memoization over original actors
      if (!possibleMappings.containsKey(originalActor)) {
        final List<ComponentInstance> cis = scenario.getPossibleMappings(originalActor);
        final List<CoreAbstraction> cas = new ArrayList<>();

        cis.forEach(ci -> cas.add(ciTOca.get(ci)));
        if (cas.isEmpty()) {
          cas.add(defaultCore);
        }
        possibleMappings.put(originalActor, cas);
      }
      // initializes the min/maxStartTime of periodic actors from firing instance number
      if (aa instanceof final PeriodicElement pe) {
        final long period = pe.getPeriod().evaluate();
        if (period > 0 && pe instanceof final Actor a) {
          va.isPeriodic = true;
          final long firingInstance = a.getFiringInstance();
          final long ns = firingInstance * period;
          va.minStartTime = ns;
          va.maxStartTime = (firingInstance + 1) * period;
        }
      }

    }

    // copy fifos
    for (final Fifo f : piGraph.getFifos()) {
      final DataOutputPort dop = f.getSourcePort();
      final DataInputPort dip = f.getTargetPort();

      final AbstractActor aaSrc = dop.getContainingActor();
      final AbstractActor aaTgt = dip.getContainingActor();

      final VertexAbstraction vaSrc = aaTOva.get(aaSrc);
      final VertexAbstraction vaTgt = aaTOva.get(aaTgt);

      EdgeAbstraction fa = absGraph.getEdge(vaSrc, vaTgt);
      if (fa == null) {
        fa = new EdgeAbstraction();
        final boolean res = absGraph.addEdge(vaSrc, vaTgt, fa);
        if (!res) {
          throw new PreesmSchedulingException("Problem while creating graph copy.");
        }
      }
    }

    // TODO check if DAG? should be done by AbstractScheduler if so

    return absGraph;
  }

  /**
   * Get execution time from scenario (cores are considered as homogeneous).
   *
   * @param actor
   *          Actor to consider (from SRDAG or original PiGraph).
   * @param slamDesign
   *          Architecture.
   * @param scenario
   *          Scenario.
   * @return Actor execution time.
   */
  protected long getLoad(AbstractActor actor, Design slamDesign, Scenario scenario) {
    long wcet = ScenarioConstants.DEFAULT_TIMING_TASK.getValue();
    for (final Component operatorDefinitionID : slamDesign.getProcessingElements()) {
      wcet = scenario.getTimings().evaluateExecutionTimeOrDefault(actor, operatorDefinitionID);
    }
    return wcet;
  }

  /**
   *
   *
   * @param absGraph
   *          Graph abstraction of SRDAG.
   * @param horizon
   *          Deadline of the graph execution.
   * @param firstNodes
   *          Source nodes of the graph.
   * @param lastNodes
   *          Sink nodes of the graph.
   * @throws PreesmSchedulingException
   *           If remaining idle time is greater than maximum allowed.
   */
  protected static void setAbsGraph(DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph, long horizon,
      List<VertexAbstraction> firstNodes, List<VertexAbstraction> lastNodes) {
    for (final VertexAbstraction va : absGraph.vertexSet()) {
      if (!va.isPeriodic) {
        va.maxStartTime = horizon;
      }
    }
    // set min start time
    List<VertexAbstraction> toVisit = new LinkedList<>(firstNodes);
    while (!toVisit.isEmpty()) {
      final VertexAbstraction va = toVisit.remove(0);
      final long succns = va.minStartTime + va.load;
      for (final EdgeAbstraction ea : absGraph.outgoingEdgesOf(va)) {
        final VertexAbstraction oppositeva = absGraph.getEdgeTarget(ea);
        oppositeva.minStartTime = Math.max(oppositeva.minStartTime, succns);
        updateNbVisits(absGraph, oppositeva, false, toVisit);
      }
    }
    // set max and average start time
    toVisit = new LinkedList<>(lastNodes);
    while (!toVisit.isEmpty()) {
      final VertexAbstraction va = toVisit.remove(0);
      va.predFinishTime = va.minStartTime;
      va.maxStartTime -= va.load;
      if (va.minStartTime > va.maxStartTime) {
        throw new PreesmSchedulingException(
            "Cannot schedule following firing, min start time > max start time: " + va.aa.getName());
      }
      va.averageStartTime = (va.minStartTime + va.maxStartTime) / 2;
      final long predxs = va.maxStartTime;
      for (final EdgeAbstraction ea : absGraph.incomingEdgesOf(va)) {
        final VertexAbstraction oppositeva = absGraph.getEdgeSource(ea);
        oppositeva.maxStartTime = Math.min(oppositeva.maxStartTime, predxs);
        updateNbVisits(absGraph, oppositeva, true, toVisit);
      }
    }

  }

  /**
   * Updates number of visits of a node, and adds it to the queue if all its predecessors have also been visited.
   * <p>
   * The nbVisits attribute is reset to 0 if added to the queue.
   *
   * @param absGraph
   *          Graph to consider.
   * @param va
   *          Node of the graph.
   * @param reverse
   *          In normal order (false) or reverse order (true).
   * @param queue
   *          Queue to add new ready nodes.
   */
  protected static void updateNbVisits(DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph,
      VertexAbstraction va, boolean reverse, List<VertexAbstraction> queue) {
    va.nbVisits += 1;
    Set<EdgeAbstraction> seteas = null;
    if (reverse) {
      seteas = absGraph.outgoingEdgesOf(va);
    } else {
      seteas = absGraph.incomingEdgesOf(va);
    }
    if (va.nbVisits == seteas.size()) {
      queue.add(va);
      va.nbVisits = 0;
    }
  }

  /**
   * Schedule the abstract graph of SRDAG.
   */
  protected void schedule() {
    final long dualCtot = horizon * cores.size() - cTot;
    long emptyTime = 0;

    // first add all source nodes in the queue
    final List<VertexAbstraction> queue = new LinkedList<>();
    for (final VertexAbstraction va : firstNodes) {
      insertTaskInScheduleQueue(va, queue);
    }

    // empty the queue
    while (!queue.isEmpty()) {
      final VertexAbstraction va = queue.get(0);
      final int previousAllocations = nbFiringsAllocated;
      if (isThereACoreIdlingBefore(cores, va.predFinishTime)) {
        // manage empty space
        for (final VertexAbstraction vab : possibleAllocationBefore(queue, cores, va.predFinishTime)) {
          emptyTime = allocateAndRemoveIfBefore(vab, queue, emptyTime, dualCtot, va.predFinishTime);
        }
        if (nbFiringsAllocated > previousAllocations) {
          continue;
        }
      }
      emptyTime = allocateAndRemove(va, queue, emptyTime, dualCtot);
    }

  }

  /**
   * Computes if idle time will occur if next execution fires at deadline.
   *
   * @param cores
   *          List of cores, sorted by least implTime first.
   * @param deadline
   *          Next start time to consider.
   * @return Wheter or not idle time will occur before deadline.
   */
  protected static boolean isThereACoreIdlingBefore(List<CoreAbstraction> cores, long deadline) {
    return cores.get(0).implTime < deadline;
  }

  /**
   * Compute list of nodes that could be executed before the one at deadline.
   *
   * @param queue
   *          Current nodes ready for execution.
   * @param cores
   *          List of cores, sorted by least implTime first.
   * @param deadline
   *          Next execution start time.
   * @return List of nodes able to start before deadline, sorted by increasing average start time.
   */
  protected static List<VertexAbstraction> possibleAllocationBefore(List<VertexAbstraction> queue,
      List<CoreAbstraction> cores, long deadline) {
    final List<VertexAbstraction> res = new LinkedList<>();
    // compute idle time
    long emptyLoad = 0;
    for (final CoreAbstraction ca : cores) {
      if (ca.implTime < deadline) {
        emptyLoad += deadline - ca.implTime;
      }
    }
    // select all tasks that could fit without exceeding idle time nor deadline
    long currentLoad = 0;
    for (final VertexAbstraction va : queue) {
      if (va.predFinishTime + va.load < deadline) {
        insertTaskInScheduleQueue(va, res);
        currentLoad += va.load;
      }
      if (currentLoad > emptyLoad) {
        break;
      }
    }

    return res;
  }

  /**
   * Allocate node and remove it from queue.
   *
   * @param va
   *          Node to consider.
   * @param queue
   *          Ready to schedule nodes.
   * @param emptyTime
   *          Current available idle time until horizon.
   * @param loadDual
   *          Maximum idle time until horizon.
   * @return Remaining idle time until horizon.
   */
  protected long allocateAndRemove(VertexAbstraction va, List<VertexAbstraction> queue, long emptyTime, long loadDual) {
    return allocateAndRemoveIfBefore(va, queue, emptyTime, loadDual, 0);
  }

  /**
   * Allocate node and remove it from queue.
   * <p>
   * Allocates only if finish time is before deadline.
   *
   * @param va
   *          Node to consider.
   * @param queue
   *          Ready to schedule nodes.
   * @param emptyTime
   *          Current available idle time until horizon.
   * @param loadDual
   *          Maximum idle time until horizon.
   * @param deadline
   *          Maximum finish time, or 0 if no limit.
   *
   * @return Remaining idle time until horizon.
   * @throws PreesmSchedulingException
   *           If remaining idle time is greater than maximum allowed.
   */
  protected long allocateAndRemoveIfBefore(VertexAbstraction va, List<VertexAbstraction> queue, long emptyTime,
      long loadDual, long deadline) {

    final CoreAbstraction ca = popFirstPossibleCore(va, cores, possibleMappings);
    // check start time
    if (ca == null || ca.implTime > va.maxStartTime) {
      throw new PreesmSchedulingException(
          "Could not allocate the following task, no component or start time is overdue:  " + va.aa.getName());
    }
    // check deadline
    final long startTime = Math.max(va.predFinishTime, ca.implTime);
    if (deadline > 0 && startTime + va.load > deadline) {
      insertCoreInImplOrder(ca, cores);
      return emptyTime;
    }
    // update attributes
    nbFiringsAllocated++;
    ca.coreSched.getActorList().add(va.aa);
    if (va.aa instanceof final InitActor initActor) {
      // once init actor is mapped, we force the opposite end to be mapped on the same core
      final AbstractActor endReference = initActor.getEndReference();
      final List<CoreAbstraction> uniqueEndMapping = new ArrayList<>();
      uniqueEndMapping.add(ca);
      possibleMappings.put(endReference, uniqueEndMapping);
    }
    resultMapping.getMappings().put(va.aa, ECollections.singletonEList(ca.ci));
    va.startTime = startTime;
    final long extraIdleTime = va.startTime - ca.implTime;
    ca.implTime = va.startTime + va.load;
    insertCoreInImplOrder(ca, cores);
    queue.remove(va);
    // update the queue since the node has been visited
    updateAllocationNbVisits(absGraph, va, queue, ca.implTime);
    return casRemainingLoad(extraIdleTime, loadDual, emptyTime);
  }

  /**
   * Selects the least loaded core which can execute the node.
   *
   * @param va
   *          Node to consider.
   * @param cores
   *          List of cores, sorted by least implTime first.
   * @param possibleMappings
   *          Map of allowed mappings (per original actor).
   * @return The core to map the node, or {@code null} if none is available.
   */
  protected static CoreAbstraction popFirstPossibleCore(VertexAbstraction va, List<CoreAbstraction> cores,
      Map<AbstractActor, List<CoreAbstraction>> possibleMappings) {
    final List<CoreAbstraction> posmaps = possibleMappings.get(va.ori);
    final ListIterator<CoreAbstraction> it = cores.listIterator();
    while (it.hasNext()) {
      final CoreAbstraction ca = it.next();
      if (posmaps.contains(ca)) {
        it.remove();
        return ca;
      }
    }
    return null;
  }

  /**
   * Updates number of visits of a node, and adds it to the queue if all its predecessors have also been visited.
   * <p>
   * The nbVisits attribute is reset to 0 if added to the queue.
   *
   * @param absGraph
   *          Graph to consider.
   * @param va
   *          Node of the graph.
   * @param queue
   *          Queue to add new ready nodes, sorted by average start time.
   * @param finishTime
   *          Finish time of the allocated node (equals {@code va.startTime + va.load})
   */
  protected static void updateAllocationNbVisits(DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph,
      VertexAbstraction va, List<VertexAbstraction> queue, long finishTime) {
    // Here we could restrict the possible mappings of following fork/broadcast/join/roundbuffer
    // to be on the same core as their direct predecessor va
    // and inserts it first in ready queue
    // --> it has been tested, and results are actually worse
    for (final EdgeAbstraction ea : absGraph.outgoingEdgesOf(va)) {
      final VertexAbstraction tgt = absGraph.getEdgeTarget(ea);
      tgt.nbVisits += 1;
      tgt.predFinishTime = Math.max(finishTime, tgt.predFinishTime);
      final Set<EdgeAbstraction> seteas = absGraph.incomingEdgesOf(tgt);
      if (tgt.nbVisits == seteas.size()) {
        insertTaskInScheduleQueue(tgt, queue);
        tgt.nbVisits = 0;
      }
    }
  }

  // ascending as, and ascending ns if equality of as
  protected static void insertTaskInScheduleQueue(VertexAbstraction va, List<VertexAbstraction> queue) {
    final ListIterator<VertexAbstraction> it = queue.listIterator();
    while (it.hasNext()) {
      final VertexAbstraction next = it.next();
      if (next.averageStartTime < va.averageStartTime) {
        // performance trick, since this test is shorter in time, we do it first
        continue;
      }
      if (next.averageStartTime > va.averageStartTime
          || (next.averageStartTime == va.averageStartTime && next.minStartTime > va.minStartTime)) {
        it.previous();
        break;
      }
    }
    it.add(va);
  }

  // ascending implTime order
  protected static void insertCoreInImplOrder(CoreAbstraction ca, List<CoreAbstraction> order) {
    final ListIterator<CoreAbstraction> it = order.listIterator();
    while (it.hasNext()) {
      final CoreAbstraction next = it.next();
      if (next.implTime > ca.implTime) {
        it.previous();
        break;
      }
    }
    it.add(ca);
  }

  /**
   * Check if remaining idle time is greater than maximum allowed.
   * <p>
   * "cas" stands for "check and set"
   *
   * @param extraIdleTime
   *          Idle time generated by last allocation.
   * @param loadDual
   *          Maximum allowed idle time.
   * @param previousEmptyTime
   *          Idle time prior to the last allocation.
   * @return Current idle time until last allocation.
   *
   * @throws PreesmSchedulingException
   *           If remaining idle time is greater than maximum allowed.
   */
  protected static long casRemainingLoad(long extraIdleTime, long loadDual, long previousEmptyTime) {
    final long res = previousEmptyTime + extraIdleTime;
    if (res > loadDual) {
      throw new PreesmSchedulingException("Impossible schedule: there are too much unccupied space.");
    }
    return res;
  }

}

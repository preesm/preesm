package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import org.eclipse.emf.common.util.ECollections;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.mapping.model.MappingFactory;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.PeriodicElement;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * This scheduler handles the periods defined in the PiGraph and in its actors. However, it does not take into account
 * communication time.
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

    long startTime;
    long predFinishTime;
    long maxStartTime;
    long minStartTime;
    long averageStartTime;

    long    load;
    int     nbVisits;
    boolean isPeriodic;

    AbstractActor aa;
    AbstractActor ori;

    private VertexAbstraction(AbstractActor aa) {
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

    private EdgeAbstraction() {
      this.weight = 0;
    }
  }

  /**
   * 
   * @author ahonorat
   *
   */
  protected static class CoreAbstraction {

    long              implTime;
    ComponentInstance ci;
    ActorSchedule     coreSched;

    private CoreAbstraction(ComponentInstance ci, ActorSchedule coreSched) {
      this.implTime = 0;
      this.ci = ci;
      this.coreSched = coreSched;
    }

  }

  protected PiGraph  piGraph;
  protected Design   slamDesign;
  protected Scenario scenario;

  protected DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph;
  protected List<VertexAbstraction>                                  firstNodes;
  protected List<VertexAbstraction>                                  lastNodes;

  protected HierarchicalSchedule            topParallelSchedule;
  protected Mapping                         resultMapping;
  Map<ComponentInstance, CoreAbstraction>   ciTOca;
  Map<AbstractActor, List<CoreAbstraction>> possibleMappings;
  protected List<CoreAbstraction>           cores;
  protected CoreAbstraction                 defaultCore;

  protected long horizon;
  protected long Ctot;
  protected int  nbFiringsAllocated;

  @Override
  protected SynthesisResult exec(PiGraph piGraph, Design slamDesign, Scenario scenario) {

    if (slamDesign.getOperatorComponents().size() != 1) {
      throw new PreesmRuntimeException("This task must be called with a homogeneous architecture, abandon.");
    }

    int nbCore = slamDesign.getOperatorComponents().get(0).getInstances().size();
    PreesmLogger.getLogger().log(Level.INFO, "Found " + nbCore + " cores.");

    long graphPeriod = piGraph.getPeriod().evaluate();
    PreesmLogger.getLogger().log(Level.INFO, "Graph period is: " + graphPeriod);

    this.piGraph = piGraph;
    this.slamDesign = slamDesign;
    this.scenario = scenario;

    nbFiringsAllocated = 0;
    cores = new ArrayList<>();
    ciTOca = new HashMap<>();
    possibleMappings = new TreeMap<>(new ActorNameComparator());
    topParallelSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
    resultMapping = MappingFactory.eINSTANCE.createMapping();
    for (ComponentInstance ci : slamDesign.getOperatorComponents().get(0).getInstances()) {
      final ActorSchedule createActorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
      topParallelSchedule.getScheduleTree().add(createActorSchedule);
      CoreAbstraction ca = new CoreAbstraction(ci, createActorSchedule);
      cores.add(ca);
      ciTOca.put(ci, ca);
      if (ci.equals(scenario.getSimulationInfo().getMainOperator())) {
        defaultCore = ca;
      }
    }

    createAbsGraph();

    PreesmLogger.getLogger().log(Level.INFO,
        "Starting to schedule and map " + absGraph.vertexSet().size() + " actors.");

    Ctot = 0;
    firstNodes = new ArrayList<>();
    lastNodes = new ArrayList<>();
    for (VertexAbstraction va : absGraph.vertexSet()) {
      Ctot += va.load;
      if (absGraph.incomingEdgesOf(va).isEmpty()) {
        firstNodes.add(va);
      }
      if (absGraph.outgoingEdgesOf(va).isEmpty()) {
        lastNodes.add(va);
      }
    }

    horizon = piGraph.getPeriod().evaluate();
    if (horizon <= 0) {
      horizon = Ctot;
      PreesmLogger.getLogger().log(Level.INFO,
          "No period found: scheduling performed with sequential worst case: " + Ctot + " time unit.");
    } else if (Ctot / (double) nbCore > horizon) {
      throw new PreesmRuntimeException(
          "Your graph is clearly not schedulable: utilization factor is higher than number of cores. Total load: "
              + Ctot);
    }
    setAbsGraph(absGraph, horizon, firstNodes, lastNodes);

    schedule();

    long maxImpl = 0;
    for (CoreAbstraction ca : cores) {
      if (ca.implTime > maxImpl) {
        maxImpl = ca.implTime;
      }
    }
    PreesmLogger.getLogger().log(Level.INFO, "Periodic scheduler found an implementation time of: " + maxImpl);

    return new SynthesisResult(resultMapping, topParallelSchedule, null);
  }

  /**
   * Compare actors per name.
   * 
   * @author ahonorat
   */
  private static class ActorNameComparator implements Comparator<AbstractActor> {
    @Override
    public int compare(AbstractActor arg0, AbstractActor arg1) {
      return arg0.getName().compareTo(arg1.getName());
    }
  }

  protected DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> createAbsGraph() {
    absGraph = new DefaultDirectedGraph<>(EdgeAbstraction.class);

    Map<AbstractActor, VertexAbstraction> aaTOva = new TreeMap<>(new ActorNameComparator());
    Map<AbstractActor, Long> loadMemoization = new TreeMap<>(new ActorNameComparator());
    for (final AbstractActor aa : piGraph.getActors()) {
      VertexAbstraction va = new VertexAbstraction(aa);
      absGraph.addVertex(va);
      aaTOva.put(aa, va);

      AbstractActor originalActor = va.ori;
      if (!loadMemoization.containsKey(originalActor)) {
        long load = getLoad(originalActor, slamDesign, scenario);
        loadMemoization.put(originalActor, load);
        va.load = load;
      } else {
        va.load = loadMemoization.get(originalActor);
      }
      // memoization on allowed mappings
      if (!possibleMappings.containsKey(originalActor)) {
        List<ComponentInstance> cis = scenario.getPossibleMappings(originalActor);
        List<CoreAbstraction> cas = new ArrayList<>();
        for (ComponentInstance ci : cis) {
          cas.add(ciTOca.get(ci));
        }
        if (cas.isEmpty()) {
          cas.add(defaultCore);
        }
        possibleMappings.put(originalActor, cas);
      }

      if (aa instanceof PeriodicElement) {
        PeriodicElement pe = (PeriodicElement) aa;
        long period = pe.getPeriod().evaluate();
        if (period > 0 && pe instanceof Actor) {
          va.isPeriodic = true;
          Actor a = (Actor) pe;
          long firingInstance = a.getFiringInstance();
          long ns = firingInstance * period;
          va.minStartTime = ns;
          va.maxStartTime = (firingInstance + 1) * period;
        }
      }

    }

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
          throw new PreesmRuntimeException("Problem while creating graph copy.");
        }
      }
    }

    // TODO check if DAG?

    return absGraph;
  }

  protected static long getLoad(AbstractActor actor, Design slamDesign, Scenario scenario) {
    long wcet = ScenarioConstants.DEFAULT_TIMING_TASK.getValue();
    for (final Component operatorDefinitionID : slamDesign.getOperatorComponents()) {
      wcet = scenario.getTimings().evaluateTimingOrDefault((AbstractActor) actor, operatorDefinitionID);
    }
    return wcet;
  }

  protected static void setAbsGraph(DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph, long horizon,
      List<VertexAbstraction> firstNodes, List<VertexAbstraction> lastNodes) {
    for (VertexAbstraction va : absGraph.vertexSet()) {
      if (!va.isPeriodic) {
        va.maxStartTime = horizon;
      }
    }
    // set min start time
    List<VertexAbstraction> toVisit = new LinkedList<>(firstNodes);
    while (!toVisit.isEmpty()) {
      VertexAbstraction va = toVisit.remove(0);
      long succns = va.minStartTime + va.load;
      for (EdgeAbstraction ea : absGraph.outgoingEdgesOf(va)) {
        VertexAbstraction oppositeva = absGraph.getEdgeTarget(ea);
        oppositeva.minStartTime = Math.max(oppositeva.minStartTime, succns);
        updateNbVisits(absGraph, oppositeva, false, toVisit);
      }
    }
    // set max and average start time
    toVisit = new LinkedList<>(lastNodes);
    while (!toVisit.isEmpty()) {
      VertexAbstraction va = toVisit.remove(0);
      va.predFinishTime = va.minStartTime;
      va.maxStartTime -= va.load;
      if (va.minStartTime > va.maxStartTime) {
        throw new PreesmRuntimeException(
            "Cannot schedule following firing, min start time > max start time: " + va.aa.getName());
      }
      va.averageStartTime = (va.minStartTime + va.maxStartTime) / 2;
      long predxs = va.maxStartTime;
      for (EdgeAbstraction ea : absGraph.incomingEdgesOf(va)) {
        VertexAbstraction oppositeva = absGraph.getEdgeSource(ea);
        oppositeva.maxStartTime = Math.min(oppositeva.maxStartTime, predxs);
        updateNbVisits(absGraph, oppositeva, true, toVisit);
      }
    }

  }

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

  protected void schedule() {
    long dualCtot = horizon * cores.size() - Ctot;
    long emptyTime = 0;

    List<VertexAbstraction> queue = new LinkedList<>();
    for (VertexAbstraction va : firstNodes) {
      insertTaskInScheduleQueue(va, queue);
    }

    while (!queue.isEmpty()) {
      VertexAbstraction va = queue.get(0);
      int previousAllocations = nbFiringsAllocated;
      if (isThereACoreIdlingBefore(cores, va.predFinishTime)) {
        // manage empty space
        for (VertexAbstraction vab : possibleAllocationBefore(queue, cores, va.predFinishTime)) {
          emptyTime = allocateAndRemoveIfBefore(vab, queue, emptyTime, dualCtot, va.predFinishTime);
        }
        if (nbFiringsAllocated > previousAllocations) {
          continue;
        }
      }
      emptyTime = allocateAndRemove(va, queue, emptyTime, dualCtot);
    }

  }

  protected static boolean isThereACoreIdlingBefore(List<CoreAbstraction> cores, long deadline) {
    return cores.get(0).implTime < deadline;
  }

  protected static List<VertexAbstraction> possibleAllocationBefore(List<VertexAbstraction> queue,
      List<CoreAbstraction> cores, long deadline) {
    List<VertexAbstraction> res = new LinkedList<>();

    long emptyLoad = 0;
    for (CoreAbstraction ca : cores) {
      if (ca.implTime < deadline) {
        emptyLoad += deadline - ca.implTime;
      }
    }
    long currentLoad = 0;
    for (VertexAbstraction va : queue) {
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

  protected long allocateAndRemove(VertexAbstraction va, List<VertexAbstraction> queue, long emptyTime, long loadDual) {
    return allocateAndRemoveIfBefore(va, queue, emptyTime, loadDual, 0);
  }

  protected long allocateAndRemoveIfBefore(VertexAbstraction va, List<VertexAbstraction> queue, long emptyTime,
      long loadDual, long deadline) {
    CoreAbstraction ca = popFirstPossibleCore(va, cores, possibleMappings);
    if (ca == null || ca.implTime > va.maxStartTime) {
      throw new PreesmRuntimeException(
          "Could not allocate the following task, no component or start time is overdue:  " + va.aa.getName());
    }
    long startTime = Math.max(va.predFinishTime, ca.implTime);
    if (deadline > 0 && startTime + va.load > deadline) {
      insertCoreInImplOrder(ca, cores);
      return emptyTime;
    }
    nbFiringsAllocated++;
    ca.coreSched.getActorList().add(va.aa);
    if (va.aa instanceof InitActor) {
      final AbstractActor endReference = ((InitActor) va.aa).getEndReference();
      resultMapping.getMappings().put(endReference, ECollections.singletonEList(ca.ci));
    }
    if (!(va.aa instanceof EndActor)) {
      resultMapping.getMappings().put(va.aa, ECollections.singletonEList(ca.ci));
    }
    va.startTime = startTime;
    final long extraIdleTime = va.startTime - ca.implTime;
    ca.implTime = va.startTime + va.load;
    insertCoreInImplOrder(ca, cores);
    queue.remove(va);
    updateAllocationNbVisits(absGraph, va, queue, ca.implTime);
    return casRemainingLoad(extraIdleTime, loadDual, emptyTime);
  }

  protected static CoreAbstraction popFirstPossibleCore(VertexAbstraction va, List<CoreAbstraction> cores,
      Map<AbstractActor, List<CoreAbstraction>> possibleMappings) {
    List<CoreAbstraction> posmaps = possibleMappings.get(va.ori);
    ListIterator<CoreAbstraction> it = cores.listIterator();
    while (it.hasNext()) {
      CoreAbstraction ca = it.next();
      if (posmaps.contains(ca)) {
        it.remove();
        return ca;
      }
    }
    return null;
  }

  protected static void updateAllocationNbVisits(DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph,
      VertexAbstraction va, List<VertexAbstraction> queue, long finishTime) {
    for (EdgeAbstraction ea : absGraph.outgoingEdgesOf(va)) {
      VertexAbstraction tgt = absGraph.getEdgeTarget(ea);
      tgt.nbVisits += 1;
      tgt.predFinishTime = Math.max(finishTime, tgt.predFinishTime);
      Set<EdgeAbstraction> seteas = absGraph.incomingEdgesOf(tgt);
      if (tgt.nbVisits == seteas.size()) {
        insertTaskInScheduleQueue(tgt, queue);
        tgt.nbVisits = 0;
      }
    }
  }

  // ascending as, and ascending ns if equality of as
  protected static void insertTaskInScheduleQueue(VertexAbstraction va, List<VertexAbstraction> queue) {
    ListIterator<VertexAbstraction> it = queue.listIterator();
    while (it.hasNext()) {
      VertexAbstraction next = it.next();
      if (next.averageStartTime < va.averageStartTime) {
        // performance trick
        continue;
      } else if (next.averageStartTime > va.averageStartTime
          || (next.averageStartTime == va.averageStartTime && next.minStartTime > va.minStartTime)) {
        it.previous();
        break;
      }
    }
    it.add(va);
  }

  protected static void insertCoreInImplOrder(CoreAbstraction ca, List<CoreAbstraction> order) {
    ListIterator<CoreAbstraction> it = order.listIterator();
    while (it.hasNext()) {
      CoreAbstraction next = it.next();
      if (next.implTime > ca.implTime) {
        it.previous();
        break;
      }
    }
    it.add(ca);
  }

  protected static long casRemainingLoad(long extraIdleTime, long loadDual, long previousEmptyTime) {
    long res = previousEmptyTime + extraIdleTime;
    if (res > loadDual) {
      throw new PreesmRuntimeException("Impossible schedule: there are too much unccupied space.");
    }
    return res;
  }

}

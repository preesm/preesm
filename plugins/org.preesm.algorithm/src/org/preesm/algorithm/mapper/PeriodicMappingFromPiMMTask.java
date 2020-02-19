package org.preesm.algorithm.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.preesm.algorithm.mapper.abc.taskscheduling.SimpleTaskSched;
import org.preesm.algorithm.mapper.algo.InitialLists;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.params.AbcParameters;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.pisdf.pimm2srdag.StaticPiMM2MapperDAGVisitor;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PeriodicScheduler;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Map PiMM SRDAG with new periodic scheduler.
 * 
 * @author ahonorat
 */
@PreesmTask(id = "pisdf-mapper.periodic.DAG", name = "Periodic Scheduling from PiSDF to old DAG",
    category = "Schedulers",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "architecture", type = Design.class),
        @Port(name = "scenario", type = Scenario.class) },

    outputs = { @Port(name = "DAG", type = DirectedAcyclicGraph.class), @Port(name = "ABC", type = LatencyAbc.class) },

    parameters = {
        @Parameter(name = "SCHEDULE_FILE", values = { @Value(name = "/schedule.json", effect = "default value") })

    })
public class PeriodicMappingFromPiMMTask extends AbstractMappingFromDAG {

  private PiGraph inputPiGraphSRDAG = null;

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    final MapperDAG dag = StaticPiMM2MapperDAGVisitor.convert(algorithm, architecture, scenario);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, dag);
    inputPiGraphSRDAG = algorithm;

    return super.execute(inputs, parameters, monitor, nodeName, workflow);
  }

  @Override
  protected LatencyAbc schedule(Map<String, Object> outputs, Map<String, String> parameters, InitialLists initial,
      Scenario scenario, AbcParameters abcParams, MapperDAG dag, Design architecture, AbstractTaskSched taskSched) {

    final IScheduler scheduler = new PeriodicScheduler();
    final SynthesisResult scheduleAndMap = scheduler.scheduleAndMap(inputPiGraphSRDAG, architecture, scenario);

    final Map<AbstractActor, DAGVertex> newTOold = new HashMap<>();
    final Set<DAGVertex> sourceActors = new HashSet<>();
    for (DAGVertex vertex : dag.vertexSet()) {
      if (vertex.incomingEdges().isEmpty()) {
        sourceActors.add(vertex);
      }
      newTOold.put(vertex.getReferencePiVertex(), vertex);
    }

    long nbScheduledActors = 0;
    final Map<ComponentInstance, List<DAGVertex>> schedByCI = new LinkedHashMap<>();
    for (final Schedule sd : scheduleAndMap.schedule.getChildren()) {
      if (sd instanceof ActorSchedule) {
        final ActorSchedule as = (ActorSchedule) sd;
        final List<AbstractActor> seqActors = as.getActorList();
        nbScheduledActors += seqActors.size();
        if (!seqActors.isEmpty()) {
          final AbstractActor aaFirst = seqActors.get(0);
          final List<ComponentInstance> mappings = scheduleAndMap.mapping.getMapping(aaFirst);
          final List<DAGVertex> seqVertices = new ArrayList<>();
          for (final AbstractActor aa : seqActors) {
            seqVertices.add(newTOold.get(aa));
          }
          schedByCI.put(mappings.get(0), seqVertices);
        }
      }
    }

    if (nbScheduledActors != newTOold.size()) {
      throw new PreesmRuntimeException("Periodic and legacy schedulers differs in the number of vertex to schedule.");
    }

    final LatencyAbc abc = LatencyAbc.getInstance(abcParams, dag, architecture, scenario);
    final AbstractTaskSched ats = new SimpleTaskSched();
    abc.setTaskScheduler(ats);
    final Map<DAGVertex, Integer> visitedInputsActors = new HashMap<>();
    final List<DAGVertex> readyToSchedule = new LinkedList<>(sourceActors);

    while (!readyToSchedule.isEmpty()) {
      DAGVertex firingToMap = null;
      ComponentInstance ci = null;
      for (Entry<ComponentInstance, List<DAGVertex>> e : schedByCI.entrySet()) {
        final ComponentInstance componentInstance = e.getKey();
        final List<DAGVertex> list = e.getValue();
        if (list.isEmpty()) {
          continue;
        }
        final DAGVertex firstOnList = list.get(0);
        if (readyToSchedule.contains(firstOnList)) {
          firingToMap = firstOnList;
          ci = componentInstance;
          list.remove(firstOnList);
          break;
        }
      }

      if (firingToMap != null && ci != null) {
        ExternalMappingFromDAG.mapVertex(abc, ci, firingToMap);

        readyToSchedule.remove(firingToMap);
        addReadyFirings(firingToMap, readyToSchedule, visitedInputsActors);
      } else {
        throw new PreesmRuntimeException("Unable to map next vertex.");
      }
    }

    return abc;
  }

  private static void addReadyFirings(DAGVertex firingToMap, List<DAGVertex> readyToSchedule,
      Map<DAGVertex, Integer> visitedInputsActors) {

    for (DAGEdge outEdge : firingToMap.outgoingEdges()) {
      DAGVertex dst = outEdge.getTarget();
      int visitedEdges = visitedInputsActors.getOrDefault(dst, 0) + 1;
      visitedInputsActors.put(dst, visitedEdges);
      if (visitedEdges == dst.incomingEdges().size()) {
        visitedInputsActors.remove(dst);
        readyToSchedule.add(dst);
      }
    }

  }

}

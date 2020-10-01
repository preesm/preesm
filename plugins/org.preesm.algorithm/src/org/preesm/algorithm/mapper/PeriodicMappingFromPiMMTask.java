/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
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
    category = "Schedulers", shortDescription = "Schedule and maps actors according to their periods.",
    description = "Schedule and map actors according to their periods thanks to a list scheduler. "
        + "Only works for homogeneous architectures, does not take into account communication times. "
        + "Works also if there are no periods in the graph. "
        + "Result is exported in the same format as pisdf-mapper.list standard scheduler.",
    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "architecture", type = Design.class),
        @Port(name = "scenario", type = Scenario.class) },

    outputs = { @Port(name = "DAG", type = DirectedAcyclicGraph.class), @Port(name = "ABC", type = LatencyAbc.class) })
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

  @Override
  public String monitorMessage() {
    return "Mapping/Scheduling according to the periodic list scheduler.";
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    return parameters;
  }

}

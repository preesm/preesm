package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.emf.common.util.ECollections;
import org.preesm.algorithm.mapper.ListSchedulingMappingFromPiMM;
import org.preesm.algorithm.mapper.ScheduledDAGIterator;
import org.preesm.algorithm.mapper.graphtransfo.ImplementationPropertyNames;
import org.preesm.algorithm.mapper.graphtransfo.VertexType;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.mapping.model.MappingFactory;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Calls the legacy schedule algorithm with the input design, algo and scenario. The Schedule and Mapping are built
 * using the resulting MapperDAG.
 *
 * @author anmorvan
 */
public class LegacyListScheduler extends AbstractScheduler {

  @Override
  protected SynthesisResult exec(final PiGraph algorithm /* SRDAG */, final Design architecture,
      final Scenario scenario) {

    // build task input
    final Map<String, Object> inputs = new LinkedHashMap<>();
    inputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, algorithm);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, architecture);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);

    // call legacy scheduler
    final ListSchedulingMappingFromPiMM legacySched = new ListSchedulingMappingFromPiMM();
    final Map<String, Object> outputs = legacySched.execute(inputs, legacySched.getDefaultParameters(), null, "", null);

    // get scheduled MapperDAG
    final MapperDAG dag = (MapperDAG) outputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_DAG);

    // build Schedule and Mapping objects from the result MapperDAG
    final Map<ComponentInstance, ActorSchedule> cmpSchedules = new LinkedHashMap<>();
    final HierarchicalSchedule topParallelSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
    final Mapping createMapping = MappingFactory.eINSTANCE.createMapping();

    final ScheduledDAGIterator scheduledDAGIterator = new ScheduledDAGIterator(dag);
    scheduledDAGIterator.forEachRemaining(vert -> {
      final AbstractActor orderedActor = vert.getReferencePiVertex();
      if (orderedActor == null) {
        final String vertexType = vert.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
            .toString();
        if (VertexType.TYPE_SEND.equals(vertexType) || VertexType.TYPE_RECEIVE.equals(vertexType)) {
          // skip
        } else {
          throw new PreesmRuntimeException(
              "MapperDag vertex '" + vert + "' of type [" + vertexType + "] has no PiSDF reference");
        }
      } else {

        final ComponentInstance targetCmpIntance = vert.getPropertyBean()
            .getValue(ImplementationPropertyNames.Vertex_Operator);

        createMapping.getMappings().put(orderedActor, ECollections.singletonEList(targetCmpIntance));
        if (!cmpSchedules.containsKey(targetCmpIntance)) {
          final ActorSchedule createActorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
          cmpSchedules.put(targetCmpIntance, createActorSchedule);
          topParallelSchedule.getScheduleTree().add(createActorSchedule);
        }
        cmpSchedules.get(targetCmpIntance).getActorList().add(orderedActor);
      }
    });

    return new SynthesisResult(createMapping, topParallelSchedule, null);
  }

}

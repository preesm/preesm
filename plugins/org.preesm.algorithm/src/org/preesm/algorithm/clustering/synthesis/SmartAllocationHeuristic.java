package org.preesm.algorithm.clustering.synthesis;

import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.heuristics.AllocationHeuristic;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.MemoryAllocationFactory;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PortMemoryAnnotation;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

public class SmartAllocationHeuristic extends AllocationHeuristic {

  Map<AbstractVertex, Long> clusterBrv;

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    super.initHeuristicParameters(graph, scenario, arch, taskParameters);

    /* Here, graph = cluster. We keep "graph" to match the override */
    clusterBrv = PiBRV.compute(graph, BRVMethod.LCM);

  }

  @Override
  public Allocation allocate(PiGraph cluster, Schedule clusterSchedule, Mapping clusterMapping) {

    final Allocation clusterAllocation = MemoryAllocationFactory.eINSTANCE.createAllocation();
    final PhysicalBuffer mainBuffer = MemoryAllocationFactory.eINSTANCE.createPhysicalBuffer();

    // TODO : awfull
    ComponentInstance componentInstance = arch
        .getComponentInstance(clusterMapping.getAllInvolvedComponentInstances().get(0).toString());
    if (componentInstance == null) {
      componentInstance = scenario.getSimulationInfo().getMainComNode();
    }
    clusterAllocation.getPhysicalBuffers().add(mainBuffer);
    mainBuffer.setMemoryBank(componentInstance);

    recursiveAllocation(cluster, clusterSchedule, clusterMapping, clusterAllocation);
    return clusterAllocation;

  }

  /**
   * a {@link Schedule schedule} can be recursive, and because the {@link Allocation allocation} of a cluster is based
   * on the schedule in this {@link SimpleAllocationHeuristic heuristic}, the {@link Allocation allocation} has to be
   * built recursively.
   *
   * @param cluster
   *          the current cluster
   * @param subSchedule
   *          the current {@link Schedule schedule} to study. It can be a scope containing other schedules, or a list of
   *          actors that can be executed in parallel or sequentially depending on the type of {@link Schedule
   *          subSchedule}.
   * @param clusterMapping
   *          the mapping of the cluster.
   * @return the {@link Allocation allocation} based on {@link Schedule subSchedule}
   */
  private void recursiveAllocation(PiGraph cluster, Schedule subSchedule, Mapping clusterMapping,
      Allocation clusterAllocation) {

    final long size = 0;

    for (final Schedule schedule : subSchedule.getChildren()) {

      if (schedule instanceof final ActorSchedule actorSchedule) {

        /* The repetition of the current scope, might be useless ! */
        long totalScopeRepetition = actorSchedule.getRepetition();
        Schedule parent = schedule.getParent();
        while (parent != null) {
          totalScopeRepetition *= parent.getRepetition();
          parent = parent.getParent();
        }

        /* All the actor in the scope */
        final List<AbstractActor> actors = actorSchedule.getActorList();
        // TODO big part here
        for (final AbstractActor actor : actors) {

          final long actorRepetition = clusterBrv.get(actor) / totalScopeRepetition;

          if (actor instanceof SpecialActor) {
            boolean reuseBuffer = false;

            /* Condition 1. */
            if (actorRepetition == 1) {

              if (actor instanceof BroadcastActor || actor instanceof ForkActor) {

                // TODO : compute beginj and endj
                final long beginj = 0;
                final long endj = 0;

                for (final DataOutputPort out : actor.getDataOutputPorts()) {
                  final Fifo fifo = out.getFifo();
                  final DataInputPort inB = fifo.getTargetPort();

                  /* Condition 2 */
                  if (inB.getAnnotation() == PortMemoryAnnotation.READ_ONLY) {

                    final long rateIn = actor.getDataInputPorts().get(0).getExpression().evaluateAsLong();
                    final long rateInB = inB.getExpression().evaluateAsLong();
                    final long kj = MathFunctionsHelper.lcm(rateIn, rateInB);

                    final long beginji = beginj;
                    final long endji = 0;

                    for (int i = 0; i < kj; i++) {

                      /* Condition 3 */
                      if (rateIn >= endji) {
                        reuseBuffer = true;
                        // TODO make reuse
                      }
                    }
                  }
                }
              } else if (actor instanceof JoinActor || actor instanceof RoundBufferActor) {
                for (final DataInputPort in : actor.getDataInputPorts()) {
                }
              }
            }
            if (!reuseBuffer) {
              // TODO make memcpy
            }
          }
        }
      } else {
        recursiveAllocation(cluster, schedule, clusterMapping, clusterAllocation);
      }
    }

    // TODO define size
    // TODO : ugly,
    clusterAllocation.getPhysicalBuffers().get(0).setSizeInBit(size);
  }
}

package org.preesm.algorithm.clustering.synthesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * Helper class for cluster synthesis (scheduling & allocation)
 */
public class ClusterSynthesisHelper {

  private ClusterSynthesisHelper() {
    /* This utility class should not be instantiated */
  }

  /**
   * This method generates {@link SpecialActor special actors} with one {@link DataInputPort input} and one
   * {@link DataOutputPort output}, to be able to perform smart cluster memory {@link Allocation allocation}. This
   * allocation is made with a {@link PiGraph PiSDF}, not with a SrDAG. That is why we are generating special actors
   * that would have been generated in the SrDAG.
   *
   * @param cluster
   *          the input cluster (PiSDF graph)
   */
  public static void addSpecialActors(PiGraph cluster) {
    // I think adding Fork and Join actors is useless to improve memory reuse in cluster
    addBroadcastActors(cluster);
    addRoundBufferActors(cluster);
  }

  /**
   * For every {@link DataInputInterface data input interface}, it checks if a {@link BroadcastActor broadcast actor}
   * needs to be generated. The condition is : if a is linked to b, a being the data input interface, and brv value of b
   * is strictly higher than 1, then we can add a {@link BroadcastActor broadcast actor} to make a smart cluster memory
   * allocation in a future step of the {@link ClusterSynthesisTask cluster synthesis task}.
   *
   * @param cluster
   *          the input cluster
   */
  private static void addBroadcastActors(PiGraph cluster) {

    long nameCounter = 0;

    final Map<AbstractVertex, Long> brv = PiBRV.compute(cluster, BRVMethod.LCM);

    for (final DataInputInterface a : cluster.getDataInputInterfaces()) {
      final DataOutputPort aOut = a.getDataPort();
      final Fifo a2b = aOut.getFifo();
      final AbstractActor b = a2b.getTarget();
      final DataInputPort bIn = a2b.getTargetPort();
      final long aExpr = a.getGraphPort().getExpression().evaluateAsLong();
      final long bInExpr = bIn.getExpression().evaluateAsLong();

      // If b is executed only once or next actor is already a broadcast actor, it means adding a broadcast is not
      // necessary
      if (brv.get(b) * bInExpr == aExpr || b instanceof BroadcastActor) {
        continue;
      }

      // Creating broadcast actor
      final BroadcastActor brd = PiMMUserFactory.instance.createBroadcastActor();
      brd.setName("brd_" + nameCounter++);

      // Creating in/out broadcast ports
      final DataInputPort brdIn = PiMMUserFactory.instance.createDataInputPort();
      brdIn.setName("brd_in");
      final DataOutputPort brdOut = PiMMUserFactory.instance.createDataOutputPort();
      brdOut.setName("brd_out");
      brd.getDataInputPorts().add(brdIn);
      brd.getDataOutputPorts().add(brdOut);

      // Setting expression of ports
      aOut.setExpression(aExpr); // otherwise it bugs...
      brdIn.setExpression(aExpr);
      brdOut.setExpression(brv.get(b) * bInExpr);

      // Linking broadcast with a and b
      final String dataType = a2b.getType();
      final Fifo a2brd = a2b;
      a2brd.setTargetPort(brdIn);
      final Fifo brd2b = PiMMUserFactory.instance.createFifo(brdOut, bIn, dataType);
      cluster.addActor(brd);
      cluster.addFifo(brd2b);
    }
  }

  /**
   * For every {@link DataOutputInterface data input interface}, it checks if a {@link RoundBufferActor round buffer
   * actor} needs to be generated. The condition is : if a is linked to b, b being the data input interface, and brv
   * value of a is strictly higher than 1, then we can add a {@link RoundBufferActor round buffer actor} to allow a
   * smart cluster memory allocation in a future step of the {@link ClusterSynthesisTask cluster synthesis task}.
   *
   * @param cluster
   *          the input cluster
   */
  private static void addRoundBufferActors(PiGraph cluster) {

    long nameCounter = 0;

    final Map<AbstractVertex, Long> brv = PiBRV.compute(cluster, BRVMethod.LCM);

    for (final DataOutputInterface b : cluster.getDataOutputInterfaces()) {
      final DataInputPort bIn = b.getDataPort();
      final Fifo a2b = bIn.getFifo();
      final AbstractActor a = a2b.getSource();
      final DataOutputPort aOut = a2b.getSourcePort();
      final long aOutExpr = aOut.getExpression().evaluateAsLong();
      final long bExpr = b.getGraphPort().getExpression().evaluateAsLong();

      // If a is executed only once or next actor is already a round buffer actor, it means adding a broadcast is not
      // necessary
      if (brv.get(a) * aOutExpr == bExpr || b instanceof RoundBufferActor) {
        continue;
      }

      // Creating round buffer actor
      final RoundBufferActor rdb = PiMMUserFactory.instance.createRoundBufferActor();
      rdb.setName("rdb_" + nameCounter++);

      // Creating in/out broadcast ports
      final DataInputPort rdbIn = PiMMUserFactory.instance.createDataInputPort();
      rdbIn.setName("rdb_in");
      final DataOutputPort rdbOut = PiMMUserFactory.instance.createDataOutputPort();
      rdbOut.setName("rdb_out");
      rdb.getDataInputPorts().add(rdbIn);
      rdb.getDataOutputPorts().add(rdbOut);

      // Setting expression of ports
      bIn.setExpression(bExpr); // otherwise it bugs...
      rdbIn.setExpression(brv.get(a) * aOutExpr);
      rdbOut.setExpression(bExpr);

      // Linking broadcast with a and b
      final String dataType = a2b.getType();
      final Fifo a2rdb = a2b;
      a2rdb.setTargetPort(rdbIn);
      final Fifo brd2b = PiMMUserFactory.instance.createFifo(rdbOut, bIn, dataType);
      cluster.addActor(rdb);
      cluster.addFifo(brd2b);
    }
  }

  /**
   * Computes the scope repetition of actor a in schedule s. For example, with s = a2(b4c), the scope repetition of b
   * and c will be 2, and 1 for a. CARE : This method supposes that each actor appears one and only one time in the
   * schedule. For example, if s = a2(b4c)ab, s is not valid (because a and b repeat multiple times) and the method will
   * return -1 for actor a and b. Additionally, if a is not present in s, the method will also return -1.
   *
   * @param a
   *          current actor
   * @param s
   *          the schedule (it is not mandatory that the schedule is the root of the hierarchical schedule to have an
   *          exhaustive search, the method will automatically start from the root of the given schedule)
   * @return the scope repetition of actor a, or -1 if there is multiple occurrences of a, or if a is not present in the
   *         schedule.
   */
  public static long getActorScopeRepetition(final AbstractActor a, final Schedule s) {

    if (a instanceof InterfaceActor) {
      return 1;
    }

    final List<Long> results = getScopeRecursively(a, s.getRoot());
    final long nbRep = results.stream().filter(x -> x != -1).toList().size();
    if (nbRep != 1) {
      return -1;
    }
    return results.stream().filter(x -> x != -1).toList().getFirst();
  }

  /**
   * Checks if actor a is in every {@link SequentialActorSchedule}. It returns a list that has a size equals to the
   * number of {@link SequentialActorSchedule} in the schedule. If a is not in a {@link SequentialActorSchedule}, the
   * value in the list will be equal to -1.
   *
   * @param a
   *          current actor
   * @param s
   *          current schedule
   * @return the list that keeps track of actor a presence in every {@link SequentialActorSchedule}
   */
  private static List<Long> getScopeRecursively(AbstractActor a, Schedule s) {
    final List<Long> result = new ArrayList<>();

    if (s instanceof final ActorSchedule as) {
      final List<AbstractActor> actors = as.getActorList();
      boolean aIsFound = false;
      for (final AbstractActor current : actors) {
        if (a == current) {
          result.add(computeScopeRepetition(s));
          aIsFound = true;
        }
      }
      if (!aIsFound) {
        result.add(-1L);
      }

    } else {
      for (final Schedule child : s.getChildren()) {

        result.addAll(getScopeRecursively(a, child));
      }
    }
    return result;
  }

  /**
   * Computes the scope repetition of current schedule. For example, if s.getRoot = a2(b3(c2d)), computeScopeRepetition
   * of b3(c2d) will be equal to 2, and computeScopeRepetition of c2d will be equal to 2 * 3 = 6.
   *
   * @param s
   *          current schedule
   * @return the scope repetition of s
   */
  public static long computeScopeRepetition(final Schedule s) {
    long scopeRepetition = s.getRepetition();
    if (scopeRepetition == 0) {
      scopeRepetition = 1;
    }
    Schedule parent = s.getParent();
    while (parent != null) {
      final long parentRep = parent.getRepetition();
      scopeRepetition *= (parentRep == 0) ? 1 : parentRep;
      parent = parent.getParent();
    }
    return scopeRepetition;
  }
}

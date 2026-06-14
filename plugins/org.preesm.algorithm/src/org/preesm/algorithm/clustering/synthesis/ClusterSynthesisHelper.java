package org.preesm.algorithm.clustering.synthesis;

import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EMap;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.Buffer;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

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
    // addForkActors(cluster);
    // addJoinActors(cluster);
    addBroadcastActors(cluster);
    addRoundBufferActors(cluster);
  }

  /**
   * For every {@link Fifo fifo}, it checks if a {@link ForkActor fork actor} needs to be generated. The condition is :
   * if a is linked to b, and b has a brv value strictly higher than a, then we can add a {@link ForkActor fork actor}
   * to make a smart cluster memory allocation in a future step of the {@link ClusterSynthesisTask cluster synthesis
   * task}.
   *
   * @param cluster
   *          the input cluster
   */
  private static void addForkActors(PiGraph cluster) {

    final Map<AbstractVertex, Long> brv = PiBRV.compute(cluster, BRVMethod.LCM);

    // Iterating on every FIFO of the cluster
    for (final Fifo fifo : cluster.getFifos()) {
      final AbstractActor a = fifo.getSource();
      final AbstractActor b = fifo.getTarget();

      // Fork is useless if one of the actors is a already a special actor (fork, join, broadcast, or round buffer)
      // Or if a is executed more time than b
      if (a instanceof SpecialActor || b instanceof SpecialActor || (brv.get(a) >= brv.get(b))) {
        continue;
      }

      final DataInputPort bIn = fifo.getTargetPort();
      final DataOutputPort aOut = fifo.getSourcePort();

      // Creating fork actor
      final ForkActor fork = PiMMUserFactory.instance.createForkActor();

      // Creating in/out fork ports
      final DataInputPort forkIn = PiMMUserFactory.instance.createDataInputPort();
      final DataOutputPort forkOut = PiMMUserFactory.instance.createDataOutputPort();
      fork.getDataInputPorts().add(forkIn);
      fork.getDataOutputPorts().add(forkOut);

      // Setting expression of ports
      forkIn.setExpression(aOut.getExpression());
      forkOut.setExpression(aOut.getExpression());

      // Linking fork with a and b
      final String dataType = aOut.getFifo().getType();
      final Fifo a2fork = PiMMUserFactory.instance.createFifo(aOut, forkIn, dataType);
      final Fifo fork2b = PiMMUserFactory.instance.createFifo(forkOut, bIn, dataType);
      cluster.addActor(fork);
      cluster.addFifo(a2fork);
      cluster.addFifo(fork2b);
    }
  }

  /**
   * For every {@link Fifo fifo}, it checks if a {@link JoinActor join actor} needs to be generated. The condition is :
   * if a is linked to b, and b has a brv value strictly lower than a, then we can add a {@link JoinActor join actor} to
   * make a smart cluster memory allocation in a future step of the {@link ClusterSynthesisTask cluster synthesis task}.
   *
   * @param cluster
   *          the input cluster
   */
  private static void addJoinActors(PiGraph cluster) {
    final Map<AbstractVertex, Long> brv = PiBRV.compute(cluster, BRVMethod.LCM);

    // Iterating on every FIFO of the cluster
    for (final Fifo fifo : cluster.getFifos()) {
      final AbstractActor a = fifo.getSource();
      final AbstractActor b = fifo.getTarget();

      // Join is useless if one of the actors is a already a special actor (fork, join, broadcast, or round buffer)
      // Or if a is executed less time than b
      if (a instanceof SpecialActor || b instanceof SpecialActor || (brv.get(a) <= brv.get(b))) {
        continue;
      }

      final DataInputPort bIn = fifo.getTargetPort();
      final DataOutputPort aOut = fifo.getSourcePort();

      // Creating join actor
      final JoinActor join = PiMMUserFactory.instance.createJoinActor();

      // Creating in/out join ports
      final DataInputPort joinIn = PiMMUserFactory.instance.createDataInputPort();
      final DataOutputPort joinOut = PiMMUserFactory.instance.createDataOutputPort();
      join.getDataInputPorts().add(joinIn);
      join.getDataOutputPorts().add(joinOut);

      // Setting expression of ports
      joinIn.setExpression(aOut.getExpression());
      joinOut.setExpression(aOut.getExpression());

      // Linking join with a and b
      final String dataType = aOut.getFifo().getType();
      final Fifo a2join = PiMMUserFactory.instance.createFifo(aOut, joinIn, dataType);
      final Fifo join2b = PiMMUserFactory.instance.createFifo(joinOut, bIn, dataType);
      cluster.addActor(join);
      cluster.addFifo(a2join);
      cluster.addFifo(join2b);
    }
  }

  /**
   * For every data input interface, it checks if a {@link BroadcastActor broadcast actor} needs to be generated. The
   * condition is : if a is linked to b, a being the data input interface, and brv value of b is strictly higher than 1,
   * then we can add a {@link BroadcastActor broadcast actor} to make a smart cluster memory allocation in a future step
   * of the {@link ClusterSynthesisTask cluster synthesis task}.
   *
   * @param cluster
   *          the input cluster
   */
  private static void addBroadcastActors(PiGraph cluster) {
    final Map<AbstractVertex, Long> brv = PiBRV.compute(cluster, BRVMethod.LCM);
    for (final DataInputInterface a : cluster.getDataInputInterfaces()) {
      final DataOutputPort aOut = a.getDataPort();
      final AbstractActor b = aOut.getFifo().getTarget();

      // If b is executed only once, it means no broadcast is necessary
      if (brv.get(b) == 1) {
        continue;
      }

      final DataInputPort bIn = aOut.getFifo().getTargetPort();

      // Creating broadcast actor
      final BroadcastActor brd = PiMMUserFactory.instance.createBroadcastActor();

      // Creating in/out broadcast ports
      final DataInputPort brdIn = PiMMUserFactory.instance.createDataInputPort();
      final DataOutputPort brdOut = PiMMUserFactory.instance.createDataOutputPort();
      brd.getDataInputPorts().add(brdIn);
      brd.getDataOutputPorts().add(brdOut);

      // Setting expression of ports
      brdIn.setExpression(aOut.getExpression());
      brdOut.setExpression(aOut.getExpression().evaluateAsDouble() * brv.get(b));

      // Linking broadcast with a and b
      final String dataType = aOut.getFifo().getType();
      final Fifo a2brd = PiMMUserFactory.instance.createFifo(aOut, brdIn, dataType);
      final Fifo brd2b = PiMMUserFactory.instance.createFifo(brdOut, bIn, dataType);
      cluster.addActor(brd);
      cluster.addFifo(a2brd);
      cluster.addFifo(brd2b);

    }
  }

  /**
   * For every data output interface, it checks if a {@link RoundBufferActor round buffer actor} needs to be generated.
   * The condition is : if a is linked to b, b being the {@link DataOutputInterface data output interface}, and brv
   * value of a is strictly higher than 1, then we can add a {@link RoundBufferActor round buffer actor} to make a smart
   * cluster memory allocation in a future step of the {@link ClusterSynthesisTask cluster synthesis task}.
   *
   * @param cluster
   *          the input cluster
   */
  private static void addRoundBufferActors(PiGraph cluster) {
    final Map<AbstractVertex, Long> brv = PiBRV.compute(cluster, BRVMethod.LCM);
    for (final DataOutputInterface b : cluster.getDataOutputInterfaces()) {
      final DataInputPort bIn = b.getDataPort();
      final AbstractActor a = bIn.getFifo().getSource();

      // If b is executed only once, it means no broadcast is necessary
      if (brv.get(a) == 1) {
        continue;
      }

      final DataOutputPort aOut = bIn.getFifo().getSourcePort();

      // Creating round buffer actor
      final BroadcastActor rb = PiMMUserFactory.instance.createBroadcastActor();

      // Creating in/out round buffer ports
      final DataInputPort rbIn = PiMMUserFactory.instance.createDataInputPort();
      final DataOutputPort rbOut = PiMMUserFactory.instance.createDataOutputPort();
      rb.getDataInputPorts().add(rbIn);
      rb.getDataOutputPorts().add(rbOut);

      // Setting expression of ports
      rbIn.setExpression(aOut.getExpression());
      rbOut.setExpression(aOut.getExpression().evaluateAsDouble() * brv.get(b));

      // Linking round buffer with a and b
      final String dataType = aOut.getFifo().getType();
      final Fifo a2rb = PiMMUserFactory.instance.createFifo(aOut, rbIn, dataType);
      final Fifo rb2b = PiMMUserFactory.instance.createFifo(rbOut, bIn, dataType);
      cluster.addActor(rb);
      cluster.addFifo(a2rb);
      cluster.addFifo(rb2b);

    }
  }

  /***
   * Method that fuses 2 {@link Allocation allocations}, alloc and dst. It consists of merging all the arrays of the two
   * {@link Allocation allocations}.
   *
   * @param dst
   *          The first {@link Allocation allocation}, that is where the result will be stored
   * @param alloc
   *          The second {@link Allocation allocation}, that will be merged in dst
   */
  public static void fuseAllocations(final Allocation dst, final Allocation alloc) {

    final List<PhysicalBuffer> dstBuffers = dst.getPhysicalBuffers();
    final List<PhysicalBuffer> allocBuffers = alloc.getPhysicalBuffers();
    dstBuffers.addAll(allocBuffers);

    final EMap<Fifo, FifoAllocation> dstFifosAlloc = dst.getFifoAllocations();
    final EMap<Fifo, FifoAllocation> allocFifosAlloc = alloc.getFifoAllocations();
    dstFifosAlloc.addAll(allocFifosAlloc);

    final EMap<InitActor, Buffer> dstDelaysAlloc = dst.getDelayAllocations();
    final EMap<InitActor, Buffer> allocDelaysAlloc = alloc.getDelayAllocations();
    dstDelaysAlloc.addAll(allocDelaysAlloc);
  }

}

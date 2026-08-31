package org.preesm.algorithm.clustering;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.preesm.commons.CollectionUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * Main method of this class is the {@link #create(PiGraph, Set, String) create} method.
 *
 * @author jamorin
 * @author rcazoulat
 *
 */
public class ClusterCreator {
  private ClusterCreator() {
  }

  /**
   * This method will create a {@link PiGraph cluster} containing {@link AbstractActor actors} in the actorsToMerge set
   * and remove them from the parent graph, and will put this new cluster in the parent graph. It will also create the
   * {@link Fifo fifos} linking actors in parentGraph with the cluster, and will also add the parameter dependencies
   * between parameters and the cluster.
   *
   * @param parentGraph
   *          the input graph
   * @param name
   *          the new hierarchical actor's name
   * @param actorsToMerge
   *          the set of actors that have to be merged
   * @param isCluster
   *          if set to true, output graph will be set as a cluster. If set to false, it won't.
   *
   * @returns the cluster, added and connected in parent graph
   */
  public static PiGraph create(PiGraph parentGraph, Set<AbstractActor> actorsToMerge, String name, boolean isCluster) {

    final Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(parentGraph, BRVMethod.LCM);

    final long subGraphRepetition = MathFunctionsHelper.gcd(CollectionUtil.mapGetAll(repetitionVector, actorsToMerge));

    // creating the subgraph / the cluster
    final PiGraph cluster = PiMMUserFactory.instance.createPiGraph();
    if (isCluster) {
      cluster.setClusterValue(true);
    }
    cluster.setName(name);
    cluster.setExpression(PiMMUserFactory.instance.createExpression()); // why ?
    cluster.setUrl("");
    parentGraph.addActor(cluster);

    // This will automatically remove actors from graph
    for (final AbstractActor a : actorsToMerge) {
      if (!parentGraph.getActors().contains(a)) {
        throw new PreesmRuntimeException("graph " + parentGraph.getName() + " does not contains actor " + a.getName());
      }
      cluster.addActor(a);
    }

    final Map<AbstractVertex, Long> brv = PiBRV.compute(cluster, BRVMethod.LCM);

    for (final AbstractActor a : actorsToMerge) {

      a.getDataInputPorts().stream().forEach(dataInputPort -> {
        final Fifo fifo = dataInputPort.getFifo();

        // Check if input Interface is needed
        if (!(actorsToMerge.contains(fifo.getSource()))) {

          // Create interface
          final DataInputInterface inputInterface = PiMMUserFactory.instance
              .createDataInputInterface(dataInputPort.getName());
          cluster.addActor(inputInterface);
          inputInterface.getGraphPort().setIncomingFifo(fifo); // set the outer port's incoming fifo

          // Create FIFO interface -> dataInputPort
          final String FifoDataType = fifo.getType();
          final Fifo internalFifo = PiMMUserFactory.instance.createFifo(inputInterface.getDataPort(), dataInputPort,
              FifoDataType);
          cluster.addFifo(internalFifo);

          // Compute interface rate
          final long interfaceRate = dataInputPort.getExpression().evaluateAsLong() * brv.get(a) / subGraphRepetition;
          inputInterface.getGraphPort().setExpression(interfaceRate);
          inputInterface.getDataPort().setExpression(interfaceRate);

        } else {
          cluster.addFifo(fifo);
          if (fifo.getDelay() != null) {
            cluster.addDelay(fifo.getDelay());
          }
        }
      });

      a.getDataOutputPorts().stream().forEach(dataOutputPort -> {
        final Fifo fifo = dataOutputPort.getFifo();

        // Check if output interface is needed
        if (!(actorsToMerge.contains(fifo.getTarget()))) {

          // Create interface
          final DataOutputInterface outputInterface = PiMMUserFactory.instance
              .createDataOutputInterface(dataOutputPort.getName());
          cluster.addActor(outputInterface);
          outputInterface.getGraphPort().setOutgoingFifo(fifo);

          // Create FIFO dataOutputPort -> interface
          final String fifoDataType = fifo.getType();
          final Fifo internalFifo = PiMMUserFactory.instance.createFifo(dataOutputPort, outputInterface.getDataPort(),
              fifoDataType);
          cluster.addFifo(internalFifo);

          // Compute Interface rate
          final long interfaceRate = dataOutputPort.getExpression().evaluateAsLong() * brv.get(a) / subGraphRepetition;
          outputInterface.getGraphPort().setExpression(interfaceRate);
          outputInterface.getDataPort().setExpression(interfaceRate);
        } else {
          cluster.addFifo(fifo);
          if (fifo.getDelay() != null) {
            cluster.addDelay(fifo.getDelay());
          }
        }
      });

      a.getConfigInputPorts().stream().forEach(cip -> {

        final Dependency outerDep = cip.getIncomingDependency();

        // check if there is already a configIputPort plugged to this parameter (because another actor uses it)
        final Optional<ConfigInputInterface> optParam = cluster.getConfigInputInterfaces().stream()
            .filter(cii -> cii.getName().equals(((Parameter) outerDep.getSource()).getName())).findAny();

        ConfigInputInterface innerCii;

        if (optParam.isEmpty()) {

          // create new configInputInterface for the inside
          innerCii = PiMMUserFactory.instance
              .createConfigInputInterface(((Parameter) (outerDep.getSetter())).getName());

          innerCii.setExpression(((Parameter) outerDep.getSetter()).getExpression().evaluateAsDouble());

          cluster.addParameter(innerCii);

          // create a new config link from the original parameter to the new inner one
          final Dependency newOuterDep = PiMMUserFactory.instance.createDependency(outerDep.getSetter(),
              innerCii.getGraphPort());

          parentGraph.addDependency(newOuterDep);

        } else {
          innerCii = optParam.get();
        }

        // link the outerDep inside, making it the inner dep
        final Dependency innerDep = outerDep;
        innerDep.setGetter(cip);
        innerDep.setSetter(innerCii);
        cluster.addDependency(innerDep);
      });
    }
    for (final ConfigInputInterface cii : cluster.getConfigInputInterfaces()) {
      if (cii.getGraphPort().getIncomingDependency() == null) {
        PreesmLogger.getLogger().info("<WARNING> Interface without external dependencies : " + cii.getName());
      }
    }

    // This line is here because of a bug occurring when creating a cluster in a cluster.
    // In details, it seems that the actorIndex (here just to keep track of the number of actors in a graph) of
    // cluster is not updated when adding an existing actor (of parentGraph) is
    // added in cluster. So for now, we update by hand the actorIndex at the end of the method.
    cluster.setActorIndex(cluster.getActors().size());
    cluster.setFifoWithoutDelayIndex(cluster.getFifosWithoutDelay().size());
    cluster.setFifoWithDelayIndex(cluster.getFifosWithDelay().size());

    return cluster;
  }

  /**
   * This method will create a {@link PiGraph cluster} containing {@link AbstractActor actors} in the actorsToMerge set
   * and remove them from the parent graph, and will put this new cluster in the parent graph. It will also create the
   * {@link Fifo fifos} linking actors in parentGraph with the cluster, and will also add the parameter dependencies
   * between parameters and the cluster. The output PiGraph is marked by default as a cluster. Call the following
   * {@link #create(PiGraph, Set, String, boolean) method} to not make it a cluster.
   *
   * @param parentGraph
   *          the input graph
   * @param name
   *          the new hierarchical actor's name
   * @param actorsToMerge
   *          the set of actors that have to be merged
   *
   * @returns the cluster, added and connected in parent graph
   */
  public static PiGraph create(PiGraph parentGraph, Set<AbstractActor> actorsToMerge, String name) {
    return create(parentGraph, actorsToMerge, name, true);

  }
}

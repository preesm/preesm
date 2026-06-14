package org.preesm.algorithm.clustering;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
 *
 * @author jamorin
 *
 */

public class ActorMerger {
  /**
   *
   * @param graph
   *          the input graph
   * @param name
   *          the new hierarchical actor's name
   * @param actorsToMerge
   *          the set of actors that have to be merged
   */
  public static PiGraph mergeActors(PiGraph graph, Set<AbstractActor> actorsToMerge, String name) {

    // creating the subgraph / the cluster
    final PiGraph innerSDF = PiMMUserFactory.instance.createCluster();
    innerSDF.setName(name);
    innerSDF.setExpression(PiMMUserFactory.instance.createExpression()); // RC : why this line exists ??
    innerSDF.setUrl("");
    graph.addActor(innerSDF);

    // Automatically remove actors from graph
    for (final AbstractActor a : actorsToMerge) {
      innerSDF.addActor(a);
    }

    final Map<AbstractVertex, Long> brv = PiBRV.compute(innerSDF, BRVMethod.LCM);

    // Iterating on all actors to merge
    for (final AbstractActor actor : actorsToMerge) {

      // ------------------------------------------------------------------------------------------- //
      // Data Input Interfaces creation
      // ------------------------------------------------------------------------------------------- //
      actor.getDataInputPorts().stream().forEach(dip -> {

        // if the fifo connects one inner and one outer actor, we have to connect them through the graph ports
        if (!(actorsToMerge.contains(dip.getIncomingFifo().getSource()))) {

          // interface seen from the inside
          final DataInputInterface inInterface = PiMMUserFactory.instance.createDataInputInterface(dip.getName());
          innerSDF.addActor(inInterface);

          final String FifoDataType = dip.getFifo().getType();
          inInterface.getGraphPort().setIncomingFifo(dip.getFifo()); // set the outer port's incoming fifo
          // PreesmLogger.getLogger().info("[DEBUG] > In mergeActors, previousActor check : " +
          // inInterface.getGraphPort().getFifo().getSource())

          // set outer port's rate : inner port's rate times actor's repetition value
          // It is easier to simply evaluate it now that keeping it in parametric form, though a bit less generic
          // an inner interface's RV is 1 (otherwise data will be discarded or duplicated), so we have to set the inner
          // and outer interfaces' rates to the same value
          final long interfaceRate = dip.getExpression().evaluateAsLong() * brv.get(actor);

          inInterface.getGraphPort().setExpression(interfaceRate);
          inInterface.getDataPort().setExpression(interfaceRate);

          final Fifo internalFifo = PiMMUserFactory.instance.createFifo(inInterface.getDataPort(), dip, FifoDataType);
          innerSDF.addFifo(internalFifo);

        } else { // if the fifos connects 2 inner actors, add it to the inner graph (which removes it from the outer
          // one)
          innerSDF.addFifo(dip.getIncomingFifo());
        }
      });

      // ------------------------------------------------------------------------------------------- //
      // Data Output Interfaces creation
      // ------------------------------------------------------------------------------------------- //
      actor.getDataOutputPorts().stream().forEach(dop -> {

        // Il faut aussi ajouter aux nouvelles interfaces d'I/O créées les dépendances aux acteurs auxquels ils sont
        // liés, pour les taux paramétrés de leurs fifos
        // Ou plus simplement, on évalue l'expression en double à ce moment
        // RC : option choisie -> évaluer l'expression en double,
        // puis faire une passe de partitionnement avec une heuristique prévue à cette effet

        if (!(actorsToMerge.contains(dop.getOutgoingFifo().getTarget()))) {

          final DataOutputInterface outInterface = PiMMUserFactory.instance.createDataOutputInterface(dop.getName());
          innerSDF.addActor(outInterface);

          final String fifoDataType = dop.getFifo().getType();

          // set outer port's rate : inner port's rate times actor's repetition value
          // It is easier to simply evaluate it now that keeping it in parametric form, though a bit less generic
          final long interfaceRate = dop.getExpression().evaluateAsLong() * brv.get(actor);
          outInterface.getGraphPort().setExpression(interfaceRate);
          outInterface.getDataPort().setExpression(interfaceRate);

          // first plug the old fifo in to the new interface to avoid conflict
          // (can't have 2 fifos linked to 1 interface)
          outInterface.getGraphPort().setOutgoingFifo(dop.getFifo());

          // Then create a new fifo to connect the inner actor to the hierar. interface
          final Fifo internalFifo = PiMMUserFactory.instance.createFifo(dop, outInterface.getDataPort(), fifoDataType);
          innerSDF.addFifo(internalFifo);

        } else {
          innerSDF.addFifo(dop.getOutgoingFifo());
        }
      });

      // ------------------------------------------------------------------------------------------- //
      // Config Input Interfaces creation
      // ------------------------------------------------------------------------------------------- //
      actor.getConfigInputPorts().stream().forEach(cip -> {

        // récupérer toutes les dépendances (paramètres) de l'acteur
        final Dependency outerDep = cip.getIncomingDependency();

        // check if there is already a configIputPort plugged to this parameter (because another actor uses it)
        final Optional<ConfigInputInterface> optParam = innerSDF.getConfigInputInterfaces().stream()
            .filter(cii -> cii.getName().equals(((Parameter) outerDep.getSource()).getName())).findAny();

        ConfigInputInterface innerCii;

        if (optParam.isEmpty()) {

          // create new configInputInterface for the inside
          innerCii = PiMMUserFactory.instance
              .createConfigInputInterface(((Parameter) (outerDep.getSetter())).getName());

          innerSDF.addParameter(innerCii);

          // create a new config link from the original parameter to the new inner one
          final Dependency newOuterDep = PiMMUserFactory.instance.createDependency(outerDep.getSetter(),
              innerCii.getGraphPort());

          graph.addDependency(newOuterDep);

        } else {
          innerCii = optParam.get();
        }

        // link the outerDep inside, making it the inner dep
        final Dependency innerDep = outerDep;
        innerDep.setGetter(cip);
        innerDep.setSetter(innerCii);
        innerSDF.addDependency(innerDep);
      });
    }

    // This line is here because of a bug occurring when creating a cluster in a cluster.
    // In details, it seems that the actorIndex (here just to keep track of the number of actors in the graph) of
    // innerSDF is not updated when adding an existing actor (of graph) is
    // added in innerSDF. So for now, we update by hand the actorIndex at the end of the method.
    innerSDF.setActorIndex(innerSDF.getActors().size());
    innerSDF.setFifoWithoutDelayIndex(innerSDF.getFifosWithoutDelay().size());
    innerSDF.setFifoWithDelayIndex(innerSDF.getFifosWithDelay().size());

    return innerSDF;
  }
}

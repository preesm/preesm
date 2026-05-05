package org.preesm.algorithm.clustering;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;

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
    final var PiMMFactory = org.preesm.model.pisdf.factory.PiMMUserFactory.instance;

    final PiGraph innerSDF = PiMMFactory.createCluster();
    innerSDF.setName(name);
    innerSDF.setExpression(PiMMFactory.createExpression());
    graph.addActor(innerSDF);

    for (final AbstractActor a : actorsToMerge) { // ça les retire automatiquement de graph
      innerSDF.addActor(a);
    }

    final Map<AbstractVertex, Long> brv = PiBRV.compute(innerSDF, BRVMethod.LCM); // Autre méthode que LCM ?

    // extract input and output interfaces for the new hierarchical actor
    for (final AbstractActor actor : actorsToMerge) {
      actor.getDataInputPorts().stream().forEach(dip -> {
        // if the fifo connects one inner and one outer actor, we have to connect them through the graph ports
        if (!(actorsToMerge.contains(dip.getIncomingFifo().getSource()))) {

          // interface vue de l'intérieur
          final DataInputInterface innerInterface = PiMMFactory.createDataInputInterface(dip.getName());
          innerSDF.addActor(innerInterface);

          final String FifoDataType = dip.getFifo().getType();
          innerInterface.getGraphPort().setIncomingFifo(dip.getFifo()); // set the outer port's incoming fifo

          // set outer port's rate : inner port's rate times actor's repetition value
          // It is easier to simply evaluate it now that keeping it in parametric form, though a bit less generic
          // an inner interface's RV is 1 (otherwise data will be discarded or duplicated), so we have to set the inner
          // and outer interfaces' rates to the same value
          final long interfaceRate = dip.getExpression().evaluateAsLong() * brv.get(actor);

          innerInterface.getGraphPort().setExpression(interfaceRate);
          innerInterface.getDataPort().setExpression(interfaceRate);

          final Fifo internalFifo = PiMMFactory.createFifo(innerInterface.getDataPort(), dip, FifoDataType);
          innerSDF.addFifo(internalFifo);

        } else { // if the fifo connects 2 inner actors, add it to the inner graph (which removes it from the outer one)
          innerSDF.addFifo(dip.getIncomingFifo());
        }
      });

      for (final DataOutputPort dop : actor.getDataOutputPorts().stream()
          .filter(dop -> !(actorsToMerge.contains(dop.getOutgoingFifo().getTarget()))).toList()) {

        // Il faut aussi ajouter aux nouvelles interfaces d'I/O créées les dépendances aux acteurs auxquels ils sont
        // liés, pour les taux paramétrés de leurs fifos
        // Ou plus simplement, on évalue l'expression en double à ce moment

        if (!(actorsToMerge.contains(dop.getOutgoingFifo().getTarget()))) {
          final DataOutputInterface innerInterface = PiMMFactory.createDataOutputInterface(dop.getName());
          innerSDF.addActor(innerInterface);

          final String fifoDataType = dop.getFifo().getType();

          // set outer port's rate : inner port's rate times actor's repetition value
          // It is easier to simply evaluate it now that keeping it in parametric form, though a bit less generic
          final long innerRate = dop.getExpression().evaluateAsLong();

          final long interfaceRate = dop.getExpression().evaluateAsLong() * brv.get(actor);
          innerInterface.getGraphPort().setExpression(interfaceRate);
          innerInterface.getDataPort().setExpression(interfaceRate);

          // first plug the old fifo in to the new interface to avoid conflict (can't have 2 fifos linked to 1
          // interface)
          // then create a new fifo to connect the inner actor to the hierar. interface
          innerInterface.getGraphPort().setOutgoingFifo(dop.getFifo());

          final Fifo internalFifo = PiMMFactory.createFifo(dop, innerInterface.getDataPort(), fifoDataType);
          innerSDF.addFifo(internalFifo);

        } else {
          innerSDF.addFifo(dop.getOutgoingFifo());
        }
      }

      for (final ConfigInputPort cip : actor.getConfigInputPorts()) {
        // récupérer toutes les dépendances (paramètres) de l'acteur
        final Dependency outerDep = cip.getIncomingDependency();

        // check if there already is a configIputPort plugged to this parameter (because another actor uses it)
        final Optional<ConfigInputInterface> optParam = innerSDF.getConfigInputInterfaces().stream()
            .filter(cii -> cii.getName().equals(((Parameter) outerDep.getSource()).getName())).findAny();

        ConfigInputInterface innerCii;

        if (optParam.isEmpty()) {
          // create new configInputInterface for the inside
          innerCii = PiMMFactory.createConfigInputInterface(((Parameter) (outerDep.getSetter())).getName());

          innerSDF.addParameter(innerCii);

          // create a new config link from the original parameter to the new inner one
          final Dependency newOuterDep = PiMMFactory.createDependency(outerDep.getSetter(), innerCii.getGraphPort());
          // innerCii.setExpression(((Parameter) outerDep.getSetter()).getExpression());
          graph.addDependency(newOuterDep);

        } else {
          innerCii = optParam.get();
        }

        // link the outerDep inside, making it the inner dep
        final Dependency innerDep = outerDep;
        innerDep.setGetter(cip);
        innerDep.setSetter(innerCii);
        innerSDF.addDependency(innerDep);
      }
    }

    return innerSDF;

    // Je suppose que : actorIndex est mis à jour par addActor() ; on se fout de clusterValue et eFlags ;

  }
}

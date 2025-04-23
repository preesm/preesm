package org.preesm.algorithm.clustering;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;

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

    final PiGraph innerSDF = PiMMFactory.createPiGraph();
    innerSDF.setName(name);

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
          final Expression innerRate = dip.getExpression(); // get the inner port's rate formula
          final String outerPortRate = "(" + innerRate.getExpressionAsString() + ")" + "*" + brv.get(actor).toString();
          final Expression outerPortExpression = PiMMFactory.createExpression(outerPortRate);

          innerInterface.getGraphPort().setExpression(outerPortExpression);
          // met le taux de l'interface interne à celui de l'acteur lié
          innerInterface.getDataPort().setExpression(dip.getExpression());

          final Fifo internalFifo = PiMMFactory.createFifo(innerInterface.getDataPort(), dip, FifoDataType);
          innerSDF.addFifo(internalFifo);
          // No idea why, but the process seems to null dip's expression, so I save it and re-assign it at the end
          dip.setExpression(innerRate);

        } else { // if the fifo connects 2 inner actors, add it to the inner graph (which removes it from the outer one)
          innerSDF.addFifo(dip.getIncomingFifo());
        }
      });

      for (final DataOutputPort dop : actor.getDataOutputPorts().stream()
          .filter(dop -> !(actorsToMerge.contains(dop.getOutgoingFifo().getTarget()))).toList()) {
        /*
         * actor.getDataOutputPorts().stream().filter(dop ->
         * !(actorsToMerge.contains(dop.getOutgoingFifo().getTarget()))) .forEach(dop -> {
         */
        if (!(actorsToMerge.contains(dop.getOutgoingFifo().getTarget()))) {
          final DataOutputInterface innerInterface = PiMMFactory.createDataOutputInterface(dop.getName());
          innerSDF.addActor(innerInterface);

          final String FifoDataType = dop.getFifo().getType();

          // set outer port's rate : inner port's rate times actor's repetition value
          final Expression innerRate = dop.getExpression(); // get the inner port's rate formula
          final String outerPortRate = "(" + innerRate.getExpressionAsString() + ")" + "*" + brv.get(actor).toString();
          final var outerPortExpression = PiMMFactory.createExpression(outerPortRate);
          innerInterface.getGraphPort().setExpression(outerPortExpression);
          // met le taux de l'interface interne à celui de l'acteur lié
          innerInterface.getDataPort().setExpression(innerRate);

          // first plug the old fifo in to the new interface to avoid conflict (can't have 2 fifos linked to 1
          // interface)
          // then create a new fifo to connect the inner actor to the hierar. interface
          innerInterface.getGraphPort().setOutgoingFifo(dop.getFifo());

          final Fifo internalFifo = PiMMFactory.createFifo(dop, innerInterface.getDataPort(), FifoDataType);
          innerSDF.addFifo(internalFifo);
          dop.setExpression(innerRate);

        } else {
          innerSDF.addFifo(dop.getOutgoingFifo());
        }
      }

      // });

      actor.getConfigInputPorts().stream().forEach(cip -> {
        // récupérer toutes les dépendences (paramètres) de l'acteur
        final Dependency innerDep = cip.getIncomingDependency();

        // change graph dependency ownership to inner graph
        graph.removeDependency(innerDep); // useless ?
        innerSDF.addDependency(innerDep);

        // check if parameter has already been added to the inner graph earlier
        final Optional<ConfigInputInterface> optParam = innerSDF.getConfigInputInterfaces().stream()
            .filter(cii -> cii.getName().equals(((Parameter) innerDep.getSource()).getName())).findAny();

        ConfigInputInterface innerCii;

        if (optParam.isEmpty()) {
          innerCii = PiMMFactory
              .createConfigInputInterface(((Parameter) (cip.getIncomingDependency().getSetter())).getName());

          final Dependency outerDep = PiMMFactory.createDependency(innerDep.getSetter(), innerCii.getGraphPort());
          graph.addDependency(outerDep);
          innerSDF.addParameter(innerCii);
          // On suppose qu'il y a un check de l'unicité lors de l'ajout (vérifier mais semble bon)

        } else {
          innerCii = optParam.get();
        }

        innerDep.setSetter(innerCii);
      });
    }

    graph.addActor(innerSDF);

    return innerSDF;

    // Je suppose que : actorIndex est mis à jour par addActor() ; on se fout de clusterValue et eFlags ;

  }
}

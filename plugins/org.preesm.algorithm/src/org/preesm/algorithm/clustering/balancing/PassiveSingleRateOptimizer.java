package org.preesm.algorithm.clustering.balancing;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.preesm.algorithm.synthesis.memalloc.passive.PassiveActorEngine;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

public class PassiveSingleRateOptimizer {
  private PassiveSingleRateOptimizer() {
    /* This utility class should not be instantiated */
  }

  public static void optimizeSrDAG(PiGraph algorithm, PiGraph passiveAlgorithm, List<PiGraph> clusters) {
    final Map<AbstractVertex, Long> passiveBrv = PiBRV.compute(passiveAlgorithm, BRVMethod.LCM);
    final Map<AbstractVertex, Long> oriBrv = PiBRV.compute(algorithm, BRVMethod.LCM);

    final Map<AbstractActor, PassiveActor> map = PassiveActorEngine.createEquivalencyMap(algorithm, passiveAlgorithm);
    for (final PiGraph cluster : clusters) {
      for (final DataInterface clusterDi : cluster.getDataInterfaces()) {
        final DataPort dp = clusterDi.getGraphPort().getOppositePort();
        final AbstractActor adjActor = dp.getContainingActor();
        if (!map.containsKey(adjActor)) {
          continue;
        }

        // Get equivalent passive port
        final PassiveActor pa = map.get(adjActor);
        final PassivePort pp = PassiveActorEngine.retrievePassivePort(adjActor, pa, dp);
        if (pp == null) {
          PreesmLogger.getLogger().warning(() -> "data port " + dp.getName() + " of actor " + adjActor.getName()
              + " has no equivalent in passive actor " + pa.getName());
          continue;
        }

        // Change rate of actor in srdag
        final long gcd = MathFunctionsHelper.gcd(oriBrv.get(adjActor), oriBrv.get(cluster));
        final long relativeClusterRep = oriBrv.get(cluster) / gcd;
        final long index = (passiveBrv.get(pp.getOppositePort().getContainingActor()) / relativeClusterRep) - 1;
        final long newClusterRate = pp.getBeginiEndi().get((int) index % pp.getBeginiEndi().size()).getValue();
        dp.setExpression(newClusterRate * (double) relativeClusterRep);

        // Change rate of cluster interface
        clusterDi.getGraphPort().setExpression(newClusterRate);
        clusterDi.getDataPort().setExpression(newClusterRate);

        // Copy actor in cluster
        if (adjActor instanceof final Actor actor) {
          final Map<AbstractVertex, Long> clusterBrv = PiBRV.compute(cluster, BRVMethod.LCM);

          // Create inner actor
          final Actor inClusterActor = PiMMUserFactory.instance.copyWithHistory(actor);
          inClusterActor.setName(adjActor.getName() + "_prime");
          cluster.addActor(inClusterActor);

          // Removing every input and output port but one for each
          final DataInputPort firstDip = inClusterActor.getDataInputPorts().getFirst();
          final DataOutputPort firstDop = inClusterActor.getDataOutputPorts().getFirst();
          inClusterActor.getDataInputPorts().clear();
          inClusterActor.getDataInputPorts().add(firstDip);
          inClusterActor.getDataOutputPorts().clear();
          inClusterActor.getDataOutputPorts().add(firstDop);

          // Retrieving cluster fifo of the interface
          final Fifo fifo = clusterDi.getDataPort().getFifo();
          Fifo newFifo;

          // Creating new fifo and connecting the new inner actor
          if (clusterDi instanceof final DataInputInterface dii) {

            // Replacing its write passive script by the default one (otherwise the original behavior is not respected)
            inClusterActor
                .setWritePassiveScriptPath(PassiveActorEngine.createSpecialUrl(PassiveActorEngine.WDEFAULT).toString());

            fifo.setSourcePort(firstDop);
            newFifo = PiMMUserFactory.instance.createFifo(dii.getDataPort(), firstDip, fifo.getType());
            final long dopExpr = fifo.getTargetPort().getExpression().evaluateAsLong()
                * clusterBrv.get(fifo.getTarget());
            firstDip.setExpression(newClusterRate);
            firstDop.setExpression(dopExpr);

          } else {
            final DataOutputInterface doi = (DataOutputInterface) clusterDi;

            // Replacing its read passive script by the default one (otherwise the original behavior is not respected)
            inClusterActor
                .setReadPassiveScriptPath(PassiveActorEngine.createSpecialUrl(PassiveActorEngine.RDEFAULT).toString());

            fifo.setTargetPort(firstDip);
            newFifo = PiMMUserFactory.instance.createFifo(firstDop, doi.getDataPort(), fifo.getType());
            final long dipExpr = fifo.getSourcePort().getExpression().evaluateAsLong()
                * clusterBrv.get(fifo.getSource());
            firstDip.setExpression(dipExpr);
            firstDop.setExpression(newClusterRate);
          }
          cluster.addFifo(newFifo);

          fillDependencies(inClusterActor, adjActor, algorithm, cluster);
        }
      }
    }
  }

  private static void fillDependencies(final AbstractActor innerActor, final AbstractActor outerActor,
      final PiGraph top, final PiGraph cluster) {

    for (final ConfigInputPort cip : innerActor.getConfigInputPorts()) {
      if (cip.getIncomingDependency() != null) {
        continue;
      }
      final ConfigInputPort sourceCip = retrieveSourceConfigInputPort(innerActor, outerActor, cip);
      final ISetter topSetter = sourceCip.getIncomingDependency().getSetter();
      final ConfigInputInterface cii = retrieveClusterConfigInputInterface(topSetter, top, cluster);
      final Dependency dep = PiMMUserFactory.instance.createDependency(cii, cip);
      cluster.addDependency(dep);
    }
  }

  private static ConfigInputPort retrieveSourceConfigInputPort(AbstractActor current, AbstractActor source,
      ConfigInputPort currentCip) {
    for (final ConfigInputPort sourceCip : source.getConfigInputPorts()) {
      if (Objects.equals(sourceCip.getName(), currentCip.getName())) {
        return sourceCip;
      }
    }
    throw new PreesmRuntimeException("Can't retrieve the source cip of " + currentCip.getName() + " of actor "
        + current.getName() + " with source being " + source.getName());
  }

  private static ConfigInputInterface retrieveClusterConfigInputInterface(final ISetter topSetter, final PiGraph top,
      final PiGraph cluster) {
    for (final ConfigInputInterface cii : cluster.getConfigInputInterfaces()) {
      final Dependency inDep = cii.getGraphPort().getIncomingDependency();
      if (inDep != null && inDep.getSetter() == topSetter) {
        return cii;
      }
    }
    if (!(topSetter instanceof final Parameter param)) {
      throw new PreesmRuntimeException("Unsupported setter type for cluster dependency: " + topSetter);
    }
    final ConfigInputInterface cii = PiMMUserFactory.instance.createConfigInputInterface();
    cii.setName(param.getName());
    cii.setExpression(param.getExpression().evaluateAsLong());

    // creates cii's graph port and registers it on the cluster
    if (!cluster.addParameter(cii)) {
      throw new PreesmRuntimeException(
          "Could not add config input interface " + cii.getName() + " to cluster " + cluster.getName());
    }
    final Dependency dep = PiMMUserFactory.instance.createDependency(param, cii.getGraphPort());
    top.addDependency(dep);

    return cii;
  }
}

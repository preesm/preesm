package org.preesm.algorithm.schedule.fpga;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.mapper.ui.stats.IStatGenerator;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiMMHelper;
import org.preesm.model.scenario.Scenario;

/**
 * Class to evaluate buffer sizes thanks to an ADFG abstraction.
 * 
 * @author ahonorat
 */
public class AdfgFpgaFifoEvaluator extends AbstractGenericFpgaFifoEvaluator {

  public static final String FIFO_EVALUATOR_ADFG = "adfgFifoEval";

  protected AdfgFpgaFifoEvaluator() {
    super();
    // forbid instantiation outside package and inherited classed
  }

  @Override
  public Pair<IStatGenerator, Map<Fifo, Long>> performAnalysis(PiGraph flatGraph, Scenario scenario,
      Map<AbstractVertex, Long> brv) {

    // Get all sub graph (connected components) composing the current graph
    final List<List<AbstractActor>> subgraphsWOInterfaces = PiMMHelper.getAllConnectedComponentsWOInterfaces(flatGraph);

    final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos = new LinkedHashMap<>();
    // check and set the II for each subgraph
    for (List<AbstractActor> cc : subgraphsWOInterfaces) {
      mapActorNormalizedInfos.putAll(checkAndSetActorNormalizedInfos(cc, scenario, brv));
    }

    // compute the lambda of each actor
    final StringBuilder logLambda = new StringBuilder(
        "Lambda of actor ports (in number of tokens between 0 and the rate, the closest to 0 the better):\n");
    mapActorNormalizedInfos.values().forEach(ani -> {
      logLambda.append(String.format("/actor <%s>\n", ani.aa.getName()));

      final String logLambdaPorts = ani.aa.getAllDataPorts().stream().map(dp -> {
        final double rate = (double) dp.getExpression().evaluate();
        final double lambda = rate * (1.0d - rate / ani.oriII);
        return String.format(Locale.US, "%s: %4.2e", dp.getName(), lambda);
      }).collect(Collectors.joining(", "));

      logLambda.append(logLambdaPorts + "\n");
    });
    PreesmLogger.getLogger().info(logLambda::toString);

    // TODO compute the fifo sizes thanks to the ARS ILP formulation of ADFG
    // ILP stands for Integer Linear Programming
    // ARS stands for Affine Relation Synthesis
    // ADFG stands for Affine DataFlow Graph (work of Adnan Bouakaz)
    // ojAlgo dependency should be used to create the model because it has dedicated code to ILP,
    // or Choco (but not dedicated to ILP) at last resort.

    // TODO build a schedule using the normalized graph II and each actor offset (computed by the ILP)

    throw new PreesmRuntimeException("This analysis is not yet completely implemented, stopping here.");
  }

}

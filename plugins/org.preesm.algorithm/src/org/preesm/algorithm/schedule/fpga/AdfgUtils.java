package org.preesm.algorithm.schedule.fpga;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.math3.fraction.BigFraction;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.ActorNormalizedInfos;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataPort;

/**
 * This class regroups multiple static methods used by ADFG evaluators
 *
 * @author ahonorat
 */
class AdfgUtils {

  private AdfgUtils() {
    // forbid instantiation
  }

  static class AffineRelation {
    protected final long    nProd;
    protected final long    dCons;
    protected final int     phiIndex;
    protected final boolean phiNegate;

    AffineRelation(final long nProd, final long dCons, final int phiIndex, final boolean phiNegate) {
      this.nProd = nProd;
      this.dCons = dCons;
      this.phiIndex = phiIndex;
      this.phiNegate = phiNegate;
    }

  }

  /**
   * Builds a directed graph with affine relation information. Each edge is doubled (in a direction and in the opposite,
   * even if only one direction is present in the original graph).
   *
   * @param ddg
   *          Abstract directed simple graph.
   * @param dug
   *          Abstract undirected simple graph.
   * @param mapActorNormalizedInfos
   *          Map of actor general informations, used to get II.
   * @param fifoAbsToPhiVariableID
   *          Map from edges in the undirected graph to the phi variable index in the model.
   * @return Directed simple graph of doubled affine relation (one in each direction).
   */
  protected static DefaultDirectedGraph<AbstractActor, AffineRelation> buildGraphAR(
      final DefaultDirectedGraph<AbstractActor, FifoAbstraction> ddg,
      final DefaultUndirectedGraph<AbstractActor, FifoAbstraction> dug,
      final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos,
      final Map<FifoAbstraction, Integer> fifoAbsToPhiVariableID) {
    final DefaultDirectedGraph<AbstractActor, AffineRelation> ddgAR = new DefaultDirectedGraph<>(AffineRelation.class);
    for (final AbstractActor aa : ddg.vertexSet()) {
      ddgAR.addVertex(aa);
    }
    for (final FifoAbstraction fa : dug.edgeSet()) {
      final AbstractActor src = ddg.getEdgeSource(fa);
      final AbstractActor tgt = ddg.getEdgeTarget(fa);
      final long srcII = mapActorNormalizedInfos.get(src).oriII;
      final long tgtII = mapActorNormalizedInfos.get(tgt).oriII;
      final long nProd = fa.getProdRate() * tgtII;
      final long dCons = fa.getConsRate() * srcII;
      final long gcd = MathFunctionsHelper.gcd(nProd, dCons);
      final AffineRelation ar = new AffineRelation(nProd / gcd, dCons / gcd, fifoAbsToPhiVariableID.get(fa), false);
      ddgAR.addEdge(src, tgt, ar);
      if (src != tgt) {
        final AffineRelation arReverse = new AffineRelation(ar.dCons, ar.nProd, ar.phiIndex, true);
        ddgAR.addEdge(tgt, src, arReverse);
      }
    }

    return ddgAR;
  }

  /**
   * Update II Information in ActorNormalizedInfos map
   *
   * @param mapActorNormalizedInfos
   *          map to be updated
   * @param aa
   *          actor to update
   * @param ii
   *          new II
   */
  private static void updateIIInfo(final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos,
      final AbstractActor aa, final long ii) {
    final ActorNormalizedInfos ori = mapActorNormalizedInfos.get(aa);
    final long updatedET = Math.max(ori.oriET, ii);
    final ActorNormalizedInfos updated = new ActorNormalizedInfos(ori.aa, ori.ori, updatedET, ii, ori.brv);
    mapActorNormalizedInfos.put(ori.aa, updated);
  }

  /**
   * Increase actor II for small differences to avoid overflow in ADFG
   *
   * @param mapActorNormalizedInfos
   *          Actors infos with II to update.
   */
  static void overestimateIIToSimplifyADFG(final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos) {
    final float overestimationRatio = (float) 1.005;

    // Sort actors by rate to equalize them when close
    final List<ActorNormalizedInfos> listInfos = new ArrayList<>(mapActorNormalizedInfos.values());
    Collections.sort(listInfos, new DecreasingActorIIComparator());

    for (int i = 0; i < listInfos.size(); i++) {
      final ActorNormalizedInfos current = listInfos.get(i);
      long updatedII = current.oriII;

      // Step 1: Simplify the ratio between II and rate
      updatedII = simplifyIIRateRatio(overestimationRatio, current, updatedII);

      // Step 2: Equalize II for actors with close ratios
      if (i > 0) {
        final ActorNormalizedInfos previous = listInfos.get(i - 1);
        if (updatedII != previous.oriII && (float) previous.oriII / updatedII < overestimationRatio) {
          updatedII = previous.oriII;
        }
      }

      // Update actor II if needed
      if (updatedII != current.oriII) {
        final String log = String.format("Update actor %s II from %d to %d to avoid overflow in ADFG",
            current.aa.getName(), current.oriII, updatedII);
        PreesmLogger.getLogger().finest(log);
        updateIIInfo(mapActorNormalizedInfos, current.aa, updatedII);
        listInfos.set(i, mapActorNormalizedInfos.get(current.aa));
      }
    }
  }

  private static long simplifyIIRateRatio(final float overestimationRatio, final ActorNormalizedInfos current,
      long updatedII) {
    for (final DataPort dp : current.aa.getAllDataPorts()) {
      final long maxNumberOfSteps = 1000;
      final long limit = Math.min(updatedII + maxNumberOfSteps, (long) (current.oriII * overestimationRatio));
      final long rate = dp.getExpression().evaluate();
      long refFracNumerator = new BigFraction(updatedII, rate).getNumeratorAsLong();
      for (long testII = updatedII; testII < limit; testII++) {
        final BigFraction testFrac = new BigFraction(testII, rate);
        if (testFrac.getNumeratorAsLong() < refFracNumerator) {
          updatedII = testII;
          refFracNumerator = new BigFraction(updatedII, rate).getNumeratorAsLong();
        }
      }
    }
    return updatedII;
  }

  /**
   * Compute and log all lambda (as map per data port). Lambda are symmetrical: upper = lower.
   *
   * @param mapActorNormalizedInfos
   *          Standard information about actors, used to get II.
   * @return Map of lambda per data port of all actors in the given map.
   */
  protected static Map<DataPort, BigFraction>
      computeAndLogLambdas(final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos) {
    final Map<DataPort, BigFraction> lambdaPerPort = new LinkedHashMap<>();
    final StringBuilder logLambda = new StringBuilder(
        "Lambda of actor ports (in number of tokens between 0 and the rate, the closest to 0 the better):\n");

    final List<DataPort> negativeDP = new ArrayList<>();
    mapActorNormalizedInfos.values().forEach(ani -> {
      logLambda.append(String.format("/actor <%s>%n", ani.aa.getName()));

      final String logLambdaPorts = ani.aa.getAllDataPorts().stream().map(dp -> {
        final long rate = dp.getExpression().evaluate();
        final BigFraction lambdaFr = computeLambda(rate, ani.oriII);
        lambdaPerPort.put(dp, lambdaFr);
        final double valD = lambdaFr.doubleValue();
        if (valD < 0d) {
          negativeDP.add(dp);
        }
        return String.format(Locale.US, "%s: %4.2e", dp.getName(), valD);
      }).collect(Collectors.joining(", "));

      logLambda.append(logLambdaPorts + "\n");
    });
    PreesmLogger.getLogger().info(logLambda::toString);
    if (!negativeDP.isEmpty()) {
      throw new PreesmRuntimeException(
          "Some lambda were negative which means that they produce more than 1 bit per cycle. "
              + "Please increase the Initiation Interval of corresponding actors in the scenario to fix that..");
    }
    return lambdaPerPort;
  }

  /**
   * Compute the lambda value for a given rate and II
   *
   * @param rate
   *          production/consumption rate of the port
   * @param ii
   *          initiation interval of the port's actor
   * @return lambda value
   */
  public static BigFraction computeLambda(long rate, long ii) {
    return new BigFraction(-rate, ii).add(1L).multiply(rate);
  }

  static BigInteger ceiling(BigFraction frac) {
    return frac.getNumerator().add(frac.getDenominator()).subtract(BigInteger.ONE).divide(frac.getDenominator());
  }

  static BigInteger floor(BigFraction frac) {
    return frac.getNumerator().divide(frac.getDenominator());
  }

  public static class DecreasingActorIIComparator implements Comparator<ActorNormalizedInfos> {
    @Override
    public int compare(ActorNormalizedInfos arg0, ActorNormalizedInfos arg1) {
      return Long.compare(arg1.oriII, arg0.oriII);
    }
  }

}

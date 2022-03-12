package org.preesm.algorithm.schedule.fpga;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Pair;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation.Result;
import org.ojalgo.optimisation.Optimisation.State;
import org.ojalgo.optimisation.Variable;
import org.preesm.algorithm.mapper.ui.stats.IStatGenerator;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.LongFraction;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
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
    final Map<DataPort, LongFraction> lambdaPerPort = computeAndLogLambdas(mapActorNormalizedInfos);

    // compute the fifo sizes thanks to the ARS ILP formulation of ADFG
    // ILP stands for Integer Linear Programming
    // ARS stands for Affine Relation Synthesis
    // ADFG stands for Affine DataFlow Graph (work of Adnan Bouakaz)
    // ojAlgo dependency should be used to create the model because it has dedicated code to ILP,
    // or Choco (but not dedicated to ILP) at last resort.

    // create intermediate FifoAbstraction graphs
    final DefaultDirectedGraph<AbstractActor, FifoAbstraction> ddg = AbstractGraph.createAbsGraph(flatGraph, brv);
    final DefaultUndirectedGraph<AbstractActor, FifoAbstraction> dug = AbstractGraph.undirectedGraph(ddg);

    // build model
    // create Maps to retrieve ID of variables (ID in order of addition in the model)
    final ExpressionsBasedModel model = new ExpressionsBasedModel();

    // FifoAbstraction to phi Variable ID
    final Map<FifoAbstraction, Integer> fifoAbsToPhiVariableID = new LinkedHashMap<>();
    for (final FifoAbstraction fifoAbs : dug.edgeSet()) {
      final int index = fifoAbsToPhiVariableID.size();
      fifoAbsToPhiVariableID.put(fifoAbs, index);
      // we separate neg. from pos. because unsure that ojAlgo handles negative integers
      final Variable varPhiPos = new Variable("phi_pos_" + index);
      varPhiPos.setInteger(true);
      varPhiPos.lower(0L);
      model.addVariable(varPhiPos);
      final Variable varPhiNeg = new Variable("phi_neg_" + index);
      varPhiNeg.setInteger(true);
      varPhiNeg.lower(0L);
      model.addVariable(varPhiNeg);
    }

    // create intermediate AffineRelation graph and cycle lists
    final DefaultDirectedGraph<AbstractActor,
        AffineRelation> ddgAR = buildGraphAR(ddg, dug, mapActorNormalizedInfos, fifoAbsToPhiVariableID);
    final Set<
        GraphPath<AbstractActor, FifoAbstraction>> cyclesGP = new PatonCycleBase<AbstractActor, FifoAbstraction>(dug)
            .getCycleBasis().getCyclesAsGraphPaths();
    final Set<List<AbstractActor>> cyclesAA = new LinkedHashSet<>();
    cyclesGP.forEach(gp -> cyclesAA.add(gp.getVertexList()));

    // add equations for cycles to the model
    for (final List<AbstractActor> cycleAA : cyclesAA) {
      generateCycleConstraint(ddgAR, cycleAA, model);
    }

    // Fifo to delta/size Variable ID (theta/delay is fixed for us, so not a variable)
    // before using this ID, we must offset it by the number of phi variables (twice the number of FAs)
    final Map<Fifo, Integer> fifoToSizeVariableID = new LinkedHashMap<>();
    // create size variables/equations
    for (final FifoAbstraction fa : ddg.edgeSet()) {
      final AbstractActor src = ddg.getEdgeSource(fa);
      final AbstractActor tgt = ddg.getEdgeTarget(fa);
      final AffineRelation ar = ddgAR.getEdge(src, tgt);

      for (final Fifo fifo : fa.fifos) {
        // create size variable and underflow and overflow expression, set objective
        generateChannelConstraint(scenario, model, fifoToSizeVariableID, mapActorNormalizedInfos, lambdaPerPort, fifo,
            ar);
      }
    }

    // objective function (minimize buffer sizes + phi)
    final Result modelResult = model.minimise();
    logModelAndResults(model, modelResult);

    // fill FIFO sizes map result
    final Map<Fifo, Long> computedFifoSizes = new LinkedHashMap<>();
    final int indexOffset = 2 * ddg.edgeSet().size(); // offset for phi
    fifoToSizeVariableID.forEach((k, v) -> {
      final long size = modelResult.get((long) v + indexOffset).longValue();
      computedFifoSizes.put(k, size);
    });

    final State modelState = modelResult.getState();
    if (modelState != State.OPTIMAL) {
      throw new PreesmRuntimeException("ILP result was not optimal state: " + modelState
          + ".\n Check consistency or retry with extra delays on feedback FIFO buffers.");
    }

    // TODO build a schedule using the normalized graph II and each actor offset (computed by the ILP)
    // same ILP as in ADFG but not fixing Tbasis: only fixing all T being greater than 1
    // result will be a period in number of cycles and will be overestimated
    return new Pair<>(null, computedFifoSizes);
  }

  /**
   * Compute and log all lambda (as map per data port). Lambda are symmetrical: upper = lower.
   * 
   * @param mapActorNormalizedInfos
   *          Standard information about actors, used to get II.
   * @return Map of lambda per data port of all actors in the given map.
   */
  protected static Map<DataPort, LongFraction>
      computeAndLogLambdas(final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos) {
    final Map<DataPort, LongFraction> lambdaPerPort = new LinkedHashMap<>();
    final StringBuilder logLambda = new StringBuilder(
        "Lambda of actor ports (in number of tokens between 0 and the rate, the closest to 0 the better):\n");
    mapActorNormalizedInfos.values().forEach(ani -> {
      logLambda.append(String.format("/actor <%s>\n", ani.aa.getName()));

      final String logLambdaPorts = ani.aa.getAllDataPorts().stream().map(dp -> {
        final long rate = dp.getExpression().evaluate();
        final LongFraction lambdaFr = new LongFraction(-rate, ani.oriII).add(1).multiply(rate);
        // final double lambda = rate * (1.0d - rate / ani.oriII);
        lambdaPerPort.put(dp, lambdaFr);
        return String.format(Locale.US, "%s: %4.2e", dp.getName(), lambdaFr.doubleValue());
      }).collect(Collectors.joining(", "));

      logLambda.append(logLambdaPorts + "\n");
    });
    PreesmLogger.getLogger().info(logLambda::toString);
    return lambdaPerPort;
  }

  /**
   * Log expressions in the model, and variables values.
   * 
   * @param model
   *          Model to log (expressions).
   * @param modelResult
   *          Result to log (variables).
   */
  protected static void logModelAndResults(final ExpressionsBasedModel model, final Result modelResult) {
    final StringBuilder sbLogModel = new StringBuilder("Details of ILP model.\n");
    sbLogModel.append("-- expressions: " + model.countExpressions() + "\n");
    for (final Expression exp : model.getExpressions()) {
      sbLogModel.append(exp.toString() + "\n");
      sbLogModel.append(exp.getLinearEntrySet().stream()
          .map(e -> model.getVariable(e.getKey()).getName() + " x " + e.getValue().longValue())
          .collect(Collectors.joining(" + ")) + "\n");
    }
    sbLogModel.append("-- varibales: " + model.countVariables() + "\n");
    for (int i = 0; i < model.countVariables(); i++) {
      sbLogModel.append(model.getVariable(i).getName() + ": " + modelResult.get(i) + "\n");
    }
    PreesmLogger.getLogger().fine(sbLogModel::toString);
  }

  protected static class AffineRelation {
    protected final long    nProd;
    protected final long    dCons;
    protected final int     phiIndex;
    protected final boolean phiNegate;

    protected AffineRelation(final long nProd, final long dCons, final int phiIndex, final boolean phiNegate) {
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
    DefaultDirectedGraph<AbstractActor, AffineRelation> ddgAR = new DefaultDirectedGraph<>(AffineRelation.class);
    for (final AbstractActor aa : ddg.vertexSet()) {
      ddgAR.addVertex(aa);
    }
    for (final FifoAbstraction fa : dug.edgeSet()) {
      final AbstractActor src = ddg.getEdgeSource(fa);
      final AbstractActor tgt = ddg.getEdgeTarget(fa);
      final long srcII = mapActorNormalizedInfos.get(src).oriII;
      final long tgtII = mapActorNormalizedInfos.get(tgt).oriII;
      final long gcdII = MathFunctionsHelper.gcd(tgtII, srcII);
      final AffineRelation ar = new AffineRelation(fa.getProdRate() * (tgtII / gcdII),
          fa.getConsRate() * (srcII / gcdII), fifoAbsToPhiVariableID.get(fa), false);
      ddgAR.addEdge(src, tgt, ar);
      if (src != tgt) {
        final AffineRelation arReverse = new AffineRelation(ar.dCons, ar.nProd, ar.phiIndex, true);
        ddgAR.addEdge(tgt, src, arReverse);
      }
    }

    return ddgAR;
  }

  /**
   * Fill the model with cycle equations.
   * <p>
   * This method is adapted from the ADFG software. See Adnan Bouakaz thesis p. 70, proposition 2.8 .
   * 
   * @param ddgAR
   *          Directed simple graph of Affine Relation;
   * @param cycleAA
   *          Cycle to consider.
   * @param model
   *          Model where to add the constraint.
   */
  protected static void generateCycleConstraint(final DefaultDirectedGraph<AbstractActor, AffineRelation> ddgAR,
      final List<AbstractActor> cycleAA, final ExpressionsBasedModel model) {
    // cycle is redundant (last == first)
    final int cycleSize = cycleAA.size();
    if (cycleSize <= 2) {
      // since cycle is redundant, then only two actors in it means it is a selfloop
      // phi for self loops is forced to be positive
      if (cycleSize == 2 && cycleAA.get(0) == cycleAA.get(1)) {
        final AffineRelation ar = ddgAR.getEdge(cycleAA.get(0), cycleAA.get(1));
        final int index_2 = ar.phiIndex * 2;
        // TODO force the value oh phi to be II?
        // final Expression expPhiPos = model.addExpression().level(1L);
        // final Variable varPhiPos = model.getVariable(index_2);
        // expPhiPos.set(varPhiPos, 1L);
        final Expression expPhiNeg = model.addExpression().level(0L);
        final Variable varPhiNeg = model.getVariable(index_2 + 1);
        expPhiNeg.set(varPhiNeg, 1L);
        return;
      }
      throw new PreesmRuntimeException("While building model, one cycle could not be considered: "
          + cycleAA.stream().map(AbstractActor::getName).collect(Collectors.joining(" --> ")));
    }
    // the constraint expression must be always equal to 0
    final Expression expression = model.addExpression().level(0L);
    // init arrays storing coefs for memoization
    final AffineRelation[] ars = new AffineRelation[cycleSize];
    final long[] coefsPhi = new long[cycleSize];
    for (int i = 0; i < coefsPhi.length; ++i) {
      coefsPhi[i] = 1;
    }
    int nbPhi = 0;
    long mulN = 1;
    long mulD = 1;
    // update all memoized coefs
    final Iterator<AbstractActor> aaIterator = cycleAA.iterator();
    AbstractActor dest = aaIterator.next();
    while (aaIterator.hasNext()) {
      final AbstractActor src = dest;
      dest = aaIterator.next();
      final AffineRelation ar = ddgAR.getEdge(src, dest);
      ars[nbPhi] = ar;

      for (int i = 0; i < nbPhi; ++i) {
        coefsPhi[i] *= ar.nProd;
      }
      for (int i = nbPhi + 1; i < coefsPhi.length; ++i) {
        coefsPhi[i] *= ar.dCons;
      }
      mulN *= ar.nProd;
      mulD *= ar.dCons;
      long g = MathFunctionsHelper.gcd(mulN, mulD);
      mulN /= g;
      mulD /= g;
      g = coefsPhi[0];
      for (int i = 0; i < coefsPhi.length; ++i) {
        g = MathFunctionsHelper.gcd(g, coefsPhi[i]);
      }
      for (int i = 0; i < coefsPhi.length; ++i) {
        coefsPhi[i] = coefsPhi[i] / g;
      }
      ++nbPhi;
    }

    if (mulN != mulD) {
      throw new PreesmRuntimeException("Some cycles do not satisfy consistency Part 1.");
    }
    // create equation
    for (int i = 0; i < ars.length - 1; ++i) {
      final long coefSign = ars[i].phiNegate ? -1L : 1L;
      final int index_2 = ars[i].phiIndex * 2;
      final Variable varPhiPos = model.getVariable(index_2);
      expression.set(varPhiPos, coefsPhi[i] * coefSign);
      final Variable varPhiNeg = model.getVariable(index_2 + 1);
      expression.set(varPhiNeg, coefsPhi[i] * (-coefSign));
    }

  }

  /**
   * Fill the model with underflow and overflow equations. Also set the minimization objective.
   * <p>
   * See Adnan Bouakaz thesis p. 72-78.
   * 
   * @param scenario
   *          Scenario used to get FIFO data sizes.
   * @param model
   *          Model to consider.
   * @param fifoToSizeVariableID
   *          Map of fifo to variable index in model (to be updated).
   * @param mapActorNormalizedInfos
   *          Map of actor general informations, used to get II.
   * @param lambdaPerPort
   *          Map of port to lambda to consider.
   * @param fifo
   *          Fifo to consider.
   * @param ar
   *          Affine Relation to consider.
   */
  protected static void generateChannelConstraint(final Scenario scenario, final ExpressionsBasedModel model,
      final Map<Fifo, Integer> fifoToSizeVariableID,
      final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos,
      final Map<DataPort, LongFraction> lambdaPerPort, final Fifo fifo, final AffineRelation ar) {
    final int index = fifoToSizeVariableID.size();
    final Variable sizeVar = new Variable("size_" + index);
    sizeVar.setInteger(true);
    sizeVar.lower(0L); // could be refined to max(prod, cons, delau)
    model.addVariable(sizeVar);
    fifoToSizeVariableID.put(fifo, index);
    // write objective for data size to be minimized
    final long typeSizeBits = scenario.getSimulationInfo().getDataTypeSizeInBit(fifo.getType());
    sizeVar.weight(typeSizeBits);
    // write objective for phase to be minimized (weight for positive and negative part should be equal)
    final Variable phi_pos = model.getVariable(ar.phiIndex * 2);
    final Variable phi_neg = model.getVariable(ar.phiIndex * 2 + 1);
    final BigDecimal rawCurrentPhiPosWeight = phi_pos.getContributionWeight();
    final long currentPhiPosWeight = rawCurrentPhiPosWeight != null ? rawCurrentPhiPosWeight.longValue() : 0L;
    final long fifoProdSize = fifo.getSourcePort().getExpression().evaluate();
    long ceilPhiRatio = typeSizeBits * ((fifoProdSize + ar.dCons - 1L) / ar.dCons);
    phi_pos.weight(currentPhiPosWeight + ceilPhiRatio);
    phi_neg.weight(currentPhiPosWeight + ceilPhiRatio);
    // compute delay if any
    final Delay delay = fifo.getDelay();
    long delaySize = 0L;
    if (delay != null) {
      delaySize = delay.getExpression().evaluate();
    }
    // compute coefficients: lambda and others
    final LongFraction lambda_p = lambdaPerPort.get(fifo.getSourcePort());
    final LongFraction lambda_c = lambdaPerPort.get(fifo.getTargetPort());
    final LongFraction lambda_sum = lambda_p.add(lambda_c);
    final LongFraction coef_under = new LongFraction(ar.nProd + ar.dCons - 1L, ar.nProd);
    final LongFraction coef_over = new LongFraction(ar.nProd + ar.dCons - 1L, ar.dCons);
    final AbstractActor src = fifo.getSourcePort().getContainingActor();
    final AbstractActor tgt = fifo.getTargetPort().getContainingActor();
    final long srcII = mapActorNormalizedInfos.get(src).oriII;
    final long tgtII = mapActorNormalizedInfos.get(tgt).oriII;
    final LongFraction a_p = new LongFraction(fifoProdSize, srcII);
    final LongFraction a_c = new LongFraction(fifo.getTargetPort().getExpression().evaluate(), tgtII);
    // get phi variables
    final long coefSign = ar.phiNegate ? -1L : 1L;
    final int index_2 = ar.phiIndex * 2;
    final Variable varPhiPos = model.getVariable(index_2);
    final Variable varPhiNeg = model.getVariable(index_2 + 1);
    // write underflow constraint
    final LongFraction sumConstantU = lambda_sum.subtract(delaySize).add(a_p.multiply(coef_under));
    final LongFraction coefPhiU = a_p.divide(ar.nProd);
    final long lcmDenomU = MathFunctionsHelper.lcm(sumConstantU.getDenominator(), coefPhiU.getDenominator());
    final LongFraction sumConstantUreduced = sumConstantU.multiply(lcmDenomU);
    final LongFraction coefPhiUreduced = coefPhiU.multiply(lcmDenomU);
    // here the reduced denominators should be 1
    final Expression expressionU = model.addExpression().lower(sumConstantUreduced.getNumerator());
    expressionU.set(varPhiPos, coefPhiUreduced.getNumerator() * coefSign);
    expressionU.set(varPhiNeg, coefPhiUreduced.getNumerator() * (-coefSign));
    // write overflow constraint
    final LongFraction sumConstantO = lambda_sum.add(delaySize).add(a_p.multiply(coef_over));
    final LongFraction coefPhiO = a_c.divide(ar.dCons);
    final long lcmDenomO = MathFunctionsHelper.lcm(sumConstantO.getDenominator(), coefPhiO.getDenominator());
    final LongFraction sumConstantOreduced = sumConstantO.multiply(lcmDenomO);
    final LongFraction coefPhiOreduced = coefPhiO.multiply(lcmDenomO);
    // here the reduced denominators should be 1
    final Expression expressionO = model.addExpression().lower(sumConstantOreduced.getNumerator());
    expressionO.set(varPhiPos, coefPhiOreduced.getNumerator() * (-coefSign));
    expressionO.set(varPhiNeg, coefPhiOreduced.getNumerator() * coefSign);
    expressionO.set(sizeVar, 1L);
  }

}

package org.preesm.algorithm.schedule.fpga;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.math3.fraction.BigFraction;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation.Result;
import org.ojalgo.optimisation.Optimisation.State;
import org.ojalgo.optimisation.Variable;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.algorithm.schedule.fpga.AdfgUtils.AffineRelation;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.scenario.Scenario;

/**
 * Class to evaluate buffer sizes thanks to an ADFG abstraction, using ojAlgo internal solver.
 *
 * @author ahonorat
 */
public class AdfgOjalgoFpgaFifoEvaluator extends AbstractGenericFpgaFifoEvaluator {

  public static final String FIFO_EVALUATOR_ADFG_OJALGO_EXACT  = "adfgPOjalgoFifoEvalExact";
  public static final String FIFO_EVALUATOR_ADFG_OJALGO_LINEAR = "adfgOjalgoFifoEvalLinear";

  private final boolean exactEvaluation;

  AdfgOjalgoFpgaFifoEvaluator(boolean exactEvaluation) {
    super();
    // forbid instantiation outside package and inherited classed
    this.exactEvaluation = exactEvaluation;
  }

  @Override
  public void performAnalysis(final Scenario scenario, final AnalysisResultFPGA analysisResult) {

    final Map<AbstractActor,
        ActorNormalizedInfos> mapActorNormalizedInfos = logCheckAndSetActorNormalizedInfos(scenario, analysisResult);

    // create intermediate FifoAbstraction graphs
    final DefaultDirectedGraph<AbstractActor,
        FifoAbstraction> ddg = AbstractGraph.createAbsGraph(analysisResult.flatGraph, analysisResult.flatBrv);
    final DefaultUndirectedGraph<AbstractActor, FifoAbstraction> dug = AbstractGraph.undirectedGraph(ddg);

    // Increase actor II for small differences to avoid overflow in ADFG cycle computation
    AdfgUtils.equalizeII(mapActorNormalizedInfos);

    // compute the lambda of each actor
    final Map<DataPort, BigFraction> lambdaPerPort = AdfgUtils.computeAndLogLambdas(mapActorNormalizedInfos);

    // compute the fifo sizes thanks to the ARS ILP formulation of ADFG
    // ILP stands for Integer Linear Programming
    // ARS stands for Affine Relation Synthesis
    // ADFG stands for Affine DataFlow Graph (work of Adnan Bouakaz)
    // ojAlgo dependency should be used to create the model because it has dedicated code to ILP,
    // or Choco (but not dedicated to ILP) at last resort.

    // build model
    // create Maps to retrieve ID of variables (ID in order of addition in the model)
    final ExpressionsBasedModel model = new ExpressionsBasedModel();

    // FifoAbstraction to phi Variable ID
    final Map<FifoAbstraction, Integer> fifoAbsToPhiVariableID = new LinkedHashMap<>();
    for (final FifoAbstraction fifoAbs : dug.edgeSet()) {
      final int index = fifoAbsToPhiVariableID.size();
      fifoAbsToPhiVariableID.put(fifoAbs, index);
      // we separate neg. from pos. because unsure that ojAlgo handles negative integers
      // commented code corresponds to the former loose ET-II constraint
      // now this constraint is coded with the channel constraints
      final Variable varPhiPos = model.newVariable("phi_pos_" + index);
      varPhiPos.setInteger(exactEvaluation);
      varPhiPos.lower(0L);

      PreesmLogger.getLogger()
          .finer("Created variable " + varPhiPos.getName() + " for fifo abs rep " + fifoAbs.fifos.get(0).getId());

      // if (fifoAbs.isFullyDelayed()) {
      // varPhiPos.lower(0L);
      // } else {
      // // Add phi to represent delay before token production, production happens only during II cycles.
      // final AbstractActor src = ddg.getEdgeSource(fifoAbs);
      // final long srcII = mapActorNormalizedInfos.get(src).oriII;
      // final long srcET = mapActorNormalizedInfos.get(src).oriET;
      // varPhiPos.lower(srcET - srcII);
      // }

      final Variable varPhiNeg = model.newVariable("phi_neg_" + index);
      varPhiNeg.setInteger(exactEvaluation);
      varPhiNeg.lower(0L);
      // if (fifoAbs.isFullyDelayed()) {
      // varPhiNeg.lower(0L);
      // } else {
      // varPhiNeg.level(0L);
      // }
      // note that we cannot set an upper limit to both neg and post part, ojAlgo bug?!
    }

    // create intermediate AffineRelation graph and cycle lists
    final DefaultDirectedGraph<AbstractActor,
        AffineRelation> ddgAR = AdfgUtils.buildGraphAR(ddg, dug, mapActorNormalizedInfos, fifoAbsToPhiVariableID);
    final Set<GraphPath<AbstractActor, FifoAbstraction>> cyclesGP = new PatonCycleBase<>(dug).getCycleBasis()
        .getCyclesAsGraphPaths();
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

    logModel(model);
    // call objective function (minimize buffer sizes + phi)
    final Result modelResult = model.minimise();
    final StringBuilder sbLogResult = new StringBuilder("-- variable final values: " + model.countVariables() + "\n");
    for (int i = 0; i < model.countVariables(); i++) {
      final Variable v = model.getVariable(i);
      sbLogResult.append("var " + v.getName() + " integer = " + modelResult.get(i) + ";\n");
    }
    PreesmLogger.getLogger().finer(sbLogResult::toString);

    final State modelState = modelResult.getState();
    if (modelState != State.OPTIMAL && !model.getVariables().isEmpty()) {
      throw new PreesmRuntimeException("ILP result was not optimal state: " + modelState
          + ".\n Check consistency or retry with extra delays on feedback FIFO buffers.");
    }

    // fill FIFO sizes map result in number of elements
    final Map<Fifo, Long> computedFifoSizes = new LinkedHashMap<>();
    final int indexOffset = 2 * ddg.edgeSet().size(); // offset for phi
    fifoToSizeVariableID.forEach((k, v) -> {
      final long sizeInElts = (long) Math.ceil(modelResult.get((long) v + indexOffset).floatValue());
      final long typeSizeBits = scenario.getSimulationInfo().getDataTypeSizeInBit(k.getType());
      final long fifoSizeInBits = Math.max(sizeInElts, 2) * typeSizeBits;
      computedFifoSizes.put(k, fifoSizeInBits);
      PreesmLogger.getLogger().info("FIFO " + k.getId() + " size: " + fifoSizeInBits + " bits");
    });

    // store the results before returning
    analysisResult.flatFifoSizes = computedFifoSizes;
    // TODO build a schedule using the normalized graph II and each actor offset (computed by the ILP)
    // same ILP as in ADFG but not fixing Tbasis: only fixing all T being greater than 1
    // result will be a period in number of cycles and will be overestimated, seems not useful
  }

  /**
   * Log expressions in the model, and variable domain.
   *
   * @param model
   *          Model to log (expressions).
   */
  protected static void logModel(final ExpressionsBasedModel model) {
    final StringBuilder sbLogModel = new StringBuilder(
        "Details of ILP model (compatible with GNU MathProg Language Reference).\n");
    sbLogModel.append("-- variable initial domain:\n");
    model.getVariables().stream().forEach(v -> sbLogModel
        .append("var " + v.getName() + " integer >= " + v.getLowerLimit() + ", <= " + v.getUpperLimit() + ";\n"));
    sbLogModel.append("minimize o: ");
    sbLogModel.append(model.getVariables().stream().map(v -> v.getContributionWeight() + "*" + v.getName())
        .collect(Collectors.joining(" + ")));
    sbLogModel.append(";\n-- constraints: " + model.countExpressions() + "\n");
    for (final Expression exp : model.getExpressions()) {
      sbLogModel.append("subject to " + exp.getName() + ": " + exp.getLowerLimit() + " <= ");
      sbLogModel.append(exp.getLinearEntrySet().stream()
          .map(e -> e.getValue().longValue() + "*" + model.getVariable(e.getKey()).getName())
          .collect(Collectors.joining(" + ")) + ";\n");
    }
    PreesmLogger.getLogger().finer(sbLogModel::toString);
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

    // init arrays storing coefs for memoization
    final AffineRelation[] ars = new AffineRelation[cycleSize - 1];
    // final long[] coefsPhi = new long[cycleSize - 1];
    final BigInteger[] coefsPhi = new BigInteger[cycleSize - 1];
    for (int i = 0; i < coefsPhi.length; ++i) {
      coefsPhi[i] = BigInteger.ONE;
    }
    int nbPhi = 0;
    long mulN = 1;
    long mulD = 1;
    // update all memoized coefs
    // Algorithm is applying required coefficient to all phi at once, which is equivalent to ADFG proposition 2.8
    final Iterator<AbstractActor> aaIterator = cycleAA.iterator();
    AbstractActor dest = aaIterator.next();
    while (aaIterator.hasNext()) {
      final AbstractActor src = dest;
      dest = aaIterator.next();
      final AffineRelation ar = ddgAR.getEdge(src, dest);
      ars[nbPhi] = ar;

      for (int i = 0; i < nbPhi; ++i) {
        coefsPhi[i] = coefsPhi[i].multiply(BigInteger.valueOf(ar.nProd));
      }
      for (int i = nbPhi + 1; i < coefsPhi.length; ++i) {
        coefsPhi[i] = coefsPhi[i].multiply(BigInteger.valueOf(ar.dCons));
      }
      mulN *= ar.nProd;
      mulD *= ar.dCons;
      final long g = MathFunctionsHelper.gcd(mulN, mulD);
      mulN /= g;
      mulD /= g;
      BigInteger gb = coefsPhi[0];
      for (int i = 1; i < coefsPhi.length; ++i) {
        gb = gb.gcd(coefsPhi[i]);
      }
      for (int i = 0; i < coefsPhi.length; ++i) {
        coefsPhi[i] = coefsPhi[i].divide(gb);
      }
      ++nbPhi;
    }
    if (mulN != mulD) {
      throw new PreesmRuntimeException("Some cycles do not satisfy consistency Part 1.");
    }

    // the constraint expression must be always equal to 0
    final Expression expression = model.addExpression().level(0L);
    // create equation
    for (int i = 0; i < ars.length; ++i) {
      final long coefSign = ars[i].phiNegate ? -1L : 1L;
      final int index_2 = ars[i].phiIndex * 2;
      final Variable varPhiPos = model.getVariable(index_2);
      final long coefPhi = coefsPhi[i].longValueExact();
      expression.set(varPhiPos, coefPhi * coefSign);
      final Variable varPhiNeg = model.getVariable(index_2 + 1);
      expression.set(varPhiNeg, coefPhi * (-coefSign));
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
  protected void generateChannelConstraint(final Scenario scenario, final ExpressionsBasedModel model,
      final Map<Fifo, Integer> fifoToSizeVariableID,
      final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos,
      final Map<DataPort, BigFraction> lambdaPerPort, final Fifo fifo, final AffineRelation ar) {
    final int index = fifoToSizeVariableID.size();
    final Variable sizeVar = model.newVariable("size_" + index);
    PreesmLogger.getLogger().finer(() -> "Created variable " + sizeVar.getName() + " for fifo " + fifo.getId());
    sizeVar.setInteger(exactEvaluation);
    sizeVar.lower(1L); // could be refined to max(prod, cons, delau)
    // ojAlgo seems to bug if we set upper limit above Integer.MAX_VALUE
    fifoToSizeVariableID.put(fifo, index);
    // write objective for data size to be minimized
    // weighted by type size (used only for objective)
    final long typeSizeBits = scenario.getSimulationInfo().getDataTypeSizeInBit(fifo.getType());
    sizeVar.weight(typeSizeBits);
    // write objective for phase to be minimized (weight for positive and negative part should be equal)
    final Variable phiPos = model.getVariable(ar.phiIndex * 2);
    final Variable phiNeg = model.getVariable(ar.phiIndex * 2 + 1);
    final BigDecimal rawCurrentPhiPosWeight = phiPos.getContributionWeight();
    final long currentPhiPosWeight = rawCurrentPhiPosWeight != null ? rawCurrentPhiPosWeight.longValue() : 0L;
    final long fifoProdSize = fifo.getSourcePort().getExpression().evaluate();
    final long ceilPhiRatio = typeSizeBits * ((fifoProdSize + ar.dCons - 1L) / ar.dCons);
    phiPos.weight(currentPhiPosWeight + ceilPhiRatio);
    phiNeg.weight(currentPhiPosWeight + ceilPhiRatio);
    // compute delay if any
    final Delay delay = fifo.getDelay();
    long delaySize = 0L;
    if (delay != null) {
      delaySize = delay.getExpression().evaluate();
    }
    // compute coefficients: lambda and others
    final BigFraction lambdaP = lambdaPerPort.get(fifo.getSourcePort());
    final BigFraction lambdaC = lambdaPerPort.get(fifo.getTargetPort());
    final BigFraction lambdaSum = lambdaP.add(lambdaC);
    final AbstractActor src = fifo.getSourcePort().getContainingActor();
    final AbstractActor tgt = fifo.getTargetPort().getContainingActor();
    final long srcII = mapActorNormalizedInfos.get(src).oriII;
    final long tgtII = mapActorNormalizedInfos.get(tgt).oriII;
    final BigFraction aP = new BigFraction(fifoProdSize, srcII);
    final BigFraction aC = new BigFraction(fifo.getTargetPort().getExpression().evaluate(), tgtII);
    // get phi variables
    final long coefSign = ar.phiNegate ? -1L : 1L;
    final int index_2 = ar.phiIndex * 2;
    final Variable varPhiPos = model.getVariable(index_2);
    final Variable varPhiNeg = model.getVariable(index_2 + 1);
    final StringBuilder constantsLog = new StringBuilder("n = " + ar.nProd + " d = " + ar.dCons + "\n");
    constantsLog.append("a_p = " + aP + " a_c = " + aC + "\n");
    // compute common coefficients
    final BigFraction aCOverd = aC.divide(ar.dCons);
    final BigFraction fractionConstant = lambdaSum.add(aCOverd.multiply(ar.nProd + ar.dCons - 1L));

    // write ET-II constraint
    final long srcTimeDiff = mapActorNormalizedInfos.get(src).oriET - srcII;
    // safe approximation -- needs to be expressed as lower bound
    final BigFraction scaledDelay = aCOverd.reciprocal().multiply(delaySize);
    final long ceiledDelay = AdfgUtils.ceiling(scaledDelay).longValueExact();
    final Expression expressionPhase = model.addExpression().lower(srcTimeDiff - ceiledDelay);
    expressionPhase.set(varPhiPos, coefSign);
    expressionPhase.set(varPhiNeg, -coefSign);

    // write underflow constraint
    final BigFraction fractionSumConstantU = fractionConstant.subtract(delaySize).multiply(aCOverd.reciprocal());
    final long sumConstantU = fractionSumConstantU.getNumerator().longValueExact();
    constantsLog.append("ConstantU = " + sumConstantU + "\n");
    final Expression expressionU = model.addExpression().lower(sumConstantU);
    final long coefPhiU = fractionSumConstantU.getDenominator().longValueExact();
    constantsLog.append("CoefPhiU = " + coefPhiU + "\n");
    expressionU.set(varPhiPos, coefPhiU * coefSign);
    expressionU.set(varPhiNeg, coefPhiU * (-coefSign));
    // write overflow constraint
    final BigFraction fractionSumConstantO = fractionConstant.add(delaySize).multiply(aCOverd.reciprocal());
    final BigFraction fractionCoefSize = aCOverd.reciprocal();
    final long sumConstantO = AdfgUtils.ceiling(fractionSumConstantO).longValueExact();
    final long coefPhiO = 1;
    final long coefSize = AdfgUtils.floor(fractionCoefSize).longValueExact();
    constantsLog.append("ConstantO = " + sumConstantO + "\n");
    constantsLog.append("CoefPhiO = " + coefPhiO + "\n");
    constantsLog.append("CoefSize = " + coefSize + "\n");
    final Expression expressionO = model.addExpression().lower(sumConstantO);
    expressionO.set(varPhiPos, coefPhiO * (-coefSign));
    expressionO.set(varPhiNeg, coefPhiO * (coefSign));
    expressionO.set(sizeVar, coefSize);
    PreesmLogger.getLogger().finer(constantsLog::toString);
  }

}

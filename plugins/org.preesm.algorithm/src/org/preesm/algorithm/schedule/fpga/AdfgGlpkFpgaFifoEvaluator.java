package org.preesm.algorithm.schedule.fpga;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.math3.fraction.BigFraction;
import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
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
 * Class to evaluate buffer sizes thanks to an ADFG abstraction, using GLPK external solver. Code is partly inspired
 * from: https://lists.nongnu.org/archive/html/help-glpk/2009-11/msg00074.html
 *
 * @author ahonorat
 */
public class AdfgGlpkFpgaFifoEvaluator extends AbstractGenericFpgaFifoEvaluator {

  // static {
  // try {
  // // try to load Linux library
  // System.loadLibrary("glpk_java");
  // } catch (final UnsatisfiedLinkError e) {
  // // try to load Windows library
  // PreesmLogger.getLogger().severe("Could not find the glpk_java library in the system, will fail.");
  // }
  // }

  public static final String FIFO_EVALUATOR_ADFG_GLPK_EXACT  = "adfgGlpkFifoEvalExact";
  public static final String FIFO_EVALUATOR_ADFG_GLPK_LINEAR = "adfgGlpkFifoEvalLinear";

  private final boolean exactEvaluation;

  AdfgGlpkFpgaFifoEvaluator(boolean exactEvaluation) {
    super();
    // forbid instantiation outside package and inherited classed
    this.exactEvaluation = exactEvaluation;
  }

  @Override
  public void performAnalysis(Scenario scenario, AnalysisResultFPGA analysisResult) {
    // final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
    // Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
    PreesmLogger.getLogger().warning("This evaluator does not work yet!! Should be fixed one day.");

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
    // external GLPK dependency is used to create the model because ojAlgo seems bugged.

    final glp_prob model = GLPK.glp_create_prob();
    GLPK.glp_set_prob_name(model, "ADFGLPKfpgaFifoEvaluation");
    GLPK.glp_set_obj_name(model, "minSumSizePhi");
    GLPK.glp_set_obj_dir(model, GLPKConstants.GLP_MIN);

    // FifoAbstraction to phi Variable ID
    final Map<FifoAbstraction, Integer> fifoAbsToPhiVariableID = new LinkedHashMap<>();
    for (final FifoAbstraction fifoAbs : dug.edgeSet()) {
      final int index = fifoAbsToPhiVariableID.size();
      fifoAbsToPhiVariableID.put(fifoAbs, index);
      // we separate neg. from pos. because unsure that ojAlgo handles negative integers
      // commented code corresponds to the former loose ET-II constraint
      // now this constraint is coded with the channel constraints
      GLPK.glp_add_cols(model, 2);
      // all index starts from 1 in GLPK

      GLPK.glp_set_col_name(model, index * 2 + 1, "phi_pos_" + index);
      if (exactEvaluation) {
        GLPK.glp_set_col_kind(model, index * 2 + 1, GLPKConstants.GLP_IV);
      } else {
        GLPK.glp_set_col_kind(model, index * 2 + 1, GLPKConstants.GLP_CV);
      }
      GLPK.glp_set_col_bnds(model, index * 2 + 1, GLPKConstants.GLP_LO, 0.0, 0.0);
      PreesmLogger.getLogger().finer("Created variable " + GLPK.glp_get_col_name(model, index * 2 + 1)
          + " for fifo abs rep " + fifoAbs.fifos.get(0).getId());

      GLPK.glp_set_col_name(model, index * 2 + 2, "phi_neg_" + index);
      if (exactEvaluation) {
        GLPK.glp_set_col_kind(model, index * 2 + 2, GLPKConstants.GLP_IV);
      } else {
        GLPK.glp_set_col_kind(model, index * 2 + 2, GLPKConstants.GLP_CV);
      }
      GLPK.glp_set_col_bnds(model, index * 2 + 2, GLPKConstants.GLP_LO, 0.0, 0.0);
    }

    // Fifo to delta/size Variable ID (theta/delay is fixed for us, so not a variable)
    final Map<Fifo, Integer> fifoToSizeVariableID = new LinkedHashMap<>();
    for (final FifoAbstraction fifoAbs : dug.edgeSet()) {
      for (final Fifo fifo : fifoAbs.fifos) {
        // create size variable

        final int indexF = GLPK.glp_add_cols(model, 1);
        // all index starts from 1 in GLPK
        fifoToSizeVariableID.put(fifo, indexF);

        GLPK.glp_set_col_name(model, indexF, "size_" + indexF);

        if (exactEvaluation) {
          GLPK.glp_set_col_kind(model, indexF, GLPKConstants.GLP_IV);
        } else {
          GLPK.glp_set_col_kind(model, indexF, GLPKConstants.GLP_CV);
        }
        GLPK.glp_set_col_bnds(model, indexF, GLPKConstants.GLP_LO, 1.0, 0);
        PreesmLogger.getLogger()
            .finer(() -> "Created variable " + GLPK.glp_get_col_name(model, indexF) + " for fifo " + fifo.getId());
      }
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

    // create size equations

    for (final FifoAbstraction fa : ddg.edgeSet()) {
      final AbstractActor src = ddg.getEdgeSource(fa);
      final AbstractActor tgt = ddg.getEdgeTarget(fa);
      final AffineRelation ar = ddgAR.getEdge(src, tgt);

      for (final Fifo fifo : fa.fifos) {
        generateChannelConstraint(scenario, model, fifoToSizeVariableID, mapActorNormalizedInfos, lambdaPerPort, fifo,
            ar);
      }
    }

    // set objective
    for (final FifoAbstraction fa : ddg.edgeSet()) {
      final AbstractActor src = ddg.getEdgeSource(fa);
      final AbstractActor tgt = ddg.getEdgeTarget(fa);
      final AffineRelation ar = ddgAR.getEdge(src, tgt);

      for (final Fifo fifo : fa.fifos) {
        // create size variable and underflow and overflow expression, set objective
        // generateChannelConstraint(scenario, model, fifoToSizeVariableID, mapActorNormalizedInfos, lambdaPerPort,
        // fifo,
        // ar);
        final int index = fifoToSizeVariableID.get(fifo);
        // write objective for data size to be minimized
        // weighted by type size (used only for objective)
        final long typeSizeBits = scenario.getSimulationInfo().getDataTypeSizeInBit(fifo.getType());
        GLPK.glp_set_obj_coef(model, index, typeSizeBits);
        // write objective for phase to be minimized (weight for positive and negative part should be equal)
        final double rawCurrentPhiPosWeight = GLPK.glp_get_obj_coef(model, ar.phiIndex * 2 + 1);
        // final long currentPhiPosWeight = (long) Math.ceil(rawCurrentPhiPosWeight);
        final long fifoProdSize = fifo.getSourcePort().getExpression().evaluate();
        final long ceilPhiRatio = typeSizeBits * ((fifoProdSize + ar.dCons - 1L) / ar.dCons);
        GLPK.glp_set_obj_coef(model, ar.phiIndex * 2 + 1, rawCurrentPhiPosWeight + ceilPhiRatio);
        GLPK.glp_set_obj_coef(model, ar.phiIndex * 2 + 2, rawCurrentPhiPosWeight + ceilPhiRatio);

      }
    }

    logModel(model);

    // solve model
    int returnValue = GLPK.GLP_EFAIL;
    if (exactEvaluation) {
      final glp_iocp parm = new glp_iocp();
      GLPK.glp_init_iocp(parm);
      returnValue = GLPK.glp_intopt(model, parm);
    } else {
      final glp_smcp parm = new glp_smcp();
      GLPK.glp_init_smcp(parm);
      returnValue = GLPK.glp_simplex(model, parm);
    }
    // 0 == GLPK.GLP_SOL ?
    if (returnValue != 0) {
      throw new PreesmRuntimeException("ILP solver did not succeed, return value is: " + returnValue
          + ".\n Check consistency or retry with extra delays on feedback FIFO buffers.");
    }

    final int nbCols = GLPK.glp_get_num_cols(model);
    final StringBuilder sbLogResult = new StringBuilder("# variable final values: " + nbCols + "\n");
    // we have only integer variables without upper limit in our case
    for (int i = 1; i <= nbCols; ++i) {
      if (GLPK.glp_get_col_type(model, i) == GLPK.GLP_IV) {
        sbLogResult
            .append("var " + GLPK.glp_get_col_name(model, i) + " integer = " + GLPK.glp_get_col_prim(model, i) + ";\n");
      } else {
        sbLogResult.append("var " + GLPK.glp_get_col_name(model, i) + " = " + GLPK.glp_get_col_prim(model, i) + ";\n");
      }
    }

    // fill FIFO sizes map result in number of elements
    final Map<Fifo, Long> computedFifoSizes = new LinkedHashMap<>();
    fifoToSizeVariableID.forEach((k, v) -> {
      final long sizeInElts = (long) Math.ceil(GLPK.glp_get_col_prim(model, v));
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

    // // 99- set back default class loader
    // Thread.currentThread().setContextClassLoader(oldContextClassLoader);
  }

  /**
   * Log expressions in the model, and variable domain.
   *
   * @param model
   *          Model to log (expressions).
   */
  protected static void logModel(final glp_prob model) {
    final StringBuilder sbLogModel = new StringBuilder(
        "Details of ILP model (compatible with GNU MathProg Language Reference).\n");
    sbLogModel.append("# variable initial domain:\n");
    final int nbCols = GLPK.glp_get_num_cols(model);
    // we have only integer variables without upper limit in our case
    for (int i = 1; i <= nbCols; ++i) {
      if (GLPK.glp_get_col_type(model, i) == GLPK.GLP_IV) {
        sbLogModel
            .append("var " + GLPK.glp_get_col_name(model, i) + " integer >= " + GLPK.glp_get_col_lb(model, i) + ";\n");
      } else {
        sbLogModel.append("var " + GLPK.glp_get_col_name(model, i) + " >= " + GLPK.glp_get_col_lb(model, i) + ";\n");
      }
    }
    sbLogModel.append("minimize o: ");
    for (int i = 1; i < nbCols; ++i) {
      sbLogModel.append(GLPK.glp_get_obj_coef(model, i) + "*" + GLPK.glp_get_col_name(model, i) + " + ");
    }
    sbLogModel.append(GLPK.glp_get_obj_coef(model, nbCols) + "*" + GLPK.glp_get_col_name(model, nbCols));
    final int nbRows = GLPK.glp_get_num_rows(model);
    sbLogModel.append(";\n# constraints: " + nbRows + "\n");
    for (int i = 1; i <= nbRows; ++i) {
      sbLogModel
          .append("subject to " + GLPK.glp_get_row_name(model, i) + ": " + GLPK.glp_get_row_lb(model, i) + " <= ");
      // we have only expressions with lower limit in our case, except for cycles (lower = upper = 0)
      final int lenMat = GLPK.glp_get_mat_row(model, i, null, null);
      final SWIGTYPE_p_int ind = GLPK.new_intArray(lenMat);
      final SWIGTYPE_p_double val = GLPK.new_doubleArray(lenMat);
      GLPK.glp_get_mat_row(model, i, ind, val);
      for (int j = 1; j < lenMat; ++j) {
        final int varIndex = GLPK.intArray_getitem(ind, j);
        final double varVal = GLPK.doubleArray_getitem(val, j);
        sbLogModel.append(Double.toString(varVal) + "*" + GLPK.glp_get_col_name(model, varIndex) + " + ");
      }
      final int varIndex = GLPK.intArray_getitem(ind, lenMat);
      final double varVal = GLPK.doubleArray_getitem(val, lenMat);
      sbLogModel.append(Double.toString(varVal) + "*" + GLPK.glp_get_col_name(model, varIndex));
      if (GLPK.glp_get_row_type(model, i) == GLPK.GLP_FX) {
        sbLogModel.append(" <= " + GLPK.glp_get_row_lb(model, i));
      }
      sbLogModel.append(";\n");
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
      final List<AbstractActor> cycleAA, final glp_prob model) {
    // cycle is redundant (last == first)
    final int cycleSize = cycleAA.size();
    if (cycleSize <= 2) {
      // since cycle is redundant, then only two actors in it means it is a selfloop
      // phi for self loops is forced to be positive
      if (cycleSize == 2 && cycleAA.get(0) == cycleAA.get(1)) {
        final AffineRelation ar = ddgAR.getEdge(cycleAA.get(0), cycleAA.get(1));
        final int index_2 = ar.phiIndex * 2 + 1;
        // TODO force the value oh phi to be II?
        // final Expression expPhiPos = model.addExpression().level(1L);
        // final Variable varPhiPos = model.getVariable(index_2);
        // expPhiPos.set(varPhiPos, 1L);

        final int row_num = GLPK.glp_add_rows(model, 1);
        GLPK.glp_set_row_name(model, row_num, "cycleCstr" + Integer.toString(row_num));
        GLPK.glp_set_row_bnds(model, row_num, GLPKConstants.GLP_FX, 0.0, 0.0);
        final SWIGTYPE_p_int ind = GLPK.new_intArray(1);
        GLPK.intArray_setitem(ind, 1, index_2 + 1);
        final SWIGTYPE_p_double val = GLPK.new_doubleArray(1);
        GLPK.doubleArray_setitem(val, 1, 1.0);
        GLPK.glp_set_mat_row(model, row_num, 1, ind, val);
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
    final int row_num = GLPK.glp_add_rows(model, 1);
    GLPK.glp_set_row_name(model, row_num, "cycleCstr" + Integer.toString(row_num));
    GLPK.glp_set_row_bnds(model, row_num, GLPKConstants.GLP_FX, 0.0, 0.0);
    final SWIGTYPE_p_int ind = GLPK.new_intArray(ars.length * 2);
    final SWIGTYPE_p_double val = GLPK.new_doubleArray(ars.length * 2);
    // create equation
    for (int i = 0; i < ars.length; ++i) {
      final long coefSign = ars[i].phiNegate ? -1L : 1L;
      final int index_2 = ars[i].phiIndex * 2 + 1;
      GLPK.intArray_setitem(ind, i * 2 + 1, index_2);
      GLPK.intArray_setitem(ind, i * 2 + 2, index_2 + 1);

      final double coefPhi = coefsPhi[i].doubleValue();
      GLPK.doubleArray_setitem(val, i * 2 + 1, coefPhi * coefSign);
      GLPK.doubleArray_setitem(val, i * 2 + 2, coefPhi * (-coefSign));
    }
    GLPK.glp_set_mat_row(model, row_num, ars.length * 2, ind, val);

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
  protected void generateChannelConstraint(final Scenario scenario, final glp_prob model,
      final Map<Fifo, Integer> fifoToSizeVariableID,
      final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos,
      final Map<DataPort, BigFraction> lambdaPerPort, final Fifo fifo, final AffineRelation ar) {

    final int indexF = fifoToSizeVariableID.get(fifo);

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
    final long fifoProdSize = fifo.getSourcePort().getExpression().evaluate();
    final BigFraction aP = new BigFraction(fifoProdSize, srcII);
    final BigFraction aC = new BigFraction(fifo.getTargetPort().getExpression().evaluate(), tgtII);
    // get phi variables
    final long coefSign = ar.phiNegate ? -1L : 1L;
    final int index_2 = ar.phiIndex * 2 + 1;
    final StringBuilder constantsLog = new StringBuilder("n = " + ar.nProd + " d = " + ar.dCons + "\n");
    constantsLog.append("a_p = " + aP + " a_c = " + aC + "\n");
    // compute common coefficients
    final BigFraction aCOverd = aC.divide(ar.dCons);
    final BigFraction fractionConstant = lambdaSum.add(aCOverd.multiply(ar.nProd + ar.dCons - 1L));

    // write ET-II constraint
    final long srcTimeDiff = mapActorNormalizedInfos.get(src).oriET - srcII;
    // safe approximation -- needs to be expressed as lower bound
    final BigFraction scaledDelay = aCOverd.reciprocal().multiply(delaySize);
    final double ceiledDelay = AdfgUtils.ceiling(scaledDelay).doubleValue();
    final double lowBnd = srcTimeDiff - ceiledDelay;
    final int row_num = GLPK.glp_add_rows(model, 2);
    GLPK.glp_set_row_name(model, row_num, "iietCstr" + Integer.toString(row_num));
    GLPK.glp_set_row_bnds(model, row_num, GLPKConstants.GLP_LO, lowBnd, 0.0);
    final SWIGTYPE_p_int ind = GLPK.new_intArray(2);
    GLPK.intArray_setitem(ind, 1, index_2);
    GLPK.intArray_setitem(ind, 2, index_2 + 1);
    final SWIGTYPE_p_double val = GLPK.new_doubleArray(2);
    GLPK.doubleArray_setitem(val, 1, coefSign);
    GLPK.doubleArray_setitem(val, 2, -coefSign);
    GLPK.glp_set_mat_row(model, row_num, 2, ind, val);

    // write underflow constraint
    final BigFraction fractionSumConstantU = fractionConstant.subtract(delaySize).multiply(aCOverd.reciprocal());
    final long sumConstantU = fractionSumConstantU.getNumerator().longValueExact();
    constantsLog.append("ConstantU = " + sumConstantU + "\n");
    final double coefPhiU = fractionSumConstantU.getDenominator().doubleValue();
    constantsLog.append("CoefPhiU = " + coefPhiU + "\n");
    GLPK.glp_set_row_name(model, row_num + 1, "underflowCstr" + Integer.toString(row_num + 1));
    GLPK.glp_set_row_bnds(model, row_num + 1, GLPKConstants.GLP_LO, sumConstantU, 0.0);
    final SWIGTYPE_p_int indu = GLPK.new_intArray(2);
    GLPK.intArray_setitem(indu, 1, index_2);
    GLPK.intArray_setitem(indu, 2, index_2 + 1);
    final SWIGTYPE_p_double valu = GLPK.new_doubleArray(2);
    GLPK.doubleArray_setitem(valu, 1, coefPhiU * coefSign);
    GLPK.doubleArray_setitem(valu, 2, coefPhiU * (-coefSign));
    GLPK.glp_set_mat_row(model, row_num + 1, 2, indu, valu);

    // FOLLOWING CODE FAILS, DONT KNOW WHY!!! should not be commented
    // write overflow constraint
    // final BigFraction fractionSumConstantO = fractionConstant.add(delaySize).multiply(aCOverd.reciprocal());
    // final BigFraction fractionCoefSize = aCOverd.reciprocal();
    // final long sumConstantO = AdfgUtils.ceiling(fractionSumConstantO).longValueExact();
    // final double coefPhiO = 1.0;
    // final double coefSize = AdfgUtils.floor(fractionCoefSize).doubleValue();
    // constantsLog.append("ConstantO = " + sumConstantO + "\n");
    // constantsLog.append("CoefPhiO = " + coefPhiO + "\n");
    // constantsLog.append("CoefSize = " + coefSize + "\n");
    // final int row_num3 = GLPK.glp_add_rows(model, 1);
    // GLPK.glp_set_row_name(model, row_num3, "overflowCstr" + Integer.toString(row_num + 2));
    // GLPK.glp_set_row_bnds(model, row_num3, GLPKConstants.GLP_LO, sumConstantO, 0.0);
    // final SWIGTYPE_p_int indo = GLPK.new_intArray(3);
    // GLPK.intArray_setitem(indo, 1, index_2);
    // GLPK.intArray_setitem(indo, 2, index_2 + 1);
    // GLPK.intArray_setitem(indo, 3, indexF);
    // final SWIGTYPE_p_double valo = GLPK.new_doubleArray(3);
    // GLPK.doubleArray_setitem(valo, 1, coefPhiO * (-coefSign));
    // GLPK.doubleArray_setitem(valo, 2, coefPhiO * coefSign);
    // GLPK.doubleArray_setitem(valo, 3, coefSize);
    // GLPK.glp_set_mat_row(model, row_num3, 3, indo, valo);

    PreesmLogger.getLogger().finer(constantsLog::toString);
  }

}

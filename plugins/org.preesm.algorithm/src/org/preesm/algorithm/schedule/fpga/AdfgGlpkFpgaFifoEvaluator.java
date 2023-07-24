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
import org.gnu.glpk.glp_prob;
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
import org.preesm.model.scenario.Scenario;

/**
 * Class to evaluate buffer sizes thanks to an ADFG abstraction, using GLPK external solver. Code is partly inspired
 * from: https://lists.nongnu.org/archive/html/help-glpk/2009-11/msg00074.html
 *
 * @author ahonorat
 */
public class AdfgGlpkFpgaFifoEvaluator extends AbstractGenericFpgaFifoEvaluator {

  static {
    try {
      // try to load Linux library
      System.loadLibrary("glpk_java");
    } catch (final UnsatisfiedLinkError e) {
      // try to load Windows library
      PreesmLogger.getLogger().severe("Could not find the glpk_java library in the system, will fail.");
    }
  }

  public static final String FIFO_EVALUATOR_ADFG_GLPK_EXACT  = "adfgPGlpkFifoEvalExact";
  public static final String FIFO_EVALUATOR_ADFG_GLPK_LINEAR = "adfgGlpkFifoEvalLinear";

  private final boolean exactEvaluation;

  AdfgGlpkFpgaFifoEvaluator(boolean exactEvaluation) {
    super();
    // forbid instantiation outside package and inherited classed
    this.exactEvaluation = exactEvaluation;
  }

  @Override
  public void performAnalysis(Scenario scenario, AnalysisResultFPGA analysisResult) {

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
      GLPK.glp_set_col_bnds(model, index * 2 + 1, GLPKConstants.GLP_LO, 0, 0);
      PreesmLogger.getLogger().finer("Created variable " + GLPK.glp_get_col_name(model, index * 2 + 1)
          + " for fifo abs rep " + fifoAbs.fifos.get(0).getId());

      GLPK.glp_set_col_name(model, index * 2 + 2, "phi_neg_" + index);
      if (exactEvaluation) {
        GLPK.glp_set_col_kind(model, index * 2 + 2, GLPKConstants.GLP_IV);
      } else {
        GLPK.glp_set_col_kind(model, index * 2 + 2, GLPKConstants.GLP_CV);
      }
      GLPK.glp_set_col_bnds(model, index * 2 + 2, GLPKConstants.GLP_LO, 0, 0);
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
        GLPK.glp_set_row_bnds(model, 1, GLPKConstants.GLP_FX, 0, 0);
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
    GLPK.glp_set_row_bnds(model, 1, GLPKConstants.GLP_FX, 0, 0);
    // create equation
    for (int i = 0; i < ars.length; ++i) {
      final long coefSign = ars[i].phiNegate ? -1L : 1L;
      final int index_2 = ars[i].phiIndex * 2 + 1;
      final SWIGTYPE_p_int ind = GLPK.new_intArray(2);
      GLPK.intArray_setitem(ind, 1, index_2);
      GLPK.intArray_setitem(ind, 2, index_2 + 1);

      final long coefPhi = coefsPhi[i].longValueExact();
      final SWIGTYPE_p_double val = GLPK.new_doubleArray(2);
      GLPK.doubleArray_setitem(val, 1, coefPhi * coefSign);
      GLPK.doubleArray_setitem(val, 2, coefPhi * (-coefSign));

      GLPK.glp_set_mat_row(model, row_num, 2, ind, val);
    }

    // TODO finish !!

  }

}

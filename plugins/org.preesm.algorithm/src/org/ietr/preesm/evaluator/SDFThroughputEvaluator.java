/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * blaunay <bapt.launay@gmail.com> (2015)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2018)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.preesm.evaluator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.preesm.mathematicalModels.PeriodicScheduleModelOjAlgo;
import org.preesm.commons.logger.PreesmLogger;

// TODO: Auto-generated Javadoc
/**
 * Class used to compute the optimal periodic schedule and the throughput of a non hierarchical graph (SDF or already
 * flattened IBSDF).
 *
 * @author blaunay
 */
public class SDFThroughputEvaluator extends ThroughputEvaluator {

  /**
   * Computes the throughput on the optimal periodic schedule (if it exists) of a given graph under the given scenario.
   *
   * @param inputGraph
   *          the normalized graph
   * @return the period of the optimal periodic schedule
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  @Override
  public double launch(final SDFGraph inputGraph) throws InvalidExpressionException {

    double period;
    final SDFGraph sdf = inputGraph.copy();

    // Check condition of existence of a periodic schedule (Bellman Ford)
    final boolean periodic_schedule = has_periodic_schedule(sdf);

    if (periodic_schedule) {
      // Find the cycle with L/H max (using linear program)
      period = period_computation(sdf);
      // Deduce throughput of the schedule
    } else {
      PreesmLogger.getLogger().log(Level.SEVERE, "No periodic schedule for this graph ");
      return 0;
    }
    return period;
  }

  /**
   * Computes the optimal periodic schedule for the given graph and scenario, using a linear program solved by GLPK.
   *
   * @param sdf
   *          the sdf
   * @return the optimal normalized period
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  private double period_computation(final SDFGraph sdf) throws InvalidExpressionException {
    // // Map to associate each edge with an index
    // final ArrayList<SDFEdge> edges = new ArrayList<>(sdf.edgeSet());
    // double H;
    // int r;
    // int k;
    // long L;
    //
    // // Now we create the model for the problem and for GLPK
    // glp_prob prob;
    //
    // prob = GLPK.glp_create_prob();
    // GLPK.glp_set_prob_name(prob, "Max_Cycle_L/H");
    // GLPK.glp_set_obj_dir(prob, GLPKConstants.GLP_MAX); // maximization problem
    //
    // // Number of constraints nbvertex+1
    // GLPK.glp_add_rows(prob, sdf.vertexSet().size() + 1);
    // // Number of variables nbedge
    // GLPK.glp_add_cols(prob, sdf.edgeSet().size());
    //
    // for (int j = 1; j <= edges.size(); j++) {
    // // Bounds on the variables : >=0
    // GLPK.glp_set_col_bnds(prob, j, GLPKConstants.GLP_LO, 0.0, 0.0);
    // // Continuous variables
    // GLPK.glp_set_col_kind(prob, j, GLPKConstants.GLP_CV);
    // // Retrieve timing of the actor (coef in obj function)
    // if (edges.get(j - 1).getSource() instanceof SDFSourceInterfaceVertex) {
    // L = 0;
    // } else {
    // L = this.scenar.getTimingManager().getTimingOrDefault(edges.get(j - 1).getTarget().getId(), "x86").getTime();
    // }
    // GLPK.glp_set_obj_coef(prob, j, L);
    // }
    //
    // // Set the bounds on constraints
    // for (int j = 1; j <= sdf.vertexSet().size(); j++) {
    // GLPK.glp_set_row_bnds(prob, j, GLPKConstants.GLP_FX, 0.0, 0.0);
    // }
    // GLPK.glp_set_row_bnds(prob, sdf.vertexSet().size() + 1, GLPKConstants.GLP_FX, 1, 1);
    //
    // SWIGTYPE_p_int ind = GLPK.new_intArray(edges.size() + 1);
    // SWIGTYPE_p_double val = GLPK.new_doubleArray(edges.size() + 1);
    //
    // // First constraint
    // int n = 1;
    // for (final SDFAbstractVertex vertex : sdf.vertexSet()) {
    // k = 1;
    // for (final SDFInterfaceVertex in : vertex.getSources()) {
    // r = edges.indexOf(vertex.getAssociatedEdge(in));
    // // check that the edge does not loop on the actor
    // // (if that is the case, the variable is ignored in the constraint)
    // if (edges.get(r).getSource() != vertex) {
    // GLPK.intArray_setitem(ind, k, r + 1);
    // GLPK.doubleArray_setitem(val, k, 1.0);
    // k++;
    // }
    // }
    // for (final SDFInterfaceVertex out : vertex.getSinks()) {
    // r = edges.indexOf(vertex.getAssociatedEdge(out));
    // if (edges.get(r).getTarget() != vertex) {
    // GLPK.intArray_setitem(ind, k, r + 1);
    // GLPK.doubleArray_setitem(val, k, -1.0);
    // k++;
    // }
    // }
    // GLPK.glp_set_mat_row(prob, n, k - 1, ind, val);
    // n++;
    // ind = GLPK.new_intArray(edges.size() + 1);
    // val = GLPK.new_doubleArray(edges.size() + 1);
    // }
    //
    // // Second constraint : sum of H.x = 1
    // for (int j = 1; j <= edges.size(); j++) {
    // GLPK.intArray_setitem(ind, j, j);
    // H = ((double) (edges.get(j - 1).getDelay().getValue())
    // + SDFMathD.gcd((double) (edges.get(j - 1).getCons().getValue()), (double) (edges.get(j -
    // 1).getProd().getValue())))
    // - (double) (edges.get(j - 1).getCons().getValue());
    // GLPK.doubleArray_setitem(val, j, H);
    // }
    //
    // GLPK.glp_set_mat_row(prob, sdf.vertexSet().size() + 1, edges.size(), ind, val);
    //
    // final glp_smcp parm = new glp_smcp();
    // GLPK.glp_init_smcp(parm);
    // parm.setMsg_lev(GLPKConstants.GLP_MSG_OFF);
    // // Launch the resolution
    // GLPK.glp_simplex(prob, parm);
    //
    // // Write the complete model in a file (optional)
    // // GLPK.glp_write_lp(prob, null, "model.lp");
    //
    // // The objective value gives us the normalized period
    // final double period = GLPK.glp_get_obj_val(prob);
    // GLPK.glp_delete_prob(prob);
    // GLPK.glp_free_env();

    // return period;
    final PeriodicScheduleModelOjAlgo method = new PeriodicScheduleModelOjAlgo();
    return method.computeNormalizedPeriod(sdf).doubleValue();
  }

  /**
   * Checks if a periodic schedule can be computed for the given SDF graph.
   *
   * @param input
   *          the input
   * @return true, if successful
   */
  private boolean has_periodic_schedule(final SDFGraph input) {
    final Map<SDFAbstractVertex, Double> v = new LinkedHashMap<>();
    final Map<SDFEdge, Double> e = new LinkedHashMap<>();

    // Init the weights on the edges (i,j) : w = M0 + gcd(i,j) - Zj
    for (final SDFEdge edge : input.edgeSet()) {
      e.put(edge,
          ((double) (edge.getDelay().getValue())
              + SDFMathD.gcd((double) (edge.getProd().getValue()), (double) (edge.getCons().getValue())))
              - (double) (edge.getCons().getValue()));
    }

    // Initialization : source.dist = 0, v.dist = infinity
    for (final SDFAbstractVertex vertex : input.vertexSet()) {
      // v.dist = infinity
      v.put(vertex, Double.POSITIVE_INFINITY);

      // Add the edge looping on the actor
      final SDFEdge loop = input.addEdge(vertex, vertex);
      final SDFSourceInterfaceVertex in = new SDFSourceInterfaceVertex();
      in.setName(vertex.getName() + "In");
      final SDFSinkInterfaceVertex out = new SDFSinkInterfaceVertex();
      out.setName(vertex.getName() + "Out");
      AbstractEdgePropertyType<?> x;
      if (vertex.getSources().size() != 0) {
        x = vertex.getAssociatedEdge(vertex.getSources().get(0)).getCons();
      } else {
        x = vertex.getAssociatedEdge(vertex.getSinks().get(0)).getProd();
      }
      vertex.addSource(in);
      vertex.addSink(out);
      loop.setSourceInterface(out);
      loop.setTargetInterface(in);
      loop.setDelay(x);
      loop.setCons(x);
      loop.setProd(x);
      e.put(loop, (double) (loop.getDelay().getValue()));
    }
    // launch a Bellman Ford algorithm
    // source.dist = 0
    v.put(input.vertexSet().iterator().next(), (double) 0);

    // Relax all the edges
    for (int i = 1; i <= (v.size() - 1); i++) {
      for (final Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
        if ((v.get(entry.getKey().getSource()) + entry.getValue()) < v.get(entry.getKey().getTarget())) {
          v.put(entry.getKey().getTarget(), v.get(entry.getKey().getSource()) + entry.getValue());
        }
      }
    }

    // Check for negative cycle
    for (final Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
      if ((v.get(entry.getKey().getSource()) + entry.getValue()) < v.get(entry.getKey().getTarget())) {
        // Cycle of negative weight found, condition H(c) > 0 not respected -> no periodic schedule
        return false;
      }
    }
    return true;
  }
}

package org.ietr.preesm.mathematicalModels;

import java.util.Hashtable;
import java.util.Map.Entry;
import org.apache.commons.lang3.math.Fraction;
import org.gnu.glpk.GLPK;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.preesm.throughput.helpers.MathFunctionsHelper;

/**
 * @author hderoui
 *
 */
public class PeriodicScheduleModel_GLPK {
  public Hashtable<String, Integer> edgeIndex;
  // get solution

  /**
   * @param SDF
   *          graph
   * @return normalized period
   */
  public Fraction computeNormalizedPeriod(SDFGraph SDF) {

    // adding an array for the variable index
    this.edgeIndex = new Hashtable<>(SDF.edgeSet().size());

    // set the index for decision variable
    int index = 1;
    for (SDFEdge e : SDF.edgeSet()) {
      edgeIndex.put((String) e.getPropertyBean().getValue("edgeName"), index++);
    }

    // ----- create the GLPK model ---------------------------------
    glp_prob prob = GLPK.glp_create_prob();
    GLPK.glp_set_prob_name(prob, "Max_Cycle_L/H");

    // ----- Objectif function -------------------------------------
    GLPK.glp_set_obj_dir(prob, GLPK.GLP_MAX);

    // ----- Variables --------------------------------------------
    GLPK.glp_add_cols(prob, SDF.edgeSet().size()); // declare the array of variables
    for (SDFEdge e : SDF.edgeSet()) { // set the bounds for constraints
      GLPK.glp_set_col_bnds(prob, edgeIndex.get((String) e.getPropertyBean().getValue("edgeName")), GLPK.GLP_LO, 0.0, 0.0); // all variable must be positive
      GLPK.glp_set_col_kind(prob, edgeIndex.get((String) e.getPropertyBean().getValue("edgeName")), GLPK.GLP_CV); // set the type of the variables
      GLPK.glp_set_obj_coef(prob, edgeIndex.get((String) e.getPropertyBean().getValue("edgeName")),
          (double) e.getSource().getPropertyBean().getValue("duration")); // coefficient in the objective function
    }

    // ----- Constraints ------------------------------------------
    GLPK.glp_add_rows(prob, SDF.vertexSet().size() + 1); // the number of constraints

    SWIGTYPE_p_int ind = GLPK.new_intArray(SDF.edgeSet().size()); // index array for column
    SWIGTYPE_p_double val = GLPK.new_doubleArray(SDF.edgeSet().size()); // value array for column

    // First constraint
    int n = 1;
    for (SDFAbstractVertex vertex : SDF.vertexSet()) {
      int k = 1;
      // skip the reentrant edges
      for (SDFInterfaceVertex input : vertex.getSources()) {
        SDFEdge e = vertex.getAssociatedEdge(input);
        if (e.getSource().getName() != vertex.getName()) {
          GLPK.intArray_setitem(ind, k, edgeIndex.get((String) e.getPropertyBean().getValue("edgeName"))); // add the index of the variable to the constraint
          GLPK.doubleArray_setitem(val, k, 1.0); // set the coefficient of the variable in the constraints
          k++; // Increments the column counter
        }
      }

      // skip the reentrant edges
      for (SDFInterfaceVertex output : vertex.getSinks()) {
        SDFEdge e = vertex.getAssociatedEdge(output);
        if (e.getTarget().getName() != vertex.getName()) {
          GLPK.intArray_setitem(ind, k, edgeIndex.get((String) e.getPropertyBean().getValue("edgeName"))); // add the index of the variable in the constraints
          GLPK.doubleArray_setitem(val, k, -1.0); // set the coefficient of the variable in the constraint
          k++; // increments the column counter
        }
      }

      GLPK.glp_set_mat_row(prob, n, k - 1, ind, val); // add the constraints to the model
      GLPK.glp_set_row_bnds(prob, n, GLPK.GLP_FX, 0.0, 0.0); // the bound for the first constraint

      ind = GLPK.new_intArray(SDF.edgeSet().size()); // reset the array index
      val = GLPK.new_doubleArray(SDF.edgeSet().size()); // reset the array value
      n++;
    }

    // Second constraint : sum of H.x = 1
    int j = 1;
    for (SDFEdge e : SDF.edgeSet()) {
      GLPK.intArray_setitem(ind, j, edgeIndex.get((String) e.getPropertyBean().getValue("edgeName")));
      double h = (e.getDelay().intValue() - e.getCons().intValue() + MathFunctionsHelper.gcd(e.getProd().intValue(), e.getCons().intValue()))
          * (double) e.getPropertyBean().getValue("normalizationFactor");
      GLPK.doubleArray_setitem(val, j, h);
      j++;
    }

    GLPK.glp_set_mat_row(prob, n, SDF.edgeSet().size(), ind, val); // add the constraint to the model
    GLPK.glp_set_row_bnds(prob, n, GLPK.GLP_FX, 1, 1); // the bound for the second constraint

    // ----- solver parameter -------------------------------------
    glp_smcp parm = new glp_smcp();
    GLPK.glp_init_smcp(parm);
    // parm.setMsg_lev(GLPK.GLP_MSG_OFF);

    // Launch the resolution
    GLPK.glp_simplex(prob, parm);

    // Write the complete model in a file (optional)
    // GLPK.glp_write_lp(prob, null, "mode"+SDF.id+".lp");

    // The objective value gives us the normalized period
    Fraction period = Fraction.getFraction(GLPK.glp_get_obj_val(prob));
    GLPK.glp_delete_prob(prob);
    GLPK.glp_free_env();
    return period;
  }

  /**
   * @param index
   *          edge index
   * @return edge
   */
  @SuppressWarnings("unused")
  private String getEdge(int index) {
    for (Entry<String, Integer> entry : this.edgeIndex.entrySet()) {
      if (entry.getValue() == index) {
        return entry.getKey();
      }
    }
    return null;
  }
}

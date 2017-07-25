package org.ietr.preesm.mathematicalModels;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import java.util.Hashtable;
import org.apache.commons.lang.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.preesm.throughput.helpers.MathFunctionsHelper;

/**
 * @author hderoui
 *
 */
public class PeriodicScheduleModel_Gurobi {
  public Hashtable<String, GRBVar> edgeVar;

  /**
   * @param SDF
   *          graph
   * @return normalized period
   */
  public Fraction computeNormalizedPeriod(SDFGraph SDF) {
    // Stopwatch timerPerSche = new Stopwatch();
    // timerPerSche.start();

    edgeVar = new Hashtable<String, GRBVar>(SDF.edgeSet().size());
    Fraction period = Fraction.getFraction(0);

    try {
      // ----- create the Gurobi model ---------------------------------
      GRBEnv env = new GRBEnv();
      GRBModel model = new GRBModel(env);
      model.set(GRB.StringAttr.ModelName, "Max_Cycle_L/H");

      // ----- Variables ---------------------------------------------
      for (SDFEdge e : SDF.edgeSet()) {
        this.edgeVar.put((String) e.getPropertyBean().getValue("edgeName"),
            model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, (String) e.getPropertyBean().getValue("edgeName")));
      }
      model.update();

      // ----- Objectif function -------------------------------------
      GRBLinExpr expr = new GRBLinExpr();
      for (SDFEdge e : SDF.edgeSet()) {
        expr.addTerm((double) e.getSource().getPropertyBean().getValue("duration"), edgeVar.get((String) e.getPropertyBean().getValue("edgeName")));
      }
      model.setObjective(expr, GRB.MAXIMIZE);

      // ----- Constraints ------------------------------------------
      // First constraint
      for (SDFAbstractVertex a : SDF.vertexSet()) {
        expr = new GRBLinExpr();
        // skip the reentrant edges
        for (SDFInterfaceVertex input : a.getSources()) {
          SDFEdge e = a.getAssociatedEdge(input);
          if (e.getSource().getName() != a.getName()) {
            expr.addTerm(1.0, edgeVar.get((String) e.getPropertyBean().getValue("edgeName")));
          }
        }

        // skip the reentrant edges
        for (SDFInterfaceVertex output : a.getSinks()) {
          SDFEdge e = a.getAssociatedEdge(output);
          if (e.getTarget().getName() != a.getName()) {
            expr.addTerm(-1.0, edgeVar.get((String) e.getPropertyBean().getValue("edgeName")));
          }
        }
        model.addConstr(expr, GRB.EQUAL, 0.0, a.getName());
      }

      // Second constraint : sum of H.x = 1
      expr = new GRBLinExpr();
      for (SDFEdge e : SDF.edgeSet()) {
        double h = (e.getDelay().intValue() - e.getCons().intValue() + MathFunctionsHelper.gcd(e.getProd().intValue(), e.getCons().intValue()))
            * (double) e.getPropertyBean().getValue("normalizationFactor");
        expr.addTerm(h, edgeVar.get((String) e.getPropertyBean().getValue("edgeName")));
      }
      model.addConstr(expr, GRB.EQUAL, 1.0, "sumHX");

      // ----- solve the problem -------------------------------------
      model.optimize();

      if (model.get(GRB.DoubleAttr.ObjVal) > 0) {
        period = Fraction.getFraction(model.get(GRB.DoubleAttr.ObjVal));
      }
      model.dispose();
      env.dispose();
    } catch (GRBException e) {
      // TODO Auto-generated catch block
      // e1.printStackTrace();
    }

    // timerPerSche.stop();
    // System.out.println("SDF Graph Scheduled in " + timerPerSche.toString());

    // set he normalized period
    SDF.setPropertyValue("normalizedPeriod", period);
    System.out.println("Kopt found !! " + period);

    return period;

  }
}

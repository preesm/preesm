package org.ietr.preesm.mathematicalModels;

import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;

public class PeriodicScheduleModel_Gurobi {
  public Hashtable<String, GRBVar> edgeVar;

  public double getNormalizedPeriod(SDFGraph SDF) {
    Stopwatch timerPerSche = new Stopwatch();
    timerPerSche.start();

    edgeVar = new Hashtable<String, GRBVar>(SDF.edges.size());
    double period = 0.;
    try {
      // ----- create the Gurobi model ---------------------------------
      GRBEnv env = new GRBEnv();
      GRBModel model = new GRBModel(env);
      model.set(GRB.StringAttr.ModelName, "Max_Cycle_L/H");

      // ----- Variables ---------------------------------------------
      for (Edge e : SDF.edges.values())
        this.edgeVar.put(e.id, model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, e.id));
      model.update();

      // ----- Objectif function -------------------------------------
      GRBLinExpr expr = new GRBLinExpr();
      for (Edge e : SDF.edges.values())
        expr.addTerm(e.sourceActor.duration, edgeVar.get(e.id));
      model.setObjective(expr, GRB.MAXIMIZE);

      // ----- Constraints ------------------------------------------
      // First constraint
      for (Actor a : SDF.actors.values()) {
        expr = new GRBLinExpr();
        // skip the reentrant edges
        for (Edge in : a.InputEdges.values())
          if (in.sourceActor.id != a.id)
            expr.addTerm(1.0, edgeVar.get(in.id));
        // skip the reentrant edges
        for (Edge out : a.OutputEdges.values())
          if (out.targetActor.id != a.id)
            expr.addTerm(-1.0, edgeVar.get(out.id));

        model.addConstr(expr, GRB.EQUAL, 0.0, a.id);
      }

      // Second constraint : sum of H.x = 1
      expr = new GRBLinExpr();
      for (Edge e : SDF.edges.values())
        expr.addTerm((e.initialMarking - e.prod + tool.gcd(e.cons, e.prod)) * e.normalizationFactor, edgeVar.get(e.id));
      model.addConstr(expr, GRB.EQUAL, 1.0, "sumHX");

      // ----- solve the problem -------------------------------------
      model.optimize();

      if (model.get(GRB.DoubleAttr.ObjVal) > 0)
        period = model.get(GRB.DoubleAttr.ObjVal);

      model.dispose();
      env.dispose();
    } catch (GRBException e) {
      // TODO Auto-generated catch block
      // e1.printStackTrace();
    }

    timerPerSche.stop();
    System.out.println("SDF Graph Scheduled in " + timerPerSche.toString());

    return period;

  }
}

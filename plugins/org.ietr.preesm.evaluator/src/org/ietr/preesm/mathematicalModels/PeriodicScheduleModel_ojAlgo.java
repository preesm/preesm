package org.ietr.preesm.mathematicalModels;

import java.util.Hashtable;
import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.preesm.throughput.tools.helpers.MathFunctionsHelper;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

/**
 * @author hderoui
 *
 */
public class PeriodicScheduleModel_ojAlgo implements SolverMethod {
  public Hashtable<String, Variable> edgeVariables;

  /**
   * @param SDF
   *          graph
   * @return normalized period
   */
  public Fraction computeNormalizedPeriod(SDFGraph SDF) {
    // Stopwatch timerPerSche = new Stopwatch();
    // timerPerSche.start();

    // ----- Variables ---------------------------------------------
    edgeVariables = new Hashtable<String, Variable>(SDF.edgeSet().size());
    for (SDFEdge e : SDF.edgeSet()) {
      this.edgeVariables.put((String) e.getPropertyBean().getValue("edgeName"), Variable.make((String) e.getPropertyBean().getValue("edgeName")).lower(0)
          .upper(1).weight((Double) e.getSource().getPropertyBean().getValue("duration")));

    }

    // ----- ojAlgo model ---------------------------------
    ExpressionsBasedModel model = new ExpressionsBasedModel();
    model.addVariables(edgeVariables.values());

    // ----- Constraints ------------------------------------------
    // First constraint
    for (SDFAbstractVertex a : SDF.vertexSet()) {
      Expression expr = model.addExpression(a.getName()).lower(0).upper(0);
      // skip the reentrant edges
      for (SDFInterfaceVertex input : a.getSources()) {
        SDFEdge e = a.getAssociatedEdge(input);
        if (e.getSource().getName() != a.getName()) {
          expr.set(edgeVariables.get((String) e.getPropertyBean().getValue("edgeName")), 1);
        }
      }

      // skip the reentrant edges
      for (SDFInterfaceVertex output : a.getSinks()) {
        SDFEdge e = a.getAssociatedEdge(output);
        if (e.getTarget().getName() != a.getName()) {
          expr.set(edgeVariables.get((String) e.getPropertyBean().getValue("edgeName")), -1);
        }
      }
    }

    // Second constraint : sum of H.x = 1
    Expression expr = model.addExpression("sumHX").lower(1).upper(1);
    for (SDFEdge e : SDF.edgeSet()) {
      double h = (e.getDelay().intValue() - e.getCons().intValue() + MathFunctionsHelper.gcd(e.getProd().intValue(), e.getCons().intValue()))
          * (double) e.getPropertyBean().getValue("normalizationFactor");
      expr.set(edgeVariables.get((String) e.getPropertyBean().getValue("edgeName")), h);
    }

    // ----- solve the problem -------------------------------------
    Optimisation.Result result = model.maximise();
    Fraction period = Fraction.getFraction(result.getValue());

    // timerPerSche.stop();
    // System.out.println("SDF Graph Scheduled in " + timerPerSche.toString());

    // set he normalized period
    SDF.setPropertyValue("normalizedPeriod", period);
    System.out.println("Normalized period found K = " + period);
    return period;
  }

}

package org.ietr.preesm.deadlock;

import java.util.Hashtable;
import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.ietr.preesm.throughput.helpers.MathFunctionsHelper;

/**
 * @author hderoui
 *
 */
public abstract class SDFConsistency {

  private static Hashtable<String, Fraction> reps;

  /**
   * Computes the Repetition Vector (RV) of the SDF graph
   * 
   * @param graph
   *          SDF graph
   * @return true if the graph is consistent, false if not
   */
  public static boolean computeRV(SDFGraph graph) {
    reps = new Hashtable<String, Fraction>();
    for (SDFAbstractVertex actor : graph.vertexSet()) {
      reps.put(actor.getName(), Fraction.getFraction(0));
    }

    // pick a random actor and do a DFS to compute RV
    SDFConsistency.SetReps(graph, graph.vertexSet().iterator().next(), Fraction.getFraction(1));

    // compute the lcm of the denominators
    double lcm = 1;
    for (Fraction f : reps.values()) {
      lcm = MathFunctionsHelper.lcm(lcm, f.getDenominator());
    }
    // Set actors repetition factor
    for (SDFAbstractVertex actor : graph.vertexSet()) {
      actor.setNbRepeat((int) ((reps.get(actor.getName()).getNumerator() * lcm) / reps.get(actor.getName()).getDenominator()));
    }
    // edge verification
    for (SDFEdge e : graph.edgeSet()) {
      if (e.getSource().getNbRepeatAsInteger() * e.getProd().intValue() != e.getTarget().getNbRepeatAsInteger() * e.getCons().intValue()) {
        System.err.println("Graph not consistent !!");
        return false;
      }
    }

    // change the interfaces cons/prod rate
    for (SDFAbstractVertex actor : graph.vertexSet()) {
      // input interface
      if (actor instanceof SDFSourceInterfaceVertex) {
        SDFEdge e = actor.getAssociatedEdge(actor.getSinks().iterator().next());
        int prod = e.getProd().intValue();
        e.setProd(new SDFIntEdgePropertyType(prod * actor.getNbRepeatAsInteger()));
        actor.setNbRepeat(1);
      }
      // output interface
      if (actor instanceof SDFSinkInterfaceVertex) {
        SDFEdge e = actor.getAssociatedEdge(actor.getSources().iterator().next());
        int cons = e.getCons().intValue();
        e.setCons(new SDFIntEdgePropertyType(cons * actor.getNbRepeatAsInteger()));
        actor.setNbRepeat(1);
      }
    }

    System.out.println("Graph consistent !!");
    return true;
  }

  private static int SetReps(SDFGraph g, SDFAbstractVertex a, Fraction n) {
    reps.put(a.getName(), n);
    // DFS forward
    for (SDFInterfaceVertex output : a.getSinks()) {
      SDFEdge e = a.getAssociatedEdge(output);
      Fraction fa = reps.get(e.getTarget().getName());
      if (fa.getNumerator() == 0) {
        Fraction f = Fraction.getFraction(n.getNumerator() * e.getProd().intValue(), n.getDenominator() * e.getCons().intValue());
        SDFConsistency.SetReps(g, e.getTarget(), f.reduce());
      }
    }

    // DFS backward
    for (SDFInterfaceVertex input : a.getSources()) {
      SDFEdge e = a.getAssociatedEdge(input);
      Fraction fa = reps.get(e.getSource().getName());
      if (fa.getNumerator() == 0) {
        Fraction f = Fraction.getFraction(n.getNumerator() * e.getCons().intValue(), n.getDenominator() * e.getProd().intValue());
        SDFConsistency.SetReps(g, e.getSource(), f.reduce());
      }
    }
    return 1;
  }
}

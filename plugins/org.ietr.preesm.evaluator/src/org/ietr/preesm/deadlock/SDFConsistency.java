/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017)
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
package org.ietr.preesm.deadlock;

import java.util.Hashtable;
import java.util.List;
import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.ietr.preesm.throughput.tools.helpers.MathFunctionsHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;

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
    Stopwatch timer = new Stopwatch();
    timer.start();

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
        timer.stop();
        System.err.println("Graph not consistent !! evaluated in " + timer.toString());
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

    timer.stop();
    System.out.println("Graph consistent !! evaluated in " + timer.toString());
    return true;
  }

  private static int SetReps(SDFGraph g, SDFAbstractVertex a, Fraction n) {
    reps.put(a.getName(), n);
    // DFS forward
    List<SDFInterfaceVertex> Sinks = a.getSinks();
    List<SDFInterfaceVertex> Sources = a.getSources();

    for (SDFInterfaceVertex output : Sinks) {
      SDFEdge e = a.getAssociatedEdge(output);
      Fraction fa = reps.get(e.getTarget().getName());
      if (fa.getNumerator() == 0) {
        Fraction f = Fraction.getFraction(n.getNumerator() * e.getProd().intValue(), n.getDenominator() * e.getCons().intValue());
        SDFConsistency.SetReps(g, e.getTarget(), f.reduce());
      }
    }

    // DFS backward
    for (SDFInterfaceVertex input : Sources) {
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

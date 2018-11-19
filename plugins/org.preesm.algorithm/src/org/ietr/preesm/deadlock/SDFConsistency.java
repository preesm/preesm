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

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.fraction.BigFraction;
import org.ietr.preesm.evaluator.EvaluationException;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.preesm.algorithm.model.types.LongEdgePropertyType;
import org.preesm.commons.math.MathFunctionsHelper;

/**
 * @author hderoui
 *
 */
public abstract class SDFConsistency {

  private SDFConsistency() {
    // forbid instantiation
  }

  private static final Map<String, BigFraction> reps = new LinkedHashMap<>();

  /**
   * Computes the Repetition Vector (RV) of the SDF graph
   *
   * @param graph
   *          SDF graph
   * @return true if the graph is consistent, false if not
   */
  public static boolean computeRV(final SDFGraph graph) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    SDFConsistency.reps.clear();
    for (final SDFAbstractVertex actor : graph.vertexSet()) {
      SDFConsistency.reps.put(actor.getName(), BigFraction.ZERO);
    }

    // pick a random actor and do a DFS to compute RV
    SDFConsistency.setReps(graph, graph.vertexSet().iterator().next(), BigFraction.ONE);

    // compute the lcm of the denominators
    long lcm = 1;
    for (final BigFraction f : SDFConsistency.reps.values()) {
      lcm = MathFunctionsHelper.lcm(lcm, f.getDenominator().longValue());
    }
    // Set actors repetition factor
    for (final SDFAbstractVertex actor : graph.vertexSet()) {
      final String actorName = actor.getName();
      final BigFraction actorRepetition = SDFConsistency.reps.get(actorName);
      actor.setNbRepeat(actorRepetition.multiply(lcm).longValue());
    }
    // edge verification
    for (final SDFEdge e : graph.edgeSet()) {
      if ((e.getSource().getNbRepeatAsLong() * e.getProd().longValue()) != (e.getTarget().getNbRepeatAsLong()
          * e.getCons().longValue())) {
        timer.stop();
        final String message = "Graph not consistent !! evaluated in " + timer.toString();
        throw new EvaluationException(message);
      }
    }

    // change the interfaces cons/prod rate
    for (final SDFAbstractVertex actor : graph.vertexSet()) {
      // input interface
      if (actor instanceof SDFSourceInterfaceVertex) {
        final SDFEdge e = actor.getAssociatedEdge(actor.getSinks().iterator().next());
        final long prod = e.getProd().longValue();
        e.setProd(new LongEdgePropertyType(prod * actor.getNbRepeatAsLong()));
        actor.setNbRepeat(1L);
      }
      // output interface
      if (actor instanceof SDFSinkInterfaceVertex) {
        final SDFEdge e = actor.getAssociatedEdge(actor.getSources().iterator().next());
        final long cons = e.getCons().longValue();
        e.setCons(new LongEdgePropertyType(cons * actor.getNbRepeatAsLong()));
        actor.setNbRepeat(1L);
      }
    }

    timer.stop();
    return true;
  }

  private static int setReps(final SDFGraph g, final SDFAbstractVertex a, final BigFraction n) {
    SDFConsistency.reps.put(a.getName(), n);
    // DFS forward
    final List<SDFSinkInterfaceVertex> sinks = a.getSinks();
    final List<SDFSourceInterfaceVertex> sources = a.getSources();

    for (final SDFSinkInterfaceVertex output : sinks) {
      final SDFEdge e = a.getAssociatedEdge(output);
      final BigFraction fa = SDFConsistency.reps.get(e.getTarget().getName());
      if (fa.getNumerator().equals(BigInteger.ZERO)) {
        final long numerator = n.getNumerator().longValue() * e.getProd().longValue();
        final long denominator = n.getDenominator().longValue() * e.getCons().longValue();
        final BigFraction f = new BigFraction(numerator, denominator);
        SDFConsistency.setReps(g, e.getTarget(), f.reduce());
      }
    }

    // DFS backward
    for (final SDFSourceInterfaceVertex input : sources) {
      final SDFEdge e = a.getAssociatedEdge(input);
      final BigFraction fa = SDFConsistency.reps.get(e.getSource().getName());
      if (fa.getNumerator().equals(BigInteger.ZERO)) {
        final long numerator = n.getNumerator().longValue() * e.getCons().longValue();
        final long denominator = n.getDenominator().longValue() * e.getProd().longValue();
        final BigFraction f = new BigFraction((int) numerator, (int) denominator);
        SDFConsistency.setReps(g, e.getSource(), f.reduce());
      }
    }
    return 1;
  }
}

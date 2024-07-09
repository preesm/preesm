/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Baptiste Launay [bapt.launay@gmail.com] (2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
package org.preesm.algorithm.evaluator;

import org.preesm.algorithm.model.IGraphVisitor;
import org.preesm.algorithm.model.IInterface;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.commons.math.MathFunctionsHelper;

/**
 * Visitor used to normalize a graph, hierarchical (IBSDF) or not (SDF).
 *
 * @author blaunay
 */
public class NormalizeVisitor implements IGraphVisitor<SDFGraph, SDFAbstractVertex, SDFEdge> {

  private static final String PORT_LITERAL  = "port";
  private static final String INPUT_LITERAL = "Input";
  /** The output graph. */
  private SDFGraph            outputGraph;

  /**
   * Gets the output.
   *
   * @return the output
   */
  public SDFGraph getOutput() {
    return this.outputGraph;
  }

  @Override
  public void visit(final SDFGraph sdf) {

    this.outputGraph = sdf;

    // change values on the edges from int to double before normalization
    prepareNorm(this.outputGraph);
    // Normalization bottom->up
    normalizeup(this.outputGraph);
    // Normalization up->bottom
    normalizeDown(this.outputGraph, 0);
  }

  @Override
  public void visit(final SDFEdge sdfEdge) {
    // nothing to do on edges
  }

  @Override
  public void visit(final SDFAbstractVertex sdfVertex) {
    // nothing to do on abstract vertices
  }

  /**
   * Converts all the data on the edges of the given graph from int to double, which is more convenient to do the
   * normalization (32 bits may not be enough for some lcm).
   *
   * @param g
   *          the g
   */
  private void prepareNorm(final SDFGraph g) {
    // Use double instead of int for all edges
    for (final SDFEdge edge : g.edgeSet()) {
      edge.setProd(new SDFDoubleEdgePropertyType((edge.getProd().longValue())));
      edge.setDelay(new SDFDoubleEdgePropertyType((edge.getDelay().longValue())));
      edge.setCons(new SDFDoubleEdgePropertyType((edge.getCons().longValue())));
    }
    // Apply the same to the lower levels of hierarchy
    for (final SDFAbstractVertex v : g.vertexSet()) {
      if (v.getGraphDescription() instanceof final SDFGraph sdfGraph) {
        prepareNorm(sdfGraph);
      }
    }
  }

  /**
   * First step of the normalization of the graph, normalizing all levels of hierarchy from the bottom to the top of the
   * graph.
   *
   * @param g
   *          the g
   * @return the double
   */
  private double normalizeup(final SDFGraph g) {
    // M = ppcm (N_t * z_t, N_t * in_a)
    double m = 1;
    for (final SDFAbstractVertex vertex : g.vertexSet()) {
      m = normalizeVertices(m, vertex);
    }

    // new Z for each actor : M / repet_actor
    for (final SDFEdge edge : g.edgeSet()) {
      normalizeEdges(m, edge);
    }
    return m;
  }

  private void normalizeEdges(double m, final SDFEdge edge) {
    double in;
    // sink port
    if (edge.getTarget().getKind().equals(PORT_LITERAL)) {
      in = (double) edge.getProd().getValue();
      edge.setProd(new SDFDoubleEdgePropertyType(m / edge.getSource().getNbRepeatAsLong()));
      edge.setCons(new SDFDoubleEdgePropertyType(m));
      edge.setDelay(new SDFDoubleEdgePropertyType(
          ((double) edge.getProd().getValue() / in) * (double) edge.getDelay().getValue()));
    } else if (edge.getSource().getKind().equals(PORT_LITERAL)) {
      // source port
      in = (double) edge.getCons().getValue();
      edge.setCons(new SDFDoubleEdgePropertyType(m / edge.getTarget().getNbRepeatAsLong()));
      edge.setProd(new SDFDoubleEdgePropertyType(m));
      edge.setDelay(new SDFDoubleEdgePropertyType(
          ((double) edge.getCons().getValue() / in) * (double) edge.getDelay().getValue()));
    } else {
      in = (double) edge.getProd().getValue();
      edge.setProd(new SDFDoubleEdgePropertyType(m / edge.getSource().getNbRepeatAsLong()));
      edge.setCons(new SDFDoubleEdgePropertyType(m / edge.getTarget().getNbRepeatAsLong()));
      edge.setDelay(new SDFDoubleEdgePropertyType(
          ((double) edge.getProd().getValue() / in) * (double) (edge.getDelay().getValue())));
    }
  }

  private double normalizeVertices(double m, final SDFAbstractVertex vertex) {
    double z;
    double in;
    double out;
    z = 1;
    if (!vertex.getKind().equals(PORT_LITERAL)) {
      if (vertex.getGraphDescription() instanceof final SDFGraph sdfGraph) {
        // If it's a hierarchical actor, normalize its subgraph to obtain the z
        z = normalizeup(sdfGraph);

        // Retrieve the values on the output edges, used to compute M (ppcm (N_t * in_a))
        m = updateM(m, vertex);
      } else {
        // the vertex is a "normal" actor, we compute its z with the in & out rates
        // of its adjacent edges
        for (final IInterface port : vertex.getInterfaces()) {
          if (port.getDirection().toString().equals(INPUT_LITERAL)) {
            out = (double) ((SDFEdge) vertex.getAssociatedEdge(port)).getCons().getValue();
            z = MathFunctionsHelper.lcm(z, out);
            m = MathFunctionsHelper.lcm(m, vertex.getNbRepeatAsLong() * out);
          } else {
            in = (double) ((SDFEdge) vertex.getAssociatedEdge(port)).getProd().getValue();
            z = MathFunctionsHelper.lcm(z, in);
            // ppcm (N_t * in_a)
            m = MathFunctionsHelper.lcm(m, vertex.getNbRepeatAsLong() * in);
          }
        }
      }
      // M = ppcm(N_t * z_t)
      m = MathFunctionsHelper.lcm(m, vertex.getNbRepeatAsLong() * z);
    }
    return m;
  }

  private double updateM(double m, final SDFAbstractVertex vertex) {
    for (final SDFInterfaceVertex port : vertex.getSinks()) {
      m = MathFunctionsHelper.lcm(m,
          vertex.getNbRepeatAsLong() * (double) vertex.getAssociatedEdge(port).getProd().getValue());
    }
    for (final SDFInterfaceVertex port : vertex.getSources()) {
      m = MathFunctionsHelper.lcm(m,
          vertex.getNbRepeatAsLong() * (double) vertex.getAssociatedEdge(port).getCons().getValue());
    }
    return m;
  }

  /**
   * Second step of the normalization of the graph, making sure that the normalization between levels is coherent.
   *
   * @param g
   *          the g
   * @param z
   *          the z
   */
  private void normalizeDown(final SDFGraph g, final double z) {
    final double m = 1;
    double zUp;

    // if Z == 0, it is the level zero of hierarchy, nothing to do here
    if (z != 0) {
      normalizeDown(g, z, m);
    }

    for (final SDFAbstractVertex vertex : g.vertexSet()) {
      // For each hierarchic actor
      if (vertex.getGraphDescription() instanceof final SDFGraph sdfGraph) {
        // Retrieve the normalization value of the actor (Z)
        if (vertex.getInterfaces().get(0).getDirection().toString().equals(INPUT_LITERAL)) {
          zUp = (double) (((SDFEdge) vertex.getAssociatedEdge(vertex.getInterfaces().get(0))).getCons().getValue());
        } else {
          zUp = (double) (((SDFEdge) vertex.getAssociatedEdge(vertex.getInterfaces().get(0))).getProd().getValue());
        }
        // Continue the normalization in the lower levels
        normalizeDown(sdfGraph, zUp);
      }
    }
  }

  private void normalizeDown(final SDFGraph g, final double Z, double m) {
    // retrieve the value on the interfaces
    for (final SDFAbstractVertex v : g.vertexSet()) {
      if (v.getKind().equals(PORT_LITERAL)) {
        if (v.getInterfaces().get(0).getDirection().toString().equals(INPUT_LITERAL)) {
          m = (double) ((SDFEdge) v.getAssociatedEdge(v.getInterfaces().get(0))).getCons().getValue();
        } else {
          m = (double) ((SDFEdge) v.getAssociatedEdge(v.getInterfaces().get(0))).getProd().getValue();
        }
      }
    }
    // if Z == M, no need to multiply anything
    if (Z != m) {
      // Need to multiply rates of the subgraph on the edges by Z/M
      for (final SDFEdge edge : g.edgeSet()) {
        edge.setProd(new SDFDoubleEdgePropertyType((double) (edge.getProd().getValue()) * (Z / m)));
        edge.setCons(new SDFDoubleEdgePropertyType((double) (edge.getCons().getValue()) * (Z / m)));
        edge.setDelay(new SDFDoubleEdgePropertyType((double) (edge.getDelay().getValue()) * (Z / m)));
      }
    }
  }
}

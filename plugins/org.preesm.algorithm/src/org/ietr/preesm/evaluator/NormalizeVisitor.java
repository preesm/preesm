/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * blaunay <bapt.launay@gmail.com> (2015)
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

import org.preesm.algorithm.model.IInterface;
import org.preesm.algorithm.model.parameters.InvalidExpressionException;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.algorithm.model.visitors.IGraphVisitor;
import org.preesm.algorithm.model.visitors.SDF4JException;

/**
 * Visitor used to normalize a graph, hierarchical (IBSDF) or not (SDF).
 *
 * @author blaunay
 */
public class NormalizeVisitor implements IGraphVisitor<SDFGraph, SDFAbstractVertex, SDFEdge> {

  /** The output graph. */
  private SDFGraph outputGraph;

  /**
   * Gets the output.
   *
   * @return the output
   */
  public SDFGraph getOutput() {
    return this.outputGraph;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public void visit(final SDFGraph sdf) throws SDF4JException {

    this.outputGraph = sdf;

    // change values on the edges from int to double before normalization
    prepareNorm(this.outputGraph);
    // Normalization bottom->up
    normalizeup(this.outputGraph);
    // Normalization up->bottom
    normalizedown(this.outputGraph, 0);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractEdge)
   */
  @Override
  public void visit(final SDFEdge sdfEdge) {
    // nothing to do on edges
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractVertex)
   */
  @Override
  public void visit(final SDFAbstractVertex sdfVertex) throws SDF4JException {
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
    try {
      for (final SDFEdge edge : g.edgeSet()) {
        edge.setProd(new SDFDoubleEdgePropertyType((edge.getProd().longValue())));
        edge.setDelay(new SDFDoubleEdgePropertyType((edge.getDelay().longValue())));
        edge.setCons(new SDFDoubleEdgePropertyType((edge.getCons().longValue())));
      }
    } catch (final InvalidExpressionException e) {
      throw new EvaluationException("Could not prepare normalization", e);
    }
    // Apply the same to the lower levels of hierarchy
    for (final SDFAbstractVertex v : g.vertexSet()) {
      if (v.getGraphDescription() instanceof SDFGraph) {
        prepareNorm((SDFGraph) v.getGraphDescription());
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
    double M = 1;
    double z = 1;
    double in;
    double out;
    try {
      for (final SDFAbstractVertex vertex : g.vertexSet()) {
        z = 1;
        if (!vertex.getKind().equals("port")) {
          if (vertex.getGraphDescription() instanceof SDFGraph) {
            // If it's a hierarchical actor, normalize its subgraph to obtain the z
            z = normalizeup((SDFGraph) vertex.getGraphDescription());

            // Retrieve the values on the output edges, used to compute M (ppcm (N_t * in_a))
            for (final SDFInterfaceVertex port : vertex.getSinks()) {
              M = SDFMathD.lcm(M,
                  vertex.getNbRepeatAsLong() * (double) vertex.getAssociatedEdge(port).getProd().getValue());
            }
            for (final SDFInterfaceVertex port : vertex.getSources()) {
              M = SDFMathD.lcm(M,
                  vertex.getNbRepeatAsLong() * (double) vertex.getAssociatedEdge(port).getCons().getValue());
            }
          } else {
            // the vertex is a "normal" actor, we compute its z with the in & out rates
            // of its adjacent edges
            for (final IInterface port : vertex.getInterfaces()) {
              if (port.getDirection().toString().equals("Input")) {
                out = (double) ((SDFEdge) vertex.getAssociatedEdge(port)).getCons().getValue();
                z = SDFMathD.lcm(z, out);
                M = SDFMathD.lcm(M, vertex.getNbRepeatAsLong() * out);
              } else {
                in = (double) ((SDFEdge) vertex.getAssociatedEdge(port)).getProd().getValue();
                z = SDFMathD.lcm(z, in);
                // ppcm (N_t * in_a)
                M = SDFMathD.lcm(M, vertex.getNbRepeatAsLong() * in);
              }
            }
          }
          // M = ppcm(N_t * z_t)
          M = SDFMathD.lcm(M, vertex.getNbRepeatAsLong() * z);
        }
      }

      // new Z for each actor : M / repet_actor
      for (final SDFEdge edge : g.edgeSet()) {
        // sink port
        if (edge.getTarget().getKind().equals("port")) {
          in = (double) edge.getProd().getValue();
          edge.setProd(new SDFDoubleEdgePropertyType(M / edge.getSource().getNbRepeatAsLong()));
          edge.setCons(new SDFDoubleEdgePropertyType(M));
          edge.setDelay(new SDFDoubleEdgePropertyType(
              ((double) edge.getProd().getValue() / in) * (double) edge.getDelay().getValue()));
        } else {
          // source port
          if (edge.getSource().getKind().equals("port")) {
            in = (double) edge.getCons().getValue();
            edge.setCons(new SDFDoubleEdgePropertyType(M / edge.getTarget().getNbRepeatAsLong()));
            edge.setProd(new SDFDoubleEdgePropertyType(M));
            edge.setDelay(new SDFDoubleEdgePropertyType(
                ((double) edge.getCons().getValue() / in) * (double) edge.getDelay().getValue()));
          } else {
            in = (double) edge.getProd().getValue();
            edge.setProd(new SDFDoubleEdgePropertyType(M / edge.getSource().getNbRepeatAsLong()));
            edge.setCons(new SDFDoubleEdgePropertyType(M / edge.getTarget().getNbRepeatAsLong()));
            edge.setDelay(new SDFDoubleEdgePropertyType(
                ((double) edge.getProd().getValue() / in) * (double) (edge.getDelay().getValue())));
          }
        }
      }
    } catch (final InvalidExpressionException e) {
      // Auto-generated catch block
      e.printStackTrace();
    }
    return M;
  }

  /**
   * Second step of the normalization of the graph, making sure that the normalization between levels is coherent.
   *
   * @param g
   *          the g
   * @param Z
   *          the z
   */
  private void normalizedown(final SDFGraph g, final double Z) {
    double M = 1;
    double z_up;

    // if Z == 0, it is the level zero of hierarchy, nothing to do here
    if (Z != 0) {
      // retrieve the value on the interfaces
      for (final SDFAbstractVertex v : g.vertexSet()) {
        if (v.getKind().equals("port")) {
          if (v.getInterfaces().get(0).getDirection().toString().equals("Input")) {
            M = (double) ((SDFEdge) v.getAssociatedEdge(v.getInterfaces().get(0))).getCons().getValue();
          } else {
            M = (double) ((SDFEdge) v.getAssociatedEdge(v.getInterfaces().get(0))).getProd().getValue();
          }
        }
      }
      // if Z == M, no need to multiply anything
      if (Z != M) {
        // Need to multiply rates of the subgraph on the edges by Z/M
        for (final SDFEdge edge : g.edgeSet()) {
          edge.setProd(new SDFDoubleEdgePropertyType((double) (edge.getProd().getValue()) * (Z / M)));
          edge.setCons(new SDFDoubleEdgePropertyType((double) (edge.getCons().getValue()) * (Z / M)));
          edge.setDelay(new SDFDoubleEdgePropertyType((double) (edge.getDelay().getValue()) * (Z / M)));
        }
      }
    }

    for (final SDFAbstractVertex vertex : g.vertexSet()) {
      // For each hierarchic actor
      if ((vertex.getGraphDescription() != null) && (vertex.getGraphDescription() instanceof SDFGraph)) {
        // Retrieve the normalization value of the actor (Z)
        if (vertex.getInterfaces().get(0).getDirection().toString().equals("Input")) {
          z_up = (double) (((SDFEdge) vertex.getAssociatedEdge(vertex.getInterfaces().get(0))).getCons().getValue());
        } else {
          z_up = (double) (((SDFEdge) vertex.getAssociatedEdge(vertex.getInterfaces().get(0))).getProd().getValue());
        }
        // Continue the normalization in the lower levels
        normalizedown((SDFGraph) vertex.getGraphDescription(), z_up);
      }
    }
  }
}

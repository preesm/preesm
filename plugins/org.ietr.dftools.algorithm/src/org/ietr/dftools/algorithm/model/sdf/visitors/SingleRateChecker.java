/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2016 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2016)
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
package org.ietr.dftools.algorithm.model.sdf.visitors;

import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;

/**
 * Checks whether a visited graph is single-rate.
 *
 * Conditions for a graph to be single-rate are:
 * <ul>
 * <li>Each edge has an identical production and consumption rate.</li>
 * <li>Each actor has a unit coefficient in the repetition vector.</li>
 * <li>The number of delay on each edge is 0 or a multiplier of the exchange rate on this edge.</li>
 * </ul>
 *
 * @author kdesnos
 *
 */
public class SingleRateChecker implements IGraphVisitor<SDFGraph, SDFAbstractVertex, SDFEdge> {

  /** The is single rate. */
  private boolean isSingleRate;

  public SingleRateChecker() {
    isSingleRate = true;
  }

  public final boolean isSingleRate() {
    return this.isSingleRate;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractEdge)
   */
  @Override
  public void visit(final SDFEdge sdfEdge) {
    try {
      this.isSingleRate &= sdfEdge.getCons().longValue() == sdfEdge.getProd().longValue();
      this.isSingleRate &= ((sdfEdge.getDelay().longValue() % sdfEdge.getCons().longValue()) == 0);
    } catch (final InvalidExpressionException e) {
      // Supposedly, will not happen, expressions were already parsed when
      // verifying actors number of repetition.
      throw new DFToolsAlgoException("Could not check rate", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public void visit(final SDFGraph sdf) {
    // Visit vertices
    for (final SDFAbstractVertex vertex : sdf.vertexSet()) {
      vertex.accept(this);
    }

    // Visit edges
    for (final SDFEdge edge : sdf.edgeSet()) {
      edge.accept(this);
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractVertex)
   */
  @Override
  public void visit(final SDFAbstractVertex sdfVertex) {
    // Check number of repetitions
    try {
      this.isSingleRate &= (sdfVertex.getNbRepeatAsLong() == 1);
    } catch (final InvalidExpressionException e) {
      throw new SDF4JException(e.getMessage());
    }
  }
}

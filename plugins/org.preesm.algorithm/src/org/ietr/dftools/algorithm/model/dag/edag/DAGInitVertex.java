/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.algorithm.model.dag.edag;

import org.ietr.dftools.algorithm.model.AbstractVertexPropertyType;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;

/**
 * Represent a initialization vertex in the DAG.
 *
 * @author jpiat
 */
public class DAGInitVertex extends DAGVertex {

  /** Key to access to property dag_init_vertex. */
  public static final String DAG_INIT_VERTEX = "dag_init_vertex";

  /** Persistence level of a delay */
  public static final String PERSISTENCE_LEVEL = "persistence_level";

  /** The Constant END_REFERENCE. */
  public static final String END_REFERENCE = "END_REFERENCE";

  /** The Constant INIT_SIZE. */
  public static final String INIT_SIZE = "INIT_SIZE";

  /**
   * Creates a new DAGInitVertex.
   */
  public DAGInitVertex() {
    super();
    setKind(DAGInitVertex.DAG_INIT_VERTEX);
  }

  /**
   * Creates a new DAGForkVertex with the name "n", the execution time "t" and the number of repetition "nb".
   *
   * @param n
   *          This Vertex name
   * @param t
   *          This Vertex execution time
   * @param nb
   *          This Vertex number of repetition
   */
  public DAGInitVertex(final String n, final AbstractVertexPropertyType<?> t, final AbstractVertexPropertyType<?> nb) {
    super(n, t, nb);
    setKind(DAGInitVertex.DAG_INIT_VERTEX);
  }

  /**
   * Sets the end reference.
   *
   * @param ref
   *          the new end reference
   */
  public void setEndReference(final DAGEndVertex ref) {
    getPropertyBean().setValue(DAGInitVertex.END_REFERENCE, ref);
  }

  /**
   * Gets the end reference.
   *
   * @return the end reference
   */
  public DAGEndVertex getEndReference() {
    return (DAGEndVertex) getPropertyBean().getValue(DAGInitVertex.END_REFERENCE);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.dag.DAGVertex#toString()
   */
  @Override
  public String toString() {
    return getName();
  }
}

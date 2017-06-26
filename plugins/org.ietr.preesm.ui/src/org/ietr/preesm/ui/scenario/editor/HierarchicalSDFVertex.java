/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2010 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2010 - 2011)
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
package org.ietr.preesm.ui.scenario.editor;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;

// TODO: Auto-generated Javadoc
/**
 * Class used as a scenario editor tree content to distinguish two vertices with the same name but different paths (stored in "info" property).
 *
 * @author mpelcat
 */
public class HierarchicalSDFVertex implements IHierarchicalVertex {

  /** The stored vertex. */
  private final SDFAbstractVertex storedVertex;

  /**
   * Instantiates a new hierarchical SDF vertex.
   *
   * @param storedVertex
   *          the stored vertex
   */
  public HierarchicalSDFVertex(final SDFAbstractVertex storedVertex) {
    super();
    this.storedVertex = storedVertex;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.ui.scenario.editor.IHierarchicalVertex#getStoredVertex()
   */
  @Override
  public SDFAbstractVertex getStoredVertex() {
    return this.storedVertex;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.ui.scenario.editor.IHierarchicalVertex#getName()
   */
  @Override
  public String getName() {
    return this.storedVertex.getName();
  }

  /**
   * Checking equality between vertices but also between their paths.
   *
   * @param e
   *          the e
   * @return true, if successful
   */
  @Override
  public boolean equals(final Object e) {
    if (e instanceof HierarchicalSDFVertex) {
      final HierarchicalSDFVertex v = ((HierarchicalSDFVertex) e);
      final SDFAbstractVertex vStored = v.getStoredVertex();
      final SDFAbstractVertex thisStored = getStoredVertex();

      boolean equals = vStored.equals(thisStored);

      if (equals) {
        if (!((vStored.getInfo() == null) || (thisStored.getInfo() == null))) {
          if ((!(vStored.getInfo().isEmpty()) || thisStored.getInfo().isEmpty())) {
            equals = vStored.getInfo().equals(thisStored.getInfo());
          }
        }
      }
      return equals;
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.storedVertex.getInfo();
  }
}

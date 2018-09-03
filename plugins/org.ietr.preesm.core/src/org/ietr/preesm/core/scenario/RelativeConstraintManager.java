/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2012)
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
package org.ietr.preesm.core.scenario;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * Manager of the relative constraints.
 *
 * @author mpelcat
 */
public class RelativeConstraintManager {

  /** Integer representing no relative constraint. */
  public static final int NONE = -1;

  /**
   * List of all relative constraints as groups represented by integers and linked to vertices represented by their ID.
   */
  private final Map<String, Integer> relativeConstraints;

  /** Path to a file containing relative constraints. */
  private String excelFileURL = "";

  /**
   * Instantiates a new relative constraint manager.
   */
  public RelativeConstraintManager() {
    this.relativeConstraints = new LinkedHashMap<>();
  }

  /**
   * Set the group of a given vertex represented by its ID.
   *
   * @param sdfVertexId
   *          the sdf vertex id
   * @param groupId
   *          the group id
   */
  public void addConstraint(final String sdfVertexId, final int groupId) {
    if (groupId != RelativeConstraintManager.NONE) {
      this.relativeConstraints.put(sdfVertexId, groupId);
    }
  }

  /**
   * Gets the group if any, associated with the vertex ID, or NONE otherwise.
   *
   * @param sdfVertexId
   *          the sdf vertex id
   * @return the constraint or default
   */
  public int getConstraintOrDefault(final String sdfVertexId) {
    if (this.relativeConstraints.keySet().contains(sdfVertexId)) {
      return this.relativeConstraints.get(sdfVertexId);
    } else {
      return RelativeConstraintManager.NONE;
    }
  }

  /**
   * Checks if the vertex is associated to a group.
   *
   * @param sdfVertexId
   *          the sdf vertex id
   * @return true, if successful
   */
  public boolean hasRelativeConstraint(final String sdfVertexId) {
    return this.relativeConstraints.keySet().contains(sdfVertexId);
  }

  /**
   * Get the IDs of all vertices having a group.
   *
   * @return the explicit constraint ids
   */
  public Set<String> getExplicitConstraintIds() {
    return this.relativeConstraints.keySet();
  }

  /**
   * Gets the excel file URL.
   *
   * @return the excel file URL
   */
  public String getExcelFileURL() {
    return this.excelFileURL;
  }

  /**
   * Sets the excel file URL.
   *
   * @param excelFileURL
   *          the new excel file URL
   */
  public void setExcelFileURL(final String excelFileURL) {
    this.excelFileURL = excelFileURL;
  }
}

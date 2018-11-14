/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
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
package org.preesm.model.pisdf.util;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.dialogs.IInputValidator;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * This validator is used to check whether a vertex in a graph already has a given name.
 *
 * @author kdesnos
 *
 */
public class VertexNameValidator implements IInputValidator {

  /** The graph. */
  protected PiGraph graph;

  /** The existing names. */
  protected Collection<String> existingNames;

  /**
   * Constructor of the {@link VertexNameValidator}.
   *
   * @param graph
   *          the graph to which we want to add/rename a vertex
   * @param renamedVertex
   *          the renamed vertex
   */
  public VertexNameValidator(final PiGraph graph, final AbstractVertex renamedVertex) {
    this.graph = graph;
    // Retrieve a list of all the actor and parameter names in the graph
    this.existingNames = new ArrayList<>(graph.getActorsNames());
    this.existingNames.addAll(graph.getParametersNames());

    if (renamedVertex != null) {
      this.existingNames.remove(renamedVertex.getName());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
   */
  @Override
  public String isValid(final String newVertexName) {
    String message = null;
    // Check if the name is not empty
    if (newVertexName.length() < 1) {
      message = "/!\\ Name cannot be empty /!\\";
      return message;
    }

    // Check if the name contains a space
    if (newVertexName.contains(" ")) {
      message = "/!\\ Name must not contain spaces /!\\";
      return message;
    }

    // Check if the name already exists
    if (this.existingNames.contains(newVertexName)) {
      message = "/!\\ An actor or a parameter with name " + newVertexName + " already exists /!\\";
      return message;
    }
    return message;
  }

}

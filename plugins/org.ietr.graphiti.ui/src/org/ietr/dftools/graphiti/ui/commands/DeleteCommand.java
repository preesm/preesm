/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
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
package org.ietr.dftools.graphiti.ui.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.Vertex;

/**
 * This class provides a command that deletes a vertex. NOTE: this command can delete a vertex OR an edge. Also, if an
 * edge has already been deleted and a DeleteCommand is issued on it, execute() won't do anything because the edge does
 * not have a parent anymore at this point.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 *
 */
public class DeleteCommand extends Command {

  /** The edge. */
  private Edge edge;

  /** The parent. */
  private Graph parent;

  /** The vertex. */
  private Vertex vertex;

  /**
   * Creates a new delete command with the selected object.
   *
   * @param obj
   *          An object to delete.
   */
  public DeleteCommand(final Object obj) {
    if (obj instanceof Vertex) {
      this.vertex = (Vertex) obj;
      this.parent = this.vertex.getParent();
    } else if (obj instanceof Edge) {
      this.edge = (Edge) obj;
      this.parent = this.edge.getParent();
    }
  }

  /**
   * Adds a vertex to the parent graph and sets its size.
   *
   * @param vertex
   *          a vertex
   */
  private void addVertex(final Vertex vertex) {
    final Rectangle bounds = (Rectangle) vertex.getValue(Vertex.PROPERTY_SIZE);
    this.parent.addVertex(vertex);
    vertex.setValue(Vertex.PROPERTY_SIZE, bounds);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#canExecute()
   */
  @Override
  public boolean canExecute() {
    return ((this.parent != null) && ((this.vertex != null) || (this.edge != null)));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#execute()
   */
  @Override
  public void execute() {
    if (this.parent != null) {
      if (this.vertex != null) {
        this.parent.removeVertex(this.vertex);
      } else if (this.edge != null) {
        this.parent.removeEdge(this.edge);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#getLabel()
   */
  @Override
  public String getLabel() {
    return "Delete";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#undo()
   */
  @Override
  public void undo() {
    if (this.parent != null) {
      if (this.vertex != null) {
        addVertex(this.vertex);
      } else if (this.edge != null) {
        addVertex(this.edge.getSource());
        addVertex(this.edge.getTarget());
        this.parent.addEdge(this.edge);
      }
    }
  }

}

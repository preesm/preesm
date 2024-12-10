/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Jonathan Piat [jpiat@laas.fr] (2008)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2010)
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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Vertex;

/**
 * This class allows the creation of vertices.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 *
 */
public class VertexCreateCommand extends Command {

  /** The bounds. */
  private Rectangle bounds;

  /** The graph. */
  private Graph graph;

  /** The vertex. */
  private Vertex vertex;

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#execute()
   */
  @Override
  public void execute() {
    if ((this.graph != null) && (this.vertex != null)) {
      String id;
      if (this.vertex.getType().getName().equals("Scenario source")) {
        id = "scenario";

      } else {
        id = getVertexId();
      }
      if (id != null) {
        this.vertex.setValue(ObjectType.PARAMETER_ID, id);
        this.graph.addVertex(this.vertex);

        // retrieve the vertex bounds (they have been set by the edit
        // part)
        // and set the location
        final Rectangle vertexBounds = (Rectangle) this.vertex.getValue(Vertex.PROPERTY_SIZE);
        final Rectangle newBounds = vertexBounds.getCopy();
        newBounds.x = this.bounds.x;
        newBounds.y = this.bounds.y;
        this.vertex.setValue(Vertex.PROPERTY_SIZE, newBounds);
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
    if (this.vertex != null) {
      final String type = this.vertex.getType().getName();
      return "Create " + type;
    } else {
      return "Create vertex";
    }
  }

  /**
   * Returns a vertex identifier.
   *
   * @return A unique vertex identifier, or <code>null</code>.
   */
  private String getVertexId() {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final Shell shell = window.getShell();

    final InputDialog dialog = new InputDialog(shell, "New vertex", "Please enter a vertex identifier", "",
        vertexId -> {
          if (vertexId.isEmpty()) {
            return "";
          }

          if (VertexCreateCommand.this.graph != null) {
            final Vertex foundVertex = VertexCreateCommand.this.graph.findVertex(vertexId);
            if (foundVertex != null) {
              return "A vertex already exists with the same identifier";
            }
          }

          return null;
        });
    dialog.open();

    final String value = dialog.getValue();
    if ((value == null) || value.isEmpty()) {
      return null;
    } else {
      return value;
    }
  }

  /**
   * Sets the initial bounds of this vertex.
   *
   * @param bounds
   *          the new bounds
   */
  public void setBounds(final Rectangle bounds) {
    this.bounds = bounds;
  }

  /**
   * Sets this command model.
   *
   * @param model
   *          The model to use.
   */
  public void setModel(final Object model) {
    if (model instanceof Graph) {
      this.graph = (Graph) model;
    }
  }

  /**
   * Sets the new object that should be added to the model.
   *
   * @param newObject
   *          the newly created object.
   */
  public void setNewObject(final Object newObject) {
    if (newObject instanceof Vertex) {
      this.vertex = (Vertex) newObject;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#undo()
   */
  @Override
  public void undo() {
    if ((this.graph != null) && (this.vertex != null)) {
      this.graph.removeVertex(this.vertex);
    }
  }
}

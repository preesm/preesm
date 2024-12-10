/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
package org.ietr.dftools.graphiti.ui.commands.copypaste;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.graphiti.ui.editparts.GraphEditPart;

/**
 * This class provides a command that pastes vertices from the clipboard.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 *
 */
public class PasteCommand extends Command {

  /** The added. */
  private List<Object> added;

  /** The dirty. */
  private boolean dirty;

  /**
   * The target EditPart.
   */
  private final Graph graph;

  /** The vertices. */
  private final List<Vertex> vertices;

  /**
   * Creates a new paste command with the given target part and vertices.
   *
   * @param target
   *          The target part.
   * @param vertices
   *          A list of vertices to cut.
   */
  public PasteCommand(final GraphEditPart target, final List<Vertex> vertices) {
    this.graph = (Graph) target.getModel();
    this.vertices = vertices;
  }

  /**
   * Check vertex id.
   *
   * @param graph
   *          the graph
   * @param vertex
   *          the vertex
   * @return the string
   */
  private String checkVertexId(final Graph graph, final Vertex vertex) {
    String id = (String) vertex.getValue(ObjectType.PARAMETER_ID);
    final Vertex existing = graph.findVertex(id);
    if (existing != vertex) {
      id = getVertexId(id);
      if (id != null) {
        vertex.setValue(ObjectType.PARAMETER_ID, id);
      }
    }

    return id;
  }

  /**
   * Returns a vertex identifier.
   *
   * @param initialValue
   *          The initial id.
   * @return A unique vertex identifier, or <code>null</code>.
   */
  private String getVertexId(final String initialValue) {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final Shell shell = window.getShell();

    final InputDialog dialog = new InputDialog(shell, "New vertex", "Please enter a vertex identifier", initialValue,
        vertexId -> {
          if (vertexId.isEmpty()) {
            return "";
          }

          if (PasteCommand.this.graph != null) {
            final Vertex vertex = PasteCommand.this.graph.findVertex(vertexId);
            if (vertex != null) {
              return "A vertex already exists with the same identifier";
            }
          }

          return null;
        });
    final int res = dialog.open();
    if (res == Window.OK) {
      final String value = dialog.getValue();
      if ((value == null) || value.isEmpty()) {
        return null;
      } else {
        return value;
      }
    } else {
      return null;
    }
  }

  /**
   * Checks if is dirty.
   *
   * @return true, if is dirty
   */
  public boolean isDirty() {
    return this.dirty;
  }

  /**
   * Run.
   */
  public void run() {
    this.added = new ArrayList<>();

    for (final Vertex vertex : this.vertices) {
      // check id
      if (checkVertexId(this.graph, vertex) != null) {
        // Adds the cloned graph to the parent graph and the added list
        this.added.add(vertex);
        final Rectangle previousBounds = (Rectangle) vertex.getValue(Vertex.PROPERTY_SIZE);
        this.graph.addVertex(vertex);
        final Rectangle bounds = (Rectangle) vertex.getValue(Vertex.PROPERTY_SIZE);

        final Rectangle newBounds = new Rectangle(previousBounds.x + previousBounds.width + 10,
            previousBounds.y + previousBounds.height + 10, bounds.width, bounds.height);
        vertex.firePropertyChange(Vertex.PROPERTY_SIZE, null, newBounds);

        this.dirty = true;
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#undo()
   */
  @Override
  public void undo() {
    for (final Object model : this.added) {
      if (model instanceof Vertex) {
        this.graph.removeVertex((Vertex) model);
      }
    }
  }
}

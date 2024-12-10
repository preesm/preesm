/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
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
import org.ietr.dftools.graphiti.model.Vertex;

/**
 * This class executes a command that moves a vertex.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 */
public class VertexMoveCommand extends Command {

  /** The new bounds. */
  private final Rectangle newBounds;

  /** The old bounds. */
  private final Rectangle oldBounds;

  /** The vertex. */
  private final Vertex vertex;

  /**
   * Instantiates a new vertex move command.
   *
   * @param vertex
   *          the vertex
   * @param newBounds
   *          the new bounds
   */
  public VertexMoveCommand(final Vertex vertex, final Rectangle newBounds) {
    this.newBounds = newBounds;
    this.vertex = vertex;
    final Rectangle bounds = (Rectangle) vertex.getValue(Vertex.PROPERTY_SIZE);
    this.oldBounds = bounds.getCopy();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#execute()
   */
  @Override
  public void execute() {
    this.vertex.setValue(Vertex.PROPERTY_SIZE, this.newBounds);
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
      return "Move " + type;
    } else {
      return "Move vertex";
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#undo()
   */
  @Override
  public void undo() {
    this.vertex.setValue(Vertex.PROPERTY_SIZE, this.oldBounds);
  }
}

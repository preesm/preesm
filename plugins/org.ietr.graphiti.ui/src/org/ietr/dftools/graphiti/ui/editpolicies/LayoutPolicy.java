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
package org.ietr.dftools.graphiti.ui.editpolicies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.jface.viewers.StructuredSelection;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.graphiti.ui.commands.VertexCreateCommand;
import org.ietr.dftools.graphiti.ui.commands.VertexMoveCommand;
import org.ietr.dftools.graphiti.ui.commands.refinement.OpenRefinementCommand;
import org.ietr.dftools.graphiti.ui.editparts.VertexEditPart;

/**
 * This class provides the policy of the layout used in the editor view. Namely it implements the
 * <code>createChangeConstraintCommand</code> and <code>getCreateCommand</code> methods to move and create a graph
 * respectively.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 */
public class LayoutPolicy extends XYLayoutEditPolicy {

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart,
   * java.lang.Object)
   */
  @Override
  protected Command createChangeConstraintCommand(final ChangeBoundsRequest request, final EditPart child,
      final Object constraint) {
    VertexMoveCommand command = null;

    if (child instanceof VertexEditPart) {
      final VertexEditPart editPart = (VertexEditPart) child;
      final Vertex vertex = (Vertex) editPart.getModel();

      command = new VertexMoveCommand(vertex, (Rectangle) constraint);
    }

    return command;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChildEditPolicy(org.eclipse.gef.EditPart)
   */
  @Override
  protected EditPolicy createChildEditPolicy(final EditPart child) {
    return new NonResizableEditPolicy();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#getCommand(org.eclipse.gef.Request)
   */
  @Override
  public Command getCommand(final Request request) {
    if (RequestConstants.REQ_OPEN.equals(request.getType())) {
      final OpenRefinementCommand command = new OpenRefinementCommand();
      command.setSelection(new StructuredSelection(getHost()));
      return command;
    } else {
      return super.getCommand(request);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
   */
  @Override
  protected Command getCreateCommand(final CreateRequest request) {
    final Object newObject = request.getNewObject();
    final VertexCreateCommand command = new VertexCreateCommand();

    command.setNewObject(newObject);
    command.setModel(getHost().getModel());
    command.setBounds((Rectangle) getConstraintFor(request));

    return command;
  }
}

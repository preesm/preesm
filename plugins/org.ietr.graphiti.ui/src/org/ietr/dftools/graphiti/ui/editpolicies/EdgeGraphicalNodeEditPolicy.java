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
package org.ietr.dftools.graphiti.ui.editpolicies;

import org.eclipse.draw2d.Connection;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.graphiti.ui.commands.EdgeCreateCommand;
import org.ietr.dftools.graphiti.ui.commands.EdgeReconnectCommand;
import org.ietr.dftools.graphiti.ui.editparts.VertexEditPart;
import org.ietr.dftools.graphiti.ui.figure.EdgeFigure;

/**
 * This class provides methods that deal with creations and connections of dependencies. When the user clicks a source
 * port, the method <code>getConnectionCreateCommand</code> is first called. When they click the destination port,
 * <code>getConnectionCompleteCommand</code> is called.
 *
 * <code>getReconnectSourceCommand</code> and <code>getReconnectTargetCommand</code> are called when the user reconnects
 * one end of a dependency (they have previously disconnected).
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 */
public class EdgeGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#createDummyConnection(org.eclipse.gef.Request)
   */
  @Override
  protected Connection createDummyConnection(final Request req) {
    final Object obj = ((CreateConnectionRequest) req).getNewObject();
    return new EdgeFigure((Edge) obj);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.
   * CreateConnectionRequest)
   */
  @Override
  protected Command getConnectionCompleteCommand(final CreateConnectionRequest request) {
    final EdgeCreateCommand command = (EdgeCreateCommand) request.getStartCommand();
    final VertexEditPart vertexEditPart = (VertexEditPart) request.getTargetEditPart();
    command.setTarget((Vertex) (vertexEditPart.getModel()));
    return command;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.
   * CreateConnectionRequest)
   */
  @Override
  protected Command getConnectionCreateCommand(final CreateConnectionRequest request) {
    final EdgeCreateCommand command = new EdgeCreateCommand((Edge) request.getNewObject());
    final VertexEditPart vertexEditPart = (VertexEditPart) request.getTargetEditPart();
    command.setSource((Vertex) (vertexEditPart.getModel()));
    request.setStartCommand(command);
    return command;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.
   * ReconnectRequest)
   */
  @Override
  protected Command getReconnectSourceCommand(final ReconnectRequest request) {
    final EdgeReconnectCommand command = new EdgeReconnectCommand();
    command.setOriginalEdge((Edge) request.getConnectionEditPart().getModel());
    final VertexEditPart vertexEditPart = (VertexEditPart) getHost();
    command.setSource((Vertex) vertexEditPart.getModel());
    return command;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.
   * ReconnectRequest)
   */
  @Override
  protected Command getReconnectTargetCommand(final ReconnectRequest request) {
    final EdgeReconnectCommand command = new EdgeReconnectCommand();
    command.setOriginalEdge((Edge) request.getConnectionEditPart().getModel());
    final VertexEditPart vertexEditPart = (VertexEditPart) getHost();
    command.setTarget((Vertex) vertexEditPart.getModel());
    return command;
  }
}

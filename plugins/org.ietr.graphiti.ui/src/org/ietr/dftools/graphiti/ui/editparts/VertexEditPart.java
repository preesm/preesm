/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
package org.ietr.dftools.graphiti.ui.editparts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.CompoundDirectedGraphLayout;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.draw2d.graph.Subgraph;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.graphiti.ui.editpolicies.DeleteComponentEditPolicy;
import org.ietr.dftools.graphiti.ui.editpolicies.EdgeGraphicalNodeEditPolicy;
import org.ietr.dftools.graphiti.ui.editpolicies.LayoutPolicy;
import org.ietr.dftools.graphiti.ui.editpolicies.VertexDirectEditPolicy;
import org.ietr.dftools.graphiti.ui.figure.VertexFigure;
import org.ietr.dftools.graphiti.ui.figure.shapes.IShape;
import org.ietr.dftools.graphiti.ui.figure.shapes.ShapeFactory;

/**
 * The EditPart associated to the Graph gives methods to refresh the view when a property has changed.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 *
 */
public class VertexEditPart extends AbstractGraphitiEditPart implements NodeEditPart {

  /** The direct edit manager. */
  private DirectEditManager directEditManager;

  /**
   * This attribute is set by {@link GraphEditPart#addNodes}.
   */
  private Node node;

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
   */
  @Override
  public void activate() {
    super.activate();
    ((Vertex) getModel()).addPropertyChangeListener(this);
  }

  /**
   * Adds edges of this EditPart to the {@link EdgeList} of the parent.
   *
   * @param edges
   *          the edges
   */
  @SuppressWarnings("unchecked")
  void addEdges(final EdgeList edges) {
    final List<EdgeEditPart> connections = new ArrayList<>();
    final List<EdgeEditPart> sourceConnections2 = getSourceConnections();
    connections.addAll(sourceConnections2);

    for (final EdgeEditPart dependency : connections) {
      final VertexEditPart source = (VertexEditPart) dependency.getSource();
      final VertexEditPart target = (VertexEditPart) dependency.getTarget();

      if (((source != null) && (target != null)) && (source != target)) {
        final org.eclipse.draw2d.graph.Edge edge = new org.eclipse.draw2d.graph.Edge(source.node, target.node);
        edges.add(edge);
      }
    }
  }

  /**
   * Adds this node to its parent. In the future will also add its children if it has any.
   *
   * @param nodes
   *          The list of {@link Node} in the Draw2D Graph.
   * @param parent
   *          Its parent subgraph.
   */
  @SuppressWarnings("unchecked")
  void addNodes(final NodeList nodes, final Subgraph parent) {
    this.node = new Node(this, parent);
    nodes.add(this.node);

    // Graphical stuff
    final Figure figure = (Figure) getFigure();
    this.node.setSize(figure.getPreferredSize());
    this.node.setPadding(new Insets(35, 35, 35, 35));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
   */
  @Override
  protected void createEditPolicies() {
    installEditPolicy(EditPolicy.COMPONENT_ROLE, new DeleteComponentEditPolicy());
    installEditPolicy(EditPolicy.LAYOUT_ROLE, new LayoutPolicy());
    installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new EdgeGraphicalNodeEditPolicy());
    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new VertexDirectEditPolicy());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
   */
  @Override
  protected IFigure createFigure() {
    final Vertex vertex = (Vertex) getModel();

    // Get default width and height
    final int width = (Integer) vertex.getAttribute(ObjectType.ATTRIBUTE_WIDTH);
    final int height = (Integer) vertex.getAttribute(ObjectType.ATTRIBUTE_HEIGHT);

    // Get dimension, color, shape
    final Dimension dimension = new Dimension(width, height);
    final Color color = (Color) vertex.getAttribute(ObjectType.ATTRIBUTE_COLOR);
    final String name = (String) vertex.getAttribute(ObjectType.ATTRIBUTE_SHAPE);
    final IShape shape = ShapeFactory.createShape(name);

    // Creates the figure with the specified properties, sets its id
    final Font font = ((GraphicalEditPart) getParent()).getFigure().getFont();
    final VertexFigure figure = new VertexFigure(font, dimension, color, shape);
    final String id = (String) vertex.getValue(ObjectType.PARAMETER_ID);
    figure.setId(id);

    // update the figure position (if the graph has layout information)
    if (Boolean.TRUE.equals(vertex.getParent().getValue(Graph.PROPERTY_HAS_LAYOUT))) {
      final Rectangle bounds = (Rectangle) vertex.getValue(Vertex.PROPERTY_SIZE);
      if (bounds != null) {
        figure.setBounds(bounds);
      }
    }

    // update its size
    updatePorts(figure);

    return figure;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
   */
  @Override
  protected List<Edge> getModelSourceConnections() {
    if (getModel() instanceof Vertex) {
      final Vertex vertex = (Vertex) getModel();
      final Graph parent = vertex.getParent();

      // we get the *output* dependencies of vertex
      final Set<Edge> edges = parent.outgoingEdgesOf(vertex);

      // return the dependencies
      return new ArrayList<>(edges);
    }

    return Collections.emptyList();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
   */
  @Override
  protected List<Edge> getModelTargetConnections() {
    if (getModel() instanceof Vertex) {
      final Vertex vertex = (Vertex) getModel();
      final Graph parent = vertex.getParent();

      // we get the *input* dependencies of vertex
      final Set<Edge> edges = parent.incomingEdgesOf(vertex);

      // dependencies
      return new ArrayList<>(edges);
    }

    return Collections.emptyList();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
   */
  @Override
  public ConnectionAnchor getSourceConnectionAnchor(final ConnectionEditPart connection) {
    final VertexFigure figure = (VertexFigure) getFigure();
    final Edge edge = (Edge) connection.getModel();
    final Connection conn = (Connection) connection.getFigure();
    return figure.getSourceAnchor(edge, conn);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
   */
  @Override
  public ConnectionAnchor getSourceConnectionAnchor(final Request request) {
    final VertexFigure figure = (VertexFigure) getFigure();
    return figure.getSourceAnchor();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
   */
  @Override
  public ConnectionAnchor getTargetConnectionAnchor(final ConnectionEditPart connection) {
    final VertexFigure figure = (VertexFigure) getFigure();
    final Edge edge = (Edge) connection.getModel();
    return figure.getTargetAnchor(edge);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
   */
  @Override
  public ConnectionAnchor getTargetConnectionAnchor(final Request request) {
    final VertexFigure figure = (VertexFigure) getFigure();
    return figure.getTargetAnchor();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractEditPart#performRequest(org.eclipse.gef.Request)
   */
  @Override
  public void performRequest(final Request request) {
    if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
      if (this.directEditManager == null) {
        final VertexFigure figure = (VertexFigure) getFigure();
        this.directEditManager = new VertexDirectEditManager(this, figure);
      }
      this.directEditManager.show();
    } else if (request.getType() == RequestConstants.REQ_OPEN) {
      final Command command = getCommand(request);
      if ((command != null) && command.canExecute()) {
        command.execute();
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(final PropertyChangeEvent evt) {
    final String propertyName = evt.getPropertyName();
    if (propertyName.equals(Vertex.PROPERTY_SIZE)) {
      final VertexFigure vertexFigure = (VertexFigure) getFigure();
      vertexFigure.setBounds((Rectangle) evt.getNewValue());
      refresh();
    } else if (propertyName.equals(Vertex.PROPERTY_SRC_VERTEX) || propertyName.equals(Vertex.PROPERTY_DST_VERTEX)) {
      updatePorts(getFigure());
      // refresh will cause source anchor to be rightly put
      refresh();
    } else {
      // other parameters
      if (propertyName.equals(ObjectType.PARAMETER_ID)) {
        refreshVisuals();
      }

      // updates the figure size
      final Vertex vertex = (Vertex) getModel();
      ((VertexFigure) getFigure()).adjustSize();
      final Rectangle bounds = getFigure().getBounds().getCopy();
      vertex.setValue(Vertex.PROPERTY_SIZE, bounds);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
   */
  @Override
  protected void refreshVisuals() {
    final Vertex vertex = (Vertex) getModel();

    final VertexFigure figure = (VertexFigure) getFigure();
    figure.setId((String) vertex.getValue(ObjectType.PARAMETER_ID));
  }

  /**
   * This method is called by the {@link GraphLayoutManager}, and applies back the changes of the
   * {@link CompoundDirectedGraphLayout} algorithm to the different figures, by setting their bounds.
   */
  void updateFigures() {
    final Vertex vertex = (Vertex) getModel();
    Rectangle bounds = (Rectangle) vertex.getValue(Vertex.PROPERTY_SIZE);
    if (bounds == null) {
      bounds = getFigure().getBounds();
    }

    final Rectangle newBounds = bounds.getCopy();
    newBounds.x = this.node.x;
    newBounds.y = this.node.y;
    vertex.setValue(Vertex.PROPERTY_SIZE, newBounds);
  }

  /**
   * Update the ports of the given figure. The figure has to be passed to the function because updatePorts can be called
   * by createFigure, and getFigure() at this time returns null.
   *
   * @param fig
   *          the fig
   */
  private void updatePorts(final IFigure fig) {
    final Vertex vertex = (Vertex) getModel();
    final VertexFigure figure = (VertexFigure) fig;
    final Graph parent = vertex.getParent();

    figure.resetPorts();

    for (final Edge edge : parent.incomingEdgesOf(vertex)) {
      final String port = (String) edge.getValue(ObjectType.PARAMETER_TARGET_PORT);
      figure.addInputPort(port);
    }

    for (final Edge edge : parent.outgoingEdgesOf(vertex)) {
      final String port = (String) edge.getValue(ObjectType.PARAMETER_SOURCE_PORT);
      figure.addOutputPort(port);
    }

    figure.adjustSize();
    vertex.setValue(Vertex.PROPERTY_SIZE, figure.getBounds().getCopy());
  }
}

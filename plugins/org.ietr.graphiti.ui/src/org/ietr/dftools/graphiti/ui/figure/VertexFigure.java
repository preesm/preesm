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
package org.ietr.dftools.graphiti.ui.figure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.ui.figure.shapes.IShape;

/**
 * This class provides a figure for vertices.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 */
public class VertexFigure extends Figure {

  /**
   * This class is here because it is necessary to circumvent a missing feature in GEF.
   * <p>
   * Most objects in Draw2D are compared by reference. Granted, this is a lot faster, but in some cases unwanted. For
   * some reason {@link VertexFigure#getSourceAnchor(Edge, Connection)} is called several times per connection. We want
   * to set bendpoints on a connection only once, because otherwise Draw2D messes up if the same bendpoints occur
   * several times.
   * </p>
   *
   * <p>
   * So basically this class holds all the information we need (we want bendpoint after the start or before the end and
   * not both) and it has an {@link Object#equals(Object)} method which allows us to check whether a connection
   * constraint list contains this. We have to store the association connection -> list of concrete bendpoints in a map,
   * namely the {@link VertexFigure#bendpoints} field.
   * </p>
   *
   * @author Matthieu Wipliez
   */
  private class ConcreteBendpoint {

    /** The end. */
    boolean end;

    /** The offset. */
    int offset;

    /**
     * Instantiates a new concrete bendpoint.
     *
     * @param end
     *          the end
     * @param offset
     *          the offset
     */
    public ConcreteBendpoint(final boolean end, final int offset) {
      this.end = end;
      this.offset = offset;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof ConcreteBendpoint) {
        final ConcreteBendpoint cbp = (ConcreteBendpoint) obj;
        return (this.end == cbp.end) && (this.offset == cbp.offset);
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return (Integer.hashCode(this.offset) * 31) + Boolean.hashCode(this.end);
    }

    /**
     * Returns a {@link RelativeBendpoint} with the same characteristics as this bendpoint.
     *
     * @param conn
     *          The {@link Connection} to attach the relative bendpoint to.
     * @return A {@link RelativeBendpoint}
     */
    public RelativeBendpoint getBendpoint(final Connection conn) {
      final RelativeBendpoint bp = new RelativeBendpoint(conn);
      if (this.end) {
        bp.setWeight(1.0f);
        bp.setRelativeDimensions(new Dimension(0, 0), new Dimension(this.offset, 0));
      } else {
        bp.setWeight(0.0f);
        bp.setRelativeDimensions(new Dimension(this.offset, 0), new Dimension(0, 0));
      }

      return bp;
    }
  }

  /**
   * The reason for this field is listed in the documentation for the inner class {@link ConcreteBendpoint}.
   *
   * @see ConcreteBendpoint
   */
  private final Map<Connection, List<ConcreteBendpoint>> bendpoints;

  /** The input ports. */
  private final Map<String, Label> inputPorts;

  /** The label id. */
  private Label labelId;

  /** The label tool tip. */
  private Label labelToolTip;

  /** The output ports. */
  private final Map<String, Label> outputPorts;

  /** The shape. */
  private IShape shape;

  /**
   * Creates a new VertexFigure using the given vertex model.
   *
   * @param font
   *          the font
   * @param dimension
   *          the dimension
   * @param color
   *          the color
   * @param shape
   *          the shape
   */
  public VertexFigure(final Font font, final Dimension dimension, final Color color, final IShape shape) {
    this.bendpoints = new LinkedHashMap<>();
    this.inputPorts = new LinkedHashMap<>();
    this.outputPorts = new LinkedHashMap<>();

    // necessary for adjustSize
    setFont(font);

    setLayoutManager(new XYLayout());
    initLabels();
    initShape(shape, color, dimension);
  }

  /**
   * Adds an input port with the given name.
   *
   * @param portName
   *          The port name.
   */
  public void addInputPort(final String portName) {
    if ((portName != null) && !portName.isEmpty()) {
      this.inputPorts.put(portName, null);
    }
  }

  /**
   * Adds an output port with the given name.
   *
   * @param portName
   *          The port name.
   */
  public void addOutputPort(final String portName) {
    if ((portName != null) && !portName.isEmpty()) {
      this.outputPorts.put(portName, null);
    }
  }

  /**
   * Adds a bendpoint to the bendpoint list that is the routing constraint of the <code>conn</code> connection. For
   * additional information, see {@link ConcreteBendpoint}.
   *
   * @param conn
   *          The connection.
   * @param end
   *          True for the end anchor, false for the start anchor.
   * @param offset
   *          The offset.
   */
  public void addToList(final Connection conn, final boolean end, final int offset) {
    // get the concrete list
    if (!this.bendpoints.containsKey(conn)) {
      this.bendpoints.put(conn, new ArrayList<>());
    }
    final List<ConcreteBendpoint> list = this.bendpoints.get(conn);

    final ConcreteBendpoint cbp = new ConcreteBendpoint(end, offset);

    // check we do not already have this bendpoint
    if (!list.contains(cbp)) {
      // ok we're fine, let's get the routing list
      @SuppressWarnings("unchecked")
      List<RelativeBendpoint> cstList = (List<RelativeBendpoint>) conn.getRoutingConstraint();
      if (cstList == null) {
        // oops! it's not here, we create it and set it
        cstList = new ArrayList<>();
        conn.setRoutingConstraint(cstList);
      }

      // now the magic part: we add it to our "concrete" list
      // and to the constraint list as a relative bendpoint
      list.add(cbp);
      cstList.add(cbp.getBendpoint(conn));
    }
  }

  /**
   * Adjusts the size of this figure according to its id and ports.
   */
  public void adjustSize() {
    @SuppressWarnings("unchecked")
    final List<IFigure> children = new ArrayList<>(this.shape.getChildren());
    children.remove(this.labelId);
    for (final IFigure child : children) {
      this.shape.remove(child);
    }

    // update the figure ports
    updatePorts();

    // set the shape dimension (if polyline), and our size.
    if (this.shape instanceof Polyline) {
      this.shape.setDimension(this.shape.getPreferredSize());
    }
    setSize(this.shape.getPreferredSize());
  }

  /**
   * Returns the label for the input port whose name is given.
   *
   * @param portName
   *          The input port name.
   * @return Its label.
   */
  public Label getInputPortLabel(final String portName) {
    return this.inputPorts.get(portName);
  }

  /**
   * Returns the id label object.
   *
   * @return A {@link Label}.
   */
  public Label getLabelId() {
    return this.labelId;
  }

  /**
   * Returns the label for the output port whose name is given.
   *
   * @param portName
   *          The output port name.
   * @return Its label.
   */
  public Label getOutputPortLabel(final String portName) {
    return this.outputPorts.get(portName);
  }

  /**
   * Returns the size of this VertexFigure.
   *
   * @param w
   *          the w
   * @param h
   *          the h
   * @return The size of this VertexFigure.
   */
  @Override
  public Dimension getPreferredSize(final int w, final int h) {
    return getBounds().getSize();
  }

  /**
   * Returns the shape of this figure.
   *
   * @return The shape of this figure.
   */
  public IShape getShape() {
    return this.shape;
  }

  /**
   * Returns the connection source anchor, i.e. where connections start.
   *
   * @return The {@link ConnectionAnchor} of the underlying shape.
   */
  public ConnectionAnchor getSourceAnchor() {
    return this.shape.getConnectionAnchor(this, null, true);
  }

  /**
   * Returns the connection source anchor, i.e. where connections start.
   *
   * @param edge
   *          The edge model of the connection. Allows the figure to retrieve the source port.
   * @param conn
   *          The connection figure.
   * @return The {@link ConnectionAnchor} of the underlying shape.
   */
  public ConnectionAnchor getSourceAnchor(final Edge edge, final Connection conn) {
    final String portName = (String) edge.getValue(ObjectType.PARAMETER_SOURCE_PORT);
    final ConnectionAnchor anchor = this.shape.getConnectionAnchor(this, portName, true);

    // again another serious flaw in Draw2D: if we decide to add one
    // bendpoint near the start, and the other one in getTargetAnchor, half
    // of the time Draw2D will mess up.
    // Note that NONE of this is written in the documentation. Hell, the
    // documentation does not even *state* that the constraint of a
    // connection should be a List of Bendpoints...
    addToList(conn, false, 20);
    addToList(conn, true, -20);

    return anchor;
  }

  /**
   * Returns the connection target anchor, i.e. where connections end.
   *
   * @return The {@link ConnectionAnchor} of the underlying shape.
   */
  public ConnectionAnchor getTargetAnchor() {
    return this.shape.getConnectionAnchor(this, null, false);
  }

  /**
   * Returns the connection target anchor, i.e. where connections end.
   *
   * @param edge
   *          The edge model of the connection. Allows the figure to retrieve the target port.
   * @return The {@link ConnectionAnchor} of the underlying shape.
   */
  public ConnectionAnchor getTargetAnchor(final Edge edge) {
    final String portName = (String) edge.getValue(ObjectType.PARAMETER_TARGET_PORT);
    return this.shape.getConnectionAnchor(this, portName, false);
  }

  /**
   * Initializes the labels.
   */
  private void initLabels() {
    // Label id
    this.labelId = new Label();
    this.labelId.setForegroundColor(ColorConstants.black);

    // Sets the tool tip and adds it so that it is visible
    this.labelToolTip = new Label();
    this.labelToolTip.setForegroundColor(ColorConstants.black);
    setToolTip(this.labelToolTip);
  }

  /**
   * Initializes the shape and adds it to this figure.
   *
   * @param shape
   *          An {@link IShape}.
   * @param color
   *          Its {@link Color}.
   * @param dimension
   *          Its {@link Dimension}.
   */
  private void initShape(final IShape shape, final Color color, final Dimension dimension) {
    // Sets shape properties and size
    this.shape = shape;
    shape.setBackgroundColor(color);
    shape.setDimension(new Dimension(dimension.width, dimension.height));

    // Adds shape to this, and label to shape.
    add(shape, new Rectangle(0, 0, -1, -1));
    final GridData data = new GridData(SWT.CENTER, SWT.CENTER, true, true);
    data.horizontalSpan = 2;
    shape.add(this.labelId, data);
  }

  /**
   * Resets the ports sets.
   */
  public void resetPorts() {
    this.inputPorts.clear();
    this.outputPorts.clear();
  }

  /**
   * The name to set.
   *
   * @param text
   *          the new id
   */
  public void setId(final String text) {
    this.labelId.setText(text);
    this.labelToolTip.setText(text);
  }

  /**
   * Adds a label for the given entry. The entry key is a port name used in the label, and the entry value is updated to
   * the newly-created label.
   *
   * @param entry
   *          An entry of {@link #inputPorts} or {@link #outputPorts}.
   * @param horizontalAlignment
   *          The horizontal alignment: {@link SWT#BEGINNING}, {@link SWT#CENTER} or {@link SWT#END}.
   * @param horizontalSpan
   *          The horizontal span, <code>1</code> or <code>2</code>.
   */
  private void updatePortLabel(final Entry<String, Label> entry, final int horizontalAlignment,
      final int horizontalSpan) {
    final Label label = new Label(entry.getKey());
    entry.setValue(label);
    final GridData data = new GridData(horizontalAlignment, SWT.CENTER, false, false);
    data.horizontalSpan = horizontalSpan;
    this.shape.add(label, data);
  }

  /**
   * Adds label for all ports of this figure in the grid layout.
   */
  private void updatePorts() {
    // prepare port lists
    final List<Entry<String, Label>> inputPortList = new ArrayList<>(this.inputPorts.entrySet());
    final List<Entry<String, Label>> outputPortList = new ArrayList<>(this.outputPorts.entrySet());
    final List<Entry<String, Label>> portList = new ArrayList<>();

    // ports having the same name are taken from input/outputPortList and
    // put in portList
    for (final Entry<String, Label> inputPort : this.inputPorts.entrySet()) {
      if (this.outputPorts.containsKey(inputPort.getKey())) {
        portList.add(inputPort);
        outputPortList.remove(inputPort);
        inputPortList.remove(inputPort);
      }
    }

    // now we can go on with the process
    updatePortsFromLists(inputPortList, outputPortList, portList);
  }

  /**
   * Adds labels from the ports of the given lists.
   *
   * @param inputPortList
   *          The list of input ports.
   * @param outputPortList
   *          The list of output ports.
   * @param portList
   *          The list of undirected ports/ports having the same name.
   */
  private void updatePortsFromLists(final List<Entry<String, Label>> inputPortList,
      final List<Entry<String, Label>> outputPortList, final List<Entry<String, Label>> portList) {
    final Iterator<Entry<String, Label>> itInput = inputPortList.iterator();
    final Iterator<Entry<String, Label>> itOutput = outputPortList.iterator();

    // we iterate on input ports first
    while (itInput.hasNext()) {
      if (itOutput.hasNext()) {
        // If the output port iterator has at least one output port, we
        // add both.
        updatePortLabel(itInput.next(), SWT.BEGINNING, 1);
        updatePortLabel(itOutput.next(), SWT.END, 1);
      } else {
        // Otherwise, we add only the input port with an horizontal span
        // equal to 2
        updatePortLabel(itInput.next(), SWT.BEGINNING, 2);
      }
    }

    // Adds any remaining output port with an horizontal span of 2.
    while (itOutput.hasNext()) {
      updatePortLabel(itOutput.next(), SWT.END, 2);
    }

    // Adds undirected ports with an horizontal span of 2.
    for (final Entry<String, Label> entry : portList) {
      updatePortLabel(entry, SWT.CENTER, 2);
      final String portName = entry.getKey();
      final Label label = entry.getValue();
      this.inputPorts.put(portName, label);
      this.outputPorts.put(portName, label);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
   */
  @Override
  protected boolean useLocalCoordinates() {
    return true;
  }

}

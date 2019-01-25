/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018 - 2019)
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
package org.preesm.ui.pisdf.features;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.util.PiMMSwitch;
import org.preesm.ui.utils.ErrorWithExceptionDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class SVGExporterSwitch.
 */
public class SVGExporterSwitch extends PiMMSwitch<Integer> {

  private static final String CIRCLE_LITTERAL = "circle";

  private static final String TRANSFORM_LITTERAL = "transform";

  private static final String HEIGHT_LITTERAL = "height";

  private static final String WIDTH_LITTERAL = "width";

  private static final String STROKE_WIDTH_LITTERAL = "stroke-width";

  private static final String POINTS_LITTERAL = "points";

  private static final String POLYGON_LITTERAL = "polygon";

  private static final String FONT_FAMILY_LITTERAL = "font-family";

  private static final String WIDTH_LITERAL = WIDTH_LITTERAL;

  private static final String HEIGHT_LITERAL = HEIGHT_LITTERAL;

  private static final String START_LITERAL = "start";

  /**
   *
   */
  private final ExportSVGFeature exportSVGFeature;

  /** The doc. */
  protected Document doc;

  /** The svg. */
  protected Element svg;

  /** The total width. */
  protected int totalWidth;

  /** The total height. */
  protected int totalHeight;

  /**
   * Gets the total width.
   *
   * @return the totalWidth
   */
  public int getTotalWidth() {
    return this.totalWidth;
  }

  /**
   * Gets the total height.
   *
   * @return the totalHeight
   */
  public int getTotalHeight() {
    return this.totalHeight;
  }

  /**
   * Instantiates a new SVG exporter switch.
   *
   */
  public SVGExporterSwitch(ExportSVGFeature exportSVGFeature) {
    this.exportSVGFeature = exportSVGFeature;
    this.totalWidth = 0;
    this.totalHeight = 0;
  }

  /**
   *
   */
  public String exportPiGraphToSVG(final PiGraph graph) {
    /* Create Document Builder */
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      builder = dbf.newDocumentBuilder();
    } catch (final ParserConfigurationException e) {
      final String message = "Could not create new document";
      ErrorWithExceptionDialog.errorDialogWithStackTrace(message, e);
      throw new PreesmRuntimeException(message, e);
    }
    this.doc = builder.newDocument();

    /* Populate XML Files with File Header */
    this.svg = doc.createElement("svg");
    doc.appendChild(svg);
    svg.setAttribute(FONT_FAMILY_LITTERAL, "Arial");
    svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");

    final Element defs = doc.createElement("defs");
    svg.appendChild(defs);

    final Element fifoMarker = doc.createElement("marker");
    defs.appendChild(fifoMarker);
    fifoMarker.setAttribute("id", "fifoEnd");
    fifoMarker.setAttribute("markerWidth", "4");
    fifoMarker.setAttribute("markerHeight", "4");
    fifoMarker.setAttribute("refX", "4");
    fifoMarker.setAttribute("refY", "2");
    final Element polygon1 = doc.createElement(POLYGON_LITTERAL);
    fifoMarker.appendChild(polygon1);
    polygon1.setAttribute(POINTS_LITTERAL, "0,0 5,2 0,4");
    polygon1.setAttribute("fill", "rgb(100, 100, 100)");
    polygon1.setAttribute(STROKE_WIDTH_LITTERAL, "none");

    final Element depMarker = doc.createElement("marker");
    defs.appendChild(depMarker);
    depMarker.setAttribute("id", "depEnd");
    depMarker.setAttribute("markerWidth", "4");
    depMarker.setAttribute("markerHeight", "4");
    depMarker.setAttribute("refX", "4");
    depMarker.setAttribute("refY", "2");
    final Element polygon = doc.createElement(POLYGON_LITTERAL);
    depMarker.appendChild(polygon);
    polygon.setAttribute(POINTS_LITTERAL, "0,0 5,2 0,4");
    polygon.setAttribute("fill", "rgb(98, 131, 167)");
    polygon.setAttribute(STROKE_WIDTH_LITTERAL, "none");

    /* Populate SVG File with Graph Data */
    for (final Dependency d : graph.getDependencies()) {
      doSwitch(d);
    }
    for (final Fifo f : graph.getFifos()) {
      doSwitch(f);
    }
    for (final Parameter p : graph.getParameters()) {
      doSwitch(p);
    }
    for (final AbstractActor aa : graph.getActors()) {
      doSwitch(aa);
    }

    svg.setAttribute(WIDTH_LITTERAL, "" + (getTotalWidth() + 20));
    svg.setAttribute(HEIGHT_LITTERAL, "" + (getTotalHeight() + 20));

    /* Write the SVG to String */
    Transformer tf;
    try {
      tf = TransformerFactory.newInstance().newTransformer();
      tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      tf.setOutputProperty(OutputKeys.INDENT, "yes");
      tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
      tf.transform(new DOMSource(doc), new StreamResult(bufferedOutputStream));
      return new String(byteArrayOutputStream.toByteArray());
    } catch (TransformerFactoryConfigurationError | TransformerException e) {
      final String message = "Could not transform SVG to String";
      ErrorWithExceptionDialog.errorDialogWithStackTrace(message, e);
      throw new PreesmRuntimeException(message, e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#caseParameter(org.ietr.preesm.experiment.model.pimm.
   * Parameter)
   */
  @Override
  public Integer caseParameter(final Parameter p) {
    if (p.isLocallyStatic()
        && (p.isConfigurationInterface() && (((ConfigInputInterface) p).getGraphPort() instanceof ConfigInputPort))) {
      return caseConfigInputInterface(p);
    }

    int x = 0;
    int y = 0;
    final PictogramElement[] paramPes = this.exportSVGFeature.getFeatureProvider()
        .getAllPictogramElementsForBusinessObject(p);
    if (paramPes == null) {
      return null;
    }

    x = paramPes[0].getGraphicsAlgorithm().getX();
    y = paramPes[0].getGraphicsAlgorithm().getY();
    final int width = paramPes[0].getGraphicsAlgorithm().getWidth();
    final int height = paramPes[0].getGraphicsAlgorithm().getHeight();

    this.totalWidth = java.lang.Math.max(x + width, this.totalWidth);
    this.totalHeight = java.lang.Math.max(y + height, this.totalHeight);

    final Element paramNode = this.doc.createElement("g");
    this.svg.appendChild(paramNode);
    paramNode.setAttribute("id", p.getName());
    paramNode.setAttribute(TRANSFORM_LITTERAL, "translate" + "(" + x + "," + y + ")");
    final Element polygon = this.doc.createElement(POLYGON_LITTERAL);
    paramNode.appendChild(polygon);
    polygon.setAttribute(POINTS_LITTERAL, "0," + (height) + " " + "0," + (height / 2) + " " + (width / 2) + ",0 "
        + (width) + "," + (height / 2) + " " + (width) + "," + (height));
    polygon.setAttribute("fill", "rgb(187,218,247)");
    polygon.setAttribute("stroke", "rgb(98,131,167)");
    polygon.setAttribute(STROKE_WIDTH_LITTERAL, "4px");

    if (!p.isLocallyStatic()) {
      final Element circle = this.doc.createElement(CIRCLE_LITTERAL);
      paramNode.appendChild(circle);
      circle.setAttribute("cx", "" + (width / 2));
      circle.setAttribute("cy", "15");
      circle.setAttribute("r", "6");
      circle.setAttribute("fill", "white");
      circle.setAttribute(STROKE_WIDTH_LITTERAL, "2px");
      circle.setAttribute("stroke", "rgb(98,131,167)");
    }

    final Element text = this.doc.createElement("text");
    paramNode.appendChild(text);
    text.setAttribute("x", "" + (width / 2));
    text.setAttribute("y", "" + (height - 5));
    text.setAttribute("fill", "black");
    text.setAttribute("text-anchor", "middle");
    addFontToSVG(text, getFont(p));
    text.appendChild(this.doc.createTextNode(p.getName()));
    return 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#caseDataInputInterface(org.ietr.preesm.experiment.model.
   * pimm.DataInputInterface)
   */
  @Override
  public Integer caseDataInputInterface(final DataInputInterface dii) {
    int x = 0;
    int y = 0;
    int width = 0;
    int height = 0;
    final PictogramElement[] diiPes = this.exportSVGFeature.getFeatureProvider()
        .getAllPictogramElementsForBusinessObject(dii);
    if (diiPes != null) {
      x = diiPes[1].getGraphicsAlgorithm().getX();
      y = diiPes[1].getGraphicsAlgorithm().getY();
      width = diiPes[1].getGraphicsAlgorithm().getWidth();
      height = diiPes[1].getGraphicsAlgorithm().getHeight();
    }

    this.totalWidth = java.lang.Math.max(x + width, this.totalWidth);
    this.totalHeight = java.lang.Math.max(y + height, this.totalHeight);

    final Element diiNode = this.doc.createElement("g");
    this.svg.appendChild(diiNode);
    diiNode.setAttribute("id", dii.getName());
    diiNode.setAttribute(TRANSFORM_LITTERAL, "translate(" + x + "," + y + ")");
    {
      final Element rect = this.doc.createElement("rect");
      diiNode.appendChild(rect);
      rect.setAttribute("rx", "2");
      rect.setAttribute("ry", "2");
      rect.setAttribute("x", "" + (width - 16));
      rect.setAttribute("y", "0");
      rect.setAttribute(WIDTH_LITERAL, "16");
      rect.setAttribute(HEIGHT_LITERAL, "16");
      rect.setAttribute("fill", "rgb(182, 215, 122)");
      rect.setAttribute("stroke", "rgb(100,100,100)");
      rect.setAttribute(STROKE_WIDTH_LITTERAL, "3px");

      final Element text = this.doc.createElement("text");
      diiNode.appendChild(text);
      text.setAttribute("x", "2");
      text.setAttribute("y", "11");
      text.setAttribute("fill", "black");
      text.setAttribute("text-anchor", START_LITERAL);
      addFontToSVG(text, getFont(dii));
      text.appendChild(this.doc.createTextNode(dii.getName()));
    }
    return 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#caseDataOutputInterface(org.ietr.preesm.experiment.model.
   * pimm.DataOutputInterface)
   */
  @Override
  public Integer caseDataOutputInterface(final DataOutputInterface doi) {
    int x = 0;
    int y = 0;
    int width = 0;
    int height = 0;
    final PictogramElement[] doiPes = this.exportSVGFeature.getFeatureProvider()
        .getAllPictogramElementsForBusinessObject(doi);
    if (doiPes != null) {
      x = doiPes[1].getGraphicsAlgorithm().getX();
      y = doiPes[1].getGraphicsAlgorithm().getY();
      width = doiPes[1].getGraphicsAlgorithm().getWidth();
      height = doiPes[1].getGraphicsAlgorithm().getHeight();
    }

    this.totalWidth = java.lang.Math.max(x + width, this.totalWidth);
    this.totalHeight = java.lang.Math.max(y + height, this.totalHeight);

    final Element doiNode = this.doc.createElement("g");
    this.svg.appendChild(doiNode);
    doiNode.setAttribute("id", doi.getName());
    doiNode.setAttribute(TRANSFORM_LITTERAL, "translate(" + x + "," + y + ")");
    final Element rect = this.doc.createElement("rect");
    doiNode.appendChild(rect);
    rect.setAttribute("rx", "2");
    rect.setAttribute("ry", "2");
    rect.setAttribute("x", "0");
    rect.setAttribute("y", "0");
    rect.setAttribute(WIDTH_LITERAL, "16");
    rect.setAttribute(HEIGHT_LITERAL, "16");
    rect.setAttribute("fill", "rgb(234, 153, 153)");
    rect.setAttribute("stroke", "rgb(100,100,100)");
    rect.setAttribute(STROKE_WIDTH_LITTERAL, "3px");

    final Element text = this.doc.createElement("text");
    doiNode.appendChild(text);
    text.setAttribute("x", "21");
    text.setAttribute("y", "11");
    text.setAttribute("fill", "black");
    text.setAttribute("text-anchor", START_LITERAL);
    addFontToSVG(text, getFont(doi));
    text.appendChild(this.doc.createTextNode(doi.getName()));
    return 0;
  }

  /**
   * Case config input interface.
   *
   * @param cii
   *          the cii
   * @return the integer
   */
  public Integer caseConfigInputInterface(final Parameter cii) {
    final PictogramElement[] ciiPes = this.exportSVGFeature.getFeatureProvider()
        .getAllPictogramElementsForBusinessObject(cii);
    if (ciiPes == null) {
      throw new IllegalStateException();
    }
    final int x = ciiPes[2].getGraphicsAlgorithm().getX();
    final int y = ciiPes[2].getGraphicsAlgorithm().getY();
    final int width = ciiPes[2].getGraphicsAlgorithm().getWidth();
    final int height = ciiPes[2].getGraphicsAlgorithm().getHeight();

    this.totalWidth = java.lang.Math.max(x + width, this.totalWidth);
    this.totalHeight = java.lang.Math.max(y + height, this.totalHeight);

    final Element ciiNode = this.doc.createElement("g");
    this.svg.appendChild(ciiNode);
    ciiNode.setAttribute("id", cii.getName());
    ciiNode.setAttribute(TRANSFORM_LITTERAL, "translate(" + x + "," + y + ")");
    final Element polygon = this.doc.createElement(POLYGON_LITTERAL);
    final PictogramElement pictogramElement = ciiPes[0];
    final Polygon polyPe = (Polygon) pictogramElement.getGraphicsAlgorithm();
    ciiNode.appendChild(polygon);
    final StringBuilder points = new StringBuilder();
    for (final Point p : polyPe.getPoints()) {
      points.append((p.getX() + 3) + "," + (p.getY() + 16) + " ");
    }
    polygon.setAttribute(POINTS_LITTERAL, points.toString());
    polygon.setAttribute("fill", "rgb(187, 218, 247)");
    polygon.setAttribute("stroke", "rgb(98,131,167)");
    polygon.setAttribute(STROKE_WIDTH_LITTERAL, "3px");

    final Element text = this.doc.createElement("text");
    ciiNode.appendChild(text);
    text.setAttribute("x", "" + (width / 2));
    text.setAttribute("y", "10");
    text.setAttribute("fill", "black");
    text.setAttribute("text-anchor", "middle");
    addFontToSVG(text, getFont(cii));
    text.appendChild(this.doc.createTextNode(cii.getName()));
    return 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#caseConfigOutputInterface(org.ietr.preesm.experiment.model.
   * pimm.ConfigOutputInterface)
   */
  @Override
  public Integer caseConfigOutputInterface(final ConfigOutputInterface coi) {
    int x = 0;
    int y = 0;
    final PictogramElement[] coiPes = this.exportSVGFeature.getFeatureProvider()
        .getAllPictogramElementsForBusinessObject(coi);
    if (coiPes != null) {
      x = coiPes[1].getGraphicsAlgorithm().getX();
      y = coiPes[1].getGraphicsAlgorithm().getY();
    }

    // TODO Adjust size
    this.totalWidth = java.lang.Math.max(x + 16, this.totalWidth);
    this.totalHeight = java.lang.Math.max(y + 16, this.totalHeight);

    final Element coiNode = this.doc.createElement("g");
    this.svg.appendChild(coiNode);
    coiNode.setAttribute("id", coi.getName());
    coiNode.setAttribute(TRANSFORM_LITTERAL, "translate(" + x + "," + y + ")");
    final Element polygon = this.doc.createElement(POLYGON_LITTERAL);
    coiNode.appendChild(polygon);
    polygon.setAttribute(POINTS_LITTERAL, "0,0 16,8 0,16");
    polygon.setAttribute("fill", "rgb(255, 229, 153)");
    polygon.setAttribute("stroke", "rgb(100,100,100)");
    polygon.setAttribute(STROKE_WIDTH_LITTERAL, "3px");

    final Element text = this.doc.createElement("text");
    coiNode.appendChild(text);
    text.setAttribute("x", "21");
    text.setAttribute("y", "11");
    text.setAttribute("fill", "black");
    text.setAttribute("text-anchor", START_LITERAL);
    addFontToSVG(text, getFont(coi));
    text.appendChild(this.doc.createTextNode(coi.getName()));
    return 0;
  }

  @Override
  public Integer caseDelayActor(DelayActor object) {
    // skip
    return 0;
  }

  @Override
  public Integer casePiGraph(PiGraph object) {
    // skip
    return 0;
  }

  @Override
  public Integer caseAbstractActor(final AbstractActor ea) {
    int x = 0;
    int y = 0;
    final PictogramElement[] actorPes = this.exportSVGFeature.getFeatureProvider()
        .getAllPictogramElementsForBusinessObject(ea);
    if (actorPes == null) {
      return null;
    }

    x = actorPes[0].getGraphicsAlgorithm().getX();
    y = actorPes[0].getGraphicsAlgorithm().getY();
    final int width = actorPes[0].getGraphicsAlgorithm().getWidth();
    final int height = actorPes[0].getGraphicsAlgorithm().getHeight();

    this.totalWidth = java.lang.Math.max(x + width, this.totalWidth);
    this.totalHeight = java.lang.Math.max(y + height, this.totalHeight);

    /* Draw Actor */
    final Element actorNode = drawActor(ea, x, y, actorPes, width, height);

    /* Draw Config Input Ports */
    for (int i = 0; i < ea.getConfigInputPorts().size(); i++) {
      final ConfigInputPort cip = ea.getConfigInputPorts().get(i);
      final BoxRelativeAnchor bra = getPortBra(cip);

      final int portX = (int) (bra.getRelativeWidth() * width);
      final int portY = (int) (bra.getRelativeHeight() * height);
      Text portText = null;

      for (final GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
        if (ga instanceof Text) {
          portText = (Text) ga;
        }
      }

      if (portText == null) {
        return null;
      }

      final Element portNode = this.doc.createElement("g");
      actorNode.appendChild(portNode);
      portNode.setAttribute("id", cip.getName());
      portNode.setAttribute(TRANSFORM_LITTERAL, "translate(" + portX + "," + portY + ")");
      final Element polygon = this.doc.createElement(POLYGON_LITTERAL);
      portNode.appendChild(polygon);
      polygon.setAttribute(POINTS_LITTERAL, "0,0 8,5 0,10");
      polygon.setAttribute("fill", "rgb(187, 218, 247)");
      polygon.setAttribute("stroke", "rgb(100,100,100)");
      polygon.setAttribute(STROKE_WIDTH_LITTERAL, "1px");

      final Element text = this.doc.createElement("text");
      portNode.appendChild(text);
      configTextToSVG(text, portText);
      text.appendChild(this.doc.createTextNode(cip.getName()));
    }

    /* Draw Config Output Ports */
    for (int i = 0; i < ea.getConfigOutputPorts().size(); i++) {
      final ConfigOutputPort cop = ea.getConfigOutputPorts().get(i);
      final BoxRelativeAnchor bra = getPortBra(cop);

      final int portX = (int) (bra.getRelativeWidth() * width);
      final int portY = (int) (bra.getRelativeHeight() * height);
      Text portText = null;

      for (final GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
        if (ga instanceof Text) {
          portText = (Text) ga;
        }
      }

      if (portText == null) {
        return null;
      }

      final Element portNode = this.doc.createElement("g");
      actorNode.appendChild(portNode);
      portNode.setAttribute("id", cop.getName());
      portNode.setAttribute(TRANSFORM_LITTERAL, "translate(" + portX + "," + portY + ")");
      final Element polygon = this.doc.createElement(POLYGON_LITTERAL);
      portNode.appendChild(polygon);
      polygon.setAttribute(POINTS_LITTERAL, "0,0 -8,5 0,10");
      polygon.setAttribute("fill", "rgb(255, 229, 153)");
      polygon.setAttribute("stroke", "rgb(100,100,100)");
      polygon.setAttribute(STROKE_WIDTH_LITTERAL, "1px");

      final Element text = this.doc.createElement("text");
      portNode.appendChild(text);
      configTextToSVG(text, portText);
      text.appendChild(this.doc.createTextNode(cop.getName()));
    }

    /* Draw Data Input Ports */
    for (int i = 0; i < ea.getDataInputPorts().size(); i++) {
      final DataInputPort dip = ea.getDataInputPorts().get(i);
      final BoxRelativeAnchor bra = getPortBra(dip);

      final int portX = (int) (bra.getRelativeWidth() * width);
      final int portY = (int) (bra.getRelativeHeight() * height);
      Text portText = null;

      for (final GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
        if (ga instanceof Text) {
          portText = (Text) ga;
        }
      }

      if (portText == null) {
        return null;
      }

      final Element portNode = this.doc.createElement("g");
      actorNode.appendChild(portNode);
      portNode.setAttribute("id", dip.getName());
      portNode.setAttribute(TRANSFORM_LITTERAL, "translate(" + portX + "," + portY + ")");
      final Element rect = this.doc.createElement("rect");
      portNode.appendChild(rect);
      rect.setAttribute("x", "0");
      rect.setAttribute("y", "1");
      rect.setAttribute(WIDTH_LITERAL, "8");
      rect.setAttribute(HEIGHT_LITERAL, "8");
      rect.setAttribute("fill", "rgb(182, 215, 122)");
      rect.setAttribute("stroke", "rgb(100,100,100)");
      rect.setAttribute(STROKE_WIDTH_LITTERAL, "1px");

      final Element text = this.doc.createElement("text");
      portNode.appendChild(text);
      configTextToSVG(text, portText);
      text.appendChild(this.doc.createTextNode(dip.getName()));
    }

    /* Draw Data Output Ports */
    for (int i = 0; i < ea.getDataOutputPorts().size(); i++) {
      final DataOutputPort dop = ea.getDataOutputPorts().get(i);
      final BoxRelativeAnchor bra = getPortBra(dop);

      final int portX = (int) (bra.getRelativeWidth() * width - 8);
      final int portY = (int) (bra.getRelativeHeight() * height);
      Text portText = null;

      for (final GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
        if (ga instanceof Text) {
          portText = (Text) ga;
        }
      }

      if (portText == null) {
        return null;
      }

      final Element portNode = this.doc.createElement("g");
      actorNode.appendChild(portNode);
      portNode.setAttribute("id", dop.getName());
      portNode.setAttribute(TRANSFORM_LITTERAL, "translate(" + portX + "," + portY + ")");
      final Element rect = this.doc.createElement("rect");
      portNode.appendChild(rect);
      rect.setAttribute("x", "0");
      rect.setAttribute("y", "1");
      rect.setAttribute(WIDTH_LITERAL, "-8");
      rect.setAttribute(HEIGHT_LITERAL, "8");
      rect.setAttribute("fill", "rgb(234, 153, 153)");
      rect.setAttribute("stroke", "rgb(100,100,100)");
      rect.setAttribute(STROKE_WIDTH_LITTERAL, "1px");

      final Element text = this.doc.createElement("text");
      portNode.appendChild(text);
      configTextToSVG(text, portText);
      text.appendChild(this.doc.createTextNode(dop.getName()));
    }

    return 1;
  }

  private Element drawActor(final AbstractActor ea, int x, int y, final PictogramElement[] actorPes, final int width,
      final int height) {
    final Element actorNode = this.doc.createElement("g");
    this.svg.appendChild(actorNode);
    actorNode.setAttribute("id", ea.getName());
    actorNode.setAttribute(TRANSFORM_LITTERAL, "translate(" + x + "," + y + ")");
    final ContainerShape containerShape = (ContainerShape) actorPes[0];
    final EList<Shape> childrenShapes = containerShape.getChildren();

    Text nameText = null;
    final RoundedRectangle actorRect = (RoundedRectangle) actorPes[0].getGraphicsAlgorithm();
    for (final Shape shape : childrenShapes) {
      final GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
      if (child instanceof Text) {
        nameText = (Text) child;
      }
    }

    final Element rect = this.doc.createElement("rect");
    actorNode.appendChild(rect);
    rect.setAttribute("rx", "4");
    rect.setAttribute("ry", "4");
    rect.setAttribute(WIDTH_LITERAL, "" + width);
    rect.setAttribute(HEIGHT_LITERAL, "" + height);

    rect.setAttribute("fill", "rgb(" + actorRect.getBackground().getRed() + ", " + actorRect.getBackground().getGreen()
        + ", " + actorRect.getBackground().getBlue() + ")");

    rect.setAttribute("stroke", "rgb(" + actorRect.getForeground().getRed() + ", "
        + actorRect.getForeground().getGreen() + ", " + actorRect.getForeground().getBlue() + ")");

    rect.setAttribute(STROKE_WIDTH_LITTERAL, "3px");

    if (!ea.getConfigOutputPorts().isEmpty()) {
      final Element circle = this.doc.createElement(CIRCLE_LITTERAL);
      actorNode.appendChild(circle);
      circle.setAttribute("cx", "" + (width - 8));
      circle.setAttribute("cy", "9");
      circle.setAttribute("r", "4");
      circle.setAttribute("fill", "white");
      circle.setAttribute(STROKE_WIDTH_LITTERAL, "2px");
      circle.setAttribute("stroke", "rgb(100,100,100)");
    }

    final Element text = this.doc.createElement("text");
    actorNode.appendChild(text);
    configTextToSVG(text, nameText);
    text.appendChild(this.doc.createTextNode(ea.getName()));
    return actorNode;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#caseDependency(org.ietr.preesm.experiment.model.pimm.
   * Dependency)
   */
  @Override
  public Integer caseDependency(final Dependency d) {
    final FreeFormConnection ffc = getDepFFC(d);

    final Element depNode = this.doc.createElement("path");
    this.svg.appendChild(depNode);

    final ILocation start = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(ffc.getStart());

    if (d.getSetter() instanceof ConfigOutputPort) {
      start.setY(start.getY() + 5);
    }

    final ILocation end = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(ffc.getEnd());

    if (d.getGetter().eContainer() instanceof Parameter) {
      final Parameter p = (Parameter) d.getGetter().eContainer();
      final PictogramElement[] pPes = this.exportSVGFeature.getFeatureProvider()
          .getAllPictogramElementsForBusinessObject(p);
      end.setX(end.getX() - (pPes[0].getGraphicsAlgorithm().getWidth() / 2));
    } else {
      end.setY(end.getY() + 5);
    }

    final StringBuilder points = new StringBuilder("m ");
    int prevX = start.getX();
    int prevY = start.getY();
    points.append(start.getX() + "," + start.getY() + " ");
    for (final org.eclipse.graphiti.mm.algorithms.styles.Point p : ffc.getBendpoints()) {
      points.append((p.getX() - prevX) + "," + (p.getY() - prevY) + " ");
      prevX = p.getX();
      prevY = p.getY();
    }
    points.append((end.getX() - prevX) + "," + (end.getY() - prevY) + " ");

    depNode.setAttribute("d", points.toString());
    depNode.setAttribute("fill", "none");
    depNode.setAttribute("stroke", "rgb(98, 131, 167)");
    depNode.setAttribute(STROKE_WIDTH_LITTERAL, "3px");
    depNode.setAttribute("stroke-dasharray", "5,2");
    depNode.setAttribute("marker-end", "url(#depEnd)");

    return 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#caseFifo(org.ietr.preesm.experiment.model.pimm.Fifo)
   */
  @Override
  public Integer caseFifo(final Fifo f) {
    final Set<FreeFormConnection> ffcs = getFifoFFC(f);

    for (final FreeFormConnection ffc : ffcs) {
      final Element depNode = this.doc.createElement("path");
      this.svg.appendChild(depNode);

      final ILocation start = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(ffc.getStart());

      if (f.getSourcePort() instanceof DataOutputPort) {
        start.setY(start.getY() + 5);
      }

      final ILocation end = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(ffc.getEnd());

      if (f.getTargetPort() instanceof DataInputPort) {
        end.setY(end.getY() + 5);
      }

      final StringBuilder points = new StringBuilder("m ");
      int prevX = start.getX();
      int prevY = start.getY();
      points.append(start.getX() + "," + start.getY() + " ");
      for (final org.eclipse.graphiti.mm.algorithms.styles.Point p : ffc.getBendpoints()) {
        points.append((p.getX() - prevX) + "," + (p.getY() - prevY) + " ");
        prevX = p.getX();
        prevY = p.getY();
      }
      points.append((end.getX() - prevX) + "," + (end.getY() - prevY) + " ");

      depNode.setAttribute("d", points.toString());
      depNode.setAttribute("fill", "none");
      depNode.setAttribute("stroke", "rgb(100, 100, 100)");
      depNode.setAttribute(STROKE_WIDTH_LITTERAL, "3px");
      depNode.setAttribute("marker-end", "url(#fifoEnd)");
    }

    if (f.getDelay() != null) {
      final PictogramElement[] pes = this.exportSVGFeature.getFeatureProvider()
          .getAllPictogramElementsForBusinessObject(f.getDelay());
      final Ellipse delay = (Ellipse) (pes[0].getGraphicsAlgorithm());

      final Element circle = this.doc.createElement(CIRCLE_LITTERAL);
      this.svg.appendChild(circle);
      circle.setAttribute("cx", "" + (delay.getX() + 12));
      circle.setAttribute("cy", "" + (delay.getY() + 12));
      circle.setAttribute("r", "8");
      circle.setAttribute("fill", "rgb(100,100,100)");
      circle.setAttribute("stroke", "rgb(100,100,100)");
      circle.setAttribute(STROKE_WIDTH_LITTERAL, "1px");

      pes[0].getLink();
    }

    return 0;
  }

  /**
   * Gets the font.
   *
   * @param aa
   *          the aa
   * @return the font
   */
  private Font getFont(final AbstractVertex aa) {
    // Retrieve the shape and the graphic algorithm
    final PictogramElement[] actorPes = this.exportSVGFeature.getFeatureProvider()
        .getAllPictogramElementsForBusinessObject(aa);

    ContainerShape containerShape = null;
    for (final PictogramElement pe : actorPes) {
      if (pe instanceof ContainerShape) {
        containerShape = (ContainerShape) pe;
      }
    }
    if (containerShape == null) {
      throw new IllegalArgumentException("getFont of a AbstractVertex without ContainerShape");
    }

    final EList<Shape> childrenShapes = containerShape.getChildren();

    for (final Shape shape : childrenShapes) {
      final GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
      // The name should be the only children with type text
      if (child instanceof Text) {
        return ((Text) child).getFont();
      }
    }
    return null;
  }

  /**
   * Gets the fifo FFC.
   *
   * @param f
   *          the f
   * @return the fifo FFC
   */
  private Set<FreeFormConnection> getFifoFFC(final Fifo f) {
    final Set<FreeFormConnection> result = new LinkedHashSet<>();
    final PictogramElement[] pes = this.exportSVGFeature.getFeatureProvider()
        .getAllPictogramElementsForBusinessObject(f);
    if (pes == null) {
      return Collections.emptySet();
    }

    for (final PictogramElement pe : pes) {
      final Fifo fPe = (Fifo) (((pe).getLink().getBusinessObjects()).get(0));
      if (fPe.equals(f)) {
        result.add((FreeFormConnection) pe);
      }
    }
    return result;
  }

  /**
   * Gets the dep FFC.
   *
   * @param d
   *          the d
   * @return the dep FFC
   */
  private FreeFormConnection getDepFFC(final Dependency d) {
    final PictogramElement[] pes = this.exportSVGFeature.getFeatureProvider()
        .getAllPictogramElementsForBusinessObject(d);
    if (pes == null) {
      throw new PreesmRuntimeException();
    }

    int id = -1;
    for (int i1 = 0; i1 < pes.length; i1++) {
      final Dependency dPe = (Dependency) (((pes[i1]).getLink().getBusinessObjects()).get(0));
      if (dPe.equals(d)) {
        id = i1;
      }
    }

    return (FreeFormConnection) pes[id];
  }

  /**
   * Gets the port bra.
   *
   * @param p
   *          the p
   * @return the port bra
   */
  private BoxRelativeAnchor getPortBra(final Port p) {
    final PictogramElement[] pes = this.exportSVGFeature.getFeatureProvider()
        .getAllPictogramElementsForBusinessObject(p);
    if (pes == null) {
      throw new PreesmRuntimeException();
    }

    int id = -1;
    for (int i1 = 0; i1 < pes.length; i1++) {
      final Port pPe = (Port) (((pes[i1]).getLink().getBusinessObjects()).get(0));
      if (pPe.equals(p)) {
        id = i1;
      }
    }

    return (BoxRelativeAnchor) pes[id];
  }

  /**
   * Config text to SVG.
   *
   * @param el
   *          the el
   * @param t
   *          the t
   */
  private void configTextToSVG(final Element el, final Text t) {
    if (t == null) {
      return;
    }
    final int textHeight = GraphitiUi.getUiLayoutService().calculateTextSize(t.getValue(), t.getFont()).getHeight();
    switch (t.getVerticalAlignment()) {
      case ALIGNMENT_BOTTOM:
        el.setAttribute("y", "" + (t.getY() + t.getHeight()));
        break;
      case ALIGNMENT_CENTER:
        el.setAttribute("y", "" + ((t.getY() + (t.getHeight() / 2) + (t.getFont().getSize() / 2)) - 2));
        break;
      case ALIGNMENT_TOP:
        el.setAttribute("y", "" + (t.getY() + textHeight));
        break;
      default:
    }

    switch (t.getHorizontalAlignment()) {
      case ALIGNMENT_LEFT:
        // TODO (t.getX()-t.getWidth()));
        el.setAttribute("x", "" + (-10));
        el.setAttribute("text-anchor", "end");
        break;
      case ALIGNMENT_RIGHT:
        el.setAttribute("x", "" + (t.getX() + 2));
        el.setAttribute("text-anchor", START_LITERAL);
        break;
      default:
      case ALIGNMENT_MIDDLE:
        el.setAttribute("x", "" + (t.getX() + (t.getWidth() / 2)));
        el.setAttribute("text-anchor", "middle");
        break;
    }

    el.setAttribute("fill", "rgb(" + t.getForeground().getRed() + ", " + t.getForeground().getGreen() + ", "
        + t.getForeground().getBlue() + ")");

    el.setAttribute("font-size", t.getFont().getSize() + "pt");
    el.setAttribute(FONT_FAMILY_LITTERAL, t.getFont().getName());
    if (t.getFont().isBold()) {
      el.setAttribute("font-weight", "bold");
    }
    if (t.getFont().isItalic()) {
      el.setAttribute("font-style", "italic");
    }

  }

  /**
   * Adds the font to SVG.
   *
   * @param e
   *          the e
   * @param f
   *          the f
   */
  private void addFontToSVG(final Element e, final Font f) {
    if (f == null) {
      return;
    }

    e.setAttribute("font-size", f.getSize() + "pt");
    e.setAttribute(FONT_FAMILY_LITTERAL, f.getName());
    if (f.isBold()) {
      e.setAttribute("font-weight", "bold");
    }
    if (f.isItalic()) {
      e.setAttribute("font-style", "italic");
    }
  }

  /**
   * Compute actor height.
   *
   * @param ea
   *          the ea
   * @return the int
   */
  protected static int computeActorHeight(final ExecutableActor ea) {
    int height;

    /* Compute Actor Height */
    final int nConfigPorts = java.lang.Math.max(ea.getConfigInputPorts().size(), ea.getConfigOutputPorts().size());
    final int nDataPorts = java.lang.Math.max(ea.getDataInputPorts().size(), ea.getDataOutputPorts().size());
    height = 25 /* Name */
        + (nConfigPorts * 15) + (nDataPorts * 15);

    return height;
  }

}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Jerôme Gorin [jerome.gorin@insa-rennes.fr] (2009)
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
package org.ietr.dftools.graphiti.ui.editors;

import java.util.Set;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.ietr.dftools.graphiti.model.Configuration;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.ui.GraphitiUiPlugin;
import org.ietr.dftools.graphiti.ui.editparts.EdgeCreationFactory;
import org.ietr.dftools.graphiti.ui.editparts.VertexCreationFactory;
import org.ietr.dftools.graphiti.ui.figure.VertexFigure;
import org.ietr.dftools.graphiti.ui.figure.shapes.IShape;
import org.ietr.dftools.graphiti.ui.figure.shapes.ShapeFactory;

/**
 * Creates the Palette on the GUI with all the tools and the appropriate icons icons have to be in the same directory as
 * the Model.
 *
 * @author Samuel Beaussier & Nicolas Isch
 */
public class GraphitiPalette {

  private GraphitiPalette() {

  }

  /**
   * Add the different edge types.
   *
   * @param graph
   *          The graph used to configure this palette.
   * @param container
   *          the container
   */
  private static void addEdgeTypes(final Graph graph, final PaletteContainer container) {
    if (graph != null) {
      final PaletteDrawer edgeDrawer = new PaletteDrawer("Connections");

      final Configuration config = graph.getConfiguration();
      final Set<ObjectType> edgeTypes = config.getEdgeTypes();
      for (final ObjectType type : edgeTypes) {
        final String typeStr = type.getName();

        final ImageDescriptor id = GraphitiPalette.getImgDescEdge(type);

        final ToolEntry tool = new ConnectionCreationToolEntry(typeStr, "Create a new " + typeStr,
            new EdgeCreationFactory(type), id, ImageDescriptor.getMissingImageDescriptor());

        edgeDrawer.add(tool);
      }

      // Add connection tool
      container.add(edgeDrawer);
    }
  }

  /**
   * Add the different vertex types.
   *
   * @param graph
   *          The graph used to configure this palette.
   * @param container
   *          the container
   */
  private static void addVertexTypes(final Graph graph, final PaletteContainer container) {
    if (graph != null) {
      final PaletteDrawer toolDrawer = new PaletteDrawer("Vertices");

      final Configuration config = graph.getConfiguration();
      final Set<ObjectType> vertexTypes = config.getVertexTypes();
      for (final ObjectType type : vertexTypes) {
        final String typeStr = type.getName();

        final ImageDescriptor id = GraphitiPalette.getImgDescVertex(type);

        final ToolEntry tool = new CreationToolEntry(typeStr, "Create a new " + typeStr,
            new VertexCreationFactory(type), id, null);

        toolDrawer.add(tool);
      }

      container.add(toolDrawer);
    }
  }

  /**
   * Returns a new image descriptor from the given edge type.
   *
   * @param type
   *          A string representation of the edge type.
   * @return A new {@link ImageDescriptor}.
   */
  private static ImageDescriptor getImgDescEdge(final ObjectType type) {
    final String imagePath;
    final Boolean directed = (Boolean) type.getAttribute(ObjectType.ATTRIBUTE_DIRECTED);
    if (Boolean.FALSE.equals(directed)) {
      imagePath = "icons/undirected_edge.gif";
    } else {
      imagePath = "icons/directed_edge.gif";
    }

    final Image image = GraphitiUiPlugin.getImage(imagePath);

    // retrieve the color
    Color color = (Color) type.getAttribute(ObjectType.ATTRIBUTE_COLOR);
    if (color == null) {
      color = ColorConstants.black;
    }

    return ImageDescriptor.createFromImageDataProvider(new RecoloredImageDataProvider(image, color));
  }

  /**
   *
   */
  private static final class RecoloredImageDataProvider implements ImageDataProvider {

    private final ImageData image;
    private final Color     color;

    public RecoloredImageDataProvider(final Image image, final Color color) {
      this.image = image.getImageData();
      this.color = color;
    }

    @Override
    public ImageData getImageData(final int zoom) {

      final ImageData data = this.image;
      if (data.palette.colors != null) {
        final RGB rgb = data.palette.colors[0];
        rgb.red = this.color.getRed();
        rgb.green = this.color.getGreen();
        rgb.blue = this.color.getBlue();
      }

      return data;
    }

  }

  /**
   * Returns a new image descriptor from the given vertex type.
   *
   * @param type
   *          A string representation of the vertex type.
   * @return A new {@link ImageDescriptor}.
   */
  private static ImageDescriptor getImgDescVertex(final ObjectType type) {
    // attributes
    int width = (Integer) type.getAttribute(ObjectType.ATTRIBUTE_WIDTH);
    int height = (Integer) type.getAttribute(ObjectType.ATTRIBUTE_HEIGHT);
    final Color color = (Color) type.getAttribute(ObjectType.ATTRIBUTE_COLOR);
    final String name = (String) type.getAttribute(ObjectType.ATTRIBUTE_SHAPE);
    final IShape shape = ShapeFactory.createShape(name);

    // adjust width and height
    final double ratio = (double) width / (double) height;
    width = 16;
    height = (int) (width / ratio);

    // Creates a new vertex figure
    final Font font = Display.getDefault().getSystemFont();
    final VertexFigure figure = new VertexFigure(font, new Dimension(width, height), color, shape);

    // Creates a new image of width x height on the current display
    final Image image = new Image(Display.getCurrent(), width, height);

    // Paints the figure on it
    final GC gc = new GC(image);
    final Graphics graphics = new SWTGraphics(gc);
    figure.paint(graphics);

    // Get the image data back
    final ImageDescriptor id = ImageDescriptor
        .createFromImageDataProvider(new RecoloredImageDataProvider(image, color));

    // Disposes image (and GC btw) and SWT graphics
    image.dispose();
    graphics.dispose();

    return id;
  }

  /**
   * Gets a palette root which is configured by the given {@link Graph}. If <code>graph == null</code>,
   * <code>null</code> is returned.
   *
   * @param graph
   *          The graph used to configure this palette.
   * @return A {@link PaletteRoot} or <code>null</code>.
   */
  public static PaletteRoot getPaletteRoot(final Graph graph) {
    if (graph == null) {
      return null;
    }

    final PaletteRoot paletteModel = new PaletteRoot();
    final PaletteGroup toolGroup = new PaletteGroup("Tools");
    paletteModel.add(toolGroup);

    // Add a selection tool to the group
    final ToolEntry tool = new SelectionToolEntry();
    toolGroup.add(tool);
    paletteModel.setDefaultEntry(tool);

    // Add a marquee tool to the group
    toolGroup.add(new MarqueeToolEntry());

    GraphitiPalette.addVertexTypes(graph, paletteModel);
    GraphitiPalette.addEdgeTypes(graph, paletteModel);

    return paletteModel;
  }
}

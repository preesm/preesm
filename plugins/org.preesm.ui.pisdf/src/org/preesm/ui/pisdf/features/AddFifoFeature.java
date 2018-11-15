/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
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

import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * Add feature to add a {@link Fifo} to the {@link Diagram}.
 *
 * @author kdesnos
 */
public class AddFifoFeature extends AbstractAddFeature {

  /** The Constant FIFO_FOREGROUND. */
  public static final IColorConstant FIFO_FOREGROUND = new ColorConstant(100, 100, 100);

  /**
   * The default constructor of {@link AddFifoFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public AddFifoFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public PictogramElement add(final IAddContext context) {
    final IAddConnectionContext addContext = (IAddConnectionContext) context;
    final Fifo addedFifo = (Fifo) addContext.getNewObject();
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();

    // if added Fifo has no resource we add it to the resource
    // of the diagram
    // in a real scenario the business model would have its own resource
    if (addedFifo.eResource() == null) {
      final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
      graph.addFifo(addedFifo);
    }

    // CONNECTION WITH POLYLINE
    final FreeFormConnection connection = peCreateService.createFreeFormConnection(getDiagram());
    createEdge(addContext, connection);

    // Add the arrow
    ConnectionDecorator cd;
    cd = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
    createArrow(cd);

    // create link and wire it
    link(connection, addedFifo);

    return connection;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public boolean canAdd(final IAddContext context) {
    // Return true if the given Business object is a Fifo and the context is
    // an instance of IAddConnectionContext
    if ((context instanceof IAddConnectionContext) && (context.getNewObject() instanceof Fifo)) {
      return true;
    }
    return false;
  }

  /**
   * Create the arrow {@link Polygon} that will decorate the {@link Fifo}.
   *
   * @param gaContainer
   *          the {@link GraphicsAlgorithmContainer}
   * @return the polygon
   */
  protected Polygon createArrow(final GraphicsAlgorithmContainer gaContainer) {
    final IGaService gaService = Graphiti.getGaService();
    final Polygon polygon = gaService.createPlainPolygon(gaContainer, new int[] { -10, 5, 0, 0, -10, -5 });
    polygon.setForeground(manageColor(AddFifoFeature.FIFO_FOREGROUND));
    polygon.setBackground(manageColor(AddFifoFeature.FIFO_FOREGROUND));
    polygon.setLineWidth(2);
    return polygon;
  }

  /**
   * Creates the edge.
   *
   * @param addContext
   *          the add context
   * @param connection
   *          the connection
   */
  protected void createEdge(final IAddConnectionContext addContext, final FreeFormConnection connection) {
    // Set the connection src and snk
    connection.setStart(addContext.getSourceAnchor());
    connection.setEnd(addContext.getTargetAnchor());

    // Layout the edge
    // Call the move feature of the anchor owner to layout the connection
    final MoveAbstractActorFeature moveFeature = new MoveAbstractActorFeature(getFeatureProvider());
    // Move source
    final Anchor start = connection.getStart();
    final GraphicsAlgorithm referencedGraphicsAlgorithm = start.getReferencedGraphicsAlgorithm();
    ContainerShape cs;
    MoveShapeContext moveCtxt;
    ILocation csLoc;
    if (referencedGraphicsAlgorithm != null) {
      cs = (ContainerShape) referencedGraphicsAlgorithm.getPictogramElement();
      moveCtxt = new MoveShapeContext(cs);
      moveCtxt.setDeltaX(0);
      moveCtxt.setDeltaY(0);
      csLoc = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(cs);
      moveCtxt.setLocation(csLoc.getX(), csLoc.getY());
      moveFeature.moveShape(moveCtxt);
    }

    // Move target
    final Anchor end = connection.getEnd();
    final GraphicsAlgorithm referencedGraphicsAlgorithm2 = end.getReferencedGraphicsAlgorithm();
    if (referencedGraphicsAlgorithm2 != null) {
      cs = (ContainerShape) referencedGraphicsAlgorithm2.getPictogramElement();
      moveCtxt = new MoveShapeContext(cs);
      moveCtxt.setDeltaX(0);
      moveCtxt.setDeltaY(0);
      csLoc = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(cs);
      moveCtxt.setLocation(csLoc.getX(), csLoc.getY());
      moveFeature.moveShape(moveCtxt);
    }

    // Create the associated Polyline
    final IGaService gaService = Graphiti.getGaService();
    final Polyline polyline = gaService.createPolyline(connection);
    polyline.setLineWidth(2);
    polyline.setForeground(manageColor(AddFifoFeature.FIFO_FOREGROUND));
  }
}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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
package org.ietr.preesm.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * Add Feature to add a {@link Dependency} to the {@link Diagram}.
 *
 * @author kdesnos
 */
public class AddDependencyFeature extends AbstractAddFeature {

  /** The Constant DEPENDENCY_FOREGROUND. */
  private static final IColorConstant DEPENDENCY_FOREGROUND = new ColorConstant(98, 131, 167);

  /**
   * Default constructor of th {@link AddDependencyFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public AddDependencyFeature(final IFeatureProvider fp) {
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
    final Dependency addedDependency = (Dependency) context.getNewObject();
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();

    // if added Dependency has no resource we add it to the resource
    // of the Graph
    if (addedDependency.eResource() == null) {
      final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
      graph.addDependency(addedDependency);
    }

    // CONNECTION WITH POLYLINE
    final FreeFormConnection connection = peCreateService.createFreeFormConnection(getDiagram());
    createEdge(addContext, connection);

    // Add the arrow
    ConnectionDecorator cd;
    cd = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
    createArrow(cd);

    // create link and wire it
    link(connection, addedDependency);

    return connection;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public boolean canAdd(final IAddContext context) {
    // Return true if the given Business object is a Dependency and the
    // context is an instance of IAddConnectionContext
    final Object ctxtNewObject = context.getNewObject();
    final boolean newObjectIsDependency = ctxtNewObject instanceof Dependency;
    return newObjectIsDependency;
  }

  /**
   * Create the arrow {@link Polyline} that will decorate the {@link Dependency}.
   *
   * @param gaContainer
   *          the {@link GraphicsAlgorithmContainer}
   * @return the polyline
   */
  protected Polyline createArrow(final GraphicsAlgorithmContainer gaContainer) {
    final IGaService gaService = Graphiti.getGaService();
    final Polyline polyline = gaService.createPlainPolyline(gaContainer, new int[] { -10, 5, 0, 0, -10, -5 });
    polyline.setForeground(manageColor(AddDependencyFeature.DEPENDENCY_FOREGROUND));
    polyline.setLineWidth(2);
    return polyline;
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
    /*
     * MoveAbstractActorFeature moveFeature = new MoveAbstractActorFeature( getFeatureProvider()); ContainerShape cs =
     * (ContainerShape) connection.getStart() .getReferencedGraphicsAlgorithm().getPictogramElement(); MoveShapeContext
     * moveCtxt = new MoveShapeContext(cs); moveCtxt.setDeltaX(0); moveCtxt.setDeltaY(0); ILocation csLoc =
     * Graphiti.getPeLayoutService() .getLocationRelativeToDiagram(cs); moveCtxt.setLocation(csLoc.getX(),
     * csLoc.getY()); moveFeature.moveShape(moveCtxt);
     */

    // Create the associated Polyline
    final IGaService gaService = Graphiti.getGaService();
    final Polyline polyline = gaService.createPolyline(connection);
    polyline.setLineWidth(2);
    polyline.setForeground(manageColor(AddDependencyFeature.DEPENDENCY_FOREGROUND));
    polyline.setLineStyle(LineStyle.DASH);
  }

}

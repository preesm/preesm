/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.services.IPeLayoutService;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;

/**
 * Add feature responsible for creating and adding a delay to a {@link Fifo}.
 *
 * @author kdesnos
 * @author jheulot
 *
 */
public class AddDelayFeature extends AbstractCustomFeature {

  /** The Constant DELAY_SIZE. */
  public static final int DELAY_SIZE = 16;

  /**
   * XXX Hack to keep track of created PEs in order to link them with the proper delay (not the one created in the execute() method...)
   */
  private List<PictogramElement> createdPEs;

  /**
   * The default constructor for {@link AddDelayFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public AddDelayFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Add Delay";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Add a Delay to the Fifo";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
   */
  @Override
  public boolean canExecute(final ICustomContext context) {
    // allow if exactly one pictogram element
    // representing an Fifo is selected
    boolean ret = false;
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length == 1)) {
      final Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof Fifo) {
        // Check that the Fifo has no existing delay
        if (((Fifo) bo).getDelay() == null) {
          ret = true;
        }
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
   */
  @Override
  public void execute(final ICustomContext context) {
    // Recheck if the execution is possible (probably useless)
    if (!canExecute(context)) {
      return;
    }
    createdPEs = new LinkedList<>();
    // Get the Fifo
    final PictogramElement[] pes = context.getPictogramElements();
    final FreeFormConnection connection = (FreeFormConnection) pes[0];
    final Fifo fifo = (Fifo) getBusinessObjectForPictogramElement(connection);

    // Create the Delay and add it to the Fifo
    final Delay delay = PiMMFactory.eINSTANCE.createDelay();
    fifo.setDelay(delay);

    // Get the GaService
    final IGaService gaService = Graphiti.getGaService();
    // Get the PeCreateService
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();

    // Get the target Diagram
    final Diagram targetDiagram = getDiagram();
    final ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

    // Create a graphical representation for the Delay
    Ellipse ellipse;
    {
      ellipse = gaService.createEllipse(containerShape);
      ellipse.setBackground(manageColor(AddActorFeature.ACTOR_FOREGROUND));
      ellipse.setForeground(manageColor(AddActorFeature.ACTOR_FOREGROUND));
      ellipse.setLineWidth(1);
      ellipse.setLineVisible(false);
      gaService.setLocationAndSize(ellipse, context.getX() - (AddDelayFeature.DELAY_SIZE / 2), context.getY() - (AddDelayFeature.DELAY_SIZE / 2),
          AddDelayFeature.DELAY_SIZE, AddDelayFeature.DELAY_SIZE);
    }
    link(containerShape, delay);
    createdPEs.add(containerShape);

    // Add a ChopBoxAnchor for the Delay
    final ChopboxAnchor cba = peCreateService.createChopboxAnchor(containerShape);
    link(cba, delay);
    createdPEs.add(cba);

    final int posX = context.getX();
    final int posY = context.getY();

    // Connect the polyline to the delay appropriately
    connectDelayToFifo(connection, fifo, containerShape, cba, posX, posY);

    // Select the whole fifo
    getDiagramBehavior().getDiagramContainer().setPictogramElementForSelection(containerShape);

  }

  /**
   * Connect delay to fifo.
   *
   * @param connection
   *          the connection
   * @param fifo
   *          the fifo
   * @param containerShape
   *          the container shape
   * @param cba
   *          the cba
   * @param posX
   *          the pos X
   * @param posY
   *          the pos Y
   */
  public void connectDelayToFifo(final FreeFormConnection connection, final Fifo fifo, final ContainerShape containerShape, final ChopboxAnchor cba,
      final int posX, final int posY) {

    final IGaService gaService = Graphiti.getGaService();

    // Create a list of all points of the connection (including source
    // and target anchor)
    List<Point> points;
    {
      final IPeLayoutService peLayoutService = Graphiti.getPeLayoutService();

      final ILocation srcLoc = peLayoutService.getLocationRelativeToDiagram(connection.getStart());
      final Point pSrc = gaService.createPoint(srcLoc.getX(), srcLoc.getY());
      final ILocation tgtLoc = peLayoutService.getLocationRelativeToDiagram(connection.getEnd());
      final Point pTgt = gaService.createPoint(tgtLoc.getX(), tgtLoc.getY());
      points = new ArrayList<>(connection.getBendpoints());
      points.add(0, pSrc);
      points.add(pTgt);
    }

    // Identify between which pair of points the delay was created
    double smallestDist = Double.MAX_VALUE;
    // Point pBefore = points.get(0);
    Point pAfter = points.get(points.size() - 1);
    for (int i = 0; i < (points.size() - 1); i++) {
      final Point p1 = points.get(i);
      final Point p2 = points.get(i + 1);

      // Distance of the point to the line
      final double distP1 = Math.sqrt(Math.pow(posX - p1.getX(), 2) + Math.pow(posY - p1.getY(), 2));
      final double distP2 = Math.sqrt(Math.pow(posX - p2.getX(), 2) + Math.pow(posY - p2.getY(), 2));
      final double distP1P2 = Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));

      if ((distP1 <= distP1P2) && (distP2 <= distP1P2)) {
        // line equation ax+by+c=0
        final int a = p2.getY() - p1.getY();
        final int b = p1.getX() - p2.getX();
        final int c = -((b * p1.getY()) + (a * p1.getX()));

        // Distance of the point to the line
        final double dist = Math.abs((a * posX) + (b * posY) + c) / Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));

        if (dist < smallestDist) {
          smallestDist = dist;
          // pBefore = p1;
          pAfter = p2;
        }
      }
    }

    // Create a list of preceding and succeeding points.
    final List<Point> precedingPoints = new ArrayList<>(points.subList(0, points.indexOf(pAfter)));
    precedingPoints.remove(0); // remove the anchor point from the list
    connection.getBendpoints().removeAll(precedingPoints);

    // Create the new connection and its polyline
    final FreeFormConnection preConnection = Graphiti.getPeCreateService().createFreeFormConnection(getDiagram());
    preConnection.setStart(connection.getStart());
    preConnection.setEnd(cba);
    preConnection.getBendpoints().addAll(precedingPoints);
    // Create the associated Polyline
    final Polyline polyline = gaService.createPolyline(preConnection);
    polyline.setLineWidth(2);
    polyline.setForeground(manageColor(AddFifoFeature.FIFO_FOREGROUND));
    link(preConnection, fifo);

    // Reconnect the original connection
    connection.setStart(cba);
  }

  public List<PictogramElement> getCreatedPEs() {
    return createdPEs;
  }
}

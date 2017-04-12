/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * The Class AddForkActorFeature.
 */
public class AddForkActorFeature extends AbstractAddFeature {

  /** The Constant FORK_ACTOR_TEXT_FOREGROUND. */
  public static final IColorConstant FORK_ACTOR_TEXT_FOREGROUND = IColorConstant.BLACK;

  /** The Constant FORK_ACTOR_FOREGROUND. */
  public static final IColorConstant FORK_ACTOR_FOREGROUND = new ColorConstant(100, 100, 100); // Grey
  // 98, 131, 167); // Blue

  /** The Constant FORK_ACTOR_BACKGROUND. */
  public static final IColorConstant FORK_ACTOR_BACKGROUND = new ColorConstant(255, 165, 79);

  // 187, 218, 247); // Parameter
  // 237, 237, 237); // Actor

  /**
   * Instantiates a new adds the fork actor feature.
   *
   * @param fp
   *          the fp
   */
  public AddForkActorFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public boolean canAdd(final IAddContext context) {
    // Check that the user wants to add an Actor to the Diagram
    return (context.getNewObject() instanceof ForkActor) && (context.getTargetContainer() instanceof Diagram);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public PictogramElement add(final IAddContext context) {
    final ForkActor addedActor = (ForkActor) context.getNewObject();
    final Diagram targetDiagram = (Diagram) context.getTargetContainer();

    // CONTAINER SHAPE WITH ROUNDED RECTANGLE
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

    // define a default size for the shape
    final int width = 70;
    final int height = 50;
    final IGaService gaService = Graphiti.getGaService();

    RoundedRectangle roundedRectangle; // need to access it later
    {
      // create and set graphics algorithm
      roundedRectangle = gaService.createRoundedRectangle(containerShape, 5, 5);
      roundedRectangle.setForeground(manageColor(AddForkActorFeature.FORK_ACTOR_FOREGROUND));
      roundedRectangle.setBackground(manageColor(AddForkActorFeature.FORK_ACTOR_BACKGROUND));
      roundedRectangle.setLineWidth(2);
      gaService.setLocationAndSize(roundedRectangle, context.getX(), context.getY(), width, height);

      // if added Class has no resource we add it to the resource
      // of the diagram
      // in a real scenario the business model would have its own resource
      if (addedActor.eResource() == null) {
        final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
        graph.getVertices().add(addedActor);
      }
      // create link and wire it
      link(containerShape, addedActor);
    }

    // Name of the actor - SHAPE WITH TEXT
    {
      // create shape for text
      final Shape shape = peCreateService.createShape(containerShape, false);

      // create and set text graphics algorithm
      final Text text = gaService.createText(shape, addedActor.getName());
      text.setForeground(manageColor(AddForkActorFeature.FORK_ACTOR_TEXT_FOREGROUND));
      text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
      // vertical alignment has as default value "center"
      text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
      gaService.setLocationAndSize(text, 0, 0, width, 20);

      // create link and wire it
      link(shape, addedActor);
    }

    // Add a ChopBoxAnchor for the actor
    // this ChopBoxAnchor is used to create connection from an actor to
    // another rather than between ports (output and input ports are then
    // created)
    final ChopboxAnchor cba = peCreateService.createChopboxAnchor(containerShape);
    link(cba, addedActor);

    // Call the layout feature
    layoutPictogramElement(containerShape);

    return containerShape;
  }

}

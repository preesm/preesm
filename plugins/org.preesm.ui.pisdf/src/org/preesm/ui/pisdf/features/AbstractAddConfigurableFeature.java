/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2024) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
import org.eclipse.graphiti.util.IColorConstant;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.PiGraph;

public abstract class AbstractAddConfigurableFeature extends AbstractAddFeature {

  protected AbstractAddConfigurableFeature(final IFeatureProvider fp) {
    super(fp);
  }

  abstract int getDefaultWidth();

  abstract int getDefaultHeight();

  abstract IColorConstant getForegroundColor();

  abstract IColorConstant getBackgroundColor();

  abstract IColorConstant getTextForegroundColor();

  public PictogramElement add(final IAddContext context) {
    final Configurable addedActor = (Configurable) context.getNewObject();
    final Diagram targetDiagram = (Diagram) context.getTargetContainer();

    // CONTAINER SHAPE WITH ROUNDED RECTANGLE
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

    // Create and link shape UI element
    createConfigurableShape(context, containerShape);

    // Create and link text UI element
    createConfigurableText(context, containerShape);

    // Add a ChopBoxAnchor for the actor
    // this ChopBoxAnchor is used to create connection from an actor to
    // another rather than between ports (output and input ports are then created)
    final ChopboxAnchor cba = peCreateService.createChopboxAnchor(containerShape);
    link(cba, addedActor);

    // Call the layout feature
    layoutPictogramElement(containerShape);
    return containerShape;
  }

  // This method is actually only for executable actors, and need to be overridden for other configurable
  void createConfigurableShape(IAddContext context, ContainerShape containerShape) {

    final ExecutableActor addedActor = (ExecutableActor) context.getNewObject();
    final IGaService gaService = Graphiti.getGaService();

    final int width = getDefaultWidth();
    final int height = getDefaultHeight();

    // create and set graphics algorithm
    final RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(containerShape, 5, 5);
    roundedRectangle.setForeground(manageColor(getForegroundColor()));
    roundedRectangle.setBackground(manageColor(getBackgroundColor()));
    roundedRectangle.setLineWidth(2);
    gaService.setLocationAndSize(roundedRectangle, context.getX(), context.getY(), width, height);

    // if added Class has no resource we add it to the resource of the diagram
    // in a real scenario the business model would have its own resource
    if (addedActor.eResource() == null) {
      final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
      graph.addActor(addedActor);
    }
    // create link and wire it
    link(containerShape, addedActor);
  }

  void createConfigurableText(IAddContext context, ContainerShape containerShape) {

    final ExecutableActor addedActor = (ExecutableActor) context.getNewObject();

    final IGaService gaService = Graphiti.getGaService();
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();

    final int width = getDefaultWidth();

    // create shape for text
    final Shape shape = peCreateService.createShape(containerShape, false);

    // create and set text graphics algorithm
    final Text text = gaService.createText(shape, addedActor.getName());
    text.setForeground(manageColor(getTextForegroundColor()));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);

    // vertical alignment has as default value "center"
    text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
    gaService.setLocationAndSize(text, 0, 0, width, 20);

    // create link and wire it
    link(shape, addedActor);
  }

}

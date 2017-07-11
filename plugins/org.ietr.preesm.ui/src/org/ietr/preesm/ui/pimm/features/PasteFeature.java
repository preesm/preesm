package org.ietr.preesm.ui.pimm.features;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.util.VertexNameValidator;

/**
 * Graphiti feature that implements the Paste feature for PiMM Vertices. Creates a new copy of the PiMM element recursively, and insert pictogram elements for
 * vertices and children elements.
 *
 * @author anmorvan
 *
 */
public class PasteFeature extends AbstractPasteFeature {

  public PasteFeature(final IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public void paste(final IPasteContext context) {
    // get the EClasses from the clipboard without copying them
    // (only copy the pictogram element, not the business object)
    // then create new pictogram elements using the add feature
    final Object[] objects = getFromClipboard();
    for (final Object object : objects) {
      if (object instanceof AbstractVertex) {
        final AbstractVertex vertex = (AbstractVertex) object;

        final AbstractVertex copy = PiMMUserFactory.instance.copy(vertex);
        final String name = computeUniqueNameForCopy(vertex);
        copy.setName(name);

        addGraphicalElementsForCopy(context, copy);
      }
    }
  }

  private String computeUniqueNameForCopy(final AbstractVertex vertex) {
    final Diagram diagram = getDiagram();
    final PiGraph pigraph = (PiGraph) getBusinessObjectForPictogramElement(diagram);
    final VertexNameValidator vertexNameValidator = new VertexNameValidator(pigraph, null);
    int i = 0;
    String name = vertex.getName();
    while (vertexNameValidator.isValid(name) != null) {
      name = vertex.getName() + "_" + (i++);
    }
    return name;
  }

  private void addGraphicalElementsForCopy(final IPasteContext context, final AbstractVertex vertexModelCopy) {
    final AddContext addCtxt = new AddContext();
    final Diagram diagram = getDiagram();
    // For simplicity paste all objects at the location given in the
    // context (no stacking or similar)
    addCtxt.setLocation(context.getX(), context.getY());
    addCtxt.setTargetContainer(diagram);
    final PictogramElement newVertexPE = addGraphicalRepresentation(addCtxt, vertexModelCopy);

    final TreeIterator<EObject> vertexChildrenElements = vertexModelCopy.eAllContents();

    vertexChildrenElements.forEachRemaining(childElement -> {
      if (childElement instanceof Port) {
        final Port copiedPort = (Port) childElement;
        final String portKind = copiedPort.getKind();
        final AbstractAddActorPortFeature addPortFeature;
        switch (portKind) {
          case "input":
            addPortFeature = new AddDataInputPortFeature(getFeatureProvider());
            break;
          case "output":
            addPortFeature = new AddDataOutputPortFeature(getFeatureProvider());
            break;
          case "cfg_input":
            addPortFeature = new AddConfigInputPortFeature(getFeatureProvider());
            break;
          case "cfg_output":
            addPortFeature = new AddConfigOutputPortFeature(getFeatureProvider());
            break;
          default:
            throw new UnsupportedOperationException("Port kind [" + portKind + "] not supported.");
        }
        addPortFeature.addPictogramElement(newVertexPE, copiedPort);
      } else {
        addGraphicalRepresentation(addCtxt, childElement);
      }
    });
  }

  @Override
  public boolean canPaste(final IPasteContext context) {
    // can paste, if all objects on the clipboard are EClasses
    final Object[] fromClipboard = getFromClipboard();
    if ((fromClipboard == null) || (fromClipboard.length == 0)) {
      return false;
    }
    for (final Object object : fromClipboard) {
      if (!(object instanceof AbstractVertex)) {
        return false;
      }
    }
    return true;
  }

}

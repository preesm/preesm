package org.ietr.preesm.ui.pimm.features;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
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

  private final PiGraph getPiGraph() {
    final Diagram diagram = getDiagram();
    return (PiGraph) getBusinessObjectForPictogramElement(diagram);
  }

  private final Map<EObject, PictogramElement> links = new LinkedHashMap<>();

  @Override
  public void paste(final IPasteContext context) {
    this.links.clear();
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

        autoConnectInputConfigPorts(vertex, copy);
      }
    }

    this.links.clear();
  }

  private void autoConnectInputConfigPorts(final AbstractVertex originalVertex, final AbstractVertex vertexCopy) {

    if (originalVertex.eContainer() != vertexCopy.eContainer()) {
      return;
    }

    final PiGraph pigraph = getPiGraph();
    final EList<Dependency> dependencies = pigraph.getDependencies();

    final List<Dependency> newDependencies = new LinkedList<>();
    for (final Dependency dep : dependencies) {
      final ConfigInputPort getter = dep.getGetter();
      if (originalVertex.getConfigInputPorts().contains(getter)) {
        final ISetter setter = dep.getSetter();
        final ConfigInputPort getterCopy = lookupInputConfigPort(vertexCopy, getter);

        final Dependency newDep = PiMMUserFactory.instance.createDependency(setter, getterCopy);

        // check names after creating the dependency (ConfigInputPort.getName() lookup dependency.getSetter()).
        final String copiedName = getterCopy.getName();
        final String origName = getter.getName();
        if (((copiedName != null) && !(copiedName.equals(origName))) || ((copiedName == null) && (origName != null))) {
          throw new IllegalStateException();
        }

        newDependencies.add(newDep);
      }
    }

    for (final Dependency newDep : newDependencies) {
      dependencies.add(newDep);
      addGraphicalRepresentationForNewDependency(newDep);
    }
  }

  private void addGraphicalRepresentationForNewDependency(final Dependency newDep) {
    // getter should be a ConfigInputPort
    final ConfigInputPort getter = newDep.getGetter();
    final Anchor getterPE = (Anchor) findPE(getter);

    // setter is either a Parameter or ConfigOutputPort
    final ISetter setter = newDep.getSetter();
    final Anchor setterPE;
    if (setter instanceof ConfigInputInterface) {
      setterPE = (Anchor) findPE(setter);
    } else if (setter instanceof Parameter) {
      final PictogramElement pe = findPE(setter);
      if (pe instanceof Anchor) {
        setterPE = (Anchor) pe;
      } else {
        final ContainerShape findPE = (ContainerShape) pe;
        final EList<Anchor> anchors = findPE.getAnchors();
        if ((anchors == null) || (anchors.size() != 1)) {
          throw new IllegalStateException();
        }
        setterPE = anchors.get(0);
      }
    } else if (setter instanceof ConfigOutputPort) {
      setterPE = (Anchor) findPE(setter);
    } else {
      throw new UnsupportedOperationException();
    }

    final AddConnectionContext addCtxt = new AddConnectionContext(setterPE, getterPE);
    addCtxt.setNewObject(newDep);
    final IAddFeature addFeature = getFeatureProvider().getAddFeature(addCtxt);
    getDiagramBehavior().executeFeature(addFeature, addCtxt);
  }

  private PictogramElement findPE(final EObject businessObject) {
    if (this.links.containsKey(businessObject)) {
      return this.links.get(businessObject);
    }

    final PictogramElement boPEs = getFeatureProvider().getPictogramElementForBusinessObject(businessObject);
    if (boPEs == null) {
      final String message = "Business objcet [" + businessObject + "] has no graphical representations (several PictogramElements) : \n";
      throw new IllegalStateException(message);
    }
    return boPEs;
  }

  /**
   * Lookup the copied getter in the vertex copy. The lookup is based on the name only.
   *
   * @param vertexCopy
   *          vertex copy
   * @param getter
   *          input port in the original vertex
   * @return the vertexCopy's input port whose name matches getter
   */
  private ConfigInputPort lookupInputConfigPort(final AbstractVertex vertexCopy, final ConfigInputPort getter) {
    final EList<ConfigInputPort> copiedConfigInputPorts = vertexCopy.getConfigInputPorts();
    final Parameterizable eContainer = (Parameterizable) getter.eContainer();
    final EList<ConfigInputPort> origConfigInputPorts = eContainer.getConfigInputPorts();

    if (copiedConfigInputPorts.size() != origConfigInputPorts.size()) {
      throw new IllegalStateException();
    }

    final ConfigInputPort target;
    if (origConfigInputPorts.contains(getter)) {
      final int indexOf = origConfigInputPorts.indexOf(getter);
      target = copiedConfigInputPorts.get(indexOf);
    } else {
      throw new IllegalStateException();
    }

    return target;
  }

  private String computeUniqueNameForCopy(final AbstractVertex vertex) {
    final PiGraph pigraph = getPiGraph();
    final VertexNameValidator vertexNameValidator = new VertexNameValidator(pigraph, null);
    int i = 0;
    String name = vertex.getName();
    while (vertexNameValidator.isValid(name) != null) {
      name = vertex.getName() + "_" + (i++);
    }
    return name;
  }

  /**
   * Add graphical representation for the vertex copy and its content (that is the input/output ports/configs)
   */
  private PictogramElement addGraphicalElementsForCopy(final IPasteContext context, final AbstractVertex vertexModelCopy) {
    final AddContext addCtxt = new AddContext();
    final Diagram diagram = getDiagram();
    // For simplicity paste all objects at the location given in the
    // context (no stacking or similar)
    addCtxt.setLocation(context.getX(), context.getY());
    addCtxt.setTargetContainer(diagram);
    final PictogramElement newVertexPE = addGraphicalRepresentation(addCtxt, vertexModelCopy);
    this.links.put(vertexModelCopy, newVertexPE);

    final TreeIterator<EObject> vertexChildrenElements = vertexModelCopy.eAllContents();

    vertexChildrenElements.forEachRemaining(childElement -> {
      final PictogramElement pe;
      if (childElement instanceof Port) {
        final Port copiedPort = (Port) childElement;
        final String portKind = copiedPort.getKind();
        if (vertexModelCopy instanceof ExecutableActor) {
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
          pe = addPortFeature.addPictogramElement(newVertexPE, copiedPort);
        } else {
          pe = GraphitiUi.getPeService().getChopboxAnchor((AnchorContainer) newVertexPE);
          if (pe == null) {
            throw new IllegalStateException();
          }
        }
      } else {
        pe = addGraphicalRepresentation(addCtxt, childElement);
      }
      this.links.put(childElement, pe);
    });
    return newVertexPE;
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

package org.ietr.preesm.ui.pimm.features;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
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

  private PiGraph getOriginalPiGraph() {
    PiGraph result = null;
    final Object[] originalObjects = getFromClipboard();
    for (Object o : originalObjects) {
      if (o instanceof AbstractVertex) {
        final EObject eContainer = ((AbstractVertex) o).eContainer();
        if (!(eContainer instanceof PiGraph) || (result != null && result != eContainer)) {
          // all vertices should be contained by a PiGraph object.
          throw new IllegalStateException("Paste elements that have been deleted is not supported");
        } else {
          result = (PiGraph) eContainer;
        }
      }
    }
    if (result == null) {
      throw new IllegalStateException();
    }
    return result;
  }

  private final Map<EObject, PictogramElement>        links         = new LinkedHashMap<>();
  private final Map<Parameterizable, Parameterizable> copiedObjects = new LinkedHashMap<>();

  @Override
  public void paste(final IPasteContext context) {
    this.links.clear();
    this.copiedObjects.clear();
    // get the EClasses from the clipboard without copying them
    // (only copy the pictogram element, not the business object)
    // then create new pictogram elements using the add feature
    final Object[] objects = getFromClipboard();
    for (final Object object : objects) {
      if (object instanceof AbstractVertex) {
        final AbstractVertex vertex = (AbstractVertex) object;

        final PictogramElement[] allPictogramElementsForBusinessObject = getFeatureProvider().getAllPictogramElementsForBusinessObject(vertex);

        final AbstractVertex copy = PiMMUserFactory.instance.copy(vertex);
        final String name = computeUniqueNameForCopy(vertex);
        copy.setName(name);
        addGraphicalElementsForCopy(context, copy, allPictogramElementsForBusinessObject);
        this.copiedObjects.put(vertex, copy);

        autoConnectInputConfigPorts(vertex, copy);
      }
    }

    connectFifos(context);

    if (getPiGraph() != getOriginalPiGraph()) {
      connectDependencies();
    }

    postProcess();

    this.copiedObjects.clear();
    this.links.clear();
  }

  private void postProcess() {
    for (Entry<Parameterizable, Parameterizable> e : copiedObjects.entrySet()) {
      final Parameterizable value = e.getValue();
      if (value instanceof ExecutableActor) {
        // continue
      } else {
        final EList<ConfigInputPort> configInputPorts = value.getConfigInputPorts();
        List<ConfigInputPort> portsToRemove = new LinkedList<>();
        for (ConfigInputPort port : configInputPorts) {
          if (port.getIncomingDependency() == null) {
            portsToRemove.add(port);
          }
        }
        configInputPorts.removeAll(portsToRemove);
      }
    }
  }

  private void connectDependencies() {
    final List<Dependency> originalDependencies = getOriginalPiGraph().getDependencies();

    final PiGraph targetPiGraph = getPiGraph();

    for (final Dependency dep : originalDependencies) {
      final ISetter setter = dep.getSetter();
      final ConfigInputPort getter = dep.getGetter();
      final Parameterizable targetParameterizable = (Parameterizable) getter.eContainer();
      final boolean shouldConnectDep = shouldConnectDep(setter, targetParameterizable);
      if (shouldConnectDep) {
        connectDep(targetPiGraph, setter, getter, targetParameterizable);
      }
    }
  }

  private void connectDep(final PiGraph targetPiGraph, final ISetter setter, final ConfigInputPort getter, final Parameterizable targetParameterizable) {
    final Parameterizable copiedParameterizable = copiedObjects.get(targetParameterizable);
    // lookup copied setter
    ISetter copiedSetter = null;
    if (setter instanceof Parameter) {
      copiedSetter = (Parameter) copiedObjects.get(setter);
    } else if (setter instanceof ConfigOutputPort) {
      final AbstractActor originalActor = (AbstractActor) setter.eContainer();
      final ConfigOutputPort originalConfigPort = (ConfigOutputPort) setter;
      final AbstractActor copiedActor = (AbstractActor) copiedObjects.get(originalActor);
      final ConfigOutputPort lookupConfigOutputPort = lookupConfigOutputPort(copiedActor, originalConfigPort);
      copiedSetter = lookupConfigOutputPort;
    } else {
      throw new IllegalStateException();
    }

    final ConfigInputPort copiedConfigInputPort = lookupConfigInputPort(copiedParameterizable, getter);
    final Dependency newDep = PiMMUserFactory.instance.createDependency(copiedSetter, copiedConfigInputPort);
    targetPiGraph.getDependencies().add(newDep);
    addGraphicalRepresentationForNewDependency(newDep);
  }

  private boolean shouldConnectDep(final ISetter setter, final Parameterizable targetParameterizable) {
    final boolean sourceOk;
    if (setter instanceof Parameter) {
      sourceOk = copiedObjects.containsKey(setter);
    } else if (setter instanceof ConfigOutputPort) {
      sourceOk = copiedObjects.containsKey(setter.eContainer());
    } else {
      sourceOk = false;
    }

    final boolean targetOk;
    if (targetParameterizable instanceof AbstractVertex) {
      targetOk = copiedObjects.containsKey(targetParameterizable);
    } else if (targetParameterizable instanceof Delay) {
      final Fifo fifo = (Fifo) targetParameterizable.eContainer();
      final EObject fifoSource = fifo.getSourcePort().eContainer();
      final EObject fifoTarget = fifo.getTargetPort().eContainer();
      targetOk = copiedObjects.containsKey(fifoSource) && copiedObjects.containsKey(fifoTarget);
    } else {
      targetOk = false;
    }
    return sourceOk && targetOk;
  }

  private void connectFifos(IPasteContext pasteContext) {
    final EList<Fifo> originalFifos = getOriginalPiGraph().getFifos();
    final PiGraph targetPiGraph = getPiGraph();

    final Map<Fifo, Fifo> newFifos = new LinkedHashMap<>();

    copyFifos(originalFifos, newFifos);

    addFifos(pasteContext, targetPiGraph, newFifos);
  }

  private void addFifos(IPasteContext pasteContext, final PiGraph targetPiGraph, final Map<Fifo, Fifo> newFifos) {
    for (Entry<Fifo, Fifo> fifoEntry : newFifos.entrySet()) {
      final Fifo copiedFifo = fifoEntry.getKey();
      final Fifo originalFifo = fifoEntry.getValue();
      targetPiGraph.getFifos().add(copiedFifo);

      final Anchor sourceAnchor = (Anchor) findPE(copiedFifo.getSourcePort());
      final Anchor targetAnchor = (Anchor) findPE(copiedFifo.getTargetPort());
      final AddConnectionContext context = new AddConnectionContext(sourceAnchor, targetAnchor);
      context.setNewObject(copiedFifo);

      final AddFifoFeature addFifoFeature = new AddFifoFeature(getFeatureProvider());
      addFifoFeature.execute(context);
      final PictogramElement pictogramElementForBusinessObject = getFeatureProvider().getPictogramElementForBusinessObject(copiedFifo);

      final Delay delay = originalFifo.getDelay();
      if (delay != null) {
        copyDelay(pasteContext, copiedFifo, pictogramElementForBusinessObject, delay);
      }

    }
  }

  private void copyDelay(IPasteContext pasteContext, final Fifo copiedFifo, final PictogramElement pictogramElementForBusinessObject, final Delay delay) {
    final Delay delayCopy = PiMMUserFactory.instance.copy(delay);
    final AddDelayFeature addDelayFeature = new AddDelayFeature(getFeatureProvider());
    final CustomContext customContext = new CustomContext(new PictogramElement[] { pictogramElementForBusinessObject });
    customContext.setLocation(pasteContext.getX(), pasteContext.getY());
    addDelayFeature.execute(customContext);
    // one delay is created during the addDelayFeature.
    // Force reference to the one created above ?
    // check config input ports...
    copiedFifo.setDelay(delayCopy);
    // and overwrite links
    final List<PictogramElement> createdPEs = addDelayFeature.getCreatedPEs();
    for (PictogramElement pe : createdPEs) {
      pe.getLink().getBusinessObjects().clear();
      pe.getLink().getBusinessObjects().add(delayCopy);
    }
    // add input port anchors
    final EList<ConfigInputPort> configInputPorts = delayCopy.getConfigInputPorts();
    for (ConfigInputPort port : configInputPorts) {
      final IPeService peService = GraphitiUi.getPeService();
      final Anchor chopboxAnchor = peService.getChopboxAnchor((AnchorContainer) createdPEs.get(0));
      chopboxAnchor.setReferencedGraphicsAlgorithm(createdPEs.get(0).getGraphicsAlgorithm());
      this.links.put(port, chopboxAnchor);

    }
    autoConnectInputConfigPorts(delay, delayCopy);
    copiedObjects.put(delay, delayCopy);
  }

  private void copyFifos(final EList<Fifo> originalFifos, final Map<Fifo, Fifo> newFifos) {
    for (final Fifo fifo : originalFifos) {
      final DataOutputPort sourcePort = fifo.getSourcePort();
      final DataInputPort targetPort = fifo.getTargetPort();
      final EObject sourceVertex = sourcePort.eContainer();
      final EObject targetVertex = targetPort.eContainer();
      if ((sourceVertex != null && (sourceVertex instanceof AbstractActor)) && (targetVertex != null && (targetVertex instanceof AbstractActor))) {
        // ok
        AbstractActor source = (AbstractActor) sourceVertex;
        AbstractActor target = (AbstractActor) targetVertex;
        if (copiedObjects.containsKey(source) && copiedObjects.containsKey(target)) {
          final AbstractActor sourceCopy = (AbstractActor) copiedObjects.get(source);
          final AbstractActor targetCopy = (AbstractActor) copiedObjects.get(target);

          final DataOutputPort sourcePortCopy = lookupDataOutputPort(sourceCopy, sourcePort);
          final DataInputPort targetPortCopy = lookupDataInputPort(targetCopy, targetPort);

          final Fifo copiedFifo = PiMMUserFactory.instance.createFifo(sourcePortCopy, targetPortCopy, fifo.getType());
          newFifos.put(copiedFifo, fifo);

        }
      } else {
        // not supported
        throw new UnsupportedOperationException();
      }
    }
  }

  private void autoConnectInputConfigPorts(final Parameterizable originalParameterizable, final Parameterizable parameterizableCopy) {

    if (getPiGraph() != getOriginalPiGraph()) {
      return;
    }

    final PiGraph pigraph = getPiGraph();
    final EList<Dependency> dependencies = pigraph.getDependencies();

    final List<Dependency> newDependencies = new LinkedList<>();
    for (final Dependency dep : dependencies) {
      final ConfigInputPort getter = dep.getGetter();
      if (originalParameterizable.getConfigInputPorts().contains(getter)) {
        final ISetter setter = dep.getSetter();
        final ConfigInputPort getterCopy = lookupConfigInputPort(parameterizableCopy, getter);
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
  private ConfigInputPort lookupConfigInputPort(final Parameterizable vertexCopy, final ConfigInputPort getter) {
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

  /**
   * Lookup the copied getter in the vertex copy. The lookup is based on the name only.
   *
   * @param vertexCopy
   *          vertex copy
   * @param getter
   *          input port in the original vertex
   * @return the vertexCopy's input port whose name matches getter
   */
  private ConfigOutputPort lookupConfigOutputPort(final AbstractActor vertexCopy, final ConfigOutputPort setter) {
    final EList<ConfigOutputPort> copiedConfigOutputPorts = vertexCopy.getConfigOutputPorts();
    final AbstractActor eContainer = (AbstractActor) setter.eContainer();
    final EList<ConfigOutputPort> origConfigOutputPorts = eContainer.getConfigOutputPorts();

    if (copiedConfigOutputPorts.size() != origConfigOutputPorts.size()) {
      throw new IllegalStateException();
    }

    final ConfigOutputPort source;
    if (origConfigOutputPorts.contains(setter)) {
      final int indexOf = origConfigOutputPorts.indexOf(setter);
      source = copiedConfigOutputPorts.get(indexOf);
    } else {
      throw new IllegalStateException();
    }

    return source;
  }

  private DataInputPort lookupDataInputPort(final AbstractActor vertexCopy, final DataInputPort getter) {
    final EList<DataInputPort> copiedConfigInputPorts = vertexCopy.getDataInputPorts();
    final AbstractActor eContainer = (AbstractActor) getter.eContainer();
    final EList<DataInputPort> origConfigInputPorts = eContainer.getDataInputPorts();

    if (copiedConfigInputPorts.size() != origConfigInputPorts.size()) {
      throw new IllegalStateException();
    }

    final DataInputPort target;
    if (origConfigInputPorts.contains(getter)) {
      final int indexOf = origConfigInputPorts.indexOf(getter);
      target = copiedConfigInputPorts.get(indexOf);
    } else {
      throw new IllegalStateException();
    }

    return target;
  }

  private DataOutputPort lookupDataOutputPort(final AbstractActor vertexCopy, final DataOutputPort getter) {
    final EList<DataOutputPort> copiedConfigInputPorts = vertexCopy.getDataOutputPorts();
    final AbstractActor eContainer = (AbstractActor) getter.eContainer();
    final EList<DataOutputPort> origConfigInputPorts = eContainer.getDataOutputPorts();

    if (copiedConfigInputPorts.size() != origConfigInputPorts.size()) {
      throw new IllegalStateException();
    }

    final DataOutputPort target;
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
  private void addGraphicalElementsForCopy(final IPasteContext context, final AbstractVertex vertexModelCopy,
      final PictogramElement[] allPictogramElementsForBusinessObject) {
    final AddContext addCtxt = new AddContext();
    final Diagram diagram = getDiagram();
    // For simplicity paste all objects at the location given in the
    // context (no stacking or similar)
    // TODO: improve location

    // final PictogramElement pictogramElement = allPictogramElementsForBusinessObject[0];

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
        } else if (vertexModelCopy instanceof InterfaceActor) {
          // the AddIn/OutInterfaceFeature creates an anchor for the only in/out port and place it first in the list
          final EList<Anchor> anchors = ((AnchorContainer) newVertexPE).getAnchors();
          if (!anchors.isEmpty()) {
            pe = anchors.get(0);
          } else {
            throw new IllegalStateException();
          }
        } else {
          final IPeService peService = GraphitiUi.getPeService();
          final Anchor chopboxAnchor = peService.getChopboxAnchor((AnchorContainer) newVertexPE);
          chopboxAnchor.setReferencedGraphicsAlgorithm(newVertexPE.getGraphicsAlgorithm());
          pe = chopboxAnchor;
        }
      } else {
        pe = addGraphicalRepresentation(addCtxt, childElement);
      }
      this.links.put(childElement, pe);
    });
  }

  @Override
  public boolean canPaste(final IPasteContext context) {
    // can paste, if all objects on the clipboard are Vertices
    final Object[] fromClipboard = getFromClipboard();
    if ((fromClipboard == null) || (fromClipboard.length == 0)) {
      return false;
    }
    boolean hasOneVertex = false;
    for (final Object object : fromClipboard) {
      final boolean objectIsVertex = object instanceof AbstractVertex;
      if (objectIsVertex) {
        hasOneVertex = true;
      } else {
        if (!(object instanceof Delay)) {
          return false;
        }
      }
    }
    return hasOneVertex;
  }

}

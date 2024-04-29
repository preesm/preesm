/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2020) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Parameterizable;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.ui.pisdf.features.CopyFeature.VertexCopy;
import org.preesm.ui.pisdf.util.VertexNameValidator;

/**
 * Graphiti feature that implements the Paste feature for PiMM Vertices. Creates a new copy of the PiMM element
 * recursively, and insert pictogram elements for vertices and children elements.
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
    final Object[] originalObjects = getFromClipboard();

    return Arrays.stream(originalObjects).filter(VertexCopy.class::isInstance)
        .map(o -> ((VertexCopy) o).getOriginalPiGraph()).findFirst().orElseThrow(IllegalStateException::new);
  }

  private final Map<EObject, PictogramElement>  links         = new LinkedHashMap<>();
  private final Map<Configurable, Configurable> copiedObjects = new LinkedHashMap<>();

  @Override
  public void paste(final IPasteContext context) {
    this.links.clear();
    this.copiedObjects.clear();
    // get the EClasses from the clipboard without copying them
    // (only copy the pictogram element, not the business object)
    // then create new pictogram elements using the add feature
    final Object[] objects = getFromClipboard();

    final List<VertexCopy> copies = Arrays.stream(objects).filter(VertexCopy.class::isInstance).map(o -> (VertexCopy) o)
        .toList();

    final Map<VertexCopy, Pair<Integer, Integer>> calculatePositions = calculatePositions(context, copies);

    for (final VertexCopy vertexCopy : copies) {
      final Configurable vertex = vertexCopy.getOriginalVertex();

      final Configurable copy = PiMMUserFactory.instance.copy(vertex);
      final String name = computeUniqueNameForCopy(vertex);
      copy.setName(name);

      // If the vertex to copy is a Delay, creating a copy of the DelayActor and attaching it to the Delay copy
      if (vertex instanceof final Delay delay) {
        final DelayActor delayActorCopy = PiMMUserFactory.instance.copy(delay.getActor());
        delayActorCopy.setName(name); // Delay and associated DelayActor have the same name
        ((Delay) copy).setActor(delayActorCopy);
      }

      final Pair<Integer, Integer> pair = calculatePositions.get(vertexCopy);
      final Integer x = pair.getKey();
      final Integer y = pair.getValue();
      addGraphicalRepresentationForVertex(copy, x, y);
      this.copiedObjects.put(vertex, copy);
    }

    connectFifos();

    // Actual auto connection of dependencies is postponed until after Delays
    // have had their PictogramElement created and linked to the vertexCopy

    // Connect dependency if target graph == source graph
    // Connect dependency with parameter copy if it exists, otherwise to original parameter
    this.copiedObjects.forEach(this::autoConnectInputConfigPorts);

    // Connect dependency if target graph != source graph
    if (getPiGraph() != getOriginalPiGraph()) {
      connectDependencies();
    }

    postProcess();

    this.copiedObjects.clear();
    this.links.clear();
  }

  private Map<VertexCopy, Pair<Integer, Integer>> calculatePositions(final IPasteContext context,
      final List<VertexCopy> copies) {
    final Map<VertexCopy, Pair<Integer, Integer>> positions = new LinkedHashMap<>();
    // determine the surrounding box
    int maxX = Integer.MIN_VALUE;
    int minX = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE;
    for (final VertexCopy copy : copies) {
      final int originalX = copy.getOriginalX();
      final int originalY = copy.getOriginalY();
      maxX = Math.max(maxX, originalX);
      maxY = Math.max(maxY, originalY);
      minX = Math.min(minX, originalX);
      minY = Math.min(minY, originalY);
    }

    final int avgX = (maxX + minX) / 2;
    final int avgY = (maxY + minY) / 2;

    final int pasteX = context.getX();
    final int pasteY = context.getY();

    for (final VertexCopy copy : copies) {
      final int newX = pasteX + (copy.getOriginalX() - avgX);
      final int newY = pasteY + (copy.getOriginalY() - avgY);
      positions.put(copy, new Pair<>(newX, newY));
    }
    return positions;
  }

  /**
   * Post process the pasted objects. Actually remove parameter input ports that have no associated dependencies
   */
  public void postProcess() {
    for (final Entry<Configurable, Configurable> e : this.copiedObjects.entrySet()) {
      final Configurable value = e.getValue();
      if (value instanceof ExecutableActor) {
        // continue
      } else {
        final EList<ConfigInputPort> configInputPorts = value.getConfigInputPorts();
        final List<ConfigInputPort> portsToRemove = new LinkedList<>();
        for (final ConfigInputPort port : configInputPorts) {
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

  private void connectDep(final PiGraph targetPiGraph, final ISetter setter, final ConfigInputPort getter,
      final Parameterizable targetParameterizable) {
    final Configurable copiedParameterizable = this.copiedObjects.get(targetParameterizable);
    // lookup copied setter
    ISetter copiedSetter = null;
    if (setter instanceof final Parameter param) {
      copiedSetter = (Parameter) this.copiedObjects.get(param);
    } else if (setter instanceof final ConfigOutputPort cop) {
      final AbstractActor originalActor = (AbstractActor) setter.eContainer();
      final ConfigOutputPort originalConfigPort = cop;
      final AbstractActor copiedActor = (AbstractActor) this.copiedObjects.get(originalActor);
      final ConfigOutputPort lookupConfigOutputPort = lookupConfigOutputPort(copiedActor, originalConfigPort);
      copiedSetter = lookupConfigOutputPort;
    } else {
      throw new IllegalStateException();
    }

    final ConfigInputPort copiedConfigInputPort = lookupConfigInputPort(copiedParameterizable, getter);
    final Dependency newDep = PiMMUserFactory.instance.createDependency(copiedSetter, copiedConfigInputPort);
    targetPiGraph.addDependency(newDep);
    addGraphicalRepresentationForDependency(newDep);
  }

  private boolean shouldConnectDep(final ISetter setter, final Parameterizable targetParameterizable) {
    final boolean sourceOk;
    if (setter instanceof final Parameter param) {
      sourceOk = this.copiedObjects.containsKey(param);
    } else if (setter instanceof ConfigOutputPort) {
      sourceOk = this.copiedObjects.containsKey(setter.eContainer());
    } else {
      sourceOk = false;
    }

    final boolean targetOk;
    if (targetParameterizable instanceof AbstractVertex) {
      targetOk = this.copiedObjects.containsKey(targetParameterizable);
    } else if (targetParameterizable instanceof final Delay delay) {
      final Fifo fifo = delay.getContainingFifo();
      final EObject fifoSource = fifo.getSourcePort().eContainer();
      final EObject fifoTarget = fifo.getTargetPort().eContainer();
      targetOk = this.copiedObjects.containsKey(fifoSource) && this.copiedObjects.containsKey(fifoTarget);
    } else {
      targetOk = false;
    }
    return sourceOk && targetOk;
  }

  private void connectFifos() {
    final EList<Fifo> originalFifos = getOriginalPiGraph().getFifos();

    final Map<Fifo, Fifo> newFifos = new LinkedHashMap<>();

    copyFifos(originalFifos, newFifos);

    addFifos(newFifos);
  }

  private void addFifos(final Map<Fifo, Fifo> newFifos) {

    final PiGraph piGraph = getPiGraph();

    for (final Entry<Fifo, Fifo> fifoEntry : newFifos.entrySet()) {
      final Fifo copiedFifo = fifoEntry.getKey();
      final Fifo originalFifo = fifoEntry.getValue();
      piGraph.addFifo(copiedFifo);

      final FreeFormConnection addGraphicalRepresentationForFifo = addGraphicalRepresentationForFifo(copiedFifo);

      final Delay delay = originalFifo.getDelay();
      if (delay != null) {
        final Delay delayCopy = (Delay) this.copiedObjects.get(delay);
        addGraphicalRepresentationForDelay(copiedFifo, addGraphicalRepresentationForFifo, delayCopy);
      }
    }
  }

  /**
   *
   */
  public FreeFormConnection addGraphicalRepresentationForFifo(final Fifo copiedFifo) {
    final DataOutputPort sourcePort = copiedFifo.getSourcePort();
    final DataInputPort targetPort = copiedFifo.getTargetPort();
    if (targetPort == null || sourcePort == null) {
      throw new PreesmRuntimeException("The fifo [" + copiedFifo + "] is inconsistent");
    }

    final Anchor sourceAnchor = (Anchor) findPE(sourcePort);
    final Anchor targetAnchor = (Anchor) findPE(targetPort);
    final AddConnectionContext context = new AddConnectionContext(sourceAnchor, targetAnchor);
    context.setNewObject(copiedFifo);

    final AddFifoFeature addFifoFeature = new AddFifoFeature(getFeatureProvider());
    final PictogramElement add = addFifoFeature.add(context);

    return (FreeFormConnection) add;
  }

  /**
   *
   */
  public void addGraphicalRepresentationForDelay(final Fifo copiedFifo, final FreeFormConnection fifoConnection,
      final Delay delayCopy) {

    final ILocation connectionMidpoint = GraphitiUi.getPeService().getConnectionMidpoint(fifoConnection, 0.5);

    copiedFifo.setDelay(delayCopy);
    copiedFifo.getContainingPiGraph().addDelay(delayCopy);

    final AddDelayFeature addDelayFeature = new AddDelayFeature(getFeatureProvider());
    final AddContext addCtxt = new AddContext();

    final Diagram diagram = getDiagram();

    addCtxt.setTargetConnection(fifoConnection);
    addCtxt.setLocation(connectionMidpoint.getX(), connectionMidpoint.getY());
    addCtxt.setTargetContainer(diagram);
    addCtxt.setNewObject(delayCopy);

    final PictogramElement newDelayPE = addDelayFeature.add(addCtxt);
    this.links.put(delayCopy, newDelayPE);

    final List<PictogramElement> createdPEs = addDelayFeature.getCreatedPEs();

    // add input port anchors
    final EList<ConfigInputPort> configInputPorts = delayCopy.getConfigInputPorts();
    for (final ConfigInputPort port : configInputPorts) {
      final IPeService peService = GraphitiUi.getPeService();
      final Anchor chopboxAnchor = peService.getChopboxAnchor((AnchorContainer) createdPEs.get(0));
      chopboxAnchor.setReferencedGraphicsAlgorithm(createdPEs.get(0).getGraphicsAlgorithm());
      this.links.put(port, chopboxAnchor);
    }

    // add input port anchors
    final DelayActor actor = delayCopy.getActor();
    final EList<DataPort> delayPorts = actor.getAllDataPorts();
    for (final DataPort port : delayPorts) {
      final IPeService peService = GraphitiUi.getPeService();
      final Anchor chopboxAnchor = peService.getChopboxAnchor((AnchorContainer) createdPEs.get(0));
      chopboxAnchor.setReferencedGraphicsAlgorithm(createdPEs.get(0).getGraphicsAlgorithm());
      this.links.put(port, chopboxAnchor);
    }
  }

  private void copyFifos(final EList<Fifo> originalFifos, final Map<Fifo, Fifo> newFifos) {
    for (final Fifo fifo : originalFifos) {
      final DataOutputPort sourcePort = fifo.getSourcePort();
      final DataInputPort targetPort = fifo.getTargetPort();
      final EObject sourceVertex = sourcePort.eContainer();
      final EObject targetVertex = targetPort.eContainer();
      if (!(sourceVertex instanceof final AbstractActor source)
          || !(targetVertex instanceof final AbstractActor target)) {
        // not supported
        throw new UnsupportedOperationException();
      }
      // Checking if source is NOT in the list of copied AbstractActor and
      // if it is a DelayActor belonging to a Delay which is NOT in the list of copied AbstractActor
      // Checking if target is NOT in the list of copied AbstractActor and
      // if it is a DelayActor belonging to a Delay which is NOT in the list of copied AbstractActor
      if (!((this.copiedObjects.containsKey(source)) || ((source instanceof DelayActor)
          && (this.copiedObjects.containsKey(((DelayActor) source).getLinkedDelay()))))
          || !((this.copiedObjects.containsKey(target)) || ((target instanceof DelayActor)
              && (this.copiedObjects.containsKey(((DelayActor) target).getLinkedDelay()))))) {
        continue;
      }

      final AbstractActor sourceCopy;
      final AbstractActor targetCopy;

      if (source instanceof final DelayActor sourceDelayActor) {
        sourceCopy = ((Delay) this.copiedObjects.get(sourceDelayActor.getLinkedDelay())).getActor();
      } else {
        sourceCopy = (AbstractActor) this.copiedObjects.get(source);
      }

      if (target instanceof final DelayActor targetDelayActor) {
        targetCopy = ((Delay) this.copiedObjects.get(targetDelayActor.getLinkedDelay())).getActor();
      } else {
        targetCopy = (AbstractActor) this.copiedObjects.get(target);
      }

      final DataOutputPort sourcePortCopy;
      if (sourcePort instanceof final ConfigOutputPort configSourcePort) {
        sourcePortCopy = lookupConfigOutputPort(sourceCopy, configSourcePort);
      } else {
        sourcePortCopy = lookupDataOutputPort(sourceCopy, sourcePort);
      }

      final DataInputPort targetPortCopy = lookupDataInputPort(targetCopy, targetPort);

      final Fifo copiedFifo = PiMMUserFactory.instance.createFifo(sourcePortCopy, targetPortCopy, fifo.getType());
      newFifos.put(copiedFifo, fifo);
    }
  }

  private void autoConnectInputConfigPorts(final Configurable originalParameterizable,
      final Configurable parameterizableCopy) {

    if (getPiGraph() != getOriginalPiGraph()) {
      return;
    }

    final PiGraph pigraph = getPiGraph();

    final List<Dependency> newDependencies = new LinkedList<>();
    for (final Dependency dep : pigraph.getDependencies()) {
      final ConfigInputPort getter = dep.getGetter();
      if (originalParameterizable.getConfigInputPorts().contains(getter)) {
        ISetter setter = dep.getSetter();

        // If dependency setter is
        if (setter instanceof final Configurable cSetter && this.copiedObjects.containsKey(cSetter)) {
          setter = (ISetter) this.copiedObjects.get(cSetter);
        }

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
      pigraph.addDependency(newDep);
      addGraphicalRepresentationForDependency(newDep);
    }
  }

  /**
   *
   */
  public void addGraphicalRepresentationForDependency(final Dependency newDep) {
    // getter should be a ConfigInputPort
    final ConfigInputPort getter = newDep.getGetter();
    final Anchor getterPE = (Anchor) findPE(getter);

    // setter is either a Parameter or ConfigOutputPort
    final ISetter setter = newDep.getSetter();
    final Anchor setterPE;
    if ((setter instanceof ConfigInputInterface) || (setter instanceof Parameter)) {
      final PictogramElement pe = findPE(setter);
      if (pe instanceof final Anchor anchor) {
        setterPE = anchor;
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
    if (businessObject == null) {
      throw new NullPointerException();
    }
    if (this.links.containsKey(businessObject)) {
      return this.links.get(businessObject);
    }

    final PictogramElement boPEs = getFeatureProvider().getPictogramElementForBusinessObject(businessObject);
    if (boPEs == null) {
      final String message = "Business object [" + businessObject
          + "] has no graphical representations (several PictogramElements) -- " + businessObject.eContainer();
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
  private ConfigInputPort lookupConfigInputPort(final Configurable vertexCopy, final ConfigInputPort getter) {
    final EList<ConfigInputPort> copiedConfigInputPorts = vertexCopy.getConfigInputPorts();
    final Configurable eContainer = getter.getConfigurable();
    final EList<ConfigInputPort> origConfigInputPorts = eContainer.getConfigInputPorts();

    if ((copiedConfigInputPorts.size() != origConfigInputPorts.size()) || !origConfigInputPorts.contains(getter)) {
      throw new IllegalStateException();
    }
    final int indexOf = origConfigInputPorts.indexOf(getter);
    return copiedConfigInputPorts.get(indexOf);
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

    if ((copiedConfigOutputPorts.size() != origConfigOutputPorts.size()) || !origConfigOutputPorts.contains(setter)) {
      throw new IllegalStateException();
    }
    final int indexOf = origConfigOutputPorts.indexOf(setter);
    return copiedConfigOutputPorts.get(indexOf);
  }

  private DataInputPort lookupDataInputPort(final AbstractActor vertexCopy, final DataInputPort getter) {
    final EList<DataInputPort> copiedDataInputPorts = vertexCopy.getDataInputPorts();
    final AbstractActor eContainer = (AbstractActor) getter.eContainer();
    final EList<DataInputPort> origDataInputPorts = eContainer.getDataInputPorts();

    if ((copiedDataInputPorts.size() != origDataInputPorts.size()) || !origDataInputPorts.contains(getter)) {
      throw new IllegalStateException();
    }
    final int indexOf = origDataInputPorts.indexOf(getter);
    return copiedDataInputPorts.get(indexOf);
  }

  private DataOutputPort lookupDataOutputPort(final AbstractActor vertexCopy, final DataOutputPort getter) {
    final EList<DataOutputPort> copiedDataOutputPorts = vertexCopy.getDataOutputPorts();
    final AbstractActor eContainer = (AbstractActor) getter.eContainer();
    final EList<DataOutputPort> origDataOutputPorts = eContainer.getDataOutputPorts();

    if ((copiedDataOutputPorts.size() != origDataOutputPorts.size()) || !origDataOutputPorts.contains(getter)) {
      throw new IllegalStateException();
    }
    final int indexOf = origDataOutputPorts.indexOf(getter);
    return copiedDataOutputPorts.get(indexOf);
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
  public void addGraphicalRepresentationForVertex(final AbstractVertex vertexModelCopy, final int x, final int y) {

    // Delays are handled with addGraphicalRepresentationForDelay when adding copied fifos
    if (vertexModelCopy instanceof Delay) {
      return;
    }

    final AddContext addCtxt = new AddContext();
    final Diagram diagram = getDiagram();

    addCtxt.setLocation(x, y);
    addCtxt.setTargetContainer(diagram);

    final PictogramElement newVertexPE = addGraphicalRepresentation(addCtxt, vertexModelCopy);
    this.links.put(vertexModelCopy, newVertexPE);

    final TreeIterator<EObject> vertexChildrenElements = vertexModelCopy.eAllContents();

    vertexChildrenElements.forEachRemaining(childElement -> {
      final PictogramElement pe;
      if (childElement instanceof final Port copiedPort) {
        if (vertexModelCopy instanceof ExecutableActor) {

          final AbstractAddActorPortFeature addPortFeature = switch (copiedPort.getKind()) {
            case DATA_INPUT -> new AddDataInputPortFeature(getFeatureProvider());
            case DATA_OUTPUT -> new AddDataOutputPortFeature(getFeatureProvider());
            case CFG_INPUT -> new AddConfigInputPortFeature(getFeatureProvider());
            case CFG_OUTPUT -> new AddConfigOutputPortFeature(getFeatureProvider());
          };

          pe = addPortFeature.addPictogramElement(newVertexPE, copiedPort);
        } else if (vertexModelCopy instanceof InterfaceActor) {
          // the AddIn/OutInterfaceFeature creates an anchor for the only in/out port and place it first in the list
          final EList<Anchor> anchors = ((AnchorContainer) newVertexPE).getAnchors();
          if (anchors.isEmpty()) {
            throw new IllegalStateException();
          }
          pe = anchors.get(0);
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
      final boolean objectIsVertex = object instanceof VertexCopy;
      if (objectIsVertex) {
        hasOneVertex = true;
      }
    }
    return hasOneVertex;
  }

}

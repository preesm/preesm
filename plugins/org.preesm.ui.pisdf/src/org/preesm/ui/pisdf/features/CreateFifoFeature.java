/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2014)
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
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.PortKind;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.ui.pisdf.util.PiMMUtil;

// TODO: Auto-generated Javadoc
/**
 * Create feature to create a new {@link Fifo} in the Diagram.
 *
 * @author kdesnos
 */
public class CreateFifoFeature extends AbstractCreateConnectionFeature {

  /** The Constant FEATURE_NAME. */
  private static final String FEATURE_NAME = "Fifo";

  /** The Constant FEATURE_DESCRIPTION. */
  private static final String FEATURE_DESCRIPTION = "Create Fifo";

  /**
   * Default constructor for the {@link CreateFifoFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public CreateFifoFeature(final IFeatureProvider fp) {
    super(fp, CreateFifoFeature.FEATURE_NAME, CreateFifoFeature.FEATURE_DESCRIPTION);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ICreateConnection#canCreate(org.eclipse.graphiti.features.context.
   * ICreateConnectionContext)
   */
  @Override
  public boolean canCreate(final ICreateConnectionContext context) {
    // This function is called when selecting the end of a created connection.
    // We assume that the canStartConnection is already true

    // Refresh to remove all remaining tooltip
    getDiagramBehavior().refresh();

    // True if the connection is created between an input and an output port
    final Port target = getTargetPort(context, context.getTargetAnchor());

    // False if the target is an outputPort
    if (target instanceof DataOutputPort) {
      // Create tooltip message
      PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor().getGraphicsAlgorithm(), getDiagramBehavior(),
          "A FIFO cannot end at an output port");
      return false;
    }

    // False if the target is a config input port
    if (target instanceof ConfigInputPort) {
      // Create tooltip message
      PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor().getGraphicsAlgorithm(), getDiagramBehavior(),
          "A FIFO cannot end at an config. input port");
      return false;
    }

    final boolean targetOK = target instanceof DataInputPort;
    if (targetOK) {
      // Check that no Fifo is connected to the ports
      if (((DataInputPort) target).getIncomingFifo() != null) {
        // Create tooltip message
        PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor().getGraphicsAlgorithm(),
            getDiagramBehavior(), "A port cannot be connected to several FIFOs");
        return false;
      }

      // Same check that the one in the canStartConnection
      final DataInputPort targetPort = (DataInputPort) target;
      final AbstractActor targetActor = targetPort.getContainingActor();
      if (targetActor instanceof final DelayActor delayActor) {
        final DataOutputPort source = (DataOutputPort) getSourcePort(context, context.getSourceAnchor());
        if (source != null) {
          final AbstractActor sourceActor = source.getContainingActor();
          if (sourceActor instanceof DelayActor && sourceActor == targetActor) {
            PiMMUtil.setToolTip(getFeatureProvider(), context.getSourcePictogramElement().getGraphicsAlgorithm(),
                getDiagramBehavior(), "A delay cannot be connected to itself.");
            return false;
          }
        }
        final Delay delay = delayActor.getLinkedDelay();
        if (delay.getLevel().equals(PersistenceLevel.LOCAL) || delay.getLevel().equals(PersistenceLevel.PERMANENT)) {
          PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetPictogramElement().getGraphicsAlgorithm(),
              getDiagramBehavior(),
              "A delay with local or permanent data tokens persistence can not have a setter actor.");
          return false;
        }
      }

      return true;
    }

    // Check if the target can create a port
    final boolean targetCanCreatePort = (CreateFifoFeature.canCreatePort(context.getTargetPictogramElement(),
        getFeatureProvider(), PortKind.DATA_INPUT) != null);

    // The method also returns true if the the target can create a new port.
    return targetCanCreatePort || targetOK;
  }

  /**
   * Method to check whether it is possible to create a {@link Port} for the given source/target
   * {@link PictogramElement}.
   *
   * @param pe
   *          the {@link PictogramElement} tested
   * @param fp
   *          the {@link IFeatureProvider} used for this diagram.
   * @param direction
   *          the direction of the port we want to create ("input" or "output")
   * @return an {@link AbstractAddActorPortFeature} if the given {@link PictogramElement} can create a {@link Port} with
   *         the given direction. Return <code>null</code> else.
   */
  protected static AbstractAddActorPortFeature canCreatePort(final PictogramElement pe, final IFeatureProvider fp,
      final PortKind direction) {
    boolean canCreatePort = false;
    final PictogramElement peSource = pe;

    // Create the FeatureProvider
    final CustomContext sourceContext = new CustomContext(new PictogramElement[] { peSource });

    final AbstractAddActorPortFeature addPortFeature = switch (direction) {
      case DATA_INPUT -> new AddDataInputPortFeature(fp);
      case DATA_OUTPUT -> new AddDataOutputPortFeature(fp);
      default -> null;
    };

    if (addPortFeature != null) {
      canCreatePort = addPortFeature.canExecute(sourceContext);
    }
    if (canCreatePort) {
      return addPortFeature;
    }
    return null;
  }

  /**
   * Method to retrieve the {@link Port} corresponding to an {@link Anchor}.
   *
   * @param anchor
   *          the anchor to treat
   * @return the found {@link Port}, or <code>null</code> if no port corresponds to this {@link Anchor}
   */
  protected Port getPort(final Anchor anchor) {
    if (anchor != null) {
      final Object obj = getBusinessObjectForPictogramElement(anchor);
      if (obj instanceof final Port port) {
        return port;
      }
    }
    return null;
  }

  protected Port getSourcePort(final ICreateConnectionContext context, Anchor sourceAnchor) {
    final PictogramElement sourcePe = context.getSourcePictogramElement();
    final Object obj = getBusinessObjectForPictogramElement(sourcePe);
    if (obj instanceof final Delay delay) {
      final DelayActor actor = delay.getActor();
      return actor.getDataOutputPort();
    }
    return getPort(sourceAnchor);
  }

  protected Port getTargetPort(final ICreateConnectionContext context, Anchor targetAnchor) {
    final PictogramElement targetPe = context.getTargetPictogramElement();
    final Object obj = getBusinessObjectForPictogramElement(targetPe);
    if (obj instanceof final Delay delay) {
      final DelayActor actor = delay.getActor();
      return actor.getDataInputPort();
    }
    return getPort(targetAnchor);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.func.ICreateConnection#create(org.eclipse.graphiti.features.context.ICreateConnectionContext)
   */
  @Override
  public Connection create(final ICreateConnectionContext context) {
    Connection newConnection = null;

    // get Ports which should be connected
    Anchor sourceAnchor = context.getSourceAnchor();
    Anchor targetAnchor = context.getTargetAnchor();
    Port source = getSourcePort(context, sourceAnchor);
    Port target = getTargetPort(context, targetAnchor);

    // Create the sourcePort if needed
    if (source == null) {
      final PictogramElement sourcePe = context.getSourcePictogramElement();
      final AbstractAddActorPortFeature addPortFeature = CreateFifoFeature.canCreatePort(sourcePe, getFeatureProvider(),
          PortKind.DATA_OUTPUT);
      if (addPortFeature != null) {
        final CustomContext sourceContext = new CustomContext(new PictogramElement[] { sourcePe });
        addPortFeature.execute(sourceContext);
        sourceAnchor = addPortFeature.getCreatedAnchor();
        source = addPortFeature.getCreatedPort();
      }
    }

    // Create the targetPort if needed
    if (target == null) {
      final PictogramElement targetPe = context.getTargetPictogramElement();
      final AbstractAddActorPortFeature addPortFeature = CreateFifoFeature.canCreatePort(targetPe, getFeatureProvider(),
          PortKind.DATA_INPUT);
      if (addPortFeature != null) {
        final CustomContext targetContext = new CustomContext(new PictogramElement[] { targetPe });
        addPortFeature.execute(targetContext);
        targetAnchor = addPortFeature.getCreatedAnchor();
        target = addPortFeature.getCreatedPort();
      }
    }

    if ((source != null) && (target != null) && (source instanceof final DataOutputPort dopSource)
        && (target instanceof final DataInputPort dipTarget)) {
      // create new business object
      final Fifo fifo = createFifo(dopSource, dipTarget);

      // add connection for business object
      final AddConnectionContext addContext = new AddConnectionContext(sourceAnchor, targetAnchor);
      addContext.setNewObject(fifo);
      newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
    }

    return newConnection;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature#endConnecting()
   */
  @Override
  public void endConnecting() {
    // Refresh to remove all remaining tooltip;
    getDiagramBehavior().refresh();
    super.endConnecting();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ICreateConnection#canStartConnection(org.eclipse.graphiti.features.context.
   * ICreateConnectionContext)
   */
  @Override
  public boolean canStartConnection(final ICreateConnectionContext context) {

    // Return true if the connection starts at an output port (config or
    // not)
    final Anchor sourceAnchor = context.getSourceAnchor();
    final Port source = getSourcePort(context, sourceAnchor);

    if ((source != null) && ((source instanceof DataInputPort) || (source instanceof ConfigInputPort))) {
      // Create tooltip message
      PiMMUtil.setToolTip(getFeatureProvider(), context.getSourceAnchor().getGraphicsAlgorithm(), getDiagramBehavior(),
          "A FIFO cannot start at an input port");
      return false;
    }

    // Also true if the source is a vertex that can create ports
    if (CreateFifoFeature.canCreatePort(context.getSourcePictogramElement(), getFeatureProvider(),
        PortKind.DATA_OUTPUT) != null) {
      return true;
    }

    if (source instanceof final DataOutputPort sourcePort) {
      // Check that no Fifo is connected to the ports
      if (sourcePort.getOutgoingFifo() != null) {
        // Create tooltip message
        PiMMUtil.setToolTip(getFeatureProvider(), context.getSourceAnchor().getGraphicsAlgorithm(),
            getDiagramBehavior(), "A port cannot be connected to several FIFOs");
        return false;
      }
      // Check if the outputPort is a configurationOutputPort wit no
      // outgoing dependency
      if ((source instanceof final ConfigOutputPort cop) && !cop.getOutgoingDependencies().isEmpty()) {
        // Karol: I deliberately left the possibility for a ConfigOutputPort to be connected both with a Fifo and
        // dependencies.
        // Indeed, it seems to me that the coexistence of a unique fifo and one or several dependencies is not a problem
        // since each connection has a very precise semantics.
      }

      // we need to check if we start from a delay that it is allowed
      final AbstractActor sourceActor = sourcePort.getContainingActor();
      if (sourceActor instanceof final DelayActor delayActor) {
        final Delay delay = delayActor.getLinkedDelay();
        if (delay.getLevel().equals(PersistenceLevel.LOCAL) || delay.getLevel().equals(PersistenceLevel.PERMANENT)) {
          PiMMUtil.setToolTip(getFeatureProvider(), context.getSourcePictogramElement().getGraphicsAlgorithm(),
              getDiagramBehavior(),
              "A delay with local or permanent data tokens persistence can not have a getter actor.");
          return false;
        }
      }
      return true;
    }

    return false;
  }

  /**
   * Creates a {@link Fifo} between the two {@link Port}s. Also add the created {@link Fifo} to the {@link PiGraph} of
   * the current {@link Diagram}.
   *
   * @param source
   *          the source {@link DataOutputPort} of the {@link Fifo}
   * @param target
   *          the target {@link DataInputPort} of the {@link Fifo}
   * @return the created {@link Fifo}
   */
  protected Fifo createFifo(final DataOutputPort source, final DataInputPort target) {

    // Refresh to remove all remaining tooltip;
    getDiagramBehavior().refresh();

    // Retrieve the graph
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());

    // Create the Fifo
    final Fifo fifo = PiMMUserFactory.instance.createFifo();
    fifo.setSourcePort(source);
    fifo.setTargetPort(target);
    if ((source instanceof ConfigOutputPort) || (target.getContainingActor() instanceof ConfigOutputInterface)) {
      fifo.setType("long");
    }

    // Add the new Fifo to the graph
    graph.addFifo(fifo);

    return fifo;
  }
}

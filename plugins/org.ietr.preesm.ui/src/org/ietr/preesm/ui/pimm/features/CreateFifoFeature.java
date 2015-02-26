/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 ******************************************************************************/
package org.ietr.preesm.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;

/**
 * Create feature to create a new {@link Fifo} in the Diagram
 * 
 * @author kdesnos
 * 
 */
public class CreateFifoFeature extends AbstractCreateConnectionFeature {

	private static final String FEATURE_NAME = "Fifo";

	private static final String FEATURE_DESCRIPTION = "Create Fifo";

	/**
	 * Default constructor for the {@link CreateFifoFeature}.
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public CreateFifoFeature(IFeatureProvider fp) {
		super(fp, FEATURE_NAME, FEATURE_DESCRIPTION);
	}

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		// This function is called when selecting the end of a created
		// connection.
		// We assume that the canStartConnection is already true

		// Refresh to remove all remaining tooltip;
		getDiagramBehavior().refresh();

		// True if the connection is created between an input and an output port
		Port target = getPort(context.getTargetAnchor());
		boolean targetOK = (target != null && target instanceof DataInputPort);
		if (targetOK) {
			// Check that no Fifo is connected to the ports
			if (((DataInputPort) target).getIncomingFifo() != null) {
				// Create tooltip message
				PiMMUtil.setToolTip(getFeatureProvider(), context
						.getTargetAnchor().getGraphicsAlgorithm(),
						getDiagramBehavior(),
						"A port cannot be connected to several FIFOs");
				return false;
			}

			return true;
		}

		// False if the target is an outputPort
		if (target != null && target instanceof DataOutputPort) {
			// Create tooltip message
			PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor()
					.getGraphicsAlgorithm(), getDiagramBehavior(),
					"A FIFO cannot end at an output port");
			return false;
		}

		// False if the target is an outputPort
		if (target != null && target instanceof ConfigInputPort) {
			// Create tooltip message
			PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor()
					.getGraphicsAlgorithm(), getDiagramBehavior(),
					"A FIFO cannot end at an config. input port");
			return false;
		}

		// Check if the target can create a port
		boolean targetCanCreatePort = (canCreatePort(
				context.getTargetPictogramElement(), getFeatureProvider(), "input") != null);

		// The method also returns true if the the target can
		// create a new port.
		if ((targetCanCreatePort || targetOK)) {
			return true;
		}

		return false;
	}

	/**
	 * Method to check whether it is possible to create a {@link Port} for the
	 * given source/target {@link PictogramElement}
	 * 
	 * @param pe
	 *            the {@link PictogramElement} tested
	 * @param fp
	 * 				the {@link IFeatureProvider} used for this diagram.
	 * @param direction
	 *            the direction of the port we want to create ("input" or
	 *            "output")
	 * @return an {@link AbstractAddActorPortFeature} if the given
	 *         {@link PictogramElement} can create a {@link Port} with the given
	 *         direction. Return <code>null</code> else.
	 */
	static protected AbstractAddActorPortFeature canCreatePort(PictogramElement pe,
			IFeatureProvider fp, String direction) {
		boolean canCreatePort = false;
		PictogramElement peSource = pe;

		// Create the FeatureProvider
		CustomContext sourceContext = new CustomContext(
				new PictogramElement[] { peSource });
		AbstractAddActorPortFeature addPortFeature = null;
		if (direction.equals("input")) {
			addPortFeature = new AddDataInputPortFeature(fp);
		}
		if (direction.equals("output")) {
			addPortFeature = new AddDataOutputPortFeature(fp);
		}
		if (addPortFeature != null) {
			canCreatePort = addPortFeature.canExecute(sourceContext);
		}
		if (canCreatePort) {
			return addPortFeature;
		} else {
			return null;
		}
	}

	/**
	 * Method to retrieve the {@link Port} corresponding to an {@link Anchor}
	 * 
	 * @param anchor
	 *            the anchor to treat
	 * @return the found {@link Port}, or <code>null</code> if no port
	 *         corresponds to this {@link Anchor}
	 */
	protected Port getPort(Anchor anchor) {
		if (anchor != null) {
			Object obj = getBusinessObjectForPictogramElement(anchor);
			if (obj instanceof Port) {
				return (Port) obj;
			}
		}
		return null;
	}

	@Override
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;

		// get Ports which should be connected
		Anchor sourceAnchor = context.getSourceAnchor();
		Anchor targetAnchor = context.getTargetAnchor();
		Port source = getPort(sourceAnchor);
		Port target = getPort(targetAnchor);

		// Create the sourcePort if needed
		if (source == null) {
			PictogramElement sourcePe = context.getSourcePictogramElement();
			AbstractAddActorPortFeature addPortFeature = canCreatePort(
					sourcePe, getFeatureProvider(), "output");
			if (addPortFeature != null) {
				CustomContext sourceContext = new CustomContext(
						new PictogramElement[] { sourcePe });
				addPortFeature.execute(sourceContext);
				sourceAnchor = addPortFeature.getCreatedAnchor();
				source = addPortFeature.getCreatedPort();
			}
		}

		// Create the targetPort if needed
		if (target == null) {
			PictogramElement targetPe = context.getTargetPictogramElement();
			AbstractAddActorPortFeature addPortFeature = canCreatePort(
					targetPe, getFeatureProvider(), "input");
			if (addPortFeature != null) {
				CustomContext targetContext = new CustomContext(
						new PictogramElement[] { targetPe });
				addPortFeature.execute(targetContext);
				targetAnchor = addPortFeature.getCreatedAnchor();
				target = addPortFeature.getCreatedPort();
			}
		}

		if (source != null && target != null && source instanceof DataOutputPort
				&& target instanceof DataInputPort) {
			// create new business object
			Fifo fifo = createFifo((DataOutputPort) source, (DataInputPort) target);

			// add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					sourceAnchor, targetAnchor);
			addContext.setNewObject(fifo);
			newConnection = (Connection) getFeatureProvider().addIfPossible(
					addContext);
		}

		return newConnection;
	}

	@Override
	public void endConnecting() {
		// Refresh to remove all remaining tooltip;
		getDiagramBehavior().refresh();
		super.endConnecting();
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		// Return true if the connection starts at an output port (config or
		// not)
		Port source = getPort(context.getSourceAnchor());
		if (source != null && source instanceof DataOutputPort) {
			// Check that no Fifo is connected to the ports
			if (((DataOutputPort) source).getOutgoingFifo() == null) {
				// Check if the outputPort is a configurationOutputPort wit no
				// outgoing dependency
				if (source instanceof ConfigOutputPort
						&& !((ConfigOutputPort) source)
								.getOutgoingDependencies().isEmpty()) {
					// Karol: I deliberately left the possibility for a
					// ConfigOutputPort to be connected both with a Fifo and
					// dependencies.
					// Indeed, it seems to me that the coexistence of a unique
					// fifo and one or several dependencies is not a problem
					// since each connection has a very precise semantics.
				}
				return true;
			} else {
				// Create tooltip message
				PiMMUtil.setToolTip(getFeatureProvider(), context
						.getSourceAnchor().getGraphicsAlgorithm(),
						getDiagramBehavior(),
						"A port cannot be connected to several FIFOs");
				return false;
			}
		}

		if (source != null
				&& (source instanceof DataInputPort || source instanceof ConfigInputPort)) {
			// Create tooltip message
			PiMMUtil.setToolTip(getFeatureProvider(), context.getSourceAnchor()
					.getGraphicsAlgorithm(), getDiagramBehavior(),
					"A FIFO cannot start at an input port");
			return false;
		}

		// Also true if the source is a vertex that can create ports
		if (canCreatePort(context.getSourcePictogramElement(), getFeatureProvider(), "output") != null) {
			return true;
		}
		return false;
	}

	/**
	 * Creates a {@link Fifo} between the two {@link Port}s. Also add the
	 * created {@link Fifo} to the {@link PiGraph} of the current {@link Diagram}.
	 * 
	 * @param source
	 *            the source {@link DataOutputPort} of the {@link Fifo}
	 * @param target
	 *            the target {@link DataInputPort} of the {@link Fifo}
	 * @return the created {@link Fifo}
	 */
	protected Fifo createFifo(DataOutputPort source, DataInputPort target) {

		// Refresh to remove all remaining tooltip;
		getDiagramBehavior().refresh();

		// Retrieve the graph
		PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());

		// Create the Fifo
		Fifo fifo = PiMMFactory.eINSTANCE.createFifo();
		fifo.setSourcePort(source);
		fifo.setTargetPort(target);

		// Add the new Fifo to the graph
		graph.getFifos().add(fifo);

		return fifo;
	}
}

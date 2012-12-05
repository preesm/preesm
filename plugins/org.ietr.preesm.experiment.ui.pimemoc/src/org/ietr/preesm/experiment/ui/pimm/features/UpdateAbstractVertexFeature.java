package org.ietr.preesm.experiment.ui.pimm.features;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.ui.pimm.util.PortEqualityHelper;

public class UpdateAbstractVertexFeature extends AbstractUpdateFeature {

	/**
	 * Default constructor of the {@link UpdateAbstractVertexFeature}
	 * 
	 * @param fp
	 */
	public UpdateAbstractVertexFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context
				.getPictogramElement());
		return (bo instanceof AbstractVertex);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context
				.getPictogramElement());

		IReason ret = nameUpdateNeeded(context);
		if (!ret.toBoolean()) {
			if (bo instanceof Actor) {
				ret = portsUpdateNeeded(context, (Actor) bo);
			}
		}

		return ret;
	}

	/**
	 * This method will check whether the ports of the actor refinement are
	 * different from the current
	 * 
	 * @param context
	 *            the context of the feature
	 * @param actor
	 *            the tested {@link Actor}
	 * @return a reason stating if an update of the ports is needed
	 */
	protected IReason portsUpdateNeeded(IUpdateContext context, Actor actor) {
		AbstractVertex vertex = actor.getRefinement().getAbstractVertex();
		if (vertex != null) {
			Map<SimpleEntry<Port, Port>, IReason> m = PortEqualityHelper
					.buildEquivalentPortsMap(actor, vertex);

			// throw new RuntimeException(
			// "This code has been reached ! This is a TODO reminder !");
			// return
			String reasons = "";
			for (Entry<SimpleEntry<Port, Port>, IReason> e : m.entrySet()) {
				if (!e.getValue().toBoolean()) {
					if (e.getValue().getText().equals(PortEqualityHelper.NULL_PORT)) {
						Port actorPort = e.getKey().getKey();
						Port refinePort = e.getKey().getValue();
						if (actorPort != null) {
							reasons += "\nPort \"" + actorPort.getName()
									+ "\" not present in refinement.";
						} else {
							reasons += "\nRefinement has an extra "
									+ refinePort.getKind() + " port \""
									+ refinePort.getName() + "\".";
						}
					}
				}
			}
			if (!reasons.equals("")) {
				return Reason
						.createTrueReason("Ports are out of sync with the refinement graph."
								+ reasons);
			}
		}
		return Reason.createFalseReason();
	}

	/**
	 * @param context
	 * @return
	 */
	protected IReason nameUpdateNeeded(IUpdateContext context) {
		// retrieve name from pictogram model
		String pictogramName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) shape.getGraphicsAlgorithm();
					pictogramName = text.getValue();
				}
			}
		}

		// retrieve AbstractVertex name from business model (from the graph)
		String businessName = null;
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof AbstractVertex) {
			AbstractVertex vertex = (AbstractVertex) bo;
			businessName = vertex.getName();
		}

		// update needed, if names are different
		boolean updateNameNeeded = ((pictogramName == null && businessName != null) || (pictogramName != null && !pictogramName
				.equals(businessName)));
		if (updateNameNeeded) {
			return Reason.createTrueReason("Name is out of date\nNew name: "
					+ businessName);
		}

		return Reason.createFalseReason();
	}

	@Override
	public boolean update(IUpdateContext context) {
		return updateName(context);
	}

	/**
	 * @param context
	 * @return
	 */
	protected boolean updateName(IUpdateContext context) {
		// retrieve name from business model
		String businessName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof AbstractVertex) {
			AbstractVertex vertex = (AbstractVertex) bo;
			businessName = vertex.getName();
		}

		// Set name in pictogram model
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) shape.getGraphicsAlgorithm();
					text.setValue(businessName);
					return true;
				}
			}
		}
		// Update not completed
		return false;
	}
}

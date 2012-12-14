package org.ietr.preesm.experiment.ui.pimm.features;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.ui.pimm.util.PortEqualityHelper;

public class UpdateActorFeature extends UpdateAbstractVertexFeature {

	/**
	 * Default constructor of the {@link UpdateActorFeature}
	 * 
	 * @param fp
	 */
	public UpdateActorFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context
				.getPictogramElement());
		return (bo instanceof Actor);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context
				.getPictogramElement());

		// Check if a name update is required
		IReason ret = super.updateNeeded(context);

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
		AbstractActor vertex = actor.getRefinement().getAbstractActor();
		if (vertex != null) {
			Map<SimpleEntry<Port, Port>, IReason> m = PortEqualityHelper
					.buildEquivalentPortsMap(actor, vertex);

			String reasons = "";
			for (Entry<SimpleEntry<Port, Port>, IReason> e : m.entrySet()) {
				if (!e.getValue().toBoolean()) {
					if (e.getValue().getText()
							.equals(PortEqualityHelper.NULL_PORT)) {
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

	@Override
	public boolean update(IUpdateContext context) {
		return super.updateName(context);
	}
}

package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.util.VertexNameValidator;
import org.ietr.preesm.experiment.ui.pimm.util.PiMMUtil;

public class CreateParameterFeature extends AbstractCreateFeature {

	private static final String FEATURE_NAME = "Parameter";

	private static final String FEATURE_DESCRIPTION = "Create Parameter";

	protected Boolean hasDoneChanges;

	/**
	 * Default constructor for the {@link CreateParameterFeature}.
	 * 
	 * @param fp
	 *            the feature provider
	 * @param name
	 *            the name of
	 * @param description
	 */
	public CreateParameterFeature(IFeatureProvider fp) {
		super(fp, FEATURE_NAME, FEATURE_DESCRIPTION);
		hasDoneChanges = false;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		// Retrieve the graph
		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());

		// Ask user for Parameter name until a valid name is entered.
		String question = "Enter new parameter name";
		String newParameterName = "ParameterName";

		// TODO create a parameter name validator
		newParameterName = PiMMUtil.askString("Create Parameter", question,
				newParameterName, new VertexNameValidator(graph, null));
		if (newParameterName == null || newParameterName.trim().length() == 0) {
			this.hasDoneChanges = false; // If this is not done, the graph is
											// considered modified.
			return EMPTY;
		}

		// create Parameter
		Parameter newParameter = PiMMFactory.eINSTANCE.createParameter();
		newParameter.setName(newParameterName);
		newParameter.setConfigurationInterface(false);
		newParameter.setLocallyStatic(true);

		// Add new parameter to the graph.
		if (graph.getParameters().add(newParameter)) {
			this.hasDoneChanges = true;
		}

		// do the add to the Diagram
		addGraphicalRepresentation(context, newParameter);

		// return newly created business object(s)
		return new Object[] { newParameter };
	}

}

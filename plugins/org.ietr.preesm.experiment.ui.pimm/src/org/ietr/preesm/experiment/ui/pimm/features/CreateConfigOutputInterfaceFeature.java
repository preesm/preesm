package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.util.VertexNameValidator;
import org.ietr.preesm.experiment.ui.pimm.util.PiMMUtil;

/**
 * Create feature for {@link ConfigOutputInterface}
 * 
 * @author kdesnos
 * 
 */
public class CreateConfigOutputInterfaceFeature extends AbstractCreateFeature {

	boolean hasDoneChanges = false;

	private static final String FEATURE_NAME = "Config Output Interface";

	private static final String FEATURE_DESCRIPTION = "Create Config Output Interface";

	/**
	 * the Default constructor of {@link CreateConfigOutputInterfaceFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public CreateConfigOutputInterfaceFeature(IFeatureProvider fp) {
		super(fp, FEATURE_NAME, FEATURE_DESCRIPTION);
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		// Retrieve the graph
		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());

		// Ask user for ConfigOutputInterface name until a valid name is
		// entered.
		String question = "Enter new Config. Output Interface name";
		String newCfgOutName = "cfgOutIfName";

		newCfgOutName = PiMMUtil.askString("Create Config. Output Interface",
				question, newCfgOutName, new VertexNameValidator(graph, null));
		if (newCfgOutName == null || newCfgOutName.trim().length() == 0) {
			this.hasDoneChanges = false; // If this is not done, the graph is
											// considered modified.
			return EMPTY;
		}

		// create ConfigOutInterface
		ConfigOutputInterface newCfgOutIf = PiMMFactory.eINSTANCE
				.createConfigOutputInterface();
		newCfgOutIf.setName(newCfgOutName);

		// Add new ConfigOutInterface to the graph.
		if (graph.getVertices().add(newCfgOutIf)) {
			this.hasDoneChanges = true;
		}

		// do the add to the Diagram
		addGraphicalRepresentation(context, newCfgOutIf);

		// return newly created business object(s)
		return new Object[] { newCfgOutIf };
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}

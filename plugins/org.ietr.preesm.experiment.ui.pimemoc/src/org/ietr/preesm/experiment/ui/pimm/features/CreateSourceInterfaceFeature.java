package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;
import org.ietr.preesm.experiment.model.pimm.util.VertexNameValidator;
import org.ietr.preesm.experiment.ui.pimm.util.PiMMUtil;

/**
 * Create feature for {@link SourceInterface}
 * 
 * @author kdesnos
 * 
 */
public class CreateSourceInterfaceFeature extends AbstractCreateFeature {

	boolean hasDoneChanges = false;

	private static final String FEATURE_NAME = "Source Interface";

	private static final String FEATURE_DESCRIPTION = "Create Source Interface";

	/**
	 * the Default constructor of {@link CreateSourceInterfaceFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public CreateSourceInterfaceFeature(IFeatureProvider fp) {
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

		// Ask user for Actor name until a valid name is entered.
		String question = "Enter new Source Interface name";
		String newSrcName = "SrcInterfaceName";

		newSrcName = PiMMUtil.askString("Create Source Interface", question,
				newSrcName, new VertexNameValidator(graph, null));
		if (newSrcName == null || newSrcName.trim().length() == 0) {
			this.hasDoneChanges = false; // If this is not done, the graph is
											// considered modified.
			return EMPTY;
		}

		// create SourceInterface
		SourceInterface newSrcInterface = PiMMFactory.eINSTANCE
				.createSourceInterface();
		newSrcInterface.setName(newSrcName);

		// Add new sourceInterface to the graph.
		if (graph.getVertices().add(newSrcInterface)) {
			this.hasDoneChanges = true;
		}

		// do the add to the Diagram
		addGraphicalRepresentation(context, newSrcInterface);

		// return newly created business object(s)
		return new Object[] { newSrcInterface };
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}

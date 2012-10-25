package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ietr.preesm.experiment.model.pimemoc.Graph;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCFactory;
import org.ietr.preesm.experiment.model.pimemoc.SinkInterface;
import org.ietr.preesm.experiment.model.pimemoc.util.VertexNameValidator;
import org.ietr.preesm.experiment.ui.pimemoc.util.PimemocUtil;

/**
 * Create feature for {@link SinkInterface}
 * 
 * @author kdesnos
 * 
 */
public class CreateSinkInterfaceFeature extends AbstractCreateFeature {

	boolean hasDoneChanges = false;

	private static final String FEATURE_NAME = "Sink Interface";

	private static final String FEATURE_DESCRIPTION = "Create Sink Interface";

	/**
	 * the Default constructor of {@link CreateSinkInterfaceFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public CreateSinkInterfaceFeature(IFeatureProvider fp) {
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

		// Ask user for SinkInterface name until a valid name is entered.
		String question = "Enter new Sink Interface name";
		String newSnkName = "SnkInterfaceName";

		newSnkName = PimemocUtil.askString("Create Sink Interface", question,
				newSnkName, new VertexNameValidator(graph, null));
		if (newSnkName == null || newSnkName.trim().length() == 0) {
			this.hasDoneChanges = false; // If this is not done, the graph is
											// considered modified.
			return EMPTY;
		}

		// create SinkInterface
		SinkInterface newSinkInterface = PIMeMoCFactory.eINSTANCE
				.createSinkInterface();
		newSinkInterface.setName(newSnkName);

		// Add new SinkInterface to the graph.
		if (graph.addInterfaceVertex(newSinkInterface)) {
			this.hasDoneChanges = true;
		}

		// do the add to the Diagram
		addGraphicalRepresentation(context, newSinkInterface);

		// return newly created business object(s)
		return new Object[] { newSinkInterface };
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}

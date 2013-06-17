package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.util.VertexNameValidator;
import org.ietr.preesm.experiment.ui.pimm.util.PiMMUtil;

/**
 * Custom feature to rename an {@link AbstractVertex}.
 * 
 * @author kdesnos
 * 
 */
public class RenameAbstractVertexFeature extends AbstractCustomFeature {

	protected boolean hasDoneChanges = false;

	/**
	 * Default Constructor
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public RenameAbstractVertexFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Rename";
	}

	@Override
	public String getDescription() {
		return "Change the name";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// allow rename if exactly one pictogram element
		// representing an AbstractVertex is selected
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof AbstractVertex) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void execute(ICustomContext context) {
		// Retrieve the graph
		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());

		// Re-check if only one element is selected
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof AbstractVertex) {
				AbstractVertex vertex = (AbstractVertex) bo;
				String currentName = vertex.getName();

				// Ask user for AbstractVertex name until a valid name is
				// entered.
				String className = bo.getClass().getSimpleName();
				className = className.substring(0,
						className.lastIndexOf("Impl"));
				String question = "Enter new " + className + " name";
				String newVertexName = vertex.getName();

				newVertexName = PiMMUtil.askString("Rename " + className,
						question, newVertexName, new VertexNameValidator(graph,
								vertex));

				if (newVertexName != null && !newVertexName.equals(currentName)) {
					this.hasDoneChanges = true;
					vertex.setName(newVertexName);

					// Update the Pictogram element
					updatePictogramElement(pes[0]);

					// Call the layout feature
					layoutPictogramElement(pes[0]);
				}
			}
		}
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}

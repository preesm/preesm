package org.ietr.preesm.experiment.ui.pimemoc.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.ietr.preesm.experiment.model.pimemoc.Actor;
import org.ietr.preesm.experiment.ui.pimemoc.features.AddActorFeature;
import org.ietr.preesm.experiment.ui.pimemoc.features.AddInputPortFeature;
import org.ietr.preesm.experiment.ui.pimemoc.features.AddOutputPortFeature;
import org.ietr.preesm.experiment.ui.pimemoc.features.CreateActorFeature;
import org.ietr.preesm.experiment.ui.pimemoc.features.CustomDeleteFeature;
import org.ietr.preesm.experiment.ui.pimemoc.features.DeletePortFeature;
import org.ietr.preesm.experiment.ui.pimemoc.features.DirectEditingActorNameFeature;
import org.ietr.preesm.experiment.ui.pimemoc.features.LayoutActorFeature;
import org.ietr.preesm.experiment.ui.pimemoc.features.RenameActorFeature;
import org.ietr.preesm.experiment.ui.pimemoc.features.UpdateActorFeature;

public class PimemocFeatureProvider extends DefaultFeatureProvider {

	public PimemocFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// is object for add request an Actor?
		if (context.getNewObject() instanceof Actor) {
			return new AddActorFeature(this);
		}
		return super.getAddFeature(context);
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(
			IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof Actor) {
			return new DirectEditingActorNameFeature(this);
		}
		return super.getDirectEditingFeature(context);
	}

	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		return new ICustomFeature[] { new RenameActorFeature(this),
				new AddOutputPortFeature(this),
				new AddInputPortFeature(this)};
	}

	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof Actor) {
				// We do not allow manual resize of Actor's pictogram elements.
				// The size of these elements will be computed automatically
				// to fit the content of the shape
				return null;
			}
		}
		return super.getResizeShapeFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] { new CreateActorFeature(this) };
	}

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof Actor) {
			return new LayoutActorFeature(this);
		}
		return super.getLayoutFeature(context);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof Actor) {
				return new UpdateActorFeature(this);
			}
		}
		return super.getUpdateFeature(context);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		return null; // remove disabled for the UI
		// Since the remove feature is used in the deleteFeature,
		// it must be providedSomehow. This is the purpose of the
		// PimemocFeatureProviderWithRemove class.
	}

	/**
	 * Provide the default remove feature when needed. This will be used in the
	 * deletion feature.
	 * 
	 * @see PimemocFeatureProviderWithRemove
	 * @see CustomDeleteFeature
	 * @see http 
	 *      ://www.eclipse.org/forums/index.php/mv/msg/234410/720417/#msg_720417
	 * @param context
	 *            the context
	 * @return remove feature according to the given context
	 */
	protected IRemoveFeature getRemoveFeatureEnabled(IRemoveContext context) {
		return super.getRemoveFeature(context); // used where we enable remove
												// (deleting...)
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		if(context.getPictogramElement() instanceof Anchor){
			return new DeletePortFeature(this);
		}
		return new CustomDeleteFeature(this);
	}

}

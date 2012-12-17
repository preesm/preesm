package org.ietr.preesm.experiment.ui.pimm.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveAnchorFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveAnchorContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.SinkInterface;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;
import org.ietr.preesm.experiment.ui.pimm.features.AddActorFeature;
import org.ietr.preesm.experiment.ui.pimm.features.AddConfigInputPortFeature;
import org.ietr.preesm.experiment.ui.pimm.features.AddDependencyFeature;
import org.ietr.preesm.experiment.ui.pimm.features.AddFifoFeature;
import org.ietr.preesm.experiment.ui.pimm.features.AddInputPortFeature;
import org.ietr.preesm.experiment.ui.pimm.features.AddOutputPortFeature;
import org.ietr.preesm.experiment.ui.pimm.features.AddParameterFeature;
import org.ietr.preesm.experiment.ui.pimm.features.AddSinkInterfaceFeature;
import org.ietr.preesm.experiment.ui.pimm.features.AddSourceInterfaceFeature;
import org.ietr.preesm.experiment.ui.pimm.features.CreateActorFeature;
import org.ietr.preesm.experiment.ui.pimm.features.CreateDependencyFeature;
import org.ietr.preesm.experiment.ui.pimm.features.CreateFifoFeature;
import org.ietr.preesm.experiment.ui.pimm.features.CreateParameterFeature;
import org.ietr.preesm.experiment.ui.pimm.features.CreateSinkInterfaceFeature;
import org.ietr.preesm.experiment.ui.pimm.features.CreateSourceInterfaceFeature;
import org.ietr.preesm.experiment.ui.pimm.features.CustomDeleteFeature;
import org.ietr.preesm.experiment.ui.pimm.features.DeleteAbstractActorFeature;
import org.ietr.preesm.experiment.ui.pimm.features.DeleteActorPortFeature;
import org.ietr.preesm.experiment.ui.pimm.features.DeleteInterfaceActorFeature;
import org.ietr.preesm.experiment.ui.pimm.features.DeleteParameterFeature;
import org.ietr.preesm.experiment.ui.pimm.features.DirectEditingAbstractActorNameFeature;
import org.ietr.preesm.experiment.ui.pimm.features.LayoutActorFeature;
import org.ietr.preesm.experiment.ui.pimm.features.LayoutInterfaceActorFeature;
import org.ietr.preesm.experiment.ui.pimm.features.LayoutParameterFeature;
import org.ietr.preesm.experiment.ui.pimm.features.LayoutPortFeature;
import org.ietr.preesm.experiment.ui.pimm.features.MoveAbstractActorFeature;
import org.ietr.preesm.experiment.ui.pimm.features.OpenRefinementFeature;
import org.ietr.preesm.experiment.ui.pimm.features.ReconnectionFifoFeature;
import org.ietr.preesm.experiment.ui.pimm.features.RenameActorFeature;
import org.ietr.preesm.experiment.ui.pimm.features.RenameActorPortFeature;
import org.ietr.preesm.experiment.ui.pimm.features.SetActorRefinementFeature;
import org.ietr.preesm.experiment.ui.pimm.features.UpdateAbstractVertexFeature;
import org.ietr.preesm.experiment.ui.pimm.features.UpdateActorFeature;
import org.ietr.preesm.experiment.ui.pimm.features.UpdatePortFeature;

public class PiMMFeatureProvider extends DefaultFeatureProvider {

	public PiMMFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// is object for add request an Actor?
		if (context.getNewObject() instanceof Actor) {
			return new AddActorFeature(this);
		}

		if (context.getNewObject() instanceof Parameter) {
			return new AddParameterFeature(this);
		}

		if (context.getNewObject() instanceof SourceInterface) {
			return new AddSourceInterfaceFeature(this);
		}

		if (context.getNewObject() instanceof SinkInterface) {
			return new AddSinkInterfaceFeature(this);
		}

		if (context.getNewObject() instanceof Fifo) {
			return new AddFifoFeature(this);
		}

		if (context.getNewObject() instanceof Dependency) {
			return new AddDependencyFeature(this);
		}
		return super.getAddFeature(context);
	}

	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] { new CreateFifoFeature(this),
				new CreateDependencyFeature(this) };
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] { new CreateActorFeature(this),
				new CreateParameterFeature(this),
				new CreateSourceInterfaceFeature(this),
				new CreateSinkInterfaceFeature(this) };
	}

	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		return new ICustomFeature[] { new RenameActorFeature(this),
				new AddOutputPortFeature(this), new AddInputPortFeature(this),
				new AddConfigInputPortFeature(this),
				new RenameActorPortFeature(this),
				new SetActorRefinementFeature(this),
				new OpenRefinementFeature(this) };
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);

		if (bo instanceof Port) {
			if (((Port) bo).eContainer() instanceof Actor) {
				return new DeleteActorPortFeature(this);
			}
			if (((Port) bo).eContainer() instanceof InterfaceActor) {
				// We do not allow deletion of the port of an InterfaceVertex
				// through the GUI
				return null;
			}
		}
		if (bo instanceof InterfaceActor) {
			return new DeleteInterfaceActorFeature(this);
		} else if (bo instanceof AbstractActor) {
			return new DeleteAbstractActorFeature(this);
		}

		if (bo instanceof Parameter) {
			return new DeleteParameterFeature(this);
		}
		
		return new CustomDeleteFeature(this);
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(
			IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof AbstractVertex) {
			return new DirectEditingAbstractActorNameFeature(this);
		}
		return super.getDirectEditingFeature(context);
	}

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof Actor) {
			return new LayoutActorFeature(this);
		}
		if (bo instanceof Port) {
			return new LayoutPortFeature(this);
		}
		if (bo instanceof InterfaceActor) {
			return new LayoutInterfaceActorFeature(this);
		}
		if (bo instanceof Parameter) {
			return new LayoutParameterFeature(this);
		}
		return super.getLayoutFeature(context);
	}

	@Override
	public IMoveAnchorFeature getMoveAnchorFeature(IMoveAnchorContext context) {
		// We forbid the user from moving anchors
		return null;
	}

	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof AbstractActor) {
			return new MoveAbstractActorFeature(this);
		}
		return super.getMoveShapeFeature(context);
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(
			IReconnectionContext context) {
		return new ReconnectionFifoFeature(this);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		return null; // remove disabled for the UI
		// Since the remove feature is used in the deleteFeature,
		// it must be providedSomehow. This is the purpose of the
		// PiMMFeatureProviderWithRemove class.
	}

	/**
	 * Provide the default remove feature when needed. This will be used in the
	 * deletion feature.
	 * 
	 * @see PiMMFeatureProviderWithRemove
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

			if (bo instanceof InterfaceActor) {
				// We do not allow manual resize of Actor's pictogram elements.
				// The size of these elements will be computed automatically
				// to fit the content of the shape
				return null;
			}

			if (bo instanceof Parameter) {
				// We do not allow manual resize of Actor's pictogram elements.
				// The size of these elements will be computed automatically
				// to fit the content of the shape
				return null;
			}
		}
		return super.getResizeShapeFeature(context);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof Actor) {
				return new UpdateActorFeature(this);
			} else if (bo instanceof AbstractVertex) {
				return new UpdateAbstractVertexFeature(this);
			}

		}
		if (pictogramElement instanceof BoxRelativeAnchor) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof Port) {
				return new UpdatePortFeature(this);
			}
		}
		return super.getUpdateFeature(context);
	}

}

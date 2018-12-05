/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.ui.pisdf.diagram;

import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveAnchorFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveAnchorContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.util.PiMMSwitch;
import org.preesm.ui.pisdf.features.AddActorFeature;
import org.preesm.ui.pisdf.features.AddActorFromRefinementFeature;
import org.preesm.ui.pisdf.features.AddBroadcastActorFeature;
import org.preesm.ui.pisdf.features.AddConfigInputInterfaceFeature;
import org.preesm.ui.pisdf.features.AddConfigInputPortFeature;
import org.preesm.ui.pisdf.features.AddConfigOutputInterfaceFeature;
import org.preesm.ui.pisdf.features.AddConfigOutputPortFeature;
import org.preesm.ui.pisdf.features.AddDataInputInterfaceFeature;
import org.preesm.ui.pisdf.features.AddDataInputPortFeature;
import org.preesm.ui.pisdf.features.AddDataOutputInterfaceFeature;
import org.preesm.ui.pisdf.features.AddDataOutputPortFeature;
import org.preesm.ui.pisdf.features.AddDelayFeature;
import org.preesm.ui.pisdf.features.AddDependencyFeature;
import org.preesm.ui.pisdf.features.AddEndActorFeature;
import org.preesm.ui.pisdf.features.AddFifoFeature;
import org.preesm.ui.pisdf.features.AddForkActorFeature;
import org.preesm.ui.pisdf.features.AddInitActorFeature;
import org.preesm.ui.pisdf.features.AddJoinActorFeature;
import org.preesm.ui.pisdf.features.AddParameterFeature;
import org.preesm.ui.pisdf.features.AddRefinementFeature;
import org.preesm.ui.pisdf.features.AddRoundBufferActorFeature;
import org.preesm.ui.pisdf.features.ClearActorMemoryScriptFeature;
import org.preesm.ui.pisdf.features.ClearActorRefinementFeature;
import org.preesm.ui.pisdf.features.CopyFeature;
import org.preesm.ui.pisdf.features.CreateActorFeature;
import org.preesm.ui.pisdf.features.CreateBroadcastActorFeature;
import org.preesm.ui.pisdf.features.CreateConfigInputInterfaceFeature;
import org.preesm.ui.pisdf.features.CreateConfigOutputInterfaceFeature;
import org.preesm.ui.pisdf.features.CreateDataInputInterfaceFeature;
import org.preesm.ui.pisdf.features.CreateDataOutputInterfaceFeature;
import org.preesm.ui.pisdf.features.CreateDependencyFeature;
import org.preesm.ui.pisdf.features.CreateFifoFeature;
import org.preesm.ui.pisdf.features.CreateForkActorFeature;
import org.preesm.ui.pisdf.features.CreateJoinActorFeature;
import org.preesm.ui.pisdf.features.CreateParameterFeature;
import org.preesm.ui.pisdf.features.CreateRoundBufferActorFeature;
import org.preesm.ui.pisdf.features.DeleteAbstractActorFeature;
import org.preesm.ui.pisdf.features.DeleteActorPortFeature;
import org.preesm.ui.pisdf.features.DeleteDelayFeature;
import org.preesm.ui.pisdf.features.DeleteDependencyFeature;
import org.preesm.ui.pisdf.features.DeleteFifoFeature;
import org.preesm.ui.pisdf.features.DeleteParameterizableFeature;
import org.preesm.ui.pisdf.features.DirectEditingAbstractActorNameFeature;
import org.preesm.ui.pisdf.features.ExportSVGFeature;
import org.preesm.ui.pisdf.features.LayoutActorFeature;
import org.preesm.ui.pisdf.features.LayoutInterfaceFeature;
import org.preesm.ui.pisdf.features.LayoutParameterFeature;
import org.preesm.ui.pisdf.features.LayoutPortFeature;
import org.preesm.ui.pisdf.features.MoveAbstractActorFeature;
import org.preesm.ui.pisdf.features.MoveDownActorPortFeature;
import org.preesm.ui.pisdf.features.MoveUpActorPortFeature;
import org.preesm.ui.pisdf.features.OpenMemoryScriptFeature;
import org.preesm.ui.pisdf.features.OpenRefinementFeature;
import org.preesm.ui.pisdf.features.PasteFeature;
import org.preesm.ui.pisdf.features.ReconnectionDependencyFeature;
import org.preesm.ui.pisdf.features.ReconnectionFifoFeature;
import org.preesm.ui.pisdf.features.RenameAbstractVertexFeature;
import org.preesm.ui.pisdf.features.RenameActorPortFeature;
import org.preesm.ui.pisdf.features.SetActorMemoryScriptFeature;
import org.preesm.ui.pisdf.features.SetActorRefinementFeature;
import org.preesm.ui.pisdf.features.SetFifoTypeFeature;
import org.preesm.ui.pisdf.features.SetPersistenceLevelFeature;
import org.preesm.ui.pisdf.features.SetPortMemoryAnnotationFeature;
import org.preesm.ui.pisdf.features.SetVisibleAllDependenciesFeature;
import org.preesm.ui.pisdf.features.SetVisibleDependenciesFromParameterFeature;
import org.preesm.ui.pisdf.features.UpdateAbstractVertexFeature;
import org.preesm.ui.pisdf.features.UpdateActorFeature;
import org.preesm.ui.pisdf.features.UpdateDiagramFeature;
import org.preesm.ui.pisdf.features.UpdatePortFeature;
import org.preesm.ui.pisdf.layout.AutoLayoutFeature;

/**
 * {@link DefaultFeatureProvider} for the {@link Diagram} with type {@link PiMMFeatureProvider}.
 *
 * @author kdesnos
 * @author jheulot
 *
 */
public class PiMMFeatureProvider extends DefaultFeatureProvider {

  private boolean editable = true;

  public final boolean isEditable() {
    return this.editable;
  }

  public final void setEditable(final boolean editable) {
    this.editable = editable;
  }

  /**
   * Instantiates a new pi MM feature provider.
   *
   * @param dtp
   *          the dtp
   */
  public PiMMFeatureProvider(final IDiagramTypeProvider dtp) {
    super(dtp);
  }

  /**
   * Simple switch to select what IAddFeature to return given the object type;
   *
   * @author anmorvan
   *
   */
  private class PiMMAddFeatureSelectionSwitch extends PiMMSwitch<IAddFeature> {

    @Override
    public IAddFeature caseActor(final Actor object) {
      return new AddActorFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseBroadcastActor(final BroadcastActor object) {
      return new AddBroadcastActorFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseInitActor(InitActor object) {
      return new AddInitActorFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseEndActor(EndActor object) {
      return new AddEndActorFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseJoinActor(final JoinActor object) {
      return new AddJoinActorFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseForkActor(final ForkActor object) {
      return new AddForkActorFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseRoundBufferActor(final RoundBufferActor object) {
      return new AddRoundBufferActorFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseParameter(final Parameter object) {
      if (object.isConfigurationInterface()) {
        return new AddConfigInputInterfaceFeature(PiMMFeatureProvider.this);
      } else {
        return new AddParameterFeature(PiMMFeatureProvider.this);
      }
    }

    @Override
    public IAddFeature caseConfigInputInterface(final ConfigInputInterface object) {
      return new AddConfigInputInterfaceFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseDataInputInterface(final DataInputInterface object) {
      return new AddDataInputInterfaceFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseDataOutputInterface(final DataOutputInterface object) {
      return new AddDataOutputInterfaceFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseConfigOutputInterface(final ConfigOutputInterface object) {
      return new AddConfigOutputInterfaceFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseFifo(final Fifo object) {
      return new AddFifoFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IAddFeature caseDependency(final Dependency object) {
      return new AddDependencyFeature(PiMMFeatureProvider.this);
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getAddFeature(org.eclipse.graphiti.features.context.
   * IAddContext)
   */
  @Override
  public IAddFeature getAddFeature(final IAddContext context) {
    if (!isEditable()) {
      return null;
    }
    final Object newObject = context.getNewObject();
    final IAddFeature addFeature;
    if (newObject instanceof EObject) {
      final PiMMAddFeatureSelectionSwitch piMMAddFeatureSelectionSwitch = new PiMMAddFeatureSelectionSwitch();
      addFeature = piMMAddFeatureSelectionSwitch.doSwitch((EObject) newObject);
    } else if (newObject instanceof IFile) {
      final Object businessObjectForPictogramElement = getBusinessObjectForPictogramElement(
          context.getTargetContainer());
      if (businessObjectForPictogramElement instanceof Actor || businessObjectForPictogramElement instanceof Delay) {
        addFeature = new AddRefinementFeature(this);
      } else if (businessObjectForPictogramElement instanceof PiGraph) {
        addFeature = new AddActorFromRefinementFeature(this);
      } else {
        addFeature = null;
      }
    } else {
      addFeature = null;
    }

    return addFeature;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getCreateConnectionFeatures()
   */
  @Override
  public ICreateConnectionFeature[] getCreateConnectionFeatures() {
    if (!isEditable()) {
      return new ICreateConnectionFeature[0];
    }
    return new ICreateConnectionFeature[] { new CreateFifoFeature(this), new CreateDependencyFeature(this) };
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getCreateFeatures()
   */
  @Override
  public ICreateFeature[] getCreateFeatures() {
    if (!isEditable()) {
      return new ICreateFeature[0];
    }
    return new ICreateFeature[] { new CreateActorFeature(this), new CreateParameterFeature(this),
        new CreateConfigInputInterfaceFeature(this), new CreateConfigOutputInterfaceFeature(this),
        new CreateDataInputInterfaceFeature(this), new CreateDataOutputInterfaceFeature(this),
        new CreateBroadcastActorFeature(this), new CreateJoinActorFeature(this), new CreateForkActorFeature(this),
        new CreateRoundBufferActorFeature(this) };
  }

  @Override
  public ICopyFeature getCopyFeature(final ICopyContext context) {
    return new CopyFeature(this);
  }

  @Override
  public IPasteFeature getPasteFeature(final IPasteContext context) {
    if (!isEditable()) {
      return null;
    }
    return new PasteFeature(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getCustomFeatures(org.eclipse.graphiti.features.context.
   * ICustomContext)
   */
  @Override
  public ICustomFeature[] getCustomFeatures(final ICustomContext context) {
    if (!isEditable()) {
      return new ICustomFeature[0];
    }

    final ArrayList<ICustomFeature> features = new ArrayList<>();

    final PictogramElement[] pes = context.getPictogramElements();
    if (pes.length != 1) {
      return new ICustomFeature[0];
    }
    final Object obj = getBusinessObjectForPictogramElement(pes[0]);

    if (obj instanceof DelayActor) {
      return new ICustomFeature[0];
    }

    if (obj instanceof PiGraph) {
      features.add(new SetVisibleAllDependenciesFeature(this, true));
      features.add(new SetVisibleAllDependenciesFeature(this, false));
      features.add(new AutoLayoutFeature(this));
      features.add(new ExportSVGFeature(this));
    }

    if (obj instanceof AbstractVertex) {
      features.add(new RenameAbstractVertexFeature(this));
    }

    if (obj instanceof ExecutableActor || obj instanceof EndActor || obj instanceof InitActor) {
      final ICustomFeature[] actorFeatures = new ICustomFeature[] { new AddDataOutputPortFeature(this),
          new AddDataInputPortFeature(this), new AddConfigInputPortFeature(this),
          new AddConfigOutputPortFeature(this) };
      for (final ICustomFeature feature : actorFeatures) {
        features.add(feature);
      }
    }
    if (obj instanceof Actor) {
      final ICustomFeature[] actorFeatures = new ICustomFeature[] { new SetActorRefinementFeature(this),
          new ClearActorRefinementFeature(this), new OpenRefinementFeature(this), new SetActorMemoryScriptFeature(this),
          new ClearActorMemoryScriptFeature(this), new OpenMemoryScriptFeature(this) };
      for (final ICustomFeature feature : actorFeatures) {
        features.add(feature);
      }
    }

    if (obj instanceof Parameter) {
      features.add(new SetVisibleDependenciesFromParameterFeature(this, true));
      features.add(new SetVisibleDependenciesFromParameterFeature(this, false));
    }

    if (obj instanceof Port) {
      features.add(new RenameActorPortFeature(this));
      features.add(new MoveUpActorPortFeature(this));
      features.add(new MoveDownActorPortFeature(this));
    }

    if (obj instanceof DataPort) {
      features.add(new SetPortMemoryAnnotationFeature(this));
    }

    if (obj instanceof Fifo) {
      features.add(new AddDelayFeature(this));
      features.add(new SetFifoTypeFeature(this));
    }

    if (obj instanceof Delay) {
      features.add(new SetPersistenceLevelFeature(this));
    }

    return features.toArray(new ICustomFeature[features.size()]);

  }

  /**
   *
   * @author anmorvan
   *
   */
  private class PiMMDeleteFeatureSelectionSwitch extends PiMMSwitch<IDeleteFeature> {

    @Override
    public IDeleteFeature casePort(final Port object) {

      final EObject eContainer = object.eContainer();
      if (eContainer instanceof ExecutableActor || eContainer instanceof EndActor || eContainer instanceof InitActor) {
        return new DeleteActorPortFeature(PiMMFeatureProvider.this);
      } else if (eContainer instanceof InterfaceActor) {
        // We do not allow deletion of the port of an InterfaceVertex
        // through the GUI
        return new DefaultDeleteFeature(PiMMFeatureProvider.this) {
          @Override
          public boolean canDelete(final IDeleteContext context) {
            return false;
          }
        };
      } else {
        return new DefaultDeleteFeature(PiMMFeatureProvider.this);
      }
    }

    @Override
    public IDeleteFeature caseAbstractActor(final AbstractActor object) {
      return new DeleteAbstractActorFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IDeleteFeature caseParameter(final Parameter object) {
      return new DeleteParameterizableFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IDeleteFeature caseFifo(final Fifo object) {
      return new DeleteFifoFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IDeleteFeature caseDependency(final Dependency object) {
      return new DeleteDependencyFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IDeleteFeature caseDelay(final Delay object) {
      return new DeleteDelayFeature(PiMMFeatureProvider.this);
    }

    @Override
    public IDeleteFeature defaultCase(final EObject object) {
      return new DefaultDeleteFeature(PiMMFeatureProvider.this);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getDeleteFeature(org.eclipse.graphiti.features.context.
   * IDeleteContext)
   */
  @Override
  public IDeleteFeature getDeleteFeature(final IDeleteContext context) {
    if (!isEditable()) {
      return null;
    }

    final PictogramElement pe = context.getPictogramElement();
    final Object bo = getBusinessObjectForPictogramElement(pe);
    final IDeleteFeature delFeature;
    if (bo instanceof EObject) {
      final PiMMDeleteFeatureSelectionSwitch piMMDeleteFeatureSelectionSwitch = new PiMMDeleteFeatureSelectionSwitch();
      delFeature = piMMDeleteFeatureSelectionSwitch.doSwitch((EObject) bo);
    } else {
      delFeature = new DefaultDeleteFeature(this);
    }

    return delFeature;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getDirectEditingFeature(org.eclipse.graphiti.features.
   * context.IDirectEditingContext)
   */
  @Override
  public IDirectEditingFeature getDirectEditingFeature(final IDirectEditingContext context) {
    if (!isEditable()) {
      return null;
    }
    final PictogramElement pe = context.getPictogramElement();
    final Object bo = getBusinessObjectForPictogramElement(pe);
    if (bo instanceof AbstractVertex) {
      return new DirectEditingAbstractActorNameFeature(this);
    }
    return super.getDirectEditingFeature(context);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getLayoutFeature(org.eclipse.graphiti.features.context.
   * ILayoutContext)
   */
  @Override
  public ILayoutFeature getLayoutFeature(final ILayoutContext context) {
    if (!isEditable()) {
      return null;
    }
    final PictogramElement pictogramElement = context.getPictogramElement();
    final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    if (bo instanceof ExecutableActor || bo instanceof EndActor || bo instanceof InitActor) {
      return new LayoutActorFeature(this);
    }
    if (bo instanceof Port) {
      return new LayoutPortFeature(this);
    }
    if (bo instanceof InterfaceActor) {
      return new LayoutInterfaceFeature(this);
    }
    if (bo instanceof Parameter) {
      if (((Parameter) bo).isConfigurationInterface()) {
        return new LayoutInterfaceFeature(this);
      } else {
        return new LayoutParameterFeature(this);
      }
    }
    return super.getLayoutFeature(context);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getMoveAnchorFeature(org.eclipse.graphiti.features.context.
   * IMoveAnchorContext)
   */
  @Override
  public IMoveAnchorFeature getMoveAnchorFeature(final IMoveAnchorContext context) {
    // We forbid the user from moving anchors
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getMoveShapeFeature(org.eclipse.graphiti.features.context.
   * IMoveShapeContext)
   */
  @Override
  public IMoveShapeFeature getMoveShapeFeature(final IMoveShapeContext context) {
    if (!isEditable()) {
      return null;
    }
    final PictogramElement pe = context.getPictogramElement();
    final Object bo = getBusinessObjectForPictogramElement(pe);
    if (bo instanceof AbstractActor) {
      return new MoveAbstractActorFeature(this);
    }
    return super.getMoveShapeFeature(context);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getReconnectionFeature(org.eclipse.graphiti.features.
   * context.IReconnectionContext)
   */
  @Override
  public IReconnectionFeature getReconnectionFeature(final IReconnectionContext context) {
    if (!isEditable()) {
      return null;
    }

    final Connection connection = context.getConnection();
    final Object obj = getBusinessObjectForPictogramElement(connection);

    if (obj instanceof EObject) {
      return new PiMMSwitch<IReconnectionFeature>() {
        @Override
        public IReconnectionFeature caseFifo(final Fifo object) {
          return new ReconnectionFifoFeature(PiMMFeatureProvider.this);
        }

        @Override
        public IReconnectionFeature caseDependency(final Dependency object) {
          return new ReconnectionDependencyFeature(PiMMFeatureProvider.this);
        }
      }.doSwitch((EObject) obj);
    }

    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getRemoveFeature(org.eclipse.graphiti.features.context.
   * IRemoveContext)
   */
  @Override
  public IRemoveFeature getRemoveFeature(final IRemoveContext context) {
    if (!isEditable()) {
      return null;
    }
    return new DefaultRemoveFeature(this) {
      @Override
      public boolean isAvailable(final IContext context) {
        return false;
      }
    };
  }

  /**
   * Provide the default remove feature when needed. This will be used in the deletion feature.
   *
   * @param context
   *          the context
   * @return remove feature according to the given context
   * @see PiMMFeatureProviderWithRemove
   * @see http ://www.eclipse.org/forums/index.php/mv/msg/234410/720417/#msg_720417
   */
  protected IRemoveFeature getRemoveFeatureEnabled(final IRemoveContext context) {
    return super.getRemoveFeature(context); // used where we enable remove
    // (deleting...)
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getResizeShapeFeature(org.eclipse.graphiti.features.context
   * .IResizeShapeContext)
   */
  @Override
  public IResizeShapeFeature getResizeShapeFeature(final IResizeShapeContext context) {
    final PictogramElement pictogramElement = context.getPictogramElement();
    if (pictogramElement instanceof ContainerShape) {
      final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
      if (bo instanceof ExecutableActor || bo instanceof EndActor || bo instanceof InitActor) {
        // We do not allow manual resize of Actor's pictogram elements.
        // The size of these elements will be computed automatically
        // to fit the content of the shape
        return null;
      }

      if (bo instanceof InterfaceActor) {
        // We do not allow manual resize of Interface Actor's pictogram
        // elements.
        // The size of these elements will be computed automatically
        // to fit the content of the shape
        return null;
      }

      if (bo instanceof Parameter) {
        // We do not allow manual resize of Parameter's pictogram
        // elements.
        // The size of these elements will be computed automatically
        // to fit the content of the shape
        return null;
      }

      if (bo instanceof Delay) {
        // We do not allow manual resize of Delay's pictogram elements.
        // The size of these elements will be computed automatically
        // to fit the content of the shape
        return null;
      }
    }
    return super.getResizeShapeFeature(context);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getUpdateFeature(org.eclipse.graphiti.features.context.
   * IUpdateContext)
   */
  @Override
  public IUpdateFeature getUpdateFeature(final IUpdateContext context) {
    if (!isEditable()) {
      return null;
    }
    final PictogramElement pictogramElement = context.getPictogramElement();
    if (pictogramElement instanceof Diagram) {
      return new UpdateDiagramFeature(this);
    }
    if (pictogramElement instanceof ContainerShape) {
      final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
      if (bo instanceof ExecutableActor || bo instanceof EndActor || bo instanceof InitActor) {
        return new UpdateActorFeature(this);
      } else if (bo instanceof AbstractVertex) {
        return new UpdateAbstractVertexFeature(this);
      }

    }
    if (pictogramElement instanceof BoxRelativeAnchor) {
      final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
      if (bo instanceof Port) {
        return new UpdatePortFeature(this);
      }
    }
    return super.getUpdateFeature(context);
  }

}

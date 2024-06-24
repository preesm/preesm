package org.preesm.ui.pisdf.features;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * Common implementation for all deletion features.
 *
 * @author ahonorat
 *
 */
public class DeletePiMMelementFeature extends DefaultDeleteFeature {

  protected PictogramElement pe;
  protected EObject          pimmObject;
  protected PiGraph          containingPiGraph;

  public DeletePiMMelementFeature(IFeatureProvider fp) {
    super(fp);
    pe = null;
    pimmObject = null;
    containingPiGraph = null;
  }

  /*
   * Disable the Delete feature if the selected pictogram element is the diagram itself.
   */
  @Override
  public boolean isAvailable(IContext context) {
    return !(context instanceof final IDeleteContext delContext
        && (delContext.getPictogramElement() instanceof Diagram));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.ui.features.DefaultDeleteFeature#preDelete(org.eclipse.graphiti.features.context.
   * IDeleteContext)
   */
  @Override
  public void preDelete(final IDeleteContext context) {
    super.preDelete(context);

    pe = context.getPictogramElement();
    if (pe != null) {
      pimmObject = (EObject) getBusinessObjectForPictogramElement(pe);
      containingPiGraph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.ui.features.DefaultDeleteFeature#postDelete(org.eclipse.graphiti.features.context.
   * IDeleteContext)
   */
  @Override
  public void postDelete(final IDeleteContext context) {
    super.postDelete(context);
    if (containingPiGraph != null && pimmObject != null) {
      final PiGraphElementDeletionSwitch deleteSwitch = new PiGraphElementDeletionSwitch();
      deleteSwitch.doSwitch(pimmObject);
    }
    pe = null;
    pimmObject = null;
    containingPiGraph = null;
  }

  private class PiGraphElementDeletionSwitch extends PiMMSwitch<Boolean> {

    @Override
    public Boolean caseAbstractActor(final AbstractActor actor) {
      return containingPiGraph.removeActor(actor);
    }

    @Override
    public Boolean caseParameter(final Parameter param) {
      return containingPiGraph.removeParameter(param);
    }

    @Override
    public Boolean caseFifo(final Fifo fifo) {
      return containingPiGraph.removeFifo(fifo);
    }

    @Override
    public Boolean caseDelay(final Delay delay) {
      return containingPiGraph.removeDelay(delay);
    }

    @Override
    public Boolean caseDependency(final Dependency dep) {
      return containingPiGraph.removeDependency(dep);
    }

    @Override
    public Boolean caseDataInputPort(final DataInputPort dip) {
      final AbstractActor aa = dip.getContainingActor();
      if (aa != null) {
        return aa.getDataInputPorts().remove(dip);
      }
      return true;
    }

    @Override
    public Boolean caseDataOutputPort(final DataOutputPort dop) {
      final AbstractActor aa = dop.getContainingActor();
      if (aa != null) {
        return aa.getDataOutputPorts().remove(dop);
      }
      return true;
    }

    @Override
    public Boolean caseConfigInputPort(final ConfigInputPort cip) {
      final Configurable c = cip.getConfigurable();
      if (c != null) {
        return c.getConfigInputPorts().remove(cip);
      }
      return true;
    }

    @Override
    public Boolean caseConfigOutputPort(final ConfigOutputPort cop) {
      final AbstractActor aa = cop.getContainingActor();
      if (aa != null) {
        return aa.getConfigOutputPorts().remove(cop);
      }
      return true;
    }

  }

}

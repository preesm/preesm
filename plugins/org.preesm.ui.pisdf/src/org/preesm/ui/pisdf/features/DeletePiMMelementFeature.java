/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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

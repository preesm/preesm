/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.preesm.model.pisdf.ExecutableActor;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateActorFeature.
 */
public class UpdateActorFeature extends UpdateAbstractVertexFeature {

  /**
   * Default constructor of the {@link UpdateActorFeature}.
   *
   * @param fp
   *          the fp
   */
  public UpdateActorFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.ui.pimm.features.UpdateAbstractVertexFeature#canUpdate(org.eclipse.graphiti.features.context.
   * IUpdateContext)
   */
  @Override
  public boolean canUpdate(final IUpdateContext context) {
    final Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return (bo instanceof ExecutableActor);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.preesm.ui.pimm.features.UpdateAbstractVertexFeature#updateNeeded(org.eclipse.graphiti.features.context.
   * IUpdateContext)
   */
  @Override
  public IReason updateNeeded(final IUpdateContext context) {

    // Check if a name update is required
    final IReason ret = super.updateNeeded(context);

    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.ui.pimm.features.UpdateAbstractVertexFeature#update(org.eclipse.graphiti.features.context.
   * IUpdateContext)
   */
  @Override
  public boolean update(final IUpdateContext context) {
    return super.updateName(context);
  }
}

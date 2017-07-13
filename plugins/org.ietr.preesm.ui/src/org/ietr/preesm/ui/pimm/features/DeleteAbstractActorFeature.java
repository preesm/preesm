/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.ietr.preesm.ui.pimm.features;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;

/**
 * Delete Feature for {@link AbstractActor}.
 *
 * @author kdesnos
 */
public class DeleteAbstractActorFeature extends DeleteParameterizableFeature {

  /**
   * Default constructor of {@link DeleteAbstractActorFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public DeleteAbstractActorFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.ui.pimm.features.DeleteParameterizableFeature#preDelete(org.eclipse.graphiti.features.context.IDeleteContext)
   */
  @Override
  public void preDelete(final IDeleteContext context) {
    super.preDelete(context);

    // Delete all the Fifo and dependencies linked to this actor
    final ContainerShape cs = (ContainerShape) context.getPictogramElement();

    // First create all the deleteFeatures and their context and store them
    // in a Map. (this is because cs.getAnchor cannot be modified while
    // iterated on)
    final Map<IDeleteFeature, IDeleteContext> delFeatures = new LinkedHashMap<>();
    for (final Anchor anchor : cs.getAnchors()) {
      // Skip the current iteration if the anchor is not a
      // BoxRelativeAnchor
      // The anchor can be a ChopBox anchor (for dependencies)
      if (!(anchor instanceof BoxRelativeAnchor)) {
        continue;
      }

      final DeleteActorPortFeature delPortFeature = new DeleteActorPortFeature(getFeatureProvider());
      final DeleteContext delCtxt = new DeleteContext(anchor);
      final MultiDeleteInfo multi = new MultiDeleteInfo(false, false, 0);
      delCtxt.setMultiDeleteInfo(multi);
      delFeatures.put(delPortFeature, delCtxt);
    }

    // Actually delete
    for (final Entry<IDeleteFeature, IDeleteContext> deleteEntry : delFeatures.entrySet()) {
      final IDeleteFeature deleteFeature = deleteEntry.getKey();
      final IDeleteContext deleteContext = deleteEntry.getValue();
      deleteFeature.delete(deleteContext);
    }
  }
}

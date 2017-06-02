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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.ietr.preesm.experiment.model.pimm.Dependency;

// TODO: Auto-generated Javadoc
/**
 * Delete feature for {@link Parameterizable}s elements.
 *
 * @author kdesnos
 *
 */
public class DeleteParameterizableFeature extends DefaultDeleteFeature {

  /**
   * Default constructor for the {@link DeleteParameterizableFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public DeleteParameterizableFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /**
   * Method to delete the {@link Dependency} connected to the deleted {@link Parameterizable} element.
   *
   * @param cba
   *          the {@link ChopboxAnchor} of the deleted element
   */
  protected void deleteConnectedConnection(final ChopboxAnchor cba) {
    // First, the list of connections is scanned in order to fill a map with
    // the deleteFeatures and their context.
    Map<IDeleteFeature, IDeleteContext> delFeatures;
    delFeatures = new HashMap<>();
    final EList<Connection> connections = cba.getIncomingConnections();
    connections.addAll(cba.getOutgoingConnections());
    for (final Connection connect : connections) {
      final DeleteContext delCtxt = new DeleteContext(connect);
      delCtxt.setMultiDeleteInfo(null);
      final IDeleteFeature delFeature = getFeatureProvider().getDeleteFeature(delCtxt);
      if (delFeature.canDelete(delCtxt)) {
        // Cannot delete directly because this would mean concurrent
        // modifications of the connections Elist
        delCtxt.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 0));
        delFeatures.put(delFeature, delCtxt);
      }
    }

    // Actually delete
    for (final IDeleteFeature delFeature : delFeatures.keySet()) {
      delFeature.delete(delFeatures.get(delFeature));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.ui.features.DefaultDeleteFeature#preDelete(org.eclipse.graphiti.features.context.IDeleteContext)
   */
  @Override
  public void preDelete(final IDeleteContext context) {
    super.preDelete(context);

    // Delete all the dependencies linked to this parameterizable element
    final ContainerShape cs = (ContainerShape) context.getPictogramElement();

    // Scan the anchors (There should be only one ChopBoxAnchor)
    for (final Anchor anchor : cs.getAnchors()) {
      if (anchor instanceof ChopboxAnchor) {
        deleteConnectedConnection((ChopboxAnchor) anchor);
      }
    }
  }
}

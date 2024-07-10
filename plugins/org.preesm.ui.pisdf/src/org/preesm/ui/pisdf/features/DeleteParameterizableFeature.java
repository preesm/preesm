/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2021) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2013)
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.preesm.model.pisdf.Dependency;

/**
 * Delete feature for {@link Parameterizable}s elements.
 *
 * @author kdesnos
 *
 */
public class DeleteParameterizableFeature extends DeletePiMMelementFeature {

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
   * Fill the map with connections to delete.
   * 
   * @param delFeatures
   *          Map.
   * @param connecs
   *          Connections to delete.
   */
  protected void fillDeleteMap(Map<IDeleteFeature, IDeleteContext> delFeatures, EList<Connection> connecs) {
    for (final Connection connect : connecs) {
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
  }

  /**
   * Method to delete the {@link Dependency} connected to the deleted {@link Parameterizable} element.
   *
   * @param cba
   *          the {@link ChopboxAnchor} of the deleted element
   */
  protected void deleteConnectedConnection(final Anchor cba) {
    // First, the list of connections is scanned in order to fill a map with
    // the deleteFeatures and their context.
    Map<IDeleteFeature, IDeleteContext> delFeatures;
    delFeatures = new LinkedHashMap<>();
    fillDeleteMap(delFeatures, cba.getOutgoingConnections());
    fillDeleteMap(delFeatures, cba.getIncomingConnections());

    // Actually delete
    for (final Entry<IDeleteFeature, IDeleteContext> deleteEntry : delFeatures.entrySet()) {
      final IDeleteFeature deleteFeature = deleteEntry.getKey();
      final IDeleteContext deleteContext = deleteEntry.getValue();
      deleteFeature.delete(deleteContext);
    }
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

    // Delete all the dependencies linked to this parameterizable element
    final ContainerShape cs = (ContainerShape) pe;

    // Scan the anchors
    final EList<Anchor> anchors = cs.getAnchors();
    for (final Anchor anchor : anchors) {
      // hack ... should be the same behavior for all anchor type
      if (anchor instanceof ChopboxAnchor) {
        // case Parameter or Actor
        deleteConnectedConnection(anchor);
      } else if (anchor instanceof BoxRelativeAnchor) {
        // case ConfigInputInterface
        deleteConnectedConnection(anchor);
      } else {
        throw new UnsupportedOperationException("Unsupported anchor type");
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.ui.features.DefaultDeleteFeature#canDelete(org.eclipse.graphiti.features.context.
   * IDeleteContext)
   */
  @Override
  public boolean canDelete(final IDeleteContext context) {
    // if the small triangle (BoxRelativAnchor) below the interface name is selected,
    // we do not authorize the deletion
    return (context.getPictogramElement() instanceof ContainerShape);
  }

}

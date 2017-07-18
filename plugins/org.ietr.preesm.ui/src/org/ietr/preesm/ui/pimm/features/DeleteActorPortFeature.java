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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IMultiDeleteInfo;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * Delete feature for the ports.
 *
 * @author kdesnos
 */
public class DeleteActorPortFeature extends DefaultDeleteFeature {

  /**
   * Default constructor.
   *
   * @param fp
   *          the fp
   */
  public DeleteActorPortFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.ui.features.DefaultDeleteFeature#delete(org.eclipse.graphiti.features.context.IDeleteContext)
   */
  @Override
  public void delete(final IDeleteContext context) {
    // Retrieve the graphic algorithm of the enclosing actor
    final BoxRelativeAnchor bra = (BoxRelativeAnchor) context.getPictogramElement();

    final GraphicsAlgorithm actorGA = bra.getReferencedGraphicsAlgorithm();

    // fetch user decision beforehand
    final boolean userDecision;
    final IMultiDeleteInfo multiDeleteInfo = context.getMultiDeleteInfo();
    if (multiDeleteInfo != null) {
      userDecision = !multiDeleteInfo.isShowDialog();
    } else {
      userDecision = super.getUserDecision(context);
    }

    if (userDecision) {
      // create a new delete context to avoid having multiple popups during deletion
      final DeleteContext deleteContext;
      deleteContext = new DeleteContext(context.getPictogramElement());
      deleteContext.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 0));

      // Begin by deleting the Fifos or dependencies linked to this port
      deleteConnectedConnection(bra);

      // Force the layout computation
      layoutPictogramElement(actorGA.getPictogramElement());
      // Delete the port
      super.delete(deleteContext);
    }

  }

  /**
   * Method to delete the {@link Fifo} or {@link Dependency} connected to the deleted {@link Port}.
   *
   * @param bra
   *          the {@link BoxRelativeAnchor} of the deleted {@link Port}
   */
  protected void deleteConnectedConnection(final BoxRelativeAnchor bra) {
    // First, the list of connections is scanned in order to fill a map with
    // the deleteFeatures and their context.
    Map<IDeleteFeature, IDeleteContext> delFeatures;
    delFeatures = new LinkedHashMap<>();

    final List<Connection> connections = new ArrayList<>();
    connections.addAll(bra.getIncomingConnections());
    connections.addAll(bra.getOutgoingConnections());

    for (final Connection connect : connections) {

      final DeleteContext delCtxt = new DeleteContext(connect);
      delCtxt.setMultiDeleteInfo(null);
      final IDeleteFeature delFeature = getFeatureProvider().getDeleteFeature(delCtxt);
      if (delFeature.canDelete(delCtxt)) {
        // To deactivate dialog box
        delCtxt.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 0));

        // Cannot delete directly because this would mean concurrent
        // modifications of the connections Elist
        delFeatures.put(delFeature, delCtxt);
      }
    }

    // Actually delete
    for (final Entry<IDeleteFeature, IDeleteContext> deleteEntry : delFeatures.entrySet()) {
      final IDeleteFeature deleteFeature = deleteEntry.getKey();
      final IDeleteContext deleteContext = deleteEntry.getValue();
      deleteFeature.delete(deleteContext);
    }
  }

}

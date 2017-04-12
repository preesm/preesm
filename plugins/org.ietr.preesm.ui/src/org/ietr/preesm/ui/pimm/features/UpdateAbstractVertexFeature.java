/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateAbstractVertexFeature.
 */
public class UpdateAbstractVertexFeature extends AbstractUpdateFeature {

  /**
   * Instantiates a new update abstract vertex feature.
   *
   * @param fp
   *          the fp
   */
  public UpdateAbstractVertexFeature(final IFeatureProvider fp) {
    super(fp);
    // TODO Auto-generated constructor stub
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IUpdate#canUpdate(org.eclipse.graphiti.features.context.IUpdateContext)
   */
  @Override
  public boolean canUpdate(final IUpdateContext context) {
    final Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return (bo instanceof AbstractVertex);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IUpdate#updateNeeded(org.eclipse.graphiti.features.context.IUpdateContext)
   */
  @Override
  public IReason updateNeeded(final IUpdateContext context) {
    final IReason ret = nameUpdateNeeded(context);
    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IUpdate#update(org.eclipse.graphiti.features.context.IUpdateContext)
   */
  @Override
  public boolean update(final IUpdateContext context) {
    final boolean res = updateName(context);
    layoutPictogramElement(context.getPictogramElement());
    return res;
  }

  /**
   * Name update needed.
   *
   * @param context
   *          the context
   * @return the i reason
   */
  protected IReason nameUpdateNeeded(final IUpdateContext context) {
    // retrieve name from pictogram model
    String pictogramName = null;
    final PictogramElement pictogramElement = context.getPictogramElement();
    if (pictogramElement instanceof ContainerShape) {
      final ContainerShape cs = (ContainerShape) pictogramElement;
      for (final Shape shape : cs.getChildren()) {
        if (shape.getGraphicsAlgorithm() instanceof Text) {
          final Text text = (Text) shape.getGraphicsAlgorithm();
          pictogramName = text.getValue();
        }
      }
    }

    // retrieve AbstractVertex name from business model (from the graph)
    String businessName = null;
    final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    if (bo instanceof AbstractVertex) {
      final AbstractVertex vertex = (AbstractVertex) bo;
      businessName = vertex.getName();
    }

    // update needed, if names are different
    final boolean updateNameNeeded = (((pictogramName == null) && (businessName != null)) || ((pictogramName != null) && !pictogramName.equals(businessName)));
    if (updateNameNeeded) {
      return Reason.createTrueReason("Name is out of date\nNew name: " + businessName);
    }

    return Reason.createFalseReason();
  }

  /**
   * Update name.
   *
   * @param context
   *          the context
   * @return true, if successful
   */
  protected boolean updateName(final IUpdateContext context) {
    // retrieve name from business model
    String businessName = null;
    final PictogramElement pictogramElement = context.getPictogramElement();
    final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    if (bo instanceof AbstractVertex) {
      final AbstractVertex vertex = (AbstractVertex) bo;
      businessName = vertex.getName();
    }

    // Set name in pictogram model
    if (pictogramElement instanceof ContainerShape) {
      final ContainerShape cs = (ContainerShape) pictogramElement;
      for (final Shape shape : cs.getChildren()) {
        if (shape.getGraphicsAlgorithm() instanceof Text) {
          final Text text = (Text) shape.getGraphicsAlgorithm();
          text.setValue(businessName);
          return true;
        }
      }
    }
    // Update not completed
    return false;
  }

}

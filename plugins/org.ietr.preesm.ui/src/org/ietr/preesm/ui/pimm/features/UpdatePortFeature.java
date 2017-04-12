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
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Port;

// TODO: Auto-generated Javadoc
/**
 * Feature to update a port.
 *
 * @author kdesnos
 */
public class UpdatePortFeature extends AbstractUpdateFeature {

  /**
   * Default constructor of the {@link UpdatePortFeature}.
   *
   * @param fp
   *          the fp
   */
  public UpdatePortFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.graphiti.func.IUpdate#canUpdate(org.eclipse.graphiti.features.context.IUpdateContext)
   */
  @Override
  public boolean canUpdate(final IUpdateContext context) {
    final Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return (bo instanceof Port);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.graphiti.func.IUpdate#updateNeeded(org.eclipse.graphiti.features.context.IUpdateContext)
   */
  @Override
  public IReason updateNeeded(final IUpdateContext context) {
    // retrieve name from pictogram model
    String pictogramName = null;
    final PictogramElement pictogramElement = context.getPictogramElement();
    if (pictogramElement instanceof BoxRelativeAnchor) {
      final BoxRelativeAnchor bra = (BoxRelativeAnchor) pictogramElement;
      // The label of the port is the only child with type Text
      for (final GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
        if (ga instanceof Text) {
          pictogramName = ((Text) ga).getValue();
        }
      }
    }

    // retrieve Port name from business model (from the graph)
    String businessName = null;
    final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    if (bo instanceof Port) {
      // if the container actor is a data interface
      // then the port has no name.
      if (((Port) bo).eContainer() instanceof InterfaceActor) {
        businessName = null;
      } else {
        final Port port = (Port) bo;
        businessName = port.getName();
      }
    }

    // update needed, if names are different
    final boolean updateNameNeeded = (((pictogramName == null) && (businessName != null)) || ((pictogramName != null) && !pictogramName.equals(businessName)));
    if (updateNameNeeded) {
      return Reason.createTrueReason("Name is out of date\nNew name: " + businessName);
    } else {
      return Reason.createFalseReason();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.graphiti.func.IUpdate#update(org.eclipse.graphiti.features.context.IUpdateContext)
   */
  @Override
  public boolean update(final IUpdateContext context) {
    // retrieve name from business model
    String businessName = null;
    final PictogramElement pictogramElement = context.getPictogramElement();
    final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    if (bo instanceof Port) {
      final Port port = (Port) bo;
      businessName = port.getName();
    }

    // Set name in pictogram model
    if (pictogramElement instanceof BoxRelativeAnchor) {
      final BoxRelativeAnchor bra = (BoxRelativeAnchor) pictogramElement;
      // The label of the port is the only child with type Text
      for (final GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
        if (ga instanceof Text) {
          ((Text) ga).setValue(businessName);
        }
      }
    }

    layoutPictogramElement(pictogramElement);

    // Call the layout feature
    final GraphicsAlgorithm bra = ((BoxRelativeAnchor) pictogramElement).getReferencedGraphicsAlgorithm();
    layoutPictogramElement(bra.getPictogramElement());

    // Update not completed
    return true;
  }

}

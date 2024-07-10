/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2014)
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
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.Port;

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
    if (pictogramElement instanceof final BoxRelativeAnchor bra) {
      // The label of the port is the only child with type Text
      for (final GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
        if (ga instanceof final Text text) {
          pictogramName = text.getValue();
        }
      }
    }

    // retrieve Port name from business model (from the graph)
    String businessName = null;
    final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    if (bo instanceof final Port port) {
      // if the container actor is a data interface then the port has no name.
      if (((Port) bo).eContainer() instanceof InterfaceActor) {
        businessName = null;
      } else {
        businessName = port.getName();
      }
    }

    // update needed, if names are different
    final boolean updateNameNeeded = (((pictogramName == null) && (businessName != null))
        || ((pictogramName != null) && !pictogramName.equals(businessName)));
    if (updateNameNeeded) {
      return Reason.createTrueReason("Name <" + pictogramName + "> is out of date\nNew name: " + businessName);
    }
    return Reason.createFalseReason();
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
    if (bo instanceof final Port port) {
      businessName = port.getName();
    }

    // Set name in pictogram model
    if (pictogramElement instanceof final BoxRelativeAnchor bra) {
      // The label of the port is the only child with type Text
      for (final GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
        if (ga instanceof final Text text) {
          text.setValue(businessName);
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

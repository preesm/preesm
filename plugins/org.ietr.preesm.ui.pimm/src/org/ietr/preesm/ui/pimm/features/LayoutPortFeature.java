/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ietr.preesm.experiment.model.pimm.Port;

// TODO: Auto-generated Javadoc
/**
 * Layout Feature for Ports.
 *
 * @author kdesnos
 */
public class LayoutPortFeature extends AbstractLayoutFeature {

  /**
   * Default constructor of the {@link LayoutPortFeature}.
   *
   * @param fp
   *          the fp
   */
  public LayoutPortFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ILayout#canLayout(org.eclipse.graphiti.features.context.ILayoutContext)
   */
  @Override
  public boolean canLayout(final ILayoutContext context) {
    // return true, if pictogram element is linked to an Port
    final PictogramElement pe = context.getPictogramElement();
    if (!(pe instanceof BoxRelativeAnchor)) {
      return false;
    }

    final EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
    return (businessObjects.size() == 1) && (businessObjects.get(0) instanceof Port);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ILayout#layout(org.eclipse.graphiti.features.context.ILayoutContext)
   */
  @Override
  public boolean layout(final ILayoutContext context) {
    // Get the GaService
    final IGaService gaService = Graphiti.getGaService();
    // retrieve the boxRelativeAnchor and port
    final BoxRelativeAnchor bra = (BoxRelativeAnchor) context.getPictogramElement();
    final EList<EObject> businessObjects = bra.getLink().getBusinessObjects();
    final Port port = (Port) businessObjects.get(0);

    // Retrieve the size of the text
    final GraphicsAlgorithm graphicsAlgorithm = bra.getGraphicsAlgorithm();
    IDimension size = gaService.calculateSize(graphicsAlgorithm);
    for (final GraphicsAlgorithm ga : graphicsAlgorithm.getGraphicsAlgorithmChildren()) {
      if (ga instanceof Text) {
        final IDimension calculateTextSize = GraphitiUi.getUiLayoutService().calculateTextSize(port.getName(),
            ((Text) ga).getFont());
        size = calculateTextSize == null ? size : calculateTextSize;
      }
    }

    // define a few constant
    final int anchorGaSize = AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE;
    final int labelGaSpace = AbstractAddActorPortFeature.PORT_LABEL_GA_SPACE;
    final int portFontHeight = size.getHeight();

    // Layout the invisible rectangle
    if (bra.getRelativeWidth() == 0.0) {
      gaService.setLocationAndSize(graphicsAlgorithm, 0, 0, size.getWidth() + anchorGaSize + labelGaSpace,
          size.getHeight());
    } else {
      gaService.setLocationAndSize(graphicsAlgorithm, -size.getWidth() - anchorGaSize - labelGaSpace, 0,
          size.getWidth() + anchorGaSize + labelGaSpace, size.getHeight());
    }

    // Layout the children of the bra
    for (final GraphicsAlgorithm ga : graphicsAlgorithm.getGraphicsAlgorithmChildren()) {
      if (ga instanceof Text) {
        gaService.setWidth(ga, graphicsAlgorithm.getWidth() - anchorGaSize);
        if (bra.getRelativeWidth() == 0.0) {
          // input port
          gaService.setLocation(ga, anchorGaSize, 0);
        } else {
          // output port
          gaService.setLocation(ga, 0, 0);
        }
      }

      // Position the port anchor for data ports
      if (ga instanceof Rectangle) {
        if (bra.getRelativeWidth() == 0.0) {
          // input port
          gaService.setLocation(ga, 0, 0 + ((portFontHeight - anchorGaSize) / 2));
        } else {
          // output port
          gaService.setLocation(ga, graphicsAlgorithm.getWidth() - anchorGaSize,
              0 + ((portFontHeight - anchorGaSize) / 2));
        }
      }

      // Position the port anchor for configuration ports
      if (ga instanceof Polygon) {
        if (bra.getRelativeWidth() == 0.0) {
          // input port
          gaService.setLocation(ga, 0, -1 + ((portFontHeight - anchorGaSize) / 2));
          // portFontHeight - PORT_ANCHOR_GA_SIZE - 2) / 2
        } else {
          // output port
          gaService.setLocation(ga, graphicsAlgorithm.getWidth() - anchorGaSize - 1,
              -1 + ((portFontHeight - anchorGaSize) / 2));
        }
      }
    }
    return true;
  }

}

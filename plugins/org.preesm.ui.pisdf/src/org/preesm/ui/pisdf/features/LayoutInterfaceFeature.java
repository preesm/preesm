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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.Parameter;

/**
 * Layout Feature for {@link InterfaceActor} and Config Input Interface (i.e. {@link Parameter}).
 *
 * @author kdesnos
 *
 */
public class LayoutInterfaceFeature extends AbstractLayoutFeature {

  /**
   * Default constructor of the {@link LayoutInterfaceFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public LayoutInterfaceFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ILayout#canLayout(org.eclipse.graphiti.features.context.ILayoutContext)
   */
  @Override
  public boolean canLayout(final ILayoutContext context) {
    // return true, if pictogram element is linked to an InterfaceVertex or
    // a Parameter used as a configuration input interface
    final PictogramElement pe = context.getPictogramElement();
    if (!(pe instanceof ContainerShape)) {
      return false;
    }

    final EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
    return (businessObjects.size() == 1)
        && ((businessObjects.get(0) instanceof InterfaceActor) || ((businessObjects.get(0) instanceof Parameter)
            && ((Parameter) businessObjects.get(0)).isConfigurationInterface()));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ILayout#layout(org.eclipse.graphiti.features.context.ILayoutContext)
   */
  @Override
  public boolean layout(final ILayoutContext context) {
    // Retrieve the shape and the graphic algorithm
    final ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
    final GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();
    final AbstractVertex vertex = (AbstractVertex) getBusinessObjectForPictogramElement(containerShape);

    // Retrieve the size of the text
    IDimension size = GraphitiUi.getGaService().calculateSize(containerShape.getGraphicsAlgorithm());
    for (final Shape shape : containerShape.getChildren()) {
      final GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
      if (ga instanceof Text) {
        size = GraphitiUi.getUiLayoutService().calculateTextSize(vertex.getName(), ((Text) ga).getFont());
      }
    }

    if (vertex instanceof InterfaceActor) {
      layoutInterfaceActor(containerShape, containerGa, (InterfaceActor) vertex, size);
    }

    if (vertex instanceof Parameter) {
      layoutParameter(containerShape, containerGa, size);
    }
    return true;
  }

  private void layoutParameter(final ContainerShape containerShape, final GraphicsAlgorithm containerGa,
      final IDimension size) {
    final int width = (size.getWidth() < 18) ? 18 : size.getWidth();
    // Layout the invisible rectangle
    containerGa.setWidth(width);
    // Layout the label
    for (final Shape shape : containerShape.getChildren()) {
      final GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
      if (ga instanceof Text) {
        ga.setWidth(width);
      }
    }
  }

  private void layoutInterfaceActor(final ContainerShape containerShape, final GraphicsAlgorithm containerGa,
      final InterfaceActor vertex, final IDimension size) {
    // Layout the invisible rectangle
    containerGa.setWidth(size.getWidth() + 16 + 3);
    // Layout the label
    for (final Shape shape : containerShape.getChildren()) {
      final GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
      if (ga instanceof Text) {
        switch (vertex.getKind()) {
          case DATA_INPUT:
            ga.setWidth(size.getWidth());
            Graphiti.getGaService().setLocation(ga, 0, 0);
            break;
          case DATA_OUTPUT:
          case CFG_OUTPUT:
            ga.setWidth(size.getWidth());
            Graphiti.getGaService().setLocation(ga, 16 + 3, 0);
            break;
          default:
        }

      }
    }
  }

}

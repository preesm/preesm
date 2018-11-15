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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.func.IDirectEditing;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ietr.preesm.ui.pimm.util.VertexNameValidator;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * This class provide the feature that allows the direct edition of an {@link AbstractActor} name.
 *
 * @author kdesnos
 *
 */
public class DirectEditingAbstractActorNameFeature extends AbstractDirectEditingFeature {

  /**
   * Default constructor of the {@link DirectEditingAbstractActorNameFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public DirectEditingAbstractActorNameFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IDirectEditing#getEditingType()
   */
  @Override
  public int getEditingType() {
    return IDirectEditing.TYPE_TEXT;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature#canDirectEdit(org.eclipse.graphiti.features.context
   * .IDirectEditingContext)
   */
  @Override
  public boolean canDirectEdit(final IDirectEditingContext context) {
    final PictogramElement pe = context.getPictogramElement();
    final Object bo = getBusinessObjectForPictogramElement(pe);
    final GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
    // support direct editing, if it is a AbstractVertex, and the user clicked
    // directly on the text and not somewhere else in the rectangle
    if ((bo instanceof AbstractVertex) && (ga instanceof Text)) {
      return true;
    }
    // direct editing not supported in all other cases
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IDirectEditing#getInitialValue(org.eclipse.graphiti.features.context.
   * IDirectEditingContext)
   */
  @Override
  public String getInitialValue(final IDirectEditingContext context) {
    // return the current name of the AbstractVertex
    final PictogramElement pe = context.getPictogramElement();
    final AbstractVertex vertex = (AbstractVertex) getBusinessObjectForPictogramElement(pe);
    return vertex.getName();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature#checkValueValid(java.lang.String,
   * org.eclipse.graphiti.features.context.IDirectEditingContext)
   */
  @Override
  public String checkValueValid(final String value, final IDirectEditingContext context) {
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
    final PictogramElement pe = context.getPictogramElement();
    final AbstractVertex renamedVertex = (AbstractVertex) getBusinessObjectForPictogramElement(pe);
    final VertexNameValidator validator = new VertexNameValidator(graph, renamedVertex);

    return validator.isValid(value);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature#setValue(java.lang.String,
   * org.eclipse.graphiti.features.context.IDirectEditingContext)
   */
  @Override
  public void setValue(final String value, final IDirectEditingContext context) {
    // set the new name for the AbstractVertex
    final PictogramElement pe = context.getPictogramElement();
    final AbstractVertex vertex = (AbstractVertex) getBusinessObjectForPictogramElement(pe);
    vertex.setName(value);
    if (vertex instanceof InterfaceActor) {
      final InterfaceActor iface = (InterfaceActor) vertex;
      iface.getDataPort().setName(value);
    }

    // we know, that pe is the Shape of the Text, so its container is the
    // main shape of the AbstractVertex
    updatePictogramElement(((Shape) pe).getContainer());

    // Call the layout feature
    layoutPictogramElement(((Shape) pe).getContainer());
  }

}

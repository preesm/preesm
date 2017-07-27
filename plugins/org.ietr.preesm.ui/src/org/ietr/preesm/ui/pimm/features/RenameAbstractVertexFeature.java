/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
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
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.util.VertexNameValidator;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;

/**
 * Custom feature to rename an {@link AbstractVertex}.
 *
 * @author kdesnos
 *
 */
public class RenameAbstractVertexFeature extends AbstractCustomFeature {

  /** The has done changes. */
  protected boolean hasDoneChanges = false;

  /**
   * Default Constructor.
   *
   * @param fp
   *          the feature provider
   */
  public RenameAbstractVertexFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Rename\tF2";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Change the name";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
   */
  @Override
  public boolean canExecute(final ICustomContext context) {
    // allow rename if exactly one pictogram element
    // representing an AbstractVertex is selected
    boolean ret = false;
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length == 1)) {
      final Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof AbstractVertex) {
        ret = true;
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
   */
  @Override
  public void execute(final ICustomContext context) {
    // Retrieve the graph
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());

    // Re-check if only one element is selected
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length == 1)) {
      final Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof AbstractVertex) {
        final AbstractVertex vertex = (AbstractVertex) bo;
        final String currentName = vertex.getName();

        // Ask user for AbstractVertex name until a valid name is
        // entered.
        String className = bo.getClass().getSimpleName();
        className = className.substring(0, className.lastIndexOf("Impl"));
        final String question = "Enter new " + className + " name";
        String newVertexName = vertex.getName();

        newVertexName = PiMMUtil.askString("Rename " + className, question, newVertexName, new VertexNameValidator(graph, vertex));

        if ((newVertexName != null) && !newVertexName.equals(currentName)) {
          this.hasDoneChanges = true;
          vertex.setName(newVertexName);

          // Update the Pictogram element
          updatePictogramElement(pes[0]);

          // Call the layout feature
          layoutPictogramElement(pes[0]);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
   */
  @Override
  public boolean hasDoneChanges() {
    return this.hasDoneChanges;
  }

}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2024) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.func.ICreate;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.ui.pisdf.util.VertexNameValidator;
import org.preesm.ui.utils.DialogUtil;

public abstract class AbstractCreateConfigurableFeature extends AbstractCreateFeature {

  /** The has done changes. */
  protected Boolean hasDoneChanges;

  private Object[] objects;

  protected AbstractCreateConfigurableFeature(final IFeatureProvider fp, String featureName,
      String featureDescription) {
    // Set name and description of the creation feature
    super(fp, featureName, featureDescription);
    this.hasDoneChanges = false;
  }

  abstract String getFeatureDescription();

  abstract String getDefaultName();

  abstract String getQuestion();

  abstract Configurable createConfigurable(final String newConfigurableName);

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ICreate#canCreate(org.eclipse.graphiti.features.context.ICreateContext)
   */
  @Override
  public boolean canCreate(final ICreateContext context) {
    return context.getTargetContainer() instanceof Diagram;
  }

  @Override
  public Object[] create(final ICreateContext context) {
    // Retrieve the graph
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());

    // Ask user for Configurable name until a valid name is entered.
    // TODO create a parameter name validator ?
    final String newConfigurableName = DialogUtil.askString(getFeatureDescription(), getQuestion(), getDefaultName(),
        new VertexNameValidator(graph, null));
    if ((newConfigurableName == null) || (newConfigurableName.trim().length() == 0)) {
      this.hasDoneChanges = false; // If this is not done, the graph is considered modified.
      return ICreate.EMPTY;
    }

    // create Actor
    final Configurable newConfigurable = createConfigurable(newConfigurableName);

    // Add new configurable to the graph.
    if (graph.addConfigurable(newConfigurable)) {
      this.hasDoneChanges = true;
    }

    // do the add to the Diagram
    addGraphicalRepresentation(context, newConfigurable);

    // return newly created business object(s)
    setObjects(new Object[] { newConfigurable });
    return getObjects();
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

  public Object[] getObjects() {
    return this.objects;
  }

  public void setObjects(final Object[] objects) {
    this.objects = objects;
  }

}

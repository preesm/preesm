package org.preesm.ui.pisdf.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.func.ICreate;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.Parameter;
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
    if (newConfigurable instanceof final AbstractActor newActor) {
      if (graph.addActor(newActor)) {
        this.hasDoneChanges = true;
      }
    } else if (newConfigurable instanceof final Parameter newParameter) {
      if (graph.addParameter(newParameter)) {
        this.hasDoneChanges = true;
      }
    } else {
      throw new PreesmRuntimeException();
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

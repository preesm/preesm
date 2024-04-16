package org.preesm.ui.pisdf.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

public class CreateDelayFeature extends AbstractCustomFeature {

  /**
   * The default constructor for {@link AddDelayFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public CreateDelayFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /** The Constant FEATURE_NAME. */
  private static final String FEATURE_NAME = "Add Delay";

  /** The Constant FEATURE_DESCRIPTION. */
  private static final String FEATURE_DESCRIPTION = "Add Delay to the Fifo";

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return FEATURE_NAME;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return FEATURE_DESCRIPTION;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.
   * ICustomContext)
   */
  @Override
  public boolean canExecute(final ICustomContext context) {
    // allow if exactly one pictogram element
    // representing a Fifo is selected
    boolean ret = false;
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length == 1)) {
      final Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof final Fifo fifo && fifo.getDelay() == null) {
        // Check that the Fifo has no existing delay
        ret = true;
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
   */
  @Override
  public void execute(final ICustomContext context) {
    // Recheck if the execution is possible (probably useless)
    if (!canExecute(context)) {
      return;
    }
    // Get the Fifo
    final PictogramElement[] pes = context.getPictogramElements();
    final FreeFormConnection connection = (FreeFormConnection) pes[0];
    final Fifo fifo = (Fifo) getBusinessObjectForPictogramElement(connection);

    // Create the Delay and add it to the Fifo
    final Delay newDelay = PiMMUserFactory.instance.createDelay();
    fifo.setDelay(newDelay);
    newDelay.setName(newDelay.getId());
    newDelay.getActor().setName(newDelay.getId());

    final PiGraph graph = fifo.getContainingPiGraph();
    graph.addDelay(newDelay);

    // add graphical element for delay
    final int posX = context.getX();
    final int posY = context.getY();

    final AddContext addCtxt = new AddContext();
    final Diagram diagram = getDiagram();

    addCtxt.setLocation(posX, posY);
    addCtxt.setTargetConnection(connection);
    addCtxt.setTargetContainer(diagram);

    final PictogramElement containerShape = addGraphicalRepresentation(addCtxt, newDelay);

    // Select the whole fifo
    getDiagramBehavior().getDiagramContainer().setPictogramElementForSelection(containerShape);
  }

}

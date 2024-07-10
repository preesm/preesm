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

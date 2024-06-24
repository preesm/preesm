/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
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
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Port;

// TODO: Auto-generated Javadoc
/**
 * Custom feature to move down a port.
 *
 * @author jheulot
 * @author kdesnos
 *
 */
public class MoveDownActorPortFeature extends MoveUpActorPortFeature {

  /** The Constant HINT. */
  public static final String HINT = "down";

  /**
   * Default Constructor.
   *
   * @param fp
   *          the feature provider
   */
  public MoveDownActorPortFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.ui.pisdf.features.MoveUpActorPortFeature#getName()
   */
  @Override
  public String getName() {
    // Should be "Move up Port\tCtrl+Arrow Down" but printing keyboard shortcut involving arrows seems complicated.
    return "Move down Port (Ctrl+Arrow Down)";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.ui.pisdf.features.MoveUpActorPortFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Move down the Port";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.ui.pisdf.features.MoveUpActorPortFeature#canExecute(org.eclipse.graphiti.features.context.
   * ICustomContext)
   */
  @Override
  public boolean canExecute(final ICustomContext context) {
    // allow move up if exactly one pictogram element representing a Port is selected and it is not the first port
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length == 1)) {
      final Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof final Port port && (port.eContainer() instanceof final ExecutableActor actor)) {
        return switch (port.getKind()) {
          case CFG_INPUT -> actor.getConfigInputPorts().size() > 1
              && (actor.getConfigInputPorts().indexOf(port) < (actor.getConfigInputPorts().size() - 1));
          case CFG_OUTPUT -> actor.getConfigOutputPorts().size() > 1
              && (actor.getConfigOutputPorts().indexOf(port) < (actor.getConfigOutputPorts().size() - 1));
          case DATA_INPUT -> actor.getDataInputPorts().size() > 1
              && (actor.getDataInputPorts().indexOf(port) < (actor.getDataInputPorts().size() - 1));
          case DATA_OUTPUT -> actor.getDataOutputPorts().size() > 1
              && (actor.getDataOutputPorts().indexOf(port) < (actor.getDataOutputPorts().size() - 1));
        };
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.ui.pisdf.features.MoveUpActorPortFeature#execute(org.eclipse.graphiti.features.context.
   * ICustomContext)
   */
  @Override
  public void execute(final ICustomContext context) {

    // Re-check if only one element is selected
    final PictogramElement[] pes = context.getPictogramElements();

    if ((pes == null) || (pes.length != 1)) {
      return;
    }

    final PictogramElement anchorToMoveDown = pes[0];
    final Object bo = getBusinessObjectForPictogramElement(anchorToMoveDown);
    if (bo instanceof final Port portToMoveDown) {
      Port portToMoveUp = null;
      int portToMoveDownIndex = -1;
      ExecutableActor actor;

      actor = (ExecutableActor) (portToMoveDown.eContainer());
      // Switch Port into Actor Object
      switch (portToMoveDown.getKind()) {
        case DATA_INPUT:
          portToMoveDownIndex = actor.getDataInputPorts().indexOf(portToMoveDown);
          portToMoveUp = actor.getDataInputPorts().get(portToMoveDownIndex + 1);
          break;
        case DATA_OUTPUT:
          portToMoveDownIndex = actor.getDataOutputPorts().indexOf(portToMoveDown);
          portToMoveUp = actor.getDataOutputPorts().get(portToMoveDownIndex + 1);
          break;
        case CFG_INPUT:
          portToMoveDownIndex = actor.getConfigInputPorts().indexOf(portToMoveDown);
          portToMoveUp = actor.getConfigInputPorts().get(portToMoveDownIndex + 1);
          break;
        case CFG_OUTPUT:
          portToMoveDownIndex = actor.getConfigOutputPorts().indexOf(portToMoveDown);
          portToMoveUp = actor.getConfigOutputPorts().get(portToMoveDownIndex + 1);
          break;
        default:
      }
      // Change context to use portToMoveUp feature
      // Get Graphical Elements
      final ContainerShape csActor = (ContainerShape) ((BoxRelativeAnchor) anchorToMoveDown)
          .getReferencedGraphicsAlgorithm().getPictogramElement();
      final EList<Anchor> anchors = csActor.getAnchors();

      Anchor anchorToMoveUp = null;
      for (final Anchor a : anchors) {
        if (a.getLink().getBusinessObjects().get(0).equals(portToMoveUp)) {
          anchorToMoveUp = a;
          break;
        }
      }

      context.getPictogramElements()[0] = anchorToMoveUp;
      super.execute(context);
    }
  }
}

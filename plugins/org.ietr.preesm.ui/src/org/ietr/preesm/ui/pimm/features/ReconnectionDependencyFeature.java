/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.Port;

// TODO: Auto-generated Javadoc
/**
 * Feature to reconnect a dependency in the PiMM GUI.
 *
 * @author kdesnos
 *
 */
public class ReconnectionDependencyFeature extends DefaultReconnectionFeature {

  /** The has done changes. */
  protected boolean hasDoneChanges = false;

  /**
   * Default constructor for the {@link ReconnectionDependencyFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public ReconnectionDependencyFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.DefaultReconnectionFeature#canReconnect(org.eclipse.graphiti.features.context.IReconnectionContext)
   */
  @Override
  public boolean canReconnect(final IReconnectionContext context) {
    // If the new anchor is the same as the old one, reconnection
    // is possible
    if (context.getOldAnchor().equals(context.getNewAnchor())) {
      return true;
    }

    // The create dependency feature is used to check the
    // reconnection feasibility with the same criteria as the creation
    // of a new dependency.
    final CreateDependencyFeature createFeature = new CreateDependencyFeature(getFeatureProvider());
    final CreateConnectionContext createContext = new CreateConnectionContext();
    createContext.setTargetAnchor(context.getConnection().getEnd());
    createContext.setTargetPictogramElement(context.getConnection().getEnd());
    createContext.setSourceAnchor(context.getConnection().getStart());
    createContext.setSourcePictogramElement(context.getConnection().getStart());

    // Check whether the setter or the getter is reconnected.
    if (context.getConnection().getStart() == context.getOldAnchor()) {
      // The setter is reconnected
      createContext.setSourceAnchor(context.getNewAnchor());
      createContext.setSourceLocation(context.getTargetLocation());
      createContext.setSourcePictogramElement(context.getTargetPictogramElement());

      if (createFeature.canStartConnection(createContext)) {
        // Check that the new source is not the getter
        if (context.getConnection().getEnd() == context.getNewAnchor()) {
          return false;
        }

        // If the getter is an actor port, check that the setter
        // is not a configuration actor
        if ((getBusinessObjectForPictogramElement(context.getConnection().getEnd()) instanceof ConfigInputPort)
            && (getBusinessObjectForPictogramElement(context.getNewAnchor()) instanceof ConfigOutputPort)) {
          return false;
        }

        // No special case prevents the the reconnection.
        return true;
      } else {
        return false;
      }
    } else {
      // The getter is reconnected
      createContext.setTargetAnchor(context.getNewAnchor());
      createContext.setTargetLocation(context.getTargetLocation());
      createContext.setTargetPictogramElement(context.getTargetPictogramElement());

      return createFeature.canCreate(createContext);
    }

    // return false;
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

  /**
   * Method to retrieve the {@link Port} corresponding to an {@link Anchor}.
   *
   * @param anchor
   *          the anchor to treat
   * @return the found {@link Port}, or <code>null</code> if no port corresponds to this {@link Anchor}
   */
  protected Port getPort(final Anchor anchor) {
    if (anchor != null) {
      final Object obj = getBusinessObjectForPictogramElement(anchor);
      if (obj instanceof Port) {
        return (Port) obj;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.DefaultReconnectionFeature#preReconnect(org.eclipse.graphiti.features.context.IReconnectionContext)
   */
  @Override
  public void preReconnect(final IReconnectionContext context) {
    // If we reconnect to the same anchor: nothing to do
    if (context.getOldAnchor().equals(context.getNewAnchor())) {
      return;
    }

    // If the reconnection involve the creation of a new config input port
    // Create it

    if (context.getOldAnchor() == context.getConnection().getEnd()) {
      final PictogramElement tgtPE = context.getTargetPictogramElement();
      final Object getterObject = getBusinessObjectForPictogramElement(tgtPE);
      final Anchor getterAnchor = context.getNewAnchor();
      Port getter = getPort(getterAnchor);

      if ((getter == null) && (getterObject instanceof Parameterizable)) {

        // The target can be: A Parameter, A Fifo, An Actor, An
        // interface.

        // If the getter is an actor
        if (getterObject instanceof ExecutableActor) {
          // Create a ConfigInputPort
          final PictogramElement targetPe = context.getTargetPictogramElement();
          final AbstractAddActorPortFeature addPortFeature = CreateDependencyFeature.canCreateConfigPort(targetPe, getFeatureProvider(), "config_input");
          if (addPortFeature != null) {
            final CustomContext targetContext = new CustomContext(new PictogramElement[] { targetPe });
            addPortFeature.execute(targetContext);
            ((ReconnectionContext) context).setNewAnchor(addPortFeature.getCreatedAnchor());
            getter = addPortFeature.getCreatedPort();
          }

          // if getter is null (in case a port creation
          // failed or was aborted)
          if (getter == null) {
            ((ReconnectionContext) context).setNewAnchor(context.getOldAnchor());
          }
        }

        // TODO implement the creation of configInputPort
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.DefaultReconnectionFeature#postReconnect(org.eclipse.graphiti.features.context.IReconnectionContext)
   */
  @Override
  public void postReconnect(final IReconnectionContext context) {
    // Apply changes to the BusinessModel
    // If we reconnect to the same anchor: nothing to do
    if (context.getOldAnchor().equals(context.getNewAnchor())) {
      return;
    }

    final Dependency dependency = (Dependency) getBusinessObjectForPictogramElement(context.getConnection());

    // Get the Old and new objects
    if (context.getNewAnchor() == context.getConnection().getEnd()) {
      // The reconnected side is the getter
      ConfigInputPort newGetter = null;

      final Object getterObject = getBusinessObjectForPictogramElement(context.getTargetPictogramElement());
      // If the getter is a Parameter, a FIFO, or an InterfaceActor
      if ((getterObject instanceof Parameter) || (getterObject instanceof InterfaceActor) || (getterObject instanceof Delay)) {
        // Create a ConfigInputPort
        newGetter = PiMMFactory.eINSTANCE.createConfigInputPort();
        ((Parameterizable) getterObject).getConfigInputPorts().add(newGetter);
      } else {
        newGetter = (ConfigInputPort) getBusinessObjectForPictogramElement(context.getConnection().getEnd());
      }

      dependency.setGetter(newGetter);
    } else {
      // The reconnected side is the setter
      final ISetter newSetter = (ISetter) getBusinessObjectForPictogramElement(context.getConnection().getStart());
      dependency.setSetter(newSetter);
    }

    this.hasDoneChanges = true;
  }
}

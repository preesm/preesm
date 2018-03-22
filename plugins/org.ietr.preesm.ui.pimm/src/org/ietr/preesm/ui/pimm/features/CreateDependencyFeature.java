/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2014)
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
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Configurable;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;

// TODO: Auto-generated Javadoc
/**
 * Create feature to create a new {@link Dependency} in the {@link Diagram}.
 *
 * @author kdesnos
 *
 */
public class CreateDependencyFeature extends AbstractCreateConnectionFeature {

  /** The has done changes. */
  protected boolean hasDoneChanges = false;

  /** The Constant FEATURE_NAME. */
  private static final String FEATURE_NAME = "Dependency";

  /** The Constant FEATURE_DESCRIPTION. */
  private static final String FEATURE_DESCRIPTION = "Create Dependency";

  /**
   * The default constructor of the {@link CreateDependencyFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public CreateDependencyFeature(final IFeatureProvider fp) {
    super(fp, CreateDependencyFeature.FEATURE_NAME, CreateDependencyFeature.FEATURE_DESCRIPTION);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ICreateConnection#canCreate(org.eclipse.graphiti.features.context.ICreateConnectionContext)
   */
  @Override
  public boolean canCreate(final ICreateConnectionContext context) {
    // This function is called when selecting the end of a created
    // dependency. We assume that the canStartConnection is already true.

    // Refresh to remove all remaining tooltip;
    getDiagramBehavior().refresh();
    final PictogramElement targetPE = context.getTargetPictogramElement();
    final Object targetObj = getBusinessObjectForPictogramElement(targetPE);

    // False if the target is a Graph (i.e. the diagram)
    if (targetObj instanceof PiGraph) {
      return false;
    }

    final ISetter setter = getSetter(context.getSourceAnchor());
    // If the setter is a ConfigOutputPort, only a Parameter can receive the
    // dependency
    if ((setter instanceof ConfigOutputPort) && !(targetObj instanceof Parameter)) {
      if (context.getTargetAnchor() != null) {
        // Create tooltip message
        PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor().getGraphicsAlgorithm(), getDiagramBehavior(),
            "A dependency set by a config. output port can only target a parameter.");
      }
      return false;
    }

    // True if the target is a ConfigInputPort of an actor (and the source
    // is not a ConfigOutpuPort
    final Port target = getPort(context.getTargetAnchor());
    final boolean targetOK = ((target != null) && (target instanceof ConfigInputPort));
    if (targetOK) {
      // Check that no dependency is connected to the ports
      if (((ConfigInputPort) target).getIncomingDependency() != null) {
        // Create tooltip message
        PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor().getGraphicsAlgorithm(), getDiagramBehavior(),
            "A config port cannot be connected to several Dependencies");
        return false;
      }

      return true;
    }

    // False if the target is the setter
    if (context.getTargetAnchor() == context.getSourceAnchor()) {
      PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor().getGraphicsAlgorithm(), getDiagramBehavior(),
          "A self dependency is strictly forbidden (as well as cyclic dependencies)");
      return false;
    }

    // False if target is a config input/output interface
    if (((targetObj instanceof Parameter) && ((Parameter) targetObj).isConfigurationInterface()) || (targetObj instanceof ConfigOutputInterface)) {
      PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetPictogramElement().getGraphicsAlgorithm(), getDiagramBehavior(),
          "Configuration Interfaces cannot be the getter of a dependency.\nCheck the interface port instead.");
      return false;
    }

    // True if the target is "Parameterizable" (except
    // ConfigInput/OutputInterfaces)
    if (targetObj instanceof Parameterizable) {
      return true;
    }

    // False if the target is an outputPort
    if ((target != null) && ((target instanceof DataOutputPort) || (target instanceof DataInputPort))) {
      // Create tooltip message
      PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor().getGraphicsAlgorithm(), getDiagramBehavior(),
          "A Dependency cannot end at a data port");
      return false;
    }

    // TODO Check if the target can create a port

    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ICreateConnection#canStartConnection(org.eclipse.graphiti.features.context.ICreateConnectionContext)
   */
  @Override
  public boolean canStartConnection(final ICreateConnectionContext context) {

    // Refresh to remove all remaining tooltip;
    getDiagramBehavior().refresh();

    // Return true if the connection starts at a ISetter
    final ISetter setter = getSetter(context.getSourceAnchor());

    if (setter != null) {
      return true;
    }

    final Anchor anchor = context.getSourceAnchor();
    final Object obj = getBusinessObjectForPictogramElement(anchor);
    if ((obj instanceof DataInputPort) || (obj instanceof ConfigInputPort)) {
      // Create tooltip message
      PiMMUtil.setToolTip(getFeatureProvider(), context.getSourceAnchor().getGraphicsAlgorithm(), getDiagramBehavior(),
          "A Dependency cannot start at an input port");
      return false;
    }

    if (obj instanceof DataOutputPort) {
      // Create tooltip message
      PiMMUtil.setToolTip(getFeatureProvider(), context.getSourceAnchor().getGraphicsAlgorithm(), getDiagramBehavior(),
          "A Dependency cannot start at an data output port");
      return false;
    }

    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ICreateConnection#create(org.eclipse.graphiti.features.context.ICreateConnectionContext)
   */
  @Override
  public Connection create(final ICreateConnectionContext context) {
    // We suppose that this method is called only if the canStart and
    // canCreate methods were both positive

    Connection newConnection = null;

    // get anchors which should be connected
    final Anchor setterAnchor = context.getSourceAnchor();
    Anchor getterAnchor = context.getTargetAnchor();
    final ISetter setter = getSetter(setterAnchor);
    Port getter = getPort(getterAnchor);

    // If setter is null, something went wrong
    // TODO implement the creation of configOutputPort
    if (setter == null) {
      return null;
    }

    // If getter port is null
    if (getter == null) {
      // If the target is a Parameterizable item
      // Create a configInputPort
      final PictogramElement tgtPE = context.getTargetPictogramElement();
      final Object tgtObj = getBusinessObjectForPictogramElement(tgtPE);
      if (tgtObj instanceof Configurable) {
        // The target can be: A Parameter, A Fifo, An Actor, An
        // interface.

        // If the getter is an actor
        if (tgtObj instanceof ExecutableActor) {
          // Create a ConfigInputPort
          final AbstractAddActorPortFeature addPortFeature = CreateDependencyFeature.canCreateConfigPort(tgtPE, getFeatureProvider(), "config_input");
          if (addPortFeature != null) {
            final CustomContext targetContext = new CustomContext(new PictogramElement[] { tgtPE });
            // If Src is a Parameter (or config inputPort), give it as default port name
            if (setter instanceof Parameter) {
              addPortFeature.execute(targetContext, ((Parameter) setter).getName());
            } else {
              addPortFeature.execute(targetContext);
            }

            getterAnchor = addPortFeature.getCreatedAnchor();
            getter = addPortFeature.getCreatedPort();
          }
        }

        // If the getter is a Parameter or an InterfaceActor
        if ((tgtObj instanceof Parameter) || (tgtObj instanceof InterfaceActor) || (tgtObj instanceof Delay)) {
          // Create a ConfigInputPort
          getter = PiMMUserFactory.instance.createConfigInputPort();
          ((Configurable) tgtObj).getConfigInputPorts().add((ConfigInputPort) getter);
        }

        // TODO implement the creation of configInputPort
      }
    }

    // Re-check if getter and setter are non-null (in case a port creation
    // failed or was aborted)

    if (getter instanceof DataPort) {
      MessageDialog.openWarning(null, "Preesm Error", "Can not connect dependencies to data ports. Try connecting the dependency to the containing actor.\n"
          + "\nNote: if you are trying to connect the dependency to an interface, drop its end on the interface name.");
      return null;
    }

    if ((getter != null) && (setter != null)) {
      // Create new business object
      final Dependency dependendy = createDependency(setter, (ConfigInputPort) getter);
      // add connection for business object
      final AddConnectionContext addContext = new AddConnectionContext(setterAnchor, getterAnchor);
      addContext.setNewObject(dependendy);
      newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
      this.hasDoneChanges = true;

      return newConnection;
    }

    return null;
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

  /**
   * Method to retrieve the {@link ISetter} corresponding to an {@link Anchor}.
   *
   * @param anchor
   *          the {@link Anchor} to treat
   * @return the found {@link ISetter}, or <code>null</code> if no {@link ISetter} corresponds to this {@link Anchor}
   */
  protected ISetter getSetter(final Anchor anchor) {
    if (anchor != null) {
      final Object obj = getBusinessObjectForPictogramElement(anchor);
      if (obj instanceof ISetter) {
        return (ISetter) obj;
      }
    }
    return null;
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
   * Creates a {@link Dependency} between the {@link ISetter} and the {@link ConfigInputPort}. Also add the created {@link Dependency} to the {@link PiGraph} of
   * the current {@link Diagram}.
   *
   * @param setter
   *          the source {@link ISetter} of the {@link Dependency}
   * @param getter
   *          the target {@link ConfigInputPort} of the {@link Dependency}
   * @return the created {@link Dependency}
   */
  protected Dependency createDependency(final ISetter setter, final ConfigInputPort getter) {

    // Refresh to remove all remaining tooltip;
    getDiagramBehavior().refresh();

    // Retrieve the graph
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());

    // Create the Fifo
    final Dependency dependency = PiMMUserFactory.instance.createDependency(setter, getter);

    // Add the new Fifo to the graph
    graph.addDependency(dependency);

    return dependency;
  }

  /**
   * Method to check whether it is possible to create a Configuration {@link Port} for the given source/target {@link PictogramElement}.
   *
   * @param pe
   *          the {@link PictogramElement} tested
   * @param fp
   *          A {@link IFeatureProvider} for the diagram.
   * @param direction
   *          the direction of the port we want to create ("config_input" or "config_output")
   * @return an {@link AbstractAddActorPortFeature} if the given {@link PictogramElement} can create a {@link Port} with the given direction. Return
   *         <code>null</code> else.
   */
  protected static AbstractAddActorPortFeature canCreateConfigPort(final PictogramElement pe, final IFeatureProvider fp, final String direction) {
    boolean canCreatePort = false;
    final PictogramElement peSource = pe;

    // Create the FeatureProvider
    final CustomContext sourceContext = new CustomContext(new PictogramElement[] { peSource });
    AbstractAddActorPortFeature addPortFeature = null;
    if (direction.equals("config_input")) {
      addPortFeature = new AddConfigInputPortFeature(fp);
    }
    // if (direction.equals("config_output")) {
    // addPortFeature = new AddOutputPortFeature(getFeatureProvider());
    // }
    if (addPortFeature != null) {
      canCreatePort = addPortFeature.canExecute(sourceContext);
    }
    if (canCreatePort) {
      return addPortFeature;
    } else {
      return null;
    }
  }
}

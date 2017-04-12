/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Port;

// TODO: Auto-generated Javadoc
/**
 * The Class AddActorFromRefinementFeature.
 */
public class AddActorFromRefinementFeature extends AbstractAddFeature {

  /** The has done changes. */
  boolean hasDoneChanges = false;

  /**
   * When a file is drag and dropped on an graph, the feature attempts to create a new {@link Actor} and to set this file as the refinement of the actor.
   *
   * <p>
   * Works only for IDL, H and PI files.
   * </p>
   *
   * @author kdesnos
   * @param fp
   *          the fp
   */
  public AddActorFromRefinementFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public boolean canAdd(final IAddContext context) {
    if (!(context.getNewObject() instanceof IFile)) {
      return false;
    } else {
      final String fileExtension = ((IFile) context.getNewObject()).getFileExtension();

      if ("pi".equals(fileExtension) || "h".equals(fileExtension) || "idl".equals(fileExtension)) {
        return true;
      }
      return false;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public PictogramElement add(final IAddContext context) {
    // 1- Create and Add Actor
    final CreateActorFeature createActorFeature = new CreateActorFeature(getFeatureProvider());
    final CreateContext createContext = new CreateContext();
    createContext.setLocation(context.getX(), context.getY());
    createContext.setSize(context.getWidth(), context.getWidth());
    createContext.setTargetContainer(context.getTargetContainer());
    createContext.setTargetConnection(context.getTargetConnection());
    final Object[] actors = createActorFeature.create(createContext);

    // Stop if actor creation was cancelled
    if (actors.length == 0) {
      return null;
    } else {
      this.hasDoneChanges = true;
    }

    // 2- Set Refinement
    final Actor actor = (Actor) actors[0];
    final SetActorRefinementFeature setRefinementFeature = new SetActorRefinementFeature(getFeatureProvider());

    IPath newFilePath;
    if (context.getNewObject() instanceof IFile) {
      newFilePath = ((IFile) context.getNewObject()).getFullPath();
    } else {
      return null;
    }
    setRefinementFeature.setShowOnlyValidPrototypes(false);
    setRefinementFeature.setActorRefinement(actor, newFilePath);

    // 3- Create all ports corresponding to the refinement.
    final PictogramElement[] pictElements = new PictogramElement[1];
    pictElements[0] = getFeatureProvider().getAllPictogramElementsForBusinessObject(actors[0])[0];

    final AbstractActor protoPort = actor.getRefinement().getAbstractActor();
    // protoPort is Null if actor creation was cancelled during refinement
    // prototype selection
    if (protoPort != null) {
      // Process DataInputPorts
      for (final Port p : protoPort.getDataInputPorts()) {
        final AddDataInputPortFeature addFeature = new AddDataInputPortFeature(getFeatureProvider());
        final ICustomContext portContext = new CustomContext(pictElements);
        portContext.putProperty("name", p.getName());
        addFeature.execute(portContext);
      }

      // Process DataOutputPorts
      for (final Port p : protoPort.getDataOutputPorts()) {
        final AddDataOutputPortFeature addFeature = new AddDataOutputPortFeature(getFeatureProvider());
        final ICustomContext portContext = new CustomContext(pictElements);
        portContext.putProperty("name", p.getName());
        addFeature.execute(portContext);
      }

      // Process ConfigInputPorts
      for (final Port p : protoPort.getConfigInputPorts()) {
        final AddConfigInputPortFeature addFeature = new AddConfigInputPortFeature(getFeatureProvider());
        final ICustomContext portContext = new CustomContext(pictElements);
        portContext.putProperty("name", p.getName());
        addFeature.execute(portContext);
      }

      // Process ConfigOutputPorts
      for (final Port p : protoPort.getConfigOutputPorts()) {
        final AddConfigOutputPortFeature addFeature = new AddConfigOutputPortFeature(getFeatureProvider());
        final ICustomContext portContext = new CustomContext(pictElements);
        portContext.putProperty("name", p.getName());
        addFeature.execute(portContext);
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
}

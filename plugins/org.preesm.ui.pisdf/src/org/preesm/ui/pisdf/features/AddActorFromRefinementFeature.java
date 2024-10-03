/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023 - 2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2015)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2014 - 2015)
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.check.RefinementChecker;

/**
 * The Class AddActorFromRefinementFeature.
 */
public class AddActorFromRefinementFeature extends AbstractAddFeature {

  /** The has done changes. */
  boolean hasDoneChanges = true;

  /**
   * When a file is drag and dropped on an graph, the feature attempts to create a new {@link Actor} and to set this
   * file as the refinement of the actor.
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
    }
    final String fileExtension = ((IFile) context.getNewObject()).getFileExtension();

    return "pi".equals(fileExtension) || "idl".equals(fileExtension)
        || RefinementChecker.isAsupportedHeaderFileExtension(fileExtension);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public PictogramElement add(final IAddContext context) {
    // 1- Create and Add Actor
    final Actor actor = createActor(context);

    // Stop if actor creation was cancelled
    if (actor == null) {
      this.hasDoneChanges = false;
      return null;
    }

    // 2- Set Refinement
    final boolean validFile = setRefinement(context, actor);
    if (!validFile) {
      this.hasDoneChanges = false;
      return null;
    }

    // 3- Create all ports corresponding to the refinement.
    final PictogramElement[] pictElements = createPorts(actor);

    return pictElements[0];
  }

  private PictogramElement[] createPorts(final Actor actor) {
    final PictogramElement[] pictElements = new PictogramElement[1];
    pictElements[0] = getFeatureProvider().getAllPictogramElementsForBusinessObject(actor)[0];

    final AbstractActor protoPort = actor.getChildAbstractActor();
    // protoPort is Null if actor creation was cancelled during refinement prototype selection
    if (protoPort != null) {

      protoPort.getAllPorts().forEach(port -> {
        AbstractAddActorPortFeature addFeature;

        // Could be replaced with instanceof pattern matching in a switch statement once it comes out of preview
        // Instance of ConfigOutputPort needs to be check BEFORE DataOutputPort because the former extends the latter
        if (port instanceof ConfigInputPort) {
          addFeature = new AddConfigInputPortFeature(getFeatureProvider());
        } else if (port instanceof ConfigOutputPort) {
          addFeature = new AddConfigOutputPortFeature(getFeatureProvider());
        } else if (port instanceof DataInputPort) {
          addFeature = new AddDataInputPortFeature(getFeatureProvider());
        } else if (port instanceof DataOutputPort) {
          addFeature = new AddDataOutputPortFeature(getFeatureProvider());
        } else {
          throw new PreesmRuntimeException("Unrecognized port type");
        }

        final ICustomContext portContext = new CustomContext(pictElements);
        addFeature.setGivenName(port.getName());
        addFeature.execute(portContext);
      });
    }
    return pictElements;
  }

  private boolean setRefinement(final IAddContext context, final Actor actor) {
    final SetActorRefinementFeature setRefinementFeature = new SetActorRefinementFeature(getFeatureProvider());
    IPath newFilePath;
    if (!(context.getNewObject() instanceof IFile)) {
      return false;
    }
    newFilePath = ((IFile) context.getNewObject()).getFullPath();
    setRefinementFeature.setShowOnlyValidPrototypes(false);
    setRefinementFeature.setActorRefinement(actor, newFilePath);

    return actor.getRefinement() != null;
  }

  private Actor createActor(final IAddContext context) {
    final CreateActorFeature createActorFeature = new CreateActorFeature(getFeatureProvider());
    final CreateContext createContext = new CreateContext();
    createContext.setLocation(context.getX(), context.getY());
    createContext.setSize(context.getWidth(), context.getWidth());
    createContext.setTargetContainer(context.getTargetContainer());
    createContext.setTargetConnection(context.getTargetConnection());
    createActorFeature.execute(createContext);
    final Object[] actors = createActorFeature.getObjects();

    if (actors == null || actors.length == 0) {
      return null;
    }

    return (Actor) actors[0];
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

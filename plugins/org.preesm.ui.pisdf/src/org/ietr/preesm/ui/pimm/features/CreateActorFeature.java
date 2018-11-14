/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.func.ICreate;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.VertexNameValidator;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateActorFeature.
 */
public class CreateActorFeature extends AbstractCreateFeature {

  /** The Constant FEATURE_NAME. */
  private static final String FEATURE_NAME = "Actor";

  /** The Constant FEATURE_DESCRIPTION. */
  private static final String FEATURE_DESCRIPTION = "Create Actor";

  /** The has done changes. */
  protected Boolean hasDoneChanges;

  /**
   * Default constructor.
   *
   * @param fp
   *          the feature provider
   */
  public CreateActorFeature(final IFeatureProvider fp) {
    // Set name and description of the creation feature
    super(fp, CreateActorFeature.FEATURE_NAME, CreateActorFeature.FEATURE_DESCRIPTION);
    this.hasDoneChanges = false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ICreate#canCreate(org.eclipse.graphiti.features.context.ICreateContext)
   */
  @Override
  public boolean canCreate(final ICreateContext context) {
    return context.getTargetContainer() instanceof Diagram;
  }

  private Object[] objects;

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
   */
  @Override
  public Object[] create(final ICreateContext context) {
    // Retrieve the graph
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());

    // Ask user for Actor name until a valid name is entered.
    final String question = "Enter new actor name";
    String newActorName = "ActorName";

    newActorName = PiMMUtil.askString("Create Actor", question, newActorName, new VertexNameValidator(graph, null));
    if ((newActorName == null) || (newActorName.trim().length() == 0)) {
      this.hasDoneChanges = false; // If this is not done, the graph is considered modified.
      return ICreate.EMPTY;
    }

    // create Actor
    final Actor newActor = PiMMUserFactory.instance.createActor();
    newActor.setName(newActorName);

    // Add new actor to the graph.
    if (graph.addActor(newActor)) {
      this.hasDoneChanges = true;
    }

    // do the add to the Diagram
    addGraphicalRepresentation(context, newActor);

    // return newly created business object(s)
    setObjects(new Object[] { newActor });
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

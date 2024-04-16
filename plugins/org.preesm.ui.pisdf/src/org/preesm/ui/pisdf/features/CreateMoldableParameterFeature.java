/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2013)
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
import org.preesm.model.pisdf.MoldableParameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.ui.pisdf.util.VertexNameValidator;
import org.preesm.ui.utils.DialogUtil;

// TODO: Auto-generated Javadoc
/**
 * Create Feature for {@link MoldableParameter}s.
 *
 * @author kdesnos
 * @author jheulot
 */
public class CreateMoldableParameterFeature extends AbstractCreateFeature {

  /** The Constant FEATURE_NAME. */
  private static final String FEATURE_NAME = "Moldable Parameter";

  /** The Constant FEATURE_DESCRIPTION. */
  private static final String FEATURE_DESCRIPTION = "Create Moldable Parameter";

  /** The has done changes. */
  protected Boolean hasDoneChanges;

  /**
   * Default constructor for the {@link CreateMoldableParameterFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public CreateMoldableParameterFeature(final IFeatureProvider fp) {
    super(fp, CreateMoldableParameterFeature.FEATURE_NAME, CreateMoldableParameterFeature.FEATURE_DESCRIPTION);
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

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
   */
  @Override
  public Object[] create(final ICreateContext context) {
    // Retrieve the graph
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());

    // Ask user for Parameter name until a valid name is entered.
    final String question = "Enter new moldable parameter name";
    String newParameterName = "MoldableParameterName";

    // TODO create a parameter name validator
    newParameterName = DialogUtil.askString("Create Parameter", question, newParameterName,
        new VertexNameValidator(graph, null));
    if ((newParameterName == null) || (newParameterName.trim().length() == 0)) {
      this.hasDoneChanges = false; // If this is not done, the graph is
      // considered modified.
      return ICreate.EMPTY;
    }

    // create Parameter
    final MoldableParameter newParameter = PiMMUserFactory.instance.createMoldableParameter();
    newParameter.setName(newParameterName);
    // this parameter

    // Add new parameter to the graph.
    if (graph.addParameter(newParameter)) {
      this.hasDoneChanges = true;
    }

    // do the add to the Diagram
    addGraphicalRepresentation(context, newParameter);

    // return newly created business object(s)
    return new Object[] { newParameter };
  }

}

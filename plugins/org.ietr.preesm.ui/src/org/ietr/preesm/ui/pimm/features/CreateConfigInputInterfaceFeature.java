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
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.util.VertexNameValidator;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;

/**
 * Create feature for Configuration Input Interface.
 * 
 * @author kdesnos
 * 
 */
public class CreateConfigInputInterfaceFeature extends AbstractCreateFeature {

	private static final String FEATURE_NAME = "Config. Input Interface";

	private static final String FEATURE_DESCRIPTION = "Create Config. Input Interface";

	protected Boolean hasDoneChanges;

	/**
	 * Default constructor for the {@link CreateConfigInputInterfaceFeature}.
	 * 
	 * @param fp
	 *            the feature provider
	 * @param name
	 *            the name of
	 * @param description
	 */
	public CreateConfigInputInterfaceFeature(IFeatureProvider fp) {
		super(fp, FEATURE_NAME, FEATURE_DESCRIPTION);
		hasDoneChanges = false;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		// Retrieve the graph
		PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());

		// Ask user for Parameter name until a valid name is entered.
		String question = "Enter new configuration input interface name";
		String newCfgInIfName = "iCfgName";

		// TODO create a parameter name validator
		newCfgInIfName = PiMMUtil.askString("Create Config. Input Interface",
				question, newCfgInIfName, new VertexNameValidator(graph, null));
		if (newCfgInIfName == null || newCfgInIfName.trim().length() == 0) {
			this.hasDoneChanges = false; // If this is not done, the graph is
											// considered modified.
			return EMPTY;
		}

		// create Configuration Input Interface (i.e. a Parameter)
		Parameter newParameter = PiMMFactory.eINSTANCE.createParameter();
		newParameter.setName(newCfgInIfName);
		newParameter.setConfigurationInterface(true);
		//newParameter.setLocallyStatic(true); 

		// Add new parameter to the graph.
		if (graph.getParameters().add(newParameter)) {
			this.hasDoneChanges = true;
		}

		// do the add to the Diagram
		addGraphicalRepresentation(context, newParameter);

		// return newly created business object(s)
		return new Object[] { newParameter };
	}

}

/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 ******************************************************************************/
package org.ietr.preesm.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.util.VertexNameValidator;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;

/**
 * Create feature for {@link DataInputInterface}
 * 
 * @author kdesnos
 * 
 */
public class CreateDataInputInterfaceFeature extends AbstractCreateFeature {

	boolean hasDoneChanges = false;

	private static final String FEATURE_NAME = "Data Input Interface";

	private static final String FEATURE_DESCRIPTION = "Create Data Input Interface";

	/**
	 * the Default constructor of {@link CreateDataInputInterfaceFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public CreateDataInputInterfaceFeature(IFeatureProvider fp) {
		super(fp, FEATURE_NAME, FEATURE_DESCRIPTION);
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		// Retrieve the graph
		PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());

		// Ask user for Actor name until a valid name is entered.
		String question = "Enter new Source Interface name";
		String newSrcName = "SrcInterfaceName";

		newSrcName = PiMMUtil.askString("Create Source Interface", question,
				newSrcName, new VertexNameValidator(graph, null));
		if (newSrcName == null || newSrcName.trim().length() == 0) {
			this.hasDoneChanges = false; // If this is not done, the graph is
											// considered modified.
			return EMPTY;
		}

		// create SourceInterface
		DataInputInterface newSrcInterface = PiMMFactory.eINSTANCE
				.createDataInputInterface();
		newSrcInterface.setName(newSrcName);

		// Add new sourceInterface to the graph.
		if (graph.getVertices().add(newSrcInterface)) {
			this.hasDoneChanges = true;
		}

		// do the add to the Diagram
		addGraphicalRepresentation(context, newSrcInterface);

		// return newly created business object(s)
		return new Object[] { newSrcInterface };
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}

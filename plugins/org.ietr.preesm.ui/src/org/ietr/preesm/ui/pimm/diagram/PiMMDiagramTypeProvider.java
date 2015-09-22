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
package org.ietr.preesm.ui.pimm.diagram;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.ui.util.EditUIMarkerHelper;
import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.serialize.PiResourceImpl;
import org.ietr.preesm.pimm.algorithm.checker.PiMMAlgorithmChecker;

/**
 * The {@link IDiagramTypeProvider} for the PiMM diagram type
 * 
 * @author kdesnos
 * 
 */
public class PiMMDiagramTypeProvider extends AbstractDiagramTypeProvider {
	/**
	 * The {@link IToolBehaviorProvider} of this type of {@link Diagram}
	 */
	private IToolBehaviorProvider[] toolBehaviorProviders;

	/**
	 * The default constructor of {@link PiMMDiagramTypeProvider}
	 */
	public PiMMDiagramTypeProvider() {
		super();
		setFeatureProvider(new PiMMFeatureProvider(this));
	}

	@Override
	public void resourcesSaved(Diagram diagram, Resource[] savedResources) {

		// Check for errors before saving
		PiMMAlgorithmChecker checker = new PiMMAlgorithmChecker();

		// Get the PiGraph resource
		PiResourceImpl resource = (savedResources[0] instanceof PiResourceImpl) ? (PiResourceImpl) savedResources[0]
				: null;
		resource = (resource == null && savedResources.length > 1 && savedResources[1] instanceof PiResourceImpl)
				? (PiResourceImpl) savedResources[1] : resource;
		try {
			if (resource != null && !checker.checkGraph((PiGraph) resource.getContents().get(0))) {
				EditUIMarkerHelper helper = new EditUIMarkerHelper();

				// Warnings
				for (String msg : checker.getWarningMsgs()) {
					BasicDiagnostic d = new BasicDiagnostic(org.eclipse.emf.common.util.Diagnostic.WARNING, null, 0,
							msg, new Object[] { resource });
					helper.createMarkers(d);
				}

				// Errors
				for (String msg : checker.getErrorMsgs()) {
					BasicDiagnostic d = new BasicDiagnostic(org.eclipse.emf.common.util.Diagnostic.ERROR, "ICI", 0, msg,
							new Object[] { resource });
					helper.createMarkers(d);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		super.resourcesSaved(diagram, savedResources);
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {

		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { //
					new PiMMToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}

	@Override
	public boolean isAutoUpdateAtStartup() {
		return true;
	}
}

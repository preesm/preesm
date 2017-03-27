/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
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

package org.ietr.preesm.ui.pimm.diagram;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.MarkerHelper;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.ui.util.EditUIMarkerHelper;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DefaultMarkerBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.internal.GraphitiUIPlugin;
import org.eclipse.graphiti.ui.internal.T;
import org.eclipse.swt.widgets.Display;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.serialize.PiResourceImpl;
import org.ietr.preesm.pimm.algorithm.checker.PiMMAlgorithmChecker;
import org.ietr.preesm.ui.Activator;

/**
 * Class inheriting from the {@link DefaultMarkerBehavior}. This class was
 * created to define a custom {@link DefaultMarkerBehavior} that does not reset
 * problems related to graphs on startup of the editor.
 * 
 * @author kdesnos
 *
 */
@SuppressWarnings("restriction")
public class PiMMMarkerBehavior extends DefaultMarkerBehavior {

	/**
	 * Map to store the diagnostic associated with a resource.
	 */
	protected Map<Resource, Diagnostic> resourceToDiagnosticMap = new LinkedHashMap<Resource, Diagnostic>();

	/**
	 * The marker helper instance is responsible for creating workspace resource
	 * markers presented in Eclipse's Problems View.
	 */
	private MarkerHelper markerHelper = new PiMMMarkerHelper();

	/**
	 * Controls whether the problem indication should be updated.
	 */
	protected boolean updateProblemIndication = true;

	/**
	 * Default constructor
	 * 
	 * @param diagramBehavior
	 */
	public PiMMMarkerBehavior(DiagramBehavior diagramBehavior) {
		super(diagramBehavior);
	}

	@Override
	public void initialize() {
		diagramBehavior.getResourceSet().eAdapters().add(pimmAdapter);
		super.initialize();
		super.disableProblemIndicationUpdate();
	}

	@Override
	public void enableProblemIndicationUpdate() {
		updateProblemIndication = true;
		super.enableProblemIndicationUpdate();
		refreshProblemIndication();
	}

	@Override
	public void disableProblemIndicationUpdate() {
		updateProblemIndication = false;
		super.disableProblemIndicationUpdate();
	}

	public Diagnostic checkPiResourceProblems(Resource resource, Exception exception) {
		// Check for errors before saving
		PiMMAlgorithmChecker checker = new PiMMAlgorithmChecker();

		// Get the PiGraph resource
		if (resource instanceof PiResourceImpl) {
			BasicDiagnostic result = new BasicDiagnostic();
			try {
				Diagram diagram = diagramBehavior.getDiagramContainer().getDiagramTypeProvider().getDiagram();
				if (resource != null && !checker.checkGraph((PiGraph) resource.getContents().get(0))) {
					// Warnings
					for (Entry<String, EObject> msgs : checker.getWarningMsgs().entrySet()) {
						String msg = msgs.getKey();
						// Diagnostic for .pi file, removed for simplicity
						//
						// BasicDiagnostic d = new
						// BasicDiagnostic(org.eclipse.emf.common.util.Diagnostic.WARNING,
						// Activator.PLUGIN_ID, 0, msg, new Object[] {
						// msgs.getValue() });
						// result.add(d);
						List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(diagram,
								msgs.getValue());						
						String uriFragment = ((EObject) pes.get(0)).eResource().getURIFragment(pes.get(0));
						BasicDiagnostic d = new BasicDiagnostic(org.eclipse.emf.common.util.Diagnostic.WARNING,
								Activator.PLUGIN_ID, 0, msg, new Object[] { pes.get(0), uriFragment });

						result.add(d);
					}

					// Errors
					for (Entry<String, EObject> msgs : checker.getErrorMsgs().entrySet()) {
						String msg = msgs.getKey();
						// Diagnostic for .pi file, removed for simplicity
						//
						// BasicDiagnostic d = new BasicDiagnostic(org.eclipse.emf.common.util.Diagnostic.ERROR,
						//		Activator.PLUGIN_ID, 0, msg, new Object[] { resource });
						// result.add(d);
						List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(diagram,
								msgs.getValue());	
						String uriFragment = ((EObject) pes.get(0)).eResource().getURIFragment(pes.get(0));
						BasicDiagnostic d = new BasicDiagnostic(org.eclipse.emf.common.util.Diagnostic.ERROR,
								Activator.PLUGIN_ID, 0, msg, new Object[] { pes.get(0), uriFragment });

						result.add(d);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		return Diagnostic.OK_INSTANCE;
	}

	@Override
	public void dispose() {
		super.dispose();
		resourceToDiagnosticMap.clear();
		resourceToDiagnosticMap = null;
	}

	/**
	 * Updates the problems indication markers in the editor. The default
	 * implementation used an EMF {@link BasicDiagnostic} to do the checks and
	 * {@link EditUIMarkerHelper} to check and set markers for {@link EObject}s.
	 * 
	 * Method copied from {@link DefaultMarkerBehavior} (because it is a private
	 * method)
	 */
	void refreshProblemIndication() {
		if (diagramBehavior == null) {
			// Already disposed
			return;
		}
		TransactionalEditingDomain editingDomain = diagramBehavior.getEditingDomain();
		if (updateProblemIndication && editingDomain != null) {
			ResourceSet resourceSet = editingDomain.getResourceSet();
			final BasicDiagnostic diagnostic = new BasicDiagnostic(Diagnostic.OK, GraphitiUIPlugin.PLUGIN_ID, 0, null,
					new Object[] { resourceSet });
			for (final Diagnostic childDiagnostic : resourceToDiagnosticMap.values()) {
				if (childDiagnostic.getSeverity() != Diagnostic.OK) {
					diagnostic.add(childDiagnostic);
				}
			}
			if (markerHelper.hasMarkers(resourceSet)) {
				markerHelper.deleteMarkers(resourceSet);
			}
			if (diagnostic.getSeverity() != Diagnostic.OK) {
				try {
					markerHelper.createMarkers(diagnostic);
					T.racer().info(diagnostic.toString());
				} catch (final CoreException exception) {
					T.racer().error(exception.getMessage(), exception);
				}
			}
		}
	}

	/**
	 * Adapter used to update the problem indication when resources are demanded
	 * loaded.
	 * 
	 * Class adapted from {@link DefaultMarkerBehavior} (because it is a private
	 * class)
	 */
	protected EContentAdapter pimmAdapter = new EContentAdapter() {
		@Override
		public void notifyChanged(Notification notification) {
			if (notification.getNotifier() instanceof PiResourceImpl) {
				switch (notification.getFeatureID(Resource.class)) {
				case Resource.RESOURCE__IS_LOADED:
				case Resource.RESOURCE__IS_MODIFIED: {
					final Resource resource = (Resource) notification.getNotifier();
					final Diagnostic diagnostic = checkPiResourceProblems(resource, null);
					if (diagnostic.getSeverity() != Diagnostic.OK) {
						resourceToDiagnosticMap.put(resource, diagnostic);
					} else {
						resourceToDiagnosticMap.remove(resource);
					}

					if (updateProblemIndication) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								refreshProblemIndication();
							}
						});
					}
					break;
				}
				}
			} else {
				super.notifyChanged(notification);
			}
		}

		@Override
		protected void setTarget(Resource target) {
			basicSetTarget(target);
		}

		@Override
		protected void unsetTarget(Resource target) {
			basicUnsetTarget(target);
		}

	};
}

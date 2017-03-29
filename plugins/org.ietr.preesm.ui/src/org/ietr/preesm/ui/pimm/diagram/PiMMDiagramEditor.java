/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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

package org.ietr.preesm.ui.pimm.diagram;

import org.eclipse.core.resources.IMarker;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DefaultMarkerBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.ide.IGotoMarker;

/**
 * Class inheriting from the {@link DiagramEditor}. This class was created to
 * define a custom {@link DefaultMarkerBehavior} that does not reset problems
 * related to graphs on startup of the editor.
 * 
 * @author kdesnos
 *
 */
public class PiMMDiagramEditor extends DiagramEditor implements IGotoMarker {
	@Override
	protected DiagramBehavior createDiagramBehavior() {
		return new PiMMDiagramBehavior(this);
	}

	@Override
	public void gotoMarker(IMarker marker) {
		// Find the pictogram element associated to the marker
		String uriFragment = marker.getAttribute(PiMMMarkerHelper.DIAGRAM_URI, null);
		if (uriFragment == null)
			return;
		EObject object = getDiagramTypeProvider().getDiagram().eResource().getEObject(uriFragment);
		if (object == null || !(object instanceof PictogramElement))
			return;
		selectPictogramElements(new PictogramElement[] { (PictogramElement) object });

		// Open the property view
		IViewPart propSheet = getWorkbenchPart().getSite().getPage().findView(IPageLayout.ID_PROP_SHEET);
		if (propSheet != null) {
			getWorkbenchPart().getSite().getPage().bringToTop(propSheet);
		}
	}
}

/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
 
package org.ietr.preesm.core.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * This class creates the layout associated with the preesm core perspective.
 * 
 * @author mpelcat
 * 
 */
public class CorePerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = "org.ietr.preesm.core.ui.perspective";

	@Override
	 public void createInitialLayout(IPageLayout layout) {
		
			// Get the editor area.
			String editorArea = layout.getEditorArea();
			
			// Top left: Resource Navigator view and Bookmarks view placeholder
			IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.20f,
				editorArea);
			
			// Adds the files navigator
			topLeft.addView(IPageLayout.ID_RES_NAV);

			// Bottom left: Outline view and Property Sheet view
			IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.75f,
					editorArea);

			// Adds the progress bar and console
			bottomRight.addView(IPageLayout.ID_PROGRESS_VIEW);
			
			bottomRight.addView("org.eclipse.ui.console.ConsoleView");
			
		}


}

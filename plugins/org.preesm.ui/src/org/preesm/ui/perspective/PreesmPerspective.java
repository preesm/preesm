/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015)
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
package org.preesm.ui.perspective;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 *
 */
public class PreesmPerspective implements IPerspectiveFactory {

  /**
   * The corresponding ID in the plugin.xml file
   */
  public static final String PERSPECTIVE_ID = "org.preesm.ui.perspective";

  @Override
  public void createInitialLayout(final IPageLayout layout) {
    // Get the editor area.
    final String editorArea = layout.getEditorArea();

    // Top left: Resource Navigator view and Bookmarks view placeholder
    final IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
    topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
    topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

    // Bottom left: Outline view and Property Sheet view
    final IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f, "topLeft");
    bottomLeft.addView(IPageLayout.ID_OUTLINE);

    // Bottom right: Task List view
    final IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.66f, editorArea);
    bottomRight.addView(IPageLayout.ID_PROP_SHEET);
    bottomRight.addView(IPageLayout.ID_TASK_LIST);
    bottomRight.addView(IPageLayout.ID_PROBLEM_VIEW);

    layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
    layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
  }

}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2010)
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
package org.ietr.dftools.graphiti.ui.editors;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

/**
 * Fill the action tool bar. Retargets actions Print, undo/redo, delete, cut/copy/paste, and zoom in/out.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 *
 */
public class GraphActionBarContributor extends ActionBarContributor {

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
   */
  @Override
  protected void buildActions() {
    final IWorkbenchWindow iww = getPage().getWorkbenchWindow();

    addRetargetAction((RetargetAction) ActionFactory.COPY.create(iww));
    addRetargetAction((RetargetAction) ActionFactory.CUT.create(iww));
    addRetargetAction((RetargetAction) ActionFactory.DELETE.create(iww));
    addRetargetAction((RetargetAction) ActionFactory.PASTE.create(iww));
    addRetargetAction((RetargetAction) ActionFactory.PRINT.create(iww));
    addRetargetAction((RetargetAction) ActionFactory.REDO.create(iww));
    addRetargetAction((RetargetAction) ActionFactory.UNDO.create(iww));

    addRetargetAction(new ZoomInRetargetAction());
    addRetargetAction(new ZoomOutRetargetAction());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
   */
  @Override
  public void contributeToToolBar(final IToolBarManager toolBarManager) {
    toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
    toolBarManager.add(getAction(ActionFactory.REDO.getId()));
    toolBarManager.add(getAction(ActionFactory.DELETE.getId()));

    toolBarManager.add(new Separator());

    toolBarManager.add(getAction(ActionFactory.CUT.getId()));
    toolBarManager.add(getAction(ActionFactory.COPY.getId()));
    toolBarManager.add(getAction(ActionFactory.PASTE.getId()));

    toolBarManager.add(new Separator());

    toolBarManager.add(getAction(GEFActionConstants.ZOOM_IN));
    toolBarManager.add(getAction(GEFActionConstants.ZOOM_OUT));
    toolBarManager.add(new ZoomComboContributionItem(getPage()));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
   */
  @Override
  protected void declareGlobalActionKeys() {
    addGlobalActionKey(ActionFactory.PRINT.getId());

    addGlobalActionKey(ActionFactory.UNDO.getId());
    addGlobalActionKey(ActionFactory.REDO.getId());
    addGlobalActionKey(ActionFactory.DELETE.getId());

    addGlobalActionKey(ActionFactory.CUT.getId());
    addGlobalActionKey(ActionFactory.COPY.getId());
    addGlobalActionKey(ActionFactory.PASTE.getId());

    addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
  }

}

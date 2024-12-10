/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
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

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;
import org.ietr.dftools.graphiti.ui.actions.SetRefinementAction;

/**
 * Build the context menu which is opened when right click with mouse.
 *
 * @author Samuel Beaussier & Nicolas Isch
 */
public class GraphEditorContextMenuProvider extends ContextMenuProvider {

  /** The action registry. */
  private ActionRegistry actionRegistry;

  /**
   * Create context menu associating a viewer and a register.
   *
   * @param viewer
   *          the viewer
   * @param registry
   *          the registry
   */
  public GraphEditorContextMenuProvider(final EditPartViewer viewer, final ActionRegistry registry) {
    super(viewer);
    setActionRegistry(registry);
  }

  /**
   * Adds the if enabled.
   *
   * @param registry
   *          the registry
   * @param menu
   *          the menu
   * @param actionId
   *          the action id
   */
  private void addIfEnabled(final ActionRegistry registry, final IMenuManager menu, final String actionId) {
    final IAction action = registry.getAction(actionId);
    if (action.isEnabled()) {
      menu.appendToGroup(GEFActionConstants.GROUP_VIEW, action);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
   */
  @Override
  public void buildContextMenu(final IMenuManager menu) {
    IAction action;

    GEFActionConstants.addStandardActionGroups(menu);
    final ActionRegistry registry = getActionRegistry();

    addIfEnabled(registry, menu, SetRefinementAction.getActionId());

    action = registry.getAction(ActionFactory.UNDO.getId());
    menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

    action = registry.getAction(ActionFactory.REDO.getId());
    menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

    action = registry.getAction(ActionFactory.CUT.getId());
    menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);

    action = registry.getAction(ActionFactory.COPY.getId());
    menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);

    action = registry.getAction(ActionFactory.PASTE.getId());
    menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);

    action = registry.getAction(ActionFactory.DELETE.getId());
    menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
  }

  /**
   * Gets the action registry.
   *
   * @return the actionRegistry
   * @uml.property name="actionRegistry"
   */
  private ActionRegistry getActionRegistry() {
    return this.actionRegistry;
  }

  /**
   * Sets the action registry.
   *
   * @param registry
   *          the new action registry
   * @uml.property name="actionRegistry"
   */
  private void setActionRegistry(final ActionRegistry registry) {
    this.actionRegistry = registry;
  }

}

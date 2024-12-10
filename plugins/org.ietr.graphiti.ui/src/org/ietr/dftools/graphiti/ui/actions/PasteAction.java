/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
package org.ietr.dftools.graphiti.ui.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.graphiti.ui.commands.copypaste.PasteCommand;
import org.ietr.dftools.graphiti.ui.editparts.GraphEditPart;
import org.ietr.dftools.graphiti.ui.editparts.VertexEditPart;

/**
 * This class provides an implementation of the paste action.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 *
 */
public class PasteAction extends SelectionAction implements PropertyChangeListener {

  /**
   * Constructor for PasteAction.
   *
   * @param editor
   *          the editor
   */
  public PasteAction(final IWorkbenchPart editor) {
    super(editor);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
   */
  @Override
  protected boolean calculateEnabled() {
    // Enabled if the clipboard is not empty and we know where to paste:
    // either the selected object is a GraphEditPart or a VertexEditPart
    final List<?> selection = getSelectedObjects();
    final List<?> vertices = getClipboardContents();
    return ((vertices != null) && (!vertices.isEmpty()) && (vertices.get(0) instanceof Vertex) && (selection != null)
        && (selection.size() == 1)
        && ((selection.get(0) instanceof GraphEditPart) || (selection.get(0) instanceof VertexEditPart)));
  }

  /**
   * Gets the clipboard contents.
   *
   * @return the clipboard contents
   */
  protected List<Vertex> getClipboardContents() {
    final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
    final Object data = GraphitiClipboard.getInstance().getContents(transfer);
    if (data instanceof IStructuredSelection) {
      @SuppressWarnings("unchecked")
      final List<Vertex> list = ((IStructuredSelection) data).toList();
      return list;
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Inits the.
   *
   * @see org.eclipse.gef.ui.actions.EditorPartAction#init()
   */
  @Override
  protected void init() {
    setId(ActionFactory.PASTE.getId());
    setText("Paste");
    setToolTipText("Paste");

    final ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
    setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
    setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
    setEnabled(false);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(final PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals(GraphitiClipboard.CONTENTS_SET_EVENT)) {
      setEnabled(calculateEnabled());
    }
  }

  /**
   * Executes a new {@link PasteCommand}.
   */
  @Override
  public void run() {
    final Object obj = getSelectedObjects().get(0);
    GraphEditPart part = null;
    if (obj instanceof GraphEditPart) {
      part = (GraphEditPart) obj;
    } else if (obj instanceof VertexEditPart) {
      part = (GraphEditPart) ((VertexEditPart) obj).getParent();
    }

    // execute the paste command
    final List<Vertex> contents = getClipboardContents();
    final PasteCommand command = new PasteCommand(part, contents);
    command.run();
    if (command.isDirty()) {
      execute(command);
    }
  }
}

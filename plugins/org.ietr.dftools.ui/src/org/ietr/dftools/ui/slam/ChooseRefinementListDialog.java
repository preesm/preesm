/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
/**
 *
 */
package org.ietr.dftools.ui.slam;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.ietr.dftools.architecture.slam.serialize.RefinementList;
import org.ietr.dftools.graphiti.model.Vertex;

/**
 * A dialog box used to select multiple refinements for a single vertex in a Graphiti graph. It also gives a mean to
 * chose one of the refinements.
 *
 * @author mpelcat
 *
 */
public class ChooseRefinementListDialog extends ListDialog {

  /**
   * Listener used when the buttons are pressed.
   *
   * @see RefinementEvent
   */
  private class RefinementListener implements SelectionListener {

    /** type of action of the listener. */
    private final String type;

    /** the called dialog window. */
    private final ChooseRefinementListDialog dialog;

    /**
     * Instantiates a new refinement listener.
     *
     * @param type
     *          the type
     * @param dialog
     *          the dialog
     */
    public RefinementListener(final String type, final ChooseRefinementListDialog dialog) {
      this.type = type;
      this.dialog = dialog;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetSelected(final SelectionEvent e) {
      final TableViewer tableViewer = this.dialog.getTableViewer();

      if (tableViewer.getSelection() instanceof IStructuredSelection) {
        final IStructuredSelection tableSelection = (IStructuredSelection) tableViewer.getSelection();
        final Object[] tableSelections = tableSelection.toArray();
        if (this.type.equals("add")) {
          final IPath name = ChooseRefinementListDialog.this.policy
              .useExistingFile(ChooseRefinementListDialog.this.vertex, "slam");
          tableViewer.add(name);
        } else if (this.type.equals("remove")) {
          if ((tableSelections.length == 1) && (tableSelections[0] instanceof String)) {
            final String name = (String) tableSelections[0];
            tableViewer.remove(name);
            ChooseRefinementListDialog.this.refinementList.removeName(name);
          }
        }
      }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
      // no action
    }
  }

  /** Listener used when the add button is pressed. */
  private RefinementListener addListener = null;

  /** Listener used when the remove button is pressed. */
  private RefinementListener removeListener = null;

  /** Refinement policy used to call refinement dialog. */
  private SlamRefinementPolicy policy = null;

  /** The vertex being refined. */
  private Vertex vertex = null;

  /** The returned refinement. */
  private RefinementList refinementList = null;

  /** Is this window used to edit the list or to choose one element. */
  private boolean edit = false;

  /**
   * Creating A dialog box used to select multiple refinements for a single vertex in a Graphiti graph.
   *
   * @param vertex
   *          the vertex being refined
   * @param shell
   *          the parent Shell
   * @param policy
   *          the refinement policy that initiated this window
   * @param edit
   *          is this window used to edit the list or to choose one element
   */
  public ChooseRefinementListDialog(final Vertex vertex, final Shell shell, final SlamRefinementPolicy policy,
      final boolean edit) {
    super(shell);
    // the cancel button is enabled only in case of choice
    setAddCancelButton(!edit);

    setHelpAvailable(false);
    this.policy = policy;
    this.vertex = vertex;
    this.edit = edit;

    final ILabelProvider labelProvider = new LabelProvider();
    setLabelProvider(labelProvider);
    setContentProvider(new WorkbenchContentProvider() {
      @Override
      public Object[] getChildren(final Object element) {
        if (element instanceof RefinementList) {
          return ((RefinementList) element).toStringArray();
        }
        return super.getChildren(element);
      }
    });
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.dialogs.ListDialog#setInput(java.lang.Object)
   */
  @Override
  public void setInput(final Object input) {
    super.setInput(input);

    if (input instanceof RefinementList) {
      this.refinementList = (RefinementList) input;
    }
  }

  /**
   * Open dialog.
   *
   * @return the refinement list
   */
  public RefinementList openDialog() {
    open();
    return this.refinementList;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.dialogs.ListDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(final Composite container) {
    final Control control = super.createDialogArea(container);

    this.addListener = new RefinementListener("add", this);
    this.removeListener = new RefinementListener("remove", this);

    return control;
  }

  /**
   * Creating specific buttons additionnally to the original ones from {@link ListDialog}.
   *
   * @param parent
   *          the parent Composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    if (this.edit) {
      final Button addButton = createButton(parent, 5000, "Add", true);
      final Button removeButton = createButton(parent, 5001, "Remove", true);

      addButton.addSelectionListener(this.addListener);
      removeButton.addSelectionListener(this.removeListener);

    }

    super.createButtonsForButtonBar(parent);
  }

}

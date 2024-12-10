/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
package org.ietr.dftools.graphiti.ui.properties;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ietr.dftools.graphiti.model.AbstractObject;
import org.ietr.dftools.graphiti.ui.commands.ParameterChangeValueCommand;
import org.ietr.dftools.graphiti.ui.editors.GraphEditor;

/**
 * This class defines a list section.
 *
 * @author Matthieu Wipliez
 *
 */
public class ListSection extends AbstractSection {

  /**
   * This class is a {@link CellLabelProvider} for a list.
   *
   * @author Matthieu Wipliez
   */
  private class ListCellLabelProvider extends CellLabelProvider {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.CellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
     */
    @Override
    public void update(final ViewerCell cell) {
      if (cell.getColumnIndex() == 0) {
        cell.setText((String) cell.getElement());
      }
    }

  }

  /**
   * This class defines a content provider for a list.
   *
   * @author Matthieu Wipliez
   */
  private class ListContentProvider implements IStructuredContentProvider {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(final Object inputElement) {
      final AbstractObject model = (AbstractObject) inputElement;
      final java.util.List<?> list = (java.util.List<?>) model.getValue(ListSection.this.parameterName);
      return list.toArray();
    }

  }

  /**
   * This class provides {@link EditingSupport} for lists.
   *
   * @author Matthieu Wipliez
   *
   */
  private class ListEditingSupport extends EditingSupport {

    /** The editor. */
    private final TextCellEditor editor;

    /**
     * Creates a new {@link ListEditingSupport} on the given column viewer and table.
     *
     * @param viewer
     *          the viewer
     * @param table
     *          the table
     */
    public ListEditingSupport(final ColumnViewer viewer, final Table table) {
      super(viewer);
      this.editor = new TextCellEditor(table);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
     */
    @Override
    protected boolean canEdit(final Object element) {
      return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
     */
    @Override
    protected CellEditor getCellEditor(final Object element) {
      return this.editor;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
     */
    @Override
    protected Object getValue(final Object element) {
      return element.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void setValue(final Object element, final Object value) {
      final AbstractObject model = (AbstractObject) getViewer().getInput();
      @SuppressWarnings("unchecked")
      final java.util.List<Object> oldList = (java.util.List<Object>) model.getValue(ListSection.this.parameterName);
      final int index = oldList.indexOf(element);
      if ((index == -1) || oldList.get(index).equals(value)) {
        return;
      }

      final java.util.List<Object> newList = new ArrayList<>(oldList);
      newList.set(index, value);

      final IWorkbenchPart part = getPart();
      if (part instanceof GraphEditor) {
        final ParameterChangeValueCommand command = new ParameterChangeValueCommand(model, "Change list value");
        command.setValue(ListSection.this.parameterName, newList);
        ((GraphEditor) part).executeCommand(command);
      }
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.ui.properties.AbstractSection#buttonAddSelected()
   */
  @Override
  protected void buttonAddSelected() {
    final AbstractObject model = getModel();

    final String dialogTitle = "New value";
    final String dialogMessage = "Please enter a value:";
    final String initialValue = "";
    final InputDialog dialog = new InputDialog(getShell(), dialogTitle, dialogMessage, initialValue,
        newText -> newText.isEmpty() ? "" : null);

    if (dialog.open() == Window.OK) {
      @SuppressWarnings("unchecked")
      final List<Object> oldList = (List<Object>) model.getValue(this.parameterName);
      final List<Object> newList = new ArrayList<>(oldList);
      newList.add(dialog.getValue());

      final IWorkbenchPart part = getPart();
      if (part instanceof GraphEditor) {
        final ParameterChangeValueCommand command = new ParameterChangeValueCommand(model, "Add element to list");
        command.setValue(this.parameterName, newList);
        ((GraphEditor) part).executeCommand(command);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.ui.properties.AbstractSection#buttonRemoveSelected()
   */
  @Override
  protected void buttonRemoveSelected() {
    final IStructuredSelection ssel = getTableSelection();
    if ((ssel != null) && !ssel.isEmpty()) {
      final Object obj = ssel.getFirstElement();

      final AbstractObject model = getModel();
      @SuppressWarnings("unchecked")
      final List<Object> oldList = (List<Object>) model.getValue(this.parameterName);
      final List<Object> newList = new ArrayList<>(oldList);
      newList.remove(obj);

      final IWorkbenchPart part = getPart();
      if (part instanceof GraphEditor) {
        final ParameterChangeValueCommand command = new ParameterChangeValueCommand(model, "Remove element from list");
        command.setValue(this.parameterName, newList);
        ((GraphEditor) part).executeCommand(command);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.ui.properties.AbstractSection#createControls(org.eclipse.swt.widgets.Composite,
   * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
   */
  @Override
  public void createControls(final Composite parent, final TabbedPropertySheetPage aTabbedPropertySheetPage) {
    super.createControls(parent, aTabbedPropertySheetPage);

    createListTable(getForm().getBody());
  }

  /**
   * Creates the list table.
   *
   * @param parent
   *          the parent
   */
  private void createListTable(final Composite parent) {
    final Table table = createTable(parent);

    // spans on 4 vertical cells
    final GridData data = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 4);
    table.setLayoutData(data);

    createShiftButtons(parent, table);

    final TableViewer tableViewer = getViewer();
    tableViewer.setContentProvider(new ListContentProvider());
    tableViewer.setLabelProvider(new ListCellLabelProvider());

    // 1st column
    final TableColumn column = new TableColumn(table, SWT.NONE);
    column.setText("Value");
    column.setWidth(200);

    // first column
    final TableViewerColumn tvc = new TableViewerColumn(tableViewer, column);
    tvc.setLabelProvider(new ListCellLabelProvider());

    // editing support
    final EditingSupport editing = new ListEditingSupport(tvc.getViewer(), tableViewer.getTable());
    tvc.setEditingSupport(editing);
  }

  /**
   * Creates the shift buttons.
   *
   * @param parent
   *          the parent
   * @param table
   *          the table
   */
  private void createShiftButtons(final Composite parent, final Table table) {
    // create buttons
    final Button shiftUp = getWidgetFactory().createButton(parent, "Up", SWT.NONE);
    final Button shiftDown = getWidgetFactory().createButton(parent, "Down", SWT.NONE);

    shiftUp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
    shiftUp.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        shiftValue(-1);
        updateButtonsState(table, shiftUp, shiftDown);
      }

    });

    // create buttons
    shiftDown.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
    shiftDown.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        shiftValue(1);
        updateButtonsState(table, shiftUp, shiftDown);
      }

    });

    table.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        updateButtonsState(table, shiftUp, shiftDown);
      }

    });
  }

  /**
   * Shift value.
   *
   * @param offset
   *          the offset
   */
  private void shiftValue(final int offset) {
    final IStructuredSelection ssel = getTableSelection();
    if ((ssel != null) && !ssel.isEmpty()) {
      final Object obj = ssel.getFirstElement();

      final AbstractObject model = getModel();
      @SuppressWarnings("unchecked")
      final List<Object> oldList = (List<Object>) model.getValue(this.parameterName);
      final List<Object> newList = new ArrayList<>(oldList);

      final int index = newList.indexOf(obj);
      final Object element = newList.remove(index);
      newList.add(index + offset, element);

      final IWorkbenchPart part = getPart();
      if (part instanceof GraphEditor) {
        final ParameterChangeValueCommand command = new ParameterChangeValueCommand(model, "Move element");
        command.setValue(this.parameterName, newList);
        ((GraphEditor) part).executeCommand(command);
      }
    }
  }

  /**
   * Update buttons state.
   *
   * @param table
   *          the table
   * @param shiftUp
   *          the shift up
   * @param shiftDown
   *          the shift down
   */
  private void updateButtonsState(final Table table, final Button shiftUp, final Button shiftDown) {
    final int index = table.getSelectionIndex();
    shiftUp.setEnabled(index > 0);
    shiftDown.setEnabled(index < (table.getItemCount() - 1));
  }

}

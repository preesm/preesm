/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2011)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2011)
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

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ietr.dftools.graphiti.model.AbstractObject;
import org.ietr.dftools.graphiti.ui.commands.ParameterChangeValueCommand;
import org.ietr.dftools.graphiti.ui.editors.GraphEditor;

/**
 * This class defines a map section.
 *
 * @author Matthieu Wipliez
 *
 */
public class MapSection extends AbstractSection {

  /**
   * This class is a {@link CellLabelProvider} for a map.
   *
   * @author Matthieu Wipliez
   */
  private class MapCellLabelProvider extends CellLabelProvider {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.CellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
     */
    @Override
    public void update(final ViewerCell cell) {
      final Object element = cell.getElement();
      @SuppressWarnings("unchecked")
      final Entry<Object, Object> entry = (Entry<Object, Object>) element;
      if (cell.getColumnIndex() == 0) {
        cell.setText(entry.getKey().toString());
      } else {
        Object value = entry.getValue();
        if (value == null) {
          value = "";
        }
        cell.setText(value.toString());
      }
    }

  }

  /**
   * This class defines a content provider for a map.
   *
   * @author Matthieu Wipliez
   */
  private class MapContentProvider implements IStructuredContentProvider {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(final Object inputElement) {
      final AbstractObject model = (AbstractObject) inputElement;
      final Map<?, ?> map = (Map<?, ?>) model.getValue(MapSection.this.parameterName);
      return map.entrySet().toArray();
    }

  }

  /**
   * This class provides {@link EditingSupport} for keys of a map.
   *
   * @author Matthieu Wipliez
   *
   */
  private class MapEditingSupport extends EditingSupport {

    /** The editor. */
    private final TextCellEditor editor;

    /** The key. */
    private final boolean key;

    /**
     * Creates a new {@link MapEditingSupport} on the given column viewer and table.
     *
     * @param viewer
     *          the viewer
     * @param table
     *          the table
     * @param keyMode
     *          <code>true</code> for key, <code>false</code> for value
     */
    public MapEditingSupport(final ColumnViewer viewer, final Table table, final boolean keyMode) {
      super(viewer);
      this.editor = new TextCellEditor(table);
      this.key = keyMode;
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
      @SuppressWarnings("unchecked")
      final Entry<Object, Object> entry = (Entry<Object, Object>) element;
      Object value = this.key ? entry.getKey() : entry.getValue();
      if (value == null) {
        value = "";
      }

      return value.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void setValue(final Object element, final Object value) {
      // if key, value is a new key; otherwise it is a new value
      // if value only contains spaces, ignore it
      if (((String) value).trim().isEmpty()) {
        return;
      }

      final AbstractObject model = (AbstractObject) getViewer().getInput();
      @SuppressWarnings("unchecked")
      final Map<Object, Object> oldMap = (Map<Object, Object>) model.getValue(MapSection.this.parameterName);

      @SuppressWarnings("unchecked")
      final Entry<Object, Object> entry = (Entry<Object, Object>) element;
      if (value.equals(this.key ? entry.getKey() : entry.getValue())) {
        return;
      }

      final Map<Object, Object> newMap = new TreeMap<>(oldMap);
      if (this.key) {
        // update the value associated with the new key
        newMap.put(value, newMap.remove(entry.getKey()));
      } else {
        newMap.put(entry.getKey(), value);
      }

      final IWorkbenchPart part = getPart();
      if (part instanceof GraphEditor) {
        final ParameterChangeValueCommand command = new ParameterChangeValueCommand(model,
            "Change " + (this.key ? "name of value" : "value"));
        command.setValue(MapSection.this.parameterName, newMap);
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
    final String dialogTitle = "New value";
    final String dialogMessage = "Please enter a value:";
    final String initialValue = "";
    final InputDialog dialog = new InputDialog(getShell(), dialogTitle, dialogMessage, initialValue,
        newText -> newText.isEmpty() ? "" : null);

    if (dialog.open() == Window.OK) {
      final AbstractObject model = getModel();
      @SuppressWarnings("unchecked")
      final Map<Object, Object> oldMap = (Map<Object, Object>) model.getValue(this.parameterName);
      final Map<Object, Object> newMap = new TreeMap<>(oldMap);
      newMap.put(dialog.getValue(), "");

      final IWorkbenchPart part = getPart();
      if (part instanceof GraphEditor) {
        final ParameterChangeValueCommand command = new ParameterChangeValueCommand(model, "Add element from map");
        command.setValue(this.parameterName, newMap);
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
      final Map<Object, Object> oldMap = (Map<Object, Object>) model.getValue(this.parameterName);
      final Map<Object, Object> newMap = new TreeMap<>(oldMap);
      @SuppressWarnings("unchecked")
      final Entry<Object, Object> entry = (Entry<Object, Object>) obj;
      newMap.remove(entry.getKey());

      final IWorkbenchPart part = getPart();
      if (part instanceof GraphEditor) {
        final ParameterChangeValueCommand command = new ParameterChangeValueCommand(model, "Remove element from map");
        command.setValue(this.parameterName, newMap);
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

    // insuring that the containing composite is filled by the table
    final GridData data = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
    parent.setLayoutData(data);
    createMapTable(getForm().getBody());
  }

  /**
   * Creates the map table.
   *
   * @param parent
   *          the parent
   */
  private void createMapTable(final Composite parent) {
    final Table table = createTable(parent);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    // spans on 2 vertical cells
    final GridData data = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2);

    // preventing the table from becoming too small
    data.minimumHeight = 100;
    table.setLayoutData(data);

    final TableViewer tableViewer = getViewer();
    tableViewer.setContentProvider(new MapContentProvider());
    tableViewer.setLabelProvider(new MapCellLabelProvider());

    final MapCellLabelProvider labelProvider = new MapCellLabelProvider();

    // 1st column
    final TableColumn column1 = new TableColumn(table, SWT.NONE, 0);
    column1.setText("Name");
    column1.setWidth(200);

    // 2nd column
    final TableColumn column2 = new TableColumn(table, SWT.NONE, 1);
    column2.setText("Value");
    column2.setWidth(200);

    final TableViewerColumn tvc1 = new TableViewerColumn(tableViewer, column1);
    tvc1.setLabelProvider(labelProvider);

    final TableViewerColumn tvc2 = new TableViewerColumn(tableViewer, column2);
    tvc2.setLabelProvider(labelProvider);

    // editing support for first and second column
    tvc1.setEditingSupport(new MapEditingSupport(tvc1.getViewer(), table, true));
    tvc2.setEditingSupport(new MapEditingSupport(tvc2.getViewer(), table, false));
  }

}

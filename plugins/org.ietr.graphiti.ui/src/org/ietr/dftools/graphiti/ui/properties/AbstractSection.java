/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2011)
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ietr.dftools.graphiti.model.AbstractObject;

/**
 * This class defines an abstract section.
 *
 * @author Matthieu Wipliez
 *
 */
public abstract class AbstractSection extends AbstractPropertySection implements PropertyChangeListener {

  /** The button add. */
  private Button buttonAdd;

  /** The button remove. */
  private Button buttonRemove;

  /** The form. */
  private Form form;

  /** The parameter name. */
  protected String parameterName;

  /** The table viewer. */
  private TableViewer tableViewer;

  /**
   * Called when "Add..." is pressed.
   */
  protected abstract void buttonAddSelected();

  /**
   * Called when "Remove" is pressed.
   */
  protected abstract void buttonRemoveSelected();

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite,
   * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
   */
  @Override
  public void createControls(final Composite parent, final TabbedPropertySheetPage aTabbedPropertySheetPage) {
    super.createControls(parent, aTabbedPropertySheetPage);

    this.form = getWidgetFactory().createForm(parent);
    getWidgetFactory().decorateFormHeading(this.form);

    final Composite composite = this.form.getBody();
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
  }

  /**
   * Creates the table component from the <code>parent</code> composite.
   *
   * @param parent
   *          The parent composite.
   * @return The table created.
   */
  protected final Table createTable(final Composite parent) {
    // create table
    final int style = SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

    final Table table = getWidgetFactory().createTable(parent, style);
    this.tableViewer = new TableViewer(table);

    // create buttons
    this.buttonAdd = getWidgetFactory().createButton(parent, "Add...", SWT.NONE);
    this.buttonAdd.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
    this.buttonAdd.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        buttonAddSelected();
      }
    });

    // create buttons
    this.buttonRemove = getWidgetFactory().createButton(parent, "Remove", SWT.NONE);
    this.buttonRemove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
    this.buttonRemove.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        buttonRemoveSelected();
      }
    });

    return table;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
   */
  @Override
  public void dispose() {
    final AbstractObject model = getModel();
    if (model != null) {
      model.removePropertyChangeListener(this);
    }

    if (this.form != null) {
      this.form.dispose();
    }
  }

  /**
   * Returns the form of this section.
   *
   * @return the form of this section
   */
  public Form getForm() {
    return this.form;
  }

  /**
   * Returns the model associated with this section. May be <code>null</code>.
   *
   * @return the model associated with this section
   */
  public AbstractObject getModel() {
    if (this.tableViewer == null) {
      return null;
    }
    return (AbstractObject) this.tableViewer.getInput();
  }

  /**
   * Returns the shell associated with the form of this section.
   *
   * @return the shell associated with the form of this section
   */
  public Shell getShell() {
    return this.form.getShell();
  }

  /**
   * Returns the current selection on the table of this section. May be <code>null</code>.
   *
   * @return the current selection on the table of this section
   */
  public IStructuredSelection getTableSelection() {
    final ISelection sel = this.tableViewer.getSelection();
    if (sel instanceof IStructuredSelection) {
      return (IStructuredSelection) sel;
    }
    return null;
  }

  /**
   * Returns the viewer of the table of this section.
   *
   * @return the viewer of the table of this section
   */
  public TableViewer getViewer() {
    return this.tableViewer;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(final PropertyChangeEvent evt) {
    refresh();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
   */
  @Override
  public void refresh() {
    this.tableViewer.refresh();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput(org.eclipse.ui.IWorkbenchPart,
   * org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void setInput(final IWorkbenchPart part, final ISelection selection) {
    super.setInput(part, selection);

    // remove property listener on old model
    final AbstractObject oldModel = getModel();
    if (oldModel != null) {
      oldModel.removePropertyChangeListener(this);
    }

    if (selection instanceof IStructuredSelection) {
      final Object object = ((IStructuredSelection) selection).getFirstElement();
      if (object instanceof EditPart) {
        final Object editPartModel = ((EditPart) object).getModel();
        if (editPartModel instanceof AbstractObject) {
          final AbstractObject model = (AbstractObject) editPartModel;

          if (model.getParameter(this.parameterName) == null) {
            this.tableViewer.getTable().setEnabled(false);
            this.buttonAdd.setEnabled(false);
            this.buttonRemove.setEnabled(false);
          } else {
            this.tableViewer.getTable().setEnabled(true);
            this.buttonAdd.setEnabled(true);
            this.buttonRemove.setEnabled(true);

            model.addPropertyChangeListener(this);
            this.tableViewer.setInput(model);
          }
        }
      }
    }
  }

  /**
   * Sets the name of the parameter that this section uses.
   *
   * @param parameterName
   *          name of a parameter
   */
  public void setParameterName(final String parameterName) {
    this.parameterName = parameterName;
  }

}

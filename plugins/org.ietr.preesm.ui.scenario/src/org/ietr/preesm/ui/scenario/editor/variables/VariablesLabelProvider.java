/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2012)
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
package org.ietr.preesm.ui.scenario.editor.variables;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.ietr.dftools.algorithm.model.parameters.Variable;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.ui.scenario.editor.Messages;

// TODO: Auto-generated Javadoc
/**
 * Displays the labels for variables names and values.
 *
 * @author mpelcat
 */
public class VariablesLabelProvider implements ITableLabelProvider {

  /** The scenario. */
  private PreesmScenario scenario = null;

  /** The table viewer. */
  private TableViewer tableViewer = null;

  /** Constraints page used as a property listener to change the dirty state. */
  private IPropertyListener propertyListener = null;

  /**
   * Instantiates a new variables label provider.
   *
   * @param scenario
   *          the scenario
   * @param tableViewer
   *          the table viewer
   * @param propertyListener
   *          the property listener
   */
  public VariablesLabelProvider(final PreesmScenario scenario, final TableViewer tableViewer,
      final IPropertyListener propertyListener) {
    super();
    this.scenario = scenario;
    this.tableViewer = tableViewer;
    this.propertyListener = propertyListener;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
   */
  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String text = "";

    if (element instanceof Variable) {
      final Variable var = (Variable) element;

      if (columnIndex == 0) {
        text = var.getName();
      } else if (columnIndex == 1) {
        text = var.getValue();
      }
    }

    return text;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
   */
  @Override
  public void addListener(final ILabelProviderListener listener) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
   */
  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
   */
  @Override
  public void removeListener(final ILabelProviderListener listener) {
    // TODO Auto-generated method stub

  }

  /**
   * Handle double click.
   *
   * @param selection
   *          the selection
   */
  public void handleDoubleClick(final IStructuredSelection selection) {

    final IInputValidator validator = newText -> {
      String message = null;
      int size = 0;

      try {
        size = Integer.valueOf(newText);
      } catch (final NumberFormatException e) {
        size = 0;
      }

      if (size == 0) {
        message = "invalid integer";
      }

      return message;
    };

    if (selection.getFirstElement() instanceof Variable) {
      final Variable var = (Variable) selection.getFirstElement();

      final String title = Messages.getString("Variables.dialog.title");
      final String message = Messages.getString("Variables.dialog.message") + var.getName();

      final String init = var.getValue();

      final InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
          message, init, validator);
      if (dialog.open() == Window.OK) {
        final String value = dialog.getValue();

        var.setValue(value);
        this.scenario.getVariablesManager().setVariable(var.getName(), value);

        this.tableViewer.refresh();
        this.propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
      }
    }

  }

}

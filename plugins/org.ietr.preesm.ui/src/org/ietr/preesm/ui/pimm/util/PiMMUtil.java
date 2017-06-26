/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Hascoet <jhascoet@kalray.eu> (2016)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2016)
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
package org.ietr.preesm.ui.pimm.util;

import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.platform.IDiagramBehavior;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.ui.pimm.diagram.PiMMToolBehaviorProvider;
import org.ietr.preesm.ui.scenario.editor.EditorTools.FileContentProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class PiMMUtil.
 */
public class PiMMUtil {
  /**
   * Returns the currently active Shell.
   *
   * @return The currently active Shell.
   */
  private static Shell getShell() {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
  }

  /**
   * Opens an simple input dialog with OK and Cancel buttons.
   *
   * @param dialogTitle
   *          the dialog title, or <code>null</code> if none
   * @param dialogMessage
   *          the dialog message, or <code>null</code> if none
   * @param initialValue
   *          the initial input value, or <code>null</code> if none (equivalent to the empty string)
   * @param validator
   *          an input validator, or <code>null</code> if none
   * @return the string, or <code>null</code> if user cancels
   */
  public static String askString(final String dialogTitle, final String dialogMessage, final String initialValue, final IInputValidator validator) {
    String ret = null;
    final Shell shell = PiMMUtil.getShell();
    final InputDialog inputDialog = new InputDialog(shell, dialogTitle, dialogMessage, initialValue, validator);
    final int retDialog = inputDialog.open();
    if (retDialog == Window.OK) {
      ret = inputDialog.getValue();
    }
    return ret;
  }

  /**
   * Open an input dialog to select a pi file.
   *
   * @param dialogTitle
   *          the dialog title, or <code>null</code> if none
   * @param dialogMessage
   *          the dialog message, or <code>null</code> if none
   * @param validator
   *          an input validator, or <code>null</code> if none
   * @return the string, or <code>null</code> if user cancels
   */
  @Deprecated
  public static IPath askRefinement(final String dialogTitle, final String dialogMessage, final IInputValidator validator) {
    final Shell shell = PiMMUtil.getShell();

    // For now, authorized refinements are other PiGraphs (.pi files) and
    // .idl prototypes
    final Set<String> fileExtensions = new LinkedHashSet<>();
    fileExtensions.add("pi");
    fileExtensions.add("idl");
    fileExtensions.add("h");
    final FileContentProvider contentProvider = new FileContentProvider(fileExtensions);

    final ElementTreeSelectionDialog inputDialog = new ElementTreeSelectionDialog(shell, WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
        contentProvider);
    inputDialog.setAllowMultiple(false);
    inputDialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
    inputDialog.setMessage(dialogMessage);
    inputDialog.setTitle(dialogTitle);

    final int retDialog = inputDialog.open();
    if (retDialog == Window.OK) {
      final IFile file = (IFile) (inputDialog.getResult()[0]);
      return file.getFullPath();

    }
    return null;
  }

  /**
   * Ask file.
   *
   * @param dialogTitle
   *          the dialog title
   * @param dialogMessage
   *          the dialog message
   * @param validator
   *          the validator
   * @param fileExtensions
   *          the file extensions
   * @return the i path
   */
  public static IPath askFile(final String dialogTitle, final String dialogMessage, final IInputValidator validator, final Set<String> fileExtensions) {
    final Shell shell = PiMMUtil.getShell();

    final FileContentProvider contentProvider = new FileContentProvider(fileExtensions);

    final ElementTreeSelectionDialog inputDialog = new ElementTreeSelectionDialog(shell, WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
        contentProvider);
    inputDialog.setAllowMultiple(false);
    inputDialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
    inputDialog.setMessage(dialogMessage);
    inputDialog.setTitle(dialogTitle);

    final int retDialog = inputDialog.open();
    if (retDialog == Window.OK) {
      final IFile file = (IFile) (inputDialog.getResult()[0]);
      return file.getFullPath();
    }
    return null;
  }

  /**
   * Ask save file.
   *
   * @param dialogText
   *          the dialog text
   * @param fileExtensions
   *          the file extensions
   * @return the i path
   */
  public static IPath askSaveFile(final String dialogText, final Set<String> fileExtensions) {
    final Shell shell = PiMMUtil.getShell();

    final FileDialog inputDialog = new FileDialog(shell, SWT.SAVE);
    inputDialog.setText(dialogText);
    inputDialog.setOverwrite(true);
    inputDialog.setFilterExtensions(fileExtensions.toArray(new String[fileExtensions.size()]));

    final String retDialog = inputDialog.open();
    if (retDialog != null) {
      return new Path(retDialog);
    }
    return null;
  }

  /**
   * Select function.
   *
   * @param prototypes
   *          the prototypes
   * @param allProtoArray
   *          the all proto array
   * @param title
   *          the title
   * @param message
   *          the message
   * @param showOnlyValidPrototypes
   *          the show only valid prototypes
   * @return the function prototype
   */
  public static FunctionPrototype selectFunction(final FunctionPrototype[] prototypes, final FunctionPrototype[] allProtoArray, final String title,
      final String message, final boolean showOnlyValidPrototypes) {
    final ElementListSelectionDialog dialog = new ElementListSelectionDialog(PiMMUtil.getShell(), new LabelProvider() {

      @Override
      public String getText(final Object element) {
        if ((element != null) && (element instanceof FunctionPrototype)) {
          return ((FunctionPrototype) element).format();
        } else {
          return "";
        }
      }
    }) {

      final FunctionPrototype[] filteredPrototypes = prototypes;
      final FunctionPrototype[] allPrototypes      = allProtoArray;

      protected void switchDisplayedPrototypes(final boolean filtered) {
        if (filtered) {
          setListElements(this.filteredPrototypes);
        } else {
          setListElements(this.allPrototypes);
        }

        // Trick to force an update
        setFilter(getFilter());
      }

      @Override
      protected Control createDialogArea(final Composite parent) {
        final Composite composite = (Composite) super.createDialogArea(parent);
        final GridData data = new GridData();
        data.grabExcessVerticalSpace = false;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.BEGINNING;
        // Checkbox check = new Checkbox("Show only functions with
        // corresponding ports.", true);
        final Button check = new Button(composite, SWT.CHECK);
        check.setText("Show only functions with corresponding ports.");
        check.setSelection(showOnlyValidPrototypes);

        final SelectionListener listener = new SelectionAdapter() {
          @Override
          public void widgetSelected(final SelectionEvent e) {
            final boolean newState = ((Button) e.getSource()).getSelection();
            switchDisplayedPrototypes(newState);
          }
        };

        check.addSelectionListener(listener);

        return composite;
      }
    };

    dialog.setTitle(title);
    dialog.setMessage(message);
    if (showOnlyValidPrototypes) {
      dialog.setElements(prototypes);
    } else {
      dialog.setElements(allProtoArray);
    }

    final int retDialog = dialog.open();
    if (retDialog == Window.OK) {
      final FunctionPrototype proto = (FunctionPrototype) (dialog.getResult()[0]);
      return proto;
    }
    return null;
  }

  /**
   * Utility method used to set a temporary tooltip message for a given {@link GraphicsAlgorithm}.
   *
   * @param fp
   *          the {@link IFeatureProvider}
   * @param ga
   *          the {@link GraphicsAlgorithm}
   * @param iDiagramEditor
   *          the {@link IDiagramEditor}
   * @param message
   *          the message to put in the tooltip
   */
  public static void setToolTip(final IFeatureProvider fp, final GraphicsAlgorithm ga, final IDiagramBehavior iDiagramEditor, final String message) {
    final IToolBehaviorProvider behaviorProvider = fp.getDiagramTypeProvider().getCurrentToolBehaviorProvider();
    ((PiMMToolBehaviorProvider) behaviorProvider).setToolTip(ga, message);

    iDiagramEditor.refresh();
    ((PiMMToolBehaviorProvider) behaviorProvider).setToolTip(ga, null);
  }
}

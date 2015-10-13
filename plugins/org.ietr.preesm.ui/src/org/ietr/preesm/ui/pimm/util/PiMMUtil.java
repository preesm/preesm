/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.ui.pimm.util;

import java.util.HashSet;
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
import org.eclipse.ui.dialogs.FileSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.ui.pimm.diagram.PiMMToolBehaviorProvider;
import org.ietr.preesm.ui.scenario.editor.EditorTools.FileContentProvider;

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
	 * <p>
	 * 
	 * @param dialogTitle
	 *            the dialog title, or <code>null</code> if none
	 * @param dialogMessage
	 *            the dialog message, or <code>null</code> if none
	 * @param initialValue
	 *            the initial input value, or <code>null</code> if none
	 *            (equivalent to the empty string)
	 * @param validator
	 *            an input validator, or <code>null</code> if none
	 * @return the string, or <code>null</code> if user cancels
	 */
	public static String askString(String dialogTitle, String dialogMessage, String initialValue,
			IInputValidator validator) {
		String ret = null;
		Shell shell = getShell();
		InputDialog inputDialog = new InputDialog(shell, dialogTitle, dialogMessage, initialValue, validator);
		int retDialog = inputDialog.open();
		if (retDialog == Window.OK) {
			ret = inputDialog.getValue();
		}
		return ret;
	}

	/**
	 * Open an input dialog to select a pi file
	 * 
	 * @param dialogTitle
	 *            the dialog title, or <code>null</code> if none
	 * @param dialogMessage
	 *            the dialog message, or <code>null</code> if none
	 * @param validator
	 *            an input validator, or <code>null</code> if none
	 * @return the string, or <code>null</code> if user cancels
	 */
	@Deprecated
	public static IPath askRefinement(String dialogTitle, String dialogMessage, IInputValidator validator) {
		Shell shell = getShell();

		// For now, authorized refinements are other PiGraphs (.pi files) and
		// .idl prototypes
		Set<String> fileExtensions = new HashSet<String>();
		fileExtensions.add("pi");
		fileExtensions.add("idl");
		fileExtensions.add("h");
		FileContentProvider contentProvider = new FileContentProvider(fileExtensions);

		ElementTreeSelectionDialog inputDialog = new ElementTreeSelectionDialog(shell,
				WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(), contentProvider);
		inputDialog.setAllowMultiple(false);
		inputDialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		inputDialog.setMessage(dialogMessage);
		inputDialog.setTitle(dialogTitle);

		int retDialog = inputDialog.open();
		if (retDialog == Window.OK) {
			IFile file = (IFile) (inputDialog.getResult()[0]);
			return file.getFullPath();

		}
		return null;
	}

	public static IPath askFile(String dialogTitle, String dialogMessage, IInputValidator validator,
			Set<String> fileExtensions) {
		Shell shell = getShell();

		FileContentProvider contentProvider = new FileContentProvider(fileExtensions);

		ElementTreeSelectionDialog inputDialog = new ElementTreeSelectionDialog(shell,
				WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(), contentProvider);
		inputDialog.setAllowMultiple(false);
		inputDialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		inputDialog.setMessage(dialogMessage);
		inputDialog.setTitle(dialogTitle);

		int retDialog = inputDialog.open();
		if (retDialog == Window.OK) {
			IFile file = (IFile) (inputDialog.getResult()[0]);
			return file.getFullPath();
		}
		return null;
	}
	
	public static IPath askSaveFile(String dialogText, Set<String> fileExtensions) {
		Shell shell = getShell();

		FileDialog inputDialog = new FileDialog(shell, SWT.SAVE);
		inputDialog.setText(dialogText);
		inputDialog.setOverwrite(true);
		inputDialog.setFilterExtensions(fileExtensions.toArray(new String[fileExtensions.size()]));
		
		String retDialog = inputDialog.open();
		if (retDialog != null) {
			return new Path(retDialog);
		}
		return null;
	}

	public static FunctionPrototype selectFunction(final FunctionPrototype[] prototypes,
			final FunctionPrototype[] allProtoArray, String title, String message, boolean showOnlyValidPrototypes) {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element != null && element instanceof FunctionPrototype)
					return ((FunctionPrototype) element).format();
				else
					return "";
			}
		}) {

			final FunctionPrototype[] filteredPrototypes = prototypes;
			final FunctionPrototype[] allPrototypes = allProtoArray;

			protected void switchDisplayedPrototypes(boolean filtered) {
				if (filtered) {
					setListElements(filteredPrototypes);
				} else {
					setListElements(allPrototypes);
				}

				// Trick to force an update
				setFilter(getFilter());
			}

			@Override
			protected Control createDialogArea(Composite parent) {
				Composite composite = (Composite) super.createDialogArea(parent);
				GridData data = new GridData();
				data.grabExcessVerticalSpace = false;
				data.grabExcessHorizontalSpace = true;
				data.horizontalAlignment = GridData.FILL;
				data.verticalAlignment = GridData.BEGINNING;
				// Checkbox check = new Checkbox("Show only functions with
				// corresponding ports.", true);
				Button check = new Button(composite, SWT.CHECK);
				check.setText("Show only functions with corresponding ports.");
				check.setSelection(showOnlyValidPrototypes);

				SelectionListener listener = new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						boolean newState = ((Button) e.getSource()).getSelection();
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

		int retDialog = dialog.open();
		if (retDialog == Window.OK) {
			FunctionPrototype proto = (FunctionPrototype) (dialog.getResult()[0]);
			return proto;
		}
		return null;
	}

	/**
	 * Utility method used to set a temporary tooltip message for a given
	 * {@link GraphicsAlgorithm}
	 * 
	 * @param fp
	 *            the {@link IFeatureProvider}
	 * @param ga
	 *            the {@link GraphicsAlgorithm}
	 * @param iDiagramEditor
	 *            the {@link IDiagramEditor}
	 * @param message
	 *            the message to put in the tooltip
	 */
	public static void setToolTip(IFeatureProvider fp, GraphicsAlgorithm ga, IDiagramBehavior iDiagramEditor,
			String message) {
		IToolBehaviorProvider behaviorProvider = fp.getDiagramTypeProvider().getCurrentToolBehaviorProvider();
		((PiMMToolBehaviorProvider) behaviorProvider).setToolTip(ga, message);

		iDiagramEditor.refresh();
		((PiMMToolBehaviorProvider) behaviorProvider).setToolTip(ga, null);
	}
}

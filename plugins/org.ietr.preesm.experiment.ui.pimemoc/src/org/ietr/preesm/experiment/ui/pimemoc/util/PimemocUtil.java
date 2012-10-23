package org.ietr.preesm.experiment.ui.pimemoc.util;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.ietr.preesm.experiment.ui.pimemoc.diagram.PimemocToolBehaviorProvider;

public class PimemocUtil {
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
	public static String askString(String dialogTitle, String dialogMessage,
			String initialValue, IInputValidator validator) {
		String ret = null;
		Shell shell = getShell();
		InputDialog inputDialog = new InputDialog(shell, dialogTitle,
				dialogMessage, initialValue, validator);
		int retDialog = inputDialog.open();
		if (retDialog == Window.OK) {
			ret = inputDialog.getValue();
		}
		return ret;
	}



	/**
	 * @param context
	 */
	public static void setToolTip(IFeatureProvider fp, GraphicsAlgorithm ga,
			IDiagramEditor iDiagramEditor, String message) {
		IToolBehaviorProvider behaviorProvider = fp.getDiagramTypeProvider()
				.getCurrentToolBehaviorProvider();
		((PimemocToolBehaviorProvider) behaviorProvider)
				.setToolTip(ga, message);
	
		iDiagramEditor.refresh();
		((PimemocToolBehaviorProvider) behaviorProvider).setToolTip(ga, null);
	}

}

package org.ietr.preesm.core.ui.console;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleFactory;
import org.ietr.preesm.core.log.PreesmLogger;

/**
 * @author mpelcat
 * 
 */
public class CoreConsoleFactory implements IConsoleFactory {
	@Override
	public void openConsole() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				// Creates a console for the logger
				PreesmLogger.getLogger().createConsole();
			}
		}
	}

}

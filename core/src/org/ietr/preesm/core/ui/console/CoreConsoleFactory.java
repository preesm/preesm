package org.ietr.preesm.core.ui.console;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
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

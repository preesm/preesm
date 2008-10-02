package org.ietr.preesm.plugin.mapper.plot.timeswtdisplay;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class TimeEditorRunnable implements Runnable {

	private IEditorInput input;
	
	public TimeEditorRunnable(IEditorInput input) {
		super();
		this.input = input;
	}

	@Override
	public void run() {

		IWorkbenchWindow dwindow = PlatformUI.getWorkbench()
				.getWorkbenchWindows()[0];

		if (dwindow != null && input instanceof TimeEditorInput) {
			IWorkbenchPage page = dwindow.getActivePage();

			TimeEditor editor;
			try {
				editor = (TimeEditor) page
						.openEditor(input,
								"org.ietr.preesm.plugin.mapper.plot.timeswtdisplay.TimeEditor");

			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}

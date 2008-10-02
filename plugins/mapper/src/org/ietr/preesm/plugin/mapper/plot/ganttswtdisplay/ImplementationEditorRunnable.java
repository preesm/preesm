package org.ietr.preesm.plugin.mapper.plot.ganttswtdisplay;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ImplementationEditorRunnable implements Runnable {

	private IEditorInput input;
	
	public ImplementationEditorRunnable(IEditorInput input) {
		super();
		this.input = input;
	}

	@Override
	public void run() {

		IWorkbenchWindow dwindow = PlatformUI.getWorkbench()
				.getWorkbenchWindows()[0];

		if (dwindow != null && input instanceof ImplementationEditorInput) {
			IWorkbenchPage page = dwindow.getActivePage();

			ImplementationEditor editor;
			try {
				editor = (ImplementationEditor) page
						.openEditor(input,
								"org.ietr.preesm.plugin.mapper.plot.ganttswtdisplay.ImplementationEditor");

			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}

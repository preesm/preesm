package org.ietr.preesm.core.workflow;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenWorkflowOutput implements Runnable{
	
	private String editorId ;
	private IEditorInput input ;
	public OpenWorkflowOutput(IEditorInput input, String editorId){
		this.editorId = editorId ;
		this.input = input ;
	}
	
	public void run() {
		IWorkbenchWindow dwindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage page = dwindow.getActivePage();
		if (page != null) {

			try {
				page.openEditor(input,
						editorId);
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}

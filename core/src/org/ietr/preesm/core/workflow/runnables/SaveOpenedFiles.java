/**
 * 
 */
package org.ietr.preesm.core.workflow.runnables;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Saves all opened files in the workbench.
 * This runnable must be executed in the display thread.
 * 
 * @author mpelcat
 */
public class SaveOpenedFiles implements Runnable {

	@Override
	public void run() {
		IWorkbench workbench = PlatformUI.getWorkbench();

		for(IWorkbenchWindow window : workbench.getWorkbenchWindows()){
			for(IWorkbenchPage page : window.getPages()){
				for(IEditorReference edRef : page.getEditorReferences()){
					IEditorPart part = edRef.getEditor(false);
					if(part!=null){
						part.doSave(null);
					}
				}
			}
		}
	}

}

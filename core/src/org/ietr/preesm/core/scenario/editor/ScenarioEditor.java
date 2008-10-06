/**
 * 
 */
package org.ietr.preesm.core.scenario.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;

/**
 * @author mpelcat
 *
 */
public class ScenarioEditor extends SharedHeaderFormEditor {

	@Override
	protected void addPages() {
		this.activateSite();
		IFormPage page = new ConstraintsPage("Constraints","Constraints");
		try {
			addPage(page);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

}

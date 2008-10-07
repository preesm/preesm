/**
 * 
 */
package org.ietr.preesm.core.scenario.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author mpelcat
 *
 */
public class ScenarioEditor extends SharedHeaderFormEditor {

	private IFile scenarioFile = null;
	
	public ScenarioEditor() {
		super();

		IEditorInput input = getEditorInput();
		
		if(input instanceof FileEditorInput){
			FileEditorInput fileInput = (FileEditorInput) input;
			scenarioFile = fileInput.getFile();
		}
	}

	@Override
	protected void addPages() {
		//this.activateSite();
		IFormPage constraintsPage = new ConstraintsPage(null,this, "Constraints","Constraints");
		IFormPage timingsPage = new ConstraintsPage(null,this, "Timings","Timings");
		
		try {
			addPage(constraintsPage);
			addPage(timingsPage);
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

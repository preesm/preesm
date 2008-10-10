/**
 * 
 */
package org.ietr.preesm.core.scenario.editor.timings;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.ietr.preesm.core.scenario.Scenario;

/**
 * Timing editor within the implementation editor
 * 
 * @author mpelcat
 */
public class TimingsPage extends FormPage {
	
	Scenario scenario;
	
	@Override
	public Control getPartControl() {
		// TODO Auto-generated method stub
		return super.getPartControl();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		// TODO Auto-generated method stub
		super.createFormContent(managedForm);
		
		ScrolledForm f = managedForm.getForm();
		f.setText("Formular:");
		f.getBody().setLayout(new GridLayout());
		managedForm.getToolkit().createLabel(f.getBody(), "Feld1:");
		managedForm.getToolkit().createText(f.getBody(), "Wert1");
		managedForm.getToolkit().createHyperlink(f.getBody(), "Dies ist der Text", 0);
		managedForm.refresh();

	}

	public TimingsPage(Scenario scenario, FormEditor editor, String id, String title) {
		super(editor, id, title);
		
		this.scenario = scenario;
	}

	@Override
	public void createPartControl(Composite parent) {

		super.createPartControl(parent);
	}
}

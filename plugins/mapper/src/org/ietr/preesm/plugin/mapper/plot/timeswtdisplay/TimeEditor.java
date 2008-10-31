/**
 * 
 */
package org.ietr.preesm.plugin.mapper.plot.timeswtdisplay;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.ietr.preesm.plugin.mapper.plot.PlotBestLatency;

/**
 * Editor of an implementation Gantt chart
 * 
 * @author mpelcat
 */
public class TimeEditor extends EditorPart {

	private PlotBestLatency plotter = null;
	
	public TimeEditor() {
		super();
		// TODO Auto-generated constructor stub
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
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		
		try {
			setSite(site);
			setInput(input);
			setPartName(input.getName());
			
			if(input instanceof TimeEditorInput){
				TimeEditorInput implinput = (TimeEditorInput)input;
				this.plotter = implinput.getPlotter();
			}
			
		} catch (Exception e) {
			// Editor might not exist anymore if switching databases.  So
			// just close it.
			this.getEditorSite().getPage().closeEditor(this, false);
			throw new PartInitException("File " + input.getName()
					+ " does not exist.");
		} 

	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		
		if(plotter != null){

			plotter.display(parent);
		}
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public static void createEditor(PlotBestLatency plotter) {
		IEditorInput input = new TimeEditorInput(plotter);

		PlatformUI.getWorkbench().getDisplay().asyncExec(
				new TimeEditorRunnable(input));
		
	}
}
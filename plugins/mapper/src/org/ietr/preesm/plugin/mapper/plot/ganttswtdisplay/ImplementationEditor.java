/**
 * 
 */
package org.ietr.preesm.plugin.mapper.plot.ganttswtdisplay;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.plot.GanttPlotter;
import org.sdf4j.model.PropertyBean;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Editor of an implementation Gantt chart
 * 
 * @author mpelcat
 */
public class ImplementationEditor extends EditorPart {

	private MapperDAG dag = null;
	private SDFGraph sdf = null;
	private IArchitecture archi = null;
	private IScenario scenario = null;
	private TextParameters params = null;
	
	public ImplementationEditor() {
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
			
			if(input instanceof ImplementationEditorInput){
				ImplementationEditorInput implinput = (ImplementationEditorInput)input;
				this.archi = implinput.getArchi();
				this.dag = implinput.getDag();
				this.params = implinput.getParams();
				this.scenario = implinput.getScenario();
				this.sdf = implinput.getSdf();
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

		PropertyBean bean = dag.getPropertyBean();
		
		
		if(dag != null && sdf != null && archi != null && scenario != null && params != null){

			AbcType abctype = (AbcType)bean.getValue(AbstractAbc.propertyBeanName);
			
			IAbc simu = AbstractAbc
			.getInstance(abctype, dag, archi);

			simu.setDAG(dag);

			simu.getFinalTime();
			GanttPlotter.plotInComposite(dag, simu, parent);
		}
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
}
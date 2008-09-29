/**
 * 
 */
package org.ietr.preesm.plugin.mapper.plot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.sdf4j.importer.GMLDAGImporter;
import org.sdf4j.importer.InvalidFileException;
import org.sdf4j.model.dag.DirectedAcyclicGraph;

/**
 * Editor of an implementation Gantt chart
 * 
 * @author mpelcat
 */
public class ImplementationEditor extends EditorPart {

	private DirectedAcyclicGraph dag = null;
	
	private IAbc simulator = null;
	
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
		
		/*
		if(input instanceof FileEditorInput){
			IPath path = ((FileEditorInput)input).getPath();

			GMLDAGImporter importer = new GMLDAGImporter() ;
			try {
				dag = importer.parse(new FileInputStream(path.toOSString()));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
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

		if(dag != null && simulator != null){
			MapperDAG mapperDAG = (MapperDAG)dag;
			GanttPlotter.plotInComposite(mapperDAG, simulator, parent);
		}
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
}
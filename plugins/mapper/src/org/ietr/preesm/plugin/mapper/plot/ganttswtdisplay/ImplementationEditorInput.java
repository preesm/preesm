/**
 * 
 */
package org.ietr.preesm.plugin.mapper.plot.ganttswtdisplay;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.constraints.IScenario;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.mapper.Activator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * @author mpelcat
 *
 */
public class ImplementationEditorInput implements IEditorInput {

	private MapperDAG dag = null;
	private SDFGraph sdf = null;
	private IArchitecture archi = null;
	private IScenario scenario = null;
	private TextParameters params = null;
	
	public ImplementationEditorInput(IArchitecture archi, MapperDAG dag,
			TextParameters params, IScenario scenario, SDFGraph sdf) {
		super();
		this.archi = archi;
		this.dag = dag;
		this.params = params;
		this.scenario = scenario;
		this.sdf = sdf;
	}

	public MapperDAG getDag() {
		return dag;
	}

	public SDFGraph getSdf() {
		return sdf;
	}

	public IArchitecture getArchi() {
		return archi;
	}

	public IScenario getScenario() {
		return scenario;
	}

	public TextParameters getParams() {
		return params;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		ImageDescriptor img = Activator.getImageDescriptor("icons/preesm2mini.PNG");
		return img;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName() {
		return "Implementation of " + sdf.getName() + " on " + archi.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return "Implementation";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

}

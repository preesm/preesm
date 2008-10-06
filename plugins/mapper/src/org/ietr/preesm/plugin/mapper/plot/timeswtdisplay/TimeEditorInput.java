/**
 * 
 */
package org.ietr.preesm.plugin.mapper.plot.timeswtdisplay;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.mapper.Activator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.plot.PlotBestLatency;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * @author mpelcat
 *
 */
public class TimeEditorInput implements IEditorInput {

	private PlotBestLatency plotter = null;

	public TimeEditorInput(PlotBestLatency plotter) {
		super();
		this.plotter = plotter;
	}

	
	public PlotBestLatency getPlotter() {
		return plotter;
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
		return "Best Latency";
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
		return "Best Latency";
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

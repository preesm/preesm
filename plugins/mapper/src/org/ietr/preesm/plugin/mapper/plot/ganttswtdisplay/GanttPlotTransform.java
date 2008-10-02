/**
 * 
 */
package org.ietr.preesm.plugin.mapper.plot.ganttswtdisplay;

import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.constraints.IScenario;
import org.ietr.preesm.core.task.IPlotter;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFGraph;

public class GanttPlotTransform implements IPlotter {

	@Override
	public void transform(DirectedAcyclicGraph dag, SDFGraph sdf,
			IArchitecture archi, IScenario scenario, TextParameters params) {

		MapperDAG mapperDag = (MapperDAG) dag;


		IEditorInput input = new ImplementationEditorInput(archi, mapperDag, params, scenario, sdf);

		PlatformUI.getWorkbench().getDisplay().asyncExec(
				new ImplementationEditorRunnable(input));
	}

}
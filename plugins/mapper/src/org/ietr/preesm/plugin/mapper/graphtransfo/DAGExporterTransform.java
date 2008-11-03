/**
 * 
 */
package org.ietr.preesm.plugin.mapper.graphtransfo;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.IExporter;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.sdf4j.model.AbstractGraph;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFGraph;

public class DAGExporterTransform implements IExporter{

	@Override
	public void transform(DirectedAcyclicGraph dag, SDFGraph sdf, MultiCoreArchitecture archi, IScenario scenario, TextParameters params) {

		// TODO: complete with edition capabilities
		Path path = new Path(params.getVariable("path"));
		
		MapperDAG mapperDag = (MapperDAG)dag;
		GMLMapperDAGExporter exporter = new GMLMapperDAGExporter() ;
		MapperDAG clone = mapperDag.clone() ;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResource resource = workspace.getRoot().findMember(
				path.toOSString());
		exporter.export(clone, resource.getLocation().toOSString());
	}

	@Override
	public boolean isDAGExporter() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isSDFExporter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void transform(AbstractGraph algorithm, TextParameters params) {
		// TODO Auto-generated method stub
		
	}

}
/**
 * 
 */
package org.ietr.preesm.plugin.mapper.plot;

import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.task.IExporter;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.exporter.GMLDAGExporter;
import org.sdf4j.exporter.GMLSDFExporter;
import org.sdf4j.model.AbstractGraph;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFGraph;

public class GanttExporter implements IExporter{

	@Override
	public void transform(AbstractGraph algorithm, TextParameters params) {

		Path path = new Path(params.getVariable("path"));
		
		MapperDAG dag = (MapperDAG)algorithm;
		GMLDAGExporter exporter = new GMLDAGExporter() ;
		MapperDAG clone = dag.clone() ;
		exporter.export(clone, path.toString());
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

}
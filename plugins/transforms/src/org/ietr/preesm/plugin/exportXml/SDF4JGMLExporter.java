package org.ietr.preesm.plugin.exportXml;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.task.IExporter;
import org.ietr.preesm.core.task.TextParameters;
import org.sdf4j.exporter.GMLSDFExporter;
import org.sdf4j.model.AbstractGraph;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFGraph;

public class SDF4JGMLExporter implements IExporter{


	@SuppressWarnings("unchecked")
	public void transform(AbstractGraph algorithm, TextParameters params) {
			GMLSDFExporter exporter = new GMLSDFExporter() ;
			SDFGraph clone = ((SDFGraph)(algorithm)).clone() ;
			exporter.export(clone, params.getVariable("path"));
	}

	@Override
	public boolean isDAGExporter() {
		return false;
	}

	@Override
	public boolean isSDFExporter() {
		return true;
	}

	@Override
	public void transform(DirectedAcyclicGraph dag, SDFGraph sdf,
			MultiCoreArchitecture archi,
			org.ietr.preesm.core.scenario.IScenario scenario,
			TextParameters params) {
		// TODO Auto-generated method stub
		
	}


}

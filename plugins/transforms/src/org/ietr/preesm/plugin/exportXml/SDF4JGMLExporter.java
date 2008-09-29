package org.ietr.preesm.plugin.exportXml;

import org.ietr.preesm.core.task.IExporter;
import org.ietr.preesm.core.task.TextParameters;
import org.sdf4j.exporter.GMLSDFExporter;
import org.sdf4j.model.AbstractGraph;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSDFExporter() {
		// TODO Auto-generated method stub
		return false;
	}


}

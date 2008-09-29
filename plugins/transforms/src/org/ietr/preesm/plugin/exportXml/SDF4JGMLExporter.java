package org.ietr.preesm.plugin.exportXml;

import org.ietr.preesm.core.task.IExporter;
import org.ietr.preesm.core.task.TextParameters;
import org.sdf4j.exporter.GMLSDFExporter;
import org.sdf4j.model.sdf.SDFGraph;

public class SDF4JGMLExporter implements IExporter{

	@Override
	public void transform(SDFGraph algorithm, TextParameters params) {
		GMLSDFExporter exporter = new GMLSDFExporter() ;
		SDFGraph clone = algorithm.clone() ;
		exporter.export(clone, params.getVariable("path"));
	}

}

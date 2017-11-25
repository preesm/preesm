package fi.abo.preesm.dataparallel

import org.ietr.preesm.algorithm.exportXml.SDF2GraphmlExporter
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.exporter.GMLSDFExporter
import org.eclipse.core.runtime.CoreException

class SDFExporter extends SDF2GraphmlExporter {
	new() {
		super()
	}
	
	public def void export(SDFGraph algorithm, String xmlPath) {
		val exporter = new GMLSDFExporter
		try {
			exporter.export(algorithm, xmlPath + ".graphml")	
		} catch(CoreException ex) {
			ex.printStackTrace
		}
	}
}
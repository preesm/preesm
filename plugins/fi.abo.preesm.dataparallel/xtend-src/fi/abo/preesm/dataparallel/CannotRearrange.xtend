package fi.abo.preesm.dataparallel

import java.io.File
import org.ietr.dftools.algorithm.model.sdf.SDFGraph

class CannotRearrange extends Exception {
	static val message = "Cannot rearrange the DAG due to insufficient root instances."
	
	static val sdf_message = message + "\nExporting the associated SrSDF/SDF graph to "
	
	static val dag_message = message + "\nExporting the associated DAG to "
	
	static val sdf_dag_message = message + "\nExporting the associated SrSDF/SDF graph and DAG to "
	
	static val bug_graph_path = "./failed_sdf"
	
	static val bug_dag_path = "./failed_dag"
	
	static val bug_graph_file = new File(bug_graph_path)
	
	static val bug_dag_file = new File(bug_dag_path)
	
	static val bug_graph_abs_path = bug_graph_file.absolutePath
	
	static val bug_dag_abs_path = bug_dag_file.absolutePath
	
	new() {
		super(message)
	}
	
	new(String mes) {
		super(mes + "\n" + message )
	}
	
	new(SDFGraph graph, String mes) {
		super(mes + "\n" + sdf_message  + bug_graph_abs_path + " .\n")
		
    	val SDFExporter exporter = new SDFExporter()
    	exporter.export(graph, bug_graph_abs_path)
	}
	
	new(PureDAGConstructor dag, String mes) {
		super(mes + "\n" + dag_message + bug_dag_abs_path + " .\n")
    	val SDFExporter exporter = new SDFExporter()
    	exporter.export(dag.outputGraph, bug_dag_abs_path)
	}
	
	new(PureDAGConstructor dag, SDFGraph graph, String mes) {
		super(mes + "\n" + sdf_dag_message + bug_dag_abs_path + " and " + bug_graph_abs_path + " .\n")
		var SDFExporter exporter = new SDFExporter()
    	exporter.export(dag.outputGraph, bug_dag_abs_path)
    	exporter.export(graph, bug_graph_abs_path)
	}
}
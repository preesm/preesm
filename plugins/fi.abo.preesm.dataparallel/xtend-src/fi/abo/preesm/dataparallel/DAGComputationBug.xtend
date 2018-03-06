/**
 * Copyright or © or Copr. Åbo Akademi University (2017 - 2018),
 * IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Sudeep Kanur <skanur@abo.fi> (2017 - 2018)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fi.abo.preesm.dataparallel

import java.io.File
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.visitors.SDF4JException

/**
 * Exception class used exclusively when certain assertions fail
 * The exception is used to indicate existence of bugs and prints
 * additional contact information
 * 
 * @author Sudeep Kanur
 */
class DAGComputationBug extends SDF4JException {
	
	static val message = "Open an issue, or contact Sudeep Kanur (skanur@abo.fi, skanur@protonmail.com) with the graph that caused the exception."
	
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
		super("BUG!\n" + message, null)
	}
		
	/**
	 * Creates a new Bug exception with custom message
	 *
	 * @param message
	 *          The error message
	 */
	new(String mes) {
		super("BUG!\n" + mes + "\n" + message, null);
	}
	
	new(SDFGraph graph, String mes) {
		super("BUG!\n" + mes + "\n" + sdf_message  + bug_graph_abs_path + " .\n")
		
		val SDFExporter exporter = new SDFExporter()
		exporter.export(graph, bug_graph_abs_path)
	}
	
	new(PureDAGConstructor dag, String mes) {
		super("BUG!\n" + mes + "\n" + dag_message + bug_dag_abs_path + " .\n")
		val SDFExporter exporter = new SDFExporter()
		exporter.export(dag.outputGraph, bug_dag_abs_path)
	}
	
	new(PureDAGConstructor dag, SDFGraph graph, String mes) {
		super("BUG!\n" + mes + "\n" + sdf_dag_message + bug_dag_abs_path + " and " + 
			bug_graph_abs_path + " .\n")
		var SDFExporter exporter = new SDFExporter()
		exporter.export(dag.outputGraph, bug_dag_abs_path)
		exporter.export(graph, bug_graph_abs_path)
	}
}

package org.ietr.preesm.core.task;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.constraints.IScenario;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * This class provides a generic result for a transformation.
 * 
 * @author mpelcat
 */
public class TaskResult {

	/**
	 * Returned algorithm
	 */
	private SDFGraph sdf;
	
	/**
	 * Returned algorithm
	 */
	private DirectedAcyclicGraph dag;

	/**
	 * Returned architecture
	 */
	private IArchitecture architecture;

	/**
	 * Returned source file list
	 */
	private SourceFileList sourcefilelist;
	
	
	/**
	 * Returned scenario
	 */
	private IScenario scenario ;
	

	/**
	 * Creates a result with no contents.
	 */
	public TaskResult() {
		sdf = null;
		dag = null;
		architecture = null;
		sourcefilelist = null;
		scenario = null;
	}

	/**
	 * Returns the algorithm of this result.
	 */
	public SDFGraph getSDF() {
		return sdf;
	}

	/**
	 * Returns the algorithm of this result.
	 */
	public DirectedAcyclicGraph getDAG() {
		return dag;
	}

	/**
	 * Returns the architecture of this result.
	 */
	public IArchitecture getArchitecture() {
		return architecture;
	}

	/**
	 * Returns the source files of this result.
	 */
	public SourceFileList getSourcefilelist() {
		return sourcefilelist;
	}
	
	/**
	 * Gives the scenario of this result
	 * @return The scenarion of this result
	 */
	public IScenario getScenario(){
		return scenario ;
	}

	/**
	 * Returns true if the algorithm of this result exists.
	 */
	public boolean hasSDF() {
		return sdf != null;
	}

	/**
	 * Returns true if the algorithm of this result exists.
	 */
	public boolean hasDAG() {
		return dag != null;
	}

	/**
	 * Returns true if the architecture of this result exists.
	 */
	public boolean hasArchitecture() {
		return architecture != null;
	}

	/**
	 * Returns true if the source files of this result exists.
	 */
	public boolean hasSourcefilelist() {
		return sourcefilelist != null;
	}

	public void setSDF(SDFGraph sdf) {
		this.sdf = sdf;
	}

	public void setDAG(DirectedAcyclicGraph dag) {
		this.dag = dag;
	}
	
	public void setArchitecture(IArchitecture architecture) {
		this.architecture = architecture;
	}

	public void setSourcefilelist(SourceFileList sourcefilelist) {
		this.sourcefilelist = sourcefilelist;
	}
	
	public void setScenario(IScenario scenario){
		this.scenario = scenario ;
	}

	public String toString() {
		
		String result = "";
		
		if (sdf != null) {
			result += getSDF().toString();
		}

		if (architecture == null) {
			result += getArchitecture().toString();
		}
		
		return result;
	}
}


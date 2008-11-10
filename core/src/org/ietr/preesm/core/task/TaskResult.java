/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.task;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.scenario.IScenario;
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
	private MultiCoreArchitecture architecture;

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
	public MultiCoreArchitecture getArchitecture() {
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
	
	public void setArchitecture(MultiCoreArchitecture architecture) {
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


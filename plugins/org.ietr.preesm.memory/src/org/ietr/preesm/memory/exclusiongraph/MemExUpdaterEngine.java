/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.memory.exclusiongraph;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.algorithm.transforms.ForkJoinRemover;

public class MemExUpdaterEngine {

	private boolean verbose;
	private DirectedAcyclicGraph dag;
	private MemoryExclusionGraph memEx;
	private DirectedAcyclicGraph localDAG;
	private int before;
	private double density;
	// Rem: Logger is used to display messages in the console
	private Logger logger = WorkflowLogger.getLogger();

	public MemExUpdaterEngine(DirectedAcyclicGraph dag,
			MemoryExclusionGraph memEx, boolean verbose) {
		this.verbose = verbose;
		this.dag = dag;
		this.memEx = memEx;
		this.before = memEx.edgeSet().size();
	}

	public void createLocalDag(boolean forkJoin) {
		// Make a copy of the Input DAG for treatment
		// Clone is deep copy i.e. vertices are thus copied too.
		localDAG = (DirectedAcyclicGraph) dag.clone();
		if (localDAG == null) {
			localDAG = dag;
		}

		if (forkJoin) {
			ForkJoinRemover.supprImplodeExplode(localDAG);
		}
	}

	public void update(boolean lifetime) {
		updateWithSchedule();
		
		if (lifetime) {
			updateWithLifetimes();
		}
	}

	private void updateWithSchedule() {
		if (verbose) {
			logger.log(Level.INFO,
					"Memory exclusion graph : start updating with schedule");
		}
		
		memEx.updateWithSchedule(localDAG);

		this.density = memEx.edgeSet().size()
				/ (memEx.vertexSet().size() * (memEx.vertexSet().size() - 1) / 2.0);
		
		if (verbose) {
			logger.log(Level.INFO, "Memory exclusion graph updated with "
					+ memEx.vertexSet().size() + " vertices and density = "
					+ density);
			logger.log(
					Level.INFO,
					"Exclusions removed: "
							+ (before - memEx.edgeSet().size())
							+ " ("
							+ Math.round(100.00
									* (before - memEx.edgeSet().size())
									/ before) + "%)");
		}
	}
	
	private void updateWithLifetimes() {
		before = memEx.edgeSet().size();
		if (verbose) {
			logger.log(Level.INFO,
					"Memory exclusion graph : start updating with memObject lifetimes");
		}

		memEx.updateWithMemObjectLifetimes(localDAG);

		density = memEx.edgeSet().size()
				/ (memEx.vertexSet().size()
						* (memEx.vertexSet().size() - 1) / 2.0);

		if (verbose) {
			logger.log(Level.INFO, "Memory exclusion graph updated with "
					+ memEx.vertexSet().size() + " vertices and density = "
					+ density);
			logger.log(
					Level.INFO,
					"Exclusions removed: "
							+ (before - memEx.edgeSet().size())
							+ " ("
							+ Math.round(100.00
									* (before - memEx.edgeSet().size())
									/ before) + "%)");
		}
	
	}
}

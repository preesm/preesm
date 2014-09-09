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

			System.out.print(memEx.vertexSet().size()
					+ ";"
					+ memEx.edgeSet().size()
					/ (memEx.vertexSet().size()
							* (memEx.vertexSet().size() - 1) / 2.0) + ";");
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
									/ (double) before) + "%)");
			System.out.print(density + ";");
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
									/ (double) before) + "%)");
			System.out.println(density + ";");
		}
	
	}
}

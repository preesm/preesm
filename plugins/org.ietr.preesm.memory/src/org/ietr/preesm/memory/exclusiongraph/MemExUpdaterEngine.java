/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/
package org.ietr.preesm.memory.exclusiongraph;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.algorithm.transforms.ForkJoinRemover;

// TODO: Auto-generated Javadoc
/**
 * The Class MemExUpdaterEngine.
 */
public class MemExUpdaterEngine {

  /** The verbose. */
  private final boolean verbose;

  /** The dag. */
  private final DirectedAcyclicGraph dag;

  /** The mem ex. */
  private final MemoryExclusionGraph memEx;

  /** The local DAG. */
  private DirectedAcyclicGraph localDAG;

  /** The before. */
  private int before;

  /** The density. */
  private double density;

  /** The logger. */
  // Rem: Logger is used to display messages in the console
  private final Logger logger = WorkflowLogger.getLogger();

  /**
   * Instantiates a new mem ex updater engine.
   *
   * @param dag
   *          the dag
   * @param memEx
   *          the mem ex
   * @param verbose
   *          the verbose
   */
  public MemExUpdaterEngine(final DirectedAcyclicGraph dag, final MemoryExclusionGraph memEx, final boolean verbose) {
    this.verbose = verbose;
    this.dag = dag;
    this.memEx = memEx;
    this.before = memEx.edgeSet().size();
  }

  /**
   * Creates the local dag.
   *
   * @param forkJoin
   *          the fork join
   */
  public void createLocalDag(final boolean forkJoin) {
    // Make a copy of the Input DAG for treatment
    // Clone is deep copy i.e. vertices are thus copied too.
    this.localDAG = (DirectedAcyclicGraph) this.dag.clone();
    if (this.localDAG == null) {
      this.localDAG = this.dag;
    }

    if (forkJoin) {
      ForkJoinRemover.supprImplodeExplode(this.localDAG);
    }
  }

  /**
   * Update.
   *
   * @param lifetime
   *          the lifetime
   */
  public void update(final boolean lifetime) {
    updateWithSchedule();

    if (lifetime) {
      updateWithLifetimes();
    }
  }

  /**
   * Update with schedule.
   */
  private void updateWithSchedule() {
    if (this.verbose) {
      this.logger.log(Level.INFO, "Memory exclusion graph : start updating with schedule");
    }

    this.memEx.updateWithSchedule(this.localDAG);

    this.density = this.memEx.edgeSet().size() / ((this.memEx.vertexSet().size() * (this.memEx.vertexSet().size() - 1)) / 2.0);

    if (this.verbose) {
      this.logger.log(Level.INFO, "Memory exclusion graph updated with " + this.memEx.vertexSet().size() + " vertices and density = " + this.density);
      this.logger.log(Level.INFO, "Exclusions removed: " + (this.before - this.memEx.edgeSet().size()) + " ("
          + Math.round((100.00 * (this.before - this.memEx.edgeSet().size())) / this.before) + "%)");
    }
  }

  /**
   * Update with lifetimes.
   */
  private void updateWithLifetimes() {
    this.before = this.memEx.edgeSet().size();
    if (this.verbose) {
      this.logger.log(Level.INFO, "Memory exclusion graph : start updating with memObject lifetimes");
    }

    this.memEx.updateWithMemObjectLifetimes(this.localDAG);

    this.density = this.memEx.edgeSet().size() / ((this.memEx.vertexSet().size() * (this.memEx.vertexSet().size() - 1)) / 2.0);

    if (this.verbose) {
      this.logger.log(Level.INFO, "Memory exclusion graph updated with " + this.memEx.vertexSet().size() + " vertices and density = " + this.density);
      this.logger.log(Level.INFO, "Exclusions removed: " + (this.before - this.memEx.edgeSet().size()) + " ("
          + Math.round((100.00 * (this.before - this.memEx.edgeSet().size())) / this.before) + "%)");
    }

  }
}

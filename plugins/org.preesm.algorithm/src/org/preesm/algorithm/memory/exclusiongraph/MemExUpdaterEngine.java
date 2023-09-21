/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
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
package org.preesm.algorithm.memory.exclusiongraph;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.commons.logger.PreesmLogger;

/**
 * The Class MemExUpdaterEngine.
 */
public class MemExUpdaterEngine {

  private final boolean              verbose;
  private final DirectedAcyclicGraph dag;
  private final MemoryExclusionGraph memEx;
  private DirectedAcyclicGraph       localDAG;
  private final int                  before;
  private final Logger               logger = PreesmLogger.getLogger();

  /**
   */
  public MemExUpdaterEngine(final DirectedAcyclicGraph dag, final MemoryExclusionGraph memEx, final boolean verbose) {
    this.verbose = verbose;
    this.dag = dag;
    this.memEx = memEx;
    this.before = memEx.edgeSet().size();
  }

  /**
   * Creates the local dag.
   */
  public void createLocalDag() {
    // Make a copy of the Input DAG for treatment
    // Clone is deep copy i.e. vertices are thus copied too.
    this.localDAG = this.dag.copy();
    if (this.localDAG == null) {
      this.localDAG = this.dag;
    }
  }

  /**
   */
  public void update() {
    if (this.verbose) {
      this.logger.log(Level.INFO, "Memory exclusion graph : start updating with schedule");
    }

    this.memEx.updateWithSchedule(this.localDAG);

    final double density = this.memEx.edgeSet().size()
        / ((this.memEx.vertexSet().size() * (this.memEx.vertexSet().size() - 1)) / 2.0);

    if (this.verbose) {
      this.logger.log(Level.INFO, () -> "Memory exclusion graph updated with " + this.memEx.vertexSet().size()
          + " vertices and density = " + density);
      this.logger.log(Level.INFO, () -> "Exclusions removed: " + (this.before - this.memEx.edgeSet().size()) + " ("
          + Math.round((100.00 * (this.before - this.memEx.edgeSet().size())) / this.before) + "%)");
    }
  }
}

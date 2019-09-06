/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
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
package org.preesm.codegen.model.generator;

import java.util.List;
import java.util.Map;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.memory.allocation.MemoryAllocator;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.codegen.model.ActorBlock;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * @author dgageot
 *
 */
public abstract class AbstractCodegenModelGenerator {

  /** Targeted {@link Design Architecture} of the code generation. */
  protected Design archi;

  /**
   * {@link DirectedAcyclicGraph DAG} used to generate code. This {@link DirectedAcyclicGraph DAG} must be the result of
   * mapping/scheduling process.
   */
  protected MapperDAG algo;

  /**
   * {@link Map} of {@link String} and {@link MemoryExclusionGraph MEG} used to generate code. These
   * {@link MemoryExclusionGraph MemEx MEGs} must be the result of an allocation process. Each {@link String}
   * corresponds to a memory bank where the associated MEG is allocated.
   *
   * @see MemoryAllocator
   */
  protected Map<String, MemoryExclusionGraph> megs;

  /**
   * {@link PreesmScenario Scenario} at the origin of the call to the {@link AbstractCodegenPrinter Code Generator}.
   */
  protected Scenario scenario;

  /** The flag to activate PAPIFY instrumentation. */
  protected boolean papifyActive;

  /**
   * @param archi
   *          See {@link AbstractCodegenPrinter#archi}
   * @param algo
   *          See {@link AbstractCodegenPrinter#dag}
   * @param megs
   *          See {@link AbstractCodegenPrinter#megs}
   * @param scenario
   *          See {@link AbstractCodegenPrinter#scenario}
   */
  public AbstractCodegenModelGenerator(final Design archi, final MapperDAG algo,
      final Map<String, MemoryExclusionGraph> megs, final Scenario scenario) {
    this.archi = archi;
    this.algo = algo;
    this.megs = megs;
    this.scenario = scenario;
  }

  /**
   * Sets PAPIFY flag.
   *
   * @param papifyMonitoring
   *          the flag to set papify instrumentation
   */
  public final void registerPapify(final String papifyMonitoring) {

    if (!papifyMonitoring.equalsIgnoreCase("true")) {
      this.papifyActive = false;
    } else {
      this.papifyActive = true;
    }

  }

  /**
   * Method to generate the intermediate model of the codegen.
   *
   * @return a set of {@link Block blocks}. Each of these block corresponds to a part of the code to generate:
   *         <ul>
   *         <li>{@link CoreBlock A block corresponding to the code executed by a core}</li>
   *         <li>{@link ActorBlock A block corresponding to the code of an non-flattened hierarchical actor}</li>
   *         </ul>
   */
  public abstract List<Block> generate();

}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017)
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
package org.ietr.preesm.throughput;

import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.schedule.PeriodicScheduler_SDF;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;
import org.ietr.preesm.throughput.tools.transformers.IBSDFTransformer;
import org.ietr.preesm.throughput.tools.transformers.SDFTransformer;

/**
 * @author hderoui
 *
 */
public class ClassicalMethod {
  public Stopwatch timer;

  /**
   * Compute the throughput of the graph using the classical method base on flattening the hierarchy into a srSDF graph
   *
   * @param inputGraph
   *          srSDF graph
   * @return throughput of the graph
   */
  public double evaluate(final SDFGraph inputGraph, final boolean withExecRulres) {
    System.out.println("Computing the throughput of the graph using classical method ...");
    this.timer = new Stopwatch();
    this.timer.start();

    // Phase 1: convert the IBSDF graph to a flat srSDF graph
    System.out.println("Phase 1: convert the IBSDF graph to a flat srSDF graph");
    final SDFGraph srSDF = IBSDFTransformer.convertToSrSDF(inputGraph, withExecRulres);

    // Phase 2: compute the throughput of the flat srSDF graph using the periodic schedule
    System.out.println("Phase 2: compute the throughput of the flat srSDF graph using the periodic schedule");
    // -> Step 1: normalize the graph
    SDFTransformer.normalize(srSDF);

    // -> Step 2: compute the normalized period
    final PeriodicScheduler_SDF periodic = new PeriodicScheduler_SDF();
    final Fraction k = periodic.computeNormalizedPeriod(srSDF, PeriodicScheduler_SDF.Method.LinearProgram_Gurobi);

    // -> Step 3: compute the throughput as 1/k
    final double throughput = 1 / k.doubleValue();
    this.timer.stop();
    System.out.println("Throughput of the graph = " + throughput + " computed in " + this.timer.toString());

    return throughput;
  }

}

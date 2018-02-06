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
  public double evaluate(SDFGraph inputGraph, boolean withExecRulres) {
    System.out.println("Computing the throughput of the graph using classical method ...");
    this.timer = new Stopwatch();
    timer.start();

    // Phase 1: convert the IBSDF graph to a flat srSDF graph
    System.out.println("Phase 1: convert the IBSDF graph to a flat srSDF graph");
    SDFGraph srSDF = IBSDFTransformer.convertToSrSDF(inputGraph, withExecRulres);

    // Phase 2: compute the throughput of the flat srSDF graph using the periodic schedule
    System.out.println("Phase 2: compute the throughput of the flat srSDF graph using the periodic schedule");
    // -> Step 1: normalize the graph
    SDFTransformer.normalize(srSDF);

    // -> Step 2: compute the normalized period
    PeriodicScheduler_SDF periodic = new PeriodicScheduler_SDF();
    Fraction k = periodic.computeNormalizedPeriod(srSDF, PeriodicScheduler_SDF.Method.LinearProgram_Gurobi);

    // -> Step 3: compute the throughput as 1/k
    double throughput = 1 / k.doubleValue();
    timer.stop();
    System.out.println("Throughput of the graph = " + throughput + " computed in " + timer.toString());

    return throughput;
  }

}

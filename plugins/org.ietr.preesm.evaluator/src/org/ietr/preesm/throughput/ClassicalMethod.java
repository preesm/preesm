package org.ietr.preesm.throughput;

import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.schedule.PeriodicSchedule_SDF;
import org.ietr.preesm.throughput.transformers.IBSDFTransformer;
import org.ietr.preesm.throughput.transformers.SDFTransformer;

/**
 * @author hderoui
 *
 */
public class ClassicalMethod {

  /**
   * Compute the throughput of the graph using the classical method base on flattening the hierarchy into a srSDF graph
   * 
   * @param inputGraph
   *          srSDF graph
   * @param scenario
   *          contains actors duration
   * @return throughput of the graph
   */
  public double evaluate(SDFGraph inputGraph, PreesmScenario scenario) {
    System.out.println("Computing the throughput of the graph using classical method ...");

    // Phase 1: convert the IBSDF graph to a flat srSDF graph
    System.out.println("Phase 1: convert the IBSDF graph to a flat srSDF graph");
    SDFGraph srSDF = IBSDFTransformer.convertToSrSDF(inputGraph, false);

    // Phase 2: compute the throughput of the flat srSDF graph using the periodic schedule
    System.out.println("Phase 2: compute the throughput of the flat srSDF graph using the periodic schedule");
    // -> Step 1: normalize the graph
    SDFTransformer.normalize(srSDF);

    // -> Step 2: compute the normalized period
    PeriodicSchedule_SDF periodic = new PeriodicSchedule_SDF();
    Fraction k = periodic.computeNormalizedPeriod(srSDF, PeriodicSchedule_SDF.Method.LinearProgram_Gurobi);

    // -> Step 3: compute the throughput as 1/k
    double throughput = 1 / k.doubleValue();
    System.out.println("Throughput of the graph = " + throughput);

    return throughput;
  }

}

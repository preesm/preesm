package org.ietr.preesm.throughput.parsers.test;

import java.util.logging.Level;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.deadlock.IBSDFConsistency;
import org.ietr.preesm.latency.LatencyEvaluationEngine;
import org.ietr.preesm.latency.LatencyEvaluationTask.LatencyMethod;
import org.ietr.preesm.schedule.ASAPScheduler_DAG;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;
import org.ietr.preesm.throughput.tools.parsers.TurbineParser;
import org.ietr.preesm.throughput.tools.transformers.IBSDFTransformer;
import org.ietr.preesm.throughput.tools.transformers.SrSDFTransformer;
import org.junit.Test;

/**
 * Unit test of GraphStrucutureHelper class
 * 
 * @author hderoui
 *
 */
public class TurbineParserTest {

  @Test
  public void testIBSDFGraphShouldBeImported() {

    // ------------------------- Get the IBSDF Graph ------------------
    // import IBSDF graph
    String dirPath = "C:/Users/hderoui/OneDrive/1 To/Thèse/Rédaction/GitLab_INSA/[dev]/JDD/";
    String ibsdf_file = "Graph2.tur"; // 3Lvl_5Act_5EA07211.IBSDF
    SDFGraph inputGraph = TurbineParser.importIBSDFGraph(dirPath + ibsdf_file, null);

    // compute RV
    IBSDFConsistency.computeRV(inputGraph);

    // compute the latency
    Stopwatch timer = new Stopwatch();
    double latency = 0;
    // LatencyEvaluationEngine evaluator = new LatencyEvaluationEngine();
    // timer.start();
    // // double latency = evaluator.getMinLatencySingleCore(ibsdf, null);
    // double latency = evaluator.getMinLatencyMultiCore(ibsdf, null, false);
    // timer.stop();
    //
    // System.out.println("Latency = " + latency + " computed in " + timer.toString());

    LatencyMethod inputMethod = LatencyMethod.fast;

    switch (inputMethod) {
      case flat_LP:
        // Based on flattening the hierarchy into a Flat srSDF graph
        timer.start();

        // convert the IBSDF graph to a flat srSDF graph then to a dag
        SDFGraph dag_lp = SrSDFTransformer.convertToDAG(IBSDFTransformer.convertToSrSDF(inputGraph, false));

        // compute the value of the longest path in the dag
        latency = GraphStructureHelper.getLongestPath(dag_lp, null, null);

        timer.stop();
        break;

      case flat_SE:
        // Based on flattening the hierarchy into a Flat srSDF graph
        timer.start();

        // convert the IBSDF graph to a flat srSDF graph then to a dag
        SDFGraph dag_simu = SrSDFTransformer.convertToDAG(IBSDFTransformer.convertToSrSDF(inputGraph, false));

        // Simulate an ASAP schedule
        ASAPScheduler_DAG schedule = new ASAPScheduler_DAG();
        latency = schedule.schedule(dag_simu);

        timer.stop();
        break;

      case fast:
        // Based on a hierarchical evaluation of the latency (evaluate-replace)
        LatencyEvaluationEngine evaluator = new LatencyEvaluationEngine();
        latency = evaluator.getMinLatencyMultiCore(inputGraph, null, false);
        timer = evaluator.timer;
        break;

      default:
        WorkflowLogger.getLogger().log(Level.WARNING, "The suported methods are: \"flat\" = classical method, \"fast\" = hierarchical method !");
        break;
    }

    System.out.println("Latency = " + latency + " computed in " + timer.toString());

    // check the results
    // Assert.assertEquals(36200, latency, 0);
  }

}

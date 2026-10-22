package org.preesm.algorithm.clustering.identification;

import java.util.Map;
import org.preesm.algorithm.clustering.heuristics.VerticalHeuristic;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * @author rcazoulat
 */
public class DownardWeightPropagation implements VerticalHeuristic {

  @Override
  public void assessFlatteningBefore(PiGraph parentGraph, PiGraph childGraph) {

    // It means that childGraph is top graph -> we don't do anything
    if (parentGraph == null) {
      return;
    }

    Map<AbstractVertex, Long> brv = PiBRV.compute(parentGraph, BRVMethod.LCM);
    final long childRep = brv.get(childGraph);

    // If childGraph is fired more than 1 time in parent graph, then we propagate the weights so that it is fired only
    // one time
    if (childRep > 1) {
      for (final DataInterface di : childGraph.getDataInterfaces()) {
        final DataPort dataPort = di.getDataPort();
        final DataPort graphPort = di.getGraphPort();
        final long dataExpr = dataPort.getExpression().evaluateAsLong();
        final long graphExpr = graphPort.getExpression().evaluateAsLong();
        if (dataExpr != graphExpr) {
          throw new PreesmRuntimeException(
              "Data Interface " + di.getName() + " doesn't have the same expression for data and graph port.");
        }
        final long expr = dataExpr;

        // Changing the weights of interface
        final double interfaceExpr = expr * (double) childRep;
        dataPort.setExpression(interfaceExpr);
        graphPort.setExpression(interfaceExpr);
      }

      // Verifying that the child graph is now fired 1 time
      brv = PiBRV.compute(parentGraph, BRVMethod.LCM);
      if (brv.get(childGraph) > 1) {
        throw new PreesmRuntimeException("Something has been wrong whild propagating the weights: child graph "
            + childGraph.getName() + " is still firing more than 1 time in parent graph " + parentGraph.getName());
      }
    }
  }

  @Override
  public void assessFlatteningAfter(PiGraph parentGraph, PiGraph childGraph) {
    // No need to do any actions here
  }

  @Override
  public String getPrefix() {
    return "flattened";
  }

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    // Nothing to do here

  }

  @Override
  public boolean assessGraph(PiGraph graph) {
    return false;
  }

}

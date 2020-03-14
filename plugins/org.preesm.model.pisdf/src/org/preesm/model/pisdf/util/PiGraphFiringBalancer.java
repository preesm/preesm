package org.preesm.model.pisdf.util;

import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.PiGraph;

/**
 * This class is used to balance repetition count of a given PiGraph with a specified factor that is power of two.
 *
 * @author dgageot
 *
 */
public class PiGraphFiringBalancer extends PiMMSwitch<Boolean> {

  /**
   * PiGraph to process.
   */
  private final PiGraph graph;

  /**
   * Balancing factor.
   */
  private final int balancingFactor;

  /**
   * Builds a PiGraphFiringBalancer based on the subgraph to process.
   * 
   * @param graph
   *          Input PiGraph to process. Must be contained in another PiGraph.
   * @param balancingFactor
   *          Balancing factor, must be power of 2.
   */
  public PiGraphFiringBalancer(final PiGraph graph, final int balancingFactor) {
    // If the given PiGraph is not contained in a graph, throw an exception.
    if (graph.getContainingPiGraph() == null) {
      throw new PreesmRuntimeException("PiGraphFiringBalancer: " + graph.getName() + " has no parent graph.");
    }
    this.graph = graph;
    // If balancing factor is not power of 2, throw an exception.
    double estimatedPower = Math.log(balancingFactor) / Math.log(2);
    if (Math.ceil(estimatedPower) != Math.floor(estimatedPower)) {
      throw new PreesmRuntimeException(
          "PiGraphFiringBalancer: balancing factor " + balancingFactor + " is not power of 2.");
    }
    this.balancingFactor = balancingFactor;
  }

  /**
   * Balance the repetition count of the hierarchy with a specified factor. As an example, if factor equals 2,
   * expressions on graph data ports would be multiplied by 2, leading to divide by 2 the number of firing of the whole
   * hierarchy, but multiply by two firings of internal actors.
   */
  public void balance() {
    doSwitch(this.graph);
  }

  @Override
  public Boolean casePiGraph(PiGraph graph) {
    // Process all input interfaces.
    for (DataInputInterface inputInterface : graph.getDataInputInterfaces()) {
      doSwitch(inputInterface);
    }
    // Process all output interfaces.
    for (DataOutputInterface outputInterface : graph.getDataOutputInterfaces()) {
      doSwitch(outputInterface);
    }
    return super.casePiGraph(graph);
  }

  @Override
  public Boolean caseDataInputPort(DataInputPort dataPort) {
    Long newExpression = dataPort.getExpression().evaluate() * this.balancingFactor;
    dataPort.setExpression(newExpression);
    return true;
  }

  @Override
  public Boolean caseDataOutputPort(DataOutputPort dataPort) {
    Long newExpression = dataPort.getExpression().evaluate() * this.balancingFactor;
    dataPort.setExpression(newExpression);
    return true;
  }

  @Override
  public Boolean caseDataInputInterface(DataInputInterface inputInterface) {
    doSwitch(inputInterface.getDataPort());
    doSwitch(inputInterface.getGraphPort());
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(DataOutputInterface outputInterface) {
    doSwitch(outputInterface.getDataPort());
    doSwitch(outputInterface.getGraphPort());
    return true;
  }

}

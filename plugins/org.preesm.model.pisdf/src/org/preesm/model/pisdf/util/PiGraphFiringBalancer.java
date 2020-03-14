package org.preesm.model.pisdf.util;

import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;

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
  private final long balancingFactor;

  /**
   * Builds a PiGraphFiringBalancer based on the subgraph to process.
   * 
   * @param graph
   *          Input PiGraph to process. Must be contained in another PiGraph.
   * @param balancingFactor
   *          Balancing factor, must be power of 2 and greater or equal to 1.
   */
  public PiGraphFiringBalancer(final PiGraph graph, final long balancingFactor) {
    // Check if given PiGraph is non-null.
    if (graph == null) {
      throw new PreesmRuntimeException("PiGraphFiringBalancer: no graph given in parameter.");
    }
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
    // Process input PiGraph.
    doSwitch(this.graph);
    // Check consistency of the graph.
    PiGraphConsistenceChecker.check(this.graph);
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
  public Boolean caseDataPort(DataPort dataPort) {
    // Update rates on the data port.
    Long newExpression = dataPort.getExpression().evaluate() * this.balancingFactor;
    dataPort.setExpression(newExpression);
    return true;
  }

  @Override
  public Boolean caseInterfaceActor(InterfaceActor interfaceActor) {
    // Explore inside data port and graph data port.
    doSwitch(interfaceActor.getDataPort());
    doSwitch(interfaceActor.getGraphPort());
    return true;
  }

}

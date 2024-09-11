/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020 - 2021) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Dylan Gageot [gageot.dylan@gmail.com] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.model.pisdf.util;

import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
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
    final double estimatedPower = Math.log(balancingFactor) / Math.log(2);
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
    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgcc.check(this.graph);
  }

  @Override
  public Boolean casePiGraph(PiGraph graph) {
    // Process all input interfaces.
    for (final DataInputInterface inputInterface : graph.getDataInputInterfaces()) {
      doSwitch(inputInterface);
    }
    // Process all output interfaces.
    for (final DataOutputInterface outputInterface : graph.getDataOutputInterfaces()) {
      doSwitch(outputInterface);
    }
    return super.casePiGraph(graph);
  }

  @Override
  public Boolean caseDataPort(DataPort dataPort) {
    // Update rates on the data port.
    final Long newExpression = dataPort.getExpression().evaluateAsLong() * this.balancingFactor;
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

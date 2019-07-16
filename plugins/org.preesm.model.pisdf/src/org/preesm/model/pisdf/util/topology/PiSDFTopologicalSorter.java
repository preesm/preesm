/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
package org.preesm.model.pisdf.util.topology;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;

/**
 * This class offer a method for sorting a list of actors in the topoligical order.
 *
 * @author anmorvan
 *
 */
public class PiSDFTopologicalSorter {

  private final List<AbstractActor>  visitedOrdered = new ArrayList<>();
  private final Deque<AbstractActor> visiting       = new LinkedList<>();

  /**
   * Sorts the list of actors in topological order. Fails if the graph containing the actors is not a DAG.
   */
  public static final List<AbstractActor> depthFirstSort(final List<AbstractActor> actors) {
    final PiSDFTopologicalSorter piSDFPredecessorSwitch = new PiSDFTopologicalSorter();

    for (AbstractActor a : actors) {
      if (!(piSDFPredecessorSwitch.visitedOrdered.contains(a))) {
        piSDFPredecessorSwitch.visit(a);
      }
    }
    return piSDFPredecessorSwitch.visitedOrdered;
  }

  /*
   * see https://en.wikipedia.org/wiki/Topological_sorting#Depth-first_search
   */
  private void visit(final AbstractActor actor) {
    if (visitedOrdered.contains(actor)) {
      // skip
    } else {
      if (visiting.contains(actor)) {
        // not a DAG
        throw new PreesmRuntimeException("Graph is not a DAG");
      } else {
        visiting.push(actor);
        actor.getDataOutputPorts().forEach(p -> visit(p.getFifo().getTargetPort().getContainingActor()));
        visiting.pop();
        visitedOrdered.add(0, actor);
      }
    }
  }
}

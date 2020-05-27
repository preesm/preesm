/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;

/**
 *
 * @author anmorvan
 *
 */
public class VertexPath {

  private VertexPath() {
    // no instantiation allowed
  }

  /**
   *
   */
  public static final <T extends AbstractVertex> T lookup(final PiGraph graph, final String actorPath) {
    if (actorPath == null) {
      return null;
    }
    String safePath = actorPath.trim().replaceAll("/+", "/").replaceAll("^/", "").replaceAll("/$", "");
    if (safePath.equals(graph.getName()) || safePath.isEmpty()) {
      @SuppressWarnings("unchecked")
      final T res = (T) graph;
      return res;
    } else {
      // graph name is removed from path /!\ /!\
      // we use replaceAll method instead of replace to benefit from regex
      safePath = safePath.replaceAll("^" + graph.getName() + "/", "");
    }
    final List<String> pathFragments = new ArrayList<>(Arrays.asList(safePath.split("/")));
    final String firstFragment = pathFragments.remove(0);
    final AbstractVertex current = graph.getActors().stream().filter(a -> firstFragment.equals(a.getName())).findFirst()
        .orElse(null);
    if (pathFragments.isEmpty()) {
      // we were at the end of the path, so what we found is what was asked
      @SuppressWarnings("unchecked")
      final T res = (T) current;
      return res;
    } else {
      // we are NOT at the end of the path, so what we found is the next child to visit
      // we must reintroduce the graph name in case of the subgraph having the same name
      // of the first fragment name
      final String remainingPathFragments = String.join("/", pathFragments);
      if (current instanceof PiGraph) {
        String recursionPath = current.getName() + "/" + remainingPathFragments;
        return VertexPath.lookup((PiGraph) current, recursionPath);
      } else if (current instanceof Actor) {
        final Actor actor = (Actor) current;
        if (actor.isHierarchical()) {
          PiGraph refinementGraph = actor.getSubGraph();
          String recursionPath = refinementGraph.getName() + "/" + remainingPathFragments;
          return VertexPath.lookup(refinementGraph, recursionPath);
        } else {
          return null;
        }
      } else {
        return null;
      }
    }
  }
}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.preesm.experiment.model.pimm.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 *
 * @author anmorvan
 *
 */
public class ActorPath {

  private ActorPath() {
    // no instantiation allowed
  }

  /**
   *
   */
  public static final AbstractActor lookup(final PiGraph graph, final String actorPath) {

    final String safePath = actorPath.replaceAll("/+", "/").replaceAll("^/*" + graph.getName(), "").replaceAll("^/", "")
        .replaceAll("/$", "");
    if (safePath.isEmpty()) {
      return graph;
    }
    final List<String> pathFragments = new ArrayList<>(Arrays.asList(safePath.split("/")));
    final String firstFragment = pathFragments.remove(0);
    final AbstractActor current = graph.getActors().stream().filter(a -> firstFragment.equals(a.getName())).findFirst()
        .orElse(null);
    if (pathFragments.isEmpty()) {
      return current;
    } else {
      final String remainingPathFragments = String.join("/", pathFragments);
      if (current instanceof PiGraph) {
        return ActorPath.lookup((PiGraph) current, remainingPathFragments);
      } else if (current instanceof Actor) {
        final Actor actor = (Actor) current;
        if (actor.isHierarchical()) {
          return ActorPath.lookup(actor.getSubGraph(), remainingPathFragments);
        } else {
          return null;
        }
      } else {
        return null;
      }
    }
  }
}

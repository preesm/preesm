/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
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
package org.preesm.algorithm.pisdf.periods;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking.TopoVisit;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;

/**
 * This class aims to select periodic actors on which execute the period checkers (nbff and nblf).
 *
 * @author ahonorat
 */
class HeuristicPeriodicActorSelection {

  private HeuristicPeriodicActorSelection() {
    // Forbids instantiation
  }

  static Map<Actor, Double> selectActors(final Map<Actor, Long> periodicActors, final HeuristicLoopBreakingDelays hlbd,
      final int rate, final Map<AbstractVertex, Long> wcets, final boolean reverse) {
    if (rate == 0 || periodicActors.isEmpty()) {
      return new LinkedHashMap<>();
    }

    Map<AbstractActor, TopoVisit> topoRanks = null;
    if (reverse) {
      topoRanks = TopologicalRanking.topologicalAsapRankingT(hlbd);
    } else {
      topoRanks = TopologicalRanking.topologicalAsapRanking(hlbd);
    }
    final Map<Actor, Double> topoRanksPeriodic = new LinkedHashMap<>();
    for (final Entry<Actor, Long> e : periodicActors.entrySet()) {
      final Actor actor = e.getKey();
      if (!topoRanks.containsKey(actor)) {
        continue;
      }
      final long rank = topoRanks.get(actor).getRank();
      final long period = e.getValue();
      final long wcetMin = wcets.get(actor);
      topoRanksPeriodic.put(actor, (period - wcetMin) / (double) rank);
    }
    final StringBuilder sb = new StringBuilder();
    topoRanksPeriodic.entrySet().forEach(a -> sb.append(a.getKey().getName() + "(" + a.getValue() + ") / "));
    PreesmLogger.getLogger().info(() -> "Periodic actor ranks: " + sb.toString());

    return HeuristicPeriodicActorSelection.selectFromRate(periodicActors, topoRanksPeriodic, rate);
  }

  private static Map<Actor, Double> selectFromRate(final Map<Actor, Long> periodicActors,
      final Map<Actor, Double> topoRanksPeriodic, final int rate) {
    final int nbPeriodicActors = periodicActors.size();
    final double nActorsToSelect = nbPeriodicActors * (rate / 100.0);
    final int nbActorsToSelect = Math.max((int) Math.ceil(nActorsToSelect), 1);

    return topoRanksPeriodic.entrySet().stream().sorted(Map.Entry.comparingByValue()).limit(nbActorsToSelect)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
package org.preesm.algorithm.synthesis.evaluation.latency;

import java.util.Map;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.synthesis.evaluation.ISynthesisEvaluator;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.algorithm.synthesis.timer.ActorExecutionTiming;
import org.preesm.algorithm.synthesis.timer.SimpleTimer;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 *
 * @author anmorvan
 *
 */
public class SimpleLatencyEvaluation implements ISynthesisEvaluator<LatencyCost> {

  @Override
  public LatencyCost evaluate(final PiGraph algo, final Design slamDesign, final Scenario scenario,
      final Mapping mapping, final ScheduleOrderManager scheduleOM) {

    final Map<AbstractActor,
        ActorExecutionTiming> computeTimings = new SimpleTimer(scenario, mapping).computeTimings(scheduleOM);

    final long latency = computeTimings.entrySet().stream().mapToLong(entry -> entry.getValue().getEndTime()).max()
        .orElse(0L);

    return new LatencyCost(latency, computeTimings);
  }

}

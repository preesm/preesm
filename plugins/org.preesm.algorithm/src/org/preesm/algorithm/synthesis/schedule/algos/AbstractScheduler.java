/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.ArrayList;
import java.util.List;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.PreesmSynthesisException;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.PiGraphSRDAGChecker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 *
 */
public abstract class AbstractScheduler implements IScheduler {

  @Override
  public SynthesisResult scheduleAndMap(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {
    verifyInputs(piGraph, slamDesign, scenario);
    final SynthesisResult res = exec(piGraph, slamDesign, scenario);
    if (res.alloc != null) {
      throw new PreesmSynthesisException("Simple scheduling API should not allocate memory");
    }
    verifyOutputs(piGraph, slamDesign, scenario, res.schedule, res.mapping);
    return res;
  }

  /**
   * Defines how the actors of the PiGraph are scheduled between them and mapped onto the slamDesign, respecting
   * constraints from the scenario.
   */
  protected abstract SynthesisResult exec(final PiGraph piGraph, final Design slamDesign, final Scenario scenario);

  /**
   * Verifies the consistency of the inputs.
   */
  private void verifyInputs(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {
    /*
     * Check graph
     */
    final PiGraph originalPiGraph = PreesmCopyTracker.getOriginalSource(piGraph);
    if (originalPiGraph != scenario.getAlgorithm()) {
      throw new PreesmSynthesisException("Input PiSDF graph is not derived from the scenario algorithm.");
    }
    // check that graph is an SRDAG
    if (!PiGraphSRDAGChecker.isPiGraphSRADG(piGraph)) {
      throw new PreesmSynthesisException(
          "Synthesis can be applied only on SRADG graphs (no hierarchy, single-rate, no cycles, no delays). Please "
              + "consider using the output of the pisdf-srdag workflow task as an output of  "
              + "the synthesis workflow task.");
    }

    /*
     * Check design
     */
    final Design originalDesign = PreesmCopyTracker.getOriginalSource(slamDesign);
    if (originalDesign != scenario.getDesign()) {
      throw new PreesmSynthesisException("Input Slam design is not derived from the scenario design.");
    }

  }

  /**
   * Verifies the consistency of the outputs.
   */
  private void verifyOutputs(final PiGraph piGraph, final Design slamDesign, final Scenario scenario,
      final Schedule schedule, final Mapping mapping) {

    // make sure all actors have been scheduled and schedule contains only actors from the input graph
    final List<AbstractActor> piGraphAllActors = new ArrayList<>(piGraph.getAllActors());

    final List<AbstractActor> actors = ScheduleUtil.getAllReferencedActors(schedule);
    final List<AbstractActor> scheduledActors = new ArrayList<>(actors);
    if (!piGraphAllActors.containsAll(scheduledActors)) {
      throw new PreesmSynthesisException("Schedule refers actors not present in the input PiSDF.");
    }
    if (!scheduledActors.containsAll(piGraphAllActors)) {
      throw new PreesmSynthesisException("Schedule is missing order for some actors of the input PiSDF.");
    }

    if (!mapping.getMappings().keySet().containsAll(scheduledActors)) {
      throw new PreesmSynthesisException("Mapping is missing actors of the input PiSDF.");
    }

    final List<ComponentInstance> slamCmpInstances = new ArrayList<>(slamDesign.getComponentInstances());
    final List<ComponentInstance> usedCmpInstances = new ArrayList<>(mapping.getAllInvolvedComponentInstances());
    if (!slamCmpInstances.containsAll(usedCmpInstances)) {
      throw new PreesmSynthesisException("Mapping is using unknown component instances.");
    }

    for (final AbstractActor actor : piGraphAllActors) {
      verifyActor(scenario, mapping, actor);
    }
  }

  private void verifyActor(final Scenario scenario, final Mapping mapping, final AbstractActor actor) {
    final List<ComponentInstance> actorMapping = new ArrayList<>(mapping.getMapping(actor));

    final List<ComponentInstance> possibleMappings = new ArrayList<>(scenario.getPossibleMappings(actor));
    if (!possibleMappings.containsAll(actorMapping)) {
      throw new PreesmSynthesisException("Actor '" + actor + "' is mapped on '" + actorMapping
          + "' which is not in the authorized components list '" + possibleMappings + "'.");
    }

    if (actor instanceof final InitActor initActor) {
      final AbstractActor endReference = initActor.getEndReference();
      final List<ComponentInstance> targetMappings = new ArrayList<>(mapping.getMapping(endReference));
      if (!targetMappings.equals(actorMapping)) {
        throw new PreesmSynthesisException("Init and End actors are not mapped onto the same PE.");
      }
    } else if (actor instanceof final EndActor endActor) {
      final AbstractActor initReference = endActor.getInitReference();
      final List<ComponentInstance> targetMappings = new ArrayList<>(mapping.getMapping(initReference));
      if (!targetMappings.equals(actorMapping)) {
        throw new PreesmSynthesisException("Init and End actors are not mapped onto the same PE.");
      }
    }
  }

}

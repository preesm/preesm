/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2025) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2025)
 * Ophelie-Renaud [ophelie.renaud@insa-rennes.fr] (2025)
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

package org.preesm.algorithm.clustering.scape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.check.SlamDesignPEtypeChecker;

/**
 * This class divides the data parallelism according to Euclidean division based on the architecture's equivalent core
 * count. The number of equivalent cores comes down to calculating the linear function that relates the frequency of the
 * cores that make up the architecture.
 *
 * @author orenaud
 *
 */
public class EuclideTransfo {
  /**
   * Input graph.
   */
  private final PiGraph  graph;
  /**
   * Workflow scenario.
   */
  private final Scenario scenario;

  private Map<Long, List<PiGraph>> hierarchicalLevelOrdered = new HashMap<>();

  private Long levelBound = 0L;

  /**
   * Builds a EuclideTransfo object.
   *
   * @param scenario
   *          Workflow scenario.
   *
   */
  public EuclideTransfo(Scenario scenario) {
    this.graph = scenario.getAlgorithm();
    this.scenario = scenario;

  }

  /**
   * @return Transformed PiGraph
   */
  public PiGraph execute() {
    if (SlamDesignPEtypeChecker.isOnlyCPU(scenario.getDesign())) {
      // check if there is no global or local delay, and there is only single actor loop
      if (graph.getAllDelays().stream().anyMatch(x -> x.getLevel() != PersistenceLevel.NONE) || !graph.getDelays()
          .stream().allMatch(x -> x.getContainingFifo().getSource().equals(x.getContainingFifo().getTarget()))) {
        return graph;
      }
      final Long coreEquivalent = computeSingleNodeCoreEquivalent(scenario);
      // construct hierarchical structure

      hierarchicalLevelOrdered = HierarchicalRoute.fillHierarchicalStructure(graph);
      levelBound = (long) (hierarchicalLevelOrdered.size() - 1);

      // compute Euclide-able level ID
      divideIDs(coreEquivalent);

      // check consistency
      final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
      final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ALL,
          CheckerErrorLevel.NONE);
      pgcc.check(graph);
      PiBRV.printRV(brv);
    }
    return graph;
  }

  /**
   * the method compute the number of equivalent CPU cores that comes down to calculating the linear function that
   * relates the frequency of the cores that make up the architecture.
   *
   * @return The number of equivalent cores (not sure for Long value, may be an int)
   */

  public static Long computeSingleNodeCoreEquivalent(Scenario inputScenario) {
    final PiGraph inputGraph = inputScenario.getAlgorithm();
    final Design inputArchi = inputScenario.getDesign();
    // filter CPU component
    final List<ComponentInstance> cpuInstances = inputArchi.getOperatorComponentInstances().stream()
        .filter(opId -> opId.getComponent() instanceof CPU).toList();
    Long coreEq = 0L;
    int actorNumber = 0;
    for (final AbstractActor actor : inputGraph.getExecutableActors()) {
      // sink and source actor replace interface for SimSDP
      if (actor instanceof Actor && !actor.getName().contains("src_") && !actor.getName().contains("snk_")
          && !(actor instanceof DelayActor)) {

        Long sumTiming = 0L;
        Long slow = Long.valueOf(inputScenario.getTimings().getExecutionTimeOrDefault(actor,
            inputArchi.getOperatorComponentInstances().stream().map(ComponentInstance::getComponent)
                .filter(CPU.class::isInstance).findFirst().orElseThrow()));

        for (final ComponentInstance cpu : cpuInstances) {
          sumTiming += Long.valueOf(inputScenario.getTimings().getExecutionTimeOrDefault(actor, cpu.getComponent()));
          final Long timeSeek = Long
              .valueOf(inputScenario.getTimings().getExecutionTimeOrDefault(actor, cpu.getComponent()));

          slow = timeSeek < slow ? timeSeek : slow;

        }
        coreEq += (sumTiming / slow);
        actorNumber++;
      }
    }
    coreEq = actorNumber > 0 ? coreEq / actorNumber
        : inputArchi.getOperatorComponentInstances().stream().filter(opId -> opId.getComponent() instanceof CPU)
            .count();

    return coreEq;
  }

  /**
   * Identify potential candidates, i.e. actors with a degree of parallelism not divisible by the number of equivalent
   * cores in the target.
   *
   * @param coreEquivalent
   *          number of equivalent cores
   */
  private void divideIDs(Long coreEquivalent) {
    for (Long i = levelBound; i >= 0L; i--) {
      for (final PiGraph g : hierarchicalLevelOrdered.get(i)) {
        final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
        for (final AbstractActor a : g.getExecutableActors()) {
          // maybe not for Special Actor
          if (rv.get(a) % coreEquivalent > 0 && rv.get(a) > coreEquivalent) {
            euclide(a, rv, coreEquivalent);
          }
        }
      }
    }
  }

  /**
   * The process consists in dividing an actor instance into 2. One will repeat QxD, the other R. To do this, we
   * duplicate the instance and redistribute the data.
   *
   * @param a
   *          The identified actor
   * @param rv
   *          The genuine repetition vector
   * @param coreEquivalent
   *          number of equivalent cores
   */

  private void euclide(AbstractActor a, Map<AbstractVertex, Long> rv, Long coreEquivalent) {

    final PiGraph upperGraph = a.getContainingPiGraph();

    final Long rv2 = rv.get(a) % coreEquivalent; // rest
    final Long rv1 = rv.get(a) - rv2;// quotient * divisor
    // copy instance
    final AbstractActor copyActor = PiMMUserFactory.instance.copy(a);
    copyActor.setName(a.getName() + "2");
    upperGraph.addActor(copyActor);
    int index = 0;
    for (final DataInputPort in : a.getDataInputPorts()) {
      if (!in.getFifo().isDelayPresent()) {
        final ForkActor frk = PiMMUserFactory.instance.createForkActor();
        frk.setName("Fork_eu_" + a.getName() + index);
        upperGraph.addActor(frk);

        // connect din to frk
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in");
        final Long dt = in.getExpression().evaluateAsLong() * rv.get(a);
        din.setExpression(dt);
        frk.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setType(in.getFifo().getType());
        fin.setSourcePort(in.getFifo().getSourcePort());
        fin.setTargetPort(din);
        upperGraph.addFifo(fin);

        // connect fork to oEmpty_0
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        dout.setName("out_0");
        final Long rt1 = in.getExpression().evaluateAsLong() * rv1;
        dout.setExpression(rt1);
        frk.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(in.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(in);
        upperGraph.addFifo(fout);

        // connect fork to duplicated actors
        final DataOutputPort doutn = PiMMUserFactory.instance.createDataOutputPort();
        doutn.setName("out_" + 1);
        final Long rt2 = in.getExpression().evaluateAsLong() * rv2;
        doutn.setExpression(rt2);
        frk.getDataOutputPorts().add(doutn);
        final Fifo foutn = PiMMUserFactory.instance.createFifo();
        foutn.setType(fin.getType());
        foutn.setSourcePort(doutn);
        upperGraph.addFifo(foutn);
        copyActor.getDataInputPorts().stream().filter(x -> x.getName().equals(in.getName()))
            .forEach(x -> x.setIncomingFifo(foutn));

        index++;
      } else if (in.getFifo().getDelay().getLevel().equals(PersistenceLevel.NONE)) {
        // copy delay
        final Fifo fdin = PiMMUserFactory.instance.createFifo();
        final String type = in.getFifo().getType();
        fdin.setType(type);
        upperGraph.addFifo(fdin);
        final Delay copyDelay = PiMMUserFactory.instance.copy(in.getFifo().getDelay());
        copyDelay.setName(in.getFifo().getDelay().getName() + "2");
        final DelayActor copyDelayActor = PiMMUserFactory.instance.copy(in.getFifo().getDelay().getDelayActor());
        copyDelayActor.setName(in.getFifo().getDelay().getDelayActor().getName() + "2");
        copyDelay.setDelayActor(copyDelayActor);

        upperGraph.addDelay(copyDelay);

        fdin.setDelay(copyDelay);
        // the getter of the initial delay is moved to get the delay of the copied actor
        final DataInputPort getterPort = in.getFifo().getDelay().getDelayActor().getDataOutputPort().getFifo()
            .getTargetPort();
        // the setter of the copied delay is the output of the initial delay
        final Fifo fDelayActorIn = PiMMUserFactory.instance.createFifo();
        fDelayActorIn.setType(type);
        upperGraph.addFifo(fDelayActorIn);
        fDelayActorIn.setSourcePort(in.getFifo().getDelay().getDelayActor().getDataOutputPort());
        fDelayActorIn.setTargetPort(copyDelayActor.getDataInputPort());
        final Fifo fDelayActorOut = PiMMUserFactory.instance.createFifo();
        fDelayActorOut.setType(type);
        upperGraph.addFifo(fDelayActorOut);
        fDelayActorOut.setTargetPort(getterPort);
        fDelayActorOut.setSourcePort(copyDelayActor.getDataOutputPort());

        // connect delay to actor
        copyActor.getDataInputPorts().stream().filter(x -> x.getName().equals(in.getName()))
            .forEach(x -> x.setIncomingFifo(fdin));
        copyActor.getDataOutputPorts().stream().filter(x -> x.getName().equals(in.getFifo().getSourcePort().getName()))
            .forEach(x -> x.setOutgoingFifo(fdin));

      }
    }
    index = 0;
    for (final DataOutputPort out : a.getDataOutputPorts()) {
      if (!out.getFifo().isDelayPresent()) {
        final JoinActor jn = PiMMUserFactory.instance.createJoinActor();
        jn.setName("Join_eu_" + a.getName() + index);
        upperGraph.addActor(jn);

        // connect Join to dout
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        dout.setName("out");
        final Long dt = out.getExpression().evaluateAsLong() * rv.get(a);
        dout.setExpression(dt);
        jn.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(out.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(out.getFifo().getTargetPort());
        upperGraph.addFifo(fout);

        // connect oEmpty_0 to Join
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in_0");
        final Long rt1 = out.getExpression().evaluateAsLong() * rv1;
        din.setExpression(rt1);
        jn.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setSourcePort(out);
        fin.setTargetPort(din);
        upperGraph.addFifo(fin);
        out.getFifo().setType(fout.getType());

        // connect duplicated actors to Join
        final DataInputPort dinn = PiMMUserFactory.instance.createDataInputPort();
        dinn.setName("in_" + 1);
        final Long rt2 = out.getExpression().evaluateAsLong() * rv2;
        dinn.setExpression(rt2);
        jn.getDataInputPorts().add(dinn);
        final Fifo finn = PiMMUserFactory.instance.createFifo();
        finn.setType(fout.getType());
        finn.setTargetPort(dinn);
        upperGraph.addFifo(finn);
        copyActor.getDataOutputPorts().stream().filter(x -> x.getName().equals(out.getName()))
            .forEach(x -> x.setOutgoingFifo(finn));

        index++;
      }
    }

    for (final ConfigInputPort cfg : a.getConfigInputPorts()) {
      copyActor.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName()))
          .forEach(x -> PiMMUserFactory.instance.createDependency(cfg.getIncomingDependency().getSetter(), x));
      copyActor.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName()))
          .forEach(x -> cfg.getIncomingDependency().getContainingPiGraph().addDependency(x.getIncomingDependency()));
    }

    // remove empty introduced fifo
    a.getContainingPiGraph().getFifos().stream().filter(x -> x.getSourcePort() == null)
        .forEach(x -> a.getContainingPiGraph().removeFifo(x));
    a.getContainingPiGraph().getFifos().stream().filter(x -> x.getTargetPort() == null)
        .forEach(x -> a.getContainingPiGraph().removeFifo(x));
  }

}

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
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

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

  /**
   * Number of hierarchical level.
   */
  private final int levelNumber;
  /**
   * SCAPE mode : 1 (...); 2 (...); 3 (...).
   */
  private final int mode;

  private Map<Long, List<PiGraph>> hierarchicalLevelOrdered = new HashMap<>();

  private Long levelBound = 0L;

  /**
   * Builds a EuclideTransfo object.
   *
   * @param scenario
   *          Workflow scenario.
   *
   */
  public EuclideTransfo(Scenario scenario, int mode, int levelNumber) {
    this.graph = scenario.getAlgorithm();
    this.scenario = scenario;
    this.mode = mode;
    this.levelNumber = levelNumber;

  }

  /**
   * @return Transformed PiGraph
   */
  public PiGraph execute() {
    // check if there is no global or local delay, and there is only single actor loop
    if (graph.getDelays().stream().anyMatch(x -> x.getLevel() != PersistenceLevel.NONE) && !graph.getDelays().stream()
        .allMatch(x -> x.getContainingFifo().getSource().equals(x.getContainingFifo().getTarget()))) {
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
    return graph;
  }

  /**
   * the method compute the number of equivalent cores that comes down to calculating the linear function that relates
   * the frequency of the cores that make up the architecture.
   *
   * @return The number of equivalent cores (not sure for Long value, may be an int)
   */

  public static Long computeSingleNodeCoreEquivalent(Scenario inputScenario) {
    final PiGraph inputGraph = inputScenario.getAlgorithm();
    final Design inputArchi = inputScenario.getDesign();
    Long coreEq = 0L;
    int actorNumber = 0;
    for (final AbstractActor actor : inputGraph.getOnlyActors()) {
      // sink and source actor replace interface for SimSDP
      if (actor instanceof Actor && !actor.getName().contains("src_") && !actor.getName().contains("snk_")
          && !(actor instanceof DelayActor)) {

        Long sumTiming = 0L;
        Long slow = Long.valueOf(inputScenario.getTimings().getExecutionTimeOrDefault(actor,
            inputArchi.getOperatorComponentInstances().get(0).getComponent()));
        for (final ComponentInstance opId : inputArchi.getOperatorComponentInstances()) {
          sumTiming += Long.valueOf(inputScenario.getTimings().getExecutionTimeOrDefault(actor, opId.getComponent()));
          final Long timeSeek = Long
              .valueOf(inputScenario.getTimings().getExecutionTimeOrDefault(actor, opId.getComponent()));
          if (timeSeek < slow) {
            slow = timeSeek;
          }
        }
        coreEq += (sumTiming / slow);
        actorNumber++;
      }
    }
    if (actorNumber > 0) {
      coreEq /= actorNumber;
    } else {
      coreEq = (long) inputArchi.getOperatorComponentInstances().size();
    }
    return coreEq;
  }

  /**
   * Identify potential candidates, i.e. actors with a degree of parallelism not divisible by the number of equivalent
   * cores in the target.
   *
   * @param coreEquivalent
   *
   */
  private void divideIDs(Long coreEquivalent) {
    for (Long i = levelBound; i >= 0L; i--) {
      for (final PiGraph g : hierarchicalLevelOrdered.get(i)) {
        final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
        for (final AbstractActor a : g.getOnlyActors()) {
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
    //
    final Long rv2 = rv.get(a) % coreEquivalent; // rest
    final Long rv1 = rv.get(a) - rv2;// quotient * divisor
    // copy instance
    final AbstractActor copyActor = PiMMUserFactory.instance.copy(a);
    copyActor.setName(a.getName() + "2");
    copyActor.setContainingGraph(a.getContainingGraph());
    int index = 0;
    for (final DataInputPort in : a.getDataInputPorts()) {
      if (!in.getFifo().isHasADelay()) {
        final ForkActor frk = PiMMUserFactory.instance.createForkActor();
        frk.setName("Fork_eu_" + a.getName() + index);
        frk.setContainingGraph(a.getContainingGraph());

        // connect din to frk
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in");
        //
        final Long dt = in.getExpression().evaluate() * rv.get(a);
        final Long rt1 = in.getExpression().evaluate() * rv1;
        final Long rt2 = in.getExpression().evaluate() * rv2;
        din.setExpression(dt);

        frk.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setType(in.getFifo().getType());
        fin.setSourcePort(in.getFifo().getSourcePort());
        fin.setTargetPort(din);

        fin.setContainingGraph(a.getContainingGraph());

        // connect fork to oEmpty_0
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        dout.setName("out_0");
        dout.setExpression(rt1);
        frk.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(in.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(in);
        fout.setContainingGraph(a.getContainingGraph());

        // connect fork to duplicated actors
        final DataOutputPort doutn = PiMMUserFactory.instance.createDataOutputPort();
        doutn.setName("out_" + 1);
        doutn.setExpression(rt2);
        frk.getDataOutputPorts().add(doutn);
        final Fifo foutn = PiMMUserFactory.instance.createFifo();
        foutn.setType(fin.getType());
        foutn.setSourcePort(doutn);
        foutn.setContainingGraph(a.getContainingGraph());
        copyActor.getDataInputPorts().stream().filter(x -> x.getName().equals(in.getName()))
            .forEach(x -> x.setIncomingFifo(foutn));

        index++;
      } else {
        // copy delay
        final Fifo fdin = PiMMUserFactory.instance.createFifo();
        final String type = in.getFifo().getType();
        fdin.setType(type);
        fdin.setContainingGraph(a.getContainingGraph());
        final Delay copyDelay = PiMMUserFactory.instance.copy(in.getFifo().getDelay());
        copyDelay.setName(in.getFifo().getDelay().getName() + "2");
        copyDelay.setContainingGraph(a.getContainingGraph());
        final DelayActor copyDelayActor = PiMMUserFactory.instance.copy(in.getFifo().getDelay().getActor());
        copyDelayActor.setName(in.getFifo().getDelay().getActor().getName() + "2");
        copyDelayActor.setContainingGraph(a.getContainingGraph());
        copyDelay.setActor(copyDelayActor);
        fdin.assignDelay(copyDelay);
        // the getter of the initial delay is moved to get the delay of the copied actor
        final DataInputPort getterPort = in.getFifo().getDelay().getActor().getDataOutputPort().getFifo()
            .getTargetPort();
        // the setter of the copied delay is the output of the initial delay
        final Fifo fDelayActorIn = PiMMUserFactory.instance.createFifo();
        fDelayActorIn.setType(type);
        fDelayActorIn.setContainingGraph(a.getContainingGraph());
        fDelayActorIn.setSourcePort(in.getFifo().getDelay().getActor().getDataOutputPort());
        fDelayActorIn.setTargetPort(copyDelayActor.getDataInputPort());
        final Fifo fDelayActorOut = PiMMUserFactory.instance.createFifo();
        fDelayActorOut.setType(type);
        fDelayActorOut.setContainingGraph(a.getContainingGraph());
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
      if (!out.getFifo().isHasADelay()) {
        final JoinActor jn = PiMMUserFactory.instance.createJoinActor();
        jn.setName("Join_eu_" + a.getName() + index);
        jn.setContainingGraph(a.getContainingGraph());

        // connect Join to dout
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();

        dout.setName("out");
        final Long dt = out.getExpression().evaluate() * rv.get(a);
        final Long rt1 = out.getExpression().evaluate() * rv1;
        final Long rt2 = out.getExpression().evaluate() * rv2;
        dout.setExpression(dt);
        jn.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(out.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(out.getFifo().getTargetPort());
        fout.setContainingGraph(a.getContainingGraph());

        // connect oEmpty_0 to Join
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in_0");
        din.setExpression(rt1);
        jn.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setSourcePort(out);
        fin.setTargetPort(din);
        fin.setContainingGraph(a.getContainingGraph());
        out.getFifo().setType(fout.getType());

        // connect duplicated actors to Join
        final DataInputPort dinn = PiMMUserFactory.instance.createDataInputPort();
        dinn.setName("in_" + 1);
        dinn.setExpression(rt2);
        jn.getDataInputPorts().add(dinn);
        final Fifo finn = PiMMUserFactory.instance.createFifo();
        finn.setType(fout.getType());
        finn.setTargetPort(dinn);
        finn.setContainingGraph(a.getContainingGraph());
        copyActor.getDataOutputPorts().stream().filter(x -> x.getName().equals(out.getName()))
            .forEach(x -> x.setOutgoingFifo(finn));

        index++;
      }
    }
    for (final ConfigInputPort cfg : a.getConfigInputPorts()) {
      copyActor.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName()))
          .forEach(x -> PiMMUserFactory.instance.createDependency(cfg.getIncomingDependency().getSetter(), x));
      copyActor.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName()))
          .forEach(x -> x.getIncomingDependency().setContainingGraph(cfg.getIncomingDependency().getContainingGraph()));

    }

    // remove empty introduced fifo
    ((PiGraph) a.getContainingGraph()).getFifos().stream().filter(x -> x.getSourcePort() == null)
        .forEach(x -> ((PiGraph) a.getContainingGraph()).removeFifo(x));
    ((PiGraph) a.getContainingGraph()).getFifos().stream().filter(x -> x.getTargetPort() == null)
        .forEach(x -> ((PiGraph) a.getContainingGraph()).removeFifo(x));

  }

}

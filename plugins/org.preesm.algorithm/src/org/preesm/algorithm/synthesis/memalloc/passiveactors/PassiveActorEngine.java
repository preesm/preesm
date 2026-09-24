package org.preesm.algorithm.synthesis.memalloc.passiveactors;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveInputPort;
import org.preesm.model.pisdf.PassiveOutputPort;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

/**
 * @author rcazoulat
 */
public class PassiveActorEngine {

  PassiveScriptRunner scriptRunner;

  PassiveActorVerifier verifier;

  List<PassiveActor> passiveActors;

  PiGraph graph;

  Scenario scenario;

  Map<AbstractVertex, Long> brv;

  URL defaultWriteUrl;
  URL defaultReadUrl;

  public PassiveActorEngine(PiGraph graph, Scenario scenario, long alignment) {
    this.brv = PiBRV.compute(graph, BRVMethod.LCM);

    passiveActors = new ArrayList<>();
    scriptRunner = new PassiveScriptRunner(alignment, brv);

    this.graph = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
    this.scenario = scenario;

    this.defaultWriteUrl = createSpecialUrl(PiMMUserFactory.WDEFAULT);
    this.defaultReadUrl = createSpecialUrl(PiMMUserFactory.RDEFAULT);

  }

  // =====================================================================================
  //
  // Public methods
  //
  // =====================================================================================

  /**
   *
   */
  public void processPassiveActors() {
    findPassiveActors(this.graph);

    try {
      new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS, CheckerErrorLevel.NONE).check(this.graph);
    } catch (final Exception e) {
      throw new PreesmRuntimeException("Problems have occured will replacing Actors and SpecialActors in graph "
          + graph.getName() + "error is : " + e.getLocalizedMessage());
    }

    // New brv compute, otherwise passive actors won't appear.
    this.brv = PiBRV.compute(this.graph, BRVMethod.LCM);
    scriptRunner.updateBrv(brv);

    runPassiveScripts();

    verifyPassiveActors();
  }

  /**
   *
   */
  public void composePassiveActors() {
    new PassiveActorComposer(scriptRunner).doFusion(graph);
  }

  private static URL createSpecialUrl(final String filePath) {
    final URL url = PreesmResourcesHelper.getInstance().resolve(filePath, PassiveScriptRunner.class);
    if (url == null) {
      throw new PreesmRuntimeException("can't resolve following url : " + filePath);
    }
    try {
      return url.toURI().normalize().toURL();
    } catch (URISyntaxException | MalformedURLException e) {
      throw new PreesmRuntimeException("can't normalize url : " + filePath, e);
    }
  }

  public PiGraph getPassiveIR() {
    return this.graph;
  }

  // =====================================================================================
  //
  // Private methods
  //
  // =====================================================================================

  /**
   *
   * @param graph
   *          the input graph
   */
  private void findPassiveActors(PiGraph graph) {

    // Recursively explore children graphs
    for (final PiGraph child : graph.getChildrenGraphs().stream().filter(g -> !g.isCluster()).toList()) {
      findPassiveActors(child);
    }

    // Fin actors that could be passive actors.
    // There is two conditions :
    // (1) it is a special actor
    // (2) it is an Actor that has at least one passive script
    for (final AbstractActor abstractActor : graph.getActors()) {
      final boolean isActorWithPassiveScript = abstractActor instanceof final Actor actor
          && (actor.getWritePassiveScriptPath() != null || actor.getReadPassiveScriptPath() != null);

      final boolean isSpecialActor = abstractActor instanceof SpecialActor;

      if (isActorWithPassiveScript || isSpecialActor) {

        String writeScript = "";
        String readScript = "";
        if (isActorWithPassiveScript) {
          final Actor actor = (Actor) abstractActor;
          writeScript = actor.getWritePassiveScriptPath() != null ? actor.getWritePassiveScriptPath()
              : PiMMUserFactory.WDEFAULT;
          readScript = actor.getReadPassiveScriptPath() != null ? actor.getReadPassiveScriptPath()
              : PiMMUserFactory.RDEFAULT;

        } else if (abstractActor instanceof BroadcastActor) {
          writeScript = defaultWriteUrl.toString();
          readScript = createSpecialUrl(PiMMUserFactory.RBROADCAST).toString();

        } else if (abstractActor instanceof JoinActor) {
          writeScript = createSpecialUrl(PiMMUserFactory.WJOIN).toString();
          readScript = defaultReadUrl.toString();

        } else if (abstractActor instanceof ForkActor) {
          writeScript = defaultWriteUrl.toString();
          readScript = createSpecialUrl(PiMMUserFactory.RFORK).toString();

        } else if (abstractActor instanceof RoundBufferActor) {
          writeScript = createSpecialUrl(PiMMUserFactory.WROUNDBUFFER).toString();
          readScript = defaultReadUrl.toString();
        }

        PreesmLogger.getLogger()
            .info("[PASSIVE] actor " + abstractActor.getName() + " is transformed in passive actor");

        PreesmLogger.getLogger().info("[PASSIVE] actor " + abstractActor.getName() + " write script = " + writeScript
            + ", read script = " + readScript);

        final PassiveActor pa = PiMMUserFactory.instance.createPassiveActor();
        pa.setName(abstractActor.getName());
        passiveActors.add(pa);

        // Replace actor by its passive form in graph
        graph.addActor(pa);

        for (final DataInputPort dip : abstractActor.getDataInputPorts()) {
          final PassiveInputPort pip = PiMMUserFactory.instance.createPassiveInputPort();
          pip.setExpression(dip.getExpression());
          pip.setAnnotation(dip.getAnnotation());
          pip.setName(dip.getName());
          pip.setWritePassiveScriptPath(writeScript);
          pip.setOffset(0);
          pip.setSubBufferSize(0);
          pa.getPassiveInputPorts().add(pip);
          dip.getFifo().setTargetPort(pip);
        }

        for (final DataOutputPort dop : abstractActor.getDataOutputPorts()) {
          final PassiveOutputPort pop = PiMMUserFactory.instance.createPassiveOutputPort();
          pop.setExpression(dop.getExpression());
          pop.setAnnotation(dop.getAnnotation());
          pop.setName(dop.getName());
          pop.setReadPassiveScriptPath(readScript);
          pop.setOffset(0);
          pop.setSubBufferSize(0);
          pa.getPassiveOutputPorts().add(pop);
          dop.getFifo().setSourcePort(pop);
        }

        for (final ConfigInputPort cip : abstractActor.getConfigInputPorts()) {
          final ConfigInputPort newCip = PiMMUserFactory.instance.createConfigInputPort();
          pa.getConfigInputPorts().add(newCip);
          newCip.setName(cip.getName());
          cip.getIncomingDependency().setGetter(newCip);
        }

        for (final ConfigOutputPort cop : abstractActor.getConfigOutputPorts()) {
          final ConfigOutputPort newCop = PiMMUserFactory.instance.createConfigOutputPort();
          newCop.setName(cop.getName());
          newCop.setAnnotation(cop.getAnnotation());
          newCop.setExpression(cop.getExpression());
          cop.getOutgoingFifo().setSourcePort(newCop);
          pa.getConfigOutputPorts().add(newCop);
        }

        for (final ComponentInstance ci : scenario.getPossibleMappings(abstractActor)) {
          scenario.getConstraints().addConstraint(ci, pa);
        }

        graph.removeActor(abstractActor);

      }
    }
  }

  /**
   *
   */
  private void runPassiveScripts() {
    scriptRunner.run(passiveActors);

    PreesmLogger.getLogger().info(scriptRunner.toString());

    // Computing the buffer size of all passive actors with the script results
    for (final PassiveActor pa : this.passiveActors) {
      long size = 0;

      // Port level
      for (final PassivePort pp : pa.getAllPassivePorts()) {

        // Port instance level (if the actor linked to pp has a rep value of 3, then there is max 3 instances)
        for (final Pair<Long, Long> beginEndInstancePtr : scriptRunner.getBeginEndAllInstances(pp)) {
          final long instanceEnd = beginEndInstancePtr.getValue();
          if (instanceEnd > size) {
            size = instanceEnd;
          }
        }
      }

      if (size == 0) {
        throw new PreesmRuntimeException("buffer size of actor " + pa.getName() + " is equal to 0");
      }

      // Before composition of passive actor, every passive ports have subBufferSize = bufferSize, and offset = 0.
      // We are still (steal/steel ?) in the "standard passive actor" era.
      for (final PassivePort pp : pa.getAllPassivePorts()) {
        pp.setSubBufferSize(size);
        pp.setOffset(0);

        final long end = (scriptRunner.getBeginEnd(pp).getKey() + pp.getExpression().evaluateAsLong()) % size;

        scriptRunner.setEnd(pp, end);
      }
    }
  }

  private void verifyPassiveActors() {

    for (final PassiveActor pa : this.passiveActors) {

      PassiveActorVerifier.verifyPassiveActorConditions(pa, scriptRunner);

      final Map<PassivePort, Boolean> mask = new HashMap<>();
      for (final PassivePort pp : pa.getAllPassivePorts()) {
        mask.put(pp, PassiveActorVerifier.verifyPassivePortConditions(pp, brv));
      }

      pa.setRealBufferSize(PassiveActorVerifier.getRealBufferSize(pa, scriptRunner, mask));

      for (final Entry<PassivePort, Boolean> entry : mask.entrySet()) {
        if (Boolean.TRUE.equals(entry.getValue())) {
          scriptRunner.populatePortWithResults(entry.getKey());
        }
      }
    }
  }
}

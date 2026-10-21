package org.preesm.algorithm.synthesis.memalloc.passiveactors;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveInputPort;
import org.preesm.model.pisdf.PassiveOutputPort;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SimplePassiveActor;
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

  private static final String PASSIVE_SCRIPT_FOLDER = "passiveScripts";

  private static final String JOIN_W_SCRIPT        = "join.bsh";
  private static final String ROUNDBUFFER_W_SCRIPT = "roundbuffer.bsh";
  private static final String FORK_R_SCRIPT        = "fork.bsh";
  private static final String BROADCAST_R_SCRIPT   = "broadcast.bsh";

  // Paths to the special scripts files
  public static final String WJOIN        = PASSIVE_SCRIPT_FOLDER + IPath.SEPARATOR + JOIN_W_SCRIPT;
  public static final String RFORK        = PASSIVE_SCRIPT_FOLDER + IPath.SEPARATOR + FORK_R_SCRIPT;
  public static final String WROUNDBUFFER = PASSIVE_SCRIPT_FOLDER + IPath.SEPARATOR + ROUNDBUFFER_W_SCRIPT;
  public static final String RBROADCAST   = PASSIVE_SCRIPT_FOLDER + IPath.SEPARATOR + BROADCAST_R_SCRIPT;

  public static final String WDEFAULT = WJOIN;
  public static final String RDEFAULT = RFORK;

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

    this.graph = graph; // no graph copy (would only complicate further operations)
    this.scenario = scenario;

    this.defaultWriteUrl = createSpecialUrl(WDEFAULT);
    this.defaultReadUrl = createSpecialUrl(RDEFAULT);

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

    /** First, transforming special actors in passive actors */
    findPassiveActors(this.graph);

    try {
      new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS, CheckerErrorLevel.NONE).check(this.graph);
    } catch (final Exception e) {
      throw new PreesmRuntimeException("Problems have occured will replacing Actors and SpecialActors in graph "
          + graph.getName() + "error is: " + e.getLocalizedMessage());
    }

    /** Second, running passive scripts to evaluate passive actors' buffer sizes */

    // New brv compute, otherwise passive actors won't appear.
    this.brv = PiBRV.compute(this.graph, BRVMethod.LCM);
    scriptRunner.updateBrv(brv);

    // It will run all scripts, and fill in all infos in the passive ports
    // (buffer size, begin*, end* ...)
    runPassiveScripts(this.passiveActors);

    // It will verify that all passive actors are correctly built
    verifyPassiveAllActors();

    // It will compute the real buffer size,that will be useful in the codegen step
    computeRealBufferSizes();
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

  /**
   *
   */
  public void transformFifosIntoPassiveActors() {
    this.transformFifosIntoPassiveActors(this.graph);
  }

  /**
   *
   */
  public void prunePassiveFifos() {
    this.prunePassiveFifos(this.graph);
  }

  /**
   *
   */
  public void generateBroadcasts() {
    this.generateBroadcasts(this.graph);
  }

  /**
   *
   * @return the graph that has been transformed
   */
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
          writeScript = actor.getWritePassiveScriptPath() != null ? actor.getWritePassiveScriptPath() : WDEFAULT;
          readScript = actor.getReadPassiveScriptPath() != null ? actor.getReadPassiveScriptPath() : RDEFAULT;

        } else if (abstractActor instanceof BroadcastActor) {
          writeScript = defaultWriteUrl.toString();
          readScript = createSpecialUrl(RBROADCAST).toString();

        } else if (abstractActor instanceof JoinActor) {
          writeScript = createSpecialUrl(WJOIN).toString();
          readScript = defaultReadUrl.toString();

        } else if (abstractActor instanceof ForkActor) {
          writeScript = defaultWriteUrl.toString();
          readScript = createSpecialUrl(RFORK).toString();

        } else if (abstractActor instanceof RoundBufferActor) {
          writeScript = createSpecialUrl(WROUNDBUFFER).toString();
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
  private void runPassiveScripts(List<PassiveActor> pas) {

    // Runs all scripts and stores them in maps in scriptRunner class
    scriptRunner.run(pas);

    final String log = scriptRunner.toString();
    PreesmLogger.getLogger().info(log);

    // Computing the buffer size of all passive actors with the script results
    for (final PassiveActor pa : pas) {

      // Computing the buffer size
      long size = 0;
      for (final PassivePort pp : pa.getAllPassivePorts()) {

        // Port instance level (if the actor linked to pp has a rep value of 3, then there is max 3 instances)
        for (final Pair<Long, Long> beginEndInstancePtr : scriptRunner.getBeginEndAllInstances(pp)) {
          final long instanceEnd = beginEndInstancePtr.getValue();
          size = Math.max(instanceEnd, size);
        }
      }

      if (size == 0) {
        throw new PreesmRuntimeException("buffer size of actor " + pa.getName() + " is equal to 0");
      }

      // Before composition of passive actor, every passive ports have subBufferSize = bufferSize, and offset = 0.
      // We are still in the "standard passive actor" era.
      for (final PassivePort pp : pa.getAllPassivePorts()) {
        pp.setSubBufferSize(size);
        pp.setOffset(0);

        // Updating Endj of current passive port once we have the buffer size
        final long end = (scriptRunner.getBeginEnd(pp).getKey() + pp.getExpression().evaluateAsLong()) % size;
        scriptRunner.setEnd(pp, end);

        // It will pass beginj, endj, and all the begini and endi in the port object
        scriptRunner.populatePortWithResults(pp);

      }
    }
  }

  private void verifyPassiveAllActors() {
    for (final PassiveActor pa : this.passiveActors) {

      // if this method passes without raising an error, then it means than there is no critical error in pa
      PassiveActorVerifier.verifyPassiveActorConditions(pa, scriptRunner);
    }
  }

  private void computeRealBufferSizes() {
    for (final PassiveActor pa : this.passiveActors) {
      pa.setRealBufferSize(getRealBufferSize(pa, scriptRunner));
    }
  }

  private void transformFifosIntoPassiveActors(PiGraph graph) {
    final List<PiGraph> children = graph.getChildrenGraphs();

    for (final PiGraph child : children) {
      transformFifosIntoPassiveActors(child);
    }

    for (final Fifo fifo : graph.getFifos()) {

      // An interface is only
      boolean toOutside = fifo.getTarget() instanceof InterfaceActor && !children.contains(fifo.getTarget());
      toOutside |= fifo.getSource() instanceof InterfaceActor && !children.contains(fifo.getSource());
      if (toOutside) {
        continue;
      }

      final SimplePassiveActor passiveFifo = PiMMUserFactory.instance.createSimplePassiveActor(fifo, brv);
      passiveFifo.getInputPort().setWritePassiveScriptPath(this.defaultWriteUrl.toString());
      passiveFifo.getOutputPort().setReadPassiveScriptPath(this.defaultReadUrl.toString());
      graph.addActor(passiveFifo);
      final DataInputPort targetPort = fifo.getTargetPort();
      fifo.setTargetPort(passiveFifo.getDataInputPorts().getFirst());
      final Fifo outFifo = PiMMUserFactory.instance.createFifo(passiveFifo.getOutputPort(), targetPort, fifo.getType());
      graph.addFifo(outFifo);
      this.passiveActors.add(passiveFifo);
    }

    // New brv compute, otherwise simple passive actors won't appear.
    this.brv = PiBRV.compute(this.graph, BRVMethod.LCM);
    scriptRunner.updateBrv(brv);
    runPassiveScripts(this.passiveActors.stream().filter(pa -> pa.isSimple()).toList());
  }

  private void prunePassiveFifos(PiGraph graph) {
    for (final PiGraph child : graph.getChildrenGraphs()) {
      prunePassiveFifos(child);
    }

    final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);

    for (final PassiveActor complexPa : graph.getPassiveActors().stream().filter(a -> !a.isSimple()).toList()) {

      for (final PassivePort pp : complexPa.getAllPassivePorts()) {
        if (pp.getOppositePort().getContainingActor() instanceof final SimplePassiveActor passiveFifo) {

          final PassivePort spp = pp instanceof DataInputPort ? passiveFifo.getInputPort()
              : passiveFifo.getOutputPort();
          if (PassiveActorVerifier.verifyPassivePortConditions(spp, brv)) {
            pp.setLinkedFifo(passiveFifo.getLinkedFifo());
            final Fifo oppositeFifo = pp instanceof PassiveInputPort ? passiveFifo.getInputPort().getFifo()
                : passiveFifo.getOutputPort().getFifo();
            graph.removeFifo(pp.getFifo());
            graph.removeActor(passiveFifo);
            if (pp instanceof final PassiveInputPort pip) {
              oppositeFifo.setTargetPort(pip);
            } else {
              oppositeFifo.setSourcePort((PassiveOutputPort) pp);
            }
          } else {
            PreesmLogger.getLogger().info("Can't prune");
          }
        }
      }
    }
    PreesmLogger.getLogger().info("Pruning done");
  }

  private void generateBroadcasts(PiGraph graph) {
    for (final PiGraph child : graph.getChildrenGraphs()) {
      generateBroadcasts(child);
    }

    int id = 0;
    for (final PassiveActor pa : graph.getPassiveActors()) {
      for (final PassiveOutputPort pop : pa.getPassiveOutputPorts()) {
        final AbstractActor oppositeActor = pop.getOppositePort().getContainingActor();
        if (oppositeActor instanceof PassiveActor) {
          final Fifo fifo = pop.getFifo();
          final DataInputPort oldTarget = fifo.getTargetPort();
          final BroadcastActor memcpy = PiMMUserFactory.instance.createBroadcastActor("passive_brd_" + id++);
          graph.addActor(memcpy);
          final DataInputPort memcpyInputPort = PiMMUserFactory.instance.createDataInputPort("in");
          final DataOutputPort memcpyOutputPort = PiMMUserFactory.instance.createDataOutputPort("out");
          memcpyInputPort.setExpression(pop.getExpression().evaluateAsLong());
          memcpyOutputPort.setExpression(pop.getOppositePort().getExpression().evaluateAsLong());
          memcpy.getDataInputPorts().add(memcpyInputPort);
          memcpy.getDataOutputPorts().add(memcpyOutputPort);

          fifo.setTargetPort(memcpyInputPort);
          final Fifo outFifo = PiMMUserFactory.instance.createFifo(memcpyOutputPort, oldTarget, fifo.getType());
          graph.addFifo(outFifo);

        }
      }
    }
  }

  public static long getRealBufferSize(PassiveActor pa, PassiveScriptRunner psr) {

    long realBufferSize = 0;

    final long bufferSize = pa.getBufferSize();

    for (final PassivePort pp : pa.getAllPassivePorts()) {

      final AbstractActor oppositeActor = pp.getOppositePort().getContainingActor();
      final long oppositeRV = oppositeActor instanceof DataInterface ? 1 : psr.brv.get(oppositeActor);

      long ratei = pp.getOppositePort().getExpression().evaluateAsLong();
      ratei *= psr.brv.get(pa) / MathFunctionsHelper.gcd(psr.brv.get(pa), oppositeRV);

      for (final Pair<Long, Long> beginjiEndji : psr.getBeginEndAllInstances(pp)) {
        final long beginji = beginjiEndji.getKey();
        realBufferSize = Math.max(realBufferSize, Math.max(beginji + ratei, bufferSize));
      }
    }
    final String log = "[PASSIVE] actor " + pa.getName() + " has a buffer size = " + pa.getBufferSize()
        + " and a real buffer size = " + realBufferSize;
    PreesmLogger.getLogger().info(log);
    return realBufferSize;
  }

}

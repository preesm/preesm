package org.preesm.algorithm.schedule.fpga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.TimingType;

/**
 * Class to determine the normalized throughput of each actor.
 * 
 * @author ahonorat
 */
public class AsapFpgaIIevaluator {

  private AsapFpgaIIevaluator() {
    // do nothing
  }

  protected static class ActorNormalizedInfos {
    protected final AbstractActor aa;
    protected final AbstractActor ori;
    protected final long          brv;
    protected final long          oriET;
    protected final long          oriII;
    protected final long          normGraphII;
    protected long                avgII;

    public ActorNormalizedInfos(final AbstractActor aa, final AbstractActor ori, final long oriET, final long oriII,
        final long brv) {
      this.aa = aa;
      this.ori = ori;
      this.oriET = oriET;
      this.oriII = oriII;
      this.brv = brv;
      this.normGraphII = brv * oriII;
    }
  }

  /**
   * Computes the normalized infos about actors II.
   * 
   * @param cc
   *          Actors in the connected component, except interfaces.
   * @param scenario
   *          Scenario to get the timings and mapping constraints.
   * @param brv
   *          Repetition vector of actors in cc.
   * @return List of actor infos, sorted by decreasing normGraphII.
   */
  public static List<ActorNormalizedInfos> checkAndSetActorInfos(final List<AbstractActor> cc, final Scenario scenario,
      final Map<AbstractVertex, Long> brv) {
    final ComponentInstance fpga = scenario.getDesign().getComponentInstances().get(0);
    final List<ActorNormalizedInfos> listInfos = new ArrayList<>();
    // check and set standard infos
    for (final AbstractActor aa : cc) {
      AbstractActor ori = PreesmCopyTracker.getOriginalSource(aa);
      if (!scenario.getPossibleMappings(ori).contains(fpga)) {
        throw new PreesmRuntimeException("Actor " + ori.getVertexPath() + " is not mapped to the only fpga.");
      }
      // check mapping
      final long ii = scenario.getTimings().evaluateTimingOrDefault(ori, fpga.getComponent(),
          TimingType.INITIATION_INTERVAL);
      final long et = scenario.getTimings().evaluateTimingOrDefault(ori, fpga.getComponent(),
          TimingType.EXECUTION_TIME);
      final long rv = brv.get(aa);
      final ActorNormalizedInfos ani = new ActorNormalizedInfos(aa, ori, et, ii, rv);
      listInfos.add(ani);
    }

    Collections.sort(listInfos, new DecreasingGraphIIComparator());
    // set each avg II
    final ActorNormalizedInfos slowestActorInfos = listInfos.get(0);
    final long slowestGraphII = slowestActorInfos.normGraphII;
    for (final ActorNormalizedInfos ani : listInfos) {
      ani.avgII = slowestGraphII / ani.brv;
    }
    if (listInfos.size() > 1) {
      final ActorNormalizedInfos fastestActorInfos = listInfos.get(listInfos.size() - 1);
      PreesmLogger.getLogger()
          .info("Throughput of your application is limited by the actor " + slowestActorInfos.ori.getVertexPath()
              + " with graph II=" + slowestGraphII + " whereas fastest actor " + fastestActorInfos.ori.getVertexPath()
              + " has its graph II=" + fastestActorInfos.normGraphII);

    }
    return listInfos;
  }

  public static class DecreasingGraphIIComparator implements Comparator<ActorNormalizedInfos> {

    @Override
    public int compare(ActorNormalizedInfos arg0, ActorNormalizedInfos arg1) {
      return Long.compare(arg1.normGraphII, arg0.normGraphII);
    }

  }

}

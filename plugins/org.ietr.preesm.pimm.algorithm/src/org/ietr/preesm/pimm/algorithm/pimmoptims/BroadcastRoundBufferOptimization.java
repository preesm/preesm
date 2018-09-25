/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.pimmoptims;

import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;

/**
 * @author farresti
 *
 */
public class BroadcastRoundBufferOptimization extends PiMMSwitch<Boolean> implements PiMMOptimization {

  boolean keepGoing = false;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ietr.preesm.pimm.algorithm.pimmoptims.PiMMOptimization#optimize(org.ietr.preesm.experiment.model.pimm.PiGraph)
   */
  @Override
  public boolean optimize(PiGraph graph) {
    do {
      this.keepGoing = false;
      final Boolean doSwitch = doSwitch(graph);
      if (doSwitch == null) {
        return false;
      }
    } while (this.keepGoing);
    return true;
  }

  @Override
  public Boolean casePiGraph(PiGraph graph) {
    graph.getActors().forEach(this::doSwitch);
    return true;
  }

  @Override
  public Boolean caseBroadcastActor(BroadcastActor actor) {
    final ForkOptimization forkOptimization = new ForkOptimization();
    this.keepGoing |= forkOptimization.remove(actor.getContainingPiGraph(), actor);
    return true;
  }

  @Override
  public Boolean caseRoundBufferActor(RoundBufferActor actor) {
    final JoinOptimization joinOptimization = new JoinOptimization();
    this.keepGoing |= joinOptimization.remove(actor.getContainingPiGraph(), actor);
    return true;
  }
}

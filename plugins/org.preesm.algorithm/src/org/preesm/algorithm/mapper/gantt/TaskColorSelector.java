package org.preesm.algorithm.mapper.gantt;

import java.awt.Color;
import org.eclipse.emf.ecore.EObject;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * This class selects color for Gantt Actors, based on their type.
 * 
 * @author ahonorat
 */
public class TaskColorSelector extends PiMMSwitch<Color> {

  private final Color initActorC;
  private final Color endActorC;
  private final Color joinActorC;
  private final Color forkActorC;
  private final Color rbActorC;
  private final Color bcActorC;
  private final Color ssActorC;
  private final Color seActorC;
  private final Color rsActorC;
  private final Color reActorC;

  /**
   * Initializes default colors.
   */
  public TaskColorSelector() {
    super();

    ssActorC = new Color(240, 128, 128); // light coral
    seActorC = new Color(205, 92, 92); // indian red
    rsActorC = new Color(107, 142, 35); // olive green
    reActorC = new Color(102, 205, 170); // aquamarine green

    // same color as in Graphiti editor
    initActorC = new Color(215, 228, 189);
    endActorC = new Color(230, 185, 184);
    joinActorC = new Color(205, 133, 63);
    forkActorC = new Color(255, 165, 79);
    rbActorC = new Color(255, 218, 185);
    bcActorC = new Color(222, 184, 135);

  }

  /**
   * Special actors (i.e. communication actors) with dedicated timing switch.
   */
  @Override
  public Color defaultCase(final EObject object) {
    if (object instanceof CommunicationActor) {
      return new CommunicationColor().doSwitch(object);
    }
    return null;
  }

  /**
   * Special switch for communication actors introduced in the Schedule XCore model.
   */
  final class CommunicationColor extends ScheduleSwitch<Color> {

    @Override
    public Color caseSendStartActor(final SendStartActor sendStartActor) {
      return ssActorC;
    }

    @Override
    public Color caseSendEndActor(final SendEndActor sendEndActor) {
      return seActorC;
    }

    @Override
    public Color caseReceiveStartActor(final ReceiveStartActor receiveStartActor) {
      return rsActorC;
    }

    @Override
    public Color caseReceiveEndActor(final ReceiveEndActor receiveEndActor) {
      return reActorC;
    }
  }

  @Override
  public Color caseInitActor(final InitActor initActor) {
    return initActorC;
  }

  @Override
  public Color caseEndActor(final EndActor endActor) {
    return endActorC;
  }

  @Override
  public Color caseForkActor(final ForkActor forkActor) {
    return forkActorC;
  }

  @Override
  public Color caseJoinActor(final JoinActor joinActor) {
    return joinActorC;
  }

  @Override
  public Color caseBroadcastActor(final BroadcastActor broadcastActor) {
    return bcActorC;
  }

  @Override
  public Color caseRoundBufferActor(final RoundBufferActor roundbufferActor) {
    return rbActorC;
  }

  /**
   * Return a color for vertices in MapperDAG
   * 
   * @param vertex
   *          Vertex to consider.
   * @return Color, or null if default actor.
   */
  public Color mapperDAGcompability(MapperDAGVertex vertex) {
    String kind = vertex.getKind();
    Color res = null;
    switch (kind) {
      case MapperDAGVertex.DAG_BROADCAST_VERTEX:
      case MapperDAGVertex.SPECIAL_TYPE_BROADCAST:
        res = bcActorC;
        break;
      case MapperDAGVertex.SPECIAL_TYPE_ROUNDBUFFER:
        res = rbActorC;
        break;
      case MapperDAGVertex.DAG_FORK_VERTEX:
        res = forkActorC;
        break;
      case MapperDAGVertex.DAG_JOIN_VERTEX:
        res = joinActorC;
        break;
      case MapperDAGVertex.DAG_END_VERTEX:
        res = endActorC;
        break;
      case MapperDAGVertex.DAG_INIT_VERTEX:
        res = initActorC;
        break;
      default:
    }
    return res;
  }

}

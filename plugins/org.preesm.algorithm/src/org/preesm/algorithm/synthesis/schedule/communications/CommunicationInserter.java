package org.preesm.algorithm.synthesis.schedule.communications;

import java.util.List;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 *
 * @author anmorvan
 *
 */
public interface CommunicationInserter {

  /**
   * Insert communication actors in the schedule and mapping (but not in the PiGraph). Returns the list of inserted
   * nodes.
   */
  public List<CommunicationActor> insertCommunications(final PiGraph piGraph, final Design slamDesign,
      final Scenario scenario, final Schedule schedule, final Mapping mapping);

}

package org.preesm.algorithm.clustering;

import java.util.LinkedHashMap;
import java.util.Map;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;

/**
 * @author dgageot
 *
 */
public class ClusteringBuilder {

  private Map<AbstractActor, Schedule> scheduleMapping;

  private PiGraph algorithm;

  private Map<AbstractVertex, Long> repetitionVector;

  /**
   * @param algorithm
   *          PiGraph to clusterize
   * @param clusteringAlgorithm
   *          type of clustering algorithm
   */
  public ClusteringBuilder(final PiGraph algorithm, final String clusteringAlgorithm) {
    this.scheduleMapping = new LinkedHashMap<>();
    this.algorithm = algorithm;
    this.repetitionVector = null;
  }

  public PiGraph getAlgorithm() {
    return algorithm;
  }

  public Map<AbstractActor, Schedule> getScheduleMapping() {
    return scheduleMapping;
  }

  public Map<AbstractVertex, Long> getRepetitionVector() {
    return repetitionVector;
  }

}

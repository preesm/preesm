/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2021) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021)
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
package org.preesm.algorithm.mapper.ui.stats;

import java.util.Map.Entry;
import org.preesm.algorithm.mapper.gantt.GanttData;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.synthesis.evaluation.latency.LatencyCost;
import org.preesm.algorithm.synthesis.timer.ActorExecutionTiming;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * Builds stats from synthesis (schedule, mapping and memory allocation)
 * 
 * @author ahonorat
 *
 */
public class StatGeneratorSynthesis extends AbstractStatGenerator {

  private final Mapping     mapping;
  private final Allocation  memAlloc;
  private final LatencyCost latencyCost;

  private final GanttData gd;

  /**
   * Builds stats from synthesis results.
   * 
   * @param mapping
   *          Mapping of the PiGraph actors.
   * @param memAlloc
   *          MemoryAllocation of the PiGraph (may be null)
   * @param latencyCost
   *          Latency of the schedule.
   */
  public StatGeneratorSynthesis(final Design architecture, final Scenario scenario, final Mapping mapping,
      final Allocation memAlloc, final LatencyCost latencyCost) {
    super(architecture, scenario);
    this.mapping = mapping;
    this.memAlloc = memAlloc;
    this.latencyCost = latencyCost;

    this.gd = new GanttData();
    gd.insertSchedulerMapping(mapping, latencyCost.getExecTimings());
  }

  @Override
  public long getDAGSpanLength() {
    // TODO Auto-generated method stub
    return 0L;
  }

  @Override
  public long getDAGWorkLength() {
    // TODO Auto-generated method stub
    return 0L;
  }

  @Override
  public long getFinalTime() {
    return latencyCost.getValue();
  }

  @Override
  public long getLoad(ComponentInstance operator) {
    long res = 0L;
    for (Entry<AbstractActor, ActorExecutionTiming> e : latencyCost.getExecTimings().entrySet()) {
      AbstractActor aa = e.getKey();
      ActorExecutionTiming aet = e.getValue();
      for (ComponentInstance ci : mapping.getMapping(aa)) {
        if (ci.equals(operator)) {
          res += aet.getDuration();
        }
      }
    }
    return res;
  }

  @Override
  public long getMem(ComponentInstance operator) {
    long res = 0L;
    if (memAlloc != null) {
      for (PhysicalBuffer pb : memAlloc.getPhysicalBuffers()) {
        res += pb.getSizeInBit();
      }
    }
    return res;
  }

  @Override
  public int getNbUsedOperators() {
    return mapping.getAllInvolvedComponentInstances().size();
  }

  @Override
  public GanttData getGanttData() {
    return gd;
  }

}

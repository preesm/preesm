/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
package org.preesm.algorithm.synthesis;

import java.util.List;
import java.util.stream.Collectors;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.schedule.iterator.SimpleScheduleIterator;
import org.preesm.model.pisdf.AbstractActor;

/**
 *
 */
public class SynthesisResult {
  public final Mapping    mapping;
  public final Allocation alloc;
  public final Schedule   schedule;

  /**
   *
   */
  public SynthesisResult(final Mapping mapping, final Schedule schedule, final Allocation alloc) {
    this.mapping = mapping;
    this.schedule = schedule;
    this.alloc = alloc;
  }

  @Override
  public String toString() {
    return "\n\n" + SynthesisResult.buildString(this.schedule, this.mapping, "").toString();
  }

  private static StringBuilder buildString(final Schedule sched, final Mapping mapp, final String indent) {
    final StringBuilder res = new StringBuilder("");
    res.append(indent + sched.getClass().getSimpleName() + " {\n");
    if (sched instanceof HierarchicalSchedule) {
      for (final Schedule child : sched.getChildren()) {
        res.append(SynthesisResult.buildString(child, mapp, indent + "  ").toString());
      }
    } else {
      final List<AbstractActor> actors = new SimpleScheduleIterator(sched).getOrderedList();
      for (final AbstractActor actor : actors) {
        final List<String> collect = mapp.getMapping(actor).stream().map(m -> m.getInstanceName())
            .collect(Collectors.toList());
        res.append(indent + "  " + collect + " " + actor.getName() + "\n");
      }
    }
    res.append(indent + "}\n");
    return res;
  }

}

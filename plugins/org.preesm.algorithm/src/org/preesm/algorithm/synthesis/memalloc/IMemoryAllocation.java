/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2023) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
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
package org.preesm.algorithm.synthesis.memalloc;

import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memory.allocation.tasks.MemoryAllocatorTask;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * Memory allocation of a PiSDF under schedule/mapping constraints
 */
public interface IMemoryAllocation {

  public Allocation allocateMemory(final PiGraph piGraph, final Design slamDesign, final Scenario scenario,
      final Schedule schedule, final Mapping mapping);

  /**
   */
  public static long extractAlignment(final String valueAlignment) {
    return switch (valueAlignment.substring(0, Math.min(valueAlignment.length(), 7))) {
      case MemoryAllocatorTask.VALUE_ALIGNEMENT_NONE -> -1;
      case MemoryAllocatorTask.VALUE_ALIGNEMENT_DATA -> 0;
      case MemoryAllocatorTask.VALUE_ALIGNEMENT_FIXED -> Long.parseLong(valueAlignment.substring(7));
      default -> -1;
    };
  }
}

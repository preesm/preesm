/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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

import java.util.HashMap;
import java.util.Map;
import org.preesm.algorithm.mapper.gantt.GanttData;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * This class is the default one, given all information in the constructor.
 * 
 * @author ahonorat
 */
public class StatGeneratorPrecomputed extends AbstractStatGenerator {

  public final long                         dagSpanLength;
  public final long                         dagWorkLength;
  public final long                         finalTime;
  public final int                          nbUsedOperators;
  public final Map<ComponentInstance, Long> loads;
  public final Map<ComponentInstance, Long> mems;

  public final GanttData gantt;

  public StatGeneratorPrecomputed(final Design architecture, final Scenario scenario, final long dagSpanLength,
      final long dagWorkLength, final long finalTime, final int nbUsedOperators,
      final Map<ComponentInstance, Long> loads, final Map<ComponentInstance, Long> mems, final GanttData gantt) {
    super(architecture, scenario);

    this.dagSpanLength = dagSpanLength;
    this.dagWorkLength = dagWorkLength;
    this.finalTime = finalTime;
    this.nbUsedOperators = nbUsedOperators;
    this.loads = new HashMap<>(loads);
    this.mems = new HashMap<>(mems);
    this.gantt = gantt;
  }

  @Override
  public long getDAGSpanLength() {
    return dagSpanLength;
  }

  @Override
  public long getDAGWorkLength() {
    return dagWorkLength;
  }

  @Override
  public long getFinalTime() {
    return finalTime;
  }

  @Override
  public long getLoad(ComponentInstance operator) {
    return loads.getOrDefault(operator, 0L);
  }

  @Override
  public long getMem(ComponentInstance operator) {
    return mems.getOrDefault(operator, 0L);
  }

  @Override
  public int getNbUsedOperators() {
    // alternatively, if it was not given:
    // (int) gantt.getComponents().stream().filter(x -> !x.getTasks().isEmpty()).count();
    return nbUsedOperators;
  }

  @Override
  public GanttData getGanttData() {
    return gantt;
  }

}

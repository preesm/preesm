/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
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
package org.preesm.algorithm.synthesis.schedule;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.model.pisdf.AbstractActor;

/**
 * @author dgageot
 *
 */
public class SchedulePrinterSwitch extends ScheduleSwitch<String> {

  public static final String print(final EObject s) {
    return new SchedulePrinterSwitch().doSwitch(s);
  }

  Schedule parentNode;

  public SchedulePrinterSwitch() {
    super();
    this.parentNode = null;
  }

  @Override
  public String caseActorSchedule(final ActorSchedule object) {
    final StringBuilder toPrint = new StringBuilder();
    if (object.getRepetition() > 1) {
      toPrint.append(object.getRepetition());
      if (object.isParallel()) {
        toPrint.append("/");
      }
      toPrint.append("(");
    }

    // Print actors names
    final List<String> actorsNames = new LinkedList<>();
    final List<AbstractActor> actors = new ScheduleOrderManager(object).getSimpleOrderedList();
    for (final AbstractActor actor : actors) {
      actorsNames.add(actor.getName());
    }
    toPrint.append(String.join("", actorsNames));

    if (object.getRepetition() > 1) {
      toPrint.append(")");
    }
    return toPrint.toString();
  }

  @Override
  public String caseSequentialHiearchicalSchedule(final SequentialHiearchicalSchedule object) {
    final StringBuilder toPrint = new StringBuilder();

    if (object.getRepetition() > 1) {
      toPrint.append(object.getRepetition());
      if ((this.parentNode != null) && this.parentNode.isParallel()) {
        toPrint.append("/");
      }
      toPrint.append("(");
    }

    // Print sequential operator
    final List<String> schedulesExpressions = new LinkedList<>();
    for (final Schedule children : object.getChildren()) {
      this.parentNode = object;
      schedulesExpressions.add(doSwitch(children));
    }
    toPrint.append(String.join("*", schedulesExpressions));

    if (object.getRepetition() > 1) {
      toPrint.append(")");
    }

    return toPrint.toString();
  }

  @Override
  public String caseParallelHiearchicalSchedule(final ParallelHiearchicalSchedule object) {
    final StringBuilder toPrint = new StringBuilder();

    if (object.getRepetition() > 1) {
      toPrint.append(object.getRepetition() + "/(");
    }

    // Print parallel operator
    final List<String> schedulesExpressions = new LinkedList<>();
    for (final Schedule children : object.getChildren()) {
      this.parentNode = object;
      schedulesExpressions.add(doSwitch(children));
    }

    toPrint.append(String.join("|", schedulesExpressions));

    if (object.getRepetition() > 1) {
      toPrint.append(")");
    }

    return toPrint.toString();
  }

}

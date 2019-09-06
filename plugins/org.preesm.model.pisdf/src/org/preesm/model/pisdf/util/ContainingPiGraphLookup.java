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
package org.preesm.model.pisdf.util;

import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.Refinement;

/**
 *
 */
public class ContainingPiGraphLookup extends PiMMSwitch<PiGraph> {

  @Override
  public PiGraph caseAbstractVertex(final AbstractVertex object) {
    return object.getContainingPiGraph();
  }

  @Override
  public PiGraph casePiGraph(final PiGraph graph) {
    return graph;
  }

  @Override
  public PiGraph caseParameter(final Parameter param) {
    return param.getContainingPiGraph();
  }

  @Override
  public PiGraph caseFifo(final Fifo fifo) {
    return fifo.getContainingPiGraph();
  }

  @Override
  public PiGraph caseDependency(final Dependency dependency) {
    return dependency.getContainingPiGraph();
  }

  @Override
  public PiGraph casePort(final Port object) {
    return doSwitch(object.eContainer());
  }

  @Override
  public PiGraph caseRefinement(Refinement object) {
    return doSwitch(object.getRefinementContainer());
  }

  @Override
  public PiGraph caseExpression(Expression object) {
    return doSwitch(object.getHolder());
  }

}

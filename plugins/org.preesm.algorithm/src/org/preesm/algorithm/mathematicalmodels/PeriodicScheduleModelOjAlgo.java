/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2023) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hamza Deroui [hamza.deroui@insa-rennes.fr] (2018)
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
package org.preesm.algorithm.mathematicalmodels;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang3.math.Fraction;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;

/**
 * @author hderoui
 *
 */
public class PeriodicScheduleModelOjAlgo implements SolverMethod {

  private static final String EDGE_NAME = "edgeName";

  /**
   * @param sdf
   *          graph
   * @return normalized period
   */
  @Override
  public Fraction computeNormalizedPeriod(final SDFGraph sdf) {

    // ----- ojAlgo model ---------------------------------
    final ExpressionsBasedModel model = new ExpressionsBasedModel();

    // ----- Variables ---------------------------------------------
    final Map<String, Variable> edgeVariables = new LinkedHashMap<>(sdf.edgeSet().size());
    for (final SDFEdge e : sdf.edgeSet()) {
      edgeVariables.put((String) e.getPropertyBean().getValue(EDGE_NAME),
          model.newVariable((String) e.getPropertyBean().getValue(EDGE_NAME)).lower(0).upper(1)
              .weight((Double) e.getSource().getPropertyBean().getValue("duration")));
    }

    // ----- Constraints ------------------------------------------
    // First constraint
    for (final SDFAbstractVertex a : sdf.vertexSet()) {
      final Expression expr = model.addExpression(a.getName()).lower(0).upper(0);
      // skip the reentrant edges
      for (final SDFInterfaceVertex input : a.getSources()) {
        final SDFEdge e = a.getAssociatedEdge(input);
        if (!(e.getSource().getName().equals(a.getName()))) {
          expr.set(edgeVariables.get(e.getPropertyBean().getValue(EDGE_NAME)), 1);
        }
      }

      // skip the reentrant edges
      for (final SDFInterfaceVertex output : a.getSinks()) {
        final SDFEdge e = a.getAssociatedEdge(output);
        if (!(e.getTarget().getName().equals(a.getName()))) {
          expr.set(edgeVariables.get(e.getPropertyBean().getValue(EDGE_NAME)), -1);
        }
      }
    }

    // Second constraint : sum of H.x = 1
    final Expression expr = model.addExpression("sumHX").lower(1).upper(1);
    for (final SDFEdge e : sdf.edgeSet()) {
      final long difference = e.getDelay().longValue() - e.getCons().longValue();
      final long gcd = MathFunctionsHelper.gcd(e.getProd().longValue(), e.getCons().longValue());
      final double normalizationFactor = e.getPropertyBean().getValue("normalizationFactor");
      final double h = (difference + gcd) * normalizationFactor;
      expr.set(edgeVariables.get(e.getPropertyBean().getValue(EDGE_NAME)), h);
    }

    // ----- solve the problem -------------------------------------
    final Optimisation.Result result = model.maximise();
    final Fraction period = Fraction.getFraction(result.getValue());

    // set he normalized period
    sdf.setPropertyValue("normalizedPeriod", period);
    final String msg = "Normalized period found K = " + period;
    PreesmLogger.getLogger().log(Level.INFO, msg);
    return period;
  }

}

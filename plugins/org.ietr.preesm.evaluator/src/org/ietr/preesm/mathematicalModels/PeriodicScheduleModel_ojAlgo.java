/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2018)
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
package org.ietr.preesm.mathematicalModels;

import java.util.Hashtable;
import java.util.Map;
import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.preesm.utils.math.MathFunctionsHelper;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

/**
 * @author hderoui
 *
 */
public class PeriodicScheduleModel_ojAlgo implements SolverMethod {
  public Map<String, Variable> edgeVariables;

  /**
   * @param SDF
   *          graph
   * @return normalized period
   */
  @Override
  public Fraction computeNormalizedPeriod(final SDFGraph SDF) {

    // ----- Variables ---------------------------------------------
    this.edgeVariables = new Hashtable<>(SDF.edgeSet().size());
    for (final SDFEdge e : SDF.edgeSet()) {
      this.edgeVariables.put((String) e.getPropertyBean().getValue("edgeName"),
          Variable.make((String) e.getPropertyBean().getValue("edgeName")).lower(0).upper(1)
              .weight((Double) e.getSource().getPropertyBean().getValue("duration")));

    }

    // ----- ojAlgo model ---------------------------------
    final ExpressionsBasedModel model = new ExpressionsBasedModel();
    model.addVariables(this.edgeVariables.values());

    // ----- Constraints ------------------------------------------
    // First constraint
    for (final SDFAbstractVertex a : SDF.vertexSet()) {
      final Expression expr = model.addExpression(a.getName()).lower(0).upper(0);
      // skip the reentrant edges
      for (final SDFInterfaceVertex input : a.getSources()) {
        final SDFEdge e = a.getAssociatedEdge(input);
        if (e.getSource().getName() != a.getName()) {
          expr.set(this.edgeVariables.get(e.getPropertyBean().getValue("edgeName")), 1);
        }
      }

      // skip the reentrant edges
      for (final SDFInterfaceVertex output : a.getSinks()) {
        final SDFEdge e = a.getAssociatedEdge(output);
        if (e.getTarget().getName() != a.getName()) {
          expr.set(this.edgeVariables.get(e.getPropertyBean().getValue("edgeName")), -1);
        }
      }
    }

    // Second constraint : sum of H.x = 1
    final Expression expr = model.addExpression("sumHX").lower(1).upper(1);
    for (final SDFEdge e : SDF.edgeSet()) {
      final double h = ((e.getDelay().intValue() - e.getCons().intValue())
          + MathFunctionsHelper.gcd(e.getProd().intValue(), e.getCons().intValue()))
          * (double) e.getPropertyBean().getValue("normalizationFactor");
      expr.set(this.edgeVariables.get(e.getPropertyBean().getValue("edgeName")), h);
    }

    // ----- solve the problem -------------------------------------
    final Optimisation.Result result = model.maximise();
    final Fraction period = Fraction.getFraction(result.getValue());

    // set he normalized period
    SDF.setPropertyValue("normalizedPeriod", period);
    System.out.println("Normalized period found K = " + period);
    return period;
  }

}

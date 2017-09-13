package org.ietr.preesm.mathematicalModels;

import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;

/**
 * 
 * @author hderoui
 *
 */
public interface SolverMethod {

  Fraction computeNormalizedPeriod(SDFGraph grapgh);
}

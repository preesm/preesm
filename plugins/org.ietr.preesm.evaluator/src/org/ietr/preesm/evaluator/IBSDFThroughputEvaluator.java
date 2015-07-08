package org.ietr.preesm.evaluator;

import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;

public class IBSDFThroughputEvaluator {
	
	/**
	 * Computes (heuristically, so not necessarily exact) the throughput on the optimal
	 * periodic schedule (if it exists) of a given graph under the given scenario
	 * 
	 */
	public double launch(SDFGraph inputGraph, PreesmScenario scenario) throws InvalidExpressionException {
		// Step 1 : compute K_min = max {K_g forall g in G}
		
		// Step 2 : Test if k_min a valid period for the graph test_period(K_min,G)
			// if true : 
				//K_min is the minimal normalized period
			// otherwise :
		
				// Step 3 : Find a value for K_max
					// K_max = x * K_min (x TBD)
					// while test_period(K_max,G) false
						// increase K_max
				// Step 4 : Improve K 
					// while K_max - K_min > eps :
						// K = (K_max + K_min) / 2
						// if test_period(K,G) == true :
							// K_max = K
						// otherwise :
							// K_min = K
		return 0;
	}
}

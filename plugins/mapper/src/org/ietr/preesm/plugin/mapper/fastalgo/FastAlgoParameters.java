/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.plugin.mapper.fastalgo;

import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.mapper.AbstractParameters;
import org.ietr.preesm.plugin.mapper.edgescheduling.EdgeSchedType;

/**
 * Class which purpose is setting the parameters for the fast algorithm
 * 
 * @author pmenuet
 */

public class FastAlgoParameters extends AbstractParameters {

	/**
	 * This is the number of probabilistic local jump which the user want. The
	 * PFast, as the Fast, does probabilistic jump on the critical path which
	 * modify totally the mapping. The margIn parameter is the number you want
	 * to try probabilistic jump PER vertex to refine the solution locally.
	 */
	private int margIn;

	/**
	 * MaxCount is the number of iteration (big probabilistic jump) the
	 * algorithm does. An iteration is a probabilistic jump on the critical path
	 * to find a better solution.
	 */
	private int maxCount;

	/**
	 * MaxStep determines the number of vertices chosen to do the local
	 * refinement.
	 */
	private int maxStep;

	public FastAlgoParameters(TextParameters textParameters) {
		super(textParameters);
		
		this.maxCount = textParameters.getIntVariable("maxCount");
		this.maxStep = textParameters.getIntVariable("maxStep");
		this.margIn = textParameters.getIntVariable("margIn");
	}

	/**
	 * 
	 * Constructors
	 * 
	 */

	public FastAlgoParameters(int maxCount, int maxStep, int margIn,
			AbcType simulatorType, EdgeSchedType edgeSchedType) {
		super(simulatorType,edgeSchedType);
		textParameters.addVariable("maxCount",maxCount);
		textParameters.addVariable("maxStep",maxStep);
		textParameters.addVariable("margIn",margIn);
		textParameters.addVariable("simulatorType", simulatorType.toString());
		
		this.maxCount = maxCount;
		this.maxStep = maxStep;
		this.margIn = margIn;
	}

	/**
	 * 
	 * Getters and setters
	 * 
	 */

	public int getMargIn() {
		return margIn;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public int getMaxStep() {
		return maxStep;
	}

	public void setMargIn(int margIn) {
		this.margIn = margIn;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public void setMaxStep(int maxStep) {
		this.maxStep = maxStep;
	}

}

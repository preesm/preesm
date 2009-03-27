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

package org.ietr.preesm.plugin.mapper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.mapper.listsched.AlgorithmTransformer;
import org.ietr.preesm.plugin.mapper.listsched.CombListSched;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Class calling a tranformation for communication contentious list scheduling
 * 
 * @author pmu
 */
public class ListSchedTransformation extends AbstractMapping {

	@Override
	public void transform(SDFGraph algorithm, SDFGraph transformedAlgorithm) {
		// TODO Auto-generated method stub

	}

	@Override
	public TaskResult transform(SDFGraph algorithm, MultiCoreArchitecture architecture,
			TextParameters textParameters, IScenario scenario, IProgressMonitor monitor) {

		super.transform(algorithm,architecture,textParameters,scenario,monitor);
		// TODO Add here the calls to your task scheduling algorithm
		// in which you ask communicationcontentiouslistschedulingdabc for
		// implementation evaluations
		System.out
				.println("Communication Contentious List Scheduling Transformation!");
		TaskResult result = new TaskResult();
		// CommunicationContentiousListSchedulingParameters parameters = new
		// CommunicationContentiousListSchedulingParameters(
		// textParameters);
		// AlgorithmTransformer algoTransformer = new AlgorithmTransformer();
		// architecture = Examples.get3C64Archi();
		CombListSched scheduler = new CombListSched(algorithm, architecture,
				scenario);

		scheduler.schedule();

		// result.setDAG(algoTransformer.algorithm2DAG(scheduler
		// .getBestScheduler()));
		super.clean(architecture,scenario);
		return result;
	}

	public static void main(String[] args) {
		AlgorithmTransformer algoTransformer = new AlgorithmTransformer();

		// Generating random sdf dag
		int nbVertex = 100, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph sdf = algoTransformer.randomSDF(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 500, 1000);
		MultiCoreArchitecture architecture = Examples.get4PArchi();
		CombListSched scheduler = new CombListSched(sdf, architecture, null);
		scheduler.schedule();
	}
}

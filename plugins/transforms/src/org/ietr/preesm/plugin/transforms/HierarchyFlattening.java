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
 
package org.ietr.preesm.plugin.transforms;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.task.IGraphTransformation;
import org.ietr.preesm.core.task.PreesmException;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.visitors.ConsistencyChecker;
import org.sdf4j.visitors.SDF4JException;
import org.sdf4j.visitors.VisitorOutput;

/**
 * Class used to flatten the hierarchy of a given graph
 * 
 * @author jpiat
 * 
 */
public class HierarchyFlattening implements IGraphTransformation {

	@Override
	public TaskResult transform(SDFGraph algorithm, TextParameters params) throws PreesmException{
		String depthS = params.getVariable("depth");
		int depth;
		if (depthS != null) {
			depth = Integer.decode(depthS);
		} else {
			depth = 1;
		}
		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINEST);
		VisitorOutput.setLogger(logger);
		ConsistencyChecker checkConsistent = new ConsistencyChecker();
		if(checkConsistent.verifyGraph(algorithm)){
			logger.log(Level.FINER, "flattening application "+algorithm.getName()+" at level "+depth);
			org.sdf4j.visitors.HierarchyFlattening flatHier = new org.sdf4j.visitors.HierarchyFlattening();
			VisitorOutput.setLogger(logger);
			if(algorithm.validateModel()){
				try {
					flatHier.flattenGraph(algorithm, depth);
				} catch (SDF4JException e) {
					e.printStackTrace();
					throw(new PreesmException(e.getMessage()));
				}
				logger.log(Level.FINER, "flattening complete");
				TaskResult result = new TaskResult();
				result.setSDF((SDFGraph) flatHier.getOutput());
				return result;
			}else{
				throw(new PreesmException("Graph not valid, not schedulable"));
			}
		}else{
			logger.log(Level.SEVERE, "Inconsistent Hierarchy, graph can't be flattened");
			TaskResult result = new TaskResult();
			result.setSDF((SDFGraph) algorithm.clone());
			throw(new PreesmException("Inconsistent Hierarchy, graph can't be flattened"));
		}
		

	}

}

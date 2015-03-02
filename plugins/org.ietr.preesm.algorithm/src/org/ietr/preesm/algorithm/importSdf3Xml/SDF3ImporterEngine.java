/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.algorithm.importSdf3Xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.scenario.ConstraintGroupManager;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.types.DataType;

public class SDF3ImporterEngine {

	private Sdf3XmlParser sdf3Parser;

	public SDF3ImporterEngine() {
		sdf3Parser = new Sdf3XmlParser();
	}

	public SDFGraph importFrom(IPath path, PreesmScenario scenario,
			Design architecture, Logger logger) throws WorkflowException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile iFile = workspace.getRoot().getFile(path);

		if (!iFile.exists()) {
			String message = "The parsed xml file does not exists: "
					+ path.toOSString();
			logger.log(Level.SEVERE, message);
			throw new WorkflowException(message);
		}

		File file = new File(iFile.getRawLocation().toOSString());
		InputStream iStream = null;
		try {
			iStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Parse the input SDF3 graph
		SDFGraph graph = null;
		try {
			graph = (sdf3Parser.parse(iStream));
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "SDF3 Parser Error: " + e.getMessage());
			return null;
		}

		if (graph != null) {
			updateScenario(graph, scenario, architecture);
		}

		return graph;
	}

	private void updateScenario(SDFGraph graph, PreesmScenario scenario,
			Design architecture) {
		// Update the input scenario so that all task can be scheduled
		// on all operators, and all have the same runtime.
		ConstraintGroupManager constraint = scenario
				.getConstraintGroupManager();
		// For each operator of the architecture
		for (ComponentInstance component : architecture.getComponentInstances()) {
			// for each actor of the graph
			for (Entry<SDFAbstractVertex, Integer> entry : sdf3Parser
					.getActorExecTimes().entrySet()) {
				// Add the operator to the available operator for the
				// current actor
				entry.getKey().setInfo(entry.getKey().getName());
				constraint.addConstraint(component.getInstanceName(),
						entry.getKey());
				// Set the timing of the actor
				Timing t = scenario.getTimingManager().addTiming(
						entry.getKey().getName(),
						component.getComponent().getVlnv().getName());
				t.setTime(entry.getValue());
			}
		}
		// Add the data types of the SDF3 graph to the scenario
		for (Entry<String, Integer> entry : sdf3Parser.getDataTypes()
				.entrySet()) {
			DataType type = new DataType(entry.getKey(), entry.getValue());
			scenario.getSimulationManager().putDataType(type);
		}
	}

}

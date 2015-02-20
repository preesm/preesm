/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.core.scenario.serialize;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.Activator;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Refinement;

/**
 * Importing timings in a scenario from a csv file. 
 * task names are rows while operator types are columns
 * 
 * @author jheulot
 */
public class CsvTimingParser {

	private PreesmScenario scenario = null;

	public CsvTimingParser(PreesmScenario scenario) {
		super();
		this.scenario = scenario;
	}

	public void parse(String url, Set<String> opDefIds)
			throws InvalidModelException, FileNotFoundException {
		WorkflowLogger
				.getLogger()
				.log(Level.INFO,
						"Importing timings from a csv sheet. Non precised timings are kept unmodified.");

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		Activator.updateWorkspace();

		Path path = new Path(url);
		IFile file = workspace.getRoot().getFile(path);
		try {
			Map<String, Map<String, String>> timings = new HashMap<>();
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()));
			
			String line;
			
			/* Read header */
			line = br.readLine();
			String[] opNames = line.split(",");
			if(opNames.length <= 1 || ! opNames[0].equals("Actors")){
				WorkflowLogger.getLogger().log(
						Level.WARNING,
						"Timing csv file must have an header line starting with \"Actors\"\nNothing done");
				return;
			}
								
			/* Parse the whole file to create the timings Map */
			while (( line = br.readLine()) != null) {
				String[] cells = line.split(",");
				if(cells.length > 1){
					Map<String, String> timing = new HashMap<>();
					
					for(int i=1; i<cells.length; i++){
						timing.put(opNames[i], cells[i]);
					}
					
					timings.put(cells[0], timing);					
				}
		    }

			parseTimings(timings, opDefIds);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void parseTimings(
			Map<String, Map<String, String>> timings, 
			Set<String> opDefIds)
			throws FileNotFoundException, InvalidModelException, CoreException {
		// Depending on the type of SDF graph we process (IBSDF or PISDF), call
		// one or the other method
		if (scenario.isIBSDFScenario()) {
			throw new InvalidModelException();
		}

		else if (scenario.isPISDFScenario()) {
			PiGraph currentGraph = ScenarioParser.getPiGraph(scenario.getAlgorithmURL());
			parseTimingsForPISDFGraph(
					timings, 
					currentGraph, 
					opDefIds);
		}

	}

	private void parseTimingsForPISDFGraph(
			Map<String, Map<String, String>> timings, 
			PiGraph currentGraph,
			Set<String> opDefIds) {
		// Each of the vertices of the graph is either itself a graph
		// (hierarchical vertex), in which case we call recursively this method;
		// a standard actor, in which case we parser its timing; or a special
		// vertex, in which case we do nothing
		for (AbstractActor vertex : currentGraph.getVertices()) {
			// Handle connected graphs from hierarchical vertices
			if (vertex instanceof PiGraph) {
				parseTimingsForPISDFGraph(timings, (PiGraph) vertex, opDefIds);
			} else if (vertex instanceof Actor) {
				Actor actor = (Actor) vertex;

				// Handle unconnected graphs from hierarchical vertices
				Refinement refinement = actor.getRefinement();
				AbstractActor subgraph = null;
				if (refinement != null)
					subgraph = refinement.getAbstractActor();

				if (subgraph != null && subgraph instanceof PiGraph) {
					parseTimingsForPISDFGraph(timings, (PiGraph) subgraph, opDefIds);
				}
				// If the actor is not hierarchical, parse its timing
				else {
					parseTimingForVertex(timings, vertex.getName(), opDefIds);
				}
			}
		}

	}

	private void parseTimingForVertex(
			Map<String, Map<String, String>> timings, 
			String vertexName,
			Set<String> opDefIds) {
		// For each kind of processing elements, we look for a timing for given vertex
		for (String opDefId : opDefIds) {
			if (!opDefId.isEmpty() && !vertexName.isEmpty()) {
				// Get the timing we are looking for
				try{
					String expression = timings.get(vertexName).get(opDefId);
					Timing timing = new Timing(
						opDefId, 
						vertexName,
						expression
					);

					scenario.getTimingManager().addTiming(timing);

					WorkflowLogger.getLogger().log(
							Level.INFO,
							"Importing timing: " + timing.toString());
					
				}catch(Exception e){
					WorkflowLogger.getLogger().log(
							Level.INFO,
							"Cannot retreive timing for (" 
									+ vertexName + ", "
									+ opDefId + ")");					
				}
			}
		}
	}
}

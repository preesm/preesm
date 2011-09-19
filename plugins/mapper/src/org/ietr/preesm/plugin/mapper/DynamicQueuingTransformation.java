/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.plugin.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.tools.AbstractWorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.Interconnection;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.parser.VLNV;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNode;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNodeDefinition;
import org.ietr.preesm.core.scenario.SDFAndArchitectureScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.taskscheduling.SimpleTaskSched;
import org.ietr.preesm.plugin.mapper.algo.dynamic.DynamicQueuingScheduler;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.graphtransfo.TagDAG;
import org.ietr.preesm.plugin.mapper.model.InitialEdgeProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.params.AbcParameters;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.types.DAGDefaultVertexPropertyType;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.SDFVertex;

/**
 * Plug-in class for dynamic queuing scheduling
 * 
 * @author mpelcat
 */
public class DynamicQueuingTransformation extends AbstractMapping {

	/**
	 * 
	 */
	public DynamicQueuingTransformation() {
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = super.getDefaultParameters();

		parameters.put("iterationNr", "0");
		parameters.put("iterationPeriod", "0");
		parameters.put("listType", "optimised");
		return parameters;
	}

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName) throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();
		MultiCoreArchitecture architecture = (MultiCoreArchitecture) inputs
				.get("architecture");
		SDFGraph algorithm = (SDFGraph) inputs.get("SDF");
		SDFAndArchitectureScenario scenario = (SDFAndArchitectureScenario) inputs
				.get("scenario");

		// The graph may be repeated a predefined number of times
		// with a predefined period
		int iterationNr = Integer.valueOf(parameters.get("iterationNr"));
		int iterationPeriod = Integer
				.valueOf(parameters.get("iterationPeriod"));

		// Repeating the graph to simulate several calls. Repetitions are
		// delayed through
		// Special dependencies to virtual operator tasks
		Operator virtualDelayManager = null;
		if (iterationNr != 0) {
			// Creating a virtual component
			VLNV v = new VLNV("nobody", "none", "DelayManager", "1.0");
			OperatorDefinition opdef = new OperatorDefinition(v);
			virtualDelayManager = new Operator("VirtualDelayManager", opdef);
			architecture.addComponent(virtualDelayManager);

			// Connecting the virtual component to all cores
			for (ArchitectureComponent cmp : architecture.getComponents()) {
				if (cmp instanceof Operator
						&& !cmp.getId().equals("VirtualDelayManager")) {
					Operator op = (Operator) cmp;
					VLNV v2 = new VLNV("nobody", "none", "DelayManagerNodeTo"
							+ op.getId(), "1.0");
					ParallelNodeDefinition nodeDef = new ParallelNodeDefinition(
							v2);
					nodeDef.setDataRate(1000000);
					ParallelNode virtualNode = new ParallelNode("virtualNodeTo"
							+ op.getId(), nodeDef);
					architecture.addComponent(virtualNode);
					Interconnection c1 = architecture.addEdge(virtualNode, op);
					c1.setDirected(false);
					Interconnection c2 = architecture.addEdge(virtualNode,
							virtualDelayManager);
					c2.setDirected(false);
				}
			}
		}

		super.execute(inputs, parameters, monitor, nodeName);

		AbcParameters abcParameters = new AbcParameters(parameters);

		MapperDAG dag = SdfToDagConverter.convert(algorithm, architecture,
				scenario, false);

		// Repeating the graph to simulate several calls.
		if (iterationNr != 0) {
			AbstractWorkflowLogger.getLogger().log(
					Level.INFO,
					"Repetition of the graph " + iterationNr
							+ " time(s) with period " + iterationPeriod
							+ " required in dynamic scheduling");

			// Creating virtual actors to delay iterations
			MapperDAGVertex lastCreatedVertex = null;
			for (int i = 0; i < iterationNr; i++) {
				// A virtual actor with SDF corresponding vertex to associate a
				// timing
				MapperDAGVertex v = new MapperDAGVertex();
				SDFAbstractVertex sdfV = new SDFVertex();
				sdfV.setName("VirtualDelay");
				sdfV.setId("VirtualDelay");
				v.setCorrespondingSDFVertex(sdfV);
				v.setName("VirtualDelay" + "__@" + (i + 2));
				v.setId("VirtualDelay" + "__@" + (i + 2));
				v.setNbRepeat(new DAGDefaultVertexPropertyType(1));
				v.getInitialVertexProperty().addOperator(virtualDelayManager);
				Timing timing = new Timing(virtualDelayManager.getDefinition()
						.getId(), sdfV.getId(), iterationPeriod);
				v.getInitialVertexProperty().addTiming(timing);
				dag.addVertex(v);

				// Edges between actors ensure the order of appearance
				if (lastCreatedVertex != null) {
					MapperDAGEdge e = (MapperDAGEdge) dag.addEdge(
							lastCreatedVertex, v);
					InitialEdgeProperty p = new InitialEdgeProperty(0);
					e.setInitialEdgeProperty(p);
				}
				lastCreatedVertex = v;
			}

			for (int i = 0; i < iterationNr; i++) {
				MapperDAG clone = dag.clone();

				MapperDAGVertex correspondingVirtualVertex = (MapperDAGVertex) dag
						.getVertex("VirtualDelay" + "__@" + (i + 2));

				// Copy cloned vertices into dag
				for (DAGVertex v : clone.vertexSet()) {
					if (!v.getName().contains("__@")) {
						// Cloning the vertices to duplicate the graph
						v.setName(v.getName() + "__@" + (i + 2));
						v.setId(v.getId() + "__@" + (i + 2));
						dag.addVertex(v);

						// Adding edges to delay correctly the execution of
						// iterations. Only vertices without predecessor are
						// concerned
						if (v.incomingEdges().isEmpty()) {
							MapperDAGEdge e = (MapperDAGEdge) dag.addEdge(
									correspondingVirtualVertex, v);

							// 0 data edges will be ignored while routing
							InitialEdgeProperty p = new InitialEdgeProperty(0);
							e.setInitialEdgeProperty(p);
						}
					}
				}

				// Copy cloned edges into dag
				String currentPostFix = "__@" + (i + 2);
				for (DAGEdge e : clone.edgeSet()) {
					if (e.getSource().getName().contains(currentPostFix)
							&& e.getTarget().getName().contains(currentPostFix)) {
						dag.addEdge(e.getSource(), e.getTarget(), e);
					}
				}
			}
		}

		// calculates the DAG span length on the architecture main operator (the
		// tasks that can
		// not be executed by the main operator are deported without transfer
		// time to other operator)
		calculateSpan(dag, architecture, scenario, abcParameters);

		// Generating the vertex list in correct order
		IAbc simu = new InfiniteHomogeneousAbc(abcParameters, dag,
				architecture, abcParameters.getSimulatorType()
						.getTaskSchedType(), scenario);

		AbstractWorkflowLogger.getLogger()
				.log(Level.INFO, "Dynamic Scheduling");

		IAbc simu2 = AbstractAbc.getInstance(abcParameters, dag, architecture,
				scenario);
		simu2.setTaskScheduler(new SimpleTaskSched());

		DynamicQueuingScheduler dynamicSched = new DynamicQueuingScheduler(
				simu.getTotalOrder(), parameters);
		dynamicSched.mapVertices(simu2);

		simu2.retrieveTotalOrder();

		TagDAG tagSDF = new TagDAG();

		try {
			tagSDF.tag(dag, architecture, scenario, simu2,
					abcParameters.getEdgeSchedType());
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
			throw (new WorkflowException(e.getMessage()));
		}

		outputs.put("DAG", dag);
		outputs.put("ABC", simu2);

		super.clean(architecture, scenario);

		AbstractWorkflowLogger.getLogger().log(Level.INFO,
				"End of Dynamic Scheduling");

		return outputs;
	}

}

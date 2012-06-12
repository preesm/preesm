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

package org.ietr.preesm.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.dag.types.DAGDefaultVertexPropertyType;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.algorithm.model.sdf.SDFVertex;
import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.architecture.slam.SlamFactory;
import net.sf.dftools.architecture.slam.attributes.AttributesFactory;
import net.sf.dftools.architecture.slam.attributes.VLNV;
import net.sf.dftools.architecture.slam.component.ComNode;
import net.sf.dftools.architecture.slam.component.Component;
import net.sf.dftools.architecture.slam.component.ComponentFactory;
import net.sf.dftools.architecture.slam.component.Operator;
import net.sf.dftools.architecture.slam.link.Link;
import net.sf.dftools.architecture.slam.link.LinkFactory;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.mapper.abc.AbstractAbc;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.mapper.abc.taskscheduling.SimpleTaskSched;
import org.ietr.preesm.mapper.algo.dynamic.DynamicQueuingScheduler;
import org.ietr.preesm.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.mapper.graphtransfo.TagDAG;
import org.ietr.preesm.mapper.model.InitialEdgeProperty;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.params.AbcParameters;

/**
 * Plug-in class for dynamic queuing scheduling
 * 
 * @author mpelcat
 */
public class DynamicQueuingMapping extends AbstractMapping {

	/**
	 * 
	 */
	public DynamicQueuingMapping() {
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
			String nodeName, Workflow workflow) throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();
		Design architecture = (Design) inputs.get("architecture");
		SDFGraph algorithm = (SDFGraph) inputs.get("SDF");
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

		// The graph may be repeated a predefined number of times
		// with a predefined period
		int iterationNr = Integer.valueOf(parameters.get("iterationNr"));
		int iterationPeriod = Integer
				.valueOf(parameters.get("iterationPeriod"));

		// Repeating the graph to simulate several calls. Repetitions are
		// delayed through
		// Special dependencies to virtual operator tasks
		ComponentInstance virtualDelayManager = null;
		if (iterationNr != 0) {
			// Creating a virtual component
			VLNV v = AttributesFactory.eINSTANCE.createVLNV();
			v.setVendor("nobody");
			v.setLibrary("none");
			v.setName("DelayManager");
			v.setVersion("1.0");

			Component component = (Component) ComponentFactory.eINSTANCE
					.createOperator();
			component.setVlnv(v);
			architecture.getComponentHolder().getComponents().add(component);
			SlamFactory.eINSTANCE.createComponentInstance();
			virtualDelayManager = SlamFactory.eINSTANCE
					.createComponentInstance();
			virtualDelayManager.setInstanceName("VirtualDelayManager");
			virtualDelayManager.setComponent(component);
			architecture.getComponentInstances().add(virtualDelayManager);

			// Connecting the virtual component to all cores
			for (ComponentInstance cmp : DesignTools
					.getComponentInstances(architecture)) {
				if (cmp.getComponent() instanceof Operator
						&& !cmp.getInstanceName().equals("VirtualDelayManager")) {
					VLNV v2 = AttributesFactory.eINSTANCE.createVLNV();
					v.setVendor("nobody");
					v.setLibrary("none");
					v.setName("DelayManagerNodeTo" + cmp.getInstanceName());
					v.setVersion("1.0");
					ComNode nodeDef = ComponentFactory.eINSTANCE
							.createComNode();
					nodeDef.setParallel(true);
					nodeDef.setVlnv(v2);
					nodeDef.setSpeed(1000000);
					ComponentInstance virtualNode = SlamFactory.eINSTANCE
							.createComponentInstance();
					virtualDelayManager.setInstanceName("virtualNodeTo"
							+ cmp.getInstanceName());
					virtualDelayManager.setComponent(nodeDef);
					architecture.getComponentInstances().add(virtualNode);
					architecture.getComponentHolder().getComponents()
							.add(nodeDef);
					Link c1 = LinkFactory.eINSTANCE.createDataLink();
					c1.setDirected(false);
					c1.setSourceComponentInstance(virtualNode);
					c1.setDestinationComponentInstance(cmp);
					architecture.getLinks().add(c1);
					Link c2 = LinkFactory.eINSTANCE.createDataLink();
					c2.setDirected(false);
					c2.setSourceComponentInstance(virtualNode);
					c2.setDestinationComponentInstance(virtualDelayManager);
					architecture.getLinks().add(c2);
				}
			}
		}

		super.execute(inputs, parameters, monitor, nodeName, workflow);

		AbcParameters abcParameters = new AbcParameters(parameters);

		MapperDAG dag = SdfToDagConverter.convert(algorithm, architecture,
				scenario, false);

		// Repeating the graph to simulate several calls.
		if (iterationNr != 0) {
			WorkflowLogger.getLogger().log(
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
				Timing timing = new Timing(virtualDelayManager.getComponent()
						.getVlnv().getName(), sdfV.getName(), iterationPeriod);
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

		WorkflowLogger.getLogger().log(Level.INFO, "Dynamic Scheduling");

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

		WorkflowLogger.getLogger().log(Level.INFO, "End of Dynamic Scheduling");

		return outputs;
	}

}

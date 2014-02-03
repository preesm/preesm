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

package org.ietr.preesm.mapper.graphtransfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.dftools.algorithm.demo.SDFAdapterDemo;
import org.ietr.dftools.algorithm.demo.SDFtoDAGDemo;
import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.visitors.DAGTransformation;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.component.Operator;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.RelativeConstraintManager;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.mapper.abc.SpecialVertexManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.MapperEdgeFactory;
import org.ietr.preesm.mapper.model.MapperVertexFactory;
import org.ietr.preesm.mapper.model.property.EdgeInit;
import org.ietr.preesm.mapper.model.property.VertexInit;
import org.ietr.preesm.mapper.model.special.TransferVertex;
import org.ietr.preesm.mapper.tools.TopologicalDAGIterator;

/**
 * Uses the SDF4J library to convert the input SDF into a DAG before scheduling.
 * 
 * @author mpelcat
 */
public class SdfToDagConverter {

	/**
	 * Converts a SDF in a DAG and retrieves the interesting properties from the
	 * SDF.
	 * 
	 * @author mpelcat
	 */
	public static MapperDAG convert(SDFGraph sdfIn, Design architecture,
			PreesmScenario scenario, boolean display) {

		WorkflowLogger.getLogger().log(Level.INFO,
				"Converting from SDF to DAG.");

		try {

			if (!sdfIn.validateModel(WorkflowLogger.getLogger())) {
				return null;
			}
		} catch (SDF4JException e) {
			WorkflowLogger.getLogger().log(Level.SEVERE, e.getMessage());
			return null;
		}
		SDFGraph sdf = sdfIn.clone();
		// Generates a dag
		MapperDAG dag = new MapperDAG(new MapperEdgeFactory(), sdf);

		// Creates a visitor parameterized with the DAG
		DAGTransformation<MapperDAG> visitor = new DAGTransformation<MapperDAG>(
				dag, MapperVertexFactory.getInstance());

		// visits the SDF to generate the DAG
		try {
			sdf.accept(visitor);
		} catch (SDF4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WorkflowLogger.getLogger().log(Level.SEVERE, e.getMessage());
		}

		// Adds the necessary properties to vertices and edges
		addInitialProperties(dag, architecture, scenario);

		// Displays the DAG
		if (display) {
			SDFAdapterDemo applet1 = new SDFAdapterDemo();
			applet1.init(sdf);
			SDFtoDAGDemo applet2 = new SDFtoDAGDemo();
			applet2.init(dag);
		}

		if (dag.vertexSet().size() == 0) {
			WorkflowLogger.getLogger().log(Level.SEVERE,
					"Can not map a DAG with no vertex.");
		} else {
			WorkflowLogger.getLogger().log(Level.INFO, "Conversion finished.");
			WorkflowLogger.getLogger().log(
					Level.INFO,
					"mapping a DAG with " + dag.vertexSet().size()
							+ " vertices and " + dag.edgeSet().size()
							+ " edges.");
		}

		return dag;
	}

	/**
	 * Retrieves the constraints and adds them to the DAG initial properties
	 * 
	 * @return The DAG with initial properties
	 */
	public static MapperDAG addInitialProperties(MapperDAG dag,
			Design architecture, PreesmScenario scenario) {

		addInitialVertexProperties(dag, architecture, scenario);
		addInitialEdgeProperties(dag, architecture, scenario);
		addInitialSpecialVertexProperties(dag, architecture, scenario);
		addInitialConstraintsProperties(dag, architecture, scenario);
		addInitialRelativeConstraintsProperties(dag, architecture, scenario);
		addInitialTimingProperties(dag, architecture, scenario);
		return null;
	}

	/**
	 * Retrieves the relative constraints and adds them to the DAG initial properties.
	 * It consists in sharing between vertices information stored in VertexMapping objects.
	 */
	public static void addInitialRelativeConstraintsProperties(MapperDAG dag,
			Design architecture, PreesmScenario scenario) {
		
		// Initial relative constraints are stored in the scenario
		RelativeConstraintManager manager =  scenario.getRelativeconstraintManager();
		Map<Integer,Set<MapperDAGVertex>> relativeConstraints = new HashMap<Integer,Set<MapperDAGVertex>>();
		
		// We iterate the dag to add to each vertex its relative constraints (if any)
		for(DAGVertex dv : dag.vertexSet()){
			String sdfVName = dv.getCorrespondingSDFVertex().getId();
			
			// If a group was found, the actor is associated to a group with other actors
			if(manager.hasRelativeConstraint(sdfVName)){
				
				// We get the group ID if any was set in the scenario
				int group = manager.getConstraintOrDefault(sdfVName);
				
				if(!relativeConstraints.containsKey(group)){
					relativeConstraints.put(group, new HashSet<MapperDAGVertex>());
				}
				relativeConstraints.get(group).add((MapperDAGVertex)dv);
			}
			// otherwise, the actor has no relative constraint
			else{
				dag.getMappings().dedicate((MapperDAGVertex)dv);
			}
		}
		
		// A DAGMappings object is stored in a DAG. It stores relative constraints
		// between vertices of the DAG.
		// We associate here vertices that will share mapping constraints
		for(int group : relativeConstraints.keySet()){
			dag.getMappings().associate(relativeConstraints.get(group));
		}
	}

	/**
	 * Sets timing dependencies between vertices
	 */
	public static void addInitialTimingProperties(MapperDAG dag,
			Design architecture, PreesmScenario scenario) {
		
		// New timing objects are created they are not initialized here
		for(DAGVertex v : dag.vertexSet()){
			dag.getTimings().dedicate((MapperDAGVertex)v);
		}
	}
	
	private static int getVertexInputBuffersSize(MapperDAGVertex v) {
		int inputDataSize = 0;

		for (DAGEdge e : v.incomingEdges()) {
			MapperDAGEdge me = (MapperDAGEdge) e;
			if (!(me.getSource() instanceof TransferVertex)) {
				inputDataSize += me.getInit().getDataSize();
			}
		}

		return inputDataSize;
	}

	private static int getVertexOutputBuffersSize(MapperDAGVertex v) {
		int outputDataSize = 0;

		for (DAGEdge e : v.outgoingEdges()) {
			MapperDAGEdge me = (MapperDAGEdge) e;
			if (!(me.getTarget() instanceof TransferVertex)) {
				outputDataSize += me.getInit().getDataSize();
			}
		}

		return outputDataSize;

	}
	
	/**
	 * Retrieves the vertex timings and adds them to the DAG initial properties
	 * 
	 * @return The DAG with initial properties
	 */
	public static void addInitialVertexProperties(MapperDAG dag,
			Design architecture, PreesmScenario scenario) {

		/**
		 * Importing default timings
		 */
		// Iterating over dag vertices
		TopologicalDAGIterator dagiterator = new TopologicalDAGIterator(dag);

		while (dagiterator.hasNext()) {
			MapperDAGVertex currentVertex = (MapperDAGVertex) dagiterator
					.next();
			VertexInit currentVertexInit = currentVertex
					.getInit();

			// Setting repetition number
			int nbRepeat = currentVertex.getNbRepeat().intValue();
			currentVertexInit.setNbRepeat(nbRepeat);

			// The SDF vertex id is used to reference the timings
			List<Timing> timelist = scenario.getTimingManager()
					.getGraphTimings(currentVertex,
							DesignTools.getOperatorComponentIds(architecture));

			// Iterating over timings for each DAG vertex
			Iterator<Timing> listiterator = timelist.iterator();

			// Special vertices time computation is delayed until edge sizes are initialized
			if(SpecialVertexManager.isSpecial(currentVertex)){
			}
			// If there is no time defined
			else if (timelist.size() != 0) {
				while (listiterator.hasNext()) {
					Timing timing = listiterator.next();
					
					// Importing timings from scenario
					currentVertexInit.addTiming(timing);
				}
			// If the vertex is not special and has no defined timings
			} else {
				// Default timings are given
				for (ComponentInstance op : DesignTools
						.getOperatorInstances(architecture)) {
					Timing time = new Timing(op.getComponent().getVlnv()
							.getName(), currentVertex
							.getCorrespondingSDFVertex().getId(), Timing.DEFAULT_TASK_TIME);
					currentVertexInit.addTiming(time);
				}
			}

		}
	}

	/**
	 * Retrieves the vertex timings and adds them to the DAG initial properties
	 * 
	 * @return The DAG with initial properties
	 */
	public static void addInitialSpecialVertexProperties(MapperDAG dag,
			Design architecture, PreesmScenario scenario) {

		// Iterating over dag vertices
		TopologicalDAGIterator dagiterator = new TopologicalDAGIterator(dag);

		while (dagiterator.hasNext()) {
			MapperDAGVertex currentVertex = (MapperDAGVertex) dagiterator
					.next();

			// Special vertices have timings computed from data copy speed
			if(SpecialVertexManager.isSpecial(currentVertex)){
				VertexInit currentVertexInit = currentVertex
						.getInit();
				
				for(String opDef : scenario.getTimingManager().getMemcpySpeeds().keySet()){
					long sut = scenario.getTimingManager().getMemcpySetupTime(opDef);
					float tpu = scenario.getTimingManager().getMemcpyTimePerUnit(opDef);
					Timing timing = new Timing(opDef, currentVertex
							.getCorrespondingSDFVertex().getId());

					// Depending on the type of vertex, time is given by the size of output or input buffers
					if(SpecialVertexManager.isFork(currentVertex) || SpecialVertexManager.isJoin(currentVertex) || SpecialVertexManager.isEnd(currentVertex)){
						timing.setTime(sut + (long)(tpu * getVertexInputBuffersSize(currentVertex)));
					}
					else if(SpecialVertexManager.isBroadCast(currentVertex)|| SpecialVertexManager.isInit(currentVertex)){
						timing.setTime(sut + (long)(tpu * getVertexOutputBuffersSize(currentVertex)));
					}
					
					currentVertexInit.addTiming(timing);
				}
			}
		}
	}
	
	/**
	 * Retrieves the edge weights and adds them to the DAG initial properties
	 * 
	 * @return The DAG with initial properties
	 */
	@SuppressWarnings("unchecked")
	public static void addInitialEdgeProperties(MapperDAG dag,
			Design architecture, PreesmScenario scenario) {

		/**
		 * Importing data edge weights and multiplying by type size when
		 * available
		 */
		Iterator<DAGEdge> edgeiterator = dag.edgeSet().iterator();

		while (edgeiterator.hasNext()) {
			MapperDAGEdge currentEdge = (MapperDAGEdge) edgeiterator.next();
			EdgeInit currentEdgeInit = currentEdge
					.getInit();

			int weight = 0;

			// Calculating the edge weight for simulation:
			// edge production * number of source repetitions * type size
			for (AbstractEdge<SDFGraph, SDFAbstractVertex> aggMember : currentEdge
					.getAggregate()) {
				SDFEdge sdfAggMember = (SDFEdge) aggMember;
				int prod;
				try {
					prod = sdfAggMember.getProd().intValue();
				} catch (InvalidExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					prod = 1;
				}
				int nbRepeat = currentEdge.getSource().getNbRepeat().intValue();
				int typeSize = scenario.getSimulationManager()
						.getDataTypeSizeOrDefault(
								sdfAggMember.getDataType().toString());
				weight += prod * nbRepeat * typeSize;
			}

			currentEdgeInit.setDataSize(weight);

		}
	}

	/**
	 * Retrieves the constraints and adds them to the DAG initial properties.
	 * Also imports timings
	 */
	public static void addInitialConstraintsProperties(MapperDAG dag,
			Design architecture, PreesmScenario scenario) {
		/**
		 * Importing scenario: Only the timings corresponding to allowed
		 * mappings are set.
		 */

		// Iterating over constraint groups
		Iterator<ConstraintGroup> cgit = scenario.getConstraintGroupManager()
				.getConstraintGroups().iterator();

		while (cgit.hasNext()) {
			ConstraintGroup cg = cgit.next();

			// Iterating over vertices in DAG with their SDF ref in the
			// constraint group
			Set<String> sdfVertexIds = cg.getVertexPaths();

			for (DAGVertex v : dag.vertexSet()) {
				MapperDAGVertex mv = (MapperDAGVertex) v;
				if (sdfVertexIds.contains(mv.getCorrespondingSDFVertex()
						.getInfo())) {

					for (String opId : cg.getOperatorIds()) {
						ComponentInstance currentIOp = DesignTools
								.getComponentInstance(architecture, opId);
						if (currentIOp.getComponent() instanceof Operator) {

							if (!mv.getInit().isMapable(
									currentIOp)) {

								mv.getInit().addOperator(
										currentIOp);

								// Initializes a default timing that may be erased
								// when timings are imported
								Timing newTiming = new Timing(currentIOp
										.getComponent().getVlnv().getName(),
										mv.getName());
								mv.getInit().addTiming(
										newTiming);
							}

						}
					}
				}
			}
		}

		/*
		 * Special type vertices are first enabled on any core set in scenario
		 * to execute them
		 */
		TopologicalDAGIterator it = new TopologicalDAGIterator(dag);
		List<DAGVertex> vList = new ArrayList<DAGVertex>();
		Set<String> specialOpIds = scenario.getSimulationManager()
				.getSpecialVertexOperatorIds();

		while (it.hasNext()) {
			MapperDAGVertex v = (MapperDAGVertex) it.next();
			if (SpecialVertexManager.isSpecial(v)) {
				vList.add(v);
				for (String id : specialOpIds) {
					ComponentInstance o = DesignTools.getComponentInstance(
							architecture, id);
					((MapperDAGVertex) v).getInit()
							.addOperator(o);
				}
			}
		}
	}
}

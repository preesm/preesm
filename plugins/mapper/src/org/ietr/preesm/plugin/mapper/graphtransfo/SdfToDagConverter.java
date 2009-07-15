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

package org.ietr.preesm.plugin.mapper.graphtransfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.IOperator;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.TimingManager;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.SpecialVertexManager;
import org.ietr.preesm.plugin.mapper.model.InitialEdgeProperty;
import org.ietr.preesm.plugin.mapper.model.InitialVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.MapperEdgeFactory;
import org.ietr.preesm.plugin.mapper.model.MapperVertexFactory;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import org.sdf4j.demo.SDFAdapterDemo;
import org.sdf4j.demo.SDFtoDAGDemo;
import org.sdf4j.generator.SDFRandomGraph;
import org.sdf4j.model.AbstractEdge;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.visitors.DAGTransformation;
import org.sdf4j.model.visitors.SDF4JException;

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
	public static MapperDAG convert(SDFGraph sdfIn,
			MultiCoreArchitecture architecture, IScenario scenario,
			boolean display) {

		PreesmLogger.getLogger().log(Level.INFO, "Converting from SDF to DAG.");

		try {

			if (!sdfIn.validateModel(PreesmLogger.getLogger())) {
				return null;
			}
		} catch (SDF4JException e) {
			PreesmLogger.getLogger().log(Level.SEVERE, e.getMessage());
			return null;
		}
		SDFGraph sdf = sdfIn.clone();
		// Generates a dag
		MapperDAG dag = new MapperDAG(new MapperEdgeFactory(), sdf);

		// Creates a visitor parameterized with the DAG
		DAGTransformation<MapperDAG> visitor = new DAGTransformation<MapperDAG>(
				dag, new MapperVertexFactory());

		// visits the SDF to generate the DAG
		try {
			sdf.accept(visitor);
		} catch (SDF4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			PreesmLogger.getLogger().log(Level.SEVERE, e.getMessage());
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
			PreesmLogger.getLogger().log(Level.SEVERE,
					"Can not map a DAG with no vertex.");
		} else {
			PreesmLogger.getLogger().log(Level.INFO, "Conversion finished.");
			PreesmLogger.getLogger().log(
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
			MultiCoreArchitecture architecture, IScenario scenario) {

		addInitialVertexProperties(dag, architecture, scenario);
		addInitialEdgeProperties(dag, architecture, scenario);
		addInitialConstraintsProperties(dag, architecture, scenario);

		return null;
	}

	/**
	 * Retrieves the vertex timings and adds them to the DAG initial properties
	 * 
	 * @return The DAG with initial properties
	 */
	public static void addInitialVertexProperties(MapperDAG dag,
			MultiCoreArchitecture architecture, IScenario scenario) {

		/**
		 * Importing default timings
		 */
		// Iterating over dag vertices
		TopologicalDAGIterator dagiterator = new TopologicalDAGIterator(dag);

		while (dagiterator.hasNext()) {
			MapperDAGVertex currentVertex = (MapperDAGVertex) dagiterator
					.next();
			InitialVertexProperty currentVertexInit = currentVertex
					.getInitialVertexProperty();

			// Setting repetition number
			int nbRepeat = currentVertex.getNbRepeat().intValue();
			currentVertexInit.setNbRepeat(nbRepeat);

			// The SDF vertex id is used to reference the timings
			List<Timing> timelist = scenario.getTimingManager()
					.getGraphTimings(currentVertex, architecture);

			// Iterating over timings for each DAG vertex
			Iterator<Timing> listiterator = timelist.iterator();

			if (timelist.size() != 0) {
				while (listiterator.hasNext()) {
					Timing timing = listiterator.next();
					currentVertexInit.addTiming(timing);
				}
			} else {
				for (ArchitectureComponent op : architecture
						.getComponents(ArchitectureComponentType.operator)) {
					Timing time = new Timing((OperatorDefinition) op
							.getDefinition(), currentVertex
							.getCorrespondingSDFVertex(), 1);
					time.setTime(Timing.DEFAULT_TASK_TIME);
					currentVertexInit.addTiming(time);
				}
			}

		}
	}

	/**
	 * Retrieves the edge weights and adds them to the DAG initial properties
	 * 
	 * @return The DAG with initial properties
	 */
	public static void addInitialEdgeProperties(MapperDAG dag,
			MultiCoreArchitecture architecture, IScenario scenario) {

		/**
		 * Importing data edge weights and multiplying by type size when
		 * available
		 */
		Iterator<DAGEdge> edgeiterator = dag.edgeSet().iterator();

		while (edgeiterator.hasNext()) {
			MapperDAGEdge currentEdge = (MapperDAGEdge) edgeiterator.next();
			InitialEdgeProperty currentEdgeInit = currentEdge
					.getInitialEdgeProperty();

			// Old version using directly the weights set by the SDF4J sdf2dag
			/*
			 * int weight = currentEdge.getWeight().intValue();
			 */
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
	 * Retrieves the constraints and adds them to the DAG initial properties
	 */
	public static void addInitialConstraintsProperties(MapperDAG dag,
			MultiCoreArchitecture architecture, IScenario scenario) {
		/**
		 * Importing scenario: Only the timings corresponding to allowed
		 * mappings are set.
		 */

		// Iterating over constraint groups
		Iterator<ConstraintGroup> cgit = scenario.getConstraintGroupManager()
				.getConstraintGroups().iterator();

		while (cgit.hasNext()) {
			ConstraintGroup cg = cgit.next();

			// Iterating over graphs in constraint groups
			Iterator<SDFAbstractVertex> graphit = cg.getVertices().iterator();

			while (graphit.hasNext()) {
				SDFAbstractVertex currentsdfvertex = graphit.next();

				// Getting all DAG vertices which corresponding SDF graph has
				// the same id
				// as the current sdf vertex.
				Set<MapperDAGVertex> vertexset = dag
						.getVertices(currentsdfvertex);

				// Iterating over DAG vertices corresponding to the same sdf
				// graph
				Iterator<MapperDAGVertex> vertexit = vertexset.iterator();

				while (vertexit.hasNext()) {
					MapperDAGVertex currentvertex = vertexit.next();

					// Iterating over operators in constraint group
					Iterator<IOperator> opit = cg.getOperators().iterator();

					while (opit.hasNext()) {
						IOperator currentIOp = opit.next();
						if (((ArchitectureComponent) currentIOp).getType() == ArchitectureComponentType.operator) {
							Operator currentop = (Operator) currentIOp;

							if (!currentvertex.getInitialVertexProperty()
									.isImplantable(currentop)) {

								currentvertex.getInitialVertexProperty()
										.addOperator(currentop);
								// If no timing is set, we add a unavailable
								// timing
								Timing newTiming = new Timing(
										(OperatorDefinition) currentop
												.getDefinition(),
										currentsdfvertex);
								currentvertex.getInitialVertexProperty()
										.addTiming(newTiming);
							}

						}
					}
				}

			}

		}

		/*
		 * Special type vertices are first enabled on any core set in scenario
		 * to executed them but a refinement of A the constraints is done
		 * depending on their neighbors
		 */
		TopologicalDAGIterator it = new TopologicalDAGIterator(dag);
		List<DAGVertex> vList = new ArrayList<DAGVertex>();
		Set<ArchitectureComponent> specialCmps = scenario.getSimulationManager()
		.getSpecialVertexOperators();
		
		while (it.hasNext()) {
			MapperDAGVertex v = (MapperDAGVertex) it.next();
			if (SpecialVertexManager.isSpecial(v)) {
				vList.add(v);
				for (ArchitectureComponent o : specialCmps) {
					((MapperDAGVertex) v).getInitialVertexProperty()
							.addOperator((Operator) o);
				}
			}
		}

		// Checking predecessors to reduce the possible allocations
		for (DAGVertex v : vList) {
			if (SpecialVertexManager.isBroadCast(v) || SpecialVertexManager.isFork(v)) {
			for (ArchitectureComponent o : specialCmps) {
				((MapperDAGVertex) v).getInitialVertexProperty()
						.removeOperatorIfPredNotImplantable((Operator) o);
			}}
		}

		// Checking successors to reduce the possible allocations
		ListIterator<DAGVertex> vIt = vList.listIterator();
		while (vIt.hasPrevious()) {
			MapperDAGVertex v = (MapperDAGVertex) vIt.previous();
			if (SpecialVertexManager.isJoin(v)) {
			for (ArchitectureComponent o : specialCmps) {
				v.getInitialVertexProperty()
						.removeOperatorIfPredNotImplantable((Operator) o);
			}
			}
		}

		// If no allocation is possible any more, adds all cores from scenario special vertex section and displays a warning
		for (DAGVertex dv : vList) {
			MapperDAGVertex v = (MapperDAGVertex) dv;
			
			if(v.getInitialVertexProperty().getOperatorSet().isEmpty()){
				for (ArchitectureComponent o : specialCmps) {
					((MapperDAGVertex) v).getInitialVertexProperty()
							.addOperator((Operator) o);
				}
				
				PreesmLogger.getLogger().log(Level.WARNING,"the special vertex " + v.getName() + " can not be executed on the same operator as its reference neighbor.");
			}
		}
	}
}

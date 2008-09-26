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

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.constraints.Timing;
import org.ietr.preesm.plugin.mapper.model.InitialEdgeProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.MapperEdgeFactory;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * The DAGCreator converts a DAG described with SDF objects in mapper DAG model
 * 
 * @author pmenuet     
 */
public class DAGCreator {

	public DAGCreator() {
	}

	/**
	 * Kwok example 1 for fast algo
	 */
	public MapperDAG dagexample1(IArchitecture architecture) {

		/* Construct DAG */

		MapperEdgeFactory factory = new MapperEdgeFactory();
		MapperDAG dag = new MapperDAG(factory, new SDFGraph());

		// Exemplestaticplot de KWOK : DAG simple

		// Homogeneous architecture: one timing on the operator definition
		OperatorDefinition opDef = (OperatorDefinition) architecture
				.getMainOperator().getDefinition();

		MapperDAGVertex num1 = new MapperDAGVertex("n1", dag);
		num1.getInitialVertexProperty().addTiming(new Timing(opDef, null, 2));

		dag.addVertex(num1);

		MapperDAGVertex num2 = new MapperDAGVertex("n2", dag);
		num2.getInitialVertexProperty().addTiming(new Timing(opDef, null, 3));

		dag.addVertex(num2);

		MapperDAGVertex num3 = new MapperDAGVertex("n3", dag);
		num3.getInitialVertexProperty().addTiming(new Timing(opDef, null, 3));

		dag.addVertex(num3);

		MapperDAGVertex num4 = new MapperDAGVertex("n4", dag);
		num4.getInitialVertexProperty().addTiming(new Timing(opDef, null, 4));

		dag.addVertex(num4);

		MapperDAGVertex num5 = new MapperDAGVertex("n5", dag);
		num5.getInitialVertexProperty().addTiming(new Timing(opDef, null, 5));

		dag.addVertex(num5);

		MapperDAGVertex num6 = new MapperDAGVertex("n6", dag);
		num6.getInitialVertexProperty().addTiming(new Timing(opDef, null, 4));

		dag.addVertex(num6);

		MapperDAGVertex num7 = new MapperDAGVertex("n7", dag);
		num7.getInitialVertexProperty().addTiming(new Timing(opDef, null, 4));

		dag.addVertex(num7);

		MapperDAGVertex num8 = new MapperDAGVertex("n8", dag);
		num8.getInitialVertexProperty().addTiming(new Timing(opDef, null, 4));

		dag.addVertex(num8);

		MapperDAGVertex num9 = new MapperDAGVertex("n9", dag);
		num9.getInitialVertexProperty().addTiming(new Timing(opDef, null, 1));

		dag.addVertex(num9);

		try {
			MapperDAGEdge edge = (MapperDAGEdge) dag.addEdge(num1,
					num2);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(4));

			edge = (MapperDAGEdge) dag.addEdge(num1, num3);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));

			edge = (MapperDAGEdge) dag.addEdge(num1, num4);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));

			edge = (MapperDAGEdge) dag.addEdge(num1, num5);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));

			edge = (MapperDAGEdge) dag.addEdge(num2, num6);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));

			edge = (MapperDAGEdge) dag.addEdge(num2, num7);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));

			edge = (MapperDAGEdge) dag.addEdge(num1, num7);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(10));

			edge = (MapperDAGEdge) dag.addEdge(num3, num8);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));

			edge = (MapperDAGEdge) dag.addEdge(num4, num8);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));

			edge = (MapperDAGEdge) dag.addEdge(num6, num9);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(5));

			edge = (MapperDAGEdge) dag.addEdge(num7, num9);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(6));

			edge = (MapperDAGEdge) dag.addEdge(num8, num9);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(5));

		} catch (Exception e) {
			System.out.print(e.getMessage());
		}

		return dag;
	}

	/**
	 * Kwok example 2 for fast algo
	 */
	public MapperDAG dagexample2(IArchitecture architecture) {

		/* Retrieve constraints */
		/* Construct DAG */

		MapperEdgeFactory factory = new MapperEdgeFactory();
		MapperDAG dag = new MapperDAG(factory, new SDFGraph());

		// Exemplestaticplot de KWOK : DAG simple

		// Homogeneous architecture: one timing on the operator definition
		OperatorDefinition opDef = (OperatorDefinition) architecture
				.getMainOperator().getDefinition();

		MapperDAGVertex num1 = new MapperDAGVertex("n1", dag);
		num1.getInitialVertexProperty().addTiming(new Timing(opDef, null, 2));
		dag.addVertex(num1);

		MapperDAGVertex num2 = new MapperDAGVertex("n2", dag);
		num2.getInitialVertexProperty().addTiming(new Timing(opDef, null, 3));
		dag.addVertex(num2);

		MapperDAGVertex num3 = new MapperDAGVertex("n3", dag);
		num3.getInitialVertexProperty().addTiming(new Timing(opDef, null, 3));
		dag.addVertex(num3);

		MapperDAGVertex num4 = new MapperDAGVertex("n4", dag);
		num4.getInitialVertexProperty().addTiming(new Timing(opDef, null, 4));
		dag.addVertex(num4);

		MapperDAGVertex num5 = new MapperDAGVertex("n5", dag);
		num5.getInitialVertexProperty().addTiming(new Timing(opDef, null, 5));
		dag.addVertex(num5);

		MapperDAGVertex num6 = new MapperDAGVertex("n6", dag);
		num6.getInitialVertexProperty().addTiming(new Timing(opDef, null, 4));
		dag.addVertex(num6);

		MapperDAGVertex num7 = new MapperDAGVertex("n7", dag);
		num7.getInitialVertexProperty().addTiming(new Timing(opDef, null, 4));
		dag.addVertex(num7);

		MapperDAGVertex num8 = new MapperDAGVertex("n8", dag);
		num8.getInitialVertexProperty().addTiming(new Timing(opDef, null, 4));
		dag.addVertex(num8);

		MapperDAGVertex num9 = new MapperDAGVertex("n9", dag);
		num9.getInitialVertexProperty().addTiming(new Timing(opDef, null, 1));
		dag.addVertex(num9);

		try {
			MapperDAGEdge edge = (MapperDAGEdge) dag.addEdge(num1,
					num2);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(4));

			edge = (MapperDAGEdge) dag.addEdge(num1, num3);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));
			edge = (MapperDAGEdge) dag.addEdge(num1, num4);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));
			edge = (MapperDAGEdge) dag.addEdge(num1, num5);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));
			edge = (MapperDAGEdge) dag.addEdge(num1, num7);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(20));
			edge = (MapperDAGEdge) dag.addEdge(num2, num6);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));
			edge = (MapperDAGEdge) dag.addEdge(num2, num7);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(5));
			edge = (MapperDAGEdge) dag.addEdge(num2, num8);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(5));
			edge = (MapperDAGEdge) dag.addEdge(num3, num8);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));
			edge = (MapperDAGEdge) dag.addEdge(num3, num7);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(5));
			edge = (MapperDAGEdge) dag.addEdge(num4, num8);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(1));
			edge = (MapperDAGEdge) dag.addEdge(num5, num8);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(10));
			edge = (MapperDAGEdge) dag.addEdge(num6, num9);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(10));
			edge = (MapperDAGEdge) dag.addEdge(num7, num9);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(10));
			edge = (MapperDAGEdge) dag.addEdge(num8, num9);
			edge.setInitialEdgeProperty(new InitialEdgeProperty(10));

		} catch (Exception e) {
			System.out.print(e.getMessage());
		}

		return dag;
	}

}

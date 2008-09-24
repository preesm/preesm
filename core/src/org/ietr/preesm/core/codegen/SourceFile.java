/**
 * 
 */
package org.ietr.preesm.core.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Source file to be executed on a given core. A source file contains Buffer
 * declarations and threads.
 * 
 * @author mwipliez
 * @author mpelcat
 */
public class SourceFile extends AbstractBufferContainer {

	/**
	 * File name
	 */
	private String name;
	/**
	 * Operator on which this source file will run
	 */
	private Operator operator;

	/**
	 * Operator on which this source file will run
	 */
	private List<ThreadDeclaration> threads;

	/**
	 * 
	 */
	public SourceFile(String name, Operator operator) {

		super(null);
		this.name = name;
		this.operator = operator;
		threads = new ArrayList<ThreadDeclaration>();
	}

	/**
	 * Accepts a printer visitor
	 */
	public void accept(AbstractPrinter printer) {

		printer.visit(this,0); // Visit self
		super.accept(printer); // Accept the buffer allocation
		printer.visit(this,1); // Visit self

		Iterator<ThreadDeclaration> iterator = threads.iterator();

		while (iterator.hasNext()) {
			ThreadDeclaration thread = iterator.next();

			thread.accept(printer); //Accept the threads
			printer.visit(this,2); // Visit self
		}
		printer.visit(this,3); // Visit self
	}

	public void addThread(ThreadDeclaration thread) {
		threads.add(thread);
	}

	/**
	 * Fills itself from an SDF and an architecture
	 */
	public void generateSource(DirectedAcyclicGraph algorithm, IArchitecture architecture) {

		// Gets the task vertices allocated to the current operator in
		// scheduling order
		SortedSet<DAGVertex> ownTaskVertices = getOwnVertices(algorithm, VertexType.task);

		// Gets the communication vertices allocated to the current operator in
		// scheduling order
		SortedSet<DAGVertex> ownCommunicationVertices = getOwnVertices(algorithm, VertexType.send);
		ownCommunicationVertices.addAll(getOwnVertices(algorithm, VertexType.receive));
		
		// Buffers defined as global variables are retrieved here. They are
		// added globally to the file
		allocateBuffers(ownTaskVertices);
		
		// Allocation of route step buffers
		allocateRouteSteps(ownCommunicationVertices);

		// Creating computation thread in which all SDF function calls will be placed
		ComputationThreadDeclaration computationThread = new ComputationThreadDeclaration(
				this);
		addThread(computationThread);
		
		// Adding all function calls corresponding do computation vertices
		computationThread.addUserFunctionCalls(ownTaskVertices);
		
		// Creating communication where communication processes are launched
		CommunicationThreadDeclaration communicationThread = new CommunicationThreadDeclaration(
				this);
		addThread(communicationThread);

		// Adds the code for send operations
		communicationThread.addSendsAndReceives(ownCommunicationVertices);

		// Adding all function calls corresponding do computation vertices
		computationThread.addSemaphorePends(ownTaskVertices);
		communicationThread.addSemaphores(ownCommunicationVertices);
		
		// Allocates the semaphores globally
		getSemaphoreContainer().allocateSemaphores();
	}

	public String getName() {
		return name;
	}

	/**
	 * Gets every task vertices allocated to the current operator in their scheduling
	 * order
	 */
	public SortedSet<DAGVertex> getOwnVertices(DirectedAcyclicGraph algorithm, VertexType currentType) {

		ConcurrentSkipListSet<DAGVertex> schedule = new ConcurrentSkipListSet<DAGVertex>(
				new SchedulingOrderComparator());
		Iterator<DAGVertex> iterator = algorithm.vertexSet().iterator();

		while (iterator.hasNext()) {
			DAGVertex vertex = iterator.next();

			// retrieving the operator where the vertex is allocated
			Operator vertexOperator = (Operator) vertex.getPropertyBean()
					.getValue(Operator.propertyBeanName);

			// retrieving the type of the vertex
			VertexType vertexType = (VertexType) vertex.getPropertyBean()
					.getValue(VertexType.propertyBeanName);

			// If the vertex is allocated on the current operator, we add it to
			// the set in scheduling order
			if (vertexOperator != null && vertexOperator.equals(operator)
					&& vertexType != null && vertexType.equals(currentType)) {
				schedule.add(vertex);
			}
		}

		return schedule;
	}

	public void removeThread(ThreadDeclaration thread) {
		threads.remove(thread);
	}
	
	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		String code = "";

		code += "\n/////////////\n//File: " + getName() + "\n//////////////\n";

		// Displays buffer allocation
		code += super.toString();

		code += "\n";

		Iterator<ThreadDeclaration> iterator = threads.iterator();

		while (iterator.hasNext()) {
			ThreadDeclaration thread = iterator.next();

			code += thread.toString();

			code += "\n";
		}

		return code;
	}
}

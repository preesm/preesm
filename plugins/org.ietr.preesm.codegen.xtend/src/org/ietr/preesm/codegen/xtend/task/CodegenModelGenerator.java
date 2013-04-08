/**
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
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
 */

package org.ietr.preesm.codegen.xtend.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.dftools.algorithm.iterators.DAGIterator;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.architecture.slam.Design;

import org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.VertexType;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

/**
 * The objective of this class is to generate an intermediate model that will be
 * used to print the generated code. <br>
 * The generation of the intermediate model is based on elements resulting from
 * a workflow execution: an {@link Design architecture}, a scheduled
 * {@link DirectedAcyclicGraph DAG}, a {@link MemoryExclusionGraph Memory
 * Allocation} and a {@link PreesmScenario scenario}. The generated model is
 * composed of objects of the {@link CodegenPackage Codegen EMF model}.
 * 
 * 
 * @author kdesnos
 * 
 */
public class CodegenModelGenerator {
	/**
	 * Targeted {@link Design Architecture} of the code generation
	 */
	private Design archi;

	/**
	 * {@link DirectedAcyclicGraph DAG} used to generate code. This
	 * {@link DirectedAcyclicGraph DAG} must be the result of mapping/scheduling
	 * process.
	 */
	private DirectedAcyclicGraph dag;

	/**
	 * {@link MemoryExclusionGraph MemEx} used to generate code. This
	 * {@link MemoryExclusionGraph MemEx} must be the result of an allocation
	 * process.
	 * 
	 * @see MemoryAllocator
	 */
	private MemoryExclusionGraph memEx;

	/**
	 * {@link PreesmScenario Scenario} at the origin of the call to the
	 * {@link CodegenPrinter Code Generator}.
	 */
	@SuppressWarnings("unused")
	private PreesmScenario scenario;

	/**
	 * This {@link Map} associates each {@link ComponentInstance} to its
	 * corresponding {@link CoreBlock}.
	 */
	protected Map<ComponentInstance, CoreBlock> coreBlocks;

	/**
	 * Constructor of the {@link CodegenModelGenerator}. The constructor
	 * performs verification to ensure that the inputs are valid:
	 * <ul>
	 * <li>The {@link DirectedAcyclicGraph DAG} is scheduled</li>
	 * <li>The {@link DirectedAcyclicGraph DAG} is mapped on the input
	 * {@link Design architecture}</li>
	 * <li>The {@link MemoryExclusionGraph MemEx} is derived from the
	 * {@link DirectedAcyclicGraph DAG}</li>
	 * <li>The {@link MemoryExclusionGraph MemEx} is allocated</li>
	 * </ul>
	 * 
	 * @param archi
	 *            See {@link CodegenPrinter#archi}
	 * @param dag
	 *            See {@link CodegenPrinter#dag}
	 * @param memEx
	 *            See {@link CodegenPrinter#memEx}
	 * @param scenario
	 *            See {@link CodegenPrinter#scenario}
	 * @throws CodegenException
	 *             When one of the previous verification fails.
	 */
	public CodegenModelGenerator(final Design archi,
			final DirectedAcyclicGraph dag, final MemoryExclusionGraph memEx,
			final PreesmScenario scenario) throws CodegenException {
		this.archi = archi;
		this.dag = dag;
		this.memEx = memEx;
		this.scenario = scenario;

		checkInputs(this.archi, this.dag, this.memEx);

		this.coreBlocks = new HashMap<ComponentInstance, CoreBlock>();
	}

	/**
	 * Method to generate the intermediate model of the codegen based on the
	 * {@link Design architecture}, the {@link MemoryExclusionGraph MemEx graph}
	 * , the {@link DirectedAcyclicGraph DAG} and the {@link PreesmScenario
	 * scenario}.
	 * 
	 * @return a set of {@link Block blocks}. Each of these block corresponds to
	 *         a part of the code to generate:
	 *         <ul>
	 *         <li>{@link CoreBlock A block corresponding to the code executed
	 *         by a core}</li>
	 *         <li>{@link ActorBlock A block corresponding to the code of an
	 *         non-flattened hierarchical actor}</li>
	 *         </ul>
	 * @throws CodegenException
	 *             If a vertex has an unknown {@link DAGVertex#getKind() Kind}.
	 */
	public Set<Block> generate() throws CodegenException {
		// 1 - Iterate on the actors of the DAG
		// 1.0 - Identify the core used.
		// 1.1 - Construct the "loop" of each core.
		// 1.2 - The init function of actors is executed on the same core of
		// their first firing.
		// 1.3 - Identify the buffer accessed/owned by each core.

		DAGIterator iter = new DAGIterator(dag);
		while (iter.hasNext()) {

			DAGVertex vert = iter.next();

			// 1.0 - Identify the core used.
			ComponentInstance operator = null;
			CoreBlock operatorBlock = null;
			{
				// This call can not fail as checks were already performed in
				// the constructor
				operator = (ComponentInstance) vert.getPropertyBean().getValue(
						"Operator", ComponentInstance.class);
				// If this is the first time this operator is encountered,
				// Create a Block and store it.
				operatorBlock = coreBlocks.get(operator);
				if (operatorBlock == null) {
					operatorBlock = CodegenFactory.eINSTANCE.createCoreBlock();
					coreBlocks.put(operator, operatorBlock);
				}
			} // end 1.0

			// 1.1 - Construct the "loop" of each core.
			{
				switch (((VertexType) vert.getPropertyBean().getValue(
						"vertexType", VertexType.class)).toString()) {

				case VertexType.TYPE_TASK:
					// Actor (Hierarchical or not) call
					// Fork Join call
					break;

				case VertexType.TYPE_SEND:
					break;

				case VertexType.TYPE_RECEIVE:
					break;
				default:
					throw new CodegenException("Vertex " + vert
							+ " has an unknown kind: " + vert.getKind());

				}
			}
		}
		return null;

	}

	/**
	 * Verification to ensure that the inputs are valid:
	 * <ul>
	 * <li>The {@link DirectedAcyclicGraph DAG} is scheduled</li>
	 * <li>The {@link DirectedAcyclicGraph DAG} is mapped on the input
	 * {@link Design architecture}</li>
	 * <li>The {@link MemoryExclusionGraph MemEx} is derived from the
	 * {@link DirectedAcyclicGraph DAG}</li>
	 * <li>The {@link MemoryExclusionGraph MemEx} is allocated</li>
	 * </ul>
	 * 
	 * @param archi
	 *            See {@link CodegenPrinter#archi}
	 * @param dag
	 *            See {@link CodegenPrinter#dag}
	 * @param memEx
	 *            See {@link CodegenPrinter#memEx}
	 * @throws CodegenException
	 *             When one of the previous verification fails.
	 */
	protected void checkInputs(final Design archi,
			final DirectedAcyclicGraph dag, final MemoryExclusionGraph memEx)
			throws CodegenException {
		// Check that the input DAG is scheduled and Mapped on the targeted
		// architecture
		for (DAGVertex vertex : dag.vertexSet()) {
			ComponentInstance operator = (ComponentInstance) vertex
					.getPropertyBean().getValue("Operator",
							ComponentInstance.class);
			if (operator == null) {
				throw new CodegenException(
						"The DAG Actor "
								+ vertex
								+ " is not mapped on any operator.\n"
								+ " All actors must be mapped before using the code generation.");
			}

			if (!archi.getComponentInstances().contains(operator)) {
				throw new CodegenException("The DAG Actor " + vertex
						+ " is not mapped on an operator " + operator
						+ " that does not belong to the ipnut architecture.");
			}
		}

		for (MemoryExclusionVertex memObj : memEx.vertexSet()) {
			// Check that the MemEx is derived from the Input DAG
			String sourceName = memObj.getSource();
			String sinkName = memObj.getSink();

			if (dag.getVertex(sourceName) == null) {
				throw new CodegenException(
						"MemEx graph memory object ("
								+ memObj
								+ ") refers to a DAG Vertex "
								+ sourceName
								+ " that does not exist in the input DAG.\n"
								+ "Make sure that the MemEx is derived from the input DAG of the codegen.");
			}
			if (dag.getVertex(sinkName) == null) {
				throw new CodegenException(
						"MemEx graph memory object ("
								+ memObj
								+ ") refers to a DAG Vertex "
								+ sinkName
								+ " that does not exist in the input DAG.\n"
								+ "Make sure that the MemEx is derived from the input DAG of the codegen.");
			}
			// If the memObject corresponds to an edge of the DAG
			if (!sinkName.equals(sourceName)) {
				// Check that the edge corresponding to the MemObject exists.
				if (!dag.containsEdge(dag.getVertex(sourceName),
						dag.getVertex(sinkName))) {
					throw new CodegenException(
							"MemEx graph memory object ("
									+ memObj
									+ ") refers to a DAG Edge"
									+ " that does not exist in the input DAG.\n"
									+ "Make sure that the MemEx is derived from the input DAG of the codegen.");
				}
			}

			// Check that the MemEx graph is allocated.
			Integer offset = (Integer) memObj.getPropertyBean()
					.getValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY,
							Integer.class);
			if (offset == null) {
				throw new CodegenException(
						"MemEx graph memory object ("
								+ memObj
								+ ") was not allocated in memory. \n"
								+ "Make sure that the MemEx is processed by an allocation task before entering the codegen.");
			}
		}
	}
}

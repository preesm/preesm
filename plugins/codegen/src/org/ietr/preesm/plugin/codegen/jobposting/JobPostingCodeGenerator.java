/**
 * 
 */
package org.ietr.preesm.plugin.codegen.jobposting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferAllocation;
import org.ietr.preesm.core.codegen.calls.Constant;
import org.ietr.preesm.core.codegen.calls.FunctionArgument;
import org.ietr.preesm.core.codegen.calls.UserFunctionCall;
import org.ietr.preesm.core.codegen.model.CodeGenSDFEdge;
import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.codegen.model.CodeGenSDFReceiveVertex;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.core.codegen.types.CodeSectionType;
import org.ietr.preesm.core.codegen.types.DataType;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.sdf4j.demo.SDFAdapterDemo;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Generating code to address a job posting runtime system. It generates xml
 * data converted in a .h with useful data sizes and a .h with a list of task
 * descriptors
 * 
 * @author mpelcat
 */
public class JobPostingCodeGenerator {

	private CodeGenSDFGraph codeGenSDFGraph;

	/**
	 * If true, the functions that are called are just simulation functions and
	 * not the real functional code. Every job executes then a function named
	 * "simulation".
	 */
	boolean timedSimulation;

	public JobPostingCodeGenerator(CodeGenSDFGraph codeGenSDFGraph,
			PreesmScenario scenario, boolean timedSimulation) {
		super();
		this.codeGenSDFGraph = codeGenSDFGraph;
		this.timedSimulation = timedSimulation;

		// Displays the DAG
		if (true) {
			SDFAdapterDemo applet2 = new SDFAdapterDemo();
			applet2.init(this.codeGenSDFGraph);
		}
	}

	public JobPostingSource generate() {

		// Putting the vertices in scheduling order
		List<SDFAbstractVertex> list = new ArrayList<SDFAbstractVertex>(
				codeGenSDFGraph.vertexSet());
		Collections.sort(list, new Comparator<SDFAbstractVertex>() {

			@Override
			public int compare(SDFAbstractVertex o1, SDFAbstractVertex o2) {
				Integer totalOrdero1 = (Integer) o1.getPropertyBean().getValue(
						ImplementationPropertyNames.Vertex_schedulingOrder);
				Integer totalOrdero2 = (Integer) o2.getPropertyBean().getValue(
						ImplementationPropertyNames.Vertex_schedulingOrder);

				if (totalOrdero1 != null && totalOrdero2 != null) {
					return totalOrdero1 - totalOrdero2;
				}
				return 0;
			}

		});
		JobPostingSource sourceFile = new JobPostingSource();

		DirectedNeighborIndex<SDFAbstractVertex, SDFEdge> neighIndex = new DirectedNeighborIndex<SDFAbstractVertex, SDFEdge>(
				codeGenSDFGraph);

		// Generating buffers from the edges. Send and Receive buffers are
		// ignored as we consider a shared memory job posting system
		for (SDFEdge edge : codeGenSDFGraph.edgeSet()) {
			SDFAbstractVertex source = edge.getSource();
			SDFAbstractVertex target = edge.getTarget();

			if (source instanceof CodeGenSDFReceiveVertex
					|| target instanceof CodeGenSDFReceiveVertex) {
				continue;
			}

			Buffer buf = new Buffer(source.getName(), target.getName(), edge
					.getSourceInterface().getName(), edge.getTargetInterface()
					.getName(), ((CodeGenSDFEdge) edge).getSize(),
					new DataType(edge.getDataType().toString()), edge,
					sourceFile.getGlobalContainer());

			BufferAllocation allocation = new BufferAllocation(buf);
			sourceFile.addBuffer(allocation);

		}

		// Generating the job descriptors
		for (SDFAbstractVertex vertex : list) {
			JobDescriptor desc = null;

			if (vertex.getRefinement() instanceof FunctionCall) {
				desc = new JobDescriptor();

				FunctionCall call = (FunctionCall) vertex.getRefinement();

				// Adding function parameters
				UserFunctionCall userCall = new UserFunctionCall(vertex,
						sourceFile.getGlobalContainer(), CodeSectionType.loop,
						true);
				List<FunctionArgument> params = userCall.getCallParameters();

				for (FunctionArgument param : params) {
					if (param instanceof Constant)
						desc.addConstant((Constant) param);
				}
				for (FunctionArgument param : params) {
					if (param instanceof Buffer)
						desc.addBuffer((Buffer) param);
				}

				// Setting job names
				if (timedSimulation) {
					desc.setFunctionName("simulation");
				} else {
					desc.setFunctionName(call.getFunctionName());
				}
				desc.setVertexName(vertex.getName());

				Integer totalOrder = (Integer) vertex
						.getPropertyBean()
						.getValue(
								ImplementationPropertyNames.Vertex_schedulingOrder);

				// Setting job id
				if (totalOrder != null) {
					desc.setId(totalOrder);
				}

				int taskTime = (Integer) vertex.getPropertyBean().getValue(
						ImplementationPropertyNames.Task_duration);
				desc.setTime(taskTime);

				// Adding predecessors
				for (SDFAbstractVertex pred : neighIndex.predecessorsOf(vertex)) {
					// If the vertex is a receive, go to the source.
					SDFAbstractVertex predecessorToAdd = pred;
					while (!(predecessorToAdd.getRefinement() instanceof FunctionCall)) {
						predecessorToAdd = neighIndex.predecessorListOf(
								predecessorToAdd).get(0);
					}
					desc.addPredecessor(sourceFile
							.getJobDescriptorByVertexName(predecessorToAdd
									.getName()));
				}
			}

			if (desc != null) {
				sourceFile.addDescriptor(desc);
			}
		}

		return sourceFile;
	}
}

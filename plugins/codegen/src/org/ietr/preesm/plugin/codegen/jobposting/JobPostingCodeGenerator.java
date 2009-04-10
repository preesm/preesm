/**
 * 
 */
package org.ietr.preesm.plugin.codegen.jobposting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ietr.preesm.core.codegen.DataType;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferAllocation;
import org.ietr.preesm.core.codegen.model.CodeGenSDFEdge;
import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSendVertex;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.core.scenario.IScenario;
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
	private IScenario scenario;

	public JobPostingCodeGenerator(CodeGenSDFGraph codeGenSDFGraph,
			IScenario scenario) {
		super();
		this.codeGenSDFGraph = codeGenSDFGraph;
		this.scenario = scenario;
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
		JobPostingSource source = new JobPostingSource();

		// Generating buffers from the edges
		/*
		 * for (SDFEdge edge : codeGenSDFGraph.edgeSet()) {
		 * 
		 * if (!(edge.getSource() instanceof CodeGenSDFSendVertex) &&
		 * !(edge.getTarget() instanceof CodeGenSDFSendVertex)) {
		 * 
		 * Buffer buf = new Buffer(edge.getSource().getName(), edge
		 * .getTarget().getName(), edge.getSourceInterface() .getName(),
		 * edge.getTargetInterface().getName(), ((CodeGenSDFEdge)
		 * edge).getSize(), new DataType(edge .getDataType().toString()), edge,
		 * source .getGlobalContainer());
		 * 
		 * BufferAllocation allocation = new BufferAllocation(buf);
		 * source.addBuffer(allocation); } }
		 */

		// Generating the job descriptors
		for (SDFAbstractVertex vertex : list) {
			JobDescriptor desc = null;

			if (vertex.getRefinement() instanceof FunctionCall) {
				desc = new JobDescriptor();

				FunctionCall call = (FunctionCall) vertex.getRefinement();
				desc.setFunctionName(call.getFunctionName());
				desc.setVertexName(vertex.getName());

				Integer totalOrder = (Integer) vertex
						.getPropertyBean()
						.getValue(
								ImplementationPropertyNames.Vertex_schedulingOrder);
				if (totalOrder != null) {
					desc.setId(totalOrder);
				}
			}

			if (desc != null) {
				source.addDescriptor(desc);
			}
		}

		return source;
	}
}

package org.ietr.preesm.core.codegen.factories;

import java.util.HashMap;
import java.util.Map;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.calls.Constant;
import org.ietr.preesm.core.codegen.calls.FunctionArgument;
import org.ietr.preesm.core.codegen.calls.PointerOn;
import org.ietr.preesm.core.codegen.calls.Variable;
import org.ietr.preesm.core.codegen.model.CodeGenArgument;
import org.ietr.preesm.core.codegen.model.CodeGenParameter;
import org.ietr.preesm.core.codegen.model.CodeGenSDFInitVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSubInitVertex;
import org.ietr.preesm.core.codegen.types.DataType;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.parameters.NoIntegerValueException;
import net.sf.dftools.algorithm.model.psdf.parameters.PSDFDynamicArgument;
import net.sf.dftools.algorithm.model.psdf.parameters.PSDFDynamicParameter;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;

public class FunctionArgumentFactory {

	public static FunctionArgument createFunctionArgument(CodeGenArgument arg,
			SDFAbstractVertex vertex, AbstractBufferContainer parentContainer) {
		Map<SDFEdge, Buffer> candidateBuffers = retrieveBuffersFromEdges(
				vertex, parentContainer);
		FunctionArgument currentParam = null;
		String argName = arg.getName();
		if (arg.getDirection() == CodeGenArgument.INPUT) {
			for (SDFEdge link : candidateBuffers.keySet()) {
				String port = link.getTargetInterface().getName();

				if (port.equals(arg.getName())) {
					if (link.getTarget().equals(vertex)) {
						currentParam = candidateBuffers.get(link);
					}
				}
			}
		} else if (arg.getDirection() == CodeGenArgument.OUTPUT) {
			for (SDFEdge link : candidateBuffers.keySet()) {
				String port = link.getSourceInterface().getName();
				if (port.equals(arg.getName())
						&& link.getSource().equals(vertex)) {
					currentParam = candidateBuffers.get(link);
				}
			}
		}

		// If no buffer was found with the given port name, a
		// parameter is sought
		if (currentParam == null && arg.getDirection() == CodeGenArgument.INPUT) {
			if (vertex.getArgument(argName) != null) {
				try {
					currentParam = new Constant(argName, vertex.getArgument(
							argName).intValue());
				} catch (InvalidExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoIntegerValueException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (currentParam == null
				&& arg.getDirection() == CodeGenArgument.OUTPUT) {
			if (vertex.getArgument(argName) != null) {
				try {
					currentParam = new Constant(argName, vertex.getArgument(
							argName).intValue());
				} catch (InvalidExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoIntegerValueException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (vertex instanceof CodeGenSDFSubInitVertex) {
				if (((CodeGenSDFSubInitVertex) vertex)
						.getAffectedParameter(argName) != null) {
					currentParam = new Variable(argName, new DataType("long"));
				}
			}
		}

		return currentParam;
	}

	public static FunctionArgument createFunctionArgument(
			CodeGenParameter param, SDFAbstractVertex vertex,
			AbstractBufferContainer parentContainer) {
		FunctionArgument currentParam;
		if (vertex.getArgument(param.getName()) != null) {
			try {
				if (vertex.getArgument(param.getName()) instanceof PSDFDynamicArgument) {
					currentParam = parentContainer.getVariable(vertex
							.getArgument(param.getName()).getValue());
				} else if (vertex.getBase().getParameter(
						vertex.getArgument(param.getName()).getValue()) instanceof PSDFDynamicParameter) {
					currentParam = parentContainer.getVariable(vertex
							.getArgument(param.getName()).getObjectValue()
							.getValue());
				} else {
					if (vertex.getBase().getParameter(
							vertex.getArgument(param.getName()).getValue()) != null) {
						String valueName = vertex.getArgument(param.getName())
								.getValue();
						if (vertex.getBase().getParentVertex()
								.getArgument(valueName) instanceof PSDFDynamicArgument) {
							currentParam = parentContainer.getVariable(vertex
									.getBase().getParentVertex()
									.getArgument(valueName).getValue());
						}
					}
					try {
						currentParam = new Constant(param.getName(), vertex
								.getArgument(param.getName()).intValue());
					} catch (NoIntegerValueException e) {
						currentParam = new Constant(param.getName(), vertex
								.getArgument(param.getName()).getObjectValue()
								.toString());
					}
				}
				if (param.isOutput()) {
					currentParam = new PointerOn(currentParam);
				}
				return currentParam;
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		if (vertex instanceof CodeGenSDFInitVertex) {
			CodeGenSDFInitVertex initVertex = (CodeGenSDFInitVertex) vertex;
			if (initVertex.getAffectedParameter(param.getName()) != null) {
				currentParam = parentContainer.getVariable(param.getName());
				if (param.isOutput()) {
					currentParam = new PointerOn(currentParam);
				}
				return currentParam;
			}
		} else if (vertex instanceof CodeGenSDFSubInitVertex) {
			CodeGenSDFSubInitVertex initVertex = (CodeGenSDFSubInitVertex) vertex;
			if (initVertex.getAffectedParameter(param.getName()) != null) {
				currentParam = parentContainer.getVariable(param.getName());
				if (param.isOutput()) {
					currentParam = new PointerOn(currentParam);
				}
				return currentParam;
			}
		}
		return null;
	}

	/**
	 * Retrieves the buffers possibly used by the function call from the buffer
	 * container. If send and receive are ignored, we consider the input edge of
	 * the send vertex to be the one used as a reference for the buffer.
	 */
	private static Map<SDFEdge, Buffer> retrieveBuffersFromEdges(
			SDFAbstractVertex vertex, AbstractBufferContainer parentContainer) {

		Map<SDFEdge, Buffer> candidateBuffers = new HashMap<SDFEdge, Buffer>();
		// Adding output buffers
		for (SDFEdge edge : ((SDFGraph) vertex.getBase())
				.outgoingEdgesOf(vertex)) {
			AbstractBufferContainer parentBufferContainer = parentContainer;
			while (parentBufferContainer != null
					&& parentBufferContainer.getBuffer(edge) == null) {
				parentBufferContainer = parentBufferContainer
						.getParentContainer();
			}
			if (parentBufferContainer != null) {
				candidateBuffers.put(edge,
						parentBufferContainer.getBuffer(edge));
			}
		}

		// Adding input buffers
		for (SDFEdge edge : ((SDFGraph) vertex.getBase())
				.incomingEdgesOf(vertex)) {
			AbstractBufferContainer parentBufferContainer = parentContainer;
			while (parentBufferContainer != null
					&& parentBufferContainer.getBuffer(edge) == null) {
				parentBufferContainer = parentBufferContainer
						.getParentContainer();
			}
			if (parentBufferContainer != null) {
				candidateBuffers.put(edge,
						parentBufferContainer.getBuffer(edge));
			}
		}

		return candidateBuffers;
	}
}

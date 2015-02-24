/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-B
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
knowledge of the CeCILL-B license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.codegen.model.factories;

import java.util.HashMap;
import java.util.Map;

import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.parameters.NoIntegerValueException;
import org.ietr.dftools.algorithm.model.psdf.parameters.PSDFDynamicArgument;
import org.ietr.dftools.algorithm.model.psdf.parameters.PSDFDynamicParameter;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;
import org.ietr.preesm.codegen.model.CodeGenSDFInitVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFSubInitVertex;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.call.Constant;
import org.ietr.preesm.codegen.model.call.FunctionArgument;
import org.ietr.preesm.codegen.model.call.PointerOn;
import org.ietr.preesm.codegen.model.call.Variable;
import org.ietr.preesm.core.types.DataType;

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
				} catch (InvalidExpressionException | NoIntegerValueException e) {
					e.printStackTrace();
				}
			}
		} else if (currentParam == null
				&& arg.getDirection() == CodeGenArgument.OUTPUT) {
			if (vertex.getArgument(argName) != null) {
				try {
					currentParam = new Constant(argName, vertex.getArgument(
							argName).intValue());
				} catch (InvalidExpressionException | NoIntegerValueException e) {
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

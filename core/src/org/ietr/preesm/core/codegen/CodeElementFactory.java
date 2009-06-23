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

package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferAtIndex;
import org.ietr.preesm.core.codegen.buffer.SubBuffer;
import org.ietr.preesm.core.codegen.buffer.SubBufferAllocation;
import org.ietr.preesm.core.codegen.expression.BinaryExpression;
import org.ietr.preesm.core.codegen.expression.ConstantValue;
import org.ietr.preesm.core.codegen.expression.IExpression;
import org.ietr.preesm.core.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFInitVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSendVertex;
import org.ietr.preesm.core.codegen.model.ICodeGenSDFVertex;
import org.sdf4j.SDFMath;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Creating code elements from a vertex
 * 
 * @author jpiat
 * @author mpelcat
 */
public class CodeElementFactory {

	/**
	 * Create an element considering its type
	 * 
	 * @param name
	 *            The name of the code element to be created
	 * @param parentContainer
	 *            The parent container of the code element
	 * @param vertex
	 *            The vertex corresponding to the code element
	 * @return The created code element, null if failed to create the code
	 *         element
	 */
	public static ICodeElement createElement(String name,
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex) {
		try {
			if (vertex.getNbRepeat() > 1) {
				FiniteForLoop loop = new FiniteForLoop(parentContainer,
						(ICodeGenSDFVertex) vertex);
				return loop;
			} else if (vertex instanceof CodeGenSDFBroadcastVertex) {
				return createBroadcast((CodeGenSDFBroadcastVertex) vertex,
						parentContainer);
			} else if (vertex instanceof CodeGenSDFForkVertex) {
				return createExplode((CodeGenSDFForkVertex) vertex,
						parentContainer);
			} else if (vertex instanceof CodeGenSDFJoinVertex) {
				return createImplode((CodeGenSDFJoinVertex) vertex,
						parentContainer);
			} else if (vertex instanceof CodeGenSDFInitVertex) {
				if (vertex.getBase().outgoingEdgesOf(vertex).size() > 0) {
					SDFEdge initEdge = (SDFEdge) vertex.getBase()
							.outgoingEdgesOf(vertex).toArray()[0];
					UserFunctionCall initCall = new UserFunctionCall("init_"
							+ initEdge.getTargetInterface().getName(),
							parentContainer);
					initCall.addParameter(parentContainer.getBuffer(initEdge));
					try {
						initCall.addParameter(new Constant("init_size",
								initEdge.getProd().intValue()));
					} catch (InvalidExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					initCall.setCorrespondingVertex(vertex);
					return initCall;
				} else if (vertex.getBase().incomingEdgesOf(vertex).size() > 0) {
					SDFEdge initEdge = (SDFEdge) vertex.getBase()
							.incomingEdgesOf(vertex).toArray()[0];
					UserFunctionCall initCall = new UserFunctionCall("init_"
							+ initEdge.getTargetInterface().getName(),
							parentContainer);
					initCall.addParameter(parentContainer.getBuffer(initEdge));
					try {
						initCall.addParameter(new Constant("init_size",
								initEdge.getProd().intValue()));
					} catch (InvalidExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					initCall.setCorrespondingVertex(vertex);
					return initCall;
				}
				return null;
			} else if (vertex instanceof CodeGenSDFRoundBufferVertex) {
				return createRoundBuffer((CodeGenSDFRoundBufferVertex) vertex,
						parentContainer);
			} else if (vertex instanceof ICodeGenSDFVertex
					&& vertex.getGraphDescription() == null) {
				UserFunctionCall call = new UserFunctionCall(vertex,
						parentContainer, CodeSectionType.loop, false);
				if (call.getName() == null) {
					return null;
				}
				return call;
			} else {
				CompoundCodeElement compound = new CompoundCodeElement(name,
						parentContainer, (ICodeGenSDFVertex) vertex);
				return compound;
			}
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ICodeElement createBroadcast(
			CodeGenSDFBroadcastVertex vertex,
			AbstractBufferContainer parentContainer) {
		SDFEdge incomingEdge = null;
		CompoundCodeElement container = new CompoundCodeElement(vertex
				.getName(), parentContainer);
		container.setCorrespondingVertex(vertex);
		for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
			incomingEdge = inEdge;
		}
		for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
			try {
				if (outEdge.getProd().intValue() == incomingEdge.getCons()
						.intValue()) {
					if (outEdge.getTarget() instanceof CodeGenSDFSendVertex) {
						UserFunctionCall copyCall = new UserFunctionCall(
								"memcpy", parentContainer);
						copyCall.addParameter(parentContainer
								.getBuffer(outEdge));
						copyCall.addParameter(parentContainer
								.getBuffer(incomingEdge));
						try {
							copyCall.addParameter(new Constant("size",
									incomingEdge.getCons().intValue()
											+ "*sizeof("
											+ incomingEdge.getDataType()
													.toString() + ")"));
						} catch (InvalidExpressionException e) {
							copyCall.addParameter(new Constant("size", 0));
						}
						container.addCall(copyCall);
					} else {
						Buffer outBuff = parentContainer.getBuffer(outEdge);
						outBuff.setSize(0);
						Assignment ass = new Assignment(outBuff, "&"
								+ parentContainer.getBuffer(incomingEdge)
										.getName() + "[0]");
						container.addCall(ass);

					}
				} else if (outEdge.getProd().intValue() > incomingEdge
						.getCons().intValue()) {
					int minToken = SDFMath.gcd(outEdge.getProd().intValue(),
							incomingEdge.getCons().intValue());
					for (int i = 0; i < outEdge.getProd().intValue() / minToken; i++) {
						UserFunctionCall copyCall = new UserFunctionCall(
								"memcpy", parentContainer);
						copyCall.addParameter(new BufferAtIndex(new ConstantValue(i*minToken), parentContainer
								.getBuffer(outEdge)));
						copyCall.addParameter(parentContainer
								.getBuffer(incomingEdge));
						copyCall.addParameter(new Constant("size", minToken
								+ "*sizeof("
								+ incomingEdge.getDataType().toString() + ")"));
						container.addCall(copyCall);
					}
				}
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return container;
	}

	public static ICodeElement createExplode(CodeGenSDFForkVertex vertex,
			AbstractBufferContainer parentContainer) {
		SDFEdge incomingEdge = null;
		CompoundCodeElement container = new CompoundCodeElement(vertex
				.getName(), parentContainer);
		container.setCorrespondingVertex(vertex);
		for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
			incomingEdge = inEdge;
		}
		for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
			if (outEdge.getTarget() instanceof CodeGenSDFSendVertex) {
				UserFunctionCall copyCall = new UserFunctionCall("memcpy",
						parentContainer);
				try {
					copyCall.addParameter(parentContainer.getBuffer(outEdge));
					copyCall.addParameter(new BufferAtIndex(new ConstantValue(
							"", new DataType("int"), vertex
									.getEdgeIndex(outEdge)
									* outEdge.getProd().intValue()),
							parentContainer.getBuffer(incomingEdge)));
					copyCall.addParameter(new Constant("size", outEdge
							.getProd().intValue()
							+ "*sizeof("
							+ incomingEdge.getDataType().toString() + ")"));
				} catch (InvalidExpressionException e) {
					copyCall.addParameter(new Constant("size", 0));
				}
				container.addCall(copyCall);
			} else {
				Buffer outBuff = parentContainer.getBuffer(outEdge);
				outBuff.setSize(0);
				Assignment ass;
				try {
					ass = new Assignment(outBuff, "&"
							+ parentContainer.getBuffer(incomingEdge).getName()
							+ "[" + vertex.getEdgeIndex(outEdge)
							* outEdge.getProd().intValue() + "]");
					container.addCall(ass);
				} catch (InvalidExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return container;
	}

	public static ICodeElement createImplode(CodeGenSDFJoinVertex vertex,
			AbstractBufferContainer parentContainer) {
		SDFEdge outgoingEdge = null;
		CompoundCodeElement container = new CompoundCodeElement(vertex
				.getName(), parentContainer);
		container.setCorrespondingVertex(vertex);
		for (SDFEdge outedge : vertex.getBase().outgoingEdgesOf(vertex)) {
			outgoingEdge = outedge;
		}
		for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
			UserFunctionCall copyCall = new UserFunctionCall("memcpy",
					parentContainer);
			try {
				copyCall.addParameter(new BufferAtIndex(new ConstantValue("",
						new DataType("int"), vertex.getEdgeIndex(inEdge)
								* inEdge.getProd().intValue()), parentContainer
						.getBuffer(outgoingEdge)));
				copyCall.addParameter(parentContainer.getBuffer(inEdge));
				copyCall.addParameter(new Constant("size", inEdge.getCons()
						.intValue()
						+ "*sizeof("
						+ outgoingEdge.getDataType().toString()
						+ ")"));
			} catch (InvalidExpressionException e) {
				copyCall.addParameter(new Constant("size", 0));
			}
			container.addCall(copyCall);
		}
		return container;
	}

	public static ICodeElement createRoundBuffer(
			CodeGenSDFRoundBufferVertex vertex,
			AbstractBufferContainer parentContainer) {
		SDFEdge outgoingEdge = null;
		CompoundCodeElement container = new CompoundCodeElement(vertex
				.getName(), parentContainer);
		container.setCorrespondingVertex(vertex);
		for (SDFEdge outedge : vertex.getBase().outgoingEdgesOf(vertex)) {
			outgoingEdge = outedge;
		}
		for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
			if (vertex.getEdgeIndex(inEdge) == (vertex.getBase()
					.incomingEdgesOf(vertex).size() - 1)) {
				UserFunctionCall copyCall = new UserFunctionCall("memcpy",
						parentContainer);
				try {
					copyCall.addParameter(parentContainer
							.getBuffer(outgoingEdge));
					copyCall.addParameter(parentContainer.getBuffer(inEdge));
					copyCall.addParameter(new Constant("size", inEdge.getProd()
							.intValue()
							+ "*sizeof("
							+ outgoingEdge.getDataType().toString() + ")"));
				} catch (InvalidExpressionException e) {
					copyCall.addParameter(new Constant("size", 0));
				}
				container.addCall(copyCall);
			}
		}
		return container;
	}

	public static void treatSpecialBehaviorVertex(String name,
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex) {
		try {
			if (vertex instanceof CodeGenSDFForkVertex) {
				SDFEdge incomingEdge = null;
				int i = 0;
				for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
					incomingEdge = inEdge;
				}
				Buffer inBuffer = parentContainer.getBuffer(incomingEdge);
				for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
					ConstantValue index = new ConstantValue("", new DataType(
							"int"), ((CodeGenSDFForkVertex) vertex)
							.getEdgeIndex(outEdge));
					String buffName = parentContainer.getBuffer(outEdge)
							.getName();
					
					IExpression expr = new BinaryExpression("%",new BinaryExpression("*",index,new ConstantValue(outEdge
							.getProd().intValue())),new ConstantValue(inBuffer.getSize()));
					SubBuffer subElt = new SubBuffer(buffName, outEdge
							.getProd().intValue(),expr , inBuffer, outEdge,
							parentContainer);
					parentContainer.removeBufferAllocation(parentContainer
							.getBuffer(outEdge));
					parentContainer.addSubBufferAllocation(new SubBufferAllocation(subElt));
					i++;
				}
			} else if (vertex instanceof CodeGenSDFJoinVertex) {
				SDFEdge outgoingEdge = null;
				int i = 0;
				for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
					outgoingEdge = outEdge;
				}
				Buffer outBuffer = parentContainer.getBuffer(outgoingEdge);
				for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
					ConstantValue index = new ConstantValue("", new DataType(
							"int"), ((CodeGenSDFJoinVertex) vertex)
							.getEdgeIndex(inEdge));
					String buffName = parentContainer.getBuffer(inEdge)
							.getName();
					IExpression expr = new BinaryExpression("%",new BinaryExpression("*",index,new ConstantValue(inEdge.getCons()
							.intValue())),new ConstantValue(outBuffer.getSize()));
					SubBuffer subElt = new SubBuffer(buffName, inEdge.getCons()
							.intValue(), expr, outBuffer, inEdge,
							parentContainer);
					parentContainer.removeBufferAllocation(parentContainer
							.getBuffer(inEdge));
					parentContainer.addSubBufferAllocation(new SubBufferAllocation(subElt));
					i++;
				}
			} else if (vertex instanceof CodeGenSDFBroadcastVertex) {
				SDFEdge incomingEdge = null;
				for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
					incomingEdge = inEdge;
				}
				Buffer inBuffer = parentContainer.getBuffer(incomingEdge);
				for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
					ConstantValue index = new ConstantValue("", new DataType(
							"int"), 0);
					String buffName = parentContainer.getBuffer(outEdge)
							.getName();
					IExpression expr = new BinaryExpression("%",new BinaryExpression("*",index,new ConstantValue(outEdge
							.getCons().intValue())),new ConstantValue(inBuffer.getSize()));
					SubBuffer subElt = new SubBuffer(buffName, outEdge
							.getCons().intValue(), expr, inBuffer, outEdge,
							parentContainer);
					parentContainer.removeBufferAllocation(parentContainer
							.getBuffer(outEdge));
					parentContainer.addSubBufferAllocation(new SubBufferAllocation(subElt));
				}
			} else if (vertex instanceof CodeGenSDFRoundBufferVertex) {
				SDFEdge outgoingEdge = null;
				for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
					outgoingEdge = outEdge;
				}
				Buffer outBuffer = parentContainer.getBuffer(outgoingEdge);
				for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
					ConstantValue index = new ConstantValue("", new DataType(
							"int"), 0);
					String buffName = parentContainer.getBuffer(inEdge)
							.getName();
					IExpression expr = new BinaryExpression("%",new BinaryExpression("*",index,new ConstantValue(inEdge
							.getCons().intValue())),new ConstantValue(outBuffer.getSize()));
					SubBuffer subElt = new SubBuffer(buffName, inEdge.getCons()
							.intValue(),expr, outBuffer, inEdge,
							parentContainer);
					parentContainer.removeBufferAllocation(parentContainer
							.getBuffer(inEdge));
					parentContainer.addSubBufferAllocation(new SubBufferAllocation(subElt));
				}
			}
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
	}
}

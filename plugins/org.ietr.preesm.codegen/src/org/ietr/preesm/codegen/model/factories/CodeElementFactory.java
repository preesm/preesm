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

package org.ietr.preesm.codegen.model.factories;

import java.util.logging.Level;

import jscl.math.Expression;
import jscl.math.JSCLInteger;

import org.ietr.dftools.algorithm.SDFMath;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.psdf.types.PSDFEdgePropertyType;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFFifoPullVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFFifoPushVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFInitVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFSendVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFSinkInterfaceVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFSourceInterfaceVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFSubInitVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFTaskVertex;
import org.ietr.preesm.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.buffer.BufferAtIndex;
import org.ietr.preesm.codegen.model.buffer.SubBuffer;
import org.ietr.preesm.codegen.model.call.Constant;
import org.ietr.preesm.codegen.model.call.PointerOn;
import org.ietr.preesm.codegen.model.call.UserFunctionCall;
import org.ietr.preesm.codegen.model.containers.AbstractCodeContainer;
import org.ietr.preesm.codegen.model.containers.CodeSectionType;
import org.ietr.preesm.codegen.model.containers.CompoundCodeElement;
import org.ietr.preesm.codegen.model.containers.FiniteForLoop;
import org.ietr.preesm.codegen.model.containers.ForLoop;
import org.ietr.preesm.codegen.model.containers.CodeSectionType.MajorType;
import org.ietr.preesm.codegen.model.expression.ConstantExpression;
import org.ietr.preesm.codegen.model.main.Assignment;
import org.ietr.preesm.codegen.model.main.ICodeElement;
import org.ietr.preesm.core.types.DataType;

/**
 * Creating code elements from a vertex. This element can be hierarchical.
 * 
 * @author jpiat
 * @author mpelcat
 */
public class CodeElementFactory {

	/**
	 * tests if a code element is needed for any type of vertex
	 * 
	 * @param parentContainer
	 *            This container is used to get access to buffer resources
	 * @param vertex
	 *            vertex to create code for
	 * @param sectionType
	 *            init or loop phase for example
	 */
	public static boolean needElement(AbstractCodeContainer parentContainer,
			SDFAbstractVertex vertex, CodeSectionType sectionType) {
		return createElement(parentContainer, vertex, sectionType) != null;
	}

	/**
	 * Creates code element for any type of vertex
	 * 
	 * @param parentContainer
	 *            This container is used to get access to buffer resources
	 * @param vertex
	 *            vertex to create code for
	 * @param sectionType
	 *            init or loop phase for example
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer, SDFAbstractVertex vertex,
			CodeSectionType sectionType) {
		if (vertex instanceof CodeGenSDFBroadcastVertex)
			return createElement(parentContainer,
					(CodeGenSDFBroadcastVertex) vertex, sectionType);
		else if (vertex instanceof CodeGenSDFForkVertex)
			return createElement(parentContainer,
					(CodeGenSDFForkVertex) vertex, sectionType);
		else if (vertex instanceof CodeGenSDFJoinVertex)
			return createElement(parentContainer,
					(CodeGenSDFJoinVertex) vertex, sectionType);
		else if (vertex instanceof CodeGenSDFInitVertex)
			return createElement(parentContainer,
					(CodeGenSDFInitVertex) vertex, sectionType);
		else if (vertex instanceof CodeGenSDFRoundBufferVertex)
			return createElement(parentContainer,
					(CodeGenSDFRoundBufferVertex) vertex, sectionType);
		else if (vertex instanceof CodeGenSDFSinkInterfaceVertex)
			return createElement(parentContainer,
					(CodeGenSDFSinkInterfaceVertex) vertex, sectionType);
		else if (vertex instanceof CodeGenSDFSourceInterfaceVertex)
			return createElement(parentContainer,
					(CodeGenSDFSourceInterfaceVertex) vertex, sectionType);
		else if (vertex instanceof CodeGenSDFSubInitVertex)
			return createElement(parentContainer,
					(CodeGenSDFSubInitVertex) vertex, sectionType);
		else if (vertex instanceof CodeGenSDFTaskVertex)
			return createElement(parentContainer,
					(CodeGenSDFTaskVertex) vertex, sectionType);
		else if (vertex instanceof CodeGenSDFFifoPushVertex)
			return createElement(parentContainer,
					(CodeGenSDFFifoPushVertex) vertex, sectionType);
		else if (vertex instanceof CodeGenSDFFifoPullVertex)
			return createElement(parentContainer,
					(CodeGenSDFFifoPullVertex) vertex, sectionType);
		return null;
	}

	/**
	 * Creates code for broadcast
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer,
			CodeGenSDFBroadcastVertex vertex, CodeSectionType sectionType) {
		SDFEdge incomingEdge = null;
		CompoundCodeElement container = new CompoundCodeElement(
				vertex.getName(), parentContainer, sectionType.toString());
		container.setCorrespondingVertex(vertex);
		for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
				.incomingEdgesOf(vertex)) {
			incomingEdge = inEdge;
		}
		for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
				.outgoingEdgesOf(vertex)) {
			try {
				if (outEdge.getProd().intValue() == incomingEdge.getCons()
						.intValue()) {
					if (outEdge.getTarget() instanceof CodeGenSDFSendVertex
							|| outEdge.getTarget() instanceof CodeGenSDFSinkInterfaceVertex) {
						UserFunctionCall copyCall = new UserFunctionCall(
								"memcpy", parentContainer);
						copyCall.addArgument(parentContainer.getBuffer(outEdge));
						copyCall.addArgument(parentContainer
								.getBuffer(incomingEdge));
						try {
							copyCall.addArgument(new Constant("size",
									incomingEdge.getCons().intValue()
											+ "*sizeof("
											+ incomingEdge.getDataType()
													.toString() + ")"));
						} catch (InvalidExpressionException e) {
							copyCall.addArgument(new Constant("size", 0));
						}
						container.addCall(copyCall);
					} else {
						Buffer outBuff = parentContainer.getBuffer(outEdge);
						if (outBuff == null) {
							container.addBuffer(
									container.getBuffer(incomingEdge), outEdge);
						} else {
							outBuff.setSize(0);
							Assignment ass = new Assignment(outBuff, "&"
									+ parentContainer.getBuffer(incomingEdge)
											.getName() + "[0]");
							container.addCall(ass);
						}

					}
				} else if (outEdge.getProd().intValue() > incomingEdge
						.getCons().intValue()) {
					int minToken = SDFMath.gcd(outEdge.getProd().intValue(),
							incomingEdge.getCons().intValue());
					for (int i = 0; i < outEdge.getProd().intValue() / minToken; i++) {
						UserFunctionCall copyCall = new UserFunctionCall(
								"memcpy", parentContainer);
						copyCall.addArgument(new BufferAtIndex(
								new ConstantExpression(i * minToken),
								parentContainer.getBuffer(outEdge)));
						copyCall.addArgument(parentContainer
								.getBuffer(incomingEdge));
						copyCall.addArgument(new Constant("size", minToken
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

	/**
	 * Creates code for fork
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer, CodeGenSDFForkVertex vertex,
			CodeSectionType sectionType) {
		SDFEdge incomingEdge = null;
		CompoundCodeElement container = new CompoundCodeElement(
				vertex.getName(), parentContainer, sectionType.toString());
		container.setCorrespondingVertex(vertex);
		for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
				.incomingEdgesOf(vertex)) {
			incomingEdge = inEdge;
		}
		for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
				.outgoingEdgesOf(vertex)) {
			if (outEdge.getTarget() instanceof CodeGenSDFSendVertex) {
				UserFunctionCall copyCall = new UserFunctionCall("memcpy",
						parentContainer);
				try {
					copyCall.addArgument(parentContainer.getBuffer(outEdge));
					copyCall.addArgument(new BufferAtIndex(
							new ConstantExpression("", new DataType("int"),
									vertex.getEdgeIndex(outEdge)
											* outEdge.getProd().intValue()),
							parentContainer.getBuffer(incomingEdge)));
					copyCall.addArgument(new Constant("size", outEdge.getProd()
							.intValue()
							+ "*sizeof("
							+ incomingEdge.getDataType().toString() + ")"));
				} catch (InvalidExpressionException e) {
					copyCall.addArgument(new Constant("size", 0));
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

	/**
	 * Creates code for join
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer, CodeGenSDFJoinVertex vertex,
			CodeSectionType sectionType) {
		SDFEdge outgoingEdge = null;
		CompoundCodeElement container = new CompoundCodeElement(
				vertex.getName(), parentContainer, sectionType.toString());
		container.setCorrespondingVertex(vertex);
		for (SDFEdge outedge : ((SDFGraph) vertex.getBase())
				.outgoingEdgesOf(vertex)) {
			outgoingEdge = outedge;
		}
		for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
				.incomingEdgesOf(vertex)) {
			UserFunctionCall copyCall = new UserFunctionCall("memcpy",
					parentContainer);
			try {
				copyCall.addArgument(new BufferAtIndex(new ConstantExpression(
						"", new DataType("int"), vertex.getEdgeIndex(inEdge)
								* inEdge.getProd().intValue()), parentContainer
						.getBuffer(outgoingEdge)));
				copyCall.addArgument(parentContainer.getBuffer(inEdge));
				copyCall.addArgument(new Constant("size", inEdge.getCons()
						.intValue()
						+ "*sizeof("
						+ outgoingEdge.getDataType().toString() + ")"));
			} catch (InvalidExpressionException e) {
				copyCall.addArgument(new Constant("size", 0));
			}
			container.addCall(copyCall);
		}
		return container;
	}

	/**
	 * Creates code for init vertices
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer, CodeGenSDFInitVertex vertex,
			CodeSectionType sectionType) {
		CompoundCodeElement container = new CompoundCodeElement(
				vertex.getName(), parentContainer, sectionType.toString());
		container.setCorrespondingVertex(vertex);
		UserFunctionCall call = new UserFunctionCall(vertex, parentContainer,
				sectionType, false);
		container.addCall(call);
		for (SDFEdge edge : ((SDFGraph) vertex.getBase()).edgeSet()) {
			if (edge.getCons() instanceof PSDFEdgePropertyType) {
				PSDFEdgePropertyType prop = (PSDFEdgePropertyType) edge
						.getCons();
				String name = prop.getSymbolicName();
				if (vertex.getAffectedParameter(name) != null) {
					Buffer buff = parentContainer.getBuffer(edge);
					while (buff != null && buff instanceof SubBuffer)
						buff = ((SubBuffer) buff).getParentBuffer();
					if (buff != null && buff.getAllocatedSize() == 0) {
						UserFunctionCall mallocCall = new UserFunctionCall(
								"malloc", parentContainer);
						try {
							mallocCall.setReturn(buff);
							mallocCall.addArgument(new Constant("size", name
									+ "*" + edge.getTarget().getNbRepeat()
									+ "*" + "sizeof("
									+ buff.getType().getTypeName() + ")"));
						} catch (InvalidExpressionException e) {
							mallocCall.addArgument(new Constant("size", 0));
						}
						container.addCall(mallocCall);
					}
				}
			} else if (edge.getProd() instanceof PSDFEdgePropertyType) {
				PSDFEdgePropertyType prop = (PSDFEdgePropertyType) edge
						.getProd();
				String name = prop.getSymbolicName();
				if (vertex.getAffectedParameter(name) != null) {
					Buffer buff = parentContainer.getBuffer(edge);
					while (buff != null && buff instanceof SubBuffer)
						buff = ((SubBuffer) buff).getParentBuffer();
					SDFEdge allocEdge = buff.getEdge();
					if (buff != null) {
						UserFunctionCall mallocCall = new UserFunctionCall(
								"malloc", parentContainer);
						try {
							mallocCall.setReturn(buff);
							mallocCall.addArgument(new Constant("size", name
									+ "*" + allocEdge.getSource().getNbRepeat()
									+ "*" + "sizeof("
									+ buff.getType().getTypeName() + ")"));
						} catch (InvalidExpressionException e) {
							mallocCall.addArgument(new Constant("size", 0));
						}
						container.addCall(mallocCall);
					}
				}
			}
		}
		AbstractBufferContainer oldContainer = parentContainer;
		AbstractBufferContainer newContainer = parentContainer;
		do {
			newContainer = parentContainer.getParentContainer();
		} while ((newContainer != null && newContainer instanceof AbstractBufferContainer)
				&& !(newContainer instanceof AbstractCodeContainer));
		if (newContainer == null) {
			newContainer = oldContainer;
		}
		container.setParentContainer(newContainer);
		return container;

	}

	/**
	 * Creates code for round buffers
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer,
			CodeGenSDFRoundBufferVertex vertex, CodeSectionType sectionType) {
		SDFEdge outgoingEdge = null;
		CompoundCodeElement container = new CompoundCodeElement(
				vertex.getName(), parentContainer, sectionType.toString());
		container.setCorrespondingVertex(vertex);
		for (SDFEdge outedge : ((SDFGraph) vertex.getBase())
				.outgoingEdgesOf(vertex)) {
			outgoingEdge = outedge;
		}
		for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
				.incomingEdgesOf(vertex)) {
			if (vertex.getEdgeIndex(inEdge) == (((SDFGraph) vertex.getBase())
					.incomingEdgesOf(vertex).size() - 1)) {
				UserFunctionCall copyCall = new UserFunctionCall("memcpy",
						parentContainer);
				try {
					copyCall.addArgument(parentContainer
							.getBuffer(outgoingEdge));
					copyCall.addArgument(parentContainer.getBuffer(inEdge));
					copyCall.addArgument(new Constant("size", inEdge.getProd()
							.intValue()
							+ "*sizeof("
							+ outgoingEdge.getDataType().toString() + ")"));
				} catch (InvalidExpressionException e) {
					copyCall.addArgument(new Constant("size", 0));
				}
				container.addCall(copyCall);
			}
		}
		return container;
	}

	/**
	 * Creates code for sink
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer,
			CodeGenSDFSinkInterfaceVertex vertex, CodeSectionType sectionType) {
		return null;
	}

	/**
	 * Creates code for source
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer,
			CodeGenSDFSourceInterfaceVertex vertex, CodeSectionType sectionType) {
		return null;
	}

	/**
	 * Creates code for subinit
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer,
			CodeGenSDFSubInitVertex vertex, CodeSectionType sectionType) {
		return null;
	}

	/**
	 * Creates code for user defined task
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer, CodeGenSDFTaskVertex vertex,
			CodeSectionType sectionType) {
		try {
			// Generating loop when the vertex is repeated
			if ((vertex.getNbRepeat() instanceof Integer && (vertex
					.getNbRepeatAsInteger() > 1))
					|| vertex.getNbRepeat() instanceof Expression
					|| (vertex.getNbRepeat() instanceof JSCLInteger && (vertex
							.getNbRepeatAsInteger() > 1))) {
				FiniteForLoop loop = new FiniteForLoop(parentContainer,
						(ICodeGenSDFVertex) vertex, sectionType);
				return loop;
			} else if (vertex.getGraphDescription() == null
					&& vertex.getRefinement() instanceof ActorPrototypes) {
				ActorPrototypes protos = ((ActorPrototypes) vertex
						.getRefinement());
				if (protos.hasPrototype(sectionType)) {
					UserFunctionCall call = new UserFunctionCall(vertex,
							parentContainer, sectionType, false);
					return call;
				}
			} else if (vertex.getGraphDescription() != null) {
				CompoundCodeElement compound = new CompoundCodeElement(
						vertex.getName(), parentContainer,
						(ICodeGenSDFVertex) vertex, sectionType);
				return compound;
			}
			return null;
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates code for token end
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer,
			CodeGenSDFFifoPushVertex vertex, CodeSectionType sectionType) {
		SDFEdge incomingEdge = null;
		if (vertex.getEndReference() != null) {
			for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
					.incomingEdgesOf(vertex)) {
				incomingEdge = inEdge;
			}
			if (incomingEdge != null) {
				UserFunctionCall delayCall = new UserFunctionCall("push",
						parentContainer, vertex);
				if (((CodeGenSDFFifoPullVertex) vertex.getEndReference())
						.getDelayVariable() == null) {
					WorkflowLogger.getLogger().log(
							Level.SEVERE,
							"Fifo variable not found for vertex "
									+ vertex.getName() + " with end reference "
									+ vertex.getEndReference().getName());
					return null;
				}

				delayCall.addArgument(
						"fifo",
						new PointerOn(((CodeGenSDFFifoPullVertex) vertex
								.getEndReference()).getDelayVariable()));
				delayCall.addArgument("buffer",
						parentContainer.getBuffer(incomingEdge));
				try {
					delayCall.addArgument("nb_token", new Constant("nb_token",
							incomingEdge.getCons().intValue()));
					return delayCall;
				} catch (InvalidExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Creates code for token init
	 */
	public static ICodeElement createElement(
			AbstractCodeContainer parentContainer,
			CodeGenSDFFifoPullVertex vertex, CodeSectionType sectionType) {
		SDFEdge outgoingEdge = null;
		if (parentContainer instanceof ForLoop
				|| sectionType.getMajor() == MajorType.INIT) {
			for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
					.outgoingEdgesOf(vertex)) {
				outgoingEdge = outEdge;
			}
			if (outgoingEdge != null && vertex.getDelayVariable() != null) {
				UserFunctionCall delayCall = new UserFunctionCall("pull",
						parentContainer, vertex);
				delayCall.addArgument("fifo",
						new PointerOn(vertex.getDelayVariable()));
				delayCall.addArgument("buffer",
						parentContainer.getBuffer(outgoingEdge));
				try {
					delayCall.addArgument("nb_token", new Constant("nb_token",
							outgoingEdge.getProd().intValue()));
					return delayCall;
				} catch (InvalidExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			AbstractBufferContainer newContainer = (AbstractBufferContainer) parentContainer
					.getParentContainer();
			while (newContainer instanceof AbstractCodeContainer
					&& !(newContainer instanceof CompoundCodeElement)) {
				newContainer = newContainer.getParentContainer();
			}

		}

		return null;
	}
}

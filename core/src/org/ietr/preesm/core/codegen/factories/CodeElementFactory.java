/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.core.codegen.factories;

import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.SubBuffer;
import org.ietr.preesm.core.codegen.buffer.SubBufferAllocation;
import org.ietr.preesm.core.codegen.containers.AbstractCodeContainer;
import org.ietr.preesm.core.codegen.expression.BinaryExpression;
import org.ietr.preesm.core.codegen.expression.ConstantExpression;
import org.ietr.preesm.core.codegen.expression.IExpression;
import org.ietr.preesm.core.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.core.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.core.codegen.types.DataType;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;

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
			AbstractCodeContainer parentContainer, SDFAbstractVertex vertex) {
		try {
			return ((ICodeGenSDFVertex) vertex).getCodeElement(parentContainer);
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void treatSpecialBehaviorVertex(String name,
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex) {
		try {
			if (vertex instanceof CodeGenSDFForkVertex) {
				SDFEdge incomingEdge = null;
				for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
						.incomingEdgesOf(vertex)) {
					incomingEdge = inEdge;
				}
				Buffer inBuffer = parentContainer.getBuffer(incomingEdge);
				for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
						.outgoingEdgesOf(vertex)) {
					ConstantExpression index = new ConstantExpression("",
							new DataType("int"),
							((CodeGenSDFForkVertex) vertex)
									.getEdgeIndex(outEdge));
					String buffName = parentContainer.getBuffer(outEdge)
							.getName();

					IExpression expr = new BinaryExpression("%",
							new BinaryExpression("*", index,
									new ConstantExpression(outEdge.getProd()
											.intValue())),
							new ConstantExpression(inBuffer.getSize()));
					SubBuffer subElt = new SubBuffer(buffName, outEdge
							.getProd().intValue(), expr, inBuffer, outEdge,
							parentContainer);
					parentContainer.removeBufferAllocation(parentContainer
							.getBuffer(outEdge));
					parentContainer
							.addSubBufferAllocation(new SubBufferAllocation(
									subElt));
				}
			} else if (vertex instanceof CodeGenSDFJoinVertex) {
				SDFEdge outgoingEdge = null;
				for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
						.outgoingEdgesOf(vertex)) {
					outgoingEdge = outEdge;
				}
				Buffer outBuffer = parentContainer.getBuffer(outgoingEdge);
				for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
						.incomingEdgesOf(vertex)) {
					ConstantExpression index = new ConstantExpression("",
							new DataType("int"),
							((CodeGenSDFJoinVertex) vertex)
									.getEdgeIndex(inEdge));
					String buffName = parentContainer.getBuffer(inEdge)
							.getName();
					IExpression expr = new BinaryExpression("%",
							new BinaryExpression("*", index,
									new ConstantExpression(inEdge.getCons()
											.intValue())),
							new ConstantExpression(outBuffer.getSize()));
					SubBuffer subElt = new SubBuffer(buffName, inEdge.getCons()
							.intValue(), expr, outBuffer, inEdge,
							parentContainer);
					parentContainer.removeBufferAllocation(parentContainer
							.getBuffer(inEdge));
					parentContainer
							.addSubBufferAllocation(new SubBufferAllocation(
									subElt));
				}
			} else if (vertex instanceof CodeGenSDFBroadcastVertex) {
				SDFEdge incomingEdge = null;
				for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
						.incomingEdgesOf(vertex)) {
					incomingEdge = inEdge;
				}
				Buffer inBuffer = parentContainer.getBuffer(incomingEdge);
				for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
						.outgoingEdgesOf(vertex)) {
					ConstantExpression index = new ConstantExpression("",
							new DataType("int"), 0);
					String buffName = parentContainer.getBuffer(outEdge)
							.getName();
					IExpression expr = new BinaryExpression("%",
							new BinaryExpression("*", index,
									new ConstantExpression(outEdge.getCons()
											.intValue())),
							new ConstantExpression(inBuffer.getSize()));
					SubBuffer subElt = new SubBuffer(buffName, outEdge
							.getCons().intValue(), expr, inBuffer, outEdge,
							parentContainer);
					parentContainer.removeBufferAllocation(parentContainer
							.getBuffer(outEdge));
					parentContainer
							.addSubBufferAllocation(new SubBufferAllocation(
									subElt));
				}
			} else if (vertex instanceof CodeGenSDFRoundBufferVertex) {
				SDFEdge outgoingEdge = null;
				for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
						.outgoingEdgesOf(vertex)) {
					outgoingEdge = outEdge;
				}
				Buffer outBuffer = parentContainer.getBuffer(outgoingEdge);
				for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
						.incomingEdgesOf(vertex)) {
					ConstantExpression index = new ConstantExpression("",
							new DataType("int"), 0);
					String buffName = parentContainer.getBuffer(inEdge)
							.getName();
					IExpression expr = new BinaryExpression("%",
							new BinaryExpression("*", index,
									new ConstantExpression(inEdge.getCons()
											.intValue())),
							new ConstantExpression(outBuffer.getSize()));
					SubBuffer subElt = new SubBuffer(buffName, inEdge.getCons()
							.intValue(), expr, outBuffer, inEdge,
							parentContainer);
					parentContainer.removeBufferAllocation(parentContainer
							.getBuffer(inEdge));
					parentContainer
							.addSubBufferAllocation(new SubBufferAllocation(
									subElt));
				}
			}
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
	}
}

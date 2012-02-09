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

package org.ietr.preesm.core.codegen.containers;

import java.util.HashMap;
import java.util.Iterator;

import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;

import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.LoopIndex;
import org.ietr.preesm.core.codegen.VariableAllocation;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferToolBox;
import org.ietr.preesm.core.codegen.buffer.SubBuffer;
import org.ietr.preesm.core.codegen.buffer.SubBufferAllocation;
import org.ietr.preesm.core.codegen.calls.Variable;
import org.ietr.preesm.core.codegen.expression.IExpression;
import org.ietr.preesm.core.codegen.expression.VariableExpression;
import org.ietr.preesm.core.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.ietr.preesm.core.codegen.threads.ComputationThreadDeclaration;
import org.ietr.preesm.core.codegen.types.DataType;

/**
 * A Finite for loop is a loop with a given and limited iteration domain
 * 
 * @author jpiat
 * 
 */
public class FiniteForLoop extends AbstractBufferContainer implements
		ICodeElement {

	private HashMap<SDFEdge, Buffer> allocatedBuffers;

	private SDFAbstractVertex correspondingVertex;

	private LoopIndex index;

	private ICodeElement content;

	private AbstractBufferContainer parentContainer;

	/**
	 * Creates a Finite For Loop
	 * 
	 * @param parentContainer
	 *            The parent container of the loop
	 * @param correspondingVertex
	 *            The vertex corresponding to this loop
	 */
	public FiniteForLoop(AbstractBufferContainer parentContainer,
			ICodeGenSDFVertex correspondingVertex) {
		super(parentContainer);
		AbstractBufferContainer parentLoop = parentContainer;
		while (parentLoop != null && !(parentLoop instanceof FiniteForLoop)) {
			parentLoop = parentLoop.getParentContainer();
		}
		if (parentLoop != null && parentLoop instanceof FiniteForLoop) {
			char newIndex = (char) (((int) ((FiniteForLoop) parentLoop)
					.getIndex().getNameAsChar()) + 1);
			index = new LoopIndex(newIndex, new DataType("long"));
		} else {
			index = new LoopIndex('i', new DataType("long"));
		}
		parentLoop = parentContainer;
		while (parentLoop != null
				&& !(parentLoop instanceof ComputationThreadDeclaration)) {
			parentLoop = parentLoop.getParentContainer();
		}
		if (parentLoop.getVariable(index.getName()) == null) {
			parentLoop.addVariable(index);
		}
		allocatedBuffers = new HashMap<SDFEdge, Buffer>();
		this.parentContainer = parentContainer;
		this.correspondingVertex = (SDFAbstractVertex) correspondingVertex;
		for (SDFEdge edge : ((SDFGraph) this.correspondingVertex.getBase())
				.outgoingEdgesOf(this.correspondingVertex)) {
			AbstractBufferContainer parentBufferContainer = parentContainer;
			while (parentBufferContainer != null
					&& parentBufferContainer.getBuffer(edge) == null) {
				parentBufferContainer = parentBufferContainer
						.getParentContainer();
			}
			try {
				if (parentBufferContainer != null
						&& allocatedBuffers.get(edge) == null) {
					if (edge.getProd().intValue() != parentBufferContainer
							.getBuffer(edge).getSize()
							|| edge.getProd().intValue() == 0) {
						IExpression expr = BufferToolBox.createBufferIndex(
								index, edge, parentBufferContainer, true);
						SubBuffer newBuffer = new SubBuffer("inSub_"
								+ index.getName()
								+ "_"
								+ parentBufferContainer.getBuffer(edge)
										.getName(), edge.getProd().intValue(),
								expr, parentBufferContainer.getBuffer(edge),
								parentBufferContainer);
						this.addSubBufferAllocation(new SubBufferAllocation(
								newBuffer));
						this.addBuffer(newBuffer, edge);
					} else {
						this.addBuffer(parentBufferContainer.getBuffer(edge),
								edge);
					}
				}
				if (edge.getDelay().intValue() > 0
						&& parentBufferContainer instanceof AbstractCodeContainer) {

				}
			} catch (InvalidExpressionException e) {
				e.printStackTrace();
			}
		}
		for (SDFEdge edge : ((SDFGraph) this.correspondingVertex.getBase())
				.incomingEdgesOf(this.correspondingVertex)) {
			AbstractBufferContainer parentBufferContainer = parentContainer;
			while (parentBufferContainer != null
					&& parentBufferContainer.getBuffer(edge) == null) {
				parentBufferContainer = parentBufferContainer
						.getParentContainer();
			}
			try {
				if (parentBufferContainer != null
						&& allocatedBuffers.get(edge) == null) {
					if (edge.getCons().intValue() != parentBufferContainer
							.getBuffer(edge).getSize()
							|| edge.getCons().intValue() == 0) {
						IExpression expr = BufferToolBox.createBufferIndex(
								index, edge, parentBufferContainer, false);
						SubBuffer newBuffer = new SubBuffer("outSub_"
								+ index.getName()
								+ "_"
								+ parentBufferContainer.getBuffer(edge)
										.getName(), edge.getCons().intValue(),
								expr, parentBufferContainer.getBuffer(edge),
								parentBufferContainer);
						this.addSubBufferAllocation(new SubBufferAllocation(
								newBuffer));
						this.addBuffer(newBuffer, edge);
					} else {
						this.addBuffer(parentBufferContainer.getBuffer(edge),
								edge);
					}

				}
			} catch (InvalidExpressionException e) {
				e.printStackTrace();
			}
		}
		content = new CompoundCodeElement(this.correspondingVertex.getName(),
				this, correspondingVertex);
	}

	public ICodeElement getContent() {
		return content;
	}

	public void addBuffer(Buffer buff, SDFEdge edge) {
		if (allocatedBuffers.get(edge) == null) {
			allocatedBuffers.put(edge, buff);
		}
	}

	public String getNbIteration() {
		try {
			return correspondingVertex.getNbRepeat().toString();
		} catch (InvalidExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "0";
		}
	}

	public Buffer getBuffer(SDFEdge edge) {
		if (allocatedBuffers.get(edge) == null) {
			return super.getBuffer(edge);
		} else {
			return allocatedBuffers.get(edge);
		}
	}

	@Override
	public SDFAbstractVertex getCorrespondingVertex() {
		return correspondingVertex;
	}

	public AbstractBufferContainer getParentContainer() {
		return parentContainer;
	}

	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		Iterator<VariableAllocation> iterator2 = variables.iterator();
		while (iterator2.hasNext()) {
			VariableAllocation alloc = iterator2.next();
			alloc.accept(printer, currentLocation); // Accepts allocations
		}
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation);
		content.accept(printer, currentLocation);
	}

	public LoopIndex getIndex() {
		return index;
	}

	/**
	 * Returns the name of this finite for loop.
	 * 
	 * @return <code>""</code>.
	 */
	@Override
	public String getName() {
		return "for";
	}

	public String toString() {
		return "";
	}

	/**
	 * Adds the given variable to this buffer container variable list.
	 * 
	 * @param var
	 *            A {@link VariableExpression}.
	 */
	public void addVariable(Variable var) {
		this.getParentContainer().addVariable(var);
	}

}

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

package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.calls.Constant;
import org.ietr.preesm.core.codegen.calls.PointerOn;
import org.ietr.preesm.core.codegen.calls.UserFunctionCall;
import org.ietr.preesm.core.codegen.calls.Variable;
import org.ietr.preesm.core.codegen.containers.AbstractCodeContainer;
import org.ietr.preesm.core.codegen.containers.CompoundCodeElement;
import org.ietr.preesm.core.codegen.containers.ForLoop;
import org.sdf4j.model.IRefinement;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.esdf.SDFInitVertex;

public class CodeGenSDFTokenInitVertex extends SDFInitVertex implements
		ICodeGenSDFVertex {

	private Variable delayVariable;
	public static final String TYPE = ImplementationPropertyNames.Vertex_vertexType;

	public CodeGenSDFTokenInitVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.task);
	}

	public ArchitectureComponent getOperator() {
		return (ArchitectureComponent) this.getPropertyBean().getValue(
				OPERATOR, ArchitectureComponent.class);
	}

	public void setOperator(ArchitectureComponent op) {
		this.getPropertyBean().setValue(OPERATOR, getOperator(), op);
	}

	public int getPos() {
		if (this.getPropertyBean().getValue(POS) != null) {
			return (Integer) this.getPropertyBean()
					.getValue(POS, Integer.class);
		}
		return 0;
	}

	public void setPos(int pos) {
		this.getPropertyBean().setValue(POS, getPos(), pos);
	}

	public String toString() {
		return "";
	}

	public IRefinement getRefinement() {
		return null;
	}

	public void setDelayVariable(Variable var) {
		this.delayVariable = var;
	}

	public Variable getDelayVariable() {
		return this.delayVariable;
	}

	@Override
	public ICodeElement getCodeElement(AbstractCodeContainer parentContainer) {
		SDFEdge outgoingEdge = null;
		if (parentContainer instanceof ForLoop) {
			for (SDFEdge outEdge : ((SDFGraph) this.getBase())
					.outgoingEdgesOf(this)) {
				outgoingEdge = outEdge;
			}
			if (outgoingEdge != null) {
				UserFunctionCall delayCall = new UserFunctionCall("read_delay",
						parentContainer);
				delayCall.addArgument("delay",
						new PointerOn(this.getDelayVariable()));
				delayCall.addArgument("buffer",
						parentContainer.getBuffer(outgoingEdge));
				try {
					delayCall.addArgument("nb_elt", new Constant("nb_elt",
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

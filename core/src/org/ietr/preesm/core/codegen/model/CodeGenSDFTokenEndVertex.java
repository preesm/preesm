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

import java.util.logging.Level;

import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.calls.Constant;
import org.ietr.preesm.core.codegen.calls.PointerOn;
import org.ietr.preesm.core.codegen.calls.UserFunctionCall;
import org.ietr.preesm.core.codegen.containers.AbstractCodeContainer;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.esdf.SDFEndVertex;

public class CodeGenSDFTokenEndVertex extends SDFEndVertex implements
		ICodeGenSDFVertex {

	public static final String TYPE = ImplementationPropertyNames.Vertex_vertexType;

	public CodeGenSDFTokenEndVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.task);
	}

	public ComponentInstance getOperator() {
		return (ComponentInstance) this.getPropertyBean().getValue(OPERATOR,
				ComponentInstance.class);
	}

	public void setOperator(ComponentInstance op) {
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

	@Override
	public ICodeElement getCodeElement(AbstractCodeContainer parentContainer) {
		SDFEdge incomingEdge = null;
		if (this.getEndReference() != null) {
			for (SDFEdge inEdge : ((SDFGraph) this.getBase())
					.incomingEdgesOf(this)) {
				incomingEdge = inEdge;
			}
			if (incomingEdge != null) {
				UserFunctionCall delayCall = new UserFunctionCall(
						"write_delay", parentContainer);
				if (((CodeGenSDFTokenInitVertex) this.getEndReference())
						.getDelayVariable() == null) {
					WorkflowLogger.getLogger().log(
							Level.SEVERE,
							"Delay variable nor found for vertex "
									+ this.getName() + " with end reference "
									+ this.getEndReference().getName());
					return null;
				}

				delayCall.addArgument(
						"delay",
						new PointerOn(((CodeGenSDFTokenInitVertex) this
								.getEndReference()).getDelayVariable()));
				delayCall.addArgument("buffer",
						parentContainer.getBuffer(incomingEdge));
				try {
					delayCall.addArgument("nb_elt", new Constant("nb_elt",
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

}

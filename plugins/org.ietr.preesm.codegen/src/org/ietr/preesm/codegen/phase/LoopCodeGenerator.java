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

package org.ietr.preesm.codegen.phase;

import java.util.SortedSet;
import java.util.logging.Level;

import net.sf.dftools.algorithm.model.parameters.Parameter;
import net.sf.dftools.algorithm.model.parameters.ParameterSet;
import net.sf.dftools.algorithm.model.psdf.PSDFInitVertex;
import net.sf.dftools.algorithm.model.psdf.PSDFSubInitVertex;
import net.sf.dftools.algorithm.model.psdf.parameters.PSDFDynamicParameter;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.codegen.communication.ComCodeGeneratorFactory;
import org.ietr.preesm.codegen.communication.IComCodeGenerator;
import org.ietr.preesm.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.call.Variable;
import org.ietr.preesm.codegen.model.containers.AbstractCodeContainer;
import org.ietr.preesm.codegen.model.factories.CodeElementFactory;
import org.ietr.preesm.codegen.model.main.ICodeElement;
import org.ietr.preesm.codegen.model.types.CodeSectionType;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.core.types.ImplementationPropertyNames;

/**
 * Generates code for the computation loop phase
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 */
public class LoopCodeGenerator extends AbstractPhaseCodeGenerator {

	private AbstractCodeContainer loop;

	public LoopCodeGenerator(AbstractCodeContainer loop) {
		super(loop);
		this.loop = loop;
	}


	/**
	 * Adds send and receive functions from vertices allocated on the current
	 * core. Vertices are already in the correct order. The code thread com
	 * generator delegates com creation to each route step appropriate generator
	 */
	@Override
	public void addSendsAndReceives(SortedSet<SDFAbstractVertex> vertices,
			AbstractBufferContainer bufferContainer, CodeSectionType sectionType) {

		// a com code generator factory outputs the commmunication generator
		// that will add communication primitives into the code
		ComCodeGeneratorFactory factory = new ComCodeGeneratorFactory(
				loop, vertices);
		for (SDFAbstractVertex vertex : vertices) {
			AbstractRouteStep step = (AbstractRouteStep) vertex
					.getPropertyBean().getValue(
							ImplementationPropertyNames.SendReceive_routeStep);

			// Delegates the com creation to the appropriate generator
			IComCodeGenerator generator = factory.getCodeGenerator(step);

			// Creates all functions and buffers related to the given vertex
			generator.insertComs(vertex, sectionType);
		}
	}

	/**
	 * Adding variables for PSDF parameters
	 */
	@Override
	public void addDynamicParameter(ParameterSet params) {
		if (params != null) {
			for (Parameter param : params.values()) {
				if (param instanceof PSDFDynamicParameter) {
					loop.addVariable(
									new Variable(param.getName(), new DataType(
											"long")));
				}
			}
		}
	}

	/**
	 * Adds one function call for each vertex in the ordered set
	 */
	@Override
	public void addUserFunctionCalls(SortedSet<SDFAbstractVertex> vertices, CodeSectionType sectionType) {

		// Treating regular vertices
		for (SDFAbstractVertex vertex : vertices) {
			if (vertex instanceof ICodeGenSDFVertex) {
				ICodeElement loopCall = CodeElementFactory.createElement(loop, vertex, sectionType);
				if (loopCall != null) {
					if (vertex instanceof PSDFInitVertex) {
						loop.addInitCodeElement(loopCall);
					} else if (vertex instanceof PSDFSubInitVertex) {
						loop.addCodeElementFirst(loopCall);
					} else {
						// Adding loop call if any
						WorkflowLogger.getLogger()
								.log(Level.FINE,
										"Adding code elt "
												+ loopCall.toString()
												+ " on operator "
												+ loop.getParentContainer().getParentContainer()
														.getName());
						loop.addCodeElement(loopCall);
					}
				}
			}
		}
	}
}

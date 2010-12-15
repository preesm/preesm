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

package org.ietr.preesm.plugin.codegen.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.codegen.model.CodeGenSDFInitVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFReceiveVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSendVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSinkInterfaceVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSourceInterfaceVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSubInitVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFTaskVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFTokenEndVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFTokenInitVertex;
import org.ietr.preesm.core.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.core.codegen.model.VertexType;
import org.ietr.preesm.core.task.PreesmException;
import org.ietr.preesm.plugin.codegen.model.idl.IDLFunctionFactory;
import org.sdf4j.model.CodeRefinement;
import org.sdf4j.model.CodeRefinement.Language;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.parameters.Argument;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.psdf.PSDFInitVertex;
import org.sdf4j.model.psdf.PSDFSubInitVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.esdf.SDFBroadcastVertex;
import org.sdf4j.model.sdf.esdf.SDFEndVertex;
import org.sdf4j.model.sdf.esdf.SDFForkVertex;
import org.sdf4j.model.sdf.esdf.SDFInitVertex;
import org.sdf4j.model.sdf.esdf.SDFJoinVertex;
import org.sdf4j.model.sdf.esdf.SDFRoundBufferVertex;
import org.sdf4j.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.sdf4j.model.visitors.SDF4JException;

/**
 * @author jpiat
 */
public class CodeGenSDFVertexFactory {

	private IFile mainFile;

	public CodeGenSDFVertexFactory(IFile parentAlgoFile) {
		mainFile = parentAlgoFile;
	}

	public SDFAbstractVertex create(DAGVertex dagVertex)
			throws InvalidExpressionException, SDF4JException, PreesmException {
		CodeGenSDFGraphFactory graphFactory = new CodeGenSDFGraphFactory(
				mainFile);
		ICodeGenSDFVertex newVertex;
		VertexType vertexType = (VertexType) dagVertex.getPropertyBean()
				.getValue(ImplementationPropertyNames.Vertex_vertexType);
		if (vertexType != null && vertexType.equals(VertexType.task)) {
			SDFAbstractVertex sdfVertex = dagVertex.getCorrespondingSDFVertex();
			if (sdfVertex instanceof SDFBroadcastVertex
					&& !(sdfVertex instanceof SDFRoundBufferVertex)) {
				newVertex = new CodeGenSDFBroadcastVertex();
			} else if (sdfVertex instanceof SDFForkVertex) {
				newVertex = new CodeGenSDFForkVertex();
			} else if (sdfVertex instanceof SDFJoinVertex) {
				newVertex = new CodeGenSDFJoinVertex();
			} else if (sdfVertex instanceof SDFRoundBufferVertex) {
				newVertex = new CodeGenSDFRoundBufferVertex();
			} else if (sdfVertex instanceof SDFEndVertex) {
				newVertex = new CodeGenSDFTokenEndVertex();
				((CodeGenSDFTokenEndVertex) newVertex)
						.setEndReference(((SDFInitVertex) sdfVertex)
								.getEndReference());
			} else if (sdfVertex instanceof SDFInitVertex) {
				newVertex = new CodeGenSDFTokenInitVertex();
				((CodeGenSDFTokenInitVertex) newVertex)
						.setEndReference(((SDFInitVertex) sdfVertex)
								.getEndReference());
				((CodeGenSDFTokenInitVertex) newVertex)
						.setInitSize(((SDFInitVertex) sdfVertex).getInitSize());
			} else if (sdfVertex instanceof PSDFInitVertex) {
				newVertex = new CodeGenSDFInitVertex();
			} else {
				newVertex = new CodeGenSDFTaskVertex();
			}
		} else if (vertexType != null && vertexType.equals(VertexType.send)) {
			newVertex = new CodeGenSDFSendVertex();
		} else if (vertexType != null && vertexType.equals(VertexType.receive)) {
			newVertex = new CodeGenSDFReceiveVertex();
		} else {
			newVertex = new CodeGenSDFTaskVertex();
		}
		((SDFAbstractVertex) newVertex).setName(dagVertex.getName());
		if (dagVertex.getCorrespondingSDFVertex() != null
				&& dagVertex.getCorrespondingSDFVertex().getGraphDescription() != null) {
			((SDFAbstractVertex) newVertex).setGraphDescription(graphFactory
					.create(dagVertex.getCorrespondingSDFVertex()
							.getGraphDescription()));
			for (SDFAbstractVertex child : ((CodeGenSDFGraph) newVertex
					.getGraphDescription()).vertexSet()) {
				if (child instanceof SDFSinkInterfaceVertex) {
					((SDFAbstractVertex) newVertex).getSinks().remove(
							(((SDFAbstractVertex) newVertex).getInterface(child
									.getName())));
					((SDFAbstractVertex) newVertex)
							.addSink((SDFSinkInterfaceVertex) child);
				} else if (child instanceof SDFSourceInterfaceVertex) {
					((SDFAbstractVertex) newVertex).getSources().remove(
							(((SDFAbstractVertex) newVertex).getInterface(child
									.getName())));
					((SDFAbstractVertex) newVertex)
							.addSource((SDFSourceInterfaceVertex) child);
				}
			}
		} else if (dagVertex.getCorrespondingSDFVertex() != null
				&& dagVertex.getCorrespondingSDFVertex().getRefinement() instanceof CodeRefinement) {
			CodeRefinement codeRef = (CodeRefinement) dagVertex
					.getCorrespondingSDFVertex().getRefinement();
			IFile iFile = mainFile.getParent().getFile(
					new Path(codeRef.getName()));
			try {
				if (!iFile.exists()) {
					iFile.create(null, false, new NullProgressMonitor());
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}

			if (codeRef.getLanguage() == Language.IDL) {
				IDLFunctionFactory factory = IDLFunctionFactory.getInstance();
				((SDFAbstractVertex) newVertex).setRefinement(factory
						.create(iFile.getRawLocation().toOSString()));
			}
		}
		((SDFAbstractVertex) newVertex).copyProperties(dagVertex);
		if (dagVertex.getCorrespondingSDFVertex() != null
				&& newVertex instanceof CodeGenSDFTaskVertex) {
			((SDFAbstractVertex) newVertex).setNbRepeat(dagVertex
					.getCorrespondingSDFVertex().getNbRepeat());
		}
		if (dagVertex.getCorrespondingSDFVertex() != null
				&& dagVertex.getCorrespondingSDFVertex().getArguments() != null) {
			for (Argument arg : dagVertex.getCorrespondingSDFVertex()
					.getArguments().values()) {
				((SDFAbstractVertex) newVertex).addArgument(arg);
			}
		}

		// if ((ArchitectureComponent) dagVertex.getPropertyBean().getValue(
		// ImplementationPropertyNames.Vertex_Operator) != null) {
		// newVertex.setOperator((ArchitectureComponent) dagVertex
		// .getPropertyBean().getValue(
		// ImplementationPropertyNames.Vertex_Operator));
		// }
		// if ((Integer) dagVertex.getPropertyBean().getValue(
		// ImplementationPropertyNames.Vertex_schedulingOrder) != null) {
		// newVertex.setPos((Integer) dagVertex.getPropertyBean().getValue(
		// ImplementationPropertyNames.Vertex_schedulingOrder));
		// }
		return ((SDFAbstractVertex) newVertex);
	}

	public SDFAbstractVertex create(SDFAbstractVertex sdfVertex)
			throws InvalidExpressionException, SDF4JException, PreesmException {
		SDFAbstractVertex newVertex;
		if (sdfVertex instanceof SDFSinkInterfaceVertex) {
			newVertex = new CodeGenSDFSinkInterfaceVertex();
		} else if (sdfVertex instanceof SDFSourceInterfaceVertex) {
			newVertex = new CodeGenSDFSourceInterfaceVertex();
		} else {
			if (sdfVertex instanceof SDFBroadcastVertex) {
				newVertex = new CodeGenSDFBroadcastVertex();
			} else if (sdfVertex instanceof SDFForkVertex) {
				newVertex = new CodeGenSDFForkVertex();
			} else if (sdfVertex instanceof SDFJoinVertex) {
				newVertex = new CodeGenSDFJoinVertex();
			} else if (sdfVertex instanceof SDFRoundBufferVertex) {
				newVertex = new CodeGenSDFRoundBufferVertex();
			} else if (sdfVertex instanceof SDFInitVertex) {
				newVertex = new CodeGenSDFTokenInitVertex();
			} else if (sdfVertex instanceof PSDFInitVertex) {
				newVertex = new CodeGenSDFInitVertex();
			} else if (sdfVertex instanceof PSDFSubInitVertex) {
				newVertex = new CodeGenSDFSubInitVertex();
			} else {
				newVertex = new CodeGenSDFTaskVertex();
			}
		}
		newVertex.copyProperties(sdfVertex);
		CodeGenSDFGraphFactory graphFactory = new CodeGenSDFGraphFactory(
				mainFile);
		if (sdfVertex.getGraphDescription() != null) {
			newVertex.setGraphDescription(graphFactory.create(sdfVertex
					.getGraphDescription()));
			for (SDFAbstractVertex child : ((CodeGenSDFGraph) newVertex
					.getGraphDescription()).vertexSet()) {
				if (child instanceof SDFSinkInterfaceVertex) {
					newVertex.getSinks().remove(
							(newVertex.getInterface(child.getName())));
					newVertex.addSink((SDFSinkInterfaceVertex) child);
				} else if (child instanceof SDFSourceInterfaceVertex) {
					newVertex.getSources().remove(
							(newVertex.getInterface(child.getName())));
					newVertex.addSource((SDFSourceInterfaceVertex) child);
				}
			}
		} else if (sdfVertex.getRefinement() instanceof CodeRefinement) {
			CodeRefinement codeRef = (CodeRefinement) sdfVertex.getRefinement();
			IFile iFile = mainFile.getParent().getFile(
					new Path(codeRef.getName()));
			try {
				if (!iFile.exists()) {
					iFile.create(null, false, new NullProgressMonitor());
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}

			if (codeRef.getLanguage() == Language.IDL) {
				IDLFunctionFactory factory = IDLFunctionFactory.getInstance();
				newVertex.setRefinement(factory.create(iFile.getRawLocation()
						.toOSString()));
			}
		}
		return newVertex;
	}
}

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

package org.ietr.preesm.codegen.model;

import org.ietr.dftools.algorithm.model.CodeRefinement;
import org.ietr.dftools.algorithm.model.CodeRefinement.Language;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.psdf.PSDFInitVertex;
import org.ietr.dftools.algorithm.model.psdf.PSDFSubInitVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFEndVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFInitVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.codegen.idl.IDLPrototypeFactory;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;
import org.ietr.preesm.core.workflow.PreesmException;

/**
 * Generating code generation vertices from mapped vertices
 * 
 * @author jpiat
 */
public class CodeGenSDFVertexFactory {

	private IFile mainFile;

	public CodeGenSDFVertexFactory(IFile parentAlgoFile) {
		mainFile = parentAlgoFile;
	}

	public SDFAbstractVertex create(DAGVertex dagVertex, IDLPrototypeFactory idlPrototypeFactory)
			throws InvalidExpressionException, SDF4JException, PreesmException {
		CodeGenSDFGraphFactory graphFactory = new CodeGenSDFGraphFactory(
				mainFile);
		ICodeGenSDFVertex newVertex;
		VertexType vertexType = (VertexType) dagVertex.getPropertyBean()
				.getValue(ImplementationPropertyNames.Vertex_vertexType);
		if (vertexType != null && vertexType.equals(VertexType.TASK)) {
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
				newVertex = new CodeGenSDFFifoPushVertex();
				((CodeGenSDFFifoPushVertex) newVertex)
						.setEndReference(((SDFInitVertex) sdfVertex)
								.getEndReference());
			} else if (sdfVertex instanceof SDFInitVertex) {
				newVertex = new CodeGenSDFFifoPullVertex();
				((CodeGenSDFFifoPullVertex) newVertex)
						.setEndReference(((SDFInitVertex) sdfVertex)
								.getEndReference());
				((CodeGenSDFFifoPullVertex) newVertex)
						.setInitSize(((SDFInitVertex) sdfVertex).getInitSize());
			} else if (sdfVertex instanceof PSDFInitVertex) {
				newVertex = new CodeGenSDFInitVertex();
			} else {
				newVertex = new CodeGenSDFTaskVertex();
			}
		} else if (vertexType != null && vertexType.equals(VertexType.SEND)) {
			newVertex = new CodeGenSDFSendVertex();
		} else if (vertexType != null && vertexType.equals(VertexType.RECEIVE)) {
			newVertex = new CodeGenSDFReceiveVertex();
		} else {
			newVertex = new CodeGenSDFTaskVertex();
		}
		((SDFAbstractVertex) newVertex).setName(dagVertex.getName());
		if (dagVertex.getCorrespondingSDFVertex() != null
				&& dagVertex.getCorrespondingSDFVertex().getGraphDescription() != null) {
			((SDFAbstractVertex) newVertex).setGraphDescription(graphFactory
					.create(dagVertex.getCorrespondingSDFVertex()
							.getGraphDescription(), idlPrototypeFactory));
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
			if(codeRef.getLanguage() == Language.TEXT){
				((SDFAbstractVertex) newVertex).setRefinement(codeRef);
			}
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
				IDLPrototypeFactory factory = idlPrototypeFactory;
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

		return ((SDFAbstractVertex) newVertex);
	}

	public SDFAbstractVertex create(SDFAbstractVertex sdfVertex, IDLPrototypeFactory idlPrototypeFactory)
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
				newVertex = new CodeGenSDFFifoPullVertex();
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
					.getGraphDescription(), idlPrototypeFactory));
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
			if(codeRef.getLanguage() == Language.TEXT){
				newVertex.setRefinement(codeRef);
			}
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
				IDLPrototypeFactory factory = idlPrototypeFactory;
				newVertex.setRefinement(factory.create(iFile.getRawLocation()
						.toOSString()));
			}
		}
		return newVertex;
	}
}

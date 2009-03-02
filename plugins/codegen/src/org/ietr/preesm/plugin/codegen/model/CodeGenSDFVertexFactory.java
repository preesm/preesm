package org.ietr.preesm.plugin.codegen.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.VertexType;
import org.ietr.preesm.core.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.codegen.model.CodeGenSDFInitVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSendVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSinkInterfaceVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSourceInterfaceVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFVertex;
import org.ietr.preesm.plugin.codegen.model.cal.CALFunctionFactory;
import org.ietr.preesm.plugin.codegen.model.idl.IDLFunctionFactory;
import org.sdf4j.model.CodeRefinement;
import org.sdf4j.model.CodeRefinement.Language;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.esdf.SDFBroadcastVertex;
import org.sdf4j.model.sdf.esdf.SDFForkVertex;
import org.sdf4j.model.sdf.esdf.SDFInitVertex;
import org.sdf4j.model.sdf.esdf.SDFJoinVertex;
import org.sdf4j.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSourceInterfaceVertex;

/**
 * @author jpiat
 */
public class CodeGenSDFVertexFactory {
	
	private IFile mainFile ;
	
	public CodeGenSDFVertexFactory(IFile parentAlgoFile){
		mainFile = parentAlgoFile ;
	}

	public SDFAbstractVertex create(DAGVertex dagVertex) {
		CodeGenSDFGraphFactory graphFactory = new CodeGenSDFGraphFactory(mainFile);
		CodeGenSDFVertex newVertex;
		VertexType vertexType = (VertexType) dagVertex.getPropertyBean().getValue(
				ImplementationPropertyNames.Vertex_vertexType);
		if (vertexType != null && vertexType.equals(VertexType.task)) {
			SDFAbstractVertex sdfVertex = dagVertex.getCorrespondingSDFVertex();
			if (sdfVertex instanceof SDFBroadcastVertex) {
				newVertex = new CodeGenSDFBroadcastVertex();
			} else if (sdfVertex instanceof SDFForkVertex) {
				newVertex = new CodeGenSDFForkVertex();
			} else if (sdfVertex instanceof SDFJoinVertex) {
				newVertex = new CodeGenSDFJoinVertex();
			} else if (sdfVertex instanceof SDFInitVertex) {
				newVertex = new CodeGenSDFInitVertex();
			} else {
				newVertex = new CodeGenSDFVertex();
			}
		} else if (vertexType != null && vertexType.equals(VertexType.send)) {
			newVertex = new CodeGenSDFSendVertex();
		} else if (vertexType != null && vertexType.equals(VertexType.receive)) {
			newVertex = new CodeGenSDFReceiveVertex();
		} else {
			newVertex = new CodeGenSDFVertex();
		}
		newVertex.setName(dagVertex.getName());
		if (dagVertex.getCorrespondingSDFVertex() != null && dagVertex.getCorrespondingSDFVertex().getGraphDescription() != null) {
			newVertex.setGraphDescription(graphFactory
					.create((SDFGraph) dagVertex.getCorrespondingSDFVertex()
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
		}else if(dagVertex.getCorrespondingSDFVertex() != null && dagVertex.getCorrespondingSDFVertex().getRefinement() instanceof CodeRefinement){
			CodeRefinement codeRef = (CodeRefinement) dagVertex.getCorrespondingSDFVertex().getRefinement();
			IFile iFile = mainFile.getParent().getFile(new Path(codeRef.getName()));
			try {
				if (!iFile.exists()) {
					iFile.create(null, false, new NullProgressMonitor());
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
			if(codeRef.getLanguage() == Language.CAL){
				CALFunctionFactory factory = CALFunctionFactory.getInstance();
				newVertex.setRefinement(factory.create(iFile.getRawLocation().toOSString()));
			}else if(codeRef.getLanguage() == Language.IDL){
				IDLFunctionFactory factory = IDLFunctionFactory.getInstance();
				newVertex.setRefinement(factory.create(iFile.getRawLocation().toOSString()));
			}
		}
		if ((ArchitectureComponent) dagVertex.getPropertyBean().getValue(
				ImplementationPropertyNames.Vertex_Operator) != null) {
			newVertex.setOperator((ArchitectureComponent) dagVertex
					.getPropertyBean().getValue(
							ImplementationPropertyNames.Vertex_Operator));
		}
		if ((Integer) dagVertex.getPropertyBean().getValue(
				ImplementationPropertyNames.Vertex_schedulingOrder) != null) {
			newVertex.setPos((Integer) dagVertex.getPropertyBean().getValue(
					ImplementationPropertyNames.Vertex_schedulingOrder));
		}
		return newVertex;
	}

	public SDFAbstractVertex create(SDFAbstractVertex sdfVertex) {
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
			} else if (sdfVertex instanceof SDFInitVertex) {
				newVertex = new CodeGenSDFInitVertex();
			} else {
				newVertex = new CodeGenSDFVertex();
			}
		}
		newVertex.copyProperties(sdfVertex);
		CodeGenSDFGraphFactory graphFactory = new CodeGenSDFGraphFactory(mainFile);
		if (sdfVertex.getGraphDescription() != null) {
			newVertex.setGraphDescription(graphFactory
					.create((SDFGraph) sdfVertex.getGraphDescription()));
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
		}else if(sdfVertex.getRefinement() instanceof CodeRefinement){
			CodeRefinement codeRef = (CodeRefinement) sdfVertex.getRefinement();
			IFile iFile = mainFile.getParent().getFile(new Path(codeRef.getName()));
			try {
				if (!iFile.exists()) {
					iFile.create(null, false, new NullProgressMonitor());
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
			if(codeRef.getLanguage() == Language.CAL){
				CALFunctionFactory factory = CALFunctionFactory.getInstance();
				newVertex.setRefinement(factory.create(iFile.getRawLocation().toOSString()));
			}else if(codeRef.getLanguage() == Language.IDL){
				IDLFunctionFactory factory = IDLFunctionFactory.getInstance();
				newVertex.setRefinement(factory.create(iFile.getRawLocation().toOSString()));
			}
		}
		return newVertex;
	}
}

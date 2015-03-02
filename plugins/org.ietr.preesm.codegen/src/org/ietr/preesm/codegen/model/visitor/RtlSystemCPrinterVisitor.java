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

package org.ietr.preesm.codegen.model.visitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.CodeRefinement;
import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.parameters.Parameter;
import org.ietr.dftools.algorithm.model.parameters.Variable;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFEdge;
import org.ietr.preesm.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFReceiveVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFSendVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFTaskVertex;

public class RtlSystemCPrinterVisitor extends SystemCPrinterVisitor {

	private static String rtl_template = "preesm_rtl_systemc.stg";
	private boolean isWrapper;

	protected List<StringTemplate> endRules;
	protected List<StringTemplate> startRules;

	public RtlSystemCPrinterVisitor(String templatePath, String outputPath) {
		super();
		try {
			endRules = new ArrayList<StringTemplate>();
			startRules = new ArrayList<StringTemplate>();
			this.templatePath = templatePath;
			group = new StringTemplateGroup(new FileReader(templatePath
					+ File.separator + rtl_template));
			this.outputPath = outputPath;
			isWrapper = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public RtlSystemCPrinterVisitor(StringTemplateGroup templateGroup,
			String outputPath) {
		super();
		endRules = new ArrayList<StringTemplate>();
		startRules = new ArrayList<StringTemplate>();
		group = templateGroup;
		this.outputPath = outputPath;
		isWrapper = false;
	}

	@Override
	public void visit(CodeGenSDFEdge sdfEdge) {
		String fifoName = new String();
		if (!(sdfEdge.getSource() instanceof SDFSourceInterfaceVertex || sdfEdge
				.getTarget() instanceof SDFSinkInterfaceVertex) || isWrapper) {
			fifoName = sdfEdge.getTarget().getName() + "_"
					+ sdfEdge.getSource().getName() + "_"
					+ sdfEdge.getTargetInterface().getName();

			StringTemplate edgeDeclarationTemplate = group
					.getInstanceOf("edge_declaration");
			StringTemplate edgeInstanciationTemplate = group
					.getInstanceOf("edge_instanciation");
			edgeDeclarationTemplate.setAttribute("name", fifoName);
			edgeInstanciationTemplate.setAttribute("name", fifoName);
			edgeDeclarationTemplate.setAttribute("type", sdfEdge.getDataType()
					.toString());
			edgeInstanciationTemplate.setAttribute("size", sdfEdge.getSize());
			edges_instanciations.add(edgeInstanciationTemplate);
			edges_declarations.add(edgeDeclarationTemplate);
		}

		/*
		 * try { if (sdfEdge.getDelay().intValue() > 0) { StringTemplate
		 * edgeDelayTemplate = group .getInstanceOf("edge_delay");
		 * edgeDelayTemplate.setAttribute("fifo", fifoName);
		 * edgeDelayTemplate.setAttribute("delay_size", sdfEdge.getDelay()
		 * .intValue()); edgeDelayTemplate.setAttribute("delay_value", 0);
		 * edge_delay_init.add(edgeDelayTemplate); } } catch
		 * (InvalidExpressionException e) { e.printStackTrace(); }
		 */
		StringTemplate srcConnection = group.getInstanceOf("connection");
		if (sdfEdge.getTarget() instanceof SDFSinkInterfaceVertex && !isWrapper) {
			srcConnection.setAttribute("actor", sdfEdge.getSource().getName());
			srcConnection.setAttribute("edge", sdfEdge.getTarget().getName());
		} else {
			srcConnection.setAttribute("actor", sdfEdge.getSource().getName());
			srcConnection.setAttribute("edge", fifoName);
		}
		if (sdfEdge.getSource() instanceof CodeGenSDFForkVertex) {
			CodeGenSDFForkVertex forkSource = ((CodeGenSDFForkVertex) sdfEdge
					.getSource());
			int edgeIndex = forkSource.getEdgeIndex(sdfEdge);
			srcConnection.setAttribute("port", "outs[" + edgeIndex + "]");
		} else if (sdfEdge.getSource() instanceof CodeGenSDFBroadcastVertex) {
			CodeGenSDFBroadcastVertex broadcastSource = ((CodeGenSDFBroadcastVertex) sdfEdge
					.getSource());
			int edgeIndex = broadcastSource.getEdgeIndex(sdfEdge);
			srcConnection.setAttribute("port", "outs[" + edgeIndex + "]");
		} else if (sdfEdge.getSource() instanceof CodeGenSDFJoinVertex) {
			srcConnection.setAttribute("port", "out");
		} else if (sdfEdge.getSource() instanceof CodeGenSDFSendVertex
				|| sdfEdge.getSource() instanceof CodeGenSDFReceiveVertex) {
			srcConnection.setAttribute("port", "out");
		} else {
			srcConnection.setAttribute("port", sdfEdge.getSourceInterface()
					.getName());
		}

		if (!(sdfEdge.getSource() instanceof SDFSourceInterfaceVertex)) {
			connections.add(srcConnection);
		}

		StringTemplate trgtConnection = group.getInstanceOf("connection");
		if (sdfEdge.getSource() instanceof SDFSourceInterfaceVertex
				&& !isWrapper) {
			trgtConnection.setAttribute("actor", sdfEdge.getTarget().getName());
			trgtConnection.setAttribute("edge", sdfEdge.getSource().getName());
		} else {
			trgtConnection.setAttribute("actor", sdfEdge.getTarget().getName());
			trgtConnection.setAttribute("edge", fifoName);
		}
		if (sdfEdge.getTarget() instanceof CodeGenSDFJoinVertex) {
			CodeGenSDFJoinVertex joinTarget = ((CodeGenSDFJoinVertex) sdfEdge
					.getTarget());
			int edgeIndex = joinTarget.getEdgeIndex(sdfEdge);
			trgtConnection.setAttribute("port", "ins[" + edgeIndex + "]");
		} else if (sdfEdge.getTarget() instanceof CodeGenSDFForkVertex) {
			trgtConnection.setAttribute("port", "in");
		} else if (sdfEdge.getTarget() instanceof CodeGenSDFSendVertex
				|| sdfEdge.getTarget() instanceof CodeGenSDFReceiveVertex) {
			trgtConnection.setAttribute("port", "in");
		} else {
			trgtConnection.setAttribute("port", sdfEdge.getTargetInterface()
					.getName());
		}

		if (!(sdfEdge.getTarget() instanceof SDFSinkInterfaceVertex)) {
			connections.add(trgtConnection);
		}

	}

	@Override
	public void visit(CodeGenSDFGraph sdf) throws SDF4JException {
		for (SDFEdge edge : sdf.edgeSet()) {
			edge.accept(this);
		}
		for (SDFAbstractVertex vertex : sdf.vertexSet()) {
			if (vertex instanceof SDFInterfaceVertex) {
				treatWrapperInterface((SDFInterfaceVertex) vertex, null, sdf,
						ports, firingRules, firingRulesSensitivityList);
			} else {
				vertex.accept(this);
			}
		}

		String graphName = sdf.getName();
		if (graphName.contains(".")) {
			graphName = graphName.substring(0, graphName.lastIndexOf('.'));
		}

		StringTemplate actorDeclarationTemplate;
		if (isWrapper) {
			actorDeclarationTemplate = group
					.getInstanceOf("wrapper_declaration");
			actorDeclarationTemplate.setAttribute("end_rules", endRules);
			actorDeclarationTemplate.setAttribute("start_rules", startRules);
		} else {
			actorDeclarationTemplate = group.getInstanceOf("actor_declaration");
		}

		actorDeclarationTemplate.setAttribute("name", graphName);
		actorDeclarationTemplate.setAttribute("ports", ports);
		actorDeclarationTemplate.setAttribute("actor_declarations",
				actor_declarations);
		actorDeclarationTemplate.setAttribute("edge_declarations",
				edges_declarations);
		actorDeclarationTemplate.setAttribute("connections", connections);
		actorDeclarationTemplate.setAttribute("edges_instanciations",
				edges_instanciations);
		// actorDeclarationTemplate.setAttribute("body", actorBodyTemplate);
		actorDeclarationTemplate.setAttribute("firing_rules", firingRules);
		actorDeclarationTemplate.setAttribute("firing_rules_sensitivity",
				firingRulesSensitivityList);
		actorDeclarationTemplate.setAttribute("actor_instanciations",
				actor_instanciations);
		actorDeclarationTemplate.setAttribute("edge_delay", edge_delay_init);

		defines.clear();
		if (sdf.getVariables() != null) {
			for (Variable var : sdf.getVariables().values()) {
				StringTemplate variableDeclatationTemplate = group
						.getInstanceOf("define");
				variableDeclatationTemplate
						.setAttribute("label", var.getName());
				variableDeclatationTemplate.setAttribute("value",
						var.getValue());
				defines.add(variableDeclatationTemplate);
			}
		}

		if (!isTestBed && sdf.getParameters() != null
				&& sdf.getParameters().size() > 0) {
			List<String> parNames = new ArrayList<String>();
			for (Parameter par : sdf.getParameters().values()) {
				parNames.add(par.getName().toLowerCase());
			}
			actorDeclarationTemplate.setAttribute("generics", parNames);
		}

		StringTemplate fileTemplate = group.getInstanceOf("actor_file");
		fileTemplate.setAttribute("defines", defines);
		fileTemplate.setAttribute("includes", includes);
		fileTemplate.setAttribute("actor", actorDeclarationTemplate);
		fileTemplate.setAttribute("symbol", graphName.toUpperCase() + "_H");

		IPath path = new Path(this.outputPath);
		String extension;
		if (isTestBed) {
			extension = ".cpp";
		} else {
			extension = ".h";
		}

		path = path.append(graphName + extension);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String fsPath;
		if (workspace.getRoot().getFile(path).getLocation() != null) {
			fsPath = workspace.getRoot().getFile(path).getLocation()
					.toOSString();
		} else {
			fsPath = path.toString();
		}

		File printFile = new File(fsPath);
		FileWriter fileWriter;
		try {
			if (!printFile.exists()) {
				printFile.createNewFile();
			}
			fileWriter = new FileWriter(printFile);
			fileWriter.write(fileTemplate.toString());
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(actorDeclarationTemplate);
	}

	void treatWrapperInterface(SDFInterfaceVertex interfaceVertex,
			SDFAbstractVertex parentVertex, SDFGraph parentGraph,
			List<StringTemplate> actorPorts,
			List<StringTemplate> actorFiringRules,
			List<StringTemplate> actorFiringRulesSensitivity) {
		StringTemplate interfaceTemplate;
		if (interfaceVertex instanceof SDFSourceInterfaceVertex) {
			interfaceTemplate = group.getInstanceOf("wrapper_input");
		} else if (interfaceVertex instanceof SDFSinkInterfaceVertex) {
			interfaceTemplate = group.getInstanceOf("wrapper_output");
		} else {
			interfaceTemplate = group.getInstanceOf("wrapper_output");
		}
		if (interfaceVertex.getDataType() != null) {
			interfaceTemplate.setAttribute("type",
					interfaceVertex.getDataType());
		} else {
			if (interfaceVertex instanceof SDFSourceInterfaceVertex
					&& parentGraph.incomingEdgesOf(parentVertex) != null) {
				for (SDFEdge edge : parentGraph.incomingEdgesOf(parentVertex)) {
					if (edge.getTargetInterface().equals(interfaceVertex)) {
						interfaceTemplate.setAttribute("type",
								edge.getDataType());
						break;
					}
				}
			}
			if (interfaceVertex instanceof SDFSinkInterfaceVertex
					&& parentGraph.outgoingEdgesOf(parentVertex) != null) {
				for (SDFEdge edge : parentGraph.outgoingEdgesOf(parentVertex)) {
					if (edge.getSourceInterface().equals(interfaceVertex)) {
						interfaceTemplate.setAttribute("type",
								edge.getDataType());
						break;
					}
				}
			}
		}
		interfaceTemplate.setAttribute("name", interfaceVertex.getName());
		actorPorts.add(interfaceTemplate);

		if (interfaceVertex instanceof SDFSourceInterfaceVertex) {
			Object interfaceSize = null;
			StringTemplate sdfFiringRuleTemplate = group
					.getInstanceOf("sdf_firing_rule");
			sdfFiringRuleTemplate.setAttribute("port",
					interfaceVertex.getName());
			for (SDFEdge edge : parentGraph.edgeSet()) {
				if (edge.getSource().equals(interfaceVertex)
						|| (edge.getTargetInterface().equals(interfaceVertex) && edge
								.getTarget().equals(parentVertex))) {
					interfaceSize = edge.getCons();
					StringTemplate portEvent = group
							.getInstanceOf("port_event");
					portEvent.setAttribute("port", interfaceVertex.getName());
					actorFiringRulesSensitivity.add(portEvent);
					break;
				}
			}
			if (interfaceSize != null) {
				sdfFiringRuleTemplate.setAttribute("nb_tokens", interfaceSize
						.toString().toLowerCase()); // TODO: need to get cleaner
													// lower case version
				actorFiringRules.add(sdfFiringRuleTemplate);
			}
		}

	}

	private String getTriggeringSignal(SDFAbstractVertex v) {
		for (SDFEdge incEdge : ((SDFGraph) v.getBase()).incomingEdgesOf(v)) {
			if (incEdge.getSource() instanceof SDFSourceInterfaceVertex) {
				if (isWrapper) {
					return "enable_pipeline";
				} else {
					return "invoke";
				}
			} else if (incEdge.getSource() != v) { // avoid state edges ...
				return "dv_" + incEdge.getSource().getName();
			}
		}
		return "";
	}

	@SuppressWarnings("unused")
	private boolean isPipelineEndVertex(SDFAbstractVertex v) {
		for (SDFEdge outEdge : ((SDFGraph) v.getBase()).outgoingEdgesOf(v)) {
			if (outEdge.getTarget() instanceof SDFSinkInterfaceVertex) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void visit(SDFAbstractVertex sdfVertex) throws SDF4JException {
		StringTemplate vertexInstanciationTemplate = group
				.getInstanceOf("vertex_instanciation");

		StringTemplate vertexDeclarationTemplate;

		vertexDeclarationTemplate = group.getInstanceOf("vertex_declaration");
		if (sdfVertex instanceof CodeGenSDFBroadcastVertex) {
			StringTemplate broadcastTemplate = broadcastTemplateAttribute((CodeGenSDFBroadcastVertex) sdfVertex);
			vertexDeclarationTemplate.setAttribute("type_template",
					broadcastTemplate);
			vertexDeclarationTemplate.setAttribute("type",
					"preesm_rtl_broadcast");
			if (!includes.contains("preesm_rtl_broadcast")) {
				includes.add("preesm_rtl_broadcast");
			}
		} else if (sdfVertex instanceof CodeGenSDFJoinVertex) {
			StringTemplate joinTemplate = joinTemplateAttribute((CodeGenSDFJoinVertex) sdfVertex);
			vertexDeclarationTemplate.setAttribute("type_template",
					joinTemplate);
			vertexDeclarationTemplate.setAttribute("type", "preesm_rtl_join");
			if (!includes.contains("preesm_rtl_join")) {
				includes.add("preesm_rtl_join");
			}
		} else if (sdfVertex instanceof CodeGenSDFForkVertex) {
			StringTemplate forkTemplate = forkTemplateAttribute((CodeGenSDFForkVertex) sdfVertex);
			vertexDeclarationTemplate.setAttribute("type_template",
					forkTemplate);
			vertexDeclarationTemplate.setAttribute("type", "preesm_rtl_fork");
			if (!includes.contains("preesm_rtl_fork")) {
				includes.add("preesm_rtl_fork");
			}
		} else if (sdfVertex instanceof CodeGenSDFSendVertex) {
			StringTemplate sendTemplate = sendTemplateAttribute((CodeGenSDFSendVertex) sdfVertex);
			vertexDeclarationTemplate.setAttribute("type_template",
					sendTemplate);
			vertexDeclarationTemplate.setAttribute("type", "preesm_send");
			if (!includes.contains("preesm_send")) {
				includes.add("preesm_send");
			}
		} else if (sdfVertex instanceof CodeGenSDFReceiveVertex) {
			StringTemplate recTemplate = receiveTemplateAttribute((CodeGenSDFReceiveVertex) sdfVertex);
			vertexDeclarationTemplate
					.setAttribute("type_template", recTemplate);
			vertexDeclarationTemplate.setAttribute("type", "preesm_receive");
			if (!includes.contains("preesm_receive")) {
				includes.add("preesm_receive");
			}
		} else if (sdfVertex instanceof CodeGenSDFTaskVertex) {
			String refinementName = null;
			if (((CodeGenSDFTaskVertex) sdfVertex).getRefinement() != null
					&& ((CodeGenSDFTaskVertex) sdfVertex).getRefinement() instanceof ActorPrototypes) {
				refinementName = ((ActorPrototypes) ((CodeGenSDFTaskVertex) sdfVertex)
						.getRefinement()).getLoopPrototype().getFunctionName();
				if (!includes.contains(refinementName)) {
					includes.add(refinementName);
				}
				exportAtomicActor((CodeGenSDFTaskVertex) sdfVertex);
			} else if (((CodeGenSDFTaskVertex) sdfVertex).getRefinement() != null
					&& ((CodeGenSDFTaskVertex) sdfVertex).getRefinement() instanceof CodeRefinement) {
				refinementName = sdfVertex.getRefinement().toString();
				if (!includes.contains(refinementName)) {
					includes.add(refinementName);
				}
				exportAtomicActor((CodeGenSDFTaskVertex) sdfVertex);
			} else if (((CodeGenSDFTaskVertex) sdfVertex).getRefinement() != null
					&& ((CodeGenSDFTaskVertex) sdfVertex).getRefinement() instanceof CodeGenSDFGraph) {
				CodeGenSDFGraph refGraph = ((CodeGenSDFGraph) ((CodeGenSDFTaskVertex) sdfVertex)
						.getRefinement());
				refinementName = refGraph.getName();
				if (refinementName.lastIndexOf(".") > 0) {
					refinementName = refinementName.substring(0,
							refinementName.lastIndexOf("."));
				}
				if (!includes.contains(refinementName)) {
					includes.add(refinementName);
				}
				RtlSystemCPrinterVisitor childVisitor = new RtlSystemCPrinterVisitor(
						this.group, this.outputPath);

				refGraph.accept(childVisitor);
			}
			vertexDeclarationTemplate.setAttribute("type", refinementName);
		} else {
			vertexDeclarationTemplate.setAttribute("type", sdfVertex.getName());
		}
		vertexInstanciationTemplate.setAttribute("name", sdfVertex.getName());
		vertexDeclarationTemplate.setAttribute("name", sdfVertex.getName());
		if (sdfVertex.getArguments() != null
				&& sdfVertex.getArguments().size() > 0) {
			List<String> argVals = new ArrayList<String>();
			for (Argument arg : sdfVertex.getArguments().values()) {
				argVals.add(arg.getValue());
			}
			vertexInstanciationTemplate.setAttribute("args", argVals);
			vertexDeclarationTemplate.setAttribute("args", argVals);
		}

		actor_declarations.add(vertexDeclarationTemplate);
		actor_instanciations.add(vertexInstanciationTemplate);

		StringTemplate signalDeclarationTemplate = group
				.getInstanceOf("signal_declaration");
		signalDeclarationTemplate.setAttribute("name",
				"dv_" + sdfVertex.getName());
		signalDeclarationTemplate.setAttribute("type", "bool");
		edges_declarations.add(signalDeclarationTemplate);
		StringTemplate enableConnection = group.getInstanceOf("connection");
		enableConnection.setAttribute("actor", sdfVertex.getName());
		enableConnection.setAttribute("port", "dv");
		enableConnection.setAttribute("edge", "dv_" + sdfVertex.getName());
		connections.add(enableConnection);

		StringTemplate invokeConnection = group.getInstanceOf("connection");
		invokeConnection.setAttribute("actor", sdfVertex.getName());
		invokeConnection.setAttribute("port", "invoke_port");
		invokeConnection.setAttribute("edge", getTriggeringSignal(sdfVertex));
		connections.add(invokeConnection);

		StringTemplate clockConnection = group.getInstanceOf("connection");
		clockConnection.setAttribute("actor", sdfVertex.getName());
		clockConnection.setAttribute("port", "clk");
		clockConnection.setAttribute("edge", "clk");
		connections.add(clockConnection);

		if (isWrapper) {
			for (SDFEdge outEdge : ((SDFGraph) sdfVertex.getBase())
					.outgoingEdgesOf(sdfVertex)) {
				if (outEdge.getTarget() instanceof SDFSinkInterfaceVertex) {
					String dataName = outEdge.getTarget().getName() + "_"
							+ outEdge.getSource().getName() + "_"
							+ outEdge.getTargetInterface().getName();
					StringTemplate endRuleTemplate = group
							.getInstanceOf("sdf_end_rule");
					endRuleTemplate.setAttribute("dv_port",
							"dv_" + sdfVertex.getName());
					endRuleTemplate.setAttribute("data_signal", dataName);
					endRuleTemplate.setAttribute("data_fifo", outEdge
							.getTarget().getName());
					endRules.add(endRuleTemplate);
				}
			}
			for (SDFEdge incEdge : ((SDFGraph) sdfVertex.getBase())
					.incomingEdgesOf(sdfVertex)) {
				if (incEdge.getSource() instanceof SDFSourceInterfaceVertex) {
					String dataName = incEdge.getTarget().getName() + "_"
							+ incEdge.getSource().getName() + "_"
							+ incEdge.getTargetInterface().getName();
					StringTemplate startRuleTemplate = group
							.getInstanceOf("sdf_start_rule");
					startRuleTemplate.setAttribute("data_signal", dataName);
					startRuleTemplate.setAttribute("data_fifo", incEdge
							.getSource().getName());
					startRules.add(startRuleTemplate);
				}
			}
		}

	}

	@Override
	public void exportAtomicActor(CodeGenSDFTaskVertex actomicActor) {

		List<StringTemplate> atomicPorts = new ArrayList<StringTemplate>();
		List<StringTemplate> atomicFiringRules = new ArrayList<StringTemplate>();
		List<StringTemplate> atomicFiringRulesSensitivityList = new ArrayList<StringTemplate>();

		String functionName;
		if (actomicActor.getRefinement() instanceof ActorPrototypes) {
			functionName = ((ActorPrototypes) actomicActor.getRefinement())
					.getLoopPrototype().getFunctionName();
		} else {
			functionName = actomicActor.getRefinement().toString();
		}

		for (IInterface port : actomicActor.getInterfaces()) {
			treatInterface((SDFInterfaceVertex) port, actomicActor,
					(SDFGraph) actomicActor.getBase(), atomicPorts,
					atomicFiringRules, atomicFiringRulesSensitivityList);
		}

		StringTemplate actorDeclarationTemplate = group
				.getInstanceOf("actor_declaration");

		actorDeclarationTemplate.setAttribute("name", functionName);
		actorDeclarationTemplate.setAttribute("ports", atomicPorts);

		if (actomicActor.getArguments() != null
				&& actomicActor.getArguments().size() > 0) {
			List<String> argNames = new ArrayList<String>();
			for (Argument arg : actomicActor.getArguments().values()) {
				argNames.add(arg.getName().toLowerCase());
			}
			actorDeclarationTemplate.setAttribute("generics", argNames);
		}

		actorDeclarationTemplate
				.setAttribute("firing_rules", atomicFiringRules);
		actorDeclarationTemplate.setAttribute("firing_rules_sensitivity",
				atomicFiringRulesSensitivityList);

		StringTemplate fileTemplate = group.getInstanceOf("actor_file");
		fileTemplate.setAttribute("actor", actorDeclarationTemplate);
		fileTemplate.setAttribute("symbol", functionName.toUpperCase() + "_H");

		IPath path = new Path(this.outputPath);
		path = path.append(functionName + ".h");
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String fsPath;
		if (workspace.getRoot().getFile(path).getLocation() != null) {
			fsPath = workspace.getRoot().getFile(path).getLocation()
					.toOSString();
		} else {
			fsPath = path.toString();
		}

		File printFile = new File(fsPath);
		FileWriter fileWriter;
		try {
			if (!printFile.exists()) {
				printFile.createNewFile();
			}
			fileWriter = new FileWriter(printFile);
			fileWriter.write(fileTemplate.toString());
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(actorDeclarationTemplate);
	}

	@Override
	StringTemplate broadcastTemplateAttribute(
			CodeGenSDFBroadcastVertex broadcastVertex) {
		StringTemplate template_attribute = group
				.getInstanceOf("template_attribute");
		List<String> attributes = new ArrayList<String>();
		int nb_output = ((CodeGenSDFGraph) broadcastVertex.getBase())
				.outgoingEdgesOf(broadcastVertex).size();
		int outputSize = 1;
		String dataType = "char";
		for (SDFEdge edge : ((SDFGraph) broadcastVertex.getBase())
				.outgoingEdgesOf(broadcastVertex)) {
			try {
				outputSize = edge.getProd().intValue();
			} catch (InvalidExpressionException e) {

			}
			if (edge.getDataType() != null) {
				dataType = edge.getDataType().toString();
			}
		}
		attributes.add(dataType);
		attributes.add(String.valueOf(outputSize));
		attributes.add(String.valueOf(nb_output));
		template_attribute.setAttribute("attributes", attributes);
		return template_attribute;
	}

	@Override
	StringTemplate joinTemplateAttribute(CodeGenSDFJoinVertex joinVertex) {
		StringTemplate template_attribute = group
				.getInstanceOf("template_attribute");
		List<String> attributes = new ArrayList<String>();
		int nb_input = ((CodeGenSDFGraph) joinVertex.getBase())
				.incomingEdgesOf(joinVertex).size();
		int inputSize = 1;
		String dataType = "char";
		for (SDFEdge edge : ((SDFGraph) joinVertex.getBase())
				.incomingEdgesOf(joinVertex)) {
			try {
				inputSize = edge.getProd().intValue();
			} catch (InvalidExpressionException e) {

			}
			if (edge.getDataType() != null) {
				dataType = edge.getDataType().toString();
			}
		}
		attributes.add(dataType);
		attributes.add(String.valueOf(inputSize));
		attributes.add(String.valueOf(nb_input));
		template_attribute.setAttribute("attributes", attributes);
		return template_attribute;
	}

	@Override
	StringTemplate forkTemplateAttribute(CodeGenSDFForkVertex forkVertex) {
		StringTemplate template_attribute = group
				.getInstanceOf("template_attribute");
		List<String> attributes = new ArrayList<String>();
		int nb_output = ((CodeGenSDFGraph) forkVertex.getBase())
				.outgoingEdgesOf(forkVertex).size();
		int inputSize = 1;
		String dataType = "char";
		for (SDFEdge edge : ((SDFGraph) forkVertex.getBase())
				.incomingEdgesOf(forkVertex)) {
			try {
				inputSize = edge.getCons().intValue();
			} catch (InvalidExpressionException e) {

			}
			if (edge.getDataType() != null) {
				dataType = edge.getDataType().toString();
			}
		}
		attributes.add(dataType);
		attributes.add(String.valueOf(inputSize));
		attributes.add(String.valueOf(nb_output));
		template_attribute.setAttribute("attributes", attributes);
		return template_attribute;
	}

	@Override
	StringTemplate sendTemplateAttribute(CodeGenSDFSendVertex sendVertex) {
		StringTemplate template_attribute = group
				.getInstanceOf("template_attribute");
		List<String> attributes = new ArrayList<String>();
		int inputSize = 1;
		String dataType = "char";
		for (SDFEdge edge : ((SDFGraph) sendVertex.getBase())
				.incomingEdgesOf(sendVertex)) {
			try {
				inputSize = edge.getCons().intValue();
			} catch (InvalidExpressionException e) {

			}
			if (edge.getDataType() != null) {
				dataType = edge.getDataType().toString();
			}
		}
		attributes.add(dataType);
		attributes.add(String.valueOf(inputSize));
		template_attribute.setAttribute("attributes", attributes);
		return template_attribute;
	}

	@Override
	StringTemplate receiveTemplateAttribute(CodeGenSDFReceiveVertex recVertex) {
		StringTemplate template_attribute = group
				.getInstanceOf("template_attribute");
		List<String> attributes = new ArrayList<String>();
		int inputSize = 1;
		String dataType = "char";
		for (SDFEdge edge : ((SDFGraph) recVertex.getBase())
				.incomingEdgesOf(recVertex)) {
			try {
				inputSize = edge.getCons().intValue();
			} catch (InvalidExpressionException e) {

			}
			if (edge.getDataType() != null) {
				dataType = edge.getDataType().toString();
			}
		}
		attributes.add(dataType);
		attributes.add(String.valueOf(inputSize));
		template_attribute.setAttribute("attributes", attributes);
		return template_attribute;
	}

}

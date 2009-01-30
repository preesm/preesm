package org.ietr.preesm.plugin.codegen.model;

import org.sdf4j.model.sdf.SDFGraph;

public class CodeGenSDFGraph extends SDFGraph{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4150105790430446583L;
	
	public CodeGenSDFGraph(String name){
		super(new CodeGenSDFEdgeFactory());
		this.setName(name);
	}
	
	public CodeGenSDFGraph(CodeGenSDFEdgeFactory factory){
		super(factory);
	}

}

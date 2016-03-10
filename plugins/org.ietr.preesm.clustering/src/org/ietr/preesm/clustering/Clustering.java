package org.ietr.preesm.clustering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.evaluator.SDFDoubleEdgePropertyType;

public class Clustering extends AbstractTaskImplementation {

	Logger logger = WorkflowLogger.getLogger();
	
	@Override
	public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
			IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {
		Map<String, Object> outputs = new HashMap<String, Object>();
		SDFGraph inputSdf = (SDFGraph) inputs.get("SDF");
		SDFGraph outputSdf = inputSdf.clone();
		p("Hello here is my new workflow Clustering " + inputs.toString());
		//computeHierarchizedGraph(outputSdf);
		outputs.put("SDF", outputSdf);
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> defaultParams = new HashMap<String, String>();
		defaultParams.put("factor", "1");
		return defaultParams;
	}

	@Override
	public String monitorMessage() {
		return "Execute Clustering.";
	}
	
	private SDFGraph computeHierarchizedGraph(SDFGraph sdfGraph) {
		
		// go through all vertexes
		for(SDFAbstractVertex hVertex : sdfGraph.vertexSet()){
			try {
				int nbRepeat = hVertex.getNbRepeatAsInteger();
				if(nbRepeat > 1){
					/* update source cons */
					/*for(int i=0;i<hVertex.getSources().size();i++){
						SDFEdge e = hVertex.getAssociatedEdge(hVertex.getSources().get(i));
						p("update edge source cons " + e.getSourceLabel() + " " + e.getTargetLabel() + 
								" from " + e.getCons().intValue() + " to " + e.getCons().intValue()*nbRepeat);
						e.setCons(new SDFDoubleEdgePropertyType(nbRepeat*e.getCons().intValue()));
					}*/
					/* update target prod */
					/*for(int i=0;i<hVertex.getSinks().size();i++){
						SDFEdge e = hVertex.getAssociatedEdge(hVertex.getSinks().get(i));
						p("update edge target prod " + e.getSourceLabel() + " " + e.getTargetLabel() +
							" from " + e.getProd().intValue() + " to " + e.getProd().intValue()*nbRepeat);
						e.setProd(new SDFDoubleEdgePropertyType(nbRepeat*e.getProd().intValue()));
					}*/
		
					//SDFAbstractVertex v = hVertex.clone();  // v is the actor which is going to be repeated (for loop)
					//hVertex.setNbRepeat(1); 				// hierarchical actor triggered one time
					//SDFGraph ng = new SDFGraph();			// related sdf graph to the hierarchical actor
					//hVertex.setGraphDescription(ng);
					//ng.addVertex(v);
					/*for(int i=0; i<hVertex.getGraphDescription().edgeSet().size();i++ ){
						p(hVertex.getGraphDescription().edgeSet().toArray()[i].toString());
					}*/
					//hVertex.getGraphDescription().
				}
				p("vertice " + hVertex.getName() + " rep " + hVertex.getNbRepeatAsInteger());
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*p("Start printing");
		for(SDFAbstractVertex iv : sdfGraph.vertexSet()){
			p(iv.getName());
		}
		p("End printing");*/
		
		/*for(List<SDFAbstractVertex> sv : sdfGraph.getAllSubGraphs()){
			for(SDFAbstractVertex v : sv){
				if(v.getGraphDescription() != null && v.getGraphDescription() instanceof SDFGraph){
					p("subgrapf vertice " + v.getName());
				}
			}
		}
		for(SDFEdge e : sdfGraph.edgeSet()){
			if(e != null){
				try {
					p("edge " + e.getSourceLabel() + " " + e.getCons().intValue() + 
							" " + e.getTargetLabel() + " " + e.getProd().intValue());
				} catch (InvalidExpressionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}*/
		return sdfGraph;
	}
	
	private void p(String s){
		logger.log(Level.INFO, s);
	}

}

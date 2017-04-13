package org.ietr.preesm.algorithm.transforms;

//import java.awt.List;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.transformations.IbsdfFlattener;
import org.ietr.dftools.algorithm.model.sdf.visitors.ConsistencyChecker;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.algorithm.model.visitors.VisitorOutput;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
//import org.ietr.preesm.codegen.xtend.task.CodegenException;

public class HSDFBuildLoops {

	@SuppressWarnings("unused")
	private void p(String s) {
		Logger logger = WorkflowLogger.getLogger();
		logger.log(Level.INFO, "HSDFBuildLoops " + s);
	}

	private SDFEdge hasCommonEdges(SDFAbstractVertex prev, SDFAbstractVertex check, SDFAbstractVertex top) {
		if(prev == top)
		{
			//p("Hierarchical hasCommonEdges " + prev.getName() + " " + check.getName());
			for(int i=0;i<prev.getSources().size();i++)
			{
				for(int j=0;j<check.getSources().size();j++)
				{
					SDFEdge edgePrev = prev.getAssociatedEdge(prev.getSources().get(i)); /* left actor */
					SDFEdge edgeCheck = check.getAssociatedEdge(check.getSources().get(j)); /* right actor */
					//if(edgePrev.getTargetLabel().hashCode() == edgeCheck.getSourceLabel().hashCode())
					if(edgePrev.getTargetLabel() == edgeCheck.getSourceLabel())
					{
						//p("MATCH " + edgePrev.getTargetLabel().hashCode());
						//p("SOURCES Prev " + edgePrev.getSourceLabel() + " " + edgePrev.getTargetLabel() );
						//p("SOURCES Check " + edgeCheck.getSourceLabel() + " " + edgeCheck.getTargetLabel() );
						return edgePrev;
					}
				}
			}
		}else{
			//p("In graph hasCommonEdges " + prev.getName() + " " + check.getName());
			for(int i=0;i<prev.getSinks().size();i++)
			{
				for(int j=0;j<check.getSources().size();j++)
				{
					SDFEdge edgePrev = prev.getAssociatedEdge(prev.getSinks().get(i)); /* left actor */
					SDFEdge edgeCheck = check.getAssociatedEdge(check.getSources().get(j)); /* right actor */
					//if(edgePrev.getTargetLabel().hashCode() == edgeCheck.getTargetLabel().hashCode())
					if(edgePrev.getTargetLabel() == edgeCheck.getTargetLabel())
					{
						//p("MATCH " + edgePrev.getTargetLabel().hashCode());
						//p("SOURCES Prev " + edgePrev.getSourceLabel() + " " + edgePrev.getTargetLabel() );
						//p("SOURCES Check " + edgeCheck.getSourceLabel() + " " + edgeCheck.getTargetLabel() );
						return edgePrev;
					}
				}
			}
		}
		return null;
	}

	private List <SDFAbstractVertex> getHierarchicalActor(SDFGraph graph) {
		List<SDFAbstractVertex> l = new ArrayList<SDFAbstractVertex>();
		for (SDFAbstractVertex v : graph.vertexSet()) {
			Object refinement = v.getPropertyBean().getValue(AbstractVertex.REFINEMENT);
			// If the actor is hierarchical
			if (refinement instanceof AbstractGraph) {
				//p("Found hierarchical " + v.getName());
				l.add(v);
				//p("getHierarchicalActor " + v.getName());
			}
			/*if( v instanceof SDFInterfaceVertex){ p("SDF Interface Vertex " +
					 v.getName()); }else if( v instanceof SDFVertex){ p("SDF Vertex "
					 + v.getName()); }else{ p("SDF Abs Vertex " + v.getName()); }*/
		}
		return l;
	}

	private List <SDFAbstractVertex> sortVertex(Set <SDFAbstractVertex> list, SDFAbstractVertex sdfVertex) throws WorkflowException {
		List <SDFAbstractVertex> sortedRepVertexs = new ArrayList<SDFAbstractVertex>();
		List <SDFAbstractVertex> inVertexs = new ArrayList<SDFAbstractVertex>();
		for(SDFAbstractVertex v : list)
		{
			if (v instanceof SDFVertex)
			{
				inVertexs.add(v);
			}
		}
		SDFAbstractVertex prev = sdfVertex;
		int nbActor = inVertexs.size();
		//p("sortVertex nbActor " + nbActor);
		for(int nbActorLeft = 0;nbActorLeft<nbActor;nbActorLeft++)
		{
			int success = 0;
			//p("Try find right actor for actor " + prev.getName());
			for(SDFAbstractVertex v : inVertexs)
			{
				//p("Test left actor " + v.getName() + " with right actor " + prev.getName());
				if(hasCommonEdges(prev, v, sdfVertex) != null)
				{
					sortedRepVertexs.add(v);
					inVertexs.remove(v);
					prev = v;
					success = 1;
					break;
				}
			}
			if(success == 0)
			{
				throw new WorkflowException("HSDFBuildLoops sortVertex failed to find right actor for actor " + prev.getName());
			}
		}
		return sortedRepVertexs;
	}

	/*private SDFEdge hasCommonSink(SDFAbstractVertex top, SDFAbstractVertex v){
		for(int i=0;i<top.getSources().size();i++) {
			for(int j=0;j<v.getSources().size();j++) {
				SDFEdge edgeTop = top.getAssociatedEdge(top.getSources().get(i));
				SDFEdge edgeV = v.getAssociatedEdge(v.getSources().get(j));
				if(edgeTop.getTargetLabel() == edgeV.getSourceLabel()){
					return edgeV;
				}
			}
		}
		return null;
	}

	private SDFEdge hasCommonSource(SDFAbstractVertex top, SDFAbstractVertex v){
		for(int i=0;i<top.getSinks().size();i++) {
			for(int j=0;j<v.getSinks().size();j++) {
				SDFEdge edgeTop = top.getAssociatedEdge(top.getSinks().get(i));
				SDFEdge edgeV = v.getAssociatedEdge(v.getSinks().get(j));
				if(edgeTop.getTargetLabel() == edgeV.getTargetLabel()){
					return edgeV;
				}
			}
		}
		return null;
	}*/

	public List<SDFEdge> getEdgeToAllocate(SDFAbstractVertex top, SDFAbstractVertex v){
		List <SDFEdge> listEdge = new ArrayList<SDFEdge>();
		if(top == v) return listEdge;
		//p("getEdgeToAllocate " + top.getName() + " " + v.getName());
		for(SDFInterfaceVertex i : top.getSources()){
			SDFEdge edgeTop = top.getAssociatedEdge(i);
			for(SDFInterfaceVertex j : v.getSources()){
				SDFEdge edgeV = v.getAssociatedEdge(j);
				//if(edgeTop.getTargetLabel() != edgeV.getSourceLabel() &&  edgeTop.getTargetLabel() != edgeV.getTargetLabel()){
				if(edgeTop.getTargetLabel() != edgeV.getSourceLabel()){
					listEdge.add(edgeV);		
				}
			}
		}
		//for(SDFEdge e : listEdge)
		//{
		//	p("getEdgeToAllocate source " + e.getSourceLabel() + " actor " + e.getSource().getName() 
		//					+  " target " + e.getTargetLabel() + " actor " + e.getSource().getName());
		//}
		return listEdge;
	}

	private int setHierarchicalWorkingMemory(List <SDFAbstractVertex> list, SDFAbstractVertex topVertex) throws WorkflowException {
		if(list.isEmpty()){
			throw new WorkflowException("setHierarchicalWorkingMemory given list is empty");
		}
		int nbWorkingBufferAllocated = 0;
		int bufSize = 0;
		List <SDFEdge> allocEdge = new ArrayList<SDFEdge>();
		for(SDFAbstractVertex v : list){
			//p("setHierarchicalWorkingMemory check " + topVertex.getName() + " " + v.getName());
			List <SDFEdge> l = getEdgeToAllocate(topVertex, v);
			for(SDFEdge e : l){
				if(allocEdge.contains(e) == false){
					/* need to alloc working memory */
					//p("setHierarchicalWorkingMemory need to alloc for " + v.getName());
					//p("setHierarchicalWorkingMemory source " + e.getSourceLabel() + " actor " + e.getSource().getName() 
					//		+  " target " + e.getTargetLabel() + " actor " + e.getSource().getName());
					int nbRep = 0;
					try {
						nbRep = Integer.parseInt(e.getTarget().getNbRepeat().toString());
					} catch (NumberFormatException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InvalidExpressionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//Integer mem = 0;
					int mem = 0;
					try {
						mem = nbRep*e.getCons().intValue();
						//p("mem " + mem + " nbRep " + nbRep + " getCons " + e.getCons().intValue());
					} catch (InvalidExpressionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					/*try {
						mem = new Integer(nbRep*e.getCons().intValue());
					} catch (InvalidExpressionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}*/
					//e.getPropertyBean().setValue("working_memory", mem);
					//p("buf size " + bufSize + " mem " + mem);
					bufSize += mem;
					nbWorkingBufferAllocated++;
					allocEdge.add(e);
				}
			}
			topVertex.getPropertyBean().setValue("working_memory", new Integer(bufSize));
		}
		//p("setHierarchicalWorkingMemory topVertex " + topVertex.getName() + " nbBuf " + nbWorkingBufferAllocated + " bufSize " + bufSize);
		return nbWorkingBufferAllocated;
	}

	public SDFGraph execute(SDFGraph inputGraph) throws WorkflowException {
		//p("Executing");
		List <SDFAbstractVertex> hierarchicalActorslist = getHierarchicalActor(inputGraph);
		//p("nbHierarchicalActor " + hierarchicalActorslist.size());
		List <List <SDFAbstractVertex>> list = new ArrayList<List <SDFAbstractVertex>>();

		/* run through all hierarchical actors of graph inputGraph */
		for(SDFAbstractVertex v : hierarchicalActorslist) {
			SDFGraph graph = (SDFGraph) v.getGraphDescription();
			List <SDFAbstractVertex> sortedVertex = sortVertex(graph.vertexSet(), v);
			list.add(sortedVertex);
		}
		
		/* allocate internal working buffer for the hierarchical actor */
		for(int i=0;i<list.size();i++){
			//int ret = setHierarchicalWorkingMemory(list.get(i), hierarchicalActorslist.get(i));
			setHierarchicalWorkingMemory(list.get(i), hierarchicalActorslist.get(i));
			//p(hierarchicalActorslist.get(i).getName() + " nb internal buffer is " + ret);
		}
		return inputGraph;
	}
}

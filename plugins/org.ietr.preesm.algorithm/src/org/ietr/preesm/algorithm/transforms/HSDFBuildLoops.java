package org.ietr.preesm.algorithm.transforms;

//import java.awt.List;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.xbase.lib.Pair;
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

	private int getPGCD(int a, int b){
		int r;
		while (b != 0) {
			r = a%b;
			a = b;
			b = r;
		}
		return a;
	}

	private int pg(String s){
		p("generateClustering " + s);
		return 0;
	}

	private List<SDFAbstractVertex> getPredessecors(SDFAbstractVertex v){
		List <SDFAbstractVertex> l = new ArrayList<SDFAbstractVertex>();
		List<SDFAbstractVertex> tmp = getInVertexs(v);
		boolean exit = false;
		do{
			l.removeAll(tmp);
			l.addAll(tmp);
			List<SDFAbstractVertex> tmp1 = new ArrayList<SDFAbstractVertex>();
			tmp1.addAll(tmp);
			//tmp1.removeAll(l); // avoid cycles deadlock
			if(tmp.isEmpty() == true)
				exit = true;
			tmp.clear();
			for(SDFAbstractVertex e : tmp1){
				tmp.addAll(getInVertexs(e));
			}
		}while(exit == false);
		p("Predessecors of " + v.getName() + " are: ");
		for(SDFAbstractVertex e : l){
			p(" - " + e.getName());
		}
		return l;
	}

	private List<SDFAbstractVertex> getSuccessors(SDFAbstractVertex v){
		List <SDFAbstractVertex> l = new ArrayList<SDFAbstractVertex>();
		List<SDFAbstractVertex> tmp = getOutVertexs(v);
		boolean exit = false;
		do{
			l.removeAll(tmp);
			l.addAll(tmp);
			List<SDFAbstractVertex> tmp1 = new ArrayList<SDFAbstractVertex>();
			tmp1.addAll(tmp);
			//tmp1.removeAll(l); // avoid cycles deadlock
			if(tmp.isEmpty() == true)
				exit = true;
			tmp.clear();
			for(SDFAbstractVertex e : tmp1){
				tmp.addAll(getOutVertexs(e));
			}
		}while(exit == false);
		p("Sucessors of " + v.getName() + " are: ");
		for(SDFAbstractVertex e : l){
			p(" - " + e.getName());
		}
		return l;
	}

	private List<SDFAbstractVertex> getInVertexs(SDFAbstractVertex v){
		List<SDFAbstractVertex> inV = new ArrayList<SDFAbstractVertex>();
		for(SDFInterfaceVertex i : v.getSources()){
			SDFAbstractVertex vv = v.getAssociatedEdge(i).getSource();
			if(vv instanceof SDFVertex){
				if(vv != v){
					inV.add(vv);
					p("getInVertexs " + vv.getName() + " -> " + v.getName());
				}else{
					try {
						throw new WorkflowException("HSDFBuildLoops Delays not supported when generating clustering");
					} catch (WorkflowException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return inV;
	}
	
	private List<SDFAbstractVertex> getOutVertexs(SDFAbstractVertex v){
		List<SDFAbstractVertex> outV = new ArrayList<SDFAbstractVertex>();
		for(SDFInterfaceVertex i : v.getSinks()){
			SDFAbstractVertex vv = v.getAssociatedEdge(i).getTarget();
			if(vv instanceof SDFVertex){
				if(vv != v){
					outV.add(vv);
					p("getOutVertexs " + v.getName() + " -> " + vv.getName());
				}else{
					try {
						throw new WorkflowException("HSDFBuildLoops Delays not supported when generating clustering");
					} catch (WorkflowException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return outV;
	}

	private List<SDFAbstractVertex> clusteredVertexs = new ArrayList<SDFAbstractVertex>();
	private SDFAbstractVertex leftVertex = null;
	private SDFAbstractVertex rightVertex = null;

	private int simpleClusteringHeuristic(List<SDFAbstractVertex> inV, List<SDFAbstractVertex> outV, SDFAbstractVertex current){
		
		/* leftVertex ---> rightVertex */
		/* AN HEURISTIC CAN BE PLACED HERE */
		for(SDFAbstractVertex v : inV){
			if(clusteredVertexs.contains(v) == false){
				this.rightVertex = current;
				this.leftVertex = v;
				clusteredVertexs.add(v);
				return 0; // success
			}
		}
		for(SDFAbstractVertex v : outV){
			if(clusteredVertexs.contains(v) == false){
				this.leftVertex = current;
				this.rightVertex = v;
				clusteredVertexs.add(v);
				return 0; // success
			}
		}
		return -1;
	}

	private Map<SDFAbstractVertex, List<SDFAbstractVertex>> linkPred;
	private Map<SDFAbstractVertex, List<SDFAbstractVertex>> linkSucc;
	
	public REPVertex generateClustering(List<SDFAbstractVertex> vertexs){
		
		
		for(SDFAbstractVertex e : vertexs){
			getPredessecors(e);
			getSuccessors(e);
		}
		
		REPVertex repVertexs = new REPVertex();
		int first = 0;//new Random().nextInt();
		List<SDFAbstractVertex> vertexsCpy = new ArrayList<SDFAbstractVertex>();
		for(SDFAbstractVertex v : vertexs) vertexsCpy.add(v);
		/* start clustering from this vertex */
		int nbActor = vertexsCpy.size();
		int nbActorLeft = nbActor;
		SDFAbstractVertex current = vertexsCpy.get((first)%vertexs.size()); // get first actor to be clustered
		clusteredVertexs.add(current);
		nbActorLeft--;
		List<SDFAbstractVertex> inV = getInVertexs(current);
		List<SDFAbstractVertex> outV = getOutVertexs(current);

		while(nbActorLeft != 0){

			if(simpleClusteringHeuristic(inV, outV, current) == 0){
				repVertexs.setLeftVertex(leftVertex);
				repVertexs.setRightVertex(rightVertex);
				if(clusteredVertexs.contains(leftVertex) == false) clusteredVertexs.add(leftVertex); // mark as clustered
				if(clusteredVertexs.contains(rightVertex) == false) clusteredVertexs.add(rightVertex); // mark as clustered
				inV.remove(leftVertex); inV.remove(rightVertex); // nothing changed when not present
				outV.remove(leftVertex); outV.remove(rightVertex); // nothing changed when not present
				int pgdc = -1;
				int repLeft = -1;
				int repRight = -1;
				try {
					pgdc = getPGCD(leftVertex.getNbRepeatAsInteger(), rightVertex.getNbRepeatAsInteger());
					repLeft = leftVertex.getNbRepeatAsInteger()/pgdc;
					repRight = rightVertex.getNbRepeatAsInteger()/pgdc;
				} catch (InvalidExpressionException e) {
					// TODO Auto-generated catch block
					try {
						throw new WorkflowException("HSDFBuildLoops fail to get repetion vertors: pgdc" + pgdc + " repLeft" + repLeft + " repRight" + repRight);
					} catch (WorkflowException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				repVertexs.setRepeat(pgdc);
				repVertexs.setRepeatLeft(repLeft);
				repVertexs.setRepeatRight(repRight);
				p("Found " + pgdc + " ( " + repLeft + " " + this.leftVertex.getName() + " -> " + repRight + " " + this.rightVertex.getName() + " ) ");
				nbActorLeft--;
			}else{
				nbActorLeft--;
			}
		}
		/* pg(current.getAssociatedEdge(current.getSources().get(0)).getSource().getName() + " -> " + current.getName()); */
		/* for(SDFAbstractVertex v : vertexsCpy){ } */
		this.clusteredVertexs.clear();
		return repVertexs;
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

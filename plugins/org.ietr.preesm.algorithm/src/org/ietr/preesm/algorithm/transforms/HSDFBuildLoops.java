package org.ietr.preesm.algorithm.transforms;

//import java.awt.List;
import java.util.List;
import java.awt.Choice;
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
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
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
			tmp.removeAll(l); // avoid cycles deadlock
			l.addAll(tmp);
			List<SDFAbstractVertex> tmp1 = new ArrayList<SDFAbstractVertex>();
			tmp1.addAll(tmp);
			if(tmp.isEmpty() == true)
				exit = true;
			tmp.clear();
			for(SDFAbstractVertex e : tmp1){
				tmp.addAll(getInVertexs(e));
			}
		}while(exit == false);
		/*p("Predessecors of " + v.getName() + " are: ");
		for(SDFAbstractVertex e : l){
			p(" - " + e.getName());
		}*/
		return l;
	}

	private List<SDFAbstractVertex> getSuccessors(SDFAbstractVertex v){
		List <SDFAbstractVertex> l = new ArrayList<SDFAbstractVertex>();
		List<SDFAbstractVertex> tmp = getOutVertexs(v);
		boolean exit = false;
		do{
			tmp.removeAll(l); // avoid cycles deadlock
			l.addAll(tmp);
			List<SDFAbstractVertex> tmp1 = new ArrayList<SDFAbstractVertex>();
			tmp1.addAll(tmp);
			if(tmp.isEmpty() == true)
				exit = true;
			tmp.clear();
			for(SDFAbstractVertex e : tmp1){
				tmp.addAll(getOutVertexs(e));
			}
		}while(exit == false);
		/*p("Sucessors of " + v.getName() + " are: ");
		for(SDFAbstractVertex e : l){
			p(" - " + e.getName());
		}*/
		return l;
	}

	private boolean isMergeable(SDFAbstractVertex a, SDFAbstractVertex b){
		List<SDFAbstractVertex> predA = getPredessecors(a);
		List<SDFAbstractVertex> predB = getPredessecors(b);
		List<SDFAbstractVertex> succA = getSuccessors(a);
		List<SDFAbstractVertex> succB = getSuccessors(b);
		predA.retainAll(succB);
		predB.retainAll(succA);
		return predA.isEmpty() && predB.isEmpty();
		//return true;
	}

	private List <SDFEdge> getInEdges(SDFAbstractVertex v){
		List<SDFEdge> inEdge = new ArrayList<SDFEdge>();
		for(SDFInterfaceVertex i : v.getSources()){
			inEdge.add(v.getAssociatedEdge(i));
		}
		return inEdge;
	}

	private List <SDFEdge> getOutEdges(SDFAbstractVertex v){
		List<SDFEdge> outEdge = new ArrayList<SDFEdge>();
		for(SDFInterfaceVertex i : v.getSinks()){
			outEdge.add(v.getAssociatedEdge(i));
		}
		return outEdge;
	}

	private List<SDFAbstractVertex> getInVertexs(SDFAbstractVertex v){
		List<SDFAbstractVertex> inV = new ArrayList<SDFAbstractVertex>();
		List<SDFEdge> inEdge = getInEdges(v);
		for(SDFEdge i : inEdge){
			SDFAbstractVertex vv = i.getSource();
			if(vv instanceof SDFVertex){
				if(vv != v){
					inV.add(vv);
					//p("getInVertexs " + vv.getName() + " -> " + v.getName());
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
		List<SDFEdge> outEdge = getInEdges(v);
		for(SDFEdge i : outEdge){
			SDFAbstractVertex vv = i.getTarget();
			if(vv instanceof SDFVertex){
				if(vv != v){
					outV.add(vv);
					//p("getOutVertexs " + v.getName() + " -> " + vv.getName());
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

	//private List<Pair<SDFAbstractVertex, SDFAbstractVertex>> clusteredVertexs = new ArrayList<Pair<SDFAbstractVertex,SDFAbstractVertex>>();
	private HashMap<SDFAbstractVertex, List<SDFAbstractVertex>> clusteredVertexs = new HashMap<SDFAbstractVertex, List<SDFAbstractVertex>>();
	private List<SDFAbstractVertex> visitedClusteredVertexs = new ArrayList<SDFAbstractVertex>();
	private List <SDFAbstractVertex> chooseNewV = new ArrayList<SDFAbstractVertex>();
	private SDFAbstractVertex leftVertex = null;
	private SDFAbstractVertex rightVertex = null;

	private int simpleClusteringHeuristic(List<SDFAbstractVertex> inV, List<SDFAbstractVertex> outV, SDFAbstractVertex current){

		List<SDFAbstractVertex> ongoingList = this.clusteredVertexs.get(current);
		if(ongoingList == null){
			ongoingList = new ArrayList<SDFAbstractVertex>();
			this.clusteredVertexs.put(current, ongoingList);
		}

		/* leftVertex ---> rightVertex */
		/* AN HEURISTIC CAN BE PLACED HERE */
		for(SDFAbstractVertex v : inV){
			if((ongoingList.contains(v)) == false && (isMergeable(v, current) == true)){
				
				/* set vertex order */
				this.rightVertex = current;
				this.leftVertex = v;
				
				List<SDFAbstractVertex> foundOngoing = this.clusteredVertexs.get(v);
				if(foundOngoing == null){
					foundOngoing = new ArrayList<SDFAbstractVertex>();
					this.clusteredVertexs.put(v,  foundOngoing);
				}
				if(foundOngoing.contains(current) == false){
					foundOngoing.add(current);
				}
				
				ongoingList.add(v);
				inV.remove(this.leftVertex);
				inV.remove(this.rightVertex); // nothing changed when not present
				return 0;
			}
		}
		for(SDFAbstractVertex v : outV){
			if((ongoingList.contains(v) == false) && (isMergeable(v, current) == true)){
				
				/* set vertex order */
				this.leftVertex = current;
				this.rightVertex = v;
				
				List<SDFAbstractVertex> foundOngoing = this.clusteredVertexs.get(v);
				if(foundOngoing == null){
					foundOngoing = new ArrayList<SDFAbstractVertex>();
					this.clusteredVertexs.put(v,  foundOngoing);
				}
				if(foundOngoing.contains(current) == false){
					foundOngoing.add(current);
				}
				
				ongoingList.add(v);
				outV.remove(this.leftVertex);
				outV.remove(this.rightVertex); // nothing changed when not present
				return 0;
			}
		}
		return -1;
	}

	private Map<SDFAbstractVertex, List<SDFAbstractVertex>> linkPred;
	private Map<SDFAbstractVertex, List<SDFAbstractVertex>> linkSucc;

	/*public ClustSequence generateClustering(Set<SDFAbstractVertex> vertexs) throws InvalidExpressionException{
		List<SDFAbstractVertex> l = new ArrayList<SDFAbstractVertex>();
		for(SDFAbstractVertex v : vertexs){
			if (v instanceof SDFVertex) {
				l.add(v);
				p("generateClustering " + v.getName() + " RV " + v.getNbRepeatAsInteger() + " nbSource " + v.getSources().size() + " nbSinks " + v.getSinks().size());
				p("Input actor of " + v.getName() + " are: ");
				//List <SDFAbstractVertex> lp = getPredessecors(v);
				List <SDFAbstractVertex> lp = getInVertexs(v);
				for(SDFAbstractVertex e : lp){
					p(" - " + e.getName());
				}
				p("Output actor of " + v.getName() + " are: ");
				//List <SDFAbstractVertex> ls = getSuccessors(v);
				List <SDFAbstractVertex> ls = getOutVertexs(v);
				for(SDFAbstractVertex e : ls){
					p(" - " + e.getName());
				}
			}else{
				p("generateClustering other vertex " + v.getName());
			}
		}
		try {
			return generateClustering(l);
		} catch (WorkflowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}*/
	
	private SDFGraph graph = null;
	
	private List <SDFAbstractVertex> getClusteringVertexes(List<SDFAbstractVertex> vertexes) throws WorkflowException {
		if(vertexes.isEmpty() == true){
			throw new WorkflowException("getClusteringVertexes failed vertexes is empty");
		}
		List<SDFAbstractVertex> r = new ArrayList<SDFAbstractVertex>();
		if(vertexes.size() == 1){
			r.add(vertexes.get(0));
			return r;
		}

		// get in and out vertexes
		int first = vertexes.size()-1;
		int nbVertex = vertexes.size();
		for(int i = first; i < nbVertex; i++){
			SDFAbstractVertex a = vertexes.get(i);
			List<SDFAbstractVertex> inA = getInVertexs(a);
			List<SDFAbstractVertex> outA = getOutVertexs(a);
			List<SDFAbstractVertex> linkA = new ArrayList<SDFAbstractVertex>();
			linkA.addAll(inA);
			linkA.addAll(outA);
	
			// get first mergeable vertexes
			for(SDFAbstractVertex v : linkA){
				if(isMergeable(a, v)){
					if(getInVertexs(a).contains(v) == true){
						r.add(v);
						r.add(a);
					}else{
						r.add(a);
						r.add(v);
					}
					//p("isMergeable " + r.get(0).getName() + " " + r.get(1).getName());
					return r;
				}
			}

			if(i == (nbVertex -1)){
				i = -1;
				nbVertex = first;
			}
		}
		if(r.isEmpty() == true){
			p("get ClusteringVertexes Failed");
			throw new WorkflowException("Failed getClusteringVertexes");
		}
		return r;
	}
	
	private int copyEdge(SDFEdge dst, SDFEdge src) throws WorkflowException{
		if(dst == null || src == null){
			p("copy Edge Failed");
			throw new WorkflowException("Failed copy Edge");
		}
		dst.setDataType(src.getDataType());
		
		dst.setDelay(src.getDelay());
		
		dst.setCons(src.getCons());
		dst.setProd(src.getProd());
		
		dst.setSourceLabel(src.getSourceLabel());
		dst.setTargetLabel(src.getTargetLabel());
		
		dst.setTargetInterface(src.getTargetInterface());
		dst.setSourceInterface(src.getSourceInterface());
		return 0;
	}

	private SDFAbstractVertex generatePairedClusteredVertex(int pgcm, SDFAbstractVertex left, SDFAbstractVertex right) throws WorkflowException, SDF4JException{

		// execution order left ---> right

		// get in edge of vertex
		List<SDFEdge> inEdgeRight = getInEdges(right);
		List<SDFEdge> inEdgeLeft = getInEdges(left);
		List <SDFEdge> inEdgeVertex = new ArrayList<SDFEdge>();
		inEdgeVertex.addAll(inEdgeLeft); // all in edge from left go in vertex
		for(SDFEdge e : inEdgeRight){ // all not from left in vertex (others are merged)
			if(e.getSource() != left){
				inEdgeVertex.add(e);
				int cons = 0;
				int rvRight = 0;
				try {
					cons = e.getCons().intValue();
					rvRight = right.getNbRepeatAsInteger();
				} catch (InvalidExpressionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.setCons(new SDFIntEdgePropertyType(rvRight * cons / pgcm));
			}
		}

		// get out edges of vertex
		List<SDFEdge> outEdgeLeft = getOutEdges(left);
		List<SDFEdge> outEdgeRight = getOutEdges(right);
		List <SDFEdge> outEdgeVertex = new ArrayList<SDFEdge>();
		outEdgeVertex.addAll(outEdgeRight); // all out edge of right go out of vertex
		for(SDFEdge e : outEdgeLeft){
			if(e.getTarget() != right){
				outEdgeVertex.add(e);
				int prod = 0;
				int rvLeft = 0;
				try {
					prod = e.getProd().intValue();
					rvLeft = left.getNbRepeatAsInteger();
				} catch (InvalidExpressionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.setProd(new SDFIntEdgePropertyType(rvLeft * prod / pgcm));
			}
		}

		// get sources of vertex
		List <SDFInterfaceVertex> sourcesVertex = new ArrayList<SDFInterfaceVertex>();
		for(SDFEdge e : inEdgeVertex){
			sourcesVertex.add(e.getTargetInterface());
		}

		// get sinks of vertex
		List <SDFInterfaceVertex> sinksVertex = new ArrayList<SDFInterfaceVertex>();
		for(SDFEdge e : outEdgeVertex){
			sinksVertex.add(e.getSourceInterface());
		}

		// now we can build up vertex
		SDFVertex vertex = new SDFVertex();
		vertex.setName(left.getName() + "_" + right.getName());
		vertex.setNbRepeat(pgcm);
		vertex.setSinks(sinksVertex);
		vertex.setSources(sourcesVertex);
		this.graph.addVertex(vertex);
		
		for(SDFEdge e : inEdgeVertex){
			SDFEdge newEdge = this.graph.addEdge(e.getSource(), vertex);
			copyEdge(newEdge, e);
		}

		for(SDFEdge e : outEdgeVertex){
			SDFEdge newEdge = this.graph.addEdge(vertex, e.getTarget());
			copyEdge(newEdge, e);
		}

		// remove edges and vertexes
		for(SDFEdge e : inEdgeVertex){
			this.graph.removeEdge(e);
		}
		for(SDFEdge e : outEdgeVertex){
			this.graph.removeEdge(e);
		}
		this.graph.removeVertex(left);
		this.graph.removeVertex(right);
	
		if(this.graph.isSchedulable() == true){
			p("graph ok isSchedulable");
		}else{
			throw new SDF4JException("Bug found graph not schedulable while clustering"); 
		}
		return vertex;
	}

	public ClustSequence generateClustering(SDFGraph inGraph) throws WorkflowException, SDF4JException {

		this.graph = inGraph.clone();
		// copy vertexes
		List<SDFAbstractVertex> vertexesCpy = new ArrayList<SDFAbstractVertex>();
		for(SDFAbstractVertex v : graph.vertexSet()){
			if(v instanceof SDFVertex){
				vertexesCpy.add(v);
			}
		}

		// seq
		ClustSequence seq = new ClustSequence();

		int pgcm = 0;
		int nbActor = vertexesCpy.size();
		for(int i=0;i<nbActor-1;i++){
			// get actor
			List <SDFAbstractVertex> current = getClusteringVertexes(vertexesCpy);
			try {
				pgcm = getPGCD(current.get(0).getNbRepeatAsInteger(), current.get(1).getNbRepeatAsInteger());
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SDFAbstractVertex clusteredVertex = generatePairedClusteredVertex(pgcm, current.get(0)/*left*/, current.get(1)/*right*/);
			vertexesCpy.removeAll(current);
			vertexesCpy.add(clusteredVertex);
			//graph.addEdge(source, sourcePort, target, targetPort)
			/*for(SDFEdge e : getInEdges(clusteredVertex)){
				graph.addEdge(e.getSource(), e.getTarget());
			}
			for(SDFEdge e : getOutEdges(clusteredVertex)){
				graph.addEdge(e.getSource(), e.getTarget());
			}*/
			p("clusteredVertex " + clusteredVertex.getName());
		}
		p("generateClustering ok");
		return seq;

		//for(SDFAbstractVertex e : vertexs){
			//getPredessecors(e);
			//getSuccessors(e);
		//}

		/*REPVertex repVertexs = new REPVertex();
		int first = 0;//new Random().nextInt();
		List<SDFAbstractVertex> vertexsCpy = new ArrayList<SDFAbstractVertex>();
		for(SDFAbstractVertex v : vertexs) vertexsCpy.add(v);
		//start clustering from this vertex
		int nbActor = vertexsCpy.size();
		int nbActorLeft = nbActor;
		SDFAbstractVertex current = vertexsCpy.get((first)%vertexs.size()); // get first actor to be clustered
		int currentVertexJ = first;
		//clusteredVertexs.add(current);
		nbActorLeft--;
		List<SDFAbstractVertex> inV = getInVertexs(current);
		List<SDFAbstractVertex> outV = getOutVertexs(current);
		
		int deadlockCount = 0;
		int deadlockDetectCount = 20;
		SDFAbstractVertex deadlockedVertex = null;

		while(nbActorLeft != 0){

			if(simpleClusteringHeuristic(inV, outV, current) == 0){
				repVertexs.setLeftVertex(leftVertex);
				repVertexs.setRightVertex(rightVertex);
				int pgdc = -1;
				int repLeft = -1;
				int repRight = -1;
				try {

					int leftVertexRV = leftVertex.getNbRepeatAsInteger();
					int rightVertexRV = rightVertex.getNbRepeatAsInteger();

					// compute pgcd and internal RVs
					pgdc = getPGCD(leftVertexRV, rightVertexRV);
					repLeft = leftVertexRV/pgdc;
					repRight = rightVertexRV/pgdc;

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
				if(visitedClusteredVertexs.contains(current) == false){
					visitedClusteredVertexs.add(current);
				}
				// no vertex left to be clustered on vertex current
				chooseNewV.addAll(getInVertexs(current));
				chooseNewV.addAll(getOutVertexs(current));
				p("current vertex " + current.getName() + " deadlockCount " + deadlockCount + " done !");
				chooseNewV.removeAll(visitedClusteredVertexs);
				// at this point we got clustered vertexs that have non-clustered neighboors 
				for(SDFAbstractVertex v : chooseNewV){
					current = v;
					break;
				}
				if(chooseNewV.size() == 0){ // got irreguraliry
					
					for(int i = currentVertexJ; i < vertexsCpy.size();i++){
						SDFAbstractVertex v = vertexsCpy.get(i);
						//for(SDFAbstractVertex v : vertexsCpy){
						if(visitedClusteredVertexs.contains(v) == false){
							current = v;
							currentVertexJ = i;
							currentVertexJ++;
							currentVertexJ %= vertexsCpy.size();
							break;
						}
					}
				}
				inV = getInVertexs(current);
				outV = getOutVertexs(current);

				if(deadlockedVertex == current){
					deadlockCount++;
				}else{
					deadlockedVertex = current;
					deadlockCount = 0;
				}

				if(deadlockCount >= deadlockDetectCount){
					p("Failed deadlock with actor " + current.getName());
					throw new WorkflowException("Failed Deadlock");
				}
			}
		}
		// pg(current.getAssociatedEdge(current.getSources().get(0)).getSource().getName() + " -> " + current.getName());
		this.clusteredVertexs.clear();
		this.visitedClusteredVertexs.clear();
		this.chooseNewV.clear();
		return repVertexs;*/
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

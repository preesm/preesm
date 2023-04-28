package org.preesm.clustering;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.graph.Vertex;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.ui.pisdf.util.SavePiGraph;

public class NodePartitioner {
	/**
	   * Input graph.
	   */
	  private PiGraph  graph;
	  /**
	   * Workflow scenario.
	   */
	  private Scenario scenario;
	  /**
	   * Architecture design.
	   */
	  private Design   archi;
	private int node;
	private int offset;
	public NodePartitioner(PiGraph graph, Scenario scenario, Design archi,int node,int offset) {
		this.graph = graph;
		this.scenario = scenario;
		this.archi = archi;
		this.node = node;
		this.offset = offset;
	}
	public PiGraph execute() {
		//1. Compute the sum of repetition vector of each actor in the graph
		Long sum = computebrvSum();
		//2. Arrange actors in the hierarchical ASAP topological order
		Map<AbstractActor, Long> topoOrder = computeTopoOrder();
		//3. Divide the actors into sub-sets of commensurate complexity
		List<List<AbstractActor>> subSets = computeSubSet(topoOrder, sum/node);
		//4. Generate subGraph
		int index =0;
		for(List<AbstractActor>  sub : subSets) {
			PiGraph subgraph = new PiSDFSubgraphBuilder(graph, sub,"sub"+index+"_").build();
			//5. export Sub
			 graphExporter(subgraph);
			index ++;
		}
		//6. clean skeleton of top graph
		for(AbstractActor ha: graph.getActors())
			if(ha instanceof PiGraph) {
				AbstractActor oEmpty = PiMMUserFactory.instance.createActor();
				Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
				graph.replaceActor(ha, oEmpty);
				//((Actor)ha).setRefinement(refinement);
			}
		//7. pipeline top
		pipelineTop();
		
		//8.export top
		graphExporter(graph);
			
		return null;
		
	}
	private void pipelineTop() {
		int index = 0;
		Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
		for(Fifo f: graph.getFifos()) {
			Delay d = PiMMUserFactory.instance.createDelay();
			d.setName(((AbstractActor) f.getSource()).getName()+".out-"+((AbstractActor) f.getTarget()).getName()+".in_"+index);
			d.setLevel(PersistenceLevel.PERMANENT);
			d.setExpression(brv.get(f.getSource())*f.getSourcePort().getExpression().evaluate());
			d.setContainingGraph(f.getContainingGraph());
			f.assignDelay(d);
			index++;
		}
		
	}
	private void graphExporter(PiGraph printgraph) {
		final String[] uriString = graph.getUrl().split("/");
		String strPath = "/"+uriString[1]+"/"+uriString[2]+"/generated/";
		final IPath fromPortableString = Path.fromPortableString(strPath);
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
		IProject iProject = file.getProject();
		for(PiGraph subgraph: printgraph.getAllChildrenGraphs())
			SavePiGraph.savePiGraphInFolder(iProject, fromPortableString, subgraph, "_truc");
		SavePiGraph.savePiGraphInFolder(iProject, fromPortableString, printgraph, "_truc");
		
	}
	private List<List<AbstractActor>> computeSubSet(Map<AbstractActor, Long> topoOrder, long subRV) {
		List<List<AbstractActor>> subSets  = new LinkedList<>();
		for(int i=0; i<node;i++) {
			subSets.add(new LinkedList<>());
		}
		long sumSubRv = 0L;
		long rank = 0L;
		int index=0;
		boolean startSet = true;
		boolean stopSet = false;
		Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
		//List<AbstractActor> actorSub = new LinkedList<>();
		while(!topoOrder.isEmpty()) {
		if(index==node-1) {
			for(AbstractActor a : graph.getAllActors())
				if(topoOrder.get(a)!=null) {
					subSets.get(index).add(a);
			}
			subSets.get(index).add(graph.getChildrenGraphs().get(0));
			int size = subSets.get(index).size();
			for(int i = 0; i<size;i++) {
				if(subSets.get(index).contains(subSets.get(index).get(size-i-1).getContainingPiGraph()))
					subSets.get(index).remove(subSets.get(index).get(size-i-1));
			}
			topoOrder.clear();
		}else {
		//add actor if complexity OK and no cycle break on the rank
		for(AbstractActor a : graph.getAllActors()) {
			if(topoOrder.get(a)!=null)
				if(topoOrder.get(a)==rank && sumSubRv+brv.get(a)<subRV) {
					if(startSet ||(!startSet && !a.getDataInputPorts().stream().anyMatch(x -> x.getFifo().isHasADelay()))) {
						subSets.get(index).add(a);
						//actorSub.add(a);
						topoOrder.remove(a);
						sumSubRv=	sumSubRv+brv.get(a)	;
					}
				}							
			}
		
		//subSet end when an actor is leaving on the rank
		if(topoOrder.containsValue(rank)) {
			//List<AbstractActor> actorSubCopy = PiMMUserFactory.instance.copy(actorSub);
			//subSets.add(actorSubCopy);
			 startSet = true;
			 stopSet = false;
			 sumSubRv = 0L;
			 index++;
			 //actorSub.clear();
			 
			 
		}
		
		else{
			rank++;
			startSet = false;
		}
		}
		}
		return subSets;
	}
	/**
	 * Associate SDF actor to a topological rank
	 * @return linked list Actor to rank
	 */
	private Map<AbstractActor, Long> computeTopoOrder() {
		Map<AbstractActor, Long> topoOrder= new HashMap<>();
		boolean isLast = false;
	    final List<AbstractActor> curentRankList = new LinkedList<>();
	    final List<AbstractActor> nextRankList = new LinkedList<>();
	    // Init
	    /*for (final DataInputInterface i : graph.getDataInputInterfaces()) {
	      if (i.getDirectSuccessors().get(0) instanceof Actor || i.getDirectSuccessors().get(0) instanceof SpecialActor) {
	        topoOrder.put((AbstractActor) i.getDirectSuccessors().get(0), 0L);
	        curentRankList.add((AbstractActor) i.getDirectSuccessors().get(0));
	      }
	    }*/
	    for (final AbstractActor a : graph.getAllActors()) {
	      if (a.getDataInputPorts().isEmpty() && (a instanceof Actor || a instanceof SpecialActor)) {
	        topoOrder.put(a, 0L);
	        curentRankList.add(a);
	      }
	    }

	    // Loop
	    Long currentRank = 1L;
	    while (!isLast) {
	      for (final AbstractActor a : curentRankList) {
	        if (!a.getDirectSuccessors().isEmpty()) {
	          for (final Vertex aa : a.getDirectSuccessors()) {
	            if ( !(aa instanceof DelayActor)) {//!(aa instanceof DataOutputInterface) ||
	              boolean flag = false;
	              for (final DataInputPort din : ((AbstractActor) aa).getDataInputPorts()) {
	                AbstractActor aaa = (AbstractActor) din.getFifo().getSource();
	                
	                // for (Vertex aaa : aa.getDirectPredecessors()) {
	                if (!topoOrder.containsKey(aaa)
	                    && (aaa instanceof Actor || aaa instanceof SpecialActor || aaa instanceof PiGraph )
	                    && !din.getFifo().isHasADelay() || nextRankList.contains(aaa)) {
	                  // predecessors
	                  // are in the
	                  // list
	                  flag = true;
	                }
	              }
	              if (!flag && !topoOrder.containsKey(aa)
	                  && (aa instanceof Actor || aa instanceof SpecialActor || aa instanceof PiGraph|| aa instanceof DataOutputInterface)) {
	                if(aa instanceof PiGraph)
	                	for(DataInputInterface din : ((PiGraph)aa).getDataInputInterfaces()) {
	                		Vertex target = din.getOutgoingEdges().get(0).getTarget();
	                		if(!topoOrder.containsKey(target)) {
	                			topoOrder.put((AbstractActor) target, currentRank);
	        	                nextRankList.add((AbstractActor) target);
	                		}
	                	}else if(aa instanceof DataOutputInterface){
	                		topoOrder.put((AbstractActor) ((DataOutputInterface) aa).getGraphPort().getFifo().getTarget(), currentRank);
        	                nextRankList.add((AbstractActor) ((DataOutputInterface) aa).getGraphPort().getFifo().getTarget());
	                	}else {
	            	  topoOrder.put((AbstractActor) aa, currentRank);
	                nextRankList.add((AbstractActor) aa);
	                	}
	              }

	            }
	          }
	        }

	      }
	      if (nextRankList.isEmpty()) {
	        isLast = true;
	      }
	      curentRankList.clear();
	      curentRankList.addAll(nextRankList);
	      nextRankList.clear();
	      currentRank++;
	    }
		// TODO Auto-generated method stub
		return topoOrder;
	}
	/**
	 * Compute the sum of repetition vector of each actor in the graph
	 * @return
	 */
	private Long computebrvSum() {
		Long sum = 0L;
		Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
		for(AbstractActor a: graph.getAllActors())
			if(!((a instanceof DelayActor)||(a instanceof DataInputInterface)||(a instanceof DataOutputInterface)))
					sum = sum+brv.get(a);
		return sum;
	}

}


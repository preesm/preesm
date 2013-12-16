/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.experiment.core.piscenario;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.sf.dftools.algorithm.importer.InvalidModelException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.ietr.preesm.experiment.core.piscenario.serialize.PiScenarioParser;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

public class Constraints {
	/**
	 * Associate a PiMM Vertex (String) to a Set of Core capable to run it (HashSet<String>)
	 */
	HashMap<String , HashSet<String>> constraints;
		
	public Constraints() {
		constraints = new HashMap<String , HashSet<String>>();
	}
	
	public String getParentOf(String child){
		int index = child.lastIndexOf('/');
		if(index == -1) return null;
		return child.substring(0, index);		
	}
	
	public Set<String> getAllActors(){
		return constraints.keySet();
	}
	
	public Set<String> getCoreId(String actor){
		return constraints.get(actor);
	}
	
	public Set<String> getChildrenOf(String parent){
		HashSet<String> result = new HashSet<String>();
		for(String actor : constraints.keySet()){
			if(actor.startsWith(parent+"/")){
				// If it is not a hierarchical actor
				String subActor = actor.substring(parent.length()+1);
				if(subActor.contains("/")){
					String hierActorName = subActor.split("/")[0];
					result.add(parent+"/"+hierActorName);			
				}else{		
					result.add(actor);
				}
			}
		}
		return result;
	}

	public boolean getConstraint(String element, String coreId){
		if(constraints.containsKey(element)){
			return constraints.get(element).contains(coreId);
		}else{
			boolean allChecked=true;
			for(String subElement : constraints.keySet()){
				if(subElement.startsWith(element)){
					allChecked &= constraints.get(subElement).contains(coreId);
				}
			}
			return allChecked;
		}
	}
	
	public void setConstraint(String element, String coreId, boolean checked){
		if(constraints.containsKey(element)){
			if(checked)
				constraints.get(element).add(coreId);
			else
				constraints.get(element).remove(coreId);
		}else{
			for(String subElement : constraints.keySet()){
				if(subElement.startsWith(element)){
					if(checked)
						constraints.get(subElement).add(coreId);
					else
						constraints.get(subElement).remove(coreId);
				}
			}
		}
	}
	
	public Set<String> getCheckedActors(String coreId){
		HashSet<String> actors = new HashSet<String>();
		
		for(String actor : constraints.keySet()){
			if(constraints.get(actor).contains(coreId))
				actors.add(actor);
		}
		
		return actors;
	}
	
	public void updateFromGraph(PiGraph graph){
		updateFromGraph(graph, "/");
	}
		
	private void updateFromGraph(PiGraph graph, String prefix){
		prefix += graph.getName()+"/";
		for(AbstractActor abactor: graph.getVertices()){
			if(abactor instanceof Actor && ((Actor) abactor).getRefinement().getFileURI() != null){
				// Hierarchical actor
				try {
					URI uri = ((Actor) abactor).getRefinement().getFileURI();
					String file = uri.toPlatformString(true);
					PiGraph child = PiScenarioParser.getAlgorithm(file);
					updateFromGraph(child, prefix);
				} catch (InvalidModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}else if(abactor instanceof Actor){
				if(!constraints.containsKey(prefix+abactor.getName())){
					constraints.put(prefix+abactor.getName(), new HashSet<String>());
				}
			}else{
				// Do nothing
			}
		}
		
		// Remove Actor no more present
		for(String actor : getChildrenOf(prefix)){
			String actorName = actor.substring(prefix.length());
			// If it is not a hierarchical actor
			if(! actorName.contains("/")){
				// Try to find it in the graph
				boolean present = false;
				for(AbstractActor abactor: graph.getVertices()){
					if(abactor.getName() == actorName){
						present = true;
					}
				}
				if(!present)
					constraints.remove(actor);
			}else{ // It is a hierachical actor
				actorName = actorName.split("/")[0];
				// Try to find it in the graph
				boolean present = false;
				for(AbstractActor abactor: graph.getVertices()){
					if(abactor.getName() == actorName){
						present = true;
					}
				}
				if(!present){
					for(String subactor : constraints.keySet()){
						if(subactor.startsWith(actor)){
							constraints.remove(subactor);
						}
					}
				}
			}
		}
	}

}

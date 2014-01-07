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

import java.util.HashSet;
import java.util.Set;

import net.sf.dftools.algorithm.importer.InvalidModelException;

import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.experiment.core.piscenario.serialize.PiScenarioParser;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * An {@link ActorTree} is a tree of {@link ActorNode} used to store 
 * constraints and timings informations in the {@link PiScenario}.
 * @author jheulot
 *
 */
public class ActorTree {
	/**
	 * The containing {@link PiScenario}
	 */
	private PiScenario piscenario;
	
	/**
	 * The root  {@link ActorNode}
	 */
	private ActorNode root;
	
	/**
	 * Default Constructor of {@link ActorTree}
	 * @param scenario
	 */
	public ActorTree(PiScenario scenario){
		piscenario = scenario;
		root = null;	
	}

	/**
	 * Root {@link ActorNode} Getter.
	 * @return The root {@link ActorNode}
	 */
	public ActorNode getRoot() {
		return root;
	}

	/**
	 * Return the set of checked {@link ActorNode} for the given operator.
	 * @param currentOperator The operator
	 * @return The set of checked {@link ActorNode}
	 */
	public Set<ActorNode> getCheckedNodes(String currentOperator) {
		HashSet<ActorNode> result = new HashSet<ActorNode>();
		
		if(root != null)
			getCheckedNodes(result, root, currentOperator);
		
		return result;
	}
	
	private void getCheckedNodes(Set<ActorNode> result,  ActorNode currentNode, String currentOperator){
		for(ActorNode child : currentNode.getChildren()){
			if(child.getConstraint(currentOperator))
				result.add(child);
			if(!child.getChildren().isEmpty()){
				getCheckedNodes(result, child, currentOperator);
			}
		}
	}
	
	/**
	 * Update the tree, constraints and timings informations from the {@link PiScenario}. 
	 */
	public void update(){
		PiGraph graph;
		try {
			graph = PiScenarioParser.getAlgorithm(piscenario.getAlgorithmURL());
		} catch (InvalidModelException | CoreException e) {
			e.printStackTrace();
			return;
		}
		if(graph == null){
			root = null;
		}else if(root == null){
			root = new ActorNode(graph.getName(), null);
			update(graph);
		}else{
			root.setName(graph.getName());
			update(graph);
		}
	}
		
	private void update(PiGraph graph){
		// Remove Actor no more present
		for(ActorNode childNode : root.getChildren()){
			// Try to find it in the graph
			boolean present = false;
			for(Actor actor: graph.getActors()){
				if(actor.getName().contentEquals(childNode.getName())){
					present = true; 
					break;
				}
			}
			if(!present){
				root.getChildren().remove(childNode);
			}
		}
		
		// Add actor not present
		for(Actor actor: graph.getActors()){
			ActorNode childNode = root.getChild(actor.getName());
			if(childNode == null){
				childNode = new ActorNode(actor.getName(), root);
				root.getChildren().add(childNode);					
			}
			if(!actor.isHierarchical()){
				childNode.getChildren().clear();
			}
			childNode.update(piscenario.getOperatorIds(), piscenario.getOperatorTypes(), actor);
		}
	}
}

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
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.experiment.core.piscenario.ParameterValue.ParameterType;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * Leaf on the {@link ActorTree} used to store timings, 
 * constraints and parameters values in the scenario
 * @author jheulot
 *
 */
public class ActorNode {
	/**
	 * Tree Specific attributes
	 */
	private ActorNode parent;
    private Set<ActorNode> children;
    
    /**
     * Actor Specific Attributes
     */
	private String name;
	private Set<String> constraints;
	private Map<String,Timing> timings;
	private Set<ParameterValue> paramValues;

	/**
	 * Default constructor of {@link ActorNode}
	 * @param _name		Name of the Actor
	 * @param _parent	Parent {@link ActorNode}, null if none (root).
	 */
	public ActorNode(String _name, ActorNode _parent){
		name = _name;
		parent = _parent;
		children = new HashSet<ActorNode>(); 
		constraints = new HashSet<String>();
		timings = new HashMap<String,Timing>();   
		paramValues = new HashSet<ParameterValue>();      	
    }

	/**
	 * Getter of ActorNode children.
	 * @return Set of Children
	 */
    public Set<ActorNode> getChildren() {
		return children;
	}
    
    /**
     * Getter of ActorNode Parent.
     * @return	the Parent
     */
	public ActorNode getParent() {
		return parent;
	}
	
	/**
	 * Get the child with given name, null if none
	 * @param name The name of the required node
	 * @return the Child ActorNode, null if not found
	 */
    public ActorNode getChild(String name){
    	for(ActorNode node : children){
    		if(node.name.contentEquals(name)){
    			return node;
    		}
    	}
    	return null;
    }

    /**
     * Name Setter
     * @param _name
     */
	public void setName(String _name) {
		name = _name;
	}
	
	/**
	 * Name Getter
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the given constraint to this ActorNode
	 * @param currentOperator 	The given core
	 * @param isChecked			true if it can be run on the core
	 */
	public void setConstraint(String currentOperator, boolean isChecked){
		if(children.isEmpty()){
			if(isChecked)
				constraints.add(currentOperator);
			else
				constraints.remove(currentOperator);			
		}else{
			for(ActorNode child : children){
				child.setConstraint(currentOperator, isChecked);
			}	
		}
	}
	
	/**
	 * Get the given constraint to this ActorNode
	 * @param currentOperator 	The given core
	 * @return true if it can be run on this core
	 */
	public boolean getConstraint(String currentOperator) {
		if(children.isEmpty()){
			return constraints.contains(currentOperator);
		}else{
			boolean result = true;
			for(ActorNode child : children){
				result &= child.getConstraint(currentOperator);
			}
			return result;
		}
	}
	
	/**
	 * Get the {@link Timing} of the current node for the given operator type.
	 * @param currentOperatorType
	 * @return the timing if it is a leaf node, null if it is a hierarchical node
	 */
	public Timing getTiming(String currentOperatorType) {
		if(children.isEmpty()){
			return timings.get(currentOperatorType);
		}else{
			return null;
		}
	}
	
	/**
	 * Get the {@link ParameterValue} of the current node with the given name.
	 * @param currentOperatorType
	 * @return the parameter, or null if doesn't exist
	 */
	public ParameterValue getParamValue(String name) {
		for(ParameterValue param : paramValues){
			if(param.getName().contentEquals(name))
				return param;
		}
		return null;
	}
	
	/**
	 * @return the parameters
	 */
	public Set<ParameterValue> getParamValues() {
		return paramValues;
	}
	
	/**
	 * Test if the node is hierarchical
	 * @return true if the node is hierarchical
	 */
	public boolean isHierarchical(){
		return !children.isEmpty();
	}

	/**
	 * Constraints Getter
	 * @return the constraints
	 */
	public Set<String> getConstraints() {
		return constraints;
	}

	/**
	 * Get the {@link Timing} map of the current node.
	 * @return The map of Timings
	 */
	public Map<String, Timing> getTimings() {
		return timings;
	}
	
	/**
	 * Update the ActorNode values with a new Actor representation
	 * @param operatorIds	Set of operator
	 * @param operatorTypes Set of operator types
	 * @param actor			The corresponding actor
	 */
	public void update(Set<String> operatorIds, Set<String> operatorTypes, Actor actor) {
		if(actor.isHierarchical()){
			PiGraph graph = actor.getGraph();
			/* Remove Actor no more present */
			for(ActorNode childNode : children){
				/* Try to find it in the graph */
				boolean present = false;
				for(Actor childActor: graph.getActors()){
					if(childActor.getName().contentEquals(childNode.getName())){
						present = true; 
						break;
					}
				}
				if(!present){
					this.getChildren().remove(childNode);
				}
			}

			/* Remove Parameters no more present */
			for(ParameterValue var : paramValues){
				/* Try to find it in the graph */
				boolean present = false;
				for(Parameter param: graph.getParameters()){
					if(param.getName().contentEquals(param.getName())){
						present = true; 
						break;
					}
				}
				if(!present){
					paramValues.remove(var);
				}	
			}
			
			/* Update actors (Add if not present) */
			for(Actor childActor: graph.getActors()){
				ActorNode childNode = this.getChild(childActor.getName());
				if(childNode == null){
					childNode = new ActorNode(childActor.getName(), this);
					this.getChildren().add(childNode);					
				}				
				if(!childActor.isHierarchical()){
					childNode.getChildren().clear();
				}
				childNode.update(operatorIds, operatorTypes, childActor);
			}
			
			/* Update Parameters (Add if not present) */
			for(Parameter param: graph.getParameters()){
				ParameterValue paramValue = null;
				for(ParameterValue paramValueLoop : paramValues){
					if(paramValueLoop.getName().contentEquals(param.getName())){
						paramValue = paramValueLoop; 
						break;
					}
				}
				if(paramValue == null){
					if(!param.isLocallyStatic()){ 				
						/* DYNAMIC */
						paramValue = new ParameterValue(param.getName(), ParameterType.DYNAMIC, this);
					}else if(param.getConfigInputPorts().isEmpty()){ 
						/* STATIC */
						paramValue = new ParameterValue(param.getName(), ParameterType.STATIC, this);
					}else{
						/* DEPENDANT */
						paramValue = new ParameterValue(param.getName(), ParameterType.DEPENDENT, this);
						
						/* Update Parameter list */
						Set<String> params = new HashSet<String>();
						for(ConfigInputPort configInput : param.getConfigInputPorts()){
							params.add(configInput.getName());
						}
						paramValue.setInputParameters(params);
						
						/* Update Expression */
						paramValue.setExpression(param.getExpression().getString());
					}										
					paramValues.add(paramValue);	
				}	
			}
			
		}else{ /* Leaf Actor */
			/* Update operators in constraints */
			for(String core : constraints){
				if(!operatorIds.contains(core)){
					constraints.remove(core);
				}
			}
			
			/* Update Core type list in Timings */
			for(String coreType : timings.keySet()){
				if(!operatorTypes.contains(coreType)){
					timings.remove(coreType);
				}
			}
			for(String coreType : operatorTypes){
				if(!timings.containsKey(coreType)){
					timings.put(coreType, new Timing());
				}
			}
			
			/* Update Parameter list in Timings */
			Set<String> params = new HashSet<String>();
			for(ConfigInputPort configInput : actor.getConfigInputPorts()){
				params.add(configInput.getName());
			}
			for(Timing timing : timings.values()){
				timing.setInputParameters(params);
			}			
		}
	}
}

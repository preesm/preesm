package org.abo.preesm.plugin.dataparallel.operations.visitor

import java.util.Collection
import java.util.List
import java.util.Map
import org.abo.preesm.plugin.dataparallel.DAGComputationBug
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.abo.preesm.plugin.dataparallel.DAGSubsetConstructor
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex

/**
 * Static class that contains various utility functions. The functions
 * here depend on the operations or output of operations and can be 
 * computed with/without need of {@link DAGConstructor} instances
 * 
 * @author Sudeep Kanur
 */
class OperationsUtils {
	
	/**
	 * Get maximum level of the DAG, based on the level set passed
	 * 
	 * @param levels The {@link LevelsOperations#levels} passed
	 * @return int Maximum level 
	 */
	public static def int getMaxLevel(Map<SDFAbstractVertex, Integer> levels) {
		return (levels.values.max + 1)	
	}
	
	/**
	 * Get level sets based on the {@link LevelsOperations#levels} passed
	 * 
	 * @param levels The look up table consisting of node to its level in the DAG
	 * @return List of lists. Each list contains all the instances seen at a particular level. Note that the lists need not appear in any order 
	 */
	public static def List<List<SDFAbstractVertex>> getLevelSets(Map<SDFAbstractVertex, Integer> levels) {
		val List<List<SDFAbstractVertex>> levelSet = newArrayList()
		(0..<getMaxLevel(levels)).forEach[levelSet.add(newArrayList)]
		levels.forEach[instance, level | 
			levelSet.get(level).add(instance)
		]
		return levelSet
	}
	
	/**
	 * Pick an element from a collection
	 * This implementation picks the first element
	 * 
	 * @param set Any {@link Collection} containing {@link SDFAbstractVertex} instances
	 * @return An element from the collection passed 
	 */
	public static def SDFAbstractVertex pickElement(Collection<SDFAbstractVertex> set) {
		val itr = set.iterator
		return itr.next
	}
	
	/**
	 * Get the maximum of all the instances of the passed actor, based on the lookup table 
	 * passed as the parameter
	 * 
	 * @param dagGen A {@link DAGConstructor} instance containing the actor
	 * @param actor The maximum level is calculated for this actor
	 * @param levels The lookup table consisting of all the instances and levels, including instance of this actor
	 * @return The maximum level of all the instances seen for a given actor
	 */
	public static def int getMaxActorLevel(DAGConstructor dagGen, SDFAbstractVertex actor, Map<SDFAbstractVertex, Integer> levels) {
		val instances = dagGen.actor2Instances.get(actor)
		if(instances === null) {
			throw new DAGComputationBug
		}
		val levelsOfInstances = newArrayList
		instances.forEach[instance |
			levelsOfInstances.add(levels.get(instance))
		]
		
		return levelsOfInstances.max
	}
	
	/**
	 * Get the maximum of all the instances of the passed actor. 
	 * Implementation is same as {@link OperationsUtils#getMaxActorLevel} but the level set
	 * is the default level set of a {@link DAGConstructor} instance
	 * 
	 * @param dagGen A {@link DAGConstructor} instance containing the actor
	 * @param actor The maximum level is calculated for this actor
	 * @return The maximum level of all the instances seen for a given actor
	 */
	public static def int getMaxActorLevel(DAGConstructor dagGen, SDFAbstractVertex actor) {
		val levelOp = new LevelsOperations

		if(dagGen instanceof DAGSubsetConstructor) {
			dagGen.accept(levelOp)
		} else {
			(dagGen as PureDAGConstructor).accept(levelOp)
		}
		val levels = levelOp.levels
		return getMaxActorLevel(dagGen, actor, levels)
	}
	
	/**
	 * Check if a given level set is instance parallel
	 * The {@link PureDAGConstructor} instance is used to get instance to actor mapping
	 * and to check if it is instance independent
	 * 
	 * Logic of implementation is that if all the instances
	 * of an actor is not seen in a given level set, then other
	 * instances must be elsewhere and will be subsequently seen.
	 * When that happens, we return false, otherwise we return
	 * true
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @param levels The lookup table used to analyse parallelism in the DAG
	 * @return True if the passed level set is instance independent
	 */
	public static def boolean isParallel(PureDAGConstructor dagGen, Map<SDFAbstractVertex, Integer> levels) {
		val dependencyVisitor = new DependencyAnalysisOperations
		dagGen.accept(dependencyVisitor)
		if(!dependencyVisitor.isIndependent) {
			// An instance dependent DAG cannot be parallel
			return false
		}

		val levelSets = getLevelSets(levels)
		val seenActors = newHashSet
		
		for(levelSet: levelSets) {
			val seenInLevel = newHashSet
			for(instance: levelSet) {
				val actor = dagGen.instance2Actor.get(instance)
				if(actor === null) {
					throw new DAGComputationBug
				}
				if(seenActors.contains(actor)) {
					return false
				}
				seenInLevel.add(actor)
			}
			seenActors.addAll(seenInLevel)
		}
		return true
	}
	
	/**
	 * Check if a given level set is instance parallel
	 * The {@link DAGSubsetConstructor} instance is used to check if it is instance independent
	 * 
	 * A subset DAG is always parallel
	 * 
	 * @param dagGen A {@link DAGSubsetConstructor} instance
	 * @param levels Passed for the sake of preserving signature. Not used
	 * @return True if the passed level set is instance independent
	 */
	public static def boolean isParallel(DAGSubsetConstructor dagGen, Map<SDFAbstractVertex, Integer> levels) {
		val dependencyVisitor = new DependencyAnalysisOperations
		dagGen.accept(dependencyVisitor)
		if(!dependencyVisitor.isIndependent) {
			// An instance dependent DAG cannot be parallel
			return false
		}
		/*
		 * A subset DAG is always data-parallel.
		 */
		return true
	}
	
	/**
	 * Check if a given level is instance parallel
	 * This function performs dispatch based on the {@link DAGConstructor} instance
	 * 
	 * @param dagGen A {@link DAGConstructor} instance
	 * @param levels The look up table used to analyse instance independence
	 * @return True if the passed level set is instance independent
	 */
	public static def boolean isParallel(DAGConstructor dagGen, Map<SDFAbstractVertex, Integer> levels) {
		if(dagGen instanceof DAGSubsetConstructor) {
			return isParallel(dagGen, levels)
		} else {
			return isParallel(dagGen as PureDAGConstructor, levels)
		}
	}
	
	/**
	 * Get maximum depth/level at which all instances of an actor are contained
	 * in the same level
	 * 
	 * @param dagGen The levels used to find parallel level is calculated from this {@link PureDAGConstructor} instance
	 * @return If DAG has a parallel level, then it returns the maximum level. Otherwise it returns null
	 */
	public static def Integer getParallelLevel(PureDAGConstructor dagGen) {
		// Get the level set first
		val levelOps = new LevelsOperations
		dagGen.accept(levelOps)
		val levelSet = levelOps.levels
		
		return getParallelLevel(dagGen, levelSet)
	}
	
	/**
	 * Get maximum depth/level at which all instances of an actor are contained for a given levels
	 * Rest are same as {@link OperationsUtils#getParallelLevel}
	 * 
	 * @param dagGen A {@link PureDAGConstructor} used to get instance to actor mappings and vice-versa
	 * @param Given levels
	 * @return If DAG has a parallel level, then it returns the maximum level. Otherwise it returns null
	 */
	public static def Integer getParallelLevel(PureDAGConstructor dagGen, Map<SDFAbstractVertex, Integer> levels) {
		
		for(level: getLevelSets(levels)) {
			val currentLevel = levels.get(level.get(0)) 
			val actors = newHashSet
			val allInstances = newArrayList
			// Get all actors in the current level
			level.forEach[instance | actors.add(dagGen.instance2Actor.get(instance))]
			
			// Get all instances of the actors seen in current level
			actors.forEach[actor | 
				val instances = dagGen.actor2Instances.get(actor)
				allInstances.addAll(instances)
			]
			
			// A level set is parallel, if a level set contains all instances of all actors
			if(allInstances.filter[instance | !level.contains(instance)].empty) {
				return currentLevel
			}
		}
		// No parallel level exists in the graph
		return null
	}
}
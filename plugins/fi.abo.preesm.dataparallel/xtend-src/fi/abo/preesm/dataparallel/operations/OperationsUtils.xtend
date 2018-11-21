/**
 * Copyright or © or Copr. Åbo Akademi University (2017 - 2018),
 * IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Sudeep Kanur <skanur@abo.fi> (2017 - 2018)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fi.abo.preesm.dataparallel.operations

import fi.abo.preesm.dataparallel.DAGComputationBug
import fi.abo.preesm.dataparallel.DAGConstructor
import fi.abo.preesm.dataparallel.DAGSubsetConstructor
import fi.abo.preesm.dataparallel.PureDAGConstructor
import java.util.Collection
import java.util.List
import java.util.Map
import org.preesm.algorithm.model.sdf.SDFAbstractVertex

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
	static def int getMaxLevel(Map<SDFAbstractVertex, Integer> levels) {
		return (levels.values.max + 1)
	}

	/**
	 * Get level sets based on the {@link LevelsOperations#levels} passed
	 *
	 * @param levels The look up table consisting of node to its level in the DAG
	 * @return List of lists. Each list contains all the instances seen at a particular level. Note that the lists need not appear in any order
	 */
	static def List<List<SDFAbstractVertex>> getLevelSets(Map<SDFAbstractVertex, Integer> levels) {
		val List<List<SDFAbstractVertex>> levelSet = newArrayList()
		(0..<getMaxLevel(levels)).forEach[levelSet.add(newArrayList)]
		levels.forEach[instance, level |
			levelSet.get(level).add(instance)
		]
		return levelSet.filter[set | !set.empty].toList
	}

	/**
	 * Pick an element from a collection.
	 * <p>
	 * This implementation picks the first element.
	 *
	 * @param set Any {@link Collection} containing {@link SDFAbstractVertex} instances
	 * @return An element from the collection passed
	 */
	static def SDFAbstractVertex pickElement(Collection<SDFAbstractVertex> set) {
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
	static def int getMaxActorLevel(DAGConstructor dagGen, SDFAbstractVertex actor, Map<SDFAbstractVertex, Integer> levels) {
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
	static def int getMaxActorLevel(DAGConstructor dagGen, SDFAbstractVertex actor) {
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
	 * Check if a given level set is instance parallel.
	 * The {@link PureDAGConstructor} instance is used to get instance to actor mapping
	 * and to check if it is instance independent.
	 * <p>
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
	static def boolean isParallel(PureDAGConstructor dagGen, Map<SDFAbstractVertex, Integer> levels) {
		val dependencyVisitor = new DependencyAnalysisOperations
		dagGen.accept(dependencyVisitor)
		if(!dependencyVisitor.isIndependent) {
			// An instance dependent DAG cannot be parallel
			return false
		}

		val levelSets = getLevelSets(levels)
		val seenActors = newLinkedHashSet

		for(levelSet: levelSets) {
			val seenInLevel = newLinkedHashSet
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
	 * Check if a given level set is instance parallel.
	 * The {@link DAGSubsetConstructor} instance is used to check if it is instance independent.
	 * <p>
	 * A subset DAG is always parallel
	 *
	 * @param dagGen A {@link DAGSubsetConstructor} instance
	 * @param levels Passed for the sake of preserving signature. Not used
	 * @return True if the passed level set is instance independent
	 */
	static def boolean isParallel(DAGSubsetConstructor dagGen, Map<SDFAbstractVertex, Integer> levels) {
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
	 * Check if a given level is instance parallel.
	 * This function performs dispatch based on the {@link DAGConstructor} instance
	 *
	 * @param dagGen A {@link DAGConstructor} instance
	 * @param levels The look up table used to analyse instance independence
	 * @return True if the passed level set is instance independent
	 */
	static def boolean isParallel(DAGConstructor dagGen, Map<SDFAbstractVertex, Integer> levels) {
		if(dagGen instanceof DAGSubsetConstructor) {
			return isParallel(dagGen, levels)
		} else {
			return isParallel(dagGen as PureDAGConstructor, levels)
		}
	}

	/**
	 * Get maximum depth/level at which all instances of an actor are contained
	 * in the same level.
	 *
	 * @param dagGen The levels used to find parallel level is calculated from this {@link PureDAGConstructor} instance
	 * @return If DAG has a parallel level, then it returns the maximum level. Otherwise it returns null
	 */
	static def Integer getParallelLevel(PureDAGConstructor dagGen) {
		// Get the level set first
		val levelOps = new LevelsOperations
		dagGen.accept(levelOps)
		val levelSet = levelOps.levels

		return getParallelLevel(dagGen, levelSet, levelSet)
	}

	/**
	 * Use {@link GetParallelLevelBuilder} instead of this method
	 * <p>
	 * Get maximum depth/level at which all instances of an actor are contained for a given levels.
	 * Rest are same as {@link OperationsUtils#getParallelLevel}
	 *
	 * @param dagGen A {@link PureDAGConstructor} used to get instance to actor mappings and vice-versa
	 * @param origLevels Original level set containing all instances
	 * @param levels Given levels, with possible restricted levels
	 * @return If DAG has a parallel level, then it returns the maximum level. Otherwise it returns null
	 */
	static def Integer getParallelLevel(PureDAGConstructor dagGen,
		Map<SDFAbstractVertex, Integer> origLevels,
		Map<SDFAbstractVertex, Integer> levels) {

		val origLevelSets = getLevelSets(origLevels)

		// Populate only those actors that are relevant to the given level set
		for(level: getLevelSets(levels)) {
			val currentLevel = levels.get(level.get(0))
			val origLevel = origLevelSets.filter[l | l.contains(level.get(0))].get(0)
			val actors = newLinkedHashSet
			// Get all actors in the current level
			level.forEach[instance | actors.add(dagGen.instance2Actor.get(instance))]

			// Check if all instances of actors in the current level are seen
			for(actor: actors) {
				val instances = dagGen.actor2Instances.get(actor)
				if(instances.forall[instance | origLevel.contains(instance)]) {
					return currentLevel
				}
			}
		}
		// No parallel level exists in the graph
		return null
	}
}

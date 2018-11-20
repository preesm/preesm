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

import fi.abo.preesm.dataparallel.CannotRearrange
import fi.abo.preesm.dataparallel.DAG2DAG
import fi.abo.preesm.dataparallel.DAGComputationBug
import fi.abo.preesm.dataparallel.PureDAGConstructor
import fi.abo.preesm.dataparallel.SDF2DAG
import fi.abo.preesm.dataparallel.iterator.SubsetTopologicalIterator
import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.logging.Level
import java.util.logging.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import org.preesm.algorithm.model.sdf.SDFAbstractVertex
import org.preesm.algorithm.model.sdf.esdf.SDFForkVertex
import org.preesm.algorithm.model.sdf.esdf.SDFJoinVertex
import org.preesm.algorithm.model.visitors.SDF4JException

/**
 * DAG operation that finds the instances that needs to be moved in the
 * transient DAG
 *
 * @author Sudeep Kanur
 */
class MovableInstances implements DAGOperations {

	/**
	 * The lookup table of instance and levels that are
	 * obtained after rearranging.
	 * Apart from acyclic-like SDFGs, this map is not
	 * guaranteed to be parallel.
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val Map<SDFAbstractVertex, Integer> rearrangedLevels

	/**
	 * List of all the instances that are to be moved in the
	 * transient DAG
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> movableInstances

	/**
	 * List of all the instances that also form root instance of
	 * some or the other cycle
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> movableRootInstances

	/**
	 * List of all the instances that form the end of the chain
	 * starting from the {@link MovableInstances#movableRootInstances}
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> movableExitInstances

	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> interfaceActors

	/**
	 * Logger
	 */
	@Accessors(PRIVATE_GETTER, PRIVATE_GETTER)
	val Logger logger

	new(List<SDFAbstractVertex> interfaceActors, Logger logger) {
		rearrangedLevels = newLinkedHashMap
		movableInstances = newArrayList
		movableRootInstances = newArrayList
		movableExitInstances = newArrayList
		this.interfaceActors = interfaceActors
		this.logger = logger
	}

	new(List<SDFAbstractVertex> interfaceActors) {
		this(interfaceActors, null)
	}

	private def log(Level level, String message) {
		if(this.logger !== null) {
			this.logger.log(level, message)
		}
	}

	/**
	 * Helper function that finds instances that are to be moved to the transient graph. The
	 * detailed implementation algorithm is found in DASIP-2017 paper.
	 *
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @throws SDF4JException If the DAG is not instance independent
	 */
	private def void findMovableInstances(PureDAGConstructor dagGen) throws SDF4JException , CannotRearrange {
		val parallelVisitor = new DependencyAnalysisOperations
		dagGen.accept(parallelVisitor)

		val isDAGInd = parallelVisitor.isIndependent
		if(!isDAGInd) {
			throw new SDF4JException("DAG is not instance independent. Getting movable instance" +
				" is meaningless.")
		}

		// Get all the root instances
		val rootVisitor = new RootExitOperations
		dagGen.accept(rootVisitor)
		val rootInstances = rootVisitor.rootInstances

		// We want pick anchor instance from an actor that is not a
		// source actor and is part of strongly connected component.
		val interfaceActorNames = interfaceActors.map[actor | actor.name]
		val nonSourceActors = rootVisitor.rootActors.filter[actor |
			!interfaceActorNames.contains(actor.name)
		].toList

		if(nonSourceActors.empty) {
			log(Level.WARNING, "No unconnected root instances found.")
			throw new CannotRearrange()
		}

		var List<SDFAbstractVertex> anchorInstances = newArrayList
		val localNonSourceActors = new ArrayList(nonSourceActors)
		var Integer parLevel = null
		val levelVisitor = new LevelsOperations
		dagGen.accept(levelVisitor)

		// Iterate through all available root actors (except neighbours of interfaces)
		// and check if we get a parallel level
		while(!localNonSourceActors.empty && parLevel === null) {
			val anchorActor = localNonSourceActors.remove(0)
			// Get all associated instances that belong to same actor
			anchorInstances.clear
			anchorInstances = dagGen.actor2Instances.get(anchorActor).filter[instance |
				rootInstances.contains(instance)
			].toList
			val relevantRootActors = rootVisitor.rootActors.filter[actor |
				actor != anchorActor && !interfaceActors.contains(actor)
			]
			if(!relevantRootActors.empty) {
				val relevantRootActor = relevantRootActors.get(0)
				val relevantRootInstances = rootInstances.filter[instance |
					val actor = dagGen.instance2Actor.get(instance)
					actor == relevantRootActor
				].toList
				// Get levels
				rearrangedLevels.clear
				rearrangedLevels.putAll(levelVisitor.levels)

				rearrangeAcyclic(dagGen, relevantRootInstances)

				val subsetLevels = rearrangedLevels.filter[node, level |
					val actor = dagGen.instance2Actor.get(node)
					!interfaceActors.contains(actor)
				]

				// Maximum level starting from 0, where the instances need to be moved.
				// Make sure the instances from source actors are ignored, otherwise, lbar
				// will always result to 0. This can be done by removing all edges that has delays
				// greater than or equal to production rate (or consumption rate)
				parLevel = (new GetParallelLevelBuilder)
							.addDagGen(dagGen)
							.addOrigLevels(rearrangedLevels)
							.addSubsetLevels(subsetLevels)
							.build()
			}
		}
		if(parLevel === null) {
			log(Level.WARNING, "Not enough anchors or relevant instances.")
			throw new CannotRearrange()
		}

		val lbar = parLevel
		anchorInstances.forEach[anchor |
			val sit = new SubsetTopologicalIterator(dagGen, anchor)
			while(sit.hasNext) {
				val instance = sit.next
				val actor = dagGen.instance2Actor.get(instance)
				// Consider only non-interface actors
				if(!interfaceActors.contains(actor)) {
					val instanceLevel = rearrangedLevels.get(instance)
					if(instanceLevel < lbar) {
						movableInstances.add(instance)

						if(instanceLevel == 0 && !(instance instanceof SDFForkVertex)) {
							movableRootInstances.add(instance)
						} else if(instanceLevel == (lbar -1)) {
							// Check if there is an explode instance of this instance
							if(!(instance instanceof SDFForkVertex) && !(instance instanceof SDFJoinVertex)) {
								val expImpInstances = dagGen.explodeImplodeOrigInstances.filter[expImp, origInstance |
									(origInstance == instance) && (expImp instanceof SDFForkVertex)
								]
								if(expImpInstances.empty) {
									// No explode instance associated with this
									movableExitInstances.add(instance)
								}
								// else wait until its explode instance is seen and then add it
							} else {
								if(instance instanceof SDFForkVertex) {
									movableExitInstances.add(instance)
								}
							}
						}
					}
				}
			}
		]
	}

	/**
	 * Helper function to completely sort acyclic-like DAGs and partially
	 * sort cyclic-DAGs. The algorithm and its description is given in
	 * DASIP-2017 paper.
	 *
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @param rootInstance Root instances that needs to be considered for sorting
	 */
	private def void rearrangeAcyclic(PureDAGConstructor dagGen,
		List<SDFAbstractVertex> rootInstances) {
		/*
		 * Rearranging level set is same as rearranging levels
		 * Getting levels sets is computed based on levels. So we
		 * just manipulate the levels of the node
		 */

		// Get root instances
		val rootVisitor = new RootExitOperations
		dagGen.accept(rootVisitor)
		val dagGenRootInstances = rootVisitor.rootInstances
		val relevantRootInstances = dagGenRootInstances.filter[ instance |
			rootInstances.contains(instance)
		]
		if(relevantRootInstances.empty && !rootInstances.empty) {
			throw new DAGComputationBug("Instances passed as root may not be in root instances set")
		}

		relevantRootInstances.forEach[rootNode |
			val actor = dagGen.instance2Actor.get(rootNode)
			if(actor === null) {
				throw new DAGComputationBug("Instance to actor map should return appropriate"
				+ " actor for an instance")
			}

			if(OperationsUtils.getMaxActorLevel(dagGen, actor) > 0) {
				// All the instances of this actor needs rearranging
				val instancesInRoot = relevantRootInstances.filter[instance |
					dagGen.actor2Instances.get(actor).contains(instance)
				]

				// Take each instance that needs rearranging as root node, construct a
				// DAG subset and rearrange all the nodes seen in the path
				instancesInRoot.forEach[instance |
					val sit = new SubsetTopologicalIterator(dagGen, instance)
					val instanceSources = sit.instanceSources
					val seenNodes = newLinkedHashSet // List of nodes that are not implode/explode but are seen during iteration
					while(sit.hasNext) {
						val node = sit.next
						var levelOfNode = 0

						// Calculate the current level of the node
						val predecessors = instanceSources.get(node)
						if(predecessors.isEmpty) {
							levelOfNode = 0
							seenNodes.add(node)
						} else {
							val predecessorLevel = rearrangedLevels.filter[ vertex, value |
								predecessors.contains(vertex)].values.max

							if(dagGen.explodeImplodeOrigInstances.keySet.contains(node)) {
								val origInstance = dagGen.explodeImplodeOrigInstances.get(node)
								if(seenNodes.contains(origInstance)){
									levelOfNode = predecessorLevel
								} else {
									levelOfNode = predecessorLevel + 1
									seenNodes.add(origInstance)
								}
							} else {
								if(seenNodes.contains(node)) {
									levelOfNode = predecessorLevel
								} else {
									levelOfNode = predecessorLevel + 1
									seenNodes.add(node)
								}
							}
						}

						rearrangedLevels.put(node, levelOfNode)

						val maxActorLevel = OperationsUtils.getMaxActorLevel(dagGen,
							dagGen.instance2Actor.get(node), rearrangedLevels)
						if(levelOfNode != maxActorLevel) {
							// Change the levels of this node and associated fork/joins
							val forkJoinInstances = dagGen.explodeImplodeOrigInstances.filter[forkJoin, origInstance |
								origInstance == node
							]
							forkJoinInstances.forEach[forkJoin, level |
								rearrangedLevels.put(forkJoin, maxActorLevel)
							]

							rearrangedLevels.put(node, maxActorLevel)
						}
					}
				]
			}
		]

		// Update the levels of implode/expldoe instances not seen by the root instance,
		// but may be connected through some other path
		// Properly set the implode and explode according to the level of its original instance
		// TODO Iterate only through implode explode instances instead of checking the existence of node
		rearrangedLevels.keySet.forEach[vertex |
			if(dagGen.explodeImplodeOrigInstances.keySet.contains(vertex)) {
				val origNode = dagGen.explodeImplodeOrigInstances.get(vertex)
				rearrangedLevels.put(vertex, rearrangedLevels.get(origNode))
			}
		]
	}

	/**
	 * Visitor method that provides movable instances of a {@link SDF2DAG}
	 * instance.
	 * <p>
	 * Use {@link MovableInstances#movableInstances} to get list of movable instances
	 * @throws SDF4JException If the DAG is not instance independent.
	 */
	override visit(SDF2DAG dagGen) {
		findMovableInstances(dagGen)
	}

	/**
	 * Visitor method that provides movable instances of a {@link DAG2DAG}
	 * instance.
	 * <p>
	 * Use {@link MovableInstances#movableInstances} to get list of movable instances
	 * @throws SDF4JException If the DAG is not instance independent.
	 */
	override visit(DAG2DAG dagGen) {
		findMovableInstances(dagGen)
	}

}

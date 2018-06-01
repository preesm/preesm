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
package fi.abo.preesm.dataparallel

import java.util.List
import java.util.Map
import java.util.logging.Level
import java.util.logging.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex

/**
 * Implement common getter methods and instance variables for {@link DAGConstructor} class
 *
 * @author Sudeep Kanur
 */
abstract class AbstractDAGConstructor implements DAGConstructor {

	/**
	 * Logger to optionally log messages for debugging purposes
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val Logger logger

	/**
	 * Map of actor and all of its instances (including implode/explode)
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val Map<SDFAbstractVertex, List<SDFAbstractVertex>> actor2Instances

	/**
	 * Map of instance and its corresponding actor
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val Map<SDFAbstractVertex, SDFAbstractVertex> instance2Actor

	/**
	 * List of source actors
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val List<SDFAbstractVertex> sourceActors

	/**
	 * List of sink actors
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val List<SDFAbstractVertex> sinkActors

	/**
	 * Map of implode/explode instances to its original instance
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val Map<SDFAbstractVertex, SDFAbstractVertex> explodeImplodeOrigInstances

	/**
	 * Map of actor from original input SDFG to all its immediate predecessors
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val Map<SDFAbstractVertex, List<SDFAbstractVertex>> actorPredecessor

	protected new(Logger logger){
		this.logger = logger
		this.actor2Instances = newLinkedHashMap
		this.instance2Actor = newLinkedHashMap
		this.explodeImplodeOrigInstances = newLinkedHashMap
		this.sourceActors = newArrayList
		this.sinkActors = newArrayList
		this.actorPredecessor = newLinkedHashMap
	}

	protected new() {
		this(null)
	}

	/**
	 * {@link DAGConstructor#log}
	 */
	override void log(Level level, String message) {
		logger?.log(level, message)
	}

	/**
	 * {@link DAGConstructor#getSourceInstances}
	 */
	override List<SDFAbstractVertex> getSourceInstances() {
		val sourceInstances = newArrayList
		sourceActors.forEach[ actor |
			sourceInstances.addAll(actor2Instances.get(actor))
		]
		return sourceInstances
	}

	/**
	 * {@link DAGConstructor#getSinkInstances}
	 */
	override List<SDFAbstractVertex> getSinkInstances() {
		val sinkInstances = newArrayList
		sinkActors.forEach[actor|
			sinkInstances.addAll(actor2Instances.get(actor))
		]
		return sinkInstances
	}
}

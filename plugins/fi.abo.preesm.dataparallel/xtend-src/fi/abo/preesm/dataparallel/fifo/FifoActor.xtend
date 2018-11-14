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
package fi.abo.preesm.dataparallel.fifo

import java.util.List
import java.util.logging.Level
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.AbstractEdge
import org.ietr.dftools.algorithm.model.IInterface
import org.ietr.dftools.algorithm.model.InterfaceDirection
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.preesm.commons.logger.PreesmLogger

/**
 * Class that represents a FIFO initialisation refinement. Firing this actor represents
 * initialisation of a FIFO. Representing FIFO initialisation refinement as a FIFO actor allows
 * non-trivial actor initialisations, which is necessary for re-timing transformations.
 * <p>
 * {@link FifoActor} has a (optional) production and (optional) consumption rate of 1. The
 * repetition vector signifies the number of delay tokens that needs to be initialised. As edges
 * are split during SDF to SrSDF & DAG transformation, precedence of edges are maintained using
 * startIndex attribute.
 * <p>
 * If the FifoActor has no production or consumption, then the actor does not have a
 * non-trivial initialisation.
 *
 * @author Sudeep Kanur
 */
class FifoActor extends SDFAbstractVertex {

	/** Kind of node. */
	public static val String FIFOACTOR = "fifoActor";

	@Accessors(PUBLIC_GETTER, PUBLIC_SETTER)
	var int startIndex

	new(int startIndex) {
		super()
		kind = FIFOACTOR
		this.startIndex = startIndex
	}

	new() {
		this(0)
	}

	override FifoActor copy() {
		val FifoActor newActor = new FifoActor(this.startIndex)

		// Copy bean properties, if any
		this.propertyBean.keys.forEach[key |
			if(this.propertyBean.getValue(key) !== null) {
				val value = this.propertyBean.getValue(key)
				newActor.propertyBean.setValue(key, value)
			}
		]

		// Copy refinement properties
		newActor.refinement = this.refinement

		// FIFO actors cannot have nested graphs.

		// Copy source and sink interfaces, if any
		this.sinks.forEach[sink |
			newActor.addSink(sink.copy())
		]

		this.sources.forEach[source |
			newActor.addSource(source.copy())
		]

		newActor.name = this.name
		newActor.nbRepeat = this.nbRepeat

		return newActor
	}

	override connectionAdded(AbstractEdge<?, ?> e) {
		// Nothing to do
	}

	override connectionRemoved(AbstractEdge<?, ?> e) {
		// Nothing to do
	}

	/**
	 * In addition to adding interface, asserts that the {@link FifoActor} cannot have more than
	 * one sink and source interfaces.
	 *
	 * @param port {@link IInterface} port instance
	 */
	override addInterface(IInterface port) {
		if((port instanceof SDFInterfaceVertex) &&
			(port.direction == InterfaceDirection.INPUT)) {
			if(this.sources.size >= 1) {
				throw new SDF4JException("FIFO-Actor cannot have more than one source")
			}
		} else if((port instanceof SDFInterfaceVertex) &&
					(port.direction == InterfaceDirection.OUTPUT)) {
			if(this.sinks.size >= 1) {
				throw new SDF4JException("FIFO-Actor cannot have more than one sink")
			}
		}
		return super.addInterface(port)
	}

	/**
	 * In addition to adding an output port, it checks that {@link FifoActor} has not more than
	 * one sink
	 *
	 * @param sink The output port interface that needs to be added to the actor
	 */
	override addSink(SDFSinkInterfaceVertex sink) {
		if(this.sinks.size >= 1) {
			throw new SDF4JException("FIFO-Actor cannot have more than one sink")
		}
		return super.addSink(sink)
	}

	/**
	 * In addition to adding an input port, it checks that {@link FifoActor} has not more than one
	 * source
	 *
	 * @param source The input port interface that is added to the actor
	 */
	override addSource(SDFSourceInterfaceVertex source) {
		if(this.sources.size >= 1) {
			throw new SDF4JException("FIFO-Actor cannot have more than one source")
		}
		return super.addSource(source)
	}

	/**
	 * In addition to setting a list of output ports, it also checks that not more than one sink
	 * is added to the {@link FifoActor}
	 *
	 * @param sinks A list of output ports
	 */
	override setSinks(List<SDFSinkInterfaceVertex> sinks) {
		if(sinks.size > 1) {
			throw new SDF4JException("FIFO-Actor cannot have more than one sink. Argument has " +
				sinks.size + " sink interfaces.")
		}
		super.sinks = sinks
	}

	/**
	 * In addition to setting a list of input ports, it also checks that not more than one source
	 * is added to the {@link FifoActor}
	 *
	 * @param sources A list of input ports
	 */
	override setSources(List<SDFSourceInterfaceVertex> sources) {
		if(sources.size > 1) {
			throw new SDF4JException("FIFO-Actor cannot have more than one source. Argument has " +
				sources.size + " source interfaces.")
		}
	}

	/**
	 * While setting repetition rate, asserts that it can't be more than one.
	 *
	 * @param nbRepeat Repetition rate
	 */
	override setNbRepeat(long nbRepeat) {
		if(nbRepeat <= 0) {
			throw new SDF4JException("FIFO-Actor cannot have repetition vector less than 1 ")
		}
		super.nbRepeat = nbRepeat
	}

	/**
	 * Validates the assumption of the {@link FifoActor}. It checks:
	 * <ul>
	 * 	<li> The repetition rate is one
	 *  <li> The number of sources are not more than one
	 *  <li> The number of sinks are not more than one
	 * </ul>
	 */
	override validateModel() {
		if(this.sinks.size > 1) {
			PreesmLogger.getLogger().log(Level.SEVERE, "FIFO-Actor cannot have more than one sink")
			return false
		}

		if(this.sources.size > 1) {
			PreesmLogger.getLogger().log(Level.SEVERE, "FIFO-Actor cannot have more than one source")
			return false
		}

		if(this.nbRepeatAsLong <= 0) {
			PreesmLogger.getLogger().log(Level.SEVERE, "FIFO-Actor cannot have repetition vector less than 1 ")
			return false
		}

		return super.validateModel()
	}
}

package org.abo.preesm.plugin.dataparallel.fifo

import java.util.List
import java.util.logging.Level
import java.util.logging.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.AbstractEdge
import org.ietr.dftools.algorithm.model.IInterface
import org.ietr.dftools.algorithm.model.InterfaceDirection
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex
import org.ietr.dftools.algorithm.model.visitors.SDF4JException

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
	
	override clone() {
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
			newActor.addSink(sink.clone())		
		]
		
		this.sources.forEach[source |
			newActor.addSource(source.clone())
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
			(port.direction == InterfaceDirection.Input)) {
			if(this.sources.size >= 1) {
				throw new SDF4JException("FIFO-Actor cannot have more than one source")
			}
		} else if((port instanceof SDFInterfaceVertex) &&
					(port.direction == InterfaceDirection.Output)) {
			if(this.sinks.size >= 1) {
				throw new SDF4JException("FIFO-Actor cannot have more than one sink")
			}				
		}
		return super.addInterface(port)
	}
	
	/**
	 * In addition to adding a list of interfaces, it checks that the {@link FifoActor} has not 
	 * more than 1 source and sink interface
	 * 
	 * @param interfaces List of {@link IInterface} instances
	 */
	override addInterfaces(List<IInterface> interfaces) {
		interfaces.forEach[interface |
			if((interface instanceof SDFInterfaceVertex) && 
				(interface.direction == InterfaceDirection.Input)) {
				if(this.sources.size >= 1) {
					throw new SDF4JException("FIFO-Actor cannot have more than one source")
				}
			} else if((interface instanceof SDFInterfaceVertex) &&
						(interface.direction == InterfaceDirection.Output)) {
				if(this.sinks.size >= 1) {
					throw new SDF4JException("FIFO-Actor cannot have more than one sink")
				}				
			}			
		]
		super.addInterfaces(interfaces)
	}
	
	/**
	 * In addition to adding an output port, it checks that {@link FifoActor} has not more than
	 * one sink
	 * 
	 * @param sink The output port interface that needs to be added to the actor
	 */
	override addSink(SDFInterfaceVertex sink) {
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
	override addSource(SDFInterfaceVertex source) {
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
	override setSinks(List<SDFInterfaceVertex> sinks) {
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
	override setSources(List<SDFInterfaceVertex> sources) {
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
	override setNbRepeat(int nbRepeat) {
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
	override validateModel(Logger logger) {
		if(this.sinks.size > 1) {
			logger.log(Level.SEVERE, "FIFO-Actor cannot have more than one sink")
			return false
		}
		
		if(this.sources.size > 1) {
			logger.log(Level.SEVERE, "FIFO-Actor cannot have more than one source")
			return false
		}
		
		if(this.nbRepeatAsInteger <= 0) {
			logger.log(Level.SEVERE, "FIFO-Actor cannot have repetition vector less than 1 ")
			return false
		}
		
		return super.validateModel(logger)
	}
}
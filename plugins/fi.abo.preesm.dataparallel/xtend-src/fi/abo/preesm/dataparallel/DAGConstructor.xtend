/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Sudeep Kanur <skanur@abo.fi> (2017)
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
import java.util.logging.Logger
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import java.util.logging.Level

/**
 * Construct DAG from SDF or from another DAG
 * 
 * @author Sudeep Kanur
 */
interface DAGConstructor {
	
	/**
	 * Optionally return the logger
	 * 
	 * @return A {@link Logger} instance that was passed
	 */
	public def Logger getLogger()
	
	/**
	 * Optionally log message to the console
	 * 
	 * @param message Message to log
	 */
	public def void log(Level level, String message)
	
	/**
	 * Returns the instances associated with the actor. Includes implodes and explodes
	 * 
	 * @return Lookup table of actors and their associated instances
	 */
	public def Map<SDFAbstractVertex, List<SDFAbstractVertex>> getActor2Instances()
	
	/**
	 * Returns the actor associated with the instance. Includes implodes and explodes
	 * 
	 * @return Lookup table of instances and its associated actor
	 */
	public def Map<SDFAbstractVertex, SDFAbstractVertex> getInstance2Actor()
	
	/**
	 * Gets the original instance associated with an implode or explode. 
	 * The key is only implode and explode nodes
	 * 
	 * @return Lookup table of implode/explode and its associated instance
	 */
	public def Map<SDFAbstractVertex, SDFAbstractVertex> getExplodeImplodeOrigInstances()
	
	/**
	 * Get Source actors. 
	 * Source actors are defined as those actors in the original SDF 
	 * that have no inputs
	 * 
	 * @return List of source actors
	 */
	public def List<SDFAbstractVertex> getSourceActors()
	
	/**
	 * Get Sink actors. 
	 * Sink actors are defined as those actors in the original SDFG that have
	 * no outputs
	 * 
	 * @return List of sink actors
	 */
	public def List<SDFAbstractVertex> getSinkActors()
	
	/**
	 * Get instances of source actors
	 * 
	 * @return List of instances of source actors
	 */
	public def List<SDFAbstractVertex> getSourceInstances()
	
	/**
	 * Get instances of sink actors
	 * 
	 * @return List of instances of sink actors
	 */
	public def List<SDFAbstractVertex> getSinkInstances()
}

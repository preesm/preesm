package org.abo.preesm.plugin.dataparallel

import org.ietr.dftools.algorithm.model.visitors.SDF4JException

/**
 * Exception class used exclusively when certain assertions fail
 * The exception is used to indicate existence of bugs and prints
 * additional contact information
 * 
 * @author Sudeep Kanur
 */
class DAGComputationBug extends SDF4JException {
	
	static val bugMessage = "Open an issue, or contact Sudeep Kanur (skanur@abo.fi, skanur@protonmail.com) with the graph that caused the exception."
	
  /**
   * Creates a new Bug exception with custom message
   *
   * @param message
   *          The error message
   */
  new(String message) {
    super("BUG!\n" + message + "\n" + bugMessage, null);
  }

  new(String message, Throwable t) {
    super("BUG!\n" + message + "\n" + bugMessage, t);
  }
  
  new() {
  	super("BUG!\n" + bugMessage, null)
  }
}
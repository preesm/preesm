package org.ietr.preesm.experiment.pimm.cppgenerator.scala.utils

import org.ietr.preesm.experiment.model.pimm._
import java.util.Map
import java.util.HashMap
//Allows to consider Java collections as Scala collections and to use foreach...
import collection.JavaConversions._

trait CppCodeGenerationNameGenerator {
  /**
   * Returns the name of the subgraph pg
   */
  protected def getSubraphName(pg: PiGraph): String = pg.getName() + "_subGraph"

  /**
   * Returns the name of the variable pointing to the C++ object corresponding to AbstractActor aa
   */
  protected def getVertexName(aa: AbstractActor): String = "vx" + aa.getName()

  /**
   * Returns the name of the building method for the PiGraph pg
   */
  protected def getMethodName(pg: PiGraph): String = pg.getName()
  
  /**
   * Returns the name of the parameter
   */
  protected def getParameterName(p: Parameter): String = "param_" + p.getName();

  /**
   * Returns the name of the C++ edge corresponding to Fifo f
   */
  private var edgeCounter: Integer = -1
  private var edgeMap: Map[Fifo, String] = new HashMap[Fifo, String]
  protected def getEdgeName(f: Fifo): String = {
    if (!edgeMap.containsKey(f)) {
      edgeCounter = edgeCounter + 1
      edgeMap.put(f, "edge" + edgeCounter)
    }
    edgeMap.get(f)
  }
}
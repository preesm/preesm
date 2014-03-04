/**
 * *****************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 *
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
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
 * ****************************************************************************
 */
package org.ietr.preesm.experiment.pimm.cppgenerator.scala.visitor

import org.ietr.preesm.experiment.model.pimm._
import java.util.HashMap
import java.util.Map
import collection.JavaConversions._
import org.ietr.preesm.experiment.pimm.cppgenerator.scala.utils.CppCodeGenerationNameGenerator
import org.ietr.preesm.experiment.pimm.visitor.scala.PiMMVisitor

/**
 * The C++ code generation needs a preprocess in order to get source/target nodes/actors for every Fifo and Dependency
 */
class CPPCodeGenerationPreProcessVisitor extends PiMMVisitor with CppCodeGenerationNameGenerator {

  //Variables containing the name of the currently visited AbstractActor for PortDescriptions
  private var currentAbstractVertexName: String = ""
  //Map linking data ports to their corresponding description
  private val dataPortMap: Map[Port, DataPortDescription] = new HashMap[Port, DataPortDescription]
  def getDataPortMap(): Map[Port, DataPortDescription] = dataPortMap

  //Map linking configuration input ports to the name of their node
  private val cfgInPortMap: Map[ConfigInputPort, String] = new HashMap[ConfigInputPort, String]

  //Map linking ISetters (Parameter and ConfigOutputPort) to the name of their node or their name
  private val setterMap: Map[ISetter, String] = new HashMap[ISetter, String]

  //Map linking dependencies to their corresponding description
  private val dependencyMap: Map[Dependency, DependencyDescription] = new HashMap[Dependency, DependencyDescription]
  def getDependencyMap(): Map[Dependency, DependencyDescription] = dependencyMap

  def visitPiGraph(pg: PiGraph): Unit = {
    visitAbstractActor(pg)
    pg.getActors().foreach(a => visit(a))
    pg.getParameters().foreach(p => visit(p))
    pg.getDependencies().foreach(d => visit(d))
  }

  def visitActor(a: Actor): Unit = {
    //If the refinement of a points to the description of PiGraph, visit it to preprocess its content
    val innerGraph: AbstractActor = a.getRefinement().getAbstractActor()
    if (innerGraph != null) {      
      visit(innerGraph)
    } else {
      visitAbstractActor(a)
    }    
  }

  def visitAbstractActor(aa: AbstractActor): Unit = {
    //Fix currentAbstractVertexName
    currentAbstractVertexName = getVertexName(aa)
    //Visit configuration input ports to fill cfgInPortMap
    visitAbstractVertex(aa)
    //Visit data ports to fill the dataPortMap
    aa.getDataInputPorts().foreach(p => visit(p))
    aa.getDataOutputPorts().foreach(p => visit(p))
    //Visit configuration output ports to fill the setterMap
    aa.getConfigOutputPorts().foreach(p => visit(p))
  }

  /**
   * When visiting data ports, we stock the necessary informations for edge generation into PortDescriptions
   */
  def visitDataInputPort(dip: DataInputPort): Unit = {
    //Fill dataPortMap
    dataPortMap.put(dip, new DataPortDescription(currentAbstractVertexName, dip.getExpression().getString()))
  }
  def visitDataOutputPort(dop: DataOutputPort): Unit = {
    //Fill dataPortMap
    dataPortMap.put(dop, new DataPortDescription(currentAbstractVertexName, dop.getExpression().getString()))
  }

  def visitConfigInputPort(cip: ConfigInputPort): Unit = {
    //Fill cfgInPortMap
    cfgInPortMap.put(cip, currentAbstractVertexName)
  }

  def visitConfigOutputPort(cop: ConfigOutputPort): Unit = {
    //Fill setterMap
    setterMap.put(cop, currentAbstractVertexName)
  }

  def visitFifo(f: Fifo): Unit = {
    throw new UnsupportedOperationException()
  }

  def visitInterfaceActor(ia: InterfaceActor): Unit = {
    visitAbstractActor(ia)
  }

  def visitDataInputInterface(dii: DataInputInterface): Unit = {
    visitInterfaceActor(dii)
  }

  def visitDataOutputInterface(doi: DataOutputInterface): Unit = {
    visitInterfaceActor(doi)
  }

  def visitConfigOutputInterface(coi: ConfigOutputInterface): Unit = {
    visitInterfaceActor(coi)
  }

  def visitRefinement(r: Refinement): Unit = {
    throw new UnsupportedOperationException()
  }

  def visitParameter(p: Parameter): Unit = {
    //Fix currentAbstractVertexName
    currentAbstractVertexName = getParameterName(p)
    //Visit configuration input ports to fill cfgInPortMap
    visitAbstractVertex(p)
    //Fill the setterMap
    setterMap.put(p, getParameterName(p))
  }

  def visitDependency(d: Dependency): Unit = {
    //Fill the dependencyMap with the names of source and target of d
    val srcName = setterMap.get(d.getSetter())
    val tgtName = cfgInPortMap.get(d.getGetter())
    dependencyMap.put(d, new DependencyDescription(srcName, tgtName))
  }

  def visitDelay(d: Delay): Unit = {
    throw new UnsupportedOperationException()
  }

  def visitExpression(e: Expression): Unit = {
    throw new UnsupportedOperationException()
  }

  def visitConfigInputInterface(cii: ConfigInputInterface): Unit = {
    throw new UnsupportedOperationException()
  }

  def visitAbstractVertex(av: AbstractVertex): Unit = {   
    //Visit configuration input ports to fill cfgInPortMap
    av.getConfigInputPorts().foreach(p => visit(p))
  }
}

/**
 * Class allowing to stock necessary information about data ports for the edges generation
 */
class DataPortDescription(val nodeName: String, val expression: String)

/**
 * Class allowing to stock necessary information about dependencies for parameter connections
 */
class DependencyDescription(val srcName: String, val tgtName: String)
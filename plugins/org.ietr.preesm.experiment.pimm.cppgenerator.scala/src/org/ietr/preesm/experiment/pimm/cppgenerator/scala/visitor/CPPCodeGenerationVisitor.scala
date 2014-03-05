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

import org.eclipse.emf.ecore.EObject
import org.ietr.preesm.experiment.model.pimm._
import scala.collection.immutable.Stack
import java.util.Map
import java.util.HashMap
import collection.JavaConversions._
import org.ietr.preesm.experiment.pimm.cppgenerator.scala.utils.CppCodeGenerationNameGenerator
import java.util.Collection
import org.ietr.preesm.experiment.pimm.visitor.scala.PiMMVisitor

//TODO: Find a cleaner way to setParentEdge in Interfaces
/* 
 * Ugly workaround for setParentEdge in Interfaces. Must suppose that fifos are always obtained in the same order => Modify the C++ headers?
 * A better way would be a possibility to get edges from one building method to the other (since the parentEdge is in the outer graph), 
 * maybe a map from edgeNames to edges with a method getOutputEdgeByName in BaseVertex 
 */

/**
 * PiMM models visitor generating C++ code for COMPA Runtime
 * currentGraph: The most outer graph of the PiMM model
 * currentMethod: The StringBuilder used to write the C++ code
 */
class CPPCodeGenerationVisitor(private val topMethod: StringBuilder, private val preprocessor: CPPCodeGenerationPreProcessVisitor)
  extends PiMMVisitor with CppCodeGenerationNameGenerator {

  //Maps to handle hierarchical graphs
  private val graph2method: Map[PiGraph, StringBuilder] = new HashMap[PiGraph, StringBuilder]
  private val graph2subgraphs: Map[PiGraph, List[PiGraph]] = new HashMap[PiGraph, List[PiGraph]]

  def getMethods(): Collection[StringBuilder] = graph2method.values()

  private var currentMethod: StringBuilder = topMethod
  private var currentGraph: PiGraph = null
  private var currentSubGraphs: List[PiGraph] = Nil

  //Variables containing the type of the currently visited AbstractActor for AbstractActor generation
  private var currentAbstractActorType: String = ""
  private var currentAbstractActorClass: String = ""

  //Map linking data ports to their corresponding description
  private val dataPortMap: Map[Port, DataPortDescription] = preprocessor.getDataPortMap
  //Map linking data ports to their corresponding description
  private val dependencyMap: Map[Dependency, DependencyDescription] = preprocessor.getDependencyMap
  //Map linking Fifos to their C++ position in their graph collection
  private val fifoMap: Map[Fifo, Integer] = preprocessor.getFifoMap

  //Shortcut for currentMethod.append()
  private def append(a: Any) = currentMethod.append(a)

  /**
   * When visiting a PiGraph (either the most outer graph or an hierarchical actor),
   * we should generate a new C++ method
   */
  def visitPiGraph(pg: PiGraph): Unit = {
    //We should first generate the C++ code as for any Actor in the outer graph
    currentAbstractActorType = "pisdf_vertex"
    currentAbstractActorClass = "PiSDFVertex"
    visitAbstractActor(pg)

    //We add pg as a subgraph of the current graph
    currentSubGraphs = pg :: currentSubGraphs

    //We stock the informations about the current graph for later use
    var currentOuterGraph: PiGraph = currentGraph
    if (currentOuterGraph != null) {
      graph2method.put(currentOuterGraph, currentMethod)
      graph2subgraphs.put(currentOuterGraph, currentSubGraphs)
    }
    //We initialize variables which will stock informations about pg during its method generation
    //The new current graph is pg
    currentGraph = pg
    //We start a new StringBuilder to generate its method
    currentMethod = new StringBuilder
    //Currently we know no subgraphs to pg
    currentSubGraphs = Nil

    //And then visit pg as a PiGraph, generating the method to build its C++ corresponding PiSDFGraph
    val pgName = pg.getName()

    append("\n// Method building PiSDFGraph ")
    append(pgName)
    //Generating the method signature
    generateMethodSignature(pg)
    //Generating the method body
    generateMethodBody(pg)

    //If pg has no subgraphs, its method has not been added in graph2method map
    if (!graph2method.containsKey(currentGraph)) {
      graph2method.put(currentGraph, currentMethod)
    }

    //We get back the informations about the outer graph to continue visiting it
    if (currentOuterGraph != null) {
      currentMethod = graph2method.get(currentOuterGraph)
      currentSubGraphs = graph2subgraphs.get(currentOuterGraph)
    }
    currentGraph = currentOuterGraph
  }

  /**
   * Concatenate the signature of the method corresponding to a PiGraph to the currentMethod StringBuilder
   */
  private def generateMethodSignature(pg: PiGraph): Unit = {
    //The method does not return anything
    append("\nvoid ")
    append(getMethodName(pg))
    //The method accept as parameter a pointer to the PiSDFGraph graph it will build and a pointer to the parent actor of graph (i.e., the hierarchical actor)
    append("(PiSDFGraph* graph, BaseVertex* parentVertex)")
  }

  /**
   * Concatenate the body of the method corresponding to a PiGraph to the currentMethod StringBuilder
   */
  private def generateMethodBody(pg: PiGraph): Unit = {
    append("{")
    //Generating parameters
    append("\n\n\t//Parameters")
    pg.getParameters().foreach(p => visit(p))
    //Generating vertices
    append("\n\n\t//Vertices")
    pg.getVertices().foreach(v => visit(v))
    //Generating edges
    append("\n\n\t//Edges")
    pg.getFifos().foreach(f => visit(f))
    //Generating call to methods generated for subgraphs, if any
    if (!currentSubGraphs.isEmpty) {
      append("\n\n\t//Subgraphs")
      generateCallsToSubgraphs()
    }
    append("\n}\n")
  }

  private def generateCallsToSubgraphs(): Unit = {
    //For each subgraph of the current graph
    currentSubGraphs.foreach(sg => {
      val sgName = getSubraphName(sg)
      val vxName = getVertexName(sg)
      //Generate test in order to prevent to reach the limit of graphs
      append("\n\tif(nb_graphs >= MAX_NB_PiSDF_SUB_GRAPHS - 1) exitWithCode(1054);")
      //Get the pointer to the subgraph
      append("\n\tPiSDFGraph *")
      append(sgName)
      append(" = &graphs[nb_graphs];")
      //Increment the graph counter
      append("\n\tnb_graphs++;")
      //Call the building method of sg with the pointer
      append("\n\t")
      append(getMethodName(sg))
      append("(")
      //Pass the pointer to the subgraph
      append(sgName)
      append(",")
      //Pass the parent vertex
      append(vxName)
      append(");")
      append("\n\t")
      //Set the subgraph as subgraph of the vertex
      append(vxName)
      append("->setSubGraph(")
      append(sgName)
      append(");")
    })
  }

  def visitActor(a: Actor): Unit = {
    currentAbstractActorType = "pisdf_vertex"
    currentAbstractActorClass = "PiSDFVertex"
    visitAbstractActor(a)
  }

  /**
   * Generic visit method for all AbstractActors (Actors, PiGraph)
   */
  def visitAbstractActor(aa: AbstractActor): Unit = {
    val isConfigVertex = aa.getConfigOutputPorts().size() > 0
    if (isConfigVertex) {
      currentAbstractActorClass = "PiSDFConfigVertex"
      currentAbstractActorType = "config_vertex"
    }

    //Call the addVertex method on the current graph
    append("\n\t")
    append(currentAbstractActorClass)
    append(" *")
    append(getVertexName(aa))
    append(" = (")
    append(currentAbstractActorClass)
    append("*)graph->addVertex(\"")
    //Pass the name of the AbstractActor
    append(aa.getName())
    append("\",")
    //Pass the type of vertex
    append(currentAbstractActorType)
    append(");")
    //Add connections to parameters if necessary
    aa.getConfigOutputPorts().foreach(cip => cip.getOutgoingDependencies().foreach(d => {
      append("\n\t")
      append(getVertexName(aa))
      append("->addRelatedParam(")
      append(dependencyMap.get(d).tgtName)
      append(");")
    }))
    //Add connections from parameters if necessary
    aa.getConfigInputPorts().foreach(cop => {
      append("\n\t")
      append(getVertexName(aa))
      append("->addParam(")
      append(dependencyMap.get(cop.getIncomingDependency()).srcName)
      append(");")
    })
  }

  /**
   * When visiting a FIFO we should add an edge to the current graph
   */
  def visitFifo(f: Fifo): Unit = {
    //Call the addEdge method on the current graph
    append("\n\t")
    append("graph->addEdge(")
    //Use the PortDescription of the source port to get the informations about the source node
    val src: DataPortDescription = dataPortMap.get(f.getSourcePort())
    //Pass the name of the source node
    append(src.nodeName)
    append(", \"")
    //Pass the production of the source node
    append(src.expression)
    append("\", ")
    //Use the PortDescription of the target port to get the informations about the target node
    val tgt: DataPortDescription = dataPortMap.get(f.getTargetPort())
    //Pass the name of the target node
    append(tgt.nodeName)
    append(", \"")
    //Pass the consumption of the target node
    append(tgt.expression)
    append("\", ")
    //Pass the delay of the FIFO
    if (f.getDelay() != null) append(f.getDelay().getExpression().getString())
    else append("0")
    append("\"")
    append(");")
  }

  def visitDataInputInterface(dii: DataInputInterface): Unit = {
    val vertexName = getVertexName(dii)

    //Adding the vertex to the current graph
    currentAbstractActorType = "input_vertex"
    currentAbstractActorClass = "PiSDFIfVertex"
    visitAbstractActor(dii)
    //Setting direction to 0 (input)
    append("\n\t")
    append(vertexName)
    append("->setDirection(0);")
    //Setting the parent vertex
    append("\n\t")
    append(vertexName)
    append("->setParentVertex(parentVertex);")
    //Setting the parent edge
    append("\n\t")
    append(vertexName)
    append("->setParentEdge(")
    //Getting the Fifo corresponding to the parent edge
    dii.getGraphPort() match {
      case dip: DataInputPort => {
        val incomingFifo = dip.getIncomingFifo()
        append("parentVertex->getInputEdge(")
        //XXX: setParentEdge workaround
        /* 
        * Ugly way to do this. Must suppose that fifos are always obtained in the same order => Modify the C++ headers?
        * A better way would be a possibility to get edges from one building method to the other (since the parentEdge is in the outer graph), 
        * maybe a map from edgeNames to edges with a method getOutputEdgeByName in BaseVertex 
        */
        append(fifoMap.get(incomingFifo))
        append(")")
      }
      case _ => throw new RuntimeException("Graph port of DataInputInterface " + dii.getName() + " is not a DataInputPort.")
    }
    append(");")
  }

  def visitDataOutputInterface(doi: DataOutputInterface): Unit = {
    val vertexName = getVertexName(doi)

    //Adding the vertex to the current graph
    currentAbstractActorType = "output_vertex"
    currentAbstractActorClass = "PiSDFIfVertex"
    visitAbstractActor(doi)
    //Setting direction to 1 (output)
    append("\n\t")
    append(vertexName)
    append("->setDirection(1);")
    //Setting the parent vertex
    append("\n\t")
    append(vertexName)
    append("->setParentVertex(parentVertex);")
    //Setting the parent edge
    append("\n\t")
    append(vertexName)
    append("->setParentEdge(")
    //Getting the Fifo corresponding to the parent edge
    doi.getGraphPort() match {
      case dop: DataOutputPort => {
        val incomingFifo = dop.getOutgoingFifo()
        append("parentVertex->getOutputEdge(")
        //XXX: setParentEdge workaround
        /* 
        * Ugly way to do this. Must suppose that fifos are always obtained in the same order => Modify the C++ headers?
        * A better way would be a possibility to get edges from one building method to the other (since the parentEdge is in the outer graph), 
        * maybe a map from edgeNames to edges with a method getOutputEdgeByName in BaseVertex 
        */
        append(fifoMap.get(incomingFifo))
        append(")")
      }
      case _ => throw new RuntimeException("Graph port of DataOutputInterface " + doi.getName() + " is not a DataOutputPort.")
    }

    append(");")
  }

  def visitConfigOutputInterface(coi: ConfigOutputInterface): Unit = {
    //TODO: Handle ConfigOutputInterface wrt. the COMPA runtime needs (cf. Yaset)
    throw new UnsupportedOperationException()
  }

  def visitConfigInputInterface(cii: ConfigInputInterface): Unit = visitParameter(cii)

  /**
   * When visiting a parameter, we should add a parameter to the current graph
   */
  def visitParameter(p: Parameter): Unit = {
    append("\n\tPISDFParameter *")
    append(getParameterName(p))
    append(" = graph->addParameter(\"")
    append(p.getName())
    append("\");")
  }
  def visitInterfaceActor(ia: InterfaceActor): Unit = throw new UnsupportedOperationException()
  def visitRefinement(r: Refinement): Unit = throw new UnsupportedOperationException()
  def visitDataInputPort(dip: DataInputPort): Unit = throw new UnsupportedOperationException()
  def visitDataOutputPort(dop: DataOutputPort): Unit = throw new UnsupportedOperationException()
  def visitConfigInputPort(cip: ConfigInputPort): Unit = throw new UnsupportedOperationException()
  def visitConfigOutputPort(cop: ConfigOutputPort): Unit = throw new UnsupportedOperationException()
  def visitDependency(d: Dependency): Unit = throw new UnsupportedOperationException()
  def visitDelay(d: Delay): Unit = throw new UnsupportedOperationException()
  def visitExpression(e: Expression): Unit = throw new UnsupportedOperationException()
}

/**
 * Class allowing to stock necessary information about graphs when moving through the graph hierrachy
 */
class GraphDescription(var subGraphs: List[PiGraph], var method: StringBuilder)

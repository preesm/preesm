package org.ietr.preesm.experiment.pimm.cppgenerator.scala.visitor

import org.eclipse.emf.ecore.EObject
import org.ietr.preesm.experiment.model.pimm._
import scala.collection.immutable.Stack
import java.util.Map
import java.util.HashMap
//Allows to consider Java collections as Scala collections and to use foreach...
import collection.JavaConversions._

/**
 * PiMM models visitor generating C++ code for COMPA Runtime
 * currentGraph: The most outer graph of the PiMM model
 * currentMethod: The StringBuilder used to write the C++ code
 */
class CPPCodeGenerationVisitor(private var currentGraph: PiGraph, private var currentMethod: StringBuilder) {

  //Stack and list to handle hierarchical graphs
  private val graphsStack: Stack[GraphDescription] = new Stack[GraphDescription]
  private var currentSubGraphs: List[PiGraph] = Nil
  //Variable containing the name of the currently visited Actor for PortDescriptions
  private var currentAbstractActorName: String = ""
  //Map linking ports to their correspondent description
  private val portMap: Map[Port, PortDescription] = new HashMap[Port, PortDescription]

  /**
   * Dispatch method to visit the different elements of a PiMM model
   * Cases must be ordered from bottom to top of the inheritance tree:
   * subclasses (most specific classes) must come before their superclasses
   */
  def visit(e: EObject): Unit = {
    e match {

      case dii: DataInputInterface => visitDataInputInterface(dii)
      case doi: DataOutputInterface => visitDataOutputInterface(doi)
      case coi: ConfigOutputInterface => visitConfigOutputInterface(coi)

      case ia: InterfaceActor => visitInterfaceActor(ia)
      case cii: ConfigInputInterface => visitConfigInputInterface(cii)
      case pg: PiGraph => visitPiGraph(pg)
      case a: Actor => visitActor(a)

      case aa: AbstractActor => visitAbstractActor(aa)

      case p: Parameter => visitParameter(p)
      case cop: ConfigOutputPort => visitConfigOutputPort(cop)

      case dip: DataInputPort => visitDataInputPort(dip)
      case dop: DataOutputPort => visitDataOutputPort(dop)
      case cip: ConfigInputPort => visitConfigInputPort(cip)
      case d: Delay => visitDelay(d)

      case f: Fifo => visitFifo(f)
      case r: Refinement => visitRefinement(r)
      case d: Dependency => visitDependency(d)
      case e: Expression => visitExpression(e)
    }
  }

  /**
   * When visiting a PiGraph (either the most outer graph or an hierarchical actor),
   * we should generate a new C++ method
   */
  private def visitPiGraph(pg: PiGraph): Unit = {
    //Add pg to the list of subgraphs of the current graph, except in the case pg is the current graph
    if (pg != currentGraph) pg :: currentSubGraphs
    //Stock the container graph and setpg pg as the new current graph
    push(pg)

    val pgName = pg.getName()

    currentMethod.append("// Method building PiGraph ")
    currentMethod.append(pgName)
    //Generating the method signature
    generateMethodSignature(pg)
    //Generating the method body
    generateMethodBody(pg)
    //Reset the container graph as the current graph
    pop()

    //We should also generate the C++ code as for any Actor
    visitAbstractActor(pg)
  }

  /**
   * Returns the name of the building method for the PiGraph pg
   */
  private def getMethodName(pg: PiGraph): String = "build" + pg.getName()

  /**
   * Concatenate the signature of the method corresponding to a PiGraph to the currentMethod StringBuilder
   */
  private def generateMethodSignature(pg: PiGraph): Unit = {
    //The method does not return anything
    currentMethod.append("void ")
    currentMethod.append(getMethodName(pg))
    //The method accept as parameter a pointer to the PiSDFGraph graph it will build and a pointer to the parent actor of graph (i.e., the hierarchical actor)
    currentMethod.append("(PiSDFGraph* graph, BaseVertex* parentVertex)")
  }

  /**
   * Concatenate the body of the method corresponding to a PiGraph to the currentMethod StringBuilder
   */
  private def generateMethodBody(pg: PiGraph): Unit = {
    currentMethod.append("{")
    //Generating parameters
    currentMethod.append("\n\t//Parameters")
    pg.getParameters().foreach(p => visit(p))
    //Generating vertices
    currentMethod.append("\n\t//Vertices")
    pg.getVertices().foreach(v => visit(v))
    //Generating edges
    currentMethod.append("\n\t//Edges")
    pg.getFifos().foreach(f => visit(f))
    //Generating call to methods generated for subgraphs, if any
    if (!currentSubGraphs.isEmpty) {
      currentMethod.append("\n\t//Subgraphs")
      generateCallsToSubgraphs()
    }
    currentMethod.append("}\n")
    visitAbstractActor(pg)
  }

  private def generateCallsToSubgraphs(): Unit = {
    //For each subgraph of the current graph
    currentSubGraphs.foreach(sg => {
      val sgName = getSubraphName(sg)
      val vxName = getVertexName(sg)
      //Generate test in order to prevent to reach the limit of graphs
      currentMethod.append("\n\tif(nb_graphs >= MAX_NB_PiSDF_SUB_GRAPHS - 1) exitWithCode(1054);")
      //Get the pointer to the subgraph
      currentMethod.append("\n\tPiSDFGraph *")
      currentMethod.append(sgName)
      currentMethod.append(" = &graphs[nb_graphs];")
      //Increment the graph counter
      currentMethod.append("\n\tnb_graphs++;")
      //Call the building method of sg with the pointer
      currentMethod.append("\n\t")
      currentMethod.append(getMethodName(sg))
      currentMethod.append("(")
      //Pass the pointer to the subgraph
      currentMethod.append(sgName)
      currentMethod.append(",")
      //Pass the parent vertex
      currentMethod.append(vxName)
      currentMethod.append(");")
      currentMethod.append("\n\t")
      //Set the subgraph as subgraph of the vertex
      currentMethod.append(vxName)
      currentMethod.append("->setSubGraph(")
      currentMethod.append(sgName)
      currentMethod.append(");")
    })
  }

  /**
   * Returns the name of the subgraph pg
   */
  private def getSubraphName(pg: PiGraph): String = pg.getName() + "_subGraph"

  /**
   * Returns the name of the variable pointing to the C++ object corresponding to AbstractActor aa
   */
  private def getVertexName(aa: AbstractActor): String = "vx" + aa.getName()

  private def visitActor(a: Actor): Unit = {
    visitAbstractActor(a)
  }

  /**
   * Generic visit method for all AbstractActors (Actors, PiGraph)
   */
  private def visitAbstractActor(aa: AbstractActor) = {
    //Stock the name of the current AbstractActor
    currentAbstractActorName = aa.getName()

    //Call the addVertex method on the current graph
    currentMethod.append("\n\tPiSDFVertex *")
    currentMethod.append(getVertexName(aa))
    currentMethod.append(" = (PiSDFVertex*)graph->addVertex(\"")
    //Pass the name of the AbstractActor
    currentMethod.append(currentAbstractActorName)
    currentMethod.append("\",")
    //Pass the type of vertex
    currentMethod.append("pisdf_vertex")
    currentMethod.append(");")

    //Visit data ports to fill the portMap
    aa.getDataInputPorts().foreach(p => visit(p))
    aa.getDataOutputPorts().foreach(p => visit(p))
    //TODO: Keep the visit of ConfigPorts?
    aa.getConfigInputPorts().foreach(p => visit(p))
    aa.getConfigOutputPorts().foreach(p => visit(p))
  }

  /**
   * When visiting data ports, we stock the necessary informations for edge generation into PortDescriptions
   */
  private def visitDataInputPort(dip: DataInputPort): Unit = {
    portMap.put(dip, new PortDescription(currentAbstractActorName, dip.getExpression().getString()))
  }
  private def visitDataOutputPort(dop: DataOutputPort): Unit = {
    portMap.put(dop, new PortDescription(currentAbstractActorName, dop.getExpression().getString()))
  }

  private def visitConfigInputPort(cip: ConfigInputPort): Unit = {
    //TODO
  }

  private def visitConfigOutputPort(cop: ConfigOutputPort): Unit = {
    //TODO
  }

  /**
   * When visiting a FIFO we should add an edge to the current graph
   */
  private def visitFifo(f: Fifo): Unit = {
    //Call the addEdge method on the current graph
    currentMethod.append("\n\t")
    currentMethod.append("graph->addEdge(")
    val src: PortDescription = portMap.get(f.getSourcePort())
    //Pass the name of the source node
    currentMethod.append(src.nodeName)
    currentMethod.append(", \"")
    //Pass the production of the source node
    currentMethod.append(src.expression)
    currentMethod.append("\", ")
    val tgt: PortDescription = portMap.get(f.getTargetPort())
    //Pass the name of the target node
    currentMethod.append(tgt.nodeName)
    currentMethod.append(", \"")
    //Pass the consumption of the target node
    currentMethod.append(tgt.expression)
    currentMethod.append("\", ")
    //Pass the delay of the FIFO
    currentMethod.append(f.getDelay().getExpression())
    currentMethod.append("\"")
    currentMethod.append(");")
  }

  private def visitInterfaceActor(ia: InterfaceActor): Unit = {
    //TODO
  }

  private def visitDataInputInterface(dii: DataInputInterface): Unit = {
    //TODO
  }

  private def visitDataOutputInterface(doi: DataOutputInterface): Unit = {
    //TODO
  }

  private def visitConfigOutputInterface(coi: ConfigOutputInterface): Unit = {
    //TODO
  }

  private def visitRefinement(r: Refinement): Unit = {
    //TODO
  }

  private def visitParameter(p: Parameter): Unit = {
    //TODO
  }

  private def visitDependency(d: Dependency): Unit = {
    //TODO
  }

  private def visitDelay(d: Delay): Unit = {
    //TODO
  }

  private def visitExpression(e: Expression): Unit = {
    //TODO
  }

  private def visitConfigInputInterface(cii: ConfigInputInterface): Unit = {
    //TODO
  }

  /**
   * Handling the graphs and methods stacks
   */
  private def push(pg: PiGraph): Unit = {
    //Test in order to ignore the most outer graph
    if (currentGraph != pg) {
      graphsStack.push(new GraphDescription(currentGraph, currentSubGraphs, currentMethod))
      currentGraph = pg
      currentSubGraphs = Nil
      currentMethod = new StringBuilder
    }
  }
  private def pop(): Unit = {
    val top = graphsStack.top
    //Test in order to ignore the most outer graph
    if (top != null) {
      currentGraph = top.pg
      currentSubGraphs = top.subGraphs
      currentMethod = top.method
      graphsStack.pop
    }
  }
}

/**
 * Class allowing to stock necessary information about graphs
 */
class GraphDescription(val pg: PiGraph, var subGraphs: List[PiGraph], var method: StringBuilder)

/**
 * Class allowing to stock necessary information about ports for the edges generation
 */
class PortDescription(val nodeName: String, val expression: String)
package org.ietr.preesm.experiment.pimm.cppgenerator.scala.visitor

import org.eclipse.emf.ecore.EObject
import org.ietr.preesm.experiment.model.pimm._
import scala.collection.immutable.Stack
//Allows to consider Java collections as Scala collections and to use foreach...
import collection.JavaConversions._

/**
 * PiMM models visitor generating C++ code for COMPA Runtime
 * currentGraph: The most outer graph of the PiMM model
 * currentMethod: The StringBuilder used to write the C++ code
 */
class CPPCodeGenerationVisitor(private var currentGraph: PiGraph, private var currentMethod: StringBuilder) {

  //Stacks and list to handle hierarchical graphs
  private val methodsStack: Stack[StringBuilder] = new Stack[StringBuilder]
  private val graphsStack: Stack[GraphDescription] = new Stack[GraphDescription]
  private var currentSubGraphs: List[PiGraph] = Nil
  
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
    pop

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
    currentMethod.append("}\n")
    visitAbstractActor(pg)
  }

  
  private def visitActor(a: Actor): Unit = {
    //TODO
  }

  private def visitAbstractActor(aa: AbstractActor) = {
    //TODO
  }

  private def visitDataInputPort(dip: DataInputPort): Unit = {
    //TODO
  }

  private def visitDataOutputPort(dop: DataOutputPort): Unit = {
    //TODO
  }

  private def visitConfigInputPort(cip: ConfigInputPort): Unit = {
    //TODO
  }

  private def visitConfigOutputPort(cop: ConfigOutputPort): Unit = {
    //TODO
  }

  private def visitFifo(f: Fifo): Unit = {
    //TODO
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
      graphsStack.push(new GraphDescription(currentGraph, currentSubGraphs))
      currentGraph = pg
      currentSubGraphs = Nil
      methodsStack.push(currentMethod)
      currentMethod = new StringBuilder
    }
  }
  private def pop(): Unit = {
    val top = graphsStack.top
    //Test in order to ignore the most outer graph
    if (top != null) {
      currentGraph = top.pg
      currentSubGraphs = top.subGraphs
      graphsStack.pop
      currentMethod = methodsStack.top
      methodsStack.pop
    }
  }
}

/**
 * Class allowing to stock necessary information about graphs
 */
class GraphDescription(val pg: PiGraph, var subGraphs: List[PiGraph])
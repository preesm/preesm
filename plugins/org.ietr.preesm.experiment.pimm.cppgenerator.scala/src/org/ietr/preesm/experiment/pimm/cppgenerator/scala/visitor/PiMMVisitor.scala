package org.ietr.preesm.experiment.pimm.cppgenerator.scala.visitor

import org.eclipse.emf.ecore.EObject
import org.ietr.preesm.experiment.model.pimm._

/**
 * Trait for visiting PiMM models
 */
trait PiMMVisitor {
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

  def visitPiGraph(pg: PiGraph): Unit

  def visitActor(a: Actor): Unit

  def visitAbstractActor(aa: AbstractActor): Unit

  def visitDataInputPort(dip: DataInputPort): Unit

  def visitDataOutputPort(dop: DataOutputPort): Unit

  def visitConfigInputPort(cip: ConfigInputPort): Unit

  def visitConfigOutputPort(cop: ConfigOutputPort): Unit

  def visitFifo(f: Fifo): Unit

   def visitInterfaceActor(ia: InterfaceActor): Unit

   def visitDataInputInterface(dii: DataInputInterface): Unit

   def visitDataOutputInterface(doi: DataOutputInterface): Unit

   def visitConfigOutputInterface(coi: ConfigOutputInterface): Unit

   def visitRefinement(r: Refinement): Unit

   def visitParameter(p: Parameter): Unit

   def visitDependency(d: Dependency): Unit

   def visitDelay(d: Delay): Unit

   def visitExpression(e: Expression): Unit

   def visitConfigInputInterface(cii: ConfigInputInterface): Unit
}
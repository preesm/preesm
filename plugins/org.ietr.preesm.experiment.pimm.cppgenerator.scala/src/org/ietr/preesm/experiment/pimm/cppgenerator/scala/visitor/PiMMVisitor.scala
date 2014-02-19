/*******************************************************************************
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
 ******************************************************************************/
package org.ietr.preesm.experiment.pimm.cppgenerator.scala.visitor

import org.eclipse.emf.ecore.EObject
import org.ietr.preesm.experiment.model.pimm._

/**
 * Trait for visiting PiMM models
 * Should be moved to a more generic project (org.ietr.preesm.experiment.pimm.visitor.scala) if reused in other projects.
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
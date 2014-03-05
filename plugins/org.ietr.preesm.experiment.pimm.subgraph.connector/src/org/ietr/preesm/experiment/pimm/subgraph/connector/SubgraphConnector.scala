package org.ietr.preesm.experiment.pimm.subgraph.connector

import org.ietr.preesm.experiment.pimm.visitor.scala.PiMMVisitor
import org.ietr.preesm.experiment.model.pimm._
import collection.JavaConversions._
import java.util.Map
import java.util.HashMap
import java.util.List
import java.util.ArrayList

class SubgraphConnector extends PiMMVisitor {

  private val factory = PiMMFactory.eINSTANCE

  //Actor in the outer graph corresponding to the currently visited graph
  private var currentActor: AbstractActor = null
  
  private var graphReplacements: Map[PiGraph, List[ActorByGraphReplacement]] = new HashMap[PiGraph, List[ActorByGraphReplacement]]()
  def getGraphReplacements() = graphReplacements

  private var currentGraph: PiGraph = null
  
  def visitPiGraph(pg: PiGraph): Unit = {
    val oldGraph = currentGraph
    currentGraph = pg
    pg.getVertices().foreach(v => visit(v))
    pg.getParameters().foreach(p => visit(p))
    currentGraph = oldGraph
  }

  def visitActor(a: Actor): Unit = {
    //If the refinement of the Actor a points to the description of PiGraph, visit it to connect the subgraph to its supergraph
    val aa: AbstractActor = a.getRefinement().getAbstractActor()
    if (aa != null && aa.isInstanceOf[PiGraph]) {
      val innerGraph = aa.asInstanceOf[PiGraph]
      //Connect all Fifos and Dependencies incoming into a and outgoing from a in order to make them incoming into innerGraph and outgoing from innerGraph instead
      reconnectPiGraph(a, innerGraph)

      currentActor = innerGraph
      visit(innerGraph)
      
      val replacement = new ActorByGraphReplacement(a, innerGraph)
      if (!graphReplacements.containsKey(currentGraph)) {
        graphReplacements.put(currentGraph, new ArrayList[ActorByGraphReplacement]())
      }
      graphReplacements.get(currentGraph).add(replacement)
    }
  }

  /*
   * Connect all the ports of the PiGraph to the Fifos and Dependencies connected to the ports of the Actor
   */
  private def reconnectPiGraph(a: Actor, pg: PiGraph) {
    a.getDataInputPorts().foreach(dip1 => {
      pg.getDataInputPorts().foreach(dip2 => {
        if (dip1.getName() == dip2.getName()) {
          val fifo = dip1.getIncomingFifo()
          dip2.setIncomingFifo(fifo)
          fifo.setTargetPort(dip2)
        }
      })
    })
    a.getDataOutputPorts().foreach(dop1 => {
      pg.getDataOutputPorts().foreach(dop2 => {
        if (dop1.getName() == dop2.getName()) {
          val fifo = dop1.getOutgoingFifo()
          dop2.setOutgoingFifo(fifo)
          fifo.setSourcePort(dop2)
        }
      })
    })
    a.getConfigInputPorts().foreach(cip1 => {
      pg.getConfigInputPorts().foreach(cip2 => {
        if (cip1.getName() == cip2.getName()) {
          val dep = cip1.getIncomingDependency()
          cip2.setIncomingDependency(dep)
          dep.setGetter(cip2)
        }
      })
    })
    a.getConfigOutputPorts().foreach(cop1 => {
      pg.getConfigOutputPorts().foreach(cop2 => {
        if (cop1.getName() == cop2.getName()) {
          cop1.getOutgoingDependencies().foreach(dep => {
            cop2.getOutgoingDependencies().add(dep)
            dep.setSetter(cop2)
          })
        }
      })
    })
  }

  def visitAbstractActor(aa: AbstractActor): Unit = throw new UnsupportedOperationException()

  def visitDataInputPort(dip: DataInputPort): Unit = throw new UnsupportedOperationException()

  def visitDataOutputPort(dop: DataOutputPort): Unit = throw new UnsupportedOperationException()

  def visitConfigInputPort(cip: ConfigInputPort): Unit = throw new UnsupportedOperationException()

  def visitConfigOutputPort(cop: ConfigOutputPort): Unit = throw new UnsupportedOperationException()

  def visitFifo(f: Fifo): Unit = throw new UnsupportedOperationException()

  def visitInterfaceActor(ia: InterfaceActor): Unit = throw new UnsupportedOperationException()

  def visitDataInputInterface(dii: DataInputInterface): Unit = {
    //Connect the interface to the incoming fifo from the outer graph
    val correspondingPort: Option[DataInputPort] = currentActor.getDataInputPorts().find(dip => dip.getName() == dii.getName())
    correspondingPort match {
      case Some(p) => {
        dii.setGraphPort(p)
//        //We create a new Fifo
//        val fifo = factory.createFifo()
//        //Which source is the source of the incoming fifo from the outer graph
//        fifo.setSourcePort(p.getIncomingFifo().getSourcePort())
//        //And which target is an input port of the interface
//        val inputPort = factory.createDataInputPort()
//        inputPort.setName("dataIn_" + dii.getName())
//        dii.getDataInputPorts().add(inputPort)
//        fifo.setTargetPort(inputPort)

      }
      case None =>
    }
  }

  def visitDataOutputInterface(doi: DataOutputInterface): Unit = {
    //Connect the interface to the outgoing fifo to the outer graph
    val correspondingPort: Option[DataOutputPort] = currentActor.getDataOutputPorts().find(dop => dop.getName() == doi.getName())
    correspondingPort match {
      case Some(p) => {
        doi.setGraphPort(p)
//        //We create a new Fifo
//        val fifo = factory.createFifo()
//        //Which source is an output port of the interface
//        val outputPort = factory.createDataOutputPort()
//        outputPort.setName("dataOut_" + doi.getName())
//        fifo.setSourcePort(outputPort)
//        doi.getDataOutputPorts().add(outputPort)
//        //And which target is the target of the outgoing fifo to the outer graph
//        fifo.setTargetPort(p.getOutgoingFifo().getTargetPort())
      }
      case None =>
    }
  }

  def visitConfigOutputInterface(coi: ConfigOutputInterface): Unit = {
    //Connect the interface to the outgoing dependencies to the outer graph
    val correspondingPort: Option[ConfigOutputPort] = currentActor.getConfigOutputPorts().find(cop => cop.getName() == coi.getName())
    correspondingPort match {
      case Some(p) => {
        coi.setGraphPort(p)
//        val outputPort = factory.createConfigOutputPort()
//        outputPort.setName("cfgOut_" + coi.getName())
//        coi.getConfigOutputPorts().add(outputPort)
//        //For each dependency
//        p.getOutgoingDependencies().foreach(d => {
//          //We create a new Dependency
//          val dep = factory.createDependency()
//          //Which source is an output port of the interface          
//          dep.setSetter(coi.getConfigOutputPorts().get(0))
//          //And which target is the target of the outgoing dependency
//          dep.setGetter(d.getGetter())
//        })
      }
      case None =>
    }
  }

  def visitConfigInputInterface(cii: ConfigInputInterface): Unit = {
    //Connect the interface to the incoming dependencies from the outer graph
    val correspondingPort: Option[ConfigInputPort] = currentActor.getConfigInputPorts().find(cip => cip.getName() == cii.getName())
    correspondingPort match {
      case Some(p) => {
        cii.setGraphPort(p)
//        //We create a new Dependency
//        val dep = factory.createDependency()
//        //Which source (setter) is the source of the incoming dependecy
//        dep.setSetter(p.getIncomingDependency().getSetter())
//        //And which target (getter) is an input port of the interface
//        val inputPort = factory.createConfigInputPort()
//        inputPort.setName("cfgIn_" + cii.getName())
//        cii.getConfigInputPorts().add(inputPort)
//        dep.setGetter(inputPort)
      }
      case None =>
    }
  }

  def visitParameter(p: Parameter): Unit = {
    //Only ConfigInputInterfaces need a specific process
    //DO NOTHING
  }

  def visitRefinement(r: Refinement): Unit = throw new UnsupportedOperationException()

  def visitDependency(d: Dependency): Unit = throw new UnsupportedOperationException()

  def visitDelay(d: Delay): Unit = throw new UnsupportedOperationException()

  def visitExpression(e: Expression): Unit = throw new UnsupportedOperationException()

}

class ActorByGraphReplacement(val toBeRemoved: Actor, val toBeAdded: PiGraph)
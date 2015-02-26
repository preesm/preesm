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
package org.ietr.preesm.pimm.algorithm.cppgenerator.visitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.impl.FunctionParameterImpl;
import org.ietr.preesm.experiment.model.pimm.impl.FunctionPrototypeImpl;
import org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

public class CppPreProcessVisitor extends PiMMVisitor {
	private AbstractActor currentAbstractActor = null;
	private String currentAbstractVertexName = "";

	private Map<Port, Integer> portMap = new HashMap<Port, Integer>();
	private Map<ISetter, String> setterMap = new HashMap<ISetter, String>();
	// Map from Actor names to pairs of CoreType numbers and Timing expressions
	private Map<String, AbstractActor> actorNames = new HashMap<String, AbstractActor>();
	private Map<AbstractActor, Integer> functionMap = new LinkedHashMap<AbstractActor, Integer>();;

	private Map<AbstractActor, Integer> dataInPortIndices = new HashMap<AbstractActor, Integer>();
	private Map<AbstractActor, Integer> dataOutPortIndices = new HashMap<AbstractActor, Integer>();
	private Map<AbstractActor, Integer> cfgInPortIndices = new HashMap<AbstractActor, Integer>();
	private Map<AbstractActor, Integer> cfgOutPortIndices = new HashMap<AbstractActor, Integer>();
	
	// Variables containing the name of the currently visited AbstractActor for
	// PortDescriptions
	// Map linking data ports to their corresponding description
	
	public Map<Port, Integer> getPortMap() {
		return portMap;
	}

	public Map<String, AbstractActor> getActorNames() {
		return actorNames;
	}
	
	public Map<AbstractActor, Integer> getFunctionMap() {
		return functionMap;
	}

	@Override
	public void visitPiGraph(PiGraph pg) {
		visitAbstractActor(pg);
		for (AbstractActor a : pg.getVertices()) {
			a.accept(this);
		}
		for (Parameter p : pg.getParameters()) {
			p.accept(this);
		}
	}

	@Override
	public void visitAbstractActor(AbstractActor aa) {
		// Fix currentAbstractActor
		currentAbstractActor = aa;
		// Fix currentAbstractVertexName
		currentAbstractVertexName = "vx" + aa.getName();
		dataInPortIndices.put(aa, 0);
		dataOutPortIndices.put(aa, 0);
		cfgInPortIndices.put(aa, 0);
		cfgOutPortIndices.put(aa, 0);
						
		// Visit configuration input ports to fill cfgInPortMap
		visitAbstractVertex(aa);
		// Visit data ports to fill the dataPortMap
		for (DataInputPort p : aa.getDataInputPorts()) {
			p.accept(this);
		}
		for (DataOutputPort p : aa.getDataOutputPorts()) {
			p.accept(this);
		}
		// Visit configuration output ports to fill the setterMap
		for (ConfigOutputPort p : aa.getConfigOutputPorts()) {
			p.accept(this);
		}
	}

	@Override
	public void visitAbstractVertex(AbstractVertex av) {
		// Visit configuration input ports to fill cfgInPortMap
		for (ConfigInputPort p : av.getConfigInputPorts()) {
			p.accept(this);
		}
	}

	@Override
	public void visitActor(Actor a) {		
		// Register associated function
		if(!(a instanceof PiGraph)){
			functionMap.put(a, functionMap.size());
			if(!(a.getRefinement() instanceof HRefinement))
				WorkflowLogger.getLogger().warning("Actor "+a.getName()+" doesn't have correct refinement.");
		}
		
		actorNames.put(a.getName(), a);
				
		visitAbstractActor(a);
	}

	@Override
	public void visitConfigInputPort(ConfigInputPort cip) {
		int index = cfgInPortIndices.get(currentAbstractActor);
		cfgInPortIndices.put(currentAbstractActor, index+1);
		portMap.put(cip, index);		
	}

	@Override
	public void visitConfigOutputPort(ConfigOutputPort cop) {
		int index = cfgOutPortIndices.get(currentAbstractActor);
		cfgOutPortIndices.put(currentAbstractActor, index+1);
		portMap.put(cop, index);		
	}

	/**
	 * When visiting data ports, we stock the necessary informations for edge
	 * generation into PortDescriptions
	 */
	@Override
	public void visitDataInputPort(DataInputPort dip) {
		// XXX: setParentEdge workaround (see visitDataInputInterface and
		// visitDataOutputInterface in CPPCodeGenerationVisitor)
		// XXX Ugly way to do this. Must suppose that fifos are always obtained
		// in the same order => Modify the C++ headers?
		// Get the position of the incoming fifo of dip wrt.
		// currentAbstractActor
		int index = dataInPortIndices.get(currentAbstractActor);
		dataInPortIndices.put(currentAbstractActor, index+1);

		// Fill dataPortMap
		portMap.put(dip, index);
	}

	@Override
	public void visitDataOutputPort(DataOutputPort dop) {
		// XXX: setParentEdge workaround (see visitDataInputInterface and
		// visitDataOutputInterface in CPPCodeGenerationVisitor)
		// XXX Ugly way to do this. Must suppose that fifos are always obtained
		// in the same order => Modify the C++ headers?
		// Get the position of the outgoing fifo of dop wrt.
		// currentAbstractActor
		int index = dataOutPortIndices.get(currentAbstractActor);
		dataOutPortIndices.put(currentAbstractActor, index+1);

		// Fill dataPortMap
		portMap.put(dop, index);
	}

	@Override
	public void visitParameter(Parameter p) {
		// Fix currentAbstractVertexName
		currentAbstractVertexName = "param_" + p.getName();
		// Visit configuration input ports to fill cfgInPortMap
		visitAbstractVertex(p);
		// Fill the setterMap
		setterMap.put(p, currentAbstractVertexName);
	}

	@Override
	public void visitDependency(Dependency d) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitConfigOutputInterface(ConfigOutputInterface coi) {
		visitInterfaceActor(coi);
	}

	@Override
	public void visitDataInputInterface(DataInputInterface dii) {
		visitInterfaceActor(dii);
	}

	@Override
	public void visitDataOutputInterface(DataOutputInterface doi) {
		visitInterfaceActor(doi);
	}

	@Override
	public void visitInterfaceActor(InterfaceActor ia) {
		visitAbstractActor(ia);
	}

	@Override
	public void visitConfigInputInterface(ConfigInputInterface cii) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDelay(Delay d) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitExpression(Expression e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitFifo(Fifo f) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitISetter(ISetter is) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitParameterizable(Parameterizable p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitPort(Port p) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void visitDataPort(DataPort p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitRefinement(Refinement r) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitFunctionParameter(
			FunctionParameterImpl functionParameterImpl) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitFunctionPrototype(
			FunctionPrototypeImpl functionPrototypeImpl) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitHRefinement(HRefinementImpl hRefinementImpl) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitBroadcastActor(BroadcastActor ba) {
		visitAbstractActor(ba);
//		throw new UnsupportedOperationException();
	}

	@Override
	public void visitJoinActor(JoinActor ja) {
		visitAbstractActor(ja);
//		throw new UnsupportedOperationException();
	}

	@Override
	public void visitForkActor(ForkActor fa) {
		visitAbstractActor(fa);
//		throw new UnsupportedOperationException();
	}

	@Override
	public void visitRoundBufferActor(RoundBufferActor rba) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void visitExecutableActor(ExecutableActor ea) {
		throw new UnsupportedOperationException();
	}
}

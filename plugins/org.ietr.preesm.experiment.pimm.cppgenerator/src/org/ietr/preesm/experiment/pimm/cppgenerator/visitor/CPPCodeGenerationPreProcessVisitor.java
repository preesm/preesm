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
package org.ietr.preesm.experiment.pimm.cppgenerator.visitor;

import java.util.HashMap;
import java.util.Map;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;
import org.ietr.preesm.experiment.pimm.cppgenerator.utils.CPPNameGenerator;
import org.ietr.preesm.experiment.pimm.cppgenerator.utils.EdgeKind;

public class CPPCodeGenerationPreProcessVisitor extends PiMMVisitor {

	CPPNameGenerator nameGen = new CPPNameGenerator();

	private AbstractActor currentAbstractActor = null;
	// Variables containing the name of the currently visited AbstractActor for
	// PortDescriptions
	private String currentAbstractVertexName = "";
	// Map linking data ports to their corresponding description
	private Map<Port, DataPortDescription> dataPortMap = new HashMap<Port, DataPortDescription>();

	public Map<Port, DataPortDescription> getDataPortMap() {
		return dataPortMap;
	}

	// Map linking configuration input ports to the name of their node
	private Map<ConfigInputPort, String> cfgInPortMap = new HashMap<ConfigInputPort, String>();

	// Map linking ISetters (Parameter and ConfigOutputPort) to the name of
	// their node or their name
	private Map<ISetter, String> setterMap = new HashMap<ISetter, String>();

	// Map linking Fifos to their C++ names
	private Map<Fifo, Integer> fifoMap = new HashMap<Fifo, Integer>();

	public Map<Fifo, Integer> getFifoMap() {
		return fifoMap;
	}

	// Map linking dependencies to their corresponding description
	private Map<Dependency, DependencyDescription> dependencyMap = new HashMap<Dependency, DependencyDescription>();

	public Map<Dependency, DependencyDescription> getDependencyMap() {
		return dependencyMap;
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
		for (Dependency d : pg.getDependencies()) {
			d.accept(this);
		}
	}

	@Override
	public void visitAbstractActor(AbstractActor aa) {
		// Fix currentAbstractActor
		currentAbstractActor = aa;
		// Fix currentAbstractVertexName
		currentAbstractVertexName = nameGen.getVertexName(aa);
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
		visitAbstractActor(a);
	}

	@Override
	public void visitConfigInputPort(ConfigInputPort cip) {
		// Fill cfgInPortMap
		cfgInPortMap.put(cip, currentAbstractVertexName);
	}

	@Override
	public void visitConfigOutputPort(ConfigOutputPort cop) {
		// Fill setterMap
		setterMap.put(cop, currentAbstractVertexName);
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
		Fifo f = dip.getIncomingFifo();
		fifoMap.put(f,
				nameGen.getEdgeNumber(currentAbstractActor, f, EdgeKind.in));

		// Fill dataPortMap
		dataPortMap.put(dip, new DataPortDescription(currentAbstractVertexName,
				dip.getExpression().getString()));
	}

	@Override
	public void visitDataOutputPort(DataOutputPort dop) {
		// XXX: setParentEdge workaround (see visitDataInputInterface and
		// visitDataOutputInterface in CPPCodeGenerationVisitor)
		// XXX Ugly way to do this. Must suppose that fifos are always obtained
		// in the same order => Modify the C++ headers?
		// Get the position of the outgoing fifo of dop wrt.
		// currentAbstractActor
		Fifo f = dop.getOutgoingFifo();
		fifoMap.put(f,
				nameGen.getEdgeNumber(currentAbstractActor, f, EdgeKind.out));

		// Fill dataPortMap
		dataPortMap.put(dop, new DataPortDescription(currentAbstractVertexName,
				dop.getExpression().getString()));
	}

	@Override
	public void visitDependency(Dependency d) {
		// Fill the dependencyMap with the names of source and target of d
		String srcName = setterMap.get(d.getSetter());
		String tgtName = cfgInPortMap.get(d.getGetter());
		dependencyMap.put(d, new DependencyDescription(srcName, tgtName));
	}

	@Override
	public void visitParameter(Parameter p) {
		// Fix currentAbstractVertexName
		currentAbstractVertexName = nameGen.getParameterName(p);
		// Visit configuration input ports to fill cfgInPortMap
		visitAbstractVertex(p);
		// Fill the setterMap
		setterMap.put(p, nameGen.getParameterName(p));
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
	public void visitRefinement(Refinement r) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Class allowing to stock necessary information about data ports for the
	 * edges generation
	 */
	public class DataPortDescription {
		public String nodeName;
		public String expression;

		public DataPortDescription(String nodeName, String expression) {
			this.nodeName = nodeName;
			this.expression = expression;
		}
	}

	/**
	 * Class allowing to stock necessary information about dependencies for
	 * parameter connections
	 */
	public class DependencyDescription {
		String srcName;
		String tgtName;

		public DependencyDescription(String srcName, String tgtName) {
			this.srcName = srcName;
			this.tgtName = tgtName;
		}
	}
}

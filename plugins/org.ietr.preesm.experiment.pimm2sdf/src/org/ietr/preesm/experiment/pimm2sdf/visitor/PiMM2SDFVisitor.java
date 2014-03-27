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
package org.ietr.preesm.experiment.pimm2sdf.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ietr.dftools.algorithm.exporter.GMLSDFExporter;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.parameters.ExpressionValue;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFExpressionEdgePropertyType;
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

/**
 * This class visits a PiGraph and generates a corresponding SDFGraph
 * 
 * @author cguy
 * 
 */
public class PiMM2SDFVisitor extends PiMMVisitor {

	private SDFGraph result;

	// Map from original PiMM vertices to generated SDF vertices
	private Map<AbstractVertex, SDFAbstractVertex> piVx2SDFVx = new HashMap<AbstractVertex, SDFAbstractVertex>();
	// Map from PiMM ports to their vertex (used for SDFEdge creation)
	private Map<Port, Parameterizable> piPort2Vx = new HashMap<Port, Parameterizable>();
	// Fixed values for parameters of the graph
	private Map<Parameter, List<Integer>> parameterValues;
	// Set of subgraphs to visit afterwards
	private Set<PiGraph> subgraphs = new HashSet<PiGraph>();
	// Path to the generation folder, in which we will generate the .graphml
	// files
	private String generationPath;

	public void initialize(Map<Parameter, List<Integer>> parameterValues,
			String generationPath) {
		this.parameterValues = parameterValues;
		this.generationPath = generationPath;
	}

	@Override
	public void visitPiGraph(PiGraph pg) {
		// If result == null, then pg is the first PiGraph we encounter
		if (result == null) {
			result = new SDFGraph();
			result.setName(pg.getName());

			for (AbstractActor aa : pg.getVertices()) {
				aa.accept(this);
			}

			for (Fifo f : pg.getFifos()) {
				f.accept(this);
			}

			// For each subgraph of pg encountered during the visit, duplicate
			// the
			// subgraph by nbRepeat, changin the value of parameters each time
			for (PiGraph subgraph : subgraphs) {
				int nbCopies = 0;
				try {
					nbCopies = piVx2SDFVx.get(subgraph).getNbRepeatAsInteger();

				} catch (InvalidExpressionException e) {
					e.printStackTrace();
				}
				createSubgraphCopies(subgraph, nbCopies);
			}
		}
		// Otherwise, we need to visit separately pg later
		else {
			SDFVertex v = new SDFVertex();
			v.setName(pg.getName());

			piVx2SDFVx.put(pg, v);
			subgraphs.add(pg);
		}
	}

	private void createSubgraphCopies(PiGraph subgraph, int nbCopies) {
		for (int i = 0; i < nbCopies; i++) {
			// We take the original map (parameterValues) in which we will
			// overwrite values of parameters directly entering into subgraph
			Map<Parameter, List<Integer>> innerParameterValues = parameterValues;

			// Name of the vertex to be created
			String name = subgraph.getName() + "_";

			// For each incoming parameter of subgraph
			for (ConfigInputPort cip : subgraph.getConfigInputPorts()) {
				ISetter setter = cip.getIncomingDependency().getSetter();
				if (setter instanceof Parameter) {
					// Get one value which will be fixed for one of the
					// instances of subgraph
					List<Integer> setterValue = new ArrayList<Integer>();
					List<Integer> possibleValues = parameterValues.get(setter);
					int value = possibleValues.get(i % possibleValues.size());
					setterValue.add(value);
					name += ((Parameter) setter).getName() + value;

					innerParameterValues.put((Parameter) setter, setterValue);
				}
			}

			SDFGraph generatedSubgraph = createSubraphCopyWithValues(subgraph, name, innerParameterValues);
			reconnectSubgraph(piVx2SDFVx.get(subgraph), generatedSubgraph, nbCopies);
		}
	}

	private void reconnectSubgraph(SDFAbstractVertex originalVertex,
			SDFGraph newVertex, int nbCopies) {		
		for (SDFEdge edge : result.edgesOf(originalVertex)) {
			
		}
		
		for (SDFInterfaceVertex port : originalVertex.getSinks()) {
			SDFSinkInterfaceVertex inputPort = (SDFSinkInterfaceVertex) port;
			SDFSinkInterfaceVertex newInputPort = new SDFSinkInterfaceVertex();
			newInputPort.setName(inputPort.getName());
			newInputPort.setDirection(inputPort.getDirection());
			
		}
		for (SDFInterfaceVertex port : originalVertex.getSources()) {
			SDFSourceInterfaceVertex outputPort = (SDFSourceInterfaceVertex) port;
		}
	}

	private SDFGraph createSubraphCopyWithValues(PiGraph subgraph, String name,
			Map<Parameter, List<Integer>> innerParameterValues) {
		SDFVertex v = new SDFVertex();
		v.setName(name);

		PiMM2SDFVisitor innerVisitor = new PiMM2SDFVisitor();
		innerVisitor.initialize(innerParameterValues, generationPath);
		innerVisitor.visit(subgraph);

		SDFGraph generatedSubgraph = innerVisitor.getResult();

		// When the creation of the SDFGraph is finished, export it
		GMLSDFExporter exporter = new GMLSDFExporter();
		exporter.export(generatedSubgraph,
				generationPath + generatedSubgraph.getName() + ".graphml");
		// TODO: add the file as refinement of the vertex
		
		return generatedSubgraph;
	}

	@Override
	public void visitAbstractActor(AbstractActor aa) {
		for (DataInputPort dip : aa.getDataInputPorts()) {
			piPort2Vx.put(dip, aa);
			dip.accept(this);
		}
		for (DataOutputPort dop : aa.getDataOutputPorts()) {
			piPort2Vx.put(dop, aa);
			dop.accept(this);
		}
		for (ConfigOutputPort cop : aa.getConfigOutputPorts()) {
			piPort2Vx.put(cop, aa);
			cop.accept(this);
		}
		visitAbstractVertex(aa);
	}

	@Override
	public void visitAbstractVertex(AbstractVertex av) {
		visitParameterizable(av);
	}

	@Override
	public void visitActor(Actor a) {
		SDFVertex v = new SDFVertex();
		v.setName(a.getName());

		visitAbstractActor(a);

		piVx2SDFVx.put(a, v);
	}

	@Override
	public void visitConfigInputInterface(ConfigInputInterface cii) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitConfigInputPort(ConfigInputPort cip) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitConfigOutputInterface(ConfigOutputInterface coi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitConfigOutputPort(ConfigOutputPort cop) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitDataInputInterface(DataInputInterface dii) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitDataInputPort(DataInputPort dip) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitDataOutputInterface(DataOutputInterface doi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitDataOutputPort(DataOutputPort dop) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitDelay(Delay d) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitDependency(Dependency d) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitExpression(Expression e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitFifo(Fifo f) {
		Parameterizable source = piPort2Vx.get(f.getSourcePort());
		Parameterizable target = piPort2Vx.get(f.getTargetPort());

		if (source instanceof AbstractVertex
				&& target instanceof AbstractVertex) {
			AbstractEdgePropertyType<ExpressionValue> delay = new SDFExpressionEdgePropertyType(
					new ExpressionValue(f.getDelay().getExpression()
							.getString()));
			AbstractEdgePropertyType<ExpressionValue> prod = new SDFExpressionEdgePropertyType(
					new ExpressionValue(f.getTargetPort().getExpression()
							.getString()));
			AbstractEdgePropertyType<ExpressionValue> cons = new SDFExpressionEdgePropertyType(
					new ExpressionValue(f.getSourcePort().getExpression()
							.getString()));

			result.addEdge(piVx2SDFVx.get(source), piVx2SDFVx.get(target),
					prod, cons, delay);
		}

	}

	@Override
	public void visitInterfaceActor(InterfaceActor ia) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitISetter(ISetter is) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitParameter(Parameter p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitParameterizable(Parameterizable p) {
		for (ConfigInputPort cip : p.getConfigInputPorts()) {
			piPort2Vx.put(cip, p);
			cip.accept(this);
		}
	}

	@Override
	public void visitPort(Port p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitRefinement(Refinement r) {
		// TODO Auto-generated method stub

	}

	public SDFGraph getResult() {
		return result;
	}

}

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
import java.util.Vector;

import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.CodeRefinement;
import org.ietr.dftools.algorithm.model.IRefinement;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.ExpressionValue;
import org.ietr.dftools.algorithm.model.parameters.Variable;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFExpressionEdgePropertyType;
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
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
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;
import org.ietr.preesm.experiment.pimm2sdf.PiGraphExecution;

/**
 * This class visits a PiGraph with one value for each of the Parameters and
 * generates one SDFGraph
 * 
 * @author cguy
 * 
 */
public class PiMM2SDFVisitor extends PiMMVisitor {
	// SDFGraph created from the outer graph
	private SDFGraph result;
	// Original list of fixed values for all the parameters of the graph
	private PiGraphExecution execution;

	// Map from original PiMM vertices to generated SDF vertices
	private Map<AbstractVertex, SDFAbstractVertex> piVx2SDFVx = new HashMap<AbstractVertex, SDFAbstractVertex>();
	// Map from PiMM ports to their vertex (used for SDFEdge creation)
	private Map<Port, Parameterizable> piPort2Vx = new HashMap<Port, Parameterizable>();
	// Set of subgraphs to visit afterwards
	private Set<PiGraph> subgraphs = new HashSet<PiGraph>();

	// Factory for creation of new Pi Expressions
	private PiMMFactory piFactory = PiMMFactory.eINSTANCE;

	public PiMM2SDFVisitor(PiGraphExecution execution) {
		this.execution = execution;
	}

	/**
	 * Entry point of the visitor
	 */
	@Override
	public void visitPiGraph(PiGraph pg) {
		// If result == null, then pg is the first PiGraph we encounter
		if (result == null) {
			result = new SDFGraph();
			result.setName(pg.getName());

			// Set these values into the parameters of pg when possible
			for (Parameter p : pg.getParameters()) {
				p.accept(this);
			}
			computeDerivedParameterValues(pg, execution);

			// Visit each of the vertices of pg with the values set
			for (AbstractActor aa : pg.getVertices()) {
				aa.accept(this);
			}
			// And each of the data edges of pg with the values set
			for (Fifo f : pg.getFifos()) {
				f.accept(this);
			}

			// Pass the currentSDFGraph in Single Rate which will result in
			// duplicating the SDFAbstractVertices when needed
			ToHSDFVisitor toHsdf = new ToHSDFVisitor();
			// the HSDF visitor will duplicates SDFAbstractVertices
			// corresponding to subgraphs and we will just have to visit
			// them afterwards with the good parameter values
			try {
				result.accept(toHsdf);
			} catch (SDF4JException e) {
				// TODO: handle the exception in order to stop the execution and
				// inform the user
				e.printStackTrace();
			}
			if (toHsdf.hasChanged())
				result = toHsdf.getOutput();

			// Then visit the subgraphs of pg once for each duplicates of
			// their corresponding SDFAbstractVertex created by single rate
			// transformation
			visitDuplicatesOfSubgraphs(toHsdf.getMatchCopies(), execution);
		}

		// Otherwise (if pg is not the first PiGraph we encounter during this
		// visit), we need to visit separately pg later
		else {
			SDFVertex v = new SDFVertex();
			v.setName(pg.getName());

			visitAbstractActor(pg);

			result.addVertex(v);
			piVx2SDFVx.put(pg, v);

			subgraphs.add(pg);
		}
	}

	/**
	 * Set the value of parameters of a PiGraph when possible (i.e., if we have
	 * currently only one available value, or if we can compute the value)
	 * 
	 * @param graph
	 *            the PiGraph in which we want to set the values of parameters
	 * @param execution
	 *            the list of available values for each parameter
	 */
	private void computeDerivedParameterValues(PiGraph graph,
			PiGraphExecution execution) {
		// If there is no list of value for one Parameter, the value of the
		// parameter is derived (i.e., computed from other parameters values),
		// we can evaluate it (after the values of other parameters have been
		// fixed)
		for (Parameter p : graph.getParameters()) {
			if (!execution.hasValue(p)) {
				// Evaluate the expression wrt. the current values of the
				// parameters and set the result as new expression
				Expression pExp = piFactory.createExpression();
				pExp.setString(p.getExpression().evaluate());
				p.setExpression(pExp);
			}
		}
	}

	@Override
	public void visitParameter(Parameter p) {
		// If there is only one value available for Parameter p, we can set it
		Integer value = execution.getUniqueValue(p);
		if (value != null) {
			Expression pExp = piFactory.createExpression();
			pExp.setString(value.toString());
			p.setExpression(pExp);
			
			Variable v = new Variable(p.getName(), value.toString());
			result.getVariables().addVariable(v);
		}
	}

	/**
	 * Visit each subgraph of the currently visited PiGraph once for each
	 * duplicates obtained through single rate transformation
	 * 
	 * @param verticesToDuplicates
	 *            Map from the vertices of the currentSDFGraph before and its
	 *            vertices after the single rate transformation
	 * @param execution
	 *            Values for the parameters of the currently visited PiGraph and
	 *            its inner graphs
	 */
	private void visitDuplicatesOfSubgraphs(
			Map<SDFAbstractVertex, Vector<SDFAbstractVertex>> verticesToDuplicates,
			PiGraphExecution execution) {

		// For each subgraph, visit it once for each duplicates of its
		// corresponding SDFAbstractVertex, changing the value of parameters
		// each time, and associates the result of the visits to the duplicates
		for (PiGraph subgraph : subgraphs) {
			// Get all the duplicates of the SDFAbstractVertex for subgraph
			List<SDFAbstractVertex> duplicates;
			if (verticesToDuplicates != null) {
				duplicates = verticesToDuplicates.get(piVx2SDFVx.get(subgraph));
			}
			// If verticesToDuplicate is null, the graph was already in
			// single-rate, there is no duplicates for the initially generated
			// SDFAbstractVertex, use it directly
			else {
				duplicates = new ArrayList<SDFAbstractVertex>();
				duplicates.add(piVx2SDFVx.get(subgraph));
			}

			int duplicateIndex = 0;
			// For each of the duplicates
			for (SDFAbstractVertex duplicate : duplicates) {
				int selector = duplicateIndex + duplicates.size()
						* execution.getExecutionNumber();
				// Obtain a new PiGraphExecution fixing values for Parameters
				// directly contained by subgraph
				PiGraphExecution innerExecution = execution
						.extractInnerExecution(subgraph, selector);
				// Visit subgraph with the PiGraphExecution
				PiMM2SDFVisitor innerVisitor = new PiMM2SDFVisitor(
						innerExecution);
				innerVisitor.visit(subgraph);
				// Set the obtained SDFGraph as refinement for duplicate
				SDFGraph sdf = innerVisitor.getResult();
				sdf.setName(sdf.getName() + innerExecution.getExecutionLabel());
				duplicate.setGraphDescription(sdf);
				duplicateIndex++;
			}
		}
	}

	@Override
	public void visitAbstractActor(AbstractActor aa) {
		for (DataInputPort dip : aa.getDataInputPorts()) {
			piPort2Vx.put(dip, aa);
		}
		for (DataOutputPort dop : aa.getDataOutputPorts()) {
			piPort2Vx.put(dop, aa);
		}
		for (ConfigOutputPort cop : aa.getConfigOutputPorts()) {
			piPort2Vx.put(cop, aa);
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
		v.setInfo(a.getPath());
		Refinement piRef = a.getRefinement();
		IRefinement desc = new CodeRefinement(piRef.getFilePath());
		v.setRefinement(desc);

		for (ConfigInputPort p : a.getConfigInputPorts()) {
			ISetter setter = p.getIncomingDependency().getSetter();
			if (setter instanceof Parameter) {
				Parameter param = (Parameter) setter;
				Argument arg = new Argument(param.getName());
				arg.setValue(param.getName());
				v.getArguments().addArgument(arg);
			}
		}
		
		visitAbstractActor(a);

		result.addVertex(v);
		piVx2SDFVx.put(a, v);
	}

	@Override
	public void visitFifo(Fifo f) {
		Parameterizable source = piPort2Vx.get(f.getSourcePort());
		Parameterizable target = piPort2Vx.get(f.getTargetPort());

		if (source instanceof AbstractVertex
				&& target instanceof AbstractVertex) {

			DataInputPort piTgtPort = f.getTargetPort();
			DataOutputPort piSrcPort = f.getSourcePort();

			SDFAbstractVertex sdfSource = piVx2SDFVx.get(source);
			SDFAbstractVertex sdfTarget = piVx2SDFVx.get(target);
			
			SDFSinkInterfaceVertex sdfSrcPort = new SDFSinkInterfaceVertex();
			sdfSrcPort.setName(piSrcPort.getName());
			sdfSource.addSink(sdfSrcPort);
			
			SDFSourceInterfaceVertex sdfTgtPort = new SDFSourceInterfaceVertex();
			sdfTgtPort.setName(piTgtPort.getName());
			sdfTarget.addSource(sdfTgtPort);

			AbstractEdgePropertyType<ExpressionValue> delay;
			if (f.getDelay() != null) {
				// Evaluate the expression wrt. the current values of the
				// parameters
				String piDelay = f.getDelay().getExpression().evaluate();
				delay = new SDFExpressionEdgePropertyType(new ExpressionValue(
						piDelay));
			} else {
				delay = new SDFExpressionEdgePropertyType(new ExpressionValue(
						"0"));
			}
			// Evaluate the expression wrt. the current values of the parameters
			String piCons = piTgtPort.getExpression().evaluate();
			AbstractEdgePropertyType<ExpressionValue> cons = new SDFExpressionEdgePropertyType(
					new ExpressionValue(piCons));

			// Evaluate the expression wrt. the current values of the parameters
			String piProd = piSrcPort.getExpression().evaluate();
			AbstractEdgePropertyType<ExpressionValue> prod = new SDFExpressionEdgePropertyType(
					new ExpressionValue(piProd));

			result.addEdge(sdfSource, sdfSrcPort,
					sdfTarget, sdfTgtPort, prod, cons, delay);
		}

	}

	@Override
	public void visitParameterizable(Parameterizable p) {
		for (ConfigInputPort cip : p.getConfigInputPorts()) {
			piPort2Vx.put(cip, p);
		}
	}

	@Override
	public void visitInterfaceActor(InterfaceActor ia) {
		// DO NOTHING
	}

	@Override
	public void visitConfigInputInterface(ConfigInputInterface cii) {
		// DO NOTHING
	}

	@Override
	public void visitConfigOutputInterface(ConfigOutputInterface coi) {
		visitInterfaceActor(coi);
	}

	@Override
	public void visitDataInputInterface(DataInputInterface dii) {
		SDFSourceInterfaceVertex v = new SDFSourceInterfaceVertex();
		v.setName(dii.getName());

		visitAbstractActor(dii);

		result.addVertex(v);
		piVx2SDFVx.put(dii, v);
	}

	@Override
	public void visitDataOutputInterface(DataOutputInterface doi) {
		SDFSinkInterfaceVertex v = new SDFSinkInterfaceVertex();
		v.setName(doi.getName());

		visitAbstractActor(doi);

		result.addVertex(v);
		piVx2SDFVx.put(doi, v);
	}

	public SDFGraph getResult() {
		return result;
	}

	/**
	 * Methods below are unused and unimplemented visit methods
	 */

	@Override
	public void visitDataOutputPort(DataOutputPort dop) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDelay(Delay d) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDataInputPort(DataInputPort dip) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitExpression(Expression e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitConfigInputPort(ConfigInputPort cip) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitConfigOutputPort(ConfigOutputPort cop) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDependency(Dependency d) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitISetter(ISetter is) {
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
}

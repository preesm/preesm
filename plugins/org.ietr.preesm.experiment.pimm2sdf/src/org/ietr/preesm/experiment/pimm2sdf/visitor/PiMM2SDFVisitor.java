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

import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.parameters.ExpressionValue;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
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

/**
 * This class visits a PiGraph and generates a corresponding SDFGraph
 * 
 * @author cguy
 * 
 */
public class PiMM2SDFVisitor extends PiMMVisitor {
	// List of all instances created from the outer graph
	private List<SDFGraph> result;
	// Currently generated SDFGraph
	private SDFGraph currentSDFGraph;
	// Original list of fixed values for all the parameters of the graph
	private Map<String, List<Integer>> parameterValues;
	// List of fixed values for parameters of inner graphs, list of one value
	// for parameters of outer graph
	private Map<String, List<Integer>> currentParametersValues;

	// Map from original PiMM vertices to generated SDF vertices
	private Map<AbstractVertex, SDFAbstractVertex> piVx2SDFVx = new HashMap<AbstractVertex, SDFAbstractVertex>();
	// Map from PiMM ports to their vertex (used for SDFEdge creation)
	private Map<Port, Parameterizable> piPort2Vx = new HashMap<Port, Parameterizable>();
	// Set of subgraphs to visit afterwards
	private Set<PiGraph> subgraphs = new HashSet<PiGraph>();

	public PiMM2SDFVisitor(Map<String, List<Integer>> parameterValues) {
		this.parameterValues = parameterValues;
	}

	/**
	 * Entry point of the visitor
	 */
	@Override
	public void visitPiGraph(PiGraph pg) {
		// If result == null, then pg is the first PiGraph we encounter
		if (result == null) {
			result = new ArrayList<SDFGraph>();
			// Get the number of needed instances of pg
			int nbInstances = getNumberOfInstancesNeeded(pg, parameterValues);
			// Create nbInstances instances of SDFGraph corresponding to pg
			for (int i = 0; i < nbInstances; i++) {
				currentSDFGraph = new SDFGraph();
				currentSDFGraph.setName(pg.getName() + "_" + i);

				// Get the values of all parameters for this particular instance
				currentParametersValues = getValuesForOneInstance(
						new HashSet<Parameter>(pg.getParameters()), i);

				// Set values of the parameters of pg when possible
				setParameterValues(pg, currentParametersValues);

				// Visit each of the vertices of pg
				for (AbstractActor aa : pg.getVertices()) {
					aa.accept(this);
				}
				// And each of the data edges of pg
				for (Fifo f : pg.getFifos()) {
					f.accept(this);
				}

				// Pass the currentSDFGraph in Single Rate before instantiating
				// its subgraphs
				currentSDFGraphToSingleRate();

				// Then visit the subgraphs of pg once for each duplicates of
				// their corresponding SDFAbstractVertex created by Single Rate
				// transformation
				visitDuplicatesOfAllSubgraphs();

				result.add(currentSDFGraph);
			}

		}
		// Otherwise (if pg is not the first PiGraph we encounter during this
		// visit), we need to visit separately pg later, through
		// visitDuplicatesOf method
		else {
			subgraphs.add(pg);
		}
	}

	/**
	 * Transform the currentSDFGraph to an homogeneous SDF graph
	 */
	private void currentSDFGraphToSingleRate() {
		ToHSDFVisitor toHsdf = new ToHSDFVisitor();
		// the HSDF visitor will duplicates SDFAbstractVertex corresponding
		// to subgraphs and we will just have to visit them afterwards with
		// the good parameter values
		try {
			currentSDFGraph.accept(toHsdf);
		} catch (SDF4JException e) {
			e.printStackTrace();
		}
		currentSDFGraph = toHsdf.getOutput();
	}

	/**
	 * Compute the number of executions of a PiGraph wrt. the given values to
	 * its parameters and thus the number of needed instances of this PiGraph
	 * 
	 * @param graph
	 *            the PiGraph for which we want to know the number of executions
	 * @param parameterValues
	 *            the given values for graph's Parameters
	 * @return the number of instances we need to create
	 */
	private int getNumberOfInstancesNeeded(PiGraph graph,
			Map<String, List<Integer>> parameterValues) {
		// Number of instances of graph we will create
		int nbInstances = 0;
		// Get the number of instances needed (the maximum size of the different
		// lists of values for the parameters of graph)
		for (Parameter p : graph.getParameters()) {
			List<Integer> pValues = parameterValues.get(p.getName());
			if (pValues != null) {
				int nbPValues = pValues.size();
				if (nbPValues > nbInstances)
					nbInstances = nbPValues;
			}
		}
		return nbInstances;
	}

	/**
	 * Set the value of parameters of a PiGraph when possible (i.e., if we have
	 * currently only one available value, or if we can compute the value)
	 * 
	 * @param graph
	 *            the PiGraph in which we want to set the values of parameters
	 * @param parameterValues
	 *            the list of available values for each parameter
	 */
	private void setParameterValues(PiGraph graph,
			Map<String, List<Integer>> parameterValues) {
		// Factory for creation of new Pi Expressions
		PiMMFactory piFactory = PiMMFactory.eINSTANCE;
		// If there is only one value available for Parameter p, we can set it
		for (Parameter p : graph.getParameters()) {
			List<Integer> pValues = parameterValues.get(p.getName());
			if (pValues != null && pValues.size() == 1) {
				Expression pExp = piFactory.createExpression();
				pExp.setString(pValues.get(0).toString());
				p.setExpression(pExp);
			}
		}
		// If there is no list of value for one Parameter, the value of the
		// parameter is derived (i.e., computed from other parameters values),
		// we can evaluate it (after the values of other parameters have been
		// fixed)
		for (Parameter p : graph.getParameters()) {
			if (!parameterValues.containsKey(p.getName())) {
				p.getExpression().evaluate();
			}
		}
	}

	/**
	 * Visit each subgraph of the currently visited PiGraph once for each
	 * duplicates obtained through single rate transformation
	 */
	private void visitDuplicatesOfAllSubgraphs() {
		// For each subgraph of pg encountered during the visit, get the
		// duplicates
		Map<SDFGraph, Set<SDFAbstractVertex>> duplicates = new HashMap<SDFGraph, Set<SDFAbstractVertex>>();
		for (SDFAbstractVertex vertex : currentSDFGraph.vertexSet()) {
			AbstractGraph<?, ?> parent = vertex.getBase();
			if (parent != null && parent instanceof SDFGraph) {
				SDFGraph sdfParent = (SDFGraph) parent;
				if (!duplicates.containsKey(sdfParent)) {
					duplicates.put(sdfParent, new HashSet<SDFAbstractVertex>());
				}
				duplicates.get(sdfParent).add(vertex);
			}
		}
		// Then visit the subgraph, changing the value of parameters each
		// time, and associates the result of the visit to the duplicates
		for (PiGraph subgraph : subgraphs) {
			visitDuplicatesOf(subgraph,
					duplicates.get(piVx2SDFVx.get(subgraph)));
		}
	}

	/**
	 * Visit a PiGraph once for each duplicates and set the result of the visit
	 * as refinement of the duplicate vertices
	 * 
	 * @param subgraph
	 *            the PiGraph we visit
	 * @param duplicates
	 *            the set of duplicates for which we need to set a refinement
	 */
	private void visitDuplicatesOf(PiGraph subgraph,
			Set<SDFAbstractVertex> duplicates) {
		// Index for the values of the parameter
		int i = 0;
		// For each SDFAbstractVertex which are duplicates of the Actor
		// containing the PiGraph subgraph
		for (SDFAbstractVertex duplicate : duplicates) {
			// Collect the parameters to be fixed to a given value
			Set<Parameter> parametersToFix = new HashSet<Parameter>();
			for (ConfigInputPort cip : subgraph.getConfigInputPorts()) {
				ISetter setter = cip.getIncomingDependency().getSetter();
				if (setter instanceof Parameter) {
					parametersToFix.add((Parameter) setter);
				}
			}
			// And get the values for these parameters
			Map<String, List<Integer>> innerParameterValues = getValuesForOneInstance(
					parametersToFix, i);
			// Then visit the subgraph with the fixed parameter values
			createSubraphCopyWithValues(subgraph, innerParameterValues,
					duplicate);
			i++;
		}
	}

	/**
	 * Select one value for each given parameter wrt. the given instance number
	 * 
	 * @param parametersToFix
	 *            the set of Parameters for which we want one value
	 * @param instance
	 *            the number of the current instance
	 * @return a list of values for Parameters not included in parametersToFix,
	 *         one value for Parameters included in parametersToFix
	 */
	private Map<String, List<Integer>> getValuesForOneInstance(
			Set<Parameter> parametersToFix, int instance) {
		// Take the current map (currentParametersValues) in which we will
		// overwrite values of parameters directly entering into subgraph
		Map<String, List<Integer>> innerParameterValues = currentParametersValues;

		// For each parameter to fix
		for (Parameter p : parametersToFix) {
			List<Integer> setterValue = new ArrayList<Integer>();
			List<Integer> possibleValues = currentParametersValues.get(p
					.getName());
			// Select a value of the parameter wrt. the instance number
			int value = possibleValues.get(instance % possibleValues.size());
			setterValue.add(value);
			innerParameterValues.put(p.getName(), setterValue);
		}
		return innerParameterValues;
	}

	/**
	 * Create an SDFGraph corresponding to a PiGraph with fixed parameter values
	 * and set this SDFGraph as refinement of an SDFAbstractVertex
	 * 
	 * @param subgraph
	 *            the PiGraph for which we want to generate an SDFGraph
	 * @param innerParameterValues
	 *            the list of values for the Parameters
	 * @param duplicate
	 *            the SDFAbstractVertex to which we will set the refinement
	 */
	private void createSubraphCopyWithValues(PiGraph subgraph,
			Map<String, List<Integer>> innerParameterValues,
			SDFAbstractVertex duplicate) {

		// Visit subgraph with the fixed parameter values
		PiMM2SDFVisitor innerVisitor = new PiMM2SDFVisitor(innerParameterValues);
		innerVisitor.visit(subgraph);

		// Add the result of the visit as refinement of the duplicate vertex
		if (innerVisitor.getResult().size() == 1) {
			duplicate.setRefinement(innerVisitor.getResult().get(0));
		}

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
	public void visitDataOutputPort(DataOutputPort dop) {
		dop.getExpression().accept(this);
	}

	@Override
	public void visitDelay(Delay d) {
		d.getExpression().accept(this);
	}

	@Override
	public void visitDataInputPort(DataInputPort dip) {
		dip.getExpression().accept(this);
	}

	@Override
	public void visitExpression(Expression e) {
		// Evaluate e wrt. the current values of the parameters
		e.evaluate();
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

			currentSDFGraph.addEdge(piVx2SDFVx.get(source),
					piVx2SDFVx.get(target), prod, cons, delay);
		}

		if (f.getDelay() != null)
			f.getDelay().accept(this);
	}

	@Override
	public void visitParameterizable(Parameterizable p) {
		for (ConfigInputPort cip : p.getConfigInputPorts()) {
			piPort2Vx.put(cip, p);
			cip.accept(this);
		}
	}

	public List<SDFGraph> getResult() {
		return result;
	}

	/**
	 * Methods below are unimplemented visit methods
	 */

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
	public void visitDataOutputInterface(DataOutputInterface doi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitDependency(Dependency d) {
		// TODO Auto-generated method stub

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
	public void visitPort(Port p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitRefinement(Refinement r) {
		// TODO Auto-generated method stub

	}
}

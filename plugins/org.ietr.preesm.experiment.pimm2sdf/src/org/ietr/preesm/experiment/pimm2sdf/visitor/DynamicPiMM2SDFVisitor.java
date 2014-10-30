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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.pimm2sdf.PiGraphExecution;

/**
 * This class visits a PiGraph with one value for each of the Parameters and
 * generates one SDFGraph
 * 
 * @author cguy
 * 
 */
public class DynamicPiMM2SDFVisitor extends AbstractPiMM2SDFVisitor {
	// Set of subgraphs to visit afterwards
	private Set<PiGraph> subgraphs = new HashSet<PiGraph>();

	public DynamicPiMM2SDFVisitor(PiGraphExecution execution) {
		super(execution);
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

			// Save the original Path to the pigraph in the property bean (used
			// by memory scripts)
			result.setPropertyValue(AbstractGraph.PATH, pg.eResource().getURI()
					.toPlatformString(false));

			// Set these values into the parameters of pg when possible
			for (Parameter p : pg.getParameters()) {
				p.accept(this);
			}
			computeDerivedParameterValues(pg, execution);
			// Once the values are set, use them to put parameters as graph
			// variables in the resulting SDF graph
			parameters2GraphVariables(pg, result);

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
				DynamicPiMM2SDFVisitor innerVisitor = new DynamicPiMM2SDFVisitor(
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
}

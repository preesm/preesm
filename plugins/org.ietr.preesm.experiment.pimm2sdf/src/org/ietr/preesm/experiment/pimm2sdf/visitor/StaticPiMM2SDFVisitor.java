package org.ietr.preesm.experiment.pimm2sdf.visitor;

import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.pimm2sdf.PiGraphExecution;

public class StaticPiMM2SDFVisitor extends AbstractPiMM2SDFVisitor {

	public StaticPiMM2SDFVisitor(PiGraphExecution execution) {
		super(execution);
	}

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

			// Set the values into the parameters of pg when possible
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
		}
		// If result != null, pg is not the first PiGraph we encounter, it is a
		// subgraph
		else {
			SDFVertex v = new SDFVertex();
			// Handle vertex's name
			v.setName(pg.getName());
			// Handle vertex's path inside the graph hierarchy
			v.setInfo(pg.getPath());
			// Handle ID
			v.setId(pg.getName());

			visitAbstractActor(pg);

			// Visit the subgraph
			StaticPiMM2SDFVisitor innerVisitor = new StaticPiMM2SDFVisitor(
					execution);
			innerVisitor.visit(pg);
			// Set the obtained SDFGraph as refinement for v
			SDFGraph sdf = innerVisitor.getResult();
			sdf.setName(sdf.getName() + execution.getExecutionLabel());
			v.setGraphDescription(sdf);

		}
	}

}

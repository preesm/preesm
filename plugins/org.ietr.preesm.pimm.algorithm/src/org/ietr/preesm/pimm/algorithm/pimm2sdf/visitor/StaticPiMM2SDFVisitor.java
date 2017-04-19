/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/
package org.ietr.preesm.pimm.algorithm.pimm2sdf.visitor;

import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.transformations.SpecialActorPortsIndexer;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.pimm2sdf.PiGraphExecution;

// TODO: Auto-generated Javadoc
/**
 * The Class StaticPiMM2SDFVisitor.
 */
public class StaticPiMM2SDFVisitor extends AbstractPiMM2SDFVisitor {

  /**
   * Instantiates a new static pi MM 2 SDF visitor.
   *
   * @param execution
   *          the execution
   */
  public StaticPiMM2SDFVisitor(final PiGraphExecution execution) {
    super(execution);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.pimm.algorithm.pimm2sdf.visitor.AbstractPiMM2SDFVisitor#visitPiGraph(org.ietr.preesm.experiment.model.pimm.PiGraph)
   */
  @Override
  public void visitPiGraph(final PiGraph pg) {
    // If result == null, then pg is the first PiGraph we encounter
    if (this.result == null) {
      this.result = new SDFGraph();
      this.result.setName(pg.getName());

      // Save the original Path to the pigraph in the property bean (used
      // by memory scripts)
      this.result.setPropertyValue(AbstractGraph.PATH, pg.eResource().getURI().toPlatformString(false));

      // Set the values into the parameters of pg when possible
      for (final Parameter p : pg.getParameters()) {
        p.accept(this);
      }
      computeDerivedParameterValues(pg, this.execution);
      // Once the values are set, use them to put parameters as graph
      // variables in the resulting SDF graph
      parameters2GraphVariables(pg, this.result);

      // Visit each of the vertices of pg with the values set
      for (final AbstractActor aa : pg.getVertices()) {
        aa.accept(this);
      }
      // And each of the data edges of pg with the values set
      for (final Fifo f : pg.getFifos()) {
        f.accept(this);
      }

      // Make sure all ports of special actors are indexed and ordered
      // both in top and sub graphes
      SpecialActorPortsIndexer.addIndexes(this.result);
      SpecialActorPortsIndexer.sortIndexedPorts(this.result);
    } else {
      // If result != null, pg is not the first PiGraph we encounter, it is a
      // subgraph
      final SDFVertex v = new SDFVertex();
      this.piVx2SDFVx.put(pg, v);
      // Handle vertex's name
      v.setName(pg.getName());
      // Handle vertex's path inside the graph hierarchy
      v.setInfo(pg.getPath());
      // Handle ID
      v.setId(pg.getName());

      visitAbstractActor(pg);

      // Visit the subgraph
      final StaticPiMM2SDFVisitor innerVisitor = new StaticPiMM2SDFVisitor(this.execution);
      innerVisitor.visit(pg);
      // Set the obtained SDFGraph as refinement for v
      final SDFGraph sdf = innerVisitor.getResult();
      sdf.setName(sdf.getName() + this.execution.getExecutionLabel());
      v.setGraphDescription(sdf);

      this.result.addVertex(v);
    }
  }

}

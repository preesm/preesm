/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.dftools.algorithm.test;

import java.io.File;
import java.io.FileNotFoundException;
import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.factories.DAGVertexFactory;
import org.ietr.dftools.algorithm.importer.GMLSDFImporter;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.visitors.DAGTransformation;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.jgrapht.alg.cycle.CycleDetector;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class DAGTransformationTest {

  /**
   * Main method for debug purposes ...
   */
  @Test
  public void testTransfo() {

    final SDFGraph demoGraph;
    final GMLSDFImporter importer = new GMLSDFImporter();
    try {
      demoGraph = importer.parse(new File("resources/flatten.graphml"));
    } catch (InvalidModelException | FileNotFoundException e) {
      throw new DFToolsAlgoException("Could not read test file", e);
    }
    final DAGTransformation<DirectedAcyclicGraph> dageur = new DAGTransformation<>(new DirectedAcyclicGraph(),
        DAGVertexFactory.getInstance());
    try {
      demoGraph.accept(dageur);
    } catch (final SDF4JException e) {
      throw new DFToolsAlgoException("Could not transform sdf to dag", e);
    }
    final DirectedAcyclicGraph dag = dageur.getOutput();
    final CycleDetector<DAGVertex, DAGEdge> detectCycles = new CycleDetector<>(dag);
    Assert.assertFalse(detectCycles.detectCycles());
  }
}

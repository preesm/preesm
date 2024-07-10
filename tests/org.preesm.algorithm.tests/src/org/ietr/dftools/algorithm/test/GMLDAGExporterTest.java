/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2023) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2022)
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.algorithm.io.gml.GMLDAGExporter;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.model.types.LongEdgePropertyType;

/**
 */
public class GMLDAGExporterTest {

  /**
   * Creates a graph to test the Explorer.
   *
   * @return The created Graph
   */
  private DirectedAcyclicGraph createTestComGraph() {

    final DirectedAcyclicGraph graph = new DirectedAcyclicGraph();

    // test_com_basique
    final DAGVertex sensorInt = new DAGVertex(null);
    sensorInt.setName("1");
    graph.addVertex(sensorInt);

    final DAGVertex gen5 = new DAGVertex(null);
    gen5.setName("Gen5");
    graph.addVertex(gen5);

    final DAGVertex recopie5 = new DAGVertex(null);
    recopie5.setName("recopie_5");
    graph.addVertex(recopie5);

    final DAGVertex acqData = new DAGVertex(null);
    acqData.setName("acq_data");
    graph.addVertex(acqData);

    final DAGEdge sensGen = graph.addEdge(sensorInt, gen5);
    sensGen.setWeight(new LongEdgePropertyType(8));

    final DAGEdge genRec = graph.addEdge(gen5, recopie5);
    genRec.setWeight(new LongEdgePropertyType(100));

    final DAGEdge genAcq = graph.addEdge(gen5, acqData);
    genAcq.setWeight(new LongEdgePropertyType(2));

    final DAGEdge recAcq = graph.addEdge(recopie5, acqData);
    recAcq.setWeight(new LongEdgePropertyType(1000));
    return graph;
  }

  /**
   */
  @Test
  public void testExport() throws IOException {
    final DirectedAcyclicGraph graph = createTestComGraph();
    final GMLDAGExporter exporter = new GMLDAGExporter();
    exporter.exportGraph(graph);

    File createTempFile;
    if (SystemUtils.IS_OS_UNIX) {
      final FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions
          .asFileAttribute(PosixFilePermissions.fromString("rw-------"));
      createTempFile = Files.createTempFile("export_test_", ".graphml", attr).toFile();
    } else {
      createTempFile = Files.createTempFile("export_test_", ".graphml").toFile();

      boolean success = true;

      success &= createTempFile.setReadable(true, true);
      success &= createTempFile.setWritable(true, true);
      success &= createTempFile.setExecutable(true, true);
      Assert.assertTrue(success);
    }
    createTempFile.deleteOnExit();

    exporter.transform(new FileOutputStream(createTempFile));

    final List<String> readAllLines = Files.readAllLines(createTempFile.toPath());

    Assert.assertNotNull(readAllLines);
    Assert.assertNotEquals(0, readAllLines.size());
  }

}

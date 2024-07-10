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
import org.preesm.algorithm.io.gml.GMLGenericExporter;
import org.preesm.algorithm.model.AbstractEdge;
import org.preesm.algorithm.model.AbstractGraph;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.parameters.Argument;
import org.preesm.algorithm.model.parameters.Parameter;
import org.preesm.algorithm.model.parameters.Variable;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.algorithm.model.sdf.SDFVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.preesm.algorithm.model.types.LongEdgePropertyType;

/**
 *
 */
public class GMLSDFExporterTest {

  /**
   * Creates a graph to test the Explorer.
   *
   * @return The created Graph
   */
  private SDFGraph createTestComGraph() {

    final SDFGraph graph = new SDFGraph();

    // test_com_basique
    final SDFInterfaceVertex sensorInt = new SDFSourceInterfaceVertex(null);
    sensorInt.setName("sensor_Int");
    graph.addVertex(sensorInt);

    final SDFVertex gen5 = new SDFVertex(null);
    gen5.setName("Gen5");
    graph.addVertex(gen5);

    final SDFVertex recopie5 = new SDFVertex(null);
    recopie5.setName("recopie_5");
    graph.addVertex(recopie5);

    final SDFInterfaceVertex acqData = new SDFSinkInterfaceVertex(null);
    acqData.setName("acq_data");
    graph.addVertex(acqData);

    gen5.addArgument(new Argument("NB_COPY", "100"));

    final SDFEdge sensGen = graph.addEdge(sensorInt, gen5);
    sensGen.setProd(new LongEdgePropertyType(1));
    sensGen.setCons(new LongEdgePropertyType(1));

    final SDFEdge genRec = graph.addEdge(gen5, recopie5);
    genRec.setProd(new LongEdgePropertyType(2));
    genRec.setCons(new LongEdgePropertyType(3));

    final SDFEdge genAcq = graph.addEdge(gen5, acqData);
    genAcq.setProd(new LongEdgePropertyType(1));
    genAcq.setCons(new LongEdgePropertyType(1));

    final SDFEdge recAcq = graph.addEdgeWithInterfaces(recopie5, acqData);
    recAcq.setProd(new LongEdgePropertyType(3));
    recAcq.setCons(new LongEdgePropertyType(2));

    graph.addParameter(new Parameter("SIZE"));
    graph.addParameter(new Parameter("NB_COPY"));

    graph.addVariable(new Variable("a", "5"));
    graph.addVariable(new Variable("b", "10"));

    return graph;
  }

  /**
   */
  @Test
  public void testExport() throws IOException {
    final SDFGraph sdfGraph = createTestComGraph();
    final GMLGenericExporter exporter = new GMLGenericExporter();
    @SuppressWarnings({ "unchecked", "rawtypes" })
    final AbstractGraph<AbstractVertex<?>, AbstractEdge<?, ?>> graph = (AbstractGraph) sdfGraph;

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

    exporter.export(graph, createTempFile.getAbsolutePath());

    final List<String> readAllLines = Files.readAllLines(createTempFile.toPath());

    Assert.assertNotNull(readAllLines);
    Assert.assertNotEquals(0, readAllLines.size());
  }

}

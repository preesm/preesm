/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
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
import org.preesm.algorithm.io.gml.GMLSDFImporter;
import org.preesm.algorithm.model.AbstractEdge;
import org.preesm.algorithm.model.AbstractGraph;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.commons.exceptions.PreesmException;

/**
 */
public class GMLSDFImporterTest {

  private void testImport(final String filePath) throws FileNotFoundException, IOException, PreesmException {
    final GMLSDFImporter importer = new GMLSDFImporter();
    final SDFGraph graph = importer.parse(new File(filePath));

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    final AbstractGraph<AbstractVertex<?>, AbstractEdge<?, ?>> graph2 = (AbstractGraph) graph;

    final GMLGenericExporter exporter = new GMLGenericExporter();
    exporter.export(graph2, createTempFile.getAbsolutePath());

    final List<String> readAllLines = Files.readAllLines(createTempFile.toPath());

    Assert.assertNotNull(readAllLines);
    Assert.assertNotEquals(0, readAllLines.size());
  }

  /**
   */
  @Test
  public void testImportStereo() throws PreesmException, IOException {
    final String filePath = "./resources/stereo_top.graphml";
    testImport(filePath);
  }

  /**
   */
  @Test
  public void testImportStereoYUV() throws PreesmException, IOException {
    final String filePath = "./resources/yuv_stereo_top.graphml";
    testImport(filePath);
  }

  /**
   */
  @Test
  public void testImportFlatten() throws PreesmException, IOException {
    final String filePath = "./resources/flatten.graphml";
    testImport(filePath);
  }

}

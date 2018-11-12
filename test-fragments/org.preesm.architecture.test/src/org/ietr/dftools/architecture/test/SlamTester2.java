/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.architecture.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.SlamPackage;
import org.ietr.dftools.architecture.slam.process.SlamFlattener;
import org.ietr.dftools.architecture.slam.serialize.IPXACTResourceFactoryImpl;
import org.ietr.dftools.architecture.utils.SlamException;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class SlamTester2 {

  /**
   */
  @Test
  public void testSlam() throws IOException {

    // check that the factory is registered
    // (only happens in command-line mode)
    // ...
    // duck you command line :)
    final Map<String, Object> extToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
    Object instance = extToFactoryMap.get("slam");
    if (instance == null) {
      instance = new IPXACTResourceFactoryImpl();
      extToFactoryMap.put("slam", instance);
    }

    if (!EPackage.Registry.INSTANCE.containsKey(SlamPackage.eNS_URI)) {
      EPackage.Registry.INSTANCE.put(SlamPackage.eNS_URI, SlamPackage.eINSTANCE);
    }

    final File createTempFile = File.createTempFile("output_", ".slam");
    createTempFile.deleteOnExit();

    flatten("./resources/4CoreX86.slam", createTempFile.getAbsolutePath());

    final List<String> readAllLines = Files.readAllLines(createTempFile.toPath());

    Assert.assertNotNull(readAllLines);
    Assert.assertNotEquals(0, readAllLines.size());
  }

  /**
   * Flatten.
   *
   * @param inputTopPath
   *          the input top path
   * @param outputPath
   *          the output path
   */
  private void flatten(final String inputTopPath, final String outputPath) {
    // Demand load the resource into the resource set.
    final ResourceSet resourceSet = new ResourceSetImpl();

    // resourceSet.
    final Resource resource = resourceSet.getResource(URI.createFileURI(inputTopPath), true);
    // Extract the root object from the resource.
    final Design design = (Design) resource.getContents().get(0);

    final SlamFlattener flattener = new SlamFlattener();
    flattener.flattenAllLevels(design);

    final ResourceSet resourceSet2 = new ResourceSetImpl();
    final Resource resource2 = resourceSet2.createResource(URI.createFileURI(outputPath));
    resource2.getContents().add(design);
    try {
      resource2.save(null);
    } catch (final IOException e) {
      throw new SlamException("Could not save", e);
    }
  }

}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.model.slam.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.exceptions.PreesmFrameworkException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.ComInterface;
import org.preesm.model.slam.ComNode;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentHolder;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.DataLink;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.FPGA;
import org.preesm.model.slam.SlamFactory;
import org.preesm.model.slam.VLNV;
import org.preesm.model.slam.serialize.IPXACTDesignWriter;
import org.preesm.model.slam.utils.SlamUserFactory;

/**
 * Class to generate default architectures.
 *
 * @author ahonorat
 *
 */
public class ArchitecturesGenerator {

  /** The Constant scenarioDirName. */
  private static final String ARCHI_DIR_NAME = "Archi";

  final IFolder archiDir;

  public ArchitecturesGenerator(final IProject project) {
    archiDir = project.getFolder(ArchitecturesGenerator.ARCHI_DIR_NAME);
  }

  /**
   * Generate and save default X86 architecture with the specified number or cores.
   *
   * @param nbX86cores
   *          Number of cores in the generated architecture.
   *
   */
  public void generateAndSaveX86Architecture(int nbX86cores) {
    saveArchitecture(generateX86Architecture(nbX86cores));
  }

  /**
   * Generate and save default Fpga architecture.
   *
   */
  public void generateAndSaveFpgaArchitecture() {
    saveArchitecture(generateFpgaArchitecture());
  }

  public void generateAndSaveArchitecture(Map<String, Integer> cores, String nodeName, Double communicationRate) {
    saveArchitecture(generateArchitecture(cores, nodeName, communicationRate));
  }

  /**
   * Generate and save default X86 architecture with the specified number or cores.
   *
   * @param nbX86cores
   *          Number of cores in the generated architecture.
   *
   * @return The generated architecture.
   */

  public static Design generateX86Architecture(int nbX86cores) {
    final VLNV rootVLNV = SlamFactory.eINSTANCE.createVLNV();

    rootVLNV.setName(nbX86cores + "CoresX86");

    rootVLNV.setLibrary("preesm");
    rootVLNV.setVendor("ietr");
    rootVLNV.setVersion("1");

    final Design design = SlamFactory.eINSTANCE.createDesign();
    design.setVlnv(rootVLNV);
    final ComponentHolder ch = SlamFactory.eINSTANCE.createComponentHolder();
    design.setComponentHolder(ch);

    final VLNV operatorVLNV = SlamFactory.eINSTANCE.createVLNV();
    operatorVLNV.setName("x86");
    operatorVLNV.setLibrary("");
    operatorVLNV.setVendor("");
    operatorVLNV.setVersion("");
    final Component opX86 = SlamUserFactory.eINSTANCE.createComponent(operatorVLNV, CPU.class.getSimpleName());
    ch.getComponents().add(opX86);

    final ComInterface mi = SlamFactory.eINSTANCE.createComInterface();
    mi.setName("BUSshared_mem");
    opX86.getInterfaces().add(mi);

    final ComponentInstance[] cores = new ComponentInstance[nbX86cores];
    for (int i = 0; i < nbX86cores; ++i) {
      cores[i] = SlamFactory.eINSTANCE.createComponentInstance();
      cores[i].setHardwareId(i);
      cores[i].setInstanceName("Core" + i);
      design.getComponentInstances().add(cores[i]);
      cores[i].setComponent(opX86);
    }

    final VLNV comNodeVLNV = SlamUserFactory.eINSTANCE.createVLNV();
    comNodeVLNV.setName("SHARED_MEM");
    comNodeVLNV.setLibrary("");
    comNodeVLNV.setVendor("");
    comNodeVLNV.setVersion("");

    final ComNode cn = SlamFactory.eINSTANCE.createComNode();
    cn.setParallel(true);
    cn.setSpeed(1000000000F); // 1 000 000 000 = 1E9F
    cn.setVlnv(comNodeVLNV);
    cn.getInterfaces().add(mi);
    ch.getComponents().add(cn);

    final ComponentInstance sharedMem = SlamFactory.eINSTANCE.createComponentInstance();
    sharedMem.setHardwareId(0);
    sharedMem.setInstanceName("shared_mem");
    design.getComponentInstances().add(sharedMem);
    sharedMem.setComponent(cn);

    for (int i = 0; i < nbX86cores; ++i) {
      final DataLink dl = SlamFactory.eINSTANCE.createDataLink();
      dl.setDirected(false);
      dl.setUuid(Integer.toString(i));
      dl.setSourceComponentInstance(cores[i]);
      dl.setDestinationComponentInstance(sharedMem);
      dl.setSourceInterface(mi);
      dl.setDestinationInterface(mi);
      design.getLinks().add(dl);
    }

    return design;
  }

  public static Design generateArchitecture(Map<String, Integer> coresList, String nodeName, Double communicationRate) {
    int i1 = 0;
    final Design design = SlamFactory.eINSTANCE.createDesign();
    final ComponentHolder ch = SlamFactory.eINSTANCE.createComponentHolder();
    final ComInterface mi = SlamFactory.eINSTANCE.createComInterface();
    final VLNV comNodeVLNV = SlamUserFactory.eINSTANCE.createVLNV();

    comNodeVLNV.setName("SHARED_MEM");
    comNodeVLNV.setLibrary("");
    comNodeVLNV.setVendor("");
    comNodeVLNV.setVersion("");

    final ComNode cn = SlamFactory.eINSTANCE.createComNode();
    cn.setParallel(true);
    cn.setSpeed(communicationRate.floatValue()); // 1 000 000 000 = 1E9F
    cn.setVlnv(comNodeVLNV);
    cn.getInterfaces().add(mi);
    ch.getComponents().add(cn);

    final ComponentInstance sharedMem = SlamFactory.eINSTANCE.createComponentInstance();
    sharedMem.setHardwareId(0);
    sharedMem.setInstanceName("shared_mem");
    design.getComponentInstances().add(sharedMem);
    sharedMem.setComponent(cn);

    for (final Entry<String, Integer> coreMap : coresList.entrySet()) {
      final VLNV rootVLNV = SlamFactory.eINSTANCE.createVLNV();
      rootVLNV.setName(nodeName);

      rootVLNV.setLibrary("preesm");
      rootVLNV.setVendor("ietr");
      rootVLNV.setVersion("1");

      design.setVlnv(rootVLNV);

      design.setComponentHolder(ch);

      final VLNV operatorVLNV = SlamFactory.eINSTANCE.createVLNV();
      operatorVLNV.setName(coreMap.getKey());
      operatorVLNV.setLibrary("");
      operatorVLNV.setVendor("");
      operatorVLNV.setVersion("");
      final Component opZ = SlamUserFactory.eINSTANCE.createComponent(operatorVLNV, CPU.class.getSimpleName());
      ch.getComponents().add(opZ);

      mi.setName("BUSshared_mem");
      opZ.getInterfaces().add(mi);

      final ComponentInstance[] cores = new ComponentInstance[coreMap.getValue()];
      String nodeID = "Core";
      if (nodeName.equals("top")) {
        nodeID = "Node";
      }
      for (int i = i1; i < i1 + coreMap.getValue(); ++i) {
        cores[i - i1] = SlamFactory.eINSTANCE.createComponentInstance();
        cores[i - i1].setHardwareId(i);
        cores[i - i1].setInstanceName(nodeID + i);
        design.getComponentInstances().add(cores[i - i1]);
        cores[i - i1].setComponent(opZ);
      }
      for (int i = i1; i < i1 + coreMap.getValue(); ++i) {
        final DataLink dl = SlamFactory.eINSTANCE.createDataLink();
        dl.setDirected(false);
        dl.setUuid(Integer.toString(i));
        dl.setSourceComponentInstance(cores[i - i1]);
        dl.setDestinationComponentInstance(sharedMem);
        dl.setSourceInterface(mi);
        dl.setDestinationInterface(mi);
        design.getLinks().add(dl);
      }
      i1 += coreMap.getValue();
    }

    return design;
  }

  /**
   * Generate and save fpga architecture with.
   *
   * @return The generated architecture.
   */
  public static Design generateFpgaArchitecture() {
    final VLNV rootVLNV = SlamFactory.eINSTANCE.createVLNV();
    rootVLNV.setName("fpga");
    rootVLNV.setLibrary("preesm");
    rootVLNV.setVendor("ietr");
    rootVLNV.setVersion("1");

    final Design design = SlamFactory.eINSTANCE.createDesign();
    design.setVlnv(rootVLNV);
    final ComponentHolder ch = SlamFactory.eINSTANCE.createComponentHolder();
    design.setComponentHolder(ch);

    final VLNV operatorVLNV = SlamFactory.eINSTANCE.createVLNV();
    operatorVLNV.setName("FPGA");
    operatorVLNV.setLibrary("");
    operatorVLNV.setVendor("");
    operatorVLNV.setVersion("");
    final Component opFpga = SlamUserFactory.eINSTANCE.createComponent(operatorVLNV, FPGA.class.getSimpleName());
    ch.getComponents().add(opFpga);

    final ComponentInstance fpga = SlamFactory.eINSTANCE.createComponentInstance();
    fpga.setHardwareId(0);
    fpga.setInstanceName("Fpga");
    design.getComponentInstances().add(fpga);
    fpga.setComponent(opFpga);

    return design;
  }

  /**
   * Save the specified architecture in the Archi folder.
   *
   * @param design
   *          Architecture to save.
   */
  public void saveArchitecture(Design design) {
    final String name = design.getVlnv().getName();
    final IPath archiPath = new Path(name).addFileExtension("slam");
    final IFile archiFile = archiDir.getFile(archiPath);
    if (!archiFile.exists()) {
      try {
        archiFile.create(null, false, null);
      } catch (final CoreException e) {
        throw new PreesmRuntimeException(e);
      }
    }

    try (final ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
      final IPXACTDesignWriter designWriter = new IPXACTDesignWriter();
      designWriter.write(design, byteStream);
      archiFile.setContents(new ByteArrayInputStream(byteStream.toByteArray()), true, false, new NullProgressMonitor());
    } catch (final IOException | CoreException e) {
      throw new PreesmFrameworkException(e);
    }

  }

}

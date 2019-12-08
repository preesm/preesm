package org.preesm.model.slam.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.exceptions.PreesmFrameworkException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.slam.ComInterface;
import org.preesm.model.slam.ComNode;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentHolder;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.DataLink;
import org.preesm.model.slam.Design;
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
   */
  public void generateAndSaveArchitecture(int nbX86cores) {
    saveArchitecture(generateArchitecture(nbX86cores));
  }

  /**
   * Generate and save default X86 architecture with the specified number or cores.
   * 
   * @param nbX86cores
   *          Number of cores in the generated architecture.
   * @return The generated architecture.
   */
  public static Design generateArchitecture(int nbX86cores) {
    VLNV rootVLNV = SlamFactory.eINSTANCE.createVLNV();
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

    final Component opX86 = SlamUserFactory.eINSTANCE.createComponent(operatorVLNV, "Operator");
    ch.getComponents().add(opX86);

    final ComInterface mi = SlamFactory.eINSTANCE.createComInterface();
    mi.setName("BUSshared_mem");
    opX86.getInterfaces().add(mi);

    ComponentInstance[] cores = new ComponentInstance[nbX86cores];
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
      } catch (CoreException e) {
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

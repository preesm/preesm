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
import org.preesm.model.slam.ComponentHolder;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.SlamFactory;
import org.preesm.model.slam.VLNV;
import org.preesm.model.slam.serialize.IPXACTDesignWriter;

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
    saveArchitecture(generateArchitecture(nbX86cores), nbX86cores + "CoresX86");
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
    rootVLNV.setName("rootVLNV");
    rootVLNV.setLibrary("lib");
    rootVLNV.setVendor("vendor");
    rootVLNV.setVersion("1.0");

    ComponentHolder ch = SlamFactory.eINSTANCE.createComponentHolder();

    Design design = SlamFactory.eINSTANCE.createDesign();
    design.setVlnv(rootVLNV);
    design.setComponentHolder(ch);
    return design;
  }

  /**
   * Save the specified architecture in the Archi folder.
   * 
   * @param design
   *          Architecture to save.
   * @param name
   *          File name (without extension).
   */
  public void saveArchitecture(Design design, String name) {
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

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Hascoet <jhascoet@kalray.eu> (2016)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2013)
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
package org.preesm.codegen.xtend.task;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.codegen.CodegenException;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.printer.CodegenAbstractPrinter;

/**
 * The Class CodegenEngine.
 */
public class CodegenEngine {

  public static final String PRINTERS_EXTENSION_ID = "org.preesm.codegen.printers";

  /** The codegen path. */
  private final String codegenPath;

  /** The code blocks. */
  private final Collection<Block> codeBlocks;

  public Collection<Block> getCodeBlocks() {
    return codeBlocks;
  }

  /** The registered printers and blocks. */
  private Map<IConfigurationElement, List<Block>> registeredPrintersAndBlocks;

  /** The real printers. */
  private Map<IConfigurationElement, CodegenAbstractPrinter> realPrinters;

  private final CodegenModelGenerator generator;

  /**
   * Instantiates a new codegen engine.
   *
   */
  public CodegenEngine(final String codegenPath, final Collection<Block> codeBlocks, CodegenModelGenerator generator) {
    this.codegenPath = codegenPath;
    this.codeBlocks = codeBlocks;
    this.generator = generator;
  }

  public final Design getArchi() {
    return this.generator.getArchi();
  }

  public final MapperDAG getAlgo() {
    return this.generator.getAlgo();
  }

  public final Map<String, MemoryExclusionGraph> getMegs() {
    return this.generator.getMegs();
  }

  public final PreesmScenario getScenario() {
    return this.generator.getScenario();
  }

  /**
   * Initialize printer IR.
   *
   * @param codegenPath
   *          the codegen path
   */
  public void initializePrinterIR(final String codegenPath) {

    // Save the intermediate model
    // Register the XMI resource factory for the .codegen extension
    final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
    final Map<String, Object> m = reg.getExtensionToFactoryMap();
    m.put("codegen", new XMIResourceFactoryImpl());

    // Obtain a new resource set
    final ResourceSet resSet = new ResourceSetImpl();

    for (final Block b : this.codeBlocks) {
      // Create a resource
      final Resource resource = resSet.createResource(URI.createURI(codegenPath + b.getName() + ".codegen"));
      // Get the first model element and cast it to the right type, in
      // my example everything is hierarchical included in this first
      // node
      resource.getContents().add(b);
    }

    // Now save the content.
    for (final Resource resource : resSet.getResources()) {
      try {
        resource.save(Collections.EMPTY_MAP);
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

  }

  /**
   * Register printers and blocks.
   *
   * @param selectedPrinter
   *          the selected printer
   * @throws WorkflowException
   *           the workflow exception
   */
  public void registerPrintersAndBlocks(final String selectedPrinter) throws WorkflowException {
    this.registeredPrintersAndBlocks = new LinkedHashMap<>();

    // 1. Get the printers of the desired "language"
    final Set<IConfigurationElement> usablePrinters = new LinkedHashSet<>();
    final IExtensionRegistry registry = Platform.getExtensionRegistry();
    final IConfigurationElement[] elements = registry.getConfigurationElementsFor(PRINTERS_EXTENSION_ID);
    for (final IConfigurationElement element : elements) {
      if (element.getAttribute("language").equals(selectedPrinter)) {
        for (final IConfigurationElement child : element.getChildren()) {
          if (child.getName().equals("printer")) {
            usablePrinters.add(child);
          }
        }
      }
    }

    // 2. Get a printer for each Block
    for (final Block b : this.codeBlocks) {
      IConfigurationElement foundPrinter = null;
      if (b instanceof CoreBlock) {
        final String coreType = ((CoreBlock) b).getCoreType();
        for (final IConfigurationElement printer : usablePrinters) {
          final IConfigurationElement[] supportedCores = printer.getChildren();
          for (final IConfigurationElement supportedCore : supportedCores) {
            if (supportedCore.getAttribute("type").equals(coreType)) {
              foundPrinter = printer;
              break;
            }
          }
          if (foundPrinter != null) {
            break;
          }
        }
        if (foundPrinter != null) {
          List<Block> blocks = this.registeredPrintersAndBlocks.get(foundPrinter);
          if (blocks == null) {
            blocks = new ArrayList<>();
            this.registeredPrintersAndBlocks.put(foundPrinter, blocks);
          }
          blocks.add(b);
        } else {
          throw new WorkflowException(
              "Could not find a printer for language \"" + selectedPrinter + "\" and core type \"" + coreType + "\".");
        }
      } else {
        throw new WorkflowException("Only CoreBlock CodeBlocks can be printed in the current version of Preesm.");
      }
    }
  }

  /**
   * Preprocess printers.
   *
   * @throws WorkflowException
   *           the workflow exception
   */
  public void preprocessPrinters() {
    // Pre-process the printers one by one to:
    // - Erase file with the same extension from the destination directory
    // - Do the pre-processing
    // - Save the printers in a map
    this.realPrinters = new LinkedHashMap<>();
    for (final Entry<IConfigurationElement, List<Block>> printerAndBlocks : this.registeredPrintersAndBlocks
        .entrySet()) {
      final String extension = printerAndBlocks.getKey().getAttribute("extension");
      CodegenAbstractPrinter printer = null;
      try {
        printer = (CodegenAbstractPrinter) printerAndBlocks.getKey().createExecutableExtension("class");
      } catch (final CoreException e) {
        throw new WorkflowException(e.getMessage(), e);
      }

      // Erase previous files with extension
      // Lists all files in folder
      try {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
        final IFolder f = workspace.getRoot().getFolder(new Path(this.codegenPath));
        final IPath rawLocation = f.getRawLocation();
        if (rawLocation == null) {
          throw new CodegenException("Could not find target project for given path [" + this.codegenPath
              + "]. Please change path in the scenario editor.");
        }
        final String osString = rawLocation.toOSString();
        File folder = new File(osString);
        if (!folder.exists()) {
          folder.mkdirs();
          WorkflowLogger.getLogger()
              .info("Created missing target dir [" + folder.getAbsolutePath() + "] during codegen");
        }
        workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
        if (!f.exists()) {
          f.create(true, true, null);
        }
        if (!folder.exists()) {
          throw new FileNotFoundException("Target generation folder [" + folder.getAbsolutePath() + "] does not exist");
        }
        final File[] fList = folder.listFiles();
        if (fList != null) {
          // Searches .extension
          for (final File element : fList) {
            final String pes = element.getName();
            if (pes.endsWith(extension)) {
              // and deletes
              element.delete();
            }
          }
        }
      } catch (CoreException | FileNotFoundException e) {
        throw new WorkflowException(
            "Could not access target directory [" + this.codegenPath + "] during code generation", e);
      }

      // initialize printer engine
      printer.setEngine(this);

      // Do the pre-processing
      printer.preProcessing(printerAndBlocks.getValue(), this.codeBlocks);
      this.realPrinters.put(printerAndBlocks.getKey(), printer);
    }
  }

  /**
   * Prints the.
   */
  public void print() {

    for (final Entry<IConfigurationElement, List<Block>> printerAndBlocks : this.registeredPrintersAndBlocks
        .entrySet()) {

      final String extension = printerAndBlocks.getKey().getAttribute("extension");
      final CodegenAbstractPrinter printer = this.realPrinters.get(printerAndBlocks.getKey());

      for (final Block b : printerAndBlocks.getValue()) {
        final String fileContentString = printer.postProcessing(printer.doSwitch(b)).toString();
        final String fileName = b.getName() + extension;
        print(fileName, fileContentString);
      }

      // Print secondary files
      final Map<String, CharSequence> createSecondaryFiles = printer.createSecondaryFiles(printerAndBlocks.getValue(),
          this.codeBlocks);
      for (final Entry<String, CharSequence> entry : createSecondaryFiles.entrySet()) {
        final String fileName = entry.getKey();
        print(fileName, entry.getValue());
      }

      // Add standard files for this printer
      final Map<String, CharSequence> generateStandardLibFiles = printer.generateStandardLibFiles();
      for (final Entry<String, CharSequence> entry : generateStandardLibFiles.entrySet()) {
        final String fileName = entry.getKey();
        print(fileName, entry.getValue());
      }
    }
  }

  private void print(final String fileName, final CharSequence fileContent) {
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(this.codegenPath + fileName));
    try {
      final IFolder iFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(this.codegenPath));
      if (!iFolder.exists()) {
        iFolder.create(false, true, new NullProgressMonitor());
      }
      if (!iFile.exists()) {
        iFile.create(new ByteArrayInputStream("".getBytes()), false, new NullProgressMonitor());
      }
      iFile.setContents(new ByteArrayInputStream(fileContent.toString().getBytes()), true, false,
          new NullProgressMonitor());

    } catch (final CoreException ex) {
      throw new CodegenException("Could not generated source file for " + fileName, ex);
    }
  }
}

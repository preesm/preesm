/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
 * Julien Hascoet [jhascoet@kalray.eu] (2016)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2015 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013)
 * Leonardo Suriano [leonardo.suriano@upm.es] (2019)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2013)
 * Raquel Lazcano [raquel.lazcano@upm.es] (2019)
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
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.preesm.codegen.format.CodeFormatterAndPrinter;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.ClusterRaiserBlock;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.MainSimsdpBlock;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.codegen.printer.CodegenAbstractPrinter;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * The Class CodegenEngine.
 */
public class CodegenEngine {

  public static final String PRINTERS_EXTENSION_ID = "org.preesm.codegen.printers";

  /** The codegen path. */
  private final String codegenPath;

  /** The Apollo flag. */
  private boolean apolloEnabled = false;

  /** The code blocks. */
  private final Collection<Block> codeBlocks;

  public Collection<Block> getCodeBlocks() {
    return this.codeBlocks;
  }

  /** The registered printers and blocks. */
  private Map<IConfigurationElement, List<Block>> registeredPrintersAndBlocks;

  /** The real printers. */
  private Map<IConfigurationElement, CodegenAbstractPrinter> realPrinters;

  private final PiGraph algo;

  private final Design archi;

  private final Scenario scenario;

  /**
   *
   */
  public CodegenEngine(final String codegenPath, final Collection<Block> codeBlocks, final PiGraph algo,
      final Design archi, final Scenario scenario) {
    this.codegenPath = codegenPath;
    this.codeBlocks = codeBlocks;
    this.algo = algo;
    this.archi = archi;
    this.scenario = scenario;

  }

  public final Design getArchi() {
    return this.archi;
  }

  public final PiGraph getAlgo() {
    return this.algo;
  }

  public final Scenario getScenario() {
    return this.scenario;
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
        resource.save(Collections.emptyMap());
      } catch (final IOException e) {
        PreesmLogger.getLogger().log(Level.WARNING, "Could not initialize the printer IR", e);
      }
    }

  }

  /**
   * Register printers and blocks.
   *
   * @param selectedPrinter
   *          the selected printer
   * @throws PreesmException
   *           the workflow exception
   */
  public void registerPrintersAndBlocks(final String selectedPrinter) {
    this.registeredPrintersAndBlocks = new LinkedHashMap<>();

    // 1. Get the printers of the desired "language"
    final Set<IConfigurationElement> usablePrinters = getLanguagePrinter(selectedPrinter);

    // 2. Get a printer for each Block
    for (final Block b : this.codeBlocks) {
      registerBlockPrinters(selectedPrinter, usablePrinters, b);
    }
  }

  private void registerBlockPrinters(final String selectedPrinter, final Set<IConfigurationElement> usablePrinters,
      final Block b) {
    IConfigurationElement foundPrinter = null;
    if (b instanceof ClusterRaiserBlock || b instanceof MainSimsdpBlock) {
      String coreType = "";
      if (b instanceof ClusterRaiserBlock) {
        coreType = ((ClusterRaiserBlock) b).getCoreType();
      } else {
        coreType = ((MainSimsdpBlock) b).getCoreType();
      }
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
      if (foundPrinter == null) {
        throw new PreesmRuntimeException(
            "Could not find a printer for language \"" + selectedPrinter + "\" and core type \"" + coreType + "\".");
      }
      if (!this.registeredPrintersAndBlocks.containsKey(foundPrinter)) {
        this.registeredPrintersAndBlocks.put(foundPrinter, new ArrayList<>());
      }
      final List<Block> blocks = this.registeredPrintersAndBlocks.get(foundPrinter);
      blocks.add(b);
    } else if (b instanceof CoreBlock) {
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
      if (foundPrinter == null) {
        throw new PreesmRuntimeException(
            "Could not find a printer for language \"" + selectedPrinter + "\" and core type \"" + coreType + "\".");
      }
      if (!this.registeredPrintersAndBlocks.containsKey(foundPrinter)) {
        this.registeredPrintersAndBlocks.put(foundPrinter, new ArrayList<>());
      }
      final List<Block> blocks = this.registeredPrintersAndBlocks.get(foundPrinter);
      blocks.add(b);
    } else {
      throw new PreesmRuntimeException("Only CoreBlock CodeBlocks can be printed in the current version of Preesm.");
    }
  }

  private Set<IConfigurationElement> getLanguagePrinter(final String selectedPrinter) {
    final Set<IConfigurationElement> usablePrinters = new LinkedHashSet<>();
    final IExtensionRegistry registry = Platform.getExtensionRegistry();
    final IConfigurationElement[] elements = registry.getConfigurationElementsFor(CodegenEngine.PRINTERS_EXTENSION_ID);
    for (final IConfigurationElement element : elements) {
      if (element.getAttribute("language").equals(selectedPrinter)) {
        for (final IConfigurationElement child : element.getChildren()) {
          if (child.getName().equals("printer")) {
            usablePrinters.add(child);
          }
        }
      }
    }
    return usablePrinters;
  }

  /**
   * Preprocess printers.
   *
   * @throws PreesmException
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
      CodegenAbstractPrinter printer = null;
      try {
        printer = (CodegenAbstractPrinter) printerAndBlocks.getKey().createExecutableExtension("class");
      } catch (final CoreException e) {
        throw new PreesmRuntimeException(e.getMessage(), e);
      }

      // initialize printer engine
      printer.setEngine(this);

      // set up apollo intra-actor optimization
      printer.setApolloEnabled(this.apolloEnabled);

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
      boolean multinode = false;
      for (final Block b : printerAndBlocks.getValue()) {
        final String fileContentString = printer.postProcessing(printer.doSwitch(b)).toString();
        final String fileName = b.getName() + extension;
        final IFile iFile = PreesmIOHelper.getInstance().print(this.codegenPath, fileName, fileContentString);
        CodeFormatterAndPrinter.format(iFile);
        if (b instanceof CoreBlock) {
          multinode = ((CoreBlock) b).isMultinode();
        }
      }
      if (!multinode) {
        // Print secondary files (main file)
        final Map<String, CharSequence> createSecondaryFiles = printer.createSecondaryFiles(printerAndBlocks.getValue(),
            this.codeBlocks);
        for (final Entry<String, CharSequence> entry : createSecondaryFiles.entrySet()) {
          final String fileName = entry.getKey();
          final IFile iFile = PreesmIOHelper.getInstance().print(this.codegenPath, fileName, entry.getValue());
          CodeFormatterAndPrinter.format(iFile);
        }

        // Add standard files for this printer
        final Map<String, CharSequence> generateStandardLibFiles = printer.generateStandardLibFiles();
        for (final Entry<String, CharSequence> entry : generateStandardLibFiles.entrySet()) {
          final String fileName = entry.getKey();
          final IFile iFile = PreesmIOHelper.getInstance().print(this.codegenPath, fileName, entry.getValue());
          CodeFormatterAndPrinter.format(iFile);
        }
      } else {
        // Print secondary files (main file)
        final Map<String, CharSequence> createSecondaryFiles = printer.createSecondaryFiles(printerAndBlocks.getValue(),
            this.codeBlocks);
        for (final Entry<String, CharSequence> entry : createSecondaryFiles.entrySet()) {

          final String fileName = this.algo.getName() + ".c";
          final IFile iFile = PreesmIOHelper.getInstance().print(this.codegenPath, fileName, entry.getValue());
          CodeFormatterAndPrinter.format(iFile);
        }
      }
    }
  }

  /**
   * Prints SCAPE cluster files
   */
  public void printClusterRaiser() {

    // Print C files
    final Block hb = CodegenModelUserFactory.eINSTANCE.createBlock();
    for (final Entry<IConfigurationElement, List<Block>> printerAndBlocks : this.registeredPrintersAndBlocks
        .entrySet()) {

      final String extension = printerAndBlocks.getKey().getAttribute("extension");
      final CodegenAbstractPrinter printer = this.realPrinters.get(printerAndBlocks.getKey());
      final String extensionH = ".h";

      for (final Block b : printerAndBlocks.getValue()) {
        if (b instanceof ClusterRaiserBlock) {
          if (((ClusterRaiserBlock) b).getBodyBlock() != null) {
            final String fileContentString = printer.postProcessing(printer.doSwitch(b)).toString();
            final String fileName = b.getName() + extension;

            final IFile iFile = PreesmIOHelper.getInstance().print(this.codegenPath, fileName, fileContentString);
            CodeFormatterAndPrinter.format(iFile);
            hb.setName(b.getName());

            // Print H files

            final String fileContentStringH = printer.clusterRaiserSecondaryFileHeader((ClusterRaiserBlock) b)
                .toString();
            final String fileNameH = b.getName() + extensionH;
            final IFile iFileH = PreesmIOHelper.getInstance().print(this.codegenPath, fileNameH, fileContentStringH);
            CodeFormatterAndPrinter.format(iFileH);
          }
        }
      }
    }
  }

  /**
   *
   * @param apolloFlag
   *          Enable the use of Apollo for intra-actor optimization
   */
  public void registerApollo(String apolloFlag) {
    this.apolloEnabled = "true".equalsIgnoreCase(apolloFlag);
  }

  /**
   * Prints Main multi-node file
   */
  public void printMainSimSDP() {
    for (final Entry<IConfigurationElement, List<Block>> printerAndBlocks : this.registeredPrintersAndBlocks
        .entrySet()) {
      final String extension = printerAndBlocks.getKey().getAttribute("extension");
      final CodegenAbstractPrinter printer = this.realPrinters.get(printerAndBlocks.getKey());
      for (final Block b : printerAndBlocks.getValue()) {
        b.setName("mainSimSDP");
        final String fileContentString = printer.postProcessing(printer.doSwitch(b)).toString();
        final String fileName = b.getName() + extension;

        final IFile iFile = PreesmIOHelper.getInstance().print(this.codegenPath, fileName, fileContentString);
        CodeFormatterAndPrinter.format(iFile);
      }

    }
  }

  public void print2() {
    // TODO Auto-generated method stub

  }
}

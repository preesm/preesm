/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020 - 2021) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2020 - 2021)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2020)
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
package org.preesm.codegen.xtend.spider2;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.Path;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.codegen.xtend.spider2.utils.Spider2CodegenCluster;
import org.preesm.codegen.xtend.spider2.utils.Spider2CodegenPE;
import org.preesm.codegen.xtend.spider2.utils.Spider2Config;
import org.preesm.codegen.xtend.spider2.visitor.Spider2PreProcessVisitor;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;

/**
 * The Class Spider2Codegen.
 */
public class Spider2Codegen {
  /** The scenario. */
  private final Scenario scenario;

  /** The application graph */
  private final PiGraph applicationGraph;

  /** The Spider2PreProcessor **/
  private final Spider2PreProcessVisitor preprocessor;

  /** The application name **/
  private final String applicationName;

  /** The codegen folder */
  private final File folder;

  /** The original class context loader */
  private ClassLoader originalContextClassLoader = null;

  /** The VelocityEngine used by the codegen */
  private VelocityEngine velocityEngine = null;

  /**
   * Instantiates a new spider2 codegen.
   *
   * @param scenario
   *          the scenario
   * @param applicationGraph
   *          the main application graph
   * @param folder
   *          the codegen directory
   */
  public Spider2Codegen(final Scenario scenario, final PiGraph applicationGraph, final File folder) {
    this.scenario = scenario;
    this.applicationGraph = applicationGraph;
    this.folder = folder;
    /** Calls the pre-processor */
    this.preprocessor = new Spider2PreProcessVisitor(this.scenario);
    this.preprocessor.doSwitch(this.applicationGraph);
    this.applicationName = this.applicationGraph.getPiGraphName().toLowerCase();
  }

  /**
   * Initializes the context loader of the class. Must be call before every other method of this class.
   */
  public void init() {
    if (this.originalContextClassLoader != null) {
      return;
    }
    this.originalContextClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
    this.velocityEngine = new VelocityEngine();
    // https://stackoverflow.com/questions/9051413/unable-to-find-velocity-template-resources
    this.velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    this.velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    // this.velocityEngine.setProperty("resource.loader", "class");
    // this.velocityEngine.setProperty("class.resource.loader.class",
    // "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    this.velocityEngine.init();
    this.velocityEngine.removeDirective("ifndef");
    this.velocityEngine.removeDirective("define");
    this.velocityEngine.removeDirective("endif");
  }

  /**
   * Finalizes properly original class loader attribute. Must be called after finishing to use this class object.
   */
  public void end() {
    if (this.originalContextClassLoader == null) {
      return;
    }
    Thread.currentThread().setContextClassLoader(this.originalContextClassLoader);
    this.originalContextClassLoader = null;
    this.velocityEngine = null;
  }

  /**
   * Creates include, src, lib, cmake folders.
   * 
   * @param workspace
   *          the workspace
   */
  public void makeFolders(final IWorkspace workspace) {
    final String path = this.folder.getPath();
    /* We start looking backward -2 to remove the '/' at the end from the search */
    final String codegenDirectoryPath = path.substring(0, path.lastIndexOf('/', path.length() - 2) + 1);
    final File folderSrc = new File(codegenDirectoryPath + "src/");
    folderSrc.mkdirs();
    if (folderSrc.isDirectory()) {
      /* Rename the .c to .cpp if needed */
      renameFilesToCPPInFolder(folderSrc);
    }
    final File folderInclude = new File(codegenDirectoryPath + "include/");
    folderInclude.mkdirs();

    final File folderLibSpider = new File(codegenDirectoryPath + "lib/spider/api");
    folderLibSpider.mkdirs();

    final File folderCMake = new File(codegenDirectoryPath + "cmake/modules");
    folderCMake.mkdirs();

    final File folderBin = new File(codegenDirectoryPath + "bin/");
    folderBin.mkdirs();
  }

  /**
   * Move include files used by the algorithm.
   * 
   * @param workspace
   *          the workspace
   */
  public void moveIncludesToFolder(final IWorkspace workspace) {
    final String path = this.folder.getPath();
    final String codegenDirectoryPath = path.substring(0, path.lastIndexOf('/', path.length() - 2) + 1);
    final String includeDirectoryPath = codegenDirectoryPath + "include/";
    final File folderInclude = new File(codegenDirectoryPath + "include/");
    folderInclude.mkdirs();
    if (folderInclude.isDirectory()) {
      /* Fetch the refinements and move the header files into the include folder */
      for (final CHeaderRefinement refinement : this.preprocessor.getUniqueLoopHeaderList()) {
        final String refFilePath = refinement.getFilePath();
        final File oldFile = new File(
            workspace.getRoot().getFolder(new Path(refFilePath)).getRawLocation().toOSString());
        final File targetFile = new File(includeDirectoryPath + refinement.getFileName());

        try {
          Files.copy(oldFile.toPath(), targetFile.toPath(), REPLACE_EXISTING);
        } catch (IOException e) {
          throw new PreesmRuntimeException(e.toString());
        }
      }
    }
  }

  private static final void renameFilesToCPPInFolder(final File folder) {
    for (File file : folder.listFiles()) {
      if (file.isFile()) {
        final String fileName = file.toString();
        final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        if (extension.equals("c")) {
          try {
            Files.move(file.toPath(), file.toPath().resolveSibling(fileName.replace(".c", ".cpp")));
          } catch (IOException e) {
            throw new PreesmRuntimeException(e.toString());
          }
        }
      }
    }
  }

  /**
   * Generates the code of the physical architecture (s-lam based)
   */
  public void generateArchiCode() {
    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("mainPEName", this.preprocessor.getMainPEName());
    context.put("clusters", this.preprocessor.getClusterList());
    context.put("clusterCount", this.preprocessor.getClusterList().size());
    int peCount = 0;
    for (final Spider2CodegenCluster cluster : this.preprocessor.getClusterList()) {
      peCount += cluster.getPeCount();
    }
    context.put("peCount", peCount);

    /* Write the file */
    writeVelocityContext(context, "templates/cpp/app_archi_template.vm", "spider2-platform.cpp");
  }

  /**
   * Generates a default CMakeList for the spider project.
   */
  public void generateCMakeList() {
    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("appName", this.applicationName);
    context.put("folder", this.folder.getName());

    /* Write the file */
    writeVelocityContext(context, "templates/cpp/cmakelist_template.vm", "../CMakeLists.txt");

    /* Move FindThreads and FindSpider2.cmake files */
    copyFileFromTo("resources/cmake_modules/", this.scenario.getCodegenDirectory() + "/../cmake/modules/",
        "FindSpider2.cmake");
    copyFileFromTo("resources/cmake_modules/", this.scenario.getCodegenDirectory() + "/../cmake/modules/",
        "FindThreads.cmake");
  }

  /**
   * Generates the code of the main.cpp
   */
  public void generateMainCode(final Spider2Config config) {
    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("verbose", false);
    context.put("standalone", false);
    context.put("papify", false);
    context.put("apollo", false);
    context.put("genlog", true);
    context.put("exportTrace", false);
    context.put("exportSRDAG", false);
    context.put("clusterIx", "SIZE_MAX");
    context.put("genAllocPolicy", "GENERIC");
    context.put("genAllocAlign", "sizeof(int64_t)");
    context.put("genAllocSize", "SIZE_MAX");
    context.put("genAllocExtAddr", "nullptr");
    context.put("runMode", config.getRunMode());
    context.put("loopCount", "10000");
    context.put("runtimeAlgo", config.getRuntimeAlgo());
    context.put("schedAlgorithm", config.getSchedulerType());
    context.put("mapAlgorithm", config.getMapperType());
    context.put("allocType", config.getAllocatorType());
    context.put("execPolicy", config.getExecPolicyType());

    final List<Pair<String, List<ConfigInputPort>>> initPrototypes = new ArrayList<>();
    for (final PiGraph graph : this.preprocessor.getUniqueGraphSet()) {
      for (final Actor actor : graph.getActorsWithRefinement()) {
        if (actor.getRefinement() instanceof CHeaderRefinement) {
          final CHeaderRefinement refinement = (CHeaderRefinement) (actor.getRefinement());
          if (refinement.getInitPrototype() != null) {
            final FunctionPrototype ip = refinement.getInitPrototype();
            initPrototypes.add(new Pair<>(ip.getName(), new ArrayList<>(refinement.getInitConfigInputPorts())));
          }
        }
      }
    }
    context.put("initPrototypes", initPrototypes);

    /* Write the file */
    writeVelocityContext(context, "templates/cpp/app_main_template.vm", "spider2-main.cpp");

  }

  /**
   * Generates the code associated with the kernels of the application graph
   */
  public void generateKernelCode() {
    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("prototypes", this.preprocessor.getUniqueLoopPrototypeList());

    /* Write the file */
    writeVelocityContext(context, "templates/cpp/app_kernels_cpp_template.vm", "spider2-application-kernels.cpp");
  }

  /**
   * Generates the application header file.
   */
  public void generateApplicationHeader() {
    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("appName", this.applicationName);
    final Set<PiGraph> graphSet = new HashSet<>(this.preprocessor.getUniqueGraphSet());
    graphSet.remove(this.applicationGraph);
    context.put("graphs", graphSet);
    context.put("fileNames", this.preprocessor.getUniqueHeaderFileNameList());
    context.put("prototypes", this.preprocessor.getUniqueLoopPrototypeList());
    final Set<String> typesSet = new HashSet<>();
    final List<Spider2CodegenCluster> clusters = this.preprocessor.getClusterList();
    for (final Spider2CodegenCluster cluster : clusters) {
      for (final Spider2CodegenPE pe : cluster.getProcessingElements()) {
        typesSet.add(pe.getTypeName());
      }
    }
    context.put("types", typesSet);
    context.put("clusters", this.preprocessor.getClusterList());

    /* Write the file */
    writeVelocityContext(context, "templates/cpp/app_header_template.vm", "spider2-application.h");
  }

  /**
   * Generates CPP code for every unique pisdf graph of the application.
   */
  public void generateGraphCodes() {
    for (final PiGraph graph : this.preprocessor.getUniqueGraphSet()) {
      generateUniqueGraphCode(graph);
    }
  }

  /**
   * Generates cpp code for unique graph.
   * 
   * @param graph
   *          the graph
   */
  private final void generateUniqueGraphCode(final PiGraph graph) {
    /* Get the exact number of actors and edges there should be in the graph */
    int actorCount = graph.getActors().size() + graph.getFifosWithDelay().size();
    int edgeCount = graph.getFifos().size();
    for (final Fifo fifo : graph.getFifosWithDelay()) {
      if (!fifo.getDelay().hasGetterActor()) {
        actorCount += 1;
        edgeCount += 1;
      }
      if (!fifo.getDelay().hasSetterActor()) {
        actorCount += 1;
        edgeCount += 1;
      }
    }

    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("appName", this.applicationName);
    context.put("graphName", graph.getPiGraphName());
    context.put("actorCount", actorCount);
    context.put("edgeCount", edgeCount);
    context.put("paramCount", graph.getParameters().size());
    context.put("inputInterfaceCount", graph.getDataInputInterfaces().size());
    context.put("outputInterfaceCount", graph.getDataOutputInterfaces().size());
    context.put("cfgActorCount", graph.getActors().stream().filter(x -> !x.getConfigOutputPorts().isEmpty()).count());

    context.put("parameters", graph.getParameters());
    context.put("inheritedParameters", graph.getConfigInputInterfaces());
    context.put("staticParameters", this.preprocessor.getStaticParameters(graph));
    context.put("dependendStaticParameters", this.preprocessor.getStaticDependentParameters(graph));
    context.put("dynamicParameters", this.preprocessor.getDynamicParameters(graph));
    context.put("dependentDynamicParameters", this.preprocessor.getDynamicDependentParameters(graph));
    context.put("inputInterfaces", graph.getDataInputPorts());
    context.put("outputInterfaces", graph.getDataOutputPorts());
    context.put("actors", this.preprocessor.getActorSet(graph));
    context.put("subgraphsAndParameters", this.generateSubgraphsAndParametersList(graph));
    context.put("edges", this.preprocessor.getEdgeSet(graph));

    /* Write the final file */
    if (graph == this.applicationGraph) {
      writeVelocityContext(context, "templates/cpp/app_graph_template.vm", "spider2-application_graph.cpp");
    } else {
      final String outputFileName = "spider2-" + graph.getPiGraphName().toLowerCase() + "_subgraph" + ".cpp";
      writeVelocityContext(context, "templates/cpp/app_subgraph_template.vm", outputFileName);
    }
  }

  /**
   * Generates a name without '-' and spaces from a graph
   * 
   * @param graph
   *          the graph to evaluate
   * @return List of Pair of PiGraph and List of String.
   */
  private final List<Pair<PiGraph, List<String>>> generateSubgraphsAndParametersList(final PiGraph graph) {
    final List<Pair<PiGraph, List<String>>> subgraphsAndParameters = new ArrayList<>();
    for (final PiGraph subgraph : this.preprocessor.getSubgraphSet(graph)) {
      final List<String> parametersString = new ArrayList<>();
      for (final ConfigInputPort iCfg : subgraph.getConfigInputPorts()) {
        final boolean isLast = subgraph.getConfigInputPorts().indexOf(iCfg) == subgraph.getConfigInputPorts().size()
            - 1;
        String paramName = ((Parameter) (iCfg.getIncomingDependency().getSetter())).getName();
        if (!isLast) {
          paramName += ",";
        }
        parametersString.add(paramName);
      }
      subgraphsAndParameters.add(new Pair<>(subgraph, parametersString));
    }
    return subgraphsAndParameters;
  }

  /**
   * Writes a VelocityContext to a given file.
   * 
   * @param context
   *          the VelocityContext to write.
   * @param templateFileName
   *          The name of the template to use.
   * @param outputFileName
   *          The name of the output generated file.
   */
  private final void writeVelocityContext(final VelocityContext context, final String templateFileName,
      final String outputFileName) {
    try (Writer writer = new FileWriter(new File(this.folder, outputFileName))) {
      final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(templateFileName, this.getClass());
      velocityEngine.evaluate(context, writer, "org.apache.velocity", reader);
      writer.flush();
    } catch (IOException e) {
      end();
      throw new PreesmRuntimeException("Failed to open Spider2 codegen output file [" + outputFileName + "].", e);
    }
  }

  private final void copyFileFromTo(final String originalFolder, final String targetFolder, final String fileName) {
    try {
      final String content = PreesmResourcesHelper.getInstance().read(originalFolder + fileName, this.getClass());
      PreesmIOHelper.getInstance().print(targetFolder, fileName, content);
    } catch (final IOException e) {
      throw new PreesmRuntimeException(
          "Could not copy resource " + fileName + " from " + originalFolder + " to " + targetFolder + ".", e);
    }
  }

}

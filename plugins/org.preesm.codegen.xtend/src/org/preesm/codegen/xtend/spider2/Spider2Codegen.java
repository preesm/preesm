package org.preesm.codegen.xtend.spider2;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.math3.util.Pair;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.Path;
import org.preesm.codegen.xtend.spider2.utils.Spider2CodegenCluster;
import org.preesm.codegen.xtend.spider2.visitor.Spider2PreProcessVisitor;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.logger.PreesmLogger;
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
   * Initializes the context loader of the class.
   */
  public void init() {
    if (this.originalContextClassLoader != null) {
      return;
    }
    this.originalContextClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(Spider2Codegen.class.getClassLoader());
    this.velocityEngine = new VelocityEngine();
    this.velocityEngine.setProperty("resource.loader", "class");
    this.velocityEngine.setProperty("class.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    this.velocityEngine.init();
    this.velocityEngine.removeDirective("ifndef");
    this.velocityEngine.removeDirective("define");
    this.velocityEngine.removeDirective("endif");
  }

  /**
   * Finalizes properly original class loader attribute.
   */
  public void end() {
    if (this.originalContextClassLoader != null) {
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

        copyFileFromTo("resources/cmake_modules/FindSpider2.cmake", "../cmake/modules/FindSpider2.cmake");
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
    if (this.originalContextClassLoader == null) {
      init();
    }
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
    if (this.originalContextClassLoader == null) {
      init();
    }
    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("appName", this.applicationName);
    context.put("folder", this.folder.getName());

    /* Write the file */
    writeVelocityContext(context, "templates/cpp/cmakelist_template.vm", "../CMakeLists.txt");

    /* Move FindThreads and FindSpider2.cmake files */
    copyFileFromTo("resources/cmake_modules/FindSpider2.cmake", "../cmake/modules/FindSpider2.cmake");
    copyFileFromTo("resources/cmake_modules/FindThreads.cmake", "../cmake/modules/FindThreads.cmake");
  }

  /**
   * Generates the code of the main.cpp
   */
  public void generateMainCode() {
    if (this.originalContextClassLoader == null) {
      init();
    }

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
    context.put("runMode", "LOOP");
    context.put("loopCount", "10000");
    context.put("runtimeAlgo", "JITMS");
    context.put("schedAlgorithm", "LIST_BEST_FIT");

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
    if (this.originalContextClassLoader == null) {
      init();
    }

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
    if (this.originalContextClassLoader == null) {
      init();
    }

    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("appName", this.applicationName);
    final Set<PiGraph> graphSet = new HashSet<PiGraph>(this.preprocessor.getUniqueGraphSet());
    graphSet.remove(this.applicationGraph);
    context.put("graphs", graphSet);
    context.put("fileNames", this.preprocessor.getUniqueHeaderFileNameList());
    context.put("prototypes", this.preprocessor.getUniqueLoopPrototypeList());
    context.put("clusters", this.preprocessor.getClusterList());

    /* Write the file */
    writeVelocityContext(context, "templates/cpp/app_header_template.vm", "spider2-application.h");
  }

  /**
   * Generates CPP code for every unique pisdf graph of the application.
   */
  public void generateGraphCodes() {
    if (this.originalContextClassLoader == null) {
      init();
    }

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
      final String outputFileName = graph.getPiGraphName().toLowerCase() + "_subgraph" + ".cpp";
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
      final URL template = PreesmResourcesHelper.getInstance().resolve(templateFileName, this.getClass());

      try (final InputStreamReader reader = new InputStreamReader(template.openStream())) {
        velocityEngine.evaluate(context, writer, "org.apache.velocity", reader);
        writer.flush();
      } catch (IOException e) {
        end();
        throw new PreesmRuntimeException("Could not locate main template [" + template.getFile() + "].", e);
      }

    } catch (IOException e) {
      end();
      PreesmLogger.getLogger().log(Level.SEVERE, "failed to open output file [" + outputFileName + "].");
      PreesmLogger.getLogger().log(Level.SEVERE, e.toString());
    }
  }

  private final void copyFileFromTo(final String originalFile, final String targetFile) {
    final URL fileuRL = PreesmResourcesHelper.getInstance().resolve(originalFile, this.getClass());

    try (Writer writer = new FileWriter(new File(this.folder, targetFile))) {
      try (final BufferedReader reader = new BufferedReader(new InputStreamReader(fileuRL.openStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          writer.write(line + '\n');
        }
        writer.flush();
      } catch (IOException e) {
        throw new PreesmRuntimeException(e);
      }
    } catch (IOException e) {
      throw new PreesmRuntimeException(e);
    }
  }

}

package org.preesm.codegen.xtend.spider2;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.Path;
import org.preesm.codegen.xtend.spider2.visitor.Spider2PreProcessVisitor;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * The Class Spider2Codegen.
 */
public class Spider2Codegen {
  /** The scenario. */
  private final Scenario scenario;

  /** The architecture */
  private final Design architecture;

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
   * @param architecture
   *          the architecture
   * @param applicationGraph
   *          the main application graph
   */
  public Spider2Codegen(final Scenario scenario, final Design architecture, final PiGraph applicationGraph,
      final File folder) {
    this.scenario = scenario;
    this.architecture = architecture;
    this.applicationGraph = applicationGraph;
    this.folder = folder;
    /** Calls the pre-processor */
    this.preprocessor = new Spider2PreProcessVisitor(this.scenario);
    this.preprocessor.doSwitch(applicationGraph);
    this.applicationName = applicationGraph.getPiGraphName().toLowerCase();
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
    this.velocityEngine.init();
    this.velocityEngine.removeDirective("ifndef");
    this.velocityEngine.removeDirective("define");
    this.velocityEngine.removeDirective("endif");
    this.velocityEngine.removeDirective("include");
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

  public void makeIncludeAndSourceFolder(final IWorkspace workspace, final String path) {
    final String codegenDirectoryPath = path.substring(0, path.lastIndexOf('/', path.length() - 2) + 1);
    final IFolder fSrc = workspace.getRoot().getFolder(new Path(codegenDirectoryPath + "src/"));
    final File folderSrc = new File(fSrc.getRawLocation().toOSString());
    folderSrc.mkdirs();
    if (folderSrc.isDirectory()) {
      /* Rename the .c to .cpp if needed */
      renameFilesToCPPInFolder(folderSrc);
    }
    final IFolder fInclude = workspace.getRoot().getFolder(new Path(codegenDirectoryPath + "include/"));
    final File folderInclude = new File(fInclude.getRawLocation().toOSString());
    folderInclude.mkdirs();

    final IFolder fLib = workspace.getRoot().getFolder(new Path(codegenDirectoryPath + "lib/"));
    final File folderLib = new File(fLib.getRawLocation().toOSString());
    folderLib.mkdirs();

    final IFolder fCMake = workspace.getRoot().getFolder(new Path(codegenDirectoryPath + "cmake/modules"));
    final File folderCMake = new File(fCMake.getRawLocation().toOSString());
    folderCMake.mkdirs();
  }

  public void moveIncludesToFolder(final IWorkspace workspace, final String path) {
    final String codegenDirectoryPath = path.substring(0, path.lastIndexOf('/', path.length() - 2) + 1);
    final String includeDirectoryPath = codegenDirectoryPath + "include/";
    final IFolder fInclude = workspace.getRoot().getFolder(new Path(includeDirectoryPath));
    final File folderInclude = new File(fInclude.getRawLocation().toOSString());
    folderInclude.mkdirs();
    if (folderInclude.isDirectory()) {
      /* Fetch the refinements and move the header files into the include folder */
      for (final CHeaderRefinement refinement : this.preprocessor.getUniqueLoopHeaderList()) {
        final String refFilePath = refinement.getFilePath();
        final File oldFile = new File(
            workspace.getRoot().getFolder(new Path(refFilePath)).getRawLocation().toOSString());
        final File newFile = new File(workspace.getRoot()
            .getFolder(new Path(includeDirectoryPath + refinement.getFileName())).getRawLocation().toOSString());
        try {
          Files.copy(oldFile.toPath(), newFile.toPath(), REPLACE_EXISTING);
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

  public void generateCMakeList() {
    if (this.originalContextClassLoader == null) {
      init();
    }
    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("appName", this.applicationName);

    /* Write the file */
    final String outputFileName = "../CMakeLists.txt";
    writeVelocityContext(context, "templates/cpp/cmakelist_template.vm", outputFileName);
  }

  public void generateMainCode() {
    if (this.originalContextClassLoader == null) {
      init();
    }

    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("verbose", true);
    context.put("standalone", false);
    context.put("papify", false);
    context.put("apollo", false);
    context.put("genlog", true);
    context.put("clusterIx", "SIZE_MAX");
    context.put("genAllocPolicy", false);
    context.put("genAllocAlign", "sizeof(int64_t)");
    context.put("genAllocSize", "SIZE_MAX");
    context.put("genAllocExtAddr", "nullptr");
    context.put("runMode", "LOOP");
    context.put("loopCount", "10000");
    context.put("runtimeAlgo", "JITMS");

    /* Write the file */
    final String outputFileName = "main.cpp";
    writeVelocityContext(context, "templates/cpp/app_main_template.vm", outputFileName);

  }

  public void generateKernelHeader() {
    if (this.originalContextClassLoader == null) {
      init();
    }

    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("appName", this.applicationName);
    final List<CHeaderRefinement> refinements = this.preprocessor.getUniqueLoopHeaderList();
    final Set<String> fileNames = new HashSet<>();
    refinements.forEach(x -> fileNames.add(x.getFileName()));
    context.put("fileNames", fileNames);
    context.put("prototypes", this.preprocessor.getUniqueLoopPrototypeList());

    /* Write the file */
    final String outputFileName = "spider2-application-kernels.h";
    writeVelocityContext(context, "templates/cpp/app_kernels_template.vm", outputFileName);
  }

  public void generateApplicationHeader() {
    if (this.originalContextClassLoader == null) {
      init();
    }

    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("appName", this.applicationName);
    final Set<PiGraph> graphSet = this.preprocessor.getUniqueGraphSet();
    graphSet.remove(this.applicationGraph);
    context.put("graphs", this.preprocessor.getUniqueGraphSet());

    /* Write the file */
    final String outputFileName = "spider2-application.h";
    writeVelocityContext(context, "templates/cpp/app_header_template.vm", outputFileName);
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
    context.put("refinements", this.preprocessor.getUniqueLoopHeaderList());
    context.put("actors", this.preprocessor.getActorSet(graph));
    context.put("subgraphsAndParameters", this.generateSubgraphsAndParametersList(graph));
    context.put("edges", this.preprocessor.getEdgeSet(graph));

    /* Write the final file */
    if (graph == this.applicationGraph) {
      writeVelocityContext(context, "templates/cpp/app_graph_template.vm", "application_graph.cpp");
    } else {
      final String outputFileName = graph.getPiGraphName().toLowerCase() + "_subgraph" + ".cpp";
      writeVelocityContext(context, "templates/cpp/graph_template.vm", outputFileName);
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
      final URL graphCodeTemplate = PreesmResourcesHelper.getInstance().resolve(templateFileName, this.getClass());

      try (final InputStreamReader reader = new InputStreamReader(graphCodeTemplate.openStream())) {
        velocityEngine.evaluate(context, writer, "org.apache.velocity", reader);
        writer.flush();
      } catch (IOException e) {
        end();
        throw new PreesmRuntimeException("Could not locate main template [" + graphCodeTemplate.getFile() + "].", e);
      }

    } catch (IOException e) {
      end();
      PreesmLogger.getLogger().log(Level.SEVERE, "failed to open output file [" + outputFileName + "].");
      PreesmLogger.getLogger().log(Level.SEVERE, e.toString());
    }
  }

}

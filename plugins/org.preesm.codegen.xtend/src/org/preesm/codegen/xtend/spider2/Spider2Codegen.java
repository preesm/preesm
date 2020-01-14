package org.preesm.codegen.xtend.spider2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.preesm.codegen.xtend.spider2.visitor.Spider2PreProcessVisitor;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.logger.PreesmLogger;
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
  private final Spider2PreProcessVisitor preprocessor = new Spider2PreProcessVisitor();

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
    this.applicationName = generateGraphName(applicationGraph);
    this.folder = folder;
    /** Calls the pre-processor */
    this.preprocessor.doSwitch(applicationGraph);
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

  public void generateApplicationHeader() {
    if (this.originalContextClassLoader == null) {
      init();
    }

    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("appName", this.applicationName);
    final Set<PiGraph> graphSet = this.preprocessor.getUniqueGraphSet();
    graphSet.remove(this.applicationGraph);
    context.put("graphs", graphSet);

    /* Write the file */
    final String outputFileName = "spider2-application-" + this.applicationName + ".h";
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

    /* Get the clean graph name */
    final String graphName = generateGraphName(graph);

    /* Build the actor string */
    final StringBuilder cppActorsString = new StringBuilder();

    /* Build the edge string */
    final StringBuilder cppEdgesString = new StringBuilder();

    /* Build the edge string */
    final StringBuilder cppDelaysString = new StringBuilder();

    /* Fill out the context */
    VelocityContext context = new VelocityContext();
    context.put("appName", this.applicationName);
    context.put("graphName", graphName);
    context.put("actorCount", graph.getActors().size());
    context.put("edgeCount", graph.getFifos().size());
    context.put("paramCount", graph.getParameters().size());
    context.put("inputInterfaceCount", graph.getDataInputInterfaces().size());
    context.put("outputInterfaceCount", graph.getDataOutputInterfaces().size());
    context.put("cfgActorCount", graph.getActors().stream().filter(x -> !x.getConfigOutputPorts().isEmpty()).count());

    /* Fill the parameters to the context */
    context.put("parameters", graph.getParameters());
    context.put("inheritedParameters", graph.getConfigInputInterfaces());
    context.put("staticParameters", this.preprocessor.getStaticParameters(graph));
    context.put("dependendStaticParameters", this.preprocessor.getStaticDependentParameters(graph));
    context.put("dynamicParameters", this.preprocessor.getDynamicParameters(graph));
    context.put("dependentDynamicParameters", this.preprocessor.getDynamicDependentParameters(graph));
    context.put("inputInterfaces", graph.getDataInputInterfaces());
    context.put("outputInterfaces", graph.getDataOutputInterfaces());
    context.put("actors", graph.getActors());
    context.put("edges", graph.getFifos());
    context.put("delays", cppDelaysString);

    /* Write the final file */
    final String outputFileName = "graph_" + graphName + ".cpp";
    writeVelocityContext(context, "templates/cpp/graph_template.vm", outputFileName);
  }

  /**
   * Generates a name without '-' and spaces from a graph
   * 
   * @param graph
   *          the graph to evaluate
   * @return clean name
   */
  private static final String generateGraphName(final PiGraph graph) {
    String name = graph.getName();
    name = name.replace('-', '_');
    name = name.replace(" ", "");
    return name;
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

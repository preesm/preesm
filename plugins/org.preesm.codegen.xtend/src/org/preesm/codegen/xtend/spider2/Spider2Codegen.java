package org.preesm.codegen.xtend.spider2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.preesm.codegen.xtend.spider2.visitor.Spider2PreProcessVisitor;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.ConfigInputInterface;
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
  final Design architecture;

  /** The application graph */
  final PiGraph applicationGraph;

  /** The Spider2PreProcessor **/
  private final Spider2PreProcessVisitor preprocessor = new Spider2PreProcessVisitor();

  /** The application name **/
  private final String applicationName;

  /** The codegen folder */
  final File folder;

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
    // 0- without the following class loader initialization, I get the following exception when running as Eclipse
    // plugin:
    // org.apache.velocity.exception.VelocityException: The specified class for ResourceManager
    // (org.apache.velocity.runtime.resource.ResourceManagerImpl) does not implement
    // org.apache.velocity.runtime.resource.ResourceManager; Velocity is not initialized correctly.
    final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(Spider2Codegen.class.getClassLoader());
    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.init();

    velocityEngine.removeDirective("include");

    /* Get the clean graph name */
    final String graphName = generateGraphName(graph);

    /* Build the parameter string */
    for (final ConfigInputInterface cif : graph.getConfigInputInterfaces()) {
      cif.getGraphPort().getIncomingDependency().getGetter();
    }

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
    fillGraphContextParameters(context, graph);
    context.put("actors", cppActorsString);
    context.put("edges", cppEdgesString);
    context.put("delays", cppDelaysString);

    /* Write the final file */
    final String outputFile = "graph_" + graphName + ".cpp";
    try (Writer writer = new FileWriter(new File(this.folder, outputFile))) {
      final URL graphCodeTemplate = PreesmResourcesHelper.getInstance().resolve("templates/cpp/graph_template.vm",
          this.getClass());

      try (final InputStreamReader reader = new InputStreamReader(graphCodeTemplate.openStream())) {
        velocityEngine.evaluate(context, writer, "org.apache.velocity", reader);
        writer.flush();
      } catch (IOException e) {
        throw new PreesmRuntimeException("Could not locate main template [" + graphCodeTemplate.getFile() + "].", e);
      }

    } catch (IOException e) {
      PreesmLogger.getLogger().log(Level.SEVERE, "failed to open output file for graph [" + graph.getName() + "].");
      PreesmLogger.getLogger().log(Level.SEVERE, e.toString());
    } finally {
      /* 99- set back default class loader */
      Thread.currentThread().setContextClassLoader(oldContextClassLoader);
    }
  }

  private void fillGraphContextParameters(VelocityContext context, final PiGraph graph) {
    final List<Parameter> staticParametersList = graph.getParameters().stream()
        .filter(x -> !x.isDependent() && x.isLocallyStatic()).collect(Collectors.toList());
    /* We need to sort the dependent parameters in order of their dependencies */
    final List<Parameter> staticParameterPool = graph.getParameters().stream()
        .filter(x -> x.isDependent() && x.isLocallyStatic()).collect(Collectors.toList());
    final Set<Parameter> dependendStaticParametersSet = getOrderedDependentParameter(staticParametersList,
        staticParameterPool);
    /* We need to sort the dependent parameters in order of their dependencies */
    final List<Parameter> dynamicParametersList = graph.getParameters().stream().filter(x -> x.isConfigurable())
        .collect(Collectors.toList());
    final List<Parameter> dynamicParameterPool = graph.getParameters().stream()
        .filter(x -> !x.isLocallyStatic() && x.isDependent()).collect(Collectors.toList());
    final Set<Parameter> dependentDynamicParametersSet = getOrderedDependentParameter(dynamicParametersList,
        dynamicParameterPool);
    context.put("inheritedParameters", graph.getConfigInputInterfaces());
    context.put("staticParameters", staticParametersList);
    context.put("dependendStaticParameters", dependendStaticParametersSet);
    context.put("dynamicParameters", dynamicParametersList);
    context.put("dependentDynamicParameters", dependentDynamicParametersSet);
  }

  /**
   * Build an ordered Set of Parameter enforcing the dependency tree structure of the PiMM.
   * 
   * @param initList
   *          Seed list corresponding to root parameters.
   * @param paramPoolList
   *          Pool of parameter to use to sort (will be empty after the function call).
   * @return set of parameter.
   */
  private Set<Parameter> getOrderedDependentParameter(final List<Parameter> initList, List<Parameter> paramPoolList) {
    final Set<Parameter> dependentParametersSet = new LinkedHashSet<>(initList);
    while (!paramPoolList.isEmpty()) {
      /* Get only the parameter that can be added to the current stage due to their dependencies */
      final List<Parameter> nextParamsToAddList = paramPoolList
          .stream().filter(x -> x.getInputDependentParameters().stream()
              .filter(in -> dependentParametersSet.contains(in)).count() == x.getInputDependentParameters().size())
          .collect(Collectors.toList());
      dependentParametersSet.addAll(nextParamsToAddList);
      paramPoolList.removeAll(nextParamsToAddList);
    }
    /* Remove init list from the set */
    dependentParametersSet.removeAll(initList);
    return dependentParametersSet;
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

}

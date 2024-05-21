package org.preesm.algorithm.clustering.scape;

import java.io.File;
import java.util.Date;
import java.util.List;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.schedule.model.ScapeBuilder;
import org.preesm.algorithm.schedule.model.ScapeSchedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;

/**
 * This class generates additional .c and .h files for the identified cluster.
 *
 * @author orenaud
 *
 */
public class CodegenScape {

  public CodegenScape(final Scenario scenario, final PiGraph subGraph, List<ScapeSchedule> schedule, Long stackSize) {
    // print C file
    final String nameGraph = (subGraph.getContainingPiGraph() != null) ? subGraph.getContainingPiGraph().getName()
        : subGraph.getName();
    final String clusterName = "/Cluster_" + nameGraph + "_" + subGraph.getName();
    final String clusterPath = scenario.getCodegenDirectory() + File.separator;
    final String cfile = clusterName + ".c";

    final ScapeBuilder build = ScheduleFactory.eINSTANCE.createScapeBuilder();
    new CodegenScapeBuilder(build, schedule, subGraph, stackSize);

    final StringBuilder clusterCContent = buildCContent(build, subGraph);
    PreesmIOHelper.getInstance().print(clusterPath, cfile, clusterCContent);
    // print H file
    final String hfile = clusterName + ".h";
    final StringBuilder clusterHContent = buildHContent(build, subGraph);
    PreesmIOHelper.getInstance().print(clusterPath, hfile, clusterHContent);

    // Ewen Cu File

    for (final ScapeSchedule sche : schedule) {
      sche.setOnGPU(true);
    }

    final ScapeBuilder build2 = ScheduleFactory.eINSTANCE.createScapeBuilder();
    new CodegenScapeBuilder(build2, schedule, subGraph, stackSize);

    final String cufile = clusterName + ".cu";
    final StringConcatenation clusterCuContent = buildCuContent(build2, subGraph);
    PreesmIOHelper.getInstance().print(clusterPath, cufile, clusterCuContent);
  }

  /**
   * The .h file contains function prototypes associated with the actors contained in the cluster.
   *
   * @param build
   *          Clustering structure
   * @param subGraph
   *          Graph to consider.
   * @return The string content of the .h file.
   */
  private StringBuilder buildHContent(ScapeBuilder build, PiGraph subGraph) {

    final StringBuilder result = new StringBuilder();

    result.append(header(subGraph));
    final String nodeId = nodeIdentifier(subGraph);

    result.append("#include \"preesm_gen" + nodeId + ".h\"\n");
    final String upper = subGraph.getName().toUpperCase() + "_H";
    result.append("#ifndef " + upper + "\n");
    result.append("#define " + upper + "\n");
    result.append(build.getInitFunc() + ";\n");
    result.append(build.getLoopFunc() + ";\n");
    for (final AbstractActor actor : subGraph.getOnlyActors()) {
      if (actor instanceof Actor) {
        final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) actor).getRefinement());
        result.append("#include \"" + cHeaderRefinement.getFileName() + "\" \n\n");
      }
    }

    result.append("#endif \n");
    return result;
  }

  private String nodeIdentifier(PiGraph subGraph) {
    PiGraph tempg = subGraph;
    while (tempg.getContainingPiGraph() != null) {
      tempg = tempg.getContainingPiGraph();

    }
    if (tempg.getName().contains("sub")) {
      return tempg.getName().replace("sub", "");
    }

    return "";
  }

  /**
   * The .c file contains scheduled functions call associated with the actors contained in the cluster.
   *
   * @param build
   *          Clustering structure
   * @param subGraph
   *          Graph to consider.
   * @return The string content of the .c file.
   */
  private StringBuilder buildCContent(ScapeBuilder build, PiGraph subGraph) {

    final StringBuilder result = new StringBuilder();
    result.append(header(subGraph));
    final String nameGraph = (subGraph.getContainingPiGraph() != null) ? subGraph.getContainingPiGraph().getName()
        : subGraph.getName();
    result.append("#include \"Cluster_" + nameGraph + "_" + subGraph.getName() + ".h\"\n\n");
    final String initFunc = build.getInitFunc();
    result.append(initFunc + "{\n");
    for (final AbstractActor actor : subGraph.getOnlyActors()) {
      if (actor instanceof Actor) {
        final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) actor).getRefinement());
        if (cHeaderRefinement.getInitPrototype() != null) {
          result.append(cHeaderRefinement.getInitPrototype().getName() + "();\n\n");
        }
      }
    }

    result.append("}\n");

    final String loopFunc = build.getLoopFunc();
    result.append(loopFunc + "{\n\n");

    result.append("// buffer declaration\n\n");

    for (final String buffer : build.getBuffer()) {
      result.append(buffer + "\n");
    }

    result.append("// body \n");
    final String body = build.getBody();
    result.append(body + "\n\n");

    result.append("// free buffer\n");

    for (final String buffer : build.getDynmicBuffer()) {
      final String buff = buffer;
      result.append("free(" + buff + ");\n");
    }

    result.append("}\n");
    return result;
  }

  /**
   * The .cu file contains scheduled functions call associated with the GPU actors contained in the cluster. Ewen
   *
   * @param build
   *          Clustering structure
   * @param subGraph
   *          Graph to consider.
   * @return The string content of the .cu file.
   */
  private StringConcatenation buildCuContent(ScapeBuilder build, PiGraph subGraph) {

    final StringConcatenation result = new StringConcatenation();

    result.append(header(subGraph));
    result.append("#include " + "\"Cluster_" + subGraph.getContainingPiGraph().getName() + "_" + subGraph.getName()
        + ".h\" \n\n");
    final String initFunc = build.getInitFunc();
    result.append(initFunc + "{\n ", "");
    for (final AbstractActor actor : subGraph.getOnlyActors()) {
      if (actor instanceof Actor) {
        final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) actor).getRefinement());
        if (cHeaderRefinement.getInitPrototype() != null) {
          result.append(cHeaderRefinement.getInitPrototype().getName() + "(); \n\n", "");
        }
      }
    }

    result.append("}\n ", "");

    final String loopFunc = build.getLoopFunc();
    result.append(loopFunc + "{ \n\n", "");

    result.append("// buffer declaration \n\n ", "");
    for (final String buffer : build.getBuffer()) {
      result.append(buffer + "\n ", "");
    }
    for (final String buffer : build.getDynmicBuffer()) {
      result.append(buffer + "\n ", "");
    }

    result.append("// body \n ", "");
    final String body = build.getBody();
    result.append(body + "\n\n ", "");

    result.append("}\n", "");

    return result;
  }

  /**
   * The header file contains file information.
   *
   * @param subGraph
   *          Graph to consider.
   * @return The string content of the header file.
   */
  private StringBuilder header(PiGraph subGraph) {
    final StringBuilder result = new StringBuilder();
    result.append("/**\n");
    final String nameGraph = (subGraph.getContainingPiGraph() != null) ? subGraph.getContainingPiGraph().getName()
        : subGraph.getName();
    result.append("* @file /Cluster_" + nameGraph + "_" + subGraph.getName() + ".c/h\n");
    result.append("* @generated by " + this.getClass().getSimpleName() + "\n");
    result.append("* @date " + new Date() + "\n");
    result.append("*/\n\n");
    return result;
  }

}

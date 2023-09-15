package org.preesm.algorithm.clustering.scape;

import java.io.File;
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

public class CodegenScape {
  public CodegenScape(final Scenario scenario, final PiGraph subGraph, List<ScapeSchedule> schedule, Long stackSize) {
    // print C file
    final String clusterPath = scenario.getCodegenDirectory() + File.separator;
    final String cfile = subGraph.getName() + ".c";

    final ScapeBuilder build = ScheduleFactory.eINSTANCE.createScapeBuilder();
    new CodegenScapeBuilder(build, schedule, subGraph, stackSize);

    final StringConcatenation clusterCContent = buildCContent(build, subGraph);
    PreesmIOHelper.getInstance().print(clusterPath, cfile, clusterCContent);
    // print H file
    final String hfile = subGraph.getName() + ".h";
    final StringConcatenation clusterHContent = buildHContent(build, subGraph.getName());
    PreesmIOHelper.getInstance().print(clusterPath, hfile, clusterHContent);
  }

  private StringConcatenation buildHContent(ScapeBuilder build, String name) {
    final StringConcatenation result = new StringConcatenation();
    final String upper = name.toUpperCase() + "_H";
    result.append("#ifndef " + upper + "\n", "");
    result.append("#define " + upper + "\n", "");
    result.append(build.getInitFunc() + ";\n ", "");
    result.append(build.getLoopFunc() + ";\n ", "");
    result.append("#endif \n", "");
    return result;
  }

  private StringConcatenation buildCContent(ScapeBuilder build, PiGraph subGraph) {

    final StringConcatenation result = new StringConcatenation();
    final String initFunc = build.getInitFunc();
    result.append(initFunc + "{\n ", "");
    for (final AbstractActor actor : subGraph.getOnlyActors()) {
      final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) actor).getRefinement());
      result.append(cHeaderRefinement.getInitPrototype().getName() + "; \n", "");

    }

    result.append("}\n ", "");

    final String loopFunc = build.getLoopFunc();
    result.append(loopFunc + " ", "");

    result.append("\\ buffer declaration \n ", "");
    for (final String buffer : build.getBuffer()) {
      result.append(buffer + "\n ", "");
    }
    result.append("\\ body \n ", "");
    final String body = build.getBody();
    result.append(body + "\n ", "");

    result.append("\\ free buffer  \n ", "");
    for (final String buffer : build.getBuffer()) {
      result.append(buffer + "\n ", "");
    }

    result.append("}\n", "");
    return result;
  }

}

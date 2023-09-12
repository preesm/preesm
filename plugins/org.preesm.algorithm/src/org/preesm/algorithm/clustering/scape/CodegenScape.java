package org.preesm.algorithm.clustering.scape;

import java.io.File;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.schedule.model.ScapeBuilder;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;

public class CodegenScape {
  public CodegenScape(final Scenario scenario, final PiGraph subGraph, String cluster, Long stackSize) {

    final String clusterPath = scenario.getCodegenDirectory() + File.separator;
    final String clusterName = subGraph.getName() + ".c";

    final ScapeBuilder build = ScheduleFactory.eINSTANCE.createScapeBuilder();
    new CodegenScapeBuilder(build, cluster, subGraph, stackSize);

    final StringConcatenation clusterContent = buildContent(build);
    PreesmIOHelper.getInstance().print(clusterPath, clusterName, clusterContent);
  }

  private StringConcatenation buildContent(ScapeBuilder build) {

    final StringConcatenation result = new StringConcatenation();
    final String initFunc = build.getInitFunc();
    result.append(initFunc + "{\n ", "");
    // TODO integer content
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

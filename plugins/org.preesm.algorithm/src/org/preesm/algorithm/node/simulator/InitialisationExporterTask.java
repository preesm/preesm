package org.preesm.algorithm.node.simulator;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.ui.stats.StatGeneratorAbc;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This class represents a PREESM task responsible for exporting simulation metrics to a CSV file. It is specifically
 * designed for the initialization phase, exporting stats for a single core scheduling.
 *
 * The task takes input in the form of LatencyAbc and exports key metrics such as Final Latency, Memory usage, Energy,
 * and Cost to a CSV file named "initialisation.csv" in the project's Simulation directory.
 *
 * Note: This class assumes a specific project structure with a Simulation directory.
 *
 * @author orenaud
 */
@PreesmTask(id = "InitialisationExporterTask.identifier", name = "Initialisation Stats exporter",
    category = "CSV exporters", inputs = { @Port(name = "ABC", type = LatencyAbc.class) },
    shortDescription = "This task exports simulation metric on one core as a *.csv file .")

public class InitialisationExporterTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    // Extract project name and initialize path
    final String path = File.separator + workflow.getProjectName() + "/Simulation/";

    // Extract LatencyAbc input
    final LatencyAbc abc = (LatencyAbc) inputs.get("ABC");

    // Retrieve final latency and initialize memory
    final long lat = abc.getFinalLatency();
    long mem = 0L;

    // Initialize CSV content with header
    String content = "Final Latency;Memory;Energy;Cost; Max speedup \n";

    // Generate stats for each operator component instance
    final StatGeneratorAbc stat = new StatGeneratorAbc(abc);
    for (final ComponentInstance ci : abc.getArchitecture().getOperatorComponentInstances()) {
      mem += stat.getMem(ci);
    }
    final Long speed = stat.getDAGWorkLength() / stat.getDAGSpanLength();
    // Append metrics to CSV content
    content += lat + ";" + mem + ";" + 1 + ";" + 1 + ";" + speed;

    // Export content to CSV file
    PreesmIOHelper.getInstance().print(path, "initialisation.csv", content);
    pigraphInit("/" + workflow.getProjectName() + "/Algo/generated/top/");
    ganttInit(lat, "/" + workflow.getProjectName() + "/Algo/generated/top/");
    plateformInit("/" + workflow.getProjectName() + "/Archi/");
    // Return an empty map since no output is produced by this task
    return new LinkedHashMap<>();
  }

  private void plateformInit(String path) {
    final String content = """
        <!-- Cluster with crossbar:1:1:1 -->
        <?xml version='1.0'?>
        <!DOCTYPE platform SYSTEM "https://simgrid.org/simgrid.dtd">
        <platform version="4.1">
          <zone id="my zone" routing="Floyd">
            <cluster id="Cluster with crossbar" prefix="Node" radical="0" suffix="" speed="1f" bw="125MBps" lat="50us">
              <prop id="wattage_per_state" value="90.0:90.0:150.0"/>
              <prop id="wattage_range" value="100.0:200.0"/>
            </cluster>
          </zone>
        </platform>
        """;
    PreesmIOHelper.getInstance().print(path, "SimSDP_network.xml", content);
  }

  private void ganttInit(long lat, String path) {
    final String content = """
        <?xml version="1.0" encoding="UTF-8" standalone="no"?>
        <data>
            <event color="#c896fa" end="%d" mapping="Node0" start="0" title="top_0 (x1)">Step_top_0_(x1).</event>
            <perfs impl_length="%d" impl_nbCores="1" impl_nbUsedCores="1" span="%d" work="%d">
                <core id="Node0" load="100" used_mem="0">Core_Node0.</core>
            </perfs>
        </data>
        """.formatted(lat, lat, lat, lat);
    PreesmIOHelper.getInstance().print(path, "top_top_stats_pgantt.xml", content);
  }

  private void pigraphInit(String path) {
    final String content = """
        <?xml version="1.0" encoding="UTF-8"?>
        <graphml xmlns="http://graphml.graphdrawing.org/xmlns">
            <key attr.name="parameters" for="graph" id="parameters"/>
            <key attr.name="variables" for="graph" id="variables"/>
            <key attr.name="arguments" for="node" id="arguments"/>
            <key attr.name="name" attr.type="string" for="graph"/>
            <graph edgedefault="directed">
                <data key="name">init</data>
                <node id="top" kind="actor"/>
            </graph>
        </graphml>
        """;
    PreesmIOHelper.getInstance().print(path, "top.pi", content);
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Generate the initial stats of a single core scheduling.";
  }

}

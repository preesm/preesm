package org.preesm.algorithm.node.partitioner;

import java.io.File;
import java.util.Map;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;

public class CodegenSimSDP {
  public CodegenSimSDP(final Scenario scenario, final PiGraph topGraph, Map<Long, String> nodeNames) {

    final String topPath = scenario.getCodegenDirectory() + File.separator;
    final String fileName = "mainSimSDP.c";

    final StringConcatenation content = buildContent(topGraph, nodeNames);
    PreesmIOHelper.getInstance().print(topPath, fileName, content);

    PreesmLogger.getLogger().info("mainSimSDP.c file print in : " + topPath);
  }

  private StringConcatenation buildContent(PiGraph topGraph, Map<Long, String> nodeNames) {
    final StringConcatenation result = new StringConcatenation();
    result.append(init(topGraph));
    result.append(initNode(nodeNames, topGraph));
    result.append(buffer(topGraph));
    result.append(mpiEnv());
    result.append(exeNode(topGraph));
    return result;
  }

  private StringConcatenation exeNode(PiGraph topGraph) {
    StringConcatenation result = new StringConcatenation();
    for (final AbstractActor node : topGraph.getOnlyActors()) {
      if (!(node instanceof Actor)) {
        continue;
      }
      final String indexStr = node.getName().replace("sub", "");
      final Long indexL = Long.decode(indexStr);
      result.append("if (rank ==" + indexL + "){ \n");
      for (final DataInputPort din : node.getDataInputPorts()) {
        result.append("MPI_Recv(" + din.getFifo().getSourcePort().getName() + "," + din.getExpression().evaluate() + ","
            + "MPI_" + din.getFifo().getType().toUpperCase() + "," + (indexL - 1)
            + ", MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);\n");
      }
      result.append(node.getName() + "(");
      // for (final ConfigInputPort cfg : node.getConfigInputPorts()) {
      // result.append(cfg.getName() + ",");
      // }
      for (final DataInputPort din : node.getDataInputPorts()) {
        result.append(din.getFifo().getSourcePort().getName() + ",");
      }
      for (final DataOutputPort dout : node.getDataOutputPorts()) {
        result.append(dout.getName() + ",");
      }
      String temp = result.toString();
      if (result.toString().endsWith(",")) {
        temp = result.toString().substring(0, result.length() - 1);
      }
      result = new StringConcatenation();
      result.append(temp + ");\n");

      for (final DataOutputPort dout : node.getDataOutputPorts()) {
        result.append("MPI_Ssend(" + dout.getName() + "," + dout.getExpression().evaluate() + "," + "MPI_"
            + dout.getFifo().getType().toUpperCase() + "," + (indexL + 1) + ",0, MPI_COMM_WORLD);\n");
      }
      result.append("initNode" + indexL + " = 0;\n }\n");

    }
    result.append("}\n");
    for (final AbstractActor node : topGraph.getOnlyActors()) {
      if (node instanceof Actor) {
        for (final DataOutputPort dout : node.getDataOutputPorts()) {
          result.append("MPI_Free_mem(" + dout.getName() + ");\n");

        }
      }
    }
    result.append("MPI_Finalize();\n }");

    return result;
  }

  private StringConcatenation mpiEnv() {
    final StringConcatenation result = new StringConcatenation();
    result.append("// Get the name of the processor \n");
    result.append("char processor_name[MPI_MAX_PROCESSOR_NAME]; \n");
    result.append("int name_len; \n");
    // result.append("int rank = 0; \n");

    result.append("MPI_Get_processor_name(processor_name, &name_len); \n");
    result.append("int rank = -1; \n");
    result.append("for (int index = 0; index < sizeof(nodeset) / sizeof(nodeset[0]); index++) { \n");
    result.append("if (strcmp(processor_name, nodeset[index]) == 0) { \n");
    result.append("rank = index; \n");
    result.append("break;  // If the match is found, exit the loop\n");
    result.append("} \n } \n");

    result.append("if (rank != -1) { \n");
    result.append("printf(\"Processor name %s found at rank %d\\n\", processor_name, rank); \n");
    result.append("} else { \n");
    result.append("printf(\"Processor name %s not found in nodeset\\n\", processor_name); \n");
    result.append("} \n");

    result.append("for(int index=0; index< MPI_LOOP_SIZE;index++) { \n");
    return result;
  }

  private StringConcatenation buffer(PiGraph topGraph) {
    final StringConcatenation result = new StringConcatenation();
    result.append("//Allocate buffers in distributed memory \n");
    for (final AbstractActor node : topGraph.getOnlyActors()) {
      if (node instanceof Actor) {
        for (final DataOutputPort dout : node.getDataOutputPorts()) {
          result.append(dout.getFifo().getType() + " *" + dout.getName() + ";\n");
          result.append("MPI_Alloc_mem(" + dout.getExpression().evaluate() + " * sizeof(" + dout.getFifo().getType()
              + "), MPI_INFO_NULL, &" + dout.getName() + "); \n");
        }
      }
    }
    return result;
  }

  private StringConcatenation init(PiGraph topGraph) {
    final StringConcatenation result = new StringConcatenation();
    result.append("#include <stdio.h> \n");
    result.append("#include <mpi.h> \n");
    result.append("#include \"stdlib.h\" \n");

    for (int i = 0; i < topGraph.getExecutableActors().size(); i++) {
      result.append("#include \"sub" + i + "/preesm_gen" + i + ".h\" \n");
      result.append("#include \"sub" + i + "/sub" + i + ".h\" \n");
    }
    result.append("#define MPI_LOOP_SIZE 1 \n\n");
    return result;
  }

  private StringConcatenation initNode(Map<Long, String> nodeNames, PiGraph topGraph) {
    final StringConcatenation result = new StringConcatenation();
    result.append("int MPIStopNode = 0;\n");
    for (int i = 0; i < topGraph.getExecutableActors().size(); i++) {

      result.append("int initNode" + i + " = 1;\n");

    }
    String str = "";
    if (nodeNames.size() > 0) {
      result.append("const char *nodeset[" + nodeNames.size() + "] = {");

      for (Long i = 0L; i < nodeNames.size(); i++) {
        str += "\"" + nodeNames.get(i) + "\",";

      }

    } else {
      result.append("const char *nodeset[" + topGraph.getExecutableActors().size() + "] = {");
      for (Long i = 0L; i < topGraph.getExecutableActors().size(); i++) {
        str += "\"Node" + i + "\",";
      }
    }
    if (str.endsWith(",")) {
      str = str.substring(0, str.length() - 1);
    }
    result.append(str);

    result.append("};// rename the node e.g.:\"po-eii26\" \n\n");
    result.append("int main(int argc, char **argv) { \n");
    result.append("// Initialize the MPI environment \n");
    result.append("MPI_Init(NULL, NULL); \n");
    return result;
  }
}

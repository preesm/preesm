package org.preesm.algorithm.node.partitioner;

import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.scenario.Scenario;

public class CodegenSimSDP {
  Map<String, Map<Integer, Port>> portRank = new LinkedHashMap<>();
  boolean                         isHomogeneous;
  PiGraph                         topGraph;
  Map<Long, String>               nodeNames;

  public CodegenSimSDP(final Scenario scenario, final PiGraph topGraph, Map<Long, String> nodeNames,
      boolean isHomogeneous) {
    this.isHomogeneous = isHomogeneous;
    this.topGraph = topGraph;
    this.nodeNames = nodeNames;

    final String topPath = scenario.getCodegenDirectory() + File.separator;
    final String fileName = "mainSimSDP.c";

    final StringConcatenation content = buildContent();
    PreesmIOHelper.getInstance().print(topPath, fileName, content);

    PreesmLogger.getLogger().info("mainSimSDP.c file print in : " + topPath);
  }

  private StringConcatenation buildContent() {
    mappingPort();
    final StringConcatenation result = new StringConcatenation();
    result.append(header());
    result.append(includes());
    result.append(globalDefinition());
    result.append(bufferDeclaration());
    result.append(loop());
    result.append(exeNode());
    return result;
  }

  /**
   * The header file contains file information.
   *
   * @return The string content of the header file.
   */
  private StringConcatenation header() {
    final StringConcatenation result = new StringConcatenation();
    result.append("/** \n");
    result.append("* @file " + "Cluster_mainSimSDP.c \n");
    result.append("* @generated by " + this.getClass().getSimpleName() + "\n");
    result.append("* @date " + new Date() + "\n");
    result.append("*/ \n\n");
    return result;
  }

  /**
   * Include MPI and subgraph relative libraries
   *
   * @return The string content of the include part.
   */
  private StringConcatenation includes() {
    final StringConcatenation result = new StringConcatenation();
    result.append("#include <stdio.h> \n");
    result.append("#include <mpi.h> \n");
    result.append("#include \"stdlib.h\" \n");

    for (int i = 0; i < topGraph.getExecutableActors().size(); i++) {
      result.append("#include \"sub" + i + "/preesm_gen" + i + ".h\" \n");
      result.append("#include \"sub" + i + "/sub" + i + ".h\" \n");
    }
    result.append("#define MPI_LOOP_SIZE 100 \n\n");
    return result;
  }

  /**
   * Define global variable
   *
   * @return The string content of the global definition.
   */
  private StringConcatenation globalDefinition() {
    final StringConcatenation result = new StringConcatenation();
    result.append("int MPIStopNode = 0;\n");
    for (int i = 0; i < topGraph.getExecutableActors().size(); i++) {

      result.append("int initNode" + i + " = 1;\n");

    }
    // in homogeneous node MPI attribute any node to any subgraph
    // this is controlled by node name in heterogeneous case
    if (!isHomogeneous) {
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

      result.append("};// rename the node e.g.:\"po-eii26\" or \"paravance-25.rennes.grid5000.fr\" \n\n");
    }
    result.append("int main(int argc, char **argv) { \n");
    result.append(" int label = 100;\n");
    result.append(" MPI_Status status;\n");
    result.append(" // Initialize the MPI environment \n");
    result.append(" MPI_Init(&argc, &argv); \n");
    return result;
  }

  /**
   * Declare buffer
   *
   * @return The string content of the declared buffer
   */
  private StringConcatenation bufferDeclaration() {
    final StringConcatenation result = new StringConcatenation();
    if (!topGraph.getFifos().isEmpty()) {
      result.append("//Allocate buffers in distributed memory \n");
    }
    for (final AbstractActor node : topGraph.getOnlyActors()) {
      if (node instanceof Actor) {
        for (final DataOutputPort dout : node.getDataOutputPorts()) {
          final String bufferName = node.getName() + "_" + dout.getName() + "__"
              + ((AbstractActor) dout.getFifo().getTarget()).getName() + "_" + dout.getFifo().getTargetPort().getName();
          result.append(dout.getFifo().getType() + " *" + bufferName + "=(" + dout.getFifo().getType() + "*)malloc("
              + dout.getExpression().evaluate() + " * sizeof(" + dout.getFifo().getType() + "));\n");

        }
      }
    }
    return result;
  }

  /**
   * Homogeneous: MPI associate an rank to a node, Heterogeneous: MPI give the name of the node Then start the
   * procedure' loop
   *
   * @return The string content of the MPI instruction
   */
  private StringConcatenation loop() {
    final StringConcatenation result = new StringConcatenation();
    if (isHomogeneous) {
      result.append("int mpi_rank;\n");
      result.append("MPI_Comm_rank(MPI_COMM_WORLD,&mpi_rank); \n\n");

    } else {
      result.append("char processor_name[MPI_MAX_PROCESSOR_NAME]; \n");
      result.append("int name_len; \n");
      result.append("MPI_Get_processor_name(processor_name, &name_len); \n");
    }
    result.append("#ifdef MPI_LOOP_SIZE // Case of a finite loop \n");
    result.append("for(int index=0; index< MPI_LOOP_SIZE;index++) { \n");
    result.append("#else // Default case of an infinite loop \n");
    result.append("while(!MPIStopNode) { \n");
    result.append("#endif \n");
    return result;
  }

  private void mappingPort() {
    for (final AbstractActor node : topGraph.getOnlyActors()) {
      if (node instanceof Actor) {

        for (int index = 0; index < node.getDataInputPorts().size(); index++) {

          final int id = index;
          final Port port = node.getDataInputPorts().stream().filter(x -> x.getName().endsWith(String.valueOf(id)))
              .findFirst().orElse(null);
          if (portRank.containsKey(node.getName())) {
            portRank.get(node.getName()).put(index, port);
          } else {
            final Map<Integer, Port> map = new LinkedHashMap<>();
            map.put(index, port);

            portRank.put(node.getName(), map);
          }
        }
        for (int index = 0; index < node.getDataOutputPorts().size(); index++) {
          final int id = index;
          final Port port = node.getDataOutputPorts().stream().filter(x -> x.getName().endsWith(String.valueOf(id)))
              .findFirst().orElse(null);
          if (portRank.containsKey(node.getName())) {
            portRank.get(node.getName()).put(index + node.getDataInputPorts().size(), port);
          } else {
            final Map<Integer, Port> map = new LinkedHashMap<>();
            map.put(index, port);

            portRank.put(node.getName(), map);
          }
        }

      }
    }
  }

  private StringConcatenation exeNode() {
    StringConcatenation result = new StringConcatenation();
    for (final AbstractActor node : topGraph.getOnlyActors()) {
      if (!(node instanceof Actor)) {
        continue;
      }

      final String indexStr = node.getName().replace("sub", "");
      final Long indexL = Long.decode(indexStr);
      result.append("if (rank ==" + indexL + "){ \n");
      if (!node.getDataInputPorts().isEmpty()) {
        result.append("int src = " + (indexL - 1) + ";\n");
      }
      result.append(mpiRecv(node));

      result.append(node.getName() + "(");
      if (portRank.containsKey(node.getName())) {

        for (int i = 0; i < portRank.get(node.getName()).size(); i++) {

          final Port port = portRank.get(node.getName()).get(i);
          String bufferName = "";
          if (port instanceof final DataOutputPort dout) {
            bufferName = node.getName() + "_" + port.getName() + "__"
                + ((AbstractActor) dout.getFifo().getTarget()).getName() + "_"
                + dout.getFifo().getTargetPort().getName();
          } else if (port instanceof final DataInputPort din) {
            bufferName = ((AbstractActor) din.getFifo().getSource()).getName() + "_"
                + din.getFifo().getSourcePort().getName() + "__" + node.getName() + "_" + din.getName();
          }

          result.append(bufferName + ",");
        }
      }
      /*
       * for (final DataInputPort din : node.getDataInputPorts()) {
       * result.append(din.getFifo().getSourcePort().getName() + ","); } for (final DataOutputPort dout :
       * node.getDataOutputPorts()) { result.append(dout.getName() + ","); }
       */
      String temp = result.toString();
      if (result.toString().endsWith(",")) {
        temp = result.toString().substring(0, result.length() - 1);
      }
      result = new StringConcatenation();
      result.append(temp + ");\n");
      if (!node.getDataOutputPorts().isEmpty()) {
        result.append("int dest =" + (indexL + 1) + ";\n");
      }
      result.append(mpiSend(node));

      result.append("initNode" + indexL + " = 0;\n }\n");

    }
    result.append("}\n");
    for (final AbstractActor node : topGraph.getOnlyActors()) {
      if (node instanceof Actor) {

        result.append(free(node));

      }
    }

    result.append(" MPI_Finalize();\n ");
    result.append(" return 0;\n }");

    return result;
  }

  private StringConcatenation free(AbstractActor node) {
    final StringConcatenation result = new StringConcatenation();
    for (final DataOutputPort dout : node.getDataOutputPorts()) {
      final String bufferName = node.getName() + "_" + dout.getName() + "__"
          + ((AbstractActor) dout.getFifo().getTarget()).getName() + "_" + dout.getFifo().getTargetPort().getName();

      result.append("free(" + bufferName + ");\n");

    }
    return result;
  }

  private StringConcatenation mpiRecv(AbstractActor node) {
    final StringConcatenation result = new StringConcatenation();
    final Object[] args = node.getDataInputPorts().toArray();
    for (int i = 0; i < args.length; i++) {
      final int index = i;
      final DataInputPort din = node.getDataInputPorts().stream()
          .sorted(Comparator
              .comparing(port -> Integer.parseInt(port.getFifo().getSourcePort().getName().replace("out_", ""))))
          .skip(index).findFirst().orElse(null);

      final String bufferName = ((AbstractActor) din.getFifo().getSource()).getName() + "_"
          + din.getFifo().getSourcePort().getName() + "__" + node.getName() + "_" + din.getName();
      final int src = Integer.valueOf(((AbstractActor) din.getFifo().getSource()).getName().replace("sub", ""));
      String source = String.valueOf(src);
      if (isHomogeneous) {
        source = "find_rank_by_processor_name(nodeset[" + src + "])";
      }
      String type = din.getFifo().getType();
      if ("uchar".equals(type)) {
        type = "unsigned_char";
      }

      result.append("MPI_Recv(" + bufferName + "," + din.getExpression().evaluate() + "," + "MPI_" + type.toUpperCase()
          + "," + source + ", label, MPI_COMM_WORLD, &status);\n");
    }
    return result;
  }

  private StringConcatenation mpiSend(AbstractActor node) {
    final StringConcatenation result = new StringConcatenation();
    final Object[] args = node.getDataOutputPorts().toArray();
    for (int i = 0; i < args.length; i++) {
      final int index = i;
      final DataOutputPort dout = node.getDataOutputPorts().stream().filter(x -> x.getName().equals("out_" + index))
          .findFirst().orElseThrow();

      final String bufferName = node.getName() + "_" + dout.getName() + "__"
          + ((AbstractActor) dout.getFifo().getTarget()).getName() + "_" + dout.getFifo().getTargetPort().getName();
      final int dest = Integer.valueOf(((AbstractActor) dout.getFifo().getTarget()).getName().replace("sub", ""));
      String type = dout.getFifo().getType();
      String destination = String.valueOf(dest);
      if (isHomogeneous) {
        destination = "find_rank_by_processor_name(nodeset[" + dest + "])";
      }
      if ("uchar".equals(type)) {
        type = "unsigned_char";
      }
      result.append("MPI_Send(" + bufferName + "," + dout.getExpression().evaluate() + "," + "MPI_" + type.toUpperCase()
          + "," + destination + " ,label, MPI_COMM_WORLD);\n");
    }
    return result;
  }

  public static String generateFuncForHeterogeneousAttribute() {
    final StringBuilder code = new StringBuilder();

    code.append("int find_rank_by_processor_name(const char *processor_name) {\n");
    code.append("    int rank = -1;\n");
    code.append("    int size;\n");
    code.append("    MPI_Comm_size(MPI_COMM_WORLD, &size);\n");
    code.append("    char **processor_names = (char **)malloc(size * sizeof(char *));\n");
    code.append("    for (int i = 0; i < size; i++) {\n");
    code.append("        processor_names[i] = (char *)malloc(MPI_MAX_PROCESSOR_NAME * sizeof(char));\n");
    code.append("    }\n");
    code.append("    MPI_Gather(processor_name, MPI_MAX_PROCESSOR_NAME, MPI_CHAR,\n");
    code.append("               processor_names[0], MPI_MAX_PROCESSOR_NAME, MPI_CHAR,\n");
    code.append("               0, MPI_COMM_WORLD);\n\n");

    code.append("    for (int i = 0; i < size; i++) {\n");
    code.append("        printf(\"Processor name of rank %d: %s\\n\", i, processor_names[i]);\n");
    code.append("        if(strcmp(processor_names[i], processor_name) == 0) {\n");
    code.append("            rank = i;\n");
    code.append("        }\n");
    code.append("    }\n\n");

    code.append("    for (int i = 0; i < size; i++) {\n");
    code.append("        free(processor_names[i]);\n");
    code.append("    }\n");
    code.append("    free(processor_names);\n");
    code.append("    return rank;\n");
    code.append("}");

    return code.toString();
  }
}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2024)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.codegen.xtend.printer.net.c;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.CallBlock;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.Delimiter;
import org.preesm.codegen.model.Direction;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.SharedMemoryCommunication;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.xtend.printer.c.CPrinter;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmResourcesHelper;

/**
 *
 * @author anmorvan
 *
 */
public class TcpCPrinter extends CPrinter {

  private static final String STDFILES_TCPC_PATH = "stdfiles/tcpc/";

  private final List<List<List<String>>> receivingCalls;

  public TcpCPrinter() {
    super();
    receivingCalls = new ArrayList<>();
  }

  @Override
  public CharSequence printDeclarationsHeader(List<Variable> list) {
    return "";
  }

  @Override
  public Map<String, CharSequence> generateStandardLibFiles() {
    final Map<String, CharSequence> generateStandardLibFiles = super.generateStandardLibFiles();
    try {

      final String tcpcomc = PreesmResourcesHelper.getInstance().read(STDFILES_TCPC_PATH + "tcp_communication.c",
          TcpCPrinter.class);
      final String tcpcomh = PreesmResourcesHelper.getInstance().read(STDFILES_TCPC_PATH + "tcp_communication.h",
          TcpCPrinter.class);
      final String preesmgenh = PreesmResourcesHelper.getInstance().read(STDFILES_TCPC_PATH + "preesm_gen_tcp.h",
          TcpCPrinter.class);

      generateStandardLibFiles.put("tcp_communication.c", tcpcomc);
      generateStandardLibFiles.put("tcp_communication.h", tcpcomh);
      generateStandardLibFiles.put("preesm_gen_tcp.h", preesmgenh);
    } catch (final IOException e) {
      throw new PreesmRuntimeException("Could not override communication files", e);
    }
    return generateStandardLibFiles;
  }

  @Override
  public CharSequence printCoreBlockHeader(CoreBlock callBlock) {
    return "\n" + "#include \"preesm_gen_tcp.h\"\n" + "\n";
  }

  @Override
  public CharSequence printCoreInitBlockHeader(CallBlock callBlock) {
    final int coreID = ((CoreBlock) callBlock.eContainer()).getCoreID();
    final StringBuilder ff = new StringBuilder();

    final int nbCores = getEngine().getCodeBlocks().size();
    receivingCalls.add(coreID, new ArrayList<>(nbCores));
    for (int i = 0; i < nbCores; i++) {
      receivingCalls.get(coreID).add(i, new ArrayList<>());
      if (i == coreID) {
        continue;
      }
      ff.append("struct rk_sema receiveCore" + coreID + "_" + i + "Sync;\n");
    }
    ff.append("pthread_barrier_t receiveCore" + coreID + "Barrier;\n");
    ff.append("void init_receiving_threads_" + coreID + "(void *arg);\n");

    ff.append("\n");

    ff.append("void *computationThread_Core");
    ff.append(coreID);
    ff.append("(void *arg) {\n");

    ff.append("  int* socketFileDescriptors = (int*)arg;\n");
    ff.append("  init_receiving_threads_" + coreID + "(arg);\n");
    ff.append("  \n" + "#ifdef _PREESM_TCP_DEBUG_\n" + "  printf(\"[TCP-DEBUG] Core" + coreID + " READY\\n\");\n"
        + "#endif\n\n");
    return ff.toString();
  }

  @Override
  public CharSequence printCoreBlockFooter(CoreBlock block) {
    final int coreID = block.getCoreID();
    final CharSequence printCoreBlockFooter = super.printCoreBlockFooter(block);

    final StringBuilder ff = new StringBuilder(printCoreBlockFooter);

    int nbCores = 1;
    final List<List<String>> receivingCallsForCurrentCore = receivingCalls.get(coreID);
    final int size = receivingCallsForCurrentCore.size();
    for (int from = 0; from < size; from++) {
      final List<String> l = receivingCallsForCurrentCore.get(from);
      if (from == coreID || l.isEmpty()) {
        continue;
      }
      ff.append("void *receiveThread_Core");
      ff.append(coreID + "_" + from);
      ff.append("(void *arg);\n");
      nbCores++;
    }
    ff.append("\n");
    ff.append("void init_receiving_threads_" + coreID + "(void *arg) {\n");
    ff.append("  pthread_barrier_init(&receiveCore" + coreID + "Barrier, NULL, " + nbCores + ");\n");

    for (int from = 0; from < size; from++) {
      final List<String> l = receivingCallsForCurrentCore.get(from);
      if (from == coreID || l.isEmpty()) {
        continue;
      }
      ff.append("  pthread_t receiving_thread" + from + ";\n");
      ff.append("  rk_sema_init(&receiveCore" + coreID + "_" + from + "Sync, 0);\n");
      ff.append("  pthread_create(&receiving_thread" + from + ", NULL, &receiveThread_Core" + coreID + "_" + from
          + ", arg);\n");
    }
    ff.append("}\n");
    ff.append("\n");

    for (int from = 0; from < size; from++) {
      if (from == coreID) {
        continue;
      }
      final List<String> l = receivingCallsForCurrentCore.get(from);
      if (l.isEmpty()) {
        continue;
      }
      ff.append("void *receiveThread_Core");
      ff.append(coreID + "_" + from);
      ff.append("(void *arg) {\n");

      ff.append("int* socketFileDescriptors = (int*)arg;\n");
      ff.append("  while (1) {\n");
      ff.append("    pthread_barrier_wait(&receiveCore" + coreID + "Barrier);\n");
      for (final String s : l) {
        ff.append("    " + s);
      }
      ff.append("  }\n" + "  return NULL;\n" + "}\n\n");

    }

    return ff.toString();
  }

  @Override
  public CharSequence printCoreLoopBlockHeader(LoopBlock block2) {
    final CoreBlock eContainer = (CoreBlock) block2.eContainer();

    final int coreID = eContainer.getCoreID();
    final StringBuilder res = new StringBuilder();
    res.append("  int iterationCount = 0;\n");

    res.append("#ifdef PREESM_LOOP_SIZE\n");
    res.append("  for(int index=0;index<PREESM_LOOP_SIZE;index++){\n");
    res.append("#else\n");
    res.append("  while(1){\n");
    res.append("#endif\n");

    res.append("    /* START OF LOOP HEADER */\n");
    res.append("    iterationCount++;\n");
    res.append("#ifdef _PREESM_TCP_DEBUG_\n" + "    printf(\"[TCP-DEBUG] Core" + coreID
        + " iteration #%d - at barrier\\n\",iterationCount);\n" + "#endif\n");
    res.append("    preesm_barrier(socketFileDescriptors, " + coreID + ", " + this.getEngine().getCodeBlocks().size()
        + ");\n");
    res.append("#ifdef _PREESM_TCP_DEBUG_\n" + "    printf(\"[TCP-DEBUG] Core" + coreID
        + " iteration #%d - barrier passed\\n\",iterationCount);\n" + "#endif\n");

    res.append("    pthread_barrier_wait(&receiveCore" + coreID + "Barrier);\n");
    res.append("    /* END OF LOOP HEADER */\n\n");

    return res.toString();
  }

  @Override
  public CharSequence printCoreLoopBlockFooter(LoopBlock block2) {
    return super.printCoreLoopBlockFooter(block2).toString().replace("pthread_barrier_wait(&iter_barrier);",
        "// barrier at beginning");
  }

  @Override
  public CharSequence printSharedMemoryCommunication(SharedMemoryCommunication communication) {
    final StringBuilder functionCallBuilder = new StringBuilder("preesm_");

    final Direction direction = communication.getDirection();
    final int to = communication.getReceiveEnd().getCoreContainer().getCoreID();
    final int from = communication.getSendStart().getCoreContainer().getCoreID();
    switch (direction) {
      case SEND:
        functionCallBuilder.append("send_");
        break;
      case RECEIVE:
        functionCallBuilder.append("receive_");
        break;
      default:
        throw new UnsupportedOperationException("Unsupported [" + direction + "] communication direction.");
    }

    final Delimiter delimiter = communication.getDelimiter();
    switch (delimiter) {
      case START:
        functionCallBuilder.append("start");
        break;
      case END:
        functionCallBuilder.append("end");
        break;
      default:
        throw new UnsupportedOperationException("Unsupported [" + direction + "] communication direction.");
    }
    final long size = communication.getData().getNbToken();

    final String dataAddress = communication.getData().getName();

    functionCallBuilder.append("(" + from + ", " + to + ", socketFileDescriptors, " + dataAddress + ", " + size + ", \""
        + dataAddress + " " + size + "\"" + ");\n");

    if (direction == Direction.RECEIVE && delimiter == Delimiter.START) {
      receivingCalls.get(to).get(from).add(functionCallBuilder.toString());
      receivingCalls.get(to).get(from).add("rk_sema_post(&receiveCore" + to + "_" + from + "Sync);\n");
      return "rk_sema_wait(&receiveCore" + to + "_" + from + "Sync);\n";
    }
    return functionCallBuilder.toString();
  }

  @Override
  public String printMain(final List<Block> printerBlocks) {
    // 0- without the following class loader initialization, I get the following exception when running as Eclipse
    // plugin:
    // org.apache.velocity.exception.VelocityException: The specified class for ResourceManager
    // (org.apache.velocity.runtime.resource.ResourceManagerImpl) does not implement
    // org.apache.velocity.runtime.resource.ResourceManager; Velocity is not initialized correctly.
    final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(TcpCPrinter.class.getClassLoader());

    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    context.put("PREESM_DATE", new Date().toString());
    context.put("PREESM_PRINTER", this.getClass().getSimpleName());
    context.put("PREESM_NBTHREADS", printerBlocks.size());

    context.put("PREESM_MAIN_THREAD", getMainOperatorId());

    final List<String> threadFunctionNames = IntStream.range(0, printerBlocks.size())
        .mapToObj(i -> String.format("computationThread_Core%d", i)).collect(Collectors.toList());

    context.put("PREESM_THREAD_FUNCTIONS_DECLS",
        "void* " + String.join("(void *arg);\nvoid* ", threadFunctionNames) + "(void *arg);\n");

    context.put("PREESM_THREAD_FUNCTIONS", "&" + String.join(",&", threadFunctionNames));

    // 3- init template reader
    final String templateLocalPath = "templates/tcpc/main.c";
    final URL mainTemplate = PreesmResourcesHelper.getInstance().resolve(templateLocalPath, TcpCPrinter.class);
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(mainTemplate.openStream());
    } catch (final IOException e) {
      throw new PreesmRuntimeException("Could not locate main template [" + templateLocalPath + "].", e);
    }

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, "org.apache.velocity", reader);

    // 99- set back default class loader
    Thread.currentThread().setContextClassLoader(oldContextClassLoader);

    return writer.getBuffer().toString();
  }
}

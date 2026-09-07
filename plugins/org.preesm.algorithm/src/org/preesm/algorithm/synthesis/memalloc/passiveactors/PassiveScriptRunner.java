package org.preesm.algorithm.synthesis.memalloc.passiveactors;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.ParseException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.URLHelper;
import org.preesm.commons.files.URLResolver;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveInputPort;
import org.preesm.model.pisdf.PassiveOutputPort;
import org.preesm.model.pisdf.PassivePort;

public class PassiveScriptRunner {

  private static final Logger logger = PreesmLogger.getLogger();

  private static final String JOIN_W_SCRIPT        = "join.bsh";
  private static final String ROUNDBUFFER_W_SCRIPT = "roundbuffer.bsh";
  private static final String FORK_R_SCRIPT        = "fork.bsh";
  private static final String BROADCAST_R_SCRIPT   = "broadcast.bsh";

  // Paths to the special scripts files
  public static final String WJOIN        = PassiveActorEngine.PASSIVE_SCRIPT_FOLDER + IPath.SEPARATOR
      + PassiveScriptRunner.JOIN_W_SCRIPT;
  public static final String RFORK        = PassiveActorEngine.PASSIVE_SCRIPT_FOLDER + IPath.SEPARATOR
      + PassiveScriptRunner.FORK_R_SCRIPT;
  public static final String WROUNDBUFFER = PassiveActorEngine.PASSIVE_SCRIPT_FOLDER + IPath.SEPARATOR
      + PassiveScriptRunner.ROUNDBUFFER_W_SCRIPT;
  public static final String RBROADCAST   = PassiveActorEngine.PASSIVE_SCRIPT_FOLDER + IPath.SEPARATOR
      + PassiveScriptRunner.BROADCAST_R_SCRIPT;

  long                      alignment;
  Map<AbstractVertex, Long> brv;

  Map<PassivePort, Pair<Integer, Integer>>       portBeginjEndjResults;
  Map<PassivePort, List<Pair<Integer, Integer>>> portBeginiEndiResults;

  public PassiveScriptRunner(long alignment, Map<AbstractVertex, Long> brv) {
    this.alignment = alignment;
    this.brv = brv;
    portBeginjEndjResults = new HashMap<>();
    portBeginiEndiResults = new HashMap<>();
  }

  /**
   *
   * @param dp
   *          input data port
   * @return beginj and endj pointer associated to dp. (0,0) if there is no pointers associated to dp
   */
  public Pair<Integer, Integer> getBeginEnd(DataPort dp) {
    if (!(dp instanceof PassivePort) || !portBeginjEndjResults.containsKey(dp)) {
      return new Pair<>(0, 0);
    }
    return portBeginjEndjResults.get(dp);
  }

  /**
   * Note that beginji and endji pointers can be cyclic, and are stored only one cycle. To get the size of the cycle,
   * call the function {@link #getBeginEndCycleSize(DataPort) getBeginEndCycleSize}
   *
   * @param dp
   *          input data port
   * @param i
   *          instance of actor linked to dp
   * @return beginji and endji pointer associated to dp, and to the current instance of actor linked to dp. (0,0) if
   *         there is no pointers associated to dp or if i is too big
   */
  public Pair<Integer, Integer> getBeginEnd(DataPort dp, int i) {
    if (!(dp instanceof PassivePort) || !portBeginiEndiResults.containsKey(dp)
        || i >= portBeginiEndiResults.get(dp).size()) {
      return new Pair<>(0, 0);
    }
    return portBeginiEndiResults.get(dp).get(i);
  }

  /**
   * Return all begin and end pointers for a given passive port.
   *
   * @param dp
   *          the current data port
   * @return the list of begin and end pointers associated to dp. If there is no associated list (because dp is not a
   *         passive actor), an empty list will be returned.
   */
  public List<Pair<Integer, Integer>> getBeginEndAllInstances(DataPort dp) {
    if (!(dp instanceof PassivePort) || !portBeginiEndiResults.containsKey(dp)) {
      return new ArrayList<>();
    }
    return portBeginiEndiResults.get(dp);
  }

  /**
   * This function is used to determined to cycle size in the compute of beginji and endji pointers. The result might be
   * used to determine the biggest value of parameter i in method {@link #getBeginEnd(DataPort, int)
   * getBeginEnd(DataPort, int)}
   *
   * @param dp
   *          input data port
   * @return the size of stored pair of beginji and endji pointers, 0 if there is no pointers associated to dp.
   */
  public int getBeginEndCycleSize(DataPort dp) {
    if (!(dp instanceof PassivePort) || !portBeginiEndiResults.containsKey(dp)) {
      return 0;
    }
    return portBeginiEndiResults.get(dp).size();
  }

  /**
   * Execute passive scripts of passivePort of passive actors in passiveActors. Results are stored in private
   * intermediate maps.
   *
   * @param passiveActors
   *          passive actors
   * @throws EvalError
   *           if script of current data port has been correctly ran.
   */
  public void run(List<PassiveActor> passiveActors) {
    for (final PassiveActor pa : passiveActors) {
      for (final PassivePort pp : pa.getAllPassivePorts()) {
        try {
          runScript(pp);
        } catch (final Exception e) {
          throw new PreesmRuntimeException("Error for port " + pp.getName() + " of actor " + pa.getName() + " : " + e);
        }
      }
    }
  }

  private void runScript(PassivePort port) throws EvalError {

    final PassivePort passivePort = port;

    final URL scriptURL = URLResolver.findFirst(port.getScript());

    if (scriptURL == null) {
      throw new PreesmRuntimeException("script url " + port.getScript() + "of port " + port.getName() + " of actor "
          + port.getContainingActor().getName() + " is not correct");
    }

    final PassiveActor parent = (PassiveActor) port.getContainingActor();

    // TODO make verifs at this level (with rv of parent and oppositeParent)

    final Interpreter interpreter = new Interpreter();
    final BshClassManager classManager = interpreter.getClassManager();
    classManager.cacheClassInfo("PassivePort", PassivePort.class);

    final Map<String, Long> parameters = new LinkedHashMap<>();

    for (final ConfigInputPort p : parent.getConfigInputPorts()) {
      final ISetter setter = p.getIncomingDependency().getSetter();
      if (setter instanceof final Parameter param) {
        parameters.put(p.getName(), param.getExpression().evaluateAsLong());
      }
    }
    parameters.put("alignment", this.alignment);

    // Import the necessary libraries
    interpreter.eval("import " + PassivePort.class.getName() + ";");
    interpreter.eval("import " + List.class.getName() + ";");

    // Feed the parameters/inputs/outputs to the interpreter
    for (final Entry<String, Long> e : parameters.entrySet()) {
      interpreter.set(e.getKey(), e.getValue());
    }
    if (interpreter.get("parameters") == null) {
      interpreter.set("parameters", parameters);
    }
    if (interpreter.get("inputs") == null) {
      interpreter.set("inputs", parent.getDataInputPorts());
    }
    if (interpreter.get("outputs") == null) {
      interpreter.set("outputs", parent.getDataOutputPorts());
    }

    String portIdxName = "";
    String portInstanceIdxName = "";
    String beginjiName = "";
    String endjiName = "";
    int beginji = 0;
    int endji = 0;
    int portIdx = 0;
    if (port instanceof final PassiveInputPort iPort) {
      portIdxName = "inputIdx";
      portInstanceIdxName = "inputInstanceIdx";
      portIdx = parent.getPassiveInputPorts().indexOf(iPort);
      beginjiName = "wbeginji";
      endjiName = "wendji";
    } else if (port instanceof final PassiveOutputPort oPort) {
      portIdxName = "outputIdx";
      portInstanceIdxName = "outputInstanceIdx";
      portIdx = parent.getPassiveOutputPorts().indexOf(oPort);
      beginjiName = "rbeginji";
      endjiName = "rendji";
    }

    final int maxi = brv.get(port.getOppositePort().getContainingActor()).intValue() / brv.get(parent).intValue();

    final List<Integer> portInstanceIndices = new ArrayList<>();

    portInstanceIndices.add(0);
    portInstanceIndices.add(maxi);

    interpreter.set(portIdxName, portIdx);
    interpreter.set(beginjiName, beginji);
    interpreter.set(endjiName, endji);

    int beginj = 0;
    int endj = 0;
    int firstBeginji = 0;
    int firstEndji = 0;

    // First run to set portBeginjEndjResults
    for (final long portInstanceIdx : portInstanceIndices) {
      interpreter.set(portInstanceIdxName, portInstanceIdx);

      try {

        // Run the script
        interpreter.eval(URLHelper.read(scriptURL));

        // Getting output
        final Object beginjiObj = interpreter.get(beginjiName);
        final Object endjiObj = interpreter.get(endjiName);
        beginji = ((Number) beginjiObj).intValue();
        endji = ((Number) endjiObj).intValue();

        // Store the result if the execution was successful
        if (portInstanceIdx == 0) {
          beginj = beginji;
          portBeginiEndiResults.put(passivePort, new ArrayList<>());
          portBeginiEndiResults.get(passivePort).add(new Pair<>(beginji, endji));
          firstBeginji = beginji;
          firstEndji = endji;
        } else if (portInstanceIdx == maxi) {
          endj = endji;
        }
      } catch (final ParseException error) {

        // Logger is used to display messages in the console
        final String message = error.getMessage() + "\n" + error.getCause();
        PassiveScriptRunner.logger.log(Level.WARNING, error,
            () -> "Parse error in " + parent.getName() + " passive script:\n" + message);
      } catch (final EvalError error) {

        // Logger is used to display messages in the console
        final String message = error.getMessage() + "\n" + error.getCause();
        PassiveScriptRunner.logger.log(Level.WARNING, error, () -> "Evaluation error in " + parent.getName()
            + " memory script:\n[Line " + error.getErrorLineNumber() + "] " + message);
      } catch (final IOException exception) {
        PassiveScriptRunner.logger.log(Level.WARNING, exception.getMessage(), exception);
      }
    }
    portBeginjEndjResults.put(port, new Pair<>(beginj, endj));

    // Second run to set portBeginjEndiResults
    for (int i = 1; i < maxi; i++) {

      interpreter.set(portInstanceIdxName, 1);

      try {

        // Run the script
        interpreter.eval(URLHelper.read(scriptURL));

        // Getting output
        final Object beginjiObj = interpreter.get(beginjiName);
        final Object endjiObj = interpreter.get(endjiName);
        beginji = ((Number) beginjiObj).intValue();
        endji = ((Number) endjiObj).intValue();

        // Store the result if the execution was successful

        // If current beginji/endji are equal to first iter beginji/endji, it means that the results will loop, so no
        // need to execute again the script.
        if (beginji == firstBeginji && endji == firstEndji) {
          break;
        }
        portBeginiEndiResults.get(port).add(new Pair<>(beginji, endji));

      } catch (final ParseException error) {

        // Logger is used to display messages in the console
        final String message = error.getMessage() + "\n" + error.getCause();
        PassiveScriptRunner.logger.log(Level.WARNING, error,
            () -> "Parse error in " + parent.getName() + " passive script:\n" + message);
      } catch (final EvalError error) {

        // Logger is used to display messages in the console
        final String message = error.getMessage() + "\n" + error.getCause();
        PassiveScriptRunner.logger.log(Level.WARNING, error, () -> "Evaluation error in " + parent.getName()
            + " memory script:\n[Line " + error.getErrorLineNumber() + "] " + message);
      } catch (final IOException exception) {
        PassiveScriptRunner.logger.log(Level.WARNING, exception.getMessage(), exception);
      }
    }
  }

  public void updateBrv(Map<AbstractVertex, Long> newBrv) {
    brv = newBrv;
  }
}

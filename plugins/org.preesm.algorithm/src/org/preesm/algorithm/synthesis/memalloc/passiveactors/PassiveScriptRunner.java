package org.preesm.algorithm.synthesis.memalloc.passiveactors;

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
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.URLHelper;
import org.preesm.commons.files.URLResolver;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
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

  long                                     alignment;
  Map<AbstractVertex, Long>                brv;
  Map<PassivePort, Pair<Long, Long>>       portBeginjEndjResults;
  Map<PassivePort, List<Pair<Long, Long>>> portBeginiEndiResults;

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
  public Pair<Long, Long> getBeginEnd(DataPort dp) {
    if (!(dp instanceof PassivePort) || !portBeginjEndjResults.containsKey(dp)) {
      PreesmLogger.getLogger()
          .info("[WARNING] port " + dp.getName() + " is not a passive port or is not present in scripts result");
      return new Pair<>(0L, 0L);
    }
    return portBeginjEndjResults.get(dp);
  }

  public void setEnd(DataPort dp, long value) {
    final Pair<Long, Long> oldPair = this.getBeginEnd(dp);
    final Pair<Long, Long> newPair = new Pair<>(oldPair.getKey(), value);
    portBeginjEndjResults.put((PassivePort) dp, newPair);
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
  public Pair<Long, Long> getBeginEnd(DataPort dp, int i) {
    if (!(dp instanceof PassivePort) || !portBeginiEndiResults.containsKey(dp)
        || i >= portBeginiEndiResults.get(dp).size()) {
      PreesmLogger.getLogger().info(" [WARNING] port " + dp.getName()
          + " is not a passive port or is not present in scripts result, or instance " + i + " is too high");

      return new Pair<>(0L, 0L);
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
  public List<Pair<Long, Long>> getBeginEndAllInstances(DataPort dp) {
    if (!(dp instanceof PassivePort) || !portBeginiEndiResults.containsKey(dp)) {
      PreesmLogger.getLogger()
          .warning("port " + dp.getName() + " is not a passive port or is not present in scripts result");
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
      PreesmLogger.getLogger()
          .warning("port " + dp.getName() + " is not a passive port or is not present in scripts result");
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

    final Map<String, Long> parameters = new LinkedHashMap<>();

    for (final ConfigInputPort p : parent.getConfigInputPorts()) {
      final ISetter setter = p.getIncomingDependency().getSetter();
      if (setter instanceof final Parameter param) {
        parameters.put(p.getName(), param.getExpression().evaluateAsLong());
      }
    }
    parameters.put("alignment", this.alignment);

    // Import the necessary libraries
    interpreter.getNameSpace().importClass(PassivePort.class.getName());
    interpreter.getNameSpace().importClass(List.class.getName());
    interpreter.getNameSpace().importClass(PreesmLogger.class.getName());

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
    long beginji = 0L;
    long endji = 0L;
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

    final int maxi = (brv.get(port.getOppositePort().getContainingActor()).intValue() / brv.get(parent).intValue()) - 1;

    interpreter.set(portIdxName, portIdx);
    interpreter.set(beginjiName, beginji);
    interpreter.set(endjiName, endji);

    long beginj = 0L;
    long endj = 0L;
    long firstBeginji = 0L;
    long firstEndji = 0L;

    interpreter.set(portInstanceIdxName, 0);

    try {

      // Run the script
      interpreter.eval(URLHelper.read(scriptURL));

      // Getting output
      final Object beginjiObj = interpreter.get(beginjiName);
      final Object endjiObj = interpreter.get(endjiName);
      beginji = ((Number) beginjiObj).intValue();
      endji = ((Number) endjiObj).intValue();

      // Store the result if the execution was successful

      beginj = beginji;
      endj = 1; // TODO
      portBeginiEndiResults.put(passivePort, new ArrayList<>());
      portBeginiEndiResults.get(passivePort).add(new Pair<>(beginji, endji));
      firstBeginji = beginji;
      firstEndji = endji;

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
    portBeginjEndjResults.put(port, new Pair<>(beginj, endj));

    // Second run to set portBeginjEndiResults
    for (int i = 1; i <= maxi; i++) {

      interpreter.set(portInstanceIdxName, i);

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

  public void populatePortWithResults(PassivePort pp) {
    if (!this.portBeginjEndjResults.containsKey(pp)) {
      throw new PreesmRuntimeException("Port " + pp.getName() + " is not in script j results");
    }
    pp.setBeginjEndj(this.portBeginjEndjResults.get(pp));

    if (!this.portBeginiEndiResults.containsKey(pp)) {
      throw new PreesmRuntimeException("Port " + pp.getName() + " is not in script i results");
    }

    for (final Pair<Long, Long> beginiEndi : this.portBeginiEndiResults.get(pp)) {
      pp.getBeginiEndi().add(beginiEndi);
    }

  }

  @Override
  public String toString() {

    final Map<AbstractActor, List<String>> tmpMap = new HashMap<>();

    for (final Entry<PassivePort, Pair<Long, Long>> portBeginjEndj : this.portBeginjEndjResults.entrySet()) {
      final AbstractActor parent = portBeginjEndj.getKey().getContainingActor();
      if (!tmpMap.containsKey(parent)) {
        tmpMap.put(parent, new ArrayList<>());
      }
      final String tmpResult = portBeginjEndj.getKey().getName() + ": (" + portBeginjEndj.getValue().getKey() + ", "
          + portBeginjEndj.getValue().getValue() + ") ";
      tmpMap.get(parent).add(tmpResult);
    }

    String result = "PassiveScriptRunner results : \n";
    for (final Entry<AbstractActor, List<String>> actorList : tmpMap.entrySet()) {
      for (final String tmpResult : actorList.getValue()) {
        result += actorList.getKey().getName() + " -> " + tmpResult;

      }
    }
    return result;
  }
}

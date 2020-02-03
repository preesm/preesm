package org.preesm.codegen.xtend.spider2.utils;

import java.util.List;
import java.util.stream.Collectors;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.scenario.SimulationInfo;

public class Spider2CodegenEdge {
  private final static String NULLPTR_CONSTANT = "nullptr";

  /** The source actor of the edge */
  private final AbstractActor source;

  /** The source port ix of the edge */
  private final long sourceIx;

  /** The source rate expression of the edge */
  private final String sourceRateExpression;

  /** The sink actor of the edge */
  private final AbstractActor sink;

  /** The sink port ix of the edge */
  private final long sinkIx;

  /** The sink rate expression of the edge */
  private final String sinkRateExpression;

  /** The data size of the edge */
  private final String size;

  /* The delay of the edge */
  private Delay delay = null;

  /** The delay rate expression */
  private final String delayRateExpression;

  /** The delay setter rate expression */
  private final String delaySetter;

  /** The delay getter rate expression */
  private final String delayGetter;

  /** The delay setter rate expression */
  private final String delaySetterRateExpression;

  /** The delay getter rate expression */
  private final String delayGetterRateExpression;

  /** The delay setter data size */
  private final String delaySetterDataSize;

  /** The delay getter rate expression */
  private final String delayGetterDataSize;

  /**
   * Constructor of the class.
   * 
   * @param fifo
   *          the fifo associated with the Edge.
   * @param simulationInfo
   *          the simulation information of the scenario
   */
  public Spider2CodegenEdge(final Fifo fifo, final SimulationInfo simulationInfo) {
    if (fifo == null) {
      throw new PreesmRuntimeException("can not create Spider2CodegenEdge from null fifo.");
    }
    /* Retrieve the size of the edge */
    this.size = Long.toString(simulationInfo.getDataTypeSizeOrDefault(fifo.getType()));

    /* Retrieve source information */
    this.source = fifo.getSourcePort().getContainingActor();
    this.sourceIx = getRealSourcePortIx(source, fifo.getSourcePort());
    this.sourceRateExpression = createRateExpression(fifo, fifo.getSourcePort(), this.source);

    /* Retrieve sink information */
    this.sink = fifo.getTargetPort().getContainingActor();
    this.sinkIx = getRealSinkPortIx(sink, fifo.getTargetPort());
    this.sinkRateExpression = createRateExpression(fifo, fifo.getTargetPort(), this.sink);

    /* Retrieve delay information */
    this.delay = fifo.getDelay();
    this.delayRateExpression = getDelayRateExpression();

    /* Set setter information */
    this.delaySetter = getDelayActorName(this.hasDelay() ? this.delay.getSetterActor() : null);
    this.delaySetterRateExpression = getDelayDataExpression(
        this.hasDelay() && this.delay.hasSetterActor() ? this.delay.getSetterPort() : null);
    this.delaySetterDataSize = getDelayDataSize(
        this.hasDelay() && this.delay.hasSetterActor() ? this.delay.getSetterPort() : null, simulationInfo);

    /* Set getter information */
    this.delayGetter = getDelayActorName(this.hasDelay() ? this.delay.getGetterActor() : null);
    this.delayGetterRateExpression = getDelayDataExpression(
        this.hasDelay() && this.delay.hasGetterActor() ? this.delay.getGetterPort() : null);
    this.delayGetterDataSize = getDelayDataSize(
        this.hasDelay() && this.delay.hasGetterActor() ? this.delay.getGetterPort() : null, simulationInfo);
  }

  private String getDelayRateExpression() {
    return this.hasDelay() ? this.delay.getExpression().getExpressionAsString() : "0";
  }

  private String getDelayActorName(final AbstractActor actor) {
    return actor != null ? "vertex_" + actor.getName() : NULLPTR_CONSTANT;
  }

  private String getDelayDataExpression(final DataPort port) {
    return port != null ? port.getExpression().getExpressionAsString() : "0";
  }

  private String getDelayDataSize(final DataPort port, final SimulationInfo simulationInfo) {
    return port != null ? Long.toString(simulationInfo.getDataTypeSizeOrDefault(port.getFifo().getType())) : "0";
  }

  private long getRealSourcePortIx(final AbstractActor actor, final DataOutputPort sourcePort) {
    if (actor instanceof Actor) {
      final Actor a = (Actor) (actor);
      final CHeaderRefinement refinement = (CHeaderRefinement) (a.getRefinement());
      final FunctionPrototype proto = refinement.getLoopPrototype();
      final List<FunctionArgument> args = proto.getOutputArguments();
      final List<FunctionArgument> matchArgs = args.stream().filter(x -> x.getName().equals(sourcePort.getName()))
          .collect(Collectors.toList());
      if (matchArgs.size() != 1) {
        throw new PreesmRuntimeException("Did not find match for output port [" + sourcePort.getName()
            + "] in the function [" + proto.getName() + "].");
      }
      return args.indexOf(matchArgs.get(0));
    }
    return actor.getDataOutputPorts().indexOf(sourcePort);
  }

  private long getRealSinkPortIx(final AbstractActor actor, final DataInputPort targetPort) {
    if (actor instanceof Actor) {
      final Actor a = (Actor) (actor);
      final CHeaderRefinement refinement = (CHeaderRefinement) (a.getRefinement());
      final FunctionPrototype proto = refinement.getLoopPrototype();
      final List<FunctionArgument> args = proto.getInputArguments();
      final List<FunctionArgument> matchArgs = args.stream().filter(x -> x.getName().equals(targetPort.getName()))
          .collect(Collectors.toList());
      if (matchArgs.size() != 1) {
        throw new PreesmRuntimeException("Did not find match for input port [" + targetPort.getName()
            + "] in the function [" + proto.getName() + "].");
      }
      return args.indexOf(matchArgs.get(0));
    }
    return actor.getDataInputPorts().indexOf(targetPort);
  }

  private String createRateExpression(final Fifo fifo, final DataPort port, final AbstractActor actor) {
    /* We need to substitute the real parameter name in the expression */
    String expression = port.getExpression().getExpressionAsString();
    for (final ConfigInputPort iCfg : actor.getConfigInputPorts()) {
      if (expression.matches(".*?\\b" + iCfg.getName() + "\\b.*?")) {
        final String realName = ((Parameter) (iCfg.getIncomingDependency().getSetter())).getName();
        expression = expression.replace(iCfg.getName(), realName);
      }
    }
    return expression;
  }

  /**
   * Gets the source actor of the Edge.
   * 
   * @return the AbstractActor source of the Edge.
   */
  public AbstractActor getSource() {
    return this.source;
  }

  /**
   * 
   * @return index port in the source
   */
  public long getSourceIx() {
    return this.sourceIx;
  }

  /**
   * Get the source rate expression formated in format
   * 
   * @return source rate expression
   */
  public String getSourceRateExpression() {
    return this.sourceRateExpression;
  }

  /**
   * Get the name of the data source port of the Edge
   * 
   * @return the name of the source port
   */
  public String getSourcePortName() {
    return this.source.getDataOutputPorts().get((int) this.sourceIx).getName();
  }

  /**
   * Gets the sink actor of the Edge.
   * 
   * @return the AbstractActor sink of the Edge.
   */
  public AbstractActor getSink() {
    return this.sink;
  }

  /**
   * 
   * @return index port in the sink
   */
  public long getSinkIx() {
    return this.sinkIx;
  }

  /**
   * Get the sink rate expression formated in format
   * 
   * @return sink rate expression
   */
  public String getSinkRateExpression() {
    return this.sinkRateExpression;
  }

  /**
   * Get the name of the data sink port of the Edge
   * 
   * @return the name of the sink port
   */
  public String getSinkPortName() {
    return this.sink.getDataInputPorts().get((int) this.sinkIx).getName();
  }

  /**
   * Get the data size of the edge.
   * 
   * @return string of the data size.
   */
  public String getSize() {
    return this.size;
  }

  /**
   * @return true if edge has delay, false else;
   */
  public boolean hasDelay() {
    return this.delay != null;
  }

  /**
   * @return Delay associated to the edge.
   */
  public Delay getDelay() {
    return this.delay;
  }

  /**
   * @return Expression of the delay, "0" else.
   */
  public String getDelayExpression() {
    return this.delayRateExpression;
  }

  /**
   * @return name of the setter actor (if any), "nullptr" else
   */
  public String getSetterDelay() {
    return this.delaySetter;
  }

  /**
   * 
   * @return index port in the setter of the delay (0 if no setter)
   */
  public long getSetterPortIx() {
    if (hasDelay() && this.delay.hasSetterActor()) {
      return this.delay.getSetterActor().getDataOutputPorts().indexOf(this.delay.getSetterPort());
    }
    return 0;
  }

  /**
   * @return expression of the setter rate (if any), "0" else.
   */
  public String getSetterRateExpression() {
    return this.delaySetterRateExpression;
  }

  /**
   * @return data size of the edge setter -> delay, "0" else.
   */
  public String getSetterSize() {
    return this.delaySetterDataSize;
  }

  /**
   * @return name of the getter actor (if any), "nullptr" else
   */
  public String getGetterDelay() {
    return this.delayGetter;
  }

  /**
   * 
   * @return index port in the getter of the delay (0 if no getter)
   */
  public long getGetterPortIx() {
    if (hasDelay() && this.delay.hasGetterActor()) {
      return this.delay.getGetterActor().getDataInputPorts().indexOf(this.delay.getGetterPort());
    }
    return 0;
  }

  /**
   * @return expression of the getter rate (if any), "0" else.
   */
  public String getGetterRateExpression() {
    return this.delayGetterRateExpression;
  }

  /**
   * @return data size of the edge delay -> getter, "0" else.
   */
  public String getGetterSize() {
    return this.delayGetterDataSize;
  }
}

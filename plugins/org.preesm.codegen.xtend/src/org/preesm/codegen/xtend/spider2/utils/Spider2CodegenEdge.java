package org.preesm.codegen.xtend.spider2.utils;

import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Delay;

public class Spider2CodegenEdge {
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

  /* The delay of the edge */
  private Delay delay = null;

  public Spider2CodegenEdge(AbstractActor source, long sourceIx, String sourceRateExpression, AbstractActor sink,
      long sinkIx, String sinkRateExpression) {
    this.source = source;
    this.sourceIx = sourceIx;
    this.sourceRateExpression = sourceRateExpression;
    this.sink = sink;
    this.sinkIx = sinkIx;
    this.sinkRateExpression = sinkRateExpression;
  }

  public AbstractActor getSource() {
    return this.source;
  }

  public long getSourceIx() {
    return this.sourceIx;
  }

  public String getSourceRateExpression() {
    return this.sourceRateExpression;
  }

  public String getSourcePortName() {
    return this.source.getDataOutputPorts().get((int) this.sourceIx).getName();
  }

  public AbstractActor getSink() {
    return this.sink;
  }

  public long getSinkIx() {
    return this.sinkIx;
  }

  public String getSinkRateExpression() {
    return this.sinkRateExpression;
  }

  public String getSinkPortName() {
    return this.sink.getDataInputPorts().get((int) this.sinkIx).getName();
  }

  public boolean hasDelay() {
    return delay != null;
  }

  public void setDelay(final Delay delay) {
    this.delay = delay;
  }

  public Delay getDelay() {
    return delay;
  }

  public String getSetterDelay() {
    if (hasDelay() && this.delay.hasSetterActor()) {
      return "vertex_" + this.delay.getSetterActor().getName();
    }
    return "nullptr";
  }

  public long getSetterPortIx() {
    if (hasDelay() && this.delay.hasSetterActor()) {
      return this.delay.getSetterActor().getDataOutputPorts().indexOf(this.delay.getSetterPort());
    }
    return 0;
  }

  public String getSetterRateExpression() {
    if (hasDelay() && this.delay.hasSetterActor()) {
      return this.delay.getSetterPort().getExpression().getExpressionAsString();
    }
    return "";
  }

  public String getGetterDelay() {
    if (hasDelay() && this.delay.hasGetterActor()) {
      return "vertex_" + this.delay.getGetterActor().getName();
    }
    return "nullptr";
  }

  public long getGetterPortIx() {
    if (hasDelay() && this.delay.hasGetterActor()) {
      return this.delay.getGetterActor().getDataInputPorts().indexOf(this.delay.getGetterPort());
    }
    return 0;
  }

  public String getGetterRateExpression() {
    if (hasDelay() && this.delay.hasGetterActor()) {
      return this.delay.getGetterPort().getExpression().getExpressionAsString();
    }
    return "";
  }
}

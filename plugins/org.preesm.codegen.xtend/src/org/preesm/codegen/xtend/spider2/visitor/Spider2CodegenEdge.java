package org.preesm.codegen.xtend.spider2.visitor;

import org.preesm.model.pisdf.AbstractActor;

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
}

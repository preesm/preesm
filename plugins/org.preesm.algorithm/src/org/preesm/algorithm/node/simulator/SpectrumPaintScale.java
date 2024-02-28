package org.preesm.algorithm.node.simulator;

import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.data.Range;

public class SpectrumPaintScale implements PaintScale {

  private static final float H1 = 0f;
  private static final float H2 = (float) (Math.PI / 8);
  private final Range        range;

  public SpectrumPaintScale(Range r) {
    this.range = r;
  }

  @Override
  public double getLowerBound() {
    return range.getLowerBound();
  }

  @Override
  public double getUpperBound() {
    if (range.getUpperBound() == range.getLowerBound()) {
      return range.getLowerBound() + 1;
    }
    return range.getUpperBound();
  }

  @Override
  public Paint getPaint(double value) {
    final float scaledValue = (float) ((value - getLowerBound()) / (getUpperBound() - getLowerBound()));
    final float reversedValue = 1.0f - scaledValue; // Reverse the scaled value

    final float scaledH = H1 + reversedValue * (H2 - H1);
    return Color.getHSBColor(scaledH, 1f, 1f);
  }
}

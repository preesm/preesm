package org.ietr.preesm.memory.script;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author anmorvan
 *
 */
public class RangeTest {

  Range range0 = new Range(0, 10);
  Range range1 = new Range(5, 15);
  Range range2 = new Range(3, 7);
  Range range3 = new Range(15, 20);

  @Test
  public void testDifference() {
    List<Range> ranges = new ArrayList<>();
    Range.union(ranges, range0);
    Range.union(ranges, new Range(12, 17));

    List<Range> ranges1 = new ArrayList<>();
    ranges1.add(new Range(2, 3));
    ranges1.add(new Range(5, 14));

    // Check the result of range0 - range1
    List<Range> difference = range0.difference(range1);
    assertEquals("[[0..5[]", difference.toString());

    // Check the result of range0 - range3
    assertEquals("[[0..10[]", range0.difference(range3).toString());

    // Check the result of range0 - range1
    assertEquals("[[0..3[, [7..10[]", range0.difference(range2).toString());

    // Check the result of ranges - range1
    assertEquals("[[0..5[, [15..17[]", Range.difference(ranges, range1).toString());

    // Check the result of ranges - ranges1
    assertEquals("[[0..2[, [3..5[, [14..17[]", Range.difference(ranges, ranges1).toString());
  }
}

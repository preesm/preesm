/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.preesm.algorithm.memory.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.preesm.commons.CloneableProperty;

/**
 *
 */
public class Range implements CloneableProperty<Range> {

  /**
   * Realize the subtraction between the {@link Range} instance and another {@link Range}. Neither {@link Range ranges}
   * are modified by this method.
   *
   * @param range
   *          The {@link Range} subtracted from the current instance.
   * @return a {@link List} of {@link Range} corresponding to the current {@link Range} except all overlapping parts
   *         with the {@link Range} passed as a parameter.
   */
  public List<Range> difference(final Range range) {
    final List<Range> result = new ArrayList<>();
    if (Range.hasOverlap(this, range)) {
      final Range inter = this.intersection(range);
      if (inter.getStart() > getStart()) {
        result.add(new Range(getStart(), inter.getStart()));
      }

      if (inter.getEnd() < getEnd()) {
        result.add(new Range(inter.getEnd(), getEnd()));
      }
    } else {
      result.add(copy());
    }

    return result;
  }

  /**
   * {@link Range#difference(Range,Range) Subtract} the given {@link Range} from all {@link Range ranges} stored in the
   * {@link List}. Parameters are not modified by this method.
   *
   * @param ranges
   *          {@link List} of {@link Range}.
   * @param range
   *          {@link Range} subtracted from each element of the list.
   * @return a {@link List} of {@link Range} corresponding to the result of the subtraction.
   */
  public static List<Range> difference(final List<Range> ranges, final Range range) {
    final List<Range> result = new ArrayList<>();
    ranges.stream().forEach(r -> Range.union(result, r.difference(range)));
    return result;
  }

  /**
   * Successively {@link Range#difference(List,Range) subtract} the {@link Range ranges} of the second {@link List} from
   * the first. Parameters are not modified by this method
   *
   * @param ranges
   *          the original {@link List} of {@link Range}.
   * @param ranges2
   *          the subtracted {@link Range ranges}.
   *
   * @return the result of the subtraction as a {@link List} of {@link Range}.
   */
  public static List<Range> difference(final List<Range> ranges, final List<Range> ranges2) {

    // Copy the original list
    // to make sure the map function is applied only once

    List<Range> result = new ArrayList<>(ranges.stream().map(Range::copy).collect(Collectors.toList()));

    // Successively subtract all ranges from ranges2
    for (final Range range : ranges2) {
      result = Range.difference(result, range);
    }

    return result;
  }

  /**
   * {@link Range#translate(long) Translates} all {@link Range ranges} within the given {@link Collection} by the given
   * delta.
   *
   * @param delta
   *          The long value used to {@link Range#translate(long) translate} the {@link Range ranges}.
   */
  public static void translate(final Iterable<Range> ranges, final long delta) {
    StreamSupport.stream(ranges.spliterator(), false).forEach(r -> r.translate(delta));
  }

  /**
   * This method translates the {@link Range} by the given delta. In other words, both the {@link Range#getStart()
   * start} and {@link Range#getEnd() end} attributes of the range are updated by adding them with the give delta.
   *
   * @param delta
   *          the integer value used to update the range.
   * @return this
   *
   */
  public Range translate(final long delta) {
    this.start = getStart() + delta;
    this.end = getEnd() + delta;
    return this;
  }

  /**
   * Computes the intersection of a {@link List} of ranges with another {@link Range} {@link List}. <br>
   * <br>
   * If the ranges lists contains ranges that overlap, then a list containing all overlapping ranges is returned.
   * Neither the newRanges nor the ranges lists are modified in this method.
   *
   * @param ranges
   *          The original {@link List} of {@link Range}.
   *
   * @param newRanges
   *          The new {@link Range} {@link List} to compare.
   *
   * @return the {@link List} of {@link Range} ranges corresponding to the intersection of the newRanges with the
   *         ranges.
   */
  public static List<Range> intersection(final Collection<Range> ranges, final Collection<Range> newRanges) {
    final List<Range> intersectionRanges = new ArrayList<>();
    newRanges.stream().forEach(r -> Range.union(intersectionRanges, Range.intersection(ranges, r)));
    return intersectionRanges;
  }

  /**
   * Computes the intersection of a {@link List} of ranges with a new {@link Range}. <br>
   * <br>
   * If the ranges list contains ranges that overlap the new range, then a a list containing all the overlapping ranges
   * is returned. Neither the newRange nor the ranges lists are modified in this method.
   *
   * @param ranges
   *          The original list of range.
   *
   * @param newRange
   *          The new {@link Range} to compare.
   *
   * @return the {@link List} of {@link Range} ranges corresponding to the intersection of the newRange with the ranges.
   */
  public static List<Range> intersection(final Collection<Range> ranges, final Range newRange) {
    final List<Range> intersectionRanges = new ArrayList<>();
    ranges.stream().forEach(range -> {
      // If the ranges overlap
      final Range intersect = range.intersection(newRange);
      // !== is equivalent to the != of java
      // if we use !=, .equals will be called
      if (intersect != null) {
        Range.union(intersectionRanges, intersect);
      }
    });
    return intersectionRanges;
  }

  /**
   * If the two ranges overlap, the intersection of the two ranges is returned. Else, null is returned Ranges passed as
   * parameter are not modified
   */
  public Range intersection(final Range range) {
    if (Range.hasOverlap(this, range)) {
      final long newStart = Math.max(getStart(), range.getStart());
      final long newEnd = Math.min(getEnd(), range.getEnd());
      return new Range(newStart, newEnd);
    }
    return null;
  }

  /**
   * Successively computes the {@link Range#union(List,Range)} of {@link Range ranges} from <code>ranges1</code> with
   * the {@link List} <code>
   * ranges0</code>.<br>
   * Both lists are thus modified by this function, but only ranges0 contains the result of the method.
   *
   * @return ranges0, that contains the union result
   */
  public static List<Range> union(final List<Range> ranges0, final List<Range> ranges1) {
    ranges1.stream().forEach(r -> Range.union(ranges0, r));
    return ranges0;
  }

  /**
   * Computes the union of a {@link List} of ranges with a new {@link Range}. <br>
   * <br>
   * This method updates the given {@link List} of {@link Range} to include its union with the {@link Range} passed as a
   * parameter. If the ranges list contains ranges that overlap or are contiguous with the new range, then the new range
   * is updated and become the union of the new range and all overlapping ranges from the list.
   *
   * @param ranges
   *          The {@link List} of {@link Range} to update.
   *
   * @param newRange
   *          The new {@link Range} to add. <b> BEWARE: This range may be modified during the call to this method.</b>
   *
   * @return the updated newRange {@link Range}.
   */
  public static Range union(final List<Range> ranges, final Range newRange) {
    boolean changed = true;
    while (changed) {
      final Range originalRange = newRange.copy();
      final Iterator<Range> iter = ranges.iterator();
      while (iter.hasNext()) {
        final Range range = iter.next();

        // If new range overlaps with current range or are contiguous
        if (Range.hasOverlap(range, newRange) || Range.isContiguous(range, newRange)) {

          // Remove old range and include it with the new
          iter.remove();
          newRange.start = Math.min(newRange.getStart(), range.getStart());
          newRange.end = Math.max(newRange.getEnd(), range.getEnd());
        }
      }
      ranges.add(newRange);
      changed = !(newRange.equals(originalRange));
    }
    return newRange;
  }

  /**
   * Successively computes the {@link Range#lazyUnion(List,Range)} of {@link Range ranges} from <code>ranges1</code>
   * with the {@link Iterable} <code>
   * ranges0</code>.<br>
   * Both lists are thus modified by this function, but only ranges0 contains the result of the method.
   *
   * @return ranges0, that contains the lazy union result
   */
  public static List<Range> lazyUnion(final List<Range> ranges0, final Iterable<Range> ranges1) {
    StreamSupport.stream(ranges1.spliterator(), false).forEach(r -> Range.lazyUnion(ranges0, r));
    return ranges0;
  }

  /**
   * Same as {@link Range#union(List,Range)} except that {@link Range#isContiguous(Range) contiguous} {@link Range
   * ranges} are not merged.
   *
   * @param ranges
   *          The {@link List} of {@link Range} to update.
   *
   * @param newRange
   *          The new {@link Range} to add.
   *
   * @return the updated newRange {@link Range}.
   */
  public static Range lazyUnion(final List<Range> ranges, final Range newRange) {
    final Iterator<Range> iter = ranges.iterator();
    while (iter.hasNext()) {
      final Range range = iter.next();

      // If new range overlaps with current range
      if (Range.hasOverlap(range, newRange)) {

        // Remove old range and include it with the new
        iter.remove();
        newRange.start = Math.min(newRange.getStart(), range.getStart());
        newRange.end = Math.max(newRange.getEnd(), range.getEnd());
      }
    }
    ranges.add(newRange);
    return newRange;
  }

  /**
   * Return the minimum start value of the ranges
   */
  public static long minStart(final Iterable<Range> ranges) {
    return StreamSupport.stream(ranges.spliterator(), false).map(Range::getStart).reduce(Long.MAX_VALUE, Math::min);
  }

  /**
   * Return the minimum start value of the ranges
   */
  public static long maxEnd(final Iterable<Range> ranges) {
    return StreamSupport.stream(ranges.spliterator(), false).map(Range::getEnd).reduce(Long.MIN_VALUE, Math::max);
  }

  private static boolean isContiguous(final Range range1, final Range range2) {
    return (range1.getStart() == range2.getEnd()) || (range2.getStart() == range1.getEnd());
  }

  /**
   *
   */
  public static boolean hasOverlap(final List<Range> ranges, final Range range) {
    return !(ranges.stream().allMatch(r -> !Range.hasOverlap(r, range)));

  }

  public static boolean hasOverlap(final Range range1, final Range range2) {
    return (range1.getStart() < range2.getEnd()) && (range2.getStart() < range1.getEnd());
  }

  /**
   * End of the range (does not belong to the range)
   */
  private long end;

  /**
   * First element of the range (included in the range)
   */
  private long start;

  public long getStart() {
    return this.start;
  }

  public long getEnd() {
    return this.end;
  }

  private Range(final Range original) {
    this.start = original.getStart();
    this.end = original.getEnd();
  }

  public Range(final long start, final long end) {
    this.start = start;
    this.end = end;
  }

  public long getLength() {
    return getEnd() - getStart();
  }

  @Override
  public String toString() {
    return "[" + getStart() + ".." + getEnd() + "[";
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Range)) {
      return false;
    } else {
      return (getStart() == ((Range) o).getStart()) && (getEnd() == ((Range) o).getEnd());
    }
  }

  @Override
  public Range copy() {
    return new Range(this);
  }
}

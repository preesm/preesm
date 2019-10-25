/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
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
package org.preesm.algorithm.synthesis.memalloc.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.preesm.commons.CloneableProperty;

/**
 *
 */
public class PiRange implements CloneableProperty<PiRange> {

  /**
   * Realize the subtraction between the {@link PiRange} instance and another {@link PiRange}. Neither {@link PiRange
   * ranges} are modified by this method.
   *
   * @param range
   *          The {@link PiRange} subtracted from the current instance.
   * @return a {@link List} of {@link PiRange} corresponding to the current {@link PiRange} except all overlapping parts
   *         with the {@link PiRange} passed as a parameter.
   */
  public List<PiRange> difference(final PiRange range) {
    final List<PiRange> result = new ArrayList<>();
    if (PiRange.hasOverlap(this, range)) {
      final PiRange inter = this.intersection(range);
      if (inter.getStart() > getStart()) {
        result.add(new PiRange(getStart(), inter.getStart()));
      }

      if (inter.getEnd() < getEnd()) {
        result.add(new PiRange(inter.getEnd(), getEnd()));
      }
    } else {
      result.add(copy());
    }

    return result;
  }

  /**
   * {@link PiRange#difference(PiRange,PiRange) Subtract} the given {@link PiRange} from all {@link PiRange ranges}
   * stored in the {@link List}. Parameters are not modified by this method.
   *
   * @param ranges
   *          {@link List} of {@link PiRange}.
   * @param range
   *          {@link PiRange} subtracted from each element of the list.
   * @return a {@link List} of {@link PiRange} corresponding to the result of the subtraction.
   */
  public static List<PiRange> difference(final List<PiRange> ranges, final PiRange range) {
    final List<PiRange> result = new ArrayList<>();
    ranges.stream().forEach(r -> PiRange.union(result, r.difference(range)));
    return result;
  }

  /**
   * Successively {@link PiRange#difference(List,PiRange) subtract} the {@link PiRange ranges} of the second
   * {@link List} from the first. Parameters are not modified by this method
   *
   * @param ranges
   *          the original {@link List} of {@link PiRange}.
   * @param ranges2
   *          the subtracted {@link PiRange ranges}.
   *
   * @return the result of the subtraction as a {@link List} of {@link PiRange}.
   */
  public static List<PiRange> difference(final List<PiRange> ranges, final List<PiRange> ranges2) {

    // Copy the original list
    // to make sure the map function is applied only once

    List<PiRange> result = new ArrayList<>(ranges.stream().map(PiRange::copy).collect(Collectors.toList()));

    // Successively subtract all ranges from ranges2
    for (final PiRange range : ranges2) {
      result = PiRange.difference(result, range);
    }

    return result;
  }

  /**
   * {@link PiRange#translate(long) Translates} all {@link PiRange ranges} within the given {@link Collection} by the
   * given delta.
   *
   * @param delta
   *          The long value used to {@link PiRange#translate(long) translate} the {@link PiRange ranges}.
   */
  public static void translate(final Iterable<PiRange> ranges, final long delta) {
    StreamSupport.stream(ranges.spliterator(), false).forEach(r -> r.translate(delta));
  }

  /**
   * This method translates the {@link PiRange} by the given delta. In other words, both the {@link PiRange#getStart()
   * start} and {@link PiRange#getEnd() end} attributes of the range are updated by adding them with the give delta.
   *
   * @param delta
   *          the integer value used to update the range.
   * @return this
   *
   */
  public PiRange translate(final long delta) {
    this.start = getStart() + delta;
    this.end = getEnd() + delta;
    return this;
  }

  /**
   * Computes the intersection of a {@link List} of ranges with another {@link PiRange} {@link List}. <br>
   * <br>
   * If the ranges lists contains ranges that overlap, then a list containing all overlapping ranges is returned.
   * Neither the newRanges nor the ranges lists are modified in this method.
   *
   * @param ranges
   *          The original {@link List} of {@link PiRange}.
   *
   * @param newRanges
   *          The new {@link PiRange} {@link List} to compare.
   *
   * @return the {@link List} of {@link PiRange} ranges corresponding to the intersection of the newRanges with the
   *         ranges.
   */
  public static List<PiRange> intersection(final Collection<PiRange> ranges, final Collection<PiRange> newRanges) {
    final List<PiRange> intersectionRanges = new ArrayList<>();
    newRanges.stream().forEach(r -> PiRange.union(intersectionRanges, PiRange.intersection(ranges, r)));
    return intersectionRanges;
  }

  /**
   * Computes the intersection of a {@link List} of ranges with a new {@link PiRange}. <br>
   * <br>
   * If the ranges list contains ranges that overlap the new range, then a a list containing all the overlapping ranges
   * is returned. Neither the newRange nor the ranges lists are modified in this method.
   *
   * @param ranges
   *          The original list of range.
   *
   * @param newRange
   *          The new {@link PiRange} to compare.
   *
   * @return the {@link List} of {@link PiRange} ranges corresponding to the intersection of the newRange with the
   *         ranges.
   */
  public static List<PiRange> intersection(final Collection<PiRange> ranges, final PiRange newRange) {
    final List<PiRange> intersectionRanges = new ArrayList<>();
    ranges.stream().forEach(range -> {
      // If the ranges overlap
      final PiRange intersect = range.intersection(newRange);
      // !== is equivalent to the != of java
      // if we use !=, .equals will be called
      if (intersect != null) {
        PiRange.union(intersectionRanges, intersect);
      }
    });
    return intersectionRanges;
  }

  /**
   * If the two ranges overlap, the intersection of the two ranges is returned. Else, null is returned Ranges passed as
   * parameter are not modified
   */
  public PiRange intersection(final PiRange range) {
    if (PiRange.hasOverlap(this, range)) {
      final long newStart = Math.max(getStart(), range.getStart());
      final long newEnd = Math.min(getEnd(), range.getEnd());
      return new PiRange(newStart, newEnd);
    }
    return null;
  }

  /**
   * Successively computes the {@link PiRange#union(List,PiRange)} of {@link PiRange ranges} from <code>ranges1</code>
   * with the {@link List} <code>
   * ranges0</code>.<br>
   * Both lists are thus modified by this function, but only ranges0 contains the result of the method.
   *
   * @return ranges0, that contains the union result
   */
  public static List<PiRange> union(final List<PiRange> ranges0, final List<PiRange> ranges1) {
    ranges1.stream().forEach(r -> PiRange.union(ranges0, r));
    return ranges0;
  }

  /**
   * Computes the union of a {@link List} of ranges with a new {@link PiRange}. <br>
   * <br>
   * This method updates the given {@link List} of {@link PiRange} to include its union with the {@link PiRange} passed
   * as a parameter. If the ranges list contains ranges that overlap or are contiguous with the new range, then the new
   * range is updated and become the union of the new range and all overlapping ranges from the list.
   *
   * @param ranges
   *          The {@link List} of {@link PiRange} to update.
   *
   * @param newRange
   *          The new {@link PiRange} to add. <b> BEWARE: This range may be modified during the call to this method.</b>
   *
   * @return the updated newRange {@link PiRange}.
   */
  public static PiRange union(final List<PiRange> ranges, final PiRange newRange) {
    boolean changed = true;
    while (changed) {
      final PiRange originalRange = newRange.copy();
      final Iterator<PiRange> iter = ranges.iterator();
      while (iter.hasNext()) {
        final PiRange range = iter.next();

        // If new range overlaps with current range or are contiguous
        if (PiRange.hasOverlap(range, newRange) || PiRange.isContiguous(range, newRange)) {

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
   * Successively computes the {@link PiRange#lazyUnion(List,PiRange)} of {@link PiRange ranges} from
   * <code>ranges1</code> with the {@link Iterable} <code>
   * ranges0</code>.<br>
   * Both lists are thus modified by this function, but only ranges0 contains the result of the method.
   *
   * @return ranges0, that contains the lazy union result
   */
  public static List<PiRange> lazyUnion(final List<PiRange> ranges0, final Iterable<PiRange> ranges1) {
    StreamSupport.stream(ranges1.spliterator(), false).forEach(r -> PiRange.lazyUnion(ranges0, r));
    return ranges0;
  }

  /**
   * Same as {@link PiRange#union(List,PiRange)} except that {@link PiRange#isContiguous(PiRange) contiguous}
   * {@link PiRange ranges} are not merged.
   *
   * @param ranges
   *          The {@link List} of {@link PiRange} to update.
   *
   * @param newRange
   *          The new {@link PiRange} to add.
   *
   * @return the updated newRange {@link PiRange}.
   */
  public static PiRange lazyUnion(final List<PiRange> ranges, final PiRange newRange) {
    final Iterator<PiRange> iter = ranges.iterator();
    while (iter.hasNext()) {
      final PiRange range = iter.next();

      // If new range overlaps with current range
      if (PiRange.hasOverlap(range, newRange)) {

        // Remove old range and include it with the new
        iter.remove();
        newRange.start = Math.min(newRange.getStart(), range.getStart());
        newRange.end = Math.max(newRange.getEnd(), range.getEnd());
      }
    }
    ranges.add(newRange);
    return newRange;
  }

  private static boolean isContiguous(final PiRange range1, final PiRange range2) {
    return (range1.getStart() == range2.getEnd()) || (range2.getStart() == range1.getEnd());
  }

  public static boolean hasOverlap(final PiRange range1, final PiRange range2) {
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

  private PiRange(final PiRange original) {
    this.start = original.getStart();
    this.end = original.getEnd();
  }

  public PiRange(final long start, final long end) {
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
    if (!(o instanceof PiRange)) {
      return false;
    } else {
      return (getStart() == ((PiRange) o).getStart()) && (getEnd() == ((PiRange) o).getEnd());
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(getStart(), getEnd());
  }

  @Override
  public PiRange copy() {
    return new PiRange(this);
  }
}

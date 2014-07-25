package org.ietr.preesm.memory.script

import java.util.ArrayList
import java.util.Collection
import java.util.List
import org.junit.Test

import static org.junit.Assert.*

class Range {

	/**
	 * Realize the subtraction between the {@link Range} instance and another
	 * {@link Range}. Neither {@link Range ranges} are modified by this method.
	 * 
	 * @param range
	 * 	The {@link Range} subtracted from the current instance.
	 * @return a {@link List} of {@link Range} corresponding to the current
	 * {@link Range} except all overlapping parts with the {@link Range} passed
	 * as a parameter. 
	 */
	def difference(Range range) {
		val result = newArrayList
		if (this.hasOverlap(range)) {
			val inter = this.intersection(range)
			if (inter.getStart > this.getStart) {
				result.add(new Range(this.getStart, inter.getStart))
			}

			if (inter.getEnd < this.getEnd) {
				result.add(new Range(inter.getEnd, this.getEnd))
			}
		} else {
			result.add(this.clone as Range)
		}

		result
	}

	/**
	 * {@link Range#difference(Range,Range) Subtract} the given {@link Range} 
	 * from all {@link Range ranges} stored in the {@link List}. Parameters 
	 * are not modified by this method.
	 * 
	 * @param ranges 
	 * 	 {@link List} of {@link Range}.
	 * @param range
	 * 	{@link Range} subtracted from each element of the list.
	 * @return a {@link List} of {@link Range} corresponding to the result of 
	 * the subtraction.
	 */
	static def List<Range> difference(List<Range> ranges, Range range) {
		val result = newArrayList
		ranges.forEach [
			result.union(it.difference(range))
		]

		result
	}

	/**
	 * Successively {@link Range#difference(List,Range) subtract} the {@link 
	 * Range ranges} of the second {@link List} from the first. Parameters are
	 * not modified by this method
	 * 
	 * @param ranges
	 * 	the original {@link List} of {@link Range}.
	 * @param ranges2
	 * 	the subtracted {@link Range ranges}.
	 * 
	 * @return the result of the subtraction as a {@link List} of {@link 
	 * Range}.
	 */
	static def difference(List<Range> ranges, List<Range> ranges2) {

		// Copy the original list
		var List<Range> result = new ArrayList(ranges.map[it.clone as Range]) // to make sure the map function is applied only once

		// Successively subtract all ranges from ranges2
		for (range : ranges2) {
			result = result.difference(range)
		}

		result
	}

	/**
	 * {@link Range#translate(int) Translates} all {@link Range ranges} within
	 * the given {@link Collection} by the given delta.
	 * 
	 * @param delta
	 * 	The integer value used to {@link Range#translate(int) translate} the
	 * {@link Range ranges}.
	 */
	def static void translate(Iterable<Range> ranges, int delta) {
		ranges.forEach[it.translate(delta)]
	}

	/**
	 * This method translates the {@link Range} by the given delta.
	 * In other words, both the {@link Range#getStart() start} and {@link 
	 * Range#getEnd() end} attributes of the range are updated by adding them 
	 * with the give delta.
	 * 
	 * @param delta
	 *  the integer value used to update the range.
	 * @return this
	 *  
	 */
	def translate(int delta) {
		start = getStart + delta
		end = getEnd + delta
		
		this
	}

	/**
	 * Computes the intersection of a {@link List} of ranges with another 
	 * {@link Range} {@link List}.
	 * <br><br>
	 * If the ranges lists contains ranges that overlap, then a list containing
	 * all overlapping ranges is returned. Neither the newRanges nor the ranges
	 * lists are modified in this method.
	 * 
	 * @param ranges
	 * 	The original {@link List} of {@link Range}.
	 * 
	 * @param newRanges
	 *   The new {@link Range} {@link List} to compare.
	 * 
	 * @return the {@link List} of {@link Range} ranges corresponding to the 
	 * intersection of the newRanges with the ranges. 
	 */
	// dispatch is used because otherwise, XTend wants 'it' to be a Collection<Range>
	dispatch def static List<Range> intersection(Collection<Range> ranges, Collection<Range> newRanges) {
		val intersectionRanges = newArrayList
		newRanges.forEach [
			intersectionRanges.union(ranges.intersection(it))
		]

		intersectionRanges
	}

	/**
	 * Computes the intersection of a {@link List} of ranges with a new {@link
	 * Range}.
	 * <br><br>
	 * If the ranges list contains ranges that overlap the new range, then a
	 * a list containing all the overlapping ranges is returned. Neither the
	 * newRange nor the ranges lists are modified in this method.
	 * 
	 * @param ranges
	 * 	The original list of range.
	 * 
	 * @param newRange
	 *   The new {@link Range} to compare.
	 * 
	 * @return the {@link List} of {@link Range} ranges corresponding to the 
	 * intersection of the newRange with the ranges. 
	 */
	dispatch def static List<Range> intersection(List<Range> ranges, Range newRange) {
		val intersectionRanges = newArrayList
		ranges.forEach [ range |
			// If the ranges overlap
			val intersect = range.intersection(newRange)
			// !== is equivalent to the != of java
			// if we use !=, .equals will be called
			if (intersect !== null) {
				intersectionRanges.union(intersect)
			}
		]

		return intersectionRanges
	}

	/**
	 * If the two ranges overlap, the intersection of the two ranges is returned.
	 * Else, null is returned
	 * Ranges passed as parameter are not modified
	 */
	def intersection(Range range) {
		var start = 0
		var end = 0
		if (this.hasOverlap(range)) {
			start = Math::max(this.getStart, range.getStart)
			end = Math::min(this.getEnd, range.getEnd)
			return new Range(start, end)
		}
		null
	}

	/**
	 * Successively computes the {@link Range#union(List,Range)} of {@link
	 * Range ranges} from <code>ranges1</code> with the {@link List} <code>
	 * ranges0</code>.<br>
	 * Both lists are thus modified by this function, but only ranges0 contains 
	 * the result of the method.
	 * 
	 * @return ranges0, that contains the union result
	 */
	def static List<Range> union(List<Range> ranges0, List<Range> ranges1) {
		ranges1.forEach[ranges0.union(it)]
		ranges0
	}

	/**
	 * Computes the union of a {@link List} of ranges with a new {@link Range}.
	 * <br><br>
	 * This method updates the given {@link List} of {@link Range} to include
	 * its union with the {@link Range} passed as a parameter. If the ranges 
	 * list contains ranges that overlap or are contiguous with the new range,
	 * then the new range is updated and become the union of the new range and 
	 * all overlapping ranges from the list.
	 * 
	 * @param ranges
	 * 	The {@link List} of {@link Range} to update.
	 * 
	 * @param newRange
	 *   The new {@link Range} to add.
	 * 
	 * @return the updated newRange {@link Range}. 
	 */
	def static union(List<Range> ranges, Range newRange) {
		var changed = true
		while (changed == true) {
			changed = false
			val originalRange = newRange.clone
			var iter = ranges.iterator
			while (iter.hasNext) {
				val range = iter.next

				// If new range overlaps with current range or are contiguous
				if (range.hasOverlap(newRange) || range.isContiguous(newRange)) {

					// Remove old range and include it with the new
					iter.remove
					newRange.start = Math.min(newRange.getStart, range.getStart)
					newRange.end = Math.max(newRange.getEnd, range.getEnd)
				}
			}
			ranges.add(newRange)
			changed = newRange != originalRange
		}
		newRange
	}

	/**
	 * Successively computes the {@link Range#lazyUnion(List,Range)} of {@link
	 * Range ranges} from <code>ranges1</code> with the {@link Iterable} <code>
	 * ranges0</code>.<br>
	 * Both lists are thus modified by this function, but only ranges0 contains 
	 * the result of the method.
	 * 
	 * @return ranges0, that contains the lazy union result
	 */
	def static List<Range> lazyUnion(List<Range> ranges0, Iterable<Range> ranges1) {
		ranges1.forEach[ranges0.lazyUnion(it)]
		ranges0
	}

	/**
	 * Same as {@link Range#union(List,Range)} except that {@link 
	 * Range#isContiguous(Range) contiguous} {@link Range ranges} are not 
	 * merged.
	 * 
	 * @param ranges
	 * 	The {@link List} of {@link Range} to update.
	 * 
	 * @param newRange
	 *   The new {@link Range} to add.
	 * 
	 * @return the updated newRange {@link Range}. 
	 */
	def static lazyUnion(List<Range> ranges, Range newRange) {
		var iter = ranges.iterator
		while (iter.hasNext) {
			val range = iter.next

			// If new range overlaps with current range
			if (range.hasOverlap(newRange)) {

				// Remove old range and include it with the new
				iter.remove
				newRange.start = Math.min(newRange.getStart, range.getStart)
				newRange.end = Math.max(newRange.getEnd, range.getEnd)
			}
		}
		ranges.add(newRange)
		newRange
	}

	/**
	 * Return the minimum start value of the ranges
	 */
	def static minStart(Iterable<Range> ranges) {
		ranges.fold(0,
			[ res, range |
				Math::min(res, range.getStart)
			])
	}

	/**
	 * Return the minimum start value of the ranges
	 */
	def static maxEnd(Iterable<Range> ranges) {
		ranges.fold(0,
			[ res, range |
				Math::max(res, range.getEnd)
			])
	}

	def static isContiguous(Range newRange, Range range) {
		newRange.getStart == range.getEnd || range.getStart == newRange.getEnd
	}

	def static hasOverlap(List<Range> ranges, Range range) {
		!ranges.forall[!it.hasOverlap(range)]
	}

	def static hasOverlap(Range newRange, Range range) {
		newRange.getStart < range.getEnd && range.getStart < newRange.getEnd
	}

	/**
	 * End of the range (does not belong to the range)
	 */
	@Property
	var int end

	/**
	 * First element of the range (included in the range)
	 */
	@Property
	var int start

	new(Range original) {
		_start = original.getStart
		_end = original.getEnd
	}

	new(int start, int end) {
		_start = start
		_end = end
	}

	def getLength() {
		getEnd - getStart
	}

	override toString() '''[«getStart»..«getEnd»['''

	override equals(Object o) {
		if (o.class != Range) {
			false
		} else {
			this.getStart == (o as Range).getStart && this.getEnd == (o as Range).getEnd
		}
	}

	override clone() {
		new Range(this)
	}
}

class testRange {
	@Test def void testDifference() {
		var range0 = new Range(0, 10)
		var range1 = new Range(5, 15)
		var range2 = new Range(3, 7)
		var range3 = new Range(15, 20)
		var List<Range> ranges = new ArrayList<Range>
		Range::union(ranges, range0)
		Range::union(ranges, new Range(12, 17))

		var List<Range> ranges1 = newArrayList
		ranges1.add(new Range(2, 3))
		ranges1.add(new Range(5, 14))

		// Check the result of range0 - range1
		assertEquals("[[0..5[]", range0.difference(range1).toString)

		// Check the result of range0 - range3
		assertEquals("[[0..10[]", range0.difference(range3).toString)

		// Check the result of range0 - range1
		assertEquals("[[0..3[, [7..10[]", range0.difference(range2).toString)

		// Check the result of ranges - range1
		assertEquals("[[0..5[, [15..17[]", Range::difference(ranges, range1).toString)

		// Check the result of ranges - ranges1
		assertEquals("[[0..2[, [3..5[, [14..17[]", Range::difference(ranges, ranges1).toString)
	}
}

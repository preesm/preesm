package org.ietr.preesm.experiment.memory

import java.util.ArrayList

class Range {
	/**
	 * Computes the union of a {@link List} of ranges with a new {@link Range}.
	 * <br><br>
	 * This method updates the given {@link List} of {@link Range} to include
	 * its union with the {@link Range} passed as a parameter. If the ranges 
	 * list contains ranges that overlap the new range, then the new range is
	 * updated and become the union of the new range and all overlapping ranges
	 * from the list.
	 * 
	 * @param ranges
	 * 	The {@link List} of {@link Range} to update.
	 * 
	 * @param newRange
	 *   The new {@link Range} to add.
	 * 
	 * @return the updated newRange {@link Range}. 
	 */
	def static union(ArrayList<Range> ranges, Range newRange) {
		var iter = ranges.iterator
		while (iter.hasNext) {
			val range = iter.next

			// If new range overlaps with current range
			if ((newRange.start <= range.end && range.start <= newRange.end) //||
			//(newRange.start == range.end || range.start == newRange.end)
			) {

				// Remove old range and include it with the new
				iter.remove
				newRange.start = Math.min(newRange.start, range.start)
				newRange.end = Math.max(newRange.end, range.end)
			}
		}

		ranges.add(newRange)
		newRange
	}
	
	@Property
	var int end
	
	@Property
	var int start

	new(int start, int end) {
		_start = start
		_end = end
	}

	override toString() '''[«start»..«end»]'''
}
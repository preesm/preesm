package org.ietr.preesm.experiment.memory

import java.util.List

class Range {
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
	def static intersection(List<Range> ranges, Range newRange){
		val intersectionRanges = newArrayList 
		ranges.forEach[ range |
			// If the ranges overlap
			if(range.hasOverlap(newRange))
			{
				val start = Math::max(range.start, newRange.start)
				val end  = Math::min(range.end, newRange.end)
				intersectionRanges.union(new Range(start, end))
			}			
		]		
		return intersectionRanges		
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
	def static List<Range> union(List<Range> ranges0, List<Range> ranges1){
		ranges1.forEach[ranges0.union(it)]
		ranges0
	}
	
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
	def static union(List<Range> ranges, Range newRange) {
		var iter = ranges.iterator
		while (iter.hasNext) {
			val range = iter.next
			// If new range overlaps with current range or are contiguous
			if (range.hasOverlap(newRange) || range.isContiguous(newRange)) {

				// Remove old range and include it with the new
				iter.remove
				newRange.start = Math.min(newRange.start, range.start)
				newRange.end = Math.max(newRange.end, range.end)
			}
		}
		ranges.add(newRange)
		newRange
	}
	
	def static isContiguous(Range newRange, Range range) {
		newRange.start == range.end || range.start == newRange.end
	}
	
	def static hasOverlap(Range newRange, Range range) {
		newRange.start < range.end && range.start < newRange.end
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
	
	new(Range original){
		_start = original.start
		_end = original.end
	}

	new(int start, int end) {
		_start = start
		_end = end
	}
	
	def getLength(){
		end - start
	}

	override toString() '''[«start»..«end»['''
	
	override equals(Object o){
		if(o.class != Range){
			false
		} else {
			this.start == (o as Range).start && this.end == (o as Range).end	
		}		
	}
	
	override clone(){
		new Range(this)
	}
	
}
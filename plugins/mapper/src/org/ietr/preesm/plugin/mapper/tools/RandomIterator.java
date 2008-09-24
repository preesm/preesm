/**
 * 
 */
package org.ietr.preesm.plugin.mapper.tools;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This iterator iterates any list of objects with type E
 * 
 * @author pmenuet
 */

/**
 * Provides a random iteration over the given list of unknown object.
 * 
 * 
 */
public class RandomIterator<E> implements Iterator<E> {

	// variables

	// the index of the chosen element
	private int index;

	// the list where the iterator is set
	private final List<E> list;

	// size of the list where the iterator is set
	private final int LIST_SIZE;

	// random
	private final Random rand;

	/**
	 * RandomIterator constructor
	 * 
	 * @param list
	 * @param rand
	 */
	public RandomIterator(List<E> list, Random rand) {

		this.list = list;

		this.LIST_SIZE = list.size();

		long seed = System.nanoTime();
		rand.setSeed(seed);
		this.rand = rand;

	}

	/**
	 * Impossible to know because there is a next always
	 */
	@Override
	public boolean hasNext() {
		throw new UnsupportedOperationException();
	}

	/**
	 * determine the next random index and return the designated object
	 */
	@Override
	public E next() {

		index = rand.nextInt(LIST_SIZE);

		return list.get(index);
	}

	/**
	 * Useless in this case
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}

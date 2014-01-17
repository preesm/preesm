package org.ietr.preesm.experiment.memory;

import java.util.ArrayList;
import org.eclipse.xtext.xbase.lib.IntegerRange;

@SuppressWarnings("all")
public class FixedSizeList<T extends Object> extends ArrayList<T> {
  public FixedSizeList(final int capacity) {
    super(capacity);
    IntegerRange _upTo = new IntegerRange(0, (capacity - 1));
    for (final Integer i : _upTo) {
      super.add(null);
    }
  }
  
  public FixedSizeList(final T[] initialElements) {
    super(initialElements.length);
    for (final T loopElement : initialElements) {
      super.add(loopElement);
    }
  }
  
  public void clear() {
    UnsupportedOperationException _unsupportedOperationException = new UnsupportedOperationException("Elements may not be cleared from a fixed size List.");
    throw _unsupportedOperationException;
  }
  
  public boolean add(final T o) {
    UnsupportedOperationException _unsupportedOperationException = new UnsupportedOperationException("Elements may not be added to a fixed size List, use set() instead.");
    throw _unsupportedOperationException;
  }
  
  public void add(final int index, final T element) {
    UnsupportedOperationException _unsupportedOperationException = new UnsupportedOperationException("Elements may not be added to a fixed size List, use set() instead.");
    throw _unsupportedOperationException;
  }
  
  public T remove(final int index) {
    UnsupportedOperationException _unsupportedOperationException = new UnsupportedOperationException("Elements may not be removed from a fixed size List.");
    throw _unsupportedOperationException;
  }
  
  public boolean remove(final Object o) {
    UnsupportedOperationException _unsupportedOperationException = new UnsupportedOperationException("Elements may not be removed from a fixed size List.");
    throw _unsupportedOperationException;
  }
  
  public void removeRange(final int fromIndex, final int toIndex) {
    UnsupportedOperationException _unsupportedOperationException = new UnsupportedOperationException("Elements may not be removed from a fixed size List.");
    throw _unsupportedOperationException;
  }
}

package org.preesm.algorithm.schedule.evaluation;

/**
 *
 * @author anmorvan
 *
 * @param <T>
 *          The schedule cost type. Can be a single value (i.e. an Long representing the latency) or a multidimensional
 *          value (i.e. latency + energy). Or any
 */
public interface IScheduleCost<T> extends Comparable<IScheduleCost<T>> {

  public T getValue();

}

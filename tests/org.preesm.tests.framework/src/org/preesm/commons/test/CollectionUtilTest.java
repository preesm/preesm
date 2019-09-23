package org.preesm.commons.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.preesm.commons.CollectionUtil;

/**
 *
 * @author anmorvan
 *
 */
public class CollectionUtilTest {

  /**
   *
   */
  @Test
  public void testInsertAfter1() {
    final List<String> expected = new ArrayList<>(
        Arrays.asList(new String[] { "A", "B", "b1", "b2", "b3", "b4", "C", "D", "E" }));
    final List<String> subject = new ArrayList<>();

    subject.add("A");
    subject.add("B");
    subject.add("C");
    subject.add("D");
    subject.add("E");

    CollectionUtil.insertAfter(subject, "B", "b1", "b2", "b3", "b4");
    assertEquals(expected, subject);
  }

  /**
   *
   */
  @Test
  public void testInsertAfter2() {
    final List<String> expected = new ArrayList<>(Arrays.asList(new String[] { "A", "B", "C", "D", "E" }));
    final List<String> subject = new ArrayList<>();
    subject.add("A");
    subject.add("B");
    CollectionUtil.insertAfter(subject, "B", "C", "D", "E");
    assertEquals(expected, subject);
  }

  /**
   *
   */
  @Test
  public void testInsertAfter3() {
    final List<String> expected = new ArrayList<>(Arrays.asList(new String[] { "A", "B", "C", "D", "E" }));
    final List<String> subject = new ArrayList<>();
    subject.add("A");
    CollectionUtil.insertAfter(subject, "A", "B", "C", "D", "E");
    assertEquals(expected, subject);
  }

  /**
   *
   */
  @Test
  public void testInsertAfter4() {
    final List<String> expected = new ArrayList<>(Arrays.asList(new String[] { "A", "C", "D", "E", "B" }));
    final List<String> subject = new ArrayList<>();
    subject.add("A");
    subject.add("B");
    CollectionUtil.insertAfter(subject, "A", "C", "D", "E");
    assertEquals(expected, subject);
  }

  /**
   *
   */
  @Test
  public void testInsertBefore1() {
    final List<String> expected = new ArrayList<>(Arrays.asList(new String[] { "A", "b1", "b2", "B", "C", "D", "E" }));
    final List<String> subject = new ArrayList<>();

    subject.add("A");
    subject.add("B");
    subject.add("C");
    subject.add("D");
    subject.add("E");

    CollectionUtil.insertBefore(subject, "B", "b1", "b2");
    assertEquals(expected, subject);
  }

  /**
   *
   */
  @Test
  public void testInsertBefore2() {
    final List<String> expected = new ArrayList<>(Arrays.asList(new String[] { "A", "C", "D", "E", "B" }));
    final List<String> subject = new ArrayList<>();
    subject.add("A");
    subject.add("B");
    CollectionUtil.insertBefore(subject, "B", "C", "D", "E");
    assertEquals(expected, subject);
  }

  /**
   *
   */
  @Test
  public void testInsertBefore3() {
    final List<String> expected = new ArrayList<>(Arrays.asList(new String[] { "B", "C", "D", "E", "A" }));
    final List<String> subject = new ArrayList<>();
    subject.add("A");
    CollectionUtil.insertBefore(subject, "A", "B", "C", "D", "E");
    assertEquals(expected, subject);
  }

  /**
   *
   */
  @Test
  public void testInsertBefore4() {
    final List<String> expected = new ArrayList<>(Arrays.asList(new String[] { "C", "D", "E", "A", "B" }));
    final List<String> subject = new ArrayList<>();
    subject.add("A");
    subject.add("B");
    CollectionUtil.insertBefore(subject, "A", "C", "D", "E");
    assertEquals(expected, subject);
  }

}

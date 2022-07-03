/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
package org.preesm.commons.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.common.util.BasicEList;
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
    final List<String> expected = new BasicEList<>(
        Arrays.asList(new String[] { "A", "B", "b1", "b2", "b3", "b4", "C", "D", "E" }));
    final List<String> subject = new BasicEList<>();

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
    final List<String> expected = new BasicEList<>(Arrays.asList(new String[] { "A", "B", "C", "D", "E" }));
    final List<String> subject = new BasicEList<>();
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
    final List<String> expected = new BasicEList<>(Arrays.asList(new String[] { "A", "B", "C", "D", "E" }));
    final List<String> subject = new BasicEList<>();
    subject.add("A");
    CollectionUtil.insertAfter(subject, "A", "B", "C", "D", "E");
    assertEquals(expected, subject);
  }

  /**
   *
   */
  @Test
  public void testInsertAfter4() {
    final List<String> expected = new BasicEList<>(Arrays.asList(new String[] { "A", "C", "D", "E", "B" }));
    final List<String> subject = new BasicEList<>();
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
    final List<String> expected = new BasicEList<>(Arrays.asList(new String[] { "A", "b1", "b2", "B", "C", "D", "E" }));
    final List<String> subject = new BasicEList<>();

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
    final List<String> expected = new BasicEList<>(Arrays.asList(new String[] { "A", "C", "D", "E", "B" }));
    final List<String> subject = new BasicEList<>();
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
    final List<String> expected = new BasicEList<>(Arrays.asList(new String[] { "B", "C", "D", "E", "A" }));
    final List<String> subject = new BasicEList<>();
    subject.add("A");
    CollectionUtil.insertBefore(subject, "A", "B", "C", "D", "E");
    assertEquals(expected, subject);
  }

  /**
   *
   */
  @Test
  public void testInsertBefore4() {
    final List<String> expected = new BasicEList<>(Arrays.asList(new String[] { "C", "D", "E", "A", "B" }));
    final List<String> subject = new BasicEList<>();
    subject.add("A");
    subject.add("B");
    CollectionUtil.insertBefore(subject, "A", "C", "D", "E");
    assertEquals(expected, subject);
  }

}

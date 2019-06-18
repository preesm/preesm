/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2019)
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

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.commons.math.JEPWrapper;

/**
 *
 * @author anmorvan
 *
 */
public class JEPWrapperTest {

  /**
   *
   */
  @Test
  public void testInvolvement_0() {
    final String expression = "a*b";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(2, involvement.size());
    Assert.assertTrue(involvement.contains("a"));
    Assert.assertTrue(involvement.contains("b"));
  }

  /**
   *
   */
  @Test
  public void testInvolvement_1() {
    final String expression = "2*a+ceil(b)-(a*b)/3-a-a-a-a*b+b+b+b";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(2, involvement.size());
    Assert.assertTrue(involvement.contains("a"));
    Assert.assertTrue(involvement.contains("b"));
  }

  /**
  *
  */
  @Test
  public void testInvolvement_2() {
    final String expression = "a*pi";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(1, involvement.size());
    Assert.assertTrue(involvement.contains("a"));
    Assert.assertFalse(involvement.contains("pi"));
  }

  /**
  *
  */
  @Test
  public void testInvolvement_3() {
    final String expression = "floor(a)";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(1, involvement.size());
    Assert.assertTrue(involvement.contains("a"));
    Assert.assertFalse(involvement.contains("floor"));
  }

  /**
  *
  */
  @Test
  public void testInvolvement_4() {
    final String expression = "stupid_function(a)";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(2, involvement.size());
    Assert.assertTrue(involvement.contains("a"));
    Assert.assertTrue(involvement.contains("stupid_function"));
  }

  /**
  *
  */
  @Test
  public void testInvolvement_5() {
    final String expression = "floor(15)";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(0, involvement.size());
    Assert.assertFalse(involvement.contains("floor"));
  }
}

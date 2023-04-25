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
package org.preesm.tests.model.scenario.types;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.preesm.model.scenario.util.DefaultTypeSizes;

/**
 *
 * @author anmorvan
 *
 */
public class DefaultTypeSizesTest {

  @Test
  public void testUnknownTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("THIS_IS_AN_UNKNOWN_TYPENAME");
    assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, defaultTypeSize);
  }

  @Test
  public void testVoidTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("void");
    assertEquals(1L, defaultTypeSize);
  }

  @Test
  public void testCharTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("char");
    assertEquals(8L, defaultTypeSize);
  }

  @Test
  public void testUnsignedCharTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("unsigned char");
    assertEquals(8L, defaultTypeSize);
  }

  @Test
  public void testShortTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("short");
    assertEquals(16L, defaultTypeSize);
  }

  @Test
  public void testUnsignedShortTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("unsigned short");
    assertEquals(16L, defaultTypeSize);
  }

  @Test
  public void testIntTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int");
    assertEquals(32L, defaultTypeSize);
  }

  @Test
  public void testUnsignedIntTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("unsigned int");
    assertEquals(32L, defaultTypeSize);
  }

  @Test
  public void testlongTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("long");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testUnsignedLongTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("unsigned long");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testLongLongTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("long long");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testUnsignedLongLongTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("unsigned long long");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testFloatTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("float");
    assertEquals(32L, defaultTypeSize);
  }

  @Test
  public void testDoubleTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("double");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testLongDoubleTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("long double");
    assertEquals(128L, defaultTypeSize);
  }

  @Test
  public void testInt8TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int8_t");
    assertEquals(8L, defaultTypeSize);
  }

  @Test
  public void testInt16TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int16_t");
    assertEquals(16L, defaultTypeSize);
  }

  @Test
  public void testInt32TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int32_t");
    assertEquals(32L, defaultTypeSize);
  }

  @Test
  public void testInt64TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int64_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testIntFast8TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int_fast8_t");
    assertEquals(8L, defaultTypeSize);
  }

  @Test
  public void testIntFast16TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int_fast16_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testIntFast32TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int_fast32_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testIntFast64TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int_fast64_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testIntLeast8TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int_least8_t");
    assertEquals(8L, defaultTypeSize);
  }

  @Test
  public void testIntLeast16TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int_least16_t");
    assertEquals(16L, defaultTypeSize);
  }

  @Test
  public void testIntLeast32TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int_least32_t");
    assertEquals(32L, defaultTypeSize);
  }

  @Test
  public void testIntLeast64TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("int_least64_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testIntmaxTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("intmax_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testIntptrTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("intptr_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testUint8TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint8_t");
    assertEquals(8L, defaultTypeSize);
  }

  @Test
  public void testUint16TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint16_t");
    assertEquals(16L, defaultTypeSize);
  }

  @Test
  public void testUint32TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint32_t");
    assertEquals(32L, defaultTypeSize);
  }

  @Test
  public void testUint64TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint64_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testUintFast8TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint_fast8_t");
    assertEquals(8L, defaultTypeSize);
  }

  @Test
  public void testUintFast16TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint_fast16_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testUintFast32TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint_fast32_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testUintFast64TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint_fast64_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testUintLeast8TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint_least8_t");
    assertEquals(8L, defaultTypeSize);
  }

  @Test
  public void testUintLeast16TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint_least16_t");
    assertEquals(16L, defaultTypeSize);
  }

  @Test
  public void testUintLeast32TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint_least32_t");
    assertEquals(32L, defaultTypeSize);
  }

  @Test
  public void testUintLeast64TypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uint_least64_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testUintmaxTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uintmax_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testUintptrTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getTypeSize("uintptr_t");
    assertEquals(64L, defaultTypeSize);
  }

  @Test
  public void testVitisApIntSize() {
    assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_int<10>"));
    assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_uint<10>"));

    assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, DefaultTypeSizes.getInstance().getTypeSize("ap_int<0>"));
    assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, DefaultTypeSizes.getInstance().getTypeSize("ap_int<-1>"));
  }

  @Test
  public void testVitisApFixedSize() {
    assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_fixed<10,5>"));
    assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,5>"));
    assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,8, AP_RND>"));
    assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,8, AP_RND, AP_SAT>"));

    assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<0,8, AP_RND>"));
    assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<-1,8, AP_RND>"));
    assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,8, ERROR>"));
  }
}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2025) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2025)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2022)
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
package org.preesm.model.scenario.types.test;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.scenario.util.DefaultTypeSizes;

/**
 *
 * @author anmorvan
 *
 */

public class DefaultTypeSizesTest {

  static Stream<Arguments> data() {
    return Stream.of(Arguments.of("DEFAULT", ScenarioConstants.DEFAULT_DATA_TYPE_SIZE_VALUE), Arguments.of("char", 8),
        Arguments.of("unsigned char", 8), Arguments.of("short", 16), Arguments.of("unsigned short", 16),
        Arguments.of("int", 32), Arguments.of("unsigned int", 32), Arguments.of("long", 64),
        Arguments.of("unsigned long", 64), Arguments.of("long long", 64), Arguments.of("unsigned long long", 64),
        Arguments.of("float", 32), Arguments.of("double", 64), Arguments.of("long double", 128),
        Arguments.of("int8_t", 8), Arguments.of("int16_t", 16), Arguments.of("int32_t", 32),
        Arguments.of("int64_t", 64), Arguments.of("int_fast8_t", 8), Arguments.of("int_fast16_t", 64),
        Arguments.of("int_fast32_t", 64), Arguments.of("int_fast64_t", 64), Arguments.of("int_least8_t", 8),
        Arguments.of("int_least16_t", 16), Arguments.of("int_least32_t", 32), Arguments.of("int_least64_t", 64),
        Arguments.of("intmax_t", 64), Arguments.of("intptr_t", 64), Arguments.of("uint8_t", 8),
        Arguments.of("uint16_t", 16), Arguments.of("uint32_t", 32), Arguments.of("uint64_t", 64),
        Arguments.of("uint_fast8_t", 8), Arguments.of("uint_fast16_t", 64), Arguments.of("uint_fast32_t", 64),
        Arguments.of("uint_fast64_t", 64), Arguments.of("uint_least8_t", 8), Arguments.of("uint_least16_t", 16),
        Arguments.of("uint_least32_t", 32), Arguments.of("uint_least64_t", 64), Arguments.of("uintmax_t", 64),
        Arguments.of("uintptr_t", 64));
  }

  @ParameterizedTest(name = "Test {1} size = {0}")
  @MethodSource("data")
  void testTypeSizeParameterized(String typeName, long expectedSize) {
    Assertions.assertEquals(expectedSize, DefaultTypeSizes.getInstance().getTypeSizeOrDefault(typeName));
  }

  @Test
  void testUnknownTypeSize() {
    final long unknownTypeSize = DefaultTypeSizes.getInstance().getTypeSize("THIS_IS_AN_UNKNOWN_TYPENAME");
    Assertions.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, unknownTypeSize);
  }

  @Test
  void testVoidTypeSize() {
    final long voidTypeSize = DefaultTypeSizes.getInstance().getTypeSize("void");
    Assertions.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, voidTypeSize);
  }

  @Test
  void testVitisApIntSize() {
    Assertions.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_int<10>"));
    Assertions.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_uint<10>"));

    Assertions.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, DefaultTypeSizes.getInstance().getTypeSize("ap_int<0>"));
    Assertions.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, DefaultTypeSizes.getInstance().getTypeSize("ap_int<-1>"));
  }

  @Test
  void testVitisApFixedSize() {
    Assertions.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_fixed<10,5>"));
    Assertions.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,5>"));
    Assertions.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,8, AP_RND>"));
    Assertions.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,8, AP_RND, AP_SAT>"));

    Assertions.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE,
        DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<0,8, AP_RND>"));
    Assertions.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE,
        DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<-1,8, AP_RND>"));
    Assertions.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE,
        DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,8, ERROR>"));
  }

}

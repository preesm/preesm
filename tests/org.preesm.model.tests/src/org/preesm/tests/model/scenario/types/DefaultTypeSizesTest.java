/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2024)
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
package org.preesm.tests.model.scenario.types;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.scenario.util.DefaultTypeSizes;

/**
 *
 * @author anmorvan
 *
 */
@RunWith(Enclosed.class)
public class DefaultTypeSizesTest {

  private DefaultTypeSizesTest() {

  }

  @RunWith(Parameterized.class)
  public static class DefaultTypeSizesParamTest {
    private final String typeName;
    private final long   expectedSize;

    public DefaultTypeSizesParamTest(String typeName, long expectedSize) {
      this.typeName = typeName;
      this.expectedSize = expectedSize;
    }

    @Parameters(name = "Test {1} size = {0}")
    public static Collection<Object[]> data() {
      final Object[][] data = new Object[][] { { "DEFAULT", ScenarioConstants.DEFAULT_DATA_TYPE_SIZE_VALUE },
          { "char", 8 }, { "unsigned char", 8 }, { "short", 16 }, { "unsigned short", 16 }, { "int", 32 },
          { "unsigned int", 32 }, { "long", 64 }, { "unsigned long", 64 }, { "long long", 64 },
          { "unsigned long long", 64 }, { "float", 32 }, { "double", 64 }, { "long double", 128 }, { "int8_t", 8 },
          { "int16_t", 16 }, { "int32_t", 32 }, { "int64_t", 64 }, { "int_fast8_t", 8 }, { "int_fast16_t", 64 },
          { "int_fast32_t", 64 }, { "int_fast64_t", 64 }, { "int_least8_t", 8 }, { "int_least16_t", 16 },
          { "int_least32_t", 32 }, { "int_least64_t", 64 }, { "intmax_t", 64 }, { "intptr_t", 64 }, { "uint8_t", 8 },
          { "uint16_t", 16 }, { "uint32_t", 32 }, { "uint64_t", 64 }, { "uint_fast8_t", 8 }, { "uint_fast16_t", 64 },
          { "uint_fast32_t", 64 }, { "uint_fast64_t", 64 }, { "uint_least8_t", 8 }, { "uint_least16_t", 16 },
          { "uint_least32_t", 32 }, { "uint_least64_t", 64 }, { "uintmax_t", 64 }, { "uintptr_t", 64 } };

      return Arrays.asList(data);
    }

    @Test
    public void testTypeSizeParameterized() {
      Assert.assertEquals(expectedSize, DefaultTypeSizes.getInstance().getTypeSizeOrDefault(typeName));
    }
  }

  public static class DefaultTypeSizesSingleTest {

    @Test
    public void testUnknownTypeSize() {
      final long unknownTypeSize = DefaultTypeSizes.getInstance().getTypeSize("THIS_IS_AN_UNKNOWN_TYPENAME");
      Assert.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, unknownTypeSize);
    }

    @Test
    public void testVoidTypeSize() {
      final long voidTypeSize = DefaultTypeSizes.getInstance().getTypeSize("void");
      Assert.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, voidTypeSize);
    }

    @Test
    public void testVitisApIntSize() {
      Assert.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_int<10>"));
      Assert.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_uint<10>"));

      Assert.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, DefaultTypeSizes.getInstance().getTypeSize("ap_int<0>"));
      Assert.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE, DefaultTypeSizes.getInstance().getTypeSize("ap_int<-1>"));
    }

    @Test
    public void testVitisApFixedSize() {
      Assert.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_fixed<10,5>"));
      Assert.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,5>"));
      Assert.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,8, AP_RND>"));
      Assert.assertEquals(10L, DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,8, AP_RND, AP_SAT>"));

      Assert.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE,
          DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<0,8, AP_RND>"));
      Assert.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE,
          DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<-1,8, AP_RND>"));
      Assert.assertEquals(DefaultTypeSizes.UNKNOWN_TYPE,
          DefaultTypeSizes.getInstance().getTypeSize("ap_ufixed<10,8, ERROR>"));
    }
  }
}

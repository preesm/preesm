/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2024) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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

package org.preesm.model.pisdf.test;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.xtext.xbase.lib.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.preesm.model.pisdf.check.RefinementChecker;

@RunWith(Parameterized.class)
public class RefinementCheckerTest {

  private final String               input;
  private final Pair<String, String> expected;

  public RefinementCheckerTest(String input, Pair<String, String> expected) {
    this.input = input;
    this.expected = expected;
  }

  @Parameters(name = "Expression: {0}, expected = {1}")
  public static Collection<Object[]> data() {
    final Object[][] data = new Object[][] { { "hls::stream<char>", new Pair<>("char", null) },
        { "hls::stream<unsigned char>", new Pair<>("unsigned char", null) },
        { "hls::stream<ap_uint<10>>", new Pair<>("ap_uint<10>", null) },
        { "hls::stream<ap_fixed<32,16>>", new Pair<>("ap_fixed<32,16>", null) },
        { "hls::stream<filter_t>", new Pair<>("filter_t", null) },
        { "hls::stream<char, PARAM1>", new Pair<>("char", "PARAM1") },
        { "hls::stream<unsigned char, 20>", new Pair<>("unsigned char", "20") },
        { "hls::stream<ap_uint<10>, PARAM2>", new Pair<>("ap_uint<10>", "PARAM2") },
        { "hls::stream<ap_fixed<32,16>, PARAM3>", new Pair<>("ap_fixed<32,16>", "PARAM3") },
        { "hls::stream<filter_t, PARAM4>", new Pair<>("filter_t", "PARAM4") } };

    return Arrays.asList(data);
  }

  @Test
  public void hlsStreamTemplateCheckTest() {
    final Pair<String, String> result = RefinementChecker.isHlsStreamTemplated(input);
    Assert.assertEquals(expected, result);
  }

}

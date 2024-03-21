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

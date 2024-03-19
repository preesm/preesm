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
        { "hls::stream<char, PARAMETER>", new Pair<>("char", "PARAMETER") },
        { "hls::stream<unsigned char, 20>", new Pair<>("unsigned char", "20") },
        { "hls::stream<ap_uint<10>, PARAMETER>", new Pair<>("ap_uint<10>", "PARAMETER") },
        { "hls::stream<ap_fixed<32,16>, PARAMETER>", new Pair<>("ap_fixed<32,16>", "PARAMETER") },
        { "hls::stream<filter_t, PARAMETER>", new Pair<>("filter_t", "PARAMETER") } };

    return Arrays.asList(data);
  }

  @Test
  public void hlsStreamTemplateCheckTest() {
    final Pair<String, String> result = RefinementChecker.isHlsStreamTemplated(input);
    Assert.assertEquals(expected, result);
  }

}

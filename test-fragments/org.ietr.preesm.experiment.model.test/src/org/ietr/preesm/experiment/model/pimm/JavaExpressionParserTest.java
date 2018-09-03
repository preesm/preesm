/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.preesm.experiment.model.pimm;

import org.junit.Assert;
import org.junit.Test;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

/**
 * This class was generated using the script expressionTracesGenerator.sh and the traces from expression.traces.
 *
 * @generated
 */
public class JavaExpressionParserTest {

  // 0:[]
  @Test
  public void testExpression1() throws ParseException {
    final String expr = "0";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[]
  @Test
  public void testExpression2() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 12:[]
  @Test
  public void testExpression3() throws ParseException {
    final String expr = "12";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 129600:[height=270.0, width=480.0]
  @Test
  public void testExpression4() throws ParseException {
    final String expr = "129600";
    final JEP jep = new JEP();
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 137:[overlap=1.0, height=270.0, nbSlice=2.0]
  @Test
  public void testExpression5() throws ParseException {
    final String expr = "137";
    final JEP jep = new JEP();
    jep.addVariable("overlap", 1.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("nbSlice", 2.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 164920:[width=434.0, height=380.0]
  @Test
  public void testExpression6() throws ParseException {
    final String expr = "164920";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 16:[maxDisparity=8.0, minDisparity=-8.0]
  @Test
  public void testExpression7() throws ParseException {
    final String expr = "16";
    final JEP jep = new JEP();
    jep.addVariable("maxDisparity", 8.0);
    jep.addVariable("minDisparity", -8.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 19:[]
  @Test
  public void testExpression8() throws ParseException {
    final String expr = "19";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 19:[minDisparity=0.0, maxDisparity=19.0]
  @Test
  public void testExpression9() throws ParseException {
    final String expr = "19";
    final JEP jep = new JEP();
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("maxDisparity", 19.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[height=270.0, truncValue=12.0, width=480.0]
  @Test
  public void testExpression10() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    jep.addVariable("height", 270.0);
    jep.addVariable("truncValue", 12.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[horOrVert=0.0, height=270.0, width=480.0]
  @Test
  public void testExpression11() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    jep.addVariable("horOrVert", 0.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[horOrVert=1.0, height=270.0, width=480.0]
  @Test
  public void testExpression12() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    jep.addVariable("horOrVert", 1.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[nbDisparities=19.0, width=434.0, scale=4.0, minDisparity=0.0, height=380.0]
  @Test
  public void testExpression13() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    jep.addVariable("nbDisparities", 19.0);
    jep.addVariable("width", 434.0);
    jep.addVariable("scale", 4.0);
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[scale=12.0, minDisparity=-8.0, height=270.0, nbDisparities=16.0, width=480.0]
  @Test
  public void testExpression14() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    jep.addVariable("scale", 12.0);
    jep.addVariable("minDisparity", -8.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("nbDisparities", 16.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[size=129600.0]
  @Test
  public void testExpression15() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    jep.addVariable("size", 129600.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[size=164920.0]
  @Test
  public void testExpression16() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    jep.addVariable("size", 164920.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[width=434.0, height=380.0, truncValue=12.0]
  @Test
  public void testExpression17() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("height", 380.0);
    jep.addVariable("truncValue", 12.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[width=434.0, horOrVert=0.0, height=380.0]
  @Test
  public void testExpression18() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("horOrVert", 0.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[width=434.0, horOrVert=1.0, height=380.0]
  @Test
  public void testExpression19() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("horOrVert", 1.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 1:[width=434.0, scale=4.0, minDisparity=0.0, nbDisparities=4.0, height=380.0]
  @Test
  public void testExpression20() throws ParseException {
    final String expr = "1";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("scale", 4.0);
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("nbDisparities", 4.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 2:[]
  @Test
  public void testExpression21() throws ParseException {
    final String expr = "2";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 20:[]
  @Test
  public void testExpression22() throws ParseException {
    final String expr = "20";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 21:[overlap=1.0, height=380.0, nbSlice=20.0]
  @Test
  public void testExpression23() throws ParseException {
    final String expr = "21";
    final JEP jep = new JEP();
    jep.addVariable("overlap", 1.0);
    jep.addVariable("height", 380.0);
    jep.addVariable("nbSlice", 20.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 270:[]
  @Test
  public void testExpression24() throws ParseException {
    final String expr = "270";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 380:[]
  @Test
  public void testExpression25() throws ParseException {
    final String expr = "380";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 3*height*width:[width=434.0, id=1.0, height=380.0]
  @Test
  public void testExpression26() throws ParseException {
    final String expr = "3*height*width";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("id", 1.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 3*size:[size=129600.0]
  @Test
  public void testExpression27() throws ParseException {
    final String expr = "3*size";
    final JEP jep = new JEP();
    jep.addVariable("size", 129600.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 3*size:[size=164920.0]
  @Test
  public void testExpression28() throws ParseException {
    final String expr = "3*size";
    final JEP jep = new JEP();
    jep.addVariable("size", 164920.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 4:[]
  @Test
  public void testExpression29() throws ParseException {
    final String expr = "4";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 434:[]
  @Test
  public void testExpression30() throws ParseException {
    final String expr = "434";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 480:[]
  @Test
  public void testExpression31() throws ParseException {
    final String expr = "480";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 4:[minDisparity=0.0, maxDisparity=4.0]
  @Test
  public void testExpression32() throws ParseException {
    final String expr = "4";
    final JEP jep = new JEP();
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("maxDisparity", 4.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 5:[]
  @Test
  public void testExpression33() throws ParseException {
    final String expr = "5";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // -8:[]
  @Test
  public void testExpression34() throws ParseException {
    final String expr = "-8";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // 8:[]
  @Test
  public void testExpression35() throws ParseException {
    final String expr = "8";
    final JEP jep = new JEP();
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height/nbSlice+2*overlap:[overlap=1.0, height=270.0, nbSlice=2.0]
  @Test
  public void testExpression36() throws ParseException {
    final String expr = "height/nbSlice+2*overlap";
    final JEP jep = new JEP();
    jep.addVariable("overlap", 1.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("nbSlice", 2.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height/nbSlice+2*overlap:[overlap=1.0, height=380.0, nbSlice=20.0]
  @Test
  public void testExpression37() throws ParseException {
    final String expr = "height/nbSlice+2*overlap";
    final JEP jep = new JEP();
    jep.addVariable("overlap", 1.0);
    jep.addVariable("height", 380.0);
    jep.addVariable("nbSlice", 20.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width+1:[nbDisparities=19.0, width=434.0, scale=4.0, minDisparity=0.0, height=380.0]
  @Test
  public void testExpression38() throws ParseException {
    final String expr = "height*width+1";
    final JEP jep = new JEP();
    jep.addVariable("nbDisparities", 19.0);
    jep.addVariable("width", 434.0);
    jep.addVariable("scale", 4.0);
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width+1:[scale=12.0, minDisparity=-8.0, height=270.0, nbDisparities=16.0, width=480.0]
  @Test
  public void testExpression39() throws ParseException {
    final String expr = "height*width+1";
    final JEP jep = new JEP();
    jep.addVariable("scale", 12.0);
    jep.addVariable("minDisparity", -8.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("nbDisparities", 16.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width+1:[width=434.0, scale=4.0, minDisparity=0.0, nbDisparities=4.0, height=380.0]
  @Test
  public void testExpression40() throws ParseException {
    final String expr = "height*width+1";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("scale", 4.0);
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("nbDisparities", 4.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width-2*topDownBorderSize*width:[topDownBorderSize=1.0, height=137.0, width=480.0]
  @Test
  public void testExpression41() throws ParseException {
    final String expr = "height*width-2*topDownBorderSize*width";
    final JEP jep = new JEP();
    jep.addVariable("topDownBorderSize", 1.0);
    jep.addVariable("height", 137.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width-2*topDownBorderSize*width:[width=434.0, topDownBorderSize=1.0, height=21.0]
  @Test
  public void testExpression42() throws ParseException {
    final String expr = "height*width-2*topDownBorderSize*width";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("topDownBorderSize", 1.0);
    jep.addVariable("height", 21.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width*3:[horOrVert=0.0, height=270.0, width=480.0]
  @Test
  public void testExpression43() throws ParseException {
    final String expr = "height*width*3";
    final JEP jep = new JEP();
    jep.addVariable("horOrVert", 0.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width*3:[horOrVert=1.0, height=270.0, width=480.0]
  @Test
  public void testExpression44() throws ParseException {
    final String expr = "height*width*3";
    final JEP jep = new JEP();
    jep.addVariable("horOrVert", 1.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width*3:[id=0.0, height=270.0, width=480.0]
  @Test
  public void testExpression45() throws ParseException {
    final String expr = "height*width*3";
    final JEP jep = new JEP();
    jep.addVariable("id", 0.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width*3:[maxDisparity=8.0, scale=12.0, nbIterations=5.0, minDisparity=-8.0, height=270.0, truncValue=12.0,
  // width=480.0]
  @Test
  public void testExpression46() throws ParseException {
    final String expr = "height*width*3";
    final JEP jep = new JEP();
    jep.addVariable("maxDisparity", 8.0);
    jep.addVariable("scale", 12.0);
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("minDisparity", -8.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("truncValue", 12.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width*3:[nbIterations=5.0, scale=4.0, width=434.0, maxDisparity=4.0, minDisparity=0.0, height=380.0,
  // truncValue=12.0]
  @Test
  public void testExpression47() throws ParseException {
    final String expr = "height*width*3";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("scale", 4.0);
    jep.addVariable("width", 434.0);
    jep.addVariable("maxDisparity", 4.0);
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("height", 380.0);
    jep.addVariable("truncValue", 12.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width*3:[nbIterations=5.0, scale=4.0, width=434.0, minDisparity=0.0, height=380.0, maxDisparity=19.0,
  // truncValue=12.0]
  @Test
  public void testExpression48() throws ParseException {
    final String expr = "height*width*3";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("scale", 4.0);
    jep.addVariable("width", 434.0);
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("height", 380.0);
    jep.addVariable("maxDisparity", 19.0);
    jep.addVariable("truncValue", 12.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width*3*nbIterations:[nbIterations=5.0, height=270.0, width=480.0]
  @Test
  public void testExpression49() throws ParseException {
    final String expr = "height*width*3*nbIterations";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width*3*nbIterations:[nbIterations=5.0, width=434.0, height=380.0]
  @Test
  public void testExpression50() throws ParseException {
    final String expr = "height*width*3*nbIterations";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("width", 434.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width*3:[width=434.0, horOrVert=0.0, height=380.0]
  @Test
  public void testExpression51() throws ParseException {
    final String expr = "height*width*3";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("horOrVert", 0.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width*3:[width=434.0, horOrVert=1.0, height=380.0]
  @Test
  public void testExpression52() throws ParseException {
    final String expr = "height*width*3";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("horOrVert", 1.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width*3:[width=434.0, id=0.0, height=380.0]
  @Test
  public void testExpression53() throws ParseException {
    final String expr = "height*width*3";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("id", 0.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width/4:[height=270.0, width=480.0]
  @Test
  public void testExpression54() throws ParseException {
    final String expr = "height*width/4";
    final JEP jep = new JEP();
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[height=270.0, truncValue=12.0, width=480.0]
  @Test
  public void testExpression55() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("height", 270.0);
    jep.addVariable("truncValue", 12.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[height=270.0, width=480.0]
  @Test
  public void testExpression56() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[id=1.0, height=270.0, width=480.0]
  @Test
  public void testExpression57() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("id", 1.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[maxDisparity=8.0, scale=12.0, nbIterations=5.0, minDisparity=-8.0, height=270.0, truncValue=12.0,
  // width=480.0]
  @Test
  public void testExpression58() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("maxDisparity", 8.0);
    jep.addVariable("scale", 12.0);
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("minDisparity", -8.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("truncValue", 12.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[nbDisparities=19.0, width=434.0, scale=4.0, minDisparity=0.0, height=380.0]
  @Test
  public void testExpression59() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("nbDisparities", 19.0);
    jep.addVariable("width", 434.0);
    jep.addVariable("scale", 4.0);
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[nbIterations=5.0, height=270.0, width=480.0]
  @Test
  public void testExpression60() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[nbIterations=5.0, scale=4.0, width=434.0, maxDisparity=4.0, minDisparity=0.0, height=380.0,
  // truncValue=12.0]
  @Test
  public void testExpression61() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("scale", 4.0);
    jep.addVariable("width", 434.0);
    jep.addVariable("maxDisparity", 4.0);
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("height", 380.0);
    jep.addVariable("truncValue", 12.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[nbIterations=5.0, scale=4.0, width=434.0, minDisparity=0.0, height=380.0, maxDisparity=19.0,
  // truncValue=12.0]
  @Test
  public void testExpression62() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("scale", 4.0);
    jep.addVariable("width", 434.0);
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("height", 380.0);
    jep.addVariable("maxDisparity", 19.0);
    jep.addVariable("truncValue", 12.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[nbIterations=5.0, width=434.0, height=380.0]
  @Test
  public void testExpression63() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("width", 434.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[overlap=1.0, height=270.0, nbSlice=2.0, width=480.0]
  @Test
  public void testExpression64() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("overlap", 1.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("nbSlice", 2.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[scale=12.0, minDisparity=-8.0, height=270.0, nbDisparities=16.0, width=480.0]
  @Test
  public void testExpression65() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("scale", 12.0);
    jep.addVariable("minDisparity", -8.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("nbDisparities", 16.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[width=434.0, height=380.0]
  @Test
  public void testExpression66() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[width=434.0, height=380.0, truncValue=12.0]
  @Test
  public void testExpression67() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("height", 380.0);
    jep.addVariable("truncValue", 12.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[width=434.0, id=1.0, height=380.0]
  @Test
  public void testExpression68() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("id", 1.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[width=434.0, overlap=1.0, height=380.0, nbSlice=20.0]
  @Test
  public void testExpression69() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("overlap", 1.0);
    jep.addVariable("height", 380.0);
    jep.addVariable("nbSlice", 20.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // height*width:[width=434.0, scale=4.0, minDisparity=0.0, nbDisparities=4.0, height=380.0]
  @Test
  public void testExpression70() throws ParseException {
    final String expr = "height*width";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("scale", 4.0);
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("nbDisparities", 4.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // maxDisparity-minDisparity:[maxDisparity=8.0, minDisparity=-8.0]
  @Test
  public void testExpression71() throws ParseException {
    final String expr = "maxDisparity-minDisparity";
    final JEP jep = new JEP();
    jep.addVariable("maxDisparity", 8.0);
    jep.addVariable("minDisparity", -8.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // maxDisparity-minDisparity:[minDisparity=0.0, maxDisparity=19.0]
  @Test
  public void testExpression72() throws ParseException {
    final String expr = "maxDisparity-minDisparity";
    final JEP jep = new JEP();
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("maxDisparity", 19.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // maxDisparity-minDisparity:[minDisparity=0.0, maxDisparity=4.0]
  @Test
  public void testExpression73() throws ParseException {
    final String expr = "maxDisparity-minDisparity";
    final JEP jep = new JEP();
    jep.addVariable("minDisparity", 0.0);
    jep.addVariable("maxDisparity", 4.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbDisparity:[nbDisparity=16.0]
  @Test
  public void testExpression74() throws ParseException {
    final String expr = "nbDisparity";
    final JEP jep = new JEP();
    jep.addVariable("nbDisparity", 16.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbDisparity:[nbDisparity=19.0]
  @Test
  public void testExpression75() throws ParseException {
    final String expr = "nbDisparity";
    final JEP jep = new JEP();
    jep.addVariable("nbDisparity", 19.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbDisparity:[nbDisparity=4.0]
  @Test
  public void testExpression76() throws ParseException {
    final String expr = "nbDisparity";
    final JEP jep = new JEP();
    jep.addVariable("nbDisparity", 4.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbIterations:[nbDisparity=4.0, nbIterations=5.0]
  @Test
  public void testExpression77() throws ParseException {
    final String expr = "nbIterations";
    final JEP jep = new JEP();
    jep.addVariable("nbDisparity", 4.0);
    jep.addVariable("nbIterations", 5.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbIterations*nbDisparity:[nbDisparity=4.0, nbIterations=5.0]
  @Test
  public void testExpression78() throws ParseException {
    final String expr = "nbIterations*nbDisparity";
    final JEP jep = new JEP();
    jep.addVariable("nbDisparity", 4.0);
    jep.addVariable("nbIterations", 5.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbIterations*nbDisparity:[nbIterations=5.0, nbDisparity=16.0]
  @Test
  public void testExpression79() throws ParseException {
    final String expr = "nbIterations*nbDisparity";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("nbDisparity", 16.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbIterations*nbDisparity:[nbIterations=5.0, nbDisparity=19.0]
  @Test
  public void testExpression80() throws ParseException {
    final String expr = "nbIterations*nbDisparity";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("nbDisparity", 19.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbIterations:[nbIterations=5.0]
  @Test
  public void testExpression81() throws ParseException {
    final String expr = "nbIterations";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbIterations:[nbIterations=5.0, height=270.0, width=480.0]
  @Test
  public void testExpression82() throws ParseException {
    final String expr = "nbIterations";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbIterations:[nbIterations=5.0, nbDisparity=16.0]
  @Test
  public void testExpression83() throws ParseException {
    final String expr = "nbIterations";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("nbDisparity", 16.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbIterations:[nbIterations=5.0, nbDisparity=19.0]
  @Test
  public void testExpression84() throws ParseException {
    final String expr = "nbIterations";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("nbDisparity", 19.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbIterations:[nbIterations=5.0, width=434.0, height=380.0]
  @Test
  public void testExpression85() throws ParseException {
    final String expr = "nbIterations";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("width", 434.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbSlice*(height*width/nbSlice+2*overlap*width):[overlap=1.0, height=270.0, nbSlice=2.0, width=480.0]
  @Test
  public void testExpression86() throws ParseException {
    final String expr = "nbSlice*(height*width/nbSlice+2*overlap*width)";
    final JEP jep = new JEP();
    jep.addVariable("overlap", 1.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("nbSlice", 2.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // nbSlice*(height*width/nbSlice+2*overlap*width):[width=434.0, overlap=1.0, height=380.0, nbSlice=20.0]
  @Test
  public void testExpression87() throws ParseException {
    final String expr = "nbSlice*(height*width/nbSlice+2*overlap*width)";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("overlap", 1.0);
    jep.addVariable("height", 380.0);
    jep.addVariable("nbSlice", 20.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size+1:[size=129600.0]
  @Test
  public void testExpression88() throws ParseException {
    final String expr = "size+1";
    final JEP jep = new JEP();
    jep.addVariable("size", 129600.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size+1:[size=164920.0]
  @Test
  public void testExpression89() throws ParseException {
    final String expr = "size+1";
    final JEP jep = new JEP();
    jep.addVariable("size", 164920.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size*3*nbIterations:[nbDisparity=4.0, nbIterations=5.0, size=164920.0]
  @Test
  public void testExpression90() throws ParseException {
    final String expr = "size*3*nbIterations";
    final JEP jep = new JEP();
    jep.addVariable("nbDisparity", 4.0);
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("size", 164920.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size*3*nbIterations*nbDisparity:[nbDisparity=4.0, nbIterations=5.0, size=164920.0]
  @Test
  public void testExpression91() throws ParseException {
    final String expr = "size*3*nbIterations*nbDisparity";
    final JEP jep = new JEP();
    jep.addVariable("nbDisparity", 4.0);
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("size", 164920.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size*3*nbIterations*nbDisparity:[nbIterations=5.0, nbDisparity=16.0, size=129600.0]
  @Test
  public void testExpression92() throws ParseException {
    final String expr = "size*3*nbIterations*nbDisparity";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("nbDisparity", 16.0);
    jep.addVariable("size", 129600.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size*3*nbIterations*nbDisparity:[nbIterations=5.0, nbDisparity=19.0, size=164920.0]
  @Test
  public void testExpression93() throws ParseException {
    final String expr = "size*3*nbIterations*nbDisparity";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("nbDisparity", 19.0);
    jep.addVariable("size", 164920.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size*3*nbIterations:[nbIterations=5.0, nbDisparity=16.0, size=129600.0]
  @Test
  public void testExpression94() throws ParseException {
    final String expr = "size*3*nbIterations";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("nbDisparity", 16.0);
    jep.addVariable("size", 129600.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size*3*nbIterations:[nbIterations=5.0, nbDisparity=19.0, size=164920.0]
  @Test
  public void testExpression95() throws ParseException {
    final String expr = "size*3*nbIterations";
    final JEP jep = new JEP();
    jep.addVariable("nbIterations", 5.0);
    jep.addVariable("nbDisparity", 19.0);
    jep.addVariable("size", 164920.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size*3:[size=129600.0]
  @Test
  public void testExpression96() throws ParseException {
    final String expr = "size*3";
    final JEP jep = new JEP();
    jep.addVariable("size", 129600.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size*3:[size=164920.0]
  @Test
  public void testExpression97() throws ParseException {
    final String expr = "size*3";
    final JEP jep = new JEP();
    jep.addVariable("size", 164920.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size:[size=129600.0]
  @Test
  public void testExpression98() throws ParseException {
    final String expr = "size";
    final JEP jep = new JEP();
    jep.addVariable("size", 129600.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // size:[size=164920.0]
  @Test
  public void testExpression99() throws ParseException {
    final String expr = "size";
    final JEP jep = new JEP();
    jep.addVariable("size", 164920.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // width*height*3:[height=270.0, width=480.0]
  @Test
  public void testExpression100() throws ParseException {
    final String expr = "width*height*3";
    final JEP jep = new JEP();
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // width*height*3:[horOrVert=0.0, height=270.0, width=480.0]
  @Test
  public void testExpression101() throws ParseException {
    final String expr = "width*height*3";
    final JEP jep = new JEP();
    jep.addVariable("horOrVert", 0.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // width*height*3:[horOrVert=1.0, height=270.0, width=480.0]
  @Test
  public void testExpression102() throws ParseException {
    final String expr = "width*height*3";
    final JEP jep = new JEP();
    jep.addVariable("horOrVert", 1.0);
    jep.addVariable("height", 270.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // width*height*3:[width=434.0, horOrVert=0.0, height=380.0]
  @Test
  public void testExpression103() throws ParseException {
    final String expr = "width*height*3";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("horOrVert", 0.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // width*height*3:[width=434.0, horOrVert=1.0, height=380.0]
  @Test
  public void testExpression104() throws ParseException {
    final String expr = "width*height*3";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("horOrVert", 1.0);
    jep.addVariable("height", 380.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // width*height:[topDownBorderSize=1.0, height=137.0, width=480.0]
  @Test
  public void testExpression105() throws ParseException {
    final String expr = "width*height";
    final JEP jep = new JEP();
    jep.addVariable("topDownBorderSize", 1.0);
    jep.addVariable("height", 137.0);
    jep.addVariable("width", 480.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // width*height:[width=434.0, topDownBorderSize=1.0, height=21.0]
  @Test
  public void testExpression106() throws ParseException {
    final String expr = "width*height";
    final JEP jep = new JEP();
    jep.addVariable("width", 434.0);
    jep.addVariable("topDownBorderSize", 1.0);
    jep.addVariable("height", 21.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // xSize*ySize/4:[xSize=480.0, ySize=270.0, id=0.0]
  @Test
  public void testExpression107() throws ParseException {
    final String expr = "xSize*ySize/4";
    final JEP jep = new JEP();
    jep.addVariable("xSize", 480.0);
    jep.addVariable("ySize", 270.0);
    jep.addVariable("id", 0.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // xSize*ySize/4:[xSize=480.0, ySize=270.0, id=1.0]
  @Test
  public void testExpression108() throws ParseException {
    final String expr = "xSize*ySize/4";
    final JEP jep = new JEP();
    jep.addVariable("xSize", 480.0);
    jep.addVariable("ySize", 270.0);
    jep.addVariable("id", 1.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // xSize*ySize:[xSize=480.0, ySize=270.0, id=0.0]
  @Test
  public void testExpression109() throws ParseException {
    final String expr = "xSize*ySize";
    final JEP jep = new JEP();
    jep.addVariable("xSize", 480.0);
    jep.addVariable("ySize", 270.0);
    jep.addVariable("id", 0.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }

  // xSize*ySize:[xSize=480.0, ySize=270.0, id=1.0]
  @Test
  public void testExpression110() throws ParseException {
    final String expr = "xSize*ySize";
    final JEP jep = new JEP();
    jep.addVariable("xSize", 480.0);
    jep.addVariable("ySize", 270.0);
    jep.addVariable("id", 1.0);
    final Node parse = jep.parse(expr);
    final Object evaluate = jep.evaluate(parse);
    Assert.assertTrue(evaluate instanceof Double);
  }
}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2025) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2018)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023 - 2025)
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
package org.preesm.model.pisdf.test;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 *
 */
public class JEPCustomFunctionTest {

  static Stream<Arguments> data() {
    return Stream.of(Arguments.of("floor(0)", 0L), Arguments.of("floor(0)", 0L), Arguments.of("floor(-1)", -1L),
        Arguments.of("floor(1.2)", 1L), Arguments.of("floor(1.9)", 1L), Arguments.of("floor(-1.9)", -2L),
        Arguments.of("floor(-1.1)", -2L), Arguments.of("floor(-2.1)", -3L), Arguments.of("floor(5/2)", 2L),
        Arguments.of("floor(5/-2)", -3L), Arguments.of("ceil(5/2)", 3L), Arguments.of("min(min(1,-3),-2)", -3L),
        Arguments.of("max(10, max(1,-3))", 10L), Arguments.of("1000*geo_sum(3,1/2,4)", 5625L),
        Arguments.of("geo_sum(3,2,4)", 45L), Arguments.of("geo_sum(512000,0.25,7)", 682625L),
        Arguments.of("pow_div_max(2, 72)", 3L), Arguments.of("pow_div_max(2, 73)", 0L));
  }

  @ParameterizedTest(name = "Expression: {0}, expected = {1}")
  @MethodSource("data")
  void testEvaluation(String input, long expected) {
    final Expression createExpression = PiMMUserFactory.instance.createExpression(input);
    final long evaluate = createExpression.evaluateAsLong();

    Assertions.assertEquals(expected, evaluate);
  }

}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2025) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024 - 2025)
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

import java.io.IOException;
import java.net.URL;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.model.pisdf.util.PiSDFXSDValidator;
import org.preesm.model.pisdf.util.PiSDFXSDValidator.PiSDFXSDValidationException;

/**
 *
 * @author anmorvan
 *
 */
public class XSDValidationTest {

  static Stream<Arguments> data() {
    return Stream.of(Arguments.of("actor_mlp.pi"), Arguments.of("top_display.pi"), Arguments.of("adam.pi"),
        Arguments.of("layer_gradients.pi"), Arguments.of("mlp_raw.pi"), Arguments.of("mlp.pi"),
        Arguments.of("network_train.pi"), Arguments.of("output_gradients.pi"), Arguments.of("prediction.pi"),
        Arguments.of("training.pi"), Arguments.of("weight_generator.pi"));
  }

  @ParameterizedTest
  @MethodSource("data")
  void testPiSDFValidation(final String pisdfName) throws IOException {
    final URL pisdURL = PreesmResourcesHelper.getInstance().resolve("pisdf/" + pisdfName, XSDValidationTest.class);

    Assertions.assertNotNull(pisdURL, "Could not resolve PiSDF resource: " + pisdfName);

    try {
      PiSDFXSDValidator.validate(pisdURL);
    } catch (final PiSDFXSDValidationException e) {
      e.printStackTrace();
      Assertions.fail("Error on " + pisdURL.getFile() + ":\n" + e.getCause().getMessage() + ":" + e.getMessage());
    }
  }
}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
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
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.model.pisdf.util.PiSDFXSDValidator;
import org.preesm.model.pisdf.util.PiSDFXSDValidator.PiSDFXSDValidationException;

/**
 *
 * @author anmorvan
 *
 */
@RunWith(Parameterized.class)
public class XSDValidationTest {

  private final URL pisdURL;

  /**
   *
   */
  public XSDValidationTest(final String pisdfName) {
    this.pisdURL = PreesmResourcesHelper.getInstance().resolve("pisdf/" + pisdfName, XSDValidationTest.class);
  }

  /**
   *
   */
  @Parameters
  public static Collection<Object[]> data() {
    final Object[][] data = new Object[][] { { "actor_mlp.pi" }, { "top_display.pi" }, { "adam.pi" },
        { "layer_gradients.pi" }, { "mlp_raw.pi" }, { "mlp.pi" }, { "network_train.pi" }, { "output_gradients.pi" },
        { "prediction.pi" }, { "training.pi" }, { "weight_generator.pi" } };

    return Arrays.asList(data);
  }

  @Test
  public void testPiSDFValidation() throws IOException {
    if (this.pisdURL == null) {
      throw new NullPointerException();
    }
    try {
      PiSDFXSDValidator.validate(this.pisdURL);
    } catch (final PiSDFXSDValidationException e) {
      e.printStackTrace();
      Assert.fail("Error on " + pisdURL.getFile() + ":\n" + e.getCause().getMessage() + ":" + e.getMessage());
    }
  }
}

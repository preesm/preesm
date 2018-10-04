package org.ietr.preesm.experiment.model.pimm;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import org.ietr.preesm.experiment.model.pimm.util.PiSDFXSDValidator;
import org.ietr.preesm.experiment.model.pimm.util.PiSDFXSDValidator.PiSDFXSDValidationException;
import org.ietr.preesm.experiment.model.pimm.util.URLResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author anmorvan
 *
 */
@RunWith(Parameterized.class)
public class XSDValidationTest {

  private final URL pisdURL;

  public XSDValidationTest(final String pisdfName) {
    this.pisdURL = URLResolver.findFirstInBundleList("resources/pisdf/" + pisdfName,
        "org.ietr.preesm.experiment.model.test");
  }

  /**
   *
   */
  @Parameters
  public static Collection<Object[]> data() {
    Object[][] data = new Object[][] {

        { "actor_mlp.pi" },

        { "top_display.pi" }

    };
    return Arrays.asList(data);
  }

  @Test
  public void testPiSDFValidation() throws IOException {
    if (this.pisdURL == null) {
      throw new NullPointerException();
    }
    try {
      PiSDFXSDValidator.validate(this.pisdURL);
    } catch (PiSDFXSDValidationException e) {
      e.printStackTrace();
      System.out.println("Error on " + pisdURL.getFile() + ":\n" + e.getCause().getMessage());
      Assert.fail(e.getMessage());
    }
  }
}

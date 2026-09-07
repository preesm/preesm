package org.ietr.preesm.test.it.cli;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.preesm.cli.DocGenApplication;

class DocGenCliTest {

  @Test
  void start_withArgs_runsCli() throws Exception {
    final java.nio.file.Path createTempDirectory = Files.createTempDirectory("TEST_DOC_");
    final java.nio.file.Path filePath = createTempDirectory.resolve("testDoc.md");

    createTempDirectory.toFile().deleteOnExit();
    filePath.toFile().deleteOnExit();

    final DocGenApplication docGenApp = new DocGenApplication();
    final IApplicationContext context = new FakeApplicationContext(new String[] { "-mdd", filePath.toString() });

    final Object result = docGenApp.start(context);

    Assertions.assertEquals(IApplication.EXIT_OK, result);
  }

  @Test
  void start_withoutArgs_runsCli() throws Exception {
    final DocGenApplication docGenApp = new DocGenApplication();
    final IApplicationContext context = new FakeApplicationContext(new String[0]);

    final Object result = docGenApp.start(context);

    Assertions.assertEquals(IApplication.EXIT_OK, result);
  }

  protected class FakeApplicationContext implements IApplicationContext {

    private final Map<String, Object> args = new HashMap<>();

    protected FakeApplicationContext(String[] applicationArgs) {
      args.put(APPLICATION_ARGS, applicationArgs);
    }

    @Override
    public Map<String, Object> getArguments() {
      return args;
    }

    @Override
    public void applicationRunning() {
      // Unused
    }

    @Override
    public String getBrandingApplication() {
      return null;
    }

    @Override
    public String getBrandingName() {
      return null;
    }

    @Override
    public String getBrandingDescription() {
      return null;
    }

    @Override
    public String getBrandingId() {
      return null;
    }

    @Override
    public String getBrandingProperty(String key) {
      return null;
    }

    @Override
    public Bundle getBrandingBundle() {
      return null;
    }

    @Override
    public void setResult(Object result, IApplication application) {
      // Unused
    }

  }

}

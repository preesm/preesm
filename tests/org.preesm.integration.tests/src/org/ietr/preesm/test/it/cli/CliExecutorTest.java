package org.ietr.preesm.test.it.cli;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.preesm.cli.CLIWorkflowExecutor;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.files.URLHelper;
import org.preesm.commons.logger.PreesmLogger;

class CliExecutorTest {

  private IProject   project;
  java.nio.file.Path createTempDirectory;

  @Test
  void start_withArgs_runsCli() throws Exception {

    final String projectName = "org.ietr.preesm.tutorials.tutorial1";

    setUp(projectName);

    try {
      final CLIWorkflowExecutor cliExecApp = new CLIWorkflowExecutor();
      final IApplicationContext context = new FakeApplicationContext(new String[] { projectName });

      final Object result = cliExecApp.start(context);

      Assertions.assertEquals(IApplication.EXIT_OK, result);
    } finally {
      tearDown();
    }
  }

  @Test
  void start_withoutArgs_runsCli() throws Exception {
    final CLIWorkflowExecutor cliExecApp = new CLIWorkflowExecutor();
    final IApplicationContext context = new FakeApplicationContext(new String[0]);

    final Object result = cliExecApp.start(context);

    Assertions.assertEquals(IApplication.EXIT_OK, result);
  }

  private void setUp(String projectName) throws CoreException, IOException, URISyntaxException {

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();
    final IProjectDescription newProjectDescription = workspace.newProjectDescription(projectName);
    createTempDirectory = Files.createTempDirectory("PREESM_TESTS_");
    newProjectDescription.setLocationURI(createTempDirectory.toUri());
    project = root.getProject(projectName);
    project.create(newProjectDescription, null);
    project.open(null);

    final Logger logger = PreesmLogger.getLogger();
    logger.setLevel(Level.OFF);

    URL resolve;
    resolve = PreesmResourcesHelper.getInstance().resolve(projectName, CliExecutorTest.class);

    URLHelper.copyContent(resolve, project);
  }

  private void tearDown() throws CoreException, IOException {
    project.close(null);
    final Stream<java.nio.file.Path> walk = Files.walk(createTempDirectory, FileVisitOption.FOLLOW_LINKS);
    walk.sorted(Comparator.reverseOrder()).map(java.nio.file.Path::toFile).forEach(File::delete);
    walk.close();
    project.delete(true, null);
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

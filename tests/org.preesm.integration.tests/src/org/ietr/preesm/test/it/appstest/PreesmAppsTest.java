package org.ietr.preesm.test.it.appstest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.preesm.commons.exceptions.PreesmResourceException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class PreesmAppsTest {

  private final String CI_FILENAME = ".ci.yaml";

  private final String SCENARIO_KEY = "scenario";
  private final String WORKFLOW_KEY = "workflow";

  private static final String PREESM_APP_REPO = "https://github.com/preesm/preesm-apps.git";

  private static File preesmAppsFolder;

  protected static class CIConfig {
    List<Map<String, String>> config;

    public void setConfig(List<Map<String, String>> config) {
      this.config = config;
    }
  }

  @BeforeAll
  public static void setupTest() throws IOException, InvalidRemoteException, TransportException, GitAPIException {

    // Create temp folder with specific access
    if (SystemUtils.IS_OS_UNIX) {
      final FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions
          .asFileAttribute(PosixFilePermissions.fromString("rwx------"));
      preesmAppsFolder = Files.createTempDirectory("preesmAppsFolder", attr).toFile();
    } else {

      boolean fail = true;

      preesmAppsFolder = Files.createTempDirectory("preesmAppsFolder").toFile();
      fail &= preesmAppsFolder.setReadable(true, true);
      fail &= preesmAppsFolder.setWritable(true, true);
      fail &= preesmAppsFolder.setExecutable(true, true);

      Assertions.assertTrue(fail);
    }

    preesmAppsFolder.deleteOnExit();

    // pulling preesm-apps repo with submodules
    Git.cloneRepository().setURI(PREESM_APP_REPO).setDirectory(preesmAppsFolder).setCloneSubmodules(true).call();
  }

  @TestFactory
  Stream<DynamicTest> preesmAppsTestFactory() throws IOException {

    return Files.walk(Paths.get(preesmAppsFolder.toString()))
        .filter(f -> f.getFileName().toString().equals(CI_FILENAME)).flatMap(ciPathLambda -> {

          final LoaderOptions options = new LoaderOptions();
          final Yaml yaml = new Yaml(new Constructor(CIConfig.class, options));

          try {
            final InputStream inputStream = new FileInputStream(ciPathLambda.toFile());
            final CIConfig appMap = yaml.load(inputStream);

            if (appMap.config == null) {
              appMap.config = Collections.emptyList();
            }

            final String projectRoot = ciPathLambda.toString().replace(CI_FILENAME, "");
            final String projectName = getProjectName(projectRoot);

            return appMap.config.stream().filter(f -> f != null)
                .map(ciCase -> DynamicTest.dynamicTest(
                    "Running " + projectName + " with " + ciCase.get(SCENARIO_KEY) + " and " + ciCase.get(WORKFLOW_KEY),
                    () -> {
                      final String scenarioFilePathStr = "/Scenarios/" + ciCase.get(SCENARIO_KEY);
                      final String workflowFilePathStr = "/Workflows/" + ciCase.get(WORKFLOW_KEY);

                      final boolean success = WorkflowRunner.runWorkFlow(projectRoot, projectName, workflowFilePathStr,
                          scenarioFilePathStr);
                      Assertions.assertTrue(success, "Workflow [" + workflowFilePathStr + "] with scenario ["
                          + scenarioFilePathStr + "] caused failure");
                    }));

          } catch (final IOException e) {
            throw new PreesmResourceException(e);
          }
        });
  }

  private String getProjectName(String projectRoot) {

    class CustomHandler extends DefaultHandler {
      private boolean nameFlag;
      String          projectName;

      @Override
      public void startElement(String uri, String lName, String qName, Attributes attr) {
        if (qName.equals("name")) {
          nameFlag = true;
        }
      }

      @Override
      public void characters(char[] ch, int start, int length) {
        if (nameFlag) {
          nameFlag = false;
          projectName = new String(ch, start, length);
        }
      }
    }

    final SAXParserFactory factory = SAXParserFactory.newInstance();

    try {
      final SAXParser saxParser = factory.newSAXParser();
      final CustomHandler handler = new CustomHandler();

      saxParser.parse(projectRoot + ".project", handler);
      return handler.projectName;

    } catch (SAXException | ParserConfigurationException | IOException e) {
      throw new PreesmResourceException(e);
    }
  }

  // public void runScript(String command) {
  // final CommandLine oCmdLine = CommandLine.parse(command);
  // final DefaultExecutor oDefaultExecutor = new DefaultExecutor();
  // oDefaultExecutor.setExitValue(0);
  // try {
  // iExitValue = oDefaultExecutor.execute(oCmdLine);
  // } catch (final ExecuteException e) {
  // System.err.println("Execution failed.");
  // e.printStackTrace();
  // } catch (final IOException e) {
  // System.err.println("permission denied.");
  // e.printStackTrace();
  // }
  // }

}

package org.ietr.preesm.test.it.appstest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.preesm.commons.exceptions.PreesmResourceException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class PreesmAppsTest {

  private final String CI_FILENAME = ".ci.yaml";

  private final String PROJECT_KEY  = "project";
  private final String PATH_KEY     = "path";
  private final String SCENARIO_KEY = "scenario";
  private final String WORKFLOW_KEY = "workflow";

  private static File preesmAppsFolder;

  protected static class CIConfig {
    List<Map<String, String>> config;

    public void setConfig(List<Map<String, String>> config) {
      this.config = config;
    }
  }

  @BeforeAll
  public static void setupTest() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
    System.out.println("Before tests begin.");

    // Create temp folder with specific access
    if (SystemUtils.IS_OS_UNIX) {
      final FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions
          .asFileAttribute(PosixFilePermissions.fromString("rwx------"));
      preesmAppsFolder = Files.createTempDirectory("preesmAppsFolder", attr).toFile();
    } else {
      preesmAppsFolder = Files.createTempDirectory("preesmAppsFolder").toFile();
      preesmAppsFolder.setReadable(true, true);
      preesmAppsFolder.setWritable(true, true);
      preesmAppsFolder.setExecutable(true, true);
    }

    // pulling preesm-apps repo with submodules
    // Git.cloneRepository().setURI("https://github.com/preesm/preesm-apps.git").setDirectory(preesmAppsFolder)
    // .setCloneSubmodules(true).call();
    preesmAppsFolder = new File("/home/miomand/git/temp/preesm-apps");

    System.out.println("Before tests end.");
  }

  // @TestFactory // 1
  // Stream<DynamicTest> dynamicTestStream() { // 2
  // return IntStream.of(0, 3, 6, 9)
  // .mapToObj(v -> DynamicTest.dynamicTest(v + " is a multiple of 3", () -> assertEquals(0, v % 3)) // 3
  // );
  // }

  // @TestFactory
  // Stream<DynamicTest> preesmAppsTestFactory() throws IOException, CoreException {
  //
  // final Stream<Path> ciPaths = Files.walk(Paths.get(preesmAppsFolder.toString()))
  // .filter(f -> f.getFileName().toString().equals(CI_FILENAME));
  //
  // final Stream<Map<String, String>> ciCases = ciPaths.map(ciPath -> {
  // try {
  // final Yaml yaml = new Yaml();
  // final InputStream inputStream = new FileInputStream(ciPath.toFile());
  // return yaml.load(inputStream);
  // } catch (final IOException e) {
  // throw new PreesmResourceException(e);
  // }
  // });
  //
  // return ciCases.map(ciCase -> DynamicTest.dynamicTest(
  // "Running " + ciCase.get(PROJECT_KEY) + " with " + ciCase.get(SCENARIO_KEY) + " and " + ciCase.get(WORKFLOW_KEY),
  // () -> {
  // try {
  // final String projectName = ciCase.get(PROJECT_KEY);
  // final String projectRoot = preesmAppsFolder.toString() + "/" + ciCase.get(PATH_KEY) + "/";
  // final String scenarioFilePathStr = "/Scenarios/" + ciCase.get(SCENARIO_KEY);
  // final String workflowFilePathStr = "/Workflows/" + ciCase.get(WORKFLOW_KEY);
  //
  // final boolean success = WorkflowRunner.runWorkFlow(projectRoot, projectName, workflowFilePathStr,
  // scenarioFilePathStr);
  // Assertions.assertTrue(success,
  // "Workflow [" + workflowFilePathStr + "] with scenario [" + scenarioFilePathStr + "] caused failure");
  // } catch (IOException | CoreException e) {
  // throw new PreesmResourceException(e);
  // }
  // }));
  // }

  @Test
  void mainTest() throws IOException, CoreException {
    System.out.println("Main tests.");
    assertTrue(Boolean.TRUE);

    Files.walk(Paths.get(preesmAppsFolder.toString())).filter(f -> f.getFileName().toString().equals(CI_FILENAME))
        .forEach(ciPathLambda -> {
          final LoaderOptions options = new LoaderOptions();
          final Yaml yaml = new Yaml(new Constructor(CIConfig.class, options));
          // final Yaml yaml = new Yaml();
          try {

            final InputStream inputStream = new FileInputStream(ciPathLambda.toFile());
            final CIConfig appMap = yaml.load(inputStream);
            // final List<Map<String, String>> config = (List<Map<String, String>>) yaml.load(inputStream);
            // System.out.println(appMap);

            // final String projectName = appMap.get(PROJECT_KEY);
            final String projectRoot = ciPathLambda.toString().replace(CI_FILENAME, "");
            final String projectName = getProjectName(projectRoot);

            appMap.config.stream().filter(f -> !f.isEmpty()).forEach(testConfig -> {

              final String scenarioFilePathStr = "/Scenarios/" + testConfig.get(SCENARIO_KEY);
              final String workflowFilePathStr = "/Workflows/" + testConfig.get(WORKFLOW_KEY);

              try {
                final boolean success = WorkflowRunner.runWorkFlow(projectRoot, projectName, workflowFilePathStr,
                    scenarioFilePathStr);
                Assertions.assertTrue(success, "Workflow [" + workflowFilePathStr + "] with scenario ["
                    + scenarioFilePathStr + "] caused failure");

              } catch (IOException | CoreException e) {
                throw new PreesmResourceException(e);
              }
            });
          } catch (final IOException e) {
            throw new PreesmResourceException(e);
          }
        });
  }

  @AfterAll
  public static void cleanupTest() throws IOException {
    System.out.println("After tests begin.");
    // FileUtils.deleteDirectory(preesmAppsFolder);
    System.out.println("After tests end.");
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

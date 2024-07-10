/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2023) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
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
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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

  private static final String CI_FILENAME = ".ci.yaml";

  private static final String SCENARIO_KEY = "scenario";
  private static final String WORKFLOW_KEY = "workflow";

  private static final String PREESM_APP_REPO = "https://github.com/preesm/preesm-apps.git";

  private static File preesmAppsFolder;

  protected static class CIConfig {
    List<Map<String, String>> config;

    public void setConfig(List<Map<String, String>> config) {
      this.config = config;
    }
  }

  @BeforeAll
  public static void setupTest() throws IOException, GitAPIException {

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
      saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      final CustomHandler handler = new CustomHandler();

      saxParser.parse(projectRoot + ".project", handler);
      return handler.projectName;

    } catch (SAXException | ParserConfigurationException | IOException e) {
      throw new PreesmResourceException(e);
    }
  }
}

/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.preesm.algorithm.mapper.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author anmorvan
 *
 */
public class ScheduleUtils {

  private static final String read(final File fileName) throws IOException {
    String line = null;
    final StringBuilder sb = new StringBuilder();
    final FileReader fileReader = new FileReader(fileName);
    try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line + "\n");
      }
    }
    return sb.toString();
  }

  /**
   *
   */
  public static final Schedule parseJsonFile(final File inputJsonFile) throws IOException {
    final String fileContent = ScheduleUtils.read(inputJsonFile);
    return ScheduleUtils.parseJsonString(fileContent);
  }

  /**
   *
   */
  public static final Schedule parseJsonString(final String inputJsonString) throws IOException {
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(inputJsonString, Schedule.class);
  }

  /**
   *
   */
  public static final String unparseSchedule(final Schedule schedule) throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schedule);
  }
}

package org.ietr.preesm.mapper.schedule;

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

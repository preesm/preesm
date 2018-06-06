package org.ietr.preesm.mapper.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

class ScheduleEntry {
  @JsonProperty(required = true)
  String actorName;

  @JsonProperty(required = true)
  int singleRateInstanceNumber;

  @JsonProperty(required = true)
  int start;

  @JsonProperty(required = false)
  int startN;

  @JsonProperty(required = true)
  int end;

  @JsonProperty(required = false)
  int endN;

  @JsonProperty(required = false)
  int core;

  @JsonProperty(required = true)
  String processingElementName;

  @JsonProperty(required = false)
  int graphIteration;

}

class BufferEntity {
  @JsonProperty(required = true)
  String producer;
  @JsonProperty(required = true)
  String consumer;

  @JsonProperty(required = true)
  double initValue;
  @JsonProperty(required = true)
  double initValueN;
}

class ActorEntity {
  @JsonProperty(required = true)
  String actor;
  @JsonProperty(required = true)
  double delay;
  @JsonProperty(required = true)
  int    repeat;
}

public class Schedule {

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

  public static final Schedule parseJsonFile(final File inputJsonFile) throws IOException {
    final String fileContent = Schedule.read(inputJsonFile);
    return Schedule.parseJsonString(fileContent);
  }

  public static final Schedule parseJsonString(final String inputJsonString) throws IOException {
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(inputJsonString, Schedule.class);
  }

  public static final String unparseSchedule(final Schedule schedule) throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schedule);
  }

  @JsonProperty(required = false)
  List<BufferEntity> bufferEntities;

  @JsonProperty(required = false)
  List<ActorEntity> actorEntities;

  @JsonProperty(required = false)
  List<ScheduleEntry> preScheduleEntries;

  @JsonProperty(required = true)
  List<ScheduleEntry> scheduleEntries;

  @JsonProperty(required = false)
  int    period;
  @JsonProperty(required = false)
  int    intervals;
  @JsonProperty(required = false)
  int    step;
  @JsonProperty(required = false)
  double delay;
}

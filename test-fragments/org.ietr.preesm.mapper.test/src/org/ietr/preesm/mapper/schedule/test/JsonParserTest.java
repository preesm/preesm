package org.ietr.preesm.mapper.schedule.test;

import java.io.File;
import java.io.IOException;
import org.ietr.preesm.mapper.schedule.Schedule;
import org.junit.Assert;
import org.junit.Test;

public class JsonParserTest {

  @Test
  public void testParser() throws IOException {
    File file = new File("resources/X4_356_SCHED-1.json");
    Schedule parsedSchedule = Schedule.parseJsonFile(file);
    Assert.assertNotNull(parsedSchedule);
  }
}

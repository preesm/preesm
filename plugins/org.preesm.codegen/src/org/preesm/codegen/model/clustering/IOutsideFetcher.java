package org.preesm.codegen.xtend.task;

import java.util.Map;
import org.preesm.codegen.model.Buffer;
import org.preesm.model.pisdf.DataPort;

/**
 * @author dgageot
 *
 */
public interface IOutsideFetcher {

  public Buffer getOuterClusterBuffer(DataPort graphPort, Map<String, Object> input);

}

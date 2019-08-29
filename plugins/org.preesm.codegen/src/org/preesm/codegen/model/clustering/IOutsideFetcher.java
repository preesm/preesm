package org.preesm.codegen.model.clustering;

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

package org.ietr.preesm.experiment.model.pimm.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IPath;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 *
 * @author anmorvan
 *
 */
public class CHeaderUsedLocator {

  private CHeaderUsedLocator() {
    // forbid instantiation
  }

  /**
   */
  public static final List<IPath> findAllCHeadersUsed(final PiGraph graph) {
    final List<IPath> result = new ArrayList<>();
    graph.eAllContents().forEachRemaining(element -> {
      if (element instanceof CHeaderRefinement) {
        final IPath filePath = ((CHeaderRefinement) element).getFilePath();
        if (!(result.contains(filePath))) {
          result.add(filePath);
        }
      }
    });
    return result;
  }

  public static final List<String> findAllCHeaderFileNamesUsed(final PiGraph graph) {
    return findAllCHeadersUsed(graph).stream().map(IPath::toFile).map(File::getName).collect(Collectors.toList());
  }
}

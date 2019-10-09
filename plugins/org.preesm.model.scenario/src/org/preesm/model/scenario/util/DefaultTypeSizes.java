package org.preesm.model.scenario.util;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.files.URLHelper;
import org.preesm.model.scenario.ScenarioConstants;

/**
 *
 *
 * @author anmorvan
 *
 */
public final class DefaultTypeSizes {

  private static final DefaultTypeSizes instance = new DefaultTypeSizes();

  private static final String CSV_FILE_UBUNTU_18_04_X64_GCC_7_4_0 = "default_type_sizes_ubuntu_18.04_x64_gcc_7.4.0.csv";

  public static final DefaultTypeSizes getInstance() {
    return DefaultTypeSizes.instance;
  }

  private final Map<String, Long> defaultTypeSizesMap;

  private DefaultTypeSizes() {
    this.defaultTypeSizesMap = DefaultTypeSizes.initDefaultTypeSizes();
  }

  private static final Map<String, Long> initDefaultTypeSizes() {
    final URL csvFileURL = PreesmResourcesHelper.getInstance()
        .resolve(DefaultTypeSizes.CSV_FILE_UBUNTU_18_04_X64_GCC_7_4_0, DefaultTypeSizes.class);

    if (csvFileURL == null) {
      throw new PreesmRuntimeException(
          "Could not locate CSV file storing default data type sizes (should be under 'resources/"
              + DefaultTypeSizes.CSV_FILE_UBUNTU_18_04_X64_GCC_7_4_0 + "' of plugin 'org.preesm.model.scenario'");
    }

    try {
      final String read = URLHelper.read(csvFileURL);
      final Map<String, Long> res = new LinkedHashMap<>();
      Arrays.stream(read.split("\n")).forEach(typeDef -> {
        final String[] split = typeDef.split(":");
        res.put(split[0], Long.parseLong(split[1]));
      });
      return Collections.unmodifiableMap(res);
    } catch (final IOException e) {
      throw new PreesmRuntimeException("Could not read CSV file storing default data type sizes.", e);
    }
  }

  /**
   */
  public final long getDefaultTypeSize(final String typeName) {
    if (this.defaultTypeSizesMap.containsKey(typeName)) {
      return this.defaultTypeSizesMap.get(typeName);
    } else {
      return (long) ScenarioConstants.DEFAULT_DATA_TYPE_SIZE.getValue();
    }
  }

}

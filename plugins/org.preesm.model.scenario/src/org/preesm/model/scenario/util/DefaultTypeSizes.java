/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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

  public static final long UNKNOWN_TYPE = -1;

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
   * Returns the type size in bits of the provided typename if it is known, or the default
   * {@link ScenarioConstants.DEFAULT_DATA_TYPE_SIZE} otherwise.
   *
   * @param typeName
   *          To be checked.
   * @return The size of the type name if known, default {@link ScenarioConstants.DEFAULT_DATA_TYPE_SIZE} otherwise.
   */
  public final long getTypeSizeOrDefault(final String typeName) {

    final long typeSize = getTypeSize(typeName);

    if (typeSize != UNKNOWN_TYPE) {
      return typeSize;
    }
    return ScenarioConstants.DEFAULT_DATA_TYPE_SIZE_VALUE;

  }

  /**
   * Returns the type size in bits of the provided typename if it is known, -1 otherwise.
   *
   * @param typeName
   *          To be checked.
   * @return The size of the type name if known, -1 otherwise.
   */
  public final long getTypeSize(final String typeName) {
    if (this.defaultTypeSizesMap.containsKey(typeName)) {
      return this.defaultTypeSizesMap.get(typeName);
    }
    if (isSpecialType(typeName)) {
      return getSpecialTypeTokenSize(typeName);
    }
    return UNKNOWN_TYPE;
  }

  public boolean isSpecialType(String typeName) {
    return VitisTypeSize.isVitisType(typeName);
  }

  public long getSpecialTypeTokenSize(String typeName) {
    if (VitisTypeSize.isVitisType(typeName)) {
      return VitisTypeSize.getVitisTokenSize(typeName);
    }
    throw new PreesmRuntimeException("Cannot find the size of '" + typeName + "'.");
  }
}

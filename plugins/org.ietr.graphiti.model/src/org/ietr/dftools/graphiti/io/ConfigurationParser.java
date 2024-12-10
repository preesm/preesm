/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
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
package org.ietr.dftools.graphiti.io;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Color;
import org.ietr.dftools.graphiti.GraphitiException;
import org.ietr.dftools.graphiti.model.Configuration;
import org.ietr.dftools.graphiti.model.FileFormat;
import org.ietr.dftools.graphiti.model.IRefinementPolicy;
import org.ietr.dftools.graphiti.model.IValidator;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Parameter;
import org.ietr.dftools.graphiti.model.ParameterPosition;
import org.ietr.dftools.graphiti.model.Transformation;

/**
 * This class parses all configuration files located in the configuration folder (defined in the plug-in preferences).
 *
 * @author Matthieu Wipliez
 *
 */
public class ConfigurationParser {

  /** The configurations. */
  private final Map<String, Configuration> configurations;

  /**
   * Creates a new configuration parser that parses all configuration files located in the configuration folder (defined
   * in the plug-in preferences).
   *
   * @throws CoreException
   *           the core exception
   * @see FileLocator
   */
  public ConfigurationParser() throws CoreException {
    this.configurations = new LinkedHashMap<>();

    final IExtensionRegistry registry = Platform.getExtensionRegistry();
    final IConfigurationElement[] elements = registry.getConfigurationElementsFor("graphiti.definition");
    for (final IConfigurationElement element : elements) {
      final Configuration configuration = parseConfiguration(element);
      this.configurations.put(configuration.getName(), configuration);
    }
  }

  /**
   * Returns a reference to the list of configurations parsed.
   *
   * @return A reference to the list of configurations parsed.
   */
  public Map<String, Configuration> getConfigurations() {
    return this.configurations;
  }

  /**
   * Parses a configuration definition element.
   *
   * @param element
   *          A configuration definition element.
   * @return the configuration
   * @throws CoreException
   *           If anything wrong occurs.
   */
  private Configuration parseConfiguration(final IConfigurationElement element) throws CoreException {
    final String name = element.getAttribute("name");

    final String extension = element.getAttribute("extension");
    final String type = element.getAttribute("type");
    final FileFormat format = new FileFormat(extension, type);

    IConfigurationElement[] children = element.getChildren("import");
    children = children[0].getChildren();
    parseTransformations(format.getImportTransformations(), children);

    children = element.getChildren("export");
    if (children.length > 0) {
      children = children[0].getChildren();
      parseTransformations(format.getExportTransformations(), children);
    }

    final Map<String, ObjectType> graphTypes = parseTypes(element.getChildren("graphType"));
    final Map<String, ObjectType> vertexTypes = parseTypes(element.getChildren("vertexType"));
    final Map<String, ObjectType> edgeTypes = parseTypes(element.getChildren("edgeType"));

    final IValidator validator = (IValidator) element.createExecutableExtension("validator");

    final String refinement = element.getAttribute("refinement");
    IRefinementPolicy refinementPolicy = null;
    if (refinement != null) {
      refinementPolicy = (IRefinementPolicy) element.createExecutableExtension("refinement");
    }

    final IContributor contributor = element.getContributor();
    return new Configuration(name, contributor.getName(), format, graphTypes, vertexTypes, edgeTypes, validator,
        refinementPolicy);
  }

  /**
   * Parses a parameter default value.
   *
   * @param parameterType
   *          The class of the parameter.
   * @param element
   *          the element
   * @return An object, either a {@link List}, a {@link Map}, an {@link Integer}, a {@link Float}, a {@link Boolean}, or
   *         a {@link String}.
   */
  private Object parseParameter(final Class<?> parameterType, final IConfigurationElement element) {
    Object res = null;
    if (parameterType == List.class) {
      res = new ArrayList<>();
    } else if (parameterType == Map.class) {
      res = new TreeMap<>();
    } else {
      final String value = element.getAttribute("default");
      try {
        if (parameterType == Integer.class) {
          res = Integer.valueOf(value);
        } else if (parameterType == Float.class) {
          res = Float.valueOf(value);
        } else if (parameterType == Boolean.class) {
          res = Boolean.valueOf(value);
        } else if (parameterType == String.class) {
          res = value;
        } else {
          res = value;
        }
      } catch (final NumberFormatException e) {
        res = null;
      }
    }
    return res;
  }

  /**
   * Parses the parameters for the given type.
   *
   * @param type
   *          The type whose parameters are being specified.
   * @param element
   *          the element
   */
  private void parseParameters(final ObjectType type, final IConfigurationElement element) {
    final String parameterName = element.getAttribute("name");
    final String typeName = element.getAttribute("type");
    Class<?> clz = String.class;
    try {
      clz = Class.forName(typeName);
    } catch (final ClassNotFoundException e) {
      throw new GraphitiException("Could not parse parameters: unknown type '" + typeName + "'.", e);
    }

    // creates the parameter
    final ParameterPosition position = null;
    final Object value = parseParameter(clz, element);
    final Parameter parameter = new Parameter(parameterName, value, position, clz);

    // adds the parameter to the type
    type.addParameter(parameter);
  }

  /**
   * Parses the file format imports.
   *
   * @param transformations
   *          the transformations
   * @param children
   *          the children
   * @throws CoreException
   *           the core exception
   */
  private void parseTransformations(final List<Transformation> transformations, final IConfigurationElement[] children)
      throws CoreException {
    for (final IConfigurationElement element : children) {
      final String name = element.getAttribute("name");
      final String type = element.getName();
      if (type.equals("transformation")) {
        final ITransformation instance = (ITransformation) element.createExecutableExtension("class");
        transformations.add(new Transformation(instance));
      } else if (type.equals("xslt")) {
        transformations.add(new Transformation(name));
      } else {
        throw new IllegalArgumentException("Unknown type: " + type);
      }
    }
  }

  /**
   * Parses a type.
   *
   * @param type
   *          the type
   * @param children
   *          the children
   */
  private void parseType(final ObjectType type, final IConfigurationElement[] children) {
    for (final IConfigurationElement element : children) {
      if (element.getName().equals(ObjectType.ATTRIBUTE_COLOR)) {
        final int red = Integer.parseInt(element.getAttribute("red"));
        final int green = Integer.parseInt(element.getAttribute("green"));
        final int blue = Integer.parseInt(element.getAttribute("blue"));
        final Color color = new Color(null, red, green, blue);
        type.addAttribute(ObjectType.ATTRIBUTE_COLOR, color);
      } else if (element.getName().equals(ObjectType.ATTRIBUTE_DIRECTED)) {
        final boolean directed = Boolean.parseBoolean(element.getAttribute("directed"));
        type.addAttribute(ObjectType.ATTRIBUTE_DIRECTED, directed);
      } else if (element.getName().equals("size")) {
        final int width = Integer.parseInt(element.getAttribute("width"));
        type.addAttribute(ObjectType.ATTRIBUTE_WIDTH, width);
        final int height = Integer.parseInt(element.getAttribute("height"));
        type.addAttribute(ObjectType.ATTRIBUTE_HEIGHT, height);
      } else if (element.getName().equals(ObjectType.ATTRIBUTE_SHAPE)) {
        final String shape = element.getAttribute("type");
        type.addAttribute(ObjectType.ATTRIBUTE_SHAPE, shape);
      } else if (element.getName().equals("parameter")) {
        parseParameters(type, element);
      }
    }
  }

  /**
   * Parses the object types.
   *
   * @param children
   *          configuration elements
   * @return a map from strings to object types
   */
  private Map<String, ObjectType> parseTypes(final IConfigurationElement[] children) {
    final Map<String, ObjectType> types = new TreeMap<>();

    for (final IConfigurationElement element : children) {
      final String typeName = element.getAttribute("name");
      final ObjectType type = new ObjectType(typeName);

      final String isDirected = element.getAttribute("directed");
      if (isDirected != null) {
        final boolean value = Boolean.parseBoolean(isDirected);
        type.addAttribute(ObjectType.ATTRIBUTE_DIRECTED, value);
      }
      types.put(typeName, type);
      parseType(type, element.getChildren());
    }

    return types;
  }

}

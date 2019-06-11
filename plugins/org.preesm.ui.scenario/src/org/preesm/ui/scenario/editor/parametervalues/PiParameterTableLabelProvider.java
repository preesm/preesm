/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2014)
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
package org.preesm.ui.scenario.editor.parametervalues;

import java.net.URL;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Parameter;
import org.preesm.ui.PreesmUIPlugin;

/**
 * The label provider displays informations to fill the multi-column tree for parameter edition.
 *
 * @author jheulot
 */
public class PiParameterTableLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

  /** The table. */
  private final Table table;

  /** The image ok. */
  private final Image imageOk;

  /** The image error. */
  private final Image imageError;

  /**
   * Instantiates a new pi parameter table label provider.
   *
   * @param _table
   *          the table
   */
  PiParameterTableLabelProvider(final Table _table) {
    super();
    this.table = _table;

    final URL errorIconURL = PreesmResourcesHelper.getInstance().resolve("icons/error.png", PreesmUIPlugin.class);
    ImageDescriptor imageDcr = ImageDescriptor.createFromURL(errorIconURL);
    this.imageError = imageDcr.createImage();

    final URL okIconURL = PreesmResourcesHelper.getInstance().resolve("icons/ok.png", PreesmUIPlugin.class);
    imageDcr = ImageDescriptor.createFromURL(okIconURL);
    this.imageOk = imageDcr.createImage();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
   */
  @Override
  public String getText(final Object element) {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
   */
  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    @SuppressWarnings("unchecked")
    final Entry<Parameter, String> paramValue = ((Entry<Parameter, String>) element);
    if (columnIndex == 5) { // Expression Value Column
      final String value = paramValue.getValue();
      if (value != null && !value.isEmpty()) {
        if (paramValue.getKey().isConfigurable()) {
          return this.imageOk;
        } else {
          final Expression tmp = paramValue.getKey().getExpression();
          try {
            paramValue.getKey().setExpression(value);
            paramValue.getKey().getExpression().evaluate();
            return this.imageOk;
          } catch (final Exception e) {
            return this.imageError;
          } finally {
            paramValue.getKey().setExpression(tmp);
          }
        }
      } else {
        return this.imageError;
      }
    }
    return null;
  }

  private static final String[] COLUMN_NAMES = { "Parameters", "Type", "Input Parameters", "Graph Expression",
      "Override Expression", "Value" };

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    @SuppressWarnings("unchecked")
    final Entry<Parameter, String> paramValue = ((Entry<Parameter, String>) element);
    switch (columnIndex) {
      case 0: // Paremeter path
        return paramValue.getKey().getVertexPath();
      case 1: // Type Column
        return paramValue.getKey().isLocallyStatic() ? "STATIC" : "DYNAMIC";
      case 2: // Input Parameter Column
        return paramValue.getKey().getInputParameters().stream().map(Parameter::getName).collect(Collectors.toList())
            .toString();
      case 3: // Graph Expression Column
        return paramValue.getKey().getExpression().getExpressionAsString();
      case 4: // Override Expression Column
        if (paramValue.getKey().isLocallyStatic()) {
          return paramValue.getValue();
        } else {
          return " - ";
        }
      case 5: // Expression Value Column
        if (paramValue.getKey().isLocallyStatic()) {

          final Expression tmp = paramValue.getKey().getExpression();
          try {
            paramValue.getKey().setExpression(paramValue.getValue());
            return Long.toString(paramValue.getKey().getExpression().evaluate());
          } catch (final Exception e) {
            return " - ";
          } finally {
            paramValue.getKey().setExpression(tmp);
          }

        } else {
          return paramValue.getValue();
        }
      default:
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
   */
  @Override
  public Color getForeground(final Object element, final int columnIndex) {
    return this.table.getForeground();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
   */
  @Override
  public Color getBackground(final Object element, final int columnIndex) {
    @SuppressWarnings("unchecked")
    final Entry<Parameter, String> paramValue = ((Entry<Parameter, String>) element);
    switch (columnIndex) {
      case 0: // Actors Column
      case 1: // Path Column
      case 2: // Type Column
        return this.table.getBackground();
      case 3: // Expression Column
        if (paramValue.getValue() != null && !paramValue.getValue().isEmpty()) {
          return this.table.getBackground();
        } else {
          return new Color(this.table.getDisplay(), 200, 200, 200);
        }
      case 4:
      case 5: // Value Column
        if (paramValue.getKey().isLocallyStatic()) {
          return this.table.getBackground();
        } else {
          return new Color(this.table.getDisplay(), 200, 200, 200);
        }
      default:
    }
    return this.table.getBackground();
  }

}

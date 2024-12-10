/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2010)
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
package org.ietr.dftools.graphiti.ui.figure;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.geometry.Point;
import org.ietr.dftools.graphiti.model.ParameterPosition;

/**
 * This class provides a connection locator.
 *
 * @author Jonathan Piat
 *
 */
public class PropertyLocator extends ConnectionLocator {

  /** The pos. */
  private final ParameterPosition pos;

  /** The positions. */
  private final Map<Connection, List<PropertyLocator>> positions = new LinkedHashMap<>();

  /**
   * Instantiates a new property locator.
   *
   * @param c
   *          the c
   * @param p
   *          the p
   */
  public PropertyLocator(final Connection c, final ParameterPosition p) {
    super(c);
    if (this.positions.get(c) == null) {
      final List<PropertyLocator> list = new ArrayList<>();
      this.positions.put(c, list);
    }
    this.pos = p;
    this.positions.get(c).add(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.draw2d.ConnectionLocator#getReferencePoint()
   */
  @Override
  protected Point getReferencePoint() {
    final Connection conn = getConnection();
    final List<PropertyLocator> listOfProperties = this.positions.get(conn);
    final int maxIndex = listOfProperties.indexOf(this);
    int dec = 5;
    for (int i = 0; i < maxIndex; i++) {
      if (listOfProperties.get(i).pos == this.pos) {
        dec += 10;
      }
    }

    int xdirec = 0;
    int ydirec = 0;
    final Point p = Point.SINGLETON;
    final Point f = conn.getPoints().getFirstPoint();
    final Point l = conn.getPoints().getLastPoint();

    if (l.x > f.x) {
      xdirec = 1;
    } else {
      xdirec = -1;
    }

    if (l.y > f.y) {
      ydirec = 1;
    } else {
      ydirec = -1;
    }
    if (this.pos.equals(ParameterPosition.WEST) || this.pos.equals(ParameterPosition.NORTHWEST)
        || this.pos.equals(ParameterPosition.SOUTHWEST)) {
      final Point refP = conn.getPoints().getFirstPoint().getCopy();
      conn.getParent().translateToAbsolute(refP);
      p.setLocation(refP.x + (dec * xdirec), refP.y + (dec * ydirec));
    } else if (this.pos.equals(ParameterPosition.EAST) || this.pos.equals(ParameterPosition.NORTHEAST)
        || this.pos.equals(ParameterPosition.SOUTHEAST)) {
      final Point refP = conn.getPoints().getLastPoint().getCopy();
      conn.getParent().translateToAbsolute(refP);
      p.setLocation(refP.x - (dec * xdirec), refP.y - (dec * ydirec));
    } else {
      final Point refP = conn.getPoints().getMidpoint().getCopy();
      conn.getParent().translateToAbsolute(refP);
      p.setLocation(refP.x - (dec * xdirec), refP.y - (dec * ydirec));
    }

    return p;
  }

}

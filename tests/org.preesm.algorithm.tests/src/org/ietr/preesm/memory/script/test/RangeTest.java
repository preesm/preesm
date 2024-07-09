/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2023) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2023)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2022)
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
package org.ietr.preesm.memory.script.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.preesm.algorithm.memory.script.Range;

/**
 *
 * @author anmorvan
 *
 */
public class RangeTest {

  Range range0 = new Range(0, 10);
  Range range1 = new Range(5, 15);
  Range range2 = new Range(3, 7);
  Range range3 = new Range(15, 20);

  @Test
  public void testDifference() {
    List<Range> ranges = new ArrayList<>();
    Range.union(ranges, range0);
    Range.union(ranges, new Range(12, 17));

    List<Range> ranges1 = new ArrayList<>();
    ranges1.add(new Range(2, 3));
    ranges1.add(new Range(5, 14));

    List<Range> rangesInput = new ArrayList<>();

    // Check the result of range0 - range1
    rangesInput.add(new Range(0, 5));
    List<Range> difference = range0.difference(range1);
    assertEquals(rangesInput, difference);
    rangesInput.clear();

    // Check the result of range0 - range3
    rangesInput.add(new Range(0, 10));
    difference = range0.difference(range3);
    assertEquals(rangesInput, difference);
    rangesInput.clear();

    // Check the result of range0 - range2
    rangesInput.add(new Range(0, 3));
    rangesInput.add(new Range(7, 10));
    difference = range0.difference(range2);
    assertEquals(rangesInput, difference);
    rangesInput.clear();

    // Check the result of ranges - range1
    rangesInput.add(new Range(0, 5));
    rangesInput.add(new Range(15, 17));
    difference = Range.difference(ranges, range1);
    assertEquals(rangesInput, difference);
    rangesInput.clear();

    // Check the result of ranges - ranges1
    rangesInput.add(new Range(0, 2));
    rangesInput.add(new Range(3, 5));
    rangesInput.add(new Range(14, 17));
    difference = Range.difference(ranges, ranges1);
    assertEquals(rangesInput, difference);
    rangesInput.clear();
  }
}

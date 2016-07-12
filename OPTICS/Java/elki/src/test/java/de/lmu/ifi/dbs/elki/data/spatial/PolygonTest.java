package de.lmu.ifi.dbs.elki.data.spatial;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2015
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Test the polygon class.
 * 
 * @author Erich Schubert
 * @since 0.4.0
 */
public class PolygonTest {
  @Test
  public void testPolygonContainment() {
    final Polygon p1, p2, p3;
    {
      List<double[]> v1 = new ArrayList<>();
      v1.add(new double[] { 0, 0 });
      v1.add(new double[] { .9, 0 });
      v1.add(new double[] { 0, .9 });
      p1 = new Polygon(v1);
    }
    {
      List<double[]> v2 = new ArrayList<>();
      v2.add(new double[] { 1, 1 });
      v2.add(new double[] { 1, .1 });
      v2.add(new double[] { .1, 1 });
      p2 = new Polygon(v2);
    }
    {
      List<double[]> v3 = new ArrayList<>();
      v3.add(new double[] { .1, .1 });
      v3.add(new double[] { .1, .9 });
      v3.add(new double[] { .9, .9 });
      v3.add(new double[] { .9, .1 });
      p3 = new Polygon(v3);
    }
    double[] pou = new double[] { -1, -1 };
    double[] p22 = new double[] { .2, .2 };
    assertFalse("P2 not in p1", p1.containsPoint2D(pou));
    assertFalse("P2 not in p2", p2.containsPoint2D(pou));
    assertFalse("P2 not in p3", p3.containsPoint2D(pou));
    assertTrue("P2 not in p1", p1.containsPoint2D(p22));
    assertFalse("P2 in p2", p2.containsPoint2D(p22));
    assertTrue("P2 not in p3", p3.containsPoint2D(p22));
    assertFalse("Polygons p1 and p2 must not intersect.", p1.intersects2DIncomplete(p2));
    assertTrue("Polygons p1 and p3 must intersect.", p1.intersects2DIncomplete(p3));
    assertTrue("Polygons p2 and p3 must intersect.", p2.intersects2DIncomplete(p3));
  }
}

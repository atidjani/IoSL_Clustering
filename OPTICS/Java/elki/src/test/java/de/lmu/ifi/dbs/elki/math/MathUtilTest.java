package de.lmu.ifi.dbs.elki.math;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

/**
 * Unit test for some basic math functions.
 *
 * @author Erich Schubert
 * @since 0.4.1
 */
public class MathUtilTest {
  @Test
  public void testBitMath() {
    assertEquals("Bit math issues", 1024, MathUtil.nextPow2Int(912));
    assertEquals("Bit math issues", 8, MathUtil.nextPow2Int(5));
    assertEquals("Bit math issues", 4, MathUtil.nextPow2Int(4));
    assertEquals("Bit math issues", 4, MathUtil.nextPow2Int(3));
    assertEquals("Bit math issues", 2, MathUtil.nextPow2Int(2));
    assertEquals("Bit math issues", 1, MathUtil.nextPow2Int(1));
    assertEquals("Bit math issues", 0, MathUtil.nextPow2Int(0));
    assertEquals("Bit math issues", 1024L, MathUtil.nextPow2Long(912L));
    assertEquals("Bit math issues", 0, MathUtil.nextPow2Int(-1));
    assertEquals("Bit math issues", 0, MathUtil.nextPow2Int(-2));
    assertEquals("Bit math issues", 0, MathUtil.nextPow2Int(-99));
    assertEquals("Bit math issues", 15, MathUtil.nextAllOnesInt(8));
    assertEquals("Bit math issues", 7, MathUtil.nextAllOnesInt(4));
    assertEquals("Bit math issues", 3, MathUtil.nextAllOnesInt(3));
    assertEquals("Bit math issues", 3, MathUtil.nextAllOnesInt(2));
    assertEquals("Bit math issues", 1, MathUtil.nextAllOnesInt(1));
    assertEquals("Bit math issues", 0, MathUtil.nextAllOnesInt(0));
    assertEquals("Bit math issues", -1, MathUtil.nextAllOnesInt(-1));
    assertEquals("Bit math issues", 0, 0 >>> 1);
  }

  @Test
  public void testFloatToDouble() {
    Random r = new Random(1l);
    for(int i = 0; i < 10000; i++) {
      final double dbl = Double.longBitsToDouble(r.nextLong());
      final float flt = (float) dbl;
      final double uppd = MathUtil.floatToDoubleUpper(flt);
      final float uppf = (float) uppd;
      final double lowd = MathUtil.floatToDoubleLower(flt);
      final float lowf = (float) lowd;
      assertTrue("Expected value to become larger, but " + uppd + " < " + dbl, uppd >= dbl || Double.isNaN(dbl));
      assertTrue("Expected value to round to the same float.", flt == uppf || Double.isNaN(flt));
      assertTrue("Expected value to become smaller, but " + lowd + " > " + dbl, lowd <= dbl || Double.isNaN(dbl));
      assertTrue("Expected value to round to the same float.", flt == lowf || Double.isNaN(flt));
    }
  }

  @Test
  public void testPowi() {
    assertEquals("Power incorrect", 0.01, MathUtil.powi(0.1, 2), 1e-13);
    assertEquals("Power incorrect", 0.001, MathUtil.powi(0.1, 3), 1e-13);
    assertEquals("Power incorrect", 0.0001, MathUtil.powi(0.1, 4), 1e-13);
    assertEquals("Power incorrect", 0.00001, MathUtil.powi(0.1, 5), 1e-13);
  }
}

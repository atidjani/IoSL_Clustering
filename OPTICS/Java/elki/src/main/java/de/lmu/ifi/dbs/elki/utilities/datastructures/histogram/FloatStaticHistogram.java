package de.lmu.ifi.dbs.elki.utilities.datastructures.histogram;

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

import java.util.Arrays;

/**
 * Histogram class storing float values.
 * 
 * The histogram will start with "bin" bins, but it can grow dynamically to the
 * left and right.
 * 
 * @author Erich Schubert
 * @since 0.5.5
 */
public class FloatStaticHistogram extends AbstractStaticHistogram implements FloatHistogram {
  /**
   * Constructor.
   * 
   * @param bins Number of bins
   * @param min Cover minimum
   * @param max Cover maximum
   */
  public FloatStaticHistogram(int bins, double min, double max) {
    super(bins, min, max);
    if (bins >= 0) {
      data = new float[bins];
    } else {
      data = null;
    }
  }

  /**
   * Data store
   */
  float[] data;

  /**
   * Increment the value of a bin.
   * 
   * @param coord Coordinate
   * @param val Value
   */
  @Override
  public void increment(double coord, float val) {
    int bin = getBinNr(coord);
    if (bin < 0) {
      if (size - bin > data.length) {
        // Reallocate. TODO: use an arraylist-like grow strategy!
        float[] tmpdata = new float[growSize(data.length, size - bin)];
        System.arraycopy(data, 0, tmpdata, -bin, size);
        data = tmpdata;
      } else {
        // Shift in place and clear head
        System.arraycopy(data, 0, data, -bin, size);
        Arrays.fill(data, 0, -bin, (float) 0);
      }
      data[0] = val;
      // Note that bin is negative, -bin is the shift offset!
      assert (data.length >= size - bin);
      offset -= bin;
      size -= bin;
      // TODO: modCounter++; and have iterators fast-fail
    } else if (bin >= data.length) {
      float[] tmpdata = new float[growSize(data.length, bin + 1)];
      System.arraycopy(data, 0, tmpdata, 0, size);
      tmpdata[bin] = val;
      data = tmpdata;
      size = bin + 1;
      // TODO: modCounter++; and have iterators fast-fail
      // Unset max value when resizing
      max = Double.MAX_VALUE;
    } else {
      if (bin >= size) {
        // TODO: reset bins to 0 first?
        size = bin + 1;
      }
      data[bin] += val;
    }
  }

  /**
   * Get the value at a particular position.
   * 
   * @param coord Coordinate
   * @return Value
   */
  public float get(double coord) {
    int bin = getBinNr(coord);
    if (bin < 0 || bin >= size) {
      return 0;
    }
    return data[bin];
  }

  @Override
  public Iter iter() {
    return new Iter();
  }

  /**
   * Iterator class.
   * 
   * @author Erich Schubert
   */
  public class Iter extends AbstractStaticHistogram.Iter implements FloatHistogram.Iter {
    @Override
    public float getValue() {
      return data[bin];
    }
  }
}

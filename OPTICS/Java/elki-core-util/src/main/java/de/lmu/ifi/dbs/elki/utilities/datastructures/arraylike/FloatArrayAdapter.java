package de.lmu.ifi.dbs.elki.utilities.datastructures.arraylike;

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

/**
 * Use a double array as, well, double array in the ArrayAdapter API.
 * 
 * @author Erich Schubert
 * @since 0.5.0
 * 
 * @apiviz.exclude
 */
class FloatArrayAdapter implements NumberArrayAdapter<Float, float[]> {
  /**
   * Constructor.
   * 
   * Use the static instance from {@link ArrayLikeUtil}!
   */
  protected FloatArrayAdapter() {
    super();
  }

  @Override
  public int size(float[] array) {
    return array.length;
  }

  @Override
  @Deprecated
  public Float get(float[] array, int off) throws IndexOutOfBoundsException {
    return Float.valueOf(array[off]);
  }

  @Override
  public double getDouble(float[] array, int off) throws IndexOutOfBoundsException {
    return (double) array[off];
  }

  @Override
  public float getFloat(float[] array, int off) throws IndexOutOfBoundsException {
    return array[off];
  }

  @Override
  public int getInteger(float[] array, int off) throws IndexOutOfBoundsException {
    return (int) array[off];
  }

  @Override
  public short getShort(float[] array, int off) throws IndexOutOfBoundsException {
    return (short) array[off];
  }

  @Override
  public long getLong(float[] array, int off) throws IndexOutOfBoundsException {
    return (long) array[off];
  }

  @Override
  public byte getByte(float[] array, int off) throws IndexOutOfBoundsException {
    return (byte) array[off];
  }
}
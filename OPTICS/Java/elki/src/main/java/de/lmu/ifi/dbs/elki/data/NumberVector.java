package de.lmu.ifi.dbs.elki.data;

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

import de.lmu.ifi.dbs.elki.data.spatial.SpatialComparable;
import de.lmu.ifi.dbs.elki.utilities.datastructures.arraylike.NumberArrayAdapter;

/**
 * Interface NumberVector defines the methods that should be implemented by any
 * Object that is element of a real vector space of type N.
 * 
 * @author Arthur Zimek
 * @since 0.2
 * 
 * @apiviz.landmark
 * @apiviz.exclude de.lmu.ifi.dbs.elki.(datasource.filter|data.projection).*
 */
public interface NumberVector extends FeatureVector<Number>, SpatialComparable {
  @Deprecated
  @Override
  Number getValue(int dimension);
  
  /**
   * Returns the value in the specified dimension as double.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).doubleValue()}, but usually this is much more
   * efficient due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 0 &le; dimension &lt;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  double doubleValue(int dimension);

  /**
   * Returns the value in the specified dimension as float.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).floatValue()}, but usually this is much more efficient
   * due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 0 &le; dimension &lt;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  float floatValue(int dimension);

  /**
   * Returns the value in the specified dimension as int.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).intValue()}, but usually this is much more efficient
   * due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 0 &le; dimension &lt;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  int intValue(int dimension);

  /**
   * Returns the value in the specified dimension as long.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).longValue()}, but usually this is much more efficient
   * due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 0 &le; dimension &lt;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  long longValue(int dimension);

  /**
   * Returns the value in the specified dimension as short.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).shortValue()}, but usually this is much more efficient
   * due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 0 &le; dimension &lt;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  short shortValue(int dimension);

  /**
   * Returns the value in the specified dimension as byte.
   * 
   * Note: this might seem redundant with respect to
   * {@code getValue(dim).byteValue()}, but usually this is much more efficient
   * due to boxing/unboxing cost.
   * 
   * @param dimension the desired dimension, where 0 &le; dimension &lt;
   *        <code>this.getDimensionality()</code>
   * @return the value in the specified dimension
   */
  byte byteValue(int dimension);

  /**
   * Returns a double array <i>copy</i> of this vector.
   * 
   * @return Copy as {@code double[]}
   */
  double[] toArray();

  /**
   * Factory API for this feature vector.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.has NumberVector
   * 
   * @param <V> Vector type
   */
  interface Factory<V extends NumberVector> extends FeatureVector.Factory<V, Number> {
    /**
     * Returns a new NumberVector of N for the given values.
     * 
     * @param values the values of the NumberVector
     * @return a new NumberVector of N for the given values
     */
    V newNumberVector(double[] values);

    /**
     * Returns a new NumberVector of N for the given values.
     * 
     * @param values Existing number vector
     * @return a new NumberVector of N for the given values
     */
    V newNumberVector(NumberVector values);

    /**
     * Instantiate from any number-array like object.
     * 
     * @param <A> Array type
     * @param array Array
     * @param adapter Adapter
     * @return a new NumberVector of N for the given values.
     */
    <A> V newNumberVector(A array, NumberArrayAdapter<?, ? super A> adapter);
  }
}

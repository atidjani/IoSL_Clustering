package de.lmu.ifi.dbs.elki.utilities.pairs;

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
 * Pair storing a native double value and an arbitrary object.
 * 
 * @author Erich Schubert
 * @since 0.4.0
 * 
 * @param <O> Object type
 */
public class DoubleObjPair<O> implements Comparable<DoubleObjPair<O>> {
  /**
   * Double value
   */
  public double first;

  /**
   * Second object value
   */
  public O second;

  /**
   * Constructor.
   * 
   * @param first First value
   * @param second Second value
   */
  public DoubleObjPair(double first, O second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public int compareTo(DoubleObjPair<O> o) {
    return Double.compare(first, o.first);
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof DoubleObjPair)) {
      // TODO: allow comparison with arbitrary pairs?
      return false;
    }
    DoubleObjPair<?> other = (DoubleObjPair<?>) obj;
    if(first != other.first) {
      return false;
    }
    if(second == null) {
      return (other.second == null);
    }
    return second.equals(other.second);
  }

  /**
   * Canonical toString operator
   */
  @Override
  public String toString() {
    return "Pair(" + first + ", " + (second != null ? second.toString() : "null") + ")";
  }
}
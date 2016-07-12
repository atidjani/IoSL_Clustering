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

import de.lmu.ifi.dbs.elki.data.FeatureVector;

/**
 * Adapter to use a feature vector as an array of features.
 *
 * Use the static instance from {@link ArrayLikeUtil}!
 *
 * @author Erich Schubert
 * @since 0.4.0
 *
 * @param <F> Feature type
 */
public class FeatureVectorAdapter<F> implements ArrayAdapter<F, FeatureVector<F>> {
  /**
   * Constructor.
   *
   * Use the static instance from {@link ArrayLikeUtil}!
   */
  protected FeatureVectorAdapter() {
    super();
  }

  @Override
  public int size(FeatureVector<F> array) {
    return array.getDimensionality();
  }

  @Override
  public F get(FeatureVector<F> array, int off) throws IndexOutOfBoundsException {
    return array.getValue(off);
  }
}

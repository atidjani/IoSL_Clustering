package de.lmu.ifi.dbs.elki.distance.distancefunction;

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

import de.lmu.ifi.dbs.elki.database.query.distance.DistanceQuery;
import de.lmu.ifi.dbs.elki.index.Index;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;

/**
 * Distance function relying on an index (such as preprocessed neighborhoods).
 * 
 * @author Erich Schubert
 * @since 0.4.0
 * 
 * @apiviz.landmark
 * @apiviz.stereotype factory
 * @apiviz.has Instance oneway - - «create»
 * 
 * @param <O> Object type
 */
public interface IndexBasedDistanceFunction<O> extends DistanceFunction<O> {
  /**
   * OptionID for the index parameter
   */
  public static final OptionID INDEX_ID = new OptionID("distancefunction.index", "Distance index to use.");

  /**
   * Instance interface for Index based distance functions.
   * 
   * @author Erich Schubert
   * 
   * @param <T> Object type
   */
  public static interface Instance<T, I extends Index> extends DistanceQuery<T> {
    /**
     * Get the index used.
     * 
     * @return the index used
     */
    public I getIndex();
  }
}
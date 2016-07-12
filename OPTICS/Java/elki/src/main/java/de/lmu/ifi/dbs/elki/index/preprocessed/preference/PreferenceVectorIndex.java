package de.lmu.ifi.dbs.elki.index.preprocessed.preference;

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

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRef;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.index.Index;
import de.lmu.ifi.dbs.elki.index.IndexFactory;

/**
 * Interface for an index providing preference vectors.
 * 
 * @author Erich Schubert
 * @since 0.4.0
 * 
 * @param <NV> Vector type
 */
public interface PreferenceVectorIndex<NV extends NumberVector> extends Index {
  /**
   * Get the precomputed preference vector for a particular object ID.
   * 
   * @param id Object ID
   * @return Matrix
   */
  public long[] getPreferenceVector(DBIDRef id);

  /**
   * Factory interface
   * 
   * @author Erich Schubert
   * 
   * @apiviz.stereotype factory
   * @apiviz.uses PreferenceVectorIndex oneway - - «create»
   * 
   * @param <V> vector type
   * @param <I> index type
   */
  public static interface Factory<V extends NumberVector, I extends PreferenceVectorIndex<V>> extends IndexFactory<V, I> {
    /**
     * Instantiate the index for a given database.
     * 
     * @param relation Relation to use
     * 
     * @return Index
     */
    @Override
    public I instantiate(Relation<V> relation);
  }
}
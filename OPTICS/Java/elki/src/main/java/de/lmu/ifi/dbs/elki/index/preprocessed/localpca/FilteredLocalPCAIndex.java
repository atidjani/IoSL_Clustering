package de.lmu.ifi.dbs.elki.index.preprocessed.localpca;

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
import de.lmu.ifi.dbs.elki.math.linearalgebra.pca.PCAFilteredResult;

/**
 * Interface for an index providing local PCA results.
 * 
 * @author Erich Schubert
 * @since 0.4.0
 * 
 * @param <NV> Vector type
 */
public interface FilteredLocalPCAIndex<NV extends NumberVector> extends Index {
  /**
   * Get the precomputed local PCA for a particular object ID.
   * 
   * @param objid Object ID
   * @return Matrix
   */
  public PCAFilteredResult getLocalProjection(DBIDRef objid);

  /**
   * Factory interface
   *
   * @author Erich Schubert
   *
   * @apiviz.stereotype factory
   * @apiviz.uses FilteredLocalPCAIndex oneway - - «create»
   *
   * @param <NV> Vector type
   * @param <I> Index type produced
   */
  public static interface Factory<NV extends NumberVector, I extends FilteredLocalPCAIndex<NV>> extends IndexFactory<NV, I> {
    /**
     * Instantiate the index for a given database.
     *
     * @param relation Relation to use
     *
     * @return Index
     */
    @Override
    public I instantiate(Relation<NV> relation);
  }
}
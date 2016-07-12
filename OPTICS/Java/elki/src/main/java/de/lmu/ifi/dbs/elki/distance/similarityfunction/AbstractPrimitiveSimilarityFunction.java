package de.lmu.ifi.dbs.elki.distance.similarityfunction;

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

import de.lmu.ifi.dbs.elki.data.type.SimpleTypeInformation;
import de.lmu.ifi.dbs.elki.database.query.similarity.PrimitiveSimilarityQuery;
import de.lmu.ifi.dbs.elki.database.query.similarity.SimilarityQuery;
import de.lmu.ifi.dbs.elki.database.relation.Relation;

/**
 * Base implementation of a similarity function.
 * 
 * @author Arthur Zimek
 * @since 0.4.0
 * 
 * @apiviz.excludeSubtypes
 * 
 * @param <O> object type
 */
public abstract class AbstractPrimitiveSimilarityFunction<O> implements PrimitiveSimilarityFunction<O> {
  /**
   * Constructor.
   */
  protected AbstractPrimitiveSimilarityFunction() {
    super();
  }

  @Override
  public boolean isSymmetric() {
    // Assume symmetric by default!
    return true;
  }

  @Override
  abstract public SimpleTypeInformation<? super O> getInputTypeRestriction();

  @Override
  abstract public double similarity(O o1, O o2);

  @Override
  public <T extends O> SimilarityQuery<T> instantiate(Relation<T> relation) {
    return new PrimitiveSimilarityQuery<>(relation, this);
  }
}

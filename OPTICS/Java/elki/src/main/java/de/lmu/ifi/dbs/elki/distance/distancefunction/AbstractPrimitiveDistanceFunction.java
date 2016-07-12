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
import de.lmu.ifi.dbs.elki.database.query.distance.PrimitiveDistanceQuery;
import de.lmu.ifi.dbs.elki.database.relation.Relation;

/**
 * AbstractDistanceFunction provides some methods valid for any extending class.
 * 
 * @author Arthur Zimek
 * @since 0.4.0
 * 
 * @apiviz.excludeSubtypes
 * 
 * @param <O> the type of objects to compute the distances in between
 */
public abstract class AbstractPrimitiveDistanceFunction<O> implements PrimitiveDistanceFunction<O> {
  /**
   * Constructor.
   */
  public AbstractPrimitiveDistanceFunction() {
    // EMPTY
  }

  @Override
  abstract public double distance(O o1, O o2);

  @Override
  public boolean isSymmetric() {
    // Assume symmetric by default!
    return true;
  }

  @Override
  public boolean isMetric() {
    // Do NOT assume triangle equation by default!
    return false;
  }

  /**
   * Instantiate with a database to get the actual distance query.
   * 
   * @param relation Representation
   * @return Actual distance query.
   */
  @Override
  public <T extends O> DistanceQuery<T> instantiate(Relation<T> relation) {
    return new PrimitiveDistanceQuery<>(relation, this);
  }
}
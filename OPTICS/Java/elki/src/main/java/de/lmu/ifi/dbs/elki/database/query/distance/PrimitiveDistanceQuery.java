package de.lmu.ifi.dbs.elki.database.query.distance;

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

import de.lmu.ifi.dbs.elki.database.ids.DBIDRef;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.PrimitiveDistanceFunction;

/**
 * Run a database query in a database context.
 * 
 * @author Erich Schubert
 * @since 0.4.0
 * 
 * @apiviz.landmark
 * @apiviz.uses PrimitiveDistanceFunction
 * 
 * @param <O> Database object type.
 */
public class PrimitiveDistanceQuery<O> extends AbstractDistanceQuery<O> {
  /**
   * The distance function we use.
   */
  final protected PrimitiveDistanceFunction<? super O> distanceFunction;

  /**
   * Constructor.
   * 
   * @param relation Representation to use.
   * @param distanceFunction Our distance function
   */
  public PrimitiveDistanceQuery(Relation<? extends O> relation, PrimitiveDistanceFunction<? super O> distanceFunction) {
    super(relation);
    this.distanceFunction = distanceFunction;
  }

  @Override
  public double distance(DBIDRef id1, DBIDRef id2) {
    O o1 = relation.get(id1);
    O o2 = relation.get(id2);
    return distance(o1, o2);
  }

  @Override
  public double distance(O o1, DBIDRef id2) {
    O o2 = relation.get(id2);
    return distance(o1, o2);
  }

  @Override
  public double distance(DBIDRef id1, O o2) {
    O o1 = relation.get(id1);
    return distance(o1, o2);
  }

  @Override
  public double distance(O o1, O o2) {
    if(o1 == null) {
      throw new UnsupportedOperationException("This distance function can only be used for object instances.");
    }
    if(o2 == null) {
      throw new UnsupportedOperationException("This distance function can only be used for object instances.");
    }
    return distanceFunction.distance(o1, o2);
  }

  @Override
  public PrimitiveDistanceFunction<? super O> getDistanceFunction() {
    return distanceFunction;
  }
}
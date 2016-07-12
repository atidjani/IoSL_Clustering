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

import de.lmu.ifi.dbs.elki.data.spatial.SpatialComparable;
import de.lmu.ifi.dbs.elki.database.query.distance.SpatialDistanceQuery;
import de.lmu.ifi.dbs.elki.database.relation.Relation;

/**
 * API for a spatial primitive distance function.
 * 
 * @author Erich Schubert
 * @since 0.4.0
 * 
 * @param <V> Vector type
 */
public interface SpatialPrimitiveDistanceFunction<V extends SpatialComparable> extends PrimitiveDistanceFunction<V> {
  /**
   * Computes the distance between the two given MBRs according to this distance
   * function.
   * 
   * @param mbr1 the first MBR object
   * @param mbr2 the second MBR object
   * @return the distance between the two given MBRs according to this distance
   *         function
   */
  double minDist(SpatialComparable mbr1, SpatialComparable mbr2);

  @Override
  public <T extends V> SpatialDistanceQuery<T> instantiate(Relation<T> relation);
}
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

import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRef;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DBIDDistanceFunction;

/**
 * Run a distance query based on DBIDs
 * 
 * @author Erich Schubert
 * @since 0.4.0
 * 
 * @apiviz.landmark
 * @apiviz.uses DBIDDistanceFunction
 */
public class DBIDDistanceQuery extends AbstractDatabaseDistanceQuery<DBID> {
  /**
   * The distance function we use.
   */
  final protected DBIDDistanceFunction distanceFunction;

  /**
   * Constructor.
   * 
   * @param relation Database to use.
   * @param distanceFunction Our distance function
   */
  public DBIDDistanceQuery(Relation<DBID> relation, DBIDDistanceFunction distanceFunction) {
    super(relation);
    this.distanceFunction = distanceFunction;
  }

  @Override
  public double distance(DBIDRef id1, DBIDRef id2) {
    if(id1 == null) {
      throw new UnsupportedOperationException("This distance function can only be used for objects stored in the database.");
    }
    if(id2 == null) {
      throw new UnsupportedOperationException("This distance function can only be used for objects stored in the database.");
    }
    return distanceFunction.distance(id1, id2);
  }

  @Override
  public DBIDDistanceFunction getDistanceFunction() {
    return distanceFunction;
  }
}
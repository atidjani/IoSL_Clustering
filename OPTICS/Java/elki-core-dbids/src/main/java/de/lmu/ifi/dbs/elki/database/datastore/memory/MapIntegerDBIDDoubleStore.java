package de.lmu.ifi.dbs.elki.database.datastore.memory;

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

import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import de.lmu.ifi.dbs.elki.database.datastore.WritableDoubleDataStore;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRef;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;

/**
 * Writable data store for double values.
 *
 * @author Erich Schubert
 * @since 0.5.0
 */
public class MapIntegerDBIDDoubleStore implements WritableDoubleDataStore {
  /**
   * Data storage.
   */
  private TIntDoubleMap map;

  /**
   * Constructor.
   *
   * @param size Expected size
   */
  public MapIntegerDBIDDoubleStore(int size) {
    this(size, Double.NaN);
  }

  /**
   * Constructor.
   *
   * @param size Expected size
   * @param def Default value
   */
  public MapIntegerDBIDDoubleStore(int size, double def) {
    super();
    map = new TIntDoubleHashMap(size, 0.5f, Integer.MIN_VALUE, def);
  }

  @Override
  @Deprecated
  public Double get(DBIDRef id) {
    return Double.valueOf(map.get(DBIDUtil.asInteger(id)));
  }

  @Override
  public double doubleValue(DBIDRef id) {
    return map.get(DBIDUtil.asInteger(id));
  }

  @Override
  @Deprecated
  public Double put(DBIDRef id, Double value) {
    return Double.valueOf(map.put(DBIDUtil.asInteger(id), value.doubleValue()));
  }

  @Override
  public void delete(DBIDRef id) {
    map.remove(DBIDUtil.asInteger(id));
  }

  @Override
  public double putDouble(DBIDRef id, double value) {
    return map.put(DBIDUtil.asInteger(id), value);
  }

  @Override
  public double put(DBIDRef id, double value) {
    return map.put(DBIDUtil.asInteger(id), value);
  }

  @Override
  public void increment(DBIDRef id, double value) {
    map.adjustOrPutValue(DBIDUtil.asInteger(id), value, map.getNoEntryValue() + value);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public void destroy() {
    map.clear();
    map = null;
  }
}

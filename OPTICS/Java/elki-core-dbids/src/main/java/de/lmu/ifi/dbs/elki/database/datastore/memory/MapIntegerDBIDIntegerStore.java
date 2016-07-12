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

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import de.lmu.ifi.dbs.elki.database.datastore.WritableIntegerDataStore;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRef;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;

/**
 * Writable data store for double values.
 *
 * @author Erich Schubert
 * @since 0.5.0
 */
public class MapIntegerDBIDIntegerStore implements WritableIntegerDataStore {
  /**
   * Data storage.
   */
  private TIntIntMap map;

  /**
   * Constructor.
   *
   * @param size Expected size
   */
  public MapIntegerDBIDIntegerStore(int size) {
    this(size, 0);
  }

  /**
   * Constructor.
   *
   * @param size Expected size
   * @param def Default value
   */
  public MapIntegerDBIDIntegerStore(int size, int def) {
    super();
    map = new TIntIntHashMap(size, 0.5f, Integer.MIN_VALUE, def);
  }

  @Override
  @Deprecated
  public Integer get(DBIDRef id) {
    return Integer.valueOf(map.get(DBIDUtil.asInteger(id)));
  }

  @Override
  public int intValue(DBIDRef id) {
    return map.get(DBIDUtil.asInteger(id));
  }

  @Override
  @Deprecated
  public Integer put(DBIDRef id, Integer value) {
    return Integer.valueOf(map.put(DBIDUtil.asInteger(id), value.intValue()));
  }

  @Override
  public void destroy() {
    map.clear();
    map = null;
  }

  @Override
  public void delete(DBIDRef id) {
    map.remove(DBIDUtil.asInteger(id));
  }

  @Override
  public int putInt(DBIDRef id, int value) {
    return map.put(DBIDUtil.asInteger(id), value);
  }

  @Override
  public int put(DBIDRef id, int value) {
    return map.put(DBIDUtil.asInteger(id), value);
  }

  @Override
  public void increment(DBIDRef id, int adjust) {
    map.adjustOrPutValue(DBIDUtil.asInteger(id), adjust, adjust);
  }

  @Override
  public void clear() {
    map.clear();
  }
}

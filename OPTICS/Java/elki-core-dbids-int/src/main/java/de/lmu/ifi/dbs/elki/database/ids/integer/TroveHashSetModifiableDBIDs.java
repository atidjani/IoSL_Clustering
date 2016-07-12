package de.lmu.ifi.dbs.elki.database.ids.integer;

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

import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.set.hash.TIntHashSet;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDMIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRef;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDVar;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.HashSetModifiableDBIDs;
import de.lmu.ifi.dbs.elki.utilities.datastructures.iterator.Iter;

/**
 * Implementation using GNU Trove Int Hash Sets.
 * 
 * @author Erich Schubert
 * @since 0.4.0
 * 
 * @apiviz.has Itr
 */
class TroveHashSetModifiableDBIDs implements HashSetModifiableDBIDs, IntegerDBIDs {
  /**
   * The actual store.
   */
  TIntHashSet store;

  /**
   * Constructor.
   * 
   * @param size Initial size
   */
  protected TroveHashSetModifiableDBIDs(int size) {
    super();
    this.store = new TIntHashSet(size);
  }

  /**
   * Constructor.
   */
  protected TroveHashSetModifiableDBIDs() {
    super();
    this.store = new TIntHashSet();
  }

  /**
   * Constructor.
   * 
   * @param existing Existing IDs
   */
  protected TroveHashSetModifiableDBIDs(DBIDs existing) {
    this(existing.size());
    this.addDBIDs(existing);
  }

  @Override
  public Itr iter() {
    return new Itr(store);
  }

  @Override
  public boolean addDBIDs(DBIDs ids) {
    store.ensureCapacity(ids.size());
    boolean success = false;
    for(DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
      success |= store.add(DBIDUtil.asInteger(iter));
    }
    return success;
  }

  @Override
  public boolean removeDBIDs(DBIDs ids) {
    boolean success = false;
    for(DBIDIter id = ids.iter(); id.valid(); id.advance()) {
      success |= store.remove(DBIDUtil.asInteger(id));
    }
    return success;
  }

  @Override
  public boolean add(DBIDRef e) {
    return store.add(DBIDUtil.asInteger(e));
  }

  @Override
  public boolean remove(DBIDRef o) {
    return store.remove(DBIDUtil.asInteger(o));
  }

  @Override
  public boolean retainAll(DBIDs set) {
    boolean modified = false;
    for(DBIDMIter it = iter(); it.valid(); it.advance()) {
      if(!set.contains(it)) {
        it.remove();
        modified = true;
      }
    }
    return modified;
  }

  @Override
  public int size() {
    return store.size();
  }

  @Override
  public boolean isEmpty() {
    return store.isEmpty();
  }

  @Override
  public void clear() {
    store.clear();
  }

  @Override
  public boolean contains(DBIDRef o) {
    return store.contains(DBIDUtil.asInteger(o));
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    for(DBIDIter iter = iter(); iter.valid(); iter.advance()) {
      if(buf.length() > 1) {
        buf.append(", ");
      }
      buf.append(iter.toString());
    }
    buf.append(']');
    return buf.toString();
  }

  @Override
  public DBIDVar pop(DBIDVar outvar) {
    if(store.size() == 0) {
      throw new ArrayIndexOutOfBoundsException("Cannot pop() from an empty array.");
    }
    final byte[] states = store._states;
    int i = store.capacity();
    while(i-- > 0 && (states[i] != TPrimitiveHash.FULL)) {
      ; // Not occupied. Continue
    }
    if(i < 0) { // Should never happen because size > 0
      throw new ArrayIndexOutOfBoundsException("Cannot pop() from an empty array.");
    }
    final int val = store._set[i];
    if(outvar instanceof IntegerDBIDVar) {
      ((IntegerDBIDVar) outvar).internalSetIndex(val);
    }
    else { // Fallback, should not happen (more expensive).
      outvar.set(DBIDUtil.importInteger(val));
    }
    // Unfortunately, not visible: store.removeAt(i);
    store.remove(val);
    return outvar;
  }

  /**
   * Iterator over trove hashs.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  protected static class Itr implements IntegerDBIDMIter {
    /**
     * The actual iterator. We don't have multi inheritance.
     */
    TIntHashItr it;

    /**
     * Constructor.
     * 
     * @param hash Trove hash
     */
    public Itr(TIntHash hash) {
      super();
      this.it = new TIntHashItr(hash);
    }

    @Override
    public boolean valid() {
      return it.valid();
    }

    @Override
    public IntegerDBIDMIter advance() {
      it.advance();
      return this;
    }

    @Override
    public int internalGetIndex() {
      return it.getInt();
    }

    @Override
    public String toString() {
      return Integer.toString(internalGetIndex());
    }

    @Override
    public void remove() {
      it.remove();
    }

    /**
     * Custom iterator over TIntHash.
     * 
     * @author Erich Schubert
     * 
     * @apiviz.exclude
     */
    private static class TIntHashItr extends THashPrimitiveIterator implements Iter {
      /**
       * The hash we access.
       */
      private TIntHash hash;

      /**
       * Constructor.
       * 
       * @param hash Hash to iterate over.
       */
      public TIntHashItr(TIntHash hash) {
        super(hash);
        this.hash = hash;
        this._index = nextIndex(); // Find first element
      }

      /**
       * Get the current value.
       * 
       * @return Current value
       */
      public int getInt() {
        return hash._set[_index];
      }

      @Override
      public Iter advance() {
        this._index = nextIndex();
        return this;
      }

      @Override
      public boolean valid() {
        return _index >= 0;
      }
    }
  }
}

package de.lmu.ifi.dbs.elki.persistent;

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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import de.lmu.ifi.dbs.elki.logging.Logging;

/**
 * A memory based implementation of a PageFile that simulates I/O-access.<br>
 * Implemented as a Map with keys representing the ids of the saved pages.
 * 
 * @author Elke Achtert
 * @since 0.2
 * 
 * @param <P> Page type
 */
public class MemoryPageFile<P extends Page> extends AbstractStoringPageFile<P> {
  /**
   * Class logger.
   */
  private static final Logging LOG = Logging.getLogger(MemoryPageFile.class);

  /**
   * Holds the pages.
   */
  private final TIntObjectMap<P> file;

  /**
   * Creates a new MemoryPageFile that is supported by a cache with the
   * specified parameters.
   * 
   * @param pageSize the size of a page in Bytes
   */
  public MemoryPageFile(int pageSize) {
    super(pageSize);
    this.file = new TIntObjectHashMap<>();
  }

  @Override
  public synchronized P readPage(int pageID) {
    countRead();
    return file.get(pageID);
  }

  @Override
  protected void writePage(int pageID, P page) {
    countWrite();
    file.put(pageID, page);
    page.setDirty(false);
  }

  @Override
  public synchronized void deletePage(int pageID) {
    // put id to empty nodes and
    // delete from cache
    super.deletePage(pageID);

    // delete from file
    countWrite();
    file.remove(pageID);
  }

  @Override
  public void clear() {
    file.clear();
  }

  @Override
  protected Logging getLogger() {
    return LOG;
  }
}

package de.lmu.ifi.dbs.elki.index.tree;

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

/**
 * Directory Entry
 * 
 * @author Erich Schubert
 * @since 0.4.0
 * 
 * @apiviz.excludeSubtypes
 */
public interface DirectoryEntry extends Entry {
  /**
   * Returns the id of the node or data object that is represented by this
   * entry.
   * 
   * @return the id of the node or data object that is represented by this entry
   */
  Integer getEntryID();

  /**
   * Get the page ID of this leaf entry.
   */
  public Integer getPageID();
}

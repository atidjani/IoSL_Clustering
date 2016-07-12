package de.lmu.ifi.dbs.elki.result;

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

import java.util.Iterator;

/**
 * Interface of an "iterable" result (e.g. a list, table) that can be printed one-by-one.
 * (At least when the class O is TextWriteable)
 * 
 * @author Erich Schubert
 * @since 0.2
 * 
 * @apiviz.landmark
 *
 * @param <O> object class.
 */
public interface IterableResult<O> extends Result, Iterable<O> {
  /**
   * Retrieve an iterator for the result.
   * 
   * @return iterator
   */
  @Override
  public Iterator<O> iterator();
}
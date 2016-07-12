package de.lmu.ifi.dbs.elki.distance.distancefunction.external;

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

import java.io.InputStream;

/**
 * A DistanceParser shall provide a DistanceParsingResult by parsing an
 * InputStream.
 * 
 * @author Arthur Zimek
 * @since 0.2
 * 
 * @apiviz.uses DistanceCacheWriter oneway - - «create»
 */
public interface DistanceParser {
  /**
   * Returns a list of the objects parsed from the specified input stream and a
   * list of the labels associated with the objects.
   * 
   * @param in the stream to parse objects from
   * @param cache Cache writer
   */
  void parse(InputStream in, DistanceCacheWriter cache);
}

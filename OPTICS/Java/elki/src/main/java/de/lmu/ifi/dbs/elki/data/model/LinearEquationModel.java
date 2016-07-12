package de.lmu.ifi.dbs.elki.data.model;

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

import de.lmu.ifi.dbs.elki.math.linearalgebra.LinearEquationSystem;
import de.lmu.ifi.dbs.elki.result.textwriter.TextWriteable;
import de.lmu.ifi.dbs.elki.result.textwriter.TextWriterStream;

/**
 * Cluster model containing a linear equation system for the cluster.
 * 
 * @author Erich Schubert
 * @since 0.2
 *
 * @apiviz.composedOf LinearEquationSystem
 */
public class LinearEquationModel extends AbstractModel implements TextWriteable {
  /**
   * Equation system
   */
  private LinearEquationSystem les;

  /**
   * Constructor
   * @param les equation system
   */
  public LinearEquationModel(LinearEquationSystem les) {
    super();
    this.les = les;
  }

  /**
   * Get assigned Linear Equation System
   * 
   * @return linear equation system
   */
  public LinearEquationSystem getLes() {
    return les;
  }

  /**
   * Assign new Linear Equation System.
   * 
   * @param les new linear equation system
   */
  public void setLes(LinearEquationSystem les) {
    this.les = les;
  }
  
  /**
   * Implementation of {@link TextWriteable} interface
   */
  @Override
  public void writeToText(TextWriterStream out, String label) {
    super.writeToText(out, label);
    out.commentPrintLn(les.equationsToString(6));
  }

}

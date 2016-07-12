package de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints;

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

import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleArrayListParameter;

/**
 * Global parameter constraint for testing if the dimensions of each vector
 * specified by a given vector list parameter ({@link DoubleArrayListParameter})
 * correspond to the value of a integer parameter ({@link IntParameter}) given.
 * 
 * @author Steffi Wanka
 * @since 0.2
 */
public class GlobalVectorListElementSizeConstraint implements GlobalParameterConstraint {
  /**
   * Vector list parameter.
   */
  private DoubleArrayListParameter vector;

  /**
   * Integer parameter providing the size constraint.
   */
  private IntParameter size;

  /**
   * Constructs a global vector size constraint.
   * <p/>
   * Each vector of the vector list parameter given is tested for being equal to
   * the value of the integer parameter given.
   * 
   * @param vector the vector list parameter
   * @param sizeConstraint the integer parameter providing the size constraint
   */
  public GlobalVectorListElementSizeConstraint(DoubleArrayListParameter vector, IntParameter sizeConstraint) {
    this.vector = vector;
    this.size = sizeConstraint;
  }

  /**
   * Checks if the dimensions of each vector of the vector list parameter have
   * the appropriate size provided by the integer parameter. If not, a parameter
   * exception will be thrown.
   * 
   */
  @Override
  public void test() throws ParameterException {
    if(!vector.isDefined()) {
      return;
    }

    for(double[] vec : vector.getValue()) {
      if(vec.length != size.intValue()) {
        throw new WrongParameterValueException("Global Parameter Constraint Error.\n" //
        + "The vectors of vector list parameter " + vector.getName() //
        + " have not the required dimension of " + size.getValue() //
        + " given by integer parameter " + size.getName() + ".");
      }
    }
  }

  @Override
  public String getDescription() {
    return "The dimensionality of the vectors of vector list parameter " + vector.getName() + " must have the value of integer parameter " + size.getName();
  }
}

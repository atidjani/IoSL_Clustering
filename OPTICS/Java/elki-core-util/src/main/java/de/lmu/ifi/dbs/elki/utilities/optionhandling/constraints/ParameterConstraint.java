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

/**
 * <p/>
 * Interface for specifying parameter constraints.
 * </p>
 * <p/>
 * Each class specifying a constraint addressing only one parameter should implement this interface.
 * The constraint value for testing the parameter should be defined as private attribute and should be initialized in the
 * respective constructor of the class, i.e. it is a parameter of the constructor. The proper constraint
 * test should be implemented in the method {@link #test(Object) test(T)}.
 * </p>
 *
 * @author Steffi Wanka
 * @since 0.2
 * @param <T> the type of the parameter the constraint applies to
 */
public interface ParameterConstraint<T> {
    /**
     * Checks if the value {@code t} of the parameter to be tested fulfills the parameter constraint.
     * If not, a parameter exception is thrown.
     *
     * @param t Value to be checked whether or not it fulfills the underlying
     *          parameter constraint.
     * @throws ParameterException if the parameter to be tested does not
     *                            fulfill the parameter constraint
     */
    public abstract void test(T t) throws ParameterException;

    /**
     * Returns a description of this constraint.
     *
     * @param parameterName the name of the parameter this constraint is used for
     * @return a description of this constraint
     */
    public abstract String getDescription(String parameterName);

}

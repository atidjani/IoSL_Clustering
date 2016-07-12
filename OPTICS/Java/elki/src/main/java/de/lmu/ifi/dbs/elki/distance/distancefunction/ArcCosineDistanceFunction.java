package de.lmu.ifi.dbs.elki.distance.distancefunction;

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

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.VectorUtil;
import de.lmu.ifi.dbs.elki.data.spatial.SpatialComparable;
import de.lmu.ifi.dbs.elki.data.type.SimpleTypeInformation;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.utilities.Alias;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;

/**
 * Cosine distance function for feature vectors.
 * 
 * The cosine distance is computed as the arcus from the cosine similarity
 * value, i.e., <code>arccos(&lt;v1,v2&gt;)</code>.
 * 
 * @author Arthur Zimek
 * @since 0.2
 */
@Alias({ "arccos" })
public class ArcCosineDistanceFunction extends AbstractSpatialDistanceFunction {
  /**
   * Static instance
   */
  public static final ArcCosineDistanceFunction STATIC = new ArcCosineDistanceFunction();

  /**
   * Constructor - use {@link #STATIC} instead.
   * 
   * @deprecated Use static instance!
   */
  @Deprecated
  public ArcCosineDistanceFunction() {
    super();
  }

  /**
   * Computes the cosine distance for two given feature vectors.
   * 
   * The cosine distance is computed as the arcus from the cosine similarity
   * value, i.e., <code>arccos(&lt;v1,v2&gt;)</code>.
   * 
   * @param v1 first feature vector
   * @param v2 second feature vector
   * @return the cosine distance for two given feature vectors v1 and v2
   */
  @Override
  public double distance(NumberVector v1, NumberVector v2) {
    double d = Math.acos(VectorUtil.cosAngle(v1, v2));
    return (d > 0) ? d : 0;
  }

  @Override
  public double minDist(SpatialComparable mbr1, SpatialComparable mbr2) {
    double d = Math.acos(VectorUtil.minCosAngle(mbr1, mbr2));
    return (d > 0) ? d : 0;
  }

  @Override
  public String toString() {
    return "ArcCosineDistance";
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) {
      return false;
    }
    if(obj == this) {
      return true;
    }
    return this.getClass().equals(obj.getClass());
  }

  @Override
  public SimpleTypeInformation<? super NumberVector> getInputTypeRestriction() {
    return TypeUtil.NUMBER_VECTOR_VARIABLE_LENGTH;
  }

  /**
   * Parameterization class.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  public static class Parameterizer extends AbstractParameterizer {
    @Override
    protected ArcCosineDistanceFunction makeInstance() {
      return ArcCosineDistanceFunction.STATIC;
    }
  }
}

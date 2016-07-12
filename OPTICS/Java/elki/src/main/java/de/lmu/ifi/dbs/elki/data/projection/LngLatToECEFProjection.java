package de.lmu.ifi.dbs.elki.data.projection;

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
import de.lmu.ifi.dbs.elki.data.type.SimpleTypeInformation;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.data.type.VectorFieldTypeInformation;
import de.lmu.ifi.dbs.elki.math.geodesy.EarthModel;
import de.lmu.ifi.dbs.elki.math.geodesy.SphericalVincentyEarthModel;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;

/**
 * Project (Longitude, Latitude) vectors to (X, Y, Z), from spherical
 * coordinates to ECEF (earth-centered earth-fixed).
 * 
 * @author Erich Schubert
 * @since 0.4.0
 * 
 * @apiviz.composedOf EarthModel
 * @apiviz.uses NumberVector
 * 
 * @param <V> Vector type
 */
public class LngLatToECEFProjection<V extends NumberVector> implements Projection<V, V> {
  /**
   * Earth model to use
   */
  EarthModel model;

  /**
   * Vector factory to use.
   */
  private NumberVector.Factory<V> factory;

  /**
   * Constructor.
   * 
   * @param model Earth model to use.
   */
  public LngLatToECEFProjection(EarthModel model) {
    super();
    this.model = model;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(SimpleTypeInformation<V> in) {
    final VectorFieldTypeInformation<V> vin = (VectorFieldTypeInformation<V>) in;
    factory = (NumberVector.Factory<V>) vin.getFactory();
  }

  @Override
  public V project(V data) {
    return factory.newNumberVector(model.latLngDegToECEF(data.doubleValue(1), data.doubleValue(0)));
  }

  @Override
  public SimpleTypeInformation<? super V> getInputDataTypeInformation() {
    return TypeUtil.NUMBER_VECTOR_FIELD_2D;
  }

  @Override
  public SimpleTypeInformation<V> getOutputDataTypeInformation() {
    return new VectorFieldTypeInformation<>(factory, 3, 3, factory.getDefaultSerializer());
  }

  /**
   * Parameterization class.
   * 
   * @apiviz.exclude
   * 
   * @author Erich Schubert
   */
  public static class Parameterizer extends AbstractParameterizer {
    /**
     * Earth model to use.
     */
    EarthModel model;

    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);
      ObjectParameter<EarthModel> modelP = new ObjectParameter<>(EarthModel.MODEL_ID, EarthModel.class, SphericalVincentyEarthModel.class);
      if(config.grab(modelP)) {
        model = modelP.instantiateClass(config);
      }
    }

    @Override
    protected LngLatToECEFProjection<NumberVector> makeInstance() {
      return new LngLatToECEFProjection<>(model);
    }
  }
}

package de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.strategies.bulk;

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
import java.util.List;

import de.lmu.ifi.dbs.elki.data.spatial.SpatialComparable;
import de.lmu.ifi.dbs.elki.math.spacefillingcurves.SpatialSorter;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;

/**
 * Bulk loading by spatially sorting the objects, then partitioning the sorted
 * list appropriately.
 * 
 * Based conceptually on:
 * <p>
 * On packing R-trees<br/>
 * Kamel, I. and Faloutsos, C.<br/>
 * Proc. of the second international conference on Information and knowledge
 * management
 * </p>
 * 
 * @apiviz.composedOf SpatialSorter 
 * 
 * @author Erich Schubert
 * @since 0.5.0
 */
@Reference(title = "On packing R-trees", authors = "Kamel, I. and Faloutsos, C.", booktitle = "Proc. of the second international conference on Information and knowledge management", url = "http://dx.doi.org/10.1145/170088.170403")
public class SpatialSortBulkSplit extends AbstractBulkSplit {
  /**
   * Sorting class
   */
  final SpatialSorter sorter;

  /**
   * Constructor.
   * 
   * @param sorter Sorting strategy
   */
  protected SpatialSortBulkSplit(SpatialSorter sorter) {
    super();
    this.sorter = sorter;
  }

  @Override
  public <T extends SpatialComparable> List<List<T>> partition(List<T> spatialObjects, int minEntries, int maxEntries) {
    sorter.sort(spatialObjects);
    return super.trivialPartition(spatialObjects, minEntries, maxEntries);
  }

  /**
   * Parametization class
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  public static class Parameterizer extends AbstractParameterizer {
    /**
     * Option ID for spatial sorting
     */
    public static final OptionID SORTER_ID = new OptionID("rtree.bulk.spatial-sort", "Strategy for spatial sorting in bulk loading.");

    /**
     * Sorting class
     */
    SpatialSorter sorter;

    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);

      ObjectParameter<SpatialSorter> sorterP = new ObjectParameter<>(SORTER_ID, SpatialSorter.class);
      if(config.grab(sorterP)) {
        sorter = sorterP.instantiateClass(config);
      }
    }

    @Override
    protected SpatialSortBulkSplit makeInstance() {
      return new SpatialSortBulkSplit(sorter);
    }
  }
}

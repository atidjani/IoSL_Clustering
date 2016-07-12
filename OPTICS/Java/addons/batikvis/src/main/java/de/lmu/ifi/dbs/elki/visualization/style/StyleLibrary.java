package de.lmu.ifi.dbs.elki.visualization.style;

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

import de.lmu.ifi.dbs.elki.visualization.colors.ColorLibrary;
import de.lmu.ifi.dbs.elki.visualization.style.lines.LineStyleLibrary;
import de.lmu.ifi.dbs.elki.visualization.style.marker.MarkerLibrary;

/**
 * Style library interface. A style library allows the user to customize the
 * visual rendering, for example for print media or screen presentation without
 * having to change program code.
 * 
 * @author Erich Schubert
 * @since 0.3
 * 
 * @apiviz.composedOf ColorLibrary
 * @apiviz.composedOf LineStyleLibrary
 * @apiviz.composedOf MarkerLibrary
 */
public interface StyleLibrary {
  /**
   * Default
   */
  final static String DEFAULT = "";

  /**
   * Page
   */
  final static String PAGE = "page";

  /**
   * Plot
   */
  final static String PLOT = "plot";

  /**
   * Axis
   */
  final static String AXIS = "axis";

  /**
   * Axis tick
   */
  final static String AXIS_TICK = "axis.tick";

  /**
   * Axis minor tick
   */
  final static String AXIS_TICK_MINOR = "axis.tick.minor";

  /**
   * Axis label
   */
  final static String AXIS_LABEL = "axis.label";

  /**
   * Key
   */
  final static String KEY = "key";

  /**
   * Clusterorder
   */
  final static String CLUSTERORDER = "plot.clusterorder";

  /**
   * Margin
   */
  final static String MARGIN = "margin";

  /**
   * Bubble size
   */
  final static String BUBBLEPLOT = "plot.bubble";

  /**
   * Marker size
   */
  final static String MARKERPLOT = "plot.marker";

  /**
   * Dot size
   */
  final static String DOTPLOT = "plot.dot";

  /**
   * Grayed out objects
   */
  final static String PLOTGREY = "plot.grey";

  /**
   * Reference points color and size
   */
  final static String REFERENCE_POINTS = "plot.referencepoints";

  /**
   * Polygons style
   */
  final static String POLYGONS = "plot.polygons";

  /**
   * Selection color and opacity
   */
  final static String SELECTION = "plot.selection";

  /**
   * Selection color and opacity during selecting process
   */
  final static String SELECTION_ACTIVE = "plot.selection.active";

  /**
   * Scaling constant. Keep in sync with
   * {@link de.lmu.ifi.dbs.elki.visualization.projections.Projection#SCALE}
   */
  public static final double SCALE = 100.0;

  /* ** Property types ** */
  /**
   * Color
   */
  final static String COLOR = "color";

  /**
   * Background color
   */
  final static String BACKGROUND_COLOR = "background-color";

  /**
   * Text color
   */
  final static String TEXT_COLOR = "text-color";

  /**
   * Color set
   */
  final static String COLORSET = "colorset";

  /**
   * Line width
   */
  final static String LINE_WIDTH = "line-width";

  /**
   * Text size
   */
  final static String TEXT_SIZE = "text-size";

  /**
   * Font family
   */
  final static String FONT_FAMILY = "font-family";

  /**
   * Generic size
   */
  final static String GENERIC_SIZE = "size";

  /**
   * Opacity (transparency)
   */
  final static String OPACITY = "opacity";

  /**
   * XY curve styling.
   */
  static final String XYCURVE = "xycurve";

  /**
   * Retrieve a color for an item
   * 
   * @param name Reference name
   * @return color in CSS/SVG valid format: hexadecimal (#aabbcc) or names such
   *         as "red"
   */
  public String getColor(String name);

  /**
   * Retrieve background color for an item
   * 
   * @param name Reference name
   * @return color in CSS/SVG valid format: hexadecimal (#aabbcc) or names such
   *         as "red"
   */
  public String getBackgroundColor(String name);

  /**
   * Retrieve text color for an item
   * 
   * @param name Reference name
   * @return color in CSS/SVG valid format: hexadecimal (#aabbcc) or names such
   *         as "red"
   */
  public String getTextColor(String name);

  /**
   * Retrieve colorset for an item
   * 
   * @param name Reference name
   * @return color library
   */
  public ColorLibrary getColorSet(String name);

  /**
   * Get line width
   * 
   * @param key Key
   * @return line width as double
   */
  public double getLineWidth(String key);

  /**
   * Get generic size
   * 
   * @param key Key
   * @return size as double
   */
  public double getSize(String key);

  /**
   * Get text size
   * 
   * @param key Key
   * @return line width as double
   */
  public double getTextSize(String key);

  /**
   * Get font family
   * 
   * @param key Key
   * @return font family CSS string
   */
  public String getFontFamily(String key);

  /**
   * Get opacity
   * 
   * @param key Key
   * @return size as double
   */
  public double getOpacity(String key);
  
  /**
   * Get the line style library to use.
   * 
   * @return line style library
   */
  public LineStyleLibrary lines();
  
  /**
   * Get the marker library to use.
   * 
   * @return marker library
   */
  public MarkerLibrary markers();
}
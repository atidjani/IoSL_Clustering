package de.lmu.ifi.dbs.elki.visualization.colors;

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
 * Color scheme interface
 * 
 * @author Erich Schubert
 * @since 0.2
 */
public interface ColorLibrary {
  /**
   * List of line colors
   */
  final static String COLOR_LINE_COLORS = "line.colors";

  /**
   * Named color for the page background
   */
  final static String COLOR_PAGE_BACKGROUND = "page.background";

  /**
   * Named color for a typical axis
   */
  final static String COLOR_AXIS_LINE = "axis.line";

  /**
   * Named color for a typical axis tick mark
   */
  final static String COLOR_AXIS_TICK = "axis.tick";

  /**
   * Named color for a typical axis tick mark
   */
  final static String COLOR_AXIS_MINOR_TICK = "axis.tick.minor";

  /**
   * Named color for a typical axis label
   */
  final static String COLOR_AXIS_LABEL = "axis.label";

  /**
   * Named color for the background of the key box
   */
  final static String COLOR_KEY_BACKGROUND = "key.background";

  /**
   * Named color for a label in the key part
   */
  final static String COLOR_KEY_LABEL = "key.label";

  /**
   * Background color for plot area
   */
  final static String COLOR_PLOT_BACKGROUND = "plot.background";

  /**
   * Return the number of native colors available. These are guaranteed to be
   * unique.
   * 
   * @return number of native colors
   */
  public int getNumberOfNativeColors();

  /**
   * Return the i'th color.
   * 
   * @param index color index
   * @return color in hexadecimal notation (#aabbcc) or color name ("red") as
   *         valid in CSS and SVG.
   */
  public String getColor(int index);
}

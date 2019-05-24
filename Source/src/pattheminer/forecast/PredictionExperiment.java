/*
    A benchmark for Pat The Miner
    Copyright (C) 2018-2019 Laboratoire d'informatique formelle

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pattheminer.forecast;

import pattheminer.StreamExperiment;

public abstract class PredictionExperiment extends StreamExperiment
{
  /**
   * The slicing function (f)
   */
  public static final transient String F = "Slicing function (f)";
  
  /**
   * The width of the window used to compute the feature (m)
   */
  public static final transient String M = "m";
  
  /**
   * The feature computed on a window (&phi;)
   */
  public static final transient String PHI = "Feature processor (phi)";
  
  /**
   * Number of slices
   */
  public static final transient String NUM_SLICES = "Number of slices";
  
  public PredictionExperiment()
  {
    super();
    describe(F, "The slicing function (f)");
    describe(M, "The width of the window used to compute the feature (m)");
    describe(PHI, "The feature computed on a window (&phi;)");
    describe(NUM_SLICES, "Number of distinct process instances contained in the input log");
  }
}

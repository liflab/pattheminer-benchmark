/*
    A benchmark for Pat The Miner
    Copyright (C) 2018 Laboratoire d'informatique formelle

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
package pattheminer.trenddistance;

import pattheminer.TrendExperiment;

public class TrendDistanceExperiment extends TrendExperiment
{  
  public static final transient String TYPE_NAME = "Trend distance";
  
  public static final transient String RUNNING_AVG = "Running average";
  public static final transient String RUNNING_MOMENTS = "Running moments";
  public static final transient String CLOSEST_CLUSTER = "Closest cluster";
  public static final transient String SYMBOL_DISTRIBUTION = "Symbol distribution";
  public static final transient String N_GRAMS = "N-grams";
  public static final transient String N_GRAM_WIDTH = "N-gram width";
  
  public TrendDistanceExperiment()
  {
    super();
    setDescription("Measures the throughput of the trend distance processor");
    setInput(TYPE, TYPE_NAME);
  }
}

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
package pattheminer;

public abstract class TrendExperiment extends StreamExperiment
{
  public static final transient String METRIC = "Metric";
  
  public static final transient String TREND = "Trend function";
  
  public static final transient String WIDTH = "Window width";
  
  public static final transient String TYPE = "Type";

  public TrendExperiment()
  {
    super();
    describe(METRIC, "The metric used to compute the distance between the reference trend and the computed trend");
    describe(TREND, "The trend computed on the event stream");
    describe(WIDTH, "The width of the window over which the trend is computed");
    describe(TYPE, "The type of mining pattern used");
  }
}
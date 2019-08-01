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
package pattheminer.rscript;

import static pattheminer.trenddistance.TrendExperiment.METRIC;
import static pattheminer.trenddistance.TrendExperiment.TREND;
import static pattheminer.trenddistance.TrendExperiment.WIDTH;

import ca.uqac.lif.labpal.Experiment;

import static pattheminer.trenddistance.TrendExperiment.TYPE;

/**
 * Experiment that evaluates the static trend distance pattern using R
 */
public class StaticTrendDistanceRscriptExperiment extends RscriptExperiment
{
  /**
   * Creates a new empty static trend distance experiment for R
   * @param status A status to give to the experiment when it is instantiated
   */
  public StaticTrendDistanceRscriptExperiment(Experiment.Status status)
  {
    super(status);
    addDescriptions();
  }
  
  /**
   * Creates a new empty static trend distance experiment for R
   */
  public StaticTrendDistanceRscriptExperiment()
  {
    super();
    addDescriptions();
  }
  
  /**
   * Adds the descriptions of the experiment's parameters
   */
  protected void addDescriptions()
  {
    describe(METRIC, "The metric used to compute the distance between the reference trend and the computed trend");
    describe(TREND, "The trend computed on the event stream");
    describe(WIDTH, "The width of the window over which the trend is computed");
    describe(TYPE, "The type of mining pattern used");
  }
}

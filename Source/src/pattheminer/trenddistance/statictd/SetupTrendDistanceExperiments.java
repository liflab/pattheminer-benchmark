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
package pattheminer.trenddistance.statictd;

import pattheminer.MainLab;
import pattheminer.trenddistance.SetupTrendExperiments;

/**
 * Setup of experiments for the static trend distance.
 */
public class SetupTrendDistanceExperiments extends SetupTrendExperiments<StaticTrendDistanceExperiment>
{
  public SetupTrendDistanceExperiments(MainLab lab)
  {
    super(lab, new StaticTrendDistanceFactory(lab), "Static trend distance throughput", "Measures the throughput of the static trend distance pattern using various window widths and trend processors.", "static trend distance", "std");
  }
}

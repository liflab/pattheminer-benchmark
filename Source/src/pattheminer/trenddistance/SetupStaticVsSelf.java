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
package pattheminer.trenddistance;

import ca.uqac.lif.labpal.Namer;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import pattheminer.MainLab;
import pattheminer.SetupAgent;
import pattheminer.trenddistance.selftd.SelfCorrelatedExperiment;
import pattheminer.trenddistance.selftd.SelfCorrelatedFactory;
import pattheminer.trenddistance.statictd.StaticTrendDistanceExperiment;
import pattheminer.trenddistance.statictd.StaticTrendDistanceFactory;

import static pattheminer.StreamExperiment.THROUGHPUT;
import static pattheminer.trenddistance.TrendExperiment.TREND;
import static pattheminer.trenddistance.TrendExperiment.TYPE;
import static pattheminer.trenddistance.TrendExperiment.CLOSEST_CLUSTER;
import static pattheminer.trenddistance.TrendExperiment.N_GRAMS;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_AVG;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_MOMENTS;
import static pattheminer.trenddistance.TrendExperiment.SYMBOL_DISTRIBUTION;
import static pattheminer.trenddistance.TrendExperiment.WIDTH;

/**
 * Sets up tables that compare static vs self-correlated trend distance
 */
public class SetupStaticVsSelf extends SetupAgent
{
  public SetupStaticVsSelf(MainLab lab)
  {
    super(lab);
  }
  
  @Override
  public void fillWithExperiments()
  {
    StaticTrendDistanceFactory std_factory = new StaticTrendDistanceFactory(m_lab, m_lab.useFiles(), m_lab.getDataFolder());
    SelfCorrelatedFactory sc_factory = new SelfCorrelatedFactory(m_lab);
    Region reg = new Region();
    reg.add(WIDTH, MainLab.s_widths);
    reg.add(TREND, RUNNING_AVG, RUNNING_MOMENTS, CLOSEST_CLUSTER, SYMBOL_DISTRIBUTION, N_GRAMS);
    for (Region r_w : reg.all(WIDTH))
    {
      ExperimentTable et = new ExperimentTable(TREND, TYPE, THROUGHPUT);
      MainLab.s_nicknamer.setNickname(et, r_w, "t", "slowdown");
      MainLab.s_titleNamer.setTitle(et, r_w, "Throughput comparison, static vs. self-correlated trend distance: ", "");
      et.setShowInList(false);
      m_lab.add(et);
      for (Region r_t : r_w.all(TREND))
      {
        StaticTrendDistanceExperiment stde = std_factory.get(r_t);
        if (stde != null)
        {
          et.add(stde);
        }
        SelfCorrelatedExperiment sce = sc_factory.get(r_t);
        if (sce != null)
        {
          et.add(sce);
        }
      }
      TransformedTable tt = new TransformedTable(new ExpandAsColumns(TYPE, THROUGHPUT), et);
      MainLab.s_nicknamer.setNickname(tt, r_w, "t", "slowdownT");
      MainLab.s_titleNamer.setTitle(tt, r_w, "Throughput comparison, static vs. self-correlated trend distance: ", "");
      m_lab.add(tt);
      MaxSlowdownMacro macro = new MaxSlowdownMacro(m_lab, "slowdown" + Namer.latexify(Integer.toString(r_w.getInt(WIDTH))), r_w.getInt(WIDTH), tt);
      m_lab.add(macro);
    }
  }
}

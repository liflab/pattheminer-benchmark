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

import static pattheminer.TraceExperiment.SOFTWARE;
import static pattheminer.TraceExperiment.THROUGHPUT;
import static pattheminer.trenddistance.TrendExperiment.AVG_SLICE_LENGTH;
import static pattheminer.trenddistance.TrendExperiment.CLOSEST_CLUSTER;
import static pattheminer.trenddistance.TrendExperiment.N_GRAMS;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_AVG;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_MOMENTS;
import static pattheminer.trenddistance.TrendExperiment.SYMBOL_DISTRIBUTION;
import static pattheminer.trenddistance.TrendExperiment.TREND;
import static pattheminer.trenddistance.TrendExperiment.WIDTH;

import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import pattheminer.MainLab;
import pattheminer.SetupAgent;
import pattheminer.StreamExperiment;
import pattheminer.trenddistance.statictd.StaticTrendDistanceStreamExperiment;
import pattheminer.trenddistance.statictd.StaticTrendDistanceStreamFactory;

/**
 * Sets up tables that compare BeepBeep vs R
 */
public class SetupBeepBeepVsRscript extends SetupAgent
{
  /**
   * Initializes the setup agent
   * @param lab The lab to which the tables are added
   */
  public SetupBeepBeepVsRscript(MainLab lab)
  {
    super(lab);
  }

  @Override
  public void fillWithExperiments()
  {
    Group g = new Group("R experiments");
    m_lab.add(g);
    StaticTrendDistanceStreamFactory beep_factory = new StaticTrendDistanceStreamFactory(m_lab, m_lab.useFiles(), m_lab.getDataFolder());
    StaticTrendDistanceRscriptFactory r_factory = new StaticTrendDistanceRscriptFactory(m_lab, m_lab.useFiles(), m_lab.getDataFolder());
    Region reg = new Region();
    reg.add(WIDTH, MainLab.s_widths);
    reg.add(TREND, RUNNING_AVG, RUNNING_MOMENTS, CLOSEST_CLUSTER, SYMBOL_DISTRIBUTION, N_GRAMS, AVG_SLICE_LENGTH);
    reg.add(SOFTWARE, StreamExperiment.SOFTWARE_NAME, RscriptExperiment.SOFTWARE_NAME);
    for (Region r_w : reg.all(WIDTH))
    {
      ExperimentTable et = new ExperimentTable(TREND, SOFTWARE, THROUGHPUT);
      MainLab.s_nicknamer.setNickname(et, r_w, "t", "bbvsr");
      MainLab.s_titleNamer.setTitle(et, r_w, "Throughput comparison, BeepBeep vs. R: ", "");
      et.setShowInList(false);
      m_lab.add(et);
      for (Region r_t : r_w.all(TREND))
      {
        StaticTrendDistanceStreamExperiment beep_exp = beep_factory.get(r_t);
        if (beep_exp != null)
        {
          et.add(beep_exp);
        }
        StaticTrendDistanceRscriptExperiment r_exp = r_factory.get(r_t);
        if (r_exp != null)
        {
          et.add(r_exp);
          g.add(r_exp);
        }
      }
      TransformedTable tt = new TransformedTable(new ExpandAsColumns(SOFTWARE, THROUGHPUT), et);
      MainLab.s_nicknamer.setNickname(tt, r_w, "t", "bbvsrT");
      MainLab.s_titleNamer.setTitle(tt, r_w, "Throughput comparison, BeepBeep vs. R: ", "");
      m_lab.add(tt);
    }
  }
}

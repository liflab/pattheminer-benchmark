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
package pattheminer.trenddistance.threads;

import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.Join;
import ca.uqac.lif.mtnp.table.RenameColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import pattheminer.MainLab;
import pattheminer.SetupAgent;
import pattheminer.trenddistance.TrendExperiment;
import pattheminer.trenddistance.statictd.StaticTrendDistanceStreamExperiment;
import pattheminer.trenddistance.statictd.StaticTrendDistanceStreamFactory;

import static pattheminer.trenddistance.TrendExperiment.TREND;
import static pattheminer.trenddistance.TrendExperiment.WIDTH;
import static pattheminer.trenddistance.TrendExperiment.MULTITHREAD;

public class SetupThreadExperiments extends SetupAgent
{
  public SetupThreadExperiments(MainLab lab)
  {
    super(lab);
  }

  @Override
  public void fillWithExperiments()
  {
    StaticTrendDistanceStreamFactory factory = new StaticTrendDistanceStreamFactory(m_lab, m_lab.useFiles(), m_lab.getDataFolder());
    Group g_threading = new Group("Impact of multi-threading (trend distance)");
    m_lab.add(g_threading);
    {
      int for_width = MainLab.s_widths[2].intValue();
      String for_trend = TrendExperiment.RUNNING_MOMENTS;
      StaticTrendDistanceStreamExperiment exp_nt = factory.get(new Region().add(TREND, for_trend).add(WIDTH, for_width).add(MULTITHREAD, "no"));
      StaticTrendDistanceStreamExperiment exp_mt = factory.get(new Region().add(TREND, for_trend).add(WIDTH, for_width).add(MULTITHREAD, "yes"));
      ExperimentTable et_nt = new ExperimentTable(TrendExperiment.LENGTH, TrendExperiment.TIME).add(exp_nt);
      ExperimentTable et_mt = new ExperimentTable(TrendExperiment.LENGTH, TrendExperiment.TIME).add(exp_mt);
      TransformedTable t_impact_mt = new TransformedTable(new Join(TrendExperiment.LENGTH),
          new TransformedTable(new RenameColumns(TrendExperiment.LENGTH, "No threads"), et_nt),
          new TransformedTable(new RenameColumns(TrendExperiment.LENGTH, "With threads"), et_mt));
      t_impact_mt.setTitle("Impact of multi-threading");
      t_impact_mt.setNickname("tImpactMt");
      m_lab.add(t_impact_mt);
      Scatterplot plot = new Scatterplot(t_impact_mt);
      plot.setTitle("Impact of multi-threading");
      plot.setCaption(Axis.X, "Number of events").setCaption(Axis.Y, "Processing time (ms)");
      plot.setNickname("pImpactMt");
      m_lab.add(plot);
      g_threading.add(exp_nt, exp_mt);
      ThreadSpeedupMacro macro = new ThreadSpeedupMacro(m_lab, exp_nt, exp_mt);
      m_lab.add(macro);
      ThreadSpeedupClaim claim = new ThreadSpeedupClaim(m_lab, macro);
      m_lab.add(claim);
    }
  }
}

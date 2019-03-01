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
package pattheminer.extraction;

import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Namer;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.Join;
import ca.uqac.lif.mtnp.table.RenameColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import pattheminer.MainLab;
import pattheminer.SetupAgent;

public class SetupTrendExtractionExperiments extends SetupAgent
{
  public SetupTrendExtractionExperiments(MainLab lab)
  {
    super(lab);
  }

  @Override
  public void fillWithExperiments()
  {
    // Trend extraction experiments
    Group g_te = new Group("Trend extraction");
    m_lab.add(g_te);
    {
      Region reg = new Region().add(MiningExperiment.NUM_LOGS, 100, 250, 500);
      reg.add(MiningExperiment.LOG_LENGTH, 10000, 20000, 50000, 100000);

      ExperimentTable t_10000 = generateKMeansExperiment(reg, 10000, g_te);
      ExperimentTable t_20000 = generateKMeansExperiment(reg, 20000, g_te);
      ExperimentTable t_50000 = generateKMeansExperiment(reg, 50000, g_te);
      ExperimentTable t_100000 = generateKMeansExperiment(reg, 100000, g_te);
      TransformedTable tt = new TransformedTable(new Join(MiningExperiment.NUM_LOGS),
          new TransformedTable(new RenameColumns(MiningExperiment.NUM_LOGS, "10000"), t_10000),
          new TransformedTable(new RenameColumns(MiningExperiment.NUM_LOGS, "20000"), t_20000),
          new TransformedTable(new RenameColumns(MiningExperiment.NUM_LOGS, "50000"), t_50000),
          new TransformedTable(new RenameColumns(MiningExperiment.NUM_LOGS, "100000"), t_100000));
      tt.setTitle("Trend extraction speed for symbol distribution and K-means");
      tt.setNickname("tKMeansLength");
      m_lab.add(tt);
      Scatterplot k_plot = new Scatterplot(tt);
      k_plot.setNickname("pKMeansLength");
      k_plot.setTitle("Trend extraction speed for symbol distribution and K-means");
      m_lab.add(k_plot);
      MinLogsPerSecondMacro mlpsm = new MinLogsPerSecondMacro(m_lab, "minLogsKMeans", 500, tt);
      m_lab.add(mlpsm);
    }
  }

  protected ExperimentTable generateKMeansExperiment(Region r_nl, int log_length, Group g)
  {
    ExperimentTable t_by_num_logs = new ExperimentTable(MiningExperiment.NUM_LOGS, MiningExperiment.DURATION);
    t_by_num_logs.setTitle("Trend extraction speed for symbol distribution and K-means (log length " + r_nl.getInt(MiningExperiment.LOG_LENGTH) + ")");
    t_by_num_logs.setNickname("teSpeed" + Namer.latexify(Integer.toString(log_length)));
    for (Region r_ll : r_nl.all(MiningExperiment.NUM_LOGS))
    {
      DistributionKmeansExperiment dke = new DistributionKmeansExperiment(m_lab.getRandom(), r_ll.getInt(MiningExperiment.NUM_LOGS), log_length);
      m_lab.add(dke);
      g.add(dke);
      t_by_num_logs.add(dke);
    }
    t_by_num_logs.setShowInList(false);
    m_lab.add(t_by_num_logs);
    return t_by_num_logs;
  }
}

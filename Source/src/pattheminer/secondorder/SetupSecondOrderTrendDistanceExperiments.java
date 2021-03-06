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
package pattheminer.secondorder;

import ca.uqac.lif.cep.peg.TrendDistance;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import pattheminer.MainLab;
import pattheminer.SetupAgent;
import pattheminer.StreamExperiment;
import pattheminer.patterns.CumulativeAverage;
import pattheminer.source.RandomNumberSource;

import static pattheminer.secondorder.SecondOrderExperiment.NUM_TRENDS;

/**
 * Setup of experiments for the second-order trend distance.
 */
public class SetupSecondOrderTrendDistanceExperiments extends SetupAgent
{
  /**
   * The number of events to generate in each trace
   */
  protected static int s_traceLength = 100000;

  /**
   * Creates a new setup agent
   * @param lab The lab in which the experiments will be added
   */
  public SetupSecondOrderTrendDistanceExperiments(MainLab lab)
  {
    super(lab);
  }

  /**
   * Populates the lab with experiments
   */
  @Override
  public void fillWithExperiments()
  {
    SetupFactory factory = new SetupFactory(m_lab);
    
    Group g = new Group("Second-order experiments");
    m_lab.add(g);
    Region reg = new Region().add(NUM_TRENDS, 1, 3, 5, 7, 9, 11);

    // Throughput for each value of k
    {
      ExperimentTable exp_t = new ExperimentTable(StreamExperiment.LENGTH, NUM_TRENDS, StreamExperiment.TIME);
      MainLab.s_nicknamer.setNickname(exp_t, reg, "t", "2ndOrder");
      MainLab.s_titleNamer.setTitle(exp_t, reg, "Throughput for second-order trend distance ", "");
      for (Region in_r : reg.all(NUM_TRENDS))
      {
        IdenticalSecondOrderExperiment isoe = factory.get(in_r);
        g.add(isoe);
        exp_t.add(isoe);
      }
      m_lab.add(exp_t);
      TransformedTable tt = new TransformedTable(new ExpandAsColumns(NUM_TRENDS, StreamExperiment.TIME), exp_t);
      MainLab.s_nicknamer.setNickname(tt, reg, "tt", "2ndOrder");
      MainLab.s_titleNamer.setTitle(tt, reg, "Throughput for second-order trend distance ", " (grouped by " + NUM_TRENDS + ")");
      m_lab.add(tt);
      Scatterplot plot = new Scatterplot(tt);
      plot.setCaption(Axis.X, "Stream length").setCaption(Axis.Y, "Time");
      MainLab.s_nicknamer.setNickname(plot, reg, "p", "2ndOrder");
      MainLab.s_titleNamer.setTitle(plot, reg, "Throughput for second-order trend distance ", " (grouped by " + NUM_TRENDS + ")");
      m_lab.add(plot);
    }

    // Impact of k on throughput
    {
      ExperimentTable exp_t = new ExperimentTable(NUM_TRENDS, StreamExperiment.THROUGHPUT);
      MainLab.s_nicknamer.setNickname(exp_t, reg, "t", "kImpact2ndOrder");
      MainLab.s_titleNamer.setTitle(exp_t, reg, "Impact of " + NUM_TRENDS + " on second-order trend distance", "");
      for (Region in_r : reg.all(NUM_TRENDS))
      {
        IdenticalSecondOrderExperiment isoe = factory.get(in_r);
        exp_t.add(isoe);
      }
      m_lab.add(exp_t);
      Scatterplot plot = new Scatterplot(exp_t);
      plot.setCaption(Axis.X, "Stream length").setCaption(Axis.Y, "Time");
      MainLab.s_nicknamer.setNickname(plot, reg, "p", "kImpact2ndOrder");
      plot.setTitle(exp_t.getTitle());
      m_lab.add(plot);
    }
  }

  /**
   * Factory that creates experiments in this category
   */
  protected static class SetupFactory extends ExperimentFactory<MainLab,IdenticalSecondOrderExperiment>
  {
    SetupFactory(MainLab lab)
    {
      super(lab, IdenticalSecondOrderExperiment.class);
    }

    @Override
    protected IdenticalSecondOrderExperiment createExperiment(Region r)
    {
      int num_trends = r.getInt(NUM_TRENDS);
      TrendDistance<Number,Number,Number> first = new TrendDistance<Number,Number,Number>(0, 100, new CumulativeAverage(), Numbers.subtraction, 1, Numbers.isGreaterThan);
      TrendDistance<Number,Number,Number> second = new TrendDistance<Number,Number,Number>(2, 10, new TrueCount(), Numbers.subtraction, 1, Numbers.isGreaterThan);
      IdenticalSecondOrderExperiment isoe = new IdenticalSecondOrderExperiment(second, first, num_trends);
      isoe.setSource(new RandomNumberSource(m_lab.getRandom(), s_traceLength));
      return isoe;
    }
  }
}

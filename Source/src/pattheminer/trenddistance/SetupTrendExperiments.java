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

import static pattheminer.StreamExperiment.LENGTH;
import static pattheminer.StreamExperiment.THROUGHPUT;
import static pattheminer.StreamExperiment.TIME;
import static pattheminer.trenddistance.TrendExperiment.CLOSEST_CLUSTER;
import static pattheminer.trenddistance.TrendExperiment.N_GRAMS;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_AVG;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_MOMENTS;
import static pattheminer.trenddistance.TrendExperiment.AVG_SLICE_LENGTH;
import static pattheminer.trenddistance.TrendExperiment.SYMBOL_DISTRIBUTION;
import static pattheminer.trenddistance.TrendExperiment.TREND;
import static pattheminer.trenddistance.TrendExperiment.WIDTH;

import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import pattheminer.MainLab;
import pattheminer.SetupAgent;

public abstract class SetupTrendExperiments<T extends TrendExperiment> extends SetupAgent
{
  protected TrendFactory<T> m_factory;
  
  protected String m_groupName;
  
  protected String m_groupDescription;
  
  protected String m_caption;
  
  protected String m_suffix;
  
  public SetupTrendExperiments(MainLab lab, TrendFactory<T> factory, String group_name, String group_description, String caption, String suffix)
  {
    super(lab);
    m_factory = factory;
    m_groupName = group_name;
    m_groupDescription = group_description;
    m_caption = caption;
    m_suffix = suffix;
  }
  
  @Override
  public void fillWithExperiments()
  {
    // Static trend distance
    {
      Group g = new Group(m_groupName);
      g.setDescription(m_groupDescription);
      m_lab.add(g);
      Region big_reg = new Region();
      big_reg.add(WIDTH, MainLab.s_widths);
      big_reg.add(TREND, RUNNING_AVG, RUNNING_MOMENTS, SYMBOL_DISTRIBUTION, CLOSEST_CLUSTER, N_GRAMS, AVG_SLICE_LENGTH);

      // Throughput for each trend and each window width
      for (Region r_w : big_reg.all(TREND, WIDTH))
      {
        ExperimentTable et = new ExperimentTable(LENGTH, TIME);
        et.setShowInList(false);
        MainLab.s_nicknamer.setNickname(et, r_w, "t", "throughput" + m_suffix);
        MainLab.s_titleNamer.setTitle(et, r_w, "Throughput by length (" + m_caption + "):", "");
        m_lab.add(et);
        TrendExperiment tde = m_factory.get(r_w);
        if (tde == null)
        {
          continue;
        }
        g.add(tde);
        et.add(tde);
        /* 
        These plots are not really necessary
        Scatterplot plot = new Scatterplot(et);
        MainLab.s_nicknamer.setNickname(plot, r_w, "p", "throughput");
        plot.setTitle(et.getTitle());
        m_lab.add(plot);
        */
      }

      // Impact of window width for each trend
      for (Region r_t : big_reg.all(TREND))
      {
        ExperimentTable et = new ExperimentTable(LENGTH, WIDTH, TIME);
        m_lab.add(et);
        et.setShowInList(false);
        MainLab.s_nicknamer.setNickname(et, r_t, "t", "throughputWidth" + m_suffix);
        MainLab.s_titleNamer.setTitle(et, r_t, "Throughput by length and width (" + m_caption + "): ", "");
        for (Region r_w : r_t.all(WIDTH))
        {
          TrendExperiment tde = m_factory.get(r_w);
          if (tde == null)
          {
            continue;
          }
          et.add(tde);
        }
        TransformedTable tt = new TransformedTable(new ExpandAsColumns(WIDTH, TIME), et);
        MainLab.s_nicknamer.setNickname(tt, r_t, "t", "throughputWidthE" + m_suffix);
        tt.setTitle(et.getTitle());
        m_lab.add(tt);
        Scatterplot plot = new Scatterplot(tt);
        plot.setCaption(Axis.X, "Stream length").setCaption(Axis.Y, "Time (ms)");
        MainLab.s_nicknamer.setNickname(plot, r_t, "p", "throughputWidthE");
        MainLab.s_titleNamer.setTitle(plot, r_t, "Throughput by length and width: ", "");
        m_lab.add(plot);
      }

      // Global throughput by window width for each trend
      {
        ExperimentTable et = new ExperimentTable(WIDTH, TREND, THROUGHPUT);
        et.setShowInList(false);
        for (Region r : big_reg.all(TREND, WIDTH))
        {
          TrendExperiment tde = m_factory.get(r);
          if (tde == null)
          {
            continue;
          }
          et.add(tde);
        }
        TransformedTable tt = new TransformedTable(new ExpandAsColumns(TREND, THROUGHPUT), et);
        tt.setNickname("ttImpactWidthTrendDistance" + m_suffix);
        tt.setTitle("Impact of window width on throughput (" + m_caption + ")");
        m_lab.add(tt);
        Scatterplot plot = new Scatterplot(tt);
        plot.setNickname("pImpactWidthTrendDistance" + m_suffix);
        plot.setTitle(tt.getTitle());
        plot.setCaption(Axis.X, "Window width").setCaption(Axis.Y, "Throughput (Hz)");
        m_lab.add(plot);
      }
    }
    //m_lab.add(new AverageThroughputMacro(m_lab, table_50, "tp" + nickname_prefix + "Fifty", beta_name + " with a window of " + MainLab.s_width1));
    //m_lab.add(new AverageThroughputMacro(m_lab, table_200, "tp" + nickname_prefix + "TwoHundred", beta_name + " with a window of " + MainLab.s_width3));
    //m_lab.add(new MonotonicWindowClaim(tt, beta_name, Integer.toString(MainLab.s_width1), Integer.toString(MainLab.s_width2), Integer.toString(MainLab.s_width3)));
    /*
    // Fixed pattern vs. self correlated
    {
      Region r = new Region();
      r.add(TrendExperiment.WIDTH, MainLab.s_width1, MainLab.s_width2, MainLab.s_width3);
      String[] trends = new String[]{"Average", "Running moments", "Symbol distribution", "Closest cluster"};
      r.add(TrendExperiment.TREND, trends);
      for (Region sub_r : r.all(TrendExperiment.WIDTH))
      {
        int w = sub_r.getInt(TrendExperiment.WIDTH);
        ThroughputComparisonTable tab = new ThroughputComparisonTable(trends);
        tab.setTitle("Throughput comparison (window width = " + MainLab.toLatex(w) + ")");
        tab.setNickname("tThroughputComparison" + w);
        for (Region sub_r2 : sub_r.all(TrendExperiment.TREND))
        {
          Region r_t = new Region(sub_r2).add(TrendExperiment.TYPE, TrendDistanceExperiment.TYPE_NAME);
          TrendDistanceExperiment exp_t = (TrendDistanceExperiment) m_lab.getAnyExperiment(r_t);
          Region r_s = new Region(sub_r2).add(TrendExperiment.TYPE, SelfCorrelatedExperiment.TYPE_NAME);
          SelfCorrelatedExperiment exp_s = (SelfCorrelatedExperiment) m_lab.getAnyExperiment(r_s);
          tab.add(sub_r2.getString(TrendExperiment.TREND), exp_t);
          tab.add(sub_r2.getString(TrendExperiment.TREND), exp_s);
        }
        m_lab.add(tab);
        m_lab.add(new MaxSlowdownMacro(m_lab, "maxSlowdown" + MainLab.toLatex(w), w, tab));
      }
    }
     */
  }
}

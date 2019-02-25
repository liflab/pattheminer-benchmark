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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.peg.MapDistance;
import ca.uqac.lif.cep.peg.PointDistance;
import ca.uqac.lif.cep.peg.SelfCorrelatedTrendDistance;
import ca.uqac.lif.cep.peg.ml.RunningMoments;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.json.JsonString;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Random;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.Join;
import ca.uqac.lif.mtnp.table.RenameColumns;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TransformedTable;
import java.util.HashMap;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import pattheminer.AverageThroughputMacro;
import pattheminer.MainLab;
import pattheminer.MonotonicWindowClaim;
import pattheminer.RandomNumberSource;
import pattheminer.RandomSymbolSource;
import pattheminer.SelfCorrelatedExperiment;
import pattheminer.SetupAgent;
import pattheminer.StreamExperiment;
import pattheminer.patterns.CumulativeAverage;
import pattheminer.patterns.SymbolDistribution;
import pattheminer.patterns.SymbolDistributionClusters;

import static pattheminer.StreamExperiment.LENGTH;
import static pattheminer.StreamExperiment.THROUGHPUT;
import static pattheminer.StreamExperiment.TIME;

import static pattheminer.trenddistance.TrendDistanceExperiment.CLOSEST_CLUSTER;
import static pattheminer.trenddistance.TrendDistanceExperiment.N_GRAMS;
import static pattheminer.trenddistance.TrendDistanceExperiment.RUNNING_AVG;
import static pattheminer.trenddistance.TrendDistanceExperiment.RUNNING_MOMENTS;
import static pattheminer.trenddistance.TrendDistanceExperiment.SYMBOL_DISTRIBUTION;
import static pattheminer.trenddistance.TrendDistanceExperiment.TREND;
import static pattheminer.trenddistance.TrendDistanceExperiment.WIDTH;

/**
 * Setup of experiments for the trend distance.
 */
public class SetupTrendDistanceExperiments extends SetupAgent
{
  public SetupTrendDistanceExperiments(MainLab lab)
  {
    super(lab);
  }

  @Override
  public void fillWithExperiments()
  {
    TrendDistanceFactory td_factory = new TrendDistanceFactory(m_lab);

    // Static trend distance
    {
      Group g = new Group("Trend distance throughput");
      g.setDescription("Measures the throughput of the trend distance processor for various trend computations.");
      m_lab.add(g);
      Region big_reg = new Region();
      big_reg.add(WIDTH, MainLab.s_width1, MainLab.s_width2, MainLab.s_width3);
      big_reg.add(TREND, RUNNING_AVG, RUNNING_MOMENTS, SYMBOL_DISTRIBUTION, CLOSEST_CLUSTER, N_GRAMS);

      // Throughput for each trend and each window width
      for (Region r_w : big_reg.all(TREND, WIDTH))
      {
        ExperimentTable et = new ExperimentTable(LENGTH, TIME);
        et.setShowInList(false);
        MainLab.s_nicknamer.setNickname(et, r_w, "t", "throughput");
        MainLab.s_titleNamer.setTitle(et, r_w, "Throughput by length:", "");
        m_lab.add(et);
        TrendDistanceExperiment tde = td_factory.get(r_w);
        g.add(tde);
        et.add(tde);
        /* These plots are not really necessary
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
        MainLab.s_nicknamer.setNickname(et, r_t, "t", "throughputWidth");
        MainLab.s_titleNamer.setTitle(et, r_t, "Throughput by length and width: ", "");
        for (Region r_w : r_t.all(WIDTH))
        {
          TrendDistanceExperiment tde = td_factory.get(r_w);
          et.add(tde);
        }
        TransformedTable tt = new TransformedTable(new ExpandAsColumns(WIDTH, TIME), et);
        MainLab.s_nicknamer.setNickname(tt, r_t, "t", "throughputWidthE");
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
          TrendDistanceExperiment tde = td_factory.get(r);
          et.add(tde);
        }
        TransformedTable tt = new TransformedTable(new ExpandAsColumns(TREND, THROUGHPUT), et);
        tt.setNickname("ttImpactWidthTrendDistance");
        tt.setTitle("Impact of window width on throughput, static trend distance");
        m_lab.add(tt);
        Scatterplot plot = new Scatterplot(tt);
        plot.setNickname("pImpactWidthTrendDistance");
        plot.setTitle(tt.getTitle());
        plot.setCaption(Axis.X, "Window width").setCaption(Axis.Y, "Throughput (Hz)");
        m_lab.add(plot);
      }
    }
    /*
    // Self-correlated trend distance experiments
    {
      Group g = new Group("Self-correlated trend distance throughput");
      g.setDescription("Measures the throughput of the self-correlated trend distance processor for various trend computations.");
      m_lab.add(g);
      // Average experiments
      ExperimentTable et_avg = generateWindowExperiments(generateSelfAverageExperiment(MainLab.s_width1, false), generateSelfAverageExperiment(MainLab.s_width2, false), generateSelfAverageExperiment(MainLab.s_width3, false), "running average", "AverageSc", g);
      // Running moments experiments
      ExperimentTable et_moments = generateWindowExperiments(generateSelfRunningMomentsExperiment(MainLab.s_width1, false), generateSelfRunningMomentsExperiment(MainLab.s_width2, false), generateSelfRunningMomentsExperiment(MainLab.s_width3, false), "running moments", "MomentsSc", g);
      // Distribution experiments
      ExperimentTable et_distribution = generateWindowExperiments(generateSelfDistributionExperiment(MainLab.s_width1, false), generateSelfDistributionExperiment(MainLab.s_width2, false), generateSelfDistributionExperiment(MainLab.s_width3, false), "symbol distribution", "DistributionSc", g);
      // K-means experiments
      ExperimentTable et_clustering = generateWindowExperiments(generateSelfClusterDistributionExperiment(MainLab.s_width1, false), generateSelfClusterDistributionExperiment(MainLab.s_width2, false), generateSelfClusterDistributionExperiment(MainLab.s_width3, false), "closest cluster", "ClusteringSc", g);
      {
        // Table and plot for impact of window width
        TransformedTable t_impact_window = new TransformedTable(new Join(TrendDistanceExperiment.WIDTH),
            new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Running average"), et_avg),
            new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Running moments"), et_moments),
            new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Symbol distribution"), et_distribution),
            new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Closest cluster"), et_clustering)
            );
        t_impact_window.setTitle("Impact of window width (self-correlated trend distance)");
        t_impact_window.setNickname("tImpactWidthSc");
        m_lab.add(t_impact_window);
        Scatterplot plot = new Scatterplot(t_impact_window);
        plot.setTitle("Impact of window width");
        plot.setCaption(Axis.X, "Window width").setCaption(Axis.Y, "Throughput (Hz)");
        plot.setNickname("pImpactWidthSc");
        m_lab.add(plot);
      }
    }

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

  protected ExperimentTable generateWindowExperiments(StreamExperiment exp_50, StreamExperiment exp_100, StreamExperiment exp_200, String beta_name, String nickname_prefix, Group g)
  {
    ExperimentTable table_50 = m_lab.createTable(exp_50, beta_name, MainLab.s_width1);
    ExperimentTable table_100 = m_lab.createTable(exp_100, beta_name, MainLab.s_width2);
    ExperimentTable table_200 = m_lab.createTable(exp_200, beta_name, MainLab.s_width3);
    g.add(exp_50, exp_100, exp_200);
    {
      Table tt = new TransformedTable(new Join(StreamExperiment.LENGTH),
          new TransformedTable(new RenameColumns(StreamExperiment.LENGTH, Integer.toString(MainLab.s_width1)), table_50),
          new TransformedTable(new RenameColumns(StreamExperiment.LENGTH, Integer.toString(MainLab.s_width2)), table_100),
          new TransformedTable(new RenameColumns(StreamExperiment.LENGTH, Integer.toString(MainLab.s_width3)), table_200)
          );
      tt.setTitle("Running time for the " + beta_name);
      m_lab.add(tt);
      Scatterplot plot = new Scatterplot(tt);
      plot.setCaption(Axis.X, "Number of events").setCaption(Axis.Y, "Time (ms)");
      plot.setTitle("Running time for the " + beta_name);
      plot.setNickname("p" + nickname_prefix);
      m_lab.add(plot);
      m_lab.add(new AverageThroughputMacro(m_lab, table_50, "tp" + nickname_prefix + "Fifty", beta_name + " with a window of " + MainLab.s_width1));
      m_lab.add(new AverageThroughputMacro(m_lab, table_200, "tp" + nickname_prefix + "TwoHundred", beta_name + " with a window of " + MainLab.s_width3));
      m_lab.add(new MonotonicWindowClaim(tt, beta_name, Integer.toString(MainLab.s_width1), Integer.toString(MainLab.s_width2), Integer.toString(MainLab.s_width3)));
    }
    ExperimentTable et = new ExperimentTable(TrendDistanceExperiment.WIDTH, TrendDistanceExperiment.THROUGHPUT);
    et.add(exp_50).add(exp_100).add(exp_200);
    et.setTitle("Impact of window width for the " + beta_name);
    m_lab.add(et);
    return et;
  }

  protected SelfCorrelatedExperiment generateSelfAverageExperiment(int width, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    CumulativeAverage beta = new CumulativeAverage();
    SelfCorrelatedTrendDistance<Number,Number,Number> alarm = new SelfCorrelatedTrendDistance<Number,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(Numbers.subtraction, StreamVariable.X, StreamVariable.Y)), 0.5, Numbers.isLessThan);
    Source src = new RandomNumberSource(random, MainLab.MAX_TRACE_LENGTH);
    return addNewSelfCorrelatedExperiment("Average", "Subtraction", src, alarm, width, multi_thread);
  }

  protected SelfCorrelatedExperiment generateSelfRunningMomentsExperiment(int width, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    RunningMoments beta = new RunningMoments(3);
    SelfCorrelatedTrendDistance<DoublePoint,Number,Number> alarm = new SelfCorrelatedTrendDistance<DoublePoint,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new PointDistance(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    Source src = new RandomNumberSource(random, MainLab.MAX_TRACE_LENGTH);
    return addNewSelfCorrelatedExperiment("Running moments", "Subtraction", src, alarm, width, multi_thread);
  }

  protected SelfCorrelatedExperiment generateSelfDistributionExperiment(int width, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    SymbolDistribution beta = new SymbolDistribution();
    SelfCorrelatedTrendDistance<HashMap<?,?>,Number,Number> alarm = new SelfCorrelatedTrendDistance<HashMap<?,?>,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(MapDistance.instance, StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH);
    return addNewSelfCorrelatedExperiment("Symbol distribution", "Map distance", src, alarm, width, multi_thread);
  }

  protected SelfCorrelatedExperiment generateSelfClusterDistributionExperiment(int width, boolean multi_thread)
  {
    int num_symbols = 2;
    Random random = m_lab.getRandom();
    SymbolDistributionClusters beta = new SymbolDistributionClusters();
    SelfCorrelatedTrendDistance<DoublePoint,Number,Number> alarm = new SelfCorrelatedTrendDistance<DoublePoint,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new PointDistance(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 0.25, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH, num_symbols);
    return addNewSelfCorrelatedExperiment("Closest cluster", "Euclidean distance to closest cluster", src, alarm, width, multi_thread);
  }


  protected SelfCorrelatedExperiment addNewSelfCorrelatedExperiment(String trend, String metric, Source src, Processor alarm, int width, boolean multi_thread)
  {
    SelfCorrelatedExperiment tde = new SelfCorrelatedExperiment();
    tde.setSource(src);
    tde.setProcessor(alarm);
    tde.setEventStep(MainLab.s_eventStep);
    tde.setInput(TrendDistanceExperiment.WIDTH, width);
    tde.setInput(TrendDistanceExperiment.TREND, trend);
    tde.setInput(TrendDistanceExperiment.METRIC, metric);
    JsonString jb = new JsonString("yes");
    if (!multi_thread)
    {
      jb = new JsonString("no");
    }
    tde.setInput(TrendDistanceExperiment.MULTITHREAD, jb);
    m_lab.add(tde);
    return tde;
  }


}

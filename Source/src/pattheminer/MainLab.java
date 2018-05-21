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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.concurrency.NonBlockingPush;
import ca.uqac.lif.cep.concurrency.ParallelWindow;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.peg.MapDistance;
import ca.uqac.lif.cep.peg.PointDistance;
import ca.uqac.lif.cep.peg.TrendDistance;
import ca.uqac.lif.cep.peg.ml.DistanceToClosest;
import ca.uqac.lif.cep.peg.ml.RunningMoments;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.json.JsonString;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.Random;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.Join;
import ca.uqac.lif.mtnp.table.RenameColumns;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TransformedTable;
import pattheminer.patterns.CumulativeAverage;
import pattheminer.patterns.SymbolDistribution;
import pattheminer.patterns.SymbolDistributionClusters;

public class MainLab extends Laboratory
{
  protected static int s_eventStep = 10000;

  public static int MAX_TRACE_LENGTH = 100001;

  protected static transient ExecutorService s_service = Executors.newCachedThreadPool();
  
  protected transient Group m_groupTrendDistance;
  
  protected static transient int s_width1 = 50;
  protected static transient int s_width2 = 100;
  protected static transient int s_width3 = 800;

  @Override
  public void setup()
  {	  
    // Basic metadata
    setTitle("Benchmark for Pat The Miner");
    setAuthor("Laboratoire d'informatique formelle");
    
    // Trend distance experiments
    m_groupTrendDistance = new Group("Trend distance throughput");
    m_groupTrendDistance.setDescription("Measures the throughput of the trend distance processor for various trend computations.");
    add(m_groupTrendDistance);
    // Average experiments
    ExperimentTable et_avg = generateWindowExperiments(generateAverageExperiment(s_width1, false), generateAverageExperiment(s_width2, false), generateAverageExperiment(s_width3, false), "running average", "Average");
    // Running moments experiments
    ExperimentTable et_moments = generateWindowExperiments(generateRunningMomentsExperiment(s_width1, false), generateRunningMomentsExperiment(s_width2, false), generateRunningMomentsExperiment(s_width3, false), "running moments", "Moments");
    // Distribution experiments
    ExperimentTable et_distribution = generateWindowExperiments(generateDistributionExperiment(s_width1, false), generateRunningMomentsExperiment(s_width2, false), generateDistributionExperiment(s_width3, false), "symbol distribution", "Distribution");
    // K-means experiments
    ExperimentTable et_clustering = generateWindowExperiments(generateClusterDistributionExperiment(s_width1, false), generateRunningMomentsExperiment(s_width2, false), generateClusterDistributionExperiment(s_width3, false), "closest cluster", "Clustering");
    
    {
      // Table and plot for impact of window width
      TransformedTable t_impact_window = new TransformedTable(new Join(TrendDistanceExperiment.WIDTH),
          new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Running average"), et_avg),
          new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Running moments"), et_moments),
          new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Symbol distribution"), et_distribution),
          new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Closest cluster"), et_clustering)
          );
      t_impact_window.setTitle("Impact of window width");
      t_impact_window.setNickname("tImpactWidth");
      add(t_impact_window);
      Scatterplot plot = new Scatterplot(t_impact_window);
      plot.setTitle("Impact of window width");
      plot.setCaption(Axis.X, "Window width").setCaption(Axis.Y, "Throughput (Hz)");
      plot.setNickname("pImpactWidth");
      add(plot);
    }
    
    // Impact of threading
    Group g_threading = new Group("Impact of multi-threading");
    add(g_threading);
    {
      int for_width = s_width3;
      String for_trend = "Running moments";
      TrendDistanceExperiment exp_nt = (TrendDistanceExperiment) getAnyExperiment(
          new Region().add(TrendDistanceExperiment.TREND, for_trend).add(TrendDistanceExperiment.WIDTH, for_width));
      TrendDistanceExperiment exp_mt = generateRunningMomentsExperiment(for_width, true);
      ExperimentTable et_nt = new ExperimentTable(TrendDistanceExperiment.LENGTH, TrendDistanceExperiment.TIME).add(exp_nt);
      ExperimentTable et_mt = new ExperimentTable(TrendDistanceExperiment.LENGTH, TrendDistanceExperiment.TIME).add(exp_mt);
      TransformedTable t_impact_mt = new TransformedTable(new Join(TrendDistanceExperiment.LENGTH),
          new TransformedTable(new RenameColumns(TrendDistanceExperiment.LENGTH, "No threads"), et_nt),
          new TransformedTable(new RenameColumns(TrendDistanceExperiment.LENGTH, "With threads"), et_mt));
      t_impact_mt.setTitle("Impact of multi-threading");
      t_impact_mt.setNickname("tImpactMt");
      add(t_impact_mt);
      Scatterplot plot = new Scatterplot(t_impact_mt);
      plot.setTitle("Impact of multi-threading");
      plot.setCaption(Axis.X, "Number of events").setCaption(Axis.Y, "Processing time (ms)");
      plot.setNickname("pImpactMt");
      add(plot);
      g_threading.add(exp_nt, exp_mt);
      ThreadSpeedupMacro macro = new ThreadSpeedupMacro(this, exp_nt, exp_mt);
      add(macro);
      ThreadSpeedupClaim claim = new ThreadSpeedupClaim(this, macro);
      add(claim);
    }
  }

  protected ExperimentTable generateWindowExperiments(TrendDistanceExperiment exp_50, TrendDistanceExperiment exp_100, TrendDistanceExperiment exp_200, String beta_name, String nickname_prefix)
  {
    ExperimentTable table_50 = createTable(exp_50, beta_name, s_width1);
    ExperimentTable table_100 = createTable(exp_100, beta_name, s_width2);
    ExperimentTable table_200 = createTable(exp_200, beta_name, s_width3);
    m_groupTrendDistance.add(exp_50, exp_100, exp_200);
    {
      Table tt = new TransformedTable(new Join(TrendDistanceExperiment.LENGTH),
          new TransformedTable(new RenameColumns(TrendDistanceExperiment.LENGTH, Integer.toString(s_width1)), table_50),
          new TransformedTable(new RenameColumns(TrendDistanceExperiment.LENGTH, Integer.toString(s_width2)), table_100),
          new TransformedTable(new RenameColumns(TrendDistanceExperiment.LENGTH, Integer.toString(s_width3)), table_200)
          );
      tt.setTitle("Running time for the " + beta_name);
      add(tt);
      Scatterplot plot = new Scatterplot(tt);
      plot.setCaption(Axis.X, "Number of events").setCaption(Axis.Y, "Time (ms)");
      plot.setTitle("Running time for the " + beta_name);
      plot.setNickname("p" + nickname_prefix);
      add(plot);
      add(new AverageThroughputMacro(this, table_50, "tp" + nickname_prefix + "Fifty", beta_name + " with a window of " + s_width1));
      add(new AverageThroughputMacro(this, table_200, "tp" + nickname_prefix + "TwoHundred", beta_name + " with a window of " + s_width3));
      add(new MonotonicWindowClaim(tt, beta_name, Integer.toString(s_width1), Integer.toString(s_width2), Integer.toString(s_width3)));
    }
    ExperimentTable et = new ExperimentTable(TrendDistanceExperiment.WIDTH, TrendDistanceExperiment.THROUGHPUT);
    et.add(exp_50).add(exp_100).add(exp_200);
    et.setTitle("Impact of window width for the " + beta_name);
    add(et);
    return et;
  }

  protected TrendDistanceExperiment generateAverageExperiment(int width, boolean multi_thread)
  {
    Random random = getRandom();
    CumulativeAverage average = new CumulativeAverage();
    Processor wp = null;
    if (multi_thread)
    {
      NonBlockingPush nbp = new NonBlockingPush(average, s_service);
      wp = new ParallelWindow(nbp, width);
    }
    else
    {
      //wp = new Window(average, width);
      wp = new ParallelWindow(average, width);
    }
    TrendDistance<Number,Number,Number> alarm = new TrendDistance<Number,Number,Number>(6, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(Numbers.subtraction, StreamVariable.X, StreamVariable.Y)), 0.5, Numbers.isLessThan);
    Source src = new RandomNumberSource(random, MAX_TRACE_LENGTH);
    return addNewExperiment("Average", "Subtraction", src, alarm, width, multi_thread);
  }

  protected TrendDistanceExperiment generateDistributionExperiment(int width, boolean multi_thread)
  {
    Random random = getRandom();
    SymbolDistribution beta = new SymbolDistribution();
    HashMap<Object,Object> pattern = MapDistance.createMap("0", width - 2, "1", 1, "2", 1);
    Processor wp = null;
    if (multi_thread)
    {
      NonBlockingPush nbp = new NonBlockingPush(beta, s_service);
      wp = new ParallelWindow(nbp, width);
    }
    else
    {
      //wp = new Window(average, width);
      wp = new ParallelWindow(beta, width);
    }
    TrendDistance<HashMap<?,?>,Number,Number> alarm = new TrendDistance<HashMap<?,?>,Number,Number>(pattern, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(MapDistance.instance, StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MAX_TRACE_LENGTH);
    return addNewExperiment("Symbol distribution", "Map distance", src, alarm, width, multi_thread);
  }

  protected TrendDistanceExperiment generateRunningMomentsExperiment(int width, boolean multi_thread)
  {
    Random random = getRandom();
    RunningMoments beta = new RunningMoments(3);
    DoublePoint pattern = new DoublePoint(new double[]{1d, 1d, 1d});
    Processor wp = null;
    if (multi_thread)
    {
      NonBlockingPush nbp = new NonBlockingPush(beta, s_service);
      wp = new ParallelWindow(nbp, width);
    }
    else
    {
      //wp = new Window(average, width);
      wp = new ParallelWindow(beta, width);
    }
    TrendDistance<DoublePoint,Number,Number> alarm = new TrendDistance<DoublePoint,Number,Number>(pattern, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new PointDistance(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    Source src = new RandomNumberSource(random, MAX_TRACE_LENGTH);
    return addNewExperiment("Running moments", "Vector distance", src, alarm, width, multi_thread);
  }

  protected TrendDistanceExperiment generateClusterDistributionExperiment(int width, boolean multi_thread)
  {
    int num_symbols = 2;
    Random random = getRandom();
    SymbolDistributionClusters beta = new SymbolDistributionClusters();
    Set<DoublePoint> pattern = new HashSet<DoublePoint>();
    pattern.add(new DoublePoint(new double[]{0.7, 0.3}));
    pattern.add(new DoublePoint(new double[]{0.3, 0.7}));
    Processor wp = null;
    if (multi_thread)
    {
      NonBlockingPush nbp = new NonBlockingPush(beta, s_service);
      wp = new ParallelWindow(nbp, width);
    }
    else
    {
      //wp = new Window(average, width);
      wp = new ParallelWindow(beta, width);
    }
    TrendDistance<Set<DoublePoint>,Set<DoublePoint>,Number> alarm = new TrendDistance<Set<DoublePoint>,Set<DoublePoint>,Number>(pattern, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new DistanceToClosest(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 0.25, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MAX_TRACE_LENGTH, num_symbols);
    return addNewExperiment("Closest cluster", "Euclidean distance to closest cluster", src, alarm, width, multi_thread);
  }

  protected TrendDistanceExperiment addNewExperiment(String trend, String metric, Source src, TrendDistance<?,?,?> alarm, int width, boolean multi_thread)
  {
    TrendDistanceExperiment tde = new TrendDistanceExperiment();
    tde.setSource(src);
    tde.setTrendDistance(alarm);
    tde.setEventStep(s_eventStep);
    tde.setInput(TrendDistanceExperiment.WIDTH, width);
    tde.setInput(TrendDistanceExperiment.TREND, trend);
    tde.setInput(TrendDistanceExperiment.METRIC, metric);
    JsonString jb = new JsonString("yes");
    if (!multi_thread)
    {
      jb = new JsonString("no");
    }
    tde.setInput(TrendDistanceExperiment.MULTITHREAD, jb);
    add(tde);
    return tde;
  }

  protected ExperimentTable createTable(TrendDistanceExperiment tde, String trend, int width)
  {
    String title = "Running time for " + trend + ", window width = " + width;
    ExperimentTable et = new ExperimentTable(TrendDistanceExperiment.LENGTH, TrendDistanceExperiment.TIME);
    et.setTitle(title);
    et.add(tde);
    add(et);
    return et;

  }
  
  protected Experiment getAnyExperiment(Region r)
  {
    Collection<Experiment> ce = filterExperiments(r);
    for (Experiment e : ce)
    {
      return e;
    }
    return null;
  }

  public static void main(String[] args)
  {
    // Nothing else to do here
    MainLab.initialize(args, MainLab.class);
  }
}
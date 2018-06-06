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
import ca.uqac.lif.cep.peg.SelfCorrelatedTrendDistance;
import ca.uqac.lif.cep.peg.TrendDistance;
import ca.uqac.lif.cep.peg.ml.DistanceToClosest;
import ca.uqac.lif.cep.peg.ml.RunningMoments;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.json.JsonString;
import ca.uqac.lif.labpal.CliParser;
import ca.uqac.lif.labpal.CliParser.Argument;
import ca.uqac.lif.labpal.CliParser.ArgumentMap;
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

/**
 * Main laboratory for Pat The Miner
 */
public class MainLab extends Laboratory
{
  /**
   * The step (in number of events) at which measurements are made in each experiment
   */
  protected static int s_eventStep = 10000;

  /**
   * The maximum trace length to generate
   */
  public static int MAX_TRACE_LENGTH = 100001;

  /**
   * A thread pool. Used only for multi-thread experiments.
   */
  protected static transient ExecutorService s_service = Executors.newCachedThreadPool();

  /**
   * A group for experiments measuring the throughput of trend distance processors
   */
  protected transient Group m_groupTrendDistance;

  /**
   * A group for experiments measuring the throughput of self-correlated
   * trend distance processors
   */
  protected transient Group m_groupSelfCorrelated;

  /**
   * Whether to display experiments about multi-threading
   */
  protected static boolean s_includeThreadExperiments = false;

  /**
   * The first window width to be used in each experiment
   */
  protected static transient int s_width1 = 50;

  /**
   * The second window width to be used in each experiment
   */
  protected static transient int s_width2 = 100;

  /**
   * The third window width to be used in each experiment
   */
  protected static transient int s_width3 = 200;

  @Override
  public void setup()
  {	  
    // Basic metadata
    setTitle("Benchmark for Pat The Miner");
    setAuthor("Laboratoire d'informatique formelle");
    setDoi("10.5281/zenodo.1252497");

    // Command line arguments
    ArgumentMap args = getCliArguments();
    if (args.hasOption("with-mt"))
    {
      // Include multi-thread experiments
      s_includeThreadExperiments = true;
    }

    // Lab stats
    add(new LabStats(this));

    // Trend distance experiments
    {
      m_groupTrendDistance = new Group("Trend distance throughput");
      m_groupTrendDistance.setDescription("Measures the throughput of the trend distance processor for various trend computations.");
      add(m_groupTrendDistance);
      // Average experiments
      ExperimentTable et_avg = generateWindowExperiments(generateAverageExperiment(s_width1, false), generateAverageExperiment(s_width2, false), generateAverageExperiment(s_width3, false), "running average", "Average", m_groupTrendDistance);
      // Running moments experiments
      ExperimentTable et_moments = generateWindowExperiments(generateRunningMomentsExperiment(s_width1, false), generateRunningMomentsExperiment(s_width2, false), generateRunningMomentsExperiment(s_width3, false), "running moments", "Moments", m_groupTrendDistance);
      // Distribution experiments
      ExperimentTable et_distribution = generateWindowExperiments(generateDistributionExperiment(s_width1, false), generateDistributionExperiment(s_width2, false), generateDistributionExperiment(s_width3, false), "symbol distribution", "Distribution", m_groupTrendDistance);
      // K-means experiments
      ExperimentTable et_clustering = generateWindowExperiments(generateClusterDistributionExperiment(s_width1, false), generateClusterDistributionExperiment(s_width2, false), generateClusterDistributionExperiment(s_width3, false), "closest cluster", "Clustering", m_groupTrendDistance);

      {
        // Table and plot for impact of window width
        TransformedTable t_impact_window = new TransformedTable(new Join(TrendDistanceExperiment.WIDTH),
            new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Running average"), et_avg),
            new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Running moments"), et_moments),
            new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Symbol distribution"), et_distribution),
            new TransformedTable(new RenameColumns(TrendDistanceExperiment.WIDTH, "Closest cluster"), et_clustering)
            );
        t_impact_window.setTitle("Impact of window width (trend distance)");
        t_impact_window.setNickname("tImpactWidth");
        add(t_impact_window);
        Scatterplot plot = new Scatterplot(t_impact_window);
        plot.setTitle("Impact of window width");
        plot.setCaption(Axis.X, "Window width").setCaption(Axis.Y, "Throughput (Hz)");
        plot.setNickname("pImpactWidth");
        add(plot);
      }
    }

    // Impact of threading
    if (s_includeThreadExperiments)
    {
      Group g_threading = new Group("Impact of multi-threading (trend distance)");
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

    // Self-correlated trend distance experiments
    {
      m_groupSelfCorrelated = new Group("Self-correlated trend distance throughput");
      m_groupSelfCorrelated.setDescription("Measures the throughput of the self-correlated trend distance processor for various trend computations.");
      add(m_groupSelfCorrelated);
      // Average experiments
      ExperimentTable et_avg = generateWindowExperiments(generateSelfAverageExperiment(s_width1, false), generateSelfAverageExperiment(s_width2, false), generateSelfAverageExperiment(s_width3, false), "running average", "AverageSc", m_groupSelfCorrelated);
      // Running moments experiments
      ExperimentTable et_moments = generateWindowExperiments(generateSelfRunningMomentsExperiment(s_width1, false), generateSelfRunningMomentsExperiment(s_width2, false), generateSelfRunningMomentsExperiment(s_width3, false), "running moments", "MomentsSc", m_groupSelfCorrelated);
      // Distribution experiments
      ExperimentTable et_distribution = generateWindowExperiments(generateSelfDistributionExperiment(s_width1, false), generateSelfDistributionExperiment(s_width2, false), generateSelfDistributionExperiment(s_width3, false), "symbol distribution", "DistributionSc", m_groupSelfCorrelated);
      // K-means experiments
      ExperimentTable et_clustering = generateWindowExperiments(generateSelfClusterDistributionExperiment(s_width1, false), generateSelfClusterDistributionExperiment(s_width2, false), generateSelfClusterDistributionExperiment(s_width3, false), "closest cluster", "ClusteringSc", m_groupSelfCorrelated);
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
        add(t_impact_window);
        Scatterplot plot = new Scatterplot(t_impact_window);
        plot.setTitle("Impact of window width");
        plot.setCaption(Axis.X, "Window width").setCaption(Axis.Y, "Throughput (Hz)");
        plot.setNickname("pImpactWidthSc");
        add(plot);
      }
    }

    // Fixed pattern vs. self correlated
    {
      Region r = new Region();
      r.add(TrendExperiment.WIDTH, s_width1, s_width2, s_width3);
      String[] trends = new String[]{"Average", "Running moments", "Symbol distribution", "Closest cluster"};
      r.add(TrendExperiment.TREND, trends);
      for (Region sub_r : r.all(TrendExperiment.WIDTH))
      {
        int w = sub_r.getInt(TrendExperiment.WIDTH);
        ThroughputComparisonTable tab = new ThroughputComparisonTable(trends);
        tab.setTitle("Throughput comparison (window width = " + toLatex(w) + ")");
        tab.setNickname("tThroughputComparison" + w);
        for (Region sub_r2 : sub_r.all(TrendExperiment.TREND))
        {
          Region r_t = new Region(sub_r2).add(TrendExperiment.TYPE, TrendDistanceExperiment.TYPE_NAME);
          TrendDistanceExperiment exp_t = (TrendDistanceExperiment) getAnyExperiment(r_t);
          Region r_s = new Region(sub_r2).add(TrendExperiment.TYPE, SelfCorrelatedExperiment.TYPE_NAME);
          SelfCorrelatedExperiment exp_s = (SelfCorrelatedExperiment) getAnyExperiment(r_s);
          tab.add(sub_r2.getString(TrendExperiment.TREND), exp_t);
          tab.add(sub_r2.getString(TrendExperiment.TREND), exp_s);
        }
        add(tab);
        add(new MaxSlowdownMacro(this, "maxSlowdown" + toLatex(w), w, tab));
      }

      // Trend extraction experiments
      Group g_te = new Group("Trend extraction");
      add(g_te);
      {
        Region reg = new Region().add(MiningExperiment.NUM_LOGS, 100, 250, 500);
        reg.add(MiningExperiment.LOG_LENGTH, 10000, 20000, 50000);

        ExperimentTable t_20000 = generateKMeansExperiment(reg, 10000, g_te);
        ExperimentTable t_50000 = generateKMeansExperiment(reg, 20000, g_te);
        ExperimentTable t_100000 = generateKMeansExperiment(reg, 50000, g_te);
        TransformedTable tt = new TransformedTable(new Join(MiningExperiment.NUM_LOGS), 
            new TransformedTable(new RenameColumns(MiningExperiment.NUM_LOGS, "10000"), t_20000),
            new TransformedTable(new RenameColumns(MiningExperiment.NUM_LOGS, "20000"), t_50000),
            new TransformedTable(new RenameColumns(MiningExperiment.NUM_LOGS, "50000"), t_100000));
        tt.setTitle("Trend extraction speed for symbol distribution and K-means");
        add(tt);
        Scatterplot k_plot = new Scatterplot(tt);
        k_plot.setNickname("pKMeansLength");
        k_plot.setTitle("Trend extraction speed for symbol distribution and K-means");
        add(k_plot);
        MinLogsPerSecondMacro mlpsm = new MinLogsPerSecondMacro(this, "minLogsKMeans", 500, tt);
        add(mlpsm);
      }
    }
  }

  protected ExperimentTable generateKMeansExperiment(Region r_nl, int log_length, Group g)
  {
    ExperimentTable t_by_num_logs = new ExperimentTable(MiningExperiment.NUM_LOGS, MiningExperiment.DURATION);
    t_by_num_logs.setTitle("Trend extraction speed for symbol distribution and K-means (log length " + r_nl.getInt(MiningExperiment.LOG_LENGTH) + ")");
    for (Region r_ll : r_nl.all(MiningExperiment.NUM_LOGS))
    {
      DistributionKmeansExperiment dke = new DistributionKmeansExperiment(getRandom(), r_ll.getInt(MiningExperiment.NUM_LOGS), log_length);
      add(dke);
      g.add(dke);
      t_by_num_logs.add(dke);
    }
    add(t_by_num_logs);
    return t_by_num_logs;
  }

  protected ExperimentTable generateWindowExperiments(StreamExperiment exp_50, StreamExperiment exp_100, StreamExperiment exp_200, String beta_name, String nickname_prefix, Group g)
  {
    ExperimentTable table_50 = createTable(exp_50, beta_name, s_width1);
    ExperimentTable table_100 = createTable(exp_100, beta_name, s_width2);
    ExperimentTable table_200 = createTable(exp_200, beta_name, s_width3);
    g.add(exp_50, exp_100, exp_200);
    {
      Table tt = new TransformedTable(new Join(StreamExperiment.LENGTH),
          new TransformedTable(new RenameColumns(StreamExperiment.LENGTH, Integer.toString(s_width1)), table_50),
          new TransformedTable(new RenameColumns(StreamExperiment.LENGTH, Integer.toString(s_width2)), table_100),
          new TransformedTable(new RenameColumns(StreamExperiment.LENGTH, Integer.toString(s_width3)), table_200)
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
    return addNewTrendDistanceExperiment("Average", "Subtraction", src, alarm, width, multi_thread);
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
    return addNewTrendDistanceExperiment("Symbol distribution", "Map distance", src, alarm, width, multi_thread);
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
    return addNewTrendDistanceExperiment("Running moments", "Vector distance", src, alarm, width, multi_thread);
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
    return addNewTrendDistanceExperiment("Closest cluster", "Euclidean distance to closest cluster", src, alarm, width, multi_thread);
  }

  protected SelfCorrelatedExperiment generateSelfAverageExperiment(int width, boolean multi_thread)
  {
    Random random = getRandom();
    CumulativeAverage beta = new CumulativeAverage();
    SelfCorrelatedTrendDistance<Number,Number,Number> alarm = new SelfCorrelatedTrendDistance<Number,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(Numbers.subtraction, StreamVariable.X, StreamVariable.Y)), 0.5, Numbers.isLessThan);
    Source src = new RandomNumberSource(random, MAX_TRACE_LENGTH);
    return addNewSelfCorrelatedExperiment("Average", "Subtraction", src, alarm, width, multi_thread);
  }

  protected SelfCorrelatedExperiment generateSelfRunningMomentsExperiment(int width, boolean multi_thread)
  {
    Random random = getRandom();
    RunningMoments beta = new RunningMoments(3);
    SelfCorrelatedTrendDistance<DoublePoint,Number,Number> alarm = new SelfCorrelatedTrendDistance<DoublePoint,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new PointDistance(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    Source src = new RandomNumberSource(random, MAX_TRACE_LENGTH);
    return addNewSelfCorrelatedExperiment("Running moments", "Subtraction", src, alarm, width, multi_thread);
  }

  protected SelfCorrelatedExperiment generateSelfDistributionExperiment(int width, boolean multi_thread)
  {
    Random random = getRandom();
    SymbolDistribution beta = new SymbolDistribution();
    SelfCorrelatedTrendDistance<HashMap<?,?>,Number,Number> alarm = new SelfCorrelatedTrendDistance<HashMap<?,?>,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(MapDistance.instance, StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MAX_TRACE_LENGTH);
    return addNewSelfCorrelatedExperiment("Symbol distribution", "Map distance", src, alarm, width, multi_thread);
  }

  protected SelfCorrelatedExperiment generateSelfClusterDistributionExperiment(int width, boolean multi_thread)
  {
    int num_symbols = 2;
    Random random = getRandom();
    SymbolDistributionClusters beta = new SymbolDistributionClusters();
    SelfCorrelatedTrendDistance<DoublePoint,Number,Number> alarm = new SelfCorrelatedTrendDistance<DoublePoint,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new PointDistance(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 0.25, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MAX_TRACE_LENGTH, num_symbols);
    return addNewSelfCorrelatedExperiment("Closest cluster", "Euclidean distance to closest cluster", src, alarm, width, multi_thread);
  }

  protected TrendDistanceExperiment addNewTrendDistanceExperiment(String trend, String metric, Source src, Processor alarm, int width, boolean multi_thread)
  {
    TrendDistanceExperiment tde = new TrendDistanceExperiment();
    tde.setSource(src);
    tde.setProcessor(alarm);
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

  protected SelfCorrelatedExperiment addNewSelfCorrelatedExperiment(String trend, String metric, Source src, Processor alarm, int width, boolean multi_thread)
  {
    SelfCorrelatedExperiment tde = new SelfCorrelatedExperiment();
    tde.setSource(src);
    tde.setProcessor(alarm);
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

  protected ExperimentTable createTable(StreamExperiment tde, String trend, int width)
  {
    String title = "Running time for " + trend + ", window width = " + width;
    ExperimentTable et = new ExperimentTable(StreamExperiment.LENGTH, StreamExperiment.TIME);
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

  @Override
  public void setupCli(CliParser parser)
  {
    parser.addArgument(new Argument().withLongName("with-mt").withDescription("Include experiments about multi-threading"));
  }

  public static void main(String[] args)
  {
    // Nothing else to do here
    MainLab.initialize(args, MainLab.class);
  }

  /**
   * Converts a number into a "LaTeX" name (as LaTeX forbids macro names
   * that contain numbers)
   * @param x The number
   * @return The name
   */
  public static String toLatex(int x)
  {
    if (x == 50)
      return "Fifty";
    if (x == 100)
      return "Hundred";
    if (x == 200)
      return "TwoHundred";
    return "Unknown";
  }
}
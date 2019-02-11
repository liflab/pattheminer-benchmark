package pattheminer.trenddistance;

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
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Random;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.Join;
import ca.uqac.lif.mtnp.table.RenameColumns;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TransformedTable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import pattheminer.AverageThroughputMacro;
import pattheminer.MainLab;
import pattheminer.MaxSlowdownMacro;
import pattheminer.MonotonicWindowClaim;
import pattheminer.RandomNumberSource;
import pattheminer.RandomSymbolSource;
import pattheminer.SelfCorrelatedExperiment;
import pattheminer.StreamExperiment;
import pattheminer.ThroughputComparisonTable;
import pattheminer.TrendDistanceExperiment;
import pattheminer.TrendExperiment;
import pattheminer.patterns.CumulativeAverage;
import pattheminer.patterns.SymbolDistribution;
import pattheminer.patterns.SymbolDistributionClusters;

public class SetupTrendDistanceExperiments
{
  protected MainLab m_lab;

  public static void populate(MainLab lab)
  {
    SetupTrendDistanceExperiments m = new SetupTrendDistanceExperiments(lab);
    m.fillWithExperiments();
  }

  protected SetupTrendDistanceExperiments(MainLab lab)
  {
    super();
    m_lab = lab;
  }

  /**
   * Populates the lab with experiments
   */
  protected void fillWithExperiments()
  {
    // Trend distance experiments
    {
      Group g = new Group("Trend distance throughput");
      g.setDescription("Measures the throughput of the trend distance processor for various trend computations.");
      m_lab.add(g);
      // Average experiments
      ExperimentTable et_avg = generateWindowExperiments(generateAverageExperiment(MainLab.s_width1, false), generateAverageExperiment(MainLab.s_width2, false), generateAverageExperiment(MainLab.s_width3, false), "running average", "Average", g);
      // Running moments experiments
      ExperimentTable et_moments = generateWindowExperiments(generateRunningMomentsExperiment(MainLab.s_width1, false), generateRunningMomentsExperiment(MainLab.s_width2, false), generateRunningMomentsExperiment(MainLab.s_width3, false), "running moments", "Moments", g);
      // Distribution experiments
      ExperimentTable et_distribution = generateWindowExperiments(generateDistributionExperiment(MainLab.s_width1, false), generateDistributionExperiment(MainLab.s_width2, false), generateDistributionExperiment(MainLab.s_width3, false), "symbol distribution", "Distribution", g);
      // K-means experiments
      ExperimentTable et_clustering = generateWindowExperiments(generateClusterDistributionExperiment(MainLab.s_width1, false), generateClusterDistributionExperiment(MainLab.s_width2, false), generateClusterDistributionExperiment(MainLab.s_width3, false), "closest cluster", "Clustering", g);

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
        m_lab.add(t_impact_window);
        Scatterplot plot = new Scatterplot(t_impact_window);
        plot.setTitle("Impact of window width");
        plot.setCaption(Axis.X, "Window width").setCaption(Axis.Y, "Throughput (Hz)");
        plot.setNickname("pImpactWidth");
        m_lab.add(plot);
      }
    }
    
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
  }

  protected TrendDistanceExperiment generateRunningMomentsExperiment(int width, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    RunningMoments beta = new RunningMoments(3);
    DoublePoint pattern = new DoublePoint(new double[]{1d, 1d, 1d});
    Processor wp = null;
    if (multi_thread)
    {
      NonBlockingPush nbp = new NonBlockingPush(beta, MainLab.s_service);
      wp = new ParallelWindow(nbp, width);
    }
    else
    {
      //wp = new Window(average, width);
      wp = new ParallelWindow(beta, width);
    }
    TrendDistance<DoublePoint,Number,Number> alarm = new TrendDistance<DoublePoint,Number,Number>(pattern, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new PointDistance(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    Source src = new RandomNumberSource(random, MainLab.MAX_TRACE_LENGTH);
    return addNewTrendDistanceExperiment("Running moments", "Vector distance", src, alarm, width, multi_thread);
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

  protected TrendDistanceExperiment generateAverageExperiment(int width, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    CumulativeAverage average = new CumulativeAverage();
    Processor wp = null;
    if (multi_thread)
    {
      NonBlockingPush nbp = new NonBlockingPush(average, MainLab.s_service);
      wp = new ParallelWindow(nbp, width);
    }
    else
    {
      //wp = new Window(average, width);
      wp = new ParallelWindow(average, width);
    }
    TrendDistance<Number,Number,Number> alarm = new TrendDistance<Number,Number,Number>(6, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(Numbers.subtraction, StreamVariable.X, StreamVariable.Y)), 0.5, Numbers.isLessThan);
    Source src = new RandomNumberSource(random, MainLab.MAX_TRACE_LENGTH);
    return addNewTrendDistanceExperiment("Average", "Subtraction", src, alarm, width, multi_thread);
  }

  protected TrendDistanceExperiment generateDistributionExperiment(int width, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    SymbolDistribution beta = new SymbolDistribution();
    HashMap<Object,Object> pattern = MapDistance.createMap("0", width - 2, "1", 1, "2", 1);
    Processor wp = null;
    if (multi_thread)
    {
      NonBlockingPush nbp = new NonBlockingPush(beta, MainLab.s_service);
      wp = new ParallelWindow(nbp, width);
    }
    else
    {
      //wp = new Window(average, width);
      wp = new ParallelWindow(beta, width);
    }
    TrendDistance<HashMap<?,?>,Number,Number> alarm = new TrendDistance<HashMap<?,?>,Number,Number>(pattern, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(MapDistance.instance, StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH);
    return addNewTrendDistanceExperiment("Symbol distribution", "Map distance", src, alarm, width, multi_thread);
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

  protected TrendDistanceExperiment addNewTrendDistanceExperiment(String trend, String metric, Source src, Processor alarm, int width, boolean multi_thread)
  {
    TrendDistanceExperiment tde = new TrendDistanceExperiment();
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
  
  protected TrendDistanceExperiment generateClusterDistributionExperiment(int width, boolean multi_thread)
  {
    int num_symbols = 2;
    Random random = m_lab.getRandom();
    SymbolDistributionClusters beta = new SymbolDistributionClusters();
    Set<DoublePoint> pattern = new HashSet<DoublePoint>();
    pattern.add(new DoublePoint(new double[]{0.7, 0.3}));
    pattern.add(new DoublePoint(new double[]{0.3, 0.7}));
    Processor wp = null;
    if (multi_thread)
    {
      NonBlockingPush nbp = new NonBlockingPush(beta, MainLab.s_service);
      wp = new ParallelWindow(nbp, width);
    }
    else
    {
      //wp = new Window(average, width);
      wp = new ParallelWindow(beta, width);
    }
    TrendDistance<Set<DoublePoint>,Set<DoublePoint>,Number> alarm = new TrendDistance<Set<DoublePoint>,Set<DoublePoint>,Number>(pattern, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new DistanceToClosest(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 0.25, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH, num_symbols);
    return addNewTrendDistanceExperiment("Closest cluster", "Euclidean distance to closest cluster", src, alarm, width, multi_thread);
  }
}

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
package pattheminer;

import ca.uqac.lif.labpal.CliParser;
import ca.uqac.lif.labpal.CliParser.Argument;
import ca.uqac.lif.labpal.CliParser.ArgumentMap;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.Join;
import ca.uqac.lif.mtnp.table.RenameColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pattheminer.classifiers.SetupClassifierExperiments;
import pattheminer.secondorder.SetupSecondOrderTrendDistanceExperiments;
import pattheminer.trenddistance.SetupTrendDistanceExperiments;

/**
 * Main laboratory for Pat The Miner
 */
public class MainLab extends Laboratory
{
  /**
   * The step (in number of events) at which measurements are made in each experiment
   */
  public static int s_eventStep = 10000;

  /**
   * The maximum trace length to generate
   */
  public static int MAX_TRACE_LENGTH = 100001;

  /**
   * A thread pool. Used only for multi-thread experiments.
   */
  public static transient ExecutorService s_service = Executors.newCachedThreadPool();
  
  /**
   * A nicknamer
   */
  public static transient MainLabNicknamer s_nicknamer = new MainLabNicknamer();
  
  /**
   * A title namer
   */
  public static transient MainLabTitleNamer s_titleNamer = new MainLabTitleNamer();

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
   * A group for experiments measuring the throughput of self-trained
   * class prediction processors
   */
  protected transient Group m_groupSelfTrainedClassPrediction;

  /**
   * Whether to display experiments about multi-threading
   */
  protected static boolean s_includeThreadExperiments = false;
  
  /**
   * Whether to display experiments about predictive analytics
   */
  protected static boolean s_includePredictiveExperiments = false;

  /**
   * The first window width to be used in each experiment
   */
  public static transient int s_width1 = 50;

  /**
   * The second window width to be used in each experiment
   */
  public static transient int s_width2 = 100;

  /**
   * The third window width to be used in each experiment
   */
  public static transient int s_width3 = 200;

  @Override
  public void setup()
  {	  
    // Basic metadata
    setTitle("Benchmark for Pat The Miner v2");
    setAuthor("Laboratoire d'informatique formelle");
    setDoi("TODO");

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
    new SetupTrendDistanceExperiments(this).fillWithExperiments();

    // Classifier training experiments
    if (s_includePredictiveExperiments)
    {
      new SetupClassifierExperiments(this).fillWithExperiments();
    }
    
    // Second-order trend distance experiments
    new SetupSecondOrderTrendDistanceExperiments(this).fillWithExperiments();


    // Impact of threading
    /*
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
    */


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
  
  public ExperimentTable createTable(StreamExperiment tde, String trend, int width)
  {
    String title = "Running time for " + trend + ", window width = " + width;
    ExperimentTable et = new ExperimentTable(StreamExperiment.LENGTH, StreamExperiment.TIME);
    et.setTitle(title);
    et.add(tde);
    add(et);
    return et;

  }

  public Experiment getAnyExperiment(Region r)
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
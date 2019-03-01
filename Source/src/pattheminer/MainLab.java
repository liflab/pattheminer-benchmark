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
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Laboratory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pattheminer.classifiers.SetupClassifierExperiments;
import pattheminer.extraction.SetupTrendExtractionExperiments;
import pattheminer.secondorder.SetupSecondOrderTrendDistanceExperiments;
import pattheminer.trenddistance.SetupStaticVsSelf;
import pattheminer.trenddistance.context.SetupContextualExperiments;
import pattheminer.trenddistance.selftd.SetupSelfCorrelatedExperiments;
import pattheminer.trenddistance.statictd.SetupTrendDistanceExperiments;
import pattheminer.trenddistance.threads.SetupThreadExperiments;

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
   * A group for experiments measuring the throughput of self-trained
   * class prediction processors
   */
  protected transient Group m_groupSelfTrainedClassPrediction;
  
  /**
   * Whether to display experiments about contextual patterns
   */
  protected boolean m_includeContextualExperiments = true;

  /**
   * Whether to display experiments about multi-threading
   */
  protected boolean m_includeThreadExperiments = false;

  /**
   * Whether to display experiments about predictive analytics
   */
  protected boolean m_includePredictiveExperiments = false;

  /**
   * The first window width to be used in each experiment
   */
  public static transient Number[] s_widths = {50, 100, 150, 200, 250};

  @Override
  public void setup()
  {	  
    // Basic metadata
    setTitle("Benchmark for Pat The Miner v2");
    setAuthor("Laboratoire d'informatique formelle");
    setDoi("TODO");

    // Command line arguments
    ArgumentMap args = getCliArguments();
    if (args != null)
    {
      m_includeThreadExperiments = args.hasOption("with-mt");
    }

    // Lab stats
    add(new LabStats(this));

    // Static trend distance experiments
    new SetupTrendDistanceExperiments(this).fillWithExperiments();

    // Self-correlated trend distance experiments
    new SetupSelfCorrelatedExperiments(this).fillWithExperiments();

    // Contextual trend distance experiments
    if (m_includeContextualExperiments)
    {
      new SetupContextualExperiments(this).fillWithExperiments();
    }
    
    // Comparison experiments
    new SetupStaticVsSelf(this).fillWithExperiments();

    // Second-order trend distance experiments
    new SetupSecondOrderTrendDistanceExperiments(this).fillWithExperiments();
    
    // Trend extraction experiments
    new SetupTrendExtractionExperiments(this).fillWithExperiments();
    
    // Classifier training experiments
    if (m_includePredictiveExperiments)
    {
      new SetupClassifierExperiments(this).fillWithExperiments();
    }

    // Impact of threading
    if (m_includeThreadExperiments)
    {
      new SetupThreadExperiments(this).fillWithExperiments();
    }
  }
  
  @Override
  public void setupCli(CliParser parser)
  {
    parser.addArgument(new Argument().withLongName("with-mt").withDescription("Include experiments about multi-threading"));
    parser.addArgument(new Argument().withLongName("with-ct").withDescription("Include experiments about context"));
  }

  public static void main(String[] args)
  {
    // Nothing else to do here
    MainLab.initialize(args, MainLab.class);
  }
}
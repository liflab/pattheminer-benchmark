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
import ca.uqac.lif.labpal.Laboratory;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pattheminer.extraction.SetupTrendExtractionExperiments;
import pattheminer.forecast.SetupPredictionExperiments;
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
   * Whether to display experiments about contextual patterns
   */
  protected boolean m_includeContextualExperiments = true;

  /**
   * Whether to display experiments about multi-threading
   */
  protected boolean m_includeThreadExperiments = false;

  /**
   * Whether to display experiments about trends
   */
  protected boolean m_includeTrendExperiments = true;

  /**
   * Whether to display experiments about predictive analytics
   */
  protected boolean m_includePredictiveExperiments = false;

  /**
   * The folder where static files will be generated by default
   */
  protected String m_dataFolder = "./";

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
    //setDoi("TODO");

    // Command line arguments
    ArgumentMap args = getCliArguments();
    if (args != null)
    {
      m_includeThreadExperiments = args.hasOption("with-mt");
      m_includePredictiveExperiments = args.hasOption("with-pred");
      if (m_includePredictiveExperiments)
      {
        // By default, predictive experiments hide trend experiments
        m_includeTrendExperiments = false;
      }
      // ...unless they are explicitly requested
      m_includeTrendExperiments = args.hasOption("with-trend");

      if (args.hasOption("datadir"))
      {
        m_dataFolder = args.getOptionValue("datadir");
        if (!folderExists(m_dataFolder))
        {
          System.err.println("The folder " + m_dataFolder + " does not exist");
          System.exit(1); // Rather inelegant, but no other way
        }
        if (!m_dataFolder.endsWith("/"))
        {
          m_dataFolder += "/";
        }
      }
    }

    // Lab stats
    add(new LabStats(this));
    add(new OtherStats(this));

    // Trend experiments (corresponds to EDOC 2018 and Information Systems)
    if (m_includeTrendExperiments)
    {
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

      // Impact of threading
      if (m_includeThreadExperiments)
      {
        new SetupThreadExperiments(this).fillWithExperiments();
      }
    }

    // Classifier training experiments (corresponds to EDOC 2019)
    if (m_includePredictiveExperiments)
    {
      new SetupPredictionExperiments(this).fillWithExperiments();
    }
  }

  @Override
  public void setupCli(CliParser parser)
  {
    parser.addArgument(new Argument().withLongName("with-mt").withDescription("Include experiments about multi-threading"));
    parser.addArgument(new Argument().withLongName("with-ct").withDescription("Include experiments about context"));
    parser.addArgument(new Argument().withLongName("with-pred").withDescription("Include prediction experiments"));
    parser.addArgument(new Argument().withLongName("with-trend").withDescription("Include trend experiments"));
    parser.addArgument(new Argument().withLongName("datadir").withArgument("dir").withDescription("Write trace files to dir"));
  }

  public static void main(String[] args)
  {
    // Nothing else to do here
    MainLab.initialize(args, MainLab.class);
  }

  /**
   * Checks if a folder exists
   * @param folderName The name of the folder
   * @return <tt>true</tt> if the folder exists, <tt>false</tt> otherwise
   */
  public static boolean folderExists(String folderName) 
  {
    File file = new File(folderName);
    if (file.exists() && file.isDirectory()) 
    {
      return true;
    }
    return false;
  }
}
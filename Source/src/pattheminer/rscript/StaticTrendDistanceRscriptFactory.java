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
package pattheminer.rscript;

import static pattheminer.trenddistance.TrendExperiment.CLOSEST_CLUSTER;
import static pattheminer.trenddistance.TrendExperiment.N_GRAMS;
import static pattheminer.trenddistance.TrendExperiment.N_GRAM_WIDTH;
import static pattheminer.trenddistance.TrendExperiment.NUM_SLICES;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_AVG;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_MOMENTS;
import static pattheminer.trenddistance.TrendExperiment.AVG_SLICE_LENGTH;
import static pattheminer.trenddistance.TrendExperiment.SYMBOL_DISTRIBUTION;

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.Random;
import pattheminer.MainLab;
import pattheminer.source.BoundedSource;
import pattheminer.source.FileSource;
import pattheminer.source.RandomLabelSource;
import pattheminer.source.RandomNumberSource;
import pattheminer.source.RandomSymbolSource;
import pattheminer.trenddistance.statictd.StaticTrendDistanceFactory;
import pattheminer.trenddistance.statictd.StaticTrendDistanceStreamExperiment;

public class StaticTrendDistanceRscriptFactory extends StaticTrendDistanceFactory<StaticTrendDistanceRscriptExperiment>
{
  public static final transient String TYPE_NAME = "Trend distance";

  public StaticTrendDistanceRscriptFactory(MainLab lab, boolean use_files, String data_folder)
  {
    super(lab, StaticTrendDistanceRscriptExperiment.class, use_files, data_folder);
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createAverageExperiment(int width,
      boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    BoundedSource<Float> src = new RandomNumberSource(random, MainLab.MAX_TRACE_LENGTH);
    FileSource<Float> f_src = new FileSource<Float>(src, m_dataFolder);
    String script_filename = "sliding_average.R";
    StaticTrendDistanceRscriptExperiment exp = createNewTrendDistanceRscriptExperiment(RUNNING_AVG, "Subtraction", f_src, width, script_filename);
    exp.setArguments(width, 1);
    insertCommand(exp, RscriptExperiment.formatCommand(exp.getCommand()));
    return exp;
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createRunningMomentsExperiment(int width,
      boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    BoundedSource<Float> src = new RandomNumberSource(random, MainLab.MAX_TRACE_LENGTH);
    FileSource<Float> f_src = new FileSource<Float>(src, m_dataFolder);
    String script_filename = "sliding_moments.R";
    StaticTrendDistanceRscriptExperiment exp = createNewTrendDistanceRscriptExperiment(RUNNING_MOMENTS, "Vector distance", f_src, width, script_filename);
    exp.setArguments(width, 1);
    return exp;
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createClosestClusterExperiment(int width,
      boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    BoundedSource<String> src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH);
    FileSource<String> f_src = new FileSource<String>(src, m_dataFolder);
    String script_filename = "a_and_b_frequencies.R";
    StaticTrendDistanceRscriptExperiment exp = createNewTrendDistanceRscriptExperiment(CLOSEST_CLUSTER, "Euclidean distance to closest cluster", f_src, width, script_filename);
    exp.setArguments(width, 1);
    insertCommand(exp, RscriptExperiment.formatCommand(exp.getCommand()));
    return exp;
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createDistributionExperiment(int width,
      boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    BoundedSource<String> src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH);
    FileSource<String> f_src = new FileSource<String>(src, m_dataFolder);
    String script_filename = "a_and_b_frequencies.R";
    StaticTrendDistanceRscriptExperiment exp = createNewTrendDistanceRscriptExperiment(SYMBOL_DISTRIBUTION, "Map distance", f_src, width, script_filename);
    exp.setArguments(width, 1);
    insertCommand(exp, RscriptExperiment.formatCommand(exp.getCommand()));
    return exp;
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createNgramExperiment(int width, int N,
      boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    BoundedSource<String> src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH);
    FileSource<String> f_src = new FileSource<String>(src, m_dataFolder);
    String script_filename = "ngrams.R";
    StaticTrendDistanceRscriptExperiment exp = createNewTrendDistanceRscriptExperiment(N_GRAMS, "Jaccard index", f_src, width, script_filename);
    exp.setArguments(width, 1, N);
    // For the n-gram experiment, there is an additional parameter
    exp.setInput(N_GRAM_WIDTH, N);
    exp.describe(N_GRAM_WIDTH, "The width of the N-grams (i.e. the value of N");
    insertCommand(exp, RscriptExperiment.formatCommand(exp.getCommand()));
    return exp;
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createSliceLengthExperiment(int width,
      int num_slices, int slice_length, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    BoundedSource<Object[]> src = new RandomLabelSource(random, MainLab.MAX_TRACE_LENGTH, slice_length, num_slices);
    FileSource<Object[]> f_src = new FileSource<Object[]>(src, m_dataFolder);
    String script_filename = "Script not yet ready";
    // TODO: remove the "PREREQ_F" argument on this line when the script is ready
    StaticTrendDistanceRscriptExperiment exp = createNewTrendDistanceRscriptExperiment(AVG_SLICE_LENGTH, "Jaccard index", f_src, width, script_filename, Experiment.Status.PREREQ_F);
    // For the slice experiment, there are two additional parameters
    exp.setInput(NUM_SLICES, num_slices);
    exp.describe(NUM_SLICES, "The number of slices");
    exp.setInput(AVG_SLICE_LENGTH, slice_length);
    exp.describe(AVG_SLICE_LENGTH, "The length of each slice");
    insertCommand(exp, RscriptExperiment.formatCommand(exp.getCommand()));
    return exp;
  }

  /**
   * Creates a new generic static trend distance experiment
   * @param trend The name of the trend to be computed
   * @param metric The distance metric
   * @param src The processor to be used as the source 
   * @param width The window width
   * @param script_name The filename of the R script that this experiment
   * will execute
   * @return A new trend distance experiment
   */
  protected StaticTrendDistanceRscriptExperiment createNewTrendDistanceRscriptExperiment(String trend, String metric, BoundedSource<?> src, int width, String script_name)
  {
    return createNewTrendDistanceRscriptExperiment(trend, metric, src, width, script_name, null);
  }

  /**
   * Creates a new generic static trend distance experiment
   * @param trend The name of the trend to be computed
   * @param metric The distance metric
   * @param src The processor to be used as the source 
   * @param width The window width
   * @param script_name The filename of the R script that this experiment
   * will execute
   * @param status An optional status to give the experiment when it 
   * is instantiated
   * @return A new trend distance experiment
   */
  protected StaticTrendDistanceRscriptExperiment createNewTrendDistanceRscriptExperiment(String trend, String metric, BoundedSource<?> src, int width, String script_name, Experiment.Status status)
  {
    StaticTrendDistanceRscriptExperiment tde;
    if (status == null)
    {
      tde = new StaticTrendDistanceRscriptExperiment();
    }
    else
    {
      tde = new StaticTrendDistanceRscriptExperiment(status);
    }
    tde.setSource(src);
    tde.setInput(StaticTrendDistanceStreamExperiment.WIDTH, width);
    tde.setInput(StaticTrendDistanceStreamExperiment.TREND, trend);
    tde.setInput(StaticTrendDistanceStreamExperiment.METRIC, metric);
    tde.setScriptName(script_name);
    tde.setDescription("Evaluates the static trend distance pattern on an input stream using R. The pattern that is being computed is " + trend + ", using " + metric + " as the distance metric. This experiment can also be run at the command line by typing:<pre>CMD</pre>");
    return tde;
  }
  
  /**
   * Injects the name of a command into the existing description of an
   * experiment. Concretely, it looks for "CMD" and replaces it by
   * the content of the <tt>command</tt> parameter.
   * @param e The experiment
   * @param command The command to inject
   */
  protected static void insertCommand(Experiment e, String command)
  {
    String s = e.getDescription();
    s = s.replaceAll("CMD", command);
    e.setDescription(s);
  }
}

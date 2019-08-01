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

import static pattheminer.trenddistance.TrendExperiment.RUNNING_AVG;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_MOMENTS;

import ca.uqac.lif.labpal.Random;
import pattheminer.MainLab;
import pattheminer.source.BoundedSource;
import pattheminer.source.FileSource;
import pattheminer.source.RandomNumberSource;
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
    String script_filename = "sliding_moments.R";
    StaticTrendDistanceRscriptExperiment exp = createNewTrendDistanceRscriptExperiment(RUNNING_AVG, "Subtraction", f_src, width, script_filename);
    exp.setArguments(width, 1);
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createDistributionExperiment(int width,
      boolean multi_thread)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createNgramExperiment(int width, int N,
      boolean multi_thread)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createSliceLengthExperiment(int width,
      int num_slices, int slice_length, boolean multi_thread)
  {
    // TODO Auto-generated method stub
    return null;
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
    StaticTrendDistanceRscriptExperiment tde = new StaticTrendDistanceRscriptExperiment();
    tde.setSource(src);
    tde.setInput(StaticTrendDistanceStreamExperiment.WIDTH, width);
    tde.setInput(StaticTrendDistanceStreamExperiment.TREND, trend);
    tde.setInput(StaticTrendDistanceStreamExperiment.METRIC, metric);
    //tde.setScriptName("helloworld.r");
    tde.setScriptName(script_name);
    return tde;
  }
}

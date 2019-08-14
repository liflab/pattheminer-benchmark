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
package pattheminer.trenddistance.statictd;

import static pattheminer.trenddistance.TrendExperiment.CLOSEST_CLUSTER;
import static pattheminer.trenddistance.TrendExperiment.N_GRAMS;
import static pattheminer.trenddistance.TrendExperiment.N_GRAM_WIDTH;
import static pattheminer.trenddistance.TrendExperiment.NUM_SLICES;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_AVG;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_MOMENTS;
import static pattheminer.trenddistance.TrendExperiment.AVG_SLICE_LENGTH;
import static pattheminer.trenddistance.TrendExperiment.SYMBOL_DISTRIBUTION;

import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.concurrency.NonBlockingPush;
import ca.uqac.lif.cep.concurrency.ParallelWindow;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.peg.JaccardIndex;
import ca.uqac.lif.cep.peg.MapDistance;
import ca.uqac.lif.cep.peg.PointDistance;
import ca.uqac.lif.cep.peg.TrendDistance;
import ca.uqac.lif.cep.peg.ml.DistanceToClosest;
import ca.uqac.lif.cep.peg.ml.RunningMoments;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.json.JsonString;
import ca.uqac.lif.labpal.Random;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import pattheminer.MainLab;
import pattheminer.patterns.AverageSliceLength;
import pattheminer.patterns.CumulativeAverage;
import pattheminer.patterns.Ngrams;
import pattheminer.patterns.SymbolDistribution;
import pattheminer.patterns.SymbolDistributionClusters;
import pattheminer.source.BoundedSource;
import pattheminer.source.FileSource;
import pattheminer.source.RandomLabelSource;
import pattheminer.source.RandomNumberSource;
import pattheminer.source.RandomSymbolSource;

/**
 * Factory that generates static trend distance experiments using various
 * input sources and trend processors.
 */
public class StaticTrendDistanceStreamFactory extends StaticTrendDistanceFactory<StaticTrendDistanceStreamExperiment>
{
  public StaticTrendDistanceStreamFactory(MainLab lab, boolean use_files, String data_folder)
  {
    super(lab, StaticTrendDistanceStreamExperiment.class, use_files, data_folder);
  }

  @Override
  protected StaticTrendDistanceStreamExperiment createAverageExperiment(int width, boolean multi_thread)
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
      wp = new Window(average, width);
    }
    TrendDistance<Number,Number,Number> alarm = new TrendDistance<Number,Number,Number>(6, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(Numbers.subtraction, StreamVariable.X, StreamVariable.Y)), 0.5, Numbers.isLessThan);
    BoundedSource<Float> src = new RandomNumberSource(random, MainLab.MAX_TRACE_LENGTH);
    if (m_useFiles)
    {
      src = new FileSource<Float>(src, m_dataFolder);
    }
    return createNewTrendDistanceStreamExperiment(RUNNING_AVG, "Subtraction", src, alarm, width, multi_thread);
  }

  @Override
  protected StaticTrendDistanceStreamExperiment createRunningMomentsExperiment(int width, boolean multi_thread)
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
      wp = new ParallelWindow(beta, width);
    }
    TrendDistance<DoublePoint,Number,Number> alarm = new TrendDistance<DoublePoint,Number,Number>(pattern, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new PointDistance(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    BoundedSource<Float> src = new RandomNumberSource(random, MainLab.MAX_TRACE_LENGTH);
    if (m_useFiles)
    {
      src = new FileSource<Float>(src, m_dataFolder);
    }
    return createNewTrendDistanceStreamExperiment(RUNNING_MOMENTS, "Vector distance", src, alarm, width, multi_thread);
  }

  @Override
  protected StaticTrendDistanceStreamExperiment createClosestClusterExperiment(int width, boolean multi_thread)
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
      wp = new ParallelWindow(beta, width);
    }
    TrendDistance<Set<DoublePoint>,Set<DoublePoint>,Number> alarm = new TrendDistance<Set<DoublePoint>,Set<DoublePoint>,Number>(pattern, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new DistanceToClosest(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 0.25, Numbers.isLessThan);
    BoundedSource<String> src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH, num_symbols);
    if (m_useFiles)
    {
      src = new FileSource<String>(src, m_dataFolder);
    }
    return createNewTrendDistanceStreamExperiment(CLOSEST_CLUSTER, "Euclidean distance to closest cluster", src, alarm, width, multi_thread);
  }

  @Override
  protected StaticTrendDistanceStreamExperiment createDistributionExperiment(int width, boolean multi_thread)
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
      wp = new ParallelWindow(beta, width);
    }
    TrendDistance<HashMap<?,?>,Number,Number> alarm = new TrendDistance<HashMap<?,?>,Number,Number>(pattern, wp, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(MapDistance.instance, StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    BoundedSource<String> src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH);
    if (m_useFiles)
    {
      src = new FileSource<String>(src, m_dataFolder);
    }
    return createNewTrendDistanceStreamExperiment(SYMBOL_DISTRIBUTION, "Map distance", src, alarm, width, multi_thread);
  }

  @Override
  protected StaticTrendDistanceStreamExperiment createNgramExperiment(int width, int N, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    BoundedSource<String> src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH);
    if (m_useFiles)
    {
      src = new FileSource<String>(src, m_dataFolder);
    }

    // Group processor that creates and accumulates N-grams
    GroupProcessor cumul_n_grams = new Ngrams(N);

    // Put this into a window
    Processor wp = null;
    if (multi_thread)
    {
      NonBlockingPush nbp = new NonBlockingPush(cumul_n_grams, MainLab.s_service);
      wp = new ParallelWindow(nbp, width);
    }
    else
    {
      wp = new ParallelWindow(cumul_n_grams, width);
    }
    Set<Object> reference = new HashSet<Object>();
    reference.add(createList("A", "B", "A"));
    reference.add(createList("B", "B", "A"));
    reference.add(createList("C", "C", "C"));
    TrendDistance<Set<Object>,Number,Number> alarm = new TrendDistance<Set<Object>,Number,Number>(reference, wp, JaccardIndex.instance, 1, Numbers.isLessThan);
    StaticTrendDistanceStreamExperiment tde = createNewTrendDistanceStreamExperiment(N_GRAMS, "Jaccard index", src, alarm, width, multi_thread);
    // For the n-gram experiment, there is an additional parameter
    tde.setInput(N_GRAM_WIDTH, N);
    tde.describe(N_GRAM_WIDTH, "The width of the N-grams (i.e. the value of N");
    return tde;
  }

  /**
   * Creates a new experiment using the set of N-grams as the trend processor
   * @param width The window width
   * @param num_slices The number of simultaneous slices
   * @param multi_thread Whether to use multi-threading
   * @return The slice length experiment
   */
  protected StaticTrendDistanceStreamExperiment createSliceLengthExperiment(int width, int num_slices, int slice_length, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    BoundedSource<Object[]> src = new RandomLabelSource(random, MainLab.MAX_TRACE_LENGTH, slice_length, num_slices);
    if (m_useFiles)
    {
      src = new FileSource<Object[]>(src, m_dataFolder);
    }
    // Group processor that creates and accumulates N-grams
    AverageSliceLength.SliceLength asl = new AverageSliceLength.SliceLength();

    // Put this into a window
    Processor wp = null;
    if (multi_thread)
    {
      NonBlockingPush nbp = new NonBlockingPush(asl, MainLab.s_service);
      wp = new ParallelWindow(nbp, width);
    }
    else
    {
      wp = new ParallelWindow(asl, width);
    }
    TrendDistance<Number,Number,Number> alarm = new TrendDistance<Number,Number,Number>(10, wp, Numbers.subtraction, 1, Numbers.isLessThan);
    StaticTrendDistanceStreamExperiment tde = createNewTrendDistanceStreamExperiment(AVG_SLICE_LENGTH, "Subtraction", src, alarm, width, multi_thread);
    // For the slice experiment, there are two additional parameters
    tde.setInput(NUM_SLICES, num_slices);
    tde.describe(NUM_SLICES, "The number of slices");
    tde.setInput(AVG_SLICE_LENGTH, slice_length);
    tde.describe(AVG_SLICE_LENGTH, "The length of each slice");
    return tde;
  }

  /**
   * Creates a new generic static trend distance experiment
   * @param trend The name of the trend to be computed
   * @param metric The distance metric
   * @param src The processor to be used as the source
   * @param alarm 
   * @param width The window width
   * @param multi_thread Whether the experiment uses multi-threading
   * @return A new trend distance experiment
   */
  public static StaticTrendDistanceStreamExperiment createNewTrendDistanceStreamExperiment(String trend, String metric, BoundedSource<?> src, Processor alarm, int width, boolean multi_thread)
  {
    StaticTrendDistanceStreamExperiment tde = new StaticTrendDistanceStreamExperiment();
    tde.setSource(src);
    tde.setProcessor(alarm);
    tde.setEventStep(MainLab.s_eventStep);
    tde.setInput(StaticTrendDistanceStreamExperiment.WIDTH, width);
    tde.setInput(StaticTrendDistanceStreamExperiment.TREND, trend);
    tde.setInput(StaticTrendDistanceStreamExperiment.METRIC, metric);
    JsonString jb = new JsonString("yes");
    if (!multi_thread)
    {
      jb = new JsonString("no");
    }
    tde.setInput(StaticTrendDistanceStreamExperiment.MULTITHREAD, jb);
    return tde;
  }
}

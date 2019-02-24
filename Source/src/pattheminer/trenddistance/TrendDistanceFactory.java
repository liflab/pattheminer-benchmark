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
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Random;
import ca.uqac.lif.labpal.Region;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import pattheminer.MainLab;
import pattheminer.RandomNumberSource;
import pattheminer.RandomSymbolSource;
import pattheminer.patterns.CumulativeAverage;
import pattheminer.patterns.SymbolDistribution;
import pattheminer.patterns.SymbolDistributionClusters;

public class TrendDistanceFactory extends ExperimentFactory<MainLab,TrendDistanceExperiment>
{
  public TrendDistanceFactory(MainLab lab)
  {
    super(lab, TrendDistanceExperiment.class);
  }

  @Override
  protected TrendDistanceExperiment createExperiment(Region r)
  {
    String trend_name = r.getString(TrendDistanceExperiment.TREND);
    int width = r.getInt(TrendDistanceExperiment.WIDTH);
    if (trend_name.compareTo("Running average") == 0)
    {
      return createAverageExperiment(width, false);
    }
    else if (trend_name.compareTo("Running moments") == 0)
    {
      return createRunningMomentsExperiment(width, false);
    }
    else if (trend_name.compareTo("Closest cluster") == 0)
    {
      return createClosestClusterExperiment(width, false);
    }
    else if (trend_name.compareTo("Symbol distribution") == 0)
    {
      return createDistributionExperiment(width, false);
    }
    return null;
  }

  /**
   * Creates a new experiment using the running average as the trend processor
   * @param width The window width
   * @param multi_thread Whether to use multi-threading
   * @return The average experiment
   */
  protected TrendDistanceExperiment createAverageExperiment(int width, boolean multi_thread)
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
    return createNewTrendDistanceExperiment("Running average", "Subtraction", src, alarm, width, multi_thread);
  }

  /**
   * Creates a new experiment using the vector of moments as the trend processor
   * @param width The window width
   * @param multi_thread Whether to use multi-threading
   * @return The experiment     
   */
  protected TrendDistanceExperiment createRunningMomentsExperiment(int width, boolean multi_thread)
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
    return createNewTrendDistanceExperiment("Running moments", "Vector distance", src, alarm, width, multi_thread);
  }

  /**
   * Creates a new experiment using the distance to the closest cluster
   * as the trend processor
   * @param width The window width
   * @param multi_thread Whether to use multi-threading
   * @return The experiment     
   */
  protected TrendDistanceExperiment createClosestClusterExperiment(int width, boolean multi_thread)
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
    return createNewTrendDistanceExperiment("Closest cluster", "Euclidean distance to closest cluster", src, alarm, width, multi_thread);
  }

  /**
   * Creates a new experiment using the symbol distribution
   * as the trend processor
   * @param width The window width
   * @param multi_thread Whether to use multi-threading
   * @return The experiment     
   */
  protected TrendDistanceExperiment createDistributionExperiment(int width, boolean multi_thread)
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
    return createNewTrendDistanceExperiment("Symbol distribution", "Map distance", src, alarm, width, multi_thread);
  }

  /**
   * Creates a new generic trend distance experiment
   * @param trend The name of the trend to be computed
   * @param metric The distance metric
   * @param src The processor to be used as the source
   * @param alarm 
   * @param width The window width
   * @param multi_thread Whether the experiment uses multi-threading
   * @return A new trend distance experiment
   */
  protected TrendDistanceExperiment createNewTrendDistanceExperiment(String trend, String metric, Source src, Processor alarm, int width, boolean multi_thread)
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
    return tde;
  }




}

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
package pattheminer.trenddistance.selftd;

import static pattheminer.trenddistance.TrendExperiment.TREND;
import static pattheminer.trenddistance.TrendExperiment.CLOSEST_CLUSTER;
import static pattheminer.trenddistance.TrendExperiment.N_GRAMS;
import static pattheminer.trenddistance.TrendExperiment.N_GRAM_WIDTH;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_AVG;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_MOMENTS;
import static pattheminer.trenddistance.TrendExperiment.SYMBOL_DISTRIBUTION;
import static pattheminer.trenddistance.TrendExperiment.WIDTH;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.peg.JaccardIndex;
import ca.uqac.lif.cep.peg.MapDistance;
import ca.uqac.lif.cep.peg.PointDistance;
import ca.uqac.lif.cep.peg.SelfCorrelatedTrendDistance;
import ca.uqac.lif.cep.peg.ml.RunningMoments;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.json.JsonString;
import ca.uqac.lif.labpal.Random;
import ca.uqac.lif.labpal.Region;
import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import pattheminer.MainLab;
import pattheminer.RandomNumberSource;
import pattheminer.RandomSymbolSource;
import pattheminer.patterns.CumulativeAverage;
import pattheminer.patterns.Ngrams;
import pattheminer.patterns.SymbolDistribution;
import pattheminer.patterns.SymbolDistributionClusters;
import pattheminer.trenddistance.TrendFactory;
import pattheminer.trenddistance.statictd.StaticTrendDistanceExperiment;

/**
 * Factory that generates self-correlated trend distance experiments using various
 * input sources and trend processors.
 */
public class SelfCorrelatedFactory extends TrendFactory<SelfCorrelatedExperiment>
{
  public SelfCorrelatedFactory(MainLab lab)
  {
    super(lab, SelfCorrelatedExperiment.class);
  }
  
  @Override
  protected SelfCorrelatedExperiment createExperiment(Region r)
  {
    String trend_name = r.getString(TREND);
    int width = r.getInt(WIDTH);
    if (trend_name.compareTo(RUNNING_AVG) == 0)
    {
      return createSelfAverageExperiment(width, false);
    }
    else if (trend_name.compareTo(RUNNING_MOMENTS) == 0)
    {
      return createSelfRunningMomentsExperiment(width, false);
    }
    else if (trend_name.compareTo(CLOSEST_CLUSTER) == 0)
    {
      return createSelfClosestClusterExperiment(width, false);
    }
    else if (trend_name.compareTo(SYMBOL_DISTRIBUTION) == 0)
    {
      return createSelfDistributionExperiment(width, false);
    }
    else if (trend_name.compareTo(N_GRAMS) == 0)
    {
      if (r.hasDimension(N_GRAM_WIDTH))
      {
        return createSelfNgramExperiment(width, r.getInt(N_GRAM_WIDTH), false);
      }
      return createSelfNgramExperiment(width, 3, false);
    }
    return null;
  }
  
  protected SelfCorrelatedExperiment createSelfAverageExperiment(int width, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    CumulativeAverage beta = new CumulativeAverage();
    SelfCorrelatedTrendDistance<Number,Number,Number> alarm = new SelfCorrelatedTrendDistance<Number,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(Numbers.subtraction, StreamVariable.X, StreamVariable.Y)), 0.5, Numbers.isLessThan);
    Source src = new RandomNumberSource(random, MainLab.MAX_TRACE_LENGTH);
    return createNewSelfCorrelatedExperiment(RUNNING_AVG, "Subtraction", src, alarm, width, multi_thread);
  }

  protected SelfCorrelatedExperiment createSelfRunningMomentsExperiment(int width, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    RunningMoments beta = new RunningMoments(3);
    SelfCorrelatedTrendDistance<DoublePoint,Number,Number> alarm = new SelfCorrelatedTrendDistance<DoublePoint,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new PointDistance(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    Source src = new RandomNumberSource(random, MainLab.MAX_TRACE_LENGTH);
    return createNewSelfCorrelatedExperiment(RUNNING_MOMENTS, "Subtraction", src, alarm, width, multi_thread);
  }

  protected SelfCorrelatedExperiment createSelfDistributionExperiment(int width, boolean multi_thread)
  {
    Random random = m_lab.getRandom();
    SymbolDistribution beta = new SymbolDistribution();
    SelfCorrelatedTrendDistance<HashMap<?,?>,Number,Number> alarm = new SelfCorrelatedTrendDistance<HashMap<?,?>,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(MapDistance.instance, StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH);
    return createNewSelfCorrelatedExperiment(SYMBOL_DISTRIBUTION, "Map distance", src, alarm, width, multi_thread);
  }

  protected SelfCorrelatedExperiment createSelfClosestClusterExperiment(int width, boolean multi_thread)
  {
    int num_symbols = 2;
    Random random = m_lab.getRandom();
    SymbolDistributionClusters beta = new SymbolDistributionClusters();
    SelfCorrelatedTrendDistance<DoublePoint,Number,Number> alarm = new SelfCorrelatedTrendDistance<DoublePoint,Number,Number>(width, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(new PointDistance(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 0.25, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH, num_symbols);
    return createNewSelfCorrelatedExperiment(CLOSEST_CLUSTER, "Euclidean distance to closest cluster", src, alarm, width, multi_thread);
  }
  
  protected SelfCorrelatedExperiment createSelfNgramExperiment(int width, int N, boolean multi_thread)
  {
    int num_symbols = 2;
    Random random = m_lab.getRandom();
    Ngrams beta = new Ngrams(N);
    SelfCorrelatedTrendDistance<Collection<?>,Number,Number> alarm = new SelfCorrelatedTrendDistance<Collection<?>,Number,Number>(width, width, beta, 
        JaccardIndex.instance, 0.25, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MainLab.MAX_TRACE_LENGTH, num_symbols);
    return createNewSelfCorrelatedExperiment(N_GRAMS, "Jaccard index", src, alarm, width, multi_thread);
  }

  protected SelfCorrelatedExperiment createNewSelfCorrelatedExperiment(String trend, String metric, Source src, Processor alarm, int width, boolean multi_thread)
  {
    SelfCorrelatedExperiment tde = new SelfCorrelatedExperiment();
    tde.setSource(src);
    tde.setProcessor(alarm);
    tde.setEventStep(MainLab.s_eventStep);
    tde.setInput(StaticTrendDistanceExperiment.WIDTH, width);
    tde.setInput(StaticTrendDistanceExperiment.TREND, trend);
    tde.setInput(StaticTrendDistanceExperiment.METRIC, metric);
    JsonString jb = new JsonString("yes");
    if (!multi_thread)
    {
      jb = new JsonString("no");
    }
    tde.setInput(StaticTrendDistanceExperiment.MULTITHREAD, jb);
    return tde;
  }
}

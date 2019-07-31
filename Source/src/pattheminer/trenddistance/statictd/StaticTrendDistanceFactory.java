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

import static pattheminer.trenddistance.TrendExperiment.AVG_SLICE_LENGTH;
import static pattheminer.trenddistance.TrendExperiment.CLOSEST_CLUSTER;
import static pattheminer.trenddistance.TrendExperiment.NUM_SLICES;
import static pattheminer.trenddistance.TrendExperiment.N_GRAMS;
import static pattheminer.trenddistance.TrendExperiment.N_GRAM_WIDTH;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_AVG;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_MOMENTS;
import static pattheminer.trenddistance.TrendExperiment.SLICE_LENGTH;
import static pattheminer.trenddistance.TrendExperiment.SYMBOL_DISTRIBUTION;

import ca.uqac.lif.labpal.Region;
import java.util.ArrayList;
import java.util.List;
import pattheminer.MainLab;
import pattheminer.TraceExperiment;
import pattheminer.trenddistance.TraceExperimentFactory;

public abstract class StaticTrendDistanceFactory<T extends TraceExperiment> extends TraceExperimentFactory<T>
{
  public StaticTrendDistanceFactory(MainLab lab, Class<T> clazz, boolean use_files, String data_folder)
  {
    super(lab, clazz, use_files, data_folder);
  }

  @Override
  protected final T createExperiment(Region r)
  {
    String trend_name = r.getString(StaticTrendDistanceStreamExperiment.TREND);
    int width = r.getInt(StaticTrendDistanceStreamExperiment.WIDTH);
    if (trend_name.compareTo(RUNNING_AVG) == 0)
    {
      return createAverageExperiment(width, false);
    }
    else if (trend_name.compareTo(RUNNING_MOMENTS) == 0)
    {
      return createRunningMomentsExperiment(width, false);
    }
    else if (trend_name.compareTo(CLOSEST_CLUSTER) == 0)
    {
      return createClosestClusterExperiment(width, false);
    }
    else if (trend_name.compareTo(SYMBOL_DISTRIBUTION) == 0)
    {
      return createDistributionExperiment(width, false);
    }
    else if (trend_name.compareTo(N_GRAMS) == 0)
    {
      if (r.hasDimension(N_GRAM_WIDTH))
      {
        return createNgramExperiment(width, r.getInt(N_GRAM_WIDTH), false);
      }
      return createNgramExperiment(width, 3, false);
    }
    else if (trend_name.compareTo(AVG_SLICE_LENGTH) == 0)
    {
      if (r.hasDimension(NUM_SLICES))
      {
        return createSliceLengthExperiment(width, r.getInt(NUM_SLICES), r.getInt(SLICE_LENGTH), false);
      }
      return createSliceLengthExperiment(width, 3, 10, false);
    }
    return null;
  }
  
  /**
   * Creates a list with an array of elements
   * @param elements The elements
   * @return The list with the elements
   */
  protected static List<Object> createList(Object ... elements)
  {
    List<Object> out = new ArrayList<Object>(elements.length);
    for (Object o : elements)
    {
      out.add(o);
    }
    return out;
  }
  
  /**
   * Creates a new experiment using the running average as the trend processor
   * @param width The window width
   * @param multi_thread Whether to use multi-threading
   * @return The average experiment
   */
  protected abstract T createAverageExperiment(int width, boolean multi_thread);
  
  /**
   * Creates a new experiment using the vector of moments as the trend processor
   * @param width The window width
   * @param multi_thread Whether to use multi-threading
   * @return The experiment     
   */
  protected abstract T createRunningMomentsExperiment(int width, boolean multi_thread);
  
  /**
   * Creates a new experiment using the distance to the closest cluster
   * as the trend processor
   * @param width The window width
   * @param multi_thread Whether to use multi-threading
   * @return The experiment     
   */
  protected abstract T createClosestClusterExperiment(int width, boolean multi_thread);
  
  /**
   * Creates a new experiment using the symbol distribution
   * as the trend processor
   * @param width The window width
   * @param multi_thread Whether to use multi-threading
   * @return The experiment     
   */
  protected abstract T createDistributionExperiment(int width, boolean multi_thread);
  
  /**
   * Creates a new experiment using the set of N-grams as the trend processor
   * @param width The window width
   * @param N The size of the N-grams
   * @param multi_thread Whether to use multi-threading
   * @return The average experiment
   */
  protected abstract T createNgramExperiment(int width, int N, boolean multi_thread);
  
  /**
   * Creates a new experiment using the set of N-grams as the trend processor
   * @param width The window width
   * @param num_slices The number of simultaneous slices
   * @param multi_thread Whether to use multi-threading
   * @return The slice length experiment
   */
  protected abstract T createSliceLengthExperiment(int width, int num_slices, int slice_length, boolean multi_thread);
}

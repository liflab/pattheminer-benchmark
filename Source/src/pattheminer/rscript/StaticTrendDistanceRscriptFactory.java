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

import ca.uqac.lif.labpal.Region;
import pattheminer.MainLab;
import pattheminer.trenddistance.statictd.StaticTrendDistanceFactory;

public class StaticTrendDistanceRscriptFactory extends StaticTrendDistanceFactory<StaticTrendDistanceRscriptExperiment>
{
  public static final transient String TYPE_NAME = "Trend distance";
  
  public StaticTrendDistanceRscriptFactory(MainLab lab, boolean use_files, String data_folder)
  {
    super(lab, StaticTrendDistanceRscriptExperiment.class, use_files, data_folder);
    
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createExperiment(Region r)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createAverageExperiment(int width,
      boolean multi_thread)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected StaticTrendDistanceRscriptExperiment createRunningMomentsExperiment(int width,
      boolean multi_thread)
  {
    // TODO Auto-generated method stub
    return null;
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
}

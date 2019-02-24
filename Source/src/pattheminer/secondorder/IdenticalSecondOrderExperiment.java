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
package pattheminer.secondorder;

import ca.uqac.lif.cep.peg.TrendDistance;

/**
 * A {@link SecondOrderExperiment} that simply uses the same first-order
 * trend <i>k</i> times. This is used to measure the scalability of the pattern
 * with respect to the number of first-order trends. 
 */
public class IdenticalSecondOrderExperiment extends SecondOrderExperiment
{
  /**
   * Creates a new experiment.
   * @param sec_order The {@link TrendDistance} processor to use for the
   * second-order trend
   * @param first_order The {@link TrendDistance} processor to use for the
   * first-order trend 
   * @param k The number of times the first-order trend should be duplicated
   */
  public IdenticalSecondOrderExperiment(TrendDistance<?,?,?> sec_order, TrendDistance<?,?,?> first_order, int k)
  {
    super(sec_order, repeat(first_order, k));
  }
  
  /**
   * Duplicates the same processor <i>k</i> times into an array. 
   * @param p The processor to duplicate
   * @param k The number of times to duplicate it
   * @return The array of cloned processors
   */
  protected static TrendDistance<?,?,?>[] repeat(TrendDistance<?,?,?> p, int k)
  {
    TrendDistance<?,?,?>[] out = new TrendDistance<?,?,?>[k];
    for (int i = 0; i < k; i++)
    {
      out[i] = (TrendDistance<?,?,?>) p.duplicate();
    }
    return out;
  }
}

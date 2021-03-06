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

import ca.uqac.lif.cep.peg.SecondOrderTrendDistance;
import ca.uqac.lif.cep.peg.TrendDistance;
import pattheminer.StreamExperiment;

/**
 * Experiment measuring the throughput of the second-order trend
 * distance pattern.
 */
public class SecondOrderExperiment extends StreamExperiment
{
  /**
   * The number of first-order trends included in the pattern
   */
  public static final transient String NUM_TRENDS = "k";
  
  /**
   * An array storing each of the trend distance processors
   */
  protected transient TrendDistance<?,?,?>[] m_firstOrder;
  
  /**
   * The second-order trend distance processor
   */
  protected transient TrendDistance<?,?,?> m_secOrder;
  
  /**
   * Creates a new empty second-order experiment
   */
  SecondOrderExperiment()
  {
    super();
    setDescription("Evaluates the throughput of the second-order trend distance pattern.");
    describe(NUM_TRENDS, "The number of first-order trends included in the pattern");
  }
  
  /**
   * Creates a new second-order experiment
   * @param sec_order The trend distance pattern used for the second-order
   * @param first_order The list of first-order trend distance patterns
   */
  public SecondOrderExperiment(TrendDistance<?,?,?> sec_order, TrendDistance<?,?,?> ... first_order)
  {
    this();
    setInput(NUM_TRENDS, first_order.length);
    m_firstOrder = first_order;
    m_secOrder = sec_order;
    SecondOrderTrendDistance sec_p = new SecondOrderTrendDistance(m_secOrder, m_firstOrder);
    setProcessor(sec_p);
  }
}

/*
    A benchmark for Pat The Miner
    Copyright (C) 2018 Laboratoire d'informatique formelle

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

import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.macro.NumberMacro;
import ca.uqac.lif.mtnp.DataFormatter;
import pattheminer.trenddistance.TrendDistanceExperiment;

public class ThreadSpeedupMacro extends NumberMacro
{
  /**
   * The experiment performed without threads
   */
  protected transient TrendDistanceExperiment m_experimentNt;
  
  /**
   * The experiment performed with threads
   */
  protected transient TrendDistanceExperiment m_experimentMt;
  
  public ThreadSpeedupMacro(Laboratory lab, TrendDistanceExperiment ent, TrendDistanceExperiment emt)
  {
    super(lab, "threadSpeedup", "The speedup obtained by using multiple threads for a trend distance pattern processor");
    m_experimentNt = ent;
    m_experimentMt = emt;
  }

  @Override
  public Double getNumber()
  {
    float tp_nt = m_experimentNt.readFloat(TrendDistanceExperiment.THROUGHPUT);
    float tp_mt = m_experimentMt.readFloat(TrendDistanceExperiment.THROUGHPUT);
    if (tp_nt == 0)
    {
      return 0d;
    }
    return DataFormatter.roundToSignificantFigures(tp_mt / tp_nt, 2);
  }
}

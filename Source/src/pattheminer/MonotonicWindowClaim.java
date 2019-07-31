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

import ca.uqac.lif.labpal.Claim;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.mtnp.table.PrimitiveValue;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TempTable;
import pattheminer.trenddistance.statictd.StaticTrendDistanceStreamExperiment;

/**
 * Claim that checks that the running time of the same computation increases
 * with the width of the window.
 */
public class MonotonicWindowClaim extends Claim
{
  protected transient Table m_table;
  
  protected transient String[] m_keys;
  
  protected transient String m_trend;
  
  protected static final transient float s_tolerance = 0.1f; 
  
  public MonotonicWindowClaim(Table table, String trend, String ... keys)
  {
    super();
    m_table = table;
    m_keys = keys;
    m_trend = trend;
    setName("Monotonic window " + trend);
    setDescription("The running time of a trend function should increase with the width of the window.");
  }
  
  @Override
  public Result verify(Laboratory lab)
  {
    Result out_res = Result.OK;
    TempTable dt = m_table.getDataTable();
    for (TableEntry te : dt.getEntries())
    {
      for (int i = 0; i < m_keys.length - 1; i++)
      {
        PrimitiveValue pv1 = te.get(m_keys[i]);
        PrimitiveValue pv2 = te.get(m_keys[i+1]);
        if (pv1 == null || pv2 == null)
        {
          continue;
        }
        float f1 = pv1.numberValue().floatValue();
        float f2 = pv2.numberValue().floatValue();
        if ((f1 - f2) / f1 > s_tolerance)
        {
          Explanation exp = new Explanation("The running time of " + m_trend 
              + " with a window width of " + m_keys[i+1] 
              + " is shorter than that for a window width of " + m_keys[i]
              + " for a trace length of " + te.get(StaticTrendDistanceStreamExperiment.LENGTH));
          exp.add(m_table);
          addExplanation(exp);
          out_res = Result.WARNING;
        }
      }
    }
    return out_res;
  }
}

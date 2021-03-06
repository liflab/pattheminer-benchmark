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
package pattheminer.trenddistance;

import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.macro.NumberMacro;
import ca.uqac.lif.mtnp.table.PrimitiveValue;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TempTable;
import pattheminer.trenddistance.selftd.SelfCorrelatedExperiment;
import pattheminer.trenddistance.statictd.StaticTrendDistanceStreamExperiment;

public class MaxSlowdownMacro extends NumberMacro
{
  protected transient Table m_table;
  
  public MaxSlowdownMacro(Laboratory lab, String name, int width, Table table)
  {
    super(lab, name, "Maximum slowdown (in percentage) of self-correlated trend distance vs. trend distance, for a window width of " + width);
    m_table = table;
  }

  @Override
  public Integer getNumber()
  {
    TempTable tt = m_table.getDataTable();
    double slowdown = 0;
    for (TableEntry te : tt.getEntries())
    {
      PrimitiveValue pv_t = te.get(StaticTrendDistanceStreamExperiment.TYPE_NAME);
      PrimitiveValue pv_s = te.get(SelfCorrelatedExperiment.TYPE_NAME);
      if (pv_t == null || pv_s == null)
      {
        continue;
      }
      float tp_t = pv_t.numberValue().floatValue();
      float tp_s = pv_s.numberValue().floatValue();
      if (tp_t == 0 || tp_s == 0)
      {
        continue;
      }
      double c_slowdown = (tp_t - tp_s) / tp_t;
      slowdown = Math.max(slowdown, c_slowdown);
    }
    return (int) (slowdown * 100);
  }
}

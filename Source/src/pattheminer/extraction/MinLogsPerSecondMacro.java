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
package pattheminer.extraction;

import java.util.Map;

import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.macro.NumberMacro;
import ca.uqac.lif.mtnp.DataFormatter;
import ca.uqac.lif.mtnp.table.PrimitiveValue;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TempTable;

public class MinLogsPerSecondMacro extends NumberMacro
{
  protected transient Table m_table;
  
  protected transient int m_line;
  
  public MinLogsPerSecondMacro(Laboratory lab, String name, int line, Table table)
  {
    super(lab, name, "Minimum number of logs per second processed with the K-means mining algorithm");
    m_table = table;
    m_line = line;
  }

  @Override
  public Double getNumber()
  {
    TempTable tt = m_table.getDataTable();
    double max_value = Double.MIN_VALUE;
    boolean found = false;
    for (TableEntry te : tt.getEntries())
    {
      if (te.get(MiningExperiment.NUM_LOGS).numberValue().intValue() != m_line)
      {
        continue;
      }
      for (Map.Entry<String,PrimitiveValue> me : te.entrySet())
      {
        if (me.getKey().compareTo(MiningExperiment.NUM_LOGS) == 0)
          continue;
        PrimitiveValue pv = me.getValue();
        if (pv != null && !pv.isNull())
        {
          max_value = Math.max(max_value, me.getValue().numberValue().doubleValue());
          found = true;
        }
      }
    }
    if (found && max_value > 0)
    {
      return DataFormatter.roundToSignificantFigures((double) m_line * 1000d / max_value, 2);
    }
    return 0d;
  }
}

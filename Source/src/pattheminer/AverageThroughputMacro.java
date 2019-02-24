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
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.DataFormatter;
import ca.uqac.lif.mtnp.table.TableEntry;
import pattheminer.trenddistance.TrendDistanceExperiment;

/**
 * Macro that computes the average throughput of a processor chain. This is
 * done by averaging the number of events processed per second of the longest
 * trace processed.
 */
public class AverageThroughputMacro extends NumberMacro
{
  protected transient ExperimentTable m_table;
  
  public AverageThroughputMacro(Laboratory lab, ExperimentTable table, String name, String description)
  {
    super(lab, name, "The average throughput for " + description + ", in Hz");
    m_table = table;
  }
  
  @Override
  public Double getNumber()
  {
    float max_x = 0, max_y = 0;
    for (TableEntry te : m_table.getDataTable().getEntries())
    {
      float x = te.get(TrendDistanceExperiment.LENGTH).numberValue().floatValue();
      float y = te.get(TrendDistanceExperiment.TIME).numberValue().floatValue();
      if (x > max_x)
      {
        max_x = x;
        max_y = y;
      }
    }
    if (max_x == 0)
    {
      return 0d;
    }
    return DataFormatter.roundToSignificantFigures(max_x / (max_y / 1000f), 3);
  }
}

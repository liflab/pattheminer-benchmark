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

import ca.uqac.lif.json.JsonElement;
import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.macro.MacroMap;
import java.util.Map;

/**
 * Computes various statistics over the execution of the lab
 */
public class OtherStats extends MacroMap
{

  /**
   * Instantiates the macro and defines its named data points
   * @param lab The lab from which to fetch the values
   */
  public OtherStats(MainLab lab)
  {
    super(lab);
    add("numevents", "The number of events in the randomly generated streams");
  }
  
  @Override
  public void computeValues(Map<String, JsonElement> map)
  {
    map.put("numevents", new JsonNumber(MainLab.MAX_TRACE_LENGTH - 1));
  }
}

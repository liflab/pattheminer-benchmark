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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.UniformProcessor;

public class MapToVector extends UniformProcessor
{
  List<Object> m_keys;

  public MapToVector(Object ... keys)
  {
    super(1, 1);
    m_keys = new ArrayList<Object>(keys.length);
    for (Object k : keys)
    {
      m_keys.add(k);
    }
  }

  @Override
  protected boolean compute(Object[] arg0, Object[] arg1)
  {
    @SuppressWarnings("unchecked")
    Map<Object,Object> map = (Map<Object,Object>) arg0[0];
    List<Number> vector = new ArrayList<Number>(m_keys.size());
    for (Object key : m_keys)
    {
      if (!map.containsKey(key))
      {
        vector.add(0);
      }
      else
      {
        vector.add((Number) map.get(key));
      }
    }
    arg1[0] = vector;
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    MapToVector mtv = new MapToVector();
    mtv.m_keys = new ArrayList<Object>(m_keys.size());
    mtv.m_keys.addAll(m_keys);
    return mtv;
  }

}

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
package pattheminer.forecast;

import ca.uqac.lif.labpal.Random;
import java.util.ArrayList;
import java.util.Enumeration;
import pattheminer.source.RandomSource;
import weka.core.Attribute;

public class RandomArraySource extends RandomSource<Object[]>
{
  /**
   * The number of attributes
   */
  protected int m_numAttributes;
  
  protected ArrayList<Object> m_classValues;
  
  protected Attribute m_classAttribute;
  
  public RandomArraySource(Random r, int num_events, int num_attributes, Attribute class_att)
  {
    super(r, num_events);
    m_classValues = new ArrayList<Object>();
    m_numAttributes = num_attributes;
    m_classAttribute = class_att;
    Enumeration<?> en = class_att.enumerateValues();
    while (en.hasMoreElements())
    {
      m_classValues.add(en.nextElement());
    }
  }
  
  @Override
  protected Object[] getEvent()
  {
    Object[] out = new Object[m_numAttributes + 1];
    for (int i = 0; i < m_numAttributes; i++)
    {
      out[i] = m_random.nextInt(100);
    }
    out[m_numAttributes] = m_classValues.get(m_random.nextInt(m_classValues.size()));
    return out;
  }

  @Override
  public RandomArraySource duplicate(boolean with_state)
  {
    return new RandomArraySource(m_random, m_numEvents, m_numAttributes, m_classAttribute);
  }

  @Override
  public Object[] readEvent(String line)
  {
    String[] parts = line.split(",");
    return parts;
  }

  @Override
  public String printEvent(Object[] e)
  {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < e.length; i++)
    {
      if (i > 0)
      {
        out.append(",");
      }
      out.append(e[i]);
    }
    return out.toString();
  }

  @Override
  public String getFilename()
  {
    return "array-" + m_numEvents + "-" + m_numAttributes + ".csv";
  }
}

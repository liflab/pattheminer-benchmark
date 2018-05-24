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

import java.util.Queue;

import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.labpal.Random;

/**
 * A source of randomly-generated events.
 */
public abstract class RandomSource extends Source
{
  /**
   * The random number generator used to generate the numbers
   */
  protected /*@ non_null @*/ Random m_random;
  
  /**
   * The number of events to produce
   */
  protected int m_numEvents;
  
  /**
   * A counter keeping track of the number of events produced so far
   */
  protected int m_eventCount;
  
  public RandomSource(Random r, int num_events)
  {
    super(1);
    m_random = r;
    m_numEvents = num_events;
    m_eventCount = 0;
  }
  
  @Override
  public void reset()
  {
    m_eventCount = 0;
  }
  
  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    outputs.add(new Object[]{getEvent()});
    m_eventCount++;
    return m_eventCount <= m_numEvents;
  }
  
  /**
   * Gets the total number of events to produce
   * @return The number of events
   */
  public int getEventBound()
  {
    return m_numEvents;
  }

  protected abstract Object getEvent();
}

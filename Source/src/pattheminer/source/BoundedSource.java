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
package pattheminer.source;

import ca.uqac.lif.cep.tmf.Source;
import java.util.Queue;

/**
 * A BeepBeep {@link Source} that produces a fixed number of events.
 */
public abstract class BoundedSource<T> extends Source
{
  /**
   * The number of events to produce
   */
  protected int m_numEvents;
  
  /**
   * A counter keeping track of the number of events produced so far
   */
  protected int m_eventCount;
  
  /**
   * Creates a new bounded source
   * @param num_events
   */
  public BoundedSource(int num_events)
  {
    super(1);
    m_numEvents = num_events;
    m_eventCount = 0;
  }
  
  /**
   * Gets the total number of events to produce
   * @return The number of events
   */
  public int getEventBound()
  {
    return m_numEvents;
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
   * Generates the next event
   * @return The next event
   */
  protected abstract T getEvent();
  
  /**
   * Reads an event from a line of text
   * @param line The line of text
   * @return The event, or <tt>null</tt> if no event can be produced
   * from the line
   */
  /*@ null @*/ public abstract T readEvent(/*@ non_null @*/ String line);
  
  /**
   * Prints an event to a line of text
   * @param e The event
   * @return The line of text
   */
  public abstract String printEvent(T e);
}
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

import ca.uqac.lif.labpal.Random;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * A source that produces tuples made of a weekday and some random
 * integer.
 */
public class WeekdaySource extends RandomSource
{
  /**
   * An array storing the names of each weekday
   */
  protected static final transient String[] s_weekdays = new String[] {
      "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
  };
  
  /**
   * The number of events to produce before moving to the next weekday
   */
  protected int m_weekdayLength;
  
  /**
   * An index pointing to an element of <tt>s_weekdays</tt>
   */
  protected int m_weekdayIndex;
  
  /**
   * Creates a new weekday source 
   * @param r A random generator
   * @param num_events The number of events to produce
   * @param weekday_length The number of events to produce before moving to
   * the next weekday
   */
  public WeekdaySource(Random r, int num_events, int weekday_length)
  {
    super(r, num_events);
    m_weekdayLength = weekday_length;
    m_weekdayIndex = 0;
  }
  
  @Override
  protected Object getEvent()
  {
    Object[] out = new Object[] {s_weekdays[m_weekdayIndex], m_random.nextInt(1000)};
    if ((m_eventCount + 1) % m_weekdayLength == 0)
    {
      m_weekdayIndex = (m_weekdayIndex + 1) % 7;
    }
    return out;
  }

  @Override
  public WeekdaySource duplicate(boolean with_state)
  {
    WeekdaySource ws = new WeekdaySource(m_random, m_numEvents, m_weekdayLength);
    if (with_state)
    {
      ws.m_eventCount = m_eventCount;
      ws.m_weekdayIndex = m_weekdayIndex;
    }
    return ws;
  }

  @Override
  public void reset()
  {
    super.reset();
    m_weekdayIndex = 0;
  }
  
  /**
   * Function that checks if a string represents a weekday. A weekday
   * is any string that is not "Saturday" or "Sunday".
   */
  public static class IsWeekday extends UnaryFunction<String,Boolean>
  {
    /**
     * A single instance of the function
     */
    public static final transient IsWeekday instance = new IsWeekday();
    
    /**
     * Hidden constructor
     */
    IsWeekday()
    {
      super(String.class, Boolean.class);
    }
    
    @Override
    public Boolean getValue(String x)
    {
      return x.compareTo(s_weekdays[0]) != 0 && x.compareTo(s_weekdays[6]) != 0;
    }
    
  }
}

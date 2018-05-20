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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.labpal.Random;

/**
 * A source of randomly generated symbols.
 */
public class RandomSymbolSource extends RandomSource
{
  /**
   * The number of distinct symbols to choose from
   */
  protected int m_numDistinct = 3;
  
  protected String[] m_symbols;
  
  /**
   * Creates a new random number source.
   * @param r The random number generator used to generate the numbers
   * @param num_events The number of events to produce
   * @param num_symbols The number of distinct symbols to choose from 
   */
  public RandomSymbolSource(Random r, int num_events, int num_symbols)
  {
    super(r, num_events);
    m_numDistinct = num_symbols;
    m_symbols = fillSymbols(m_numDistinct);
  }
  
  /**
   * Creates a new random number source.
   * @param r The random number generator used to generate the numbers
   * @param num_events The number of events to produce
   */
  public RandomSymbolSource(Random r, int num_events)
  {
    this(r, num_events, 3);
  }
  
  @Override
  protected String getEvent()
  {
    int pos = m_random.nextInt(m_numDistinct);
    return m_symbols[pos];
  }
  
  protected static String[] fillSymbols(int n)
  {
    String[] symbs = new String[n];
    for (int i = 0; i < n; i++)
    {
      symbs[i] = Integer.toString(i);
    }
    return symbs;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    // Not supported
    return null;
  }  
}

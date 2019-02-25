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
package pattheminer.patterns;

import ca.uqac.lif.cep.Connector;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.util.Sets;
import ca.uqac.lif.structures.MathLists;

/**
 * Accumulates events into a sequence of N-grams
 */
public class Ngrams extends GroupProcessor
{
  /**
   * The width of the N-gram
   */
  protected int m_N;
  
  Ngrams()
  {
    super(1, 1);
  }
  
  /**
   * Creates a new N-gram processor
   * @param N The width of the N-gram
   */
  public Ngrams(int N)
  {
    super(1, 1);
    m_N = N;
    MathLists.PutInto n_gram_maker = new MathLists.PutInto();
    Window n_gram_win = new Window(n_gram_maker, N);
    Sets.PutIntoNew accumulate_n_grams = new Sets.PutIntoNew();
    Connector.connect(n_gram_win, accumulate_n_grams);
    associateInput(INPUT, n_gram_win, INPUT);
    associateOutput(OUTPUT, accumulate_n_grams, OUTPUT);
    addProcessors(n_gram_win, accumulate_n_grams);
  }
  
  @Override
  public Ngrams duplicate(boolean with_state)
  {
    if (with_state)
    {
      throw new UnsupportedOperationException("This processor cannot be cloned with state");
    }
    return new Ngrams(m_N);
  }
}

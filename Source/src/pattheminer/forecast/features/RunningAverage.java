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
package pattheminer.forecast.features;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Computes the running average of a stream of numbers
 */
public class RunningAverage extends GroupProcessor
{
  /**
   * A name given to this processor chain
   */
  public static final transient String NAME = "Running average";
  
  public RunningAverage()  
  {
    super(1, 1);
    Fork f = new Fork(2);
    Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(f, 0, sum, 0);
    TurnInto one = new TurnInto(1);
    Connector.connect(f, 1, one, 0);
    Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(one, sum_one);
    ApplyFunction div = new ApplyFunction(Numbers.division);
    Connector.connect(sum, 0, div, 0);
    Connector.connect(sum_one, 0, div, 1);
    addProcessors(f, sum, one, sum_one, div);
    associateInput(0, f, 0);
    associateOutput(0, div, 0);
  }

  public RunningAverage duplicate(boolean with_state)
  {
    return new RunningAverage();
  }
}

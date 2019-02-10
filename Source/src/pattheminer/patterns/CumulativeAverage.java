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
import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Numbers;

public class CumulativeAverage extends GroupProcessor
{
  public CumulativeAverage()
  {
    super(1, 1);
    Fork fork = new Fork(2);
    associateInput(INPUT, fork, INPUT);
    Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(fork, TOP, sum, INPUT);
    TurnInto one = new TurnInto(1);
    Connector.connect(fork, BOTTOM, one, INPUT);
    Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(one, sum_one);
    ApplyFunction div = new ApplyFunction(Numbers.division);
    Connector.connect(sum, OUTPUT, div, TOP);
    Connector.connect(sum_one, OUTPUT, div, BOTTOM);
    associateOutput(OUTPUT, div, OUTPUT);
    addProcessors(fork, sum, one, sum_one, div);
  }
  
  @Override
  public CumulativeAverage duplicate(boolean with_state)
  {
    return new CumulativeAverage();
  }
}

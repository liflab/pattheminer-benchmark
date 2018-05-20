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
package pattheminer.patterns;

import ca.uqac.lif.cep.Connector;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.tmf.ReplaceWith;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Numbers;

public class SymbolDistribution extends Slice
{
  public SymbolDistribution()
  {
    super(new IdentityFunction(1), getCounter());
  }
  
  protected static GroupProcessor getCounter()
  {
    GroupProcessor counter = new GroupProcessor(1, 1);
    {
      ReplaceWith one = new ReplaceWith(1);
      counter.associateInput(INPUT, one, INPUT);
      Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
      Connector.connect(one, sum_one);
      counter.associateOutput(OUTPUT, sum_one, OUTPUT);
      counter.addProcessors(one, sum_one);
    }
    return counter;
  }
}

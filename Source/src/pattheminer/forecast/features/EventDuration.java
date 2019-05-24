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
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.tuples.ProjectTuple;
import ca.uqac.lif.cep.tuples.ProjectTuple.NameFunctionPair;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;

public class EventDuration extends GroupProcessor
{
  public EventDuration()
  {
    super(1, 1);
    Fork f = new Fork(2);
    ApplyFunction get_name = new ApplyFunction(new NthElement(2));
    Connector.connect(f, 0, get_name, 0);
    ApplyFunction get_ts = new ApplyFunction(new NthElement(1));
    Connector.connect(f, 1, get_ts, 0);
    Fork f2 = new Fork(2);
    Connector.connect(get_ts, f2);
    ApplyFunction minus = new ApplyFunction(Numbers.subtraction);
    Trim trim = new Trim(1);
    Connector.connect(f2, 0, trim, 0);
    Connector.connect(f2, 1, minus, 1);
    Connector.connect(trim, 0, minus, 0);
    ApplyFunction to_tuple = new ApplyFunction(new ProjectTuple(2, 
        new NameFunctionPair("e", StreamVariable.X), new NameFunctionPair("d", StreamVariable.Y)));
    Connector.connect(get_name, 0, to_tuple, 0);
    Connector.connect(minus, 0, to_tuple, 1);
    addProcessors(f, get_name, get_ts, f2, minus, trim, to_tuple);
    associateInput(0, f, 0);
    associateOutput(0, to_tuple, 0);
  }
}

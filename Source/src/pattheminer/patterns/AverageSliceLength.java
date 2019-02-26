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
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.KeepLast;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Lists;
import ca.uqac.lif.cep.util.Maps;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;
import pattheminer.source.RandomLabelSource;

import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;

public class AverageSliceLength extends GroupProcessor
{
  public AverageSliceLength()
  {
    super(1, 1);
    Slice slice = new Slice(new FunctionTree(Numbers.numberCast, new NthElement(0)), new SliceLength());
    KeepLast last = new KeepLast();
    Connector.connect(slice, last);
    ApplyFunction map_values = new ApplyFunction(Maps.values);
    Connector.connect(last, map_values);
    Lists.Unpack unpack = new Lists.Unpack();
    Connector.connect(map_values, unpack);
    CumulativeAverage avg = new CumulativeAverage();
    Connector.connect(unpack, avg);
    associateInput(INPUT, slice, INPUT);
    associateOutput(OUTPUT, avg, OUTPUT);
    addProcessors(slice, last, map_values, unpack, avg);
  }
  
  @Override
  public AverageSliceLength duplicate(boolean with_state)
  {
    if (with_state)
    {
      throw new UnsupportedOperationException("This processor cannot be cloned with state");
    }
    return new AverageSliceLength();
  }
  
  public static class SliceLength extends GroupProcessor
  {
    public SliceLength()
    {
      super(1, 1);
      Fork f = new Fork(2);
      ApplyFunction ts1 = new ApplyFunction(new FunctionTree(Numbers.numberCast, new NthElement(1)));
      ApplyFunction ts2 = new ApplyFunction(new FunctionTree(Numbers.numberCast, new NthElement(1)));
      {
        Fork ff = new Fork(2);
        Connector.connect(f, TOP, ff, INPUT);
        ApplyFunction eq = new ApplyFunction(new FunctionTree(Equals.instance,
            new FunctionTree(new NthElement(2), StreamVariable.X),
            new Constant(RandomLabelSource.LABEL_END)));
        Connector.connect(ff, BOTTOM, eq, INPUT);
        Filter filter = new Filter();
        Connector.connect(ff, TOP, filter, TOP);
        Connector.connect(eq, OUTPUT, filter, BOTTOM);
        Connector.connect(filter, ts1);
        addProcessors(ff, eq, filter);
      }
      {
        Fork ff = new Fork(2);
        Connector.connect(f, BOTTOM, ff, INPUT);
        ApplyFunction eq = new ApplyFunction(new FunctionTree(Equals.instance,
            new FunctionTree(new NthElement(2), StreamVariable.X),
            new Constant(RandomLabelSource.LABEL_START)));
        Connector.connect(ff, BOTTOM, eq, INPUT);
        Filter filter = new Filter();
        Connector.connect(ff, TOP, filter, TOP);
        Connector.connect(eq, OUTPUT, filter, BOTTOM);
        Connector.connect(filter, ts2);
        addProcessors(ff, eq, filter);
      }
      ApplyFunction minus = new ApplyFunction(Numbers.subtraction);
      Connector.connect(ts1, OUTPUT, minus, TOP);
      Connector.connect(ts2, OUTPUT, minus, BOTTOM);
      associateInput(INPUT, f, INPUT);
      associateOutput(OUTPUT, minus, OUTPUT);
      addProcessors(f, minus, ts1, ts2);
    }
    
    @Override
    public SliceLength duplicate(boolean with_state)
    {
      if (with_state)
      {
        throw new UnsupportedOperationException("This processor cannot be cloned with state");
      }
      return new SliceLength();
    }
  }
}

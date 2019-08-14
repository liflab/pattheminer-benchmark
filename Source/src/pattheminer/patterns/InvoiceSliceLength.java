/*
echo "<Log>\n";    A benchmark for Pat The Miner
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
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.KeepLast;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Lists;
import ca.uqac.lif.cep.util.Maps;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.xml.XPathFunctionGetNumber;
import ca.uqac.lif.cep.xml.XPathFunctionGetText;

import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Variant of {@link AverageSliceLength} that processes a "real
 * world" XML log of interleaved processes.
 */
public class InvoiceSliceLength extends GroupProcessor
{
  public InvoiceSliceLength()
  {
    super(1, 1);
    Slice slice = new Slice(new XPathFunctionGetNumber("Event/ProcessInstance/text()"), new SliceLength());
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
  public InvoiceSliceLength duplicate(boolean with_state)
  {
    if (with_state)
    {
      throw new UnsupportedOperationException("This processor cannot be cloned with state");
    }
    return new InvoiceSliceLength();
  }
  
  public static class SliceLength extends GroupProcessor
  {
    public SliceLength()
    {
      super(1, 1);
      
      Fork f = new Fork(2);
      ApplyFunction ts1 = new ApplyFunction(new FunctionTree(TimestampToSeconds.instance, new XPathFunctionGetText("Event/Timestamp/text()")));
      ApplyFunction ts2 = new ApplyFunction(new FunctionTree(TimestampToSeconds.instance, new XPathFunctionGetText("Event/Timestamp/text()")));
      {
        Fork ff = new Fork(2);
        Connector.connect(f, TOP, ff, INPUT);
        ApplyFunction eq = new ApplyFunction(new FunctionTree(Equals.instance,
            new FunctionTree(new XPathFunctionGetText("Event/WorkflowModelElement/text()"), StreamVariable.X),
            new Constant("Register")));
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
            new FunctionTree(new XPathFunctionGetText("Event/WorkflowModelElement/text()"), StreamVariable.X),
            new Constant("End")));
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
  
  /**
   * Converts a timestamp into an number of secnods
   */
  protected static class TimestampToSeconds extends UnaryFunction<String,Number>
  {
    public static final transient TimestampToSeconds instance = new TimestampToSeconds();
    
    protected static final transient SimpleDateFormat s_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); 
    
    protected TimestampToSeconds()
    {
      super(String.class, Number.class);
    }

    @Override
    public Number getValue(String ts) throws FunctionException
    {
      if (ts.length() != 29)
      {
        return -1;
      }
      ts = ts.substring(0, 22);
      Date d;
      try
      {
        d = s_dateFormat.parse(ts);
      }
      catch (ParseException e)
      {
        throw new FunctionException(e);
      }
      return d.getTime();
    }
  }
}

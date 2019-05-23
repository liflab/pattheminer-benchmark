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

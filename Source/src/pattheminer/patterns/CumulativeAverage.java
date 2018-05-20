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
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.ReplaceWith;
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
    ReplaceWith one = new ReplaceWith(1);
    Connector.connect(fork, BOTTOM, one, INPUT);
    Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(one, sum_one);
    ApplyFunction div = new ApplyFunction(Numbers.division);
    Connector.connect(sum, OUTPUT, div, TOP);
    Connector.connect(sum_one, OUTPUT, div, BOTTOM);
    associateOutput(OUTPUT, div, OUTPUT);
    addProcessors(fork, sum, one, sum_one, div);
  }
}

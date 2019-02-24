package pattheminer.secondorder;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IfThenElse;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.util.Lists;
import ca.uqac.lif.cep.util.Numbers;

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;


/**
 * Processor which, given input arrays of Boolean values, returns the
 * cumulative sum of the number of elements that are true.
 */
public class TrueCount extends GroupProcessor
{
  public TrueCount()
  {
    super(1, 1);
    Lists.Unpack unpack = new Lists.Unpack();
    ApplyFunction ite = new ApplyFunction(new FunctionTree(IfThenElse.instance, StreamVariable.X, Constant.ONE, Constant.ZERO));
    Connector.connect(unpack, ite);
    Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(ite, sum);
    addProcessors(unpack, ite, sum);
    associateInput(INPUT, unpack, INPUT);
    associateOutput(OUTPUT, sum, OUTPUT);
  }
}

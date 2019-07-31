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
package pattheminer.trenddistance.context;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.BinaryFunction;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.peg.ChoiceFunction;
import ca.uqac.lif.cep.peg.ContextRef;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.json.JsonString;
import ca.uqac.lif.labpal.Region;
import java.util.HashMap;
import java.util.Map;
import pattheminer.MainLab;
import pattheminer.patterns.CumulativeAverage;
import pattheminer.source.BoundedSource;
import pattheminer.source.WeekdaySource;
import pattheminer.trenddistance.TraceExperimentFactory;
import pattheminer.trenddistance.statictd.StaticTrendDistanceExperiment;

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static pattheminer.trenddistance.TrendExperiment.TREND;
import static pattheminer.trenddistance.TrendExperiment.WIDTH;
import static pattheminer.trenddistance.context.ContextualExperiment.CONTEXT_PROCESSOR;
import static pattheminer.trenddistance.context.ContextualExperiment.CONTEXT_WIDTH;
import static pattheminer.trenddistance.context.ContextualExperiment.WEEKDAYS;

/**
 * Factory that generates contextual trend distance experiments using various
 * input sources and trend/context processors.
 */
public class ContextualExperimentFactory extends TraceExperimentFactory<ContextualExperiment>
{
  public ContextualExperimentFactory(MainLab lab)
  {
    super(lab, ContextualExperiment.class);
  }

  @Override
  protected ContextualExperiment createExperiment(Region r)
  {
    String context_name = r.getString(CONTEXT_PROCESSOR);
    if (context_name.compareTo(WEEKDAYS) == 0)
    {
      return createWeekdayExperiment(r);
    }
    return null;
  }
  
  /**
   * Creates an experiment that compares an average with a trend based on
   * the weekday.
   * @param r The region used to fetch the experiment's parameters
   * @return The experiment
   */
  protected ContextualExperiment createWeekdayExperiment(Region r)
  {
    String trend_name = r.getString(TREND);
    GroupProcessor beta = new GroupProcessor(1, 1);
    {
      Processor get = new ApplyFunction(new NthElement(1));
      Processor avg = new CumulativeAverage();
      Connector.connect(get, avg);
      beta.associateInput(INPUT, get, INPUT);
      beta.associateOutput(OUTPUT, get, OUTPUT);
      beta.addProcessors(get, avg);
    }
    
    Processor context = new ApplyFunction(new FunctionTree(WeekdaySource.IsWeekday.instance, new NthElement(0)));
    BoundedSource<?> source = new WeekdaySource(m_lab.getRandom(), MainLab.MAX_TRACE_LENGTH, 20);
    Map<Object,Object> trends = new HashMap<Object,Object>();
    trends.put(true, 100);
    trends.put(false, 10);
    Function distance = Numbers.subtraction;
    Number threshold = 0;
    BinaryFunction<Number,Number,Boolean> comparison = Numbers.isLessThan;
    ContextualExperiment ce = createNewContextualExperiment(trend_name, "Subtraction", source, beta, r.getInt(WIDTH), context, 1, trends, distance, threshold, comparison, false);
    ce.setInput(CONTEXT_PROCESSOR, WEEKDAYS);
    return ce;
  }
  
  @SuppressWarnings("unchecked")
  protected ContextualExperiment createNewContextualExperiment(String trend_name, String metric, BoundedSource<?> source, Processor beta, int n, Processor context, int m, Map<?,?> trends, Function distance, Number threshold, BinaryFunction<Number,Number,Boolean> comparison, boolean multi_thread)
  {
    ContextualExperiment tde = new ContextualExperiment();
    ChoiceFunction<String,Number> cf = new ChoiceFunction<String,Number>(String.class, Number.class, (Map<String,Number>) trends);
    ContextRef<String,Number> alarm = new ContextRef<String,Number>(beta, n, context, m, cf, distance, threshold, comparison);
    tde.setSource(source);
    tde.setProcessor(alarm);
    tde.setEventStep(MainLab.s_eventStep);
    JsonString jb = new JsonString("yes");
    if (!multi_thread)
    {
      jb = new JsonString("no");
    }
    tde.setInput(StaticTrendDistanceExperiment.MULTITHREAD, jb);
    tde.setInput(TREND, trend_name);
    tde.setInput(WIDTH, n);
    tde.setInput(CONTEXT_WIDTH, m);
    tde.setInput(ContextualExperiment.METRIC, metric);
    return tde;
  }
}

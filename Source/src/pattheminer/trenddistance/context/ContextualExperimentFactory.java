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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.labpal.Region;
import java.util.HashMap;
import java.util.Map;
import pattheminer.MainLab;
import pattheminer.source.WeekdaySource;
import pattheminer.trenddistance.TrendFactory;

import static pattheminer.trenddistance.TrendExperiment.TREND;
import static pattheminer.trenddistance.TrendExperiment.WIDTH;
import static pattheminer.trenddistance.context.ContextualExperiment.CONTEXT_PROCESSOR;
import static pattheminer.trenddistance.context.ContextualExperiment.CONTEXT_WIDTH;
import static pattheminer.trenddistance.context.ContextualExperiment.WEEKDAYS;

/**
 * Factory that generates contextual trend distance experiments using various
 * input sources and trend/context processors.
 */
public class ContextualExperimentFactory extends TrendFactory<ContextualExperiment>
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
    Processor beta = null;
    Processor context = null;
    Processor source = new WeekdaySource(m_lab.getRandom(), MainLab.MAX_TRACE_LENGTH, 20);
    Function choice = WeekdaySource.IsWeekday.instance;
    Map<Object,Object> trends = new HashMap<Object,Object>();
    trends.put(true, 100);
    trends.put(false, 10);
    ContextualExperiment ce = createNewContextualExperiment(trend_name, source, beta, r.getInt(WIDTH), context, 1, choice, trends);
    ce.setInput(CONTEXT_PROCESSOR, WEEKDAYS);
    return ce;
  }
  
  protected ContextualExperiment createNewContextualExperiment(String trend_name, Processor source, Processor beta, int n, Processor context, int m, Function choice, Map<?,?> trends)
  {
    ContextualExperiment ce = new ContextualExperiment();
    ce.setInput(TREND, trend_name);
    ce.setInput(WIDTH, n);
    ce.setInput(CONTEXT_WIDTH, m);
    return ce;
  }
}

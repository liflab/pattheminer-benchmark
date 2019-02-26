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

import pattheminer.trenddistance.TrendExperiment;

public class ContextualExperiment extends TrendExperiment
{
  /**
   * The name of this stream pattern
   */
  public static final transient String TYPE_NAME = "Contextual distance";
  
  /**
   * The name of the parameter "context window width"
   */
  public static final transient String CONTEXT_WIDTH = "Context window width";
  
  /**
   * The name of the parameter "context processor"
   */
  public static final transient String CONTEXT_PROCESSOR = "Context processor";
  
  /**
   * The weekdays vs. weekends context
   */
  public static final transient String WEEKDAYS = "Weekdays vs. weekends"; 
  
  /**
   * Creates an empty contextual experiment
   */
  public ContextualExperiment()
  {
    super();
    setDescription("Measures the throughput of the contextual distance pattern");
    setInput(TYPE, TYPE_NAME);
    describe(CONTEXT_WIDTH, "The width of the window for the context processor");
    describe(CONTEXT_PROCESSOR, "The processor used to calculate the context");
  }
}

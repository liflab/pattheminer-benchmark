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
package pattheminer;

import ca.uqac.lif.labpal.Experiment;
import pattheminer.source.BoundedSource;

public abstract class TraceExperiment extends Experiment
{
  /**
   * The average number of events processed per second
   */
  public static final transient String THROUGHPUT = "Throughput";
  
  /**
   * The software used to process the trace
   */
  public static final transient String SOFTWARE = "Software";
  
  /**
   * The source from which the input events will originate
   */
  protected transient BoundedSource<?> m_source;
  
  public TraceExperiment()
  {
    super();
    describe(THROUGHPUT, "The average number of events processed per second");
    describe(SOFTWARE, "The software used to process the trace");
  }
  
  /**
   * Sets the source from which the input events will originate
   * @param s The source
   */
  public void setSource(BoundedSource<?> s)
  {
    m_source = s;
  }
  
  @Override
  public boolean prerequisitesFulfilled()
  {
    return m_source.isReady();
  }
  
  @Override
  public void fulfillPrerequisites()
  {
    m_source.prepare();
  }
  
  @Override
  public void cleanPrerequisites()
  {
    m_source.clear();
  }
}

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
package pattheminer.extraction;

import java.util.Set;
import ca.uqac.lif.cep.peg.Sequence;
import ca.uqac.lif.cep.peg.ml.ProcessorMiningFunction;
import ca.uqac.lif.labpal.Experiment;

public class MiningExperiment extends Experiment
{
  /**
   * Name of the algorithm used for mining
   */
  public static final transient String MINING_ALGORITHM = "Algorithm";
  
  /**
   * The trend computed on each log
   */
  public static final transient String TREND = "Trend";
  
  /**
   * The number of logs on which the mining is done
   */
  public static final transient String NUM_LOGS = "Number of logs";
  
  /**
   * The length of each log
   */
  public static final transient String LOG_LENGTH = "Log length";
  
  /**
   * The duration of the mining process
   */
  public static final transient String DURATION = "Duration (ms)";
  
  /**
   * The function doing the mining over a set of logs
   */
  protected transient ProcessorMiningFunction<?,?> m_miningFunction;
  
  /**
   * A generator of sets of sequences
   */
  protected transient SequenceSetGenerator m_sequenceSetGenerator;
  
  public MiningExperiment()
  {
    super();
    describe(MINING_ALGORITHM, "Name of the algorithm used for mining (the alpha-function)");
    describe(NUM_LOGS, "The number of logs on which the mining is done");
    describe(LOG_LENGTH, "The length of each log");
    describe(TREND, "The trend computed on each log");
  }
  
  public void setMiningFunction(/*@ non_null @*/ ProcessorMiningFunction<?,?> fct)
  {
    m_miningFunction = fct;
  }
  
  public void setSequenceSetGenerator(SequenceSetGenerator gen)
  {
    m_sequenceSetGenerator = gen;
  }
  
  @Override
  public void execute()
  {
    Set<Sequence<?>> sequences = m_sequenceSetGenerator.generateSequences();
    long time_start = System.currentTimeMillis();
    m_miningFunction.mine(sequences);
    long time_end = System.currentTimeMillis();
    write(DURATION, time_end - time_start);
  }
}

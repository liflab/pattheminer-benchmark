/*
    A benchmark for Pat The Miner
    Copyright (C) 2018 Laboratoire d'informatique formelle

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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.peg.ml.ProcessorMiningFunction;
import pattheminer.patterns.CumulativeAverage;

public class WeightedAverageExperiment extends MiningExperiment
{
  public WeightedAverageExperiment(int num_logs, int log_length)
  {
    super();
    setInput(TREND, "Average");
    setInput(MINING_ALGORITHM, "Weighted average");
    RandomNumberSource src = new RandomNumberSource(getRandom(), log_length);
    m_sequenceSetGenerator = new SequenceSetGenerator(src, num_logs);
    Processor beta = new CumulativeAverage();
    //m_miningFunction = new ProcessorMiningFunction<Object,ArrayList<?>>(beta, new ApplyFunction(new KMeansFunction(2)));
    // TODO
  }
}

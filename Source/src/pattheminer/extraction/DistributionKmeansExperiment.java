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

import java.util.ArrayList;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.peg.ml.KMeansFunction;
import ca.uqac.lif.cep.peg.ml.ProcessorMiningFunction;
import ca.uqac.lif.labpal.Random;
import pattheminer.MapToVector;
import pattheminer.patterns.SymbolDistribution;
import pattheminer.source.RandomSymbolSource;

public class DistributionKmeansExperiment extends MiningExperiment
{
  protected static final transient String K = "K"; 
  
  protected DistributionKmeansExperiment()
  {
    super();
  }
  
  public DistributionKmeansExperiment(/*@ non_null @*/ Random r, int num_logs, int log_length)
  {
    super();
    describe(K, "The value of K in the algorithm");
    setInput(TREND, "Symbol distribution");
    setInput(MINING_ALGORITHM, "K-means");
    setInput(K, 2);
    setInput(LOG_LENGTH, log_length);
    setInput(NUM_LOGS, num_logs);
    RandomSymbolSource src = new RandomSymbolSource(r, log_length, 5);
    m_sequenceSetGenerator = new SequenceSetGenerator(src, num_logs);
    GroupProcessor beta = new GroupProcessor(1, 1);
    {
      SymbolDistribution sd = new SymbolDistribution();
      MapToVector mtv = new MapToVector((Object[]) src.getSymbols());
      Connector.connect(sd, mtv);
      beta.associateInput(0, sd, 0);
      beta.associateOutput(0, mtv, 0);
      beta.addProcessors(sd, mtv);
    }
    m_miningFunction = new ProcessorMiningFunction<Object,ArrayList<?>>(beta, new ApplyFunction(new KMeansFunction(2)));
  }
}

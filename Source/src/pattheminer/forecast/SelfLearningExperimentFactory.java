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
package pattheminer.forecast;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.labpal.Region;
import pattheminer.MainLab;

/**
 * Experiment factory for the self-prediction workflow pattern
 */
public class SelfLearningExperimentFactory extends LearningExperimentFactory<SelfLearningExperiment>
{
  SelfLearningExperimentFactory(/*@ non_null @*/ MainLab lab)
  {
    super(lab, SelfLearningExperiment.class);
  }
  
  @Override
  protected SelfLearningExperiment getExperiment(Region r, Processor update_classifier, Function slice_f, Processor phi, Processor kappa, int t, int m, int n)
  {
    return new SelfLearningExperiment(update_classifier, slice_f, phi, kappa, t, m, n);
  }
}

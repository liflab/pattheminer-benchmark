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
import ca.uqac.lif.cep.peg.forecast.PredictiveLearning;

/**
 * Experiment that trains a classifier by comparing two windows of the same
 * stream.
 */
public class PredictiveLearningExperiment extends LearningExperiment
{ 

  /**
   * The instance of the {@link PredictiveLearning} processor used to train
   * the classifier.
   */
  protected transient PredictiveLearning m_predictiveLearning;

  /**
   * Creates a new empty prediction experiment. You should not call this
   * constructor directly.
   */
  PredictiveLearningExperiment()
  {
    // Do nothing
  }

  /**
   * Creates a new prediction experiment
   */
  public PredictiveLearningExperiment(Processor update_classifier, Function slice_f, Processor beta, Processor kappa, int t, int m, int n)
  {
    super(t, m, n);
    setDescription("Experiment that trains a classifier by comparing two windows of the same stream.");
    setDescription("Experiment that trains a classifier on past windows of a stream, and uses this classifier to compute a prediction on the current window.");
    m_predictiveLearning = new PredictiveLearning(slice_f, beta, m, t, kappa, n, update_classifier);
    setProcessor(m_predictiveLearning);
  }
}

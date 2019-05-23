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
import weka.classifiers.Classifier;
import weka.core.Attribute;

/**
 * Experiment that trains a classifier by comparing two windows of the same
 * stream.
 */
public class PredictiveLearningExperiment extends ClassifierExperiment
{
  
  /**
   * The offset between the "trend" and the "class" windows
   */
  public static final transient String T = "t";
  
  /**
   * Width of the feature window
   */
  public static final transient String M = "m";
  
  /**
   * Width of the class window
   */
  public static final transient String N = "n";
  
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
  public PredictiveLearningExperiment(String learning_algorithm, Classifier c, int update_interval, int roll_width, Function slice_f, Processor beta, Processor kappa, int t, int m, int n, Attribute ... attributes)
  {
    super(learning_algorithm, c, update_interval, roll_width, attributes);
    setDescription("Experiment that trains a classifier by comparing two windows of the same stream.");
    describe(T, "Offset (in number of events) between the trend and the class windows");
    describe(M, "Width of the trend window");
    describe(N, "Width of the class window");
    setInput(T, t);
    setInput(M, m);
    setInput(N, n);
    m_predictiveLearning = new PredictiveLearning(slice_f, beta, m, t, kappa, n, m_updateClassifier);
    setProcessor(m_predictiveLearning);
  }
}

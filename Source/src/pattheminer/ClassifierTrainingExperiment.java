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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.peg.weka.ClassifierTraining;
import weka.classifiers.Classifier;
import weka.core.Attribute;

/**
 * Experiment that trains a classifier by comparing two windows of the same
 * stream.
 */
public class ClassifierTrainingExperiment extends ClassifierExperiment
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
   * The instance of the {@link ClassifierTraining} processor used to train
   * the classifier.
   */
  protected transient ClassifierTraining m_classifierTraining;
    
  /**
   * Creates a new empty prediction experiment. You should not call this
   * constructor directly.
   */
  ClassifierTrainingExperiment()
  {
    // Do nothing
  }
  
  /**
   * Creates a new prediction experiment
   */
  public ClassifierTrainingExperiment(String learning_algorithm, Classifier c, int update_interval, Processor beta, Processor kappa, int t, int m, int n, Attribute ... attributes)
  {
    super(learning_algorithm, c, update_interval, attributes);
    setDescription("Experiment that trains a classifier by comparing two windows of the same stream.");
    describe(T, "Offset (in number of events) between the trend and the class windows");
    describe(M, "Width of the trend window");
    describe(N, "Width of the class window");
    setInput(T, t);
    setInput(M, m);
    setInput(N, n);
    m_classifierTraining = new ClassifierTraining(beta, kappa, m_updateClassifier, t, m, n);
    setProcessor(m_classifierTraining);
  }
}

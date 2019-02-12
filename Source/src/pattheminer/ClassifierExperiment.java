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

import ca.uqac.lif.cep.peg.weka.UpdateClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;

/**
 * Experiment that takes an input stream and uses it to update a classifier.
 */
public abstract class ClassifierExperiment extends StreamExperiment
{
  /**
   * Name of the learning algorithm used for mining
   */
  public static final transient String LEARNING_ALGORITHM = "Algorithm";
  
  /**
   * Number of features computed from the window
   */
  public static final transient String NUM_FEATURES = "Number of features";
  
  /**
   * Interval at which the learning algorithm is updated
   */
  public static final transient String UPDATE_INTERVAL = "Update interval";
  
  /**
   * Number of classes in which a window can be classified
   */
  public static final transient String NUM_CLASSES = "Number of classes";
  
  /**
   * Width of the circular buffer holding the instances to learn
   */
  public static final transient String ROLL_WIDTH = "Roll width";

  /**
   * The classifier used in the prediction experiment
   */
  protected transient Classifier m_classifier;
  
  /**
   * The attributes used by the classifier
   */
  protected transient Attribute[] m_attributes;
  
  /**
   * A dummy name given to the dataset
   */
  protected transient String m_datasetName;
  
  /**
   * The processor used to update the classifier
   */
  protected transient UpdateClassifier m_updateClassifier;

  ClassifierExperiment()
  {
    super();
    m_datasetName = "LabPal"; // dummy name
    describe(LEARNING_ALGORITHM, "Name of the learning algorithm used for mining");
    describe(NUM_FEATURES, "Number of features computed from the window (i.e. number of dimensions of the learning problem)");
    describe(UPDATE_INTERVAL, "Interval at which the learning algorithm is updated");
    describe(NUM_CLASSES, "Number of classes in which a window can be classified");
    describe(ROLL_WIDTH, "Width of the circular buffer holding the instances to learn");
  }
  
  public ClassifierExperiment(String learning_algorithm, Classifier c, int update_interval, int roll_width, Attribute ... attributes)
  {
    this();
    m_classifier = c;
    m_attributes = attributes;
    setInput(LEARNING_ALGORITHM, learning_algorithm);
    setInput(NUM_FEATURES, attributes.length - 1);
    setInput(UPDATE_INTERVAL, update_interval);
    setInput(NUM_CLASSES, attributes[attributes.length - 1].numValues());
    setInput(ROLL_WIDTH, roll_width);
    m_updateClassifier = new UpdateClassifier(c, update_interval, roll_width, m_datasetName, attributes);
  }
}

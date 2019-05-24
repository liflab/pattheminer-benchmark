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

import static pattheminer.forecast.PredictionExperiment.F;
import static pattheminer.forecast.PredictionExperiment.M;
import static pattheminer.forecast.PredictionExperiment.NUM_SLICES;
import static pattheminer.forecast.PredictionExperiment.PHI;
import static pattheminer.forecast.PredictiveLearningExperiment.PATTERN;
import static pattheminer.forecast.PredictiveLearningExperiment.PATTERN_AVG_DURATION;
import static pattheminer.forecast.PredictiveLearningExperiment.PATTERN_NEXT_EVENT;
import static pattheminer.forecast.PredictiveLearningExperiment.NUM_LABELS;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.RaiseArity;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.peg.weka.UpdateClassifier;
import ca.uqac.lif.cep.peg.weka.WekaUtils;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Lists;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.structures.MathLists;
import pattheminer.MainLab;
import pattheminer.forecast.features.EventDuration;
import pattheminer.forecast.features.RunningAverage;
import pattheminer.patterns.ExtractAttributes;
import pattheminer.patterns.Ngrams;
import pattheminer.source.BoundedSource;
import pattheminer.source.RandomLabelSource;
import pattheminer.source.RandomNumberSource;
import weka.core.Attribute;

public class PredictiveLearningExperimentFactory extends ExperimentFactory<MainLab,PredictiveLearningExperiment>
{
  /**
   * Creates a new factory
   * @param lab The lab this factory is associated to
   */
  public PredictiveLearningExperimentFactory(MainLab lab)
  {
    super(lab, PredictiveLearningExperiment.class);
  }
  
  @Override
  protected PredictiveLearningExperiment createExperiment(Region r)
  {
    String prediction = r.getString(PATTERN);
    PredictiveLearningExperiment exp = null;
    if (prediction == null)
    {
      return null;
    }
    if (prediction.compareTo(PATTERN_AVG_DURATION) == 0)
    {
      exp = setupAverageDuration(r);
    }
    if (prediction.compareTo(PATTERN_NEXT_EVENT) == 0)
    {
      exp = setupMostProbableNextEvent(r);
    }
    if (exp == null)
    {
      return null;
    }
    exp.setInput(PATTERN, prediction);
    exp.setEventStep(MainLab.s_eventStep);
    return exp;
    
  }

  protected PredictiveLearningExperiment setupAverageDuration(Region r)
  {
    RandomLabelSource source = new RandomLabelSource(m_lab.getRandom(), MainLab.MAX_TRACE_LENGTH, 1000, r.getInt(NUM_SLICES), r.getInt(NUM_LABELS));
    ApplyFunction phi = new ApplyFunction(new NthElement(2));
    GroupProcessor kappa = new GroupProcessor(1, 1);
    {
      ApplyFunction get_ts = new ApplyFunction(new NthElement(1));
      Fork fork = new Fork(2);
      Connector.connect(get_ts, fork);
      Trim trim = new Trim(1);
      Connector.connect(fork, 0, trim, 0);
      ApplyFunction minus = new ApplyFunction(Numbers.subtraction);
      Connector.connect(trim, 0, minus, 0);
      Connector.connect(fork, 1, minus, 1);
      kappa.addProcessors(get_ts, fork, trim, minus);
      kappa.associateInput(0, get_ts, 0);
      kappa.associateOutput(0, minus, 0);
    }
    Slice uc = new Slice(new NthElement(0), new RunningAverage());
    NthElement slice_f = new NthElement(0);
    PredictiveLearningExperiment cte = new PredictiveLearningExperiment(uc, slice_f, phi, kappa, 0, 2, 1);
    cte.setSource(source);
    cte.setInput(NUM_SLICES, r.getInt(NUM_SLICES));
    return cte;
  }
  
  protected PredictiveLearningExperiment setupWekaLearning(Region r)
  {
    int num_features = r.getInt(ClassifierExperiment.NUM_FEATURES);
    int num_classes = r.getInt(ClassifierExperiment.NUM_CLASSES);
    String algo_name = r.getString(ClassifierExperiment.LEARNING_ALGORITHM);
    int update_interval = r.getInt(ClassifierExperiment.UPDATE_INTERVAL);
    int roll_width = r.getInt(ClassifierExperiment.ROLL_WIDTH);
    Processor beta = new ExtractAttributes(num_features);
    Processor kappa = new ApplyFunction(new NthElement(num_features));
    Attribute[] atts = createDummyAttributes(num_features, num_classes);
    RaiseArity slice_f = new RaiseArity(1, new Constant(0));
    UpdateClassifier uc = new UpdateClassifier(WekaUtils.getClassifier(algo_name), update_interval, roll_width, "test", atts);
    PredictiveLearningExperiment cte = new PredictiveLearningExperiment(uc, slice_f, beta, kappa, 1, 1, 1);
    cte.setInput(ClassifierExperiment.LEARNING_ALGORITHM, algo_name);
    cte.setSource(new RandomArraySource(m_lab.getRandom(), MainLab.MAX_TRACE_LENGTH, num_features, atts[num_features]));
    return cte;
  }
  
  /*@ non_null @*/ protected PredictiveLearningExperiment setupMostProbableNextEvent(/*@ non_null @*/ Region r)
  {
    int m = r.getInt(M);
    RandomLabelSource source = new RandomLabelSource(m_lab.getRandom(), MainLab.MAX_TRACE_LENGTH, 1000, r.getInt(NUM_SLICES), r.getInt(NUM_LABELS));
    GroupProcessor phi = new GroupProcessor(1, 1);
    {
      ApplyFunction get_name = new ApplyFunction(new NthElement(2));
      MathLists.PutInto to_list = new MathLists.PutInto();
      Connector.connect(get_name, to_list);
      Fork f = new Fork(2);
      Connector.connect(to_list, f);
      TurnInto to_true = new TurnInto(true);
      Connector.connect(f, 1, to_true, 0);
      Lists.Pack pack = new Lists.Pack();
      Connector.connect(f, 0, pack, 0);
      Connector.connect(to_true, 0, pack, 1);
      phi.addProcessors(get_name, to_list, f, to_true, pack);
      phi.associateInput(0, get_name, 0);
      phi.associateOutput(0, pack, 0);
    }
    Processor kappa = new ApplyFunction(new NthElement(2));
    NthElement slice_f = new NthElement(0);
    GroupProcessor counter = new GroupProcessor(1, 1);
    {
      TurnInto one = new TurnInto(1);
      Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
      Connector.connect(one, sum);
      counter.addProcessors(one, sum);
      counter.associateInput(0, one, 0);
      counter.associateOutput(0, sum, 0);
    }
    Slice inner_slice = new Slice(new NthElement(1), counter);
    Slice uc = new Slice(new NthElement(0), inner_slice);
    PredictiveLearningExperiment cte = new PredictiveLearningExperiment(uc, slice_f, phi, kappa, m + 1, m, 1);
    cte.setSource(source);
    cte.setInput(NUM_SLICES, r.getInt(NUM_SLICES));
    cte.setInput(NUM_LABELS, r.getInt(NUM_LABELS));
    return cte;
  }
  
  /**
   * Creates an array of dummy numerical attributes.
   * @param num_attributes The number of attributes to create.
   * @return The array of attributes
   */
  /*@ requires num_attributes >= 1 @*/
  protected static Attribute[] createDummyAttributes(int num_attributes, int num_values)
  {
    Attribute[] atts = new Attribute[num_attributes + 1];
    for (int i = 0; i < num_attributes; i++)
    {
      atts[i] = new Attribute(Integer.toString(i));
    }
    String[] att_vals = new String[num_values];
    for (int i = 0; i < num_values; i++)
    {
      att_vals[i] = Integer.toString(i);
    }
    Attribute class_att = WekaUtils.createAttribute(Integer.toString(num_attributes), att_vals);
    atts[num_attributes] = class_att;
    return atts;
  }
}

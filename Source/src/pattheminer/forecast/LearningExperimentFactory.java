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

import static pattheminer.forecast.LearningExperiment.N;
import static pattheminer.forecast.LearningExperiment.NUM_LABELS;
import static pattheminer.forecast.LearningExperiment.PATTERN;
import static pattheminer.forecast.LearningExperiment.PATTERN_AVG_DURATION;
import static pattheminer.forecast.LearningExperiment.PATTERN_CLASSIFIER;
import static pattheminer.forecast.LearningExperiment.PATTERN_NEXT_EVENT;
import static pattheminer.forecast.LearningExperiment.T;
import static pattheminer.forecast.PredictionExperiment.M;
import static pattheminer.forecast.PredictionExperiment.NUM_SLICES;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.RaiseArity;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.peg.util.MapToFunction;
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
import pattheminer.forecast.features.RunningAverage;
import pattheminer.patterns.ExtractAttributes;
import pattheminer.source.RandomLabelSource;
import weka.core.Attribute;

public abstract class LearningExperimentFactory<T extends LearningExperiment> extends ExperimentFactory<MainLab,T>
{
  LearningExperimentFactory()
  {
    super(null, null);
  }
  
  /**
   * Creates a new factory
   * @param lab The lab this factory is associated to
   */
  public LearningExperimentFactory(MainLab lab, Class<T> clazz)
  {
    super(lab, clazz);
  }
  
  @Override
  protected T createExperiment(Region r)
  {
    String prediction = r.getString(PATTERN);
    T exp = null;
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
    if (prediction.compareTo(PATTERN_CLASSIFIER) == 0)
    {
      exp = setupWekaLearning(r);
    }
    if (exp == null)
    {
      return null;
    }
    exp.setInput(PATTERN, prediction);
    exp.setEventStep(MainLab.s_eventStep);
    return exp;
    
  }

  protected T setupAverageDuration(Region r)
  {
    int m = r.getInt(M); 
    int t = 1;
    int n = 1;
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
    T cte = getExperiment(r, uc, slice_f, phi, kappa, t, m, n);
    cte.setSource(source);
    cte.setInput(NUM_SLICES, r.getInt(NUM_SLICES));
    return cte;
  }
  
  protected T setupWekaLearning(Region r)
  {
    int num_features = r.getInt(ClassifierExperiment.NUM_FEATURES);
    int num_classes = r.getInt(ClassifierExperiment.NUM_CLASSES);
    String algo_name = r.getString(ClassifierExperiment.LEARNING_ALGORITHM);
    int update_interval = r.getInt(ClassifierExperiment.UPDATE_INTERVAL);
    int roll_width = r.getInt(ClassifierExperiment.ROLL_WIDTH);
    int m = r.getInt(M); 
    int t = 1;
    int n = 1;
    Processor beta = new ExtractAttributes(num_features);
    Processor kappa = new ApplyFunction(new NthElement(num_features));
    Attribute[] atts = createDummyAttributes(num_features, num_classes);
    RaiseArity slice_f = new RaiseArity(1, new Constant(0));
    GroupProcessor uc = new GroupProcessor(1, 1);
    {
      UpdateClassifier clas = new UpdateClassifier(WekaUtils.getClassifier(algo_name), update_interval, roll_width, "test", atts);
      ApplyFunction to_fct = new ApplyFunction(new WekaUtils.CastClassifierToFunction(clas.getDataset(), atts));
      Connector.connect(clas, to_fct);
      uc.addProcessors(clas, to_fct);
      uc.associateInput(0, clas, 0);
      uc.associateOutput(0, to_fct, 0);
    }
    T cte = getExperiment(r, uc, slice_f, beta, kappa, t, m, n);
    cte.setInput(ClassifierExperiment.LEARNING_ALGORITHM, algo_name);
    cte.setInput(ClassifierExperiment.ROLL_WIDTH, roll_width);
    cte.setInput(ClassifierExperiment.ROLL_WIDTH, roll_width);
    cte.setInput(ClassifierExperiment.UPDATE_INTERVAL, update_interval);
    cte.setInput(ClassifierExperiment.NUM_FEATURES, num_features);
    cte.setInput(ClassifierExperiment.NUM_CLASSES, num_classes);
    cte.setInput(M, m);
    cte.setInput(T, t);
    cte.setInput(N, n);
    cte.setSource(new RandomArraySource(m_lab.getRandom(), MainLab.MAX_TRACE_LENGTH, num_features, atts[num_features]));
    return cte;
  }
  
  /*@ non_null @*/ protected T setupMostProbableNextEvent(/*@ non_null @*/ Region r)
  {
    int m = r.getInt(M) + 1; // Size of n-gram is 1 greater than m
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
    GroupProcessor uc = new GroupProcessor(1, 1);
    {
      Slice sl = new Slice(new NthElement(0), inner_slice);
      ApplyFunction af = new ApplyFunction(MapToFunction.instance);
      Connector.connect(sl, af);
      uc.addProcessors(sl, af);
      uc.associateInput(0, sl, 0);
      uc.associateOutput(0, af, 0);
    }
    
    T cte = getExperiment(r, uc, slice_f, phi, kappa, m + 1, m, 1);
    cte.setSource(source);
    cte.setInput(M, m - 1);
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
  
  /**
   * Creates an instance of the desired experiment
   * @param r
   * @param update_classifier
   * @param slice_f
   * @param beta
   * @param kappa
   * @param t
   * @param m
   * @param n
   * @return
   */
  protected abstract T getExperiment(Region r, Processor update_classifier, Function slice_f, Processor beta, Processor kappa, int t, int m, int n);

}

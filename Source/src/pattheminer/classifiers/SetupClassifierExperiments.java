/*
    A BeepBeep palette for mining event traces
    Copyright (C) 2017-2019 Sylvain Hallé and friends

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pattheminer.classifiers;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.peg.weka.WekaUtils;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import java.util.Collection;
import pattheminer.ClassifierExperiment;
import pattheminer.ClassifierTrainingExperiment;
import pattheminer.MainLab;
import pattheminer.StreamExperiment;
import pattheminer.patterns.ExtractAttributes;
import weka.core.Attribute;
import static pattheminer.ClassifierExperiment.*;

public class SetupClassifierExperiments
{
  protected MainLab m_lab;

  protected static int s_maxTraceLength = 10000;

  public static void populate(MainLab lab)
  {
    SetupClassifierExperiments cm = new SetupClassifierExperiments(lab);
    cm.fillWithExperiments();
  }

  protected SetupClassifierExperiments(/*@ non_null @*/ MainLab lab)
  {
    super();
    m_lab = lab;
  }

  /**
   * Populates the lab with experiments
   */
  protected void fillWithExperiments()
  {
    // Self-trained class prediction experiments
    {
      Group g = new Group("Self-trained class prediction throughput");
      g.setDescription("Measures the throughput of the self-trained class prediction processor for various trend computations.");
      m_lab.add(g);
      Region reg = new Region();
      reg.add(NUM_FEATURES, 1, 3, 5);
      reg.add(ROLL_WIDTH, 0, 1000);
      reg.add(NUM_CLASSES, 2);
      reg.add(UPDATE_INTERVAL, 1);
      reg.add(LEARNING_ALGORITHM, "J48", "Voted Perceptron");

      {
        // For each roll width, make a plot of running time
        for (Region r_w : reg.all(ClassifierExperiment.LEARNING_ALGORITHM, ClassifierExperiment.ROLL_WIDTH))
        {
          ExperimentTable original_table = new ExperimentTable(ClassifierExperiment.NUM_FEATURES, StreamExperiment.LENGTH, StreamExperiment.TIME);
          original_table.setShowInList(false);
          for (Region r_ll : r_w.all(ClassifierExperiment.NUM_CLASSES, ClassifierExperiment.LEARNING_ALGORITHM, ClassifierExperiment.NUM_FEATURES))
          {
            ClassifierTrainingExperiment cte = getClassifierTrainingExperiment(r_ll);
            original_table.add(cte);
          }
          TransformedTable t_table = new TransformedTable(new ExpandAsColumns(ClassifierExperiment.NUM_FEATURES, ClassifierExperiment.TIME), original_table);
          t_table.setTitle("Throughput for classifier training (" + r_w.getString(ClassifierExperiment.LEARNING_ALGORITHM) + "), roll width=" + r_w.getInt(ClassifierExperiment.ROLL_WIDTH));
          t_table.setNickname("tCtThroughput");
          m_lab.add(original_table, t_table);
          
          // Make a scatterplot with it
          Scatterplot plot = new Scatterplot(t_table);
          plot.setTitle("Throughput for classifier training (" + r_w.getString(ClassifierExperiment.LEARNING_ALGORITHM) + "), roll width=" + r_w.getInt(ClassifierExperiment.ROLL_WIDTH));
          plot.setNickname("pCtThroughput");
          plot.setCaption(Axis.X, "Number of events").setCaption(Axis.Y, "Time (ms)");
          m_lab.add(plot);          
        }
      }
      
      {
        // For roll width=1000 and update interval=1...
        Region rg_n = reg.set(ROLL_WIDTH, 1000).set(UPDATE_INTERVAL, 1);
        ExperimentTable original_table = new ExperimentTable(NUM_FEATURES, LEARNING_ALGORITHM, THROUGHPUT);
        original_table.setShowInList(false);
        for (Region rg_c : rg_n.all(LEARNING_ALGORITHM, NUM_FEATURES))
        {
          // For each algorithm
          ClassifierTrainingExperiment cte = getClassifierTrainingExperiment(rg_c);
          original_table.add(cte);
        }
        TransformedTable t_table = new TransformedTable(new ExpandAsColumns(LEARNING_ALGORITHM, THROUGHPUT), original_table);
        t_table.setTitle("Throughput by features for classifier training, roll width=" + rg_n.getInt(ROLL_WIDTH));
        t_table.setNickname("tCtFThroughput");
        m_lab.add(original_table, t_table);
        // Make a scatterplot with it
        Scatterplot plot = new Scatterplot(t_table);
        plot.setTitle("Throughput by features for classifier training, roll width=" + rg_n.getInt(ClassifierExperiment.ROLL_WIDTH));
        plot.setNickname("pCtFThroughput");
        plot.setCaption(Axis.X, "Number of features").setCaption(Axis.Y, "Time (ms)");
        m_lab.add(plot);
      }
    }
  }

  /**
   * Generates a new classifier training experiment with given parameters,
   * or fetches the existing one if it already exists.
   * @param r The region describing the experiment's parameters
   * @return The experiment
   */
  /*@ null @*/ protected ClassifierTrainingExperiment getClassifierTrainingExperiment(/*@ non_null @*/ Region r)
  {
    Collection<Experiment> col = m_lab.filterExperiments(r, ClassifierTrainingExperiment.class);
    if (col.isEmpty())
    {
      // Experiment does not exist
      int num_features = r.getInt(ClassifierExperiment.NUM_FEATURES);
      int num_classes = r.getInt(ClassifierExperiment.NUM_CLASSES);
      String algo_name = r.getString(ClassifierExperiment.LEARNING_ALGORITHM);
      int update_interval = r.getInt(ClassifierExperiment.UPDATE_INTERVAL);
      int roll_width = r.getInt(ClassifierExperiment.ROLL_WIDTH);
      Processor beta = new ExtractAttributes(num_features);
      Processor kappa = new ApplyFunction(new NthElement(num_features));
      Attribute[] atts = createDummyAttributes(num_features, num_classes);
      ClassifierTrainingExperiment cte = new ClassifierTrainingExperiment(algo_name, WekaUtils.getClassifier(algo_name), update_interval, roll_width, beta, kappa, 1, 1, 1, atts);
      cte.setSource(new RandomArraySource(m_lab.getRandom(), s_maxTraceLength, num_features, atts[num_features]));
      m_lab.add(cte);
      return cte;
    }
    else
    {
      for (Experiment e : col)
      {
        return (ClassifierTrainingExperiment) e;
      }
    }
    return null;
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

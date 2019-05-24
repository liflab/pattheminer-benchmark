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
package pattheminer.forecast;

import static pattheminer.forecast.ClassifierExperiment.*;
import static pattheminer.forecast.StaticPredictionExperiment.*;
import static pattheminer.forecast.PredictiveLearningExperiment.*;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.RaiseArity;
import ca.uqac.lif.cep.peg.weka.WekaUtils;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.json.JsonFalse;
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import pattheminer.MainLab;
import pattheminer.MainLabNicknamer;
import pattheminer.SetupAgent;
import pattheminer.StreamExperiment;

public class SetupPredictionExperiments extends SetupAgent
{
  protected static int s_maxTraceLength = 10000;

  public SetupPredictionExperiments(/*@ non_null @*/ MainLab lab)
  {
    super(lab);
  }

  @Override
  public void fillWithExperiments()
  {
    {
      // Static prediction experiments
      StaticPredictionExperimentFactory factory = new StaticPredictionExperimentFactory(m_lab);
      Group g = new Group("Static prediction throughput");
      g.setDescription("Measures the throughput of the static prediction pattern for various feature computations.");
      m_lab.add(g);
      Region main_region = new Region();
      main_region.add(PREDICTION, StaticPredictionExperiment.PREDICTION_AVG, 
          StaticPredictionExperiment.PREDICTION_REGRESSION);
      main_region.add(M, 5, 10, 30, 100);
      main_region.add(NUM_SLICES, 1, 5, 10, 30, 100);
      main_region.add(MULTITHREAD, JsonFalse.instance);
      {
        // Throughput by window width for each prediction, for fixed number of slices
        for (Region in_reg : main_region.all(NUM_SLICES))
        {
          ExperimentTable et = new ExperimentTable(M, PREDICTION, StreamExperiment.THROUGHPUT);
          et.setShowInList(false);
          m_lab.add(et);
          for (Region reg : in_reg.all(PREDICTION, M))
          {
            StaticPredictionExperiment exp = factory.get(reg);
            if (exp == null)
              continue;
            g.add(exp);
            et.add(exp);
          }
          TransformedTable tt = new TransformedTable(new ExpandAsColumns(PREDICTION, StreamExperiment.THROUGHPUT), et);
          tt.setTitle("Static prediction throughput by window width (" + in_reg.getInt(NUM_SLICES) + " slices)");
          tt.setNickname(MainLabNicknamer.latexify("tStaticPredictionThroughputWidth" + in_reg.getInt(NUM_SLICES) + "slices"));
          m_lab.add(tt);
        }
      }
      {
        // Throughput by number of slices width for each prediction, for fixed window width
        for (Region in_reg : main_region.all(M))
        {
          ExperimentTable et = new ExperimentTable(NUM_SLICES, PREDICTION, StreamExperiment.THROUGHPUT);
          et.setShowInList(false);
          m_lab.add(et);
          for (Region reg : in_reg.all(PREDICTION, NUM_SLICES))
          {
            StaticPredictionExperiment exp = factory.get(reg);
            if (exp == null)
              continue;
            g.add(exp);
            et.add(exp);
          }
          TransformedTable tt = new TransformedTable(new ExpandAsColumns(PREDICTION, StreamExperiment.THROUGHPUT), et);
          tt.setTitle("Static prediction throughput by number of slices (window width = " + in_reg.getInt(M) + ")");
          tt.setNickname(MainLabNicknamer.latexify("tStaticPredictionThroughputSlices" + in_reg.getInt(M) + "width"));
          m_lab.add(tt);
        }
      }
    }

    // Self-trained class prediction experiments
    {
      PredictiveLearningExperimentFactory factory = new PredictiveLearningExperimentFactory(m_lab);
      Group g = new Group("Predictive learning throughput (global)");
      g.setDescription("Measures the throughput of the predictive learning pattern for various feature, class and predictive functions.");
      m_lab.add(g);
      {
        // Throughput by number of slices for each problem
        Region main_region = new Region();
        main_region.add(NUM_FEATURES, 3);
        main_region.add(ROLL_WIDTH, 1000);
        main_region.add(NUM_CLASSES, 2);
        main_region.add(UPDATE_INTERVAL, 2);
        main_region.add(LEARNING_ALGORITHM, "J48");
        main_region.add(NUM_LABELS, 10);
        main_region.add(NUM_SLICES, 1, 5, 10, 30, 100);
        main_region.add(M, 3);
        main_region.add(PATTERN, PredictiveLearningExperiment.PATTERN_AVG_DURATION,
            PredictiveLearningExperiment.PATTERN_NEXT_EVENT);
        ExperimentTable et = new ExperimentTable(NUM_SLICES, PATTERN, THROUGHPUT);
        et.setShowInList(false);
        m_lab.add(et);
        for (Region reg : main_region.all(NUM_SLICES, PATTERN))
        {
          PredictiveLearningExperiment exp = factory.get(reg);
          if (exp == null)
            continue;
          g.add(exp);
          et.add(exp);
        }
        TransformedTable tt = new TransformedTable(new ExpandAsColumns(PATTERN, THROUGHPUT), et);
        tt.setTitle("Predictive learning throughput by number of slices");
        tt.setNickname("tPredictiveLearningThroughputBySlices");
        m_lab.add(tt);
      }
      
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
            PredictiveLearningExperiment cte = factory.get(r_ll);
            if (cte == null)
              continue;
            g.add(cte);
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
          PredictiveLearningExperiment cte = factory.get(rg_c);
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
}

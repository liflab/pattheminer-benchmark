/*
    A benchmark for Pat The Miner
    Copyright (C) 2018 Laboratoire d'informatique formelle

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

import java.util.HashMap;

import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.peg.MapDistance;
import ca.uqac.lif.cep.peg.TrendDistance;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.Random;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.Join;
import ca.uqac.lif.mtnp.table.RenameColumns;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TransformedTable;
import pattheminer.patterns.CumulativeAverage;
import pattheminer.patterns.SymbolDistribution;

public class MainLab extends Laboratory
{
  protected static int s_eventStep = 10000;

  public static int MAX_TRACE_LENGTH = 100000;


  @Override
  public void setup()
  {	  
    // Average experiments
    generateWindowExperiments(generateAverageExperiment(50), generateAverageExperiment(200), "running average", "Average");
    // Distribution experiments
    generateWindowExperiments(generateDistributionExperiment(50), generateDistributionExperiment(200), "symbol distribution", "Distribution");
  }
  
  protected void generateWindowExperiments(ExperimentTable table_50, ExperimentTable table_200, String beta_name, String nickname_prefix)
  {
    Table tt = new TransformedTable(new Join(TrendDistanceExperiment.LENGTH),
        new TransformedTable(new RenameColumns(TrendDistanceExperiment.LENGTH, "50"), table_50),
        new TransformedTable(new RenameColumns(TrendDistanceExperiment.LENGTH, "200"), table_200)
        );
    tt.setTitle("Running time for the " + beta_name);
    add(tt);
    Scatterplot plot = new Scatterplot(tt);
    plot.setCaption(Axis.X, "Number of events").setCaption(Axis.Y, "Time (ms)");
    plot.setTitle("Running time for the " + beta_name);
    plot.setNickname("p" + nickname_prefix);
    add(plot);
    add(new AverageThroughputMacro(this, table_50, "tp" + nickname_prefix + "Fifty", beta_name + " with a window of 50"));
    add(new AverageThroughputMacro(this, table_200, "tp" + nickname_prefix + "TwoHundred", beta_name + " with a window of 200"));
    add(new MonotonicWindowClaim(tt, beta_name, "50", "200"));
  }

  protected ExperimentTable generateAverageExperiment(int width)
  {
    Random random = getRandom();
    CumulativeAverage average = new CumulativeAverage();
    TrendDistance<Number,Number,Number> alarm = new TrendDistance<Number,Number,Number>(6, width, average, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(Numbers.subtraction, StreamVariable.X, StreamVariable.Y)), 0.5, Numbers.isLessThan);
    Source src = new RandomNumberSource(random, MAX_TRACE_LENGTH);
    ExperimentTable et = addNewExperiment("Average", "Subtraction", src, alarm, width);
    return et;
  }
  
  protected ExperimentTable generateDistributionExperiment(int width)
  {
    Random random = getRandom();
    SymbolDistribution beta = new SymbolDistribution();
    HashMap<Object,Object> pattern = MapDistance.createMap("0", width - 2, "1", 1, "2", 1);
    TrendDistance<HashMap<?,?>,Number,Number> alarm = new TrendDistance<HashMap<?,?>,Number,Number>(pattern, width, beta, new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(MapDistance.instance, StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
    Source src = new RandomSymbolSource(random, MAX_TRACE_LENGTH);
    ExperimentTable et = addNewExperiment("Symbol distribution", "Map distance", src, alarm, width);
    return et;
  }

  protected ExperimentTable addNewExperiment(String trend, String metric, Source src, TrendDistance<?,?,?> alarm, int width)
  {
    TrendDistanceExperiment tde = new TrendDistanceExperiment();
    tde.setSource(src);
    tde.setTrendDistance(alarm);
    tde.setEventStep(s_eventStep);
    tde.setInput(TrendDistanceExperiment.WIDTH, width);
    tde.setInput(TrendDistanceExperiment.TREND, trend);
    tde.setInput(TrendDistanceExperiment.METRIC, metric);
    add(tde);
    String title = "Running time for " + trend + ", window width = " + width;
    ExperimentTable et = new ExperimentTable(TrendDistanceExperiment.LENGTH, TrendDistanceExperiment.TIME);
    et.setTitle(title);
    et.add(tde);
    add(et);
    /*Scatterplot plot = new Scatterplot(et);
    plot.setTitle(title);
    add(plot);*/
    return et;
  }

  public static void main(String[] args)
  {
    // Nothing else to do here
    MainLab.initialize(args, MainLab.class);
  }
}

package pattheminer;

import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
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

public class MainLab extends Laboratory
{
  protected static int s_eventStep = 10000;

  public static int MAX_TRACE_LENGTH = 100000;


  @Override
  public void setup()
  {	  
    {
      ExperimentTable table_50 = generateAverageExperiment(50);
      ExperimentTable table_200 = generateAverageExperiment(200);
      Table tt = new TransformedTable(new Join(TrendDistanceExperiment.LENGTH),
          new TransformedTable(new RenameColumns(TrendDistanceExperiment.LENGTH, "50"), table_50),
          new TransformedTable(new RenameColumns(TrendDistanceExperiment.LENGTH, "200"), table_200)
          );
      tt.setTitle("Running time for the window average");
      add(tt);
      Scatterplot plot = new Scatterplot(tt);
      plot.setCaption(Axis.X, "Number of events").setCaption(Axis.Y, "Time (ms)");
      plot.setTitle("Running time for the window average");
      add(plot);
    }
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

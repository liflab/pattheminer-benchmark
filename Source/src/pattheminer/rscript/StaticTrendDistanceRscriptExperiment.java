package pattheminer.rscript;

import static pattheminer.trenddistance.statictd.StaticTrendDistanceExperiment.METRIC;
import static pattheminer.trenddistance.statictd.StaticTrendDistanceExperiment.TREND;
import static pattheminer.trenddistance.statictd.StaticTrendDistanceExperiment.WIDTH;
import static pattheminer.trenddistance.statictd.StaticTrendDistanceExperiment.TYPE;

public class StaticTrendDistanceRscriptExperiment extends RscriptExperiment
{
  public StaticTrendDistanceRscriptExperiment()
  {
    super();
    describe(METRIC, "The metric used to compute the distance between the reference trend and the computed trend");
    describe(TREND, "The trend computed on the event stream");
    describe(WIDTH, "The width of the window over which the trend is computed");
    describe(TYPE, "The type of mining pattern used");
  }
  
  @Override
  protected String getScriptFilename()
  {
    // TODO Auto-generated method stub
    return null;
  }

}

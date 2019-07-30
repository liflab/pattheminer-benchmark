package pattheminer.rscript;

import pattheminer.trenddistance.TrendFactory;
import pattheminer.trenddistance.statictd.StaticTrendDistanceExperiment;

public class StaticTrendDistanceRscriptFactory extends TrendFactory<StaticTrendDistanceRscriptExperiment>
{
  public static final transient String TYPE_NAME = "Trend distance";
  
  public StaticTrendDistanceRscriptFactory()
  {
    super();
    
  }
}

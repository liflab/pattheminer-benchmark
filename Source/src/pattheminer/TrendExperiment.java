package pattheminer;

public abstract class TrendExperiment extends StreamExperiment
{
  public static final transient String METRIC = "Metric";
  
  public static final transient String TREND = "Trend function";
  
  public static final transient String WIDTH = "Window width";  

  public TrendExperiment()
  {
    super();
    describe(METRIC, "The metric used to compute the distance between the reference trend and the computed trend");
    describe(TREND, "The trend computed on the event stream");
    describe(WIDTH, "The width of the window over which the trend is computed");
  }
}

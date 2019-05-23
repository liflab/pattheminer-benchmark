package pattheminer.forecast;

import pattheminer.StreamExperiment;

public abstract class PredictionExperiment extends StreamExperiment
{
  /**
   * The slicing function (f)
   */
  public static final transient String F = "Slicing function (f)";
  
  /**
   * The width of the window used to compute the feature (m)
   */
  public static final transient String M = "Feature window width (m)";
  
  /**
   * The feature computed on a window (&phi;)
   */
  public static final transient String PHI = "Feature processor (phi)";
  
  public PredictionExperiment()
  {
    super();
    describe(F, "The slicing function (f)");
    describe(M, "The width of the window used to compute the feature (m)");
    describe(PHI, "The feature computed on a window (&phi;)");
  }
}

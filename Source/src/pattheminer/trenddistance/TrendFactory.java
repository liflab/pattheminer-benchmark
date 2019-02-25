package pattheminer.trenddistance;

import pattheminer.MainLab;
import ca.uqac.lif.labpal.ExperimentFactory;

public abstract class TrendFactory<T extends TrendExperiment> extends ExperimentFactory<MainLab,T>
{
  public TrendFactory(MainLab lab, Class<T> c)
  {
    super(lab, c);
  }
}

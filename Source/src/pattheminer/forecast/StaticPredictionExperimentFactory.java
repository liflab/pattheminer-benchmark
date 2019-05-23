package pattheminer.forecast;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Region;
import pattheminer.MainLab;
import pattheminer.forecast.features.RunningAverage;

import static pattheminer.forecast.StaticPredictionExperiment.F;
import static pattheminer.forecast.StaticPredictionExperiment.M;
import static pattheminer.forecast.StaticPredictionExperiment.PHI;
import static pattheminer.forecast.StaticPredictionExperiment.PI;
import static pattheminer.forecast.StaticPredictionExperiment.PREDICTION;

import pattheminer.source.BoundedSource;
import pattheminer.source.RandomNumberSource;

public class StaticPredictionExperimentFactory extends ExperimentFactory<MainLab,StaticPredictionExperiment>
{
  public StaticPredictionExperimentFactory(MainLab lab, Class<StaticPredictionExperiment> c)
  {
    super(lab, StaticPredictionExperiment.class);
  }

  @Override
  protected StaticPredictionExperiment createExperiment(Region r)
  {
    String prediction = r.getString(PREDICTION);
    if (prediction == null)
    {
      return null;
    }
    if (prediction.compareTo("Average prediction") == 0)
    {
      return setupAveragePrediction(r);
    }
    
  }
  
  protected StaticPredictionExperiment setupAveragePrediction(Region r)
  {
    BoundedSource source = new RandomNumberSource(m_lab.getRandom(), MainLab.MAX_TRACE_LENGTH);
    Processor phi = new RunningAverage();
    Function pi = new IdentityFunction(1);
    StaticPredictionExperiment exp = new StaticPredictionExperiment(source, new IdentityFunction(1), r.getInt(M), phi, pi);
  }

}

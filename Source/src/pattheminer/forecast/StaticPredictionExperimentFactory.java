package pattheminer.forecast;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.functions.RaiseArity;
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Region;
import pattheminer.MainLab;
import pattheminer.forecast.features.RunningAverage;

import static pattheminer.forecast.StaticPredictionExperiment.F;
import static pattheminer.forecast.StaticPredictionExperiment.M;
import static pattheminer.forecast.StaticPredictionExperiment.PHI;
import static pattheminer.forecast.StaticPredictionExperiment.PI;
import static pattheminer.forecast.StaticPredictionExperiment.PREDICTION;
import static pattheminer.forecast.StaticPredictionExperiment.PREDICTION_AVG;

import pattheminer.source.BoundedSource;
import pattheminer.source.RandomNumberSource;

public class StaticPredictionExperimentFactory extends ExperimentFactory<MainLab,StaticPredictionExperiment>
{
  
  public StaticPredictionExperimentFactory(MainLab lab)
  {
    super(lab, StaticPredictionExperiment.class);
  }

  @Override
  /*@ null @*/ protected StaticPredictionExperiment createExperiment(/*@ non_null @*/ Region r)
  {
    String prediction = r.getString(PREDICTION);
    StaticPredictionExperiment exp = null;
    if (prediction == null)
    {
      return null;
    }
    if (prediction.compareTo(PREDICTION_AVG) == 0)
    {
      exp = setupAveragePrediction(r);
    }
    if (exp == null)
    {
      return null;
    }
    exp.setInput(PREDICTION, prediction);
    return exp;
  }
  
  /*@ non_null @*/ protected StaticPredictionExperiment setupAveragePrediction(/*@ non_null @*/ Region r)
  {
    BoundedSource source = new RandomNumberSource(m_lab.getRandom(), MainLab.MAX_TRACE_LENGTH);
    Processor phi = new RunningAverage();
    Function pi = new IdentityFunction(1);
    StaticPredictionExperiment exp = new StaticPredictionExperiment(source, new RaiseArity(1, new Constant(0)), r.getInt(M), phi, pi);
    exp.setInput(F, "Constant 0");
    exp.setInput(PHI, RunningAverage.NAME);
    exp.setInput(PI, "Identity");
    return exp;
  }

}

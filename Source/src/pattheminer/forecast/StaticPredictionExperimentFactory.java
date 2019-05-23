/*
    A benchmark for Pat The Miner
    Copyright (C) 2018-2019 Laboratoire d'informatique formelle

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
package pattheminer.forecast;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunctionArgument;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.peg.util.EvaluateAt;
import ca.uqac.lif.cep.tmf.WindowFunction;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Region;
import pattheminer.MainLab;
import pattheminer.forecast.features.LinearRegression;
import pattheminer.forecast.features.RunningAverage;

import static pattheminer.forecast.PredictionExperiment.NUM_SLICES;
import static pattheminer.forecast.PredictionExperiment.F;
import static pattheminer.forecast.PredictionExperiment.M;
import static pattheminer.forecast.PredictionExperiment.PHI;
import static pattheminer.forecast.StaticPredictionExperiment.PI;
import static pattheminer.forecast.StaticPredictionExperiment.PREDICTION;
import static pattheminer.forecast.StaticPredictionExperiment.PREDICTION_AVG;
import static pattheminer.forecast.StaticPredictionExperiment.PREDICTION_REGRESSION;

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
    else if (prediction.compareTo(PREDICTION_REGRESSION) == 0)
    {
      exp = setupRegressionPrediction(r);
    }
    if (exp == null)
    {
      return null;
    }
    exp.setInput(PREDICTION, prediction);
    exp.setEventStep(MainLab.s_eventStep);
    return exp;
  }
  
  /*@ non_null @*/ protected StaticPredictionExperiment setupAveragePrediction(/*@ non_null @*/ Region r)
  {
    BoundedSource source = new RandomNumberSource(m_lab.getRandom(), MainLab.MAX_TRACE_LENGTH);
    Processor phi = new RunningAverage();
    Function pi = new IdentityFunction(1);
    int num_slices = r.getInt(NUM_SLICES);
    Function slice_fct = new FunctionTree(Numbers.floor,
        new FunctionTree(Numbers.division, 
            new FunctionTree(Numbers.multiplication, StreamVariable.X, new Constant(1000)),
            new Constant(num_slices)));
    StaticPredictionExperiment exp = new StaticPredictionExperiment(source, slice_fct, r.getInt(M), phi, pi);
    exp.setInput(F, num_slices + " equal interval(s)");
    exp.setInput(PHI, RunningAverage.NAME);
    exp.setInput(PI, "Identity");
    exp.setInput(NUM_SLICES, num_slices);
    return exp;
  }
  
  /*@ non_null @*/ protected StaticPredictionExperiment setupRegressionPrediction(/*@ non_null @*/ Region r)
  {
    int m = r.getInt(M);
    BoundedSource source = new RandomNumberSource(m_lab.getRandom(), MainLab.MAX_TRACE_LENGTH);
    Processor phi = new WindowFunction(new LinearRegression(m));
    Function pi = new EvaluateAt(ApplyFunctionArgument.instance, m);
    int num_slices = r.getInt(NUM_SLICES);
    Function slice_fct = new FunctionTree(Numbers.floor,
        new FunctionTree(Numbers.division, 
            new FunctionTree(Numbers.multiplication, StreamVariable.X, new Constant(1000)),
            new Constant(num_slices)));
    StaticPredictionExperiment exp = new StaticPredictionExperiment(source, slice_fct, m, phi, pi);
    exp.setInput(F, num_slices + " equal interval(s)");
    exp.setInput(PHI, RunningAverage.NAME);
    exp.setInput(PI, "Linear regression function at m");
    exp.setInput(NUM_SLICES, num_slices);
    return exp;
  }

}

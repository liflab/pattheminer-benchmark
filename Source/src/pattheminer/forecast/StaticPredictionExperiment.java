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
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.peg.forecast.StaticPrediction;
import pattheminer.source.BoundedSource;

public class StaticPredictionExperiment extends PredictionExperiment
{
  
  /**
   * The function used to make a forecast on the window (&pi;)
   */
  public static final transient String PI = "Predictive function (pi)";
  
  /**
   * The name of the prediction being made
   */
  public static final transient String PREDICTION = "Prediction";
  
  /**
   * One of the possible predictions: average
   */
  public static final transient String PREDICTION_AVG = "Average prediction";
  
  /**
   * One of the possible predictions: linear regression
   */
  public static final transient String PREDICTION_REGRESSION = "Linear regression";
  
  /**
   * Empty constructor; used only for deserialization
   */
  StaticPredictionExperiment()
  {
    // Do nothing
  }
  
  /**
   * Creates a new static prediction experiment
   * @param source
   * @param f The slicing function (f)
   * @param m The width of the window used to compute the feature (m)
   * @param phi The feature computed on a window (&phi;)
   * @param pi The function used to make a forecast on the window (&pi;)
   */
  public StaticPredictionExperiment(BoundedSource source, Function f, int m, Processor phi, Function pi)
  {
    super();
    addKeyToHide(F);
    addKeyToHide(PHI);
    addKeyToHide(PI);
    describe(PI, "The function used to make a forecast on the window (&pi;)");
    describe(PREDICTION, "The name of the prediction being made");
    StaticPrediction sp = new StaticPrediction(f, phi, m, pi);
    setSource(source);
    setProcessor(sp);
    setInput(M, m);
  }
  
  /**
   * Creates a new static prediction experiment
   * @param source
   * @param f The slicing function (f)
   * @param m The width of the window used to compute the feature (m)
   * @param phi The feature computed on a window (&phi;)
   * @param pi The function used to make a forecast on the window (&pi;)
   */
  public StaticPredictionExperiment(BoundedSource source, Function f, Function phi, Function pi)
  {
    super();
    addKeyToHide(F);
    addKeyToHide(PHI);
    addKeyToHide(PI);
    describe(PI, "The function used to make a forecast on the window (&pi;)");
    describe(PREDICTION, "The name of the prediction being made");
    StaticPrediction sp = new StaticPrediction(f, phi, pi);
    setSource(source);
    setProcessor(sp);
    setInput(M, phi.getInputArity());
  }
}
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
package pattheminer.forecast.features;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.util.Numbers;
import java.util.Set;

/**
 * Computes a linear regression slope on a set of <i>n</i> points.
 * More specifically, this function receives an array of <i>n</i> values
 * <i>y</i><sub>0</sub>, &hellip;, <i>y</i><sub><i>n</i>-1</sub>, and
 * computes the regression line that best fits the points
 * (<i>i</i>, <i>y</i><sub><i>i</i></sub>) for <i>i</i> between 0 and
 * <i>n</i>-1. The output of the function is another function in one variable,
 * of the form <i>ax</i>+<i>b</i>.
 * <p>
 * The code that computes the regression is adapted from
 * <a href="https://algs4.cs.princeton.edu/14analysis/LinearRegression.java.html"><tt>LinearRegression.java</tt></a>,
 * taken from the following book:
 * <blockquote>
 * R. Sedgewick, K. Wayne. (2011). Algorithms, 4th edition.
 * <i>Addison-Wesley Professional</i>. ISBN 978-0321573513.
 * </blockquote>
 */
public class LinearRegression extends Function
{
  /**
   * The number of points on which regression is computed
   */
  protected int m_inArity;

  public LinearRegression(int in_arity)
  {
    super();
    m_inArity = in_arity;
  }

  @Override
  public void evaluate(Object[] inputs, Object[] outputs, Context context)
  {
    float y[] = new float[m_inArity];
    for (int i = 0; i < m_inArity; i++)
    {
      y[i] = ((Number) inputs[i]).floatValue();
    }
    // first pass
    float sumx = 0, sumy = 0; //, sumx2 = 0;
    for (int i = 0; i < m_inArity; i++) {
      sumx  += i;
      //sumx2 += i*i;
      sumy  += y[i];
    }
    float xbar = sumx / m_inArity;
    float ybar = sumy / m_inArity;

    // second pass: compute summary statistics
    float xxbar = 0, xybar = 0; //yybar = 0;
    for (int i = 0; i < m_inArity; i++) {
      xxbar += (i - xbar) * (i - xbar);
      //yybar += (y[i] - ybar) * (y[i] - ybar);
      xybar += (i - xbar) * (y[i] - ybar);
    }
    float slope  = xybar / xxbar;
    float intercept = ybar - slope * xbar;
    FunctionTree fct = new FunctionTree(Numbers.addition,
        new FunctionTree(Numbers.multiplication, new Constant(slope), StreamVariable.X), new Constant(intercept));
    outputs[0] = fct;
  }

  @Override
  public int getInputArity()
  {
    return m_inArity;
  }

  @Override
  public int getOutputArity()
  {
    return 1;
  }

  @Override
  public void getInputTypesFor(Set<Class<?>> classes, int index)
  {
    classes.add(Number.class);
  }

  @Override
  public Class<?> getOutputTypeFor(int index)
  {
    return Function.class;
  }

  @Override
  public LinearRegression duplicate(boolean with_state)
  {
    return new LinearRegression(m_inArity);
  }
}

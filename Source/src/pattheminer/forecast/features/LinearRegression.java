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
import ca.uqac.lif.cep.functions.RaiseArity;
import java.util.Set;

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
    // TODO pas fini
    outputs[0] = new RaiseArity(1, new Constant(0));
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

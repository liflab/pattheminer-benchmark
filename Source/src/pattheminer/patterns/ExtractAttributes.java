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
package pattheminer.patterns;

import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.peg.weka.CreateAttributeArray;
import ca.uqac.lif.cep.util.NthElement;

/**
 * Creates an array of attribute values from an input array by 
 * merely fetching the <i>n</i>-th element of the array to create the
 * <i>n</i>-th attribute value. In other words, this processor can be seen
 * as a form of "passthrough" for events that are already arrays.
 */
public class ExtractAttributes extends ApplyFunction
{
  /**
   * Creates a new instance of the processor.
   * @param num_attributes The number of attributes in the output array
   */
  public ExtractAttributes(int num_attributes)
  {
    super(getFunction(num_attributes));
  }

  /**
   * Creates the function instance that fetches attribute values
   * @param num_attributes The number of attributes in the output array
   * @return The function
   */
  public static Function getFunction(int num_attributes)
  {
    Function[] features = new Function[num_attributes];
    for (int i = 0; i < num_attributes; i++)
    {
      features[i] = new NthElement(i);
    }
    return new CreateAttributeArray<Object[]>(Object[].class, features);
  }
}

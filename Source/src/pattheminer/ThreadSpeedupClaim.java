/*
    A benchmark for Pat The Miner
    Copyright (C) 2018 Laboratoire d'informatique formelle

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
package pattheminer;

import ca.uqac.lif.labpal.Claim;
import ca.uqac.lif.labpal.Laboratory;

public class ThreadSpeedupClaim extends Claim
{
  protected transient ThreadSpeedupMacro m_macro;
  
  public ThreadSpeedupClaim(Laboratory lab, ThreadSpeedupMacro macro)
  {
    super(lab, "Thread speedup");
    m_macro = macro;
    setDescription("Checks that the running time of the trend distance pattern decreases with the use of multiple threads");
  }

  @Override
  public Result verify(Laboratory lab)
  {
    double speedup = m_macro.getNumber();
    if (speedup < 1)
    {
      Explanation exp = new Explanation("The running time for the computation without threads is faster than that with threads enabled.");
      exp.add(m_macro);
      addExplanation(exp);
      return Result.WARNING;
    }
    else if (speedup == 0)
    {
      return Result.UNKNOWN;
    }
    return Result.OK;
  }

}

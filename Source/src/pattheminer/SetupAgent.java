/*
    A BeepBeep palette for mining event traces
    Copyright (C) 2017-2019 Sylvain Hallé and friends

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pattheminer;

/**
 * An object designed to help in the setup of experiments of a particular type.
 * Agents are used to avoid cluttering the lab's main method and separate
 * the setup of experiments into categories.
 */
public abstract class SetupAgent
{
  /**
   * The lab in which experiments will be created
   */
  /*@ non_null @*/ protected MainLab m_lab;
  
  /**
   * Creates a new setup agent
   * @param lab The lab in which experiments will be created
   */
  public SetupAgent(/*@ non_null @*/ MainLab lab)
  {
    super();
    m_lab = lab;
  }
  
  /**
   * Adds new experiments to an existing lab
   */
  public abstract void fillWithExperiments();

}

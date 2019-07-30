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
package pattheminer.trenddistance;

import pattheminer.MainLab;
import ca.uqac.lif.labpal.ExperimentFactory;

public abstract class TrendFactory<T extends TrendExperiment> extends ExperimentFactory<MainLab,T>
{
  /**
   * Whether to use files when reading the traces
   */
  protected transient boolean m_useFiles = false;
  
  /**
   * Creates a new trend experiment factory
   * @param lab The lab to which the experiments will be added
   * @param c The class of the experiments to create
   * @param use_files Whether to use files when reading the traces.
   * Setting this to false will have the experiment generate the input
   * traces on-the-fly.
   */
  public TrendFactory(MainLab lab, Class<T> c, boolean use_files)
  {
    super(lab, c);
    m_useFiles = use_files;
  }
  
  public TrendFactory(MainLab lab, Class<T> c)
  {
    this(lab, c, false);
  }
}

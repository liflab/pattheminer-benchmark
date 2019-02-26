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
package pattheminer.trenddistance.context;

import static pattheminer.StreamExperiment.LENGTH;
import static pattheminer.StreamExperiment.TIME;
import static pattheminer.trenddistance.TrendExperiment.AVG_SLICE_LENGTH;
import static pattheminer.trenddistance.TrendExperiment.CLOSEST_CLUSTER;
import static pattheminer.trenddistance.TrendExperiment.N_GRAMS;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_AVG;
import static pattheminer.trenddistance.TrendExperiment.RUNNING_MOMENTS;
import static pattheminer.trenddistance.TrendExperiment.SYMBOL_DISTRIBUTION;
import static pattheminer.trenddistance.TrendExperiment.THROUGHPUT;
import static pattheminer.trenddistance.TrendExperiment.TREND;
import static pattheminer.trenddistance.TrendExperiment.WIDTH;
import static pattheminer.trenddistance.context.ContextualExperiment.CONTEXT_PROCESSOR;
import static pattheminer.trenddistance.context.ContextualExperiment.CONTEXT_WIDTH;
import static pattheminer.trenddistance.context.ContextualExperiment.WEEKDAYS;

import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import pattheminer.MainLab;
import pattheminer.trenddistance.SetupTrendExperiments;
import pattheminer.trenddistance.TrendExperiment;

/**
 * Setup of experiments for the self-correlated trend distance.
 */
public class SetupContextualExperiments extends SetupTrendExperiments<ContextualExperiment>
{
  public SetupContextualExperiments(MainLab lab)
  {
    super(lab, new ContextualExperimentFactory(lab), "Contextual trend distance throughput", "Measures the throughput of the contextual trend distance pattern using various window widths and trend processors.", "contextual trend distance", "ctd");
  }

  @Override
  public void fillWithExperiments()
  {
    // Static trend distance
    {
      Group g = new Group(m_groupName);
      g.setDescription(m_groupDescription);
      m_lab.add(g);
      
      // Weekdays experiments
      {
        Region big_reg = new Region();
        big_reg.add(WIDTH, MainLab.s_widths);
        big_reg.add(TREND, RUNNING_AVG, RUNNING_MOMENTS);
        big_reg.add(CONTEXT_PROCESSOR, WEEKDAYS);
        ExperimentTable et = new ExperimentTable(CONTEXT_PROCESSOR, TREND, WIDTH, THROUGHPUT);
        m_lab.add(et);
        et.setNickname("tThroughputContext");
        et.setTitle("Throughput for contextual trend distance");
        for (Region r_w : big_reg.all(CONTEXT_PROCESSOR, TREND, WIDTH))
        {
          TrendExperiment tde = m_factory.get(r_w);
          if (tde == null)
          {
            continue;
          }
          g.add(tde);
          et.add(tde);
        }
      }
    }
  }
}
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

import java.util.HashMap;
import java.util.Map;
import pattheminer.trenddistance.TrendExperiment;
import pattheminer.trenddistance.selftd.SelfCorrelatedExperiment;
import pattheminer.trenddistance.statictd.StaticTrendDistanceExperiment;
import ca.uqac.lif.labpal.provenance.ExperimentValue;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TempTable;
import ca.uqac.lif.petitpoucet.DirectValue;
import ca.uqac.lif.petitpoucet.NodeFunction;

/**
 * Custom table that puts side by side the throughput of the trend distance vs.
 * the self-correlated trend distance, for each of the trends involved in the
 * experiments.
 */
public class ThroughputComparisonTable extends Table
{
  protected transient String[] m_trends;
  
  protected transient Map<String,StaticTrendDistanceExperiment> m_trendExperiments;
  
  protected transient Map<String,SelfCorrelatedExperiment> m_selfCorrelatedExperiments;
  
  public ThroughputComparisonTable(String ... trends)
  {
    super();
    m_trends = trends;
    m_trendExperiments = new HashMap<String,StaticTrendDistanceExperiment>();
    m_selfCorrelatedExperiments = new HashMap<String,SelfCorrelatedExperiment>();
    for (String t : trends)
    {
      m_trendExperiments.put(t, null);
      m_selfCorrelatedExperiments.put(t, null);
    }
  }
  
  public void add(String trend, StaticTrendDistanceExperiment e)
  {
    m_trendExperiments.put(trend, e);
  }
  
  public void add(String trend, SelfCorrelatedExperiment e)
  {
    m_selfCorrelatedExperiments.put(trend, e);
  }
  
  @Override
  public TempTable getDataTable(boolean arg0)
  {
    TempTable ht = new TempTable(getId(), TrendExperiment.TREND, StaticTrendDistanceExperiment.TYPE_NAME, SelfCorrelatedExperiment.TYPE_NAME);
    fill(ht);
    return ht;
  }

  @Override
  protected TempTable getDataTable(boolean arg0, String... arg1)
  {
    TempTable ht = new TempTable(getId(), arg1);
    fill(ht);
    return ht;
  }
  
  protected void fill(TempTable ht)
  {
    for (String trend : m_trends)
    {
      TableEntry te = new TableEntry();
      te.put(TrendExperiment.TREND, trend);
      StaticTrendDistanceExperiment tde = m_trendExperiments.get(trend);
      if (tde != null)
      {
        te.put(StaticTrendDistanceExperiment.TYPE_NAME, tde.readFloat(TrendExperiment.THROUGHPUT));
      }
      else
      {
        te.put(StaticTrendDistanceExperiment.TYPE_NAME, 0);
      }
      SelfCorrelatedExperiment sce = m_selfCorrelatedExperiments.get(trend);
      if (sce != null)
      {
        te.put(SelfCorrelatedExperiment.TYPE_NAME, sce.readFloat(TrendExperiment.THROUGHPUT));
      }
      else
      {
        te.put(SelfCorrelatedExperiment.TYPE_NAME, 0);
      }
      ht.add(te);
    }
  }

  @Override
  public NodeFunction getDependency(int line, int col)
  {
    if ((line < 0 || line >= m_trends.length) && (col < 0 || col >= 3))
    {
      return null;
    }
    String trend_name = m_trends[line];
    if (col < 2)
    {
      StaticTrendDistanceExperiment tde = m_trendExperiments.get(trend_name);
      if (tde == null)
      {
        return null;
      }
      if (col == 0)
      {
        return new DirectValue(new ExperimentValue(tde, TrendExperiment.TREND));
      }
      else
      {
        return new DirectValue(new ExperimentValue(tde, TrendExperiment.THROUGHPUT));
      }
    }
    SelfCorrelatedExperiment sce = m_selfCorrelatedExperiments.get(trend_name);
    if (sce == null)
    {
      return null;
    }
    return new DirectValue(new ExperimentValue(sce, TrendExperiment.THROUGHPUT));
  }
}

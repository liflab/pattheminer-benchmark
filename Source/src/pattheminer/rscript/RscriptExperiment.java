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
package pattheminer.rscript;

import ca.uqac.lif.labpal.CommandRunner;
import ca.uqac.lif.labpal.ExperimentException;
import pattheminer.TraceExperiment;

/**
 * Experiment that processes an event trace with R.
 */
public abstract class RscriptExperiment extends TraceExperiment
{
  /**
   * The name of the executable to launch R scripts
   */
  protected static final transient String s_rExecutable = "Rscript";
  
  public RscriptExperiment()
  {
    super();
    write(SOFTWARE, "R");
  }
  
  @Override
  public void execute() throws ExperimentException, InterruptedException
  {
    CommandRunner runner = new CommandRunner(getCommand());
    long start = System.currentTimeMillis();
    runner.run();
    long end = System.currentTimeMillis();
    write(THROUGHPUT, (end - start) / (1000 * m_source.getEventBound()));
  }
  
  /**
   * Gets the command line to execute the R script
   * @return The command line with its arguments
   */
  protected String[] getCommand()
  {
    return new String[] {s_rExecutable, getScriptFilename(), m_source.getFilename()};
  }
  
  /**
   * Gets the filename of the R script to execute at the command line
   * @return The filename
   */
  protected abstract String getScriptFilename();
}

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

import ca.uqac.lif.json.JsonList;
import ca.uqac.lif.labpal.CommandRunner;
import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.mtnp.util.FileHelper;
import java.io.File;
import pattheminer.TraceExperiment;

/**
 * Experiment that processes an event trace with R.
 */
public abstract class RscriptExperiment extends TraceExperiment
{
  /**
   * The name of the software that runs this experiment
   */
  public static final transient String SOFTWARE_NAME = "R";
  
  /**
   * The name of the executable to launch R scripts
   */
  protected static final transient String s_rExecutable = "Rscript";
  
  /**
   * The name of the folder containing the R scripts <em>inside</em> the
   * LabPal directory structure. Must end with a forward slash.
   */
  protected static final transient String s_scriptFolder = "scripts/";
  
  /**
   * The name of the file containing the R script <em>inside</em> the
   * LabPal directory structure
   */
  protected transient String m_scriptFilename;
  
  /**
   * Arguments that must be passed to the script
   */
  /*@ null @*/ protected Object[] m_arguments = null;
  
  /**
   * Creates a new experiment that uses R
   */
  public RscriptExperiment()
  {
    super();
    setInput(SOFTWARE, "R");
  }
  
  @Override
  public void execute() throws ExperimentException, InterruptedException
  {
    CommandRunner runner = new CommandRunner(getCommand());
    long start = System.currentTimeMillis();
    runner.run();
    long end = System.currentTimeMillis();
    write(THROUGHPUT, (1000f * m_source.getEventBound()) / (float) (end - start));
    JsonList time = (JsonList) read(TIME);
    time.add(end - start);
    JsonList length = (JsonList) read(LENGTH);
    length.add(m_source.getEventBound());
  }
  
  /**
   * Gets the command line to execute the R script
   * @return The command line with its arguments
   */
  /*@ non_null @*/ protected String[] getCommand()
  {
    int size = 0;
    if (m_arguments != null)
    {
      size = m_arguments.length;
    }
    String[] out_array = new String[size + 3];
    out_array[0] = s_rExecutable;
    out_array[1] = m_scriptFilename;
    out_array[2] = m_source.getFilename();
    for (int i = 0; i < size; i++)
    {
      out_array[i + 3] = m_arguments[i].toString();
    }
    return out_array;
  }
  
  /**
   * Sets the filename of the R script that this experiment will execute
   * @param name The name of the file (without its path)
   */
  public void setScriptName(String name)
  {
    m_scriptFilename = name;
  }
  
  @Override
  /*@ pure @*/ public boolean prerequisitesFulfilled()
  {
    if (!FileHelper.fileExists(m_scriptFilename))
    {
      return false;
    }
    return super.prerequisitesFulfilled();
  }
  
  @Override
  public void fulfillPrerequisites() throws ExperimentException
  {
    // Do whatever preparation the parent requires
    super.fulfillPrerequisites();
    // Take the R script from inside the lab, and copy it to the local folder on the machine
    String contents = FileHelper.internalFileToString(RscriptExperiment.class, s_scriptFolder + m_scriptFilename);
    if (contents == null)
    {
      throw new ExperimentException("Could not find the R script for this experiment");
    }
    FileHelper.writeFromString(new File(m_scriptFilename), contents);
  }
  
  @Override
  public void cleanPrerequisites()
  {
    super.cleanPrerequisites();
    FileHelper.deleteFile(m_scriptFilename);
  }
  
  /**
   * Sets the arguments that must be passed to the script
   * @param args An array of objects, each of which will be converted to a
   * string and appended to the command line when the script is called.
   */
  public void setArguments(Object ... args)
  {
    m_arguments = args;
  }
}
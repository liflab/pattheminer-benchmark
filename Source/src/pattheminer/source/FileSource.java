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
package pattheminer.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Queue;
import java.util.Scanner;

import ca.uqac.lif.cep.ProcessorException;

/**
 * Source that writes events into an external file, and
 * then fetches its events from that file
 */
public abstract class FileSource<T> extends BoundedSource<T>
{
  /**
   * The name of the file to read from
   */
  /*@ non_null @*/ protected String m_filename;
  
  /**
   * The underlying source that will actually generate the events
   */
  protected BoundedSource<?> m_source;

  /**
   * A scanner used to read lines from the file
   */
  protected Scanner m_scanner;
  
  /**
   * The folder where the trace file will be written and read
   */
  protected String m_dataFolder = "./";

  /**
   * Creates a new file source
   * @param num_events The number of events to generate
   * @param source The underlying source that will actually generate the events
   * @param data_folder The folder where the trace file will be written and read
   */
  public FileSource(int num_events, BoundedSource<T> source, String data_folder)
  {
    super(num_events);
    m_source = source;
    m_dataFolder = data_folder;
  }
  
  /**
   * Creates a new file source
   * @param num_events The number of events to generate
   * @param source The underlying source that will actually generate the events
   */
  public FileSource(int num_events, BoundedSource<T> source)
  {
    this(num_events, source, "./");
  }

  /**
   * Determines if the file that the source is supposed to read
   * from already exists
   * @return <tt>true</tt> if the file exists, <tt>false</tt> otherwise
   */
  @Override
  public boolean isReady()
  {
    File f = new File(m_filename);
    return f.exists();
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    if (m_scanner == null)
    {
      try
      {
        m_scanner = new Scanner(new File(m_filename));
      }
      catch (FileNotFoundException e)
      {
        throw new ProcessorException(e);
      }
    }
    if (!m_scanner.hasNextLine())
    {
      m_scanner.close();
      return false;
    }
    String line = m_scanner.nextLine();
    Object o = getEvent(line);
    if (o != null)
    {
      outputs.add(new Object[] {o});
    }
    return true;
  }

  /**
   * Creates an event out of a line of text fetched from a file
   * @param line The line of text
   * @return An event, or <tt>null</tt> if no event can be produced
   */
  /*@ null @*/ protected abstract Object getEvent(/*@ non_null @*/ String line);

  @Override
  public void prepare() throws ProcessorException
  {
    String filename = m_dataFolder + m_source.getFilename();
    PrintStream ps = null;
    try
    {
      ps = new PrintStream(new FileOutputStream(new File(filename)));
    }
    catch (FileNotFoundException e)
    {
      throw new ProcessorException(e);
    }
    BoundedSource<?> source = (BoundedSource<?>) m_source.duplicate();
    source.printTo(ps);
    ps.close();
  }
}

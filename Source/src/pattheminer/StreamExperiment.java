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
package pattheminer;

import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.BlackHole;
import ca.uqac.lif.json.JsonList;
import ca.uqac.lif.labpal.ExperimentException;

/**
 * Experiment that connects a BeepBeep source to a processor and measures
 * its throughput
 */
public abstract class StreamExperiment extends TraceExperiment
{
  /**
   * The name of the software that runs this experiment
   */
  public static final transient String SOFTWARE_NAME = "BeepBeep";

  /**
   * Whether the experiment uses multiple threads or a single one
   */
  public static final transient String MULTITHREAD = "Multi-threaded";
  
  /**
   * Memory consumed
   */
  public static final transient String MEMORY = "Memory";
  
  /**
   * Maximum memory consumed
   */
  public static final transient String MAX_MEMORY = "Max memory";

  /**
   * The processor that is being monitored in this experiment
   */
  protected transient Processor m_processor;

  /**
   * The interval at which the experiment updates its data on
   * runtime and throughput
   */
  protected int m_eventStep = 1000;
  
  /**
   * A helper object used to compute the memory footprint of a processor
   */
  protected transient LabSizePrinter m_sizePrinter;

  /**
   * Creates a new empty stream experiment
   */
  public StreamExperiment()
  {
    super();
    setInput(SOFTWARE, SOFTWARE_NAME);
    describe(MULTITHREAD, "Whether the experiment uses multiple threads or a single one");
    describe(MEMORY, "The size of the processor object (in bytes)");
    describe(MAX_MEMORY, "The maximum size of the processor object (in bytes)");
    JsonList z = new JsonList();
    z.add(0);
    write(MEMORY, z);
    m_sizePrinter = new LabSizePrinter();
  }

  @Override
  public void execute() throws ExperimentException, InterruptedException 
  {
    JsonList length = (JsonList) read(LENGTH);
    JsonList time = (JsonList) read(TIME);
    JsonList memory = (JsonList) read(MEMORY);
    int max_memory = 0;
    // Setup processor chain
    Pullable s_p = m_source.getPullableOutput();
    Pushable t_p = m_processor.getPushableInput();
    BlackHole hole = new BlackHole();
    Connector.connect(m_processor, hole);
    long start = System.currentTimeMillis();
    int event_count = 0;
    int source_length = m_source.getEventBound();
    while (s_p.hasNext())
    {
      if (event_count % m_eventStep == 0 && event_count > 0)
      {
        long lap = System.currentTimeMillis();
        length.add(event_count);
        time.add(lap - start);
        try
        {
          m_sizePrinter.reset();
          int size = (Integer) m_sizePrinter.print(m_processor);
          memory.add(size);
          max_memory = Math.max(max_memory, size);
          write(MAX_MEMORY, max_memory);
        }
        catch (PrintException e)
        {
          throw new ExperimentException(e);
        }
        float prog = ((float) event_count) / ((float) source_length);
        setProgression(prog);
      }
      Object o = s_p.pull();
      t_p.push(o);
      event_count++;
    }
    long end = System.currentTimeMillis();
    write(THROUGHPUT, (1000f * (float) MainLab.MAX_TRACE_LENGTH) / ((float) (end - start)));
  }

  /**
   * Sets the processor that is being monitored in this experiment
   * @param p The processor
   */
  public void setProcessor(Processor p)
  {
    m_processor = p;
  }

  /**
   * Sets the interval at which the experiment updates its data on
   * runtime and throughput
   * @param step The interval
   */
  public void setEventStep(int step)
  {
    m_eventStep = step;
  }
}
package pattheminer;

import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.labpal.Random;

/**
 * A source of randomly generated numbers.
 */
public class RandomNumberSource extends Source
{
  /**
   * The random number generator used to generate the numbers
   */
  protected final Random m_random;
  
  /**
   * The number of events to produce
   */
  protected final int m_numEvents;
  
  /**
   * A counter keeping track of the number of events produced so far
   */
  protected int m_eventCount;
  
  /**
   * Creates a new random number source.
   * @param r The random number generator used to generate the numbers
   * @param num_events The number of events to produce
   */
  public RandomNumberSource(Random r, int num_events)
  {
    super(1);
    m_random = r;
    m_numEvents = num_events;
    m_eventCount = 0;
  }
  
  @Override
  public void reset()
  {
    m_eventCount = 0;
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    outputs.add(new Object[]{m_random.nextFloat()});
    m_eventCount++;
    return m_eventCount <= m_numEvents;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    // Not supported
    return null;
  }
  
  /**
   * Gets the total number of events to produce
   * @return The number of events
   */
  public int getEventBound()
  {
    return m_numEvents;
  }
}

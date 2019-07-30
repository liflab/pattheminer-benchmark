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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.labpal.Random;
import java.util.HashMap;
import java.util.Map;

/**
 * A source of randomly-generated labelled events, used for testing and
 * benchmarking.
 */
public class RandomLabelSource extends RandomSource<Object[]>
{
  /**
   * The label given to the "start" event for each slice
   */
  public static final String LABEL_START = "Start";

  /**
   * The label given to the "end" event for each slice
   */
  public static final String LABEL_END = "End";

  /**
   * The length of each slice
   */
  protected int m_sliceLength;

  /**
   * The number of slices to keep alive at any given time
   */
  protected int m_numSlices;

  /**
   * The index of the next slice for which to produce an event
   */
  protected int m_sliceIndex;

  /**
   * The ID of the lowest slice that is alive
   */
  protected int m_lowestSliceId;

  /**
   * The ID of the highest slice that is alive
   */
  protected int m_highestSliceId;

  /**
   * A map keeping the count of the number of events produced so far
   * for each "live" slice
   */
  protected Map<Integer,Integer> m_sliceStates;

  /**
   * The number of event labels (other than "start" and "end") that can occur
   */
  protected int m_numLabels = 1;

  /**
   * The other labels
   */
  protected String[] m_otherLabels;

  /**
   * Creates a new random label source
   * @param r A random generator
   * @param num_events The number of events to produce
   * @param slice_length The length of each slice
   * @param num_slices The number of slices to keep alive at any given time
   * @param num_labels The number of labels (other than "start" and "end")
   * that can occur
   */
  public RandomLabelSource(Random r, int num_events, int slice_length, int num_slices, int num_labels)
  {
    super(r, num_events);
    m_sliceLength = slice_length;
    m_numSlices = num_slices;
    m_sliceIndex = 0;
    m_lowestSliceId = 0;
    m_highestSliceId = 0;
    m_sliceStates = new HashMap<Integer,Integer>();
    m_numLabels = num_labels;
    m_otherLabels = new String[m_numLabels];
    for (int i = 0; i < m_numLabels; i++)
    {
      m_otherLabels[i] = generateRandomString(5);
    }
  }

  /**
   * Creates a new random label source
   * @param r A random generator
   * @param num_events The number of events to produce
   * @param slice_length The length of each slice
   * @param num_slices The number of slices to keep alive at any given time
   */
  public RandomLabelSource(Random r, int num_events, int slice_length, int num_slices)
  {
    this(r, num_events, slice_length, num_slices, 1);
  }

  @Override
  protected Object[] getEvent()
  {
    Object[] tuple;
    int state_size = m_sliceStates.size();
    if (state_size < m_numSlices)
    {
      // Start a new slice
      m_sliceStates.put(m_highestSliceId, 1);
      tuple = new Object[] {m_highestSliceId, m_eventCount, LABEL_START};
      m_highestSliceId++;
    }
    else
    {
      assert state_size == m_numSlices;
      int num_e = m_sliceStates.get(m_sliceIndex);
      if (num_e < m_sliceLength - 1)
      {
        // This slice is shorter than the prescribed length: emit an "other" event
        String random_label = m_otherLabels[m_random.nextInt(m_numLabels)];
        tuple = new Object[] {m_sliceIndex, m_eventCount, random_label};
        m_sliceStates.put(m_sliceIndex, num_e + 1);
        m_sliceIndex++;
        if (m_sliceIndex >= m_highestSliceId)
        {
          m_sliceIndex = m_lowestSliceId;
        }
      }
      else
      {
        // This slice has reached the prescribed length: emit "end" and
        // remove it from the map
        assert num_e == m_sliceLength;
        tuple = new Object[] {m_sliceIndex, m_eventCount, LABEL_END};
        m_sliceStates.remove(m_sliceIndex);
        m_lowestSliceId++;
        m_sliceIndex++;
        if (m_sliceIndex >= m_highestSliceId)
        {
          m_sliceIndex = m_lowestSliceId;
        }
      }
    }
    return tuple;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    throw new UnsupportedOperationException("This source cannot be duplicated");
  } 

  /**
   * Generates a new random alphanumerical string
   * @param length The length of the string
   * @return The string
   */
  protected String generateRandomString(int length)
  {
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    StringBuilder buffer = new StringBuilder(length);
    for (int i = 0; i < length; i++)
    {
      int randomLimitedInt = leftLimit + (int) 
          (m_random.nextFloat() * (rightLimit - leftLimit + 1));
      buffer.append((char) randomLimitedInt);
    }
    return buffer.toString();
  }
  
  @Override
  public void reset()
  {
    super.reset();
    m_highestSliceId = 0;
    m_lowestSliceId = 0;
    m_sliceStates.clear();
    m_sliceIndex = 0;
  }

  @Override
  public Object[] readEvent(String line)
  {
    String[] parts = line.split(",");
    Object[] tuple = new Object[parts.length];
    tuple[0] = Integer.parseInt(parts[0]);
    tuple[1] = Integer.parseInt(parts[1]);
    tuple[2] = parts[2];
    return tuple;
  }
  
  @Override
  public String printEvent(Object[] e)
  {
    StringBuilder out = new StringBuilder();
    out.append(e[0]).append(",").append(e[1]).append(",").append(e[2]);
    return out.toString();
  }
}

package pattheminer.source;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.labpal.Random;

public class FileLabelSource extends FileSource
{
  /**
   * The length of each slice
   */
  protected int m_sliceLength;

  /**
   * The number of slices to keep alive at any given time
   */
  protected int m_numSlices;
  
  /**
   * The number of event labels (other than "start" and "end") that can occur
   */
  protected int m_numLabels = 1;
  
  public FileLabelSource(Random r, int num_events, int slice_length, int num_slices, int num_labels)
  {
    super(r, num_events, "label-" + num_events + "-" + slice_length + "-" + num_slices + ".csv");
    m_sliceLength = slice_length;
    m_numSlices = num_slices;
    m_numLabels = num_labels;
  }

  @Override
  protected Object getEvent(String line)
  {
    return line.trim();
  }

  @Override
  public FileLabelSource duplicate(boolean with_state)
  {
    return new FileLabelSource(m_random, m_numEvents, m_sliceLength, m_numSlices);
  }

  @Override
  public void generateFile()
  {
    RandomLabelSource source = new RandomLabelSource(m_random, m_numEvents, m_numSlices, m_numLabels);
  }
}

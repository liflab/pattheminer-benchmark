package pattheminer.extraction;

import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.peg.Sequence;

public class SequenceSetGenerator
{
  /**
   * The source used to produce events
   */
  protected Source m_source;
  
  /**
   * The number of logs to generate
   */
  protected int m_numLogs;
  
  public SequenceSetGenerator(Source src, int num_logs)
  {
    super();
    m_source = src;
    m_numLogs = num_logs;
  }
  
  /**
   * Generates a set of sequences
   * @return A set of sequences
   */
  public Set<Sequence<?>> generateSequences()
  {
    Set<Sequence<?>> set = new HashSet<Sequence<?>>();
    for (int i = 0; i < m_numLogs; i++)
    {
      Sequence<Object> seq = new Sequence<Object>();
      Pullable p = m_source.getPullableOutput();
      while (p.hasNext())
      {
        seq.add(p.next());
      }
      set.add(seq);
      m_source.reset();
    }
    return set;
  }
}

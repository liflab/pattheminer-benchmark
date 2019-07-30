package pattheminer.forecast;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.labpal.Random;
import java.util.ArrayList;
import java.util.Enumeration;
import pattheminer.source.RandomSource;
import weka.core.Attribute;

public class RandomArraySource extends RandomSource<Object[]>
{
  /**
   * The number of attributes
   */
  protected int m_numAttributes;
  
  protected ArrayList<Object> m_classValues;
  
  public RandomArraySource(Random r, int num_events, int num_attributes, Attribute class_att)
  {
    super(r, num_events);
    m_classValues = new ArrayList<Object>();
    m_numAttributes = num_attributes;
    Enumeration<?> en = class_att.enumerateValues();
    while (en.hasMoreElements())
    {
      m_classValues.add(en.nextElement());
    }
  }
  
  @Override
  protected Object[] getEvent()
  {
    Object[] out = new Object[m_numAttributes + 1];
    for (int i = 0; i < m_numAttributes; i++)
    {
      out[i] = m_random.nextInt(100);
    }
    out[m_numAttributes] = m_classValues.get(m_random.nextInt(m_classValues.size()));
    return out;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object[] readEvent(String line)
  {
    String[] parts = line.split(",");
    return parts;
  }

  @Override
  public String printEvent(Object[] e)
  {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < e.length; i++)
    {
      if (i > 0)
      {
        out.append(",");
      }
      out.append(e[i]);
    }
    return out.toString();
  }

  @Override
  public String getFilename()
  {
    return "array-" + m_numEvents + "-" + m_numAttributes + ".csv";
  }
}

package pattheminer.source;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.labpal.Random;
import pattheminer.patterns.AverageSliceLength;

public class RSSTest
{

  public static void main(String[] args)
  {
    WeekdaySource ws = new WeekdaySource(new Random(), 20, 5);
    Pullable p = ws.getPullableOutput();
    while (p.hasNext())
    {
      Object[] o = (Object[]) p.pull();
      System.out.println(o[0] + " " + o[1]);
    }
  }

}

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
import ca.uqac.lif.azrael.size.RotePrintHandler;
import ca.uqac.lif.azrael.size.SizePrinter;
import ca.uqac.lif.azrael.size.SizeReflectionHandler;

/**
 * A custom size printer for BeepBeep processor chains
 */
public class LabSizePrinter extends SizePrinter
{
  public LabSizePrinter()
  {
    super();
    m_handlers.add(new RotePrintHandler("JSONParser", 2000));
    //m_handlers.add(new QueueSinkHandler(this));
  }
  
  
  protected static class QueueSinkHandler extends SizeReflectionHandler
  {

    public QueueSinkHandler(SizePrinter p)
    {
      super(p);
    }
    
    public boolean canHandle(Object o)
    {
      return o instanceof LabSizePrinter;
    }
    
    public Number handle(Object o) throws PrintException
    {
      int s = (Integer) super.handle(o);
      System.out.println(s);
      return s;
    }
    
  }
}

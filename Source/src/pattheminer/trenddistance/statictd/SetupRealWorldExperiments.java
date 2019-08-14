package pattheminer.trenddistance.statictd;

import static pattheminer.trenddistance.TrendExperiment.AVG_SLICE_LENGTH;
import static pattheminer.trenddistance.TrendExperiment.NUM_SLICES;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.io.ReadStringStream;
import ca.uqac.lif.cep.peg.TrendDistance;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.util.FindPattern;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.xml.ParseXml;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Random;
import ca.uqac.lif.xml.XmlElement;
import pattheminer.MainLab;
import pattheminer.patterns.AverageSliceLength;
import pattheminer.patterns.InvoiceSliceLength;
import pattheminer.source.BoundedSource;
import pattheminer.source.RandomLabelSource;

/**
 * Setup of experiments that compare a "synthetic" vs a "real-world"
 * trace on the same log.
 */
public class SetupRealWorldExperiments
{
  protected MainLab m_lab;
  
  public SetupRealWorldExperiments(MainLab lab)
  {
    super();
    m_lab = lab;
  }
  
  public void fillWithExperiments()
  {
    Random random = m_lab.getRandom();
    int width = 100;
    int slice_length = 20, num_slices = 20135, length = 134419;
    
    // Creates the "synthetic" experiment
    BoundedSource<Object[]> src = new RandomLabelSource(random, length, slice_length, num_slices);
    AverageSliceLength.SliceLength asl = new AverageSliceLength.SliceLength();
    Processor wp = new Window(asl, width);
    TrendDistance<Number,Number,Number> alarm = new TrendDistance<Number,Number,Number>(10, wp, Numbers.subtraction, 1, Numbers.isLessThan);
    StaticTrendDistanceStreamExperiment tde = StaticTrendDistanceStreamFactory.createNewTrendDistanceStreamExperiment(AVG_SLICE_LENGTH, "Subtraction", src, alarm, width, false);
    tde.setInput(NUM_SLICES, num_slices);
    tde.describe(NUM_SLICES, "The number of slices");
    tde.setInput(AVG_SLICE_LENGTH, slice_length);
    tde.describe(AVG_SLICE_LENGTH, "The length of each slice");
    tde.describe("Context", "Whether this experiment is made from a synthetic or a real-world log");
    tde.setInput("Context", "Synthetic");
    tde.setCountSize(false);
    
    // Creates the "real-world" experiment
    RoteBoundedSource rbs = new RoteBoundedSource(134419);
    InvoiceSliceLength asl2 = new InvoiceSliceLength();
    Processor wp2 = new Window(asl2, width);
    TrendDistance<Number,Number,Number> alarm2 = new TrendDistance<Number,Number,Number>(10, wp2, Numbers.subtraction, 1, Numbers.isLessThan);
    StaticTrendDistanceStreamExperiment tde2 = StaticTrendDistanceStreamFactory.createNewTrendDistanceStreamExperiment(AVG_SLICE_LENGTH, "Subtraction", rbs, alarm2, width, false);
    tde2.setInput(NUM_SLICES, num_slices);
    tde2.describe(NUM_SLICES, "The number of slices");
    tde2.setInput(AVG_SLICE_LENGTH, slice_length);
    tde2.describe(AVG_SLICE_LENGTH, "The length of each slice");
    tde2.describe("Context", "Whether this experiment is made from a synthetic or a real-world log");
    tde2.setInput("Context", "Real-world");
    tde2.setCountSize(false);
    
    m_lab.add(tde);
    m_lab.add(tde2);
    
    Group g = new Group("Real vs. synthetic");
    g.add(tde);
    g.add(tde2);
    m_lab.add(g);
    
  }
  
  protected static class RoteBoundedSource extends BoundedSource<XmlElement>
  {
    transient ReadStringStream rss = null;
    
    transient FindPattern feeder = new FindPattern("(<Event>.*?</Event>)");
    
    transient ApplyFunction to_xml = new ApplyFunction(ParseXml.instance);
    
    transient Pullable xml_pullable;
    
    public RoteBoundedSource(int num_events)
    {
      super(num_events);
      try
      {
        rss = new ReadStringStream(new FileInputStream(new File("/tmp/log.xml")));
      }
      catch (FileNotFoundException e)
      {
        throw new ProcessorException(e);
      }
      Connector.connect(rss, feeder);
      Connector.connect(feeder, to_xml);
      xml_pullable = to_xml.getPullableOutput();
    }

    @Override
    protected XmlElement getEvent()
    {
      if (!xml_pullable.hasNext())
      {
        return null;
      }
      return (XmlElement) xml_pullable.pull();
    }

    @Override
    public XmlElement readEvent(String line)
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String printEvent(XmlElement e)
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String getFilename()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public Processor duplicate(boolean arg0)
    {
      return this;
    }
    
  }
}

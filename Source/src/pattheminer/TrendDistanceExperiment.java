package pattheminer;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.peg.TrendDistance;
import ca.uqac.lif.cep.tmf.BlackHole;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.json.JsonList;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.ExperimentException;

public class TrendDistanceExperiment extends Experiment
{
  public static final transient String METRIC = "Metric";
  
  public static final transient String TREND = "Trend function";
  
  public static final transient String WIDTH = "Window width";
  
  public static final transient String TIME = "Running time";
  
  public static final transient String LENGTH = "Stream length";
  
  protected transient TrendDistance<?,?,?> m_trendDistance;
  
  protected transient Source m_source;
  
  protected int m_eventStep = 1000;
  
  public TrendDistanceExperiment()
  {
    super();
    setDescription("");
    describe(METRIC, "The metric used to compute the distance between the reference trend and the computed trend");
    describe(TREND, "The trend computed on the event stream");
    describe(WIDTH, "The width of the window over which the trend is computed");
    describe(TIME, "Cumulative running time (in ms)");
    describe(LENGTH, "Number of events processed");
    JsonList x = new JsonList();
    x.add(0);
    write(LENGTH, x);
    JsonList y = new JsonList();
    y.add(0);
    write(TIME, y);
  }
  
  protected void setTrendDistance(TrendDistance<?,?,?> td)
  {
    m_trendDistance = td;
  }
  
  protected void setSource(Source s)
  {
    m_source = s;
  }
  
  protected void setEventStep(int step)
  {
    m_eventStep = step;
  }

	@Override
	public void execute() throws ExperimentException, InterruptedException 
	{
	  JsonList length = (JsonList) read(LENGTH);
	  JsonList time = (JsonList) read(TIME);
	  // Setup processor chain
	  Pullable s_p = m_source.getPullableOutput();
		Pushable t_p = m_trendDistance.getPushableInput();
		BlackHole hole = new BlackHole();
		Connector.connect(m_trendDistance, hole);
		long start = System.currentTimeMillis();
		int event_count = 0;
		while (s_p.hasNext())
		{
		  if (event_count % m_eventStep == 0 && event_count > 0)
		  {
		    long lap = System.currentTimeMillis();
		    length.add(event_count);
		    time.add(lap - start);
		    float prog = ((float) event_count) / ((float) MainLab.MAX_TRACE_LENGTH);
		    setProgression(prog);
		  }
		  Object o = s_p.pull();
		  t_p.push(o);
		  event_count++;
		}
	}
}

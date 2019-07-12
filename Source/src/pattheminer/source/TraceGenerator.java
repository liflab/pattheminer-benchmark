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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.labpal.CliParser;
import ca.uqac.lif.labpal.CliParser.Argument;
import ca.uqac.lif.labpal.CliParser.ArgumentMap;
import ca.uqac.lif.labpal.Random;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Command line front-end to generate traces files in the same format as
 * those used in the lab's experiments. The generator's behaviour is controlled
 * through command line arguments.
 * <p>
 * <h3>Global parameters</h3>
 * <table border="1">
 * <tr><th>Argument</th><th>Function</th></tr>
 * <tr>
 *   <td><tt>--type</tt> <i>t</i></td>
 *   <td>Determines the type of trace to generate. Possible values for <i>t</i> are "label", "number", "symbol".</td>
 * </tr>
 * <tr>
 *   <td><tt>--length</tt> <i>n</i></td>
 *   <td>Generates a trace with <i>n</i> events</td>
 * </tr>
 * <tr>
 *   <td><tt>--file</tt> <i>name</i></td>
 *   <td>Writes the output to file <i>name</i> (default: print to stdout)</td>
 * </tr>
 * <tr>
 *   <td><tt>--seed</tt> <i>x</i></td>
 *   <td>Sets the random seed to <i>x</i></td>
 * </tr>
 * </table>
 * 
 * <h3>Parameters for the <tt>symbol</tt> trace type</h3>
 * <table border="1">
 * <tr><th>Argument</th><th>Function</th></tr>
 * <tr>
 *   <td><tt>--values</tt> <i>n</i></td>
 *   <td>Events can contain up to <i>n</i> distinct values</td>
 * </tr>
 * </table>
 * 
 * <h3>Parameters for the <tt>label</tt> trace type</h3>
 * <table border="1">
 * <tr><th>Argument</th><th>Function</th></tr>
 * <tr>
 *   <td><tt>--slices</tt> <i>n</i></td>
 *   <td>Generates a trace containing <i>n</i> simultaneous slices</td>
 * </tr>
 * <tr>
 *   <td><tt>--slice-length</tt> <i>n</i></td>
 *   <td>Sets each slice to contain <i>n</i> events</td>
 * </tr>
 * <tr>
 *   <td><tt>--labels</tt> <i>n</i></td>
 *   <td>Sets number of distinct event labels to <i>n</i></td>
 * </tr>
 * </table>
 */
public class TraceGenerator
{
  /**
   * OS-dependent carriage return
   */
  protected static final String CRLF = System.getProperty("line.separator");

  /**
   * A string to call the program
   */
  protected static final String USAGE_STRING = "Usage: java -jar generator.jar [options]";

  /**
   * Main program loop
   * @param args Command line arguments
   */
  public static void main(String[] args)
  {
    CliParser parser = new CliParser();
    parser.addArgument(new Argument().withLongName("type").withArgument("t").withDescription("Generate traces of type t (number, symbol, label)"));
    parser.addArgument(new Argument().withLongName("length").withArgument("x").withDescription("Generate x events"));
    parser.addArgument(new Argument().withLongName("seed").withArgument("x").withDescription("Set random seed to x"));
    parser.addArgument(new Argument().withLongName("slices").withArgument("n").withDescription("Generate n slices"));
    parser.addArgument(new Argument().withLongName("slice-length").withArgument("n").withDescription("Each slice has length n"));
    parser.addArgument(new Argument().withLongName("labels").withArgument("n").withDescription("Set number of distinct event labels"));
    parser.addArgument(new Argument().withLongName("file").withArgument("name").withDescription("Save to file name"));
    parser.addArgument(new Argument().withLongName("values").withArgument("n").withDescription("Events can contain up to n distinct values"));
    ArgumentMap parameters = parser.parse(args);
    if (parameters == null)
    {
      System.err.println("ERROR: Invalid arguments");
      parser.printHelp(USAGE_STRING, System.err);
      System.exit(-1);
    }
    if (!parameters.hasOption("length"))
    {
      System.err.println("ERROR: No length specified");
      parser.printHelp(USAGE_STRING, System.err);
      System.exit(1);
    }
    int num_events = Integer.parseInt(parameters.getOptionValue("length"));
    RandomSource source = null;
    String type = parameters.getOptionValue("type");
    if (type.compareToIgnoreCase("label") == 0)
    {
      // Random label source
      source = getRandomLabelSource(parameters, getRandom(parameters), num_events);
    }
    else if (type.compareToIgnoreCase("number") == 0)
    {
      // Random number source
      source = getRandomNumberSource(parameters, getRandom(parameters), num_events);
    }
    else if (type.compareToIgnoreCase("symbol") == 0)
    {
      // Random symbol source
      source = getRandomSymbolSource(parameters, getRandom(parameters), num_events);
    }
    if (source == null)
    {
      System.err.println("ERROR: Invalid trace type");
      parser.printHelp(USAGE_STRING, System.err);
      System.exit(2);
    }
    PrintStream out = getPrintStream(parameters);
    // Print events
    Pump pump = new Pump();
    ApplyFunction format = new ApplyFunction(PrintArray.instance);
    Print print = new Print(out).setSeparator(CRLF);
    Connector.connect(source, pump, format, print);
    pump.run();
    out.close();
    System.exit(0);
  }

  protected static Random getRandom(ArgumentMap parameters)
  {
    int seed = 0;
    if (parameters.hasOption("seed"))
    {
      seed = Integer.parseInt(parameters.getOptionValue("seed"));
    }
    return new Random(seed);
  }
  
  protected static PrintStream getPrintStream(ArgumentMap parameters)
  {
    PrintStream out = System.out;
    if (parameters.hasOption("file"))
    {
      FileOutputStream fos;
      try
      {
        fos = new FileOutputStream(parameters.getOptionValue("file"));
        out = new PrintStream(fos);
      }
      catch (FileNotFoundException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return out;
  }
  
  protected static RandomNumberSource getRandomNumberSource(ArgumentMap parameters, Random rand, int num_events)
  {
    return new RandomNumberSource(rand, num_events);
  }
  
  protected static RandomSymbolSource getRandomSymbolSource(ArgumentMap parameters, Random rand, int num_events)
  {
    int max_value = 1000;
    if (parameters.hasOption("values"))
    {
      max_value = Integer.parseInt(parameters.getOptionValue("values"));
    }
    return new RandomSymbolSource(rand, num_events, max_value);
  }

  protected static RandomLabelSource getRandomLabelSource(ArgumentMap parameters, Random rand, int num_events)
  {
    int num_slices = 1000, num_labels = 5, slice_length = 1000;
    if (parameters.hasOption("slices"))
    {
      num_slices = Integer.parseInt(parameters.getOptionValue("slices"));
    }
    if (parameters.hasOption("labels"))
    {
      num_labels = Integer.parseInt(parameters.getOptionValue("labels"));
    }
    if (parameters.hasOption("slice-length"))
    {
      slice_length = Integer.parseInt(parameters.getOptionValue("slice-length"));
    }
    return new RandomLabelSource(rand, num_events, slice_length, num_slices, num_labels);
  }

  /**
   * Function that turns an array into a string
   */
  protected static class PrintArray extends UnaryFunction<Object[],String>
  {
    /**
     * A single public instance of the function
     */
    public static final transient PrintArray instance = new PrintArray();

    protected PrintArray()
    {
      super(Object[].class, String.class);
    }

    @Override
    public String getValue(Object[] x)
    {
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < x.length; i++)
      {
        if (i > 0)
        {
          out.append(",");
        }
        out.append(x[i]);
      }
      return out.toString();
    }
  }
}

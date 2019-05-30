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
package pattheminer.forecast;

/**
 * Parent class for all experiments involving a learning part.
 */
public abstract class LearningExperiment extends PredictionExperiment
{
  /**
   * The name of the pattern being learned
   */
  public static final transient String PATTERN = "Pattern";
  
  /**
   * One of the possible patterns: average duration
   */
  public static final transient String PATTERN_AVG_DURATION = "Average duration";
  
  /**
   * One of the possible patterns: most probable next event
   */
  public static final transient String PATTERN_NEXT_EVENT = "Most probable next event";
  
  /**
   * One of the possible patterns: learn a classifier on features
   */
  public static final transient String PATTERN_CLASSIFIER = "Classifier learning";
  
  /**
   * The number of labels
   */
  public static final transient String NUM_LABELS = "Labels";
  
  /**
   * The offset between the "trend" and the "class" windows
   */
  public static final transient String T = "t";
    
  /**
   * Width of the class window
   */
  public static final transient String N = "n";
  
  /**
   * Creates a new empty learning experiment. You should not call this
   * constructor directly.
   */
  LearningExperiment()
  {
    super();
  }
  
  /**
   * Creates a new prediction experiment
   */
  public LearningExperiment(int t, int m, int n)
  {
    super();
    describe(T, "Offset (in number of events) between the trend and the class windows");
    describe(N, "Width of the class window");
    describe(NUM_LABELS, "The number of distinct event names that can occur in a log");
    setInput(T, t);
    setInput(M, m);
    setInput(N, n);
  }

}

package pattheminer.classifiers;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.peg.weka.WekaUtils;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Region;
import java.util.Collection;
import pattheminer.ClassifierExperiment;
import pattheminer.ClassifierTrainingExperiment;
import pattheminer.MainLab;
import pattheminer.patterns.ExtractAttributes;
import weka.core.Attribute;

public class SetupClassifierExperiments
{
  protected MainLab m_lab;
  
  public static void populate(MainLab lab)
  {
    SetupClassifierExperiments cm = new SetupClassifierExperiments(lab);
    cm.fillWithExperiments();
  }

  protected SetupClassifierExperiments(/*@ non_null @*/ MainLab lab)
  {
    super();
    m_lab = lab;
  }

  /**
   * Populates the lab with experiments
   */
  protected void fillWithExperiments()
  {
    // Self-trained class prediction experiments
    {
      Group g = new Group("Self-trained class prediction throughput");
      g.setDescription("Measures the throughput of the self-trained class prediction processor for various trend computations.");
      m_lab.add(g);
      Region reg = new Region();
      reg.add(ClassifierExperiment.NUM_FEATURES, 1, 3, 5);
      reg.add(ClassifierExperiment.LEARNING_ALGORITHM, "J48");
      for (Region r_ll : reg.all(ClassifierExperiment.LEARNING_ALGORITHM, ClassifierExperiment.NUM_FEATURES))
      {
        ClassifierTrainingExperiment cte = getClassifierTrainingExperiment(r_ll);
        m_lab.add(cte);
      }
    }
  }

  /**
   * Generates a new classifier training experiment with given parameters,
   * or fetches the existing one if it already exists.
   * @param r The region describing the experiment's parameters
   * @return The experiment
   */
  /*@ null @*/ protected ClassifierTrainingExperiment getClassifierTrainingExperiment(/*@ non_null @*/ Region r)
  {
    Collection<Experiment> col = m_lab.filterExperiments(r, ClassifierTrainingExperiment.class);
    if (col.isEmpty())
    {
      // Experiment does not exist
      int num_features = r.getInt(ClassifierExperiment.NUM_FEATURES);
      String algo_name = r.getString(ClassifierExperiment.LEARNING_ALGORITHM);
      int update_interval = r.getInt(ClassifierExperiment.UPDATE_INTERVAL);
      Processor beta = new ExtractAttributes(num_features);
      Processor kappa = new ApplyFunction(new NthElement(num_features));
      Attribute[] atts = createDummyAttributes(num_features);
      ClassifierTrainingExperiment cte = new ClassifierTrainingExperiment(algo_name, WekaUtils.getClassifier(algo_name), update_interval, beta, kappa, 1, 1, 1, atts);
      m_lab.add(cte);
      return cte;
    }
    else
    {
      for (Experiment e : col)
      {
        return (ClassifierTrainingExperiment) e;
      }
    }
    return null;
  }
  
  /**
   * Creates an array of dummy numerical attributes.
   * @param num_attributes The number of attributes to create.
   * @return The array of attributes
   */
  /*@ requires num_attributes >= 0 @*/
  protected static Attribute[] createDummyAttributes(int num_attributes)
  {
    Attribute[] atts = new Attribute[num_attributes];
    for (int i = 0; i < num_attributes; i++)
    {
      atts[i] = new Attribute(Integer.toString(i));
    }
    return atts;
  }
}

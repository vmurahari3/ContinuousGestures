package com.example.ubicomp.continuousgestures.Learning;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.example.ubicomp.continuousgestures.Constants.Constants;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import com.example.ubicomp.continuousgestures.Helpers.Utils;


/**
 * Created by Aman Parnami on 10/2/14.
 */
public class DataAnalyzer
{
    private static final String TAG = DataAnalyzer.class.getName();
    private final static boolean DEBUG = true;
    public String currentLabel = "";
    private Context context;

    // Classes
    public FeatureExtractionHelper featureExtractor;

    // Weka variables

    /** List of attributes for Weka */
    public ArrayList<Attribute> arffAttributes;

    /** List of class labels for Weka "result" attribute */
    public ArrayList<String> arffClassLabels;

    /** Holds instances of training data */
    public Instances arffTrainingInstances;

    /** Holds instances of test data */
    public Instances arffTestInstances;

    String[] class_labels;

    public Classifier classifier;

    /** Kernel for classifier */
    PolyKernel polyKernel = new PolyKernel();

    public DataAnalyzer(Context c) {
        // Class to calculate features
        featureExtractor = new FeatureExtractionHelper();

        arffAttributes = new ArrayList<Attribute>();
        arffClassLabels = new ArrayList<String>();
        context = c;
    }

    public void init()
    {
        arffAttributes = new ArrayList<Attribute>();
        arffClassLabels = new ArrayList<String>();

        class_labels = new String[0];

        // Set the ArffAttributes headers (important to do this before creating the Instances object)
        setArffAttributesHeaders(class_labels);

        // Initialized with arffAttributes (which must be populated ahead of time)
        arffTrainingInstances = new Instances("GestureTraining", arffAttributes, 0);
        arffTestInstances = new Instances("GestureTesting", arffAttributes, 0);


        // Set the class index label for the instance sets
        arffTrainingInstances.setClassIndex(arffTrainingInstances.numAttributes()-1);
        arffTestInstances.setClassIndex(arffTrainingInstances.numAttributes()-1);
        classifier = new SMO();

        // Classifier options - fit logistic models to SVM output so that we can get prediction values
        // More options: http://weka.sourceforge.net/doc.dev/weka/classifiers/functions/SMO.html
        String[] options = {"-M"};

        // Set exponent to cubic polynomial. Default is 1.0
        // TODO: Understand the difference between linear and cubic polynomial kernels
        PolyKernel polyKernel = new PolyKernel();
        polyKernel.setExponent(3.0);

        try {
            ((SMO) classifier).setOptions(options);
            ((SMO) classifier).setKernel(polyKernel);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //By default set a unary classifier


//       else { //Multi-class classifier
//            classifier = new SMO();
//            String[] options = {"-M"};
//            try {
//                ((SMO) classifier).setOptions(options);
////                ((LibSVM)classifier).setProbabilityEstimates(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }


    //TODO Extract the common functionality from calculateTrainingFeatures and calculateTestingFeatures into a function
    /**
     * Method to calculate features and store instances of training data
     * @param dataObject
     */
    public void calculateTrainingFeatures(ArrayList<Double[]> dataObject, String trainingLabel)
    {
        // Initialize new ArrayList for features values
        ArrayList<Double> returnedValues = new ArrayList<Double>();

        // Features returned in ArrayList<Double>
        returnedValues = featureExtractor.calculateFeatures(dataObject);

        // Add gesture label index to ArrayList
        returnedValues.add((double) arffTrainingInstances.classAttribute().indexOfValue(trainingLabel));

        // Pass features into double[]
        double[] featureValues = new double[arffAttributes.size()];
        featureValues = Utils.list2Array(returnedValues);

        // New instance with current feature values and label
        DenseInstance newInstance = new DenseInstance(1.0, featureValues);

        // Add new instance to dataset
        arffTrainingInstances.add(newInstance);

        // Debug
//        Log.d("DataAnalyzer", arffTrainingInstances.toString());
    }

    /**
     * Method to calculate features and store instances of test data
     * @param dataObject
     */
    public void calculateTestFeatures(ArrayList<Double[]> dataObject)
    {
        // Initialize new ArrayList for features values
        ArrayList<Double> returnedValues = new ArrayList<Double>();

        // Features returned in ArrayList<Double>
        returnedValues = featureExtractor.calculateFeatures(dataObject);

        // Add gesture label to ArrayList
        // Leave last index empty by default
        // Classifier will label it for us
        //returnedValues.add((double) arffTestInstances.attribute(arffAttributes.size()-1).indexOfValue(Controller.getGestureTrainingLabel()));

        // Pass features into double[]
        double[] featureValues = new double[arffAttributes.size()];
        for(int i=0; i<returnedValues.size(); i++) featureValues[i] = returnedValues.get(i);

        // New instance with current feature values and label
        DenseInstance newInstance = new DenseInstance(1.0, featureValues);

        // Add new instance to dataset
        arffTestInstances.add(newInstance);

        // Debug
        //System.out.println(arffTestInstances);

    }

    /**
     * Method to build the classifier with current set of training instances
     */
    public void buildClassifier()
    {
        try
        {
            if(arffTrainingInstances.size() > 0) classifier.buildClassifier(arffTrainingInstances);
            if(DEBUG) Log.d(TAG,"Classifier built using training instances");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method to add a new test instance
     */
    public void addTestSample(ArrayList<Double[]> dataObject)
    {
        arffTestInstances = new Instances("GestureTesting", arffAttributes, 0);
        arffTestInstances.setClassIndex(arffTrainingInstances.numAttributes()-1);

        calculateTestFeatures(dataObject);
    }

    /**
     * Method to classify current window of data (last instance in the test set)
     */
    public String classifyGesture()
    {
        double result = 0;
        double[] distribution;

        if(arffTestInstances.numInstances() >= 1)
        {
            try
            {
                result = classifier.classifyInstance(arffTestInstances.lastInstance());

                distribution = classifier.distributionForInstance(arffTestInstances.lastInstance());

//                if(DEBUG) Log.d(TAG, "Distribution: "+Utils.doubleArray2CSVString(distribution));
//                if(DEBUG) Log.d(TAG, "Result: "+result);

                if(Double.isNaN(result)) { //Unary classifier returns NaN when a match is not found
                    return "No match! Try again.";
                } else {
                    int classifierLabelIndex = (int) result;
                    return class_labels[classifierLabelIndex];
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Log.w(TAG, "No instances to classify");
        }

        return "";
    }

    /**
     * Method to create the headers for the instance file
     */
    public void setArffAttributesHeaders(String[] classLabels)
    {
        // Add RMS feature header
        for(int i=0; i<Constants.NUM_OF_CHANNELS; i++)
        {
            arffAttributes.add(new Attribute(Constants.GESTURE_CHANNEL_LABELS[i] + "_RMS"));
        }

        // Add stdev feature header
        for(int i=0; i<Constants.NUM_OF_CHANNELS; i++)
        {
            arffAttributes.add(new Attribute(Constants.GESTURE_CHANNEL_LABELS[i] + "_StDev"));
        }

        // Add mean feature header
        for(int i=0; i<Constants.NUM_OF_CHANNELS; i++)
        {
            arffAttributes.add(new Attribute(Constants.GESTURE_CHANNEL_LABELS[i] + "_Mean"));
        }

        // Add energy feature header
        for(int i=0; i<Constants.NUM_OF_CHANNELS; i++)
        {
            arffAttributes.add(new Attribute(Constants.GESTURE_CHANNEL_LABELS[i] + "_AvgEnergy"));
        }

        // Add zero crossing headers for gyroscopes
        for(int i=Constants.NUM_OF_ACCEL_CHANNELS; i<Constants.NUM_OF_CHANNELS; i++)
        {
            arffAttributes.add(new Attribute(Constants.GESTURE_CHANNEL_LABELS[i] + "_ZeroCross"));
        }

        // Add ECDF headers, goes over acceleration channels only
        for(int i=0; i<Constants.NUM_OF_ACCEL_CHANNELS; i++)
        {
            String channelLabel = Constants.GESTURE_CHANNEL_LABELS[i];
            for (int j = 0; j < Constants.ECDF_LENGTH; j++)
            {
                arffAttributes.add(new Attribute(channelLabel + "_ECDF" + j));
            }
        }

        for(String s : classLabels) arffClassLabels.add(s);
        arffAttributes.add(new Attribute("@@class@@", arffClassLabels));
    }

    public void addNewClass(String className) {

    }

    public void changeClassName(String oldName, String newName) {

    }

    public void removeClass(String className) {

    }

    public void addTrainingInstance(ArrayList<Double[]> dataObject, String className) {

    }

    public void removeTrainingInstance(int instanceId, String className) {

    }

    public ArrayList<Double> crossValidationSingleRun() {
        // loads data and set class index
        Instances data = arffTrainingInstances;
        Double result = null;

        // classifier
        Classifier cls = classifier;

        // other options
        int seed  = 1;

        //Number of folds cannot be more than number of instances
        int folds = (arffTrainingInstances.size() > 10)? 10: arffTrainingInstances.size();

        // randomize data
        Random rand = new Random(seed);
        Instances randData = new Instances(data);
        randData.randomize(rand);
        if (randData.classAttribute().isNominal())
            randData.stratify(folds);

        // perform cross-validation
        Evaluation eval = null;
        try {
            eval = new Evaluation(randData);
            // build and evaluate classifier
            Classifier clsCopy = null;
            clsCopy = AbstractClassifier.makeCopy(cls);

            try {
                eval.crossValidateModel(clsCopy, randData,folds, rand);
                result = eval.pctCorrect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(DEBUG) Log.d(TAG, "Goodness scores: ");
        ArrayList<Double> goodnessValues = new ArrayList<Double>();
        for(int i = 0; i < arffClassLabels.size(); i++)
        {
            goodnessValues.add(eval.fMeasure(i));
            if(DEBUG) Log.d(TAG, arffClassLabels.get(i) + " - " + eval.fMeasure(i));
        }

//        if(DEBUG) Log.d(TAG, "Cross validation completed.\nPercent correct: "  + result + "\n");
//        if(DEBUG) Log.d(TAG, eval.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));

        return goodnessValues;
    }

    public void loadArffFile(String filePath)
    {

        ArffLoader loader = new ArffLoader();

        try
        {
            InputStream is = context.getAssets().open("combined_gesture.arff");

            loader.setSource(is);
            arffTrainingInstances = loader.getStructure();
            arffTestInstances = loader.getStructure();
            arffTrainingInstances = loader.getDataSet();
            arffTestInstances.setRelationName("GestureTesting");
            arffTrainingInstances.setClassIndex(arffTrainingInstances.numAttributes() - 1);

//            arffClassLabels = Collections.list(loader.getStructure().classAttribute().enumerateValues());
            arffClassLabels = Collections.list(arffTrainingInstances.classAttribute().enumerateValues());

            class_labels = new String[arffClassLabels.size()];

            for(int i=0; i<arffClassLabels.size();++i)
            {
                class_labels[i] = arffClassLabels.get(i);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG,"GestureClassification: no model file exists");
        }
    }


}

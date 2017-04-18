package edu.gatech.ubicomp.continuousgestures.data.learning;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import edu.gatech.ubicomp.continuousgestures.R;
import edu.gatech.ubicomp.continuousgestures.common.Constants;
import edu.gatech.ubicomp.continuousgestures.common.DependencyUtil;
import edu.gatech.ubicomp.continuousgestures.common.rx.RxEventBus;
import edu.gatech.ubicomp.continuousgestures.data.models.SampleFeatureVector;
import edu.gatech.ubicomp.continuousgestures.data.models.GestureClass;
import edu.gatech.ubicomp.continuousgestures.data.models.GestureSample;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import edu.gatech.ubicomp.continuousgestures.common.Utils;


/**
 * Created by Aman Parnami on 10/2/14.
 */
public class DataAnalyzer
{
    private static final String TAG = DataAnalyzer.class.getName();
    private final static boolean DEBUG = true;


    @Inject
    public RxEventBus mRxEventBus;

    public String currentLabel = "";
    private Context mContext;

    // Classes
    private FeatureExtractor mFeatureExtractor;

    // Weka variables

    /** List of attributes for Weka */
    private ArrayList<Attribute> mFeatureNames = new ArrayList<>();

    /** List of class labels for Weka "result" attribute */
    private ArrayList<String> mClassLabels = new ArrayList<>();

    /** Holds instances of training data */
    private Instances mTrainingInstances;

    /** Holds instances of test data */
    private Instances mTestInstances;

    String[] class_labels;

    private Classifier mClassifier;

    /** Kernel for mClassifier */
    PolyKernel polyKernel = new PolyKernel();

    public DataAnalyzer(Context c) {
        // Class to calculate features
        mFeatureExtractor = new FeatureExtractor();
        mContext = c;
        DependencyUtil.inject(c, this);
        initialize();
    }

    private void initialize() {
        mClassLabels.clear();
//        // Assuming that the number of devices in the first gesture is same as all the gestures.
//        mNumOfGestureDevices = mGesturesForDataAnalysis.get(0).mDeviceToBodyPositions.size();
//        for (GestureClass gestureClass : mGesturesForDataAnalysis) {
//            mClassLabels.add(String.valueOf(gestureClass.id));
//        }

        //TODO Populate mClassLabels

        setFeatureNames();
        initializeTrainingInstance();
        initClassifier();
        loadArffFile("file:///android_asset/null_class_model_final.arff");

//        addExistingDataToTrainingInstance();
        buildClassifier();
    }

    /**
     * Method to create the headers for the instance file
     */
    public void setFeatureNames()
    {
        // Add RMS feature header
        for(int i = 0; i< Constants.NUM_OF_CHANNELS; i++)
        {
            mFeatureNames.add(new Attribute(Constants.GESTURE_CHANNEL_LABELS[i] + "_RMS"));
        }

        // Add stdev feature header
        for(int i=0; i<Constants.NUM_OF_CHANNELS; i++)
        {
            mFeatureNames.add(new Attribute(Constants.GESTURE_CHANNEL_LABELS[i] + "_StDev"));
        }

        // Add mean feature header
        for(int i=0; i<Constants.NUM_OF_CHANNELS; i++)
        {
            mFeatureNames.add(new Attribute(Constants.GESTURE_CHANNEL_LABELS[i] + "_Mean"));
        }

        // Add energy feature header
        for(int i=0; i<Constants.NUM_OF_CHANNELS; i++)
        {
            mFeatureNames.add(new Attribute(Constants.GESTURE_CHANNEL_LABELS[i] + "_AvgEnergy"));
        }

        // Add zero crossing headers for gyroscopes
        for(int i=Constants.NUM_OF_ACCEL_CHANNELS; i<Constants.NUM_OF_CHANNELS; i++)
        {
            mFeatureNames.add(new Attribute(Constants.GESTURE_CHANNEL_LABELS[i] + "_ZeroCross"));
        }

        // Add ECDF headers, goes over acceleration channels only
        for(int i=0; i<Constants.NUM_OF_ACCEL_CHANNELS; i++)
        {
            String channelLabel = Constants.GESTURE_CHANNEL_LABELS[i];
            for (int j = 0; j < Constants.ECDF_LENGTH; j++)
            {
                mFeatureNames.add(new Attribute(channelLabel + "_ECDF" + j));
            }
        }

//        for(String s : classLabels) mClassLabels.add(s);
        mFeatureNames.add(new Attribute("@@class@@", mClassLabels));
    }

    private void initializeTrainingInstance() {
        mTrainingInstances = new Instances("GestureTraining", mFeatureNames, 0);
        mTrainingInstances.setClassIndex(mTrainingInstances.numAttributes() - 1);
    }

    private void initClassifier() {
        mClassifier = new SMO();

        // Classifier options - fit logistic models to SVM output so that we can get prediction values
        // More options: http://weka.sourceforge.net/doc.dev/weka/classifiers/functions/SMO.html
        String[] options = {"-M"};

        // Set exponent to cubic polynomial. Default is 1.0
        // TODO: Understand the difference between linear and cubic polynomial kernels
        PolyKernel polyKernel = new PolyKernel();
        polyKernel.setExponent(3.0);

        try {
            ((SMO) mClassifier).setOptions(options);
            ((SMO) mClassifier).setKernel(polyKernel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to build the mClassifier with current set of training instances
     */
    public void buildClassifier() {
        try {
            if (mTrainingInstances.size() > 0) {
                mClassifier.buildClassifier(mTrainingInstances);
            }
            if (DEBUG) Log.d(TAG, "Classifier built using training instances");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Method to add a new test instance
     */
    public String classifyTestSample(GestureSample testSample) {
        Instances testInstances = new Instances("GestureTesting", mFeatureNames, 0);
        testInstances.setClassIndex(mTrainingInstances.numAttributes() - 1);
        testInstances.add(getFeatureDenseInstance(testSample.featureVectors));
        return classifyGesture(testInstances.lastInstance());
    }

    /**
     * Method to classify current window of data (last instance in the test set)
     */
    public String classifyGesture(Instance testInstance) {
        double result;
        double[] distribution;
        String identifiedGestureName = mContext.getString(R.string.no_match_found);

        try {
            result = mClassifier.classifyInstance(testInstance);

            if (DEBUG) {
                Log.d(TAG, "Result: " + result);
            }

            if (!Double.isNaN(result)) { //Unary mClassifier returns NaN when a match is not found
                int classifierLabelIndex = (int) result;
                identifiedGestureName = getGestureName(Long.valueOf(mClassLabels.get(classifierLabelIndex)));
                Log.d(TAG, "Result " + identifiedGestureName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return identifiedGestureName;
    }

    private String getGestureName(long id) {
        for (GestureClass gestureClass : mGesturesForDataAnalysis) {
            if (gestureClass.id == id) {
                return gestureClass.name;
            }
        }

        return mContext.getString(R.string.no_match_found);
    }

    private DenseInstance getFeatureDenseInstance(ArrayList<SampleFeatureVector> sampleFeatureVectors) {
        ArrayList<Double> featureVector = new ArrayList<>();
        for (SampleFeatureVector sampleFeatureVector : sampleFeatureVectors) {
            featureVector.addAll(sampleFeatureVector.getFeatureVector());
        }
        // New instance with current feature values and label
        return new DenseInstance(1.0, Utils.list2Array(featureVector));
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
        returnedValues = mFeatureExtractor.calculateFeatures(dataObject);

        // Add gesture label index to ArrayList
        returnedValues.add((double) mTrainingInstances.classAttribute().indexOfValue(trainingLabel));

        // Pass features into double[]
        double[] featureValues = new double[mFeatureNames.size()];
        featureValues = Utils.list2Array(returnedValues);

        // New instance with current feature values and label
        DenseInstance newInstance = new DenseInstance(1.0, featureValues);

        // Add new instance to dataset
        mTrainingInstances.add(newInstance);

        // Debug
//        Log.d("DataAnalyzer", mTrainingInstances.toString());
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
        returnedValues = mFeatureExtractor.calculateFeatures(dataObject);

        // Add gesture label to ArrayList
        // Leave last index empty by default
        // Classifier will label it for us
        //returnedValues.add((double) mTestInstances.attribute(mFeatureNames.size()-1).indexOfValue(Controller.getGestureTrainingLabel()));

        // Pass features into double[]
        double[] featureValues = new double[mFeatureNames.size()];
        for(int i=0; i<returnedValues.size(); i++) featureValues[i] = returnedValues.get(i);

        // New instance with current feature values and label
        DenseInstance newInstance = new DenseInstance(1.0, featureValues);

        // Add new instance to dataset
        mTestInstances.add(newInstance);

        // Debug
        //System.out.println(mTestInstances);

    }

    public void loadArffFile(String filePath)
    {

        ArffLoader loader = new ArffLoader();

        try
        {
            InputStream is = mContext.getAssets().open("segmentation_gesture_model.arff");

            loader.setSource(is);
            mTrainingInstances = loader.getStructure();
            mTestInstances = loader.getStructure();
            mTrainingInstances = loader.getDataSet();
            mTestInstances.setRelationName("GestureTesting");
            mTrainingInstances.setClassIndex(mTrainingInstances.numAttributes() - 1);

//            mClassLabels = Collections.list(loader.getStructure().classAttribute().enumerateValues());
            mClassLabels = Collections.list(mTrainingInstances.classAttribute().enumerateValues());

            class_labels = new String[mClassLabels.size()];

            for(int i = 0; i< mClassLabels.size(); ++i)
            {
                class_labels[i] = mClassLabels.get(i);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG,"GestureClassification: no model file exists");
        }
    }


}

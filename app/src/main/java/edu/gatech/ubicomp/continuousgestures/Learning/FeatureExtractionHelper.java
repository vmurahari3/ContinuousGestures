package edu.gatech.ubicomp.continuousgestures.Learning;

/**
 * Created by ubicomp on 3/14/17.
 */


import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import edu.gatech.ubicomp.continuousgestures.Constants.Constants;


public class FeatureExtractionHelper
{
    private static final String TAG = FeatureExtractionHelper.class.getSimpleName();
    private final static boolean DEBUG = false;

    PearsonsCorrelation correlationCalculator;
    double[][] powerSpectrum = new double[Constants.NUM_OF_CHANNELS][Constants.WIN_SIZE/2];

    /**
     * Method to calculate features of interest
     * @param dataArray
     * @return
     */
    public ArrayList<Double> calculateFeatures(ArrayList<Double[]> dataArray)
    {
        ArrayList<Double> calcValues = new ArrayList<>();

        // RMS for all channels
        for(Double elements : calculateRMS(dataArray)) calcValues.add(elements);

        // StDev for all channels
        for(Double elements : calculateStDev(dataArray)) calcValues.add(elements);

        // Mean for all channels
        for(Double elements : calculateMean(dataArray)) calcValues.add(elements);

//		// Kurtosis for all channels
//		for(Double elements : calculateKurtosis(dataArray)) calcValues.add(elements);

        //Energy for each channel
        for(Double elements : calculateSensorEnergy(dataArray)) calcValues.add(elements);

        // Correlation for sensor pairs
//		for(Double elements: calculateCorrelation(dataArray)) calcValues.add(elements);

        // Power spectrum and statistics for all channels
//		for(Double elements: calculateFFT(dataArray)) calcValues.add(elwements);

        // Cepstrum of the power spectrum and statistics
        //for(Double elements: calculateCepstrum()) calcValues.add(elements);
        //calculateCepstrum();

        // Zero crossing rate
        for (Double elements : calculateZeroCrossing(dataArray))
            calcValues.add(elements);

        // ECDF for accelerometer channels only
        for(Double elements: calculateECDF(dataArray))
            calcValues.add(elements);

        return calcValues;
    }

    /**
     * Method to calculate the RMS for given window of data
     * @param dataArray
     * @return
     */
    public ArrayList<Double> calculateRMS(ArrayList<Double[]> dataArray)
    {
        ArrayList<Double> calcValues = new ArrayList<>();
        for (int i=0; i<dataArray.size(); i++)
        {
            double result = 0;
            Double[] chData = dataArray.get(i);
            int chLen = chData.length;
            for(int j=0; j<chLen; j++)
            {
                result += chData[j] * chData[j];
            }
            calcValues.add(Math.sqrt(result / chLen));
        }
        return calcValues;
    }

    /**
     * Method to calculate the standard deviation for given window of data
     * @param dataArray
     * @return
     */
    public ArrayList<Double> calculateStDev(ArrayList<Double[]> dataArray)
    {
        ArrayList<Double> calcValues = new ArrayList<>();
        for (int i=0; i<dataArray.size(); i++)
        {
            double result = 0;
            result = StatUtils.variance(ArrayUtils.toPrimitive(dataArray.get(i)));
            calcValues.add(Math.sqrt(result));
        }
        return calcValues;
    }

    /**
     * Method to calculate the mean for given window of data
     * @param dataArray
     * @return
     */
    public ArrayList<Double> calculateMean(ArrayList<Double[]> dataArray)
    {
        ArrayList<Double> calcValues = new ArrayList<>();
        for (int i=0; i<dataArray.size(); i++)
        {
            double result = 0;
            result = StatUtils.mean(ArrayUtils.toPrimitive(dataArray.get(i)));
            calcValues.add(result);
        }
        return calcValues;
    }


    /**
     * Method to calculate the energy per channel for the given window
     * @param dataArray
     * @return
     */
    public ArrayList<Double> calculateSensorEnergy(ArrayList<Double[]> dataArray)
    {
        ArrayList<Double> calcValues = new ArrayList<>();
        double result1 = 0;
        double result2 = 0;
        int nChannels = dataArray.size();

        // Store the energy values per channel
        double[] energyValues = new double[nChannels];
        double[] avgEnergyValues = new double[nChannels];

        // Store the average energy values per sensor
        double totalGyroEnergy = 0;
        double totalAccelEnergy = 0;

        // Calculate energy for all channels
        for (int i=0; i<nChannels; i++)
        {
            Double[] chData = dataArray.get(i);
            int chLen = chData.length;
            for(int j=0; j<chLen; j++)
            {
                energyValues[i] += chData[j] * chData[j];
            }
        }

        //FIXME Remove dependence on the predefined order of channels
        for(int i=0; i<Constants.NUM_OF_ACCEL_CHANNELS; i++)
        {
            // Get the total energy for all accelerometer channels
            totalAccelEnergy += energyValues[i];
        }

        for(int i=0; i<Constants.NUM_OF_ACCEL_CHANNELS; i++)
        {
            // Get the total energy for all accelerometer channels
            avgEnergyValues[i] = energyValues[i] / totalAccelEnergy;
            calcValues.add(avgEnergyValues[i]);
        }

        for(int i=Constants.NUM_OF_ACCEL_CHANNELS; i<Constants.NUM_OF_CHANNELS; i++)
        {
            // Get the total energy for all gyro channels
            totalGyroEnergy += energyValues[i];
        }

        for(int i=Constants.NUM_OF_ACCEL_CHANNELS; i<Constants.NUM_OF_CHANNELS; i++)
        {
            // Normalize the energy values for the same sensor type
            avgEnergyValues[i] = energyValues[i] / totalGyroEnergy;
            calcValues.add(avgEnergyValues[i]);
        }

        return calcValues;
    }

    /**
     * Calculate the correlation features for a given window size
     * @param dataArray
     * @return
     */
    public ArrayList<Double> calculateCorrelation(double[][] dataArray)
    {
        ArrayList<Double> calcValues = new ArrayList<>();
        Double result = 0.0;

        // New correlation calculator
        correlationCalculator = new PearsonsCorrelation();

        // Correlation data arraylist
        ArrayList<double[]> correlationData = new ArrayList<double[]>();

        // Put all the data in the correlation data structure
        for(int i=0; i<Constants.NUM_OF_CHANNELS; i++)
        {
            correlationData.add(dataArray[i]);
        }

        // Correlation for all gyroscope channels
        for(int i=0; i<Constants.NUM_OF_GYRO_CHANNELS; i++)
        {
            for(int j=i+1; j<Constants.NUM_OF_GYRO_CHANNELS; j++)
            {
                result = correlationCalculator.correlation(correlationData.get(i), correlationData.get(j));
                calcValues.add(result);
            }
        }

        // Correlation for all accelerometer channels
        for(int i=Constants.NUM_OF_GYRO_CHANNELS; i<Constants.NUM_OF_CHANNELS; i++)
        {
            for(int j=i+1; j<Constants.NUM_OF_CHANNELS; j++)
            {
                result = correlationCalculator.correlation(correlationData.get(i), correlationData.get(j));
                calcValues.add(result);
            }
        }

        return calcValues;
    }

    /**
     * Calculate the frequency features of a given window size
     * @param dataArray
     * @return calcValues containing features
     */
    public ArrayList<Double> calculateFFT(double[][] dataArray)
    {
        ArrayList<Double> calcValues = new ArrayList<>();

        for(int i=0; i<Constants.NUM_OF_CHANNELS; i++)
        {
            // Get the power spectrum per channel
            powerSpectrum[i] = getSpectrum(dataArray[i]);

            // Add the value of the power spectrum for each channel
            for(int j=0; j<powerSpectrum[i].length; j++)
            {
                calcValues.add(powerSpectrum[i][j]);
            }

            // Add the mean of the frequency domain
            // The DC result is the mean we use here
            calcValues.add(powerSpectrum[i][0]);

            // Add the variance of the power spectrum per channel
            calcValues.add(getSpectrumVariance(powerSpectrum[i]));

            // Add the energy of the power spectrum
            calcValues.add(getSpectrumEnergy(powerSpectrum[i]));

            // Add the entropy of the power spectrum per channel
            calcValues.add(getSpectrumEntropy(powerSpectrum[i]));
        }
        return calcValues;
    }

    /**
     * Calculate the cepstrum based on the power spectrum
     * @return
     */
    public ArrayList<Double> calculateCepstrum()
    {
        ArrayList<Double> calcValues = new ArrayList<>();
        double[][] cepstrumValues = new double[Constants.NUM_OF_CHANNELS][Constants.WIN_SIZE];

        for(int i=0; i<Constants.NUM_OF_CHANNELS; i++)
        {
            // Get the power spectrum per channel
            cepstrumValues[i] = getCepstrum(powerSpectrum[i]);

            // Add the value of the power spectrum for each channel
            for(int j=0; j<cepstrumValues[i].length; j++)
            {
                calcValues.add(cepstrumValues[i][j]);
            }

            // Add the variance of the power spectrum per channel
            calcValues.add(StatUtils.variance(cepstrumValues[i]));

            // Add the entropy of the power spectrum per channel
            calcValues.add(getCepstrumEntropy(cepstrumValues[i]));
        }

        return calcValues;
    }

    /**
     * Method to get the power spectrum for each channel of data
     * @param dataChannelArray
     * @return
     */
    public static double[] getSpectrum(double[] dataChannelArray)
    {
        // Get length of input data
        int length = dataChannelArray.length;

        // Setup fft result array and output
        double[] output = new double[length];
        double[] fftResult = new double[2*length];

        // Make copy of input array
        System.arraycopy(dataChannelArray, 0, fftResult, 0, length);

        // Setup the FFT
        DoubleFFT_1D fft = new DoubleFFT_1D(length);
        fft.realForwardFull(fftResult);

        // Calculate output data
        for(int i=0; i<output.length; i++)
        {
            output[i] = fftResult[2*i]*fftResult[2*i] + fftResult[2*i+1]*fftResult[2*i+1];
        }

        // Return the output with the combined fft result
        return output;
    }

    /**
     * Method to get the energy for the power spectrum
     * @param inputArray
     * @return
     */
    public static Double getSpectrumVariance(double[] inputArray)
    {
        // Get length of input data
        int length = inputArray.length;

        // Output
        double[] outputArray = new double[length - 1];

        // Collect the sum of the square of all elements in the array
        for (int i=1; i<length; i++)
        {
            outputArray[i-1] = inputArray[i];
        }

        // Return the output
        return StatUtils.variance(outputArray);
    }

    /**
     * Method to get the power spectrum energy
     * @param inputArray contains all the power spectrum data (including DC)
     * @return value for the energy for the input array
     */
    public static Double getSpectrumEnergy(double[] inputArray)
    {
        // Get length of input data
        int length = inputArray.length;

        // Output
        double output = 0;
        int counter = 0;

        // Collect the sum of the square of all elements in the array
        for (int i=1; i<length; i++)
        {
            counter++;
            output += inputArray[i] * inputArray[i];
        }

        // Return the normalized output
        // Do not count the DC value at inputArray[0]
        return output/counter;
    }

    /**
     * Method to get the entropy of the power spectrum
     * @param inputArray
     * @return
     */
    public static double getSpectrumEntropy(double[] inputArray)
    {
        // Output
        double sum = 0;
        double result = 0;

        // Collect the sum of all elements in the array
        // Do not include inputArray[0] which is the DC value
        for (int i=1; i<inputArray.length; i++)
        {
            sum += Math.abs(inputArray[i]);
        }

        // Loop through data
        for(int i=1; i<inputArray.length; i++)
        {
            // Normalize the data
            double prob = Math.abs(inputArray[i])/sum;

            // Calculate the probability (do not use zeros)
            double logProb = prob == 0.0 ? 0 : Math.log(prob);
            double log2 = Math.log(2);

            // Get the entropy
            result -= prob * (logProb/log2);
        }

        // Return the entropy result
        return result;
    }

    /**
     * Method to get the cepstrum of an inputArray
     * @param inputArray
     * @return
     */
    public static double[] getCepstrum(double[] inputArray)
    {
        // Length of input
        int length = inputArray.length;

        // Output of cepstrum calculation
        double[] output = new double[length];

        // Copy of cepstrum values to output array
        System.arraycopy(inputArray, 0, output, 0, length);

        // Use a discrete cosine transformation
        edu.emory.mathcs.jtransforms.dct.DoubleDCT_1D  dct = new edu.emory.mathcs.jtransforms.dct.DoubleDCT_1D(length);

        // Computes 1D forward DCT with scaling boolean
        dct.forward(output, true);

        // Return cepstrum values
        return output;
    }

    /**
     * Method to get the entropy of the cepstrum
     * @param inputArray
     * @return
     */
    public static double getCepstrumEntropy(double[] inputArray)
    {
        // Output
        double sum = 0;
        double result = 0;

        // Collect the sum of all elements in the array
        for (int i=0; i<inputArray.length; i++)
        {
            sum += Math.abs(inputArray[i]);
        }

        // Loop through data
        for(int i=0; i<inputArray.length; i++)
        {
            // Normalize the data
            double prob = Math.abs(inputArray[i])/sum;

            // Calculate the probability (do not use zeros)
            double logProb = prob == 0.0 ? 0 : Math.log(prob);
            double log2 = Math.log(2);

            // Get the entropy
            result -= prob * (logProb/log2);
        }

        // Return the entropy result
        return result;
    }

    /**
     * Method to calculate the zero crossing
     * @param dataArray
     * @return
     */
    private ArrayList<Double> calculateZeroCrossing(ArrayList<Double[]> dataArray)
    {
        // channels used 3-5
        ArrayList<Double> calcValues = new ArrayList<Double>();

        for(int i=Constants.NUM_OF_ACCEL_CHANNELS; i<Constants.NUM_OF_CHANNELS; i++)
        {
            double numCrossing = 0;

            Double[] chData = dataArray.get(i);
            int chLen = chData.length;

            for (int p = 0; p < chLen-1; p++)
            {
                if ((chData[p] > 0 && chData[p + 1] <= 0) ||
                        (chData[p] < 0 && chData[p + 1] >= 0))
                {
                    numCrossing++;
                }
            }
            calcValues.add(numCrossing);
        }

        //System.out.println(calcValues);

        return calcValues;
    }

    /**
     * Method to calculate the ECDF
     * @param dataArray
     * @return
     */
    public static ArrayList<Double> calculateECDF(ArrayList<Double[]> dataArray)
    {
        // channels used 0-2

        Random random_source = new Random();

        // output is squashed down
        ArrayList<Double> calcValues = new ArrayList<>();

        // iterate over accelerometer channels
        for(int i=0; i<Constants.NUM_OF_ACCEL_CHANNELS; i++)
        {
            //System.out.println(i);

            double[] sensorData = ArrayUtils.toPrimitive(dataArray.get(i));

            // add noise, sample ECDF along support, interpolate cubic
            double[] noisyData = new double[sensorData.length];

            // ECDF paper says add unit variance zero mean noise * 0.01
            for (int j = 0; j < sensorData.length; j++)
            {
                double noise = random_source.nextGaussian() * 0.01;
                noisyData[j] = sensorData[j] + noise;
            }

            // If the values you put in are non-monotonic, then there will be problems
            Arrays.sort(noisyData);
            Arrays.sort(sensorData);

            // Create ECDF and load data
            EmpiricalDistribution ecdf = new EmpiricalDistribution(Constants.ECDF_LENGTH);
            ecdf.load(noisyData);

            // Then sample the ecdf along the points that you just put in
            //double ecdfValues[] = new double[noisyData.length];
            //for (int j = 0; j < ecdfValues.length; j++) ecdfValues[j] = ecdf.cumulativeProbability(noisyData[j]);
            //float[] f_noisyData = new float[noisyData.length];
            //float[] f_ecdfValues = new float[ecdfValues.length];
            //for (int j = 0; j < noisyData.length; j++) f_noisyData[j] = (float) noisyData[j];
            //for (int j = 0; j < ecdfValues.length; j++) f_ecdfValues[j] = (float) ecdfValues[j];

            // We need an interpolator that preserves monotonicity of the ECDF!!!!
            // MATLAB PCHIP satisfies, but there is no good implementation for Java
            //CubicInterpolator interp = new CubicInterpolator(CubicInterpolator.Method.MONOTONIC, f_ecdfValues, f_noisyData);
            //float[] f_output = interp.interpolate(x);

            // Create equally spaced x indices from 0->1
            double[] x = linspace(0.0, 1.0, Constants.ECDF_LENGTH);

            // Calculate the inverse of the CDF at the given x value
            for(double f : x)
            {
                try {
                    //TODO Check if 0.0 is a good initialization value
                    double result = 0.0;

                    try {
                        result = ecdf.inverseCumulativeProbability(f);
                    }
                    catch (org.apache.commons.math3.exception.NotStrictlyPositiveException e) {
                        //Exception occurs if the standard deviation <= 0
                        Log.e(TAG, "Standard deviation is <= 0");
                    }

                    if(Double.isNaN(result) || Double.isInfinite(result)) {
                        Log.d(TAG, "NaN calculated");
                        result = 0.0;
                    }
                    calcValues.add(result);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return calcValues;
    }

    public static double[] linspace(double min, double max, int points)
    {
        double[] d = new double[points];

        for (int i = 0; i < points; i++)
        {
            double j = (double) i;
            d[i] = min + (j * (max - min)) / (points);
        }
        return d;
    }
}
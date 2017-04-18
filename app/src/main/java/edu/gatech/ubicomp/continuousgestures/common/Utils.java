package edu.gatech.ubicomp.continuousgestures.common;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.gson.Gson;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.gatech.ubicomp.continuousgestures.MyApplication;
import edu.gatech.ubicomp.continuousgestures.data.learning.PeakDetection;


public class Utils
{
    /**
     * Transform an array to arraylist
     * @param input
     * @return
     */
    public static ArrayList<Double> array2List(double[] input)
    {
        ArrayList<Double> output = new ArrayList<Double>();

        for(int i=0; i<input.length;++i)
        {
            output.add(input[i]);
        }
        return output;
    }

    /**
     * Transform an arraylist to array
     * @param input
     * @return
     */
    public static double[] list2Array(ArrayList<Double> input)
    {
        double[] output = new double[input.size()];
        for(int i=0; i<input.size();++i)
        {
            output[i] = input.get(i);
        }
        return output;
    }

    /**
     * Transform a two dimensional arraylist to array
     * @param input
     * @return
     */
    public static double[][] twoList2Array(ArrayList<ArrayList<Double>> input)
    {
        double[][] output = new double[input.size()][input.get(0).size()];

        for(int i=0; i<input.size(); i++)
        {
            for(int j=0; j<input.get(0).size(); j++)
            {
                output[i][j] = input.get(i).get(j);
            }
        }
        return output;
    }

    /**
     * Transform a two dimensional arraylist to array and absolute the value
     * @param input
     * @return
     */
    public static double[][] twoList2ArrayAbsolute(ArrayList<ArrayList<Double>> input)
    {
        double[][] output = new double[input.size()][input.get(0).size()];

        for(int i=0; i<input.size(); i++)
        {
            for(int j=0; j<input.get(0).size(); j++)
            {
                output[i][j] = Math.abs(input.get(i).get(j));
            }
        }
        return output;
    }

    /**
     * Transform a two dimensional array to arraylist
     * @param input
     * @return
     */
    public static ArrayList<ArrayList<Double>> twoArray2List(double[][] input)
    {
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> channel;

        for(int i=0; i<input.length; i++)
        {
            channel = new ArrayList<Double>();

            for(int j=0; j<input[i].length; j++)
            {
                channel.add(input[i][j]);
            }
            output.add(channel);
        }
        return output;
    }

    /**
     * Method to transpose an ArrayList<ArrayList<Double>>
     * @param input
     * @return
     */
    public static ArrayList<ArrayList<Double>> transpose(ArrayList<ArrayList<Double>> input)
    {
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();

        final int N = input.get(0).size();
        for (int i = 0; i < N; i++)
        {
            ArrayList<Double> col = new ArrayList<Double>();
            for (ArrayList<Double> row : input)
            {
                col.add(row.get(i));
            }
            output.add(col);
        }
        return output;
    }

    /**
     * Prints a two dimensional input array
     * @param input
     * @return
     */
    public static void printArray(double[][] input)
    {
        for(int i=0; i<input.length; i++)
        {
            for(int j=0; j<input[i].length; j++)
            {
                System.out.print(input[i][j] + ",");
            }
            System.out.println("");
        }
    }

    /**
     * Method to trim a window data buffer based on a center peak index
     * @return
     */
    public static ArrayList<ArrayList<Double>> trimTransposed(ArrayList<ArrayList<Double>> dataObject, int startIndex, int endIndex)
    {
        // Initialize return object
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();

        for(int i = 0; i< Constants.NUM_OF_CHANNELS_WITH_INDEX; i++)
        {
            // New arraylist<double> for each channel
            ArrayList<Double> channel = new ArrayList<Double>();

            for(int j=startIndex; j<=endIndex; j++)
            {
                // Add each trimmed element to new channel object
                channel.add(dataObject.get(i).get(j));
            }
            // Add channel to output
            output.add(channel);
        }
        return output;
    }

    /**
     * Method to trim a non-tranposed window
     * @return
     */
    public static ArrayList<ArrayList<Double>> trim(ArrayList<ArrayList<Double>> dataObject, int startIndex, int endIndex)
    {
        // Initialize return object
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();

        for(int j=startIndex; j<=endIndex; j++)
        {
            // New arraylist<double> for each channel
            ArrayList<Double> channel = new ArrayList<Double>();

            for(int i=0; i<Constants.NUM_OF_CHANNELS_WITH_INDEX; i++)
            {
                // Add each trimmed element to new channel object
                channel.add(dataObject.get(j).get(i));
            }
            // Add channel to output
            output.add(channel);
        }
        return output;
    }



    /**
     * Method to calculate the indices of a window with centered peak
     */
    public static int getHanningWindowPeakIndex(ArrayList<ArrayList<Double>> dataObject)
    {
        int peakIndex = -1;
        ArrayList<Integer> peaks = new ArrayList<Integer>();
        ArrayList<Integer> valleys = new ArrayList<Integer>();

        // Peak detection
        PeakDetection.detect_peak(dataObject.get(0), dataObject.get(0).size(), peaks, Integer.MAX_VALUE, valleys, Integer.MAX_VALUE, Constants.DELTA_BETW_PEAK_TROUGH, true);

        // Check if multiple peaks detected and grab the first one
        if(peaks.size() >= 1)
        {
            if(peaks.get(0) > Constants.WIN_SIZE/2 && peaks.get(0) < Constants.WIN_SIZE)
            {
                peakIndex = peaks.get(0);
                //System.out.println("Peak index located at: " + peakIndex);
                System.out.println("Peak detected.");
                //TODO: add something on UI
            }
            else
            {
                //TODO: add something on UI
                //System.out.println("No window of data located, peak outside of range");
                System.out.println("No peak.");
            }
        }

        return peakIndex;
    }

    /**
     * Read data from a file and put them into buffer in memory
     * @param file
     * @param isTemplate
     * @return
     */
    public static ArrayList<ArrayList<Double>> readDataFromFiles(File file, boolean isTemplate)
    {
        // Initialize memory for the output
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();

        //int numCols = (isTemplate)? Constants.NUM_OF_CHANNELS : Constants.NUM_OF_CHANNELS_WITH_INDEX;
        int numCols = 1;

        // Initialize memory for the channels
        for(int i=0; i<numCols; i++) output.add(new ArrayList<Double>());

        BufferedReader input;
        try
        {
            input = new BufferedReader(new FileReader(file));

            // Remove the label row
            String curline = input.readLine();
            String labels[] = curline.split(",");
            int labelsCount = labels.length;

            // Grab the first line of data
            curline = input.readLine();

            while(curline!=null)
            {
                // Get the data points
                String[] dataPoints = curline.split(",");

                // Skip the line which does not contain a whole set of input sensor data
                if(dataPoints.length != labelsCount)
                {
                    // Skip to the next line
                    curline = input.readLine();
                    continue;
                }

                // Get data worth number of channels + the line index
                for(int i =0; i< numCols; i++)
                {
                    //
                    output.get(i).add(Double.parseDouble(dataPoints[i]));
                }
                curline = input.readLine();

            }
            input.close();
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Return transposed object
        return output;
    }

    /*
     * Return result with whether the inputstr is contained in the strArray
     * @param inputstr
     * @param strArray
     * @return
     */
    public static boolean isStringContainedinArray(String inputstr, String[] strArray){
        for(String str:strArray){
            if(str.equals(inputstr)){
                return true;
            }
        }
        return false;
    }

    public static String floatArray2CSVString(float[] input)
    {
        StringBuilder result = new StringBuilder();

        for(float value: input)
        {
            result.append(value);
            result.append(",");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 1): "";
    }

    public static String doubleArray2CSVString(double[] input)
    {
        StringBuilder result = new StringBuilder();

        for(double value: input)
        {
            result.append(value);
            result.append(",");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 1): "";
    }

    public static float[] csvString2FloatArray(String input)
    {
        String[] stringArray = input.split(",");
        float[] result = new float[stringArray.length];

        for(int i = 0; i<stringArray.length; i++)
        {
            result[i] = Float.parseFloat(stringArray[i]);
        }

        return result;
    }

    public static double[] csvString2DoubleArray(String input)
    {
        String[] stringArray = input.split(",");
        double[] result = new double[stringArray.length];

        for(int i = 0; i<stringArray.length; i++)
        {
            result[i] = Double.parseDouble(stringArray[i]);
        }

        return result;
    }

    public static ArrayList<Double> csvString2DoubleList(String input)
    {
        String[] stringArray = input.split(",");
        ArrayList<Double> output = new ArrayList<Double>();

        for(int i=0; i<stringArray.length;++i)
        {
            output.add(Double.parseDouble(stringArray[i]));
        }

        return output;
    }

    public static JSONObject floatArray2JSONObject(String[] labels, float[] input)
    {
        JSONObject result = new JSONObject();

        for(int i=0; i< Math.min(labels.length, input.length); i++)
        {
            try
            {
                result.put(labels[i],(Float) input[i]);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static float[] jsonObject2FloatArray(String[] labels, String sensorName, JSONObject input)
    {
        float[] result = new float[Constants.DEFAULT_NUM_CHANNELS_FOR_MOTION_SENSORS];

        try {
            JSONArray dataArr = input.getJSONArray(sensorName);

            //Get a single line of data that has information for all channels.
            JSONObject sensorDataLine = dataArr.getJSONObject(0);

            //TODO: Remove dependence on the constant
            double[] dataLineAsArr =  Utils.jsonObject2DoubleArray(Constants.DEFAULT_CHANNEL_LABELS,sensorDataLine);
            for (int i=0; i<Constants.DEFAULT_NUM_CHANNELS_FOR_MOTION_SENSORS; i++)
            {
                result[i] = (float) dataLineAsArr[i];
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static double[] jsonObject2DoubleArray(String[] labels, JSONObject input)
    {
        double[] result = new double[labels.length];

        for(int i=0; i<labels.length; i++)
        {
            try
            {
                result[i] = input.getDouble(labels[i]);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static float[] JSONString2FloatArray(String input)
    {
        float[] result = null;
        JSONObject jsonObject = null;

        try
        {
            jsonObject = new JSONObject(input);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        //TODO: double check jsonObject.length is what we want here
        for(int i=0; i<jsonObject.length(); i++)
        {
            //TODO: add logic to pass right value to float (are we assuming x,y,z channels always?)
            result[i] = 0;
        }

        return result;
    }

    public static ArrayList<Double> JSONString2DoubleList(String input)
    {
        JSONObject jsonObject = null;
        ArrayList<Double> result = new ArrayList<Double>();

        try
        {
            jsonObject = new JSONObject(input);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        //TODO: double check jsonObject.length is what we want here
        for(int i=0; i<jsonObject.length(); i++)
        {
            //TODO: add logic to pass right value to float (are we assuming x,y,z channels always?)
            result.add(0.0);
        }

        return result;
    }

    public static JSONArray StringArrayToJSONArray (String[] input)
    {
        JSONArray output = null;
        output = new JSONArray();
        for (String val: input)
        {
            output.put(val);
        }
        return output;
    }

    /**
     * Checks if val exists as a value in the JSONArray. Helps avoid duplicates.
     * @param input
     * @param val
     * @return
     */
    public static boolean isValueInJSONArray (JSONArray input, String val)
    {
        for(int i=0; i<input.length(); i++)
        {
            try {
                if(val.equals(input.getString(i))) return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getJsonString(Object gsonObject){
        return new Gson().toJson(gsonObject);
    }

    public static <T extends Object> T getObject(String jsonString, Class<T> type) {
        return new Gson().fromJson(jsonString, type);
    }

    public static <T extends Object> List<T> getArray(String jsonString, Class<T[]> classType) {
        return Arrays.asList(new Gson().fromJson(jsonString, classType));
    }

    public static double round(Double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static Drawable getDeviceIconDrawableFromName(String deviceName, Resources resources)
    {
        String deviceIconURI = "drawable/icon_device_"+ deviceName+"_black_128dp";
        int imageResource = resources.getIdentifier(deviceIconURI, null, MyApplication.PACKAGE_NAME);

//        ImageView imageView = (ImageView) findViewById(R.id.myImageView);
        Drawable image = resources.getDrawable(imageResource);

        return image;
    }

    public static Drawable getDeviceIconDrawableFromName(String deviceName, Resources resources, String color, int size)
    {
        String deviceIconURI = "drawable/icon_device_"+ deviceName+"_"+color+"_"+size+"dp";
        int imageResource = resources.getIdentifier(deviceIconURI, null, MyApplication.PACKAGE_NAME);

//        ImageView imageView = (ImageView) findViewById(R.id.myImageView);
        Drawable image = resources.getDrawable(imageResource);

        return image;
    }

    public static Drawable getBodyPosIconDrawableFromName(String bodyPos, Resources resources)
    {
        String bodyPosJoined = bodyPos.replaceAll(" ", "_").toLowerCase();
        String deviceIconURI = "drawable/icon_bodypos_"+ bodyPosJoined+"_black_48dp";
        int imageResource = resources.getIdentifier(deviceIconURI, null, MyApplication.PACKAGE_NAME);

//        ImageView imageView = (ImageView) findViewById(R.id.myImageView);
        Drawable image = resources.getDrawable(imageResource);

        return image;
    }

    public static Drawable getActivityIconDrawableFromName(String activity, Resources resources, String color, int size)
    {
        String activityIconURI = "drawable/icon_activity_"+ activity.toLowerCase()+"_"+color+"_"+size+"dp";
        int imageResource = resources.getIdentifier(activityIconURI, null, MyApplication.PACKAGE_NAME);

//        ImageView imageView = (ImageView) findViewById(R.id.myImageView);
        Drawable image = resources.getDrawable(imageResource);

        return image;
    }


    /**
     * Generating model file name from class id
     */
    public static String generateModelFileNameFromClassId(long clsId) {
        return "model_"+String.valueOf(clsId)+".arff";
    }

    public static void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath+ '/' + inputFile);
            out = new FileOutputStream(outputPath + '/' + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public static void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();


        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    public static void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath +'/' + inputFile);
            out = new FileOutputStream(outputPath +'/' + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public static void copyFile(String inputPath, String inputFile, String outputPath, String outputFile) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath +'/' + inputFile);
            out = new FileOutputStream(outputPath +'/' + outputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public static String getTimeElapsedInReadableFormat(String startTimeFromDb) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(startTimeFromDb, formatter);
        LocalDateTime currentTime = LocalDateTime.now();
        Period period = new Period(dateTime, currentTime);

        long days = period.getDays();
        long hours = period.getHours();
        long minutes = period.getMinutes();
        long seconds = period.getSeconds();
        String duration = "";
        if (days == 1) {
            duration = "a day ago";
        } else if (days > 1) {
            duration = days + " days ago";
        } else {
            if (hours == 1) {
                duration = "an hour ago";
            } else if (hours > 1) {
                duration = hours + " hours ago";
            } else {
                if (minutes == 1) {
                    duration = "a minute ago";
                } else if (minutes > 1) {
                    duration = minutes + " minutes ago";
                } else {
                    if (seconds < 30) {
                        duration = "just now";
                    } else {
                        duration = seconds + " seconds ago";
                    }
                }
            }
        }

        return duration;
    }


}
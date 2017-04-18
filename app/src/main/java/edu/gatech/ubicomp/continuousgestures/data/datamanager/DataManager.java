package edu.gatech.ubicomp.continuousgestures.data.datamanager;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.gatech.ubicomp.continuousgestures.common.Config;

/**
 * Created by gareyes on 12/11/15.
 */
public class DataManager {
    File myFile, myDirectory;
    FileOutputStream fOut;
    OutputStreamWriter myOutWriter;
    final static String FOLDER_NAME = Config.DIR_PATH_MISC;

    public DataManager() {
        try {
            Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (isSDPresent) {
                Log.d("SD", "Card present");
            } else {
                Log.d("SD", "Card not present");
            }

            //String filepath = Environment.getExternalStorageDirectory();
            //Log.d("FILE", filepath);
            myDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + FOLDER_NAME);


            Log.d("FOLDER", myDirectory.toString());

            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }

            //myFile.createNewFile();


            //myOutWriter.write("test string");
            //myOutWriter.close();
            //fOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startNewFile(String fileName) {
        try {
            myFile = new File(myDirectory.getAbsolutePath() + File.separator + fileName);
            Log.d("FILE", myFile.toString());
            myFile.createNewFile();
            fOut = new FileOutputStream(myFile);
            myOutWriter = new OutputStreamWriter(fOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFile() {
       closeDataManager();
    }

    public void saveData(String test) {
        try {
            myOutWriter.append(test);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeDataManager() {
        try {
            if(myOutWriter != null) {
                myOutWriter.flush();
                myOutWriter.close();
            }
            if (fOut != null) {
                fOut.flush();
                fOut.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNewFileName() {
        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date (month/day/year)
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");

        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        String reportDate = df.format(today);
        return reportDate+".csv";
    }
}




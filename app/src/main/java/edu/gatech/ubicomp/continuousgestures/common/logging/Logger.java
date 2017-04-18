package edu.gatech.ubicomp.continuousgestures.common.logging;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.inject.Inject;


/**
 * Logger that logs events happening around the app to a file
 * Created by batman on 26/9/16.
 */

public class Logger {
    @Inject RxEventBus mRxEventBus;
    private final String mLogFolderPath;

    private void registerForEvents() {
        mRxEventBus.registerForEvent(InteractionLogEvent.class, data -> executeWritingTask(data.getClass().getSimpleName(), data.mData));
        mRxEventBus.registerForEvent(DataLogEvent.class, data -> executeWritingTask(data.getClass().getSimpleName(), data.mData));
        mRxEventBus.registerForEvent(DebugLogEvent.class, data -> executeWritingTask(data.getClass().getSimpleName(), data.mData));
        mRxEventBus.registerForEvent(ErrorLogEvent.class, data -> executeWritingTask(data.getClass().getSimpleName(), data.mData));
    }

    public Logger(Context context, String logFolderPath) {
        mLogFolderPath = logFolderPath;
        DependencyUtil.inject(context, this);
        File logFolder = new File(logFolderPath);
        if (!logFolder.exists() || !logFolder.isDirectory()) {
            logFolder.mkdirs();
        }
        registerForEvents();
    }

    synchronized private void executeWritingTask(String eventClass, String data) {
        new FileWritingTask().execute(eventClass + "," + data);
    }

    private class FileWritingTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.d(FileWritingTask.class.getSimpleName(), "Params Length: " + params.length);
            String logText = System.currentTimeMillis() + "," + params[0];
            appendLog(logText);
            return null;
        }

        private void appendLog(String logText) {
            String logFilePath = mLogFolderPath + File.separator + "log_file" + ".csv";
            File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                    BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                    buf.append("Time,Event Class, Log");
                    buf.newLine();
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                // BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(logText);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

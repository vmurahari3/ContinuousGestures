package edu.gatech.ubicomp.continuousgestures.data.learning;

import edu.gatech.ubicomp.continuousgestures.common.Constants;

import java.util.ArrayList;

/**
 * Created by ubicomp on 3/31/17.
 */

public class Segmenter {

    private static final String TAG =  Segmenter.class.getSimpleName() ;

    /**
     *
     * @param sensorValues list of sensor channels in the format: accel_x , accel_y, accel_z, gyro_x, gyro_y, gyro_z
     * @param gyroTimestamp
     * @return
     */
    public static boolean segmentStartTest(ArrayList<ArrayList<Double>>  sensorValues, ArrayList<Long> gyroTimestamp ){
        // calculate energy of the sensor values
        double energyX = calculateEnergy(sensorValues.get(3),gyroTimestamp);
        double energyY = calculateEnergy(sensorValues.get(4),gyroTimestamp);
        double energyZ = calculateEnergy(sensorValues.get(5),gyroTimestamp);
//        Log.d(TAG,"energy X is: " + energyX);
        if( energyX >  Constants.ENERGY_THRESHOLD || energyY > Constants.ENERGY_THRESHOLD || energyZ > Constants.ENERGY_THRESHOLD){
            return true;
        }
        return false;
    }

    public static boolean segmentStopTest(ArrayList<ArrayList<Double>>  sensorValues, ArrayList<Long> gyroTimestamp ){

        // calculate energy of the sensor values

        double energyX = calculateEnergy(sensorValues.get(3),gyroTimestamp);
        double energyY = calculateEnergy(sensorValues.get(4),gyroTimestamp);
        double energyZ = calculateEnergy(sensorValues.get(5),gyroTimestamp);

//        Log.d(TAG, "energy in stop in X channel: " + energyX);

        if( energyX <  Constants.ENERGY_THRESHOLD && energyY < Constants.ENERGY_THRESHOLD && energyZ < Constants.ENERGY_THRESHOLD){
            return true;
        }
        return false;
    }

    private static double calculateEnergy(ArrayList<Double> channel, ArrayList<Long> timestamp){

        double energy = 0;

        for(int i = 0; i < channel.size()-1; i++){
           energy  += Math.abs(channel.get(i)) * Math.abs(channel.get(i)) * (timestamp.get(i+1) - timestamp.get(i));
        }

        return energy / Math.pow(10,8);
    }
}

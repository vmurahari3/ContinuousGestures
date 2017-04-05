package edu.gatech.ubicomp.continuousgestures.Learning;

import edu.gatech.ubicomp.continuousgestures.Constants.Constants;

import java.util.ArrayList;

/**
 * Created by ubicomp on 3/31/17.
 */

public class Segmentation {

    private static final String TAG =  Segmentation.class.getSimpleName() ;

    /**
     *
     * @param sensor_values list of sensor channels in the format: accel_x , accel_y, accel_z, gyro_x, gyro_y, gyro_z
     * @param gyro_timestamp
     * @return
     */
    public static boolean segment_start_test(ArrayList<ArrayList<Double>>  sensor_values,  ArrayList<Long> gyro_timestamp ){
        // calculate energy of the sensor values
        double energy_X = calculate_energy(sensor_values.get(3),gyro_timestamp);
        double energy_Y = calculate_energy(sensor_values.get(4),gyro_timestamp);
        double energy_Z = calculate_energy(sensor_values.get(5),gyro_timestamp);
//        Log.d(TAG,"energy X is: " + energy_X);
        if( energy_X >  Constants.ENERGY_THRESHOLD || energy_Y > Constants.ENERGY_THRESHOLD || energy_Z > Constants.ENERGY_THRESHOLD){
            return true;
        }
        return false;
    }

    public static boolean segment_stop_test( ArrayList<ArrayList<Double>>  sensor_values,ArrayList<Long> gyro_timestamp ){

        // calculate energy of the sensor values

        double energy_X = calculate_energy(sensor_values.get(3),gyro_timestamp);
        double energy_Y = calculate_energy(sensor_values.get(4),gyro_timestamp);
        double energy_Z = calculate_energy(sensor_values.get(5),gyro_timestamp);

//        Log.d(TAG, "energy in stop in X channel: " + energy_X);

        if( energy_X <  Constants.ENERGY_THRESHOLD && energy_Y < Constants.ENERGY_THRESHOLD && energy_Z < Constants.ENERGY_THRESHOLD){
            return true;
        }
        return false;
    }

    private static double calculate_energy(ArrayList<Double> channel, ArrayList<Long> timestamp){

        double energy = 0;

        for(int i = 0; i < channel.size()-1; i++){
           energy  += Math.abs(channel.get(i)) * Math.abs(channel.get(i)) * (timestamp.get(i+1) - timestamp.get(i));
        }

        return energy / Math.pow(10,8);
    }
}

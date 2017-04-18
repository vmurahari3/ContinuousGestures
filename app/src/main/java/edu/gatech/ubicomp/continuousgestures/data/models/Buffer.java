package edu.gatech.ubicomp.continuousgestures.data.models;

/**
 * Created by ubicomp on 3/2/17.
 */

public class Buffer {

    private double[] overflow_buffer;
    private double[] main_buffer;
    private int insert_index_main, insert_index_overflow, main_current_size, overflow_current_size;
    private double totalSum, average;
    public boolean is_main_full, is_overflow_full;

    public Buffer(int buffer_size , int slide_size) {
        main_buffer = new double[buffer_size];
        average = 0;
        main_current_size = 0;
        overflow_current_size = 0;
        insert_index_main = 0;
        insert_index_overflow = 0;
        is_main_full = false;
        is_overflow_full = false;
        overflow_buffer = new double[slide_size];
    }

    public void add(double value) {
//        Log.d("hello",main_buffer.toString());
//        if (value == null) return;
//        Log.d("hello",value +"");
        if(is_main_full){
            // need to throw stuff into overflow and then do some operations
            if(is_overflow_full){
                // we need to flush the overflow buffer and put it the main buffer
                for(int i = 0; i < overflow_buffer.length; i++){
                    main_buffer[insert_index_main] = overflow_buffer[i];
                    insert_index_main = (insert_index_main + 1) % main_buffer.length;
                }
                // need to reset values in oveflow
                insert_index_overflow = 0;
                overflow_current_size = 0;
                is_overflow_full = false;

            }else{
                // add stuff into overflow
                overflow_buffer[insert_index_overflow++] = value;
                insert_index_overflow = insert_index_overflow % overflow_buffer.length;
                overflow_current_size++;
                is_overflow_full = (overflow_current_size == overflow_buffer.length);
            }

        }else{
            // need to throw stuff into the main buffer. Don't need to worry about overflow buffer
            main_buffer[insert_index_main++] = value;
            insert_index_main = insert_index_main % main_buffer.length;
            main_current_size++;
            is_main_full = (main_current_size == main_buffer.length);
        }
    }

    public double getEnergy(double [] timestamps)
    {
//        Double[] diff_timestamps = new Double[timestamps.length];
//        for(int i = 0; i< timestamps.length - 1 ; i++){
//            diff_timestamps[i] = timestamps[i+1] - diff_timestamps[i];
//        }
        double energy = 0;
        int loop_iterator = insert_index_main;
        for( int i = 0; i < timestamps.length -1  ; i++){
            // start from index_insert to maintain order
            double temp = Math.abs(main_buffer[loop_iterator]) *  Math.abs(main_buffer[loop_iterator]) *
                    ( timestamps[(loop_iterator + 1) % main_buffer.length] - timestamps[loop_iterator] ) ;
            energy += temp/100;
            loop_iterator = (loop_iterator + 1 )  % main_buffer.length;
        }
        return energy;
    }
    public double getAverage() {
        return average;
    }

    public double getRoundedAverage(int numDecimalPlaces) {
        if (numDecimalPlaces < 0) throw new IllegalArgumentException();
        double decimalPlaces = Math.pow(10, numDecimalPlaces);
        return Math.round(average * decimalPlaces) / decimalPlaces;

    }
    public String toString()
    {
        String s = "";
        for(int i = 0; i< main_buffer.length ; i++){
            s = s + "," + main_buffer[i] + "";
        }
        return s;
    }
    public double[] getRawData(){
        return main_buffer;
    }

    public int getCurrentSize() {
        return main_current_size;
    }

    public int getBufferSize() {
        return main_buffer.length;
    }
}



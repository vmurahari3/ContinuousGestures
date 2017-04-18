package edu.gatech.ubicomp.continuousgestures.common.logging.events;


import edu.gatech.ubicomp.continuousgestures.common.rx.RxEvent;

/**
 * An event that is raised whenever a data log needs to be recorded
 * Created by batman on 26/9/16.
 */

public class DataLogEvent extends RxEvent {
    public static final String TAG = DataLogEvent.class.getSimpleName();

    public DataLogEvent(String data) {
        super(data);
    }
}

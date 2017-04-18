package edu.gatech.ubicomp.continuousgestures.common.logging.events;


import edu.gatech.ubicomp.continuousgestures.common.rx.RxEvent;

/**
 * An event that is raised whenever a debug log needs to be recorded
 * Created by batman on 26/9/16.
 */

public class DebugLogEvent extends RxEvent {
    public static final String TAG = DebugLogEvent.class.getSimpleName();

    public DebugLogEvent(String debugStatement) {
        super(debugStatement);
    }
}

package edu.gatech.ubicomp.continuousgestures.common.logging.events;

import edu.gatech.ubicomp.continuousgestures.common.rx.RxEvent;

/**
 * An interaction event that needs to be recorded to the log file is raised
 * Created by batman on 26/9/16.
 */

public class InteractionLogEvent extends RxEvent {
    public static final String TAG = InteractionLogEvent.class.getSimpleName();

    public InteractionLogEvent(String data) {
        super(data);
    }
}

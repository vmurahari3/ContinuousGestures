package edu.gatech.ubicomp.continuousgestures.data.learning.events;

import edu.gatech.ubicomp.continuousgestures.common.rx.RxEvent;

/**
 * Created by localadmin on 7/10/16.
 */

public class TrainingExampleAddedEvent extends RxEvent {
    public final long mGestureSampleID;

    public TrainingExampleAddedEvent(long gestureSampleId) {
        super(String.valueOf(gestureSampleId));
        this.mGestureSampleID = gestureSampleId;
    }
}

package edu.gatech.ubicomp.continuousgestures.data.learning.events;

import edu.gatech.ubicomp.continuousgestures.common.rx.RxEvent;
import edu.gatech.ubicomp.continuousgestures.data.models.GestureSample;

/**
 * Created by localadmin on 7/11/16.
 */

public class TrainingExampleRemovedEvent extends RxEvent {
    public GestureSample mGestureSample;


    public TrainingExampleRemovedEvent(GestureSample gestureSample) {
        super("");
        this.mGestureSample = gestureSample;
    }
}

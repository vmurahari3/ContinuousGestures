package edu.gatech.ubicomp.continuousgestures.common;

import android.content.Context;

import edu.gatech.ubicomp.continuousgestures.MyApplication;


/**
 * A Util that injects the dependencies defined by the object. Specifically designed for UI.
 * Non UI objects should have theirs injections done through the ControllerApplication
 * Created by batman on 26/9/16.
 */

public class DependencyUtil {
    public static void inject(Context p_context, Object p_value){
        ((MyApplication)p_context.getApplicationContext()).inject(p_value);
    }
}

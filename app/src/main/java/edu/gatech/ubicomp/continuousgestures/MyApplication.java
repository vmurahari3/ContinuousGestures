package edu.gatech.ubicomp.continuousgestures;

import android.app.Application;

import org.parceler.javaxinject.Inject;

import dagger.ObjectGraph;
import edu.gatech.ubicomp.continuousgestures.common.dagger.DependencyModule;
import edu.gatech.ubicomp.continuousgestures.common.logging.Logger;
import io.realm.Realm;

/**
 * Created by localadmin on 4/4/17.
 */

public class MyApplication extends Application {


    @Inject
    Logger mLogger;

    private static MyApplication instance;
    private ObjectGraph mDependencyGraph;

    public static String PACKAGE_NAME;

    public MyApplication() {
        instance = this;
    }

    public void inject(Object p_value) {
        if (mDependencyGraph != null) {
            mDependencyGraph.inject(p_value);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDependencyGraph = ObjectGraph.create(new DependencyModule(this));
        inject(this);
        PACKAGE_NAME = getPackageName();

        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }
}

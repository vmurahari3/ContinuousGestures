package edu.gatech.ubicomp.continuousgestures.common.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.gatech.ubicomp.continuousgestures.TestingActivity;
import edu.gatech.ubicomp.continuousgestures.MyApplication;
import edu.gatech.ubicomp.continuousgestures.data.learning.DataAnalyzer;


/**
 * A dependency module class that defines provides and includes for dependency injections.
 * Created by batman on 25/9/16.
 */

@Module(
        library = true,
        injects = {
                Logger.class,
                TestingActivity.class,
                DataAnalyzer.class,
                MyApplication.class
        }
)
public class DependencyModule {
    private final Context mContext;

    public DependencyModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    RxEventBus provideRxEventBus() {
        return new RxEventBus();
    }

    @Provides
    @Singleton
    Logger provideLogger() {
        return new Logger(mContext, Config.DIR_PATH_LOG);
    }
}

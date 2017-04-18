package edu.gatech.ubicomp.continuousgestures.common.rx;

import android.util.Log;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * An Event bus as a singleton that can be injected into any other class.
 * Created by batman on 25/9/16.
 */

public class RxEventBus {
    private final Subject<Object, Object> mBusSubject;

    public RxEventBus() {
        mBusSubject = new SerializedSubject<>(PublishSubject.create());
    }

    public <T> Subscription registerForEvent(final Class<T> eventClass, Action1<T> onNext) {
        return mBusSubject
                .filter(event -> event.getClass().equals(eventClass))
                .map(obj -> (T) obj)
                .doOnError(throwable -> {
                    Log.e(RxEventBus.class.getSimpleName(), "Error occurred: " + throwable);
                })
                .subscribe(onNext);
    }

    public void post(Object event) {
        mBusSubject.onNext(event);
    }
}

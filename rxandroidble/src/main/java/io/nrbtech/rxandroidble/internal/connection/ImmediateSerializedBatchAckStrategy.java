package io.nrbtech.rxandroidble.internal.connection;

import io.nrbtech.rxandroidble.RxBleConnection;

import io.reactivex.Observable;

public class ImmediateSerializedBatchAckStrategy implements RxBleConnection.WriteOperationAckStrategy {

    @Override
    public Observable<Boolean> apply(Observable<Boolean> objectObservable) {
        return objectObservable;
    }
}

package io.nrbtech.rxandroidble.internal.connection;

import io.nrbtech.rxandroidble.RxBleConnection;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;

public class NoRetryStrategy implements RxBleConnection.WriteOperationRetryStrategy {

    @Override
    public Observable<LongWriteFailure> apply(Observable<LongWriteFailure> observable) {
        return observable.flatMap(new Function<LongWriteFailure, Observable<LongWriteFailure>>() {
            @Override
            public Observable<LongWriteFailure> apply(LongWriteFailure longWriteFailure) {
                return Observable.error(longWriteFailure.getCause());
            }
        });
    }
}

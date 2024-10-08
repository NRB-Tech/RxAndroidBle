package io.nrbtech.rxandroidble

import io.nrbtech.rxandroidble.internal.util.DisposableUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.subjects.ReplaySubject

class MockRxBleAdapterStateObservable {

    public final ReplaySubject relay = ReplaySubject.create()

    public final ReplaySubject getRelay() {
        return relay
    }

    public Observable<RxBleAdapterStateObservable.BleAdapterState> asObservable() {
        Observable.create(new ObservableOnSubscribe() {
            @Override
            void subscribe(@NonNull ObservableEmitter observableEmitter) throws Exception {
                def subscription = relay.subscribeWith(DisposableUtil.disposableObserverFromEmitter(observableEmitter))
                observableEmitter.setDisposable(subscription)
            }
        })
    }

    def disableBluetooth() {
        relay.onNext(RxBleAdapterStateObservable.BleAdapterState.STATE_OFF)
    }
}

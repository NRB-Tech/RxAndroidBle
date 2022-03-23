package io.nrbtech.rxandroidble.internal.connection;


import io.nrbtech.rxandroidble.ConnectionSetup;
import io.nrbtech.rxandroidble.RxBleConnection;

import io.reactivex.Observable;

public interface Connector {

    Observable<RxBleConnection> prepareConnection(ConnectionSetup autoConnect);
}

package io.nrbtech.rxandroidble.internal.connection;

import io.nrbtech.rxandroidble.internal.operations.DisconnectOperation;
import io.nrbtech.rxandroidble.internal.serialization.ClientOperationQueue;

import bleshadow.javax.inject.Inject;

import io.reactivex.rxjava3.functions.Action;

@ConnectionScope
class DisconnectAction implements ConnectionSubscriptionWatcher {

    private final ClientOperationQueue clientOperationQueue;
    private final DisconnectOperation operationDisconnect;
    private final DisconnectionRouterInput disconnectionRouterInput;

    @Inject
    DisconnectAction(
            ClientOperationQueue clientOperationQueue,
            DisconnectOperation operationDisconnect,
            DisconnectionRouterInput disconnectionRouterInput) {
        this.clientOperationQueue = clientOperationQueue;
        this.operationDisconnect = operationDisconnect;
        this.disconnectionRouterInput = disconnectionRouterInput;
    }

    @Override
    public void onConnectionSubscribed() {
        // do nothing
    }

    @Override
    public void onConnectionUnsubscribed() {
        clientOperationQueue
                .queue(operationDisconnect)
                .ignoreElements()
                .onErrorComplete()
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        disconnectionRouterInput.close();
                    }
                });
    }
}

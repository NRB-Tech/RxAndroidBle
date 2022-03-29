package io.nrbtech.rxandroidble.internal.connection;

import io.nrbtech.rxandroidble.ConnectionParameters;


public class ConnectionParametersImpl implements ConnectionParameters {

    private final int interval, latency, timeout;

    ConnectionParametersImpl(int interval, int latency, int timeout) {
        this.interval = interval;
        this.latency = latency;
        this.timeout = timeout;
    }

    @Override
    public int getConnectionInterval() {
        return interval;
    }

    @Override
    public int getSlaveLatency() {
        return latency;
    }

    @Override
    public int getSupervisionTimeout() {
        return timeout;
    }
}

package io.nrbtech.rxandroidble.samplekotlin.util

import io.nrbtech.rxandroidble.RxBleConnection
import io.nrbtech.rxandroidble.RxBleDevice

/**
 * Returns `true` if connection state is [CONNECTED][RxBleConnection.RxBleConnectionState.CONNECTED].
 */
internal val RxBleDevice.isConnected: Boolean
    get() = connectionState == RxBleConnection.RxBleConnectionState.CONNECTED

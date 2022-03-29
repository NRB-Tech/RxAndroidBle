package io.nrbtech.rxandroidble.samplekotlin

import android.app.Application
import io.nrbtech.rxandroidble.LogConstants
import io.nrbtech.rxandroidble.LogOptions
import io.nrbtech.rxandroidble.RxBleClient

class SampleApplication : Application() {

    companion object {
        lateinit var rxBleClient: RxBleClient
            private set
    }

    override fun onCreate() {
        super.onCreate()
        rxBleClient = RxBleClient.create(this)
        RxBleClient.updateLogOptions(LogOptions.Builder()
                .setLogLevel(LogConstants.INFO)
                .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
                .setUuidsLogSetting(LogConstants.UUIDS_FULL)
                .setShouldLogAttributeValues(true)
                .build()
        )
    }
}

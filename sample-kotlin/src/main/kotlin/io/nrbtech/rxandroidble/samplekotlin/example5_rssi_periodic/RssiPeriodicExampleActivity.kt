package io.nrbtech.rxandroidble.samplekotlin.example5_rssi_periodic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.nrbtech.rxandroidble.RxBleConnection
import io.nrbtech.rxandroidble.RxBleDevice
import io.nrbtech.rxandroidble.samplekotlin.R
import io.nrbtech.rxandroidble.samplekotlin.SampleApplication
import io.nrbtech.rxandroidble.samplekotlin.databinding.ActivityExample1Binding
import io.nrbtech.rxandroidble.samplekotlin.databinding.ActivityExample5Binding
import io.nrbtech.rxandroidble.samplekotlin.util.isConnected
import io.nrbtech.rxandroidble.samplekotlin.util.showSnackbarShort
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit.SECONDS

private const val EXTRA_MAC_ADDRESS = "extra_mac_address"

class RssiPeriodicExampleActivity : AppCompatActivity() {

    companion object {
        fun newInstance(context: Context, macAddress: String) =
            Intent(context, RssiPeriodicExampleActivity::class.java).apply {
                putExtra(EXTRA_MAC_ADDRESS, macAddress)
            }
    }

    private lateinit var bleDevice: RxBleDevice

    private var stateDisposable: Disposable? = null

    private var connectionDisposable: Disposable? = null

    private lateinit var binding: ActivityExample5Binding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val macAddress = intent.getStringExtra(EXTRA_MAC_ADDRESS)
        title = getString(R.string.mac_address, macAddress)
        bleDevice = SampleApplication.rxBleClient.getBleDevice(macAddress!!)

        binding = ActivityExample5Binding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.connectToggle.setOnClickListener { onConnectToggleClick() }

        // How to listen for connection state changes
        bleDevice.observeConnectionStateChanges()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { onConnectionStateChange(it) }
            .let { stateDisposable = it }
    }

    private fun onConnectToggleClick() {
        if (bleDevice.isConnected) {
            triggerDisconnect()
        } else {
            bleDevice.establishConnection(false)
                .doFinally { clearSubscription() }
                .flatMap { connection ->
                    // Set desired interval.
                    Observable.interval(2, SECONDS).flatMapSingle { connection.readRssi() }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ updateRssi(it) }, { showSnackbarShort("Connection error: $it") })
                .let { connectionDisposable = it }
        }
    }

    private fun updateRssi(rssiValue: Int) {
        binding.rssi.text = getString(R.string.format_rssi, rssiValue)
    }

    private fun onConnectionStateChange(newState: RxBleConnection.RxBleConnectionState) {
        binding.connectionState.text = newState.toString()
        updateUI()
    }

    private fun clearSubscription() {
        connectionDisposable = null
        updateUI()
    }

    private fun triggerDisconnect() = connectionDisposable?.dispose()

    private fun updateUI() =
        binding.connectToggle.setText(if (bleDevice.isConnected) R.string.button_disconnect else R.string.button_connect)

    override fun onPause() {
        super.onPause()
        triggerDisconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        stateDisposable?.dispose()
    }
}

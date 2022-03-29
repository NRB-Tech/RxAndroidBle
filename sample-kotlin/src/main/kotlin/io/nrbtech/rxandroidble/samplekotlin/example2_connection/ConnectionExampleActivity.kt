package io.nrbtech.rxandroidble.samplekotlin.example2_connection

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.nrbtech.rxandroidble.RxBleConnection
import io.nrbtech.rxandroidble.RxBleDevice
import io.nrbtech.rxandroidble.samplekotlin.R
import io.nrbtech.rxandroidble.samplekotlin.SampleApplication
import io.nrbtech.rxandroidble.samplekotlin.databinding.ActivityExample2Binding
import io.nrbtech.rxandroidble.samplekotlin.util.isConnected
import io.nrbtech.rxandroidble.samplekotlin.util.showSnackbarShort
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

private const val EXTRA_MAC_ADDRESS = "extra_mac_address"

class ConnectionExampleActivity : AppCompatActivity() {

    companion object {
        fun newInstance(context: Context, macAddress: String) =
            Intent(context, ConnectionExampleActivity::class.java).apply {
                putExtra(EXTRA_MAC_ADDRESS, macAddress)
            }
    }

    private lateinit var bleDevice: RxBleDevice

    private var connectionDisposable: Disposable? = null

    private var stateDisposable: Disposable? = null

    private val mtuDisposable = CompositeDisposable()

    private var binding: ActivityExample2Binding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example2)

        binding = ActivityExample2Binding.inflate(layoutInflater)

        binding!!.connectToggle.setOnClickListener { onConnectToggleClick() }
        binding!!.setMtu.setOnClickListener { onSetMtu() }

        val macAddress = intent.getStringExtra(EXTRA_MAC_ADDRESS)
        title = getString(R.string.mac_address, macAddress)
        bleDevice = SampleApplication.rxBleClient.getBleDevice(macAddress!!)

        // How to listen for connection state changes
        // Note: it is meant for UI updates only — one should not observeConnectionStateChanges() with BLE connection logic
        bleDevice.observeConnectionStateChanges()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { onConnectionStateChange(it) }
            .let { stateDisposable = it }
    }

    private fun onConnectToggleClick() {
        if (bleDevice.isConnected) {
            triggerDisconnect()
        } else {
            bleDevice.establishConnection(binding!!.autoconnect.isChecked)
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { dispose() }
                .subscribe({ onConnectionReceived() }, { onConnectionFailure(it) })
                .let { connectionDisposable = it }
        }
    }

    @TargetApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
    private fun onSetMtu() {
        binding!!.newMtu.text.toString().toIntOrNull()?.let { mtu ->
            bleDevice.establishConnection(false)
                .flatMapSingle { rxBleConnection -> rxBleConnection.requestMtu(mtu) }
                .take(1) // Disconnect automatically after discovery
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { updateUI() }
                .subscribe({ onMtuReceived(it) }, { onConnectionFailure(it) })
                .let { mtuDisposable.add(it) }
        }
    }

    private fun onConnectionFailure(throwable: Throwable) = showSnackbarShort("Connection error: $throwable")

    private fun onConnectionReceived() = showSnackbarShort("Connection received")

    private fun onConnectionStateChange(newState: RxBleConnection.RxBleConnectionState) {
        binding!!.connectionState.text = newState.toString()
        updateUI()
    }

    private fun onMtuReceived(mtu: Int) = showSnackbarShort("MTU received: $mtu")

    private fun dispose() {
        connectionDisposable = null
        updateUI()
    }

    private fun triggerDisconnect() = connectionDisposable?.dispose()

    private fun updateUI() {
        binding!!.connectToggle.setText(if (bleDevice.isConnected) R.string.button_disconnect else R.string.button_connect)
        binding!!.autoconnect.isEnabled = !bleDevice.isConnected
    }

    override fun onPause() {
        super.onPause()
        triggerDisconnect()
        mtuDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        stateDisposable?.dispose()
    }
}

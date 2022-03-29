package io.nrbtech.rxandroidble.samplekotlin.example1_scanning

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.nrbtech.rxandroidble.exceptions.BleScanException
import io.nrbtech.rxandroidble.samplekotlin.DeviceActivity
import io.nrbtech.rxandroidble.samplekotlin.R
import io.nrbtech.rxandroidble.samplekotlin.SampleApplication
import io.nrbtech.rxandroidble.samplekotlin.databinding.ActivityExample1Binding
import io.nrbtech.rxandroidble.samplekotlin.example1a_background_scanning.BackgroundScanActivity
import io.nrbtech.rxandroidble.samplekotlin.util.isLocationPermissionGranted
import io.nrbtech.rxandroidble.samplekotlin.util.requestLocationPermission
import io.nrbtech.rxandroidble.samplekotlin.util.showError
import io.nrbtech.rxandroidble.scan.ScanFilter
import io.nrbtech.rxandroidble.scan.ScanResult
import io.nrbtech.rxandroidble.scan.ScanSettings
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable

class ScanActivity : AppCompatActivity() {

    private val rxBleClient = SampleApplication.rxBleClient

    private var scanDisposable: Disposable? = null

    private val resultsAdapter =
        ScanResultsAdapter { startActivity(DeviceActivity.newInstance(this, it.bleDevice.macAddress)) }

    private var hasClickedScan = false

    private val isScanning: Boolean
        get() = scanDisposable != null

    private var binding: ActivityExample1Binding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example1)
        configureResultList()

        binding = ActivityExample1Binding.inflate(layoutInflater)

        binding!!.backgroundScanBtn.setOnClickListener { startActivity(BackgroundScanActivity.newInstance(this)) }
        binding!!.scanToggleBtn.setOnClickListener { onScanToggleClick() }
    }

    private fun configureResultList() {
        with(binding!!.scanResults) {
            setHasFixedSize(true)
            itemAnimator = null
            adapter = resultsAdapter
        }
    }

    private fun onScanToggleClick() {
        if (isScanning) {
            scanDisposable?.dispose()
        } else {
            if (rxBleClient.isScanRuntimePermissionGranted) {
                scanBleDevices()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally { dispose() }
                    .subscribe({ resultsAdapter.addScanResult(it) }, { onScanFailure(it) })
                    .let { scanDisposable = it }
            } else {
                hasClickedScan = true
                requestLocationPermission(rxBleClient)
            }
        }
        updateButtonUIState()
    }

    private fun scanBleDevices(): Observable<ScanResult> {
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .build()

        val scanFilter = ScanFilter.Builder()
//            .setDeviceAddress("B4:99:4C:34:DC:8B")
            // add custom filters if needed
            .build()

        return rxBleClient.scanBleDevices(scanSettings, scanFilter)
    }

    private fun dispose() {
        scanDisposable = null
        resultsAdapter.clearScanResults()
        updateButtonUIState()
    }

    private fun onScanFailure(throwable: Throwable) {
        if (throwable is BleScanException) showError(throwable)
    }

    private fun updateButtonUIState() =
        binding!!.scanToggleBtn.setText(if (isScanning) R.string.button_stop_scan else R.string.button_start_scan)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (isLocationPermissionGranted(requestCode, grantResults) && hasClickedScan) {
            hasClickedScan = false
            scanBleDevices()
        }
    }

    public override fun onPause() {
        super.onPause()
        // Stop scanning in onPause callback.
        if (isScanning) scanDisposable?.dispose()
    }
}
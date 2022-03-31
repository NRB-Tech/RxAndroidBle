package io.nrbtech.rxandroidble.samplekotlin.example1a_background_scanning

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.nrbtech.rxandroidble.exceptions.BleScanException
import io.nrbtech.rxandroidble.samplekotlin.R
import io.nrbtech.rxandroidble.samplekotlin.SampleApplication
import io.nrbtech.rxandroidble.samplekotlin.databinding.ActivityExample1aBinding
import io.nrbtech.rxandroidble.samplekotlin.util.isLocationPermissionGranted
import io.nrbtech.rxandroidble.samplekotlin.util.requestLocationPermission
import io.nrbtech.rxandroidble.samplekotlin.util.showError
import io.nrbtech.rxandroidble.samplekotlin.util.showSnackbarShort
import io.nrbtech.rxandroidble.scan.ScanFilter
import io.nrbtech.rxandroidble.scan.ScanSettings

class BackgroundScanActivity : AppCompatActivity() {

    companion object {
        fun newInstance(context: Context) = Intent(context, BackgroundScanActivity::class.java)
    }

    private val rxBleClient = SampleApplication.rxBleClient

    private lateinit var callbackIntent: PendingIntent

    private var hasClickedScan = false

    private var binding: ActivityExample1aBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example1a)

        callbackIntent = ScanReceiver.newPendingIntent(this)

        binding = ActivityExample1aBinding.inflate(layoutInflater)

        binding!!.scanStartBtn.setOnClickListener { onScanStartClick() }
        binding!!.scanStopBtn.setOnClickListener { onScanStopClick() }
    }

    private fun onScanStartClick() {
        if (rxBleClient.isScanRuntimePermissionGranted) {
            scanBleDeviceInBackground()
        } else {
            hasClickedScan = true
            requestLocationPermission(rxBleClient)
        }
    }

    private fun scanBleDeviceInBackground() {
        if (Build.VERSION.SDK_INT >= 26 /* Build.VERSION_CODES.O */) {
            try {
                val scanSettings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .build()

                val scanFilter = ScanFilter.Builder()
//                    .setDeviceAddress("5C:31:3E:BF:F7:34")
                    // add custom filters if needed
                    .build()

                rxBleClient.backgroundScanner.scanBleDeviceInBackground(callbackIntent, scanSettings, scanFilter)
            } catch (scanException: BleScanException) {
                Log.e("BackgroundScanActivity", "Failed to start background scan", scanException)
                showError(scanException)
            }
        } else {
            showSnackbarShort("Background scanning requires at least API 26")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (isLocationPermissionGranted(requestCode, grantResults) && hasClickedScan) {
            hasClickedScan = false
            scanBleDeviceInBackground()
        }
    }

    private fun onScanStopClick() {
        if (Build.VERSION.SDK_INT >= 26 /* Build.VERSION_CODES.O */) {
            rxBleClient.backgroundScanner.stopBackgroundBleScan(callbackIntent)
        } else {
            showSnackbarShort("Background scanning requires at least API 26")
        }
    }
}

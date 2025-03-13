package io.nrbtech.rxandroidble.sample.example1a_background_scanning;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import io.nrbtech.rxandroidble.RxBleClient;
import io.nrbtech.rxandroidble.exceptions.BleScanException;
import io.nrbtech.rxandroidble.sample.SampleApplication;
import io.nrbtech.rxandroidble.sample.databinding.ActivityExample1aBinding;
import io.nrbtech.rxandroidble.sample.util.ScanExceptionHandler;
import io.nrbtech.rxandroidble.sample.util.LocationPermission;
import io.nrbtech.rxandroidble.scan.ScanFilter;
import io.nrbtech.rxandroidble.scan.ScanSettings;

public class BackgroundScanActivity extends AppCompatActivity {

    private static final int SCAN_REQUEST_CODE = 42;
    private RxBleClient rxBleClient;
    private PendingIntent callbackIntent;
    private boolean hasClickedScan;
    private ActivityExample1aBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExample1aBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rxBleClient = SampleApplication.getRxBleClient(this);
        callbackIntent = PendingIntent.getBroadcast(this, SCAN_REQUEST_CODE,
                new Intent(this, ScanReceiver.class), 0);

        // Set up click listeners
        binding.scanStartBtn.setOnClickListener(v -> onScanStartClick());
        binding.scanStopBtn.setOnClickListener(v -> onScanStopClick());
    }

    private void onScanStartClick() {
        hasClickedScan = true;
        if (rxBleClient.isScanRuntimePermissionGranted()) {
            scanBleDeviceInBackground();
        } else {
            LocationPermission.requestLocationPermission(this, rxBleClient);
        }
    }

    private void scanBleDeviceInBackground() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            try {
                rxBleClient.getBackgroundScanner().scanBleDeviceInBackground(
                        callbackIntent,
                        new ScanSettings.Builder()
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                                .build(),
                        new ScanFilter.Builder()
                                .setDeviceAddress("5C:31:3E:BF:F7:34")
                                // add custom filters if needed
                                .build()
                );
            } catch (BleScanException scanException) {
                Log.w("BackgroundScanActivity", "Failed to start background scan", scanException);
                ScanExceptionHandler.handleException(this, scanException);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
            @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (LocationPermission.isRequestLocationPermissionGranted(requestCode, permissions, grantResults, rxBleClient)
                && hasClickedScan) {
            hasClickedScan = false;
            scanBleDeviceInBackground();
        }
    }

    private void onScanStopClick() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            rxBleClient.getBackgroundScanner().stopBackgroundBleScan(callbackIntent);
        }
    }
}

package io.nrbtech.rxandroidble.sample.example1_scanning;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import io.nrbtech.rxandroidble.RxBleClient;
import io.nrbtech.rxandroidble.exceptions.BleScanException;
import io.nrbtech.rxandroidble.sample.DeviceActivity;
import io.nrbtech.rxandroidble.sample.R;
import io.nrbtech.rxandroidble.sample.SampleApplication;
import io.nrbtech.rxandroidble.sample.databinding.ActivityExample1Binding;
import io.nrbtech.rxandroidble.sample.example1a_background_scanning.BackgroundScanActivity;
import io.nrbtech.rxandroidble.sample.util.ScanExceptionHandler;
import io.nrbtech.rxandroidble.sample.util.LocationPermission;
import io.nrbtech.rxandroidble.scan.ScanFilter;
import io.nrbtech.rxandroidble.scan.ScanResult;
import io.nrbtech.rxandroidble.scan.ScanSettings;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

public class ScanActivity extends AppCompatActivity {

    private ActivityExample1Binding binding;
    private RxBleClient rxBleClient;
    private Disposable scanDisposable;
    private ScanResultsAdapter resultsAdapter;
    private boolean hasClickedScan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExample1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rxBleClient = SampleApplication.getRxBleClient(this);

        // Set up click listeners
        binding.backgroundScanBtn.setOnClickListener(v -> onBackgroundScanRequested());
        binding.scanToggleBtn.setOnClickListener(v -> onScanToggleClick());
        binding.showDetails.setOnClickListener(v -> onShowDetailsToggleClick());

        configureResultList();
    }

    public void onBackgroundScanRequested() {
        startActivity(new Intent(this, BackgroundScanActivity.class));
    }

    public void onScanToggleClick() {
        if (isScanning()) {
            scanDisposable.dispose();
        } else {
            if (rxBleClient.isScanRuntimePermissionGranted()) {
                scanBleDevices();
            } else {
                hasClickedScan = true;
                LocationPermission.requestLocationPermission(this, rxBleClient);
            }
        }

        updateButtonUIState();
    }

    public void onShowDetailsToggleClick() {
        resultsAdapter.setShowDetails(binding.showDetails.isChecked());
    }

    private void scanBleDevices() {
        scanDisposable = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .build(),
                new ScanFilter.Builder()
//                            .setDeviceAddress("B4:99:4C:34:DC:8B")
                        // add custom filters if needed
                        .build()
        )
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::dispose)
                .subscribe(resultsAdapter::addScanResult, this::onScanFailure);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
            @NonNull final int[] grantResults) {
        if (LocationPermission.isRequestLocationPermissionGranted(requestCode, permissions, grantResults, rxBleClient)
                && hasClickedScan) {
            hasClickedScan = false;
            scanBleDevices();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isScanning()) {
            /*
             * Stop scanning in onPause callback.
             */
            scanDisposable.dispose();
        }
    }

    private void configureResultList() {
        binding.scanResults.setHasFixedSize(true);
        binding.scanResults.setItemAnimator(null);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        binding.scanResults.setLayoutManager(recyclerLayoutManager);
        resultsAdapter = new ScanResultsAdapter();
        binding.scanResults.setAdapter(resultsAdapter);
        resultsAdapter.setOnAdapterItemClickListener(view -> {
            final int childAdapterPosition = binding.scanResults.getChildAdapterPosition(view);
            final ScanResult itemAtPosition = resultsAdapter.getItemAtPosition(childAdapterPosition);
            onAdapterItemClick(itemAtPosition);
        });
        resultsAdapter.setShowDetails(binding.showDetails.isChecked());
    }

    private boolean isScanning() {
        return scanDisposable != null;
    }

    private void onAdapterItemClick(ScanResult scanResults) {
        final String macAddress = scanResults.getBleDevice().getMacAddress();
        final Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra(DeviceActivity.EXTRA_MAC_ADDRESS, macAddress);
        startActivity(intent);
    }

    private void onScanFailure(Throwable throwable) {
        if (throwable instanceof BleScanException) {
            ScanExceptionHandler.handleException(this, (BleScanException) throwable);
        }
    }

    private void dispose() {
        scanDisposable = null;
        resultsAdapter.clearScanResults();
        updateButtonUIState();
    }

    private void updateButtonUIState() {
        binding.scanToggleBtn.setText(isScanning() ? R.string.stop_scan : R.string.start_scan);
    }
}

package io.nrbtech.rxandroidble.sample.example5_rssi_periodic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

import io.nrbtech.rxandroidble.RxBleConnection;
import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.sample.DeviceActivity;
import io.nrbtech.rxandroidble.sample.R;
import io.nrbtech.rxandroidble.sample.SampleApplication;
import io.nrbtech.rxandroidble.sample.databinding.ActivityExample5Binding;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RssiPeriodicExampleActivity extends AppCompatActivity {

    private ActivityExample5Binding binding;
    private RxBleDevice bleDevice;
    private Disposable connectionDisposable;
    private Disposable stateDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExample5Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String macAddress = getIntent().getStringExtra(DeviceActivity.EXTRA_MAC_ADDRESS);
        setTitle(getString(R.string.mac_address, macAddress));
        bleDevice = SampleApplication.getRxBleClient(this).getBleDevice(macAddress);

        // Set up click listener for connect button
        binding.connectToggle.setOnClickListener(v -> onConnectToggleClick());

        // How to listen for connection state changes
        stateDisposable = bleDevice.observeConnectionStateChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnectionStateChange);
    }

    private void onConnectToggleClick() {
        if (isConnected()) {
            triggerDisconnect();
        } else {
            connectionDisposable = bleDevice.establishConnection(false)
                    .doFinally(this::clearSubscription)
                    .flatMap(rxBleConnection -> // Set desired interval.
                            Observable.interval(2, SECONDS).flatMapSingle(sequence -> rxBleConnection.readRssi()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::updateRssi, this::onConnectionFailure);
        }
    }

    private void updateRssi(int rssiValue) {
        binding.rssi.setText(getString(R.string.read_rssi, rssiValue));
    }

    private boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    private void onConnectionFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(android.R.id.content), "Connection error: " + throwable, Snackbar.LENGTH_SHORT).show();
    }

    private void onConnectionStateChange(RxBleConnection.RxBleConnectionState newState) {
        binding.connectionState.setText(newState.toString());
        updateUI();
    }

    private void clearSubscription() {
        connectionDisposable = null;
        updateUI();
    }

    private void triggerDisconnect() {
        if (connectionDisposable != null) {
            connectionDisposable.dispose();
        }
    }

    private void updateUI() {
        final boolean connected = isConnected();
        binding.connectToggle.setText(connected ? R.string.disconnect : R.string.connect);
    }

    @Override
    protected void onPause() {
        super.onPause();
        triggerDisconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (stateDisposable != null) {
            stateDisposable.dispose();
        }
    }
}

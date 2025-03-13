package io.nrbtech.rxandroidble.sample.example2_connection;

import android.annotation.TargetApi;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

import io.nrbtech.rxandroidble.RxBleConnection;
import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.sample.DeviceActivity;
import io.nrbtech.rxandroidble.sample.R;
import io.nrbtech.rxandroidble.sample.SampleApplication;
import io.nrbtech.rxandroidble.sample.databinding.ActivityExample2Binding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class ConnectionExampleActivity extends AppCompatActivity {

    private ActivityExample2Binding binding;
    private RxBleDevice bleDevice;
    private Disposable connectionDisposable;
    private final CompositeDisposable mtuDisposable = new CompositeDisposable();
    private Disposable stateDisposable;

    public void onConnectToggleClick() {
        if (isConnected()) {
            triggerDisconnect();
        } else {
            connectionDisposable = bleDevice.establishConnection(binding.autoconnect.isChecked())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(this::dispose)
                    .subscribe(this::onConnectionReceived, this::onConnectionFailure);
        }
    }

    @TargetApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
    public void onSetMtu() {
        final Disposable disposable = bleDevice.establishConnection(false)
                .flatMapSingle(rxBleConnection -> rxBleConnection.requestMtu(72))
                .take(1) // Disconnect automatically after discovery
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::updateUI)
                .subscribe(this::onMtuReceived, this::onConnectionFailure);
        mtuDisposable.add(disposable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExample2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String macAddress = getIntent().getStringExtra(DeviceActivity.EXTRA_MAC_ADDRESS);
        setTitle(getString(R.string.mac_address, macAddress));
        bleDevice = SampleApplication.getRxBleClient(this).getBleDevice(macAddress);

        // Set up click listeners
        binding.connectToggle.setOnClickListener(v -> onConnectToggleClick());
        binding.setMtu.setOnClickListener(v -> onSetMtu());

        // How to listen for connection state changes
        // Note: it is meant for UI updates only — one should not observeConnectionStateChanges() with BLE connection logic
        stateDisposable = bleDevice.observeConnectionStateChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnectionStateChange);
    }

    private boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    private void onConnectionFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(android.R.id.content), "Connection error: " + throwable, Snackbar.LENGTH_SHORT).show();
    }

    @SuppressWarnings("unused")
    private void onConnectionReceived(RxBleConnection connection) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(android.R.id.content), "Connection received", Snackbar.LENGTH_SHORT).show();
    }

    private void onConnectionStateChange(RxBleConnection.RxBleConnectionState newState) {
        binding.connectionState.setText(newState.toString());
        updateUI();
    }

    private void onMtuReceived(Integer mtu) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(android.R.id.content), "MTU received: " + mtu, Snackbar.LENGTH_SHORT).show();
    }

    private void dispose() {
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
        binding.autoconnect.setEnabled(!connected);
    }

    @Override
    protected void onPause() {
        super.onPause();

        triggerDisconnect();
        mtuDisposable.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (stateDisposable != null) {
            stateDisposable.dispose();
        }
    }
}
